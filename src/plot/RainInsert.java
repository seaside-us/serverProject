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
			
			if(templateName.size()!=0) {
				if(imp.getLocalName().contains("addRainToMaRule")) {//�ҵ����ְ�����addRainToSceneRule���Ĺ���
					for(Iterator<String> its=templateName.iterator();its.hasNext();) {
						
						String templateValue= its.next();
						String templateValue1=templateValue.substring(0,templateValue.indexOf(":"));
						
						if(imp.getBody().getBrowserText().contains(templateValue1)) {
							logger.info("���еĹ������ƣ�"+imp.getLocalName());
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
		String[] RainList =new String[50];//�����洢��ȡ�����ķ��������ģ�����
		int RainFromInfoNumber=0;//���ճ�ȡ�ķ��ϵ��������
		int RainFromTemNumber=0;
		int RainFromTopNumber=0;
		String str = "p4:";
		String ieTopic;
		ArrayList<String> topicList = new ArrayList<String>();
		ArrayList<String> list = new ArrayList<String>();
		Collection mapToRainValues = null;
		
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
		OWLIndividual maIndividual = model.getOWLIndividual(maName);//��ȡmayaʵ��	
		if(null == maIndividual){
			System.out.println("mayaʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ����ڣ�");
			return doc;
		}
		
		//�õ��ĸ�������
		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
		OWLObjectProperty addRainToMaProperty = model.getOWLObjectProperty(str + "addRainToMa");
		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//�˱��������жϳ���������������

		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
		if(null == plane){
			System.out.println("�޷���ȡʵ��" + maIndividual.getBrowserText() + "��hasValueOfPlace����(�����������жϳ����������ڻ�������)��so �ܾ����꣡����֪ʶ���и������Ƿ���ڻ���ȷ��");
			return doc;
		}
		else {
			if(plane.getBrowserText().equals("InWaterDescription") || plane.getBrowserText().equals("inDoorDescription")) {
				System.out.println("�˳������ʺϼ��꣡");
				return doc;
			}
		}
		
		//����������Ϣ
		System.out.println("================���������⴦��================");
		System.out.println("================���ȴ���IE����================");
		ieTopic = doc.getRootElement().element("maName").attributeValue("topic");
		
		if(ieTopic.contains("Topic")){
			System.out.println("IE Topic = " + ieTopic);
			topicList.add(ieTopic);
			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
		} else {
			System.out.println("================IEδ��ȡ������================");
			System.out.println("================����ma��ȡ������================");
			Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);		
			if(!topics.isEmpty()){
				OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//��topicsת��Ϊ����
				
				for(int i = 0; i < IdTopics.length; i++) {
					topicList.add(IdTopics[i].getBrowserText());
				}
				
				int topicSize = topicList.size();
				if(topicSize>0) {
					System.out.print("��ȡ�������������Ϊ��" + topicSize + "�����ǣ�");
					
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
			System.out.println("���ݳ�ȡ�������⣬û�ж�Ӧ����");
		} else {
			
			for(Iterator it1 = mapToRainValues.iterator(); it1.hasNext();) {
				OWLIndividual RainIndiviual = (OWLIndividual)it1.next();
				RainList[RainFromInfoNumber] = RainIndiviual.getBrowserText();
				RainFromInfoNumber++;
				RainFromTopNumber++;
			}
				
			if(0 != RainFromTopNumber) {
				System.out.print("���������ȡ��"+RainFromTopNumber+"���꣬�ֱ��ǣ�");
					
				for(int i = 0; i < RainFromTopNumber; i++)
					System.out.print(RainList[i] + " ");
				
				System.out.println();
				System.out.println("���������ȣ���ģ�岻������");
			}
		}
				

		System.out.println("================�������⴦�����================");
		
		//����ģ����Ϣ
		if(0 == RainFromTopNumber)
		{
			System.out.println("==================ģ�崦��====================");
			int listSize = 0;
			if(null != list)
				listSize = list.size();//ģ�����
			else
				System.out.println("δ���ݽ����κ�ģ��");
			
			if(listSize > 0) {
				System.out.println("���ݽ�����ģ�����Ϊ��" + listSize + "�����ֱ��ǣ�" + list);
				executeTemplateToBackgroundSceneSWRLEngine(model,list);
				
				//ȥ��Ӧ�ĳ������ȡ����ӵ���
				mapToRainValues = null;
				mapToRainValues= maIndividual.getPropertyValues(addRainToMaProperty);
				if(mapToRainValues.isEmpty()){
					System.out.println("���ݴ��ݽ�����ģ�壬û����Ӧ���꣡");
				} else {
					
					for(Iterator it1=mapToRainValues.iterator();it1.hasNext();) {
						OWLIndividual RainIndiviual =(OWLIndividual)it1.next();
						RainList[RainFromInfoNumber]=RainIndiviual.getBrowserText();
						RainFromInfoNumber++;
						RainFromTemNumber++;
					}
					
					if(0 != RainFromTemNumber) {
						System.out.print("����ģ��鵽"+RainFromTemNumber+"���꣬�ֱ��ǣ�");
						
						for(int i = 0; i < RainFromTemNumber; i++)
							System.out.print(RainList[i] + " ");
						
						System.out.println();
					}
				}
			}
			System.out.println("================ģ�崦����ϣ�================");
		}
		
		if(0 == RainFromInfoNumber) {
			System.out.println("��������������û�г�ȡ���ʺϵ���");
			return doc;
		} 
		
		//�ڻ�ȡ�����������ѡȡһ��
		java.util.Random random = new Random();
		int rd = random.nextInt(RainFromInfoNumber);
		
		OWLIndividual RainIndiviual = model.getOWLIndividual(RainList[rd]);
		System.out.println("���ѡȡ������Ϊ��" + RainIndiviual.getBrowserText());
		
		OWLDatatypeProperty Magnitude =  model.getOWLDatatypeProperty(str + "magnitude");
		String RainType = (String) RainIndiviual.getPropertyValue(Magnitude);
		System.out.println("RainType = " + RainType);
		
		Direction RainDir;
		Direction[] Dir = Direction.values();//dir�洢ö�ٵ�ֵ
		int num = new Random().nextInt(8);//[0,8]֮�������
		RainDir = Dir[num];
		String RainDirection = RainDir.toString();
		
		System.out.println("\n������ͣ�" + RainType + "\n��ķ���" + RainDirection);
		System.out.println("======================��Ϣ��ȡ��ϣ�=====================");
		Document doc1 = printRainRule(doc,RainType,RainDirection);
		return doc1;
	}
	
	public  void RainInfer2(ArrayList<String> List,OWLModel model,String maName,String ieTopic) throws OntologyLoadException, SWRLRuleEngineException
	{
		String[] RainList =new String[50];//�����洢��ȡ�����ķ��������ģ�����
		int RainFromInfoNumber=0;//���ճ�ȡ�ķ��ϵ��������
		String str = "p4:";
		ArrayList<String> topicList = new ArrayList<String>();
		ArrayList<String> list = new ArrayList<String>();
		Collection mapToRainValues = null;
		
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
		OWLIndividual maIndividual = model.getOWLIndividual(maName);//��ȡmayaʵ��
		
		if(null == maIndividual){
			System.out.println("maya ʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ�����");
			RainFromInfoNumber=0;
			return;
		}
		
		//�õ��ĸ�������
		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
		OWLObjectProperty addRainToMaProperty = model.getOWLObjectProperty(str + "addRainToMa");
		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//�˱��������жϳ���������������

		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
		if(null == plane){
			System.out.println("�޷���ȡʵ��" + maIndividual.getBrowserText() + "��hasValueOfPlace����(�����������жϳ����������ڻ�������)��so �ܾ����꣡����֪ʶ���и������Ƿ���ڻ���ȷ��");
			return;
		}else {
			if(plane.getBrowserText().equals("InWaterDescription") || plane.getBrowserText().equals("inDoorDescription")) {
				System.out.println("�˳������ʺϼ��꣡");
				return;
			}
		}
		
		//����������Ϣ
		System.out.println("================���������⴦��================");
		System.out.println("================���ȴ���IE����================");
		if(ieTopic.contains("Topic")){
			System.out.println("IE Topic = " + ieTopic);
			topicList.add(ieTopic);
			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
		} else {
			System.out.println("================IEδ��ȡ������================");
			System.out.println("================����ma��ȡ������================");
			Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
		
			if(!topics.isEmpty()){
				OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//��topicsת��Ϊ����
				
				
				for(int i = 0; i < IdTopics.length; i++) {
					topicList.add(IdTopics[i].getBrowserText());
				}
			}
			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
		}
			
		mapToRainValues = maIndividual.getPropertyValues(addRainToMaProperty);
			
		if(mapToRainValues.isEmpty()){
			System.out.println("���ݳ�ȡ�������⣬û�ж�Ӧ�ķ�");
			RainFromInfoNumber=0;
		} else {
				
			for(Iterator it1 = mapToRainValues.iterator(); it1.hasNext();) {
				OWLIndividual RainIndiviual = (OWLIndividual)it1.next();
				RainList[RainFromInfoNumber] = RainIndiviual.getBrowserText();
				RainFromInfoNumber++;
			}
		} 
		
		System.out.println("================�������⴦�����================");
		
		//����ģ����Ϣ
		if(0 == RainFromInfoNumber)
		{
			System.out.println("==================ģ�崦��====================");
			
			if(list.size() > 0) {
				executeTemplateToBackgroundSceneSWRLEngine(model,list);
				
				//ȥ��Ӧ�ĳ������ȡ����ӵ���
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
			System.out.println("================ģ�崦����ϣ�================");
		}
		
		System.out.println("RainFromInfoNum = "+RainFromInfoNumber);
		setRain(RainFromInfoNumber);	
	}
	
	public Document printRainRule(Document doc,String RainType,String RainDirection)
	{
		System.out.println("=====================��ʼ����xml-rule======================");
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "addEffectToMa");
		ruleName.addAttribute("type","Rain");
		ruleName.addAttribute("Magnitude", RainType);
		ruleName.addAttribute("Direction", RainDirection);
		System.out.println("xml-rule�������");
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
		
		System.out.println("��ʼ!");
		RainInsert rain = new RainInsert();
//		RainInsert rain1 = new RainInsert();
	//	rain1.RainInfer2(alist, model, "kindergarten.ma","ShoppingActionTopic");
		Document document1 = rain.RainInfer(alist, model, "kindergarten.ma",document);
	//	Document document1 = rain.RainInfer(alist, model, "Tropical45.ma",document);
	//	rain.RainInfer2(alist, model, "Tropical45.ma");

		XMLWriter writer = new XMLWriter(new FileWriter("f:\\testRain.xml"));
		writer.write(document1); 
		System.out.println("������");
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
//		while(iter.hasNext()) {
//			SWRLImp imp=(SWRLImp) iter.next();
//			
//			if(templateName.size()!=0) {
//				if(imp.getLocalName().contains("addRainToMaRule")) {//�ҵ����ְ�����addRainToSceneRule���Ĺ���
//					for(Iterator<String> its=templateName.iterator();its.hasNext();) {
//						
//						String templateValue= its.next();
//						String templateValue1=templateValue.substring(0,templateValue.indexOf(":"));
//						
//						if(imp.getBody().getBrowserText().contains(templateValue1)) {
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
//	public  Document RainInfer(ArrayList<String> list,OWLModel model,String maName,Document doc) throws OntologyLoadException, SWRLRuleEngineException
//	{
//		String[] RainList =new String[50];//�����洢��ȡ�����ķ��������ģ�����
//		int RainFromInfoNumber=0;//���ճ�ȡ�ķ��ϵ��������
//		int RainFromTemNumber=0;
//		int RainFromTopNumber=0;
//		String str = "p4:";
//		String ieTopic;
//		ArrayList<String> topicList = new ArrayList<String>();
//		Collection mapToRainValues = null;
//		
//		OWLIndividual maIndividual = model.getOWLIndividual(maName);//��ȡmayaʵ��	
//		if(null == maIndividual){
//			System.out.println("mayaʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ����ڣ�");
//			return doc;
//		}
//		
//		//�õ��ĸ�������
//		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
//		OWLObjectProperty addRainToMaProperty = model.getOWLObjectProperty(str + "addRainToMa");
//		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//�˱��������жϳ���������������
//
//		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
//		if(null == plane){
//			System.out.println("�޷���ȡʵ��" + maIndividual.getBrowserText() + "��hasValueOfPlace����(�����������жϳ����������ڻ�������)��so �ܾ����꣡����֪ʶ���и������Ƿ���ڻ���ȷ��");
//			return doc;
//		}
//		else {
//			if(plane.getBrowserText().equals("InWaterDescription") || plane.getBrowserText().equals("inDoorDescription")) {
//				System.out.println("�˳������ʺϼ��꣡");
//				return doc;
//			}
//		}
//		
//		//����������Ϣ
//		System.out.println("================���������⴦��================");
//		System.out.println("================���ȴ���IE����================");
//		ieTopic = doc.getRootElement().element("maName").attributeValue("topic");
//		
//		if(ieTopic.contains("Topic")){
//			System.out.println("IE Topic = " + ieTopic);
//			topicList.add(ieTopic);
//			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
//		} else {
//			System.out.println("================IEδ��ȡ������================");
//			System.out.println("================����ma��ȡ������================");
//			Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);		
//			if(!topics.isEmpty()){
//				OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//��topicsת��Ϊ����
//				
//				for(int i = 0; i < IdTopics.length; i++) {
//					topicList.add(IdTopics[i].getBrowserText());
//				}
//				
//				int topicSize = topicList.size();
//				if(topicSize>0) {
//					System.out.print("��ȡ�������������Ϊ��" + topicSize + "�����ǣ�");
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
//			System.out.println("���ݳ�ȡ�������⣬û�ж�Ӧ����");
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
//				System.out.print("���������ȡ��"+RainFromTopNumber+"���꣬�ֱ��ǣ�");
//					
//				for(int i = 0; i < RainFromTopNumber; i++)
//					System.out.print(RainList[i] + " ");
//				
//				System.out.println();
//				System.out.println("���������ȣ���ģ�岻������");
//			}
//		}
//				
//
//		System.out.println("================�������⴦�����================");
//		
//		//����ģ����Ϣ
//		if(0 == RainFromTopNumber)
//		{
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
//				//ȥ��Ӧ�ĳ������ȡ����ӵ���
//				mapToRainValues = null;
//				mapToRainValues= maIndividual.getPropertyValues(addRainToMaProperty);
//				if(mapToRainValues.isEmpty()){
//					System.out.println("���ݴ��ݽ�����ģ�壬û����Ӧ���꣡");
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
//						System.out.print("����ģ��鵽"+RainFromTemNumber+"���꣬�ֱ��ǣ�");
//						
//						for(int i = 0; i < RainFromTemNumber; i++)
//							System.out.print(RainList[i] + " ");
//						
//						System.out.println();
//					}
//				}
//			}
//			System.out.println("================ģ�崦����ϣ�================");
//		}
//		
//		if(0 == RainFromInfoNumber) {
//			System.out.println("��������������û�г�ȡ���ʺϵ���");
//			return doc;
//		} 
//		
//		//�ڻ�ȡ�����������ѡȡһ��
//		java.util.Random random = new Random();
//		int rd = random.nextInt(RainFromInfoNumber);
//		
//		OWLIndividual RainIndiviual = model.getOWLIndividual(RainList[rd]);
//		System.out.println("���ѡȡ������Ϊ��" + RainIndiviual.getBrowserText());
//		
//		OWLDatatypeProperty Magnitude =  model.getOWLDatatypeProperty(str + "magnitude");
//		String RainType= (String) RainIndiviual.getPropertyValue(Magnitude);
//		System.out.println("RainType = " + RainType);
//		
//		
//		Direction RainDir;
//		Direction[] Dir = Direction.values();//dir�洢ö�ٵ�ֵ
//		int num = new Random().nextInt(8);//[0,8]֮�������
//		RainDir = Dir[num];
//		String RainDirection = RainDir.toString();
//		
//		System.out.println("\n������ͣ�" + RainType + "\n��ķ���" + RainDirection);
//		System.out.println("======================��Ϣ��ȡ��ϣ�=====================");
//		Document doc1 = printRainRule(doc,RainType,RainDirection);
//		return doc1;
//	}
//	
//	public  void RainInfer2(ArrayList<String> list,OWLModel model,String maName,String ieTopic) throws OntologyLoadException, SWRLRuleEngineException
//	{
//		String[] RainList =new String[50];//�����洢��ȡ�����ķ��������ģ�����
//		int RainFromInfoNumber=0;//���ճ�ȡ�ķ��ϵ��������
//		String str = "p4:";
//		ArrayList<String> topicList = new ArrayList<String>();
//		Collection mapToRainValues = null;
//		
//		OWLIndividual maIndividual = model.getOWLIndividual(maName);//��ȡmayaʵ��
//		
//		if(null == maIndividual){
//			System.out.println("maya ʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ�����");
//			RainFromInfoNumber=0;
//			return;
//		}
//		
//		//�õ��ĸ�������
//		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
//		OWLObjectProperty addRainToMaProperty = model.getOWLObjectProperty(str + "addRainToMa");
//		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//�˱��������жϳ���������������
//
//		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
//		if(null == plane){
//			System.out.println("�޷���ȡʵ��" + maIndividual.getBrowserText() + "��hasValueOfPlace����(�����������жϳ����������ڻ�������)��so �ܾ����꣡����֪ʶ���и������Ƿ���ڻ���ȷ��");
//			return;
//		}else {
//			if(plane.getBrowserText().equals("InWaterDescription") || plane.getBrowserText().equals("inDoorDescription")) {
//				System.out.println("�˳������ʺϼ��꣡");
//				return;
//			}
//		}
//		
//		//����������Ϣ
//		System.out.println("================���������⴦��================");
//		System.out.println("================���ȴ���IE����================");
//		if(ieTopic.contains("Topic")){
//			System.out.println("IE Topic = " + ieTopic);
//			topicList.add(ieTopic);
//			executeTopicToBackgroundSceneSWRLEngine(model,topicList);
//		} else {
//			System.out.println("================IEδ��ȡ������================");
//			System.out.println("================����ma��ȡ������================");
//			Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
//		
//			if(!topics.isEmpty()){
//				OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//��topicsת��Ϊ����
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
//			System.out.println("���ݳ�ȡ�������⣬û�ж�Ӧ�ķ�");
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
//		System.out.println("================�������⴦�����================");
//		
//		//����ģ����Ϣ
//		if(0 == RainFromInfoNumber)
//		{
//			System.out.println("==================ģ�崦��====================");
//			
//			if(list.size() > 0) {
//				executeTemplateToBackgroundSceneSWRLEngine(model,list);
//				
//				//ȥ��Ӧ�ĳ������ȡ����ӵ���
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
//			System.out.println("================ģ�崦����ϣ�================");
//		}
//		
//		System.out.println("RainFromInfoNum = "+RainFromInfoNumber);
//		setRain(RainFromInfoNumber);	
//	}
////	public  Document RainInfer(ArrayList<String> list,OWLModel model,String maName,Document doc) throws OntologyLoadException, SWRLRuleEngineException
////	{
////		String[] RainList =new String[50];//�����洢��ȡ�����ķ��������ģ�����
////		int RainFromInfoNumber=0;//���ճ�ȡ�ķ��ϵ��������
////		int RainFromTemNumber=0;
////		int RainFromTopNumber=0;
////		String str = "p4:";
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
////		OWLObjectProperty addRainToMaProperty = model.getOWLObjectProperty(str + "addRainToMa");
////		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//�˱��������жϳ���������������
////
////		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
////		if(null == plane){
////			System.out.println("�޷���ȡʵ��" + maIndividual.getBrowserText() + "��hasValueOfPlace����(�����������жϳ����������ڻ�������)��so �ܾ����꣡����֪ʶ���и������Ƿ���ڻ���ȷ��");
////			return doc;
////		}
////		else {
////			if(plane.getBrowserText().equals("InWaterDescription") || plane.getBrowserText().equals("inDoorDescription")) {
////				System.out.println("�˳������ʺϼ��꣡");
////				return doc;
////			}
////		}
////		
////		//����������Ϣ
////		System.out.println("================���������⴦��================");
////		Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
////		
////		if(!topics.isEmpty())
////		{
////			OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//��topicsת��Ϊ����
////			ArrayList topicList = new ArrayList();
////			
////			for(int i = 0; i < IdTopics.length; i++) {
////				topicList.add(IdTopics[i].getBrowserText());
////			}
////			
////			int topicSize = topicList.size();
////			if(topicSize>0) {
////				System.out.print("��ȡ�������������Ϊ��" + topicSize + "�����ǣ�");
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
////				System.out.println("���ݳ�ȡ�������⣬û�ж�Ӧ����");
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
////					System.out.print("���������ȡ��"+RainFromTopNumber+"���꣬�ֱ��ǣ�");
////					
////					for(int i = 0; i < RainFromTopNumber; i++)
////						System.out.print(RainList[i] + " ");
////					System.out.println();
////					System.out.println("���������ȣ���ģ�岻������");
////				}
////			}
////		} else
////			System.out.println("��" + maName + "����δ��ȡ�����⣡");
////		System.out.println("================�������⴦�����================");
////		
////		//����ģ����Ϣ
////		if(0 == RainFromTopNumber)
////		{
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
////				//ȥ��Ӧ�ĳ������ȡ����ӵ���
////				Collection mapToRainValues= maIndividual.getPropertyValues(addRainToMaProperty);
////				if(mapToRainValues.isEmpty())
////					System.out.println("���ݴ��ݽ�����ģ�壬û����Ӧ���꣡");
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
////						System.out.print("����ģ��鵽"+RainFromTemNumber+"���꣬�ֱ��ǣ�");
////						
////						for(int i = 0; i < RainFromTemNumber; i++)
////							System.out.print(RainList[i] + " ");
////						
////						System.out.println();
////					}
////				}
////			}
////			System.out.println("================ģ�崦����ϣ�================");
////		}
////		
////		if(0 == RainFromInfoNumber) {
////			System.out.println("��������������û�г�ȡ���ʺϵ���");
////			return doc;
////		} 
////		
////		//�ڻ�ȡ�����������ѡȡһ��
////		java.util.Random random = new Random();
////		int rd = random.nextInt(RainFromInfoNumber);
////		
////		OWLIndividual RainIndiviual = model.getOWLIndividual(RainList[rd]);
////		System.out.print("���ѡȡ������Ϊ��" + RainIndiviual.getBrowserText());
////		
////		int strLength = RainIndiviual.getBrowserText().length();
////		String RainType = RainIndiviual.getBrowserText().substring(3,strLength-5);
////		
////		Direction RainDir;
////		Direction[] Dir = Direction.values();//dir�洢ö�ٵ�ֵ
////		int num = new Random().nextInt(8);//[0,8]֮�������
////		RainDir = Dir[num];
////		String RainDirection = RainDir.toString();
////		
////		System.out.println("\n������ͣ�" + RainType + "\n��ķ���" + RainDirection);
////		System.out.println("======================��Ϣ��ȡ��ϣ�=====================");
////		Document doc1 = printRainRule(doc,RainType,RainDirection);
////		return doc1;
////	}
////	
////	public  void RainInfer2(ArrayList<String> list,OWLModel model,String maName) throws OntologyLoadException, SWRLRuleEngineException
////	{
////		String[] RainList =new String[50];//�����洢��ȡ�����ķ��������ģ�����
////		int RainFromInfoNumber=0;//���ճ�ȡ�ķ��ϵ��������
////
////		String str = "p4:";
////		
////		OWLIndividual maIndividual = model.getOWLIndividual(maName);//��ȡmayaʵ��
////		
////		if(null == maIndividual){
////			System.out.println("maya ʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ�����");
////			RainFromInfoNumber=0;
////			return;
////		}
////		
////		//�õ��ĸ�������
////		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
////		OWLObjectProperty addRainToMaProperty = model.getOWLObjectProperty(str + "addRainToMa");
////		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//�˱��������жϳ���������������
////
////		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
////		if(null == plane){
////			System.out.println("�޷���ȡʵ��" + maIndividual.getBrowserText() + "��hasValueOfPlace����(�����������жϳ����������ڻ�������)��so �ܾ����꣡����֪ʶ���и������Ƿ���ڻ���ȷ��");
////			return;
////		}else {
////			if(plane.getBrowserText().equals("InWaterDescription") || plane.getBrowserText().equals("inDoorDescription")) {
////				System.out.println("�˳������ʺϼ��꣡");
////				return;
////			}
////		}
////		
////		//����������Ϣ
////		System.out.println("================���������⴦��================");
////		Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
////		
////		if(!topics.isEmpty())
////		{
////			OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//��topicsת��Ϊ����
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
////				System.out.println("���ݳ�ȡ�������⣬û�ж�Ӧ�ķ�");
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
////			System.out.println("��" + maName + "����δ��ȡ�����⣡");
////			RainFromInfoNumber=0;
////		}
////		System.out.println("================�������⴦�����================");
////		
////		//����ģ����Ϣ
////		if(0 == RainFromInfoNumber)
////		{
////			System.out.println("==================ģ�崦��====================");
////			
////			if(list.size() > 0) {
////				executeTemplateToBackgroundSceneSWRLEngine(model,list);
////				
////				//ȥ��Ӧ�ĳ������ȡ����ӵ���
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
////			System.out.println("================ģ�崦����ϣ�================");
////		}
////		
////		System.out.println("RainFromInfoNum="+RainFromInfoNumber);
////		setRain(RainFromInfoNumber);	
////	}
//	
//	public Document printRainRule(Document doc,String RainType,String RainDirection)
//	{
//		System.out.println("=====================��ʼ����xml-rule======================");
//		Element rootName = (Element) doc.getRootElement();
//		Element name = rootName.element("maName");
//		Element ruleName = name.addElement("rule");
//		ruleName.addAttribute("ruleType", "addEffectToMa");
//		ruleName.addAttribute("type","Rain");
//		ruleName.addAttribute("Magnitude", RainType);
//		ruleName.addAttribute("Direction", RainDirection);
//		System.out.println("xml-rule�������");
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
//		System.out.println("��ʼ!");
//		Document document1 = rain.RainInfer(alist, model, "kindergarten.ma",document);
////		Document document1 = rain.RainInfer(alist, model, "Tropical45.ma",document);
//		rain.RainInfer2(alist, model, "Tropical45.ma","");
//		XMLWriter writer = new XMLWriter(new FileWriter("f:\\testRain.xml"));
//		writer.write(document1); 
//		System.out.println("������");
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
////				if(imp.getLocalName().contains("addRainToMaRule")) {//�ҵ����ְ�����addRainToSceneRule���Ĺ���
////					for(Iterator<String> its=templateName.iterator();its.hasNext();) {
////						
////						String templateValue= its.next();
////						String templateValue1=templateValue.substring(0,templateValue.indexOf(":"));
////						
////						if(imp.getBody().getBrowserText().contains(templateValue1)) {
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
////	public  Document RainInfer(ArrayList<String> list,OWLModel model,String maName,Document doc) throws OntologyLoadException, SWRLRuleEngineException
////	{
////		String[] RainList =new String[50];//�����洢��ȡ�����ķ��������ģ�����
////		int RainFromInfoNumber=0;//���ճ�ȡ�ķ��ϵ��������
////		int RainFromTemNumber=0;
////		int RainFromTopNumber=0;
////		String str = "p4:";
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
////		OWLObjectProperty addRainToMaProperty = model.getOWLObjectProperty(str + "addRainToMa");
////		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//�˱��������жϳ���������������
////
////		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
////		if(null == plane)
////			return doc;
////		else {
////			if(plane.getBrowserText().equals("InWaterDescription") || plane.getBrowserText().equals("inDoorDescription")) {
////				System.out.println("�˳������ʺϼ��꣡");
////				return doc;
////			}
////		}
////		
////		//����������Ϣ
////		System.out.println("================���������⴦��================");
////		Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
////		
////		if(!topics.isEmpty())
////		{
////			OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//��topicsת��Ϊ����
////			ArrayList topicList = new ArrayList();
////			
////			for(int i = 0; i < IdTopics.length; i++) {
////				topicList.add(IdTopics[i].getBrowserText());
////			}
////			
////			int topicSize = topicList.size();
////			if(topicSize>0) {
////				System.out.print("��ȡ�������������Ϊ��" + topicSize + "�����ǣ�");
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
////				System.out.println("���ݳ�ȡ�������⣬û�ж�Ӧ����");
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
////					System.out.print("���������ȡ��"+RainFromTopNumber+"���꣬�ֱ��ǣ�");
////					
////					for(int i = 0; i < RainFromTopNumber; i++)
////						System.out.print(RainList[i] + " ");
////					System.out.println();
////					System.out.println("���������ȣ���ģ�岻������");
////				}
////			}
////		} else
////			System.out.println("��" + maName + "����δ��ȡ�����⣡");
////		System.out.println("================�������⴦�����================");
////		
////		//����ģ����Ϣ
////		if(0 == RainFromTopNumber)
////		{
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
////				//ȥ��Ӧ�ĳ������ȡ����ӵ���
////				Collection mapToRainValues= maIndividual.getPropertyValues(addRainToMaProperty);
////				if(mapToRainValues.isEmpty())
////					System.out.println("���ݴ��ݽ�����ģ�壬û����Ӧ���꣡");
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
////						System.out.print("����ģ��鵽"+RainFromTemNumber+"���꣬�ֱ��ǣ�");
////						
////						for(int i = 0; i < RainFromTemNumber; i++)
////							System.out.print(RainList[i] + " ");
////						
////						System.out.println();
////					}
////				}
////			}
////			System.out.println("================ģ�崦����ϣ�================");
////		}
////		
////		if(0 == RainFromInfoNumber) {
////			System.out.println("��������������û�г�ȡ���ʺϵ���");
////			return doc;
////		} else 
////			setRain(RainFromInfoNumber);
////		
////		//�ڻ�ȡ�����������ѡȡһ��
////		java.util.Random random = new Random();
////		int rd = random.nextInt(RainFromInfoNumber);
////		
////		OWLIndividual RainIndiviual = model.getOWLIndividual(RainList[rd]);
////		System.out.print("���ѡȡ������Ϊ��" + RainIndiviual.getBrowserText());
////		
////		int strLength = RainIndiviual.getBrowserText().length();
////		String RainType = RainIndiviual.getBrowserText().substring(3,strLength-1);
////		
////		Direction RainDir;
////		Direction[] Dir = Direction.values();//dir�洢ö�ٵ�ֵ
////		int num = new Random().nextInt(8);//[0,8]֮�������
////		RainDir = Dir[num];
////		String RainDirection = RainDir.toString();
////		
////		System.out.println("\n������ͣ�" + RainType + "\n��ķ���" + RainDirection);
////		System.out.println("======================��Ϣ��ȡ��ϣ�=====================");
////		Document doc1 = printRainRule(doc,RainType,RainDirection);
////		return doc1;
////	}
////	
////	public  void RainInfer2(ArrayList<String> list,OWLModel model,String maName) throws OntologyLoadException, SWRLRuleEngineException
////	{
////		String[] RainList =new String[50];//�����洢��ȡ�����ķ��������ģ�����
////		int RainFromInfoNumber=0;//���ճ�ȡ�ķ��ϵ��������
////		int RainFromTemNumber=0;
////		int RainFromTopNumber=0;
////		String str = "p4:";
////		
////		OWLIndividual maIndividual = model.getOWLIndividual(maName);//��ȡmayaʵ��
////		
////		if(null == maIndividual){
////			RainFromInfoNumber=0;
////		}
////		
////		//�õ��ĸ�������
////		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
////		OWLObjectProperty addRainToMaProperty = model.getOWLObjectProperty(str + "addRainToMa");
////		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//�˱��������жϳ���������������
////
////		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
////		if(null == plane){
////			setRain(RainFromInfoNumber);
////			return;
////		}else {
////			if(plane.getBrowserText().equals("InWaterDescription") || plane.getBrowserText().equals("inDoorDescription")) {
////				System.out.println("�˳������ʺϼ��꣡");
////				RainFromInfoNumber=0;
////				setRain(RainFromInfoNumber);
////				return;
////			}
////		}
////		
////		//����������Ϣ
////		System.out.println("================���������⴦��================");
////		Collection topics = maIndividual.getPropertyValues(hasTopicPorperty);
////		
////		if(!topics.isEmpty())
////		{
////			OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//��topicsת��Ϊ����
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
////		System.out.println("================�������⴦�����================");
////		
////		//����ģ����Ϣ
////		if(0 == RainFromTopNumber)
////		{
////			System.out.println("==================ģ�崦��====================");
////			
////			if(list.size() > 0) {
////				executeTemplateToBackgroundSceneSWRLEngine(model,list);
////				
////				//ȥ��Ӧ�ĳ������ȡ����ӵ���
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
////			System.out.println("================ģ�崦����ϣ�================");
////		}
////			System.out.println("RainFromInfoNum="+RainFromInfoNumber);
////			setRain(RainFromInfoNumber);	
////	}
////	
////	public Document printRainRule(Document doc,String RainType,String RainDirection)
////	{
////		System.out.println("=====================��ʼ����xml-rule======================");
////		Element rootName = (Element) doc.getRootElement();
////		Element name = rootName.element("maName");
////		Element ruleName = name.addElement("rule");
////		ruleName.addAttribute("ruleType", "addEffectToMa");
////		ruleName.addAttribute("type","Rain");
////		ruleName.addAttribute("Magnitude", RainType);
////		ruleName.addAttribute("Direction", RainDirection);
////		System.out.println("xml-rule�������");
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
////		System.out.println("��ʼ!");
////		Document document1 = rain.RainInfer(alist, model, "kindergarten.ma",document);
////	//	Document document1 = rain.RainInfer(alist, model, "",document);
////		XMLWriter writer = new XMLWriter(new FileWriter("f:\\testRain.xml"));
////		writer.write(document1); 
////		System.out.println("������");
////		System.out.println("Rain num : " + rain.getRain());
////		writer.close();
////	}