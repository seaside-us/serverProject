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
	//topicAndTemplae �����ŵ��ǲ��������ģ�壬������Щ�����ģ�岢����ÿ�γ鵽��Ҫ�����Ч�������������������
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
				if (imp.getLocalName().contains("addWindToMaRule")) { // �ҵ���ΪaddWindtoMaRule�Ĺ���
					for (Iterator<String> its = templateName.iterator(); its.hasNext();) {

						String templateValue = its.next();
						String templateValue1 = templateValue.replaceAll("Individual", "");

						if (imp.getBody().getBrowserText().contains(templateValue1)) {
							logger.info("���еĹ�������Ϊ��" + imp.getLocalName());
							imp.enable();// ִ�д˹���
						}
					}
				}
			}
		}
		ruleEngine.infer();
	}

	// ִ��OWL��Ĺ���
	public void executeTemplateToBackgroundSceneSWRLEngine(OWLModel model,ArrayList<String> templateName) throws SWRLRuleEngineException {
		SWRLRuleEngine ruleEngine = createRuleEngine(model);
		ruleEngine.reset();
		SWRLFactory factory = createSWRLFactory(model);
		factory.disableAll();
		Iterator<SWRLImp> iter = factory.getImps().iterator();

		while (iter.hasNext()) {

			SWRLImp imp = (SWRLImp) iter.next();

			if (templateName.size() != 0) {
				if (imp.getLocalName().contains("addWindToMaRule")) { // �ҵ����ְ���"addWindToSceneRule"�Ĺ���
					for (Iterator<String> its = templateName.iterator(); its.hasNext();) {

						String templateValue = its.next();
						String templateValue1 = templateValue.substring(0,
						templateValue.indexOf(":"));

						if (imp.getBody().getBrowserText().contains(templateValue1)) {
							logger.info("���еĹ������֣�" + imp.getLocalName());
							imp.enable();
						}
					}
				}
			}
		}
		ruleEngine.infer();

	}

	public Document WindInfer(ArrayList<String> List, OWLModel model,String maName, Document doc) throws OntologyLoadException,SWRLRuleEngineException {
		String[] WindList = new String[20];// �����洢��ȡ�����ķ��������ģ��ķ�
		int WindFromInfoNumber = 0;// ���ճ�ȡ�ķ��ϵķ������
		int WindFromTemNumber = 0; // ��ģ��鵽��ĸ���
		int WindFromTopNumber = 0;// ������鵽��ĸ���
		String str = "p4:";
		String ieTopic;
		ArrayList<String> topicList = new ArrayList<String>();
		ArrayList<String> list = new ArrayList<String>();
		Collection mapToWindValues = null;
		
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
//
		OWLIndividual maIndividual = model.getOWLIndividual(maName); // ��ȡmayaʵ��
		if (null == maIndividual) {
			System.out.println("maya ʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ�����");
			return doc;
		}
//System.out.println("mayaʵ����");
//System.out.println(maIndividual);
		// �õ��ĸ�������
		OWLObjectProperty hasTopicProperty = model.getOWLObjectProperty("hasTopic");
		OWLObjectProperty addWindToMaProperty = model.getOWLObjectProperty(str + "addWindToMa");
		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");// �˱��������жϳ���������������
//System.out.println("hasValueOfPlane==" + hasValueOfPlane);
		// �ж�
		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
		if (null == plane) {
			System.out.println("�޷���ȡʵ��" + maIndividual.getBrowserText() + "��hasValueOfPlace����(�����������жϳ����������ڻ�������)��so �ܾ��ӷ磡����֪ʶ���и������Ƿ���ڻ���ȷ��");
			return doc;
		} else {
			if (plane.getBrowserText().equals("InWaterDescription")	|| plane.getBrowserText().equals("inDoorDescription")) {
				System.out.println("�˳������ʺϼӷ磡");
				return doc;
			}
		}

		// ����������Ϣ
		System.out.println("---------------���������⴦��---------------");
		System.out.println("     --------���ȴ���IE��ȡ������-------");

		ieTopic = doc.getRootElement().element("maName").attributeValue("topic");// ��ȡIE ����
		if (ieTopic.contains("Topic")) {
			System.out.println("IE ��ȡ�������ǣ�" + ieTopic);
			topicList.add(ieTopic);
			executeTopicToBackgroundSceneSWRLEngine(model, topicList);
			mapToWindValues = maIndividual.getPropertyValues(addWindToMaProperty);
//System.out.println("mapToWindValues==" + mapToWindValues);
		if (mapToWindValues.isEmpty()) {
			System.out.println("���ݳ�ȡ�������⣬û�ж�Ӧ�ķ�");
		} else {

			for (Iterator it1 = mapToWindValues.iterator(); it1.hasNext();) {

				OWLIndividual WindIndiviual = (OWLIndividual) it1.next();
				WindList[WindFromInfoNumber] = WindIndiviual.getBrowserText();
				WindFromInfoNumber++;
				WindFromTopNumber++;
			}

			if (WindFromTopNumber != 0) {
				System.out.print("���������ȡ��" + WindFromTopNumber + "���磬�ֱ��ǣ�");

				for (int i = 0; i < WindFromTopNumber; i++)
					System.out.print(WindList[i] + " ");

				System.out.println("\n���������ȣ���ģ�岻������");
			}
		}

		} else {
			System.out.println("     ----------IEδ��ȡ������--------");
//			System.out.println("     --------������ma��ȡ������-------");
//
//			Collection topics = maIndividual.getPropertyValues(hasTopicProperty);
//			if (!topics.isEmpty()) {
//
//				OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);// ��topicsת��Ϊ����
//
//				for (int i = 0; i < IdTopics.length; i++) {
//					topicList.add(IdTopics[i].getBrowserText());
//				}
//
//				int topicSize = topicList.size();
//				if (topicSize > 0) {
//					System.out.print("��ȡ�������������Ϊ��" + topicSize + "�����ǣ�");
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

		
		System.out.println("---------------�������⴦�����!----------------");

		// ����ģ����Ϣ
		if (WindFromTopNumber == 0) {
			System.out.println("-------------ģ�崦��---------------");
			int listSize = 0;
			if (list != null) {
				listSize = list.size();// ģ�����
			} else
				System.out.println("δ���ݽ����κ�ģ��");

			if (listSize > 0) {

				System.out.println("���ݽ�����ģ�����Ϊ��" + listSize + "�����ֱ��ǣ�" + list);
				executeTemplateToBackgroundSceneSWRLEngine(model, list);
				// ȥ��Ӧ�ĳ������ȡ����ӵķ�
				mapToWindValues = null;
				mapToWindValues = maIndividual.getPropertyValues(addWindToMaProperty);
//System.out.println("maptoWindValues == " + mapToWindValues);
				if (mapToWindValues.isEmpty())
					System.out.println("���ݴ��ݹ�����ģ�壬û����Ӧ�ķ磡");
				else {

					for (Iterator it1 = mapToWindValues.iterator(); it1.hasNext();) {
						OWLIndividual WindIndiviual = (OWLIndividual) it1.next();
						WindList[WindFromInfoNumber] = WindIndiviual.getBrowserText();
						WindFromInfoNumber++;
						WindFromTemNumber++;
					}

					if (WindFromTemNumber != 0) {

						System.out.print("����ģ��鵽" + WindFromTemNumber + "���磬�ֱ��ǣ�");

						for (int i = 0; i < WindFromTemNumber; i++)
							System.out.print(WindList[i] + " ");

						System.out.println();
					}
				}
			}
			System.out.println("-------------ģ�崦����ϣ�------------");
		}

		if (WindFromInfoNumber == 0) {
			System.out.println("��������������û�г�ȡ���ʺϵķ�");
			return doc;
		}

		// �ڻ�ȡ���ķ��������ѡȡһ��

		java.util.Random random = new Random();
		int rd = random.nextInt(WindFromInfoNumber);

		OWLIndividual WindIndiviual = model.getOWLIndividual(WindList[rd]);
		System.out.println("���ѡȡ���ķ�Ϊ��" + WindIndiviual.getBrowserText());

	//	int strLength = WindIndiviual.getBrowserText().length();

		String WindType = null;
		String WindDirection = "";
		String WindExpress = "";// ģ��Ϊ����ʱ�������ֱ��ַ�ʽ
		
		OWLDatatypeProperty Magnitude =  model.getOWLDatatypeProperty(str + "magnitude");
		WindType = (String) WindIndiviual.getPropertyValue(Magnitude);
		System.out.println("WindType == " + WindType);
		
		if (WindIndiviual.getBrowserText().contains("Emotion")) {

			if (WindType.equals("Heavy")) {
				WindExpress = "Conic";// Բ׶
			} else if (WindType.equals("Moderate")) {
				WindExpress = "Helix";// ����
			} else if (WindType.equals("Light")) {
				WindExpress = "Cardioid";// ������
			}

		} else {
			Direction WindDir;
			Direction[] Dir = Direction.values();// Dir�洢ö�ٵ�ֵ��
			int num = new Random().nextInt(8);// [0,8)֮�������
			WindDir = Dir[num];
			WindDirection = WindDir.toString();
		}

		System.out.println("\n������ͣ� " + WindType + "\n��ķ��� " + WindDirection
				+ "\n��ı����ַ���" + WindExpress + "\n------��Ϣ��ȡ��ϣ�------");

		Document doc1 = printWindRule(doc, WindType, WindDirection, WindExpress);
		return doc1;
	}

	/**
	 * �˷������ڳ������Ƿ�ӽ�ȥ�� WindFromInfoNumber ��־�ӽ�ȥ��ĸ�������set������get������ȡ
	 * */
	public void  WindInfer2(ArrayList<String> List,OWLModel model,String maName,String ieTopic) throws OntologyLoadException ,SWRLRuleEngineException {
		String[] WindList =new String[50];//�����洢��ȡ�����ķ��������ģ��ķ�
		int WindFromInfoNumber=0;//���ճ�ȡ�ķ��ϵķ������

		String str="p4:";

		ArrayList<String> topicList = new ArrayList<String>();
		ArrayList<String> list = new ArrayList<String>();
		Collection mapToWindValues = null;
		
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
		OWLIndividual maIndividual =model.getOWLIndividual(maName);//��ȡmayaʵ��
		if(null == maIndividual) {
			System.out.println("maya ʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ�����");
				return;
			}
				
			//�õ��ĸ�������
			OWLObjectProperty hasTopicProperty = model.getOWLObjectProperty("hasTopic");
			OWLObjectProperty addWindToMaProperty = model.getOWLObjectProperty(str+"addWindToMa");
			OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//�˱��������жϳ���������������
				
			//�ж�
			OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
			if(null == plane) {
				System.out.println("�޷���ȡʵ��" + maIndividual.getBrowserText() + "��hasValueOfPlace����,�����������жϳ����������ڻ������⣬����֪ʶ���и������Ƿ���ڻ���ȷ��");
				WindFromInfoNumber=0;//��ȡ�鵽�������
				return;
			}else {
				if(plane.getBrowserText().equals("InWaterDescription")||plane.getBrowserText().equals("inDoorDescription")) {
					System.out.println ("�˳������ʺϼӷ磡");
					WindFromInfoNumber=0;//��ȡ�鵽�������
					return;
				}
			}
				
			//����������Ϣ
			System.out.println("---------------���������⴦��---------------");	
			System.out.println("     --------���ȴ���IE��ȡ������-------");
		
			if(ieTopic.contains("Topic")){
				System.out.println("IE ��ȡ�������ǣ�" + ieTopic);
				topicList.add(ieTopic);
				executeTopicToBackgroundSceneSWRLEngine(model,topicList);
				mapToWindValues = maIndividual.getPropertyValues(addWindToMaProperty);
			if(mapToWindValues.isEmpty()) {
				System.out.println("���ݳ�ȡ�������⣬û�ж�Ӧ�ķ�");
				WindFromInfoNumber=0;
			} else {
						
				for(Iterator it1=mapToWindValues .iterator();it1.hasNext();) {
					OWLIndividual WindIndiviual = (OWLIndividual)it1.next();
					WindList[WindFromInfoNumber] = WindIndiviual.getBrowserText();
					WindFromInfoNumber++;
					
				}
				System.out.println("��������" + WindFromInfoNumber + "���磡");
			}

			} else {
				System.out.println("     ----------IEδ��ȡ������--------");
//				System.out.println("     --------������ma��ȡ������-------");	
//				Collection topics=maIndividual.getPropertyValues(hasTopicProperty);
//				if(!topics.isEmpty()) {
//						
//					OWLIndividual[] IdTopics =(OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//��topicsת��Ϊ����
//					
//					for (int i=0;i<IdTopics.length;i++){
//						topicList.add(IdTopics[i].getBrowserText());
//					}
//						
//					executeTopicToBackgroundSceneSWRLEngine(model,topicList);
//				}
			}
				
			
			System.out.println("---------------�������⴦�����!----------------");
				
			//����ģ����Ϣ
			if(WindFromInfoNumber == 0) {
				System.out.println("-------------ģ�崦��---------------");
					
				if(list.size()> 0) {
						
					executeTemplateToBackgroundSceneSWRLEngine(model ,list);
					//ȥ��Ӧ�ĳ������ȡ����ӵķ�
					mapToWindValues = null;
					mapToWindValues = maIndividual.getPropertyValues(addWindToMaProperty);
					if(mapToWindValues.isEmpty()){
						WindFromInfoNumber=0;
						System.out.println("	-------���ݴ��ݹ�����ģ�壬û����Ӧ�ķ磡-------");
					}
					else {
							
						for(Iterator it1=mapToWindValues.iterator();it1.hasNext();) {
							OWLIndividual WindIndiviual =(OWLIndividual)it1.next();
							WindList[WindFromInfoNumber]=WindIndiviual .getBrowserText();
							WindFromInfoNumber++;
						
						}
							System.out.println("��ģ����" + WindFromInfoNumber + "���磡");	
					}
						
				}
				System.out.println("-------------ģ�崦����ϣ�------------");
			}
		setWind(WindFromInfoNumber);//��ȡ�鵽�������
	}

	public Document printWindRule(Document doc, String WindType,
			String WindDirection, String WindExpress)// �˷�����Ȼ�з����������������δ�ã���Ϊ�����ڳ������޷�ʶ�����ϱ�����д������Ϊ���Ժ���
	{
		System.out.println("��ʼ����xml-rule");

		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");

		Element ruleName = name.addElement("rule");

		ruleName.addAttribute("ruleType", "addEffectToMa");
		ruleName.addAttribute("type", "Wind");
		ruleName.addAttribute("Magnitude", WindType);
		ruleName.addAttribute("Direction", WindDirection);
		ruleName.addAttribute("Expression", WindExpress);

		System.out.println("xml-rule�������");
		return doc;
	}

	public static void main(String[] args) throws ParserConfigurationException,SAXException, IOException, JDOMException, DocumentException,OntologyLoadException, SWRLRuleEngineException {

		// String owlPath
		// ="file:///F://ѧϰ//MyProjects//Protege//LHH//Wind2.owl";
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
		// File("F:\\ѧϰ\\MyProjects\\module\\module\\cal\\inputFile\\adl_result.xml");
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(file);
//=========================================================	    
		WindInsert wind = new WindInsert();
		System.out.println("��ʼ��");
		Document document1 = wind.WindInfer(aList, model, "riverFallingTree.ma",document);
		XMLWriter writer = new XMLWriter(new FileWriter("f:\\test1.xml"));
		writer.close();
		System.out.println("����!");
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
//				if(imp.getLocalName().contains("addWindToMaRule")) {					//�ҵ���ΪaddWindtoMaRule�Ĺ���
//					for(Iterator<String> its = templateName.iterator();its.hasNext();) {
//						
//						String templateValue = its.next();
//						String templateValue1 = templateValue.replaceAll("Individual", "");
//						
//						if(imp.getBody().getBrowserText().contains(templateValue1))	{
//							logger.info("���еĹ�������Ϊ��"+imp.getLocalName());
//							imp.enable();//ִ�д˹���
//						}
//					}
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
//		while (iter.hasNext()) {
//			
//			SWRLImp imp=(SWRLImp) iter.next();
//
//			if(templateName.size()!=0) {
//				if(imp.getLocalName().contains("addWindToMaRule")) {		//�ҵ����ְ���"addWindToSceneRule"�Ĺ���
//					for(Iterator<String> its = templateName.iterator();its.hasNext();) {
//						
//						String templateValue = its.next();
//						String templateValue1 =templateValue.substring(0, templateValue.indexOf(":"));
//						
//						if(imp.getBody().getBrowserText().contains(templateValue1)) {
//							logger.info("���еĹ������֣�"+imp.getLocalName());
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
//		String[] WindList = new String[50];// �����洢��ȡ�����ķ��������ģ��ķ�
//		int WindFromInfoNumber = 0;// ���ճ�ȡ�ķ��ϵķ������
//		int WindFromTemNumber = 0; // ��ģ��鵽��ĸ���
//		int WindFromTopNumber = 0;// ������鵽��ĸ���
//		String str = "p4:";
//		String ieTopic;
//		ArrayList<String> topicList = new ArrayList<String>();
//		Collection mapToWindValues = null;
//
//		OWLIndividual maIndividual = model.getOWLIndividual(maName); // ��ȡmayaʵ��
//		if (null == maIndividual) {
//			System.out.println("maya ʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ�����");
//			return doc;
//		}
//
//		// �õ��ĸ�������
//		OWLObjectProperty hasTopicProperty = model.getOWLObjectProperty("hasTopic");
//		OWLObjectProperty addWindToMaProperty = model.getOWLObjectProperty(str + "addWindToMa");
//		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");// �˱��������жϳ���������������
//
//		// �ж�
//		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
//		if (null == plane) {
//			System.out.println("�޷���ȡʵ��" + maIndividual.getBrowserText() + "��hasValueOfPlace����(�����������жϳ����������ڻ�������)��so �ܾ��ӷ磡����֪ʶ���и������Ƿ���ڻ���ȷ��");
//			return doc;
//		} else {
//			if (plane.getBrowserText().equals("InWaterDescription")	|| plane.getBrowserText().equals("inDoorDescription")) {
//				System.out.println("�˳������ʺϼӷ磡");
//				return doc;
//			}
//		}
//
//		// ����������Ϣ
//		System.out.println("---------------���������⴦��---------------");
//		System.out.println("     --------���ȴ���IE��ȡ������-------");
//
//		ieTopic = doc.getRootElement().element("maName").attributeValue("topic");// ��ȡIE ����
//		if (ieTopic.contains("Topic")) {
//			System.out.println("IE ��ȡ�������ǣ�" + ieTopic);
//			topicList.add(ieTopic);
//			executeTopicToBackgroundSceneSWRLEngine(model, topicList);
//		} else {
//			System.out.println("     ----------IEδ��ȡ������--------");
//			System.out.println("     --------������ma��ȡ������-------");
//
//			Collection topics = maIndividual.getPropertyValues(hasTopicProperty);
//			if (!topics.isEmpty()) {
//
//				OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);// ��topicsת��Ϊ����
//
//				for (int i = 0; i < IdTopics.length; i++) {
//					topicList.add(IdTopics[i].getBrowserText());
//				}
//
//				int topicSize = topicList.size();
//				if (topicSize > 0) {
//					System.out.print("��ȡ�������������Ϊ��" + topicSize + "�����ǣ�");
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
//			System.out.println("���ݳ�ȡ�������⣬û�ж�Ӧ�ķ�");
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
//				System.out.print("���������ȡ��" + WindFromTopNumber + "���磬�ֱ��ǣ�");
//
//				for (int i = 0; i < WindFromTopNumber; i++)
//					System.out.print(WindList[i] + " ");
//
//				System.out.println("\n���������ȣ���ģ�岻������");
//			}
//		}
//
//		System.out.println("---------------�������⴦�����!----------------");
//
//		// ����ģ����Ϣ
//		if (WindFromTopNumber == 0) {
//			System.out.println("-------------ģ�崦��---------------");
//			int listSize = 0;
//			if (list != null) {
//				listSize = list.size();// ģ�����
//			} else
//				System.out.println("δ���ݽ����κ�ģ��");
//
//			if (listSize > 0) {
//
//				System.out.println("���ݽ�����ģ�����Ϊ��" + listSize + "�����ֱ��ǣ�" + list);
//				executeTemplateToBackgroundSceneSWRLEngine(model, list);
//				// ȥ��Ӧ�ĳ������ȡ����ӵķ�
//				mapToWindValues = null;
//				mapToWindValues = maIndividual.getPropertyValues(addWindToMaProperty);
//				if (mapToWindValues.isEmpty())
//					System.out.println("���ݴ��ݹ�����ģ�壬û����Ӧ�ķ磡");
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
//						System.out.print("����ģ��鵽" + WindFromTemNumber + "���磬�ֱ��ǣ�");
//
//						for (int i = 0; i < WindFromTemNumber; i++)
//							System.out.print(WindList[i] + " ");
//
//						System.out.println();
//					}
//				}
//			}
//			System.out.println("-------------ģ�崦����ϣ�------------");
//		}
//
//		if (WindFromInfoNumber == 0) {
//			System.out.println("��������������û�г�ȡ���ʺϵķ�");
//			return doc;
//		}
//
//		// �ڻ�ȡ���ķ��������ѡȡһ��
//
//		java.util.Random random = new Random();
//		int rd = random.nextInt(WindFromInfoNumber);
//
//		OWLIndividual WindIndiviual = model.getOWLIndividual(WindList[rd]);
//		System.out.println("���ѡȡ���ķ�Ϊ��" + WindIndiviual.getBrowserText());
//
//
//		String WindType = null;
//		String WindDirection = "";
//		String WindExpress = "";// ģ��Ϊ����ʱ�������ֱ��ַ�ʽ
//		OWLDatatypeProperty Magnitude =  model.getOWLDatatypeProperty(str + "magnitude");
//		WindType = (String) WindIndiviual.getPropertyValue(Magnitude);
//		System.out.println("WindType = " + WindType);
//		
//		if (WindIndiviual.getBrowserText().contains("Emotion")) {
//
//			if (WindType.equals("Heavy")) {
//				WindExpress = "Conic";// Բ׶
//			} else if (WindType.equals("Moderate")) {
//				WindExpress = "Helix";// ����
//			} else if (WindType.equals("Light")) {
//				WindExpress = "Cardioid";// ������
//			}
//
//		} else {
//			Direction WindDir;
//			Direction[] Dir = Direction.values();// Dir�洢ö�ٵ�ֵ��
//			int num = new Random().nextInt(8);// [0,8)֮�������
//			WindDir = Dir[num];
//			WindDirection = WindDir.toString();
//		}
//
//		System.out.println("\n������ͣ� " + WindType + "\n��ķ��� " + WindDirection
//				+ "\n��ı����ַ���" + WindExpress + "\n------��Ϣ��ȡ��ϣ�------");
//
//		Document doc1 = printWindRule(doc, WindType, WindDirection, WindExpress);
//		return doc1;
//	}
//	
//	/* ***********************************************************************************
//	 							�˷������ڳ������Ƿ�ӽ�ȥ��
//	  		WindFromInfoNumber ��־�ӽ�ȥ��ĸ�������set������get������ȡ
//	*************************************************************************************/
//
//	public void  WindInfer2(ArrayList<String> list,OWLModel model,String maName,String ieTopic) throws OntologyLoadException ,SWRLRuleEngineException {
//		String[] WindList =new String[50];//�����洢��ȡ�����ķ��������ģ��ķ�
//		int WindFromInfoNumber=0;//���ճ�ȡ�ķ��ϵķ������
//
//		String str="p4:";
//
//		ArrayList<String> topicList = new ArrayList<String>();
//		Collection mapToWindValues = null;
//				
//		OWLIndividual maIndividual =model.getOWLIndividual(maName);//��ȡmayaʵ��
//		if(null == maIndividual) {
//			System.out.println("maya ʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ�����");
//				return;
//			}
//				
//			//�õ��ĸ�������
//			OWLObjectProperty hasTopicProperty = model.getOWLObjectProperty("hasTopic");
//			OWLObjectProperty addWindToMaProperty = model.getOWLObjectProperty(str+"addWindToMa");
//			OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//�˱��������жϳ���������������
//				
//			//�ж�
//			OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
//			if(null == plane) {
//				System.out.println("�޷���ȡʵ��" + maIndividual.getBrowserText() + "��hasValueOfPlace����,�����������жϳ����������ڻ������⣬����֪ʶ���и������Ƿ���ڻ���ȷ��");
//				WindFromInfoNumber=0;//��ȡ�鵽�������
//				return;
//			}else {
//				if(plane.getBrowserText().equals("InWaterDescription")||plane.getBrowserText().equals("inDoorDescription")) {
//					System.out.println ("�˳������ʺϼӷ磡");
//					WindFromInfoNumber=0;//��ȡ�鵽�������
//					return;
//				}
//			}
//				
//			//����������Ϣ
//			System.out.println("---------------���������⴦��---------------");	
//			System.out.println("     --------���ȴ���IE��ȡ������-------");
//		
//			if(ieTopic.contains("Topic")){
//				System.out.println("IE ��ȡ�������ǣ�" + ieTopic);
//				topicList.add(ieTopic);
//				executeTopicToBackgroundSceneSWRLEngine(model,topicList);
//			} else {
//				System.out.println("     ----------IEδ��ȡ������--------");
//				System.out.println("     --------������ma��ȡ������-------");	
//				Collection topics=maIndividual.getPropertyValues(hasTopicProperty);
//				if(!topics.isEmpty()) {
//						
//					OWLIndividual[] IdTopics =(OWLIndividual[]) topics.toArray(new OWLIndividual[0]);//��topicsת��Ϊ����
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
//				System.out.println("���ݳ�ȡ�������⣬û�ж�Ӧ�ķ�");
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
//			System.out.println("---------------�������⴦�����!----------------");
//				
//			//����ģ����Ϣ
//			if(WindFromInfoNumber == 0) {
//				System.out.println("-------------ģ�崦��---------------");
//					
//				if(list.size()> 0) {
//						
//					executeTemplateToBackgroundSceneSWRLEngine(model ,list);
//					//ȥ��Ӧ�ĳ������ȡ����ӵķ�
//					mapToWindValues = null;
//					mapToWindValues = maIndividual.getPropertyValues(addWindToMaProperty);
//					if(mapToWindValues.isEmpty()){
//						
//						System.out.println("	-------���ݴ��ݹ�����ģ�壬û����Ӧ�ķ磡-------");
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
//				System.out.println("-------------ģ�崦����ϣ�------------");
//			}
//			System.out.println("WindFromInfoNumber"+WindFromInfoNumber);
//		setWind(WindFromInfoNumber);//��ȡ�鵽�������
//	}
//	
//
//		public Document printWindRule(Document doc,String WindType,String WindDirection, String WindExpress)//�˷�����Ȼ�з����������������δ�ã���Ϊ�����ڳ������޷�ʶ�����ϱ�����д������Ϊ���Ժ���
//		{	
//			System.out.println("��ʼ����xml-rule");
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
//			System.out.println("xml-rule�������");
//			return doc;
//		}
//		
//		public static void main(String[] args) throws ParserConfigurationException,SAXException,IOException,JDOMException,DocumentException,OntologyLoadException,SWRLRuleEngineException
//		{
//			
//			//String owlPath ="file:///F://ѧϰ//MyProjects//Protege//LHH//Wind2.owl";
//			String owlpath="file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";
//			OWLModel model =ProtegeOWL.createJenaOWLModelFromURI(owlpath);
//			ArrayList<String> aList =new ArrayList();
//			aList.add("WindTemplate:WindTemplate");
//			aList.add("SummerTemplate:summerTemplate");
//			aList.add("AngryTopic:angryTopic");
//			aList.add("GladnessTemplate:gladnessTemplate");
//			//aList.add("SummerTemplate:summerTemplate");
//			File file =new File("f:\\test.xml");
//		//	File file =new File("F:\\ѧϰ\\MyProjects\\module\\module\\cal\\inputFile\\adl_result.xml");
//			SAXReader saxReader=new SAXReader();
//			Document document =saxReader.read(file);
//			WindInsert wind = new WindInsert();
//			System.out.println("��ʼ��");
//			Document document1=wind.WindInfer(aList,model,"kindergarten.ma",document);
//			
//		//	Document document1=wind.WindInfer(aList,model,"Tropical45.ma",document);
//			wind.WindInfer2(aList,model,"kindergarten.ma","MissTopic");
//			XMLWriter writer =new XMLWriter(new FileWriter("f:\\test1.xml"));
//		//	XMLWriter writer =new XMLWriter (new FileWriter("F:\\ѧϰ\\MyProjects\\module\\module\\cal\\inputFile\\adl_resultlhh.xml"));
//			writer.write(document1);
//			System.out.println("����!");
//			System.out.println("Wind num : " + wind.getWind());
//			writer.close();
//		}
//}
