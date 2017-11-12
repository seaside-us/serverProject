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

//��־�����ļ���D:/ljjĿ¼��
import java.text.*;
import java.util.Date;
import java.util.Calendar;

class MakeBoats {
	
	
			
			//======================����topicȷ��waveType===================
			
			private boolean makeBoatsFlag = false;
			public boolean isMakeBoatsFlag() {
				return makeBoatsFlag;
			}
			public void setMakeBoatsFlag(boolean makeBoatsFlag) {
				this.makeBoatsFlag = makeBoatsFlag;
			}
	
			
			//--------------------����waveType-------------------
			private String waveType="middle";
			public void setWaveType(String type){
				this.waveType=type;
			}
			public String getWaveType(){
				return this.waveType;
			}
			
			//----------------------waveType�������-------------------
	
			public Document makeBoatsInfer(ArrayList<String> List, OWLModel model, String maName, Document doc)
					throws OntologyLoadException, SWRLRuleEngineException,IOException {
				
				//=================��־�ļ�����ʼ====================
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
				fw.write("�����Գ������м�¼���£�\r\n");
			
				
				//================��־�ļ��������===========================
				
				
				
				

				String str = "p10:";// newFireworkǰ׺
				String str1 = "";// ģ�壬����ǰ׺

				String ieTopic;// ie����
				String waterWave = "";// �����̻���𣨴��У�С��
				Boolean isMakeBoatsFlagSeted = false;// ������ԣ�fwType���Ƿ��Ѿ����ú�
				int waterWaveTypeForTopicCounts = 0;// ����������̻�ʵ������֪ʶ���п��ܶ������͵��̻�ʵ����Ӧͬһ�����⣩
				// topicList���������
				ArrayList<String> topicList = new ArrayList<String>();
				// ���ڴ洢����IE������̻���ʵ��
				List<OWLIndividual> waterWaveIndividualForTopicList = new ArrayList<OWLIndividual>();
				/*
				 * ��ȡdoc�ĸ��ڵ�
				 */
				Element rootName = doc.getRootElement();
				Element name = rootName.element("maName");
				String sceneName=name.attributeValue("name");
				System.out.println("������ȡ��Ϊ:"+sceneName);
				fw.write("������ȡ��Ϊ:"+sceneName+lineEnd);
				String adlTopic = name.attributeValue("topic");
				System.out.println("adlTopic="+adlTopic);// ������⣨��������������ϵģ����Ǿ����ʵ����
				fw.write("adlTopic="+adlTopic+lineEnd);
				/*
				 * ��ȡmaʵ��
				 */
				OWLIndividual maIndividual = model.getOWLIndividual(str1 + maName);
				if (maIndividual == null) {
					System.out.println("maya ʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ�����");
					fw.write("maya ʵ���޷���ȡ�����ܲ����ڻ�ʧ������maName�Ƿ���ȷ�����"+lineEnd);
					return doc;
				} else
				{
					System.out.println("��ȡʵ���ɹ���mayaʵ����" + maIndividual);
					fw.write("��ȡʵ���ɹ���mayaʵ����" + maIndividual+lineEnd);
				}
				
				// �õ��ĸ�������
				OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
			
				

				// ����������Ϣ
				System.out.println("================���������⴦��================");
				System.out.println("================���ȴ���IE����================");
				fw.write("================���ȴ���IE����================"+lineEnd);
				ieTopic = doc.getRootElement().element("maName").attributeValue("topic");// ��ieTopic����Ϊ���ַ���
				/**
				 * ��adl�ĵ��г��������
				 */
				if (ieTopic.contains("Topic")) {
					System.out.println("IE Topic = " + ieTopic);
					fw.write("IE Topic = " + ieTopic+lineEnd);
					topicList.add(ieTopic);// ��ӵ��������б���
					OWLNamedClass topic = model.getOWLNamedClass(str1 + ieTopic);
					System.out.println("topic:" + topic.getBrowserText());

					OWLObjectProperty waterWaveTypeForTopicProperty = model.getOWLObjectProperty(str+"waterWaveType");
					System.out.println("waterWaveTypeForTopicProperty==" + waterWaveTypeForTopicProperty.getBrowserText());
					fw.write("waterWaveTypeForTopicProperty==" + waterWaveTypeForTopicProperty.getBrowserText()+lineEnd);
					OWLNamedClass waterWaveTypeClass = model.getOWLNamedClass(str + "waterWave");
					Collection<?> waterWaveTypeSubClass = waterWaveTypeClass.getSubclasses(true);// ��ȡfwType������
					System.out.println("waterWave���ܹ������ࣺ" + waterWaveTypeSubClass.size());
					fw.write("waterWave���ܹ������ࣺ" + waterWaveTypeSubClass.size()+lineEnd);
					/**
					 * ��������===========================��ʼ================================
					 * ===============
					 */
					for (Iterator<?> it = waterWaveTypeSubClass.iterator(); it.hasNext();) {
						OWLNamedClass xxsubclass = (OWLNamedClass) it.next();// ΪfwType���ĳ������
						// ��xxsubclass(�磺miniType,moderateType,splendidType��)û��fireworkSuitableForFirework���ԣ��������һ������
						if (xxsubclass.getSomeValuesFrom(waterWaveTypeForTopicProperty) == null)
							continue;
						// ��ȡfwTypeĳһ���ࣨ��miniType��getSomeValuesFrom��ֵ
						String result_getSomeValuesFrom = xxsubclass.getSomeValuesFrom(waterWaveTypeForTopicProperty)
								.getBrowserText();
						System.out.println("result_getSomeValuesFrom=" + result_getSomeValuesFrom);
						if (result_getSomeValuesFrom != null) {
							String hasValues = result_getSomeValuesFrom;
							// ==========��ʼ========getSomeValuesFrom��ֵ�ж���Ļ�=========================
							if (hasValues.contains("or")) {
								String[] hasValuesSpilt = hasValues.split("or");
								// System.out.println("hasValuesSpilt[0]="+hasValuesSpilt[0]);
								// �������еõ���getSomeValuesFrom�Ľ�����ж��Ƿ����������ȣ�����������������ie������̻�ʵ����firesize��������
								for (int j = 0; j < hasValuesSpilt.length; j++) {
									OWLNamedClass xxtopicClass = model.getOWLNamedClass(hasValuesSpilt[j].toString().trim());
									// System.out.println(xxtopicClass.toString());//
									// ������̻����������������
									if (xxtopicClass == null) {// ���������಻����
										continue;
									}
									// =================��ʼ======================�����������IE��ȡ������
									if (xxtopicClass.equalsStructurally(topic)) {
										System.out.println("��" + xxsubclass.getBrowserText() + "����ie�������");
										// ��ȡ���ࣨ�̻��ࣩ�µ�����ʵ��
										Collection<?> subclassIndividuals = xxsubclass.getInstances(true);
										if (subclassIndividuals.size() != 0) {
											for (Iterator<?> it1 = subclassIndividuals.iterator(); it1.hasNext();) {
												OWLIndividual xxType_1 = (OWLIndividual) it1.next();

												System.out.println("��" + xxsubclass.getBrowserText() + "���ʵ��Ϊ��"
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
									// =================����======================�����������IE��ȡ������
								}

							} // ==========����========getSomeValuesFrom��ֵ�ж���Ļ�=========================
							else if (hasValues != null) {// ��������or����ֻ��һ��ֵ��
								OWLNamedClass xxtopicClass = model.getOWLNamedClass(hasValues.trim());
								if (xxtopicClass.equalsStructurally(topic)) {
									System.out.println("��" + xxsubclass.getBrowserText() + "����ie�������");
									// ��ȡ���ࣨ�̻��ࣩ�µ�����ʵ��
									Collection<?> subclassIndividuals = xxsubclass.getInstances(true);
									if (subclassIndividuals.size() != 0) {
										for (Iterator<?> it1 = subclassIndividuals.iterator(); it1.hasNext();) {
											OWLIndividual xxType_1 = (OWLIndividual) it1.next();

											System.out.println(
													"��" + xxsubclass.getBrowserText() + "���ʵ��Ϊ��" + xxType_1.getBrowserText());
											OWLDatatypeProperty waterWaveSizeProperty = model
													.getOWLDatatypeProperty(str + "waterWaveSize");
											String waterWaveType = xxType_1.getPropertyValue(waterWaveSizeProperty).toString();
											waterWave = waterWaveType;
											System.out.println("waterWaveType=" + waterWaveType);
											setWaveType(waterWaveType);
											fw.write("����������"+lineEnd);
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
					 * ��������===========================����================================
					 * ===============
					 */
				}else if(List.size()>0){
					
					System.out.println("=======�������޷����waveType��������ģ�崦��===============    ");
					fw.write("=======�������޷����waveType��������ģ�崦��==============="+lineEnd);
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
						OWLNamedClass fwTypeClass = model.getOWLNamedClass(str + "waterWave");
						Collection fwTypeSubClass = fwTypeClass.getSubclasses(true);// ��ȡfwType������
						OWLObjectProperty waterWaveTypeForTemplateProperty = model
								.getOWLObjectProperty(str + "waterWaveTypeForTemplate");
						/**
						 * ��������===========================��ʼ============================
						 * ==== ===============
						 */
						for (Iterator it = fwTypeSubClass.iterator(); it.hasNext();) {
							OWLNamedClass xxsubclass = (OWLNamedClass) it.next();// ΪfwType���ĳ������
							// ��xxsubclass(�磺miniType,moderateType,splendidType��)û��fireworkSuitableForFirework���ԣ��������һ������
							if (xxsubclass.getSomeValuesFrom(waterWaveTypeForTemplateProperty) == null)
								continue;
							// ��ȡfwTypeĳһ���ࣨ��miniType��getSomeValuesFrom��ֵ
							String result_getSomeValuesFrom = xxsubclass.getSomeValuesFrom(waterWaveTypeForTemplateProperty)
									.getBrowserText();
							//System.out.println("result_getSomeValuesFrom=" + result_getSomeValuesFrom);
							if (result_getSomeValuesFrom != null) {
								String hasValues = result_getSomeValuesFrom;
								// ==========��ʼ========getSomeValuesFrom��ֵ�ж���Ļ�=========================
								if (hasValues.contains("or")) {
									String[] hasValuesSpilt = hasValues.split("or");
									// System.out.println("hasValuesSpilt[0]="+hasValuesSpilt[0]);
									// �������еõ���getSomeValuesFrom�Ľ�����ж��Ƿ��ģ������ȣ�����������������ie������̻�ʵ����firesize��������
									for (int t = 0; t < hasValuesSpilt.length; t++) {
										OWLNamedClass xxtemplateClass = model
												.getOWLNamedClass(hasValuesSpilt[t].toString().trim());
										// System.out.println(xxtopicClass.toString());//
										// ������̻����������������
										if (xxtemplateClass == null) {// ����ģ���಻����
											continue;
										}
										// =================��ʼ======================����������ģ������̻�ĳһʵ��������ģ��
										if (xxtemplateClass.equalsStructurally(xxTemplateListClass)) {
											System.out.println("��" + xxTemplateListClass.getBrowserText() + "ģ��ƥ�亣��Ч��");
											// ��ȡ���ࣨ�̻��ࣩ�µ�����ʵ��
											Collection subclassIndividuals = xxsubclass.getInstances(true);
											if (subclassIndividuals.size() != 0) {
												for (Iterator it1 = subclassIndividuals.iterator(); it1.hasNext();) {
													OWLIndividual xxType_1 = (OWLIndividual) it1.next();

													System.out.println("��" + xxsubclass.getBrowserText() + "���ʵ��Ϊ��"
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
										// =================����======================�����������IE��ȡ������
									}

								} // ==========����========getSomeValuesFrom��ֵ�ж���Ļ�=========================
								else if (hasValues != null) {// ��������or����ֻ��һ��ֵ��
									OWLNamedClass xxtopicClass = model.getOWLNamedClass(hasValues.trim());
									if (xxtopicClass.equalsStructurally(xxTemplateListClass)) {
										System.out.println("��" + xxsubclass.getBrowserText() + "ģ��ƥ�亣��Ч��");
										// ��ȡ���ࣨ�̻��ࣩ�µ�����ʵ��
										Collection subclassIndividuals = xxsubclass.getInstances(true);
										if (subclassIndividuals.size() != 0) {
											for (Iterator it1 = subclassIndividuals.iterator(); it1.hasNext();) {
												OWLIndividual xxType_1 = (OWLIndividual) it1.next();

												System.out.println("��" + xxsubclass.getBrowserText() + "���ʵ��Ϊ��"
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
						 * ��������===========================����============================
						 * ==== ===============
						 */
					}
					fw.write("==============================ģ�崦�����======================================================="+lineEnd);
					fw.flush();
					// ==============================ģ�崦�����=======================================================
				}
				else
				{
					System.out.println("=================�����������ģ�����waveType,����ΪĬ��ֵmiddle================");	
					fw.write("=================�����������ģ�����waveType,����ΪĬ��ֵmiddle================");	
					fw.flush();
				}
		//===================topicȷ������==================================
			
		//=================templateȷ��waveSize��ʼ========================
					
				
		//=================templateȷ��waveSize����========================			
			
			
			//���ݳ������ж��Ƿ�Ӳ���
			
			System.out.println("����Ϊȷ��makeBoats����");
			fw.write("����Ϊȷ��makeBoats����"+lineEnd);
			System.out.println("������Ϊ��"+maName);
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
				
				System.out.println("���ú�������");
				
			//	String waveType="high";
				System.out.println("=====================��ʼ����xml-rule======================");
				fw.write("���ɺ���xml-rule��ʼ��������!"+lineEnd);
				for(int i=0;i<boatModel.size();i++){
					fw.write(boatModel.get(i)+lineEnd);
					fw.write(TargetSpaceStr+lineEnd);
					doc=printMakeBoatsRule(doc,getWaveType(),boatModel.get(i),String.valueOf(i));	
				}
				System.out.println("=====================��������xml-rule======================");
				fw.write("���ɺ���xml-rule������������!"+lineEnd);
				fw.flush();
				fw.close();
				return doc;							
			}
			else
			{
				System.out.println("�޷����ú�������");
				fw.write("�޷����ú�������"+lineEnd);
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

		System.out.println("��ʼ!");
		MakeBoats makeBoats = new MakeBoats();
		Document document1 = makeBoats.makeBoatsInfer(alist, model, "beach.ma", document);
		XMLWriter writer = new XMLWriter(new FileWriter("F:/TestFilework/makeBoats_test.xml"));
		writer.write(document1);
		System.out.println("������");
		writer.close();
	}


}
