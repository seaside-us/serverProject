package plot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.xml.parsers.ParserConfigurationException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;
import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLNamedObject;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;

public class FireworkInsert {
	private boolean isfwTypesetted = false;// ���ڱ���Ƿ����ú��̻���С����
	private boolean isfwColorsetted=false;//���ڱ���Ƿ����ú��̻���ɫ����

	/*
	 * public boolean isFireworkFlag() { return fireworkFlag; }
	 * 
	 * public void setFireworkFlag(boolean fireworkFlag) { this.fireworkFlag =
	 * fireworkFlag; }
	 */

	public Document fireworkInfer(ArrayList<String> List, OWLModel model, String maName, Document doc)
			throws OntologyLoadException, SWRLRuleEngineException {

	
		 //ϵͳ������
		 String str = "p9:";
		 // newFireworkǰ׺ 
		 String str1 = "";
		 
		 
		// ����������
		/*	String str = "";// newFireworkǰ׺
		String str1 = "p2:";// ģ�壬����ǰ׺
*/
		String ieTopic;// ie����
		String fwType = "";// �����̻���𣨴��У�С��
		String fwColor = "";// �����̻���ɫ��red,blue,green,yellow,pink,default��
		// Boolean isfwTypeSeted = false;// ������ԣ�fwType���Ƿ��Ѿ����ú�
		int fwIndividualsSuitableForTopicCounts = 0;// ����������̻�ʵ������֪ʶ���п��ܶ������͵��̻�ʵ����Ӧͬһ�����⣩
		// topicList���������
		ArrayList<String> topicList = new ArrayList<String>();
		// ���ڴ洢����IE������̻���ʵ��
		List<OWLIndividual> fwIndividualForTopicList = new ArrayList<OWLIndividual>();
		/*
		 * ��ȡdoc�ĸ��ڵ�
		 */
		Element rootName = doc.getRootElement();
		Element name = rootName.element("maName");
		String adlTopic = name.attributeValue("topic");
		System.out.println("adlTopic=" + adlTopic);// ������⣨��������������ϵģ����Ǿ����ʵ����
		/*
		 * ��ȡmaʵ��
		 */
		OWLIndividual maIndividual = model.getOWLIndividual(str1 + maName);
		if (maIndividual == null) {
			System.out.println("maya ʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ�����");
			return doc;
		} else
			System.out.println("��ȡʵ���ɹ���mayaʵ����" + maIndividual);
		// �õ��ĸ�������
		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
		OWLObjectProperty hasValueOfPlaceFlag = model.getOWLObjectProperty(str1 + "hasValueOfPlace");

		/*
		 * �����жϻ�ȡ��maʵ�������ڳ����������ⳡ��
		 */

		OWLIndividual individualOfPlaceFlag = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlaceFlag);
		if (individualOfPlaceFlag == null) {
			System.out.println("�޷���ȡʵ��" + maIndividual.getBrowserText()
					+ "��hasValueOfPlace����(�����������жϳ����������ڻ�������)��so �ܾ����꣡����֪ʶ���и������Ƿ���ڻ���ȷ��");
			return doc;
		} else {
			// �����҃Ȉ������t�ܽ^����̻�
			if (individualOfPlaceFlag.getBrowserText().equals("inDoorDescription")
					|| individualOfPlaceFlag.getBrowserText().equals("InWaterDescription")) {
				System.out.println("�˳���Ϊ���ڻ�ˮ�£���˲��ʺ�����̻���");
				return doc;
			}
		}
		/**
		 * ���´��뼴�Ǵ�������Ϊ��������
		 */

