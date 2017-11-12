package plot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.Element;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;

public class InterAction {
	
	
	public static void main(String[] args) throws OntologyLoadException {
		
		 String xmlPath ="PlotDataOut/adl_resultInteraction.xml";
		
		 Document doc = XMLInfoFromIEDom4j.readXMLFile(xmlPath);//获得要输出的XML文件的头部
			System.out.println("interaction begin");
			//测试
			 String urlq="file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";
			 OWLModel model =ProtegeOWL.createJenaOWLModelFromURI(urlq);
				//测试，整合到总库后需要修改    interModel:专用于InterAction交互动作库
				String url="file:///C:/InterAction/InterAction.owl";
				 OWLModel interModel =ProtegeOWL.createJenaOWLModelFromURI(url);
			       ArrayList<String> actionTemplate=new ArrayList<String>();
			       ArrayList<String> topic=new ArrayList<String>();
					topic.add("踢足球");
					topic.add("打篮球");
					topic.add("悲");
					topic.add("喜悦");
					ArrayList<String> topiclist=new ArrayList();
					OWLNamedClass topicn=model.getOWLNamedClass("Topic");
					OWLDatatypeProperty chineseTopic=model.getOWLDatatypeProperty("chineseName");
					OWLNamedClass cls=null;
					Collection clo=topicn.getSubclasses(true);
					for(int i=0;i<topic.size();i++)
					{
						
						for(Iterator in=clo.iterator();in.hasNext();)
						{
							cls=(OWLNamedClass) in.next();
							Object hasvali=cls.getHasValue(chineseTopic);
							if(hasvali!=null && topic.get(i).equals(hasvali.toString()))
							{
								topiclist.add(cls.getBrowserText().toString());
							}
						}
					}
					System.out.println("topiclist="+topiclist);
					
					
//			       doc=new InterAction().InterActionInfer(actionTemplate,model, interModel,"Tropical45.ma", doc);
			       String aaa=new InterAction().hasInterActionInfer(actionTemplate,model,"Tropical45.ma", doc);
			       XMLInfoFromIEDom4j.doc2XmlFile(doc, xmlPath);
					System.out.println("interaction finish");
	}
	/**
	 * 主程序：根据主题、场景ma或者模板来抽出动作
	 * @param list	模板集合
	 * @param model	ontology主库
	 * @param interModel	交互动作库interaction
	 * @param maName	场景名称
	 * @param doc	adl.xml
	 * @return
	 * @throws OntologyLoadException
	 */
	
