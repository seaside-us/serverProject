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
import java.util.Random;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.dom4j.DocumentException;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;
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
public class RainInsert {
	private  int rain = 0;
	private ArrayList<String> topicAndTemplate = new ArrayList<String>(Arrays.asList("SummerTemplate","SadTopic","SadTemplate","SpringTemplate","AutumnTemplate"));
	
	static Logger logger = Logger.getLogger(RainInsert.class.getName());
	
	public int getRain() {
		return rain;
	}

	public void setRain(int rain) {
		this.rain = rain;
	}
	
	public enum Direction {
		East,West,South,North,SouthEast,NorthEast,SouthWest,NorthWest
	}
	
	public  static SWRLFactory createSWRLFactory(OWLModel model) {
		SWRLFactory factory = new SWRLFactory(model);
		return factory;
	}
	
	public static SWRLRuleEngine createRuleEngine(OWLModel model) throws SWRLRuleEngineException {
		SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create("SWRLJessBridge",model);
		return ruleEngine;
	}
	
	public void  executeTopicToBackgroundSceneSWRLEngine(OWLModel model,ArrayList<String> templateName) throws SWRLRuleEngineException {
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
	public void executeTemplateToBackgroundSceneSWRLEngine(OWLModel model,ArrayList<String> templateName) throws SWRLRuleEngineException
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
		String[] RainList =new String[50];//用来存储抽取出来的符合主题和模板的雨
		int RainFromInfoNumber=0;//最终抽取的符合的雨的数量
		int RainFromTemNumber=0;
		int RainFromTopNumber=0;
		String str = "p4:";
		String ieTopic;
		ArrayList<String> topicList = new ArrayList<String>();
		ArrayList<String> list = new ArrayList<String>();
		Collection mapToRainValues = null;
		
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
		OWLIndividual maIndividual = model.getOWLIndividual(maName);//获取maya实例	
		if(null == maIndividual){
			System.out.println("maya实例无法获取，可能不存在或丢失！请检查maName是否正确或存在？");
			return doc;
		}
		
		//用到的各种属性
		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
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
		
		//处理主题信息
		System.out.println("================场景的主题处理================");
		System.out.println("================优先处理IE主题================");
		ieTopic = doc.getRootElement().element("maName").attributeValue("topic");
		
		if(ieTopic.contains("Topic")){
			System.out.println("IE Topic = " + ieTopic);
			topicList.add(ieTopic);
			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
		} else {
			System.out.println("================IE未抽取出主题================");
			System.out.println("================处理ma抽取的主题================");
			Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);		
			if(!topics.isEmpty()){
				OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//将topics转化为数组
				
				for(int i = 0; i < IdTopics.length; i++) {
					topicList.add(IdTopics[i].getBrowserText());
				}
				
				int topicSize = topicList.size();
				if(topicSize>0) {
					System.out.print("抽取出来的主题个数为：" + topicSize + "个，是：");
					
					for(int i = 0; i < IdTopics.length; i++) {
						System.out.print(IdTopics[i].getBrowserText() + " ");
					}
					
					System.out.println();
				}
			}
			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
		}
		
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
				

		System.out.println("================场景主题处理完毕================");
		
		//处理模板信息
		if(0 == RainFromTopNumber)
		{
			System.out.println("==================模板处理====================");
			int listSize = 0;
			if(null != list)
				listSize = list.size();//模板个数
			else
				System.out.println("未传递进来任何模板");
			
			if(listSize > 0) {
				System.out.println("传递进来的模板个数为：" + listSize + "个，分别是：" + list);
				executeTemplateToBackgroundSceneSWRLEngine(model,list);
				
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
			System.out.println("================模板处理完毕！================");
		}
		
		if(0 == RainFromInfoNumber) {
			System.out.println("根据所给条件，没有抽取到适合的雨");
			return doc;
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
		Document doc1 = printRainRule(doc,RainType,RainDirection);
		return doc1;
	}
	
	public  void RainInfer2(ArrayList<String> List,OWLModel model,String maName,String ieTopic) throws OntologyLoadException, SWRLRuleEngineException
	{
		String[] RainList =new String[50];//用来存储抽取出来的符合主题和模板的雨
		int RainFromInfoNumber=0;//最终抽取的符合的雨的数量
		String str = "p4:";
		ArrayList<String> topicList = new ArrayList<String>();
		ArrayList<String> list = new ArrayList<String>();
		Collection mapToRainValues = null;
		
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
		OWLIndividual maIndividual = model.getOWLIndividual(maName);//获取maya实例
		
		if(null == maIndividual){
			System.out.println("maya 实例无法获取，可能不存在或丢失！请检查maName是否正确或存在");
			RainFromInfoNumber=0;
			return;
		}
		
		//用到的各种属性
		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
		OWLObjectProperty addRainToMaProperty = model.getOWLObjectProperty(str + "addRainToMa");
		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//此变量用来判断场景属于室内室外

		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
		if(null == plane){
			System.out.println("无法获取实例" + maIndividual.getBrowserText() + "的hasValueOfPlace属性(此属性用来判断场景属于室内还是室外)，so 拒绝加雨！请检查知识库中该属性是否存在或正确？");
			return;
		}else {
			if(plane.getBrowserText().equals("InWaterDescription") || plane.getBrowserText().equals("inDoorDescription")) {
				System.out.println("此场景不适合加雨！");
				return;
			}
		}
		
		//处理主题信息
		System.out.println("================场景的主题处理================");
		System.out.println("================优先处理IE主题================");
		if(ieTopic.contains("Topic")){
			System.out.println("IE Topic = " + ieTopic);
			topicList.add(ieTopic);
			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
		} else {
			System.out.println("================IE未抽取出主题================");
			System.out.println("================处理ma抽取的主题================");
			Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
		
			if(!topics.isEmpty()){
				OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//将topics转化为数组
				
				
				for(int i = 0; i < IdTopics.length; i++) {
					topicList.add(IdTopics[i].getBrowserText());
				}
			}
			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
		}
			
		mapToRainValues = maIndividual.getPropertyValues(addRainToMaProperty);
			
		if(mapToRainValues.isEmpty()){
			System.out.println("根据抽取出的主题，没有对应的风");
			RainFromInfoNumber=0;
		} else {
				
			for(Iterator it1 = mapToRainValues.iterator(); it1.hasNext();) {
				OWLIndividual RainIndiviual = (OWLIndividual)it1.next();
				RainList[RainFromInfoNumber] = RainIndiviual.getBrowserText();
				RainFromInfoNumber++;
			}
		} 
		
		System.out.println("================场景主题处理完毕================");
		
		//处理模板信息
		if(0 == RainFromInfoNumber)
		{
			System.out.println("==================模板处理====================");
			
			if(list.size() > 0) {
				executeTemplateToBackgroundSceneSWRLEngine(model,list);
				
				//去相应的场景里获取所添加的雨
				mapToRainValues = null;
				mapToRainValues = maIndividual.getPropertyValues(addRainToMaProperty);
				if(mapToRainValues.isEmpty())
					RainFromInfoNumber=0;
				else {
					
					for(Iterator it1=mapToRainValues.iterator();it1.hasNext();) {
						OWLIndividual RainIndiviual =(OWLIndividual)it1.next();
						RainList[RainFromInfoNumber]=RainIndiviual.getBrowserText();
						RainFromInfoNumber++;
					}
					
				}
			}
			System.out.println("================模板处理完毕！================");
		}
		
		System.out.println("RainFromInfoNum = "+RainFromInfoNumber);
		setRain(RainFromInfoNumber);	
	}
	
	public Document printRainRule(Document doc,String RainType,String RainDirection)
	{
		System.out.println("=====================开始生成xml-rule======================");
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "addEffectToMa");
		ruleName.addAttribute("type","Rain");
		ruleName.addAttribute("Magnitude", RainType);
		ruleName.addAttribute("Direction", RainDirection);
		System.out.println("xml-rule生成完毕");
		return doc;
	}	
	
