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
public class SnowInsert {
	private int snow = 0;
	private ArrayList<String> topicAndTemplate = new ArrayList<String>(Arrays.asList("SpringFestivalTopic","ChristmasDayTopic","WinterTemplate"));
	
	static Logger logger = Logger.getLogger(SnowInsert.class.getName());
	
	public int getSnow() {
		return snow;
	}

	public void setSnow(int snow) {
		this.snow = snow;
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
	public void executeTemplateToBackgroundSceneSWRLEngine(OWLModel model,ArrayList<String> templateName) throws SWRLRuleEngineException
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
		String[] SnowList =new String[50];//用来存储抽取出来的符合主题和模板的雪
		
		int SnowFromInfoNumber=0;//最终抽取的符合的雪的数量
		int SnowFromTemNumber=0;
		int SnowFromTopNumber=0;
		String str="p4:";
		String ieTopic = null;
		ArrayList<String> topicList = new ArrayList<String>();
		ArrayList<String> list = new ArrayList<String>();
		Collection mapToSnowValues = null;
		OWLIndividual maIndividual = model.getOWLIndividual(maName);//获取maya实例
		
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
		
		//处理主题信息
		System.out.println("=================场景的主题处理================");
		System.out.println("===============优先处理IE抽取的主题================");
		
		ieTopic = doc.getRootElement().element("maName").attributeValue("topic");
		if(ieTopic.contains("Topic")){
			System.out.println("IE Topic = " + ieTopic);
			topicList.add(ieTopic);
			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
		} else {
			System.out.println("=================IE未抽出主题================");
			System.out.println("===============处理ma抽取的主题================");
			
			Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
			if(!topics.isEmpty()) {
				OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//将topics转化为数组
				
				for(int i = 0; i < IdTopics.length; i++) {
					topicList.add(IdTopics[i].getBrowserText());
				}
				
				int topicSize = topicList.size();
				if(topicSize>0) {
					System.out.print("抽取出来的主题个数为："+topicSize+"个，是：");
					
					for(int i = 0; i < IdTopics.length; i++) {
						System.out.print(IdTopics[i].getBrowserText() + " ");
					}
					System.out.println();
				}
				
				executeTopicToBackgroundSceneSWRLEngine(model,topicList);			
			}
		}
		
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

		System.out.println("================场景主题处理完毕================");
		
		//处理模板信息
		if(SnowFromInfoNumber == 0) {
			System.out.println("==================模板处理====================");
			int listSize = 0;
			if(null != list)
				listSize = list.size();//模板个数
			else
				System.out.println("未传递进来任何模板");
			
			if(listSize > 0) {
				System.out.println("传递进来的模板个数为：" + listSize + "个，分别是：" + list);
				executeTemplateToBackgroundSceneSWRLEngine(model,list);
				
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
		
		if(0 == SnowFromInfoNumber) {
			System.out.println("根据所给条件，没有抽取到适合的雪");
			return doc;
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
		Document doc1 = printSnowRule(doc,SnowType,SnowDirection);
		return doc1;
	}
	
	public void SnowInfer2(ArrayList<String> List,OWLModel model,String maName,String ieTopic) throws OntologyLoadException, SWRLRuleEngineException
	{
		String[] SnowList =new String[50];//用来存储抽取出来的符合主题和模板的雪
		String str="p4:";
		int SnowFromInfoNumber=0;//最终抽取的符合的雪的数量
		ArrayList<String> topicList = new ArrayList<String>();
		ArrayList<String> list = new ArrayList<String>();
		Collection mapToSnowValues = null;
		OWLIndividual maIndividual = model.getOWLIndividual(maName);//获取maya实例
		
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
		if(null == maIndividual){
			System.out.println("maya实例无法获取，可能不存在或丢失！请检查maName是否正确或存在？");
			SnowFromInfoNumber=0;
			return;
		}
		
		//用到的各种属性
		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
		OWLObjectProperty addSnowToMaProperty = model.getOWLObjectProperty(str + "addSnowToMa");
		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//此变量用来判断场景属于室内室外
		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
		
		if(null == plane){
			System.out.println("无法获取实例" + maIndividual.getBrowserText() + "的hasValueOfPlace属性(此属性用来判断场景属于室内还是室外)，so 拒绝加雪！请检查知识库中该属性是否存在或正确？");
			SnowFromInfoNumber=0;
			return;
		}else {
			if(plane.getBrowserText().equals("InWaterDescription")||plane.getBrowserText().equals("inDoorDescription")) {
				System.out.println("此场景不适合加雪！");
				SnowFromInfoNumber=0;
				return;
			}
		}
		
		//处理主题信息
		System.out.println("================场景的主题处理================");
		System.out.println("================优先处理IE主题================");
		
		if(ieTopic.contains("Topic")){
			System.out.println("IE Topic = " + ieTopic);
			topicList.add(ieTopic);
			executeTopicToBackgroundSceneSWRLEngine(model, topicList);
		} else {
			System.out.println("================IE未抽出主题================");
			System.out.println("================处理ma抽出的主题================");
			Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
			if(!topics.isEmpty()) {
				OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//将topics转化为数组
			
				for(int i = 0; i < IdTopics.length; i++) {
					topicList.add(IdTopics[i].getBrowserText());
				}
			}
			executeTopicToBackgroundSceneSWRLEngine(model,topicList);	
		}
		
		mapToSnowValues = maIndividual.getPropertyValues(addSnowToMaProperty);
			
		if(mapToSnowValues.isEmpty()) {
			SnowFromInfoNumber=0;
			System.out.println("根据抽取出的主题，没有对应的雪");
		}else {
				
			for(Iterator it1 = mapToSnowValues.iterator();it1.hasNext();) {
				OWLIndividual SnowIndiviual = (OWLIndividual)it1.next();
				SnowList[SnowFromInfoNumber] = SnowIndiviual.getBrowserText();
				SnowFromInfoNumber++;
			}
		}
		System.out.println("================场景主题处理完毕================");
		
		//处理模板信息
		if(SnowFromInfoNumber == 0) {
			System.out.println("==================模板处理====================");
			if(list.size() > 0) {
				executeTemplateToBackgroundSceneSWRLEngine(model,list);
				
				//去相应的场景里获取所添加的雪
				mapToSnowValues = null;
				mapToSnowValues= maIndividual.getPropertyValues(addSnowToMaProperty);
				if(mapToSnowValues.isEmpty())
					SnowFromInfoNumber=0;
				else {
					
					for(Iterator it1 = mapToSnowValues.iterator();it1.hasNext();) {
						OWLIndividual SnowIndiviual = (OWLIndividual)it1.next();
						SnowList[SnowFromInfoNumber] = SnowIndiviual.getBrowserText();
						SnowFromInfoNumber++;
					}
				}
			}
			System.out.println("================模板处理完毕！================");
		}
		System.out.println("SnowFormInfoNumber:" + SnowFromInfoNumber);
		setSnow(SnowFromInfoNumber);	
	}
	
	public Document printSnowRule(Document doc,String SnowType,String SnowDirection)
	{
		System.out.println("=====================开始生成xml-rule======================");
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "addEffectToMa");
		ruleName.addAttribute("type","Snow");
		ruleName.addAttribute("Magnitude", SnowType);
		ruleName.addAttribute("Direction", SnowDirection);
		System.out.println("xml-rule生成完毕");
		return doc;
	}	
	
	public static void main(String[] args)throws ParserConfigurationException,SAXException,IOException,JDOMException,DocumentException,OntologyLoadException,SWRLRuleEngineException 
	{
		String owlpath = "file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl" ;
		OWLModel model = ProtegeOWL.createJenaOWLModelFromURI(owlpath);
		
		ArrayList<String> aList = new ArrayList<String>(); 
		aList.add("SnowTemplate:SnowTemplate");
		aList.add("WinterTemplate:WinterTemplate");
		aList.add("ChristmasDayTopic:ChristmasDayTopic");
		aList.add("SpringFestivalTopic:SpringFestivalTopic");

		
		File file = new File("f:\\test.xml");
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(file);
		SnowInsert  Snow = new SnowInsert();
		
		System.out.println("开始!");
	Document document1 = Snow.SnowInfer(aList, model, "schoolroomOut.ma",document);
	//	String maName = null;
	//	SnowInsert  snow = new SnowInsert();
	//	snow.SnowInfer2(aList, model,"schoolroomOut.ma","MissTopic");
	//	Document document1 = Snow.SnowInfer(aList, model, "Tropical45.ma",document);
//		Snow.SnowInfer2(aList, model, "Tropical45.ma");
		XMLWriter writer = new XMLWriter(new FileWriter("f:\\testSnow.xml"));
		writer.write(document1); 
		System.out.println("结束！");
	//	System.out.println("snow number:" + snow.getSnow());
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
//public class SnowInsert {
//	private int snow = 0;
//	static Logger logger = Logger.getLogger(SnowInsert.class.getName());
//	
//	public int getSnow() {
//		return snow;
//	}
//
//	public void setSnow(int snow) {
//		this.snow = snow;
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
//		
//		SWRLRuleEngine ruleEngine = createRuleEngine(model);
//		ruleEngine.reset();
//		SWRLFactory factory = createSWRLFactory(model);
//		factory.disableAll();
//		Iterator<SWRLImp> iter = factory.getImps().iterator();
//		while(iter.hasNext()) {
//			SWRLImp imp = (SWRLImp) iter.next();
//			
//			if(templateName.size() != 0) {
//				if(imp.getLocalName().contains("addSnowToMaRule")) {
//					for(Iterator<String> its = templateName.iterator();its.hasNext();) {
//						
//						String templateValue = its.next();
//						String templateValue1 = templateValue.replaceAll("Individual", "");
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
//		
//		while(iter.hasNext()) {
//			SWRLImp imp=(SWRLImp) iter.next();
//
//			if(templateName.size() != 0){
//				if(imp.getLocalName().contains("addSnowToMaRule")) {					//找到名字包含“addSnowToSceneRule”的规则
//					for(Iterator<String> its=templateName.iterator();its.hasNext();) {
//						
//						String templateValue= its.next();
//						String templateValue1=templateValue.substring(0,templateValue.indexOf(":"));
//						
//						if(imp.getBody().getBrowserText().contains(templateValue1))	{
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
//	public Document SnowInfer(ArrayList<String> list,OWLModel model,String maName,Document doc) throws OntologyLoadException, SWRLRuleEngineException
//	{
//		String[] SnowList =new String[50];//用来存储抽取出来的符合主题和模板的雪
//		
//		int SnowFromInfoNumber=0;//最终抽取的符合的雪的数量
//		int SnowFromTemNumber=0;
//		int SnowFromTopNumber=0;
//		String str="p4:";
//		String ieTopic = null;
//		ArrayList<String> topicList = new ArrayList<String>();
//		Collection mapToSnowValues = null;
//		OWLIndividual maIndividual = model.getOWLIndividual(maName);//获取maya实例
//		
//		if(null == maIndividual){
//			System.out.println("maya实例无法获取，可能不存在或丢失！请检查maName是否正确或存在？");
//			return doc;
//		}
//		
//		//用到的各种属性
//		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
//		OWLObjectProperty addSnowToMaProperty = model.getOWLObjectProperty(str + "addSnowToMa");
//		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//此变量用来判断场景属于室内室外
//		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
//		
//		if(null == plane){
//			System.out.println("无法获取实例" + maIndividual.getBrowserText() + "的hasValueOfPlace属性(此属性用来判断场景属于室内还是室外)，so 拒绝加雪！请检查知识库中该属性是否存在或正确？");
//			return doc;
//		}
//		else {
//			if(plane.getBrowserText().equals("InWaterDescription")||plane.getBrowserText().equals("inDoorDescription")) {
//				System.out.println("此场景不适合加雪！");
//				return doc;
//			}
//		}
//		
//		//处理主题信息
//		System.out.println("=================场景的主题处理================");
//		System.out.println("===============优先处理IE抽取的主题================");
//		
//		ieTopic = doc.getRootElement().element("maName").attributeValue("topic");
//		if(ieTopic.contains("Topic")){
//			System.out.println("IE Topic = " + ieTopic);
//			topicList.add(ieTopic);
//			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
//		} else {
//			System.out.println("=================IE未抽出主题================");
//			System.out.println("===============处理ma抽取的主题================");
//			
//			Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
//			if(!topics.isEmpty()) {
//				OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//将topics转化为数组
//				
//				for(int i = 0; i < IdTopics.length; i++) {
//					topicList.add(IdTopics[i].getBrowserText());
//				}
//				
//				int topicSize = topicList.size();
//				if(topicSize>0) {
//					System.out.print("抽取出来的主题个数为："+topicSize+"个，是：");
//					
//					for(int i = 0; i < IdTopics.length; i++) {
//						System.out.print(IdTopics[i].getBrowserText() + " ");
//					}
//					System.out.println();
//				}
//				
//				executeTopicToBackgroundSceneSWRLEngine(model,topicList);			
//			}
//		}
//		
//		mapToSnowValues = maIndividual.getPropertyValues(addSnowToMaProperty);
//			
//		if(mapToSnowValues.isEmpty())
//			System.out.println("根据抽取出的主题，没有对应的雪");
//		else {
//				
//			for(Iterator it1 = mapToSnowValues.iterator();it1.hasNext();) {
//				OWLIndividual SnowIndiviual = (OWLIndividual)it1.next();
//				SnowList[SnowFromInfoNumber] = SnowIndiviual.getBrowserText();
//				SnowFromInfoNumber++;
//				SnowFromTopNumber++;
//			}
//				
//			if(SnowFromInfoNumber != 0) {
//				System.out.print("根据主题抽取到"+SnowFromInfoNumber + "个雪，分别是：");
//					
//				for(int i = 0; i < SnowFromInfoNumber; i++)
//					System.out.print(SnowList[i] + " ");
//					
//			System.out.println("\n因主题优先，故模板不作处理！");
//			}
//		}
//
//		System.out.println("================场景主题处理完毕================");
//		
//		//处理模板信息
//		if(SnowFromInfoNumber == 0) {
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
//				//去相应的场景里获取所添加的雪
//				mapToSnowValues = null;
//				mapToSnowValues= maIndividual.getPropertyValues(addSnowToMaProperty);
//				if(mapToSnowValues.isEmpty())
//					System.out.println("根据传递进来的模板，没有相应的雪！");
//				else {
//					
//					for(Iterator it1 = mapToSnowValues.iterator();it1.hasNext();) {
//						OWLIndividual SnowIndiviual = (OWLIndividual)it1.next();
//						SnowList[SnowFromInfoNumber] = SnowIndiviual.getBrowserText();
//						SnowFromInfoNumber++;
//						SnowFromTemNumber++;
//					}
//					
//					if(0 != SnowFromInfoNumber) {
//						System.out.print("根据模板抽到" + SnowFromTemNumber + "个雪，分别是：");
//						for(int i = 0; i < SnowFromTemNumber; i++)
//							System.out.print(SnowList[i] + " ");
//						System.out.println();
//					}
//				}
//			}
//			System.out.println("================模板处理完毕！================");
//		}
//		
//		if(0 == SnowFromInfoNumber) {
//			System.out.println("根据所给条件，没有抽取到适合的雪");
//			return doc;
//		} 
//
//		//在获取到的雪里随机选取一个
//		Random random = new Random();
//		int rd = random.nextInt(SnowFromInfoNumber);
//		
//		OWLIndividual SnowIndiviual = model.getOWLIndividual(SnowList[rd]);
//		System.out.println("随机选取到的雪为：" + SnowIndiviual.getBrowserText());
//		
//		OWLDatatypeProperty Magnitude =  model.getOWLDatatypeProperty(str + "magnitude");
//		String SnowType = (String) SnowIndiviual.getPropertyValue(Magnitude);
//		System.out.println("SnowType = " + SnowType);
//
//		
//		Direction SnowDir;
//		Direction[] Dir = Direction.values();//dir存储枚举的值
//		int num = new Random().nextInt(8);//[0,8]之间的整数
//		SnowDir = Dir[num];
//		String SnowDirection = SnowDir.toString();
//		
//		System.out.println("\n雪的类型：" + SnowType + "\n雪的方向：" + SnowDirection);
//		System.out.println("======================信息获取完毕！=====================");
//		Document doc1 = printSnowRule(doc,SnowType,SnowDirection);
//		return doc1;
//	}
//	
//	public void SnowInfer2(ArrayList<String> list,OWLModel model,String maName,String ieTopic) throws OntologyLoadException, SWRLRuleEngineException
//	{
//		String[] SnowList =new String[50];//用来存储抽取出来的符合主题和模板的雪
//		String str="p4:";
//		int SnowFromInfoNumber=0;//最终抽取的符合的雪的数量
//		ArrayList<String> topicList = new ArrayList<String>();
//		Collection mapToSnowValues = null;
//		OWLIndividual maIndividual = model.getOWLIndividual(maName);//获取maya实例
//		
//		if(null == maIndividual){
//			System.out.println("maya实例无法获取，可能不存在或丢失！请检查maName是否正确或存在？");
//			SnowFromInfoNumber=0;
//			return;
//		}
//		
//		//用到的各种属性
//		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
//		OWLObjectProperty addSnowToMaProperty = model.getOWLObjectProperty(str + "addSnowToMa");
//		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//此变量用来判断场景属于室内室外
//		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
//		
//		if(null == plane){
//			System.out.println("无法获取实例" + maIndividual.getBrowserText() + "的hasValueOfPlace属性(此属性用来判断场景属于室内还是室外)，so 拒绝加雪！请检查知识库中该属性是否存在或正确？");
//			SnowFromInfoNumber=0;
//			return;
//		}else {
//			if(plane.getBrowserText().equals("InWaterDescription")||plane.getBrowserText().equals("inDoorDescription")) {
//				System.out.println("此场景不适合加雪！");
//				SnowFromInfoNumber=0;
//				return;
//			}
//		}
//		
//		//处理主题信息
//		System.out.println("================场景的主题处理================");
//		System.out.println("================优先处理IE主题================");
//		
//		if(ieTopic.contains("Topic")){
//			System.out.println("IE Topic = " + ieTopic);
//			topicList.add(ieTopic);
//			executeTopicToBackgroundSceneSWRLEngine(model, topicList);
//		} else {
//			System.out.println("================IE未抽出主题================");
//			System.out.println("================处理ma抽出的主题================");
//			Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
//			if(!topics.isEmpty()) {
//				OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//将topics转化为数组
//			
//				for(int i = 0; i < IdTopics.length; i++) {
//					topicList.add(IdTopics[i].getBrowserText());
//				}
//			}
//			executeTopicToBackgroundSceneSWRLEngine(model,topicList);	
//		}
//		
//		mapToSnowValues = maIndividual.getPropertyValues(addSnowToMaProperty);
//			
//		if(mapToSnowValues.isEmpty()) {
//			SnowFromInfoNumber=0;
//			System.out.println("根据抽取出的主题，没有对应的雪");
//		}else {
//				
//			for(Iterator it1 = mapToSnowValues.iterator();it1.hasNext();) {
//				OWLIndividual SnowIndiviual = (OWLIndividual)it1.next();
//				SnowList[SnowFromInfoNumber] = SnowIndiviual.getBrowserText();
//				SnowFromInfoNumber++;
//			}
//		}
//		System.out.println("================场景主题处理完毕================");
//		
//		//处理模板信息
//		if(SnowFromInfoNumber == 0) {
//			System.out.println("==================模板处理====================");
//			if(list.size() > 0) {
//				executeTemplateToBackgroundSceneSWRLEngine(model,list);
//				
//				//去相应的场景里获取所添加的雪
//				mapToSnowValues = null;
//				mapToSnowValues= maIndividual.getPropertyValues(addSnowToMaProperty);
//				if(mapToSnowValues.isEmpty())
//					SnowFromInfoNumber=0;
//				else {
//					
//					for(Iterator it1 = mapToSnowValues.iterator();it1.hasNext();) {
//						OWLIndividual SnowIndiviual = (OWLIndividual)it1.next();
//						SnowList[SnowFromInfoNumber] = SnowIndiviual.getBrowserText();
//						SnowFromInfoNumber++;
//					}
//				}
//			}
//			System.out.println("================模板处理完毕！================");
//		}
//		System.out.println("SnowFormInfoNumber:" + SnowFromInfoNumber);
//		setSnow(SnowFromInfoNumber);	
//	}
//	
////	public Document SnowInfer(ArrayList<String> list,OWLModel model,String maName,Document doc) throws OntologyLoadException, SWRLRuleEngineException
////	{
////		String[] SnowList =new String[50];//用来存储抽取出来的符合主题和模板的雪
////		String str="p4:";
////		int SnowFromInfoNumber=0;//最终抽取的符合的雪的数量
////		int SnowFromTemNumber=0;
////		int SnowFromTopNumber=0;
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
////		OWLObjectProperty addSnowToMaProperty = model.getOWLObjectProperty(str + "addSnowToMa");
////		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//此变量用来判断场景属于室内室外
////		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
////		
////		if(null == plane){
////			System.out.println("无法获取实例" + maIndividual.getBrowserText() + "的hasValueOfPlace属性(此属性用来判断场景属于室内还是室外)，so 拒绝加雪！请检查知识库中该属性是否存在或正确？");
////			return doc;
////		}
////		else {
////			if(plane.getBrowserText().equals("InWaterDescription")||plane.getBrowserText().equals("inDoorDescription")) {
////				System.out.println("此场景不适合加雪！");
////				return doc;
////			}
////		}
////		
////		//处理主题信息
////		System.out.println("================场景的主题处理================");
////		Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
////		
////		if(!topics.isEmpty()) {
////			OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//将topics转化为数组
////			ArrayList<String> topicList = new ArrayList<String>();
////			
////			for(int i = 0; i < IdTopics.length; i++) {
////				topicList.add(IdTopics[i].getBrowserText());
////			}
////			
////			int topicSize = topicList.size();
////			if(topicSize>0) {
////				System.out.print("抽取出来的主题个数为："+topicSize+"个，是：");
////				
////				for(int i = 0; i < IdTopics.length; i++) {
////					System.out.print(IdTopics[i].getBrowserText() + " ");
////				}
////				System.out.println();
////			}
////			
////			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
////			Collection mapToSnowValues = maIndividual.getPropertyValues(addSnowToMaProperty);
////			
////			if(mapToSnowValues.isEmpty())
////				System.out.println("根据抽取出的主题，没有对应的雪");
////			else {
////				
////				for(Iterator it1 = mapToSnowValues.iterator();it1.hasNext();) {
////					OWLIndividual SnowIndiviual = (OWLIndividual)it1.next();
////					SnowList[SnowFromInfoNumber] = SnowIndiviual.getBrowserText();
////					SnowFromInfoNumber++;
////					SnowFromTopNumber++;
////				}
////				
////				if(SnowFromInfoNumber != 0) {
////					System.out.print("根据主题抽取到"+SnowFromInfoNumber + "个雪，分别是：");
////					
////					for(int i = 0; i < SnowFromInfoNumber; i++)
////						System.out.print(SnowList[i] + " ");
////					
////					System.out.println("\n因主题优先，故模板不作处理！");
////				}
////			}
////		} else 
////			System.out.println("该" + maName + "场景未抽取到主题！");
////		System.out.println("================场景主题处理完毕================");
////		
////		//处理模板信息
////		if(SnowFromInfoNumber == 0) {
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
////				//去相应的场景里获取所添加的雪
////				Collection mapToSnowValues= maIndividual.getPropertyValues(addSnowToMaProperty);
////				if(mapToSnowValues.isEmpty())
////					System.out.println("根据传递进来的模板，没有相应的雪！");
////				else {
////					
////					for(Iterator it1 = mapToSnowValues.iterator();it1.hasNext();) {
////						OWLIndividual SnowIndiviual = (OWLIndividual)it1.next();
////						SnowList[SnowFromInfoNumber] = SnowIndiviual.getBrowserText();
////						SnowFromInfoNumber++;
////						SnowFromTemNumber++;
////					}
////					
////					if(0 != SnowFromInfoNumber) {
////						System.out.print("根据模板抽到" + SnowFromTemNumber + "个雪，分别是：");
////						for(int i = 0; i < SnowFromTemNumber; i++)
////							System.out.print(SnowList[i] + " ");
////						System.out.println();
////					}
////				}
////			}
////			System.out.println("================模板处理完毕！================");
////		}
////		
////		if(0 == SnowFromInfoNumber) {
////			System.out.println("根据所给条件，没有抽取到适合的雪");
////			return doc;
////		} 
////
////		//在获取到的雪里随机选取一个
////		java.util.Random random = new Random();
////		int rd = random.nextInt(SnowFromInfoNumber);
////		
////		OWLIndividual SnowIndiviual = model.getOWLIndividual(SnowList[rd]);
////		System.out.print("随机选取到的雪为：" + SnowIndiviual.getBrowserText());
////		
////		int strLength = SnowIndiviual.getBrowserText().length();
////		String SnowType = SnowIndiviual.getBrowserText().substring(3,strLength-5);
////		
////		Direction SnowDir;
////		Direction[] Dir = Direction.values();//dir存储枚举的值
////		int num = new Random().nextInt(8);//[0,8]之间的整数
////		SnowDir = Dir[num];
////		String SnowDirection = SnowDir.toString();
////		
////		System.out.println("\n雪的类型：" + SnowType + "\n雪的方向：" + SnowDirection);
////		System.out.println("======================信息获取完毕！=====================");
////		Document doc1 = printSnowRule(doc,SnowType,SnowDirection);
////		return doc1;
////	}
////	
////	public void SnowInfer2(ArrayList<String> list,OWLModel model,String maName) throws OntologyLoadException, SWRLRuleEngineException
////	{
////		String[] SnowList =new String[50];//用来存储抽取出来的符合主题和模板的雪
////		String str="p4:";
////		int SnowFromInfoNumber=0;//最终抽取的符合的雪的数量
////		
////		OWLIndividual maIndividual = model.getOWLIndividual(maName);//获取maya实例
////		
////		if(null == maIndividual){
////			System.out.println("maya实例无法获取，可能不存在或丢失！请检查maName是否正确或存在？");
////			SnowFromInfoNumber=0;
////			return;
////		}
////		
////		//用到的各种属性
////		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
////		OWLObjectProperty addSnowToMaProperty = model.getOWLObjectProperty(str + "addSnowToMa");
////		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//此变量用来判断场景属于室内室外
////		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
////		
////		if(null == plane){
////			System.out.println("无法获取实例" + maIndividual.getBrowserText() + "的hasValueOfPlace属性(此属性用来判断场景属于室内还是室外)，so 拒绝加雪！请检查知识库中该属性是否存在或正确？");
////			SnowFromInfoNumber=0;
////			return;
////		}else {
////			if(plane.getBrowserText().equals("InWaterDescription")||plane.getBrowserText().equals("inDoorDescription")) {
////				System.out.println("此场景不适合加雪！");
////				SnowFromInfoNumber=0;
////				return;
////			}
////		}
////		
////		//处理主题信息
////		System.out.println("================场景的主题处理================");
////		Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
////		
////		if(!topics.isEmpty()) {
////			OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//将topics转化为数组
////			ArrayList<String> topicList = new ArrayList<String>();
////			
////			for(int i = 0; i < IdTopics.length; i++) {
////				topicList.add(IdTopics[i].getBrowserText());
////			}
////			
////			int topicSize = topicList.size();
////			if(topicSize>0) {
////				System.out.print("抽取出来的主题个数为："+topicSize+"个，是：");
////				
////				for(int i = 0; i < IdTopics.length; i++) {
////					System.out.print(IdTopics[i].getBrowserText() + " ");
////				}
////				System.out.println();
////			}
////			
////			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
////			Collection mapToSnowValues = maIndividual.getPropertyValues(addSnowToMaProperty);
////			
////			if(mapToSnowValues.isEmpty()) {
////				SnowFromInfoNumber=0;
////				System.out.println("根据抽取出的主题，没有对应的雪");}
////			else {
////				
////				for(Iterator it1 = mapToSnowValues.iterator();it1.hasNext();) {
////					OWLIndividual SnowIndiviual = (OWLIndividual)it1.next();
////					SnowList[SnowFromInfoNumber] = SnowIndiviual.getBrowserText();
////					SnowFromInfoNumber++;
////				}
////			}
////		} else 
////			SnowFromInfoNumber=0;
////		System.out.println("================场景主题处理完毕================");
////		
////		//处理模板信息
////		if(SnowFromInfoNumber == 0) {
////			System.out.println("==================模板处理====================");
////			if(list.size() > 0) {
////				executeTemplateToBackgroundSceneSWRLEngine(model,list);
////				
////				//去相应的场景里获取所添加的雪
////				Collection mapToSnowValues= maIndividual.getPropertyValues(addSnowToMaProperty);
////				if(mapToSnowValues.isEmpty())
////					SnowFromInfoNumber=0;
////				else {
////					
////					for(Iterator it1 = mapToSnowValues.iterator();it1.hasNext();) {
////						OWLIndividual SnowIndiviual = (OWLIndividual)it1.next();
////						SnowList[SnowFromInfoNumber] = SnowIndiviual.getBrowserText();
////						SnowFromInfoNumber++;
////					}
////				}
////			}
////			System.out.println("================模板处理完毕！================");
////		}
////		System.out.println("SnowFormInfoNumber:" + SnowFromInfoNumber);
////		setSnow(SnowFromInfoNumber);
////		
////	}
//	
//	public Document printSnowRule(Document doc,String SnowType,String SnowDirection)
//	{
//		System.out.println("=====================开始生成xml-rule======================");
//		Element rootName = (Element) doc.getRootElement();
//		Element name = rootName.element("maName");
//		Element ruleName = name.addElement("rule");
//		ruleName.addAttribute("ruleType", "addEffectToMa");
//		ruleName.addAttribute("type","Snow");
//		ruleName.addAttribute("Magnitude", SnowType);
//		ruleName.addAttribute("Direction", SnowDirection);
//		System.out.println("xml-rule生成完毕");
//		return doc;
//	}	
//	
//	public static void main(String[] args)throws ParserConfigurationException,SAXException,IOException,JDOMException,DocumentException,OntologyLoadException,SWRLRuleEngineException 
//	{
//		String owlpath = "file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl" ;
//		OWLModel model = ProtegeOWL.createJenaOWLModelFromURI(owlpath);
//		
//		ArrayList<String> aList = new ArrayList(); 
//		aList.add("SnowTemplate:SnowTemplate");
//		aList.add("WindTemplate:WindTemplate");
//		aList.add("SummerTemplate:summerTemplate");
//		aList.add("AngryTopic:angryTopic");
//		aList.add("GladnessTemplate:gladnessTemplate");
//		
//		File file = new File("f:\\test1.xml");
//		SAXReader saxReader = new SAXReader();
//		Document document = saxReader.read(file);
//		SnowInsert  Snow = new SnowInsert();
//		
//		System.out.println("开始!");
//		Document document1 = Snow.SnowInfer(aList, model, "schoolroomOut.ma",document);
//		String maName = null;
//	//	Document document1 = Snow.SnowInfer(aList, model, maName,document);
//	//	Document document1 = Snow.SnowInfer(aList, model, "Tropical45.ma",document);
//		Snow.SnowInfer2(aList, model, "Tropical45.ma","");
//		XMLWriter writer = new XMLWriter(new FileWriter("f:\\testSnow.xml"));
//		writer.write(document1); 
//		System.out.println("结束！");
//		System.out.println("snow number:" + Snow.getSnow());
//		writer.close();
//	}
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
////private int snow = 0;
////	static Logger logger = Logger.getLogger(SnowInsert.class.getName());
////	
////	public int getSnow() {
////		return snow;
////	}
////
////	public void setSnow(int snow) {
////		this.snow = snow;
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
////	public void  executeTopicToBackgroundSceneSWRLEngine(OWLModel model,ArrayList<String> templateName) throws SWRLRuleEngineException {
////		
////		SWRLRuleEngine ruleEngine = createRuleEngine(model);
////		ruleEngine.reset();
////		SWRLFactory factory = createSWRLFactory(model);
////		factory.disableAll();
////		Iterator<SWRLImp> iter = factory.getImps().iterator();
////		while(iter.hasNext()) {
////			SWRLImp imp = (SWRLImp) iter.next();
////			
////			if(templateName.size() != 0) {
////				if(imp.getLocalName().contains("addSnowToMaRule")) {
////					for(Iterator<String> its = templateName.iterator();its.hasNext();) {
////						
////						String templateValue = its.next();
////						String templateValue1 = templateValue.replaceAll("Individual", "");
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
////	public void executeTemplateToBackgroundSceneSWRLEngine(OWLModel model,ArrayList<String> templateName) throws SWRLRuleEngineException
////	{
////		SWRLRuleEngine ruleEngine = createRuleEngine(model);
////		ruleEngine.reset();
////		SWRLFactory factory = createSWRLFactory(model);
////		factory.disableAll();
////		Iterator<SWRLImp> iter = factory.getImps().iterator();
////		
////		while(iter.hasNext()) {
////			SWRLImp imp=(SWRLImp) iter.next();
////
////			if(templateName.size() != 0){
////				if(imp.getLocalName().contains("addSnowToMaRule")) {					//找到名字包含“addSnowToSceneRule”的规则
////					for(Iterator<String> its=templateName.iterator();its.hasNext();) {
////						
////						String templateValue= its.next();
////						String templateValue1=templateValue.substring(0,templateValue.indexOf(":"));
////						
////						if(imp.getBody().getBrowserText().contains(templateValue1))	{
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
////	public Document SnowInfer(ArrayList<String> list,OWLModel model,String maName,Document doc) throws OntologyLoadException, SWRLRuleEngineException
////	{
////		String[] SnowList =new String[50];//用来存储抽取出来的符合主题和模板的雪
////		String str="p4:";
////		int SnowFromInfoNumber=0;//最终抽取的符合的雪的数量
////		int SnowFromTemNumber=0;
////		int SnowFromTopNumber=0;
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
////		OWLObjectProperty addSnowToMaProperty = model.getOWLObjectProperty(str + "addSnowToMa");
////		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//此变量用来判断场景属于室内室外
////		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
////		
////		if(null == plane)
////			return doc;
////		else {
////			if(plane.getBrowserText().equals("InWaterDescription")||plane.getBrowserText().equals("inDoorDescription")) {
////				System.out.println("此场景不适合加雪！");
////				return doc;
////			}
////		}
////		
////		//处理主题信息
////		System.out.println("================场景的主题处理================");
////		Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
////		
////		if(!topics.isEmpty()) {
////			OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//将topics转化为数组
////			ArrayList<String> topicList = new ArrayList<String>();
////			
////			for(int i = 0; i < IdTopics.length; i++) {
////				topicList.add(IdTopics[i].getBrowserText());
////			}
////			
////			int topicSize = topicList.size();
////			if(topicSize>0) {
////				System.out.print("抽取出来的主题个数为："+topicSize+"个，是：");
////				
////				for(int i = 0; i < IdTopics.length; i++) {
////					System.out.print(IdTopics[i].getBrowserText() + " ");
////				}
////				System.out.println();
////			}
////			
////			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
////			Collection mapToSnowValues = maIndividual.getPropertyValues(addSnowToMaProperty);
////			
////			if(mapToSnowValues.isEmpty())
////				System.out.println("根据抽取出的主题，没有对应的雪");
////			else {
////				
////				for(Iterator it1 = mapToSnowValues.iterator();it1.hasNext();) {
////					OWLIndividual SnowIndiviual = (OWLIndividual)it1.next();
////					SnowList[SnowFromInfoNumber] = SnowIndiviual.getBrowserText();
////					SnowFromInfoNumber++;
////					SnowFromTopNumber++;
////				}
////				
////				if(SnowFromInfoNumber != 0) {
////					System.out.print("根据主题抽取到"+SnowFromInfoNumber + "个雪，分别是：");
////					
////					for(int i = 0; i < SnowFromInfoNumber; i++)
////						System.out.print(SnowList[i] + " ");
////					
////					System.out.println("\n因主题优先，故模板不作处理！");
////				}
////			}
////		} else 
////			System.out.println("该" + maName + "场景未抽取到主题！");
////		System.out.println("================场景主题处理完毕================");
////		
////		//处理模板信息
////		if(SnowFromTopNumber == 0) {
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
////				//去相应的场景里获取所添加的雪
////				Collection mapToSnowValues= maIndividual.getPropertyValues(addSnowToMaProperty);
////				if(mapToSnowValues.isEmpty())
////					System.out.println("根据传递进来的模板，没有相应的雪！");
////				else {
////					
////					for(Iterator it1 = mapToSnowValues.iterator();it1.hasNext();) {
////						OWLIndividual SnowIndiviual = (OWLIndividual)it1.next();
////						SnowList[SnowFromInfoNumber] = SnowIndiviual.getBrowserText();
////						SnowFromInfoNumber++;
////						SnowFromTemNumber++;
////					}
////					
////					if(0 != SnowFromInfoNumber) {
////						System.out.print("根据模板抽到" + SnowFromTemNumber + "个雪，分别是：");
////						for(int i = 0; i < SnowFromTemNumber; i++)
////							System.out.print(SnowList[i] + " ");
////						System.out.println();
////					}
////				}
////			}
////			System.out.println("================模板处理完毕！================");
////		}
////		
////		if(0 == SnowFromInfoNumber) {
////			System.out.println("根据所给条件，没有抽取到适合的雪");
////			return doc;
////		} else 
////			setSnow(SnowFromInfoNumber);
////		
////		//在获取到的雪里随机选取一个
////		java.util.Random random = new Random();
////		int rd = random.nextInt(SnowFromInfoNumber);
////		
////		OWLIndividual SnowIndiviual = model.getOWLIndividual(SnowList[rd]);
////		System.out.print("随机选取到的雪为：" + SnowIndiviual.getBrowserText());
////		
////		int strLength = SnowIndiviual.getBrowserText().length();
////		String SnowType = SnowIndiviual.getBrowserText().substring(3,strLength-5);
////		
////		Direction SnowDir;
////		Direction[] Dir = Direction.values();//dir存储枚举的值
////		int num = new Random().nextInt(8);//[0,8]之间的整数
////		SnowDir = Dir[num];
////		String SnowDirection = SnowDir.toString();
////		
////		System.out.println("\n雪的类型：" + SnowType + "\n雪的方向：" + SnowDirection);
////		System.out.println("======================信息获取完毕！=====================");
////		Document doc1 = printSnowRule(doc,SnowType,SnowDirection);
////		return doc1;
////	}
////	
////	public void SnowInfer2(ArrayList<String> list,OWLModel model,String maName) throws OntologyLoadException, SWRLRuleEngineException
////	{
////		String[] SnowList =new String[50];//用来存储抽取出来的符合主题和模板的雪
////		String str="p4:";
////		int SnowFromInfoNumber=0;//最终抽取的符合的雪的数量
////		int SnowFromTemNumber=0;
////		int SnowFromTopNumber=0;
////		
////		OWLIndividual maIndividual = model.getOWLIndividual(maName);//获取maya实例
////		
////		if(null == maIndividual){
////			System.out.println("maya实例无法获取，可能不存在或丢失！请检查maName是否正确或存在？");
////		//	return doc;
////			SnowFromInfoNumber=0;
////		}
////		
////		//用到的各种属性
////		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
////		OWLObjectProperty addSnowToMaProperty = model.getOWLObjectProperty(str + "addSnowToMa");
////		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//此变量用来判断场景属于室内室外
////		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
////		
////		if(null == plane){
////			//return doc;
////			SnowFromInfoNumber=0;
////		}else {
////			if(plane.getBrowserText().equals("InWaterDescription")||plane.getBrowserText().equals("inDoorDescription")) {
////				System.out.println("此场景不适合加雪！");
////				//return doc;
////				SnowFromInfoNumber=0;
////			}
////		}
////		
////		//处理主题信息
////		System.out.println("================场景的主题处理================");
////		Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
////		
////		if(!topics.isEmpty()) {
////			OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//将topics转化为数组
////			ArrayList<String> topicList = new ArrayList<String>();
////			
////			for(int i = 0; i < IdTopics.length; i++) {
////				topicList.add(IdTopics[i].getBrowserText());
////			}
////			
////			int topicSize = topicList.size();
////			if(topicSize>0) {
////				System.out.print("抽取出来的主题个数为："+topicSize+"个，是：");
////				
////				for(int i = 0; i < IdTopics.length; i++) {
////					System.out.print(IdTopics[i].getBrowserText() + " ");
////				}
////				System.out.println();
////			}
////			
////			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
////			Collection mapToSnowValues = maIndividual.getPropertyValues(addSnowToMaProperty);
////			
////			if(mapToSnowValues.isEmpty())
////				{SnowFromInfoNumber=0;
////				System.out.println("根据抽取出的主题，没有对应的雪");}
////			else {
////				
////				for(Iterator it1 = mapToSnowValues.iterator();it1.hasNext();) {
////					OWLIndividual SnowIndiviual = (OWLIndividual)it1.next();
////					SnowList[SnowFromInfoNumber] = SnowIndiviual.getBrowserText();
////					SnowFromInfoNumber++;
////					SnowFromTopNumber++;
////				}
////				
////			}
////		} else 
////			SnowFromInfoNumber=0;
////		System.out.println("================场景主题处理完毕================");
////		
////		//处理模板信息
////		if(SnowFromTopNumber == 0) {
////			System.out.println("==================模板处理====================");
////			if(list.size() > 0) {
////				executeTemplateToBackgroundSceneSWRLEngine(model,list);
////				
////				//去相应的场景里获取所添加的雪
////				Collection mapToSnowValues= maIndividual.getPropertyValues(addSnowToMaProperty);
////				if(mapToSnowValues.isEmpty())
////					SnowFromInfoNumber=0;
////				else {
////					
////					for(Iterator it1 = mapToSnowValues.iterator();it1.hasNext();) {
////						OWLIndividual SnowIndiviual = (OWLIndividual)it1.next();
////						SnowList[SnowFromInfoNumber] = SnowIndiviual.getBrowserText();
////						SnowFromInfoNumber++;
////						SnowFromTemNumber++;
////					}
////				}
////			}
////			System.out.println("================模板处理完毕！================");
////		}
////			setSnow(SnowFromInfoNumber);
////		
////	}
////	
////	public Document printSnowRule(Document doc,String SnowType,String SnowDirection)
////	{
////		System.out.println("=====================开始生成xml-rule======================");
////		Element rootName = (Element) doc.getRootElement();
////		Element name = rootName.element("maName");
////		Element ruleName = name.addElement("rule");
////		ruleName.addAttribute("ruleType", "addEffectToMa");
////		ruleName.addAttribute("type","Snow");
////		ruleName.addAttribute("Magnitude", SnowType);
////		ruleName.addAttribute("Direction", SnowDirection);
////		System.out.println("xml-rule生成完毕");
////		return doc;
////	}	
////	
////	public static void main(String[] args)throws ParserConfigurationException,SAXException,IOException,JDOMException,DocumentException,OntologyLoadException,SWRLRuleEngineException 
////	{
////		String owlpath = "file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl" ;
////		OWLModel model = ProtegeOWL.createJenaOWLModelFromURI(owlpath);
////		
////		ArrayList<String> aList = new ArrayList(); 
////		aList.add("SnowTemplate:SnowTemplate");
////		aList.add("WindTemplate:WindTemplate");
////		aList.add("SummerTemplate:summerTemplate");
////		aList.add("AngryTopic:angryTopic");
////		aList.add("GladnessTemplate:gladnessTemplate");
////		
////		File file = new File("f:\\test1.xml");
////		SAXReader saxReader = new SAXReader();
////		Document document = saxReader.read(file);
////		SnowInsert  Snow = new SnowInsert();
////		
////		System.out.println("开始!");
////	//	Document document1 = Snow.SnowInfer(aList, model, "schoolroomOut.ma",document);
////		String maName = null;
////		Document document1 = Snow.SnowInfer(aList, model, maName,document);
////		XMLWriter writer = new XMLWriter(new FileWriter("f:\\testSnow.xml"));
////		writer.write(document1); 
////		System.out.println("结束！");
////		System.out.println("snow number:" + Snow.getSnow());
////		writer.close();
////	}
