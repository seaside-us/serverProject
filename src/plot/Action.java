package plot;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.Attribute;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.Element;




import jess.Rete;
import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;
import edu.stanford.smi.protegex.owl.swrl.bridge.jess.SWRLJessBridge;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;




public class Action{
	
	private boolean actionFlag=false;
		  
	public boolean isActionFlag() {
		return actionFlag;
	}

	public void setActionFlag(boolean actionFlag) {
		this.actionFlag = actionFlag;
	}

	@SuppressWarnings("deprecation")
	public static  Document actionInfer(ArrayList<String> list, OWLModel model,String maName,Document doc) throws OntologyLoadException 
	{
		
		String   str1="p2:";
		/*
		 * 获取doc的根节点
		 */
		Element rootName = doc.getRootElement();
		Element name = rootName.element("maName");
		String adlTopic = name.attributeValue("topic");
		
		System.out.println("adlTopic="+adlTopic);
	    /*
	     * 获取ma实例
	     */
		OWLIndividual maIndividual=model.getOWLIndividual(maName);
		/*
		 * 用到的各种属性
		 */
		System.out.println("model="+model.getName().toString()+" *******  ");
		OWLDatatypeProperty topicNameProperty=model.getOWLDatatypeProperty("topicName");
		OWLDatatypeProperty addModelTypeProperty=model.getOWLDatatypeProperty("addModelType");
		OWLDatatypeProperty  isDeal=model.getOWLDatatypeProperty("isUsed");
		System.out.println("......."+addModelTypeProperty.toString()+"........");
		OWLDatatypeProperty modelIDProperty=model.getOWLDatatypeProperty("modelID");
		OWLObjectProperty  hasModelNameProperty=model.getOWLObjectProperty("hasModelName");
		int totalActionNum = 0;
		
		String[] actionList1 = new String[200]; // 用来存储动作
		int actFromTemNumber = 0;
		/*
		 * 获取topic
		 */
		ArrayList topicNameList = new ArrayList();
		String topicName="";
		if (adlTopic != "") {
			topicNameList.add(adlTopic);
		}
	/*	else {

			//System.out.println("topic名字"+topicNameProperty.toString());
			Collection hasTopicValues = maIndividual.getPropertyValues(topicNameProperty);
			if (hasTopicValues.isEmpty()) {
				System.out.println("topic为空");
			}
			else {
				// topicName=maIndividual.getPropertyValue(topicNameProperty).toString();

				for (Iterator it = hasTopicValues.iterator(); it.hasNext();) {
					topicNameList.add(it.next().toString());
				}
			}
		}*/
		/*
		 * 首先处理topic信息,并取得所有符合此topic的动作名称
		 */
		
		ArrayList<String> actionList = new ArrayList<String>();
		OWLNamedClass actionClass = model.getOWLNamedClass(str1 + "Action");
		for (Iterator itTopic = topicNameList.iterator(); itTopic.hasNext();) {
		    topicName = (String) itTopic.next();
			System.out.println("topicName:" + topicName);
			OWLNamedClass topic = model.getOWLNamedClass(topicName);

			Collection ActionSubClass = actionClass.getSubclasses(true);
			OWLObjectProperty actionSuitableForTopicProperty = model.getOWLObjectProperty(str1 + "actionSuitableForTopic");
			Collection subclassIndiviual = null;
			for (Iterator it = ActionSubClass.iterator(); it.hasNext();) {
				OWLNamedClass subclass = (OWLNamedClass) it.next();
				if (subclass.getSomeValuesFrom(actionSuitableForTopicProperty) == null) {
							continue;
				}
				String hasTopicClassType = (subclass.getSomeValuesFrom(actionSuitableForTopicProperty).getClass()).getName();
				// System.out.println("####"+hasTopicClassType);
				if (hasTopicClassType.equals("edu.stanford.smi.protegex.owl.model.impl.DefaultOWLUnionClass")) {
					OWLUnionClass hasTopicUnion = (OWLUnionClass) subclass.getSomeValuesFrom(actionSuitableForTopicProperty);
					// 获取并集运算中类
					// getNamedOperands is OWLNAryLogicalClass's method: get all
					// operands which are named classes in the union formula
					Collection hasTopic_collection = hasTopicUnion
							.getNamedOperands();
					for (Iterator jm = hasTopic_collection.iterator(); jm
							.hasNext();) {
						OWLNamedClass hasTopicClass = (OWLNamedClass) jm.next();
						// 判断两个类是否相同
						// equalsStructurally is RDFObject's method: Determines
						// whether or not the specified class is structurally
						// equal to this class.
						if (hasTopicClass.equalsStructurally(topic)) {
							subclassIndiviual = subclass.getInstances(true);
							for (Iterator it1 = subclassIndiviual.iterator(); it1.hasNext();) {
								OWLIndividual actionIndiviual = (OWLIndividual) it1.next();
								String actionName = actionIndiviual.getBrowserText();
								actionList.add(actionName);
							}
							// ClassName = ColorImpSub2.getBrowserText();
							// System.out.println("Topic "+topic.getBrowserText()+" suitable color implication "
							// + subclass);
							continue;
						}
					}
				} else {
					OWLNamedClass classname = (OWLNamedClass) subclass.getSomeValuesFrom(actionSuitableForTopicProperty);
					 System.out.println(classname.getBrowserText());
					if (classname == null)
						continue;
					if (classname.equalsStructurally(topic)) {
						subclassIndiviual = subclass.getInstances(true);
						for (Iterator it1 = subclassIndiviual.iterator(); it1.hasNext();) {
							OWLIndividual actionIndiviual = (OWLIndividual) it1.next();
							String actionName = actionIndiviual.getBrowserText();
							actionList.add(actionName);
						}
						continue;
					}
				}
			}
		}
	   //  if(actFromTemNumber==0&&actionList.size()!=0)
		if (actionList.size() != 0) {
			System.out.println("根据主题抽取到" + actionList.size() + "个动作");
			actionList1 = (String[]) actionList.toArray(new String[actionList.size()]);
			totalActionNum = actionList.size();
		} 
		else
		{
			/*
			 * 处理处理模板信息
			 */
			OWLObjectProperty mapToActionProperty = model.getOWLObjectProperty(str1 + "mapToAction");
			int listSize = list.size();
			System.out.print(listSize + "\n");
			String[] listToStr = new String[listSize]; // 将arraylist转化为string[]
			String[] templateList = new String[listSize]; // 处理后的template信息

			if (listSize != 0) {
				listToStr = (String[]) list.toArray(new String[listSize]);// 增加了(String[])
				for (int i = 0; i < listToStr.length; i++) {
					String str2 = listToStr[i];
					int pos = str2.indexOf(":");
					templateList[i] = (String) str2.subSequence(pos + 1,str2.length());
					System.out.println("templateList[" + i + "]="+ templateList[i]);
				}
			}
			if (templateList.length > 0) {
				for (int i = 0; i < templateList.length; i++) {
					OWLIndividual actTemIndividual = model.getOWLIndividual(templateList[i]);
					System.out.println("tempalteList[" + i + "]="
							+ templateList[i]);
					Collection mapToActionValues = actTemIndividual
							.getPropertyValues(mapToActionProperty);
					if (!mapToActionValues.isEmpty()) {
						for (Iterator it1 = mapToActionValues.iterator(); it1
								.hasNext();)

						{
							OWLIndividual actionIndiviual = (OWLIndividual) it1
									.next();
							actionList1[actFromTemNumber] = actionIndiviual
									.getBrowserText();
							System.out.println(actionList1[actFromTemNumber]);
							actFromTemNumber++;
						}
					}
				}
			}
			if (actFromTemNumber != 0) {
				totalActionNum = actFromTemNumber;
				System.out.println("没有主题，根据原子共抽取到" + actFromTemNumber + "个动作");
			}
		}
		
			
		//在不能挑出动作的情况下，随机加动作
				if(actionList.size()==0&&actFromTemNumber==0)
				{	
					System.out.println("主题与原子没有抽取到合适的动作,若有模型，会随机加动作");
					//没有抽出动作，随机加动作
					int  actionNum = 0;
				    //String[]actionTemp=new  String[100];
					ArrayList actionlist=new ArrayList();
					actionlist.add("WalkAction");
					actionlist.add("RunAction");
					actionlist.add("WaitAction");
					for(int i=0;i<actionlist.size();i++)
					{
						String s=(String) actionlist.get(i);
						OWLNamedClass RandomClass = model.getOWLNamedClass(str1 + s);
						Collection actionAllIndividuals = RandomClass.getInstances(true);
						for(Iterator it=actionAllIndividuals.iterator();it.hasNext();)
						{  
							OWLIndividual   actionIndiviual=(OWLIndividual)it.next();
							String actionName=actionIndiviual.getBrowserText();
							actionList1[actionNum] = actionName ;
							//System.out.println(actionTemp[actionNum]);
							actionNum++;
						}
						totalActionNum = actionNum;
					}
					
				}
				
		//为添加的人物添加动作
	
		OWLNamedClass AddModelRelatedClass =model.getOWLNamedClass("AddModelRelated");
		Collection    AllAddPeopleIndividuals = AddModelRelatedClass.getInstances(true);
		for(Iterator it=AllAddPeopleIndividuals.iterator();it.hasNext();)
		{  //20170506
			OWLIndividual  addModelIndiviual=(OWLIndividual)it.next();
			
			if(addModelIndiviual.getPropertyValue(addModelTypeProperty).toString().equals("people"))
			{	String isCheck=addModelIndiviual.getPropertyValue(isDeal).toString();
				if(isCheck.equals("false")){
				String  PeopleID=addModelIndiviual.getPropertyValue(modelIDProperty).toString();
				//System.out.println(PeopleID);
				String  PeopleNameInOwl=((OWLIndividual)addModelIndiviual.getPropertyValue(hasModelNameProperty)).getBrowserText();
				int pos = PeopleNameInOwl.indexOf(":");
				String PeopleName =(String)PeopleNameInOwl.subSequence(pos+1, PeopleNameInOwl.length());
				//System.out.println(PeopleName);
				int rd=(int)(Math.random()*totalActionNum);//用来产生随机数，判断加哪个动作
				
				int pos1 = actionList1[rd].indexOf(":");
				String finalActionName =(String)actionList1[rd].subSequence(pos1+1, actionList1[rd].length());
				System.out.println("添加动作名称；"+finalActionName);
				
				
				
				
				//2014.6.18修改--------------
				OWLIndividual finalAddactionindivadual=model.getOWLIndividual(actionList1[rd]);
				
				//TODO 2016.10.16修改
				String i=new Random().nextInt(2)+"";
				OWLDatatypeProperty relateActionProperty=model.getOWLDatatypeProperty(str1+"relateAction");
				String relateAction="";
				System.out.println("XXXXXXX数据"+finalAddactionindivadual.getPropertyValue(relateActionProperty));
				if(finalAddactionindivadual.getPropertyValue(relateActionProperty)!=null){
					relateAction=finalAddactionindivadual.getPropertyValue(relateActionProperty).toString() ;
				}
				
				
				OWLDatatypeProperty ifAddConstraintProperty=model.getOWLDatatypeProperty(str1+"ifAddConstraint");
				String  ifAddConstraint="0";
				System.out.println("数据"+finalAddactionindivadual.getPropertyValue(ifAddConstraintProperty));
				if(finalAddactionindivadual.getPropertyValue(ifAddConstraintProperty)!=null){
				ifAddConstraint=finalAddactionindivadual.getPropertyValue(ifAddConstraintProperty).toString() ;
				}
		
				if(ifAddConstraint.equals("1")){
					OWLDatatypeProperty constraintTypeProperty=model.getOWLDatatypeProperty(str1+"actionConstraintType");	
					String constraintType;  
					if (finalAddactionindivadual.getPropertyValue(constraintTypeProperty)!=null) {
						constraintType = finalAddactionindivadual.getPropertyValue(constraintTypeProperty).toString();
					} else {
						constraintType = "default";
					}
					
					OWLObjectProperty relativeModelProperty=model.getOWLObjectProperty(str1+"modelSuitableForAction");	
					OWLIndividual relativeModelIndividual=null; 
					String relativeModel;  
					if (finalAddactionindivadual.getPropertyValue(relativeModelProperty)!=null) {
						relativeModelIndividual = (OWLIndividual) finalAddactionindivadual.getPropertyValue(relativeModelProperty);
						relativeModel=relativeModelIndividual.getBrowserText();
					} else {
						relativeModel = "default";
					}					
					
					OWLDatatypeProperty modelRelativePositionProperty=model.getOWLDatatypeProperty(str1+"actionRelativeModelPosition");	
					String relativeModelPosition;  
					if (finalAddactionindivadual.getPropertyValue(modelRelativePositionProperty)!=null) {
						relativeModelPosition = finalAddactionindivadual.getPropertyValue(modelRelativePositionProperty).toString();
					} else {
						relativeModelPosition = "default";
					}
					
					ifAddConstraint="ture";
					
					Element ruleName = name.addElement("rule");
					  ruleName.addAttribute("ruleType","addActionToMa");// type="model"
			          ruleName.addAttribute("type","action");
			          ruleName.addAttribute("usedModelID",PeopleID);
					  ruleName.addAttribute("usedModelInMa",PeopleName);
					  ruleName.addAttribute("actionName", finalActionName);
					  ruleName.addAttribute("ifAddConstraint", ifAddConstraint);
					  ruleName.addAttribute("actionConstraintType", constraintType);
					  ruleName.addAttribute("relativeModelMa", relativeModel);
					  ruleName.addAttribute("relativeModelPosition", relativeModelPosition);
					  //TODO  2016.10.16修改
					  
					  if(!"".equals(relateAction)&&relateAction!=null&&i.equals("1")){
						  String[] split = relateAction.split("_");
						  System.out.println("split.length="+split.length);
						  int random=new Random().nextInt(split.length);
						  String randomRelateAction=split[random];
						  ruleName.addAttribute("relateAction", randomRelateAction);
					  }else{
						  
						  ruleName.addAttribute("relateAction", "null");
					  }
					  
					  
				}else{
					//
				//写doc
				  ifAddConstraint="false";
				  Element ruleName = name.addElement("rule");
				  ruleName.addAttribute("ruleType","addActionToMa");// type="model"
		          ruleName.addAttribute("type","action");
		          ruleName.addAttribute("usedModelID",PeopleID);
				  ruleName.addAttribute("usedModelInMa",PeopleName);
				  ruleName.addAttribute("actionName", finalActionName);
				  ruleName.addAttribute("ifAddConstraint", ifAddConstraint);
				  
				  
				//TODO  2016.10.16修改
				  if(!"".equals(relateAction)&&relateAction!=null&&i.equals("1")){
					  String[] split = relateAction.split("_");
					  System.out.println("split.length="+split.length);
					  int random=new Random().nextInt(split.length);
					  String randomRelateAction=split[random];
					  ruleName.addAttribute("relateAction", randomRelateAction);
				  }else{
					  
					  ruleName.addAttribute("relateAction", "null");
				  }
//				  if(finalActionName.equals("ElatedWalk142_06")){
//					  ruleName.addAttribute("PathConstraint", "round");
//				  }
				}
				//2014.6.18hll---------------------
				
			}
			}else{
					System.out.println("没有人");
				}
		 }		
	
         return  doc;	 
		 
		 }
	
