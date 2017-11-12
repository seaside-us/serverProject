package plot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.hp.hpl.jena.sparql.pfunction.library.container;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngineFactory;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;

public class LightInsert {

	static Logger logger = Logger.getLogger(LightInsert.class.getName());

	/**
	 * ͨ��ADL�滮��ontology�еĶ������ԣ��˳����Ƕ�ȡָ���������ʵ�� 20160310 22:08 ��������������ģ�����
	 * 20160314 ����Ƶ�˼· ���⡢ģ��
	 * 
	 * 
	 * @param list
	 * @param model
	 * @param maName
	 * @param doc
	 * @return
	 * @throws SWRLRuleEngineException
	 */

	public Document LightInfer(ArrayList<String> list, OWLModel model, String maName, Document doc)
			throws SWRLRuleEngineException {

		// Ϊǰ׺��־ ���³�����Ҫ��� p7:
		String str = "p7:";
		// ����� 20160310 ���³���ǰ��Ҫɾ��p2:
		String str1 = "";

		Collection mapToWindValues = null;
		// String[] LightTopicList = new String[5];// �����洢��ȡ�����ķ��������ģ��ĵƹ�
		List<OWLIndividual> LightTopicList1 = new ArrayList<OWLIndividual>();
		String[] LightTemplateList = new String[5];// �����洢��ȡ�����ķ��������ģ��ĵƹ�
		//
		// ==============�����Կ��ܷϳ�
		String[] LightList = new String[5];
		// int LightFromInfoNumber = 0;// ���ճ�ȡ�ķ��ϵĵƹ������
		int LightFromTopNumber = 0;// ������鵽�Ƶĸ���
		int LightFromTempNumber = 0; // ��ģ��鵽�Ƶĸ���

		// topicList�������
		ArrayList topicList = new ArrayList();
		
		//�����洢ģ��   2016.9.28
		List<String> templateVolumeLight =  new ArrayList<String>();

		// ��ȡmayaʵ��
		// System.out.println("��ʼ��ȡʵ��");
		OWLIndividual maIndividual = model.getOWLIndividual(str1 + maName);
		if (null == maIndividual) {
			System.out.println("maya ʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ�����");
		}
		System.out.println("��ȡʵ���ɹ���mayaʵ����" + maIndividual);

		OWLDatatypeProperty maframenumber = model.getOWLDatatypeProperty(str1 + "maFrameNumber");
		int framenum;
		framenum = (Integer) maIndividual.getPropertyValue(maframenumber);// 300

		System.out.println("==================����֡��=========================" + framenum);

		// ========================================20160313==========================================================
		// 20160313��ʵ��
		System.out.println("======================================================================20160313");

		// ���ڵƹ� lightName1 AmbientLightIn lightName2 PointLightIn
		String AmbientLightIn = null;
		String PointLightIn = null;
		// ����ƹ� lightName3 AmbientLightOut lightName4 PointLightOut
		String AmbientLightOut = null;
		String PointLightOut = null;
		// ������
		// int lightNumber = 0;
		String autoGeneration = "yes";
		
		OWLObjectProperty hasValueOfPlace = model.getOWLObjectProperty(str1 + "hasValueOfPlace");
		OWLIndividual individualOfPlace = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlace);

		
		//===============  2016.5.22  �ж��Ƿ������ڳ���
		Boolean roomIn = false;
		
		// ====================== 2016.4.25 �ų�����������������ĳ���

		if (maName.equals("AprilFoolsDay.ma") || maName.equals("coffee_shop_in.ma")
				|| maName.equals("coffee_shop_out.ma") || maName.equals("miss.ma") || maName.equals("schoolroomIn.ma")
				|| maName.equals("schoolroomOut.ma") || maName.equals("SummerHoliday.ma")
				|| maName.equals("WinterHoliday.ma") || maName.equals("LanternFestival.ma")) {
//			AmbientLightIn = "ambientLight";
			PointLightIn = "pointLight";
			AmbientLightOut = "ambientLight";
			PointLightOut = "pointLight";

		}else if(maName.equals("room1.ma") || maName.equals("room2.ma") || maName.equals("night.ma")){
			AmbientLightIn = "ambientLight";
			PointLightIn = "spotLight";
			autoGeneration = "no";
			
		} else {
			
			//��������    2016.4.25
			
			// ���������ʵ��û��==hasValueOfPlace ����
			if (individualOfPlace != null) {
				System.out.println(individualOfPlace.getBrowserText());

				OWLObjectProperty lightLayoutContainLight = model.getOWLObjectProperty(str + "lightLayoutContainLight");
				// OWLIndividual individualOfLayout = (OWLIndividual)
				// individualOfPlace.getPropertyValue(hasValueOfPlace);
				System.out.println("lightLayoutContainLight=" + lightLayoutContainLight);

				OWLObjectProperty lightLayoutSuitableMaPlace = model
						.getOWLObjectProperty(str + "lightLayoutSuitableMaPlace");

				// ����LightLayout,Ȼ���ҵ�ֵ��Ӧ�ļ���
				OWLNamedClass LightLayoutClass = model.getOWLNamedClass(str + "LightLayout");
				Collection LightLayoutSubClass = LightLayoutClass.getSubclasses(true);
				for (Iterator it = LightLayoutSubClass.iterator(); it.hasNext();) {
					OWLNamedClass subLayoutclass = (OWLNamedClass) it.next();
					OWLNamedClass classname = (OWLNamedClass) LightLayoutClass
							.getSomeValuesFrom(lightLayoutSuitableMaPlace);
					Collection subLayoutclassIndiviual = null;
					// ��subclass�µ�ʵ��,������topic�µ�ʵ��
					subLayoutclassIndiviual = subLayoutclass.getInstances(true);
					if (subLayoutclassIndiviual.size() != 0) {
						for (Iterator it1 = subLayoutclassIndiviual.iterator(); it1.hasNext();) {
							OWLIndividual LightLayoutIndiviual = (OWLIndividual) it1.next();

							if (LightLayoutClass.getSomeValuesFrom(lightLayoutSuitableMaPlace) == null) {
								continue;
							}

							OWLIndividual ValueOfLightLayoutIndiviual = (OWLIndividual) LightLayoutIndiviual
									.getPropertyValue(lightLayoutSuitableMaPlace);

							if (LightLayoutIndiviual.getPropertyValue(lightLayoutSuitableMaPlace) == null) {
								continue;
							}

							if ((LightLayoutIndiviual.getPropertyValue(lightLayoutSuitableMaPlace))
									.equals(individualOfPlace)) {
								System.out.println("=======");
								System.out.println(LightLayoutIndiviual.getBrowserText());
								System.out.println(individualOfPlace.getBrowserText());
								Collection<OWLIndividual> individuals = (Collection<OWLIndividual>) LightLayoutIndiviual
										.getPropertyValues(lightLayoutContainLight);
								for (Iterator it2 = individuals.iterator(); it2.hasNext();) {
									OWLIndividual LightIndiviual = (OWLIndividual) it2.next();
									System.out.println("LightIndiviual:" + LightIndiviual.getBrowserText());

									if (LightIndiviual.getBrowserText().contains("ambientLightIn")) {
										AmbientLightIn = "ambientLight";
										// 2016.5.22
										roomIn = true;
									} else if (LightIndiviual.getBrowserText().contains("pointLightIn")) {
										PointLightIn = "pointLight";
									} else if (LightIndiviual.getBrowserText().contains("ambientLightOut")) {
										AmbientLightOut = "ambientLight";
									} else if (LightIndiviual.getBrowserText().contains("pointLightOut")) {
										PointLightOut = "pointLight";
									}

								}

								System.out.println("==============");
							}

						}
					}

				}

			}

		}

		System.out.println(
				"============" + AmbientLightIn + " " + PointLightIn + " " + AmbientLightOut + " " + PointLightOut);

		System.out.println(
				"======================       ����                   ����         ��ʼ              ======================================");