	public static void main(String[] args)throws ParserConfigurationException,SAXException,IOException,JDOMException,DocumentException,OntologyLoadException,SWRLRuleEngineException 
	{
	
		String owlPath = "file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl" ;
		OWLModel model = ProtegeOWL.createJenaOWLModelFromURI(owlPath);
		ArrayList<String> alist =new ArrayList<String>(); 
		alist.add("RainTemplate:RainTemplate");
		alist.add("SadTemplate:SadTemplate");
		alist.add("SadTopic:SadTopic");
		alist.add("SpringTemplate:SpringTemplate");
		alist.add("SummerTemplate:SummerTemplate");
		alist.add("AutumnTemplate:AutumnTemplate");
		File file = new File("f:\\adl_result.xml");
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(file);
		
		System.out.println("开始!");
		RainInsert rain = new RainInsert();
//		RainInsert rain1 = new RainInsert();
	//	rain1.RainInfer2(alist, model, "kindergarten.ma","ShoppingActionTopic");
		Document document1 = rain.RainInfer(alist, model, "kindergarten.ma",document);
	//	Document document1 = rain.RainInfer(alist, model, "Tropical45.ma",document);
	//	rain.RainInfer2(alist, model, "Tropical45.ma");

		XMLWriter writer = new XMLWriter(new FileWriter("f:\\testRain.xml"));
		writer.write(document1); 
		System.out.println("结束！");
	//	System.out.println("Rain num : " + rain1.getRain());
		writer.close();
	}
}

