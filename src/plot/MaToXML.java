package plot;

import java.text.DecimalFormat;
import java.util.*;
import java.io.*;
import java.math.BigDecimal;

import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
//import org.jdom.Document;
import org.dom4j.Document;

import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLFactoryException;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngineFactory;

public class MaToXML {

	static String fileName = "ColorAndLight_06_28.owl";
	static String prefixAll = "p5:";
	static Collection disContrastPlan;
	static Collection contrastPlan;
	static Random random = new Random(System.currentTimeMillis());
	static ArrayList<String> season = new ArrayList();
	// �����������������ȡ��ǰʱ����Ϊ���������
	static String human = "p1:Human";
	boolean flagFloor = false;/// ���������Ƿ����ɫ
	/**
	 * ���� �������⣬Ϊma������ÿ������������ɫ,Ϊma������ѡ�ƹⲼ�֣������õƹ���ɫ
	 * �������⣬�����ѡ��ɫ����
	 * 
	 * @param owlModel
	 * @param maName
	 *            ma���ƣ��ַ���
	 * @param doc
	 *            XML�ĵ�
	 * @return �����޸ĺ��XML�ĵ���δ���棩
	 */
	/*
	 * public Document setColorAndLight(OWLModel owlModel, String maName,
	 * Document doc) throws SWRLFactoryException, SWRLRuleEngineException{
	 * OWLIndividual ma = owlModel.getOWLIndividual(maName); OWLDatatypeProperty
	 * topicNameProperty =
	 * owlModel.getOWLDatatypeProperty(prefixAll+"topicName");
	 * OWLDatatypeProperty hasContrastProperty =
	 * owlModel.getOWLDatatypeProperty("hasContrast"); disContrastPlan =
	 * owlModel.getRDFResourcesWithPropertyValue(hasContrastProperty, false);
	 * contrastPlan =
	 * owlModel.getRDFResourcesWithPropertyValue(hasContrastProperty, true);
	 * //get the ma topic individual property
	 * 
	 * Collection topicNames = ma.getPropertyValues(topicNameProperty);
	 * if(topicNames.isEmpty()) { System.out.println("���������⣬�����ѡ��ɫ");
	 * setModelColor(owlModel, ma, doc); setLight(owlModel, ma, doc); } else{
	 * Iterator jt = topicNames.iterator(); String topicName = (String)
	 * jt.next(); String ImpClassName = getTopicImpClass(owlModel,
	 * prefixAll+topicName); if(ImpClassName.equals("")) {
	 * //���Ҳ��������Ӧ����ɫ�����࣬�����ѡ��ɫ
	 * System.out.println("�Ҳ��������Ӧ����ɫ�����࣬�����ѡ��ɫ");
	 * setModelColor(owlModel, ma, doc); setLight(owlModel, ma, doc); } else {
	 * setModelColor(owlModel, ma, doc, ImpClassName); setLight(owlModel, ma,
	 * doc, ImpClassName); } } System.out.
	 * println("-----------------------**********************THE PROGRAM IS FINISHED**********************--------------------------"
	 * ); return doc; }
	 */

	/**
	 * 2013.3�޸� �������⣬Ϊma������ÿ������������ɫ,Ϊma������ѡ�ƹⲼ�֣������õƹ���ɫ
	 * �������⣬�����ѡ��ɫ����
	 * 
	 * @param owlModel
	 * @param maName
	 *            ma���ƣ��ַ���
	 * @param doc
	 *            XML�ĵ�
	 * @return �����޸ĺ��XML�ĵ���δ���棩
	 */
	public Document setColorAndLight(OWLModel owlModel, String maName, Document doc, ArrayList<String> colorChangeAttr,
			ArrayList<String> seasonlist) throws SWRLFactoryException, SWRLRuleEngineException {
		System.out.println("------------Color----------------");
		season = seasonlist;
		int tnum = colorChangeAttr.size();

		if (tnum > 0) {
			int i;
			for (i = 0; i < tnum;) {
				doc = setTargetModelColor(owlModel, colorChangeAttr.get(i), colorChangeAttr.get(i + 1),
						colorChangeAttr.get(i + 2), doc);
				i = i + 3;
			}
		} // Ϊָ�����������ɫ
		OWLIndividual ma = owlModel.getOWLIndividual(maName);

		OWLDatatypeProperty topicNameProperty = owlModel.getOWLDatatypeProperty("topicName");
		OWLDatatypeProperty hasContrastProperty = owlModel.getOWLDatatypeProperty(prefixAll + "hasContrast");
		disContrastPlan = owlModel.getRDFResourcesWithPropertyValue(hasContrastProperty, false);
		contrastPlan = owlModel.getRDFResourcesWithPropertyValue(hasContrastProperty, true);
		// get the ma topic individual property

		Collection topicNames = ma.getPropertyValues(topicNameProperty);
		System.out.println("topicNames.size=" + topicNames.size());
		if (topicNames.isEmpty()) {
			System.out.println("���������⣬�����ѡ��ɫ");
			setModelColor(owlModel, ma, doc, colorChangeAttr);

		} else {
			Iterator jt = topicNames.iterator();
			String topicName = (String) jt.next();
			String ImpClassName = getTopicImpClass(owlModel, topicName);
			if (ImpClassName.equals("")) {
				// ���Ҳ��������Ӧ����ɫ�����࣬�����ѡ��ɫ
				System.out.println("�Ҳ��������Ӧ����ɫ�����࣬�����ѡ��ɫ");
				setModelColor(owlModel, ma, doc, colorChangeAttr);
				// setLight(owlModel, ma, doc);
			} else {
				setModelColor(owlModel, ma, doc, colorChangeAttr, ImpClassName);
				// setLight(owlModel, ma, doc, ImpClassName);
			}
		}
		if (!flagFloor)
			getTexturePlanForFloor(doc, owlModel, ma);
		System.out.println(
				"-----------------------**********************THE PROGRAM IS FINISHED**********************--------------------------");
		return doc;
	}

	/**
	 * �������⣬Ϊma������ÿ������������ɫ
	 * 
	 * @param owlModel
	 * @param ma
	 *            maʵ��
	 * @param doc
	 *            XML�ĵ�
	 * @param topicName
	 * @return �����޸ĺ��XML�ĵ���δ���棩
	 */
	public Document setModelColor(OWLModel owlModel, OWLIndividual ma, Document doc, ArrayList<String> colorChangeAttr,
			String ImpClassName) {
		// OWLNamedClass topic = owlModel.getOWLNamedClass(prefixAll +
		// ImpClassName);
		Collection colorIndividual = getColorMatchingPlan(owlModel, ImpClassName, ma);
		if (colorIndividual.isEmpty()) {
			System.out.println("δ�ҵ���ɫ���䷽������д��doc��ֱ�ӷ���");
			return doc;
		}
		Collection<OWLIndividual> modelAndColor = matchModelColor(owlModel, ma, colorIndividual, colorChangeAttr);
		if (modelAndColor == null)
			return doc;

		//////////////////////////////////////////////////////////////////////////////////////////////
		// ��������������ģ�ͣ�������ͼ�滮
		if (hasCharacter(owlModel, ma))
			getTexturePlan(doc, owlModel, ma);
		getTexturePlanForFloor(doc, owlModel, ma);
		printRule(doc, owlModel, modelAndColor);
		System.out.println("�ҵ�������ƥ�����ɫ���䷽����д��doc");
		return doc;
	}

	/**
	 * ���ѡ����ɫ������Ϊma������ÿ������������ɫ
	 * 
	 * @param owlModel
	 * @param ma
	 *            maʵ��
	 * @param doc
	 *            XML�ĵ�
	 * @return �����޸ĺ��XML�ĵ���δ���棩
	 */
	public Document setModelColor(OWLModel owlModel, OWLIndividual ma, Document doc,
			ArrayList<String> colorChangeAttr) {
		Collection colorIndividual = getColorMatchingPlan(owlModel, ma);
		if (colorIndividual.isEmpty()) {
			System.out.println("δ����ҵ���ɫ���䷽������д��doc��ֱ�ӷ���");
			return doc;
		}
		Collection<OWLIndividual> modelAndColor = matchModelColor(owlModel, ma, colorIndividual, colorChangeAttr);
		if (modelAndColor == null)
			return doc;

		//////////////////////////////////////////////////////////////////////////////////////////////
		// ��������������ģ�ͣ�������ͼ�滮
		if (hasCharacter(owlModel, ma)) {
			System.out.println("@@@@@@@@@@@ The ma scene has character~~~~");
			getTexturePlan(doc, owlModel, ma);
		}
		//
		printRule(doc, owlModel, modelAndColor);

		System.out.println("����ҵ���ɫ���䷽����д��doc");
		return doc;
	}

