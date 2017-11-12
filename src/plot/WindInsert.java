package plot;

import java.io.File;
import java.io.FileWriter;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.dom4j.DocumentException;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

//import wind.WindInsert.Direction;

import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.SystemFrames;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
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

public class WindInsert {

	private int wind = 0;
	//topicAndTemplae 数组存放的是部分主题和模板，其中这些主题和模板并不是每次抽到都要添加特效的用随机函数，随机添加
	private ArrayList<String> topicAndTemplate = new ArrayList<String>(Arrays.asList("SummerTemplate","GladTopic","GladnessTemplate","AngryTopic","PityTopic","PityTemplate","SpringTemplate","AutumnTemplate","MissTemplate"));
	
	public int getWind() {
		return wind;
	}

	public void setWind(int wind) {
		this.wind = wind;
	}

	static Logger logger = Logger.getLogger(WindInsert.class.getName());

	public enum Direction {
		East, West, South, North, SouthEast, NorthEast, SouthWest, NorthWest
	}

	public static SWRLFactory createSWRLFactory(OWLModel model) {
		SWRLFactory factory = new SWRLFactory(model);
		return factory;
	}

	public static SWRLRuleEngine createRuleEngine(OWLModel model)
			throws SWRLRuleEngineException {
		SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create(
				"SWRLJessBridge", model);
		return ruleEngine;

	}