//import java.io.File;
//import java.io.FileWriter;
//import org.dom4j.Document;
//import org.dom4j.Element;
//import org.dom4j.io.SAXReader;
//import org.dom4j.io.XMLWriter;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Iterator;
//import java.util.Random;
//import java.util.logging.Logger;
//import javax.xml.parsers.ParserConfigurationException;
//import org.dom4j.DocumentException;
//import org.jdom.JDOMException;
//import org.xml.sax.SAXException;
//import edu.stanford.smi.protege.exception.OntologyLoadException;
//import edu.stanford.smi.protege.model.SystemFrames;
//import edu.stanford.smi.protegex.owl.ProtegeOWL;
//import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
//import edu.stanford.smi.protegex.owl.model.OWLIndividual;
//import edu.stanford.smi.protegex.owl.model.OWLModel;
//import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
//import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
//import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
//import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngineFactory;
//import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;
//import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
//import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
//public class RainInsert {
//	private  int rain = 0;
//	static Logger logger = Logger.getLogger(RainInsert.class.getName());
//	
//	public int getRain() {
//		return rain;
//	}
//
//	public void setRain(int rain) {
//		this.rain = rain;
//	}
//	
//	public enum Direction {
//		East,West,South,North,SouthEast,NorthEast,SouthWest,NorthWest
//	}
//	
//	public  static SWRLFactory createSWRLFactory(OWLModel model) {
//		SWRLFactory factory = new SWRLFactory(model);
//		return factory;
//	}
//	
//	public static SWRLRuleEngine createRuleEngine(OWLModel model) throws SWRLRuleEngineException {
//		SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create("SWRLJessBridge",model);
//		return ruleEngine;
//	}
//	
//	public void  executeTopicToBackgroundSceneSWRLEngine(OWLModel model,ArrayList<String> templateName) throws SWRLRuleEngineException {
//		SWRLRuleEngine ruleEngine = createRuleEngine(model);
//		ruleEngine.reset();
//		SWRLFactory factory = createSWRLFactory(model);
//		factory.disableAll();
//		Iterator<SWRLImp> iter = factory.getImps().iterator();
//		while(iter.hasNext()) {
//			
//			SWRLImp imp = (SWRLImp) iter.next();
//			if(templateName.size()!=0) {
//				if(imp.getLocalName().contains("addRainToMaRule")) {
//					for(Iterator<String> its = templateName.iterator();its.hasNext();) {
//						
//						String templateValue = its.next();
//						String templateValue1= templateValue.replaceAll("Individual", "");
//						
//						if(imp.getBody().getBrowserText().contains(templateValue1)) {
//							logger.info("运行的规则名称为："+imp.getLocalName());
//							imp.enable();//执行此规则
//						}
//					}
//					
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
//		while(iter.hasNext()) {
//			SWRLImp imp=(SWRLImp) iter.next();
//			
//			if(templateName.size()!=0) {
//				if(imp.getLocalName().contains("addRainToMaRule")) {//找到名字包含“addRainToSceneRule”的规则
//					for(Iterator<String> its=templateName.iterator();its.hasNext();) {
//						
//						String templateValue= its.next();
//						String templateValue1=templateValue.substring(0,templateValue.indexOf(":"));
//						
//						if(imp.getBody().getBrowserText().contains(templateValue1)) {
//							logger.info("运行的规则名称："+imp.getLocalName());
//							imp.enable();
//						}
//					}
//					
//				}
//			}
//		}
//		ruleEngine.infer();
//	}
//	
//	public  Document RainInfer(ArrayList<String> list,OWLModel model,String maName,Document doc) throws OntologyLoadException, SWRLRuleEngineException
//	{
//		String[] RainList =new String[50];//用来存储抽取出来的符合主题和模板的雨
//		int RainFromInfoNumber=0;//最终抽取的符合的雨的数量
//		int RainFromTemNumber=0;
//		int RainFromTopNumber=0;
//		String str = "p4:";
//		String ieTopic;
//		ArrayList<String> topicList = new ArrayList<String>();
//		Collection mapToRainValues = null;
//		
//		OWLIndividual maIndividual = model.getOWLIndividual(maName);//获取maya实例	
//		if(null == maIndividual){
//			System.out.println("maya实例无法获取，可能不存在或丢失！请检查maName是否正确或存在？");
//			return doc;
//		}
//		
//		//用到的各种属性
//		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
//		OWLObjectProperty addRainToMaProperty = model.getOWLObjectProperty(str + "addRainToMa");
//		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//此变量用来判断场景属于室内室外
//
//		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
//		if(null == plane){
//			System.out.println("无法获取实例" + maIndividual.getBrowserText() + "的hasValueOfPlace属性(此属性用来判断场景属于室内还是室外)，so 拒绝加雨！请检查知识库中该属性是否存在或正确？");
//			return doc;
//		}
//		else {
//			if(plane.getBrowserText().equals("InWaterDescription") || plane.getBrowserText().equals("inDoorDescription")) {
//				System.out.println("此场景不适合加雨！");
//				return doc;
//			}
//		}
//		
//		//处理主题信息
//		System.out.println("================场景的主题处理================");
//		System.out.println("================优先处理IE主题================");
//		ieTopic = doc.getRootElement().element("maName").attributeValue("topic");
//		
//		if(ieTopic.contains("Topic")){
//			System.out.println("IE Topic = " + ieTopic);
//			topicList.add(ieTopic);
//			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
//		} else {
//			System.out.println("================IE未抽取出主题================");
//			System.out.println("================处理ma抽取的主题================");
//			Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);		
//			if(!topics.isEmpty()){
//				OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//将topics转化为数组
//				
//				for(int i = 0; i < IdTopics.length; i++) {
//					topicList.add(IdTopics[i].getBrowserText());
//				}
//				
//				int topicSize = topicList.size();
//				if(topicSize>0) {
//					System.out.print("抽取出来的主题个数为：" + topicSize + "个，是：");
//					
//					for(int i = 0; i < IdTopics.length; i++) {
//						System.out.print(IdTopics[i].getBrowserText() + " ");
//					}
//					
//					System.out.println();
//				}
//			}
//			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
//		}
//		
//		mapToRainValues = maIndividual.getPropertyValues(addRainToMaProperty);
//			
//		if(mapToRainValues.isEmpty()){
//			System.out.println("根据抽取出的主题，没有对应的雨");
//		} else {
//			
//			for(Iterator it1 = mapToRainValues.iterator(); it1.hasNext();) {
//				OWLIndividual RainIndiviual = (OWLIndividual)it1.next();
//				RainList[RainFromInfoNumber] = RainIndiviual.getBrowserText();
//				RainFromInfoNumber++;
//				RainFromTopNumber++;
//			}
//				
//			if(0 != RainFromTopNumber) {
//				System.out.print("根据主题抽取到"+RainFromTopNumber+"个雨，分别是：");
//					
//				for(int i = 0; i < RainFromTopNumber; i++)
//					System.out.print(RainList[i] + " ");
//				
//				System.out.println();
//				System.out.println("因主题优先，故模板不作处理！");
//			}
//		}
//				
//
//		System.out.println("================场景主题处理完毕================");
//		
//		//处理模板信息
//		if(0 == RainFromTopNumber)
//		{
//			System.out.println("==================模板处理====================");
//			int listSize = 0;
//			if(null != list)
//				listSize = list.size();//模板个数
//			else
//				System.out.println("未传递进来任何模板");
//			
//			if(listSize > 0) {
//				System.out.println("传递进来的模板个数为：" + listSize + "个，分别是：" + list);
//				executeTemplateToBackgroundSceneSWRLEngine(model,list);
//				
//				//去相应的场景里获取所添加的雨
//				mapToRainValues = null;
//				mapToRainValues= maIndividual.getPropertyValues(addRainToMaProperty);
//				if(mapToRainValues.isEmpty()){
//					System.out.println("根据传递进来的模板，没有相应的雨！");
//				} else {
//					
//					for(Iterator it1=mapToRainValues.iterator();it1.hasNext();) {
//						OWLIndividual RainIndiviual =(OWLIndividual)it1.next();
//						RainList[RainFromInfoNumber]=RainIndiviual.getBrowserText();
//						RainFromInfoNumber++;
//						RainFromTemNumber++;
//					}
//					
//					if(0 != RainFromTemNumber) {
//						System.out.print("根据模板抽到"+RainFromTemNumber+"个雨，分别是：");
//						
//						for(int i = 0; i < RainFromTemNumber; i++)
//							System.out.print(RainList[i] + " ");
//						
//						System.out.println();
//					}
//				}
//			}
//			System.out.println("================模板处理完毕！================");
//		}
//		
//		if(0 == RainFromInfoNumber) {
//			System.out.println("根据所给条件，没有抽取到适合的雨");
//			return doc;
//		} 
//		
//		//在获取到的雨里随机选取一个
//		java.util.Random random = new Random();
//		int rd = random.nextInt(RainFromInfoNumber);
//		
//		OWLIndividual RainIndiviual = model.getOWLIndividual(RainList[rd]);
//		System.out.println("随机选取到的雨为：" + RainIndiviual.getBrowserText());
//		
//		OWLDatatypeProperty Magnitude =  model.getOWLDatatypeProperty(str + "magnitude");
//		String RainType= (String) RainIndiviual.getPropertyValue(Magnitude);
//		System.out.println("RainType = " + RainType);
//		
//		
//		Direction RainDir;
//		Direction[] Dir = Direction.values();//dir存储枚举的值
//		int num = new Random().nextInt(8);//[0,8]之间的整数
//		RainDir = Dir[num];
//		String RainDirection = RainDir.toString();
//		
//		System.out.println("\n雨的类型：" + RainType + "\n雨的方向：" + RainDirection);
//		System.out.println("======================信息获取完毕！=====================");
//		Document doc1 = printRainRule(doc,RainType,RainDirection);
//		return doc1;
//	}
//	
//	public  void RainInfer2(ArrayList<String> list,OWLModel model,String maName,String ieTopic) throws OntologyLoadException, SWRLRuleEngineException
//	{
//		String[] RainList =new String[50];//用来存储抽取出来的符合主题和模板的雨
//		int RainFromInfoNumber=0;//最终抽取的符合的雨的数量
//		String str = "p4:";
//		ArrayList<String> topicList = new ArrayList<String>();
//		Collection mapToRainValues = null;
//		
//		OWLIndividual maIndividual = model.getOWLIndividual(maName);//获取maya实例
//		
//		if(null == maIndividual){
//			System.out.println("maya 实例无法获取，可能不存在或丢失！请检查maName是否正确或存在");
//			RainFromInfoNumber=0;
//			return;
//		}
//		
//		//用到的各种属性
//		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
//		OWLObjectProperty addRainToMaProperty = model.getOWLObjectProperty(str + "addRainToMa");
//		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//此变量用来判断场景属于室内室外
//
//		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
//		if(null == plane){
//			System.out.println("无法获取实例" + maIndividual.getBrowserText() + "的hasValueOfPlace属性(此属性用来判断场景属于室内还是室外)，so 拒绝加雨！请检查知识库中该属性是否存在或正确？");
//			return;
//		}else {
//			if(plane.getBrowserText().equals("InWaterDescription") || plane.getBrowserText().equals("inDoorDescription")) {
//				System.out.println("此场景不适合加雨！");
//				return;
//			}
//		}
//		
//		//处理主题信息
//		System.out.println("================场景的主题处理================");
//		System.out.println("================优先处理IE主题================");
//		if(ieTopic.contains("Topic")){
//			System.out.println("IE Topic = " + ieTopic);
//			topicList.add(ieTopic);
//			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
//		} else {
//			System.out.println("================IE未抽取出主题================");
//			System.out.println("================处理ma抽取的主题================");
//			Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
//		
//			if(!topics.isEmpty()){
//				OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//将topics转化为数组
//				
//				
//				for(int i = 0; i < IdTopics.length; i++) {
//					topicList.add(IdTopics[i].getBrowserText());
//				}
//			}
//			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
//		}
//			
//		mapToRainValues = maIndividual.getPropertyValues(addRainToMaProperty);
//			
//		if(mapToRainValues.isEmpty()){
//			System.out.println("根据抽取出的主题，没有对应的风");
//			RainFromInfoNumber=0;
//		} else {
//				
//			for(Iterator it1 = mapToRainValues.iterator(); it1.hasNext();) {
//				OWLIndividual RainIndiviual = (OWLIndividual)it1.next();
//				RainList[RainFromInfoNumber] = RainIndiviual.getBrowserText();
//				RainFromInfoNumber++;
//			}
//		} 
//		
//		System.out.println("================场景主题处理完毕================");
//		
//		//处理模板信息
//		if(0 == RainFromInfoNumber)
//		{
//			System.out.println("==================模板处理====================");
//			
//			if(list.size() > 0) {
//				executeTemplateToBackgroundSceneSWRLEngine(model,list);
//				
//				//去相应的场景里获取所添加的雨
//				mapToRainValues = null;
//				mapToRainValues = maIndividual.getPropertyValues(addRainToMaProperty);
//				if(mapToRainValues.isEmpty())
//					RainFromInfoNumber=0;
//				else {
//					
//					for(Iterator it1=mapToRainValues.iterator();it1.hasNext();) {
//						OWLIndividual RainIndiviual =(OWLIndividual)it1.next();
//						RainList[RainFromInfoNumber]=RainIndiviual.getBrowserText();
//						RainFromInfoNumber++;
//					}
//					
//				}
//			}
//			System.out.println("================模板处理完毕！================");
//		}
//		
//		System.out.println("RainFromInfoNum = "+RainFromInfoNumber);
//		setRain(RainFromInfoNumber);	
//	}
////	public  Document RainInfer(ArrayList<String> list,OWLModel model,String maName,Document doc) throws OntologyLoadException, SWRLRuleEngineException
////	{
////		String[] RainList =new String[50];//用来存储抽取出来的符合主题和模板的雨
////		int RainFromInfoNumber=0;//最终抽取的符合的雨的数量
////		int RainFromTemNumber=0;
////		int RainFromTopNumber=0;
////		String str = "p4:";
////		
////		OWLIndividual maIndividual = model.getOWLIndividual(maName);//获取maya实例
////		
////		if(null == maIndividual){
////			System.out.println("maya实例无法获取，可能不存在或丢失！请检查maName是否正确或存在？");
////			return doc;
////		}
////		
////		//用到的各种属性
////		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
////		OWLObjectProperty addRainToMaProperty = model.getOWLObjectProperty(str + "addRainToMa");
////		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//此变量用来判断场景属于室内室外
////
////		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
////		if(null == plane){
////			System.out.println("无法获取实例" + maIndividual.getBrowserText() + "的hasValueOfPlace属性(此属性用来判断场景属于室内还是室外)，so 拒绝加雨！请检查知识库中该属性是否存在或正确？");
////			return doc;
////		}
////		else {
////			if(plane.getBrowserText().equals("InWaterDescription") || plane.getBrowserText().equals("inDoorDescription")) {
////				System.out.println("此场景不适合加雨！");
////				return doc;
////			}
////		}
////		
////		//处理主题信息
////		System.out.println("================场景的主题处理================");
////		Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
////		
////		if(!topics.isEmpty())
////		{
////			OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//将topics转化为数组
////			ArrayList topicList = new ArrayList();
////			
////			for(int i = 0; i < IdTopics.length; i++) {
////				topicList.add(IdTopics[i].getBrowserText());
////			}
////			
////			int topicSize = topicList.size();
////			if(topicSize>0) {
////				System.out.print("抽取出来的主题个数为：" + topicSize + "个，是：");
////				
////				for(int i = 0; i < IdTopics.length; i++) {
////					System.out.print(IdTopics[i].getBrowserText() + " ");
////				}
////				
////				System.out.println();
////			}
////			
////			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
////			Collection mapToRainValues = maIndividual.getPropertyValues(addRainToMaProperty);
////			
////			if(mapToRainValues.isEmpty())
////				System.out.println("根据抽取出的主题，没有对应的雨");
////			else {
////				
////				for(Iterator it1 = mapToRainValues.iterator(); it1.hasNext();) {
////					OWLIndividual RainIndiviual = (OWLIndividual)it1.next();
////					RainList[RainFromInfoNumber] = RainIndiviual.getBrowserText();
////					RainFromInfoNumber++;
////					RainFromTopNumber++;
////				}
////				
////				if(0 != RainFromTopNumber) {
////					System.out.print("根据主题抽取到"+RainFromTopNumber+"个雨，分别是：");
////					
////					for(int i = 0; i < RainFromTopNumber; i++)
////						System.out.print(RainList[i] + " ");
////					System.out.println();
////					System.out.println("因主题优先，故模板不作处理！");
////				}
////			}
////		} else
////			System.out.println("该" + maName + "场景未抽取到主题！");
////		System.out.println("================场景主题处理完毕================");
////		
////		//处理模板信息
////		if(0 == RainFromTopNumber)
////		{
////			System.out.println("==================模板处理====================");
////			int listSize = 0;
////			if(null != list)
////				listSize = list.size();//模板个数
////			else
////				System.out.println("未传递进来任何模板");
////			
////			if(listSize > 0) {
////				System.out.println("传递进来的模板个数为：" + listSize + "个，分别是：" + list);
////				executeTemplateToBackgroundSceneSWRLEngine(model,list);
////				
////				//去相应的场景里获取所添加的雨
////				Collection mapToRainValues= maIndividual.getPropertyValues(addRainToMaProperty);
////				if(mapToRainValues.isEmpty())
////					System.out.println("根据传递进来的模板，没有相应的雨！");
////				else {
////					
////					for(Iterator it1=mapToRainValues.iterator();it1.hasNext();) {
////						OWLIndividual RainIndiviual =(OWLIndividual)it1.next();
////						RainList[RainFromInfoNumber]=RainIndiviual.getBrowserText();
////						RainFromInfoNumber++;
////						RainFromTemNumber++;
////					}
////					
////					if(0 != RainFromTemNumber) {
////						System.out.print("根据模板抽到"+RainFromTemNumber+"个雨，分别是：");
////						
////						for(int i = 0; i < RainFromTemNumber; i++)
////							System.out.print(RainList[i] + " ");
////						
////						System.out.println();
////					}
////				}
////			}
////			System.out.println("================模板处理完毕！================");
////		}
////		
////		if(0 == RainFromInfoNumber) {
////			System.out.println("根据所给条件，没有抽取到适合的雨");
////			return doc;
////		} 
////		
////		//在获取到的雨里随机选取一个
////		java.util.Random random = new Random();
////		int rd = random.nextInt(RainFromInfoNumber);
////		
////		OWLIndividual RainIndiviual = model.getOWLIndividual(RainList[rd]);
////		System.out.print("随机选取到的雨为：" + RainIndiviual.getBrowserText());
////		
////		int strLength = RainIndiviual.getBrowserText().length();
////		String RainType = RainIndiviual.getBrowserText().substring(3,strLength-5);
////		
////		Direction RainDir;
////		Direction[] Dir = Direction.values();//dir存储枚举的值
////		int num = new Random().nextInt(8);//[0,8]之间的整数
////		RainDir = Dir[num];
////		String RainDirection = RainDir.toString();
////		
////		System.out.println("\n雨的类型：" + RainType + "\n雨的方向：" + RainDirection);
////		System.out.println("======================信息获取完毕！=====================");
////		Document doc1 = printRainRule(doc,RainType,RainDirection);
////		return doc1;
////	}
////	
////	public  void RainInfer2(ArrayList<String> list,OWLModel model,String maName) throws OntologyLoadException, SWRLRuleEngineException
////	{
////		String[] RainList =new String[50];//用来存储抽取出来的符合主题和模板的雨
////		int RainFromInfoNumber=0;//最终抽取的符合的雨的数量
////
////		String str = "p4:";
////		
////		OWLIndividual maIndividual = model.getOWLIndividual(maName);//获取maya实例
////		
////		if(null == maIndividual){
////			System.out.println("maya 实例无法获取，可能不存在或丢失！请检查maName是否正确或存在");
////			RainFromInfoNumber=0;
////			return;
////		}
////		
////		//用到的各种属性
////		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
////		OWLObjectProperty addRainToMaProperty = model.getOWLObjectProperty(str + "addRainToMa");
////		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//此变量用来判断场景属于室内室外
////
////		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
////		if(null == plane){
////			System.out.println("无法获取实例" + maIndividual.getBrowserText() + "的hasValueOfPlace属性(此属性用来判断场景属于室内还是室外)，so 拒绝加雨！请检查知识库中该属性是否存在或正确？");
////			return;
////		}else {
////			if(plane.getBrowserText().equals("InWaterDescription") || plane.getBrowserText().equals("inDoorDescription")) {
////				System.out.println("此场景不适合加雨！");
////				return;
////			}
////		}
////		
////		//处理主题信息
////		System.out.println("================场景的主题处理================");
////		Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
////		
////		if(!topics.isEmpty())
////		{
////			OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//将topics转化为数组
////			ArrayList topicList = new ArrayList();
////			
////			for(int i = 0; i < IdTopics.length; i++) {
////				topicList.add(IdTopics[i].getBrowserText());
////			}
////			
////			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
////			Collection mapToRainValues = maIndividual.getPropertyValues(addRainToMaProperty);
////			
////			if(mapToRainValues.isEmpty()){
////				System.out.println("根据抽取出的主题，没有对应的风");
////				RainFromInfoNumber=0;
////			} else {
////				
////				for(Iterator it1 = mapToRainValues.iterator(); it1.hasNext();) {
////					OWLIndividual RainIndiviual = (OWLIndividual)it1.next();
////					RainList[RainFromInfoNumber] = RainIndiviual.getBrowserText();
////					RainFromInfoNumber++;
////				}
////			}
////		} else {
////			System.out.println("该" + maName + "场景未抽取到主题！");
////			RainFromInfoNumber=0;
////		}
////		System.out.println("================场景主题处理完毕================");
////		
////		//处理模板信息
////		if(0 == RainFromInfoNumber)
////		{
////			System.out.println("==================模板处理====================");
////			
////			if(list.size() > 0) {
////				executeTemplateToBackgroundSceneSWRLEngine(model,list);
////				
////				//去相应的场景里获取所添加的雨
////				Collection mapToRainValues= maIndividual.getPropertyValues(addRainToMaProperty);
////				if(mapToRainValues.isEmpty())
////					RainFromInfoNumber=0;
////				else {
////					
////					for(Iterator it1=mapToRainValues.iterator();it1.hasNext();) {
////						OWLIndividual RainIndiviual =(OWLIndividual)it1.next();
////						RainList[RainFromInfoNumber]=RainIndiviual.getBrowserText();
////						RainFromInfoNumber++;
////					}
////					
////				}
////			}
////			System.out.println("================模板处理完毕！================");
////		}
////		
////		System.out.println("RainFromInfoNum="+RainFromInfoNumber);
////		setRain(RainFromInfoNumber);	
////	}
//	
//	public Document printRainRule(Document doc,String RainType,String RainDirection)
//	{
//		System.out.println("=====================开始生成xml-rule======================");
//		Element rootName = (Element) doc.getRootElement();
//		Element name = rootName.element("maName");
//		Element ruleName = name.addElement("rule");
//		ruleName.addAttribute("ruleType", "addEffectToMa");
//		ruleName.addAttribute("type","Rain");
//		ruleName.addAttribute("Magnitude", RainType);
//		ruleName.addAttribute("Direction", RainDirection);
//		System.out.println("xml-rule生成完毕");
//		return doc;
//	}	
//	
//	public static void main(String[] args)throws ParserConfigurationException,SAXException,IOException,JDOMException,DocumentException,OntologyLoadException,SWRLRuleEngineException 
//	{
//	
//		String owlPath = "file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl" ;
//		OWLModel model = ProtegeOWL.createJenaOWLModelFromURI(owlPath);
//		ArrayList<String> alist =new ArrayList<String>(); 
//		alist.add("RainTemplate:RainTemplate");
//		File file = new File("f:\\test.xml");
//		SAXReader saxReader = new SAXReader();
//		Document document = saxReader.read(file);
//		RainInsert rain = new RainInsert();
//		System.out.println("开始!");
//		Document document1 = rain.RainInfer(alist, model, "kindergarten.ma",document);
////		Document document1 = rain.RainInfer(alist, model, "Tropical45.ma",document);
//		rain.RainInfer2(alist, model, "Tropical45.ma","");
//		XMLWriter writer = new XMLWriter(new FileWriter("f:\\testRain.xml"));
//		writer.write(document1); 
//		System.out.println("结束！");
//		System.out.println("Rain num : " + rain.getRain());
//		writer.close();
//	}
//}
//
//
////
////private  int rain = 0;
////	static Logger logger = Logger.getLogger(RainInsert.class.getName());
////	
////	public int getRain() {
////		return rain;
////	}
////
////	public void setRain(int rain) {
////		this.rain = rain;
////	}
////	
////	public enum Direction {
////		East,West,South,North,SouthEast,NorthEast,SouthWest,NorthWest
////	}
////	
////	public  static SWRLFactory createSWRLFactory(OWLModel model) {
////		SWRLFactory factory = new SWRLFactory(model);
////		return factory;
////	}
////	
////	public static SWRLRuleEngine createRuleEngine(OWLModel model) throws SWRLRuleEngineException {
////		SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create("SWRLJessBridge",model);
////		return ruleEngine;
////	}
////	
////	public static void  executeTopicToBackgroundSceneSWRLEngine(OWLModel model,ArrayList<String> templateName) throws SWRLRuleEngineException {
////		SWRLRuleEngine ruleEngine = createRuleEngine(model);
////		ruleEngine.reset();
////		SWRLFactory factory = createSWRLFactory(model);
////		factory.disableAll();
////		Iterator<SWRLImp> iter = factory.getImps().iterator();
////		while(iter.hasNext()) {
////			
////			SWRLImp imp = (SWRLImp) iter.next();
////			if(templateName.size()!=0) {
////				if(imp.getLocalName().contains("addRainToMaRule")) {
////					for(Iterator<String> its = templateName.iterator();its.hasNext();) {
////						
////						String templateValue = its.next();
////						String templateValue1= templateValue.replaceAll("Individual", "");
////						
////						if(imp.getBody().getBrowserText().contains(templateValue1)) {
////							logger.info("运行的规则名称为："+imp.getLocalName());
////							imp.enable();//执行此规则
////						}
////					}
////					
////				}
////			}
////		}
////		ruleEngine.infer();
////	}
////	
////	//执行OWL里的规则
////	public static void executeTemplateToBackgroundSceneSWRLEngine(OWLModel model,ArrayList<String> templateName) throws SWRLRuleEngineException
////	{
////		SWRLRuleEngine ruleEngine = createRuleEngine(model);
////		ruleEngine.reset();
////		SWRLFactory factory = createSWRLFactory(model);
////		factory.disableAll();
////		Iterator<SWRLImp> iter = factory.getImps().iterator();
////		while(iter.hasNext()) {
////			SWRLImp imp=(SWRLImp) iter.next();
////			
////			if(templateName.size()!=0) {
////				if(imp.getLocalName().contains("addRainToMaRule")) {//找到名字包含“addRainToSceneRule”的规则
////					for(Iterator<String> its=templateName.iterator();its.hasNext();) {
////						
////						String templateValue= its.next();
////						String templateValue1=templateValue.substring(0,templateValue.indexOf(":"));
////						
////						if(imp.getBody().getBrowserText().contains(templateValue1)) {
////							logger.info("运行的规则名称："+imp.getLocalName());
////							imp.enable();
////						}
////					}
////					
////				}
////			}
////		}
////		ruleEngine.infer();
////	}
////	
////	public  Document RainInfer(ArrayList<String> list,OWLModel model,String maName,Document doc) throws OntologyLoadException, SWRLRuleEngineException
////	{
////		String[] RainList =new String[50];//用来存储抽取出来的符合主题和模板的雨
////		int RainFromInfoNumber=0;//最终抽取的符合的雨的数量
////		int RainFromTemNumber=0;
////		int RainFromTopNumber=0;
////		String str = "p4:";
////		
////		OWLIndividual maIndividual = model.getOWLIndividual(maName);//获取maya实例
////		
////		if(null == maIndividual){
////			System.out.println("maya实例无法获取，可能不存在或丢失！请检查maName是否正确或存在？");
////			return doc;
////		}
////		
////		//用到的各种属性
////		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
////		OWLObjectProperty addRainToMaProperty = model.getOWLObjectProperty(str + "addRainToMa");
////		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//此变量用来判断场景属于室内室外
////
////		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
////		if(null == plane)
////			return doc;
////		else {
////			if(plane.getBrowserText().equals("InWaterDescription") || plane.getBrowserText().equals("inDoorDescription")) {
////				System.out.println("此场景不适合加雨！");
////				return doc;
////			}
////		}
////		
////		//处理主题信息
////		System.out.println("================场景的主题处理================");
////		Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
////		
////		if(!topics.isEmpty())
////		{
////			OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//将topics转化为数组
////			ArrayList topicList = new ArrayList();
////			
////			for(int i = 0; i < IdTopics.length; i++) {
////				topicList.add(IdTopics[i].getBrowserText());
////			}
////			
////			int topicSize = topicList.size();
////			if(topicSize>0) {
////				System.out.print("抽取出来的主题个数为：" + topicSize + "个，是：");
////				
////				for(int i = 0; i < IdTopics.length; i++) {
////					System.out.print(IdTopics[i].getBrowserText() + " ");
////				}
////				
////				System.out.println();
////			}
////			
////			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
////			Collection mapToRainValues = maIndividual.getPropertyValues(addRainToMaProperty);
////			
////			if(mapToRainValues.isEmpty())
////				System.out.println("根据抽取出的主题，没有对应的雨");
////			else {
////				
////				for(Iterator it1 = mapToRainValues.iterator(); it1.hasNext();) {
////					OWLIndividual RainIndiviual = (OWLIndividual)it1.next();
////					RainList[RainFromInfoNumber] = RainIndiviual.getBrowserText();
////					RainFromInfoNumber++;
////					RainFromTopNumber++;
////				}
////				
////				if(0 != RainFromTopNumber) {
////					System.out.print("根据主题抽取到"+RainFromTopNumber+"个雨，分别是：");
////					
////					for(int i = 0; i < RainFromTopNumber; i++)
////						System.out.print(RainList[i] + " ");
////					System.out.println();
////					System.out.println("因主题优先，故模板不作处理！");
////				}
////			}
////		} else
////			System.out.println("该" + maName + "场景未抽取到主题！");
////		System.out.println("================场景主题处理完毕================");
////		
////		//处理模板信息
////		if(0 == RainFromTopNumber)
////		{
////			System.out.println("==================模板处理====================");
////			int listSize = 0;
////			if(null != list)
////				listSize = list.size();//模板个数
////			else
////				System.out.println("未传递进来任何模板");
////			
////			if(listSize > 0) {
////				System.out.println("传递进来的模板个数为：" + listSize + "个，分别是：" + list);
////				executeTemplateToBackgroundSceneSWRLEngine(model,list);
////				
////				//去相应的场景里获取所添加的雨
////				Collection mapToRainValues= maIndividual.getPropertyValues(addRainToMaProperty);
////				if(mapToRainValues.isEmpty())
////					System.out.println("根据传递进来的模板，没有相应的雨！");
////				else {
////					
////					for(Iterator it1=mapToRainValues.iterator();it1.hasNext();) {
////						OWLIndividual RainIndiviual =(OWLIndividual)it1.next();
////						RainList[RainFromInfoNumber]=RainIndiviual.getBrowserText();
////						RainFromInfoNumber++;
////						RainFromTemNumber++;
////					}
////					
////					if(0 != RainFromTemNumber) {
////						System.out.print("根据模板抽到"+RainFromTemNumber+"个雨，分别是：");
////						
////						for(int i = 0; i < RainFromTemNumber; i++)
////							System.out.print(RainList[i] + " ");
////						
////						System.out.println();
////					}
////				}
////			}
////			System.out.println("================模板处理完毕！================");
////		}
////		
////		if(0 == RainFromInfoNumber) {
////			System.out.println("根据所给条件，没有抽取到适合的雨");
////			return doc;
////		} else 
////			setRain(RainFromInfoNumber);
////		
////		//在获取到的雨里随机选取一个
////		java.util.Random random = new Random();
////		int rd = random.nextInt(RainFromInfoNumber);
////		
////		OWLIndividual RainIndiviual = model.getOWLIndividual(RainList[rd]);
////		System.out.print("随机选取到的雨为：" + RainIndiviual.getBrowserText());
////		
////		int strLength = RainIndiviual.getBrowserText().length();
////		String RainType = RainIndiviual.getBrowserText().substring(3,strLength-1);
////		
////		Direction RainDir;
////		Direction[] Dir = Direction.values();//dir存储枚举的值
////		int num = new Random().nextInt(8);//[0,8]之间的整数
////		RainDir = Dir[num];
////		String RainDirection = RainDir.toString();
////		
////		System.out.println("\n雨的类型：" + RainType + "\n雨的方向：" + RainDirection);
////		System.out.println("======================信息获取完毕！=====================");
////		Document doc1 = printRainRule(doc,RainType,RainDirection);
////		return doc1;
////	}
////	
////	public  void RainInfer2(ArrayList<String> list,OWLModel model,String maName) throws OntologyLoadException, SWRLRuleEngineException
////	{
////		String[] RainList =new String[50];//用来存储抽取出来的符合主题和模板的雨
////		int RainFromInfoNumber=0;//最终抽取的符合的雨的数量
////		int RainFromTemNumber=0;
////		int RainFromTopNumber=0;
////		String str = "p4:";
////		
////		OWLIndividual maIndividual = model.getOWLIndividual(maName);//获取maya实例
////		
////		if(null == maIndividual){
////			RainFromInfoNumber=0;
////		}
////		
////		//用到的各种属性
////		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
////		OWLObjectProperty addRainToMaProperty = model.getOWLObjectProperty(str + "addRainToMa");
////		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//此变量用来判断场景属于室内室外
////
////		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
////		if(null == plane){
////			setRain(RainFromInfoNumber);
////			return;
////		}else {
////			if(plane.getBrowserText().equals("InWaterDescription") || plane.getBrowserText().equals("inDoorDescription")) {
////				System.out.println("此场景不适合加雨！");
////				RainFromInfoNumber=0;
////				setRain(RainFromInfoNumber);
////				return;
////			}
////		}
////		
////		//处理主题信息
////		System.out.println("================场景的主题处理================");
////		Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
////		
////		if(!topics.isEmpty())
////		{
////			OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//将topics转化为数组
////			ArrayList topicList = new ArrayList();
////			
////			for(int i = 0; i < IdTopics.length; i++) {
////				topicList.add(IdTopics[i].getBrowserText());
////			}
////			
////			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
////			Collection mapToRainValues = maIndividual.getPropertyValues(addRainToMaProperty);
////			
////			if(mapToRainValues.isEmpty())
////				RainFromInfoNumber=0;
////			else {
////				
////				for(Iterator it1 = mapToRainValues.iterator(); it1.hasNext();) {
////					OWLIndividual RainIndiviual = (OWLIndividual)it1.next();
////					RainList[RainFromInfoNumber] = RainIndiviual.getBrowserText();
////					RainFromInfoNumber++;
////					RainFromTopNumber++;
////				}
////			}
////		} else
////			RainFromInfoNumber=0;
////		System.out.println("================场景主题处理完毕================");
////		
////		//处理模板信息
////		if(0 == RainFromTopNumber)
////		{
////			System.out.println("==================模板处理====================");
////			
////			if(list.size() > 0) {
////				executeTemplateToBackgroundSceneSWRLEngine(model,list);
////				
////				//去相应的场景里获取所添加的雨
////				Collection mapToRainValues= maIndividual.getPropertyValues(addRainToMaProperty);
////				if(mapToRainValues.isEmpty())
////					RainFromInfoNumber=0;
////				else {
////					
////					for(Iterator it1=mapToRainValues.iterator();it1.hasNext();) {
////						OWLIndividual RainIndiviual =(OWLIndividual)it1.next();
////						RainList[RainFromInfoNumber]=RainIndiviual.getBrowserText();
////						RainFromInfoNumber++;
////						RainFromTemNumber++;
////					}
////					
////				}
////			}
////			System.out.println("================模板处理完毕！================");
////		}
////			System.out.println("RainFromInfoNum="+RainFromInfoNumber);
////			setRain(RainFromInfoNumber);	
////	}
////	
////	public Document printRainRule(Document doc,String RainType,String RainDirection)
////	{
////		System.out.println("=====================开始生成xml-rule======================");
////		Element rootName = (Element) doc.getRootElement();
////		Element name = rootName.element("maName");
////		Element ruleName = name.addElement("rule");
////		ruleName.addAttribute("ruleType", "addEffectToMa");
////		ruleName.addAttribute("type","Rain");
////		ruleName.addAttribute("Magnitude", RainType);
////		ruleName.addAttribute("Direction", RainDirection);
////		System.out.println("xml-rule生成完毕");
////		return doc;
////	}	
////	
////	public static void main(String[] args)throws ParserConfigurationException,SAXException,IOException,JDOMException,DocumentException,OntologyLoadException,SWRLRuleEngineException 
////	{
////	
////		String owlPath = "file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl" ;
////		OWLModel model = ProtegeOWL.createJenaOWLModelFromURI(owlPath);
////		ArrayList<String> alist =new ArrayList<String>(); 
////		alist.add("RainTemplate:RainTemplate");
////		File file = new File("f:\\test.xml");
////		SAXReader saxReader = new SAXReader();
////		Document document = saxReader.read(file);
////		RainInsert rain = new RainInsert();
////		System.out.println("开始!");
////		Document document1 = rain.RainInfer(alist, model, "kindergarten.ma",document);
////	//	Document document1 = rain.RainInfer(alist, model, "",document);
////		XMLWriter writer = new XMLWriter(new FileWriter("f:\\testRain.xml"));
////		writer.write(document1); 
////		System.out.println("结束！");
////		System.out.println("Rain num : " + rain.getRain());
////		writer.close();
////	}