	/**
	 * Ϊma������ѡ�ƹⲼ�֣��������������õƹ���ɫ
	 * 
	 * @param owlModel
	 * @param ma
	 *            maʵ��
	 * @param doc
	 *            XML�ĵ�
	 * @param topicName
	 * @return �����޸ĺ��XML�ĵ���δ���棩
	 */
	public Document setLight(OWLModel owlModel, OWLIndividual ma, Document doc, String ImpClassName)
			throws SWRLFactoryException, SWRLRuleEngineException {
		OWLIndividual lightLayout = chooseLightLayout(owlModel, ma);
		if (lightLayout == null) {
			System.out.println("δ�ҵ��ƹⲼ�ַ�������д��doc��ֱ�ӷ���");
			return doc;
		}
		doc = printRule(doc, owlModel, lightLayout);
		Collection<OWLIndividual> lightAndColor = setLightColor(owlModel, ImpClassName, lightLayout);
		if (lightAndColor == null) {
			System.out.println("δ�ҵ���������ĵƹ���ɫ����д��doc��ֱ�ӷ���");
			return doc;
		}
		printRule(doc, owlModel, lightAndColor);
		System.out.println("�ҵ���maƥ��ĵƹⲼ�ַ����������������õƹ���ɫ��д��doc");
		return doc;
	}

	/**
	 * Ϊma������ѡ�ƹⲼ�֣���������õƹ���ɫ
	 * 
	 * @param owlModel
	 * @param ma
	 *            maʵ��
	 * @param doc
	 *            XML�ĵ�
	 * @param topicName
	 * @return �����޸ĺ��XML�ĵ���δ���棩
	 */
	public Document setLight(OWLModel owlModel, OWLIndividual ma, Document doc)
			throws SWRLFactoryException, SWRLRuleEngineException {
		OWLIndividual lightLayout = chooseLightLayout(owlModel, ma);
		if (lightLayout == null) {
			System.out.println("δ�ҵ��ƹⲼ�ַ�������д��doc��ֱ�ӷ���");
			return doc;
		}
		doc = printRule(doc, owlModel, lightLayout);
		Collection<OWLIndividual> lightAndColor = setLightColor(owlModel, lightLayout);
		if (lightAndColor == null) {
			System.out.println("δ�ҵ���������ĵƹ���ɫ����д��doc��ֱ�ӷ���");
			return doc;
		}
		printRule(doc, owlModel, lightAndColor);
		System.out.println("�ҵ���maƥ��ĵƹⲼ�ַ�����������õƹ���ɫ��д��doc");
		return doc;
	}

	/**
	 * Ϊma������ѡ�ƹⲼ��
	 * 
	 * @param owlModel
	 *            owl�ļ�
	 * @param ma
	 *            maʵ��
	 * @return ���صƹⲼ��ʵ�����������⣬�����ѡ��ɫ
	 */
	public OWLIndividual chooseLightLayout(OWLModel owlModel, OWLIndividual ma) {

		String prefix = "";
		String maSceneName = ma.getBrowserText();
		System.out.println("maSceneName: " + maSceneName);
		OWLIndividual maScene = owlModel.getOWLIndividual(maSceneName);
		// OWLNamedClass LightLayoutClass =
		// owlModel.getOWLNamedClass(prefix+"LightLayout");
		// Collection LightLayout_collection =
		// LightLayoutClass.getInstances(true);
		OWLObjectProperty hasMaProperty = owlModel.getOWLObjectProperty(prefix + "hasMa");
		RDFSNamedClass superClass = getClassFromIndividual(owlModel, ma);
		System.out.println("superClass Name: " + superClass.getBrowserText());
		Collection SuiLightLayout = new ArrayList();
		if (superClass.getBrowserText().contains("Background") && superClass.getBrowserText().contains("Scene")) {
			OWLNamedClass BackgroundScene = owlModel.getOWLNamedClass("BackgroundScene");
			SuiLightLayout = owlModel.getRDFResourcesWithPropertyValue(hasMaProperty, BackgroundScene);
		} else if (superClass.getBrowserText().equals("EmptyScene")) {
			OWLNamedClass EmptyScene = owlModel.getOWLNamedClass("EmptyScene");
			SuiLightLayout = owlModel.getRDFResourcesWithPropertyValue(hasMaProperty, EmptyScene);
		} else
			SuiLightLayout = owlModel.getRDFResourcesWithPropertyValue(hasMaProperty, maScene);

		/**
		 * Gets all RDFResources that have a given value for a given property.
		 * maScene������ʵ��������Ϊ�ַ�������Ϊproperty�ж�Ӧ����ʵ��
		 */

		if (SuiLightLayout.isEmpty())
			return null;

		OWLIndividual[] SuiLightLayoutArr = (OWLIndividual[]) SuiLightLayout.toArray(new OWLIndividual[0]);

		int n = SuiLightLayout.size();
		int randomNum = random.nextInt(n);
		OWLIndividual LightLayout = SuiLightLayoutArr[randomNum];
		return LightLayout;
	}

	/**
	 * ��ȡ�����Ӧ��colorImplication�е���
	 * 
	 * @param owlModel
	 * @param topic
	 *            ����
	 * @return ���ض�Ӧ���ַ���
	 */
	public String getTopicImpClass(OWLModel owlModel, String topicName) {
		String prefix = "p5:";
		String ImpClassName = "";
		// OWLNamedClass topic = owlModel.getOWLNamedClass(prefix+topicName);
		OWLNamedClass ColorImplicationClass = owlModel.getOWLNamedClass(prefix + "ColorImplication");
		OWLObjectProperty hasTopicProperty = owlModel.getOWLObjectProperty(prefix + "hasTopic");
		Collection ColorImpSub1_collection = ColorImplicationClass.getSubclasses(true);// ԭ��Ϊfalse

		/**
		 * ��ÿ����ʹ��getRestrictions������ʹ��OWLRestriction�ຯ����ȡԼ����������(String���ͣ��������жϣ����Ч��
		 * �����ж�Լ���Ƿ�Ϊunion��ֻҪ��Լ�����Ƿ���topic���ɣ����Ч��
		 */
		for (Iterator jt = ColorImpSub1_collection.iterator(); jt.hasNext();) {
			OWLNamedClass ColorImpSubTemp = (OWLNamedClass) jt.next();
			Collection restrictions = ColorImpSubTemp.getRestrictions(hasTopicProperty, false);
			// false��ʾ�������������Լ������
			for (Iterator jm = restrictions.iterator(); jm.hasNext();) {
				OWLRestriction restriction = (OWLRestriction) jm.next();
				String restrict = restriction.getFillerText();
				if (restrict.indexOf(topicName) >= 0)
					ImpClassName = ColorImpSubTemp.getBrowserText();
			}
		}

		System.out.println("ImpClassName is : " + ImpClassName);
		return ImpClassName;
	}

	/**
	 * ������õƹ���ɫ
	 * 
	 * @param owlModel
	 *            owl�ļ�
	 * @param LightLayout
	 *            ma�����ƹⲼ��ʵ��
	 * @return ������ɫʵ��
	 */
	public Collection<OWLIndividual> setLightColor(OWLModel owlModel, OWLIndividual LightLayout)
			throws SWRLFactoryException, SWRLRuleEngineException {

		// �������������
		String prefix = "p5:";
		OWLNamedClass ColorClass = owlModel.getOWLNamedClass(prefix + "Color");
		OWLObjectProperty hasLightProperty = owlModel.getOWLObjectProperty(prefix + "hasLight");
		OWLDatatypeProperty isLightColorProperty = owlModel.getOWLDatatypeProperty(prefix + "isLightColor");
		if (LightLayout.getPropertyValues(hasLightProperty) == null) {
			System.out.println("�ƹⲼ�ַ�����û�еƹ�ʵ��������null");
			return null;
		}

		// set the suitable color isLightColorProperty true
		executeSWRLEngine(owlModel, "chooseLightColor");
		Collection SuiLightColor = owlModel.getRDFResourcesWithPropertyValue(isLightColorProperty, true);

		// Collection ColorIndividuals = ColorClass.getInstances(true);
		int n = SuiLightColor.size();
		int randomNum = random.nextInt(n);
		OWLIndividual[] ColorIndividual = (OWLIndividual[]) SuiLightColor.toArray(new OWLIndividual[0]);
		Collection hasLight = LightLayout.getPropertyValues(hasLightProperty);
		Collection<OWLIndividual> modelAndColor = new ArrayList<OWLIndividual>();
		for (Iterator jt = hasLight.iterator(); jt.hasNext();) {
			OWLIndividual light = (OWLIndividual) jt.next();
			modelAndColor.add(light);
			modelAndColor.add(ColorIndividual[randomNum]);
		}
		return modelAndColor;
	}