		// ����������Ϣ
		System.out.println("================���������⴦��================");
		System.out.println("================���ȴ���IE����================");
		ieTopic = doc.getRootElement().element("maName").attributeValue("topic");// ��ieTopic����Ϊ���ַ���
		/**
		 * ��adl�ĵ��г��������
		 */
		if (ieTopic.contains("Topic")) {
			System.out.println("IE Topic = " + ieTopic);
			topicList.add(ieTopic);// ��ӵ��������б���
			OWLNamedClass topic = model.getOWLNamedClass(str1 + ieTopic);
			System.out.println("topic:" + topic.getBrowserText());

			OWLObjectProperty fireworkSuitableForTopicProperty = model
					.getOWLObjectProperty(str + "fireworkSuitableForTopic");
			OWLObjectProperty fireworkColorSuitableForTopicProperty = model
					.getOWLObjectProperty(str + "fireworkColorSuitableForTopic");
			/*
			 * System.out .println("fireworkSuitableForTopicProperty==" +
			 * fireworkSuitableForTopicProperty.getBrowserText()); System.out
			 * .println("fireworkColorSuitableForTopicProperty==" +
			 * fireworkColorSuitableForTopicProperty.getBrowserText());
			 */
			OWLNamedClass fwTypeClass = model.getOWLNamedClass(str + "Type_firework");
			OWLNamedClass fwColorClass = model.getOWLNamedClass(str + "Color_firework");
			 System.out.println("fwType�ࣺ" + fwTypeClass);
			 System.out.println("fwColor" + fwColorClass);
			Collection fwTypeSubClass = fwTypeClass.getSubclasses(true);// ��ȡType_firework������
			Collection fwColorSubClass = fwColorClass.getSubclasses(true);// ��ȡColor_firework������
			
			
			 System.out.println("fwType���ܹ������ࣺ" + fwTypeSubClass.size());
			 System.out.println("fwColor���ܹ������ࣺ" + fwColorSubClass.size());
			 
			/**
			 * ����Type_firework�������===========================��ʼ========================
			 * ======== ===============
			 */
			for (Iterator it = fwTypeSubClass.iterator(); it.hasNext();) {
				OWLNamedClass xxsubclass = (OWLNamedClass) it.next();// ΪfwType���ĳ������
				// ��xxsubclass(�磺miniType,moderateType,splendidType��)û��fireworkSuitableForFirework���ԣ��������һ������
				if (xxsubclass.getSomeValuesFrom(fireworkSuitableForTopicProperty) == null)
					continue;
				// ��ȡfwTypeĳһ���ࣨ��miniType��getSomeValuesFrom��ֵ
				String result_getSomeValuesFrom = xxsubclass.getSomeValuesFrom(fireworkSuitableForTopicProperty)
						.getBrowserText();
				System.out.println("result_getSomeValuesFrom=" + result_getSomeValuesFrom);
				if (result_getSomeValuesFrom != null) {
					String hasValues = result_getSomeValuesFrom;
					// ==========��ʼ========getSomeValuesFrom��ֵ�ж���Ļ�=========================
					if (hasValues.contains("or")) {
						String[] hasValuesSpilt = hasValues.split("or");
						System.out.println("hasValuesSpilt.length=" + hasValuesSpilt.length);
						// System.out.println("hasValuesSpilt[0]="+hasValuesSpilt[0]);
						// �������еõ���getSomeValuesFrom�Ľ�����ж��Ƿ����������ȣ�����������������ie������̻�ʵ����firesize��������
						for (int j = 0; j < hasValuesSpilt.length; j++) {
							System.out.println("hasValuesSpilt[" + j + "]=" + hasValuesSpilt[j].toString().trim());
							OWLNamedClass xxtopicClass = model.getOWLNamedClass(hasValuesSpilt[j].toString().trim());

							// ������̻����������������
							if (xxtopicClass == null) {// ���������಻����
								continue;
							}
							// =================��ʼ======================�����������IE��ȡ������
							if (xxtopicClass.equalsStructurally(topic)) {
								System.out.println("��" + xxsubclass.getBrowserText() + "����ie�������");
								// ��ȡ���ࣨ�̻��ࣩ�µ�����ʵ��
								Collection subclassIndividuals = xxsubclass.getInstances(true);
								if (subclassIndividuals.size() != 0) {
									for (Iterator it1 = subclassIndividuals.iterator(); it1.hasNext();) {
										OWLIndividual xxType_1 = (OWLIndividual) it1.next();

										System.out.println("��" + xxsubclass.getBrowserText() + "���ʵ��Ϊ��"
												+ xxType_1.getBrowserText());
										OWLDatatypeProperty firesizeProperty = model
												.getOWLDatatypeProperty(str + "fireworksize");
										String fwtype = xxType_1.getPropertyValue(firesizeProperty).toString();
										fwType = fwtype;// ����̻���������ֵ
										System.out.println("fwType=" + fwType);
										/*
										 * doc = printFireworkRule(doc, fwType);
										 * return doc;
										 */
									}
								}
								isfwTypesetted = true;// �̻���С�����Ѿ����ú�
								break;
							}
							// =================����======================�����������IE��ȡ������
						}

					} // ==========����========getSomeValuesFrom��ֵ�ж���Ļ�=========================
					else if (hasValues != null) {// ��������or����ֻ��һ��ֵ��
						OWLNamedClass xxtopicClass = model.getOWLNamedClass(hasValues.trim());
						if (xxtopicClass.equalsStructurally(topic)) {
							System.out.println("��" + xxsubclass.getBrowserText() + "����ie�������");
							// ��ȡ���ࣨ�̻��ࣩ�µ�����ʵ��
							Collection subclassIndividuals = xxsubclass.getInstances(true);
							if (subclassIndividuals.size() != 0) {
								for (Iterator it1 = subclassIndividuals.iterator(); it1.hasNext();) {
									OWLIndividual xxType_1 = (OWLIndividual) it1.next();

									System.out.println(
											"��" + xxsubclass.getBrowserText() + "���ʵ��Ϊ��" + xxType_1.getBrowserText());
									OWLDatatypeProperty firesizeProperty = model
											.getOWLDatatypeProperty(str + "fireworksize");
									String fwtype = xxType_1.getPropertyValue(firesizeProperty).toString();
									fwType = fwtype;
									System.out.println("fwType=" + fwType);
									/*
									 * doc = printFireworkRule(doc, fwType);
									 * return doc;
									 */
								}
							}
							isfwTypesetted = true;// �̻���С�����Ѿ����ú�
						}
					}
				}
				if (isfwTypesetted)
					break;// ����̻���С�������úõĻ��Ͳ��ڱ���fwType�����������
			}
			/**
			 * ����Type_firework�������===========================����========================
			 * ======== ===============
			 */

			/**
			 * ����Color_firework�������===========================��ʼ=======================
			 * ========= ===============
			 */
			for (Iterator it = fwColorSubClass.iterator(); it.hasNext();) {
				OWLNamedClass xxsubclass = (OWLNamedClass) it.next();// ΪfwColor���ĳ������
				// ��xxsubclass(�磺red,blue,green,yellow,pink,default��)û��fireworkColorSuitableForTopic���ԣ��������һ������
				if (xxsubclass.getSomeValuesFrom(fireworkColorSuitableForTopicProperty) == null)
					continue;
				// ��ȡfwColorĳһ���ࣨ��red�ࣩgetSomeValuesFrom��ֵ
				String result_getSomeValuesFrom = xxsubclass.getSomeValuesFrom(fireworkColorSuitableForTopicProperty)
						.getBrowserText();
				// System.out.println("result_getSomeValuesFrom=" +
				// result_getSomeValuesFrom);
				if (result_getSomeValuesFrom != null) {
					String hasValues = result_getSomeValuesFrom;
					// ==========��ʼ========getSomeValuesFrom��ֵ�ж���Ļ�=========================
					if (hasValues.contains("or")) {
						String[] hasValuesSpilt = hasValues.split("or");
						System.out.println("hasValuesSpilt.length=" + hasValuesSpilt.length);
						// �������еõ���getSomeValuesFrom�Ľ�����ж��Ƿ����������ȣ�����������������ie������̻���ɫʵ����fireworkColor��������
						for (int j = 0; j < hasValuesSpilt.length; j++) {
							System.out.println("hasValuesSpilt[" + j + "]=" + hasValuesSpilt[j].toString().trim());
							OWLNamedClass xxtopicClass = model.getOWLNamedClass(hasValuesSpilt[j].toString().trim());

							// ������̻���ɫ���������������
							if (xxtopicClass == null) {// ���������಻����
								continue;
							}
							// =================��ʼ======================�����������IE��ȡ������
							if (xxtopicClass.equalsStructurally(topic)) {
								System.out.println("��" + xxsubclass.getBrowserText() + "����ie�������");
								// ��ȡ���ࣨ��red�ࣩ�µ�����ʵ��
								Collection subclassIndividuals = xxsubclass.getInstances(true);
								if (subclassIndividuals.size() != 0) {
									for (Iterator it1 = subclassIndividuals.iterator(); it1.hasNext();) {
										OWLIndividual xxColor = (OWLIndividual) it1.next();

										System.out.println("��" + xxsubclass.getBrowserText() + "���ʵ��Ϊ��"
												+ xxColor.getBrowserText());
										OWLDatatypeProperty fireworkcolorProperty = model
												.getOWLDatatypeProperty(str + "fireworkcolor");
										String fwcolor = xxColor.getPropertyValue(fireworkcolorProperty).toString();
										fwColor = fwcolor;
										System.out.println("fwColor=" + fwColor);// ����̻���ɫ����ֵ

									}
								}
								isfwColorsetted=true;
								break;//break the former for_loop
								
							}
							// =================����======================�����������IE��ȡ������
						}

					} // ==========����========getSomeValuesFrom��ֵ�ж���Ļ�=========================
					else if (hasValues != null) {// ��������or����ֻ��һ��ֵ��
						OWLNamedClass xxtopicClass = model.getOWLNamedClass(hasValues.trim());
						if (xxtopicClass.equalsStructurally(topic)) {
							System.out.println("��" + xxsubclass.getBrowserText() + "����ie�������");
							// ��ȡ���ࣨ��red�ࣩ�µ�����ʵ��
							Collection subclassIndividuals = xxsubclass.getInstances(true);
							if (subclassIndividuals.size() != 0) {
								for (Iterator it1 = subclassIndividuals.iterator(); it1.hasNext();) {
									OWLIndividual xxColor = (OWLIndividual) it1.next();

									System.out.println(
											"��" + xxsubclass.getBrowserText() + "���ʵ��Ϊ��" + xxColor.getBrowserText());
									OWLDatatypeProperty fireworkcolorProperty = model
											.getOWLDatatypeProperty(str + "fireworkcolor");
									String fwcolor = xxColor.getPropertyValue(fireworkcolorProperty).toString();
									fwColor = fwcolor;
									System.out.println("fwColor=" + fwColor);

								}
							}
							isfwColorsetted=true;
						}
					}
				}
				if (isfwColorsetted)
					break;// ����̻���ɫ�������úõĻ��Ͳ��ڱ���fwColor�����������)
			}
			/**
			 * ֻ��fwType��ֵ�ɹ����Ż����doc = printFireworkRule(doc, fwType, fwColor)��������̻�����
			 */
			if(!fwType.equals("")){
				if(fwColor.equals("")){
					fwColor="default";
				}
				System.out.println("fwType="+fwType+",fwColor="+fwColor);
				doc = printFireworkRule(doc, fwType, fwColor);
				return doc;
			}
			/**
			 * ����fwColor�������===========================����=======================
			 * ========= ===============
			 */
		} else
			System.out.println("=================����Ϊ��================");