		// �ж�ʱ��
		String isDynamic1 = "";
		String intensity1 = "";
		String intensity2 = "";
		OWLObjectProperty hasValueOfTime = model.getOWLObjectProperty(str1 + "hasValueOfTime");
		OWLIndividual individualOfTime = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfTime);
		// System.out.println("individualOfTime==" +
		// individualOfTime.getBrowserText());
		// �ж� �����Ƿ��� hasValueOfTime �������
		if (individualOfTime == null) {
			isDynamic1 = "no";
			intensity1 = "normal";

		} else {

			if (individualOfTime.getBrowserText().equals("p2:dayTimeDescription")) {
				isDynamic1 = "no";
				intensity1 = "normal";

			} else if (individualOfTime.getBrowserText().equals("p2:nightTimeDescription")) {
				isDynamic1 = "yes";
				intensity1 = "strong";
				intensity2 = "feebler";

			} else if (individualOfTime.getBrowserText().equals("p2:moringTimeDescription")) {
				isDynamic1 = "yes";
				intensity1 = "feeble";
				intensity2 = "strong";
			} else if (individualOfTime.getBrowserText().equals("p2:eveningTimeDescription")) {
				isDynamic1 = "yes";
				intensity1 = "normal";
				intensity2 = "feeble";
			} else {
				isDynamic1 = "no";
				intensity1 = "normal";
			}

		}

		System.out.println("intensity1:" + intensity1 + "intensity2:" + intensity2);

		System.out.println(
				"======================       ����                   ����         ����              ======================================");

		System.out.println(
				"======================       ģ��                   ����         ��ʼ              ======================================");

		String intensity_1 = "";
		String intensity_2 = "";
		if (true) {
			System.out.println("-------------ģ�崦��---------------");

			// ������ģ�������
			int listSize = 0;

			// listΪ��������ģ��
			if (list != null) {
				listSize = list.size();// ģ�����
			} else
				System.out.println("δ���ݽ����κ�ģ��");

			if (listSize > 0) {

				System.out.println("���ݽ�����ģ�����Ϊ��" + listSize + "�����ֱ��ǣ�" + list);
				String strMuban = null;
				String strMubanIndividual = null;

				// ========����ģ�����===================����������ģ�� ,�ֳ� ģ�塢ʵ��
				int listSizeMuban = list.size();
				System.out.print(listSize + "\n");
				String[] listToStr = new String[listSizeMuban]; // ��arraylistת��Ϊstring[]
				String[] templateList = new String[listSizeMuban]; // ������template��Ϣ
				String[] templateListIndividual = new String[listSizeMuban]; // ������template��individual��Ϣ
				System.out.println("listSizeMuban" + listSizeMuban);
				if (listSizeMuban != 0) {
					listToStr = (String[]) list.toArray(new String[listSizeMuban]);// ������(String[])
					for (int i = 0; i < listToStr.length; i++) {
						System.out.println("listToStr:" + listToStr);
						String str2 = listToStr[i];
						int pos = str2.indexOf(":");
						templateList[i] = (String) str2.subSequence(0, pos);
						System.out.println("templateList[" + i + "]=" + templateList[i]);
						// =====================���봦=====ģ��
						strMuban = templateList[i].trim();
						// =====================�����====ʵ��
						templateListIndividual[i] = (String) str2.subSequence(pos + 1, str2.length());
						strMubanIndividual = templateListIndividual[i].trim();
						System.out.println("strMubanIndividual ===" + strMubanIndividual);
					}
				}

				System.out.println("strMuban===" + strMuban);
				OWLNamedClass strMubanClass = model.getOWLNamedClass(str1 + strMuban);
				System.out.println("strMubanClass===" + strMubanClass.getBrowserText());

				// ========����ģ�����================����������ģ�� ,�ֳ� ģ�塢ʵ��

				// ====================20160314======================
				// ADL�����������Light�е���һ��
				Collection strMubanClassIndiviual = null;
				// ��subclass�µ�ʵ��,������topic�µ�ʵ��
				strMubanClassIndiviual = strMubanClass.getInstances(true);
				System.out.println("==================individual================");
				System.out.println("strMubanClassIndiviual=" + strMubanClassIndiviual.size());
				for (Iterator it = strMubanClassIndiviual.iterator(); it.hasNext();) {
					OWLIndividual LightIndiviual = (OWLIndividual) it.next();
					// System.out.println("LightIndiviual===" +
					// LightIndiviual.getBrowserText());
					// �ҵ����������ʵ���봫����ʵ��һ��
					if (LightIndiviual.getBrowserText().equals(str1 + strMubanIndividual)) {

						// �ҵ���lightʵ�������������ֵΪ ����ʵ����
						OWLNamedClass NewLightClass = model.getOWLNamedClass(str + "NewLight");
						Collection NewLightSubClass = NewLightClass.getSubclasses(true);
						System.out.println("NewLightSubClass size " + NewLightSubClass.size());

						for (Iterator it3 = NewLightSubClass.iterator(); it3.hasNext();) {

							OWLNamedClass subclass = (OWLNamedClass) it3.next();
							OWLObjectProperty lightIntensityForTopicTemplate = model
									.getOWLObjectProperty(str + "lightIntensitySuitableForTemplate");

							if (lightIntensityForTopicTemplate == null) {
								continue;
							}

							if (subclass.getSomeValuesFrom(lightIntensityForTopicTemplate) == null) {
								continue;
							}

							// System.out.println("subclass==" +
							// subclass.getBrowserText());
							RDFResource str2 = subclass.getSomeValuesFrom(lightIntensityForTopicTemplate);
							if (str2 != null) {
								String hasValues = str2.getBrowserText();
								// System.out.println("hasValues:" + hasValues);
								if (hasValues.contains("or") & !hasValues.equals(str1 + "MorningTemplate")) {
									String[] hasValuesSpilt = hasValues.split("or");
									if (hasValuesSpilt.length > 0) {
										// �ж��Ƿ��ж��ģ���Ӧ
										for (int j = 0; j < hasValuesSpilt.length; j++) {
											// System.out.println("hasValuesSpilt.length:"
											// + hasValuesSpilt.length);
											// System.out.println(hasValuesSpilt[j].toString());
											// System.out.println("hasValuesSpilt[j].toString().trim()"
											// +hasValuesSpilt[j].toString().trim());
											// System.out.println("�ص����ַ���" +
											// hasValuesSpilt[j].toString().trim());
											OWLNamedClass strLightClass = model
													.getOWLNamedClass(hasValuesSpilt[j].toString().trim());
											if (strLightClass == null) {
												continue;
											}
											// System.out.println("strLightClass=="
											// +
											// strLightClass.getBrowserText());
											if (strLightClass.equalsStructurally(strMubanClass)) {

												Collection subclassIndiviual1 = null;
												// ��subclass�µ�ʵ��,������topic�µ�ʵ��
												subclassIndiviual1 = subclass.getInstances(true);
												if (subclassIndiviual1.size() != 0) {
													for (Iterator it1 = subclassIndiviual1.iterator(); it1.hasNext();) {
														OWLIndividual LightIndiviual0 = (OWLIndividual) it1.next();
														// System.out.println(
														// "LightIndiviual:" +
														// LightIndiviual0.getBrowserText());
														LightFromTempNumber++;
														LightTemplateList[LightFromTempNumber] = LightIndiviual0
																.getBrowserText();

														// ��������
														OWLDatatypeProperty isDynamic = model
																.getOWLDatatypeProperty(str + "isDynamic");
														String str333 = (String) LightIndiviual0
																.getPropertyValue(isDynamic);
														System.out.println(str333);

														if (str333.equals("yes")) {
															OWLDatatypeProperty inten1 = model
																	.getOWLDatatypeProperty(str + "lightIntensity1");
															intensity_1 = (String) LightIndiviual0
																	.getPropertyValue(inten1);
															OWLDatatypeProperty inten2 = model
																	.getOWLDatatypeProperty(str + "lightIntensity2");
															intensity_2 = (String) LightIndiviual0
																	.getPropertyValue(inten2);
															isDynamic1 = "yes";
														} else {
															OWLDatatypeProperty inten1 = model
																	.getOWLDatatypeProperty(str + "lightIntensity1");
															intensity_1 = (String) LightIndiviual0
																	.getPropertyValue(inten1);
														}
													}

												}

											}
										}
									}
								} else if (hasValues != null) {
									OWLNamedClass strLightClass = model.getOWLNamedClass(hasValues.trim());
									if (strLightClass.equalsStructurally(strMubanClass)) {

										Collection subclassIndiviual1 = null;
										// ��subclass�µ�ʵ��,������topic�µ�ʵ��
										subclassIndiviual1 = subclass.getInstances(true);
										if (subclassIndiviual1.size() != 0) {
											for (Iterator it1 = subclassIndiviual1.iterator(); it1.hasNext();) {
												OWLIndividual LightIndiviual0 = (OWLIndividual) it1.next();
												// System.out.println(
												// "TemplateLightIndiviual:" +
												// LightIndiviual0.getBrowserText());
												LightFromTempNumber++;
												LightTemplateList[LightFromTempNumber] = LightIndiviual0
														.getBrowserText();

												// ��������
												OWLDatatypeProperty isDynamic = model
														.getOWLDatatypeProperty(str + "isDynamic");
												String str333 = (String) LightIndiviual0.getPropertyValue(isDynamic);
												System.out.println(str333);

												if (str333.equals("yes")) {
													OWLDatatypeProperty inten1 = model
															.getOWLDatatypeProperty(str + "lightIntensity1");
													intensity_1 = (String) LightIndiviual0.getPropertyValue(inten1);
													OWLDatatypeProperty inten2 = model
															.getOWLDatatypeProperty(str + "lightIntensity2");
													intensity_2 = (String) LightIndiviual0.getPropertyValue(inten2);
													isDynamic1 = "yes";
												} else {
													OWLDatatypeProperty inten1 = model
															.getOWLDatatypeProperty(str + "lightIntensity1");
													intensity_1 = (String) LightIndiviual0.getPropertyValue(inten1);
												}

											}

										}

									}
								}

							}

						}

					}

				}

				System.out.println("==================individual================");

				// ====================20160314======================
			}

			System.out.println("-------------ģ�崦����ϣ�------------");
		}

		System.out.println(
				"======================       ģ��                   ����         ����             ======================================");

		System.out.println(
				"======================       ʵ�ʵƹ�                   ����         ��ʼ             ======================================");

		if (!intensity_1.equals("")) {
			intensity1 = intensity_1;
		}
		if (!intensity_2.equals("")) {
			intensity2 = intensity_2;
			isDynamic1 = "yes";
		}

		System.out.println("intensity1==" + intensity1 + "intensity2" + intensity2);
		System.out.println(
				"======================       ʵ�ʵƹ�                   ����         ����             ======================================");

		System.out.println(
				"======================       ����                   ɫ��         ��ʼ            ======================================");

		String hueColor_1 = "";
		String hueColor_2 = "";

		/*
		 * ����IE��ȡ������Ϣ
		 */
		System.out.println("---------------���������⴦��---------------");
		System.out.println("     --------���ȴ���IE��ȡ������-------");
		System.out.println("---------------��������---------------\n");
		// ��ȡIE ����
		String ieTopic = doc.getRootElement().element("maName").attributeValue("topic");
		if (ieTopic.contains("Topic")) {
			System.out.println("---------------����IE���⴦��---------------");
			System.out.println("IE��ȡ�������ǣ�" + ieTopic);
			topicList.add(ieTopic);
			OWLNamedClass topic = model.getOWLNamedClass(str1 + ieTopic);
			System.out.println("topic:" + topic.getBrowserText());

			// lightIntensityForTopic��������
			OWLObjectProperty lightIntensityForTopicProperty = model
					.getOWLObjectProperty(str + "lightSuitableForTopic");
			System.out.println("lightIntensityForTopicProperty==" + lightIntensityForTopicProperty.getBrowserText());
			OWLNamedClass NewLightClass = model.getOWLNamedClass(str + "NewLight");
			Collection NewLightSubClass = NewLightClass.getSubclasses(true);
			for (Iterator it = NewLightSubClass.iterator(); it.hasNext();) {
				OWLNamedClass subclass = (OWLNamedClass) it.next();
				if (subclass.getSomeValuesFrom(lightIntensityForTopicProperty) == null) {
					continue;
				}

				// System.out.println("subclass===" +
				// subclass.getBrowserText());
				// System.out.println("subclass===" + subclass);

				// System.out.println(subclass.getSomeValuesFrom(lightIntensityForTopicProperty).getBrowserText());

				String classnameStr = subclass.getSomeValuesFrom(lightIntensityForTopicProperty).getBrowserText();

				if (classnameStr != null) {
					String hasValues = classnameStr;
					// System.out.println("hasValues" + hasValues);
					if (hasValues.contains("or")) {
						String[] hasValuesSpilt = hasValues.split("or");
						if (hasValuesSpilt.length > 0) {
							// �ж��Ƿ��ж�������Ӧ
							for (int j = 0; j < hasValuesSpilt.length; j++) {
								// System.out.println("�ص����ַ���" +
								// hasValuesSpilt[j].toString().trim());
								OWLNamedClass strLightClass = model
										.getOWLNamedClass(hasValuesSpilt[j].toString().trim());
								if (strLightClass == null) {
									continue;
								}
								// System.out.println("strLightClass==" +
								// strLightClass.getBrowserText() + "========="
								// + topic.getBrowserText());
								if (strLightClass.equalsStructurally(topic)) {

									Collection subclassIndiviual1 = null;
									// ��subclass�µ�ʵ��,������topic�µ�ʵ��
									subclassIndiviual1 = subclass.getInstances(true);
									if (subclassIndiviual1.size() != 0) {
										for (Iterator it1 = subclassIndiviual1.iterator(); it1.hasNext();) {
											OWLIndividual LightIndiviual0 = (OWLIndividual) it1.next();
											System.out.println("LightIndiviual:" + LightIndiviual0.getBrowserText());
											// LightTopicList[LightFromTopNumber]
											// =
											// LightIndiviual0.getBrowserText();
											LightTopicList1.add(LightIndiviual0);
											LightFromTopNumber++;
										}

									}

								}
							}

						}
					} else if (hasValues != null) {
						OWLNamedClass strLightClass = model.getOWLNamedClass(hasValues.trim());
						if (strLightClass.equalsStructurally(topic)) {

							Collection subclassIndiviual1 = null;
							// ��subclass�µ�ʵ��,������topic�µ�ʵ��
							subclassIndiviual1 = subclass.getInstances(true);
							if (subclassIndiviual1.size() != 0) {
								for (Iterator it1 = subclassIndiviual1.iterator(); it1.hasNext();) {
									OWLIndividual LightIndiviual0 = (OWLIndividual) it1.next();
									System.out.println("LightIndiviual:" + LightIndiviual0.getBrowserText());
									LightTopicList1.add(LightIndiviual0);
									LightFromTopNumber++;
								}

							}

						}
					}

				}

			}

			if (LightFromTopNumber != 0) {
				for (OWLIndividual owlIn : LightTopicList1) {
					System.out.println("owlIn==" + owlIn.getBrowserText());
				}
				int leng = LightTopicList1.size();
				System.out.println(LightTopicList1.size());

				int len = (int) (Math.random() * leng);
				OWLIndividual owlIn = LightTopicList1.get(len);

				OWLDatatypeProperty isDynamic0 = model.getOWLDatatypeProperty(str + "isDynamic");
				String str33 = (String) owlIn.getPropertyValue(isDynamic0);
				System.out.println("���ѡ��Ľ���ǲ��Ƕ�̬��====" + str33);

				if (str33.equals("yes")) {
					OWLDatatypeProperty inten1 = model.getOWLDatatypeProperty(str + "lightHue_1");
					hueColor_1 = (String) owlIn.getPropertyValue(inten1);
					OWLDatatypeProperty inten2 = model.getOWLDatatypeProperty(str + "lightHue_2");
					hueColor_2 = (String) owlIn.getPropertyValue(inten2);
					System.out.println("hueColor_1 " + hueColor_1 + ",hueColor_2 " + hueColor_2);
					isDynamic1 = "yes";
				} else {
					OWLDatatypeProperty inten1 = model.getOWLDatatypeProperty(str + "lightHue_1");
					hueColor_1 = (String) owlIn.getPropertyValue(inten1);
					System.out.println("hueColor_1 " + hueColor_1);
				}
			}

		} else {
			System.out.println("----------IEδ��ȡ������--------");
		}

		System.out.println(
				"======================       ����                   ɫ��         ����           ======================================");

		System.out.println(
				"======================       ģ��                  ɫ��         ��ʼ           ======================================");

		if (LightFromTopNumber == 0) {

			System.out.println("-------------ģ�崦��---------------");

			// ������ģ�������
			int listSize = 0;

			// listΪ��������ģ��
			if (list != null) {
				listSize = list.size();// ģ�����
			} else
				System.out.println("δ���ݽ����κ�ģ��");

			if (listSize > 0) {

				System.out.println("���ݽ�����ģ�����Ϊ��" + listSize + "�����ֱ��ǣ�" + list);
				String strMuban = null;
				String strMubanIndividual = null;

				// ========����ģ�����===================����������ģ�� ,�ֳ� ģ�塢ʵ��
				int listSizeMuban = list.size();
				System.out.print(listSize + "\n");
				String[] listToStr = new String[listSizeMuban]; // ��arraylistת��Ϊstring[]
				String[] templateList = new String[listSizeMuban]; // ������template��Ϣ
				String[] templateListIndividual = new String[listSizeMuban]; // ������template��individual��Ϣ
				System.out.println("listSizeMuban" + listSizeMuban);
				if (listSizeMuban != 0) {
					listToStr = (String[]) list.toArray(new String[listSizeMuban]);// ������(String[])
					for (int i = 0; i < listToStr.length; i++) {
						// System.out.println("listToStr:" + listToStr);
						String str2 = listToStr[i];
						int pos = str2.indexOf(":");
						templateList[i] = (String) str2.subSequence(0, pos);
						// System.out.println("templateList[" + i + "]=" +
						// templateList[i]);
						// =====================���봦=====ģ��
						strMuban = templateList[i].trim();
						// =====================�����====ʵ��
						templateListIndividual[i] = (String) str2.subSequence(pos + 1, str2.length());
						strMubanIndividual = templateListIndividual[i].trim();
						// System.out.println("strMubanIndividual ===" +
						// strMubanIndividual);
					}
				}

				System.out.println("strMuban===" + strMuban);
				OWLNamedClass strMubanClass = model.getOWLNamedClass(str1 + strMuban);
				System.out.println("strMubanClass===" + strMubanClass.getBrowserText());

				// ========����ģ�����================����������ģ�� ,�ֳ� ģ�塢ʵ��

				// ====================20160314======================
				// ADL�����������Light�е���һ��
				Collection strMubanClassIndiviual = null;
				// ��subclass�µ�ʵ��,������topic�µ�ʵ��
				strMubanClassIndiviual = strMubanClass.getInstances(true);
				System.out.println("==================individual================");
				// System.out.println("strMubanClassIndiviual=" +
				// strMubanClassIndiviual.size());
				for (Iterator it = strMubanClassIndiviual.iterator(); it.hasNext();) {
					OWLIndividual LightIndiviual = (OWLIndividual) it.next();
					// System.out.println("LightIndiviual===" +
					// LightIndiviual.getBrowserText());
					// �ҵ����������ʵ���봫����ʵ��һ��
					if (LightIndiviual.getBrowserText().equals(str1 + strMubanIndividual)) {

						// �ҵ���lightʵ�������������ֵΪ ����ʵ����
						OWLNamedClass NewLightClass = model.getOWLNamedClass(str + "NewLight");
						Collection NewLightSubClass = NewLightClass.getSubclasses(true);
						System.out.println("NewLightSubClass size " + NewLightSubClass.size());

						for (Iterator it3 = NewLightSubClass.iterator(); it3.hasNext();) {

							OWLNamedClass subclass = (OWLNamedClass) it3.next();
							OWLObjectProperty lightIntensityForTopicTemplate = model
									.getOWLObjectProperty(str + "lightSuitableForTemplate");

							if (lightIntensityForTopicTemplate == null) {
								continue;
							}

							if (subclass.getSomeValuesFrom(lightIntensityForTopicTemplate) == null) {
								continue;
							}

							System.out.println("subclass==" + subclass.getBrowserText());
							RDFResource str2 = subclass.getSomeValuesFrom(lightIntensityForTopicTemplate);
							if (str2 != null) {
								String hasValues = str2.getBrowserText();
								// System.out.println("hasValues:" + hasValues);
								if (hasValues.contains("or")) {
									String[] hasValuesSpilt = hasValues.split("or");
									if (hasValuesSpilt.length > 0) {
										// �ж��Ƿ��ж��ģ���Ӧ
										for (int j = 0; j < hasValuesSpilt.length; j++) {

											OWLNamedClass strLightClass = model
													.getOWLNamedClass(hasValuesSpilt[j].toString().trim());
											if (strLightClass == null) {
												continue;
											}

											if (strLightClass.equalsStructurally(strMubanClass)) {

												Collection subclassIndiviual1 = null;
												// ��subclass�µ�ʵ��,������topic�µ�ʵ��
												subclassIndiviual1 = subclass.getInstances(true);
												if (subclassIndiviual1.size() != 0) {
													for (Iterator it1 = subclassIndiviual1.iterator(); it1.hasNext();) {
														OWLIndividual LightIndiviual0 = (OWLIndividual) it1.next();
														// System.out.println(
														// "LightIndiviual:" +
														// LightIndiviual0.getBrowserText());
														LightFromTempNumber++;
														LightTemplateList[LightFromTempNumber] = LightIndiviual0
																.getBrowserText();

														// ��������
														OWLDatatypeProperty isDynamic = model
																.getOWLDatatypeProperty(str + "isDynamic");
														String str333 = (String) LightIndiviual0
																.getPropertyValue(isDynamic);
														System.out.println(str333);

														if (str333.equals("yes")) {
															OWLDatatypeProperty inten1 = model
																	.getOWLDatatypeProperty(str + "lightHue_1");
															hueColor_1 = (String) LightIndiviual0
																	.getPropertyValue(inten1);
															OWLDatatypeProperty inten2 = model
																	.getOWLDatatypeProperty(str + "lightHue_2");
															hueColor_2 = (String) LightIndiviual0
																	.getPropertyValue(inten2);
															System.out.println("hueColor_1 " + hueColor_1
																	+ ",hueColor_2 " + hueColor_2);
															isDynamic1 = "yes";
														} else {
															OWLDatatypeProperty inten1 = model
																	.getOWLDatatypeProperty(str + "lightHue_1");
															hueColor_1 = (String) LightIndiviual0
																	.getPropertyValue(inten1);
															System.out.println("hueColor_1 " + hueColor_1);
														}
													}

												}

											}
										}
									}
								} else if (hasValues != null) {
									OWLNamedClass strLightClass = model.getOWLNamedClass(hasValues.trim());
									if (strLightClass.equalsStructurally(strMubanClass)) {

										Collection subclassIndiviual1 = null;
										// ��subclass�µ�ʵ��,������topic�µ�ʵ��
										subclassIndiviual1 = subclass.getInstances(true);
										if (subclassIndiviual1.size() != 0) {
											for (Iterator it1 = subclassIndiviual1.iterator(); it1.hasNext();) {
												OWLIndividual LightIndiviual0 = (OWLIndividual) it1.next();
												// System.out.println(
												// "TemplateLightIndiviual:" +
												// LightIndiviual0.getBrowserText());
												LightFromTempNumber++;
												LightTemplateList[LightFromTempNumber] = LightIndiviual0
														.getBrowserText();

												// ��������
												OWLDatatypeProperty isDynamic = model
														.getOWLDatatypeProperty(str + "isDynamic");
												String str333 = (String) LightIndiviual0.getPropertyValue(isDynamic);
												System.out.println(str333);

												if (str333.equals("yes")) {
													OWLDatatypeProperty inten1 = model
															.getOWLDatatypeProperty(str + "lightHue_1");
													hueColor_1 = (String) LightIndiviual0.getPropertyValue(inten1);
													OWLDatatypeProperty inten2 = model
															.getOWLDatatypeProperty(str + "lightHue_2");
													hueColor_2 = (String) LightIndiviual0.getPropertyValue(inten2);
													System.out.println(
															"hueColor_1 " + hueColor_1 + ",hueColor_2 " + hueColor_2);
													isDynamic1 = "yes";
												} else {
													// ��Ҫ��
													OWLDatatypeProperty inten1 = model
															.getOWLDatatypeProperty(str + "lightHue_1");
													hueColor_1 = (String) LightIndiviual0.getPropertyValue(inten1);
													System.out.println("hueColor_1 " + hueColor_1);
												}

											}

										}

									}
								}

							}

						}

					}

				}

				System.out.println("==================individual================");

				// ====================20160314======================
			}

			System.out.println("-------------ģ�崦����ϣ�------------");

		}

		System.out.println(
				"======================       ģ��                  ɫ��         ����           ======================================");

		// String haveEffect;

		String changeFrame = "";

		String color1 = "";
		String color2 = "";
		System.out.println("========================================");
		System.out.println(hueColor_1 + "," + hueColor_2);
		System.out.println("========================================");

		// ================================������ɫ��Ϣ
		if (!hueColor_1.equals("") && hueColor_1.equals("warm")) {
			color1 = "yellow";

		} else if (!hueColor_1.equals("") && hueColor_1.equals("cold")) {
			color1 = "cyan";
		} else {
			color1 = "white";
		}
		System.out.println("color1" + color1);
		if (!hueColor_2.equals("") && hueColor_2.equals("warm")) {
			color2 = "yellow";
		} else if (!hueColor_2.equals("") && hueColor_2.equals("cold")) {
			color2 = "cyan";
		} else if (!hueColor_2.equals("")) {
			color2 = "white";
		}

		System.out.println("====================================�������ȡ���ɫ����Ϣ==================");

		System.out.println("====================================�������ȡ���ɫ����Ϣ==================");

		// ���Դ�����ѩ ע�������粻����
		Element root = doc.getRootElement();
		System.out.println("��ǰ�ڵ�����" + root.getName());
		Element node1 = root.element("maName");
		List<Element> nodes = node1.elements("rule");
		for (Element node : nodes) {

			if ("addEffectToMa".equals(node.attributeValue("ruleType"))) {

				if ("Snow".equals(node.attributeValue("type")) || "Rain".equals(node.attributeValue("type"))) {

					System.out.println("============����ѩ=====================");
					// ====== ����ӵ�
					if (isDynamic1.equals("yes")) {

						if ("Heavy".equals(node.attributeValue("Magnitude"))) {
							System.out.println("�´�ѩ��");
							intensity2 = "feeble";

						} else if ("Light".equals(node.attributeValue("Magnitude"))) {
							System.out.println("��Сѩ��");
							intensity2 = "feeble";
						} else {
							System.out.println("����ѩ��");
							intensity2 = "feeble";

						}
					} else {

						if ("Heavy".equals(node.attributeValue("Magnitude"))) {
							System.out.println("�´�ѩ��");
							intensity1 = "feeble";

						} else if ("Light".equals(node.attributeValue("Magnitude"))) {
							System.out.println("��Сѩ��");
							intensity1 = "normal";
						} else {
							System.out.println("����ѩ��");
							intensity1 = "feeble";

						}

					}

				}

			}
			if ("addFogToMa".equals(node.attributeValue("ruleType"))) {
				if (!isDynamic1.equals("")) {
					intensity1 = "feeble";
				} else {
					intensity2 = "feeble";
				}
			}
		}

		// ������ ���������⡢ģ�� ֻҪ��һ���ı�ͻ��� ��̬ ��
		if (isDynamic1.equals("yes")) {
			changeFrame = framenum / 2 + "";
		}

		System.out.println("isDynamic1" + isDynamic1);

		// ================================ 2016.3.29 �����ƶ�λ�� ===================
		String moveDirection = "";
		if (individualOfPlace.getBrowserText().equals(str1 + "outDoorDescription")
				|| individualOfPlace.getBrowserText().equals(str1 + "inAndOutDoorDescription")) {
			if (isDynamic1.equals("yes")) {
				if (intensity1.equals("feeble") && (intensity2.equals("normal") || intensity2.equals("strong"))) {
					moveDirection = "up";
				}
				if (intensity1.equals("normal") && intensity2.equals("strong")) {
					moveDirection = "up";
				}
				if (intensity1.equals("strong") && (intensity2.equals("normal") || intensity2.equals("feeble"))) {
					moveDirection = "down";
				}
				if (intensity1.equals("normal") && intensity2.equals("feeble")) {
					moveDirection = "down";
				}

			}
		}
		
		
		//===========================  2016.5.22  =============================================
		
		String intensity3 = intensity2;
		if (intensity2.equals("feebler")) {
			//�޹�
			intensity3 = "feeble";
		}
		
		
		
		
		//=====================================================================================
		
		

		// ================================ 2016.3.29 �����ƶ�λ�� ===================

		// ���������������� ע�� ��������ɵĳ�����Ҫ���� mû�м� isDynamic2
		// =================================================20160313==================================================
		//=========================  2016.9.25
				String targetMode = "all";
				String targetAmbient = "all";
				int mubanSize = list.size();
				String[] listToStr = new String[mubanSize]; // ��arraylistת��Ϊstring[]
				String[] templateList = new String[mubanSize]; // ������template��Ϣ
				if (mubanSize != 0) {
					listToStr = (String[]) list.toArray(new String[mubanSize]);// ������(String[])
					for (int i = 0; i < listToStr.length; i++) {
						String str2 = listToStr[i];
						int pos = str2.indexOf(":");
						templateList[i] = (String) str2.subSequence(0, pos);
						// =====================���봦=====ģ��
						String strMuban = templateList[i].trim();
						templateVolumeLight.add(strMuban);
					}
				}
				
				if((ieTopic==null || ieTopic.equals(""))&& templateVolumeLight !=null){
					for (int i = 0; i < templateVolumeLight.size(); i++) {
						if(templateVolumeLight.get(i).equals("AlarmTemplate") ||
								templateVolumeLight.get(i).equals("DreadTemplate") || 
								templateVolumeLight.get(i).equals("SadTemplate")){
							if(AmbientLightIn !=null || AmbientLightOut != null){
								for (Element node : nodes) {
									if ("addToMa".equals(node.attributeValue("ruleType")) && "1".equals(node.attributeValue("isTarget")) ) {
										//&& !"M_girl.ma".equals(node.attributeValue("addModel")) && !"M_boy.ma".equals(node.attributeValue("addModel"))
										String targetStr = node.attributeValue("addModel");
										targetMode = targetStr;
										targetAmbient = targetStr;
										isDynamic1 = "no";
										intensity3=moveDirection=changeFrame=color2=intensity2="";
										break;
									}
								}
							}
							
							break;
						}
					}
				}
				//ieTopic.equals("WorryTopic")||ieTopic.equals("WorryTopic")
				if(ieTopic.equals("DreadTopic")){
					for (Element node : nodes) {
						if ("addToMa".equals(node.attributeValue("ruleType")) || "1".equals(node.attributeValue("isTarget"))) {
							//&& !"M_girl.ma".equals(node.attributeValue("addModel")) && !"M_boy.ma".equals(node.attributeValue("addModel"))
							String targetStr = node.attributeValue("addModel");
							targetMode = targetStr;
							targetAmbient = targetStr;
							isDynamic1 = "no";
							intensity3=moveDirection=changeFrame=color2=intensity2="";
							if(PointLightIn==null || PointLightIn ==""){
								PointLightOut = "volumeLight";
							}else{
								PointLightIn = "volumeLight";
							}
							break;
						}
					}
				}
		
		
				// ���ڻ�����
				if (AmbientLightIn != null) {
					doc = printLightRule(doc,autoGeneration, AmbientLightIn, targetAmbient, "no", "roomIn", color1, intensity1, "no", isDynamic1,
							changeFrame, color2, intensity2, "");
				}

				// ���ڵ��Դ
				if (PointLightIn != null) {
					doc = printLightRule(doc,autoGeneration, PointLightIn, targetMode, "yes", "roomIn", color1, intensity1, "no", isDynamic1,
							changeFrame, color2, intensity2, "");
				}
				// ���⻷����
				if (AmbientLightOut != null) {
					doc = printLightRule(doc,autoGeneration, AmbientLightOut, targetAmbient, "no", "roomOut", color1, intensity1, "no", isDynamic1,
							changeFrame, color2, intensity3, moveDirection);
				}
				// ������Դ
				if (PointLightOut != null) {
					doc = printLightRule(doc,autoGeneration, PointLightOut, targetMode, "yes", "roomOut", color1, intensity1, "yes", isDynamic1,
							changeFrame, color2, intensity3, moveDirection);
				}
				
				return doc;

	}

	// ����Ƶ�
	public Document printLightRule(Document doc,String autoGeneration, String type, String targetMode, String haveShade,
			String relativeLocation, String color1, String intensity1, String haveEffect, String isDynamic,
			String changeFrame, String color2, String intensity2, String moveDirection) {
		System.out.println("��ʼ����xml-rule");
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "addLightToMa");
		ruleName.addAttribute("autoGeneration", autoGeneration);
		ruleName.addAttribute("type", type);
		ruleName.addAttribute("haveShade", haveShade);
		ruleName.addAttribute("targetMode", targetMode);
		ruleName.addAttribute("haveEffect", haveEffect);

		ruleName.addAttribute("relativeLocation", relativeLocation);
		ruleName.addAttribute("lightColor", color1);
		ruleName.addAttribute("intensity", intensity1);

		ruleName.addAttribute("isDynamic", isDynamic);
		ruleName.addAttribute("changeFrame", changeFrame);
		ruleName.addAttribute("changeColor", color2);
		ruleName.addAttribute("changeIntensity", intensity2);
		ruleName.addAttribute("moveDirection", moveDirection);
		System.out.println("xml-rule�������");
		return doc;
	}

	public static void main(String[] args)
			throws OntologyLoadException, DocumentException, SWRLRuleEngineException, IOException {

		// String owlpath =
		// "file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";
		// String owlpath = "file:///C:/TestOWL/sumoOWL2/sumo_phone3.owl";
		String owlpath = "file:///C:/TestOWL/HLLight/TheNewLight.owl";

		OWLModel model = ProtegeOWL.createJenaOWLModelFromURI(owlpath);
		ArrayList<String> aList = new ArrayList();
		// aList.add("ComfortTemplate:comfortTemplate");
		aList.add("NightTemplate:nightTemplate");
		aList.add("EncourageTemplate:encourageTemplate");
		aList.add("DreadTemplate:encourageTemplate");

		File file = new File("F:/ʵ����/test1.xml");

		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(file);

		// =========================================================
		LightInsert light = new LightInsert();
		System.out.println("��ʼ��");
		// riverFallingTree.m angry_mm.ma basketball.ma
		Document document1 = light.LightInfer(aList, model, "room1.ma", document);
		XMLWriter writer = new XMLWriter(new FileWriter("F:/ʵ����/test1.xml"));
		writer.write(document1);
		writer.close();
		System.out.println("����!");
		// ====================================================================

	}

}