	/**
	 * ���������ȡ�ƹ���ɫ
	 * 
	 * @param owlModel
	 *            owl�ļ�
	 * @param topic
	 *            ��������
	 * @param LightLayout
	 *            ma�����ƹⲼ��ʵ��
	 * @return ������ɫʵ��
	 */
	public Collection<OWLIndividual> setLightColor(OWLModel owlModel, String ImpClassName, OWLIndividual LightLayout)
			throws SWRLFactoryException, SWRLRuleEngineException {

		// �������������
		String prefix = "p5:";
		OWLObjectProperty hasLightProperty = owlModel.getOWLObjectProperty(prefix + "hasLight");
		// OWLNamedClass ColorClass = owlModel.getOWLNamedClass(prefix+"Color");
		OWLNamedClass TopicSuiColorImpClass = owlModel.getOWLNamedClass(ImpClassName);
		OWLObjectProperty hasLightColorToneProperty = owlModel.getOWLObjectProperty(prefix + "hasLightColorTone");
		// OWLObjectProperty hasLightToneProperty =
		// owlModel.getOWLObjectProperty(prefix+"hasLightTone");
		OWLDatatypeProperty isLightColorProperty = owlModel.getOWLDatatypeProperty(prefix + "isLightColor");
		// Collection<OWLNamedClass> SuiColorMatch = new
		// ArrayList<OWLNamedClass>();

		if (TopicSuiColorImpClass.getSomeValuesFrom(hasLightColorToneProperty) == null) {
			System.out.println("�����Ӧ����û�еƹ�ɫ�����ԣ�����null");
			return null;
		}
		if (LightLayout.getPropertyValues(hasLightProperty) == null) {
			System.out.println("�ƹⲼ�ַ�����û�еƹ�ʵ��������null");
			return null;
		}
		// if the hasSuitableMean property has only one value,can't use
		// collection
		// OWLNamedClass hasLightColorToneClass = (OWLNamedClass)
		// TopicSuiColorImpClass.getSomeValuesFrom(hasLightColorToneProperty);
		// get the implication class's hasLightColor property to find the
		// suitable color collection

		Collection lightColorTone_restrictions = TopicSuiColorImpClass.getRestrictions(hasLightColorToneProperty,
				false);
		String lightColorTone = "";
		OWLRestriction rest_temp = (OWLRestriction) (lightColorTone_restrictions.iterator()).next();
		lightColorTone = rest_temp.getFillerText();

		// if restriction contains any kind of tones, execute corresponding
		// rules, set the suitable color isLightColorProperty true
		if (lightColorTone.contains("ColdTone"))
			executeSWRLEngine(owlModel, "chooseLightColor_cold");
		if (lightColorTone.contains("MiddleTone"))
			executeSWRLEngine(owlModel, "chooseLightColor_middle");
		if (lightColorTone.contains("WarmTone"))
			executeSWRLEngine(owlModel, "chooseLightColor_warm");
		if (!(lightColorTone.contains("ColdTone") || lightColorTone.contains("MiddleTone")
				|| lightColorTone.contains("WarmTone"))) {
			System.out.println(
					"�����Ӧ��implication���hasLightColorToneProperty�쳣����������ֵ��Χ��ColdTone��MiddleTone��WarmTone��������null");
			return null;
		}

		Collection SuiLightColor = owlModel.getRDFResourcesWithPropertyValue(isLightColorProperty, true);
		if (SuiLightColor.isEmpty()) {
			System.out.println("û���ҵ���������ص���ɫʵ��������null");
			return null;
		}
		OWLIndividual[] ColorIndividual = (OWLIndividual[]) SuiLightColor.toArray(new OWLIndividual[0]);
		int n = ColorIndividual.length;
		int randomNum = random.nextInt(n);
		Collection hasLight = LightLayout.getPropertyValues(hasLightProperty);
		Collection<OWLIndividual> modelAndColor = new ArrayList<OWLIndividual>();
		for (Iterator jt = hasLight.iterator(); jt.hasNext();) {
			OWLIndividual light = (OWLIndividual) jt.next();
			modelAndColor.add(light);
			modelAndColor.add(ColorIndividual[randomNum]);
		}
		return modelAndColor;
	}

	/**
	 * �����ȡ��ɫ���䷽���������е���ɫ�����������ȡ
	 * 
	 * @param owlModel
	 * @return ������ɫ���䷽����������ɫʵ���ļ���
	 */
	public Collection getColorMatchingPlan(OWLModel owlModel, OWLIndividual ma) {
		String prefix = "p5:";
		OWLNamedClass ColorMatchingPlanClass = owlModel.getOWLNamedClass(prefix + "ColorMatchingPlan");
		OWLObjectProperty hasColorProperty = owlModel.getOWLObjectProperty(prefix + "hasColor");

		Collection<OWLIndividual> SuiColorMatchIndividuals = new ArrayList<OWLIndividual>();// ����Ҫ���������ɫ����ʵ��
		SuiColorMatchIndividuals = ColorMatchingPlanClass.getInstances(true);
		if (SuiColorMatchIndividuals.isEmpty()) {
			System.out.println("��ɫ����������ʵ�������ؿռ���");
			return SuiColorMatchIndividuals;
		}

		// �ж�ma�������Ƿ�������ģ�ͣ����У�����Ϊǿ�ҶԱȣ���û�У�����Ϊ��г
		boolean contrast = hasCharacter(owlModel, ma);

		int modelNum = hasModelNum(owlModel, ma);
		boolean colorPlanMore = false;
		// falseΪ������ʹ��һ����ɫ����
		if (modelNum >= 8)
			colorPlanMore = true;
		Collection colorCollection = chooseThoughContrast(owlModel, contrast, colorPlanMore, SuiColorMatchIndividuals);
		return colorCollection;

	}

	/**
	 * ��ȡ�Ͷ��������Ӧ����ɫ���䷽��
	 * 
	 * @param owlModel
	 *            owlģ����
	 * @param topic
	 *            ��������
	 * @return ������ɫ���䷽����������ɫʵ���ļ���
	 */
	public Collection getColorMatchingPlan(OWLModel owlModel, String ImpClassName, OWLIndividual ma) {
		// get the implication class of the topic class, search rule to find
		// topic
		String prefix = "p5:";
		OWLNamedClass ColorMatchingPlanClass = owlModel.getOWLNamedClass(prefix + "ColorMatchingPlan");
		OWLObjectProperty hasColorProperty = owlModel.getOWLObjectProperty(prefix + "hasColor");

		Collection colorCollection = new ArrayList();
		OWLNamedClass TopicSuiColorImpClass = owlModel.getOWLNamedClass(ImpClassName);
		// get the implication topic class and find its suitable mean, and find
		// the color matching plan which has the same emotion tag
		OWLObjectProperty hasSuitableMeanProperty = owlModel.getOWLObjectProperty(prefix + "hasSuitableMean");
		OWLObjectProperty hasEmotionTagProperty = owlModel.getOWLObjectProperty(prefix + "hasEmotionTag");
		// Collection<OWLNamedClass> SuiColorMatchClass = new
		// ArrayList<OWLNamedClass>();
		Collection<OWLIndividual> SuiColorMatchIndividuals = new ArrayList<OWLIndividual>();// ����Ҫ���������ɫ����ʵ��
		if (TopicSuiColorImpClass.getSomeValuesFrom(hasSuitableMeanProperty) == null) {
			System.out.println(TopicSuiColorImpClass.getBrowserText() + " don't have "
					+ hasSuitableMeanProperty.getBrowserText() + " rule");
			return colorCollection;
		} else {
			Collection SuiMean_restrictions = TopicSuiColorImpClass.getRestrictions(hasSuitableMeanProperty, false);
			String ColorImpSuiMean = "";
			for (Iterator jn = SuiMean_restrictions.iterator(); jn.hasNext();) {
				OWLRestriction rest_temp = (OWLRestriction) jn.next();
				ColorImpSuiMean = rest_temp.getFillerText();
			}
			Collection ColorMatch_collection = ColorMatchingPlanClass.getSubclasses(true);
			for (Iterator jt = ColorMatch_collection.iterator(); jt.hasNext();) {
				OWLNamedClass ColorMatTemp = (OWLNamedClass) jt.next();
				Collection restrictions = ColorMatTemp.getRestrictions(hasEmotionTagProperty, false);
				// false��ʾ�������������Լ������
				for (Iterator jm = restrictions.iterator(); jm.hasNext();) {
					OWLRestriction restriction = (OWLRestriction) jm.next();
					String restrict = restriction.getFillerText();
					if (ColorImpSuiMean.indexOf(restrict) >= 0) {
						// SuiColorMatchClass.add(ColorMatTemp);
						SuiColorMatchIndividuals.addAll(ColorMatTemp.getInstances(true));
					}
					// ��ɫ������emotiontagֻ��һ�����壬��ColorImp���п��ܰ���������壬������ColorImpSuiMean�Ƿ����restrict�������ж�
				}
			}
		}

		/**
		 * 2012-6-14 ���
		 */
		// ����ó����ļ�����Ϣ������ɫ��������+++++++++++
		Collection seasonPlan = getSeasonPlan(owlModel, ma);
		if (seasonPlan != null)
			SuiColorMatchIndividuals.addAll(seasonPlan);
		System.out.println(SuiColorMatchIndividuals);

		if (SuiColorMatchIndividuals.isEmpty()) {
			System.out.println("û���ʺϵ���ɫ���������ؿռ���");
			return colorCollection;
		}

		// �ж�ma�������Ƿ�������ģ�ͣ����У�����Ϊǿ�ҶԱȣ���û�У�����Ϊ��г
		boolean contrast = hasCharacter(owlModel, ma);

		int modelNum = hasModelNum(owlModel, ma);
		boolean colorPlanMore = false;
		// falseΪ������ʹ��һ����ɫ����
		if (modelNum >= 8)
			colorPlanMore = true;
		colorCollection = chooseThoughContrast(owlModel, contrast, colorPlanMore, SuiColorMatchIndividuals);
		return colorCollection;

	}

	/**
	 * Ϊ½��ģ�ͽ�����ͼ�滮 ma �������ƣ��µ�plotDesign�϶���M_Floor.ma 2015.1.5
	 */
	public void getTexturePlanForFloor(Document doc, OWLModel owlModel, OWLIndividual ma) {
		Collection<OWLIndividual> models = getModelsInMa(owlModel, ma);// get
																		// the
																		// modelIDs
		for (Iterator model = models.iterator(); model.hasNext();) {
			OWLIndividual modelIDTemp = (OWLIndividual) model.next();
			OWLIndividual modelNameTemp = getModelNameFromID(owlModel, modelIDTemp);
			if (modelNameTemp.getBrowserText().equals("M_floor.ma")) {
				texturePlanPerModel(doc, owlModel, modelIDTemp);
			}
		}
	}

