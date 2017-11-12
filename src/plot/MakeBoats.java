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

import org.jdom.*;
import org.jdom.input.*;
import java.io.*;
import java.util.*;

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

//日志导出文件在D:/ljj目录下
import java.text.*;
import java.util.Date;
import java.util.Calendar;

class MakeBoats {
	
	
			
			//======================根据topic确定waveType===================
			
			private boolean makeBoatsFlag = false;
			public boolean isMakeBoatsFlag() {
				return makeBoatsFlag;
			}
			public void setMakeBoatsFlag(boolean makeBoatsFlag) {
				this.makeBoatsFlag = makeBoatsFlag;
			}
	
			
			//--------------------定义waveType-------------------
			private String waveType="middle";
			public void setWaveType(String type){
				this.waveType=type;
			}
			public String getWaveType(){
				return this.waveType;
			}
			
			//----------------------waveType定义结束-------------------
	
			public Document makeBoatsInfer(ArrayList<String> List, OWLModel model, String maName, Document doc)
					throws OntologyLoadException, SWRLRuleEngineException,IOException {
				
				//=================日志文件程序开始====================
				Calendar rightNow=Calendar.getInstance();
				Date d1=rightNow.getTime();
				SimpleDateFormat sdf1=new SimpleDateFormat("yyyyMMddHHmmss");
				String nowtime=(sdf1.format(d1)).toString();
				
				File dir=new File("D:/ljj");
				dir.mkdirs();
				String namefile=maName.substring(0, maName.length()-3);
				String makeBoatsLogStr="D:/ljj/"+nowtime+"_"+namefile+"_MakeBoatsLog.txt";
				File makeBoatsLog=new File(makeBoatsLogStr);
				FileWriter fw;
				fw=new FileWriter(makeBoatsLog,true);
				String lineEnd="\r\n";
				
				fw.write(nowtime+lineEnd);
				fw.write("海洋定性程序运行记录如下：\r\n");
			
				
				//================日志文件程序结束===========================
				
				
				
				

				String str = "p10:";// newFirework前缀
				String str1 = "";// 模板，主题前缀

				String ieTopic;// ie主题
				String waterWave = "";// 定义烟花类别（大，中，小）
				Boolean isMakeBoatsFlagSeted = false;// 标记属性（fwType）是否已经设置好
				int waterWaveTypeForTopicCounts = 0;// 符合主题的烟花实例（即知识库中可能多种类型的烟花实例对应同一个主题）
				// topicList存放主题类
				ArrayList<String> topicList = new ArrayList<String>();
				// 用于存储符合IE主题的烟花类实例
				List<OWLIndividual> waterWaveIndividualForTopicList = new ArrayList<OWLIndividual>();
				/*
				 * 获取doc的根节点
				 */
				Element rootName = doc.getRootElement();
				Element name = rootName.element("maName");
				String sceneName=name.attributeValue("name");
				System.out.println("场景获取名为:"+sceneName);
				fw.write("场景获取名为:"+sceneName+lineEnd);
				String adlTopic = name.attributeValue("topic");
				System.out.println("adlTopic="+adlTopic);// 输出主题（此主题是类层面上的，不是具体的实例）
				fw.write("adlTopic="+adlTopic+lineEnd);
				/*
				 * 获取ma实例
				 */
				OWLIndividual maIndividual = model.getOWLIndividual(str1 + maName);
				if (maIndividual == null) {
					System.out.println("maya 实例无法获取，可能不存在或丢失！请检查maName是否正确或存在");
					fw.write("maya 实例无法获取，可能不存在或丢失！请检查maName是否正确或存在"+lineEnd);
					return doc;
				} else
				{
					System.out.println("获取实例成功，maya实例：" + maIndividual);
					fw.write("获取实例成功，maya实例：" + maIndividual+lineEnd);
				}
				
				// 用到的各种属性
				OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
			
				

				// 处理主题信息
				System.out.println("================场景的主题处理================");
				System.out.println("================优先处理IE主题================");
				fw.write("================优先处理IE主题================"+lineEnd);
				ieTopic = doc.getRootElement().element("maName").attributeValue("topic");// 此ieTopic可能为空字符串
				/**
				 * 若adl文档中抽出了主题
				 */
				if (ieTopic.contains("Topic")) {
					System.out.println("IE Topic = " + ieTopic);
					fw.write("IE Topic = " + ieTopic+lineEnd);
					topicList.add(ieTopic);// 添加到主题类列表中
					OWLNamedClass topic = model.getOWLNamedClass(str1 + ieTopic);
					System.out.println("topic:" + topic.getBrowserText());

					OWLObjectProperty waterWaveTypeForTopicProperty = model.getOWLObjectProperty(str+"waterWaveType");
					System.out.println("waterWaveTypeForTopicProperty==" + waterWaveTypeForTopicProperty.getBrowserText());
					fw.write("waterWaveTypeForTopicProperty==" + waterWaveTypeForTopicProperty.getBrowserText()+lineEnd);
					OWLNamedClass waterWaveTypeClass = model.getOWLNamedClass(str + "waterWave");
					Collection<?> waterWaveTypeSubClass = waterWaveTypeClass.getSubclasses(true);// 获取fwType的子类
					System.out.println("waterWave类总共有子类：" + waterWaveTypeSubClass.size());
					fw.write("waterWave类总共有子类：" + waterWaveTypeSubClass.size()+lineEnd);
					/**
					 * 遍历子类===========================开始================================
					 * ===============
					 */
					for (Iterator<?> it = waterWaveTypeSubClass.iterator(); it.hasNext();) {
						OWLNamedClass xxsubclass = (OWLNamedClass) it.next();// 为fwType类的某个子类
						// 若xxsubclass(如：miniType,moderateType,splendidType类)没有fireworkSuitableForFirework属性，则查找下一个子类
						if (xxsubclass.getSomeValuesFrom(waterWaveTypeForTopicProperty) == null)
							continue;
						// 获取fwType某一子类（如miniType）getSomeValuesFrom的值
						String result_getSomeValuesFrom = xxsubclass.getSomeValuesFrom(waterWaveTypeForTopicProperty)
								.getBrowserText();
						System.out.println("result_getSomeValuesFrom=" + result_getSomeValuesFrom);
						if (result_getSomeValuesFrom != null) {
							String hasValues = result_getSomeValuesFrom;
							// ==========开始========getSomeValuesFrom的值有多个的话=========================
							if (hasValues.contains("or")) {
								String[] hasValuesSpilt = hasValues.split("or");
								// System.out.println("hasValuesSpilt[0]="+hasValuesSpilt[0]);
								// 遍历所有得到的getSomeValuesFrom的结果，判断是否跟主题类相等，如相等则求出关联该ie主题的烟花实例的firesize数据属性
								for (int j = 0; j < hasValuesSpilt.length; j++) {
									OWLNamedClass xxtopicClass = model.getOWLNamedClass(hasValuesSpilt[j].toString().trim());
									// System.out.println(xxtopicClass.toString());//
									// 输出该烟花类关联的主题类名
									if (xxtopicClass == null) {// 若该主题类不存在
										continue;
									}
									// =================开始======================若该主题等于IE抽取的主题
									if (xxtopicClass.equalsStructurally(topic)) {
										System.out.println("该" + xxsubclass.getBrowserText() + "类与ie主题关联");
										// 获取该类（烟花类）下的所有实例
										Collection<?> subclassIndividuals = xxsubclass.getInstances(true);
										if (subclassIndividuals.size() != 0) {
											for (Iterator<?> it1 = subclassIndividuals.iterator(); it1.hasNext();) {
												OWLIndividual xxType_1 = (OWLIndividual) it1.next();

												System.out.println("该" + xxsubclass.getBrowserText() + "类的实例为："
														+ xxType_1.getBrowserText());
												OWLDatatypeProperty waterWaveSizeProperty = model
														.getOWLDatatypeProperty(str + "waterWaveSize");
												String waterWaveType = xxType_1.getPropertyValue(waterWaveSizeProperty).toString();
												waterWave = waterWaveType;
												System.out.println("waterWaveType=" + waterWaveType);
												setWaveType(waterWaveType);
												//doc = printMakeBoatsRule(doc, waterWaveType);
											//	return doc;
											}
										}

									}
									// =================结束======================若该主题等于IE抽取的主题
								}

							} // ==========结束========getSomeValuesFrom的值有多个的话=========================
							else if (hasValues != null) {// 若不包含or（即只有一个值）
								OWLNamedClass xxtopicClass = model.getOWLNamedClass(hasValues.trim());
								if (xxtopicClass.equalsStructurally(topic)) {
									System.out.println("该" + xxsubclass.getBrowserText() + "类与ie主题关联");
									// 获取该类（烟花类）下的所有实例
									Collection<?> subclassIndividuals = xxsubclass.getInstances(true);
									if (subclassIndividuals.size() != 0) {
										for (Iterator<?> it1 = subclassIndividuals.iterator(); it1.hasNext();) {
											OWLIndividual xxType_1 = (OWLIndividual) it1.next();

											System.out.println(
													"该" + xxsubclass.getBrowserText() + "类的实例为：" + xxType_1.getBrowserText());
											OWLDatatypeProperty waterWaveSizeProperty = model
													.getOWLDatatypeProperty(str + "waterWaveSize");
											String waterWaveType = xxType_1.getPropertyValue(waterWaveSizeProperty).toString();
											waterWave = waterWaveType;
											System.out.println("waterWaveType=" + waterWaveType);
											setWaveType(waterWaveType);
											fw.write("主题程序结束"+lineEnd);
											fw.flush();
											//doc = printMakeBoatsRule(doc, waterWaveType);
											//return doc;
										}
									}
								}
							}
						}

					}
					/**
					 * 遍历子类===========================结束================================
					 * ===============
					 */
				}else if(List.size()>0){
					
					System.out.println("=======从主题无法求得waveType，考虑用模板处理===============    ");
					fw.write("=======从主题无法求得waveType，考虑用模板处理==============="+lineEnd);
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
						OWLNamedClass fwTypeClass = model.getOWLNamedClass(str + "waterWave");
						Collection fwTypeSubClass = fwTypeClass.getSubclasses(true);// 获取fwType的子类
						OWLObjectProperty waterWaveTypeForTemplateProperty = model
								.getOWLObjectProperty(str + "waterWaveTypeForTemplate");
						/**
						 * 遍历子类===========================开始============================
						 * ==== ===============
						 */
						for (Iterator it = fwTypeSubClass.iterator(); it.hasNext();) {
							OWLNamedClass xxsubclass = (OWLNamedClass) it.next();// 为fwType类的某个子类
							// 若xxsubclass(如：miniType,moderateType,splendidType类)没有fireworkSuitableForFirework属性，则查找下一个子类
							if (xxsubclass.getSomeValuesFrom(waterWaveTypeForTemplateProperty) == null)
								continue;
							// 获取fwType某一子类（如miniType）getSomeValuesFrom的值
							String result_getSomeValuesFrom = xxsubclass.getSomeValuesFrom(waterWaveTypeForTemplateProperty)
									.getBrowserText();
							//System.out.println("result_getSomeValuesFrom=" + result_getSomeValuesFrom);
							if (result_getSomeValuesFrom != null) {
								String hasValues = result_getSomeValuesFrom;
								// ==========开始========getSomeValuesFrom的值有多个的话=========================
								if (hasValues.contains("or")) {
									String[] hasValuesSpilt = hasValues.split("or");
									// System.out.println("hasValuesSpilt[0]="+hasValuesSpilt[0]);
									// 遍历所有得到的getSomeValuesFrom的结果，判断是否跟模板类相等，如相等则求出关联该ie主题的烟花实例的firesize数据属性
									for (int t = 0; t < hasValuesSpilt.length; t++) {
										OWLNamedClass xxtemplateClass = model
												.getOWLNamedClass(hasValuesSpilt[t].toString().trim());
										// System.out.println(xxtopicClass.toString());//
										// 输出该烟花类关联的主题类名
										if (xxtemplateClass == null) {// 若该模板类不存在
											continue;
										}
										// =================开始======================若传进来的模板等于烟花某一实例关联的模板
										if (xxtemplateClass.equalsStructurally(xxTemplateListClass)) {
											System.out.println("该" + xxTemplateListClass.getBrowserText() + "模板匹配海洋效果");
											// 获取该类（烟花类）下的所有实例
											Collection subclassIndividuals = xxsubclass.getInstances(true);
											if (subclassIndividuals.size() != 0) {
												for (Iterator it1 = subclassIndividuals.iterator(); it1.hasNext();) {
													OWLIndividual xxType_1 = (OWLIndividual) it1.next();

													System.out.println("该" + xxsubclass.getBrowserText() + "类的实例为："
															+ xxType_1.getBrowserText());
													OWLDatatypeProperty waterWaveSizeProperty = model
															.getOWLDatatypeProperty(str + "waterWaveSize");
													/*String fwtype = xxType_1.getPropertyValue(waterWaveSizeProperty).toString();
													fwType = fwtype;
													System.out.println("fwType=" + fwType);
													doc = printFireworkRule(doc, fwType);
													return doc;
													*/
													String waterWaveType = xxType_1.getPropertyValue(waterWaveSizeProperty).toString();
													waterWave = waterWaveType;
													System.out.println("waterWaveType=" + waterWaveType);
													setWaveType(waterWaveType);
												}
											}

										}
										// =================结束======================若该主题等于IE抽取的主题
									}

								} // ==========结束========getSomeValuesFrom的值有多个的话=========================
								else if (hasValues != null) {// 若不包含or（即只有一个值）
									OWLNamedClass xxtopicClass = model.getOWLNamedClass(hasValues.trim());
									if (xxtopicClass.equalsStructurally(xxTemplateListClass)) {
										System.out.println("该" + xxsubclass.getBrowserText() + "模板匹配海洋效果");
										// 获取该类（烟花类）下的所有实例
										Collection subclassIndividuals = xxsubclass.getInstances(true);
										if (subclassIndividuals.size() != 0) {
											for (Iterator it1 = subclassIndividuals.iterator(); it1.hasNext();) {
												OWLIndividual xxType_1 = (OWLIndividual) it1.next();

												System.out.println("该" + xxsubclass.getBrowserText() + "类的实例为："
														+ xxType_1.getBrowserText());
												OWLDatatypeProperty waterWaveSizeProperty = model
														.getOWLDatatypeProperty(str + "waterWaveSize");
												/*String fwtype = xxType_1.getPropertyValue(waterWaveSizeProperty).toString();
												fwType = fwtype;
												System.out.println("fwType=" + fwType);
												doc = printFireworkRule(doc, fwType);
												return doc;
												*/
												String waterWaveType = xxType_1.getPropertyValue(waterWaveSizeProperty).toString();
												waterWave = waterWaveType;
												System.out.println("waterWaveType=" + waterWaveType);
												setWaveType(waterWaveType);
																		
											}
										}
									}
								}
							}

						}
						/**
						 * 遍历子类===========================结束============================
						 * ==== ===============
						 */
					}
					fw.write("==============================模板处理结束======================================================="+lineEnd);
					fw.flush();
					// ==============================模板处理结束=======================================================
				}
				else
				{
					System.out.println("=================不可由主题或模板求出waveType,设置为默认值middle================");	
					fw.write("=================不可由主题或模板求出waveType,设置为默认值middle================");	
					fw.flush();
				}
		//===================topic确定结束==================================
			
