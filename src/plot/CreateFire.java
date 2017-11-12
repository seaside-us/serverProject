package plot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.*;
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
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;

public class CreateFire {
	private boolean isaddFiresetted = false;
	private boolean issuitabletopic = false;
	@SuppressWarnings("unchecked")
	public Document CreateFireInfer(ArrayList<String> list, OWLModel model, String maName, Document doc)
		throws OntologyLoadException, SWRLRuleEngineException {
		String str = "p11:";
		String str1 = "";
		String ieTopic;//ie主题
		String ieTopic1;
		String addFire = "";
		String modelName = "";
		//topicList存放主题类
		ArrayList<String> topicList = new ArrayList<String>();
		ArrayList<String> allpeople = new ArrayList<String>();
		ArrayList<String> people_model = new ArrayList<String>();
		ArrayList<String> allmodel = new ArrayList<String>();
		/*
		 * 获取doc根节点
		 */
		Element rootName = doc.getRootElement();
		Element name = rootName.element("maName");
		String adlTopic = name.attributeValue("topic");
		System.out.println("adlTopic=" + adlTopic);
		/*
		 * 获取ma实例
		 */
		OWLIndividual maIndividual = model.getOWLIndividual(str1 + maName);
		if(maIndividual == null){
			System.out.println("ma实例无法获取，可能不存在或丢失！");
			return doc;
		}else
			System.out.println("获取实例成功，maya实例：" + maIndividual);
		// 用到的各种属性
		@SuppressWarnings("unused")
		OWLObjectProperty hasTopicPorperty = model.getOWLObjectProperty("hasTopic");
		OWLObjectProperty hasValueOfPlaceFlag = model.getOWLObjectProperty(str1 + "hasValueOfPlace");
		OWLDatatypeProperty addModelTypeProperty=model.getOWLDatatypeProperty("addModelType");
		System.out.println("......."+addModelTypeProperty.toString()+"........");
		/*
		 * 用于判断获取的ma实例是室内场景还是室外场景
		 */
		OWLIndividual individualOfPlaceFlag = (OWLIndividual) maIndividual.getPropertyValue(hasValueOfPlaceFlag);
		if (individualOfPlaceFlag == null) {
			System.out.println("无法获取实例" + maIndividual.getBrowserText()
					+ "的hasValueOfPlace属性(此属性用来判断场景属于室内还是室外)，so 拒绝加火！请检查知识库中该属性是否存在或正确？");
			return doc;
		} else {
			// 若是室內場景，則拒絕添加火焰
			if (individualOfPlaceFlag.getBrowserText().equals("InWaterDescription")) {
				System.out.println("此场景为水下，因此不适合添加火焰！");
				return doc;
			}
		}
		/**
		 * 以下代码即是处理：场景为室外的情况
		 */
		System.out.println("================场景的主题处理================");
		System.out.println("================优先处理IE主题================");
		ieTopic1 = doc.getRootElement().element("maName").attributeValue("topic");// 此ieTopic可能为空字符串
		/**
		 * 若adl文档中抽出了主题
		 */
		if(ieTopic1.contains("Angry")){
			System.out.println("=================");
			ieTopic = "AngryTopic";
			topicList.add(ieTopic);// 添加到主题类列表中
			OWLNamedClass topic = model.getOWLNamedClass(str1 + ieTopic);
			
			OWLObjectProperty createByTopicProperty = model.getOWLObjectProperty(str + "createByTopic");
			OWLNamedClass firetypeClass = model.getOWLNamedClass(str + "firetype");
			Collection firetypeSubClass = firetypeClass.getSubclasses(true);
			/*
			 * 遍历子类
			 */
			for(Iterator it = firetypeSubClass.iterator();it.hasNext();){
				OWLNamedClass xxsubclass = (OWLNamedClass) it.next();
				System.out.println(xxsubclass.getBrowserText());
				if(xxsubclass.getSomeValuesFrom(createByTopicProperty) == null){
					continue;
				}
				String result_getSomeValuesFrom = xxsubclass.getSomeValuesFrom(createByTopicProperty).getBrowserText();
				System.out.println("result_getSomeVlauesFrom=" + result_getSomeValuesFrom);
				if(result_getSomeValuesFrom !=null){
					String hasValues = result_getSomeValuesFrom;
					if(hasValues.contains("or")){
						String[] hasValuesSpilt = hasValues.split("or");
						System.out.println("hasValuesSpilt.length=" + hasValuesSpilt.length);
						for(int j = 0; j<hasValuesSpilt.length; j++){
							System.out.println("hasValuesSplit[" + j +"]" + hasValuesSpilt[j].toString().trim());
							OWLNamedClass xxtopicClass = model.getOWLNamedClass(hasValuesSpilt[j].toString().trim());
							if(xxtopicClass ==null){
								continue;
							}
							if(xxtopicClass.equalsStructurally(topic)){
								System.out.println("该" + xxsubclass.getBrowserText() + "类与ie主题关联");
								Collection subclassIndividuals = xxsubclass.getInstances(true);
								if (subclassIndividuals.size() != 0) {
									for (Iterator it1 = subclassIndividuals.iterator();it1.hasNext();){
										OWLIndividual xxType_1 = (OWLIndividual) it1.next();
										System.out.println("该" + xxsubclass.getBrowserText() + "类的实例为： " + xxType_1.getBrowserText());
										OWLDatatypeProperty firesizeProperty = model.getOWLDatatypeProperty(str + "firesize");
										String addfire = xxType_1.getPropertyValue(firesizeProperty).toString();
										addFire = addfire;
										System.out.println("addFire=" + addfire);
									}
									break;
								}
								isaddFiresetted = true;// 
								break;
							}
						}
					}
					else if(hasValues != null){
						OWLNamedClass xxtopicClass =model.getOWLNamedClass(hasValues.trim());
						if(xxtopicClass.equalsStructurally(topic)){
							System.out.println("该" +xxsubclass.getBrowserText() + "类与ie主题关联");
							Collection subclassIndividuals = xxsubclass.getInstances(true);
							if(subclassIndividuals.size() != 0){
								for(Iterator it1 =subclassIndividuals.iterator(); it1.hasNext();){
									OWLIndividual xxType_1 = (OWLIndividual) it1.next();
									System.out.println("该" + xxsubclass.getBrowserText() + "类的实例为：" + xxType_1.getBrowserText());
									OWLDatatypeProperty firesizeProperty = model.getOWLDatatypeProperty(str + "firesize");
									String addfire = xxType_1.getPropertyValue(firesizeProperty).toString();
									addFire = addfire;
									System.out.println("addFire=" + addFire);
								}
							}
							isaddFiresetted = true;
						}
					}
				}
				if(isaddFiresetted)
					break;
			}
			if(!addFire.equals("")){
				System.out.println("addFire=" + addFire);
				issuitabletopic = true;
			}
		}
		else if (ieTopic1.contains("Topic")) {
			ieTopic = ieTopic1;
			System.out.println("IE Topic = " + ieTopic);
			topicList.add(ieTopic);// 添加到主题类列表中
			OWLNamedClass topic = model.getOWLNamedClass(str1 + ieTopic);
			System.out.println("topic:" + topic.getBrowserText());
			
			OWLObjectProperty createByTopicProperty = model.getOWLObjectProperty(str + "createByTopic");
			System.out.println(createByTopicProperty.getBrowserText());
			OWLNamedClass firetypeClass = model.getOWLNamedClass(str + "firetype");
			System.out.println(firetypeClass.getBrowserText());
			Collection firetypeSubClass = firetypeClass.getSubclasses(true);
			/*
			 * 遍历子类
			 */
			for(Iterator it = firetypeSubClass.iterator();it.hasNext();){
				OWLNamedClass xxsubclass = (OWLNamedClass) it.next();
				System.out.println(xxsubclass.getBrowserText());
				if(xxsubclass.getSomeValuesFrom(createByTopicProperty) == null){
					continue;
				}
				String result_getSomeValuesFrom = xxsubclass.getSomeValuesFrom(createByTopicProperty).getBrowserText();
				System.out.println("result_getSomeVlauesFrom=" + result_getSomeValuesFrom);
				if(result_getSomeValuesFrom !=null){
					String hasValues = result_getSomeValuesFrom;
					if(hasValues.contains("or")){
						String[] hasValuesSpilt = hasValues.split("or");
						System.out.println("hasValuesSpilt.length=" + hasValuesSpilt.length);
						for(int j = 0; j<hasValuesSpilt.length; j++){
							System.out.println("hasValuesSplit[" + j +"]" + hasValuesSpilt[j].toString().trim());
							OWLNamedClass xxtopicClass = model.getOWLNamedClass(hasValuesSpilt[j].toString().trim());
							if(xxtopicClass ==null){
								continue;
							}
							if(xxtopicClass.equalsStructurally(topic)){
								System.out.println("该" + xxsubclass.getBrowserText() + "类与ie主题关联");
								Collection subclassIndividuals = xxsubclass.getInstances(true);
								if (subclassIndividuals.size() != 0) {
									for (Iterator it1 = subclassIndividuals.iterator();it1.hasNext();){
										OWLIndividual xxType_1 = (OWLIndividual) it1.next();
										System.out.println("该" + xxsubclass.getBrowserText() + "类的实例为： " + xxType_1.getBrowserText());
										OWLDatatypeProperty firesizeProperty = model.getOWLDatatypeProperty(str + "firesize");
										String addfire = xxType_1.getPropertyValue(firesizeProperty).toString();
										addFire = addfire;
										System.out.println("addFire=" + addfire);
									}
									break;
								}
								isaddFiresetted = true;// 
								break;
							}
						}
					}
					else if(hasValues != null){
						OWLNamedClass xxtopicClass =model.getOWLNamedClass(hasValues.trim());
						if(xxtopicClass.equalsStructurally(topic)){
							System.out.println("该" +xxsubclass.getBrowserText() + "类与ie主题关联");
							Collection subclassIndividuals = xxsubclass.getInstances(true);
							if(subclassIndividuals.size() != 0){
								for(Iterator it1 =subclassIndividuals.iterator(); it1.hasNext();){
									OWLIndividual xxType_1 = (OWLIndividual) it1.next();
									System.out.println("该" + xxsubclass.getBrowserText() + "类的实例为：" + xxType_1.getBrowserText());
									OWLDatatypeProperty firesizeProperty = model.getOWLDatatypeProperty(str + "firesize");
									String addfire = xxType_1.getPropertyValue(firesizeProperty).toString();
									addFire = addfire;
									System.out.println("addFire=" + addFire);
								}
							}
							isaddFiresetted = true;
						}
					}
				}
				if(isaddFiresetted)
					break;
			}
			if(!addFire.equals("")){
				System.out.println("addFire=" + addFire);
				issuitabletopic = true;
			}
		}
		if(issuitabletopic){
			//遍历模型
			List<Element> allChildren=name.elements("rule");
			Element childName;
			for(int i=0;i<allChildren.size();i++){
				childName=allChildren.get(i);
				if(childName.attributeValue("ruleType").equals("addToMa")){
					if(childName.attributeValue("type").equals("people")){
						allpeople.add(childName.attributeValue("addModel"));
					}
					else if(childName.attributeValue("type").equals("model")){
						Collection spaceList = null;
						OWLIndividual space_owl = null;
						OWLNamedClass spaceClass = model.getOWLNamedClass("PlaneSceneSpaceOnGround");
						spaceList = spaceClass.getInstances(true);
						String spacename = childName.attributeValue("spaceName");
						for(Iterator it = spaceList.iterator(); it.hasNext();){
							space_owl = (OWLIndividual) it.next();
							if(spacename.equals(space_owl.getBrowserText())){
								allmodel.add(childName.attributeValue("addModel"));
							}
						}
					}
				}
			}
		}else{
			return doc;
		}
		if(allpeople.size()>0){
			String people_name="";
			int people_num = allpeople.size();
			int rand = (int)(Math.random()*(people_num-1));
			people_name = allpeople.get(rand);
			List<Element> allChildren=name.elements("rule");
			Element childName;
			//可用空间名
			String space_people_name = "";
			for(int i=0;i<allChildren.size();i++){
				childName=allChildren.get(i);
				if((childName.attributeValue("ruleType").equals("addToMa")) && (childName.attributeValue("addModel").equals(people_name))){
					space_people_name = childName.attributeValue("spaceName");
					break;
				}
			}
			//同一可用空间模型
			for(int i=0;i<allChildren.size();i++){
				childName=allChildren.get(i);
				if((childName.attributeValue("ruleType").equals("addToMa")) && (childName.attributeValue("spaceName").equals(space_people_name)) && (childName.attributeValue("type").equals("model"))){
					people_model.add(childName.attributeValue("addModel"));
				}
			}
			//挑选模型
			if(people_model.size()>0){
				int model_num = people_model.size();
				rand = (int)(Math.random()*(model_num-1));
				modelName = allmodel.get(rand);
				doc = printCreateFireRule(doc, modelName, addFire);
			}
		}else if(allmodel.size()>0){
			int model_num = allmodel.size();
			int rand = (int)(Math.random()*(model_num-1));
			modelName = allmodel.get(rand);
			doc = printCreateFireRule(doc, modelName, addFire);
		}else{
			return doc;
		}
		return doc;
	}
	public Document printCreateFireRule(Document doc, String fireModelName, String addFire) {
		System.out.println("=====================开始生成xml-rule======================");
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "addFireToMa");
		ruleName.addAttribute("fireModelName",fireModelName);
		ruleName.addAttribute("fireType",addFire);
		System.out.println("xml-rule生成完毕");
		return doc;
	}
	

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException,
			JDOMException, DocumentException, OntologyLoadException, SWRLRuleEngineException {

		//String owlPath = "file:///E:/OWL/new/ontologyOWL/AllOwlFile/sumo_phone3.owl";
		String owlPath = "file:///C:/ontologyOWL/sumoOWL2/sumo_phone3.owl";
		OWLModel model=ProtegeOWL.createJenaOWLModelFromURI(owlPath);
		ArrayList<String> alist = new ArrayList<String>();
		alist.add("ComfortTemplate:comfortTemplate");
		alist.add("LikeTemplate:likeTemplate");
		alist.add("PromisesTemplate:promisesTemplate");
		
		File file = new File("E:/OWL/TEST/adl_result.xml");
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(file);
		
		System.out.println("开始!");
		CreateFire fire = new CreateFire();
		Document document1 = fire.CreateFireInfer(alist, model, "schoolroomOut.ma", document);
		XMLWriter writer = new XMLWriter(new FileWriter("E:/OWL/TEST/testFirework.xml"));
		writer.write(document1);
		System.out.println("结束！");
		writer.close();

	}

}