	/**
	 * Ϊpeopleģ�ͽ�����ͼ�滮
	 * 
	 * @param owlModel
	 * @param ma
	 *            �����ļ�
	 */
	public void getTexturePlan(Document doc, OWLModel owlModel, OWLIndividual ma) {
		// Collection textureModelCollection = new ArrayList();
		// ��ȡma����������ģ��
		Collection<OWLIndividual> characterCollection = getCharacters(owlModel, ma);
		System.out.println("The ma scene has " + characterCollection.size() + " characters");

		if (characterCollection.isEmpty()) {
			System.out.println("������û������ģ�ͣ����ؿռ���");
			return;
		}
		for (Iterator jm = characterCollection.iterator(); jm.hasNext();) {
			OWLIndividual characterID = (OWLIndividual) jm.next();
			System.out.println("�����д�������ģ�ͣ� " + characterID.getBrowserText());
			texturePlanPerModel(doc, owlModel, characterID);
		}
	}

	/**
	 * Ϊÿ��ģ�ͽ�����ͼ�滮
	 * 
	 * @param owlModel
	 * @param model
	 *            ģ��ʵ��
	 */
	public void texturePlanPerModel(Document doc, OWLModel owlModel, OWLIndividual model) {
		Collection<RDFSNamedClass> textureTypeCollection = new ArrayList<RDFSNamedClass>();
		Collection suiTextures = new ArrayList();
		OWLObjectProperty hasSuitableModelProperty = owlModel.getOWLObjectProperty("p5:hasSuitableModel");
		OWLDatatypeProperty hasTextureNameProperty = owlModel.getOWLDatatypeProperty("p5:hasTextureName");
		OWLObjectProperty hasSuitSeasonProperty = owlModel.getOWLObjectProperty("hasSuitSeason");
		// ����modelID�ҵ�model��ʵ��
		OWLIndividual modelName = getModelNameFromID(owlModel, model);
		suiTextures = owlModel.getRDFResourcesWithPropertyValue(hasSuitableModelProperty, modelName);

		OWLIndividual[] suiTextureIndividual = (OWLIndividual[]) suiTextures.toArray(new OWLIndividual[0]);
		ArrayList<OWLIndividual> suitIndividual = new ArrayList();

		for (int ii = 0; ii < suiTextureIndividual.length; ii++) {
			OWLIndividual ind = suiTextureIndividual[ii];
			Collection col = ind.getPropertyValues(hasSuitSeasonProperty);
			if (col.size() == 0) {
				continue;
			} else {
				for (Iterator ite = col.iterator(); ite.hasNext();) {
					OWLIndividual owl = (OWLIndividual) ite.next();
					String str = owl.getBrowserText().toString();
					String str1 = str.substring(0, str.indexOf("Description"));
					for (int is = 0; is < season.size(); is++) {
						if (season.get(is).equals(str1)) {

							suitIndividual.add(ind);
							System.out.println(ind.getBrowserText());
							break;
						}

					}

				}

			}
		}

		int n = suitIndividual.size();
		int randomNum;
		if (n > 0) {
			for (int i = 0; i < 5; i++) {
				randomNum = random.nextInt(n);
				RDFSNamedClass textureClass = getClassFromIndividual(owlModel, suitIndividual.get(randomNum));
				// δ�Դ�������ͼ���й滮
				if (!textureTypeCollection.contains(textureClass)) {
					textureTypeCollection.add(textureClass);
					System.out.println("��ͼ���ͣ� " + textureClass.getBrowserText());
					String textureName = (String) suitIndividual.get(randomNum)
							.getPropertyValue(hasTextureNameProperty);
					// ��ӡ����
					printRule(doc, owlModel, model, textureClass.getBrowserText(), textureName);
					break;
				} else
					System.out.println("��ͼ���ͳ�ͻ�� " + textureClass.getBrowserText());
			}

		} else {
			n = suiTextures.size();
			if (suiTextures.size() > 0) {
				for (int i = 0; i < 5; i++) {
					randomNum = random.nextInt(n);
					RDFSNamedClass textureClass = getClassFromIndividual(owlModel, suiTextureIndividual[randomNum]);
					// δ�Դ�������ͼ���й滮
					if (!textureTypeCollection.contains(textureClass)) {
						textureTypeCollection.add(textureClass);
						System.out.println("��ͼ���ͣ� " + textureClass.getBrowserText());
						String textureName = (String) suiTextureIndividual[randomNum]
								.getPropertyValue(hasTextureNameProperty);
						// ��ӡ����
						printRule(doc, owlModel, model, textureClass.getBrowserText(), textureName);
					} else
						System.out.println("��ͼ���ͳ�ͻ�� " + textureClass.getBrowserText());
				}
			}
		}

	}

	/**
	 * 2012-6-14 ���
	 */
	/**
	 * ���ݳ���������Ϣ�ҵ���Ӧ��ɫ��������getColorMatchingPlan�����е���++++++++++++++++
	 * 
	 * @param owlModel
	 * @param ma
	 *            ���������ļ�
	 * @return ����ƥ�����ɫ��������
	 */
	public Collection getSeasonPlan(OWLModel owlModel, OWLIndividual ma) {
		OWLObjectProperty hasSeasonProperty = owlModel.getOWLObjectProperty("hasSeason");
		Collection seasonPlan = new ArrayList();

		System.out.println("maSceneName: " + ma.getBrowserText());
		if (ma.getPropertyValue(hasSeasonProperty) == null) {
			System.out.println("There is no season attribute.");
			return null;
		}
		OWLIndividual seasonInfo = (OWLIndividual) ma.getPropertyValue(hasSeasonProperty);// ����Ψһ�Ե�����
		// ����ʵ�������࣬�����ﶬ
		RDFSNamedClass season = getClassFromIndividual(owlModel, seasonInfo);
		System.out.println("seasonPlan: " + season.getBrowserText());

		if (season.getBrowserText().equals("Winter")) {
			OWLNamedClass winterPlan = owlModel.getOWLNamedClass("p5:ColorWinter");
			seasonPlan = winterPlan.getInstances(true);
			System.out.println("seasonPlan choosen : " + season.getBrowserText());
			return seasonPlan;
		} else if (season.getBrowserText().equals("Autumn")) {
			OWLNamedClass autumnPlan = owlModel.getOWLNamedClass("p5:ColorAutumn");
			seasonPlan = autumnPlan.getInstances(true);
			System.out.println("seasonPlan choosen : " + season.getBrowserText());
			return seasonPlan;
		} else if (season.getBrowserText().equals("Summer")) {
			OWLNamedClass summerPlan = owlModel.getOWLNamedClass("p5:ColorSummer");
			seasonPlan = summerPlan.getInstances(true);
			System.out.println("seasonPlan choosen : " + season.getBrowserText());
			return seasonPlan;
		} else if (season.getBrowserText().equals("Spring")) {
			OWLNamedClass springPlan = owlModel.getOWLNamedClass("p5:ColorSpring");
			seasonPlan = springPlan.getInstances(true);
			System.out.println("seasonPlan choosen : " + season.getBrowserText());
			return seasonPlan;
		} else {
			System.out.println("season individual has wrong type.");
			return null;
		}

	}