	public  Document InterActionInfer(ArrayList<String> list, OWLModel model,String maName,
			Document doc) throws OntologyLoadException {
		String   str1="p2:";
		//临时
		//String   str1="p3:";

		/*
		 * 获取doc的根节点
		 */
		Element rootName = doc.getRootElement();
		//用于获取子节点
		Element name = rootName.element("maName");
		String adlTopic = name.attributeValue("topic");
		System.out.println(adlTopic);
		 /*
	     * 获取ma实例
	     */
		OWLIndividual maIndividual=model.getOWLIndividual(maName);
		
		/*
		 * 用到的各种属性
		 */
		System.out.println("model"+model.getName().toString()+" *******  ");
		OWLDatatypeProperty topicNameProperty=model.getOWLDatatypeProperty("topicName");
		OWLDatatypeProperty addModelTypeProperty=model.getOWLDatatypeProperty("addModelType");
		System.out.println("......."+addModelTypeProperty.toString()+"........");
		OWLDatatypeProperty modelIDProperty=model.getOWLDatatypeProperty("modelID");
		OWLObjectProperty  hasModelNameProperty=model.getOWLObjectProperty("hasModelName");
		OWLDatatypeProperty  isDeal=model.getOWLDatatypeProperty("isUsed");
		OWLDatatypeProperty  maFrameNumber=model.getOWLDatatypeProperty("maFrameNumber");
		OWLDatatypeProperty  relateActionProperty=model.getOWLDatatypeProperty(str1+"relateAction");
		OWLObjectProperty addModelRelatedSpaceProperty = model.getOWLObjectProperty("addModelRelatedSpace");
		int totalActionNum = 0;
		
		String[] actionList1 = new String[200]; // 用来存储动作
		int actFromTemNumber = 0;
		/*
		 * 获取topic
		 */
		ArrayList<String> topicNameList = new ArrayList<String>();
		String topicName="";
		if (adlTopic != "") {
			topicNameList.add(adlTopic);
		}
		else {

			Collection hasTopicValues = maIndividual.getPropertyValues(topicNameProperty);
			if (hasTopicValues.isEmpty()) {
				System.out.println("topic为空");
			}
			else {
				for (Iterator it = hasTopicValues.iterator(); it.hasNext();) {
					topicNameList.add(it.next().toString());
				}
			}
		}
		
		
		
		/*
		 * 首先处理topic信息,并取得所有符合此topic的动作名称
		 */
		String   str11="p12:";
		ArrayList<String> actionList = new ArrayList<String>();
		//*
		OWLNamedClass actionClass = model.getOWLNamedClass(str11+"ExcuteAction");
		for (Iterator itTopic = topicNameList.iterator(); itTopic.hasNext();) {
		    topicName = (String) itTopic.next();
			System.out.println("topicName:" + topicName);
			OWLNamedClass topic = model.getOWLNamedClass(topicName);
			
			Collection ActionSubClass = actionClass.getSubclasses(true);//.getSubclasses(true)
			
			OWLObjectProperty actionSuitableForTopicProperty = model.getOWLObjectProperty(str1 + "actionSuitableForTopic");
			Collection subclassIndiviual = null;
			for(Iterator it = ActionSubClass.iterator(); it.hasNext();) {
				OWLNamedClass subclass = (OWLNamedClass) it.next();
				if (subclass.getSomeValuesFrom(actionSuitableForTopicProperty) == null) {
							continue;
				}
				String hasTopicClassType = (subclass.getSomeValuesFrom(actionSuitableForTopicProperty).getClass()).getName();
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
		if (actionList.size() != 0) {
			System.out.println("根据主题抽取到" + actionList.size() + "个动作");
			actionList1 = (String[]) actionList.toArray(new String[actionList.size()]);
			totalActionNum = actionList.size();
		} 
		//若主题没有抽取到动作，则处理模板信息抽取动作
		
		else
		{
			/*
			 * 处理处理模板信息
			 */
			OWLObjectProperty mapToActionProperty = model.getOWLObjectProperty(str11 + "mapToInterAction");
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
		//在不能挑出交互动作的情况下，跳出交互动作程序、转向执行单人程序
		if(actionList.size()==0&&actFromTemNumber==0){
			//TODO
			System.out.println("主题与原子没有抽取到合适的动作,若有模型,执行单人动作");
			return null;
		}
		
		
		
		
		//为添加的人物添加动作
		//添加的人物会写入到知识库中的AddModelRelated下
		//测试、在类AddModelRelated下创建实例addModelID1并设置其属性值isUsed=true
			 
		OWLNamedClass AddModelRelatedClass =model.getOWLNamedClass("AddModelRelated");
			 /*
		OWLIndividual addIndividual = AddModelRelatedClass.createOWLIndividual("addModelID1");
		addIndividual.setPropertyValue(addModelTypeProperty, "people");
		addIndividual.setPropertyValue(modelIDProperty, "1");
		addIndividual.setPropertyValue(isDeal, "false");
		addIndividual.setPropertyValue(addModelRelatedSpaceProperty, "sp_1");
		
		OWLIndividual addIndividual1 = AddModelRelatedClass.createOWLIndividual("addModelID2");
		addIndividual1.setPropertyValue(addModelTypeProperty, "people");
		addIndividual1.setPropertyValue(modelIDProperty, "2");
		addIndividual1.setPropertyValue(isDeal, "false");
		addIndividual1.setPropertyValue(addModelRelatedSpaceProperty, "sp_1");
//		addIndividual.setPropertyValue(hasModelNameProperty, "boy");
		 */
		
		
	
		
		
		Collection  oldAllAddPeopleIndividuals = AddModelRelatedClass.getInstances(true);
		
		//从所有模型中挑选出具有相同可用空间的人物实例模型,若存在，将可用空间名字以及人物实例进行存储，然后将存储的人物实例用来遍历
		String tempSpaceName="";
		List<String> listSpace=new ArrayList<String>();
		List<String> newListSpace=new ArrayList<String>();
		List<OWLIndividual> individualSpaceList=new ArrayList<OWLIndividual>();
		
		List<List<OWLIndividual>> newIndividualSpaceList=new ArrayList<List<OWLIndividual>>();
		//将没有处理的人物以及人物所在的可用空间抽出来
		for(Iterator it=oldAllAddPeopleIndividuals.iterator();it.hasNext();){
			
			OWLIndividual  addModelIndiviual=(OWLIndividual)it.next();
			if(addModelIndiviual.getPropertyValue(addModelTypeProperty).toString().equals("people")&&addModelIndiviual.getPropertyValue(isDeal).equals("false")){
				OWLIndividual IndiviualOfSpace=(OWLIndividual)addModelIndiviual.getPropertyValue(addModelRelatedSpaceProperty);
//				String tempSpaceName= IndiviualOfSpace.getBrowserText();
				tempSpaceName=addModelIndiviual.getPropertyValue(addModelRelatedSpaceProperty).toString();
				listSpace.add(tempSpaceName);
				individualSpaceList.add(addModelIndiviual);
			}
			
		}
		if(individualSpaceList.size()<2){
			return null;
		}

		for(int i=0;i<listSpace.size();i++){
			for(int j=i+1;j<listSpace.size();j++){
				if(listSpace.get(i).equals(listSpace.get(j))){
					List<OWLIndividual> newIndividualSpaceListTemp=new ArrayList<OWLIndividual>();
//					if(newListSpace.size()==0){
						newListSpace.add(listSpace.get(i));
						newIndividualSpaceListTemp.add(individualSpaceList.get(i));
//					}
					newListSpace.add(listSpace.get(j));
					newIndividualSpaceListTemp.add(individualSpaceList.get(j));
					newIndividualSpaceList.add(newIndividualSpaceListTemp);
				}
				//20170530
				if(newListSpace.size()>=2){
					break;
				}
			}
			//至抽出一组交互动作
		
			if(newIndividualSpaceList.size()>=1){
				break;
			}
		
		}
		
		/*
		if(newListSpace.size()<2){
			//TODO
			return null;
		}
		*/
		Collection<OWLIndividual> AllAddPeopleIndividuals=new ArrayList<OWLIndividual>();
		for(int d=0;d<newIndividualSpaceList.size();d++){
			List<OWLIndividual> temp=newIndividualSpaceList.get(d);
			for(int f=0;f<temp.size();f++){
				
				AllAddPeopleIndividuals.add(temp.get(f));
			}
			int i=0;
			String otherInterAction="";
			List<String> preContainsList=new ArrayList<String>();
			int flag=0;
			String frame="";
			for(Iterator it=AllAddPeopleIndividuals.iterator();it.hasNext();)
			{  	OWLIndividual  addModelIndiviual=(OWLIndividual)it.next();
				if(i==2){
					break;
					
				}else if(i==0){
					
					if(addModelIndiviual.getPropertyValue(addModelTypeProperty).toString().equals("people")&&addModelIndiviual.getPropertyValue(isDeal).equals("false"))
					{
						String  PeopleID=addModelIndiviual.getPropertyValue(modelIDProperty).toString();
						//System.out.println(PeopleID);
						String  PeopleNameInOwl=((OWLIndividual)addModelIndiviual.getPropertyValue(hasModelNameProperty)).getBrowserText();
						int pos = PeopleNameInOwl.indexOf(":");
						String PeopleName =(String)PeopleNameInOwl.subSequence(pos+1, PeopleNameInOwl.length());
						//System.out.println(PeopleName);
						int rd=(int)(Math.random()*totalActionNum);//用来产生随机数，判断加哪个动作
						
						int pos1 = actionList1[rd].indexOf(":");
						String finalActionName =(String)actionList1[rd].subSequence(pos1+1, actionList1[rd].length());
//						actionList1[rd]="p12:passSmallSomthing3";
						System.out.println("添加动作名称；"+actionList1[rd]);
						
						//2014.6.18修改--------------
						OWLIndividual finalAddactionindivadual=model.getOWLIndividual(actionList1[rd]);
						//得到交互动作帧数并进行拼接
						frame=finalAddactionindivadual.getPropertyValue(maFrameNumber).toString() ;
						//获得属性值PreAction、InterAction、ComplishAction、InterActionObject
						//PreAction
						//OWLObjectProperty preActionProperty = interModel.getOWLObjectProperty(str11+"PreAction");getDataPropertyActionName
						//String preAction = getPreOrComplishPropertyActionName(finalAddactionindivadual,preActionProperty,i);
						OWLDatatypeProperty preActionProperty=model.getOWLDatatypeProperty(str11+"PreAction");
						String preAction = getDataPropertyActionName(finalAddactionindivadual,preActionProperty);
						//InterAction
						OWLObjectProperty interActionProperty = model.getOWLObjectProperty(str11+"InterAction");
						String interAction = getInterPropertyActionName(finalAddactionindivadual,interActionProperty);
//						OWLDatatypeProperty interActionProperty=model.getOWLDatatypeProperty(str11+"InterAction");
//						String interAction = getDataPropertyActionName(finalAddactionindivadual,interActionProperty);
						//交互动作传递
						otherInterAction=interAction;
						//ComplishAction
						//OWLObjectProperty complishActionProperty = interModel.getOWLObjectProperty(str11+"ComplishAction");
						//String complishAction = getPreOrComplishPropertyActionName(finalAddactionindivadual,complishActionProperty,i);
						OWLDatatypeProperty complishActionProperty=model.getOWLDatatypeProperty(str11+"ComplishAction");
						String complishAction = getDataPropertyActionName(finalAddactionindivadual,complishActionProperty);
						
						//InterActionObject
						OWLDatatypeProperty interActionObjectProperty=model.getOWLDatatypeProperty(str11+"InteractionObject");
						String interActionObject = getDataPropertyActionName(finalAddactionindivadual,interActionObjectProperty);
						
						OWLDatatypeProperty contactRangeProperty=model.getOWLDatatypeProperty(str11+"contactRange");
						String contactRange = getDataPropertyActionName(finalAddactionindivadual,contactRangeProperty);
						String relateAction =getDataPropertyActionName(finalAddactionindivadual,relateActionProperty);
						OWLIndividual IndiviualOfSpace=(OWLIndividual)addModelIndiviual.getPropertyValue(addModelRelatedSpaceProperty);
						String spaceName= IndiviualOfSpace.getBrowserText();
//						String spaceName = getInterPropertyActionName(finalAddactionindivadual,addModelRelatedSpaceProperty);
						//获得与该动作想交互的动作的属性以及其他值
						//写入doc
						Element ruleName = name.addElement("rule");
						 ruleName.addAttribute("ruleType","addActionToMa");
						 ruleName.addAttribute("type","interaction");
				          ruleName.addAttribute("usedModelID",PeopleID);
				          ruleName.addAttribute("usedModelInMa",PeopleName);
				          if(!preAction.equals("")){
				        	  ruleName.addAttribute("preparatoryStage",preAction);
				          }else{
				        	  ruleName.addAttribute("preparatoryStage","null");
				          }
				          if(!relateAction.equals("")){
				        	  ruleName.addAttribute("interactiveStage",finalActionName+"_"+frame+"+"+relateAction);
				          }else{
				        	  ruleName.addAttribute("interactiveStage",finalActionName+"_"+frame);
				          }
				          
				          if(!complishAction.equals("")){
				        	  ruleName.addAttribute("accomplishmentStage",complishAction);
				          }else{
				        	  ruleName.addAttribute("accomplishmentStage","null");
				          }
				          ruleName.addAttribute("contactRange",contactRange);
						if(!interActionObject.equals("")){
							ruleName.addAttribute("interactionObject",interActionObject);
						}else{
							ruleName.addAttribute("interactionObject","null");
						}
//						ruleName.addAttribute("spaceName",spaceName);
						ruleName.addAttribute("spaceClass",spaceName+"1");
						//标志位，代表该人物已经经过交互动作所处理
						addModelIndiviual.setPropertyValue(isDeal, "true");
						i++;
						}else{
							System.out.println("没有人");
						}
					
				}else{
					
					if(addModelIndiviual.getPropertyValue(addModelTypeProperty).toString().equals("people")&&addModelIndiviual.getPropertyValue(isDeal).equals("false"))
					{
						String  PeopleID=addModelIndiviual.getPropertyValue(modelIDProperty).toString();
						//System.out.println(PeopleID);
						String  PeopleNameInOwl=((OWLIndividual)addModelIndiviual.getPropertyValue(hasModelNameProperty)).getBrowserText();
						int pos = PeopleNameInOwl.indexOf(":");
						String PeopleName =(String)PeopleNameInOwl.subSequence(pos+1, PeopleNameInOwl.length());
//						pos=otherInterAction.indexOf(":");
//						otherInterAction=(String)otherInterAction.subSequence(pos+1, otherInterAction.length());
						//System.out.println(PeopleName);
						System.out.println("添加动作名称；"+otherInterAction);
						
						//2014.6.18修改--------------
						OWLIndividual finalAddactionindivadual=model.getOWLIndividual(otherInterAction);
						frame=finalAddactionindivadual.getPropertyValue(maFrameNumber).toString() ;
						//获得属性值PreAction、InterAction、ComplishAction、InterActionObject
						//PreAction
//						OWLObjectProperty preActionProperty = interModel.getOWLObjectProperty("PreAction");
//						String preAction = getPreOrComplishPropertyActionName(finalAddactionindivadual,preActionProperty,i);
						OWLDatatypeProperty preActionProperty=model.getOWLDatatypeProperty(str11+"PreAction");
						String preAction = getDataPropertyActionName(finalAddactionindivadual,preActionProperty);
						//InterAction
//						OWLObjectProperty interActionProperty = interModel.getOWLObjectProperty("InterAction");
//						String interAction = getInterPropertyActionName(finalAddactionindivadual,interActionProperty);
					//	OWLDatatypeProperty interActionProperty=model.getOWLDatatypeProperty(str11+"InterAction");
						//String interAction = getDataPropertyActionName(finalAddactionindivadual,interActionProperty);
						//交互动作传递
						//otherInterAction=interAction;
						int pos1 = otherInterAction.indexOf(":");
						String finalActionName =(String)otherInterAction.subSequence(pos1+1, otherInterAction.length());
						//ComplishAction
//						OWLObjectProperty complishActionProperty = interModel.getOWLObjectProperty("ComplishAction");
//						String complishAction = getPreOrComplishPropertyActionName(finalAddactionindivadual,complishActionProperty,i);
						OWLDatatypeProperty complishActionProperty=model.getOWLDatatypeProperty(str11+"ComplishAction");
						String complishAction = getDataPropertyActionName(finalAddactionindivadual,complishActionProperty);
						
						//InterActionObject
						OWLDatatypeProperty interActionObjectProperty=model.getOWLDatatypeProperty(str11+"InteractionObject");
						String interActionObject = getDataPropertyActionName(finalAddactionindivadual,interActionObjectProperty);
						
						OWLDatatypeProperty contactRangeProperty=model.getOWLDatatypeProperty(str11+"contactRange");
						String contactRange = getDataPropertyActionName(finalAddactionindivadual,contactRangeProperty);
						
						String relateAction =getDataPropertyActionName(finalAddactionindivadual,relateActionProperty);
						
						//spaceName
						OWLIndividual IndiviualOfSpace=(OWLIndividual)addModelIndiviual.getPropertyValue(addModelRelatedSpaceProperty);
						String spaceName= IndiviualOfSpace.getBrowserText();
//						String spaceName = getInterPropertyActionName(finalAddactionindivadual,addModelRelatedSpaceProperty);
					//获得与该动作想交互的动作的属性以及其他值
						//写入doc
						Element ruleName = name.addElement("rule");
						 ruleName.addAttribute("ruleType","addActionToMa");
						 ruleName.addAttribute("type","interaction");
				          ruleName.addAttribute("usedModelID",PeopleID);
				          ruleName.addAttribute("usedModelInMa",PeopleName);
				          if(!preAction.equals("")){
				        	  ruleName.addAttribute("preparatoryStage",preAction);
				          }else{
				        	  ruleName.addAttribute("preparatoryStage","null");
				          }
				          if(!relateAction.equals("")){
				        	  ruleName.addAttribute("interactiveStage",finalActionName+"_"+frame+"+"+relateAction);
				          }else{
				        	  ruleName.addAttribute("interactiveStage",finalActionName+"_"+frame);
				          }
				         
				          
				          if(!complishAction.equals("")){
				        	  ruleName.addAttribute("accomplishmentStage",complishAction);
				          }else{
				        	  ruleName.addAttribute("accomplishmentStage","null");
				          }
				          ruleName.addAttribute("contactRange",contactRange);
						if(!interActionObject.equals("")){
							ruleName.addAttribute("interactionObject",interActionObject);
						}else{
							ruleName.addAttribute("interactionObject","null");
						}
						
//						ruleName.addAttribute("spaceName",spaceName);
						ruleName.addAttribute("spaceClass",spaceName+"1");
						//标志位，代表该人物已经经过交互动作所处理
						addModelIndiviual.setPropertyValue(isDeal, "true");
						i++;
						}else{
							System.out.println("没有人");
						}
					
					
					
				}
				
				
			 }
			
			AllAddPeopleIndividuals.clear();
		}
		
			
			
		
		
		
		return doc;
	}
	
	
	
	
	/**
	 * 主程序：根据主题、场景ma或者模板来抽出动作
	 * @param list	模板集合
	 * @param model	ontology主库
	 * @param interModel	交互动作库interaction
	 * @param maName	场景名称
	 * @param doc	adl.xml
	 * @return
	 * @throws OntologyLoadException
	 */
	
	public String hasInterActionInfer(ArrayList<String> list, OWLModel model, String maName,
			Document doc) throws OntologyLoadException {
		String   str1="p2:";
		//临时
		//String   str1="p3:";

		/*
		 * 获取doc的根节点
		 */
		Element rootName = doc.getRootElement();
		//用于获取子节点
		Element name = rootName.element("maName");
		String adlTopic = name.attributeValue("topic");
		System.out.println(adlTopic);
		 /*
	     * 获取ma实例
	     */
		OWLIndividual maIndividual=model.getOWLIndividual(maName);
		
		/*
		 * 用到的各种属性
		 */
		System.out.println("model"+model.getName().toString()+" *******  ");
		OWLDatatypeProperty topicNameProperty=model.getOWLDatatypeProperty("topicName");
		OWLDatatypeProperty addModelTypeProperty=model.getOWLDatatypeProperty("addModelType");
		System.out.println("......."+addModelTypeProperty.toString()+"........");
		OWLDatatypeProperty modelIDProperty=model.getOWLDatatypeProperty("modelID");
		OWLObjectProperty  hasModelNameProperty=model.getOWLObjectProperty("hasModelName");
		OWLDatatypeProperty  isDeal=model.getOWLDatatypeProperty("isUsed");
		OWLDatatypeProperty  maFrameNumber=model.getOWLDatatypeProperty("maFrameNumber");
		OWLDatatypeProperty  relateActionProperty=model.getOWLDatatypeProperty(str1+"relateAction");
		OWLObjectProperty addModelRelatedSpaceProperty = model.getOWLObjectProperty("addModelRelatedSpace");
		int totalActionNum = 0;
		
		String[] actionList1 = new String[200]; // 用来存储动作
		int actFromTemNumber = 0;
		/*
		 * 获取topic
		 */
		ArrayList<String> topicNameList = new ArrayList<String>();
		String topicName="";
		if (adlTopic != "") {
			topicNameList.add(adlTopic);
		}
		else {

			Collection hasTopicValues = maIndividual.getPropertyValues(topicNameProperty);
			if (hasTopicValues.isEmpty()) {
				System.out.println("topic为空");
			}
			else {
				for (Iterator it = hasTopicValues.iterator(); it.hasNext();) {
					topicNameList.add(it.next().toString());
				}
			}
		}
		
		
		
		/*
		 * 首先处理topic信息,并取得所有符合此topic的动作名称
		 */
		String   str11="p12:";
		ArrayList<String> actionList = new ArrayList<String>();
		//*
		OWLNamedClass actionClass = model.getOWLNamedClass(str11+"ExcuteAction");
		for (Iterator itTopic = topicNameList.iterator(); itTopic.hasNext();) {
		    topicName = (String) itTopic.next();
			System.out.println("topicName:" + topicName);
			OWLNamedClass topic = model.getOWLNamedClass(topicName);
			
			Collection ActionSubClass = actionClass.getSubclasses(true);//.getSubclasses(true)
			
			OWLObjectProperty actionSuitableForTopicProperty = model.getOWLObjectProperty(str1 + "actionSuitableForTopic");
			Collection subclassIndiviual = null;
			for(Iterator it = ActionSubClass.iterator(); it.hasNext();) {
				OWLNamedClass subclass = (OWLNamedClass) it.next();
				if (subclass.getSomeValuesFrom(actionSuitableForTopicProperty) == null) {
							continue;
				}
				String hasTopicClassType = (subclass.getSomeValuesFrom(actionSuitableForTopicProperty).getClass()).getName();
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
		if (actionList.size() != 0) {
			System.out.println("根据主题抽取到" + actionList.size() + "个动作");
			actionList1 = (String[]) actionList.toArray(new String[actionList.size()]);
			totalActionNum = actionList.size();
		} 
		//若主题没有抽取到动作，则处理模板信息抽取动作
		else
		{
			/*
			 * 处理处理模板信息
			 */
			OWLObjectProperty mapToActionProperty = model.getOWLObjectProperty(str11 + "mapToInterAction");
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
		//在不能挑出交互动作的情况下，跳出交互动作程序、转向执行单人程序
		if(actionList.size()==0&&actFromTemNumber==0){
			//TODO
			System.out.println("主题与原子没有抽取到合适的动作,若有模型,执行单人动作");
			return "onePeopleAction";
		}
		
		
		
		
		//为添加的人物添加动作
		//添加的人物会写入到知识库中的AddModelRelated下
		//测试、在类AddModelRelated下创建实例addModelID1并设置其属性值isUsed=true
		/*	 */
		OWLNamedClass AddModelRelatedClass =model.getOWLNamedClass("AddModelRelated");
		/*
		OWLIndividual addIndividual = AddModelRelatedClass.createOWLIndividual("addModelID1");
		addIndividual.setPropertyValue(addModelTypeProperty, "people");
		addIndividual.setPropertyValue(modelIDProperty, "1");
		addIndividual.setPropertyValue(isDeal, "false");
		addIndividual.setPropertyValue(addModelRelatedSpaceProperty, "sp_1");
		
		OWLIndividual addIndividual1 = AddModelRelatedClass.createOWLIndividual("addModelID2");
		addIndividual1.setPropertyValue(addModelTypeProperty, "people");
		addIndividual1.setPropertyValue(modelIDProperty, "2");
		addIndividual1.setPropertyValue(isDeal, "false");
		addIndividual1.setPropertyValue(addModelRelatedSpaceProperty, "sp_1");
//		addIndividual.setPropertyValue(hasModelNameProperty, "boy");
		 
		
		*/
	
		
		
		Collection  oldAllAddPeopleIndividuals = AddModelRelatedClass.getInstances(true);
		
		//从所有模型中挑选出具有相同可用空间的人物实例模型
		String tempSpaceName="";
		List<String> listSpace=new ArrayList<String>();
		List<String> newListSpace=new ArrayList<String>();
		List<OWLIndividual> individualSpaceList=new ArrayList<OWLIndividual>();
		List<OWLIndividual> newIndividualSpaceList=new ArrayList<OWLIndividual>();
		
		for(Iterator it=oldAllAddPeopleIndividuals.iterator();it.hasNext();){
			
			OWLIndividual  addModelIndiviual=(OWLIndividual)it.next();
			if(addModelIndiviual.getPropertyValue(addModelTypeProperty).toString().equals("people")&&addModelIndiviual.getPropertyValue(isDeal).equals("false")){
				tempSpaceName=addModelIndiviual.getPropertyValue(addModelRelatedSpaceProperty).toString();
//				OWLIndividual IndiviualOfSpace=(OWLIndividual)addModelIndiviual.getPropertyValue(addModelRelatedSpaceProperty);
//				tempSpaceName= IndiviualOfSpace.getBrowserText();
				listSpace.add(tempSpaceName);
				individualSpaceList.add(addModelIndiviual);
			}
			
		}
		if(individualSpaceList.size()<2){
			return "peopleNotEnough";
		}

		for(int i=0;i<listSpace.size();i++){
			for(int j=i+1;j<listSpace.size();j++){
				if(listSpace.get(i).equals(listSpace.get(j))){
					if(newListSpace.size()==0){
						newListSpace.add(listSpace.get(i));
						newIndividualSpaceList.add(individualSpaceList.get(i));
					}
					newListSpace.add(listSpace.get(j));
					newIndividualSpaceList.add(individualSpaceList.get(j));
				}
			}
			if(newListSpace.size()>=2){
				break;
			}
		}
		
		if(newListSpace.size()<2){
			//TODO
			return "peopleNotInOneSpace";
		}
		return "interActionIsOk";
		
	}
	
	
	
	
	
	/**
	 * Collection转List
	 * @param objectValues 
	 * @return
	 */
	public List<String> collectionToList(Collection objectValues){
		List<String> objList=new ArrayList<String>();
	
			for (Iterator obj = objectValues.iterator(); obj
					.hasNext();)

			{
				String actionIndiviual = (String) obj
						.next();
				objList.add(actionIndiviual);
			}
			/*
			int random=new Random().nextInt(2); 
			if(random==1){
				Collections.reverse(objList);
			}
			*/
			return objList;
		
		
	}
	
	/**
	 * Collection转List
	 * @param objectValues 
	 * @return
	 */
	public List<String> collectionToListObjectValue(Collection objectValues){
		List<String> objList=new ArrayList<String>();
		for (Iterator obj = objectValues.iterator(); obj
				.hasNext();)

		{
			OWLIndividual actionIndiviual = (OWLIndividual) obj
					.next();
			objList.add(actionIndiviual.getBrowserText());
		}
		return objList;
	}
	
	/**
	 * 得到ObjectProperty属性的值（String）
	 * @param finalAddactionindivadual 交互动作实例
	 * @param actionProperty	交互动作Object属性
	 * @return
	 */
	public String getPreOrComplishPropertyActionName(OWLIndividual finalAddactionindivadual,OWLObjectProperty actionProperty,int i){
		String action="";
		if(i==0){
			action=getWalkActionName(finalAddactionindivadual,actionProperty);
		}else{
			action=getNoWalkActionName(finalAddactionindivadual,actionProperty);
		}
		return action;
	}
	
	/**
	 * 得到交互动作
	 * @param finalAddactionindivadual
	 * @param actionProperty
	 * @return
	 */
	public String getInterPropertyActionName(OWLIndividual finalAddactionindivadual,OWLObjectProperty actionProperty){
		Collection actionValues=finalAddactionindivadual.getPropertyValues(actionProperty);
		String action="";
		if(!actionValues.isEmpty()){
				List<String> actionList = collectionToListObjectValue(actionValues);
				int random=(int)(Math.random()*actionList.size());
				System.out.println("action数据"+actionList.get(random));
				if(actionList.get(random)!=null&&!actionList.get(random).equals("")){
					action=actionList.get(random);
				}
		}

		return action;
	}
	
	/**
	 * 得到具有位移类的动作（走、跑）
	 * @param finalAddactionindivadual
	 * @param actionProperty
	 * @return
	 */
	public String getWalkActionName(OWLIndividual finalAddactionindivadual,OWLObjectProperty actionProperty){
		
		Collection actionValues=finalAddactionindivadual.getPropertyValues(actionProperty);
		String action="";
		//判断集合是否为空并且是否有两个或两个以上的值，若有再判断所包含的值是否是有位移的动作，若有
		if(!actionValues.isEmpty()){
			if(actionValues.size()>1){
				List<String> actionList = collectionToList(actionValues);
				for (String list : actionList) {
					if(list.contains("run")||list.contains("walk")||list.contains("Run")||list.contains("Walk")){
						return list;
					}
				}
				int random=(int)(Math.random()*actionList.size());
				System.out.println("action数据"+actionList.get(random));
				if(actionList.get(random)!=null&&!actionList.get(random).equals("")){
					action=actionList.get(random);
				}
			}
		}

		return action;
		
	}
	
	/**
	 * 得到第二个非位移类动作
	 * @param finalAddactionindivadual
	 * @param actionProperty
	 * @return
	 */
	public String getNoWalkActionName(OWLIndividual finalAddactionindivadual,OWLObjectProperty actionProperty){
		
		Collection actionValues=finalAddactionindivadual.getPropertyValues(actionProperty);
		String action="";
		List<String> filterActionList=new ArrayList<String>();
		//判断集合是否为空并且是否有两个或两个以上的值，若有再判断所包含的值是否是有位移的动作，若有
		if(!actionValues.isEmpty()){
			if(actionValues.size()>1){
				List<String> actionList = collectionToList(actionValues);
				for (String list : actionList) {
					if(list.contains("run")||list.contains("walk")||list.contains("Run")||list.contains("Walk")){
						continue;
					}else{
						filterActionList.add(list);
					}
				}
				int random=(int)(Math.random()*filterActionList.size());
				System.out.println("action数据"+filterActionList.get(random));
				if(filterActionList.get(random)!=null&&!filterActionList.get(random).equals("")){
					action=filterActionList.get(random);
				}
			}
		}

		return action;
	}
	/**
	 * 得到DataProperty属性的值（String）
	 * @param finalAddactionindivadual	交互动作实例
	 * @param actionProperty	交互动作Data属性
	 * @return
	 */
	public String getDataPropertyActionName(OWLIndividual finalAddactionindivadual,OWLDatatypeProperty actionProperty){
		Collection actionValues=finalAddactionindivadual.getPropertyValues(actionProperty);
		String action="";
		if(!actionValues.isEmpty()){
		List<String> actionList = collectionToList(actionValues);
		int random=(int)(Math.random()*actionList.size());
		System.out.println("action数据"+actionList.get(random));
		if(actionList.get(random)!=null&&!actionList.get(random).equals("")){
			action=actionList.get(random);
		}
		}

		return action;
	}
	
}
