package plot;
import java.io.File;
import java.io.FileWriter;

import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.dom4j.DocumentException;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import com.ibm.icu.text.SimpleDateFormat;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.SystemFrames;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngineFactory;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
public class Effect {
	private int wind = 0; 
	private int rain = 0;
	private int snow = 0;
	private boolean maHasEffect = false;
	private boolean hasEffect = false;
	private int ifNeg=0;//0为肯定，1为否定
	
	//	private boolean WRS = false;//表示明确有风雨雪的模板。说明短信中明确提到风、雨、雪了，就不在考虑情绪等其他模板和主题了。
	public static int effect = 0;

	private ArrayList<String> topicAndTemplate = new ArrayList<String>(Arrays.asList("WindTemplate","SnowTemplate","RainTemplate"));
	
	static Logger logger = Logger.getLogger(Effect.class.getName());
	
	public enum Direction {
		East,West,South,North,SouthEast,NorthEast,SouthWest,NorthWest
	}
	
	public int getWind() {
		return wind;
	}

	public void setWind(int wind) {
		this.wind = wind;
	}

	public int getRain() {
		return rain;
	}

	public void setRain(int rain) {
		this.rain = rain;
	}

	public int getSnow() {
		return snow;
	}

	public void setSnow(int snow) {
		this.snow = snow;
	}
	
	public boolean isHasEffect() {
		return hasEffect;
	}
	public SWRLFactory createSWRLFactory(OWLModel model) {
		SWRLFactory factory = new SWRLFactory(model);
		return factory;
	}
	
	public SWRLRuleEngine createRuleEngine(OWLModel model) throws SWRLRuleEngineException {
		SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create("SWRLJessBridge",model);
		return ruleEngine;
	}
	
	private int hasEffects(ArrayList<String> list,OWLModel model,String maName) { //传进来的模板是风=1，雨=2，雪=3，风雨=4，风雪=5，雨雪=6，无风雨雪=0，风雨雪=（1,2,3）中随机一个
		OWLIndividual individual = model.getOWLIndividual(maName);
		OWLDatatypeProperty hasEff = model.getOWLDatatypeProperty("hasEffect");
		String hasEffectValue = individual.getPropertyValue(hasEff).toString();
		if(hasEffectValue.equals("rain"))
		{
			maHasEffect = true;
			System.out.println(maName + "已经存在粒子特效" + hasEffectValue);
			return 2;
		}
		else if(hasEffectValue.equals("snow"))
		{
			maHasEffect = true;
			System.out.println(maName + "已经存在粒子特效" + hasEffectValue);
			return 3;
		}
		
		System.out.println("传进来的模板是风=1，雨=2，雪=3，风雨=4，风雪=5，雨雪=6，无风雨雪=0，风雨雪=（1,2,3）中随机一个");
		
		if(list.contains("WindTemplate")) {
			
			if(list.contains("RainTemplate")) {
				
				if(list.contains("SnowTemplate")){ //有风雨雪
					
					Random rand = new Random();
					System.out.println("同时出现风雨雪，随机一个");
				//	WRS = true;
					return rand.nextInt(3) + 1;
					
				} else { //有风雨，无雪
					System.out.println("同时出现风雨，没有雪");
				//	WRS = true;
					return 4;
				}		
			} 
			else if(list.contains("SnowTemplate")){ 
				System.out.println("同时出现风雪，没有雨");
				//WRS = true;
				return 5;// 有风雪，无雨
			} else { 
				System.out.println("只有风的模板");
			//	WRS = true;
				return 1;//有风，无雨雪
			}
		} else {  //无风
			if(list.contains("RainTemplate")) {
				
				if(list.contains("SnowTemplate")) {
					System.out.println("有雨雪的模板，没有风的");
			//		WRS = true;
					return 6; //有雨雪
				} else {
					System.out.println("只有雨的模板");
			//		WRS = true;
					return 2; //有雨
				}
			} else {
				if(list.contains("SnowTemplate")) {
					System.out.println("只有雪的模板");
			//		WRS = true;
					return 3; // 有雪
				} else {
					System.out.println("风雨雪都没有");
					return 0; // 无风雨雪
				}
			}
		}
	}
	/**
	 **================================= WindInfo 用来执行添加风=================================
	 **/
	public void windExecuteTopicToBackgroundSceneSWRLEngine(OWLModel model,ArrayList<String> templateName) throws SWRLRuleEngineException {
		SWRLRuleEngine ruleEngine = createRuleEngine(model);
		ruleEngine.reset();
		SWRLFactory factory = createSWRLFactory(model);
		factory.disableAll();

		Iterator<SWRLImp> iter = factory.getImps().iterator();
		while (iter.hasNext()) {
			SWRLImp imp = (SWRLImp) iter.next();

			if (templateName.size() != 0) {
				if (imp.getLocalName().contains("addWindToMaRule")) { // 找到名为addWindtoMaRule的规则
					for (Iterator<String> its = templateName.iterator(); its.hasNext();) {

						String templateValue = its.next();
						String templateValue1 = templateValue.replaceAll("Individual", "");

						if (imp.getBody().getBrowserText().contains(templateValue1)) {
							logger.info("运行的规则名字为：" + imp.getLocalName());
							imp.enable();// 执行此规则
						}
					}
				}
			}
		}
		ruleEngine.infer();
	}

	// 执行OWL里的规则
	public void windExecuteTemplateToBackgroundSceneSWRLEngine(OWLModel model,ArrayList<String> templateName) throws SWRLRuleEngineException {
		SWRLRuleEngine ruleEngine = createRuleEngine(model);
		ruleEngine.reset();
		SWRLFactory factory = createSWRLFactory(model);
		factory.disableAll();
		Iterator<SWRLImp> iter = factory.getImps().iterator();

		while (iter.hasNext()) {

			SWRLImp imp = (SWRLImp) iter.next();

			if (templateName.size() != 0) {
				if (imp.getLocalName().contains("addWindToMaRule")) { // 找到名字包含"addWindToSceneRule"的规则
					for (Iterator<String> its = templateName.iterator(); its.hasNext();) {

						String templateValue = its.next();
						
						String templateValue1 = templateValue.substring(0,templateValue.indexOf(":"));

						if (imp.getBody().getBrowserText().contains(templateValue1)) {
							logger.info("运行的规则名字：" + imp.getLocalName());
							System.out.println("运行的规则名字：" + imp.getLocalName());
							imp.enable();
						}
					}
				}
			}
		}
		ruleEngine.infer();

	}