	/**
	 * @@@@@@@@@@@@@
	 * Ϊ����������ģ��������ɫ��ʹ����������ʽ����ģ������λ�ó�����ɫ������ȡ��ģ����ɫ
	 * ��������ģ����������3���޸ĳ�����30%-70%��ģ�ͣ������ѡ����������ģ������С�ڵ���3����ı������������ɫ
	 * 
	 * @param owlModel
	 *            owlģ��
	 * @param ma
	 *            maʵ��
	 * @param colorCollection
	 *            ��ɫ��������ɫ����
	 * @return ģ�ͺ���ɫ���ϣ�һ��ģ��ʵ����һ����ɫʵ��
	 */
	public Collection<OWLIndividual> matchModelColor(OWLModel owlModel, OWLIndividual ma, Collection colorCollection,
			ArrayList<String> colorChangeAttr) {
		OWLIndividual[] ColorIndividual = (OWLIndividual[]) colorCollection.toArray(new OWLIndividual[0]);

		Collection<OWLIndividual> modelCollection = getModelsInMa(owlModel, ma);
		// ��ȡma����������ģ��
		Collection<OWLIndividual> modelAndColor = new ArrayList<OWLIndividual>();
		int colorNum = ColorIndividual.length;
		int i = 0;
		if (modelCollection.isEmpty()) {
			System.out.println("������û��ģ�ͣ����ؿռ���");
			return null;
		}

		/**
		 * 2012-03-19�����
		 * ��������ģ����������3���޸ĳ�����30%-70%��ģ�ͣ������ѡ����������ģ������С�ڵ���3����ı������������ɫ
		 */
		// �������0-3������
		int randomNum = random.nextInt(4);
		// ��������ģ������С�ڵ���3����ı������������ɫ
		int changeNum = modelCollection.size();
		;

		// ��������ģ����������3����ѡchangeNum��ģ���޸���ɫ������Ϊ30%-70%
		if (modelCollection.size() > 3)
			changeNum = modelCollection.size() * (3 + randomNum) / 10;

		OWLIndividual[] models = (OWLIndividual[]) modelCollection.toArray(new OWLIndividual[0]);
		System.out.println("@@@@@@ the scene has " + modelCollection.size() + " models");
		System.out.println("****** change " + changeNum + " models");
		int ii, jj;
		int tnum = colorChangeAttr.size();
		for (int j = 0; j < changeNum; j++) {
			// ������ģ�͵����������ѡ�������
			int num = random.nextInt(modelCollection.size());
			OWLIndividual modelTemp = models[num];
			// ɾ���Ѿ�ѡ�е�ģ�ͣ�������תΪ����
			modelCollection.remove(modelTemp);
			models = (OWLIndividual[]) modelCollection.toArray(new OWLIndividual[0]);

			// ����modelID�ҵ�model��ʵ��
			OWLIndividual modelNameTemp = getModelNameFromID(owlModel, modelTemp);

			if (!(modelNameTemp.getBrowserText()).endsWith(".ma"))
				// ��������ж�����ģ�ͣ�����snowParticleEffect��������.ma��׺�����޸�����ɫ
				continue;

			// �ж�ģ���Ƿ�Ϊ����ģ�ͣ���Ϊ����ģ�ͣ���������ɫ�滮��
			if (isCharacter(owlModel, modelNameTemp))
				continue;
			// System.out.println(modelTemp.getBrowserText());
			if (modelNameTemp.getBrowserText().equals("M_floor.ma"))
				flagFloor = true;
			jj = 0;
			if (tnum > 0) {
				for (ii = 0; ii < tnum;) {
					if (modelNameTemp.getBrowserText().contains(colorChangeAttr.get(ii)))
						jj = 1;
					ii = ii + 2;
				}
			}
			if (jj == 1) {
				System.out.println(modelNameTemp.getBrowserText() + "��ָ����ɫ");
				continue;
			}
			modelAndColor.add(modelTemp);
			int randomColor = i % colorNum;
			modelAndColor.add(ColorIndividual[randomColor]);
			i++;
		}
		return modelAndColor;
	}

	/**
	 * @@@@@@@@@@@@@@@@@@@@ ��ȡma�����е�����ģ�ͣ�����������ӵ�ģ��
	 * 
	 * @param owlModel
	 *            owlģ��
	 * @param ma
	 *            ma����
	 * @return ma����������ģ��
	 */
	public Collection<OWLIndividual> getModelsInMa(OWLModel owlModel, OWLIndividual ma) {
		Collection<OWLIndividual> modelCollection = new ArrayList<OWLIndividual>();
		OWLObjectProperty hasModelProperty = owlModel.getOWLObjectProperty("hasmodel");

		Collection hasModelCollection = ma.getPropertyValues(hasModelProperty);
		// ��ȡ����ģ��ʵ��ID
		modelCollection.addAll(hasModelCollection);

		// �ڳ����м����ģ�͵���
		OWLNamedClass AddModelClass = owlModel.getOWLNamedClass("AddModelRelated");
		Collection addModelCollection = AddModelClass.getInstances(true);

		// addToMa�����а�����ģ��Ҳ�ǳ����е�ģ��
		// OWLObjectProperty addToMaProperty =
		// owlModel.getOWLObjectProperty(prefixAll+"addToMa");
		// Collection addModelCollection =
		// ma.getPropertyValues(addToMaProperty);
		if (addModelCollection.size() != 0)
			modelCollection.addAll(addModelCollection);

		Collection deleteModelList = new ArrayList();
		// ɾ���������õ�ģ��
		for (Iterator jt = modelCollection.iterator(); jt.hasNext();) {
			OWLIndividual modelIDTemp = (OWLIndividual) jt.next();
			// ����modelID�ҵ�model��ʵ��
			OWLIndividual modelNameTemp = getModelNameFromID(owlModel, modelIDTemp);
			// $$$$$$$$$$$$$$$$��������ģ��û��modelName��ʵ��
			if (modelNameTemp == null) {
				System.out.println("modelID: " + modelIDTemp.getBrowserText() + " don't have modelName");
				deleteModelList.add(modelIDTemp);
			}
		}
		modelCollection.removeAll(deleteModelList);

		return modelCollection;
	}

	/**
	 * @@@@@@@@@@@@@@@@@
	 * �ж�ma�������Ƿ�������ģ�ͣ����У�����Ϊǿ�ҶԱȣ�����true����û�У�����Ϊ��г������false
	 * 
	 * @param owlModel
	 *            owlģ��
	 * @param ma
	 *            ma����
	 * @return ���ضԱ��ԣ�ǿ��Ϊtrue����гΪfalse
	 */
	public boolean hasCharacter(OWLModel owlModel, OWLIndividual ma) {
		Collection<OWLIndividual> modelCollection = getModelsInMa(owlModel, ma);
		for (Iterator jt = modelCollection.iterator(); jt.hasNext();) {
			OWLIndividual modelIDTemp = (OWLIndividual) jt.next();
			// ����modelID�ҵ�model��ʵ��
			OWLIndividual modelNameTemp = getModelNameFromID(owlModel, modelIDTemp);
			RDFSNamedClass modelClass = getClassFromIndividual(owlModel, modelNameTemp);

			/**
			 * @@@@@ ��ȡʵ������������и��࣬�жϸ����Ƿ����p1:Human�࣬������Ϊ����ʵ��
			 */
			Collection superClasses = new ArrayList();
			superClasses = modelClass.getSuperclasses(true);

			RDFSNamedClass humanClass = owlModel.getRDFSNamedClass(human);
			if (superClasses.contains(humanClass))
				return true;
		}
		return false;
	}

	/**
	 * @@@@@@@@@@@@@@@ ��ȡma��������������ģ�ͣ���������ģ��ʵ���ļ���
	 * 
	 * @param owlModel
	 *            owlģ��
	 * @param ma
	 *            ma����
	 * @return ��������ģ��ʵ���ļ���
	 */
	public Collection<OWLIndividual> getCharacters(OWLModel owlModel, OWLIndividual ma) {
		Collection<OWLIndividual> modelCollection = getModelsInMa(owlModel, ma);

		Collection<OWLIndividual> characters = new ArrayList<OWLIndividual>();
		for (Iterator jt = modelCollection.iterator(); jt.hasNext();) {
			OWLIndividual modelIDTemp = (OWLIndividual) jt.next();
			// ����modelID�ҵ�model��ʵ��
			OWLIndividual modelNameTemp = getModelNameFromID(owlModel, modelIDTemp);
			RDFSNamedClass modelClass = getClassFromIndividual(owlModel, modelNameTemp);

			/**
			 * @@@@@ ��ȡʵ������������и��࣬�жϸ����Ƿ����p1:Human�࣬������Ϊ����ʵ��
			 */
			Collection superClasses = new ArrayList();
			superClasses = modelClass.getSuperclasses(true);

			RDFSNamedClass humanClass = owlModel.getRDFSNamedClass(human);
			if (superClasses.contains(humanClass))
				characters.add(modelIDTemp);
		}
		return characters;
	}

	/**
	 * �ж�ʵ���Ƿ�Ϊ����ģ��
	 * 
	 * @param owlModel
	 *            owlģ��
	 * @param model
	 *            ģ��
	 * @return �����Ƿ�Ϊ����ģ��
	 */
	public boolean isCharacter(OWLModel owlModel, OWLIndividual model) {

		RDFSNamedClass modelClass = getClassFromIndividual(owlModel, model);

		/**
		 * @@@@@ ��ȡʵ������������и��࣬�жϸ����Ƿ����p1:Human�࣬������Ϊ����ʵ��
		 */
		Collection superClasses = new ArrayList();
		superClasses = modelClass.getSuperclasses(true);

		RDFSNamedClass humanClass = owlModel.getRDFSNamedClass(human);
		if (superClasses.contains(humanClass))
			return true;
		else
			return false;
	}

	/**
	 * ��ȡma������ģ������
	 * 
	 * @param owlModel
	 *            owlģ��
	 * @param ma
	 *            ma����
	 * @return ���س�����ģ������
	 */
	public int hasModelNum(OWLModel owlModel, OWLIndividual ma) {
		Collection<OWLIndividual> modelCollection = getModelsInMa(owlModel, ma);
		System.out.println("������ģ��������" + modelCollection.size());
		return modelCollection.size();
	}

