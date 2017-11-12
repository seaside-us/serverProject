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
	private boolean hasFog = false;// 是否有"FogTemplate"

	public int getFog() {
		return fog;
	}

	public void setFog(int fog) {
		this.fog = fog;
	}

	private final OWLIndividual Null = null;
	static Logger logger = Logger.getLogger(fogInsert.class.getName());

	// 创建规则解析工厂（这个是我瞎胡理解的啊）
	public static SWRLFactory createSWRLFactory(OWLModel model) {
		SWRLFactory factory = new SWRLFactory(model);
		return factory;
	}

	// 创建规则执行器（这个是我瞎胡理解的啊）
	public SWRLRuleEngine createRuleEngine(OWLModel model)throws SWRLRuleEngineException {
		SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create("SWRLJessBridge", model);
		// SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create(model);
		return ruleEngine;
	}

	// 执行OWL里的规则
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
				if (imp.getLocalName().contains("addFogToSceneRule")) // 找到名为addFogToSceneRule的规则
				{// DayTimeTemplate(?d) ∧ hasModelFromTemplate(?d, ?x) ∧
					// AnimationScene(?y) ∧ hasSceneSpace(?y, ?z) ∧
					// PlaneSceneSpaceUponWater(?z) → addToMa(?y, ?x) ∧
					// usedSpaceInMa(?y, ?z) ∧ hasPutObjectInSpace(?z, ?x)
					for (Iterator<String> its = templateName.iterator(); its.hasNext();) // 用迭代器逐条执行addFogToSceneRule规则
					{
						String templateValue = its.next();
						String templateValue1 = templateValue.replaceAll("Individual", "");
						// templateValue =
						// "p2:"+templateValue.replace(":","(p2:")+
						// ")";//获取指定模板的addFogToSceneRule规则
						// logger.info("运行的规则名字为："+imp.getLocalName());
						if (imp.getBody().getBrowserText().contains(templateValue1))// 再找到以templateValue开头的规则
						{
							logger.info("运行的规则名字为：" + imp.getLocalName());
							System.out.println("规则" + imp.getLocalName());
							imp.enable();// 执行此规则，它会给所有场景添加规则里的结果
						}
					}
				}
			}
		}
		ruleEngine.infer();
	}

	// 执行OWL里的规则
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
				if (imp.getLocalName().contains("addFogToSceneRule")) // 找到名字包含“addFogToSceneRule”的规则
				{
					for (Iterator<String> its = templateName.iterator(); its.hasNext();) // 用迭代器逐条执行addFogToSceneRule规则
					{
						String templateValue = its.next();
						// String templateValue1="p2:"+templateValue;
						String templateValue1 = templateValue.substring(0,templateValue.indexOf(":"));
						if (imp.getBody().getBrowserText().contains(templateValue1)) {
							logger.info("运行的规则名字为：" + imp.getLocalName());
							System.out.println("规则" + imp.getLocalName());
							imp.enable();// 执行此规则
						}
					}
				}
			}
		}
		ruleEngine.infer();
	}

	public Document fogInfer(ArrayList<String> list, OWLModel model,
			String maName, Document doc) throws OntologyLoadException,
			SWRLRuleEngineException {// list为模板，
		String str = "p4:";
		/*
		 * 获取ma实例
		 */
		System.out.println("=================================ZJL1 start start start=================================\n");
		System.out.println("传递过来的模板个数为:" + list.size() + "个，是：" + list);//20161130
		logger.info("fogfogfog start 传递过来的模板个数为:" + list.size() + "个，是：" + list+" ma is"+maName);
		OWLIndividual maIndividual = model.getOWLIndividual(maName);
		/*
		 * 用到的各种属性
		 */
		OWLObjectProperty hasTopicProperty = model.getOWLObjectProperty("hasTopic");//获取对象属性
		OWLObjectProperty addFogToMaProperty = model.getOWLObjectProperty(str+ "addFogToMa");
		OWLDatatypeProperty hasColor = model.getOWLDatatypeProperty(str+ "hasColor");//获取数据属性
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
		 * 先做判断
		 */
		logger.info("hasTopicProperty="+hasTopicProperty+" addFogToMaProperty="+addFogToMaProperty);
		for (Iterator<String> iter = list.iterator(); iter.hasNext();) {
			String s = iter.next();
			if (s.contains("FogTemplate"))
				hasFog = true;
		}

		OWLIndividual plane = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlane);
		if (plane == null) {
			System.out.println("无法获取场景实例的hasValueOfPlace属性值，请检查owl里是否正确标记！");
			logger.info("无法获取场景实例的hasValueOfPlace属性值，请检查owl里是否正确标记！");
			return doc;
		}
		if (plane.getBrowserText().equals("InWaterDescription")
				|| plane.getBrowserText().equals("inDoorDescription")) {
			System.out.println("此场景不适合加雾！");
			logger.info("此场景不适合加雾！");
			return doc;
		}
		/*
		 * 最后抽取到的信息
		 */
		Collection mapToFogValues = null;
		String[] fogList = new String[50];// 用来存储抽取出来的符合主题和模板的雾
		int fogFromInfoNumber = 0;// 最终抽取的符合的雾的数量
		int fogFromTemNumber = 0;
		int fogFromTopNumber = 0;
		float isvalue1 = (float) 0;
		float isvalue2 = (float) 0;
		float isvalue3 = (float) 0;
		float isvalue4 = (float) 0;
		String fogcolor = "";
		System.out.println("=================================ZJL1 start=================================\n");
		/*
		 * 处理模板信息
		 */
		if (fogFromInfoNumber == 0) {
			System.out.println("---------------首先处理模板----------------");
			logger.info("---------------首先处理模板----------------");
			int listSize = 0;
			if (list != null)
				listSize = list.size();// 模板个数
			if(0==list.size()){
				System.out.println("未传递进来任何模板");
				logger.info("未传递进来任何模板");
			}
			if (listSize > 0) {
				System.out.println("传递过来的模板个数为:" + listSize + "个，是：" + list);
				executeTemplateToBackgroundSceneSWRLEngine(model, list);
				// 去相应的场景里获取所添加的雾
				mapToFogValues = maIndividual.getPropertyValues(addFogToMaProperty);
				if (mapToFogValues.isEmpty())
					System.out.println("根据传递过来的模板，没有相对应的雾");
				else {
					for (Iterator it1 = mapToFogValues.iterator(); it1.hasNext();) {
						OWLIndividual fogIndiviual = (OWLIndividual) it1.next();
						fogList[fogFromInfoNumber] = fogIndiviual.getBrowserText();
						fogFromInfoNumber++;
						fogFromTemNumber++;
					}
					if (fogFromInfoNumber != 0) {
						System.out.print("根据模板抽取到" + fogFromInfoNumber+ "个雾，是：");
						logger.info("根据模板抽取到" + fogFromInfoNumber+ "个雾，是：");
						for (int i = 0; i < fogFromInfoNumber; i++){
							System.out.print(fogList[i] + " ");
							logger.info(fogList[i] + " ");
						}
						System.out.println();
					}
				}
			}
			System.out.println("---------------模板处理完毕！-------------------");
		}

		if (fogFromInfoNumber == 0) {
			System.out.println("---------------根据模板未抽取到雾，开始处理主题---------------\n");
			logger.info("---------------根据模板未抽取到雾，开始处理主题---------------");
			/*
			 * 处理IE抽取主题信息
			 */
			String ieTopic = doc.getRootElement().element("maName").attributeValue("topic");// 获取IE 主题
			if (ieTopic.contains("Topic")) {
				System.out.println("---------------处理IE主题处理---------------");
				ArrayList topicList = new ArrayList();
				topicList.add(ieTopic);
				logger.info("IE topic topicList "+topicList);
				executeTopicToBackgroundSceneSWRLEngine(model, topicList);
			} else {
				Collection topics = maIndividual.getPropertyValues(hasTopicProperty);
				if (!topics.isEmpty()) {
					System.out.println("---------------IE未抽取到主题，处理场景的主题---------------");
					logger.info("---------------IE未抽取到主题，处理场景的主题---------------");
					OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);// 将topics转换为数组
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
				System.out.println("根据主题没有相对应的雾");
				logger.info("根据主题没有相对应的雾");
			}
			else {
				for (Iterator it1 = mapToFogValues.iterator(); it1.hasNext();) {
					OWLIndividual fogIndiviual = (OWLIndividual) it1.next();
					fogList[fogFromInfoNumber] = fogIndiviual.getBrowserText();
					fogFromInfoNumber++;
					fogFromTopNumber++;
				}
				if (fogFromInfoNumber != 0) {
					logger.info("根据主题抽取到" + fogFromInfoNumber + "个雾，分别是：");
					
					System.out.print("根据主题抽取到" + fogFromInfoNumber + "个雾，分别是：");
					for (int i = 0; i < fogFromInfoNumber; i++){
						System.out.print(fogList[i] + " ");
						logger.info(fogList[i] +" ");
					}
					System.out.println();
					System.out.println("因主题优先，故模板不做处理");
				}
			}
			System.out.println("---------------场景主题处理完毕！---------------");
		}

		if (0 == fogFromInfoNumber) {
			System.out.println("模板和主题处理完毕，没有合适的雾");
			logger.info("模板和主题处理完毕，没有合适的雾");
			return doc;
		}

		// 在获取到的雾里面随机选取一个
		int rd = (int) (Math.random() * fogFromInfoNumber);
		OWLIndividual fogIndividual = model.getOWLIndividual(fogList[rd]);//获取实体
		System.out.println("随机选取到的雾为:" + fogIndividual.getBrowserText());
		OWLIndividual colorIndividual = (OWLIndividual) fogIndividual.getPropertyValue(hasFogColor);
		System.out.println("hasfogcolor："+hasFogColor.toString());
		// 读取该雾实例的属性
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
		isvalueD2 = 9*(Float) colorIndividual.getPropertyValue(densityMost);//20161229,将雾的浓度变大
		System.out.println("densityLeast="+densityLeast.toString()+" Least"+isvalueD1+" most"+isvalueD2);
		fogcolor = (String) colorIndividual.getPropertyValue(hasColor);
		if ((fogcolor.equals("blue") || fogcolor.equals("pink")
				|| fogcolor.equals("yellow") || fogcolor.equals("green") || fogcolor
					.equals("purple")) && !randomFog()) {
			// 随机抽取到雾之后，还要根据其他条件判断才可以添加特效
			System.out.println("随机结果，不添加雾！");
			logger.info("随机结果，不添加雾！");
			System.out.println("=================================ZJL1 end=================================\n");
			return doc;
		} else {
			isvalue1 = randomNumber1(isvalueB1, isvalueB2);
			isvalue2 = randomNumber1(isvalueG1, isvalueG2);
			isvalue3 = randomNumber1(isvalueR1, isvalueR2);
			isvalue4 = randomNumber2(isvalueD1, isvalueD2);
			System.out.println("各属性值分别为isvalueB" + isvalue1 + " isvalueG" + isvalue2 + " isvalueR"+ isvalue3 + " isvalueD" + isvalue4 + "，雾实例信息获取完毕");// 改动过 告诉德娟
			logger.info("各属性值分别为" + isvalue1 + " " + isvalue2 + " "+ isvalue3 + " " + isvalue4 + "，雾实例信息获取完毕");
			Document doc1 = printFogRule(doc, isvalue4, fogcolor, isvalue3,isvalue2, isvalue1);
			System.out.println("=================================ZJL1 end=================================\n");
			return doc1;
		}
	}

	public boolean randomFog() {
		if (hasFog) {
			System.out.println("hasFog" + hasFog);
			return true;// 存在雾的模板，必须有雾
		}
		Random ra = new Random();
		int a = ra.nextInt(3) + 1;
		System.out.println("随机生成的数字a为：" + a);
		if (a == 3)
			return true;
		else
			return false;
	}

	public Document printFogRule(Document doc, Float fogDen, String fogC,Float hasR, Float hasG, Float hasB) {
		System.out.println("开始生成xml—rule");
		/*
		 * 获取doc的根节点
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
		System.out.println("xml—rule生成完毕");
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
	 * fog值
	 * */
	public void fogInfer2(ArrayList<String> list, OWLModel model, String maName)
			throws OntologyLoadException, SWRLRuleEngineException {
		int fogFromInfoNumber = 0;// 最终抽取的符合的雾的数量
		int fogFromTemNumber = 0;
		int fogFromTopNumber = 0;
		String str = "p4:";
		System.out.println("=================================ZJL2 start=================================\n");
		/*
		 * 获取ma实例
		 */
		OWLIndividual maIndividual = model.getOWLIndividual(maName);
		/*
		 * 用到的各种属性
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
		 * 先做判断
		 */
		
		OWLIndividual plane = (OWLIndividual) maIndividual
				.getPropertyValue(hasValueOfPlane);
		if (plane != Null) {
			System.out.println("场景位置(室内？室外): " + plane.getBrowserText());
			if (plane.getBrowserText().equals("InWaterDescription")
					|| plane.getBrowserText().equals("inDoorDescription")) {
				return;
			}
		}
		/*
		 * 最后抽取到的信息
		 */
		String[] fogList = new String[50];// 用来存储抽取出来的符合主题和模板的雾

		float isvalue1 = (float) 0;
		float isvalue2 = (float) 0;
		float isvalue3 = (float) 0;
		float isvalue4 = (float) 0;
		String fogcolor = "";
		Collection mapToFogValues = null;
		if (fogFromInfoNumber == 0) {
			System.out.println("...........模板处理............");
			if (list.size() > 0) {
				executeTemplateToBackgroundSceneSWRLEngine(model, list);
				// 去相应的场景里获取所添加的雾
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
			System.out.println("模板处理完毕！-------------------");
		}

		if (fogFromInfoNumber == 0) {
			System.out.println("场景主题处理---------------");
			Collection topics = maIndividual.getPropertyValues(hasTopicProperty);
			if (!topics.isEmpty()) {
				OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);// 将topics转换为数组
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
		System.out.println("场景主题处理完毕！---------------");
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
		File file = new File("F:/实验室文档/ADL/Fog/test.xml");
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(file);
		fogInsert tt = new fogInsert();
		System.out.println("开始");
		Document document1 = tt.fogInfer(aList, model, "shock_storm.ma",
				document);
		XMLWriter writer = new XMLWriter(new FileWriter(
				"F:/实验室文档/ADL/Fog/testfog.xml"));
		writer.write(document1);
		System.out.println("结束");

		fogInsert fog = new fogInsert();
		fog.fogInfer2(aList, model, "shock_storm.ma");
		System.out.println("fog num = " + fog.getFog());
		writer.close();
	}

}