	@SuppressWarnings("unused")
	public Document WindInfer(ArrayList<String> List, OWLModel model,String maName, Document doc) throws OntologyLoadException,SWRLRuleEngineException {
		
		String[] WindList = new String[20];// 用来存储抽取出来的符合主题和模版的风
		int WindFromInfoNumber = 0;// 最终抽取的符合的风的数量
		int WindFromTemNumber = 0; // 从模版抽到风的个数
		int WindFromTopNumber = 0;// 从主题抽到风的个数
		String str = "p4:";
		String ieTopic;
		ArrayList<String> topicList = new ArrayList<String>();
		Collection mapToWindValues = null;
		
		
		System.out.println("######################实际上执行的模板和主题######################");
		for(int i = 0; i < List.size(); i++){
			System.out.println(List.get(i));
		}
		System.out.println("############################################################");
//
		OWLIndividual maIndividual = model.getOWLIndividual(maName); // 获取maya实例
		if (null == maIndividual) {
			System.out.println("maya 实例无法获取，可能不存在或丢失！请检查maName是否正确或存在");
			return doc;
		}

		OWLObjectProperty addWindToMaProperty = model.getOWLObjectProperty(str + "addWindToMa");
		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");// 此变量用来判断场景属于室内室外

		// 判断
		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
		if (null == plane) {
			System.out.println("无法获取实例" + maIndividual.getBrowserText() + "的hasValueOfPlace属性(此属性用来判断场景属于室内还是室外)，so 拒绝加风！请检查知识库中该属性是否存在或正确？");
			return doc;
		} else {
			if (plane.getBrowserText().equals("InWaterDescription")	|| !plane.getBrowserText().equals("outDoorDescription")) {
				System.out.println("此场景不适合加风！");
				return doc;
			}
		}
		
	// 处理模板信息
		if (WindFromTopNumber == 0) {
			System.out.println("-------------模板处理---------------");
			int listSize = 0;
		
			if (List == null || List.size() == 0) {
				System.out.println("没有模板传入");
			} else 	{	
				listSize = List.size();
			}
			
			if (listSize > 0) {

				System.out.println("传递进来的模板个数为：" + listSize + "个，分别是：" + List);
				
				windExecuteTemplateToBackgroundSceneSWRLEngine(model, List);
				// 去相应的场景里获取所添加的风
				mapToWindValues = null;
				mapToWindValues = maIndividual.getPropertyValues(addWindToMaProperty);

				if (mapToWindValues.isEmpty())
					System.out.println("根据传递过来的模板，没有相应的风！");
				else {

					for (Iterator it1 = mapToWindValues.iterator(); it1.hasNext();) {
						OWLIndividual WindIndiviual = (OWLIndividual) it1.next();
						WindList[WindFromInfoNumber] = WindIndiviual.getBrowserText();
						WindFromInfoNumber++;
						WindFromTemNumber++;
					}
					
					
					if (WindFromTemNumber != 0) {

						System.out.print("根据模板抽到" + WindFromTemNumber + "个风，分别是：");

						for (int i = 0; i < WindFromTemNumber; i++)
							System.out.print(WindList[i] + " ");

						System.out.println();
					}
				}
			}
			
		}
		System.out.println("-------------模板处理完毕！------------");
		
		// 处理主题信息
		if(WindFromInfoNumber == 0){
			System.out.println("---------------场景的主题处理---------------");
			System.out.println("     --------处理IE抽取的主题-------");
	
			ieTopic = doc.getRootElement().element("maName").attributeValue("topic");// 获取IE 主题
			if (ieTopic.contains("Topic")) {
				System.out.println("IE 抽取的主题是：" + ieTopic);
				topicList.add(ieTopic);
				windExecuteTopicToBackgroundSceneSWRLEngine(model, topicList);
				mapToWindValues = maIndividual.getPropertyValues(addWindToMaProperty);
				
				if (mapToWindValues.isEmpty()) {
					System.out.println("根据抽取出的主题，没有对应的风");
				} else {
	
					for (Iterator it1 = mapToWindValues.iterator(); it1.hasNext();) {
	
						OWLIndividual WindIndiviual = (OWLIndividual) it1.next();
						WindList[WindFromInfoNumber] = WindIndiviual.getBrowserText();
						WindFromInfoNumber++;
						WindFromTopNumber++;
					}
					
					if (WindFromTopNumber != 0) {
						System.out.print("根据主题抽取到" + WindFromTopNumber + "个风，分别是：");
	
						for (int i = 0; i < WindFromTopNumber; i++)
							System.out.print(WindList[i] + " ");
	
						System.out.println("\n因主题优先，故模板不作处理！");
					}
				}
			
			} else {
				System.out.println("     ----------IE未抽取出主题--------");
			}
			
			System.out.println("---------------场景主题处理完毕!----------------");
		}
		
		if (WindFromInfoNumber == 0) {
			System.out.println("根据所给条件，没有抽取到适合的风");
			return doc;
		} else {
			hasEffect = true;
		}

		// 在获取到的风里面随机选取一个

		Random random = new Random();
		int rd = random.nextInt(WindFromInfoNumber);

		OWLIndividual WindIndiviual = model.getOWLIndividual(WindList[rd]);
		System.out.println("随机选取到的风为：" + WindIndiviual.getBrowserText());

		String WindType = null;
		String WindDirection = "";
		String WindExpress = "";// 模板为情绪时，用哪种表现方式
		
		OWLDatatypeProperty Magnitude =  model.getOWLDatatypeProperty(str + "magnitude");
		WindType = (String) WindIndiviual.getPropertyValue(Magnitude);
		System.out.println("WindType == " + WindType);
		
		if (WindIndiviual.getBrowserText().contains("Emotion")) {

			if (WindType.equals("Heavy")) {
				WindExpress = "Conic";// 圆锥
			} else if (WindType.equals("Moderate")) {
				WindExpress = "Helix";// 螺旋
			} else if (WindType.equals("Light")) {
				WindExpress = "Cardioid";// 心形线
			}

		} else {
			Direction WindDir;
			Direction[] Dir = Direction.values();// Dir存储枚举的值，
			int num = new Random().nextInt(8);// [0,8)之间的整数
			WindDir = Dir[num];
			WindDirection = WindDir.toString();
		}

		System.out.println("\n风的类型： " + WindType + "\n风的方向： " + WindDirection
				+ "\n风的表现手法：" + WindExpress + "\n------信息获取完毕！------");

		Document doc1 = printWindRule(doc, WindType, WindDirection, WindExpress);
		return doc1;
	}