	/**
	 * ���ݶ����ĶԱ�������ɸѡ��ɫ����
	 * 
	 * @param owlModel
	 *            owlģ��
	 * @param contrast
	 *            �����ĶԱ���
	 * @param planMore
	 *            �Ƿ���Ҫ�����ɫ������trueΪ2����falseΪ1��
	 * @param SuiColorMatchIndividuals
	 *            ����Ҫ�����ɫ��������
	 * @return �����Ա���ɸѡ�󣬲������ѡ�����ɫ��������(1-2����ɫ����)
	 */
	public Collection chooseThoughContrast(OWLModel owlModel, boolean contrast, boolean planMore,
			Collection<OWLIndividual> SuiColorMatchIndividuals) {

		// �������������

		Collection<OWLIndividual> ColorMatchingIndividuals = new ArrayList<OWLIndividual>();
		ColorMatchingIndividuals.addAll(SuiColorMatchIndividuals);
		System.out.println("�����������ɫ��������" + ColorMatchingIndividuals.size());
		OWLObjectProperty hasColorProperty = owlModel.getOWLObjectProperty("p5:hasColor");
		if (contrast && SuiColorMatchIndividuals.size() - disContrastPlan.size() > 4) {
			SuiColorMatchIndividuals.removeAll(disContrastPlan);
			System.out.println("�Ա���ɾ��false�����ɫ��������" + SuiColorMatchIndividuals.size());
		} else if (!contrast && SuiColorMatchIndividuals.size() - contrastPlan.size() > 4) {
			SuiColorMatchIndividuals.removeAll(contrastPlan);
			System.out.println("���Ա���ɾ��true�����ɫ��������" + SuiColorMatchIndividuals.size());
		}
		if (SuiColorMatchIndividuals.size() == 0)
			SuiColorMatchIndividuals = ColorMatchingIndividuals;
		// ��û�з��϶Ա��Ե���ɫ��������ӷ����������ɫ�����������ѡ
		OWLIndividual[] ColorMatIndividual = (OWLIndividual[]) SuiColorMatchIndividuals.toArray(new OWLIndividual[0]);
		System.out.println("colorMatI-" + ColorMatIndividual);
		int n = SuiColorMatchIndividuals.size();
		int randomNum = random.nextInt(n);
		Collection colorCollection = new ArrayList<OWLIndividual>();
		System.out.println(
				"colorP-" + ((Collection) ColorMatIndividual[randomNum].getPropertyValues(hasColorProperty)).size());
		colorCollection.addAll((Collection) ColorMatIndividual[randomNum].getPropertyValues(hasColorProperty));
		if (planMore) {
			System.out.println("������ģ����������8����Ҫ������ɫ����");
			if (randomNum < n - 1) {
				Collection temp = (Collection) ColorMatIndividual[randomNum + 1].getPropertyValues(hasColorProperty);
				colorCollection.addAll(temp);
			} else if (randomNum > 0) {
				Collection temp = (Collection) ColorMatIndividual[randomNum - 1].getPropertyValues(hasColorProperty);
				colorCollection.addAll(temp);
			}
		}
		return colorCollection;
	}

	/**
	 * ���ʵ�����ڵ�����
	 * 
	 * @param model
	 * @param individualName
	 * @return OWLNamedClass
	 */
	public RDFSNamedClass getClassFromIndividual(OWLModel model, OWLIndividual individualName) {
		String classNameStr = individualName.getRDFType().getBrowserText();
		RDFSNamedClass className = model.getRDFSNamedClass(classNameStr);
		return className;

	}

	/**
	 * ��ȡmodelID��hasModelNameʵ��
	 * 
	 * @return ����modelID��hasModelNameʵ��
	 */
	public OWLIndividual getModelNameFromID(OWLModel owlModel, OWLIndividual modelID) {
		OWLObjectProperty hasModelNameProperty = owlModel.getOWLObjectProperty("hasModelName");
		OWLIndividual modelName = (OWLIndividual) modelID.getPropertyValue(hasModelNameProperty);
		// ��������ģ��û��modelName��ʵ��
		// if(modelName!=null)
		// System.out.println("The scene has models: modelID:
		// "+modelID.getBrowserText()+" modelName:
		// "+modelName.getBrowserText());
		// else
		// System.out.println("modelID: "+modelID.getBrowserText()+" don't have
		// modelName");
		return modelName;
	}

	public void writeXMLRule(OWLModel owlModel, Collection<OWLIndividual> modelAndColor) {
		try {
			String fileName = "test.xml";
			File xmlFile = new File(fileName);
			System.out.println("xml path is " + xmlFile.getAbsolutePath());
			SAXReader reader = new SAXReader();
			Document document = (Document) reader.read(xmlFile);
			Document changedDoc = printRule(document, owlModel, modelAndColor);
			doc2XmlFile(changedDoc, fileName);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * An instance of the SWRLFactory can be created by passing an OWL model to
	 * its constructor
	 */
	public static SWRLFactory createSWRLFactory(OWLModel model) {
		SWRLFactory factory = new SWRLFactory(model);
		return factory;
	}

	/**
	 * ����rule engine
	 * 
	 * @throws SWRLRuleEngineException
	 */
	public static SWRLRuleEngine createRuleEngine(OWLModel model) throws SWRLRuleEngineException {
		SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create("SWRLJessBridge", model);
		return ruleEngine;
	}

	/**
	 * д��Ҫִ�еĹ�������ִ�ж�Ӧ����
	 * 
	 * @param model
	 *            Ontology��
	 * @param rulename
	 *            ��ִ�еĹ�����ǰ׺
	 * @return �����Ƿ�ɹ�ִ��
	 * @throws SWRLRuleEngineException
	 * @throws SWRLFactoryException
	 */
	public static boolean executeSWRLEngine(OWLModel model, String rulename)
			throws SWRLRuleEngineException, SWRLFactoryException {

		SWRLRuleEngine ruleEngine = createRuleEngine(model);
		ruleEngine.reset();
		SWRLFactory factory = createSWRLFactory(model);
		factory.disableAll();
		boolean temp = false;
		Iterator<SWRLImp> iter = factory.getImps().iterator();

		while (iter.hasNext()) {
			SWRLImp imp = (SWRLImp) iter.next();
			if (imp.getLocalName().startsWith(rulename) == true) {
				// SWRLAtomList atomList = imp.getBody();
				temp = true;
				System.out.println("LocalName:" + imp.getLocalName());
				imp.enable();
			}
		}
		ruleEngine.reset();
		ruleEngine.infer();

		System.out.println("************************ infer success!");
		return temp;
	}

	/**
	 * @@@@@@@@@@@@@ ��ӡ���Ĺ���ģ����ɫ�͵ƹ���ɫ�޸Ĺ���
	 * 
	 * @param doc
	 *            xml�ĵ�
	 * @param model
	 *            owlģ��
	 * @param modelAndColor
	 *            ����ģ�ͺ���ɫʵ���ļ���
	 * @return ����xml�ĵ�
	 */
	public Document printRule(Document doc, OWLModel model, Collection<OWLIndividual> modelAndColor) {
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");

		OWLIndividual[] modelAndColorIndividual = (OWLIndividual[]) modelAndColor.toArray(new OWLIndividual[0]);
		int num = modelAndColorIndividual.length / 2;
		OWLDatatypeProperty hasColorHueProperty = model.getOWLDatatypeProperty("p5:hasColorHue");
		OWLDatatypeProperty hasColorSaturationProperty = model.getOWLDatatypeProperty("p5:hasColorSaturation");
		OWLDatatypeProperty hasColorValueProperty = model.getOWLDatatypeProperty("p5:hasColorValue");
		OWLDatatypeProperty hasLightNodeNameValueProperty = model.getOWLDatatypeProperty("p5:hasLightNodeName");
		for (int i = 0; i < num; i++) {
			Element ruleName = name.addElement("rule");
			ruleName.addAttribute("ruleType", "setColor");

			RDFSNamedClass modelClass = getClassFromIndividual(model, modelAndColorIndividual[2 * i]);

			if (modelClass.getBrowserText().contains("Light")) {
				ruleName.addAttribute("type", "lightColor");
				String lightNodeName = (String) modelAndColorIndividual[2 * i]
						.getPropertyValue(hasLightNodeNameValueProperty);
				ruleName.addAttribute("usedModelInMa", lightNodeName);
			} else {
				ruleName.addAttribute("type", "modelColor");
				String modelID = modelAndColorIndividual[2 * i].getBrowserText();
				// int modelIDBegin = modelID.lastIndexOf(":")+1;
				ruleName.addAttribute("usedModelID", modelID);
				// ����modelID�ҵ�model��ʵ��
				OWLIndividual modelNameTemp = getModelNameFromID(model, modelAndColorIndividual[2 * i]);
				String modelName = modelNameTemp.getBrowserText();
				// int modelNameBegin = modelName.lastIndexOf(":")+1;
				ruleName.addAttribute("usedModelInMa", modelName);
			}
			RDFSNamedClass colorClass = getClassFromIndividual(model, modelAndColorIndividual[2 * i + 1]);
			String colorName = colorClass.getBrowserText();
			int colorNameBegin = colorName.lastIndexOf("_") + 1;
			ruleName.addAttribute("color", colorName.substring(colorNameBegin, colorName.length()));
			Float colorHue = (Float) modelAndColorIndividual[2 * i + 1].getPropertyValue(hasColorHueProperty);
			ruleName.addAttribute("hue", Float.toString(colorHue));
			Float colorSaturation = (Float) modelAndColorIndividual[2 * i + 1]
					.getPropertyValue(hasColorSaturationProperty);
			ruleName.addAttribute("saturation", Float.toString(colorSaturation));
			Float colorValue = (Float) modelAndColorIndividual[2 * i + 1].getPropertyValue(hasColorValueProperty);
			ruleName.addAttribute("value", Float.toString(colorValue));

		}
		return doc;
	}

	/**
	 * 2013.2.26���� ��ӡģ��ָ����ɫ����
	 * 
	 * @param doc
	 *            xml�ĵ�
	 * @param modelname
	 *            ģ����
	 * @param colorname
	 *            ָ����ɫ
	 * @return ����xml�ĵ�
	 */
	public Document printRule(Document doc, String modelname, String modelid, String colorname, float h, float s,
			float v) {
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "setTargetModelColor");
		ruleName.addAttribute("usedModelID", modelid);
		ruleName.addAttribute("usedModelInMa", modelname);
		ruleName.addAttribute("color", colorname);
		ruleName.addAttribute("hue", Float.toString(h));
		ruleName.addAttribute("saturation", Float.toString(s));
		ruleName.addAttribute("value", Float.toString(v));
		return doc;
	}

	/**
	 * ��ӡ�ƹⲼ�ֹ���
	 * 
	 * @param doc
	 *            xml�ĵ�
	 * @param model
	 *            owlģ��
	 * @param lightLayout
	 *            �ƹⲼ��ʵ��
	 * @return ����xml�ĵ�
	 */
	public Document printRule(Document doc, OWLModel model, OWLIndividual lightLayout) {
		OWLDatatypeProperty hasLayoutMaProperty = model.getOWLDatatypeProperty("p5:hasLayoutMa");
		String hasLayoutMa = (String) lightLayout.getPropertyValue(hasLayoutMaProperty);
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "setColor");
		ruleName.addAttribute("type", "chooseLightLayout");
		ruleName.addAttribute("addModel", hasLayoutMa);
		return doc;
	}