	public void executeTopicToBackgroundSceneSWRLEngine(OWLModel model,
			ArrayList<String> templateName) throws SWRLRuleEngineException {
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
	public void executeTemplateToBackgroundSceneSWRLEngine(OWLModel model,ArrayList<String> templateName) throws SWRLRuleEngineException {
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
						String templateValue1 = templateValue.substring(0,
						templateValue.indexOf(":"));

						if (imp.getBody().getBrowserText().contains(templateValue1)) {
							logger.info("运行的规则名字：" + imp.getLocalName());
							imp.enable();
						}
					}
				}
			}
		}
		ruleEngine.infer();

	}

	public Document WindInfer(ArrayList<String> List, OWLModel model,String maName, Document doc) throws OntologyLoadException,SWRLRuleEngineException {
		String[] WindList = new String[20];// 用来存储抽取出来的符合主题和模版的风
		int WindFromInfoNumber = 0;// 最终抽取的符合的风的数量
		int WindFromTemNumber = 0; // 从模版抽到风的个数
		int WindFromTopNumber = 0;// 从主题抽到风的个数
		String str = "p4:";
		String ieTopic;
		ArrayList<String> topicList = new ArrayList<String>();
		ArrayList<String> list = new ArrayList<String>();
		Collection mapToWindValues = null;
		
//		list 存放的是传递的模版和主题，现对其进行一次处理，看一下里边的主题和模板是否存在topicAndTemplate中。
		int number = 0;
		Random rand = new Random();
		for(int i = 0; i < List.size(); i++){
			String strTemp = List.get(i);
			if(topicAndTemplate.contains(strTemp.split(":")[0])){          //strTemp 存在于topicAndTemplate  以： 隔开，数组第一个值
				number = rand.nextInt(10) + 1;
				if(number > 8){
					list.add(strTemp);
				}
			} else {
				list.add(strTemp);
			}
		}
		
		System.out.println("######################实际上执行的模板和主题######################");
		for(int i = 0; i < list.size(); i++){
			System.out.println(list.get(i));
		}
		System.out.println("############################################################");
//
		OWLIndividual maIndividual = model.getOWLIndividual(maName); // 获取maya实例
		if (null == maIndividual) {
			System.out.println("maya 实例无法获取，可能不存在或丢失！请检查maName是否正确或存在");
			return doc;
		}
//System.out.println("maya实例：");
//System.out.println(maIndividual);
		// 用到的各种属性
		OWLObjectProperty hasTopicProperty = model.getOWLObjectProperty("hasTopic");
		OWLObjectProperty addWindToMaProperty = model.getOWLObjectProperty(str + "addWindToMa");
		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");// 此变量用来判断场景属于室内室外
//System.out.println("hasValueOfPlane==" + hasValueOfPlane);
		// 判断
		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
		if (null == plane) {
			System.out.println("无法获取实例" + maIndividual.getBrowserText() + "的hasValueOfPlace属性(此属性用来判断场景属于室内还是室外)，so 拒绝加风！请检查知识库中该属性是否存在或正确？");
			return doc;
		} else {
			if (plane.getBrowserText().equals("InWaterDescription")	|| plane.getBrowserText().equals("inDoorDescription")) {
				System.out.println("此场景不适合加风！");
				return doc;
			}
		}

		// 处理主题信息
		System.out.println("---------------场景的主题处理---------------");
		System.out.println("     --------优先处理IE抽取的主题-------");

		ieTopic = doc.getRootElement().element("maName").attributeValue("topic");// 获取IE 主题
		if (ieTopic.contains("Topic")) {
			System.out.println("IE 抽取的主题是：" + ieTopic);
			topicList.add(ieTopic);
			executeTopicToBackgroundSceneSWRLEngine(model, topicList);
			mapToWindValues = maIndividual.getPropertyValues(addWindToMaProperty);
//System.out.println("mapToWindValues==" + mapToWindValues);
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
//			System.out.println("     --------处理由ma抽取的主题-------");
//
//			Collection topics = maIndividual.getPropertyValues(hasTopicProperty);
//			if (!topics.isEmpty()) {
//
//				OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);// 将topics转化为数组
//
//				for (int i = 0; i < IdTopics.length; i++) {
//					topicList.add(IdTopics[i].getBrowserText());
//				}
//
//				int topicSize = topicList.size();
//				if (topicSize > 0) {
//					System.out.print("抽取出来的主题个数为：" + topicSize + "个，是：");
//
//					for (int i = 0; i < IdTopics.length; i++) {
//						System.out.print(IdTopics[i].getBrowserText() + " ");
//					}
//
//					System.out.println();
//				}
//				executeTopicToBackgroundSceneSWRLEngine(model, topicList);
//
//			}
		}

		
		System.out.println("---------------场景主题处理完毕!----------------");

		// 处理模板信息
		if (WindFromTopNumber == 0) {
			System.out.println("-------------模板处理---------------");
			int listSize = 0;
			if (list != null) {
				listSize = list.size();// 模板个数
			} else
				System.out.println("未传递进来任何模板");

			if (listSize > 0) {

				System.out.println("传递进来的模板个数为：" + listSize + "个，分别是：" + list);
				executeTemplateToBackgroundSceneSWRLEngine(model, list);
				// 去相应的场景里获取所添加的风
				mapToWindValues = null;
				mapToWindValues = maIndividual.getPropertyValues(addWindToMaProperty);
//System.out.println("maptoWindValues == " + mapToWindValues);
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
			System.out.println("-------------模板处理完毕！------------");
		}

		if (WindFromInfoNumber == 0) {
			System.out.println("根据所给条件，没有抽取到适合的风");
			return doc;
		}

		// 在获取到的风里面随机选取一个

		java.util.Random random = new Random();
		int rd = random.nextInt(WindFromInfoNumber);

		OWLIndividual WindIndiviual = model.getOWLIndividual(WindList[rd]);
		System.out.println("随机选取到的风为：" + WindIndiviual.getBrowserText());

	//	int strLength = WindIndiviual.getBrowserText().length();

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
		String[] WindList =new String[50];//用来存储抽取出来的符合主题和模版的风
		int WindFromInfoNumber=0;//最终抽取的符合的风的数量

		String str="p4:";

		ArrayList<String> topicList = new ArrayList<String>();
		ArrayList<String> list = new ArrayList<String>();
		Collection mapToWindValues = null;
		
		int number = 0;
		Random rand = new Random();
		for(int i = 0; i < List.size(); i++){
			String strTemp = List.get(i);
			if(topicAndTemplate.contains(strTemp.split(":")[0])){          //strTemp 存在于topicAndTemplate  以： 隔开，数组第一个值
				number = rand.nextInt(10) + 1;
				if(number > 8){
					list.add(strTemp);
				}
			} else {
				list.add(strTemp);
			}
		}
		OWLIndividual maIndividual =model.getOWLIndividual(maName);//获取maya实例
		if(null == maIndividual) {
			System.out.println("maya 实例无法获取，可能不存在或丢失！请检查maName是否正确或存在");
				return;
			}
				
			//用到的各种属性
			OWLObjectProperty hasTopicProperty = model.getOWLObjectProperty("hasTopic");
			OWLObjectProperty addWindToMaProperty = model.getOWLObjectProperty(str+"addWindToMa");
			OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//此变量用来判断场景属于室内室外
				
			//判断
			OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
			if(null == plane) {
				System.out.println("无法获取实例" + maIndividual.getBrowserText() + "的hasValueOfPlace属性,此属性用来判断场景属于室内还是室外，请检查知识库中该属性是否存在或正确？");
				WindFromInfoNumber=0;//获取抽到风的数量
				return;
			}else {
				if(plane.getBrowserText().equals("InWaterDescription")||plane.getBrowserText().equals("inDoorDescription")) {
					System.out.println ("此场景不适合加风！");
					WindFromInfoNumber=0;//获取抽到风的数量
					return;
				}
			}
				
			//处理主题信息
			System.out.println("---------------场景的主题处理---------------");	
			System.out.println("     --------优先处理IE抽取的主题-------");
		
			if(ieTopic.contains("Topic")){
				System.out.println("IE 抽取的主题是：" + ieTopic);
				topicList.add(ieTopic);
				executeTopicToBackgroundSceneSWRLEngine(model,topicList);
				mapToWindValues = maIndividual.getPropertyValues(addWindToMaProperty);
			if(mapToWindValues.isEmpty()) {
				System.out.println("根据抽取出的主题，没有对应的风");
				WindFromInfoNumber=0;
			} else {
						
				for(Iterator it1=mapToWindValues .iterator();it1.hasNext();) {
					OWLIndividual WindIndiviual = (OWLIndividual)it1.next();
					WindList[WindFromInfoNumber] = WindIndiviual.getBrowserText();
					WindFromInfoNumber++;
					
				}
				System.out.println("由主题抽出" + WindFromInfoNumber + "个风！");
			}

			} else {
				System.out.println("     ----------IE未抽取出主题--------");
//				System.out.println("     --------处理由ma抽取的主题-------");	
//				Collection topics=maIndividual.getPropertyValues(hasTopicProperty);
//				if(!topics.isEmpty()) {
//						
//					OWLIndividual[] IdTopics =(OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//将topics转化为数组
//					
//					for (int i=0;i<IdTopics.length;i++){
//						topicList.add(IdTopics[i].getBrowserText());
//					}
//						
//					executeTopicToBackgroundSceneSWRLEngine(model,topicList);
//				}
			}
				
			
			System.out.println("---------------场景主题处理完毕!----------------");
				
			//处理模板信息
			if(WindFromInfoNumber == 0) {
				System.out.println("-------------模板处理---------------");
					
				if(list.size()> 0) {
						
					executeTemplateToBackgroundSceneSWRLEngine(model ,list);
					//去相应的场景里获取所添加的风
					mapToWindValues = null;
					mapToWindValues = maIndividual.getPropertyValues(addWindToMaProperty);
					if(mapToWindValues.isEmpty()){
						WindFromInfoNumber=0;
						System.out.println("	-------根据传递过来的模板，没有相应的风！-------");
					}
					else {
							
						for(Iterator it1=mapToWindValues.iterator();it1.hasNext();) {
							OWLIndividual WindIndiviual =(OWLIndividual)it1.next();
							WindList[WindFromInfoNumber]=WindIndiviual .getBrowserText();
							WindFromInfoNumber++;
						
						}
							System.out.println("由模板抽出" + WindFromInfoNumber + "个风！");	
					}
						
				}
				System.out.println("-------------模板处理完毕！------------");
			}
		setWind(WindFromInfoNumber);//获取抽到风的数量
	}

	public Document printWindRule(Document doc, String WindType,
			String WindDirection, String WindExpress)// 此方法虽然有方向这个参数，但并未用，因为现在在场景中无法识别东西南北，而写在这是为了以后考虑
	{
		System.out.println("开始生成xml-rule");

		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");

		Element ruleName = name.addElement("rule");

		ruleName.addAttribute("ruleType", "addEffectToMa");
		ruleName.addAttribute("type", "Wind");
		ruleName.addAttribute("Magnitude", WindType);
		ruleName.addAttribute("Direction", WindDirection);
		ruleName.addAttribute("Expression", WindExpress);

		System.out.println("xml-rule生成完毕");
		return doc;
	}

	public static void main(String[] args) throws ParserConfigurationException,SAXException, IOException, JDOMException, DocumentException,OntologyLoadException, SWRLRuleEngineException {

		// String owlPath
		// ="file:///F://学习//MyProjects//Protege//LHH//Wind2.owl";
		String owlpath = "file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";
		OWLModel model = ProtegeOWL.createJenaOWLModelFromURI(owlpath);
		ArrayList<String> aList = new ArrayList();
		 aList.add("WindTemplate:WindTemplate");
		 aList.add("SummerTemplate:summerTemplate");
		 aList.add("MissTemplate:Misstemplate");
		aList.add("GladnessTemplate:gladnessTemplate");
		// aList.add("SummerTemplate:summerTemplate");
		// File file =new File("f:\\test.xml");
		File file = new File("f:\\4707.xml");
		// File file =new
		// File("F:\\学习\\MyProjects\\module\\module\\cal\\inputFile\\adl_result.xml");
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(file);
//=========================================================	    
		WindInsert wind = new WindInsert();
		System.out.println("开始！");
		Document document1 = wind.WindInfer(aList, model, "riverFallingTree.ma",document);
		XMLWriter writer = new XMLWriter(new FileWriter("f:\\test1.xml"));
		writer.close();
		System.out.println("结束!");
//====================================================================		
		
	    WindInsert WIND = new WindInsert();
		 WIND.WindInfer2(aList, model, "riverFallingTree.ma","");
		 System.out.println("Wind num : " + WIND.getWind());
//===========================================================================
	}
}