	/**
	 * 此方法用于场景中是否加进去风 WindFromInfoNumber 标志加进去风的个数，用set方法和get方法获取
	 * */
	public void  WindInfer2(ArrayList<String> List,OWLModel model,String maName,String ieTopic) throws OntologyLoadException ,SWRLRuleEngineException {
		String[] WindList = new String[20];// 用来存储抽取出来的符合主题和模版的风
		int WindFromInfoNumber = 0;// 最终抽取的符合的风的数量
		int WindFromTemNumber = 0; // 从模版抽到风的个数
		int WindFromTopNumber = 0;// 从主题抽到风的个数
		String str = "p4:";
	//	String ieTopic;
		ArrayList<String> topicList = new ArrayList<String>();
		Collection mapToWindValues = null;
		
		System.out.println("######################实际上执行的模板和主题######################");
		for(int i = 0; i < List.size(); i++){
			System.out.println(List.get(i));
		}
		System.out.println("############################################################");
//
		OWLIndividual maIndividual = model.getOWLIndividual(maName); // 获取maya实例
		if (null == maIndividual) {
			System.out.println("maya 实例无法获取，可能不存在或丢失！请检查maName是否正确或存在");
			return ;
		}

		OWLObjectProperty addWindToMaProperty = model.getOWLObjectProperty(str + "addWindToMa");
		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");// 此变量用来判断场景属于室内室外

		// 判断
		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
		if (null == plane) {
			System.out.println("无法获取实例" + maIndividual.getBrowserText() + "的hasValueOfPlace属性(此属性用来判断场景属于室内还是室外)，so 拒绝加风！请检查知识库中该属性是否存在或正确？");
			return ;
		} else {
			if (plane.getBrowserText().equals("InWaterDescription")	|| plane.getBrowserText().equals("inDoorDescription")) {
				System.out.println("此场景不适合加风！");
				return ;
			}
		}
		
	// 处理模板信息
		if (WindFromTopNumber == 0) {
			System.out.println("-------------模板处理---------------");
			int listSize = 0;
			
			if (List == null) {
				System.out.println("没有模板传入");
			} else 	{	
				listSize = List.size();
			}
			
			if (listSize > 0) {

				System.out.println("传递进来的模板个数为：" + listSize + "个，分别是：" + List);
				
				windExecuteTemplateToBackgroundSceneSWRLEngine(model, List);
				// 去相应的场景里获取所添加的风
				mapToWindValues = null;
				mapToWindValues = maIndividual.getPropertyValues(addWindToMaProperty);
				
				if (mapToWindValues.isEmpty())
					System.out.println("根据传递过来的模板，没有相应的风！");
				else {
					for (Iterator it1 = mapToWindValues.iterator(); it1.hasNext();) {
						OWLIndividual WindIndiviual = (OWLIndividual) it1.next();
						WindList[WindFromInfoNumber] = WindIndiviual.getBrowserText();
						WindFromInfoNumber++;
						WindFromTemNumber++;
					}
					
					if (WindFromTemNumber != 0) {

						System.out.print("根据模板抽到" + WindFromTemNumber + "个风，分别是：");

						for (int i = 0; i < WindFromTemNumber; i++)
							System.out.print(WindList[i] + " ");

						System.out.println();
					}
				}
			}
			
		}
		System.out.println("-------------模板处理完毕！------------");
		
		// 处理主题信息
		if(WindFromInfoNumber == 0){
			System.out.println("---------------场景的主题处理---------------");
			System.out.println("     --------处理IE抽取的主题-------");
	
	//		ieTopic = doc.getRootElement().element("maName").attributeValue("topic");// 获取IE 主题
			if (ieTopic.contains("Topic")) {
				System.out.println("IE 抽取的主题是：" + ieTopic);
				topicList.add(ieTopic);
				windExecuteTopicToBackgroundSceneSWRLEngine(model, topicList);
				mapToWindValues = maIndividual.getPropertyValues(addWindToMaProperty);
				
				if (mapToWindValues.isEmpty()) {
					System.out.println("根据抽取出的主题，没有对应的风");
				} else {
	
					for (Iterator it1 = mapToWindValues.iterator(); it1.hasNext();) {
	
						OWLIndividual WindIndiviual = (OWLIndividual) it1.next();
						WindList[WindFromInfoNumber] = WindIndiviual.getBrowserText();
						WindFromInfoNumber++;
						WindFromTopNumber++;
					}
	
					if (WindFromTopNumber != 0) {
						System.out.print("根据主题抽取到" + WindFromTopNumber + "个风，分别是：");
	
						for (int i = 0; i < WindFromTopNumber; i++)
							System.out.print(WindList[i] + " ");
	
						System.out.println("\n因主题优先，故模板不作处理！");
					}
				}
			
			} else {
				System.out.println("     ----------IE未抽取出主题--------");
			}
			
			System.out.println("---------------场景主题处理完毕!----------------");
		}
		
		if (WindFromInfoNumber == 0) {
			System.out.println("根据所给条件，没有抽取到适合的风");
			return ;
		} else {
			hasEffect = true;
		}

		// 在获取到的风里面随机选取一个
	}

	public Document printWindRule(Document doc, String WindType,String WindDirection, String WindExpress){// 此方法虽然有方向这个参数，但并未用，因为现在在场景中无法识别东西南北，而写在这是为了以后考虑
		System.out.println("开始生成xml-rule");

		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");

		Element ruleName = name.addElement("rule");

		ruleName.addAttribute("ruleType", "addEffectToMa");
		ruleName.addAttribute("type", "Wind");
		ruleName.addAttribute("Magnitude", WindType);
		ruleName.addAttribute("Direction", WindDirection);
		ruleName.addAttribute("Expression", WindExpress);
		ruleName.addAttribute("ifNeg", Integer.toString(ifNeg));//定性adl输出

		System.out.println("xml-rule生成完毕");
		return doc;
	}
	
	/**
	 **================================= RainInfo 用来执行添加雨=================================
	 **/
	public void  rainExecuteTopicToBackgroundSceneSWRLEngine(OWLModel model,ArrayList<String> templateName) throws SWRLRuleEngineException {
		SWRLRuleEngine ruleEngine = createRuleEngine(model);
		ruleEngine.reset();
		SWRLFactory factory = createSWRLFactory(model);
		factory.disableAll();
		Iterator<SWRLImp> iter = factory.getImps().iterator();
		while(iter.hasNext()) {
			
			SWRLImp imp = (SWRLImp) iter.next();
			if(templateName.size()!=0) {
				if(imp.getLocalName().contains("addRainToMaRule")) {
					for(Iterator<String> its = templateName.iterator();its.hasNext();) {
						
						String templateValue = its.next();
						String templateValue1= templateValue.replaceAll("Individual", "");
						
						if(imp.getBody().getBrowserText().contains(templateValue1)) {
							logger.info("运行的规则名称为："+imp.getLocalName());
							imp.enable();//执行此规则
						}
					}
					
				}
			}
		}
		ruleEngine.infer();
	}
	