	/**
	 * @@@@@@@@@@@@ ��ӡ������ͼ���� @param doc xml�ĵ�
	 * @param model
	 *            owlģ��
	 * @param character
	 *            ����ģ��
	 * @param textureType
	 *            ��ͼ����
	 * @param texturePath
	 *            ��ͼ·��
	 * @return ����xml�ĵ�
	 */
	public Document printRule(Document doc, OWLModel model, OWLIndividual characterID, String textureType,
			String texturePath) {
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "setColor");
		ruleName.addAttribute("type", "modelTexture");

		String modelID = characterID.getBrowserText();
		int modelIDBegin = modelID.lastIndexOf(":") + 1;
		ruleName.addAttribute("usedModelID", modelID.substring(modelIDBegin, modelID.length()));
		// ����modelID�ҵ�model��ʵ��
		OWLIndividual characterName = getModelNameFromID(model, characterID);
		String modelName = characterName.getBrowserText();
		int modelNameBegin = modelName.lastIndexOf(":") + 1;
		ruleName.addAttribute("usedModelInMa", modelName.substring(modelNameBegin, modelName.length()));
		if (textureType.contains("Floor")) {
			ruleName.addAttribute("textureType", textureType.substring(3));
		} else {
			ruleName.addAttribute("textureType", textureType.substring(3));
		}
		ruleName.addAttribute("texturePath", texturePath);

