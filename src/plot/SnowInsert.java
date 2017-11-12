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
							logger.info("���еĹ�������Ϊ��"+imp.getLocalName());
							imp.enable();//ִ�д˹���
						}
					}
					
				}
			}
		}
		ruleEngine.infer();
	}
	
	//ִ��OWL��Ĺ���
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
				if(imp.getLocalName().contains("addSnowToMaRule")) {					//�ҵ����ְ�����addSnowToSceneRule���Ĺ���
					for(Iterator<String> its=templateName.iterator();its.hasNext();) {
						
						String templateValue= its.next();
						String templateValue1=templateValue.substring(0,templateValue.indexOf(":"));
						
						if(imp.getBody().getBrowserText().contains(templateValue1))	{
							logger.info("���еĹ������ƣ�"+imp.getLocalName());
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
		String[] SnowList =new String[50];//�����洢��ȡ�����ķ��������ģ���ѩ
		
		int SnowFromInfoNumber=0;//���ճ�ȡ�ķ��ϵ�ѩ������
		int SnowFromTemNumber=0;
		int SnowFromTopNumber=0;
		String str="p4:";
		String ieTopic = null;
		ArrayList<String> topicList = new ArrayList<String>();
		ArrayList<String> list = new ArrayList<String>();
		Collection mapToSnowValues = null;
		OWLIndividual maIndividual = model.getOWLIndividual(maName);//��ȡmayaʵ��
		
//		list ��ŵ��Ǵ��ݵ�ģ������⣬�ֶ������һ�δ�����һ����ߵ������ģ���Ƿ����topicAndTemplate�С�
		int number = 0;
		Random rand = new Random();
		for(int i = 0; i < List.size(); i++){
			String strTemp = List.get(i);
			if(topicAndTemplate.contains(strTemp.split(":")[0])){          //strTemp ������topicAndTemplate  �ԣ� �����������һ��ֵ
				number = rand.nextInt(10) + 1;
				if(number > 8){
					list.add(strTemp);
				}
			} else {
				list.add(strTemp);
			}
		}
		
		System.out.println("######################ʵ����ִ�е�ģ�������######################");
		for(int i = 0; i < list.size(); i++){
			System.out.println(list.get(i));
		}
		System.out.println("############################################################");
		if(null == maIndividual){
			System.out.println("mayaʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ����ڣ�");
			return doc;
		}
		
		//�õ��ĸ�������
		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
		OWLObjectProperty addSnowToMaProperty = model.getOWLObjectProperty(str + "addSnowToMa");
		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//�˱��������жϳ���������������
		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
		
		if(null == plane){
			System.out.println("�޷���ȡʵ��" + maIndividual.getBrowserText() + "��hasValueOfPlace����(�����������жϳ����������ڻ�������)��so �ܾ���ѩ������֪ʶ���и������Ƿ���ڻ���ȷ��");
			return doc;
		}
		else {
			if(plane.getBrowserText().equals("InWaterDescription")||plane.getBrowserText().equals("inDoorDescription")) {
				System.out.println("�˳������ʺϼ�ѩ��");
				return doc;
			}
		}
		
		//����������Ϣ
		System.out.println("=================���������⴦��================");
		System.out.println("===============���ȴ���IE��ȡ������================");
		
		ieTopic = doc.getRootElement().element("maName").attributeValue("topic");
		if(ieTopic.contains("Topic")){
			System.out.println("IE Topic = " + ieTopic);
			topicList.add(ieTopic);
			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
		} else {
			System.out.println("=================IEδ�������================");
			System.out.println("===============����ma��ȡ������================");
			
			Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
			if(!topics.isEmpty()) {
				OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//��topicsת��Ϊ����
				
				for(int i = 0; i < IdTopics.length; i++) {
					topicList.add(IdTopics[i].getBrowserText());
				}
				
				int topicSize = topicList.size();
				if(topicSize>0) {
					System.out.print("��ȡ�������������Ϊ��"+topicSize+"�����ǣ�");
					
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
			System.out.println("���ݳ�ȡ�������⣬û�ж�Ӧ��ѩ");
		else {
				
			for(Iterator it1 = mapToSnowValues.iterator();it1.hasNext();) {
				OWLIndividual SnowIndiviual = (OWLIndividual)it1.next();
				SnowList[SnowFromInfoNumber] = SnowIndiviual.getBrowserText();
				SnowFromInfoNumber++;
				SnowFromTopNumber++;
			}
				
			if(SnowFromInfoNumber != 0) {
				System.out.print("���������ȡ��"+SnowFromInfoNumber + "��ѩ���ֱ��ǣ�");
					
				for(int i = 0; i < SnowFromInfoNumber; i++)
					System.out.print(SnowList[i] + " ");
					
			System.out.println("\n���������ȣ���ģ�岻������");
			}
		}

		System.out.println("================�������⴦�����================");
		
		//����ģ����Ϣ
		if(SnowFromInfoNumber == 0) {
			System.out.println("==================ģ�崦��====================");
			int listSize = 0;
			if(null != list)
				listSize = list.size();//ģ�����
			else
				System.out.println("δ���ݽ����κ�ģ��");
			
			if(listSize > 0) {
				System.out.println("���ݽ�����ģ�����Ϊ��" + listSize + "�����ֱ��ǣ�" + list);
				executeTemplateToBackgroundSceneSWRLEngine(model,list);
				
				//ȥ��Ӧ�ĳ������ȡ����ӵ�ѩ
				mapToSnowValues = null;
				mapToSnowValues= maIndividual.getPropertyValues(addSnowToMaProperty);
				if(mapToSnowValues.isEmpty())
					System.out.println("���ݴ��ݽ�����ģ�壬û����Ӧ��ѩ��");
				else {
					
					for(Iterator it1 = mapToSnowValues.iterator();it1.hasNext();) {
						OWLIndividual SnowIndiviual = (OWLIndividual)it1.next();
						SnowList[SnowFromInfoNumber] = SnowIndiviual.getBrowserText();
						SnowFromInfoNumber++;
						SnowFromTemNumber++;
					}
					
					if(0 != SnowFromInfoNumber) {
						System.out.print("����ģ��鵽" + SnowFromTemNumber + "��ѩ���ֱ��ǣ�");
						for(int i = 0; i < SnowFromTemNumber; i++)
							System.out.print(SnowList[i] + " ");
						System.out.println();
					}
				}
			}
			System.out.println("================ģ�崦����ϣ�================");
		}
		
		if(0 == SnowFromInfoNumber) {
			System.out.println("��������������û�г�ȡ���ʺϵ�ѩ");
			return doc;
		} 

		//�ڻ�ȡ����ѩ�����ѡȡһ��
		Random random = new Random();
		int rd = random.nextInt(SnowFromInfoNumber);
		
		OWLIndividual SnowIndiviual = model.getOWLIndividual(SnowList[rd]);
		System.out.println("���ѡȡ����ѩΪ��" + SnowIndiviual.getBrowserText());
		
		OWLDatatypeProperty Magnitude =  model.getOWLDatatypeProperty(str + "magnitude");
		String SnowType = (String) SnowIndiviual.getPropertyValue(Magnitude);
		System.out.println("SnowType = " + SnowType);

		
		Direction SnowDir;
		Direction[] Dir = Direction.values();//dir�洢ö�ٵ�ֵ
		int num = new Random().nextInt(8);//[0,8]֮�������
		SnowDir = Dir[num];
		String SnowDirection = SnowDir.toString();
		
		System.out.println("\nѩ�����ͣ�" + SnowType + "\nѩ�ķ���" + SnowDirection);
		System.out.println("======================��Ϣ��ȡ��ϣ�=====================");
		Document doc1 = printSnowRule(doc,SnowType,SnowDirection);
		return doc1;
	}
	
	public void SnowInfer2(ArrayList<String> List,OWLModel model,String maName,String ieTopic) throws OntologyLoadException, SWRLRuleEngineException
	{
		String[] SnowList =new String[50];//�����洢��ȡ�����ķ��������ģ���ѩ
		String str="p4:";
		int SnowFromInfoNumber=0;//���ճ�ȡ�ķ��ϵ�ѩ������
		ArrayList<String> topicList = new ArrayList<String>();
		ArrayList<String> list = new ArrayList<String>();
		Collection mapToSnowValues = null;
		OWLIndividual maIndividual = model.getOWLIndividual(maName);//��ȡmayaʵ��
		
		int number = 0;
		Random rand = new Random();
		for(int i = 0; i < List.size(); i++){
			String strTemp = List.get(i);
			if(topicAndTemplate.contains(strTemp.split(":")[0])){          //strTemp ������topicAndTemplate  �ԣ� �����������һ��ֵ
				number = rand.nextInt(10) + 1;
				if(number > 8){
					list.add(strTemp);
				}
			} else {
				list.add(strTemp);
			}
		}
		if(null == maIndividual){
			System.out.println("mayaʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ����ڣ�");
			SnowFromInfoNumber=0;
			return;
		}
		
		//�õ��ĸ�������
		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
		OWLObjectProperty addSnowToMaProperty = model.getOWLObjectProperty(str + "addSnowToMa");
		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//�˱��������жϳ���������������
		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
		
		if(null == plane){
			System.out.println("�޷���ȡʵ��" + maIndividual.getBrowserText() + "��hasValueOfPlace����(�����������жϳ����������ڻ�������)��so �ܾ���ѩ������֪ʶ���и������Ƿ���ڻ���ȷ��");
			SnowFromInfoNumber=0;
			return;
		}else {
			if(plane.getBrowserText().equals("InWaterDescription")||plane.getBrowserText().equals("inDoorDescription")) {
				System.out.println("�˳������ʺϼ�ѩ��");
				SnowFromInfoNumber=0;
				return;
			}
		}
		
		//����������Ϣ
		System.out.println("================���������⴦��================");
		System.out.println("================���ȴ���IE����================");
		
		if(ieTopic.contains("Topic")){
			System.out.println("IE Topic = " + ieTopic);
			topicList.add(ieTopic);
			executeTopicToBackgroundSceneSWRLEngine(model, topicList);
		} else {
			System.out.println("================IEδ�������================");
			System.out.println("================����ma���������================");
			Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
			if(!topics.isEmpty()) {
				OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//��topicsת��Ϊ����
			
				for(int i = 0; i < IdTopics.length; i++) {
					topicList.add(IdTopics[i].getBrowserText());
				}
			}
			executeTopicToBackgroundSceneSWRLEngine(model,topicList);	
		}
		
		mapToSnowValues = maIndividual.getPropertyValues(addSnowToMaProperty);
			
		if(mapToSnowValues.isEmpty()) {
			SnowFromInfoNumber=0;
			System.out.println("���ݳ�ȡ�������⣬û�ж�Ӧ��ѩ");
		}else {
				
			for(Iterator it1 = mapToSnowValues.iterator();it1.hasNext();) {
				OWLIndividual SnowIndiviual = (OWLIndividual)it1.next();
				SnowList[SnowFromInfoNumber] = SnowIndiviual.getBrowserText();
				SnowFromInfoNumber++;
			}
		}
		System.out.println("================�������⴦�����================");
		
		//����ģ����Ϣ
		if(SnowFromInfoNumber == 0) {
			System.out.println("==================ģ�崦��====================");
			if(list.size() > 0) {
				executeTemplateToBackgroundSceneSWRLEngine(model,list);
				
				//ȥ��Ӧ�ĳ������ȡ����ӵ�ѩ
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
			System.out.println("================ģ�崦����ϣ�================");
		}
		System.out.println("SnowFormInfoNumber:" + SnowFromInfoNumber);
		setSnow(SnowFromInfoNumber);	
	}
	
	public Document printSnowRule(Document doc,String SnowType,String SnowDirection)
	{
		System.out.println("=====================��ʼ����xml-rule======================");
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "addEffectToMa");
		ruleName.addAttribute("type","Snow");
		ruleName.addAttribute("Magnitude", SnowType);
		ruleName.addAttribute("Direction", SnowDirection);
		System.out.println("xml-rule�������");
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
		
		System.out.println("��ʼ!");
	Document document1 = Snow.SnowInfer(aList, model, "schoolroomOut.ma",document);
	//	String maName = null;
	//	SnowInsert  snow = new SnowInsert();
	//	snow.SnowInfer2(aList, model,"schoolroomOut.ma","MissTopic");
	//	Document document1 = Snow.SnowInfer(aList, model, "Tropical45.ma",document);
//		Snow.SnowInfer2(aList, model, "Tropical45.ma");
		XMLWriter writer = new XMLWriter(new FileWriter("f:\\testSnow.xml"));
		writer.write(document1); 
		System.out.println("������");
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
//							logger.info("���еĹ�������Ϊ��"+imp.getLocalName());
//							imp.enable();//ִ�д˹���
//						}
//					}
//					
//				}
//			}
//		}
//		ruleEngine.infer();
//	}
//	
//	//ִ��OWL��Ĺ���
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
//				if(imp.getLocalName().contains("addSnowToMaRule")) {					//�ҵ����ְ�����addSnowToSceneRule���Ĺ���
//					for(Iterator<String> its=templateName.iterator();its.hasNext();) {
//						
//						String templateValue= its.next();
//						String templateValue1=templateValue.substring(0,templateValue.indexOf(":"));
//						
//						if(imp.getBody().getBrowserText().contains(templateValue1))	{
//							logger.info("���еĹ������ƣ�"+imp.getLocalName());
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
//		String[] SnowList =new String[50];//�����洢��ȡ�����ķ��������ģ���ѩ
//		
//		int SnowFromInfoNumber=0;//���ճ�ȡ�ķ��ϵ�ѩ������
//		int SnowFromTemNumber=0;
//		int SnowFromTopNumber=0;
//		String str="p4:";
//		String ieTopic = null;
//		ArrayList<String> topicList = new ArrayList<String>();
//		Collection mapToSnowValues = null;
//		OWLIndividual maIndividual = model.getOWLIndividual(maName);//��ȡmayaʵ��
//		
//		if(null == maIndividual){
//			System.out.println("mayaʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ����ڣ�");
//			return doc;
//		}
//		
//		//�õ��ĸ�������
//		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
//		OWLObjectProperty addSnowToMaProperty = model.getOWLObjectProperty(str + "addSnowToMa");
//		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//�˱��������жϳ���������������
//		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
//		
//		if(null == plane){
//			System.out.println("�޷���ȡʵ��" + maIndividual.getBrowserText() + "��hasValueOfPlace����(�����������жϳ����������ڻ�������)��so �ܾ���ѩ������֪ʶ���и������Ƿ���ڻ���ȷ��");
//			return doc;
//		}
//		else {
//			if(plane.getBrowserText().equals("InWaterDescription")||plane.getBrowserText().equals("inDoorDescription")) {
//				System.out.println("�˳������ʺϼ�ѩ��");
//				return doc;
//			}
//		}
//		
//		//����������Ϣ
//		System.out.println("=================���������⴦��================");
//		System.out.println("===============���ȴ���IE��ȡ������================");
//		
//		ieTopic = doc.getRootElement().element("maName").attributeValue("topic");
//		if(ieTopic.contains("Topic")){
//			System.out.println("IE Topic = " + ieTopic);
//			topicList.add(ieTopic);
//			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
//		} else {
//			System.out.println("=================IEδ�������================");
//			System.out.println("===============����ma��ȡ������================");
//			
//			Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
//			if(!topics.isEmpty()) {
//				OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//��topicsת��Ϊ����
//				
//				for(int i = 0; i < IdTopics.length; i++) {
//					topicList.add(IdTopics[i].getBrowserText());
//				}
//				
//				int topicSize = topicList.size();
//				if(topicSize>0) {
//					System.out.print("��ȡ�������������Ϊ��"+topicSize+"�����ǣ�");
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
//			System.out.println("���ݳ�ȡ�������⣬û�ж�Ӧ��ѩ");
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
//				System.out.print("���������ȡ��"+SnowFromInfoNumber + "��ѩ���ֱ��ǣ�");
//					
//				for(int i = 0; i < SnowFromInfoNumber; i++)
//					System.out.print(SnowList[i] + " ");
//					
//			System.out.println("\n���������ȣ���ģ�岻������");
//			}
//		}
//
//		System.out.println("================�������⴦�����================");
//		
//		//����ģ����Ϣ
//		if(SnowFromInfoNumber == 0) {
//			System.out.println("==================ģ�崦��====================");
//			int listSize = 0;
//			if(null != list)
//				listSize = list.size();//ģ�����
//			else
//				System.out.println("δ���ݽ����κ�ģ��");
//			
//			if(listSize > 0) {
//				System.out.println("���ݽ�����ģ�����Ϊ��" + listSize + "�����ֱ��ǣ�" + list);
//				executeTemplateToBackgroundSceneSWRLEngine(model,list);
//				
//				//ȥ��Ӧ�ĳ������ȡ����ӵ�ѩ
//				mapToSnowValues = null;
//				mapToSnowValues= maIndividual.getPropertyValues(addSnowToMaProperty);
//				if(mapToSnowValues.isEmpty())
//					System.out.println("���ݴ��ݽ�����ģ�壬û����Ӧ��ѩ��");
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
//						System.out.print("����ģ��鵽" + SnowFromTemNumber + "��ѩ���ֱ��ǣ�");
//						for(int i = 0; i < SnowFromTemNumber; i++)
//							System.out.print(SnowList[i] + " ");
//						System.out.println();
//					}
//				}
//			}
//			System.out.println("================ģ�崦����ϣ�================");
//		}
//		
//		if(0 == SnowFromInfoNumber) {
//			System.out.println("��������������û�г�ȡ���ʺϵ�ѩ");
//			return doc;
//		} 
//
//		//�ڻ�ȡ����ѩ�����ѡȡһ��
//		Random random = new Random();
//		int rd = random.nextInt(SnowFromInfoNumber);
//		
//		OWLIndividual SnowIndiviual = model.getOWLIndividual(SnowList[rd]);
//		System.out.println("���ѡȡ����ѩΪ��" + SnowIndiviual.getBrowserText());
//		
//		OWLDatatypeProperty Magnitude =  model.getOWLDatatypeProperty(str + "magnitude");
//		String SnowType = (String) SnowIndiviual.getPropertyValue(Magnitude);
//		System.out.println("SnowType = " + SnowType);
//
//		
//		Direction SnowDir;
//		Direction[] Dir = Direction.values();//dir�洢ö�ٵ�ֵ
//		int num = new Random().nextInt(8);//[0,8]֮�������
//		SnowDir = Dir[num];
//		String SnowDirection = SnowDir.toString();
//		
//		System.out.println("\nѩ�����ͣ�" + SnowType + "\nѩ�ķ���" + SnowDirection);
//		System.out.println("======================��Ϣ��ȡ��ϣ�=====================");
//		Document doc1 = printSnowRule(doc,SnowType,SnowDirection);
//		return doc1;
//	}
//	
//	public void SnowInfer2(ArrayList<String> list,OWLModel model,String maName,String ieTopic) throws OntologyLoadException, SWRLRuleEngineException
//	{
//		String[] SnowList =new String[50];//�����洢��ȡ�����ķ��������ģ���ѩ
//		String str="p4:";
//		int SnowFromInfoNumber=0;//���ճ�ȡ�ķ��ϵ�ѩ������
//		ArrayList<String> topicList = new ArrayList<String>();
//		Collection mapToSnowValues = null;
//		OWLIndividual maIndividual = model.getOWLIndividual(maName);//��ȡmayaʵ��
//		
//		if(null == maIndividual){
//			System.out.println("mayaʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ����ڣ�");
//			SnowFromInfoNumber=0;
//			return;
//		}
//		
//		//�õ��ĸ�������
//		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
//		OWLObjectProperty addSnowToMaProperty = model.getOWLObjectProperty(str + "addSnowToMa");
//		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//�˱��������жϳ���������������
//		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
//		
//		if(null == plane){
//			System.out.println("�޷���ȡʵ��" + maIndividual.getBrowserText() + "��hasValueOfPlace����(�����������жϳ����������ڻ�������)��so �ܾ���ѩ������֪ʶ���и������Ƿ���ڻ���ȷ��");
//			SnowFromInfoNumber=0;
//			return;
//		}else {
//			if(plane.getBrowserText().equals("InWaterDescription")||plane.getBrowserText().equals("inDoorDescription")) {
//				System.out.println("�˳������ʺϼ�ѩ��");
//				SnowFromInfoNumber=0;
//				return;
//			}
//		}
//		
//		//����������Ϣ
//		System.out.println("================���������⴦��================");
//		System.out.println("================���ȴ���IE����================");
//		
//		if(ieTopic.contains("Topic")){
//			System.out.println("IE Topic = " + ieTopic);
//			topicList.add(ieTopic);
//			executeTopicToBackgroundSceneSWRLEngine(model, topicList);
//		} else {
//			System.out.println("================IEδ�������================");
//			System.out.println("================����ma���������================");
//			Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
//			if(!topics.isEmpty()) {
//				OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//��topicsת��Ϊ����
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
//			System.out.println("���ݳ�ȡ�������⣬û�ж�Ӧ��ѩ");
//		}else {
//				
//			for(Iterator it1 = mapToSnowValues.iterator();it1.hasNext();) {
//				OWLIndividual SnowIndiviual = (OWLIndividual)it1.next();
//				SnowList[SnowFromInfoNumber] = SnowIndiviual.getBrowserText();
//				SnowFromInfoNumber++;
//			}
//		}
//		System.out.println("================�������⴦�����================");
//		
//		//����ģ����Ϣ
//		if(SnowFromInfoNumber == 0) {
//			System.out.println("==================ģ�崦��====================");
//			if(list.size() > 0) {
//				executeTemplateToBackgroundSceneSWRLEngine(model,list);
//				
//				//ȥ��Ӧ�ĳ������ȡ����ӵ�ѩ
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
//			System.out.println("================ģ�崦����ϣ�================");
//		}
//		System.out.println("SnowFormInfoNumber:" + SnowFromInfoNumber);
//		setSnow(SnowFromInfoNumber);	
//	}
//	
////	public Document SnowInfer(ArrayList<String> list,OWLModel model,String maName,Document doc) throws OntologyLoadException, SWRLRuleEngineException
////	{
////		String[] SnowList =new String[50];//�����洢��ȡ�����ķ��������ģ���ѩ
////		String str="p4:";
////		int SnowFromInfoNumber=0;//���ճ�ȡ�ķ��ϵ�ѩ������
////		int SnowFromTemNumber=0;
////		int SnowFromTopNumber=0;
////		
////		OWLIndividual maIndividual = model.getOWLIndividual(maName);//��ȡmayaʵ��
////		
////		if(null == maIndividual){
////			System.out.println("mayaʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ����ڣ�");
////			return doc;
////		}
////		
////		//�õ��ĸ�������
////		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
////		OWLObjectProperty addSnowToMaProperty = model.getOWLObjectProperty(str + "addSnowToMa");
////		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//�˱��������жϳ���������������
////		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
////		
////		if(null == plane){
////			System.out.println("�޷���ȡʵ��" + maIndividual.getBrowserText() + "��hasValueOfPlace����(�����������жϳ����������ڻ�������)��so �ܾ���ѩ������֪ʶ���и������Ƿ���ڻ���ȷ��");
////			return doc;
////		}
////		else {
////			if(plane.getBrowserText().equals("InWaterDescription")||plane.getBrowserText().equals("inDoorDescription")) {
////				System.out.println("�˳������ʺϼ�ѩ��");
////				return doc;
////			}
////		}
////		
////		//����������Ϣ
////		System.out.println("================���������⴦��================");
////		Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
////		
////		if(!topics.isEmpty()) {
////			OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//��topicsת��Ϊ����
////			ArrayList<String> topicList = new ArrayList<String>();
////			
////			for(int i = 0; i < IdTopics.length; i++) {
////				topicList.add(IdTopics[i].getBrowserText());
////			}
////			
////			int topicSize = topicList.size();
////			if(topicSize>0) {
////				System.out.print("��ȡ�������������Ϊ��"+topicSize+"�����ǣ�");
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
////				System.out.println("���ݳ�ȡ�������⣬û�ж�Ӧ��ѩ");
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
////					System.out.print("���������ȡ��"+SnowFromInfoNumber + "��ѩ���ֱ��ǣ�");
////					
////					for(int i = 0; i < SnowFromInfoNumber; i++)
////						System.out.print(SnowList[i] + " ");
////					
////					System.out.println("\n���������ȣ���ģ�岻������");
////				}
////			}
////		} else 
////			System.out.println("��" + maName + "����δ��ȡ�����⣡");
////		System.out.println("================�������⴦�����================");
////		
////		//����ģ����Ϣ
////		if(SnowFromInfoNumber == 0) {
////			System.out.println("==================ģ�崦��====================");
////			int listSize = 0;
////			if(null != list)
////				listSize = list.size();//ģ�����
////			else
////				System.out.println("δ���ݽ����κ�ģ��");
////			
////			if(listSize > 0) {
////				System.out.println("���ݽ�����ģ�����Ϊ��" + listSize + "�����ֱ��ǣ�" + list);
////				executeTemplateToBackgroundSceneSWRLEngine(model,list);
////				
////				//ȥ��Ӧ�ĳ������ȡ����ӵ�ѩ
////				Collection mapToSnowValues= maIndividual.getPropertyValues(addSnowToMaProperty);
////				if(mapToSnowValues.isEmpty())
////					System.out.println("���ݴ��ݽ�����ģ�壬û����Ӧ��ѩ��");
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
////						System.out.print("����ģ��鵽" + SnowFromTemNumber + "��ѩ���ֱ��ǣ�");
////						for(int i = 0; i < SnowFromTemNumber; i++)
////							System.out.print(SnowList[i] + " ");
////						System.out.println();
////					}
////				}
////			}
////			System.out.println("================ģ�崦����ϣ�================");
////		}
////		
////		if(0 == SnowFromInfoNumber) {
////			System.out.println("��������������û�г�ȡ���ʺϵ�ѩ");
////			return doc;
////		} 
////
////		//�ڻ�ȡ����ѩ�����ѡȡһ��
////		java.util.Random random = new Random();
////		int rd = random.nextInt(SnowFromInfoNumber);
////		
////		OWLIndividual SnowIndiviual = model.getOWLIndividual(SnowList[rd]);
////		System.out.print("���ѡȡ����ѩΪ��" + SnowIndiviual.getBrowserText());
////		
////		int strLength = SnowIndiviual.getBrowserText().length();
////		String SnowType = SnowIndiviual.getBrowserText().substring(3,strLength-5);
////		
////		Direction SnowDir;
////		Direction[] Dir = Direction.values();//dir�洢ö�ٵ�ֵ
////		int num = new Random().nextInt(8);//[0,8]֮�������
////		SnowDir = Dir[num];
////		String SnowDirection = SnowDir.toString();
////		
////		System.out.println("\nѩ�����ͣ�" + SnowType + "\nѩ�ķ���" + SnowDirection);
////		System.out.println("======================��Ϣ��ȡ��ϣ�=====================");
////		Document doc1 = printSnowRule(doc,SnowType,SnowDirection);
////		return doc1;
////	}
////	
////	public void SnowInfer2(ArrayList<String> list,OWLModel model,String maName) throws OntologyLoadException, SWRLRuleEngineException
////	{
////		String[] SnowList =new String[50];//�����洢��ȡ�����ķ��������ģ���ѩ
////		String str="p4:";
////		int SnowFromInfoNumber=0;//���ճ�ȡ�ķ��ϵ�ѩ������
////		
////		OWLIndividual maIndividual = model.getOWLIndividual(maName);//��ȡmayaʵ��
////		
////		if(null == maIndividual){
////			System.out.println("mayaʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ����ڣ�");
////			SnowFromInfoNumber=0;
////			return;
////		}
////		
////		//�õ��ĸ�������
////		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
////		OWLObjectProperty addSnowToMaProperty = model.getOWLObjectProperty(str + "addSnowToMa");
////		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//�˱��������жϳ���������������
////		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
////		
////		if(null == plane){
////			System.out.println("�޷���ȡʵ��" + maIndividual.getBrowserText() + "��hasValueOfPlace����(�����������жϳ����������ڻ�������)��so �ܾ���ѩ������֪ʶ���и������Ƿ���ڻ���ȷ��");
////			SnowFromInfoNumber=0;
////			return;
////		}else {
////			if(plane.getBrowserText().equals("InWaterDescription")||plane.getBrowserText().equals("inDoorDescription")) {
////				System.out.println("�˳������ʺϼ�ѩ��");
////				SnowFromInfoNumber=0;
////				return;
////			}
////		}
////		
////		//����������Ϣ
////		System.out.println("================���������⴦��================");
////		Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
////		
////		if(!topics.isEmpty()) {
////			OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//��topicsת��Ϊ����
////			ArrayList<String> topicList = new ArrayList<String>();
////			
////			for(int i = 0; i < IdTopics.length; i++) {
////				topicList.add(IdTopics[i].getBrowserText());
////			}
////			
////			int topicSize = topicList.size();
////			if(topicSize>0) {
////				System.out.print("��ȡ�������������Ϊ��"+topicSize+"�����ǣ�");
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
////				System.out.println("���ݳ�ȡ�������⣬û�ж�Ӧ��ѩ");}
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
////		System.out.println("================�������⴦�����================");
////		
////		//����ģ����Ϣ
////		if(SnowFromInfoNumber == 0) {
////			System.out.println("==================ģ�崦��====================");
////			if(list.size() > 0) {
////				executeTemplateToBackgroundSceneSWRLEngine(model,list);
////				
////				//ȥ��Ӧ�ĳ������ȡ����ӵ�ѩ
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
////			System.out.println("================ģ�崦����ϣ�================");
////		}
////		System.out.println("SnowFormInfoNumber:" + SnowFromInfoNumber);
////		setSnow(SnowFromInfoNumber);
////		
////	}
//	
//	public Document printSnowRule(Document doc,String SnowType,String SnowDirection)
//	{
//		System.out.println("=====================��ʼ����xml-rule======================");
//		Element rootName = (Element) doc.getRootElement();
//		Element name = rootName.element("maName");
//		Element ruleName = name.addElement("rule");
//		ruleName.addAttribute("ruleType", "addEffectToMa");
//		ruleName.addAttribute("type","Snow");
//		ruleName.addAttribute("Magnitude", SnowType);
//		ruleName.addAttribute("Direction", SnowDirection);
//		System.out.println("xml-rule�������");
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
//		System.out.println("��ʼ!");
//		Document document1 = Snow.SnowInfer(aList, model, "schoolroomOut.ma",document);
//		String maName = null;
//	//	Document document1 = Snow.SnowInfer(aList, model, maName,document);
//	//	Document document1 = Snow.SnowInfer(aList, model, "Tropical45.ma",document);
//		Snow.SnowInfer2(aList, model, "Tropical45.ma","");
//		XMLWriter writer = new XMLWriter(new FileWriter("f:\\testSnow.xml"));
//		writer.write(document1); 
//		System.out.println("������");
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
////							logger.info("���еĹ�������Ϊ��"+imp.getLocalName());
////							imp.enable();//ִ�д˹���
////						}
////					}
////					
////				}
////			}
////		}
////		ruleEngine.infer();
////	}
////	
////	//ִ��OWL��Ĺ���
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
////				if(imp.getLocalName().contains("addSnowToMaRule")) {					//�ҵ����ְ�����addSnowToSceneRule���Ĺ���
////					for(Iterator<String> its=templateName.iterator();its.hasNext();) {
////						
////						String templateValue= its.next();
////						String templateValue1=templateValue.substring(0,templateValue.indexOf(":"));
////						
////						if(imp.getBody().getBrowserText().contains(templateValue1))	{
////							logger.info("���еĹ������ƣ�"+imp.getLocalName());
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
////		String[] SnowList =new String[50];//�����洢��ȡ�����ķ��������ģ���ѩ
////		String str="p4:";
////		int SnowFromInfoNumber=0;//���ճ�ȡ�ķ��ϵ�ѩ������
////		int SnowFromTemNumber=0;
////		int SnowFromTopNumber=0;
////		
////		OWLIndividual maIndividual = model.getOWLIndividual(maName);//��ȡmayaʵ��
////		
////		if(null == maIndividual){
////			System.out.println("mayaʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ����ڣ�");
////			return doc;
////		}
////		
////		//�õ��ĸ�������
////		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
////		OWLObjectProperty addSnowToMaProperty = model.getOWLObjectProperty(str + "addSnowToMa");
////		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//�˱��������жϳ���������������
////		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
////		
////		if(null == plane)
////			return doc;
////		else {
////			if(plane.getBrowserText().equals("InWaterDescription")||plane.getBrowserText().equals("inDoorDescription")) {
////				System.out.println("�˳������ʺϼ�ѩ��");
////				return doc;
////			}
////		}
////		
////		//����������Ϣ
////		System.out.println("================���������⴦��================");
////		Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
////		
////		if(!topics.isEmpty()) {
////			OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//��topicsת��Ϊ����
////			ArrayList<String> topicList = new ArrayList<String>();
////			
////			for(int i = 0; i < IdTopics.length; i++) {
////				topicList.add(IdTopics[i].getBrowserText());
////			}
////			
////			int topicSize = topicList.size();
////			if(topicSize>0) {
////				System.out.print("��ȡ�������������Ϊ��"+topicSize+"�����ǣ�");
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
////				System.out.println("���ݳ�ȡ�������⣬û�ж�Ӧ��ѩ");
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
////					System.out.print("���������ȡ��"+SnowFromInfoNumber + "��ѩ���ֱ��ǣ�");
////					
////					for(int i = 0; i < SnowFromInfoNumber; i++)
////						System.out.print(SnowList[i] + " ");
////					
////					System.out.println("\n���������ȣ���ģ�岻������");
////				}
////			}
////		} else 
////			System.out.println("��" + maName + "����δ��ȡ�����⣡");
////		System.out.println("================�������⴦�����================");
////		
////		//����ģ����Ϣ
////		if(SnowFromTopNumber == 0) {
////			System.out.println("==================ģ�崦��====================");
////			int listSize = 0;
////			if(null != list)
////				listSize = list.size();//ģ�����
////			else
////				System.out.println("δ���ݽ����κ�ģ��");
////			
////			if(listSize > 0) {
////				System.out.println("���ݽ�����ģ�����Ϊ��" + listSize + "�����ֱ��ǣ�" + list);
////				executeTemplateToBackgroundSceneSWRLEngine(model,list);
////				
////				//ȥ��Ӧ�ĳ������ȡ����ӵ�ѩ
////				Collection mapToSnowValues= maIndividual.getPropertyValues(addSnowToMaProperty);
////				if(mapToSnowValues.isEmpty())
////					System.out.println("���ݴ��ݽ�����ģ�壬û����Ӧ��ѩ��");
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
////						System.out.print("����ģ��鵽" + SnowFromTemNumber + "��ѩ���ֱ��ǣ�");
////						for(int i = 0; i < SnowFromTemNumber; i++)
////							System.out.print(SnowList[i] + " ");
////						System.out.println();
////					}
////				}
////			}
////			System.out.println("================ģ�崦����ϣ�================");
////		}
////		
////		if(0 == SnowFromInfoNumber) {
////			System.out.println("��������������û�г�ȡ���ʺϵ�ѩ");
////			return doc;
////		} else 
////			setSnow(SnowFromInfoNumber);
////		
////		//�ڻ�ȡ����ѩ�����ѡȡһ��
////		java.util.Random random = new Random();
////		int rd = random.nextInt(SnowFromInfoNumber);
////		
////		OWLIndividual SnowIndiviual = model.getOWLIndividual(SnowList[rd]);
////		System.out.print("���ѡȡ����ѩΪ��" + SnowIndiviual.getBrowserText());
////		
////		int strLength = SnowIndiviual.getBrowserText().length();
////		String SnowType = SnowIndiviual.getBrowserText().substring(3,strLength-5);
////		
////		Direction SnowDir;
////		Direction[] Dir = Direction.values();//dir�洢ö�ٵ�ֵ
////		int num = new Random().nextInt(8);//[0,8]֮�������
////		SnowDir = Dir[num];
////		String SnowDirection = SnowDir.toString();
////		
////		System.out.println("\nѩ�����ͣ�" + SnowType + "\nѩ�ķ���" + SnowDirection);
////		System.out.println("======================��Ϣ��ȡ��ϣ�=====================");
////		Document doc1 = printSnowRule(doc,SnowType,SnowDirection);
////		return doc1;
////	}
////	
////	public void SnowInfer2(ArrayList<String> list,OWLModel model,String maName) throws OntologyLoadException, SWRLRuleEngineException
////	{
////		String[] SnowList =new String[50];//�����洢��ȡ�����ķ��������ģ���ѩ
////		String str="p4:";
////		int SnowFromInfoNumber=0;//���ճ�ȡ�ķ��ϵ�ѩ������
////		int SnowFromTemNumber=0;
////		int SnowFromTopNumber=0;
////		
////		OWLIndividual maIndividual = model.getOWLIndividual(maName);//��ȡmayaʵ��
////		
////		if(null == maIndividual){
////			System.out.println("mayaʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ����ڣ�");
////		//	return doc;
////			SnowFromInfoNumber=0;
////		}
////		
////		//�õ��ĸ�������
////		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
////		OWLObjectProperty addSnowToMaProperty = model.getOWLObjectProperty(str + "addSnowToMa");
////		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//�˱��������жϳ���������������
////		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
////		
////		if(null == plane){
////			//return doc;
////			SnowFromInfoNumber=0;
////		}else {
////			if(plane.getBrowserText().equals("InWaterDescription")||plane.getBrowserText().equals("inDoorDescription")) {
////				System.out.println("�˳������ʺϼ�ѩ��");
////				//return doc;
////				SnowFromInfoNumber=0;
////			}
////		}
////		
////		//����������Ϣ
////		System.out.println("================���������⴦��================");
////		Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
////		
////		if(!topics.isEmpty()) {
////			OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//��topicsת��Ϊ����
////			ArrayList<String> topicList = new ArrayList<String>();
////			
////			for(int i = 0; i < IdTopics.length; i++) {
////				topicList.add(IdTopics[i].getBrowserText());
////			}
////			
////			int topicSize = topicList.size();
////			if(topicSize>0) {
////				System.out.print("��ȡ�������������Ϊ��"+topicSize+"�����ǣ�");
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
////				System.out.println("���ݳ�ȡ�������⣬û�ж�Ӧ��ѩ");}
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
////		System.out.println("================�������⴦�����================");
////		
////		//����ģ����Ϣ
////		if(SnowFromTopNumber == 0) {
////			System.out.println("==================ģ�崦��====================");
////			if(list.size() > 0) {
////				executeTemplateToBackgroundSceneSWRLEngine(model,list);
////				
////				//ȥ��Ӧ�ĳ������ȡ����ӵ�ѩ
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
////			System.out.println("================ģ�崦����ϣ�================");
////		}
////			setSnow(SnowFromInfoNumber);
////		
////	}
////	
////	public Document printSnowRule(Document doc,String SnowType,String SnowDirection)
////	{
////		System.out.println("=====================��ʼ����xml-rule======================");
////		Element rootName = (Element) doc.getRootElement();
////		Element name = rootName.element("maName");
////		Element ruleName = name.addElement("rule");
////		ruleName.addAttribute("ruleType", "addEffectToMa");
////		ruleName.addAttribute("type","Snow");
////		ruleName.addAttribute("Magnitude", SnowType);
////		ruleName.addAttribute("Direction", SnowDirection);
////		System.out.println("xml-rule�������");
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
////		System.out.println("��ʼ!");
////	//	Document document1 = Snow.SnowInfer(aList, model, "schoolroomOut.ma",document);
////		String maName = null;
////		Document document1 = Snow.SnowInfer(aList, model, maName,document);
////		XMLWriter writer = new XMLWriter(new FileWriter("f:\\testSnow.xml"));
////		writer.write(document1); 
////		System.out.println("������");
////		System.out.println("snow number:" + Snow.getSnow());
////		writer.close();
////	}