	//执行OWL里的规则
	public void rainExecuteTemplateToBackgroundSceneSWRLEngine(OWLModel model,ArrayList<String> templateName) throws SWRLRuleEngineException
	{
		SWRLRuleEngine ruleEngine = createRuleEngine(model);
		ruleEngine.reset();
		SWRLFactory factory = createSWRLFactory(model);
		factory.disableAll();
		Iterator<SWRLImp> iter = factory.getImps().iterator();
		while(iter.hasNext()) {
			SWRLImp imp=(SWRLImp) iter.next();
			
			if(templateName.size()!=0) {
				if(imp.getLocalName().contains("addRainToMaRule")) {//找到名字包含“addRainToSceneRule”的规则
					for(Iterator<String> its=templateName.iterator();its.hasNext();) {
						
						String templateValue= its.next();
						String templateValue1=templateValue.substring(0,templateValue.indexOf(":"));
						
						if(imp.getBody().getBrowserText().contains(templateValue1)) {
							logger.info("运行的规则名称："+imp.getLocalName());
							imp.enable();
						}
					}
					
				}
			}
		}
		ruleEngine.infer();
	}
	
	public  Document RainInfer(ArrayList<String> List,OWLModel model,String maName,Document doc) throws OntologyLoadException, SWRLRuleEngineException
	{
		if(hasEffect)
			return doc;
		String[] RainList =new String[50];//用来存储抽取出来的符合主题和模板的雨
		int RainFromInfoNumber=0;//最终抽取的符合的雨的数量
		int RainFromTemNumber=0;
		int RainFromTopNumber=0;
		String str = "p4:";
		String ieTopic;
		ArrayList<String> topicList = new ArrayList<String>();
	
		Collection mapToRainValues = null;
		
		System.out.println("######################实际上执行的模板和主题######################");
		for(int i = 0; i < List.size(); i++){
			System.out.println(List.get(i));
		}
		System.out.println("############################################################");
		OWLIndividual maIndividual = model.getOWLIndividual(maName);//获取maya实例	
		if(null == maIndividual){
			System.out.println("maya实例无法获取，可能不存在或丢失！请检查maName是否正确或存在？");
			return doc;
		}
		
		//用到的各种属性
//		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
		OWLObjectProperty addRainToMaProperty = model.getOWLObjectProperty(str + "addRainToMa");
		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//此变量用来判断场景属于室内室外

		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
		if(null == plane){
			System.out.println("无法获取实例" + maIndividual.getBrowserText() + "的hasValueOfPlace属性(此属性用来判断场景属于室内还是室外)，so 拒绝加雨！请检查知识库中该属性是否存在或正确？");
			return doc;
		}
		else {
			if(plane.getBrowserText().equals("InWaterDescription") || plane.getBrowserText().equals("inDoorDescription")) {
				System.out.println("此场景不适合加雨！");
				return doc;
			}
		}
		//处理模板信息
		if(0 == RainFromTopNumber){
			System.out.println("==================模板处理====================");
			int listSize = 0;

			if(List.isEmpty()){
				System.out.println("没有模板传入");
			} else {
				listSize = List.size();//模板个数
			}
				
			if(listSize > 0) {
				System.out.println("传递进来的模板个数为：" + listSize + "个，分别是：" + List);
				rainExecuteTemplateToBackgroundSceneSWRLEngine(model,List);
				
				//去相应的场景里获取所添加的雨
				mapToRainValues = null;
				mapToRainValues= maIndividual.getPropertyValues(addRainToMaProperty);
				if(mapToRainValues.isEmpty()){
					System.out.println("根据传递进来的模板，没有相应的雨！");
				} else {
					
					for(Iterator it1=mapToRainValues.iterator();it1.hasNext();) {
						OWLIndividual RainIndiviual =(OWLIndividual)it1.next();
						RainList[RainFromInfoNumber]=RainIndiviual.getBrowserText();
						RainFromInfoNumber++;
						RainFromTemNumber++;
					}
					
					if(0 != RainFromTemNumber) {
						System.out.print("根据模板抽到"+RainFromTemNumber+"个雨，分别是：");
						
						for(int i = 0; i < RainFromTemNumber; i++)
							System.out.print(RainList[i] + " ");
						
						System.out.println();
					}
				}
			}	
		}
		System.out.println("================模板处理完毕！================");
		
		if(RainFromInfoNumber == 0){
			//处理主题信息
			System.out.println("================场景的主题处理================");
			System.out.println("================优先处理IE主题================");
			ieTopic = doc.getRootElement().element("maName").attributeValue("topic");
			
			if(ieTopic.contains("Topic")){
				System.out.println("IE Topic = " + ieTopic);
				topicList.add(ieTopic);
				rainExecuteTopicToBackgroundSceneSWRLEngine(model,topicList);
				mapToRainValues = maIndividual.getPropertyValues(addRainToMaProperty);
				
				if(mapToRainValues.isEmpty()){
					System.out.println("根据抽取出的主题，没有对应的雨");
				} else {
					
					for(Iterator it1 = mapToRainValues.iterator(); it1.hasNext();) {
						OWLIndividual RainIndiviual = (OWLIndividual)it1.next();
						RainList[RainFromInfoNumber] = RainIndiviual.getBrowserText();
						RainFromInfoNumber++;
						RainFromTopNumber++;
					}
						
					if(0 != RainFromTopNumber) {
						System.out.print("根据主题抽取到"+RainFromTopNumber+"个雨，分别是：");
							
						for(int i = 0; i < RainFromTopNumber; i++)
							System.out.print(RainList[i] + " ");
						
						System.out.println();
						System.out.println("因主题优先，故模板不作处理！");
					}
				}
			} else {
				System.out.println("================IE未抽取出主题================");
			}
	
			System.out.println("================场景主题处理完毕================");
	
		}
				
		
		if(0 == RainFromInfoNumber) {
			System.out.println("根据所给条件，没有抽取到适合的雨");
			return doc;
		} else {
			hasEffect = true;
		}
		
		//在获取到的雨里随机选取一个
		java.util.Random random = new Random();
		int rd = random.nextInt(RainFromInfoNumber);
		
		OWLIndividual RainIndiviual = model.getOWLIndividual(RainList[rd]);
		System.out.println("随机选取到的雨为：" + RainIndiviual.getBrowserText());
		
		OWLDatatypeProperty Magnitude =  model.getOWLDatatypeProperty(str + "magnitude");
		String RainType = (String) RainIndiviual.getPropertyValue(Magnitude);
		System.out.println("RainType = " + RainType);
		
		Direction RainDir;
		Direction[] Dir = Direction.values();//dir存储枚举的值
		int num = new Random().nextInt(8);//[0,8]之间的整数
		RainDir = Dir[num];
		String RainDirection = RainDir.toString();
		
		System.out.println("\n雨的类型：" + RainType + "\n雨的方向：" + RainDirection);
		System.out.println("======================信息获取完毕！=====================");
		Document doc1 = printRainRule(doc,RainType,RainDirection,maHasEffect);
		return doc1;
	}
	