		return doc;
	}

	/**
	 * doc2XmlFile ��Document���󱣴�Ϊһ��xml�ļ�������
	 * 
	 * @return true:����ɹ� flase:ʧ��
	 * @param filename
	 *            ������ļ���
	 * @param document
	 *            ��Ҫ�����document����
	 */
	public boolean doc2XmlFile(Document document, String filename) {
		boolean flag = true;
		try {
			/* ��document�е�����д���ļ��� */
			// Ĭ��ΪUTF-8��ʽ��ָ��Ϊ"GB2312"
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("GB2312");
			XMLWriter writer = new XMLWriter(new FileWriter(new File(filename)), format);
			writer.write(document);
			writer.close();
		} catch (Exception ex) {
			flag = false;
			ex.printStackTrace();
		}
		return flag;
	}

	/**
	 * ����owl�ļ�
	 * 
	 * @param owlModel
	 */
	public void saveOWLFile(JenaOWLModel owlModel) {
		Collection errors = new ArrayList();
		owlModel.save(new File(fileName).toURI(), FileUtils.langXMLAbbrev, errors);
		System.out.println("File saved with " + errors.size() + " errors.");

	}

	/**
	 * 2013.2.26����
	 * 
	 * @param owlModel
	 */
	public Document setTargetModelColor(OWLModel owlModel, String ModelName, String modelid, String color0,
			Document doc) {
		String Tcolor = "p5:color_" + color0;
		OWLNamedClass testcolor = owlModel.getOWLNamedClass(Tcolor);
		Collection color = testcolor.getInstances(true);
		OWLIndividual[] ColorIndividual = (OWLIndividual[]) color.toArray(new OWLIndividual[0]);
		int colorNum = ColorIndividual.length;
		// System.out.println(colorNum);
		OWLDatatypeProperty hasColorHueProperty = owlModel.getOWLDatatypeProperty("p5:hasColorHue");
		OWLDatatypeProperty hasColorSaturationProperty = owlModel.getOWLDatatypeProperty("p5:hasColorSaturation");
		OWLDatatypeProperty hasColorValueProperty = owlModel.getOWLDatatypeProperty("p5:hasColorValue");
		int i = random.nextInt(colorNum);
		float h1, s1, v1;
		h1 = (Float) ColorIndividual[i].getPropertyValue(hasColorHueProperty);
		s1 = (Float) ColorIndividual[i].getPropertyValue(hasColorSaturationProperty);
		v1 = (Float) ColorIndividual[i].getPropertyValue(hasColorValueProperty);
		printRule(doc, ModelName, modelid, color0, h1, s1, v1);
		System.out.println("д��ָ��ģ�ͷ���");
		return doc;
	}

	/**
	 * 2013.6 ����ʱ���������Ϣ������ձ���
	 * 
	 * @param owlModel
	 * @param ma
	 *            maʵ��
	 * @param doc
	 *            XML�ĵ�
	 * @param timeandweather
	 * @return �����޸ĺ��XML�ĵ���δ���棩
	 */
	public Document setSkyBackground(OWLModel owlModel, Document doc, ArrayList<String> timeandweather) {
		String str = "p5:";
		String time = null, weather = null, cloud = null, fog = null;
		float TotalBrightness, SunBrightness, AirDensity = 0.5f, DustDensity = 0.1f;
		int UseCloud;
		float CloudDensity = 0.5f, CloudPower = 0.5f;
		float ALIntensity = 0.7f, DLIntensity = 0.7f, DLX = -65.6f, DLY = 70.0f, DLZ = -25.0f;
		float Brightnessratio = 1.0f;
		int hasweather = 0, hastime = 0, hascloud = 0, hasfog = 0;
		int i, Num;
		OWLDatatypeProperty TotalBrightnessProperty = owlModel.getOWLDatatypeProperty(str + "TotalBrightness");
		OWLDatatypeProperty SunBrightnessProperty = owlModel.getOWLDatatypeProperty(str + "SunBrightness");
		OWLDatatypeProperty AirDensityProperty = owlModel.getOWLDatatypeProperty(str + "AirDensity");
		OWLDatatypeProperty DustDensityProperty = owlModel.getOWLDatatypeProperty(str + "DustDensity");
		OWLDatatypeProperty UseCloudProperty = owlModel.getOWLDatatypeProperty(str + "UseCloudTexture");
		OWLDatatypeProperty CloudDensityProperty = owlModel.getOWLDatatypeProperty(str + "CloudDensity");
		OWLDatatypeProperty CloudPowerProperty = owlModel.getOWLDatatypeProperty(str + "CloudPower");
		OWLDatatypeProperty ALIntensityProperty = owlModel.getOWLDatatypeProperty(str + "AmbientLightIntensity");
		OWLDatatypeProperty DLIntensityProperty = owlModel.getOWLDatatypeProperty(str + "DirectionalLightIntensity");
		OWLDatatypeProperty DLXProperty = owlModel.getOWLDatatypeProperty(str + "DirectionalLightRotateX");
		OWLDatatypeProperty DLYProperty = owlModel.getOWLDatatypeProperty(str + "DirectionalLightRotateY");
		OWLDatatypeProperty DLZProperty = owlModel.getOWLDatatypeProperty(str + "DirectionalLightRotateZ");

		if (timeandweather.get(0) != null) {
			time = timeandweather.get(0);
			hastime = 1;
		}
		if (timeandweather.get(1) != null) {
			weather = timeandweather.get(1);
			hasweather = 1;
		}
		if (timeandweather.get(2) != null) {
			cloud = timeandweather.get(2);
			hascloud = 1;
		}
		if (timeandweather.get(3) != null) {
			fog = timeandweather.get(3);
			hasfog = 1;
		}
		System.out.println("0:" + timeandweather.get(0) + " has:" + hastime);
		System.out.println("1:" + timeandweather.get(1) + " has:" + hasweather);
		System.out.println("2:" + timeandweather.get(2) + " has:" + hascloud);
		System.out.println("3:" + timeandweather.get(3) + " has:" + hasfog);
		System.out.println("time:" + time);
		System.out.println("weather:" + weather);

		if (hasweather == 1) {
			OWLNamedClass weather1 = owlModel.getOWLNamedClass(weather);
			Collection weather2 = weather1.getInstances(true);
			OWLIndividual[] WeatherIndividual = (OWLIndividual[]) weather2.toArray(new OWLIndividual[0]);
			Num = WeatherIndividual.length;
			i = random.nextInt(Num);
			TotalBrightness = (Float) WeatherIndividual[i].getPropertyValue(TotalBrightnessProperty);
			SunBrightness = (Float) WeatherIndividual[i].getPropertyValue(SunBrightnessProperty);
			AirDensity = (Float) WeatherIndividual[i].getPropertyValue(AirDensityProperty);
			DustDensity = (Float) WeatherIndividual[i].getPropertyValue(DustDensityProperty);
			ALIntensity = (Float) WeatherIndividual[i].getPropertyValue(ALIntensityProperty);
			DLIntensity = (Float) WeatherIndividual[i].getPropertyValue(DLIntensityProperty);
			if (weather == "overcast") {
				Brightnessratio = 0.75f;
			}
			if (hastime == 0) {
				printWeatherRule(doc, weather, TotalBrightness, SunBrightness, AirDensity, DustDensity);
			}
		}
		if (hastime == 1) {
			OWLNamedClass time1 = owlModel.getOWLNamedClass(time);
			Collection time2 = time1.getInstances(true);
			OWLIndividual[] TimeIndividual = (OWLIndividual[]) time2.toArray(new OWLIndividual[0]);
			Num = TimeIndividual.length;
			i = random.nextInt(Num);
			TotalBrightness = (Float) TimeIndividual[i].getPropertyValue(TotalBrightnessProperty) * Brightnessratio;
			SunBrightness = (Float) TimeIndividual[i].getPropertyValue(SunBrightnessProperty) * Brightnessratio;
			ALIntensity = (Float) TimeIndividual[i].getPropertyValue(ALIntensityProperty);
			DLIntensity = (Float) TimeIndividual[i].getPropertyValue(DLIntensityProperty);
			DLX = (Float) TimeIndividual[i].getPropertyValue(DLXProperty);
			DLY = (Float) TimeIndividual[i].getPropertyValue(DLYProperty);
			DLZ = (Float) TimeIndividual[i].getPropertyValue(DLZProperty);
			if (hasweather == 0)
				printTimeRule(doc, time, TotalBrightness, SunBrightness, AirDensity, DustDensity);
			else
				printWeatherAndTimeRule(doc, weather, time, TotalBrightness, SunBrightness, AirDensity, DustDensity);
		}
		if (hascloud == 1) {
			OWLNamedClass cloud1 = owlModel.getOWLNamedClass(cloud);
			Collection cloud2 = cloud1.getInstances(true);
			OWLIndividual[] CloudIndividual = (OWLIndividual[]) cloud2.toArray(new OWLIndividual[0]);
			Num = CloudIndividual.length;
			i = random.nextInt(Num);
			UseCloud = (Integer) CloudIndividual[i].getPropertyValue(UseCloudProperty);
			if (UseCloud != 0) {
				CloudDensity = (Float) CloudIndividual[i].getPropertyValue(CloudDensityProperty);
				CloudPower = (Float) CloudIndividual[i].getPropertyValue(CloudPowerProperty);
			}
			printCloudRule(doc, UseCloud, CloudDensity, CloudPower);
		}
		/*
		 * if(hasfog==1){ double fogdensity=0.001; String fogtype;
		 * if(fog=="LightFog"){ fogtype="Fogthin"; OWLIndividual fog1 =
		 * owlModel.getOWLIndividual("p2:"+fogtype); OWLDatatypeProperty
		 * FogDensity = owlModel.getOWLDatatypeProperty("p2:fogdensity");
		 * fogdensity= (Float) fog1.getPropertyValue(FogDensity); } else
		 * if(fog=="HeavyFog"){ fogtype="Fogmedium"; OWLIndividual fog1 =
		 * owlModel.getOWLIndividual("p2:"+fogtype); OWLDatatypeProperty
		 * FogDensity = owlModel.getOWLDatatypeProperty("p2:fogdensity");
		 * fogdensity= (Float) fog1.getPropertyValue(FogDensity); } else return
		 * doc; float h1,s1,v1; String FOGCOLOR="p2:Fogcolor"; OWLNamedClass
		 * fog1 = owlModel.getOWLNamedClass(FOGCOLOR); Collection fog2
		 * =fog1.getInstances(true); OWLIndividual[] ColorIndividual =
		 * (OWLIndividual[]) fog2.toArray(new OWLIndividual[0]); Num =
		 * ColorIndividual.length; i=random.nextInt(Num); OWLDatatypeProperty
		 * hasColorH = owlModel.getOWLDatatypeProperty("p2:hascolorH");
		 * OWLDatatypeProperty hasColorS =
		 * owlModel.getOWLDatatypeProperty("p2:hascolorS"); OWLDatatypeProperty
		 * hasColorV = owlModel.getOWLDatatypeProperty("p2:hascolorV");
		 * h1=(Float) ColorIndividual[i].getPropertyValue(hasColorH); s1=(Float)
		 * ColorIndividual[i].getPropertyValue(hasColorS); v1=(Float)
		 * ColorIndividual[i].getPropertyValue(hasColorV); //String
		 * colorname=ColorIndividual[i].getBrowserText(); String
		 * colorname=ColorIndividual[i].getLocalName();
		 * printFogRule(doc,fogtype,colorname,fogdensity,h1,s1,v1); }
		 */

		printLightRule(doc, time, weather, ALIntensity, DLIntensity, DLX, DLY, DLZ);
		return doc;
	}

	/**
	 * 2013.6���� ��ӡ��ձ����滮
	 * 
	 * @param doc
	 *            xml�ĵ�
	 * @return ����xml�ĵ�
	 */
	public Document printWeatherRule(Document doc, String weather, float TotalBrightness, float SunBrightness,
			float AirDensity, float DustDensity) {
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "setBackground");
		ruleName.addAttribute("type", "Sky");
		ruleName.addAttribute("Weather", weather);
		ruleName.addAttribute("TotalBrightness", Double.toString(TotalBrightness));
		ruleName.addAttribute("SunBrightness", Double.toString(SunBrightness));
		ruleName.addAttribute("AirDensity", Double.toString(AirDensity));
		ruleName.addAttribute("DustDensity", Double.toString(DustDensity));
		return doc;
	}

	public Document printWeatherAndTimeRule(Document doc, String weather, String time, float TotalBrightness,
			float SunBrightness, float AirDensity, float DustDensity) {
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "setBackground");
		ruleName.addAttribute("type", "Sky");
		ruleName.addAttribute("Weather", weather);
		ruleName.addAttribute("Time", time);
		DecimalFormat dcmFmt = new DecimalFormat("0.0");
		ruleName.addAttribute("TotalBrightness", dcmFmt.format(TotalBrightness));
		ruleName.addAttribute("SunBrightness", dcmFmt.format(SunBrightness));
		ruleName.addAttribute("AirDensity", dcmFmt.format(AirDensity));
		ruleName.addAttribute("DustDensity", dcmFmt.format(DustDensity));
		return doc;
	}

	public Document printTimeRule(Document doc, String time, float TotalBrightness, float SunBrightness,
			float AirDensity, float DustDensity) {
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "setBackground");
		ruleName.addAttribute("type", "Sky");
		ruleName.addAttribute("Time", time);
		DecimalFormat dcmFmt = new DecimalFormat("0.0");
		ruleName.addAttribute("TotalBrightness", dcmFmt.format(TotalBrightness));
		ruleName.addAttribute("SunBrightness", dcmFmt.format(SunBrightness));
		ruleName.addAttribute("AirDensity", dcmFmt.format(AirDensity));
		ruleName.addAttribute("DustDensity", dcmFmt.format(DustDensity));
		return doc;
	}

	public Document printCloudRule(Document doc, int UseCloud, float CloudDensity, float CloudPower) {
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "setBackground");
		ruleName.addAttribute("type", "Cloud");
		ruleName.addAttribute("useCloudTexture", Integer.toString(UseCloud));
		ruleName.addAttribute("CloudDensity", Float.toString(CloudDensity));
		ruleName.addAttribute("CloudPower", Float.toString(CloudPower));
		return doc;
	}

	public Document printLightRule(Document doc, String time, String weather, float ALIntensity, float DLIntensity,
			float DLX, float DLY, float DLZ) {
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "setLight");
		ruleName.addAttribute("type", "OutdoorPlan1");
		if (time != null)
			ruleName.addAttribute("Time", time);
		if (weather != null)
			ruleName.addAttribute("Weather", weather);
		DecimalFormat dcmFmt = new DecimalFormat("0.0");
		ruleName.addAttribute("AmbientLightIntensity", Float.toString(ALIntensity));
		ruleName.addAttribute("DirectionalLightIntensity", Float.toString(DLIntensity));
		ruleName.addAttribute("DirectionalLightRotateX", dcmFmt.format(DLX));
		ruleName.addAttribute("DirectionalLightRotateY", dcmFmt.format(DLY));
		ruleName.addAttribute("DirectionalLightRotateZ", dcmFmt.format(DLZ));
		return doc;
	}

	public Document printFogRule(Document doc, String fogtype, String colorname, double fogdensity, float r, float g,
			float b) {
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "addFogToMa");
		ruleName.addAttribute("type", fogtype);
		DecimalFormat dcmFmt = new DecimalFormat("0.000");
		ruleName.addAttribute("fogDensity", dcmFmt.format(fogdensity));
		ruleName.addAttribute("color", colorname);
		ruleName.addAttribute("colorR", Float.toString(r));
		ruleName.addAttribute("colorG", Float.toString(g));
		ruleName.addAttribute("colorB", Float.toString(b));
		return doc;
	}
}