		//=================template确定waveSize开始========================
					
				
		//=================template确定waveSize结束========================			
			
			
			//根据场景名判断是否加波浪
			
			System.out.println("以下为确定makeBoats物体");
			fw.write("以下为确定makeBoats物体"+lineEnd);
			System.out.println("场景名为："+maName);
			ArrayList<String> boatModel=new ArrayList<String>();

			
			String TargetSpaceStr="";
			List<Element> allChildren=name.elements("rule");
			Element childName;
			for(int i=0;i<allChildren.size();i++){
				childName=allChildren.get(i);
				if((childName.attributeValue("ruleType")).equals("addToMa"))
					{
						if(sceneName.equals("beach.ma"))
						{			
							if((childName.attributeValue("spaceName")).equals("SP_beach_A"))
							{
								boatModel.add(childName.attributeValue("addModel"));
							}
							TargetSpaceStr="SP_beach_A";
						}
						else if(sceneName.equals("shark.ma"))
						{
							String spname=childName.attributeValue("spaceName");
									
							if(spname.equals("SP_shark_A") || str.equals("SP_shark_B"))
							{
								boatModel.add(childName.attributeValue("addModel"));
							}
							TargetSpaceStr="SP_shark_A or SP_shark_B";
						}
						else if(sceneName.equals("Bridge04.ma"))
						{
							String spname=childName.attributeValue("spaceName");
									
							if(spname.equals("SP_Bridge04_A"))
							{
								boatModel.add(childName.attributeValue("addModel"));
							}
							TargetSpaceStr="SP_Bridge04_A";
						}
						else if(sceneName.equals("undersea_2.ma"))
						{
							String spname=childName.attributeValue("spaceName");
									
							if(spname.equals("SP_undersea_2_B"))
							{
								boatModel.add(childName.attributeValue("addModel"));
							}
							TargetSpaceStr="SP_undersea_2_B";
						}
						else if(sceneName.equals("undersea_3.ma"))
						{
							String spname=childName.attributeValue("spaceName");
									
							if(spname.equals("SP_undersea_3_A")||str.equals("SP_undersea_3_C")||str.equals("SP_undersea_3_B"))
							{
								boatModel.add(childName.attributeValue("addModel"));
							}
							TargetSpaceStr="SP_undersea_3_A or SP_undersea_3_C or  SP_undersea_3_B";
						}
						else if(sceneName.equals("undersea1.ma"))
						{
							String spname=childName.attributeValue("spaceName");
									
							if(spname.equals("SP_undersea1_C")||str.equals("SP_undersea1_A"))
							{
								boatModel.add(childName.attributeValue("addModel"));
							}
							TargetSpaceStr="SP_undersea1_A";
						}	
						else if(sceneName.equals("comeBack.ma"))
						{
							String spname=childName.attributeValue("spaceName");
									
							if(spname.equals("SP_comeBack_A"))
							{
								boatModel.add(childName.attributeValue("addModel"));
							}
							TargetSpaceStr="SP_comeBack_A";
						}		
					
					
					}
			}				
			if(boatModel.size()>0)
			{
				
				System.out.println("设置海洋属性");
				
			//	String waveType="high";
				System.out.println("=====================开始生成xml-rule======================");
				fw.write("生成海洋xml-rule开始！！！！!"+lineEnd);
				for(int i=0;i<boatModel.size();i++){
					fw.write(boatModel.get(i)+lineEnd);
					fw.write(TargetSpaceStr+lineEnd);
					doc=printMakeBoatsRule(doc,getWaveType(),boatModel.get(i),String.valueOf(i));	
				}
				System.out.println("=====================结束生成xml-rule======================");
				fw.write("生成海洋xml-rule结束！！！！!"+lineEnd);
				fw.flush();
				fw.close();
				return doc;							
			}
			else
			{
				System.out.println("无法设置海洋属性");
				fw.write("无法设置海洋属性"+lineEnd);
				fw.flush();
				fw.close();
				return doc;
			}
		}


	public Document printMakeBoatsRule(Document doc, String waveType,String boatModelName,String i) {
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "makeBoatsToMa");
		ruleName.addAttribute("waveType", waveType);
		ruleName.addAttribute("boatModelName", boatModelName);
		ruleName.addAttribute("boatModelID",i);				
		return doc;
	}
	

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException,
			JDOMException, DocumentException, OntologyLoadException, SWRLRuleEngineException {

		//String owlPath = "file:///C:/ontologyOWL/AllOwlFile/sumo_phone3.owl";
		String owlPath = "file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";	
		OWLModel model = ProtegeOWL.createJenaOWLModelFromURI(owlPath);
		ArrayList<String> alist = new ArrayList<String>();
		alist.add("ComfortTemplate:comfortTemplate");
		alist.add("LikeTemplate:likeTemplate");
		alist.add("PromisesTemplate:promisesTemplate");

		File file = new File("F:/TestFilework/adl_result.xml");
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(file);

		System.out.println("开始!");
		MakeBoats makeBoats = new MakeBoats();
		Document document1 = makeBoats.makeBoatsInfer(alist, model, "beach.ma", document);
		XMLWriter writer = new XMLWriter(new FileWriter("F:/TestFilework/makeBoats_test.xml"));
		writer.write(document1);
		System.out.println("结束！");
		writer.close();
	}


}