	public  void RainInfer2(ArrayList<String> List,OWLModel model,String maName,String ieTopic) throws OntologyLoadException, SWRLRuleEngineException
	{
		if(hasEffect)
			return ;
		String[] RainList =new String[50];//用来存储抽取出来的符合主题和模板的雨
		int RainFromInfoNumber=0;//最终抽取的符合的雨的数量
		int RainFromTemNumber=0;
		int RainFromTopNumber=0;
		String str = "p4:";
		ArrayList<String> topicList = new ArrayList<String>();
	
		Collection mapToRainValues = null;
		
		System.out.println("##RainInfer2####################实际上执行的模板和主题######################");
		for(int i = 0; i < List.size(); i++){
			System.out.println(List.get(i));
		}
		System.out.println("##RainInfer2##########################################################");
		OWLIndividual maIndividual = model.getOWLIndividual(maName);//获取maya实例	
		if(null == maIndividual){
			System.out.println("maya实例无法获取，可能不存在或丢失！请检查maName是否正确或存在？");
			return ;
		}
		
		//用到的各种属性
//		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
		OWLObjectProperty addRainToMaProperty = model.getOWLObjectProperty(str + "addRainToMa");
		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//此变量用来判断场景属于室内室外

		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
		if(null == plane){
			System.out.println("无法获取实例" + maIndividual.getBrowserText() + "的hasValueOfPlace属性(此属性用来判断场景属于室内还是室外)，so 拒绝加雨！请检查知识库中该属性是否存在或正确？");
			return ;
		}
		else {
			if(plane.getBrowserText().equals("InWaterDescription") || plane.getBrowserText().equals("inDoorDescription")) {
				System.out.println("此场景不适合加雨！");
				return ;
			}
		}
		//处理模板信息
		if(0 == RainFromTopNumber){
			System.out.println("===RainInfer2===============模板处理====================");
			int listSize = 0;

			if(List.isEmpty()){
				System.out.println("没有模板传入");
			} else {
				listSize = List.size();//模板个数
			}
				
			if(listSize > 0) {
				System.out.println("传递进来的模板个数为：" + listSize + "个，分别是：" + List);
				rainExecuteTemplateToBackgroundSceneSWRLEngine(model,List);
				
				//去相应的场景里获取所添加的雨
				mapToRainValues = null;
				mapToRainValues= maIndividual.getPropertyValues(addRainToMaProperty);
				if(mapToRainValues.isEmpty()){
					System.out.println("根据传递进来的模板，没有相应的雨！");
				} else {
					
					for(Iterator it1=mapToRainValues.iterator();it1.hasNext();) {
						OWLIndividual RainIndiviual =(OWLIndividual)it1.next();
						RainList[RainFromInfoNumber]=RainIndiviual.getBrowserText();
						RainFromInfoNumber++;
						RainFromTemNumber++;
					}
					
					if(0 != RainFromTemNumber) {
						System.out.print("根据模板抽到"+RainFromTemNumber+"个雨，分别是：");
						
						for(int i = 0; i < RainFromTemNumber; i++)
							System.out.print(RainList[i] + " ");
						
						System.out.println();
					}
				}
			}	
		}
		System.out.println("====RainInfer2============模板处理完毕！================");
		
		if(RainFromInfoNumber == 0){
			//处理主题信息
			System.out.println("====RainInfer2============场景的主题处理================");
			System.out.println("====RainInfer2============优先处理IE主题================");
	//		ieTopic = doc.getRootElement().element("maName").attributeValue("topic");
			
			if(ieTopic.contains("Topic")){
				System.out.println("IE Topic = " + ieTopic);
				topicList.add(ieTopic);
				rainExecuteTopicToBackgroundSceneSWRLEngine(model,topicList);
				mapToRainValues = maIndividual.getPropertyValues(addRainToMaProperty);
				
				if(mapToRainValues.isEmpty()){
					System.out.println("根据抽取出的主题，没有对应的雨");
				} else {
					
					for(Iterator it1 = mapToRainValues.iterator(); it1.hasNext();) {
						OWLIndividual RainIndiviual = (OWLIndividual)it1.next();
						RainList[RainFromInfoNumber] = RainIndiviual.getBrowserText();
						RainFromInfoNumber++;
						RainFromTopNumber++;
					}
						
					if(0 != RainFromTopNumber) {
						System.out.print("根据主题抽取到"+RainFromTopNumber+"个雨，分别是：");
							
						for(int i = 0; i < RainFromTopNumber; i++)
							System.out.print(RainList[i] + " ");
						
						System.out.println();
						System.out.println("因主题优先，故模板不作处理！");
					}
				}
			} else {
				System.out.println("====RainInfer2============IE未抽取出主题================");
			}
	
			System.out.println("====RainInfer2============场景主题处理完毕================");
	
		}
				
		
		if(0 == RainFromInfoNumber) {
			System.out.println("根据所给条件，没有抽取到适合的雨");
			return ;
		} else {
			hasEffect = true;
		}
	}
	
	public Document printRainRule(Document doc,String RainType,String RainDirection,boolean maHasEffect)
	{
		System.out.println("=====================开始生成xml-rule======================");
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "addEffectToMa");
		ruleName.addAttribute("type","Rain");
		ruleName.addAttribute("Magnitude", RainType);
		ruleName.addAttribute("Direction", RainDirection);		
		if(maHasEffect)
			ruleName.addAttribute("existEffect", "true");
		else 
			ruleName.addAttribute("existEffect", "false");
		ruleName.addAttribute("ifNeg",Integer.toString(ifNeg));
		System.out.println("xml-rule生成完毕");
		return doc;
	}	
	
	/**
	 **================================= SnowInfo 用来执行添加雪=================================
	 **/