////import java.io.File;
////import java.io.FileWriter;
////import org.dom4j.Document;
////import org.dom4j.Element;
////import org.dom4j.io.SAXReader;
////import org.dom4j.io.XMLWriter;
////import java.io.IOException;
////import java.util.ArrayList;
////import java.util.Collection;
////import java.util.Iterator;
////import java.util.Random;
////import java.util.logging.Logger;
////import javax.xml.parsers.ParserConfigurationException;
////import org.dom4j.DocumentException;
////import org.jdom.JDOMException;
////import org.xml.sax.SAXException;
////
////import com.hp.hpl.jena.util.FileUtils;
////
////import edu.stanford.smi.protege.exception.OntologyLoadException;
////import edu.stanford.smi.protege.model.SystemFrames;
////import edu.stanford.smi.protegex.owl.ProtegeOWL;
////import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
////import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
////import edu.stanford.smi.protegex.owl.model.OWLIndividual;
////import edu.stanford.smi.protegex.owl.model.OWLModel;
////import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
////import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
////import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
////import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngineFactory;
////import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;
////import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
////import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
//public class WindInsert {
//	private int wind = 0;
//	
//	public int getWind() {
//		return wind;
//	}
//	
//	public void setWind(int wind) {
//		this.wind = wind;
//	}
//
//	static Logger logger=Logger.getLogger(WindInsert.class.getName());
//	
//	public enum Direction{
//		East,West,South,North,SouthEast,NorthEast,SouthWest,NorthWest
//	}
//	
//	public static SWRLFactory createSWRLFactory(OWLModel model)
//	{
//		SWRLFactory factory = new SWRLFactory(model);
//		return factory;
//	}
//
//	public static SWRLRuleEngine createRuleEngine(OWLModel model) throws SWRLRuleEngineException
//	{
//		SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create("SWRLJessBridge",model);
//		return ruleEngine;
//		
//	}
//
//	public void executeTopicToBackgroundSceneSWRLEngine(OWLModel model,ArrayList<String> templateName) throws SWRLRuleEngineException
//	{
//		SWRLRuleEngine ruleEngine = createRuleEngine(model);
//		ruleEngine.reset();
//		SWRLFactory factory = createSWRLFactory(model);
//		factory.disableAll();
//		Iterator<SWRLImp> iter = factory.getImps().iterator();
//		while(iter.hasNext())
//		{
//			SWRLImp imp=(SWRLImp) iter.next();
//			
//			if (templateName.size()!=0) {				
//				if(imp.getLocalName().contains("addWindToMaRule")) {					//找到名为addWindtoMaRule的规则
//					for(Iterator<String> its = templateName.iterator();its.hasNext();) {
//						
//						String templateValue = its.next();
//						String templateValue1 = templateValue.replaceAll("Individual", "");
//						
//						if(imp.getBody().getBrowserText().contains(templateValue1))	{
//							logger.info("运行的规则名字为："+imp.getLocalName());
//							imp.enable();//执行此规则
//						}
//					}
//				}
//			}
//		}
//		ruleEngine.infer();
//	}
//	
//	//执行OWL里的规则
//	public void executeTemplateToBackgroundSceneSWRLEngine(OWLModel model,ArrayList<String> templateName) throws SWRLRuleEngineException
//	{
//		SWRLRuleEngine ruleEngine = createRuleEngine(model);
//		ruleEngine.reset();
//		SWRLFactory factory = createSWRLFactory(model);
//		factory.disableAll();
//		Iterator<SWRLImp> iter = factory.getImps().iterator();
//		
//		while (iter.hasNext()) {
//			
//			SWRLImp imp=(SWRLImp) iter.next();
//
//			if(templateName.size()!=0) {
//				if(imp.getLocalName().contains("addWindToMaRule")) {		//找到名字包含"addWindToSceneRule"的规则
//					for(Iterator<String> its = templateName.iterator();its.hasNext();) {
//						
//						String templateValue = its.next();
//						String templateValue1 =templateValue.substring(0, templateValue.indexOf(":"));
//						
//						if(imp.getBody().getBrowserText().contains(templateValue1)) {
//							logger.info("运行的规则名字："+imp.getLocalName());
//							imp.enable();
//						}
//					}
//				}
//			}
//		}
//		ruleEngine.infer();
//		
//	}
//	
//	public Document WindInfer(ArrayList<String> list, OWLModel model,String maName, Document doc) throws OntologyLoadException,SWRLRuleEngineException {
//		String[] WindList = new String[50];// 用来存储抽取出来的符合主题和模版的风
//		int WindFromInfoNumber = 0;// 最终抽取的符合的风的数量
//		int WindFromTemNumber = 0; // 从模版抽到风的个数
//		int WindFromTopNumber = 0;// 从主题抽到风的个数
//		String str = "p4:";
//		String ieTopic;
//		ArrayList<String> topicList = new ArrayList<String>();
//		Collection mapToWindValues = null;
//
//		OWLIndividual maIndividual = model.getOWLIndividual(maName); // 获取maya实例
//		if (null == maIndividual) {
//			System.out.println("maya 实例无法获取，可能不存在或丢失！请检查maName是否正确或存在");
//			return doc;
//		}
//
//		// 用到的各种属性
//		OWLObjectProperty hasTopicProperty = model.getOWLObjectProperty("hasTopic");
//		OWLObjectProperty addWindToMaProperty = model.getOWLObjectProperty(str + "addWindToMa");
//		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");// 此变量用来判断场景属于室内室外
//
//		// 判断
//		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
//		if (null == plane) {
//			System.out.println("无法获取实例" + maIndividual.getBrowserText() + "的hasValueOfPlace属性(此属性用来判断场景属于室内还是室外)，so 拒绝加风！请检查知识库中该属性是否存在或正确？");
//			return doc;
//		} else {
//			if (plane.getBrowserText().equals("InWaterDescription")	|| plane.getBrowserText().equals("inDoorDescription")) {
//				System.out.println("此场景不适合加风！");
//				return doc;
//			}
//		}
//
//		// 处理主题信息
//		System.out.println("---------------场景的主题处理---------------");
//		System.out.println("     --------优先处理IE抽取的主题-------");
//
//		ieTopic = doc.getRootElement().element("maName").attributeValue("topic");// 获取IE 主题
//		if (ieTopic.contains("Topic")) {
//			System.out.println("IE 抽取的主题是：" + ieTopic);
//			topicList.add(ieTopic);
//			executeTopicToBackgroundSceneSWRLEngine(model, topicList);
//		} else {
//			System.out.println("     ----------IE未抽取出主题--------");
//			System.out.println("     --------处理由ma抽取的主题-------");
//
//			Collection topics = maIndividual.getPropertyValues(hasTopicProperty);
//			if (!topics.isEmpty()) {
//
//				OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);// 将topics转化为数组
//
//				for (int i = 0; i < IdTopics.length; i++) {
//					topicList.add(IdTopics[i].getBrowserText());
//				}
//
//				int topicSize = topicList.size();
//				if (topicSize > 0) {
//					System.out.print("抽取出来的主题个数为：" + topicSize + "个，是：");
//
//					for (int i = 0; i < IdTopics.length; i++) {
//						System.out.print(IdTopics[i].getBrowserText() + " ");
//					}
//
//					System.out.println();
//				}
//				executeTopicToBackgroundSceneSWRLEngine(model, topicList);
//
//			}
//		}
//
//		mapToWindValues = maIndividual.getPropertyValues(addWindToMaProperty);
//
//		if (mapToWindValues.isEmpty()) {
//			System.out.println("根据抽取出的主题，没有对应的风");
//		} else {
//
//			for (Iterator it1 = mapToWindValues.iterator(); it1.hasNext();) {
//
//				OWLIndividual WindIndiviual = (OWLIndividual) it1.next();
//				WindList[WindFromInfoNumber] = WindIndiviual.getBrowserText();
//				WindFromInfoNumber++;
//				WindFromTopNumber++;
//			}
//
//			if (WindFromTopNumber != 0) {
//				System.out.print("根据主题抽取到" + WindFromTopNumber + "个风，分别是：");
//
//				for (int i = 0; i < WindFromTopNumber; i++)
//					System.out.print(WindList[i] + " ");
//
//				System.out.println("\n因主题优先，故模板不作处理！");
//			}
//		}
//
//		System.out.println("---------------场景主题处理完毕!----------------");
//
//		// 处理模板信息
//		if (WindFromTopNumber == 0) {
//			System.out.println("-------------模板处理---------------");
//			int listSize = 0;
//			if (list != null) {
//				listSize = list.size();// 模板个数
//			} else
//				System.out.println("未传递进来任何模板");
//
//			if (listSize > 0) {
//
//				System.out.println("传递进来的模板个数为：" + listSize + "个，分别是：" + list);
//				executeTemplateToBackgroundSceneSWRLEngine(model, list);
//				// 去相应的场景里获取所添加的风
//				mapToWindValues = null;
//				mapToWindValues = maIndividual.getPropertyValues(addWindToMaProperty);
//				if (mapToWindValues.isEmpty())
//					System.out.println("根据传递过来的模板，没有相应的风！");
//				else {
//
//					for (Iterator it1 = mapToWindValues.iterator(); it1.hasNext();) {
//						OWLIndividual WindIndiviual = (OWLIndividual) it1.next();
//						WindList[WindFromInfoNumber] = WindIndiviual.getBrowserText();
//						WindFromInfoNumber++;
//						WindFromTemNumber++;
//					}
//
//					if (WindFromTemNumber != 0) {
//
//						System.out.print("根据模板抽到" + WindFromTemNumber + "个风，分别是：");
//
//						for (int i = 0; i < WindFromTemNumber; i++)
//							System.out.print(WindList[i] + " ");
//
//						System.out.println();
//					}
//				}
//			}
//			System.out.println("-------------模板处理完毕！------------");
//		}
//
//		if (WindFromInfoNumber == 0) {
//			System.out.println("根据所给条件，没有抽取到适合的风");
//			return doc;
//		}
//
//		// 在获取到的风里面随机选取一个
//
//		java.util.Random random = new Random();
//		int rd = random.nextInt(WindFromInfoNumber);
//
//		OWLIndividual WindIndiviual = model.getOWLIndividual(WindList[rd]);
//		System.out.println("随机选取到的风为：" + WindIndiviual.getBrowserText());
//
//
//		String WindType = null;
//		String WindDirection = "";
//		String WindExpress = "";// 模板为情绪时，用哪种表现方式
//		OWLDatatypeProperty Magnitude =  model.getOWLDatatypeProperty(str + "magnitude");
//		WindType = (String) WindIndiviual.getPropertyValue(Magnitude);
//		System.out.println("WindType = " + WindType);
//		
//		if (WindIndiviual.getBrowserText().contains("Emotion")) {
//
//			if (WindType.equals("Heavy")) {
//				WindExpress = "Conic";// 圆锥
//			} else if (WindType.equals("Moderate")) {
//				WindExpress = "Helix";// 螺旋
//			} else if (WindType.equals("Light")) {
//				WindExpress = "Cardioid";// 心形线
//			}
//
//		} else {
//			Direction WindDir;
//			Direction[] Dir = Direction.values();// Dir存储枚举的值，
//			int num = new Random().nextInt(8);// [0,8)之间的整数
//			WindDir = Dir[num];
//			WindDirection = WindDir.toString();
//		}
//
//		System.out.println("\n风的类型： " + WindType + "\n风的方向： " + WindDirection
//				+ "\n风的表现手法：" + WindExpress + "\n------信息获取完毕！------");
//
//		Document doc1 = printWindRule(doc, WindType, WindDirection, WindExpress);
//		return doc1;
//	}
//	
//	/* ***********************************************************************************
//	 							此方法用于场景中是否加进去风
//	  		WindFromInfoNumber 标志加进去风的个数，用set方法和get方法获取
//	*************************************************************************************/
//
//	public void  WindInfer2(ArrayList<String> list,OWLModel model,String maName,String ieTopic) throws OntologyLoadException ,SWRLRuleEngineException {
//		String[] WindList =new String[50];//用来存储抽取出来的符合主题和模版的风
//		int WindFromInfoNumber=0;//最终抽取的符合的风的数量
//
//		String str="p4:";
//
//		ArrayList<String> topicList = new ArrayList<String>();
//		Collection mapToWindValues = null;
//				
//		OWLIndividual maIndividual =model.getOWLIndividual(maName);//获取maya实例
//		if(null == maIndividual) {
//			System.out.println("maya 实例无法获取，可能不存在或丢失！请检查maName是否正确或存在");
//				return;
//			}
//				
//			//用到的各种属性
//			OWLObjectProperty hasTopicProperty = model.getOWLObjectProperty("hasTopic");
//			OWLObjectProperty addWindToMaProperty = model.getOWLObjectProperty(str+"addWindToMa");
//			OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//此变量用来判断场景属于室内室外
//				
//			//判断
//			OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
//			if(null == plane) {
//				System.out.println("无法获取实例" + maIndividual.getBrowserText() + "的hasValueOfPlace属性,此属性用来判断场景属于室内还是室外，请检查知识库中该属性是否存在或正确？");
//				WindFromInfoNumber=0;//获取抽到风的数量
//				return;
//			}else {
//				if(plane.getBrowserText().equals("InWaterDescription")||plane.getBrowserText().equals("inDoorDescription")) {
//					System.out.println ("此场景不适合加风！");
//					WindFromInfoNumber=0;//获取抽到风的数量
//					return;
//				}
//			}
//				
//			//处理主题信息
//			System.out.println("---------------场景的主题处理---------------");	
//			System.out.println("     --------优先处理IE抽取的主题-------");
//		
//			if(ieTopic.contains("Topic")){
//				System.out.println("IE 抽取的主题是：" + ieTopic);
//				topicList.add(ieTopic);
//				executeTopicToBackgroundSceneSWRLEngine(model,topicList);
//			} else {
//				System.out.println("     ----------IE未抽取出主题--------");
//				System.out.println("     --------处理由ma抽取的主题-------");	
//				Collection topics=maIndividual.getPropertyValues(hasTopicProperty);
//				if(!topics.isEmpty()) {
//						
//					OWLIndividual[] IdTopics =(OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//将topics转化为数组
//					
//					for (int i=0;i<IdTopics.length;i++){
//						topicList.add(IdTopics[i].getBrowserText());
//					}
//						
//					executeTopicToBackgroundSceneSWRLEngine(model,topicList);
//				}
//			}
//				
//			mapToWindValues = maIndividual.getPropertyValues(addWindToMaProperty);
//			if(mapToWindValues.isEmpty()) {
//				System.out.println("根据抽取出的主题，没有对应的风");
//				WindFromInfoNumber=0;
//			} else {
//						
//				for(Iterator it1=mapToWindValues .iterator();it1.hasNext();) {
//					OWLIndividual WindIndiviual = (OWLIndividual)it1.next();
//					WindList[WindFromInfoNumber] = WindIndiviual.getBrowserText();
//					WindFromInfoNumber++;
//				}
//			}
//
//			System.out.println("---------------场景主题处理完毕!----------------");
//				
//			//处理模板信息
//			if(WindFromInfoNumber == 0) {
//				System.out.println("-------------模板处理---------------");
//					
//				if(list.size()> 0) {
//						
//					executeTemplateToBackgroundSceneSWRLEngine(model ,list);
//					//去相应的场景里获取所添加的风
//					mapToWindValues = null;
//					mapToWindValues = maIndividual.getPropertyValues(addWindToMaProperty);
//					if(mapToWindValues.isEmpty()){
//						
//						System.out.println("	-------根据传递过来的模板，没有相应的风！-------");
//					}
//					else {
//							
//						for(Iterator it1=mapToWindValues.iterator();it1.hasNext();) {
//							OWLIndividual WindIndiviual =(OWLIndividual)it1.next();
//							WindList[WindFromInfoNumber]=WindIndiviual .getBrowserText();
//							WindFromInfoNumber++;
//						}
//							
//					}
//						
//				}
//				System.out.println("-------------模板处理完毕！------------");
//			}
//			System.out.println("WindFromInfoNumber"+WindFromInfoNumber);
//		setWind(WindFromInfoNumber);//获取抽到风的数量
//	}
//	
//
//		public Document printWindRule(Document doc,String WindType,String WindDirection, String WindExpress)//此方法虽然有方向这个参数，但并未用，因为现在在场景中无法识别东西南北，而写在这是为了以后考虑
//		{	
//			System.out.println("开始生成xml-rule");
//			
//			Element rootName=(Element) doc.getRootElement();
//			Element name=rootName.element("maName");
//			Element ruleName=name.addElement("rule");
//			
//			ruleName.addAttribute("ruleType","addEffectToMa");
//			ruleName.addAttribute("type","Wind");
//			ruleName.addAttribute("Magnitude", WindType);
//		    ruleName.addAttribute("Direction", WindDirection);
//		    ruleName.addAttribute("Expression", WindExpress);
//		    
//			System.out.println("xml-rule生成完毕");
//			return doc;
//		}
//		
//		public static void main(String[] args) throws ParserConfigurationException,SAXException,IOException,JDOMException,DocumentException,OntologyLoadException,SWRLRuleEngineException
//		{
//			
//			//String owlPath ="file:///F://学习//MyProjects//Protege//LHH//Wind2.owl";
//			String owlpath="file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";
//			OWLModel model =ProtegeOWL.createJenaOWLModelFromURI(owlpath);
//			ArrayList<String> aList =new ArrayList();
//			aList.add("WindTemplate:WindTemplate");
//			aList.add("SummerTemplate:summerTemplate");
//			aList.add("AngryTopic:angryTopic");
//			aList.add("GladnessTemplate:gladnessTemplate");
//			//aList.add("SummerTemplate:summerTemplate");
//			File file =new File("f:\\test.xml");
//		//	File file =new File("F:\\学习\\MyProjects\\module\\module\\cal\\inputFile\\adl_result.xml");
//			SAXReader saxReader=new SAXReader();
//			Document document =saxReader.read(file);
//			WindInsert wind = new WindInsert();
//			System.out.println("开始！");
//			Document document1=wind.WindInfer(aList,model,"kindergarten.ma",document);
//			
//		//	Document document1=wind.WindInfer(aList,model,"Tropical45.ma",document);
//			wind.WindInfer2(aList,model,"kindergarten.ma","MissTopic");
//			XMLWriter writer =new XMLWriter(new FileWriter("f:\\test1.xml"));
//		//	XMLWriter writer =new XMLWriter (new FileWriter("F:\\学习\\MyProjects\\module\\module\\cal\\inputFile\\adl_resultlhh.xml"));
//			writer.write(document1);
//			System.out.println("结束!");
//			System.out.println("Wind num : " + wind.getWind());
//			writer.close();
//		}
//}