		// ==============================����ģ�崦��=======================================================");

		System.out.println("=======�������⿼�ǲ����̻����ֿ���ģ���Ƿ��ʺϷ��̻�===============    ");
		// ������ģ�������
		if (List == null)
			System.out.println("=============ģ��Ϊ��==================");
		else {
			String strMuban = null;
			String strMubanIndividual = null;

			int templateCount = List.size();// ͳ�ƴ�������ģ����
			// System.out.print(listSize + "\n");
			String[] listToStr = new String[templateCount]; // ��arraylistת��Ϊstring[]
			String[] templateList = new String[templateCount]; // �洢ģ����
			String[] templateIndividualList = new String[templateCount]; // �洢ģ����ʵ��
			// System.out.println("templateCount" + templateCount);
			/**
			 * ����������ģ��List�����ģ�����б��ģ��ʵ���б�
			 */
			listToStr = (String[]) List.toArray(new String[templateCount]);// ������(String[])
			for (int i = 0; i < listToStr.length; i++) {
				// System.out.println("listToStr:" + listToStr);
				String str2 = listToStr[i];
				int pos = str2.indexOf(":");
				// =====================���봦=====ģ��
				templateList[i] = (String) str2.subSequence(0, pos);
				// =====================�����====ʵ��
				templateIndividualList[i] = (String) str2.subSequence(pos + 1, str2.length());
			}
			// ����ģ������,������ÿһ��Ԫ�ض�Ҫ��fwType����ȥƥ��
			for (int j = 0; j < templateCount; j++) {
				OWLNamedClass xxTemplateListClass = model.getOWLNamedClass(str1 + templateList[j].toString());// ��������ģ����
				OWLNamedClass fwTypeClass = model.getOWLNamedClass(str + "Type_firework");
				
				Collection fwTypeSubClass = fwTypeClass.getSubclasses(true);// ��ȡType_firework������
				
				OWLObjectProperty fireworkSuitableForTemplateProperty = model
						.getOWLObjectProperty(str + "fireworkSuitableForTemplate");
				/**
				 * ����fwType�������===========================��ʼ====================
				 * ======== ==== ===============
				 */
				for (Iterator it = fwTypeSubClass.iterator(); it.hasNext();) {
					OWLNamedClass xxsubclass = (OWLNamedClass) it.next();// ΪfwType���ĳ������
					System.out.println(xxsubclass.getBrowserText().trim());
					// ��xxsubclass(�磺miniType,moderateType,splendidType��)û��fireworkSuitableForFirework���ԣ��������һ������
					if (xxsubclass.getSomeValuesFrom(fireworkSuitableForTemplateProperty) == null)
						continue;
					// ��ȡfwTypeĳһ���ࣨ��miniType��getSomeValuesFrom��ֵ
					String result_getSomeValuesFrom = xxsubclass.getSomeValuesFrom(fireworkSuitableForTemplateProperty)
							.getBrowserText();
					// System.out.println("result_getSomeValuesFrom=" +
					// result_getSomeValuesFrom);
					if (result_getSomeValuesFrom != null) {
						String hasValues = result_getSomeValuesFrom;
						// ==========��ʼ========getSomeValuesFrom��ֵ�ж���Ļ�=========================
						if (hasValues.contains("or")) {
							String[] hasValuesSpilt = hasValues.split("or");
							System.out.println("hasValuesSpilt.lenght="+hasValuesSpilt.length);
							// �������еõ���getSomeValuesFrom�Ľ�����ж��Ƿ��ģ������ȣ�����������������ie������̻�ʵ����firesize��������
							for (int t = 0; t < hasValuesSpilt.length; t++) {
								System.out.println("hasValuesSpilt[" + t + "]=" + hasValuesSpilt[t].toString().trim());
								OWLNamedClass xxtemplateClass = model
										.getOWLNamedClass(hasValuesSpilt[t].toString().trim());
								// System.out.println(xxtopicClass.toString());//
								// ������̻����������������
								if (xxtemplateClass == null) {// ����ģ���಻����
									continue;
								}
								// =================��ʼ======================����������ģ������̻�ĳһʵ��������ģ��
								if (xxtemplateClass.equalsStructurally(xxTemplateListClass)) {

									// ��ȡ���ࣨ�̻��ࣩ�µ�����ʵ��
									Collection subclassIndividuals = xxsubclass.getInstances(true);
									if (subclassIndividuals.size() != 0) {
										for (Iterator it1 = subclassIndividuals.iterator(); it1.hasNext();) {
											OWLIndividual xxType_1 = (OWLIndividual) it1.next();

											System.out.println("��" + xxTemplateListClass.getBrowserText() + "ģ��ƥ���̻���С��"
													+ xxsubclass.getBrowserText());
											OWLDatatypeProperty firesizeProperty = model
													.getOWLDatatypeProperty(str + "fireworksize");
											String fwtype = xxType_1.getPropertyValue(firesizeProperty).toString();
											fwType = fwtype;
											System.out.println("fwType=" + fwType);
										}
									}
									isfwTypesetted=true;
									break;
								} // =================����======================�����������IE��ȡ������

							}

						} // ==========����========getSomeValuesFrom��ֵ�ж���Ļ�=========================
						else if (hasValues != null) {// ��������or����ֻ��һ��ֵ��
							OWLNamedClass xxtopicClass = model.getOWLNamedClass(hasValues.trim());
							if (xxtopicClass.equalsStructurally(xxTemplateListClass)) {
								// ��ȡ���ࣨ�̻��ࣩ�µ�����ʵ��
								Collection subclassIndividuals = xxsubclass.getInstances(true);
								if (subclassIndividuals.size() != 0) {
									for (Iterator it1 = subclassIndividuals.iterator(); it1.hasNext();) {
										OWLIndividual xxType_1 = (OWLIndividual) it1.next();

										System.out.println("��" + xxTemplateListClass.getBrowserText() + "ģ��ƥ���̻���С��"
												+ xxsubclass.getBrowserText());
										OWLDatatypeProperty firesizeProperty = model
												.getOWLDatatypeProperty(str + "fireworksize");
										String fwtype = xxType_1.getPropertyValue(firesizeProperty).toString();
										fwType = fwtype;
										System.out.println("fwType=" + fwType);
									}
								}
								isfwTypesetted=true;
							}
						}
					}
					if(isfwTypesetted)
						break;//���ڱ���ģ���������Ԫ��
				}
				/**
				 * ����fwType�������===========================����====================
				 * ======== ==== ===============
				 */	
				if(isfwTypesetted)
					break;//���ڱ���ģ���������Ԫ��
			}

			// ͬ������ģ������,������ÿһ��Ԫ�ض�Ҫ��fwColor����ȥƥ��
			for (int j = 0; j < templateCount; j++) {
				OWLNamedClass xxTemplateListClass = model.getOWLNamedClass(str1 + templateList[j].toString());// ��������ģ����
				OWLNamedClass fwColorClass = model.getOWLNamedClass(str + "Color_firework");
				Collection fwColorSubClass = fwColorClass.getSubclasses(true);// ��ȡColor_firework������
				OWLObjectProperty fireworkColorSuitableForTemplateProperty = model
						.getOWLObjectProperty(str + "fireworkColorSuitableForTemplate");
				/**
				 * ����fwColor�������===========================��ʼ===================
				 * ========= ==== ===============
				 */
				for (Iterator it = fwColorSubClass.iterator(); it.hasNext();) {
					OWLNamedClass xxsubclass = (OWLNamedClass) it.next();// ΪfwColor���ĳ������
					System.out.println(xxsubclass.getBrowserText().trim());
					// ��xxsubclass(�磺red��)û��fireworkColorSuitableForFirework���ԣ��������һ������
					if (xxsubclass.getSomeValuesFrom(fireworkColorSuitableForTemplateProperty) == null)
						continue;
					// ��ȡfwColorĳһ���ࣨ��red��getSomeValuesFrom��ֵ
					String result_getSomeValuesFrom = xxsubclass
							.getSomeValuesFrom(fireworkColorSuitableForTemplateProperty).getBrowserText();
					// System.out.println("result_getSomeValuesFrom=" +
					// result_getSomeValuesFrom);
					if (result_getSomeValuesFrom != null) {
						String hasValues = result_getSomeValuesFrom;
						// ==========��ʼ========getSomeValuesFrom��ֵ�ж���Ļ�=========================
						if (hasValues.contains("or")) {
							String[] hasValuesSpilt = hasValues.split("or");
							// System.out.println("hasValuesSpilt[0]="+hasValuesSpilt[0]);
							// �������еõ���getSomeValuesFrom�Ľ�����ж��Ƿ��ģ������ȣ�����������������ie������̻���ɫʵ����fireworkColor��������
							for (int t = 0; t < hasValuesSpilt.length; t++) {
								System.out.println("hasValuesSpilt[" + t + "]=" + hasValuesSpilt[t].toString().trim());
								OWLNamedClass xxtemplateClass = model
										.getOWLNamedClass(hasValuesSpilt[t].toString().trim());
								// System.out.println(xxtopicClass.toString());
								if (xxtemplateClass == null) {// ����ģ���಻����
									continue;
								}
								// =================��ʼ======================����������ģ������̻�ĳһʵ��������ģ��
								if (xxtemplateClass.equalsStructurally(xxTemplateListClass)) {

									// ��ȡ���ࣨ�̻���ɫ�ࣩ�µ�����ʵ��
									Collection subclassIndividuals = xxsubclass.getInstances(true);
									if (subclassIndividuals.size() != 0) {
										for (Iterator it1 = subclassIndividuals.iterator(); it1.hasNext();) {
											OWLIndividual xxColor = (OWLIndividual) it1.next();
											System.out.println("��" + xxTemplateListClass.getBrowserText() + "ģ��ƥ���̻���ɫ��"
													+ xxsubclass.getBrowserText());

											OWLDatatypeProperty fireworkcolorProperty = model
													.getOWLDatatypeProperty(str + "fireworkcolor");
											String fwcolor = xxColor.getPropertyValue(fireworkcolorProperty).toString();
											fwColor = fwcolor;
											System.out.println("fwColor=" + fwColor);
										}
									}
									isfwColorsetted=true;
									break;
								} // =================����======================�����������IE��ȡ������

							}

						} // ==========����========getSomeValuesFrom��ֵ�ж���Ļ�=========================
						else if (hasValues != null) {// ��������or����ֻ��һ��ֵ��
							OWLNamedClass xxtemplateClass = model.getOWLNamedClass(hasValues.trim());
							System.out.println(xxtemplateClass.getBrowserText().trim());
							if (xxtemplateClass.equalsStructurally(xxTemplateListClass)) {
								// ��ȡ���ࣨ�̻���ɫ�ࣩ�µ�����ʵ��
								Collection subclassIndividuals = xxsubclass.getInstances(true);
								if (subclassIndividuals.size() != 0) {
									for (Iterator it1 = subclassIndividuals.iterator(); it1.hasNext();) {
										OWLIndividual xxColor = (OWLIndividual) it1.next();

										System.out.println("��" + xxTemplateListClass.getBrowserText() + "ģ��ƥ���̻���ɫ��"
												+ xxsubclass.getBrowserText());
										OWLDatatypeProperty fireworkcolorProperty = model
												.getOWLDatatypeProperty(str + "fireworkcolor");
										String fwcolor = xxColor.getPropertyValue(fireworkcolorProperty).toString();
										fwColor = fwcolor;
										System.out.println("fwColor=" + fwColor);		
									}
								}
								isfwColorsetted=true;
							}
						}

					}
					if(isfwColorsetted)
						break;//���ڱ���ģ���������Ԫ��
				}
				/**
				 * ����fwColor�������===========================����===================
				 * ========= ==== ===============
				 */
				if(isfwColorsetted)
					break;//���ڱ���ģ���������Ԫ��
			}
			/**
			 * ֻ��fwType��ֵ�ɹ����Ż����doc = printFireworkRule(doc, fwType, fwColor)��������̻�����
			 */
			if(!fwType.equals("")){
				if(fwColor.equals("")){
					fwColor="default";
				}
				System.out.println("fwType="+fwType+",fwColor="+fwColor);
				doc = printFireworkRule(doc, fwType, fwColor);
				return doc;
			}
			// ==============================ģ�崦�����=======================================================");
		}
		return doc;
	}

	public Document printFireworkRule(Document doc, String fireworkType, String fireworkColor) {
		System.out.println("=====================��ʼ����xml-rule======================");
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "addFireworkToMa");
		ruleName.addAttribute("fireworkType", fireworkType);
		ruleName.addAttribute("fireworkColor", fireworkColor);
		System.out.println("=====================��������xml-rule======================");
		return doc;
	}

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException,
			JDOMException, DocumentException, OntologyLoadException, SWRLRuleEngineException {

		// String owlPath = "file:///C:/ontologyOWL/AllOwlFile/sumo_phone3.owl";
		String owlPath = "file:///C:/FireworkOWL/FireworkOWL.owl";
		OWLModel model = ProtegeOWL.createJenaOWLModelFromURI(owlPath);
		ArrayList<String> alist = new ArrayList<String>();
		alist.add("MissTemplate:missTemplate");
		/*alist.add("LikeTemplate:likeTemplate");
		alist.add("PromisesTemplate:promisesTemplate");
*/
		File file = new File("F:/TestFilework/adl_result.xml");
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(file);

		System.out.println("��ʼ!");
		FireworkInsert firework = new FireworkInsert();
		Document document1 = firework.fireworkInfer(alist, model, "play_football.ma", document);
		XMLWriter writer = new XMLWriter(new FileWriter("F:/TestFilework/testFirework.xml"));
		writer.write(document1);
		System.out.println("������");
		writer.close();
	}

}
