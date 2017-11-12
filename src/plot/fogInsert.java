package plot;

import java.io.File;
import java.io.FileWriter;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.DocumentException;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import edu.stanford.smi.protege.exception.OntologyLoadException;
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

public class fogInsert {

	private int fog = 0;
	private boolean hasFog = false;// �Ƿ���"FogTemplate"

	public int getFog() {
		return fog;
	}

	public void setFog(int fog) {
		this.fog = fog;
	}

	private final OWLIndividual Null = null;
	static Logger logger = Logger.getLogger(fogInsert.class.getName());

	// ������������������������Ϲ�����İ���
	public static SWRLFactory createSWRLFactory(OWLModel model) {
		SWRLFactory factory = new SWRLFactory(model);
		return factory;
	}

	// ��������ִ�������������Ϲ�����İ���
	public SWRLRuleEngine createRuleEngine(OWLModel model)throws SWRLRuleEngineException {
		SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create("SWRLJessBridge", model);
		// SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create(model);
		return ruleEngine;
	}

	// ִ��OWL��Ĺ���
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
				if (imp.getLocalName().contains("addFogToSceneRule")) // �ҵ���ΪaddFogToSceneRule�Ĺ���
				{// DayTimeTemplate(?d) �� hasModelFromTemplate(?d, ?x) ��
					// AnimationScene(?y) �� hasSceneSpace(?y, ?z) ��
					// PlaneSceneSpaceUponWater(?z) �� addToMa(?y, ?x) ��
					// usedSpaceInMa(?y, ?z) �� hasPutObjectInSpace(?z, ?x)
					for (Iterator<String> its = templateName.iterator(); its.hasNext();) // �õ���������ִ��addFogToSceneRule����
					{
						String templateValue = its.next();
						String templateValue1 = templateValue.replaceAll("Individual", "");
						// templateValue =
						// "p2:"+templateValue.replace(":","(p2:")+
						// ")";//��ȡָ��ģ���addFogToSceneRule����
						// logger.info("���еĹ�������Ϊ��"+imp.getLocalName());
						if (imp.getBody().getBrowserText().contains(templateValue1))// ���ҵ���templateValue��ͷ�Ĺ���
						{
							logger.info("���еĹ�������Ϊ��" + imp.getLocalName());
							System.out.println("����" + imp.getLocalName());
							imp.enable();// ִ�д˹�����������г�����ӹ�����Ľ��
						}
					}
				}
			}
		}
		ruleEngine.infer();
	}

	// ִ��OWL��Ĺ���
	public void executeTemplateToBackgroundSceneSWRLEngine(OWLModel model,
			ArrayList<String> templateName) throws SWRLRuleEngineException {
		SWRLRuleEngine ruleEngine = createRuleEngine(model);
		ruleEngine.reset();
		SWRLFactory factory = createSWRLFactory(model);
		factory.disableAll();
		Iterator<SWRLImp> iter = factory.getImps().iterator();
		while (iter.hasNext()) {
			SWRLImp imp = (SWRLImp) iter.next();
			if (templateName.size() != 0) {
				if (imp.getLocalName().contains("addFogToSceneRule")) // �ҵ����ְ�����addFogToSceneRule���Ĺ���
				{
					for (Iterator<String> its = templateName.iterator(); its.hasNext();) // �õ���������ִ��addFogToSceneRule����
					{
						String templateValue = its.next();
						// String templateValue1="p2:"+templateValue;
						String templateValue1 = templateValue.substring(0,templateValue.indexOf(":"));
						if (imp.getBody().getBrowserText().contains(templateValue1)) {
							logger.info("���еĹ�������Ϊ��" + imp.getLocalName());
							System.out.println("����" + imp.getLocalName());
							imp.enable();// ִ�д˹���
						}
					}
				}
			}
		}
		ruleEngine.infer();
	}

	public Document fogInfer(ArrayList<String> list, OWLModel model,
			String maName, Document doc) throws OntologyLoadException,
			SWRLRuleEngineException {// listΪģ�壬
		String str = "p4:";
		/*
		 * ��ȡmaʵ��
		 */
		System.out.println("=================================ZJL1 start start start=================================\n");
		System.out.println("���ݹ�����ģ�����Ϊ:" + list.size() + "�����ǣ�" + list);//20161130
		logger.info("fogfogfog start ���ݹ�����ģ�����Ϊ:" + list.size() + "�����ǣ�" + list+" ma is"+maName);
		OWLIndividual maIndividual = model.getOWLIndividual(maName);
		/*
		 * �õ��ĸ�������
		 */
		OWLObjectProperty hasTopicProperty = model.getOWLObjectProperty("hasTopic");//��ȡ��������
		OWLObjectProperty addFogToMaProperty = model.getOWLObjectProperty(str+ "addFogToMa");
		OWLDatatypeProperty hasColor = model.getOWLDatatypeProperty(str+ "hasColor");//��ȡ��������
		OWLDatatypeProperty colorBLeast = model.getOWLDatatypeProperty(str+ "colorBLeast");
		OWLDatatypeProperty colorBMost = model.getOWLDatatypeProperty(str+ "colorBMost");
		OWLDatatypeProperty colorGLeast = model.getOWLDatatypeProperty(str+ "colorGLeast");
		OWLDatatypeProperty colorGMost = model.getOWLDatatypeProperty(str+ "colorGMost");
		OWLDatatypeProperty colorRLeast = model.getOWLDatatypeProperty(str+ "colorRLeast");
		OWLDatatypeProperty colorRMost = model.getOWLDatatypeProperty(str+ "colorRMost");
		OWLDatatypeProperty densityLeast = model.getOWLDatatypeProperty(str+ "densityLeast");
		OWLDatatypeProperty densityMost = model.getOWLDatatypeProperty(str+ "densityMost");
		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");
		OWLObjectProperty hasFogColor = model.getOWLObjectProperty(str+ "hasFogColor");
		/*
		 * �����ж�
		 */
		logger.info("hasTopicProperty="+hasTopicProperty+" addFogToMaProperty="+addFogToMaProperty);
		for (Iterator<String> iter = list.iterator(); iter.hasNext();) {
			String s = iter.next();
			if (s.contains("FogTemplate"))
				hasFog = true;
		}

		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
		if (plane == null) {
			System.out.println("�޷���ȡ����ʵ����hasValueOfPlace����ֵ������owl���Ƿ���ȷ��ǣ�");
			logger.info("�޷���ȡ����ʵ����hasValueOfPlace����ֵ������owl���Ƿ���ȷ��ǣ�");
			return doc;
		}
		if (plane.getBrowserText().equals("InWaterDescription")
				|| plane.getBrowserText().equals("inDoorDescription")) {
			System.out.println("�˳������ʺϼ���");
			logger.info("�˳������ʺϼ���");
			return doc;
		}
		/*
		 * ����ȡ������Ϣ
		 */
		Collection mapToFogValues = null;
		String[] fogList = new String[50];// �����洢��ȡ�����ķ��������ģ�����
		int fogFromInfoNumber = 0;// ���ճ�ȡ�ķ��ϵ��������
		int fogFromTemNumber = 0;
		int fogFromTopNumber = 0;
		float isvalue1 = (float) 0;
		float isvalue2 = (float) 0;
		float isvalue3 = (float) 0;
		float isvalue4 = (float) 0;
		String fogcolor = "";
		System.out.println("=================================ZJL1 start=================================\n");
		/*
		 * ����ģ����Ϣ
		 */
		if (fogFromInfoNumber == 0) {
			System.out.println("---------------���ȴ���ģ��----------------");
			logger.info("---------------���ȴ���ģ��----------------");
			int listSize = 0;
			if (list != null)
				listSize = list.size();// ģ�����
			if(0==list.size()){
				System.out.println("δ���ݽ����κ�ģ��");
				logger.info("δ���ݽ����κ�ģ��");
			}
			if (listSize > 0) {
				System.out.println("���ݹ�����ģ�����Ϊ:" + listSize + "�����ǣ�" + list);
				executeTemplateToBackgroundSceneSWRLEngine(model, list);
				// ȥ��Ӧ�ĳ������ȡ����ӵ���
				mapToFogValues = maIndividual.getPropertyValues(addFogToMaProperty);
				if (mapToFogValues.isEmpty())
					System.out.println("���ݴ��ݹ�����ģ�壬û�����Ӧ����");
				else {
					for (Iterator it1 = mapToFogValues.iterator(); it1.hasNext();) {
						OWLIndividual fogIndiviual = (OWLIndividual) it1.next();
						fogList[fogFromInfoNumber] = fogIndiviual.getBrowserText();
						fogFromInfoNumber++;
						fogFromTemNumber++;
					}
					if (fogFromInfoNumber != 0) {
						System.out.print("����ģ���ȡ��" + fogFromInfoNumber+ "�����ǣ�");
						logger.info("����ģ���ȡ��" + fogFromInfoNumber+ "�����ǣ�");
						for (int i = 0; i < fogFromInfoNumber; i++){
							System.out.print(fogList[i] + " ");
							logger.info(fogList[i] + " ");
						}
						System.out.println();
					}
				}
			}
			System.out.println("---------------ģ�崦����ϣ�-------------------");
		}

		if (fogFromInfoNumber == 0) {
			System.out.println("---------------����ģ��δ��ȡ������ʼ��������---------------\n");
			logger.info("---------------����ģ��δ��ȡ������ʼ��������---------------");
			/*
			 * ����IE��ȡ������Ϣ
			 */
			String ieTopic = doc.getRootElement().element("maName").attributeValue("topic");// ��ȡIE ����
			if (ieTopic.contains("Topic")) {
				System.out.println("---------------����IE���⴦��---------------");
				ArrayList topicList = new ArrayList();
				topicList.add(ieTopic);
				logger.info("IE topic topicList "+topicList);
				executeTopicToBackgroundSceneSWRLEngine(model, topicList);
			} else {
				Collection topics = maIndividual.getPropertyValues(hasTopicProperty);
				if (!topics.isEmpty()) {
					System.out.println("---------------IEδ��ȡ�����⣬������������---------------");
					logger.info("---------------IEδ��ȡ�����⣬������������---------------");
					OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);// ��topicsת��Ϊ����
					ArrayList topicList = new ArrayList();
					for (int i = 0; i < IdTopics.length; i++) {
						topicList.add(IdTopics[i].getBrowserText());
					}
					logger.info("MA topic topicList "+topicList);
					executeTopicToBackgroundSceneSWRLEngine(model, topicList);
				}
			}
			mapToFogValues = maIndividual.getPropertyValues(addFogToMaProperty);
			if (mapToFogValues.isEmpty()){
				System.out.println("��������û�����Ӧ����");
				logger.info("��������û�����Ӧ����");
			}
			else {
				for (Iterator it1 = mapToFogValues.iterator(); it1.hasNext();) {
					OWLIndividual fogIndiviual = (OWLIndividual) it1.next();
					fogList[fogFromInfoNumber] = fogIndiviual.getBrowserText();
					fogFromInfoNumber++;
					fogFromTopNumber++;
				}
				if (fogFromInfoNumber != 0) {
					logger.info("���������ȡ��" + fogFromInfoNumber + "�����ֱ��ǣ�");
					
					System.out.print("���������ȡ��" + fogFromInfoNumber + "�����ֱ��ǣ�");
					for (int i = 0; i < fogFromInfoNumber; i++){
						System.out.print(fogList[i] + " ");
						logger.info(fogList[i] +" ");
					}
					System.out.println();
					System.out.println("���������ȣ���ģ�岻������");
				}
			}
			System.out.println("---------------�������⴦����ϣ�---------------");
		}

		if (0 == fogFromInfoNumber) {
			System.out.println("ģ������⴦����ϣ�û�к��ʵ���");
			logger.info("ģ������⴦����ϣ�û�к��ʵ���");
			return doc;
		}

		// �ڻ�ȡ�������������ѡȡһ��
		int rd = (int) (Math.random() * fogFromInfoNumber);
		OWLIndividual fogIndividual = model.getOWLIndividual(fogList[rd]);//��ȡʵ��
		System.out.println("���ѡȡ������Ϊ:" + fogIndividual.getBrowserText());
		OWLIndividual colorIndividual = (OWLIndividual) fogIndividual.getPropertyValue(hasFogColor);
		System.out.println("hasfogcolor��"+hasFogColor.toString());
		// ��ȡ����ʵ��������
		Float isvalueB1, isvalueB2, isvalueG1, isvalueG2, isvalueR1, isvalueR2, isvalueD1, isvalueD2;
		/*
		 * isvalueB1 = (Float) fogIndividual.getPropertyValue(colorBLeast);
		 * isvalueB2 = (Float) fogIndividual.getPropertyValue(colorBMost);
		 * isvalueG1 = (Float) fogIndividual.getPropertyValue(colorGLeast);
		 * isvalueG2 = (Float) fogIndividual.getPropertyValue(colorGMost);
		 * isvalueR1 = (Float) fogIndividual.getPropertyValue(colorRLeast);
		 * isvalueR2 = (Float) fogIndividual.getPropertyValue(colorRMost);
		 * isvalueD1 = (Float) fogIndividual.getPropertyValue(densityLeast);
		 * isvalueD2 = (Float) fogIndividual.getPropertyValue(densityMost);
		 * fogcolor=(String) fogIndividual.getPropertyValue(hasColor);
		 */
		isvalueB1 = (Float) colorIndividual.getPropertyValue(colorBLeast);
		isvalueB2 = (Float) colorIndividual.getPropertyValue(colorBMost);
		isvalueG1 = (Float) colorIndividual.getPropertyValue(colorGLeast);
		isvalueG2 = (Float) colorIndividual.getPropertyValue(colorGMost);
		isvalueR1 = (Float) colorIndividual.getPropertyValue(colorRLeast);
		isvalueR2 = (Float) colorIndividual.getPropertyValue(colorRMost);
		isvalueD1 = 9*(Float) colorIndividual.getPropertyValue(densityLeast);
		isvalueD2 = 9*(Float) colorIndividual.getPropertyValue(densityMost);//20161229,�����Ũ�ȱ��
		System.out.println("densityLeast="+densityLeast.toString()+" Least"+isvalueD1+" most"+isvalueD2);
		fogcolor = (String) colorIndividual.getPropertyValue(hasColor);
		if ((fogcolor.equals("blue") || fogcolor.equals("pink")
				|| fogcolor.equals("yellow") || fogcolor.equals("green") || fogcolor
					.equals("purple")) && !randomFog()) {
			// �����ȡ����֮�󣬻�Ҫ�������������жϲſ��������Ч
			System.out.println("���������������");
			logger.info("���������������");
			System.out.println("=================================ZJL1 end=================================\n");
			return doc;
		} else {
			isvalue1 = randomNumber1(isvalueB1, isvalueB2);
			isvalue2 = randomNumber1(isvalueG1, isvalueG2);
			isvalue3 = randomNumber1(isvalueR1, isvalueR2);
			isvalue4 = randomNumber2(isvalueD1, isvalueD2);
			System.out.println("������ֵ�ֱ�ΪisvalueB" + isvalue1 + " isvalueG" + isvalue2 + " isvalueR"+ isvalue3 + " isvalueD" + isvalue4 + "����ʵ����Ϣ��ȡ���");// �Ķ��� ���ߵ¾�
			logger.info("������ֵ�ֱ�Ϊ" + isvalue1 + " " + isvalue2 + " "+ isvalue3 + " " + isvalue4 + "����ʵ����Ϣ��ȡ���");
			Document doc1 = printFogRule(doc, isvalue4, fogcolor, isvalue3,isvalue2, isvalue1);
			System.out.println("=================================ZJL1 end=================================\n");
			return doc1;
		}
	}

	public boolean randomFog() {
		if (hasFog) {
			System.out.println("hasFog" + hasFog);
			return true;// �������ģ�壬��������
		}
		Random ra = new Random();
		int a = ra.nextInt(3) + 1;
		System.out.println("������ɵ�����aΪ��" + a);
		if (a == 3)
			return true;
		else
			return false;
	}

	public Document printFogRule(Document doc, Float fogDen, String fogC,Float hasR, Float hasG, Float hasB) {
		System.out.println("��ʼ����xml��rule");
		/*
		 * ��ȡdoc�ĸ��ڵ�
		 */
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "addFogToMa");
		ruleName.addAttribute("type", "fog");
		ruleName.addAttribute("fogDensity", Float.toString(fogDen));
		ruleName.addAttribute("fogColor", fogC);
		ruleName.addAttribute("colorR", Float.toString(hasR));
		ruleName.addAttribute("colorG", Float.toString(hasG));
		ruleName.addAttribute("colorB", Float.toString(hasB));
		System.out.println("xml��rule�������");
		return doc;
	}

	public float randomNumber1(float x, float y) {
		int x1 = (int) (x * 100);
		int y1 = (int) (y * 100);
		int z1 = y1 - x1;
		if (z1 == 0)
			return x;
		Random num = new Random();
		int p = num.nextInt(z1);
		int z = x1 + p;
		return (float) (z / 100.00);//100
	}

	public float randomNumber2(float x, float y) {
		int x1 = (int) (x * 1000);
		int y1 = (int) (y * 1000);
		int z1 = y1 - x1;
		if (z1 == 0)
			return x;
		Random num = new Random();
		int p = num.nextInt(z1);
		int z = x1 + p;
		return (float) (z / 1000.000);//1000
	}

	/**
	 * fogֵ
	 * */
	public void fogInfer2(ArrayList<String> list, OWLModel model, String maName)
			throws OntologyLoadException, SWRLRuleEngineException {
		int fogFromInfoNumber = 0;// ���ճ�ȡ�ķ��ϵ��������
		int fogFromTemNumber = 0;
		int fogFromTopNumber = 0;
		String str = "p4:";
		System.out.println("=================================ZJL2 start=================================\n");
		/*
		 * ��ȡmaʵ��
		 */
		OWLIndividual maIndividual = model.getOWLIndividual(maName);
		/*
		 * �õ��ĸ�������
		 */
		OWLObjectProperty hasTopicProperty = model.getOWLObjectProperty("hasTopic");
		OWLObjectProperty addFogToMaProperty = model.getOWLObjectProperty(str+ "addFogToMa");
		OWLDatatypeProperty hasColor = model.getOWLDatatypeProperty(str+ "hasColor");
		OWLDatatypeProperty colorBLeast = model.getOWLDatatypeProperty(str+ "colorBLeast");
		OWLDatatypeProperty colorBMost = model.getOWLDatatypeProperty(str+ "colorBMost");
		OWLDatatypeProperty colorGLeast = model.getOWLDatatypeProperty(str+ "colorGLeast");
		OWLDatatypeProperty colorGMost = model.getOWLDatatypeProperty(str+ "colorGMost");
		OWLDatatypeProperty colorRLeast = model.getOWLDatatypeProperty(str+ "colorRLeast");
		OWLDatatypeProperty colorRMost = model.getOWLDatatypeProperty(str+ "colorRMost");
		OWLDatatypeProperty densityLeast = model.getOWLDatatypeProperty(str+ "densityLeast");
		OWLDatatypeProperty densityMost = model.getOWLDatatypeProperty(str+ "densityMost");
		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");
		/*
		 * �����ж�
		 */
		
		OWLIndividual plane = (OWLIndividual) maIndividual
				.getPropertyValue(hasValueOfPlane);
		if (plane != Null) {
			System.out.println("����λ��(���ڣ�����): " + plane.getBrowserText());
			if (plane.getBrowserText().equals("InWaterDescription")
					|| plane.getBrowserText().equals("inDoorDescription")) {
				return;
			}
		}
		/*
		 * ����ȡ������Ϣ
		 */
		String[] fogList = new String[50];// �����洢��ȡ�����ķ��������ģ�����

		float isvalue1 = (float) 0;
		float isvalue2 = (float) 0;
		float isvalue3 = (float) 0;
		float isvalue4 = (float) 0;
		String fogcolor = "";
		Collection mapToFogValues = null;
		if (fogFromInfoNumber == 0) {
			System.out.println("...........ģ�崦��............");
			if (list.size() > 0) {
				executeTemplateToBackgroundSceneSWRLEngine(model, list);
				// ȥ��Ӧ�ĳ������ȡ����ӵ���
				mapToFogValues = maIndividual
						.getPropertyValues(addFogToMaProperty);
				if (mapToFogValues.isEmpty())
					fogFromInfoNumber = 0;
				else {
					for (Iterator it1 = mapToFogValues.iterator(); it1.hasNext();) {
						OWLIndividual fogIndiviual = (OWLIndividual) it1.next();
						fogList[fogFromInfoNumber] = fogIndiviual.getBrowserText();
						fogFromInfoNumber++;
						fogFromTemNumber++;
					}
				}
			}
			// setFog(fogFromInfoNumber);
			System.out.println("ģ�崦����ϣ�-------------------");
		}

		if (fogFromInfoNumber == 0) {
			System.out.println("�������⴦��---------------");
			Collection topics = maIndividual.getPropertyValues(hasTopicProperty);
			if (!topics.isEmpty()) {
				OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);// ��topicsת��Ϊ����
				ArrayList topicList = new ArrayList();
				for (int i = 0; i < IdTopics.length; i++) {
					topicList.add(IdTopics[i].getBrowserText());
				}
				int topicSize = topicList.size();
				executeTopicToBackgroundSceneSWRLEngine(model, topicList);
				mapToFogValues = maIndividual
						.getPropertyValues(addFogToMaProperty);
				if (mapToFogValues.isEmpty())
					fogFromInfoNumber = 0;
				else {
					for (Iterator it1 = mapToFogValues.iterator(); it1.hasNext();) {
						OWLIndividual fogIndiviual = (OWLIndividual) it1.next();
						fogList[fogFromInfoNumber] = fogIndiviual.getBrowserText();
						fogFromInfoNumber++;
						fogFromTopNumber++;
					}

				}
			}
		}
		System.out.println("�������⴦����ϣ�---------------");
		setFog(fogFromInfoNumber);
		System.out.println("=================================ZJL2 end=================================\n");
	}

	public static void main(String[] args) throws ParserConfigurationException,
			SAXException, IOException, JDOMException, DocumentException,
			OntologyLoadException, SWRLRuleEngineException {
		// String owlpath =
		// "file:///C://ontologyOWL//AllOwlFile//sumoOWL2//sumo_phone3.owl";
		String owlpath = "file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";

		OWLModel model = ProtegeOWL.createJenaOWLModelFromURI(owlpath);
		ArrayList<String> aList = new ArrayList<String>();
		/*
		 * aList.add("GladnessTemplate:gladnessTemplate");
		 * aList.add("FlowerTemplate:roseTemplate");
		 * aList.add("FogTemplate:fogTemplate");
		 */
		aList.add("FogTemplate:fogTemplate:0");
		aList.add("SadTemplate:sadTemplate");
		aList.add("WinterTemplate:winterTemplate:0");
		
		/* aList.add("p4:PurpleThinFog"); 
		 aList.add("p4:WhiteThickFog");*/
		 
		// // //File file = new File("F:\\test.xml");
		File file = new File("F:/ʵ�����ĵ�/ADL/Fog/test.xml");
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(file);
		fogInsert tt = new fogInsert();
		System.out.println("��ʼ");
		Document document1 = tt.fogInfer(aList, model, "shock_storm.ma",
				document);
		XMLWriter writer = new XMLWriter(new FileWriter(
				"F:/ʵ�����ĵ�/ADL/Fog/testfog.xml"));
		writer.write(document1);
		System.out.println("����");

		fogInsert fog = new fogInsert();
		fog.fogInfer2(aList, model, "shock_storm.ma");
		System.out.println("fog num = " + fog.getFog());
		writer.close();
	}

}
