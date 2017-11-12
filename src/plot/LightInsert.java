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
	 * 通过ADL规划，ontology中的对象属性，此程序是读取指定类下面的实例 20160310 22:08 不用跑主题规则和模板规则
	 * 20160314 新设计的思路 主题、模板
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

		// 为前缀标志 更新程序需要添加 p7:
		String str = "p7:";
		// 新添加 20160310 更新程序前需要删掉p2:
		String str1 = "";

		Collection mapToWindValues = null;
		// String[] LightTopicList = new String[5];// 用来存储抽取出来的符合主题和模版的灯光
		List<OWLIndividual> LightTopicList1 = new ArrayList<OWLIndividual>();
		String[] LightTemplateList = new String[5];// 用来存储抽取出来的符合主题和模版的灯光
		//
		// ==============此属性可能废除
		String[] LightList = new String[5];
		// int LightFromInfoNumber = 0;// 最终抽取的符合的灯光的数量
		int LightFromTopNumber = 0;// 从主题抽到灯的个数
		int LightFromTempNumber = 0; // 从模版抽到灯的个数

		// topicList存放主题
		ArrayList topicList = new ArrayList();
		
		//用来存储模板   2016.9.28
		List<String> templateVolumeLight =  new ArrayList<String>();

		// 获取maya实例
		// System.out.println("开始获取实例");
		OWLIndividual maIndividual = model.getOWLIndividual(str1 + maName);
		if (null == maIndividual) {
			System.out.println("maya 实例无法获取，可能不存在或丢失！请检查maName是否正确或存在");
		}
		System.out.println("获取实例成功，maya实例：" + maIndividual);

		OWLDatatypeProperty maframenumber = model.getOWLDatatypeProperty(str1 + "maFrameNumber");
		int framenum;
		framenum = (Integer) maIndividual.getPropertyValue(maframenumber);// 300

		System.out.println("==================场景帧数=========================" + framenum);

		// ========================================20160313==========================================================
		// 20160313新实验
		System.out.println("======================================================================20160313");

		// 室内灯光 lightName1 AmbientLightIn lightName2 PointLightIn
		String AmbientLightIn = null;
		String PointLightIn = null;
		// 室外灯光 lightName3 AmbientLightOut lightName4 PointLightOut
		String AmbientLightOut = null;
		String PointLightOut = null;
		// 室内外
		// int lightNumber = 0;
		String autoGeneration = "yes";
		
		OWLObjectProperty hasValueOfPlace = model.getOWLObjectProperty(str1 + "hasValueOfPlace");
		OWLIndividual individualOfPlace = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlace);

		
		//===============  2016.5.22  判断是否是室内场景
		Boolean roomIn = false;
		
		// ====================== 2016.4.25 排除既是室内又是室外的场景

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
			
			//正常流程    2016.4.25
			
			// 如果场景的实例没有==hasValueOfPlace 属性
			if (individualOfPlace != null) {
				System.out.println(individualOfPlace.getBrowserText());

				OWLObjectProperty lightLayoutContainLight = model.getOWLObjectProperty(str + "lightLayoutContainLight");
				// OWLIndividual individualOfLayout = (OWLIndividual)
				// individualOfPlace.getPropertyValue(hasValueOfPlace);
				System.out.println("lightLayoutContainLight=" + lightLayoutContainLight);

				OWLObjectProperty lightLayoutSuitableMaPlace = model
						.getOWLObjectProperty(str + "lightLayoutSuitableMaPlace");

				// 遍历LightLayout,然后找到值对应的即可
				OWLNamedClass LightLayoutClass = model.getOWLNamedClass(str + "LightLayout");
				Collection LightLayoutSubClass = LightLayoutClass.getSubclasses(true);
				for (Iterator it = LightLayoutSubClass.iterator(); it.hasNext();) {
					OWLNamedClass subLayoutclass = (OWLNamedClass) it.next();
					OWLNamedClass classname = (OWLNamedClass) LightLayoutClass
							.getSomeValuesFrom(lightLayoutSuitableMaPlace);
					Collection subLayoutclassIndiviual = null;
					// 找subclass下的实例,而不是topic下的实例
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
				"======================       场景                   亮度         开始              ======================================");

		// 判断时间
		String isDynamic1 = "";
		String intensity1 = "";
		String intensity2 = "";
		OWLObjectProperty hasValueOfTime = model.getOWLObjectProperty(str1 + "hasValueOfTime");
		OWLIndividual individualOfTime = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfTime);
		// System.out.println("individualOfTime==" +
		// individualOfTime.getBrowserText());
		// 判断 场景是否有 hasValueOfTime 这个属性
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
				"======================       场景                   亮度         结束              ======================================");

		System.out.println(
				"======================       模板                   亮度         开始              ======================================");

		String intensity_1 = "";
		String intensity_2 = "";
		if (true) {
			System.out.println("-------------模板处理---------------");

			// 传进来模板的数量
			int listSize = 0;

			// list为传进来的模板
			if (list != null) {
				listSize = list.size();// 模板个数
			} else
				System.out.println("未传递进来任何模板");

			if (listSize > 0) {

				System.out.println("传递进来的模板个数为：" + listSize + "个，分别是：" + list);
				String strMuban = null;
				String strMubanIndividual = null;

				// ========处理模板完毕===================处理传进来的模板 ,分出 模板、实例
				int listSizeMuban = list.size();
				System.out.print(listSize + "\n");
				String[] listToStr = new String[listSizeMuban]; // 将arraylist转化为string[]
				String[] templateList = new String[listSizeMuban]; // 处理后的template信息
				String[] templateListIndividual = new String[listSizeMuban]; // 处理后的template的individual信息
				System.out.println("listSizeMuban" + listSizeMuban);
				if (listSizeMuban != 0) {
					listToStr = (String[]) list.toArray(new String[listSizeMuban]);// 增加了(String[])
					for (int i = 0; i < listToStr.length; i++) {
						System.out.println("listToStr:" + listToStr);
						String str2 = listToStr[i];
						int pos = str2.indexOf(":");
						templateList[i] = (String) str2.subSequence(0, pos);
						System.out.println("templateList[" + i + "]=" + templateList[i]);
						// =====================分离处=====模板
						strMuban = templateList[i].trim();
						// =====================分离出====实例
						templateListIndividual[i] = (String) str2.subSequence(pos + 1, str2.length());
						strMubanIndividual = templateListIndividual[i].trim();
						System.out.println("strMubanIndividual ===" + strMubanIndividual);
					}
				}

				System.out.println("strMuban===" + strMuban);
				OWLNamedClass strMubanClass = model.getOWLNamedClass(str1 + strMuban);
				System.out.println("strMubanClass===" + strMubanClass.getBrowserText());

				// ========处理模板完毕================处理传进来的模板 ,分出 模板、实例

				// ====================20160314======================
				// ADL抽出的类与与Light中的类一致
				Collection strMubanClassIndiviual = null;
				// 找subclass下的实例,而不是topic下的实例
				strMubanClassIndiviual = strMubanClass.getInstances(true);
				System.out.println("==================individual================");
				System.out.println("strMubanClassIndiviual=" + strMubanClassIndiviual.size());
				for (Iterator it = strMubanClassIndiviual.iterator(); it.hasNext();) {
					OWLIndividual LightIndiviual = (OWLIndividual) it.next();
					// System.out.println("LightIndiviual===" +
					// LightIndiviual.getBrowserText());
					// 找到库类下面的实例与传来的实例一致
					if (LightIndiviual.getBrowserText().equals(str1 + strMubanIndividual)) {

						// 找到与light实例中与对象属性值为 上面实例的
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
										// 判断是否有多个模板对应
										for (int j = 0; j < hasValuesSpilt.length; j++) {
											// System.out.println("hasValuesSpilt.length:"
											// + hasValuesSpilt.length);
											// System.out.println(hasValuesSpilt[j].toString());
											// System.out.println("hasValuesSpilt[j].toString().trim()"
											// +hasValuesSpilt[j].toString().trim());
											// System.out.println("截到的字符串" +
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
												// 找subclass下的实例,而不是topic下的实例
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

														// 数据属性
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
										// 找subclass下的实例,而不是topic下的实例
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

												// 数据属性
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

			System.out.println("-------------模板处理完毕！------------");
		}

		System.out.println(
				"======================       模板                   亮度         结束             ======================================");

		System.out.println(
				"======================       实际灯光                   亮度         开始             ======================================");

		if (!intensity_1.equals("")) {
			intensity1 = intensity_1;
		}
		if (!intensity_2.equals("")) {
			intensity2 = intensity_2;
			isDynamic1 = "yes";
		}

		System.out.println("intensity1==" + intensity1 + "intensity2" + intensity2);
		System.out.println(
				"======================       实际灯光                   亮度         结束             ======================================");

		System.out.println(
				"======================       主题                   色调         开始            ======================================");

		String hueColor_1 = "";
		String hueColor_2 = "";

		/*
		 * 处理IE抽取主题信息
		 */
		System.out.println("---------------场景的主题处理---------------");
		System.out.println("     --------优先处理IE抽取的主题-------");
		System.out.println("---------------处理主题---------------\n");
		// 获取IE 主题
		String ieTopic = doc.getRootElement().element("maName").attributeValue("topic");
		if (ieTopic.contains("Topic")) {
			System.out.println("---------------处理IE主题处理---------------");
			System.out.println("IE抽取的主题是：" + ieTopic);
			topicList.add(ieTopic);
			OWLNamedClass topic = model.getOWLNamedClass(str1 + ieTopic);
			System.out.println("topic:" + topic.getBrowserText());

			// lightIntensityForTopic对象属性
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
							// 判断是否有多个主题对应
							for (int j = 0; j < hasValuesSpilt.length; j++) {
								// System.out.println("截到的字符串" +
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
									// 找subclass下的实例,而不是topic下的实例
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
							// 找subclass下的实例,而不是topic下的实例
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
				System.out.println("随机选择的结果是不是动态的====" + str33);

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
			System.out.println("----------IE未抽取出主题--------");
		}

		System.out.println(
				"======================       主题                   色调         结束           ======================================");

		System.out.println(
				"======================       模板                  色调         开始           ======================================");

		if (LightFromTopNumber == 0) {

			System.out.println("-------------模板处理---------------");

			// 传进来模板的数量
			int listSize = 0;

			// list为传进来的模板
			if (list != null) {
				listSize = list.size();// 模板个数
			} else
				System.out.println("未传递进来任何模板");

			if (listSize > 0) {

				System.out.println("传递进来的模板个数为：" + listSize + "个，分别是：" + list);
				String strMuban = null;
				String strMubanIndividual = null;

				// ========处理模板完毕===================处理传进来的模板 ,分出 模板、实例
				int listSizeMuban = list.size();
				System.out.print(listSize + "\n");
				String[] listToStr = new String[listSizeMuban]; // 将arraylist转化为string[]
				String[] templateList = new String[listSizeMuban]; // 处理后的template信息
				String[] templateListIndividual = new String[listSizeMuban]; // 处理后的template的individual信息
				System.out.println("listSizeMuban" + listSizeMuban);
				if (listSizeMuban != 0) {
					listToStr = (String[]) list.toArray(new String[listSizeMuban]);// 增加了(String[])
					for (int i = 0; i < listToStr.length; i++) {
						// System.out.println("listToStr:" + listToStr);
						String str2 = listToStr[i];
						int pos = str2.indexOf(":");
						templateList[i] = (String) str2.subSequence(0, pos);
						// System.out.println("templateList[" + i + "]=" +
						// templateList[i]);
						// =====================分离处=====模板
						strMuban = templateList[i].trim();
						// =====================分离出====实例
						templateListIndividual[i] = (String) str2.subSequence(pos + 1, str2.length());
						strMubanIndividual = templateListIndividual[i].trim();
						// System.out.println("strMubanIndividual ===" +
						// strMubanIndividual);
					}
				}

				System.out.println("strMuban===" + strMuban);
				OWLNamedClass strMubanClass = model.getOWLNamedClass(str1 + strMuban);
				System.out.println("strMubanClass===" + strMubanClass.getBrowserText());

				// ========处理模板完毕================处理传进来的模板 ,分出 模板、实例

				// ====================20160314======================
				// ADL抽出的类与与Light中的类一致
				Collection strMubanClassIndiviual = null;
				// 找subclass下的实例,而不是topic下的实例
				strMubanClassIndiviual = strMubanClass.getInstances(true);
				System.out.println("==================individual================");
				// System.out.println("strMubanClassIndiviual=" +
				// strMubanClassIndiviual.size());
				for (Iterator it = strMubanClassIndiviual.iterator(); it.hasNext();) {
					OWLIndividual LightIndiviual = (OWLIndividual) it.next();
					// System.out.println("LightIndiviual===" +
					// LightIndiviual.getBrowserText());
					// 找到库类下面的实例与传来的实例一致
					if (LightIndiviual.getBrowserText().equals(str1 + strMubanIndividual)) {

						// 找到与light实例中与对象属性值为 上面实例的
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
										// 判断是否有多个模板对应
										for (int j = 0; j < hasValuesSpilt.length; j++) {

											OWLNamedClass strLightClass = model
													.getOWLNamedClass(hasValuesSpilt[j].toString().trim());
											if (strLightClass == null) {
												continue;
											}

											if (strLightClass.equalsStructurally(strMubanClass)) {

												Collection subclassIndiviual1 = null;
												// 找subclass下的实例,而不是topic下的实例
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

														// 数据属性
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
										// 找subclass下的实例,而不是topic下的实例
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

												// 数据属性
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
													// 需要改
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

			System.out.println("-------------模板处理完毕！------------");

		}

		System.out.println(
				"======================       模板                  色调         结束           ======================================");

		// String haveEffect;

		String changeFrame = "";

		String color1 = "";
		String color2 = "";
		System.out.println("========================================");
		System.out.println(hueColor_1 + "," + hueColor_2);
		System.out.println("========================================");

		// ================================处理颜色信息
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

		System.out.println("====================================处理亮度、颜色等信息==================");

		System.out.println("====================================处理亮度、颜色等信息==================");

		// 可以处理雾、雪 注意遇见风不处理
		Element root = doc.getRootElement();
		System.out.println("当前节点名称" + root.getName());
		Element node1 = root.element("maName");
		List<Element> nodes = node1.elements("rule");
		for (Element node : nodes) {

			if ("addEffectToMa".equals(node.attributeValue("ruleType"))) {

				if ("Snow".equals(node.attributeValue("type")) || "Rain".equals(node.attributeValue("type"))) {

					System.out.println("============风雨雪=====================");
					// ====== 新添加的
					if (isDynamic1.equals("yes")) {

						if ("Heavy".equals(node.attributeValue("Magnitude"))) {
							System.out.println("下大雪啊");
							intensity2 = "feeble";

						} else if ("Light".equals(node.attributeValue("Magnitude"))) {
							System.out.println("下小雪啊");
							intensity2 = "feeble";
						} else {
							System.out.println("下中雪啊");
							intensity2 = "feeble";

						}
					} else {

						if ("Heavy".equals(node.attributeValue("Magnitude"))) {
							System.out.println("下大雪啊");
							intensity1 = "feeble";

						} else if ("Light".equals(node.attributeValue("Magnitude"))) {
							System.out.println("下小雪啊");
							intensity1 = "normal";
						} else {
							System.out.println("下中雪啊");
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

		// 不论是 场景、主题、模板 只要有一个改变就会是 动态 的
		if (isDynamic1.equals("yes")) {
			changeFrame = framenum / 2 + "";
		}

		System.out.println("isDynamic1" + isDynamic1);

		// ================================ 2016.3.29 增加移动位置 ===================
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
			//无关
			intensity3 = "feeble";
		}
		
		
		
		
		//=====================================================================================
		
		

		// ================================ 2016.3.29 增加移动位置 ===================

		// ！！！！！！！！ 注意 如果是生成的场景需要处理 m没有加 isDynamic2
		// =================================================20160313==================================================
		//=========================  2016.9.25
				String targetMode = "all";
				String targetAmbient = "all";
				int mubanSize = list.size();
				String[] listToStr = new String[mubanSize]; // 将arraylist转化为string[]
				String[] templateList = new String[mubanSize]; // 处理后的template信息
				if (mubanSize != 0) {
					listToStr = (String[]) list.toArray(new String[mubanSize]);// 增加了(String[])
					for (int i = 0; i < listToStr.length; i++) {
						String str2 = listToStr[i];
						int pos = str2.indexOf(":");
						templateList[i] = (String) str2.subSequence(0, pos);
						// =====================分离处=====模板
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
		
		
				// 室内环境光
				if (AmbientLightIn != null) {
					doc = printLightRule(doc,autoGeneration, AmbientLightIn, targetAmbient, "no", "roomIn", color1, intensity1, "no", isDynamic1,
							changeFrame, color2, intensity2, "");
				}

				// 室内点光源
				if (PointLightIn != null) {
					doc = printLightRule(doc,autoGeneration, PointLightIn, targetMode, "yes", "roomIn", color1, intensity1, "no", isDynamic1,
							changeFrame, color2, intensity2, "");
				}
				// 室外环境光
				if (AmbientLightOut != null) {
					doc = printLightRule(doc,autoGeneration, AmbientLightOut, targetAmbient, "no", "roomOut", color1, intensity1, "no", isDynamic1,
							changeFrame, color2, intensity3, moveDirection);
				}
				// 室外点光源
				if (PointLightOut != null) {
					doc = printLightRule(doc,autoGeneration, PointLightOut, targetMode, "yes", "roomOut", color1, intensity1, "yes", isDynamic1,
							changeFrame, color2, intensity3, moveDirection);
				}
				
				return doc;

	}

	// 新设计的
	public Document printLightRule(Document doc,String autoGeneration, String type, String targetMode, String haveShade,
			String relativeLocation, String color1, String intensity1, String haveEffect, String isDynamic,
			String changeFrame, String color2, String intensity2, String moveDirection) {
		System.out.println("开始生成xml-rule");
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
		System.out.println("xml-rule生成完毕");
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

		File file = new File("F:/实验室/test1.xml");

		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(file);

		// =========================================================
		LightInsert light = new LightInsert();
		System.out.println("开始！");
		// riverFallingTree.m angry_mm.ma basketball.ma
		Document document1 = light.LightInfer(aList, model, "room1.ma", document);
		XMLWriter writer = new XMLWriter(new FileWriter("F:/实验室/test1.xml"));
		writer.write(document1);
		writer.close();
		System.out.println("结束!");
		// ====================================================================

	}

}