	/**
	 * 判断最后是否加入动作
	 * @return void
	 * @param list 传递过来的模板信息 model 传递的Ontology maName 选择的场景名称
	 * */
	
	public void  actionInfer(ArrayList<String> list, OWLModel model,String adlTopic) throws OntologyLoadException {
		String   str1="p2:";
		
		System.out.println("adlTopic"+adlTopic);
	    /*
	     * 获取ma实例
	     */
		//OWLIndividual maIndividual=model.getOWLIndividual(maName);
		/*
		 * 用到的各种属性
		 */
		//System.out.println("model"+model.getName().toString()+" *******  ");
		OWLDatatypeProperty topicNameProperty=model.getOWLDatatypeProperty("topicName");
		OWLDatatypeProperty addModelTypeProperty=model.getOWLDatatypeProperty("addModelType");
		//System.out.println("......."+addModelTypeProperty.toString()+"........");
		OWLDatatypeProperty modelIDProperty=model.getOWLDatatypeProperty("modelID");
		OWLObjectProperty  hasModelNameProperty=model.getOWLObjectProperty("hasModelName");
		int totalActionNum = 0;
		
		String[] actionList1 = new String[200]; // 用来存储动作
		int actFromTemNumber = 0;
		/*
		 * 获取topic
		 */
		ArrayList topicNameList = new ArrayList();
		String topicName="";
		if(adlTopic!=""){
			topicNameList.add(adlTopic);
		}else{

			System.out.println("topic为空");

		}
		/*
		 * 首先处理topic信息,并取得所有符合此topic的动作名称
		 */
		ArrayList<String> actionList = new ArrayList<String>();
		OWLNamedClass actionClass = model.getOWLNamedClass(str1 + "Action");
		for (Iterator itTopic = topicNameList.iterator(); itTopic.hasNext();) {
		    topicName = (String) itTopic.next();
			System.out.println("topicName:" + topicName);
			OWLNamedClass topic = model.getOWLNamedClass(topicName);

			Collection ActionSubClass = actionClass.getSubclasses(true);
			OWLObjectProperty actionSuitableForTopicProperty = model.getOWLObjectProperty(str1 + "actionSuitableForTopic");
			Collection subclassIndiviual = null;
			for (Iterator it = ActionSubClass.iterator(); it.hasNext();) {
				OWLNamedClass subclass = (OWLNamedClass) it.next();
				if (subclass.getSomeValuesFrom(actionSuitableForTopicProperty) == null) {
							continue;
				}
				String hasTopicClassType = (subclass
						.getSomeValuesFrom(actionSuitableForTopicProperty)
						.getClass()).getName();
				// System.out.println("####"+hasTopicClassType);
				if (hasTopicClassType.equals("edu.stanford.smi.protegex.owl.model.impl.DefaultOWLUnionClass")) {
					OWLUnionClass hasTopicUnion = (OWLUnionClass) subclass.getSomeValuesFrom(actionSuitableForTopicProperty);
					// 获取并集运算中类
					// getNamedOperands is OWLNAryLogicalClass's method: get all
					// operands which are named classes in the union formula
					Collection hasTopic_collection = hasTopicUnion
							.getNamedOperands();
					for (Iterator jm = hasTopic_collection.iterator(); jm
							.hasNext();) {
						OWLNamedClass hasTopicClass = (OWLNamedClass) jm.next();
						// 判断两个类是否相同
						// equalsStructurally is RDFObject's method: Determines
						// whether or not the specified class is structurally
						// equal to this class.
						if (hasTopicClass.equalsStructurally(topic)) {
							subclassIndiviual = subclass.getInstances(true);
							for (Iterator it1 = subclassIndiviual.iterator(); it1.hasNext();) {
								OWLIndividual actionIndiviual = (OWLIndividual) it1.next();
								String actionName = actionIndiviual.getBrowserText();
								actionList.add(actionName);
							}
							// ClassName = ColorImpSub2.getBrowserText();
							// System.out.println("Topic "+topic.getBrowserText()+" suitable color implication "
							// + subclass);
							continue;
						}
					}
				} else {
					System.out.println(subclass.getBrowserText());
					OWLNamedClass classname = (OWLNamedClass) subclass.getSomeValuesFrom(actionSuitableForTopicProperty);
					System.out.println(classname.getBrowserText());
					// System.out.println(classname.getBrowserText());
					if (classname == null)
						continue;
					if (classname.equalsStructurally(topic)) {
						subclassIndiviual = subclass.getInstances(true);
						for (Iterator it1 = subclassIndiviual.iterator(); it1.hasNext();) {
							OWLIndividual actionIndiviual = (OWLIndividual) it1.next();
							String actionName = actionIndiviual.getBrowserText();
							actionList.add(actionName);
						}
						continue;
					}
				}
			}
		}
	   //  if(actFromTemNumber==0&&actionList.size()!=0)
		if (actionList.size() != 0) {
			System.out.println("根据主题抽取到" + actionList.size() + "个动作");
			actionList1 = (String[]) actionList.toArray(new String[actionList
					.size()]);
			totalActionNum = actionList.size();
			
		} else {
			/*
			 * 处理处理模板信息
			 */
			OWLObjectProperty mapToActionProperty = model.getOWLObjectProperty(str1 + "mapToAction");
			int listSize = list.size();
			System.out.print(listSize + "\n");
			String[] listToStr = new String[listSize]; // 将arraylist转化为string[]
			String[] templateList = new String[listSize]; // 处理后的template信息

			if (listSize != 0) {
				listToStr = (String[]) list.toArray(new String[listSize]);// 增加了(String[])
				for (int i = 0; i < listToStr.length; i++) {
					String str2 = listToStr[i];
					int pos = str2.indexOf(":");
					templateList[i] = (String) str2.subSequence(pos + 1,str2.length());
					
					System.out.println("templateList[" + i + "]="+ templateList[i]);
				}
			}
			if (templateList.length > 0) {
				for (int i = 0; i < templateList.length; i++) {
					OWLIndividual actTemIndividual = model.getOWLIndividual(templateList[i]);
					System.out.println("tempalteList[" + i + "]"
							+ templateList[i]);
					Collection mapToActionValues = actTemIndividual
							.getPropertyValues(mapToActionProperty);
					if (!mapToActionValues.isEmpty()) {
						for (Iterator it1 = mapToActionValues.iterator(); it1
								.hasNext();)

						{
							OWLIndividual actionIndiviual = (OWLIndividual) it1
									.next();
							actionList1[actFromTemNumber] = actionIndiviual
									.getBrowserText();
							System.out.println(actionList1[actFromTemNumber]);
							actFromTemNumber++;
						}
					}
				}
			}
			if (actFromTemNumber != 0) {
				totalActionNum = actFromTemNumber;
				System.out.println("没有主题，根据原子共抽取到" + actFromTemNumber + "个动作");				
			}
		}
		if(totalActionNum!=0)
		setActionFlag(true);	
		
	}
	
	public Document actionInfer1(ArrayList<String> list, OWLModel model,String maName,Document doc)
	{
		
		return doc;
	}
	
	public static void main(String[] args) throws OntologyLoadException {
		// TODO Auto-generated method stub
		
		 String xmlPath ="PlotDataOut/adl_result.xml";
		
		 Document doc = XMLInfoFromIEDom4j.readXMLFile(xmlPath);//获得要输出的XML文件的头部
//		try{
			System.out.println("qiu begin");
			 String urlq="file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";
			 OWLModel owlModel =ProtegeOWL.createJenaOWLModelFromURI(urlq);
			       Action action=new Action();
			       ArrayList<String> actionTemplate=new ArrayList<String>();
			       actionTemplate.add("WearTemplate:WearTemplate1");
			       
			       System.out.println("传递的实例"+actionTemplate);
			       doc=new Action().actionInfer(actionTemplate,owlModel, "danceFire.ma", doc);
			       XMLInfoFromIEDom4j.doc2XmlFile(doc, xmlPath);
					System.out.println("qiu finish");
//			}catch(Exception exQiu)
//			{
//				System.out.println("ERROR: Qiu Exception");
//			}				
	}


    }
	