public void  snowExecuteTopicToBackgroundSceneSWRLEngine(OWLModel model,ArrayList<String> templateName) throws SWRLRuleEngineException {
		
		SWRLRuleEngine ruleEngine = createRuleEngine(model);
		ruleEngine.reset();
		SWRLFactory factory = createSWRLFactory(model);
		factory.disableAll();
		Iterator<SWRLImp> iter = factory.getImps().iterator();
		while(iter.hasNext()) {
			SWRLImp imp = (SWRLImp) iter.next();
			
			if(templateName.size() != 0) {
				if(imp.getLocalName().contains("addSnowToMaRule")) {
					for(Iterator<String> its = templateName.iterator();its.hasNext();) {
						
						String templateValue = its.next();
						String templateValue1 = templateValue.replaceAll("Individual", "");
						
						if(imp.getBody().getBrowserText().contains(templateValue1)) {
							logger.info("运行的规则名称为："+imp.getLocalName());
							imp.enable();//执行此规则
						}
					}
					
				}
			}
		}
		ruleEngine.infer();
	}
	
	//执行OWL里的规则
	public void snowExecuteTemplateToBackgroundSceneSWRLEngine(OWLModel model,ArrayList<String> templateName) throws SWRLRuleEngineException
	{
		SWRLRuleEngine ruleEngine = createRuleEngine(model);
		ruleEngine.reset();
		SWRLFactory factory = createSWRLFactory(model);
		factory.disableAll();
		Iterator<SWRLImp> iter = factory.getImps().iterator();
		
		while(iter.hasNext()) {
			SWRLImp imp=(SWRLImp) iter.next();

			if(templateName.size() != 0){
				if(imp.getLocalName().contains("addSnowToMaRule")) {					//找到名字包含“addSnowToSceneRule”的规则
					for(Iterator<String> its=templateName.iterator();its.hasNext();) {
						
						String templateValue= its.next();
						String templateValue1=templateValue.substring(0,templateValue.indexOf(":"));
						
						if(imp.getBody().getBrowserText().contains(templateValue1))	{
							logger.info("运行的规则名称："+imp.getLocalName());
							imp.enable();
						}
					}
					
				}
			}
		}
		ruleEngine.infer();
	}
	
	public Document SnowInfer(ArrayList<String> List,OWLModel model,String maName,Document doc) throws OntologyLoadException, SWRLRuleEngineException
	{
		if(hasEffect)
			return doc;
		String[] SnowList =new String[50];//用来存储抽取出来的符合主题和模板的雪
		
		int SnowFromInfoNumber=0;//最终抽取的符合的雪的数量
		int SnowFromTemNumber=0;
		int SnowFromTopNumber=0;
		String str="p4:";
		String ieTopic = null;
		ArrayList<String> topicList = new ArrayList<String>();
	//	ArrayList<String> listSnow = new ArrayList<String>();
	//	ArrayList<String> effects = new ArrayList<String>();
		Collection mapToSnowValues = null;
		OWLIndividual maIndividual = model.getOWLIndividual(maName);//获取maya实例
		
//		list 存放的是传递的模版和主题，现对其进行一次处理，看一下里边的主题和模板是否存在topicAndTemplate中。
/*		int number = 0;
		Random rand = new Random();
		for(int i = 0; i < List.size(); i++){
			String strTemp = List.get(i);
			effects.add(strTemp.split(":")[0]);
			if(!topicAndTemplate.contains(effects.get(i))){          //strTemp 存在于topicAndTemplate  以： 隔开，数组第一个值
				number = rand.nextInt(10) + 1;
System.out.println("\n随机数是：" + number);
				if(number > 4){
					listSnow.add(strTemp);
				}
			} else {
				listSnow.add(strTemp);
			}
		}
		
//		effect = hasEffects(effects);

		if(effect >=1 && effect <= 6) {
			if(effect == 3 || effect == 5 || effect == 6) {
				System.out.println("\n 有雪的模板");
			} else {
				System.out.println("\n 无雪的模板");
				return doc;
			}		
		} else {
			if(hasEffect) {
				return doc;
			}
		}
	*/	
		System.out.println("######################实际上执行的模板和主题######################");
		for(int i = 0; i < List.size(); i++){
			System.out.println(List.get(i));
		}
		System.out.println("############################################################");
		if(null == maIndividual){
			System.out.println("maya实例无法获取，可能不存在或丢失！请检查maName是否正确或存在？");
			return doc;
		}
		
		//用到的各种属性
		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
		OWLObjectProperty addSnowToMaProperty = model.getOWLObjectProperty(str + "addSnowToMa");
		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//此变量用来判断场景属于室内室外
		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
		
		if(null == plane){
			System.out.println("无法获取实例" + maIndividual.getBrowserText() + "的hasValueOfPlace属性(此属性用来判断场景属于室内还是室外)，so 拒绝加雪！请检查知识库中该属性是否存在或正确？");
			return doc;
		}
		else {
			if(plane.getBrowserText().equals("InWaterDescription")||plane.getBrowserText().equals("inDoorDescription")) {
				System.out.println("此场景不适合加雪！");
				return doc;
			}
		}
		
		//处理模板信息
		if(SnowFromInfoNumber == 0) {
			System.out.println("==================模板处理====================");
			int listSize = 0;
			if(null != List)
				listSize = List.size();//模板个数
			else
				System.out.println("未传递进来任何模板");
			
			if(listSize > 0) {
				System.out.println("传递进来的模板个数为：" + listSize + "个，分别是：" + List);
				snowExecuteTemplateToBackgroundSceneSWRLEngine(model,List);
				
				//去相应的场景里获取所添加的雪
				mapToSnowValues = null;
				mapToSnowValues= maIndividual.getPropertyValues(addSnowToMaProperty);
				if(mapToSnowValues.isEmpty())
					System.out.println("根据传递进来的模板，没有相应的雪！");
				else {
					
					for(Iterator it1 = mapToSnowValues.iterator();it1.hasNext();) {
						OWLIndividual SnowIndiviual = (OWLIndividual)it1.next();
						SnowList[SnowFromInfoNumber] = SnowIndiviual.getBrowserText();
						SnowFromInfoNumber++;
						SnowFromTemNumber++;
					}
					
					if(0 != SnowFromInfoNumber) {
						System.out.print("根据模板抽到" + SnowFromTemNumber + "个雪，分别是：");
						for(int i = 0; i < SnowFromTemNumber; i++)
							System.out.print(SnowList[i] + " ");
						System.out.println();
					}
				}
			}
			System.out.println("================模板处理完毕！================");
		}
		
		if(SnowFromInfoNumber == 0){
			//处理主题信息
			System.out.println("=================场景的主题处理================");
			System.out.println("===============优先处理IE抽取的主题================");
			
			ieTopic = doc.getRootElement().element("maName").attributeValue("topic");
			if(ieTopic.contains("Topic")){
				System.out.println("IE Topic = " + ieTopic);
				topicList.add(ieTopic);
				snowExecuteTopicToBackgroundSceneSWRLEngine(model,topicList);
				mapToSnowValues = maIndividual.getPropertyValues(addSnowToMaProperty);
				
				if(mapToSnowValues.isEmpty())
					System.out.println("根据抽取出的主题，没有对应的雪");
				else {
						
					for(Iterator it1 = mapToSnowValues.iterator();it1.hasNext();) {
						OWLIndividual SnowIndiviual = (OWLIndividual)it1.next();
						SnowList[SnowFromInfoNumber] = SnowIndiviual.getBrowserText();
						SnowFromInfoNumber++;
						SnowFromTopNumber++;
					}
						
					if(SnowFromInfoNumber != 0) {
						System.out.print("根据主题抽取到"+SnowFromInfoNumber + "个雪，分别是：");
							
						for(int i = 0; i < SnowFromInfoNumber; i++)
							System.out.print(SnowList[i] + " ");
							
					System.out.println("\n因主题优先，故模板不作处理！");
					}
				}
			}else {
				System.out.println("=================IE未抽出主题================");
			}
		
			System.out.println("================场景主题处理完毕================");
		}
			
		if(0 == SnowFromInfoNumber) {
			System.out.println("根据所给条件，没有抽取到适合的雪");
			return doc;
		} else {
			hasEffect = true;
		}

		//在获取到的雪里随机选取一个
		Random random = new Random();
		int rd = random.nextInt(SnowFromInfoNumber);
		
		OWLIndividual SnowIndiviual = model.getOWLIndividual(SnowList[rd]);
		System.out.println("随机选取到的雪为：" + SnowIndiviual.getBrowserText());
		
		OWLDatatypeProperty Magnitude =  model.getOWLDatatypeProperty(str + "magnitude");
		String SnowType = (String) SnowIndiviual.getPropertyValue(Magnitude);
		System.out.println("SnowType = " + SnowType);

		
		Direction SnowDir;
		Direction[] Dir = Direction.values();//dir存储枚举的值
		int num = new Random().nextInt(8);//[0,8]之间的整数
		SnowDir = Dir[num];
		String SnowDirection = SnowDir.toString();
		
		System.out.println("\n雪的类型：" + SnowType + "\n雪的方向：" + SnowDirection);
		System.out.println("======================信息获取完毕！=====================");
		Document doc1 = printSnowRule(doc,SnowType,SnowDirection,maHasEffect);
		return doc1;
	}
	
	public void SnowInfer2(ArrayList<String> List,OWLModel model,String maName,String ieTopic) throws OntologyLoadException, SWRLRuleEngineException
	{
		if(hasEffect)
			return ;
		String[] SnowList =new String[50];//用来存储抽取出来的符合主题和模板的雪
		
		int SnowFromInfoNumber=0;//最终抽取的符合的雪的数量
		int SnowFromTemNumber=0;
		int SnowFromTopNumber=0;
		String str="p4:";
	//	String ieTopic = null;
		ArrayList<String> topicList = new ArrayList<String>();

		Collection mapToSnowValues = null;
		OWLIndividual maIndividual = model.getOWLIndividual(maName);//获取maya实例
		
		System.out.println("######################实际上执行的模板和主题######################");
		for(int i = 0; i < List.size(); i++){
			System.out.println(List.get(i));
		}
		System.out.println("############################################################");
		if(null == maIndividual){
			System.out.println("maya实例无法获取，可能不存在或丢失！请检查maName是否正确或存在？");
			return ;
		}
		
		//用到的各种属性
		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
		OWLObjectProperty addSnowToMaProperty = model.getOWLObjectProperty(str + "addSnowToMa");
		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//此变量用来判断场景属于室内室外
		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
		
		if(null == plane){
			System.out.println("无法获取实例" + maIndividual.getBrowserText() + "的hasValueOfPlace属性(此属性用来判断场景属于室内还是室外)，so 拒绝加雪！请检查知识库中该属性是否存在或正确？");
			return ;
		}
		else {
			if(plane.getBrowserText().equals("InWaterDescription")||plane.getBrowserText().equals("inDoorDescription")) {
				System.out.println("此场景不适合加雪！");
				return ;
			}
		}
		
		//处理模板信息
		if(SnowFromInfoNumber == 0) {
			System.out.println("==================模板处理====================");
			int listSize = 0;
			if(null != List)
				listSize = List.size();//模板个数
			else
				System.out.println("未传递进来任何模板");
			
			if(listSize > 0) {
				System.out.println("传递进来的模板个数为：" + listSize + "个，分别是：" + List);
				snowExecuteTemplateToBackgroundSceneSWRLEngine(model,List);
				
				//去相应的场景里获取所添加的雪
				mapToSnowValues = null;
				mapToSnowValues= maIndividual.getPropertyValues(addSnowToMaProperty);
				if(mapToSnowValues.isEmpty())
					System.out.println("根据传递进来的模板，没有相应的雪！");
				else {
					
					for(Iterator it1 = mapToSnowValues.iterator();it1.hasNext();) {
						OWLIndividual SnowIndiviual = (OWLIndividual)it1.next();
						SnowList[SnowFromInfoNumber] = SnowIndiviual.getBrowserText();
						SnowFromInfoNumber++;
						SnowFromTemNumber++;
					}
					
					if(0 != SnowFromInfoNumber) {
						System.out.print("根据模板抽到" + SnowFromTemNumber + "个雪，分别是：");
						for(int i = 0; i < SnowFromTemNumber; i++)
							System.out.print(SnowList[i] + " ");
						System.out.println();
					}
				}
			}
			System.out.println("================模板处理完毕！================");
		}
		
		if(SnowFromInfoNumber == 0){
			//处理主题信息
			System.out.println("=================场景的主题处理================");
			System.out.println("===============优先处理IE抽取的主题================");
			
		//	ieTopic = doc.getRootElement().element("maName").attributeValue("topic");
			if(ieTopic.contains("Topic")){
				System.out.println("IE Topic = " + ieTopic);
				topicList.add(ieTopic);
				snowExecuteTopicToBackgroundSceneSWRLEngine(model,topicList);
				mapToSnowValues = maIndividual.getPropertyValues(addSnowToMaProperty);
				
				if(mapToSnowValues.isEmpty())
					System.out.println("根据抽取出的主题，没有对应的雪");
				else {
						
					for(Iterator it1 = mapToSnowValues.iterator();it1.hasNext();) {
						OWLIndividual SnowIndiviual = (OWLIndividual)it1.next();
						SnowList[SnowFromInfoNumber] = SnowIndiviual.getBrowserText();
						SnowFromInfoNumber++;
						SnowFromTopNumber++;
					}
						
					if(SnowFromInfoNumber != 0) {
						System.out.print("根据主题抽取到"+SnowFromInfoNumber + "个雪，分别是：");
							
						for(int i = 0; i < SnowFromInfoNumber; i++)
							System.out.print(SnowList[i] + " ");
							
					System.out.println("\n因主题优先，故模板不作处理！");
					}
				}
			}else {
				System.out.println("=================IE未抽出主题================");
			}
		
			System.out.println("================场景主题处理完毕================");
		}
			
		if(0 == SnowFromInfoNumber) {
			System.out.println("根据所给条件，没有抽取到适合的雪");
			return ;
		} else {
			hasEffect = true;
		}
		
	}
	
	public Document printSnowRule(Document doc,String SnowType,String SnowDirection,boolean maHasEffect)
	{
		System.out.println("=====================开始生成xml-rule======================");
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "addEffectToMa");
		ruleName.addAttribute("type","Snow");
		ruleName.addAttribute("Magnitude", SnowType);
		ruleName.addAttribute("Direction", SnowDirection);
		if(maHasEffect)
			ruleName.addAttribute("existEffect", "true");
		else
			ruleName.addAttribute("existEffect", "false");
		ruleName.addAttribute("ifNeg", Integer.toString(ifNeg));
		System.out.println("xml-rule生成完毕");
		return doc;
	}	
	
	public Document runEffect(ArrayList<String> List,OWLModel model,String maName,Document doc){
	
		ArrayList<String> effects = new ArrayList<String>();
		for(int i = 0; i < List.size(); i++){
			String strTemp = List.get(i);
			effects.add(strTemp.split(":")[0]);
		}
		//TODO 20161021
		if(List.get(0).contains("1")){
			ifNeg=1;
		}
		System.out.println("1为否定，0为肯定，否定的值为:"+ifNeg);
		//effect = hasEffects(effects);
		effect = hasEffects(effects,model,maName);  //effct 为整数，代表不同的特效
		Document doc1 = null ;
		Random r = new Random();
		int n = 0;
		int i = 0;
		switch(effect){
		case 0:
			n = 0;break;
		case 1:
			n = 1;break;
		case 2:
			n = 2;break;
		case 3:
			n = 3;break;
		case 4:
			int [] arr1= {1,2};
			i = r.nextInt(2);
			n = arr1[i];
			break;
		case 5:
			int [] arr2= {1,3};
			i = r.nextInt(2);
			n = arr2[i];
			break;
		case 6:
			int [] arr3= {2,3};
			i = r.nextInt(2);
			n = arr3[i];
			break;
		}
		
		try {
			switch(n){
				case 0:{
					doc1 = WindInfer(List, model, maName,doc);
					doc1 = RainInfer(List, model, maName,doc1);
					doc1 = SnowInfer(List, model, maName,doc1);
					System.out.println("n=" + n);
					break;
				}
				case 1:{
					doc1 = WindInfer(List, model, maName,doc);
					System.out.println("n=" + n);
					break;
				}
				case 2:{
					doc1 = RainInfer(List, model, maName,doc);
					System.out.println("n=" + n);
					break;
				}
				case 3:{
					doc1 = SnowInfer(List, model, maName,doc);
					System.out.println("n=" + n);
					break;
				}
			}	
			
		} catch (OntologyLoadException e) {

			e.printStackTrace();
		} catch (SWRLRuleEngineException e) {

			e.printStackTrace();
		}
		return doc1;	
		
	}
	
	public boolean  IsWeather(ArrayList<String> List,OWLModel model,String maName,String ieTopic) throws OntologyLoadException, SWRLRuleEngineException{
		System.out.println("\n=-=-=-=-=-=-=-=-=-WeatherInfer2 start =-=-=-=-=-=-=-=-=-=-=-=-");
		WindInfer2(List, model, maName, ieTopic);
		RainInfer2(List, model, maName, ieTopic);
		SnowInfer2(List, model, maName, ieTopic);
		System.out.println("\n=-=-=-=-=-=-=-=-=-WeatherInfer2 end =-=-=---=-=-=-=-=-=-");
		return hasEffect;
	}
	public static void main(String[] args) throws OntologyLoadException, DocumentException, SWRLRuleEngineException, IOException {

//		boolean flag = true;
//		while (flag){
//			SimpleDateFormat stf = new SimpleDateFormat("yyyyMMddHHmmss");
//			System.out.println(stf.format(new Date()) + ".log");
//			flag = false;
//		}
//		System.out.println("lin hai ");
		String owlpath = "file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl" ;
		OWLModel model = ProtegeOWL.createJenaOWLModelFromURI(owlpath);
		
		ArrayList<String> aList = new ArrayList<String>(); 
//		aList.add("ChristmasDayTopic:ChristmasDayTopic:0");
//		aList.add("SnowTemplate:SpringFestivalTopic:1");
//		aList.add("RainTemplate:SpringFestivalTopic:1");
//		aList.add("WinterTemplate:WinterTemplate:1");
//	aList.add("WinterTemplate:WinterTemplate:0");
	/*aList.add("SnowTemplate:SnowTemplate:1");
		aList.add("WindTemplate:SnowTemplate:1");*/
		aList.add("RainTemplate:smallRainTemplate:1");
		
		
		

		File file = new File("F:/实验室文档/ADL/Effect/test.xml");
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(file);
		Effect  effect = new Effect();
		
		System.out.println("开始!");
	    Document document1;
	    
	    document1 = effect.runEffect(aList, model, "pingpong.ma", document);
	//    document1 = effect.WindInfer(aList, model, "schoolroomOut.ma",document);
	 //   document1 = effect.RainInfer(aList, model, "schoolroomOut.ma",document);
	 //   document1 = effect.SnowInfer(aList, model, "schoolroomOut.ma",document);
	    
	   
	//	String maName = null;
	//	SnowInsert  snow = new SnowInsert();
	//	snow.SnowInfer2(aList, model,"schoolroomOut.ma","MissTopic");
	//	Document document1 = Snow.SnowInfer(aList, model, "Tropical45.ma",document);
//		Snow.SnowInfer2(aList, model, "Tropical45.ma");
		XMLWriter writer = new XMLWriter(new FileWriter("F:/实验室文档/ADL/Effect/testSnow.xml"));
		writer.write(document1); 
		Effect effect2 = new Effect();
		if (effect2.IsWeather(aList, model, "pingpong.ma","TennisActionTopic"))
			System.out.println("true");
		else
			 System.out.println("false");;
		
	//	System.out.println("snow number:" + snow.getSnow());
		writer.close();
		System.out.println("结束！");
	}

}
