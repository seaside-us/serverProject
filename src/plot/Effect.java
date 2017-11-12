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
	private int ifNeg=0;//0Ϊ�϶���1Ϊ��
	
	//	private boolean WRS = false;//��ʾ��ȷ�з���ѩ��ģ�塣˵����������ȷ�ᵽ�硢�ꡢѩ�ˣ��Ͳ��ڿ�������������ģ��������ˡ�
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
	
	private int hasEffects(ArrayList<String> list,OWLModel model,String maName) { //��������ģ���Ƿ�=1����=2��ѩ=3������=4����ѩ=5����ѩ=6���޷���ѩ=0������ѩ=��1,2,3�������һ��
		OWLIndividual individual = model.getOWLIndividual(maName);
		OWLDatatypeProperty hasEff = model.getOWLDatatypeProperty("hasEffect");
		String hasEffectValue = individual.getPropertyValue(hasEff).toString();
		if(hasEffectValue.equals("rain"))
		{
			maHasEffect = true;
			System.out.println(maName + "�Ѿ�����������Ч" + hasEffectValue);
			return 2;
		}
		else if(hasEffectValue.equals("snow"))
		{
			maHasEffect = true;
			System.out.println(maName + "�Ѿ�����������Ч" + hasEffectValue);
			return 3;
		}
		
		System.out.println("��������ģ���Ƿ�=1����=2��ѩ=3������=4����ѩ=5����ѩ=6���޷���ѩ=0������ѩ=��1,2,3�������һ��");
		
		if(list.contains("WindTemplate")) {
			
			if(list.contains("RainTemplate")) {
				
				if(list.contains("SnowTemplate")){ //�з���ѩ
					
					Random rand = new Random();
					System.out.println("ͬʱ���ַ���ѩ�����һ��");
				//	WRS = true;
					return rand.nextInt(3) + 1;
					
				} else { //�з��꣬��ѩ
					System.out.println("ͬʱ���ַ��꣬û��ѩ");
				//	WRS = true;
					return 4;
				}		
			} 
			else if(list.contains("SnowTemplate")){ 
				System.out.println("ͬʱ���ַ�ѩ��û����");
				//WRS = true;
				return 5;// �з�ѩ������
			} else { 
				System.out.println("ֻ�з��ģ��");
			//	WRS = true;
				return 1;//�з磬����ѩ
			}
		} else {  //�޷�
			if(list.contains("RainTemplate")) {
				
				if(list.contains("SnowTemplate")) {
					System.out.println("����ѩ��ģ�壬û�з��");
			//		WRS = true;
					return 6; //����ѩ
				} else {
					System.out.println("ֻ�����ģ��");
			//		WRS = true;
					return 2; //����
				}
			} else {
				if(list.contains("SnowTemplate")) {
					System.out.println("ֻ��ѩ��ģ��");
			//		WRS = true;
					return 3; // ��ѩ
				} else {
					System.out.println("����ѩ��û��");
					return 0; // �޷���ѩ
				}
			}
		}
	}
	/**
	 **================================= WindInfo ����ִ����ӷ�=================================
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
	public void windExecuteTemplateToBackgroundSceneSWRLEngine(OWLModel model,ArrayList<String> templateName) throws SWRLRuleEngineException {
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
						
						String templateValue1 = templateValue.substring(0,templateValue.indexOf(":"));

						if (imp.getBody().getBrowserText().contains(templateValue1)) {
							logger.info("���еĹ������֣�" + imp.getLocalName());
							System.out.println("���еĹ������֣�" + imp.getLocalName());
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
		
		String[] WindList = new String[20];// �����洢��ȡ�����ķ��������ģ��ķ�
		int WindFromInfoNumber = 0;// ���ճ�ȡ�ķ��ϵķ������
		int WindFromTemNumber = 0; // ��ģ��鵽��ĸ���
		int WindFromTopNumber = 0;// ������鵽��ĸ���
		String str = "p4:";
		String ieTopic;
		ArrayList<String> topicList = new ArrayList<String>();
		Collection mapToWindValues = null;
		
		
		System.out.println("######################ʵ����ִ�е�ģ�������######################");
		for(int i = 0; i < List.size(); i++){
			System.out.println(List.get(i));
		}
		System.out.println("############################################################");
//
		OWLIndividual maIndividual = model.getOWLIndividual(maName); // ��ȡmayaʵ��
		if (null == maIndividual) {
			System.out.println("maya ʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ�����");
			return doc;
		}

		OWLObjectProperty addWindToMaProperty = model.getOWLObjectProperty(str + "addWindToMa");
		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");// �˱��������жϳ���������������

		// �ж�
		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
		if (null == plane) {
			System.out.println("�޷���ȡʵ��" + maIndividual.getBrowserText() + "��hasValueOfPlace����(�����������жϳ����������ڻ�������)��so �ܾ��ӷ磡����֪ʶ���и������Ƿ���ڻ���ȷ��");
			return doc;
		} else {
			if (plane.getBrowserText().equals("InWaterDescription")	|| !plane.getBrowserText().equals("outDoorDescription")) {
				System.out.println("�˳������ʺϼӷ磡");
				return doc;
			}
		}
		
	// ����ģ����Ϣ
		if (WindFromTopNumber == 0) {
			System.out.println("-------------ģ�崦��---------------");
			int listSize = 0;
		
			if (List == null || List.size() == 0) {
				System.out.println("û��ģ�崫��");
			} else 	{	
				listSize = List.size();
			}
			
			if (listSize > 0) {

				System.out.println("���ݽ�����ģ�����Ϊ��" + listSize + "�����ֱ��ǣ�" + List);
				
				windExecuteTemplateToBackgroundSceneSWRLEngine(model, List);
				// ȥ��Ӧ�ĳ������ȡ����ӵķ�
				mapToWindValues = null;
				mapToWindValues = maIndividual.getPropertyValues(addWindToMaProperty);

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
			
		}
		System.out.println("-------------ģ�崦����ϣ�------------");
		
		// ����������Ϣ
		if(WindFromInfoNumber == 0){
			System.out.println("---------------���������⴦��---------------");
			System.out.println("     --------����IE��ȡ������-------");
	
			ieTopic = doc.getRootElement().element("maName").attributeValue("topic");// ��ȡIE ����
			if (ieTopic.contains("Topic")) {
				System.out.println("IE ��ȡ�������ǣ�" + ieTopic);
				topicList.add(ieTopic);
				windExecuteTopicToBackgroundSceneSWRLEngine(model, topicList);
				mapToWindValues = maIndividual.getPropertyValues(addWindToMaProperty);
				
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
			}
			
			System.out.println("---------------�������⴦�����!----------------");
		}
		
		if (WindFromInfoNumber == 0) {
			System.out.println("��������������û�г�ȡ���ʺϵķ�");
			return doc;
		} else {
			hasEffect = true;
		}

		// �ڻ�ȡ���ķ��������ѡȡһ��

		Random random = new Random();
		int rd = random.nextInt(WindFromInfoNumber);

		OWLIndividual WindIndiviual = model.getOWLIndividual(WindList[rd]);
		System.out.println("���ѡȡ���ķ�Ϊ��" + WindIndiviual.getBrowserText());

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
		String[] WindList = new String[20];// �����洢��ȡ�����ķ��������ģ��ķ�
		int WindFromInfoNumber = 0;// ���ճ�ȡ�ķ��ϵķ������
		int WindFromTemNumber = 0; // ��ģ��鵽��ĸ���
		int WindFromTopNumber = 0;// ������鵽��ĸ���
		String str = "p4:";
	//	String ieTopic;
		ArrayList<String> topicList = new ArrayList<String>();
		Collection mapToWindValues = null;
		
		System.out.println("######################ʵ����ִ�е�ģ�������######################");
		for(int i = 0; i < List.size(); i++){
			System.out.println(List.get(i));
		}
		System.out.println("############################################################");
//
		OWLIndividual maIndividual = model.getOWLIndividual(maName); // ��ȡmayaʵ��
		if (null == maIndividual) {
			System.out.println("maya ʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ�����");
			return ;
		}

		OWLObjectProperty addWindToMaProperty = model.getOWLObjectProperty(str + "addWindToMa");
		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");// �˱��������жϳ���������������

		// �ж�
		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
		if (null == plane) {
			System.out.println("�޷���ȡʵ��" + maIndividual.getBrowserText() + "��hasValueOfPlace����(�����������жϳ����������ڻ�������)��so �ܾ��ӷ磡����֪ʶ���и������Ƿ���ڻ���ȷ��");
			return ;
		} else {
			if (plane.getBrowserText().equals("InWaterDescription")	|| plane.getBrowserText().equals("inDoorDescription")) {
				System.out.println("�˳������ʺϼӷ磡");
				return ;
			}
		}
		
	// ����ģ����Ϣ
		if (WindFromTopNumber == 0) {
			System.out.println("-------------ģ�崦��---------------");
			int listSize = 0;
			
			if (List == null) {
				System.out.println("û��ģ�崫��");
			} else 	{	
				listSize = List.size();
			}
			
			if (listSize > 0) {

				System.out.println("���ݽ�����ģ�����Ϊ��" + listSize + "�����ֱ��ǣ�" + List);
				
				windExecuteTemplateToBackgroundSceneSWRLEngine(model, List);
				// ȥ��Ӧ�ĳ������ȡ����ӵķ�
				mapToWindValues = null;
				mapToWindValues = maIndividual.getPropertyValues(addWindToMaProperty);
				
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
			
		}
		System.out.println("-------------ģ�崦����ϣ�------------");
		
		// ����������Ϣ
		if(WindFromInfoNumber == 0){
			System.out.println("---------------���������⴦��---------------");
			System.out.println("     --------����IE��ȡ������-------");
	
	//		ieTopic = doc.getRootElement().element("maName").attributeValue("topic");// ��ȡIE ����
			if (ieTopic.contains("Topic")) {
				System.out.println("IE ��ȡ�������ǣ�" + ieTopic);
				topicList.add(ieTopic);
				windExecuteTopicToBackgroundSceneSWRLEngine(model, topicList);
				mapToWindValues = maIndividual.getPropertyValues(addWindToMaProperty);
				
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
			}
			
			System.out.println("---------------�������⴦�����!----------------");
		}
		
		if (WindFromInfoNumber == 0) {
			System.out.println("��������������û�г�ȡ���ʺϵķ�");
			return ;
		} else {
			hasEffect = true;
		}

		// �ڻ�ȡ���ķ��������ѡȡһ��
	}

	public Document printWindRule(Document doc, String WindType,String WindDirection, String WindExpress){// �˷�����Ȼ�з����������������δ�ã���Ϊ�����ڳ������޷�ʶ�����ϱ�����д������Ϊ���Ժ���
		System.out.println("��ʼ����xml-rule");

		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");

		Element ruleName = name.addElement("rule");

		ruleName.addAttribute("ruleType", "addEffectToMa");
		ruleName.addAttribute("type", "Wind");
		ruleName.addAttribute("Magnitude", WindType);
		ruleName.addAttribute("Direction", WindDirection);
		ruleName.addAttribute("Expression", WindExpress);
		ruleName.addAttribute("ifNeg", Integer.toString(ifNeg));//����adl���

		System.out.println("xml-rule�������");
		return doc;
	}
	
	/**
	 **================================= RainInfo ����ִ�������=================================
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
		if(hasEffect)
			return doc;
		String[] RainList =new String[50];//�����洢��ȡ�����ķ��������ģ�����
		int RainFromInfoNumber=0;//���ճ�ȡ�ķ��ϵ��������
		int RainFromTemNumber=0;
		int RainFromTopNumber=0;
		String str = "p4:";
		String ieTopic;
		ArrayList<String> topicList = new ArrayList<String>();
	
		Collection mapToRainValues = null;
		
		System.out.println("######################ʵ����ִ�е�ģ�������######################");
		for(int i = 0; i < List.size(); i++){
			System.out.println(List.get(i));
		}
		System.out.println("############################################################");
		OWLIndividual maIndividual = model.getOWLIndividual(maName);//��ȡmayaʵ��	
		if(null == maIndividual){
			System.out.println("mayaʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ����ڣ�");
			return doc;
		}
		
		//�õ��ĸ�������
//		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
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
		//����ģ����Ϣ
		if(0 == RainFromTopNumber){
			System.out.println("==================ģ�崦��====================");
			int listSize = 0;

			if(List.isEmpty()){
				System.out.println("û��ģ�崫��");
			} else {
				listSize = List.size();//ģ�����
			}
				
			if(listSize > 0) {
				System.out.println("���ݽ�����ģ�����Ϊ��" + listSize + "�����ֱ��ǣ�" + List);
				rainExecuteTemplateToBackgroundSceneSWRLEngine(model,List);
				
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
		}
		System.out.println("================ģ�崦����ϣ�================");
		
		if(RainFromInfoNumber == 0){
			//����������Ϣ
			System.out.println("================���������⴦��================");
			System.out.println("================���ȴ���IE����================");
			ieTopic = doc.getRootElement().element("maName").attributeValue("topic");
			
			if(ieTopic.contains("Topic")){
				System.out.println("IE Topic = " + ieTopic);
				topicList.add(ieTopic);
				rainExecuteTopicToBackgroundSceneSWRLEngine(model,topicList);
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
			} else {
				System.out.println("================IEδ��ȡ������================");
			}
	
			System.out.println("================�������⴦�����================");
	
		}
				
		
		if(0 == RainFromInfoNumber) {
			System.out.println("��������������û�г�ȡ���ʺϵ���");
			return doc;
		} else {
			hasEffect = true;
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
		Document doc1 = printRainRule(doc,RainType,RainDirection,maHasEffect);
		return doc1;
	}
	
	public  void RainInfer2(ArrayList<String> List,OWLModel model,String maName,String ieTopic) throws OntologyLoadException, SWRLRuleEngineException
	{
		if(hasEffect)
			return ;
		String[] RainList =new String[50];//�����洢��ȡ�����ķ��������ģ�����
		int RainFromInfoNumber=0;//���ճ�ȡ�ķ��ϵ��������
		int RainFromTemNumber=0;
		int RainFromTopNumber=0;
		String str = "p4:";
		ArrayList<String> topicList = new ArrayList<String>();
	
		Collection mapToRainValues = null;
		
		System.out.println("##RainInfer2####################ʵ����ִ�е�ģ�������######################");
		for(int i = 0; i < List.size(); i++){
			System.out.println(List.get(i));
		}
		System.out.println("##RainInfer2##########################################################");
		OWLIndividual maIndividual = model.getOWLIndividual(maName);//��ȡmayaʵ��	
		if(null == maIndividual){
			System.out.println("mayaʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ����ڣ�");
			return ;
		}
		
		//�õ��ĸ�������
//		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
		OWLObjectProperty addRainToMaProperty = model.getOWLObjectProperty(str + "addRainToMa");
		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//�˱��������жϳ���������������

		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
		if(null == plane){
			System.out.println("�޷���ȡʵ��" + maIndividual.getBrowserText() + "��hasValueOfPlace����(�����������жϳ����������ڻ�������)��so �ܾ����꣡����֪ʶ���и������Ƿ���ڻ���ȷ��");
			return ;
		}
		else {
			if(plane.getBrowserText().equals("InWaterDescription") || plane.getBrowserText().equals("inDoorDescription")) {
				System.out.println("�˳������ʺϼ��꣡");
				return ;
			}
		}
		//����ģ����Ϣ
		if(0 == RainFromTopNumber){
			System.out.println("===RainInfer2===============ģ�崦��====================");
			int listSize = 0;

			if(List.isEmpty()){
				System.out.println("û��ģ�崫��");
			} else {
				listSize = List.size();//ģ�����
			}
				
			if(listSize > 0) {
				System.out.println("���ݽ�����ģ�����Ϊ��" + listSize + "�����ֱ��ǣ�" + List);
				rainExecuteTemplateToBackgroundSceneSWRLEngine(model,List);
				
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
		}
		System.out.println("====RainInfer2============ģ�崦����ϣ�================");
		
		if(RainFromInfoNumber == 0){
			//����������Ϣ
			System.out.println("====RainInfer2============���������⴦��================");
			System.out.println("====RainInfer2============���ȴ���IE����================");
	//		ieTopic = doc.getRootElement().element("maName").attributeValue("topic");
			
			if(ieTopic.contains("Topic")){
				System.out.println("IE Topic = " + ieTopic);
				topicList.add(ieTopic);
				rainExecuteTopicToBackgroundSceneSWRLEngine(model,topicList);
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
			} else {
				System.out.println("====RainInfer2============IEδ��ȡ������================");
			}
	
			System.out.println("====RainInfer2============�������⴦�����================");
	
		}
				
		
		if(0 == RainFromInfoNumber) {
			System.out.println("��������������û�г�ȡ���ʺϵ���");
			return ;
		} else {
			hasEffect = true;
		}
	}
	
	public Document printRainRule(Document doc,String RainType,String RainDirection,boolean maHasEffect)
	{
		System.out.println("=====================��ʼ����xml-rule======================");
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
		System.out.println("xml-rule�������");
		return doc;
	}	
	
	/**
	 **================================= SnowInfo ����ִ�����ѩ=================================
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
		if(hasEffect)
			return doc;
		String[] SnowList =new String[50];//�����洢��ȡ�����ķ��������ģ���ѩ
		
		int SnowFromInfoNumber=0;//���ճ�ȡ�ķ��ϵ�ѩ������
		int SnowFromTemNumber=0;
		int SnowFromTopNumber=0;
		String str="p4:";
		String ieTopic = null;
		ArrayList<String> topicList = new ArrayList<String>();
	//	ArrayList<String> listSnow = new ArrayList<String>();
	//	ArrayList<String> effects = new ArrayList<String>();
		Collection mapToSnowValues = null;
		OWLIndividual maIndividual = model.getOWLIndividual(maName);//��ȡmayaʵ��
		
//		list ��ŵ��Ǵ��ݵ�ģ������⣬�ֶ������һ�δ�����һ����ߵ������ģ���Ƿ����topicAndTemplate�С�
/*		int number = 0;
		Random rand = new Random();
		for(int i = 0; i < List.size(); i++){
			String strTemp = List.get(i);
			effects.add(strTemp.split(":")[0]);
			if(!topicAndTemplate.contains(effects.get(i))){          //strTemp ������topicAndTemplate  �ԣ� �����������һ��ֵ
				number = rand.nextInt(10) + 1;
System.out.println("\n������ǣ�" + number);
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
				System.out.println("\n ��ѩ��ģ��");
			} else {
				System.out.println("\n ��ѩ��ģ��");
				return doc;
			}		
		} else {
			if(hasEffect) {
				return doc;
			}
		}
	*/	
		System.out.println("######################ʵ����ִ�е�ģ�������######################");
		for(int i = 0; i < List.size(); i++){
			System.out.println(List.get(i));
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
		
		//����ģ����Ϣ
		if(SnowFromInfoNumber == 0) {
			System.out.println("==================ģ�崦��====================");
			int listSize = 0;
			if(null != List)
				listSize = List.size();//ģ�����
			else
				System.out.println("δ���ݽ����κ�ģ��");
			
			if(listSize > 0) {
				System.out.println("���ݽ�����ģ�����Ϊ��" + listSize + "�����ֱ��ǣ�" + List);
				snowExecuteTemplateToBackgroundSceneSWRLEngine(model,List);
				
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
		
		if(SnowFromInfoNumber == 0){
			//����������Ϣ
			System.out.println("=================���������⴦��================");
			System.out.println("===============���ȴ���IE��ȡ������================");
			
			ieTopic = doc.getRootElement().element("maName").attributeValue("topic");
			if(ieTopic.contains("Topic")){
				System.out.println("IE Topic = " + ieTopic);
				topicList.add(ieTopic);
				snowExecuteTopicToBackgroundSceneSWRLEngine(model,topicList);
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
			}else {
				System.out.println("=================IEδ�������================");
			}
		
			System.out.println("================�������⴦�����================");
		}
			
		if(0 == SnowFromInfoNumber) {
			System.out.println("��������������û�г�ȡ���ʺϵ�ѩ");
			return doc;
		} else {
			hasEffect = true;
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
		Document doc1 = printSnowRule(doc,SnowType,SnowDirection,maHasEffect);
		return doc1;
	}
	
	public void SnowInfer2(ArrayList<String> List,OWLModel model,String maName,String ieTopic) throws OntologyLoadException, SWRLRuleEngineException
	{
		if(hasEffect)
			return ;
		String[] SnowList =new String[50];//�����洢��ȡ�����ķ��������ģ���ѩ
		
		int SnowFromInfoNumber=0;//���ճ�ȡ�ķ��ϵ�ѩ������
		int SnowFromTemNumber=0;
		int SnowFromTopNumber=0;
		String str="p4:";
	//	String ieTopic = null;
		ArrayList<String> topicList = new ArrayList<String>();

		Collection mapToSnowValues = null;
		OWLIndividual maIndividual = model.getOWLIndividual(maName);//��ȡmayaʵ��
		
		System.out.println("######################ʵ����ִ�е�ģ�������######################");
		for(int i = 0; i < List.size(); i++){
			System.out.println(List.get(i));
		}
		System.out.println("############################################################");
		if(null == maIndividual){
			System.out.println("mayaʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ����ڣ�");
			return ;
		}
		
		//�õ��ĸ�������
		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
		OWLObjectProperty addSnowToMaProperty = model.getOWLObjectProperty(str + "addSnowToMa");
		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");//�˱��������жϳ���������������
		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
		
		if(null == plane){
			System.out.println("�޷���ȡʵ��" + maIndividual.getBrowserText() + "��hasValueOfPlace����(�����������жϳ����������ڻ�������)��so �ܾ���ѩ������֪ʶ���и������Ƿ���ڻ���ȷ��");
			return ;
		}
		else {
			if(plane.getBrowserText().equals("InWaterDescription")||plane.getBrowserText().equals("inDoorDescription")) {
				System.out.println("�˳������ʺϼ�ѩ��");
				return ;
			}
		}
		
		//����ģ����Ϣ
		if(SnowFromInfoNumber == 0) {
			System.out.println("==================ģ�崦��====================");
			int listSize = 0;
			if(null != List)
				listSize = List.size();//ģ�����
			else
				System.out.println("δ���ݽ����κ�ģ��");
			
			if(listSize > 0) {
				System.out.println("���ݽ�����ģ�����Ϊ��" + listSize + "�����ֱ��ǣ�" + List);
				snowExecuteTemplateToBackgroundSceneSWRLEngine(model,List);
				
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
		
		if(SnowFromInfoNumber == 0){
			//����������Ϣ
			System.out.println("=================���������⴦��================");
			System.out.println("===============���ȴ���IE��ȡ������================");
			
		//	ieTopic = doc.getRootElement().element("maName").attributeValue("topic");
			if(ieTopic.contains("Topic")){
				System.out.println("IE Topic = " + ieTopic);
				topicList.add(ieTopic);
				snowExecuteTopicToBackgroundSceneSWRLEngine(model,topicList);
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
			}else {
				System.out.println("=================IEδ�������================");
			}
		
			System.out.println("================�������⴦�����================");
		}
			
		if(0 == SnowFromInfoNumber) {
			System.out.println("��������������û�г�ȡ���ʺϵ�ѩ");
			return ;
		} else {
			hasEffect = true;
		}
		
	}
	
	public Document printSnowRule(Document doc,String SnowType,String SnowDirection,boolean maHasEffect)
	{
		System.out.println("=====================��ʼ����xml-rule======================");
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
		System.out.println("xml-rule�������");
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
		System.out.println("1Ϊ�񶨣�0Ϊ�϶����񶨵�ֵΪ:"+ifNeg);
		//effect = hasEffects(effects);
		effect = hasEffects(effects,model,maName);  //effct Ϊ����������ͬ����Ч
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
		
		
		

		File file = new File("F:/ʵ�����ĵ�/ADL/Effect/test.xml");
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(file);
		Effect  effect = new Effect();
		
		System.out.println("��ʼ!");
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
		XMLWriter writer = new XMLWriter(new FileWriter("F:/ʵ�����ĵ�/ADL/Effect/testSnow.xml"));
		writer.write(document1); 
		Effect effect2 = new Effect();
		if (effect2.IsWeather(aList, model, "pingpong.ma","TennisActionTopic"))
			System.out.println("true");
		else
			 System.out.println("false");;
		
	//	System.out.println("snow number:" + snow.getSnow());
		writer.close();
		System.out.println("������");
	}

}
