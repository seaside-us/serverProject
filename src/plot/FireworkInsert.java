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
	private boolean isfwTypesetted = false;// 用于标记是否设置好烟花大小类型
	private boolean isfwColorsetted=false;//用于标记是否设置好烟花颜色类型

	/*
	 * public boolean isFireworkFlag() { return fireworkFlag; }
	 * 
	 * public void setFireworkFlag(boolean fireworkFlag) { this.fireworkFlag =
	 * fireworkFlag; }
	 */

	public Document fireworkInfer(ArrayList<String> List, OWLModel model, String maName, Document doc)
			throws OntologyLoadException, SWRLRuleEngineException {

	
		 //系统环境下
		 String str = "p9:";
		 // newFirework前缀 
		 String str1 = "";
		 
		 
		// 本机环境下
		/*	String str = "";// newFirework前缀
		String str1 = "p2:";// 模板，主题前缀
*/
		String ieTopic;// ie主题
		String fwType = "";// 定义烟花类别（大，中，小）
		String fwColor = "";// 定义烟花颜色（red,blue,green,yellow,pink,default）
		// Boolean isfwTypeSeted = false;// 标记属性（fwType）是否已经设置好
		int fwIndividualsSuitableForTopicCounts = 0;// 符合主题的烟花实例（即知识库中可能多种类型的烟花实例对应同一个主题）
		// topicList存放主题类
		ArrayList<String> topicList = new ArrayList<String>();
		// 用于存储符合IE主题的烟花类实例
		List<OWLIndividual> fwIndividualForTopicList = new ArrayList<OWLIndividual>();
		/*
		 * 获取doc的根节点
		 */
		Element rootName = doc.getRootElement();
		Element name = rootName.element("maName");
		String adlTopic = name.attributeValue("topic");
		System.out.println("adlTopic=" + adlTopic);// 输出主题（此主题是类层面上的，不是具体的实例）
		/*
		 * 获取ma实例
		 */
		OWLIndividual maIndividual = model.getOWLIndividual(str1 + maName);
		if (maIndividual == null) {
			System.out.println("maya 实例无法获取，可能不存在或丢失！请检查maName是否正确或存在");
			return doc;
		} else
			System.out.println("获取实例成功，maya实例：" + maIndividual);
		// 用到的各种属性
		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
		OWLObjectProperty hasValueOfPlaceFlag = model.getOWLObjectProperty(str1 + "hasValueOfPlace");

		/*
		 * 用于判断获取的ma实例是室内场景还是室外场景
		 */

		OWLIndividual individualOfPlaceFlag = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlaceFlag);
		if (individualOfPlaceFlag == null) {
			System.out.println("无法获取实例" + maIndividual.getBrowserText()
					+ "的hasValueOfPlace属性(此属性用来判断场景属于室内还是室外)，so 拒绝加雨！请检查知识库中该属性是否存在或正确？");
			return doc;
		} else {
			// 若是室內場景，則拒絕添加烟花
			if (individualOfPlaceFlag.getBrowserText().equals("inDoorDescription")
					|| individualOfPlaceFlag.getBrowserText().equals("InWaterDescription")) {
				System.out.println("此场景为室内或水下，因此不适合添加烟花！");
				return doc;
			}
		}
		/**
		 * 以下代码即是处理：场景为室外的情况
		 */

		// 处理主题信息
		System.out.println("================场景的主题处理================");
		System.out.println("================优先处理IE主题================");
		ieTopic = doc.getRootElement().element("maName").attributeValue("topic");// 此ieTopic可能为空字符串
		/**
		 * 若adl文档中抽出了主题
		 */
		if (ieTopic.contains("Topic")) {
			System.out.println("IE Topic = " + ieTopic);
			topicList.add(ieTopic);// 添加到主题类列表中
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
			 System.out.println("fwType类：" + fwTypeClass);
			 System.out.println("fwColor" + fwColorClass);
			Collection fwTypeSubClass = fwTypeClass.getSubclasses(true);// 获取Type_firework的子类
			Collection fwColorSubClass = fwColorClass.getSubclasses(true);// 获取Color_firework的子类
			
			
			 System.out.println("fwType类总共有子类：" + fwTypeSubClass.size());
			 System.out.println("fwColor类总共有子类：" + fwColorSubClass.size());
			 
			/**
			 * 遍历Type_firework类的子类===========================开始========================
			 * ======== ===============
			 */
			for (Iterator it = fwTypeSubClass.iterator(); it.hasNext();) {
				OWLNamedClass xxsubclass = (OWLNamedClass) it.next();// 为fwType类的某个子类
				// 若xxsubclass(如：miniType,moderateType,splendidType类)没有fireworkSuitableForFirework属性，则查找下一个子类
				if (xxsubclass.getSomeValuesFrom(fireworkSuitableForTopicProperty) == null)
					continue;
				// 获取fwType某一子类（如miniType）getSomeValuesFrom的值
				String result_getSomeValuesFrom = xxsubclass.getSomeValuesFrom(fireworkSuitableForTopicProperty)
						.getBrowserText();
				System.out.println("result_getSomeValuesFrom=" + result_getSomeValuesFrom);
				if (result_getSomeValuesFrom != null) {
					String hasValues = result_getSomeValuesFrom;
					// ==========开始========getSomeValuesFrom的值有多个的话=========================
					if (hasValues.contains("or")) {
						String[] hasValuesSpilt = hasValues.split("or");
						System.out.println("hasValuesSpilt.length=" + hasValuesSpilt.length);
						// System.out.println("hasValuesSpilt[0]="+hasValuesSpilt[0]);
						// 遍历所有得到的getSomeValuesFrom的结果，判断是否跟主题类相等，如相等则求出关联该ie主题的烟花实例的firesize数据属性
						for (int j = 0; j < hasValuesSpilt.length; j++) {
							System.out.println("hasValuesSpilt[" + j + "]=" + hasValuesSpilt[j].toString().trim());
							OWLNamedClass xxtopicClass = model.getOWLNamedClass(hasValuesSpilt[j].toString().trim());

							// 输出该烟花类关联的主题类名
							if (xxtopicClass == null) {// 若该主题类不存在
								continue;
							}
							// =================开始======================若该主题等于IE抽取的主题
							if (xxtopicClass.equalsStructurally(topic)) {
								System.out.println("该" + xxsubclass.getBrowserText() + "类与ie主题关联");
								// 获取该类（烟花类）下的所有实例
								Collection subclassIndividuals = xxsubclass.getInstances(true);
								if (subclassIndividuals.size() != 0) {
									for (Iterator it1 = subclassIndividuals.iterator(); it1.hasNext();) {
										OWLIndividual xxType_1 = (OWLIndividual) it1.next();

										System.out.println("该" + xxsubclass.getBrowserText() + "类的实例为："
												+ xxType_1.getBrowserText());
										OWLDatatypeProperty firesizeProperty = model
												.getOWLDatatypeProperty(str + "fireworksize");
										String fwtype = xxType_1.getPropertyValue(firesizeProperty).toString();
										fwType = fwtype;// 获得烟花类型属性值
										System.out.println("fwType=" + fwType);
										/*
										 * doc = printFireworkRule(doc, fwType);
										 * return doc;
										 */
									}
								}
								isfwTypesetted = true;// 烟花大小类型已经设置好
								break;
							}
							// =================结束======================若该主题等于IE抽取的主题
						}

					} // ==========结束========getSomeValuesFrom的值有多个的话=========================
					else if (hasValues != null) {// 若不包含or（即只有一个值）
						OWLNamedClass xxtopicClass = model.getOWLNamedClass(hasValues.trim());
						if (xxtopicClass.equalsStructurally(topic)) {
							System.out.println("该" + xxsubclass.getBrowserText() + "类与ie主题关联");
							// 获取该类（烟花类）下的所有实例
							Collection subclassIndividuals = xxsubclass.getInstances(true);
							if (subclassIndividuals.size() != 0) {
								for (Iterator it1 = subclassIndividuals.iterator(); it1.hasNext();) {
									OWLIndividual xxType_1 = (OWLIndividual) it1.next();

									System.out.println(
											"该" + xxsubclass.getBrowserText() + "类的实例为：" + xxType_1.getBrowserText());
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
							isfwTypesetted = true;// 烟花大小类型已经设置好
						}
					}
				}
				if (isfwTypesetted)
					break;// 如果烟花大小类型设置好的话就不在遍历fwType类的其他子类
			}
			/**
			 * 遍历Type_firework类的子类===========================结束========================
			 * ======== ===============
			 */

			/**
			 * 遍历Color_firework类的子类===========================开始=======================
			 * ========= ===============
			 */
			for (Iterator it = fwColorSubClass.iterator(); it.hasNext();) {
				OWLNamedClass xxsubclass = (OWLNamedClass) it.next();// 为fwColor类的某个子类
				// 若xxsubclass(如：red,blue,green,yellow,pink,default类)没有fireworkColorSuitableForTopic属性，则查找下一个子类
				if (xxsubclass.getSomeValuesFrom(fireworkColorSuitableForTopicProperty) == null)
					continue;
				// 获取fwColor某一子类（如red类）getSomeValuesFrom的值
				String result_getSomeValuesFrom = xxsubclass.getSomeValuesFrom(fireworkColorSuitableForTopicProperty)
						.getBrowserText();
				// System.out.println("result_getSomeValuesFrom=" +
				// result_getSomeValuesFrom);
				if (result_getSomeValuesFrom != null) {
					String hasValues = result_getSomeValuesFrom;
					// ==========开始========getSomeValuesFrom的值有多个的话=========================
					if (hasValues.contains("or")) {
						String[] hasValuesSpilt = hasValues.split("or");
						System.out.println("hasValuesSpilt.length=" + hasValuesSpilt.length);
						// 遍历所有得到的getSomeValuesFrom的结果，判断是否跟主题类相等，如相等则求出关联该ie主题的烟花颜色实例的fireworkColor数据属性
						for (int j = 0; j < hasValuesSpilt.length; j++) {
							System.out.println("hasValuesSpilt[" + j + "]=" + hasValuesSpilt[j].toString().trim());
							OWLNamedClass xxtopicClass = model.getOWLNamedClass(hasValuesSpilt[j].toString().trim());

							// 输出该烟花颜色类关联的主题类名
							if (xxtopicClass == null) {// 若该主题类不存在
								continue;
							}
							// =================开始======================若该主题等于IE抽取的主题
							if (xxtopicClass.equalsStructurally(topic)) {
								System.out.println("该" + xxsubclass.getBrowserText() + "类与ie主题关联");
								// 获取该类（如red类）下的所有实例
								Collection subclassIndividuals = xxsubclass.getInstances(true);
								if (subclassIndividuals.size() != 0) {
									for (Iterator it1 = subclassIndividuals.iterator(); it1.hasNext();) {
										OWLIndividual xxColor = (OWLIndividual) it1.next();

										System.out.println("该" + xxsubclass.getBrowserText() + "类的实例为："
												+ xxColor.getBrowserText());
										OWLDatatypeProperty fireworkcolorProperty = model
												.getOWLDatatypeProperty(str + "fireworkcolor");
										String fwcolor = xxColor.getPropertyValue(fireworkcolorProperty).toString();
										fwColor = fwcolor;
										System.out.println("fwColor=" + fwColor);// 获得烟花颜色属性值

									}
								}
								isfwColorsetted=true;
								break;//break the former for_loop
								
							}
							// =================结束======================若该主题等于IE抽取的主题
						}

					} // ==========结束========getSomeValuesFrom的值有多个的话=========================
					else if (hasValues != null) {// 若不包含or（即只有一个值）
						OWLNamedClass xxtopicClass = model.getOWLNamedClass(hasValues.trim());
						if (xxtopicClass.equalsStructurally(topic)) {
							System.out.println("该" + xxsubclass.getBrowserText() + "类与ie主题关联");
							// 获取该类（如red类）下的所有实例
							Collection subclassIndividuals = xxsubclass.getInstances(true);
							if (subclassIndividuals.size() != 0) {
								for (Iterator it1 = subclassIndividuals.iterator(); it1.hasNext();) {
									OWLIndividual xxColor = (OWLIndividual) it1.next();

									System.out.println(
											"该" + xxsubclass.getBrowserText() + "类的实例为：" + xxColor.getBrowserText());
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
					break;// 如果烟花颜色类型设置好的话就不在遍历fwColor类的其他子类)
			}
			/**
			 * 只有fwType赋值成功，才会调用doc = printFireworkRule(doc, fwType, fwColor)方法添加烟花规则
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
			 * 遍历fwColor类的子类===========================结束=======================
			 * ========= ===============
			 */
		} else
			System.out.println("=================主题为空================");

		// ==============================以下模板处理=======================================================");

		System.out.println("=======仅从主题考虑不放烟花，现考虑模板是否适合放烟花===============    ");
		// 传进来模板的数量
		if (List == null)
			System.out.println("=============模板为空==================");
		else {
			String strMuban = null;
			String strMubanIndividual = null;

			int templateCount = List.size();// 统计传进来的模板数
			// System.out.print(listSize + "\n");
			String[] listToStr = new String[templateCount]; // 将arraylist转化为string[]
			String[] templateList = new String[templateCount]; // 存储模板类
			String[] templateIndividualList = new String[templateCount]; // 存储模板类实例
			// System.out.println("templateCount" + templateCount);
			/**
			 * 将传进来的模板List分离成模板类列表和模板实例列表
			 */
			listToStr = (String[]) List.toArray(new String[templateCount]);// 增加了(String[])
			for (int i = 0; i < listToStr.length; i++) {
				// System.out.println("listToStr:" + listToStr);
				String str2 = listToStr[i];
				int pos = str2.indexOf(":");
				// =====================分离处=====模板
				templateList[i] = (String) str2.subSequence(0, pos);
				// =====================分离出====实例
				templateIndividualList[i] = (String) str2.subSequence(pos + 1, str2.length());
			}
			// 遍历模板数组,对其中每一个元素都要到fwType类中去匹配
			for (int j = 0; j < templateCount; j++) {
				OWLNamedClass xxTemplateListClass = model.getOWLNamedClass(str1 + templateList[j].toString());// 传进来的模板类
				OWLNamedClass fwTypeClass = model.getOWLNamedClass(str + "Type_firework");
				
				Collection fwTypeSubClass = fwTypeClass.getSubclasses(true);// 获取Type_firework的子类
				
				OWLObjectProperty fireworkSuitableForTemplateProperty = model
						.getOWLObjectProperty(str + "fireworkSuitableForTemplate");
				/**
				 * 遍历fwType类的子类===========================开始====================
				 * ======== ==== ===============
				 */
				for (Iterator it = fwTypeSubClass.iterator(); it.hasNext();) {
					OWLNamedClass xxsubclass = (OWLNamedClass) it.next();// 为fwType类的某个子类
					System.out.println(xxsubclass.getBrowserText().trim());
					// 若xxsubclass(如：miniType,moderateType,splendidType类)没有fireworkSuitableForFirework属性，则查找下一个子类
					if (xxsubclass.getSomeValuesFrom(fireworkSuitableForTemplateProperty) == null)
						continue;
					// 获取fwType某一子类（如miniType）getSomeValuesFrom的值
					String result_getSomeValuesFrom = xxsubclass.getSomeValuesFrom(fireworkSuitableForTemplateProperty)
							.getBrowserText();
					// System.out.println("result_getSomeValuesFrom=" +
					// result_getSomeValuesFrom);
					if (result_getSomeValuesFrom != null) {
						String hasValues = result_getSomeValuesFrom;
						// ==========开始========getSomeValuesFrom的值有多个的话=========================
						if (hasValues.contains("or")) {
							String[] hasValuesSpilt = hasValues.split("or");
							System.out.println("hasValuesSpilt.lenght="+hasValuesSpilt.length);
							// 遍历所有得到的getSomeValuesFrom的结果，判断是否跟模板类相等，如相等则求出关联该ie主题的烟花实例的firesize数据属性
							for (int t = 0; t < hasValuesSpilt.length; t++) {
								System.out.println("hasValuesSpilt[" + t + "]=" + hasValuesSpilt[t].toString().trim());
								OWLNamedClass xxtemplateClass = model
										.getOWLNamedClass(hasValuesSpilt[t].toString().trim());
								// System.out.println(xxtopicClass.toString());//
								// 输出该烟花类关联的主题类名
								if (xxtemplateClass == null) {// 若该模板类不存在
									continue;
								}
								// =================开始======================若传进来的模板等于烟花某一实例关联的模板
								if (xxtemplateClass.equalsStructurally(xxTemplateListClass)) {

									// 获取该类（烟花类）下的所有实例
									Collection subclassIndividuals = xxsubclass.getInstances(true);
									if (subclassIndividuals.size() != 0) {
										for (Iterator it1 = subclassIndividuals.iterator(); it1.hasNext();) {
											OWLIndividual xxType_1 = (OWLIndividual) it1.next();

											System.out.println("该" + xxTemplateListClass.getBrowserText() + "模板匹配烟花大小类"
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
								} // =================结束======================若该主题等于IE抽取的主题

							}

						} // ==========结束========getSomeValuesFrom的值有多个的话=========================
						else if (hasValues != null) {// 若不包含or（即只有一个值）
							OWLNamedClass xxtopicClass = model.getOWLNamedClass(hasValues.trim());
							if (xxtopicClass.equalsStructurally(xxTemplateListClass)) {
								// 获取该类（烟花类）下的所有实例
								Collection subclassIndividuals = xxsubclass.getInstances(true);
								if (subclassIndividuals.size() != 0) {
									for (Iterator it1 = subclassIndividuals.iterator(); it1.hasNext();) {
										OWLIndividual xxType_1 = (OWLIndividual) it1.next();

										System.out.println("该" + xxTemplateListClass.getBrowserText() + "模板匹配烟花大小类"
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
						break;//不在遍历模板类的其他元素
				}
				/**
				 * 遍历fwType类的子类===========================结束====================
				 * ======== ==== ===============
				 */	
				if(isfwTypesetted)
					break;//不在遍历模板类的其他元素
			}

			// 同理，遍历模板数组,对其中每一个元素都要到fwColor类中去匹配
			for (int j = 0; j < templateCount; j++) {
				OWLNamedClass xxTemplateListClass = model.getOWLNamedClass(str1 + templateList[j].toString());// 传进来的模板类
				OWLNamedClass fwColorClass = model.getOWLNamedClass(str + "Color_firework");
				Collection fwColorSubClass = fwColorClass.getSubclasses(true);// 获取Color_firework的子类
				OWLObjectProperty fireworkColorSuitableForTemplateProperty = model
						.getOWLObjectProperty(str + "fireworkColorSuitableForTemplate");
				/**
				 * 遍历fwColor类的子类===========================开始===================
				 * ========= ==== ===============
				 */
				for (Iterator it = fwColorSubClass.iterator(); it.hasNext();) {
					OWLNamedClass xxsubclass = (OWLNamedClass) it.next();// 为fwColor类的某个子类
					System.out.println(xxsubclass.getBrowserText().trim());
					// 若xxsubclass(如：red类)没有fireworkColorSuitableForFirework属性，则查找下一个子类
					if (xxsubclass.getSomeValuesFrom(fireworkColorSuitableForTemplateProperty) == null)
						continue;
					// 获取fwColor某一子类（如red）getSomeValuesFrom的值
					String result_getSomeValuesFrom = xxsubclass
							.getSomeValuesFrom(fireworkColorSuitableForTemplateProperty).getBrowserText();
					// System.out.println("result_getSomeValuesFrom=" +
					// result_getSomeValuesFrom);
					if (result_getSomeValuesFrom != null) {
						String hasValues = result_getSomeValuesFrom;
						// ==========开始========getSomeValuesFrom的值有多个的话=========================
						if (hasValues.contains("or")) {
							String[] hasValuesSpilt = hasValues.split("or");
							// System.out.println("hasValuesSpilt[0]="+hasValuesSpilt[0]);
							// 遍历所有得到的getSomeValuesFrom的结果，判断是否跟模板类相等，如相等则求出关联该ie主题的烟花颜色实例的fireworkColor数据属性
							for (int t = 0; t < hasValuesSpilt.length; t++) {
								System.out.println("hasValuesSpilt[" + t + "]=" + hasValuesSpilt[t].toString().trim());
								OWLNamedClass xxtemplateClass = model
										.getOWLNamedClass(hasValuesSpilt[t].toString().trim());
								// System.out.println(xxtopicClass.toString());
								if (xxtemplateClass == null) {// 若该模板类不存在
									continue;
								}
								// =================开始======================若传进来的模板等于烟花某一实例关联的模板
								if (xxtemplateClass.equalsStructurally(xxTemplateListClass)) {

									// 获取该类（烟花颜色类）下的所有实例
									Collection subclassIndividuals = xxsubclass.getInstances(true);
									if (subclassIndividuals.size() != 0) {
										for (Iterator it1 = subclassIndividuals.iterator(); it1.hasNext();) {
											OWLIndividual xxColor = (OWLIndividual) it1.next();
											System.out.println("该" + xxTemplateListClass.getBrowserText() + "模板匹配烟花颜色类"
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
								} // =================结束======================若该主题等于IE抽取的主题

							}

						} // ==========结束========getSomeValuesFrom的值有多个的话=========================
						else if (hasValues != null) {// 若不包含or（即只有一个值）
							OWLNamedClass xxtemplateClass = model.getOWLNamedClass(hasValues.trim());
							System.out.println(xxtemplateClass.getBrowserText().trim());
							if (xxtemplateClass.equalsStructurally(xxTemplateListClass)) {
								// 获取该类（烟花颜色类）下的所有实例
								Collection subclassIndividuals = xxsubclass.getInstances(true);
								if (subclassIndividuals.size() != 0) {
									for (Iterator it1 = subclassIndividuals.iterator(); it1.hasNext();) {
										OWLIndividual xxColor = (OWLIndividual) it1.next();

										System.out.println("该" + xxTemplateListClass.getBrowserText() + "模板匹配烟花颜色类"
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
						break;//不在遍历模板类的其他元素
				}
				/**
				 * 遍历fwColor类的子类===========================结束===================
				 * ========= ==== ===============
				 */
				if(isfwColorsetted)
					break;//不在遍历模板类的其他元素
			}
			/**
			 * 只有fwType赋值成功，才会调用doc = printFireworkRule(doc, fwType, fwColor)方法添加烟花规则
			 */
			if(!fwType.equals("")){
				if(fwColor.equals("")){
					fwColor="default";
				}
				System.out.println("fwType="+fwType+",fwColor="+fwColor);
				doc = printFireworkRule(doc, fwType, fwColor);
				return doc;
			}
			// ==============================模板处理结束=======================================================");
		}
		return doc;
	}

	public Document printFireworkRule(Document doc, String fireworkType, String fireworkColor) {
		System.out.println("=====================开始生成xml-rule======================");
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "addFireworkToMa");
		ruleName.addAttribute("fireworkType", fireworkType);
		ruleName.addAttribute("fireworkColor", fireworkColor);
		System.out.println("=====================结束生成xml-rule======================");
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

		System.out.println("开始!");
		FireworkInsert firework = new FireworkInsert();
		Document document1 = firework.fireworkInfer(alist, model, "play_football.ma", document);
		XMLWriter writer = new XMLWriter(new FileWriter("F:/TestFilework/testFirework.xml"));
		writer.write(document1);
		System.out.println("结束！");
		writer.close();
	}

}
