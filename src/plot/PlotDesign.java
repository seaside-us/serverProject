package plot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.Element;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.*;

public class PlotDesign {

	ArrayList<ArrayList> TopicPlotList = new ArrayList();

	ArrayList<String> IETemplate = new ArrayList();

	public PlotDesign() {
	}

	/**
	 * ������ȡ�����ģ���¶�Ӧ��ʵ�����������������������ﻷ���ȵȣ�Ȼ������ȡ��������IE��ȡ����Ӣ��ģ������Ӧ��ģ��ʵ��
	 * ������ģ���¶����ʵ������IEģ����Ϊ׼ 0���Action 1��Ÿ���model 2����music
	 */

	@SuppressWarnings("unchecked")
	public PlotDesign(OWLModel model, String Topic, ArrayList<String> englishModel // englishmodel
																					// Ϊȡ�õ�ģ��ʵ��
	) {

		TopicPlotList = getPlotAtom(model, Topic);
		ArrayList<String> topic = TopicPlotList.get(4);
		String topicNeed = topic.get(0);

		// �õ�IE����������ģ��
		// printPlotRule(model,TopicPlotList,topicNeed+".ma",Topic);

	}

	public void printPlotRule(OWLModel model, ArrayList<ArrayList> IEPlotList, String maName, String topicName) {
		String xmlPath = XMLInfoFromIEDom4j.writeXML("adl_result.xml");
		Document doc = XMLInfoFromIEDom4j.readXMLFile(xmlPath);// ���Ҫ�����XML�ļ���ͷ��
		Element rootElement = doc.getRootElement();
		Element name = rootElement.addElement("maName");
		// String
		// maName=(String)copyIndividual.getPropertyValue(maSenceNameProperty);
		// String
		// topicClassName=(String)copyIndividual.getPropertyValue(topicNameProperty);
		name.addAttribute("name", maName);
		name.addAttribute("topic", topicName);
		Random r = new Random();
		int rand = r.nextInt(2);
		String sb = IEPlotList.get(3).get(rand).toString();

		name.addAttribute("music", sb);
		ArrayList<String> SPAll = TopicPlotList.get(5);
		String SP = SPAll.get(0);

		int modelID = 1;
		for (int i = 0; i < IEPlotList.size(); i++) {

			ArrayList<String> temp = IEPlotList.get(i);
			if (i == 0) {// HumanList
							// if(temp.size)
				for (int j = 0; j < temp.size();) {
					Element addRule = name.addElement("rule");
					addRule.addAttribute("ruleType", "addToMa");
					addRule.addAttribute("addModel", temp.get(j));
					addRule.addAttribute("class", temp.get(j + 1));
					addRule.addAttribute("spaceName", "SP_" + SP + "_A");

					addRule.addAttribute("type", "People");
					modelID++;
					String modelIDStr = "addModelID" + modelID;
					addRule.addAttribute("addModelID", "addModelID" + modelID);
					j = j + 2;
				}
			}

			if (i == 1) {// ModelList
				for (int j = 0; j < temp.size();) {
					String str = temp.get(j);
					String str1 = temp.get(j + 1);
					Element addRule = name.addElement("rule");
					addRule.addAttribute("ruleType", "addToMa");
					addRule.addAttribute("addModel", str);
					addRule.addAttribute("class1", str1);
					System.out.println("class" + str1);
					modelID++;
					// String modelIDStr="addModelID"+modelID;
					addRule.addAttribute("addModelID", "addModelID" + modelID);
					if (!temp.get(j + 2).toString().contains(".ma")) {// ��һ���洢�Ĳ���ģ��
						addRule.addAttribute("class2", temp.get(j + 2));
						j = j + 3;
					} else {
						j = j + 2;
					}
					addRule.addAttribute("spaceName", "SP_" + SP + "_A");
					addRule.addAttribute("type", "Model");

				}

			}

			if (i == 2) {// LightList
				for (int j = 0; j < temp.size();) {
					Element addRule = name.addElement("rule");
					addRule.addAttribute("ruleType", "addToMa");
					addRule.addAttribute("addModel", temp.get(j));
					addRule.addAttribute("class", temp.get(j + 1));
					addRule.addAttribute("spaceName", "SP_" + SP + "_A");

					addRule.addAttribute("type", "Light");
					modelID++;
					String modelIDStr = "addModelID" + modelID;
					addRule.addAttribute("addModelID", modelIDStr);
					j = j + 2;
				}
			}

			if (i == 4) { // ActionList
				for (int j = 0; j < temp.size();) {
					String str = temp.get(j);
					String str1 = temp.get(j + 1);
					Element addRule = name.addElement("rule");
					addRule.addAttribute("ruleType", "addActionToMa");
					addRule.addAttribute("addModel", str);
					// addRule.addAttribute("class", str1);
					System.out.println("SP" + SP);
					StringBuilder sb1 = new StringBuilder("SP_" + SP + "_A");
					System.out.println("sb1" + sb1);
					addRule.addAttribute("spaceName", sb1.toString());

					addRule.addAttribute("type", "Action");
					String modelIDStr = "addModelID" + modelID;
					addRule.addAttribute("addModelID", modelIDStr);
					j = j + 2;

				}

			}
		}
		SP = null;
		boolean yesNo = XMLInfoFromIEDom4j.doc2XmlFile(doc, xmlPath);
		System.out.println("plot design end");

	}

	/**
	 * is the add the messages or IE messages
	 * 
	 * @param IE
	 *            model
	 */

	/**
	 * ͨ������ģ��ԭ��������ģ��ԭ������Ӧ��ģ�ͣ�hasModelFromTemplate��
	 * 
	 * @param model
	 * @param englishTemplatePlot��������ֵ
	 * @return [model,super,super][model,super,super]
	 */
	public Map<String, ArrayList<ArrayList>> getIndividualFromEnglishTemplate(OWLModel model,
			ArrayList<String> englishTemplatePlot) {

		Map<String, ArrayList<ArrayList>> models = new HashMap<String, ArrayList<ArrayList>>();

		OWLObjectProperty hasModelFTpProperty = model.getOWLObjectProperty("hasModelFromTemplate");
		OWLObjectProperty hasModelProperty = model.getOWLObjectProperty("hasmodel");
		OWLObjectProperty hasFloorProperty = model.getOWLObjectProperty("hasFloor");
		for (Iterator<String> its = englishTemplatePlot.iterator(); its.hasNext();)// �������е�ģ��ԭ��
		{
			String templateAllName = its.next();
			int iPostion = templateAllName.lastIndexOf(":");
			templateAllName = templateAllName.substring(0, iPostion);
			String[] temp = templateAllName.split(":");

			String templateAutmName = templateAllName.split(":")[temp.length - 1];
			OWLIndividual templateIndividual = model.getOWLIndividual(templateAutmName);
			if (templateIndividual != null)// �鿴ģ��ԭ������Ӧ��ʵ���Ƿ����
											// if(!templateIndividual.equals(null))//�鿴ģ��ԭ������Ӧ��ʵ���Ƿ����
			{
				int valueNum = templateIndividual.getPropertyValueCount(hasModelFTpProperty);
				int valueNum1 = templateIndividual.getPropertyValueCount(hasModelProperty);
				int FloorNum = templateIndividual.getPropertyValueCount(hasFloorProperty);
				if (valueNum > 0)// ��Ӧ��hasmodelFromTemplate�����Ƿ����0
				{
					ArrayList<ArrayList> individualLists = new ArrayList();
					Collection templateModelVlaues = templateIndividual.getPropertyValues(hasModelFTpProperty);

					for (Iterator<OWLIndividual> its2 = templateModelVlaues.iterator(); its2.hasNext();) {
						ArrayList modelList = new ArrayList();
						OWLIndividual value = its2.next();
						modelList = getIndividualListFromClass(model, value);
						individualLists.add(modelList);

					}

					models.put("1", individualLists);

				}
				if (valueNum1 > 0)// ��Ӧ��hasmodel�����Ƿ����0
				{
					ArrayList<ArrayList> individualLists = new ArrayList();
					Collection templateModelVlaues = templateIndividual.getPropertyValues(hasModelProperty);

					for (Iterator<OWLIndividual> its2 = templateModelVlaues.iterator(); its2.hasNext();) {
						ArrayList modelList = new ArrayList();
						OWLIndividual value = its2.next();
						modelList = getIndividualListFromClass(model, value);
						individualLists.add(modelList);

					}
					models.put("2", individualLists);

				}
				if (FloorNum > 0) {
					Collection templateModelVlaues = templateIndividual.getPropertyValues(hasFloorProperty);
					ArrayList<ArrayList> individualLists = new ArrayList();
					for (Iterator<OWLIndividual> its2 = templateModelVlaues.iterator(); its2.hasNext();) {
						ArrayList modelList = new ArrayList();
						OWLIndividual value = its2.next();
						modelList = getIndividualListFromClass(model, value);
						individualLists.add(modelList);

					}
					models.put("3", individualLists);
				}
			}
			templateAutmName = null;
		}

		// System.out.println("IEģ��ʵ����"+individualLists);
		return models;
	}

	/***
	 * 2015.1.16 �Ķ�Ontology����ģ�������������⻻Ϊ��������
	 * ��ʱһ��ģ��ֻ��һ�����࣬ͬʱ��ȡ������ͬlocation��superclass��ģ��ȡ��
	 */

	public ArrayList<String> getIndividualListFromClass(OWLModel model, OWLIndividual value) {
		ArrayList modelList = new ArrayList();
		modelList.add(value.getBrowserText());
		OWLNamedClass modelType = (OWLNamedClass) value.getDirectType();
		// System.out.println("superclass"+modelType);
		OWLObjectProperty Location = model.getOWLObjectProperty("Location");
		Collection location = value.getPropertyValues(Location);
		if (location != null) {
			for (Iterator it = location.iterator(); it.hasNext();) { // �п����ж��location���������ⶼ���԰ڷ�
				OWLIndividual locationIndi = (OWLIndividual) it.next();
				modelList.add(locationIndi.getBrowserText()); // [model,location1,location2]
																// or[model,location]
			}
			Collection modelCollection = modelType.getDirectInstances();// �õ�ͬһ�������µ���������
			ArrayList modelsTemp = new ArrayList();
			for (Iterator it2 = modelCollection.iterator(); it2.hasNext();) {
				OWLIndividual SCmodel = (OWLIndividual) it2.next();
				modelsTemp.add(SCmodel.getBrowserText());
			}
			Random r = new Random();
			int selectedM = r.nextInt(modelsTemp.size());
			modelList.set(0, modelsTemp.get(selectedM));
			// System.out.println("modelList="+modelList);

		}
		return modelList;
	}

	/**
	 * ��ȡ���ģ���еĸ���ģ��,�ж���һ��
	 * 
	 * @param topc
	 *            ����Topic
	 * @param model
	 *            �����
	 * @return ArrayList<String> templateAtom
	 */
	public ArrayList<ArrayList> getPlotAtom(OWLModel model, String Chinesetopic) {
		ArrayList<String> EnglishTopicList = new ArrayList();
		ArrayList<ArrayList> PlotAtom = new ArrayList();
		ArrayList<String> ActionList = new ArrayList();
		ArrayList<String> ModelList = new ArrayList();
		ArrayList<String> SkyList = new ArrayList();
		ArrayList<String> LightList = new ArrayList();
		ArrayList<String> musicList = new ArrayList();
		ArrayList<String> HumanList = new ArrayList();

		int ModelNum = 0;
		OWLNamedClass EnglishTopic = null;
		OWLNamedClass PlotTemplate = model.getOWLNamedClass("PlotTemplate");
		OWLDatatypeProperty ChinesedataType = model.getOWLDatatypeProperty("chineseName");

		OWLObjectProperty MusciPro = model.getOWLObjectProperty("hasMusic");
		OWLObjectProperty Human = model.getOWLObjectProperty("hasHuman");

		OWLObjectProperty Action = model.getOWLObjectProperty("p2:hasActionType");
		OWLDatatypeProperty num = model.getOWLDatatypeProperty("addModelNumber");
		Collection PlotCls = PlotTemplate.getSubclasses(true);

		for (Iterator it = PlotCls.iterator(); it.hasNext();) {
			EnglishTopic = (OWLNamedClass) it.next();
			if (EnglishTopic.getDirectSubclassCount() == 0) {
				Object OWLChinese = EnglishTopic.getHasValue(ChinesedataType);
				if (OWLChinese != null) {

					if (OWLChinese.toString().equals(Chinesetopic)) {

						// System.out.println("EnglishTopic"+EnglishTopic.getBrowserText());
						EnglishTopicList.add(EnglishTopic.getBrowserText());
						EnglishTopicList.add("Topic");

						OWLNamedClass HumanObject = (OWLNamedClass) EnglishTopic.getAllValuesFrom(Human);
						HumanList.add(GetInstanceFromClass(model, HumanObject).getBrowserText());
						HumanList.add("People");
						PlotAtom.add(0, HumanList);
						// System.out.println("HumanObject:"+HumanList);

						if (EnglishTopic.getMaxCardinality(num) > 0)
							ModelNum = EnglishTopic.getMaxCardinality(num);// �õ�ȡ�ü���ֵ
						if (EnglishTopic.getMinCardinality(num) > 0)
							ModelNum = EnglishTopic.getMinCardinality(num);
						// if(EnglishTopic.getCardinality(num)>0)
						// ModelNum= EnglishTopic.getCardinality(num);
						// �õ�individual
						Collection owlIndis = EnglishTopic.getInstances();// ȡ��ʵ��
						for (Iterator it2 = owlIndis.iterator(); it2.hasNext();) {
							OWLIndividual TopicIns = (OWLIndividual) it2.next();
							// ���ʵ���µĶ���
							System.out.println("model ��ʼ");
							ModelList = getIndividualFromPlotTemplate(model, TopicIns, "hasmodel"); // 1
																									// ���ģ��
							PlotAtom.add(1, ModelList);
							System.out.println("Light ��ʼ");

							LightList = getIndividualFromPlotTemplate(model, TopicIns, "hasLight");
							PlotAtom.add(2, LightList);
						}

						Object Music = EnglishTopic.getSomeValuesFrom(MusciPro);// �õ�����
						if (Music != null) {
							System.out.println("Music" + Music.getClass().getName());
							if (Music.getClass().getName()
									.equals("edu.stanford.smi.protegex.owl.model.impl.DefaultOWLUnionClass")) {
								OWLUnionClass UnionMusic = (OWLUnionClass) EnglishTopic.getSomeValuesFrom(MusciPro);
								Collection Musics = UnionMusic.getNamedOperands();
								for (Iterator itm = Musics.iterator(); itm.hasNext();) {
									OWLNamedClass owlC = (OWLNamedClass) itm.next();
									System.out.println("MusicC" + owlC.getBrowserText());
									musicList.add(GetInstanceFromClass(model, owlC).getBrowserText());

								}

							} else {
								OWLNamedClass owlC = (OWLNamedClass) EnglishTopic.getAllValuesFrom(MusciPro);
								System.out.println("MusicC" + owlC.getBrowserText());
								musicList.add(GetInstanceFromClass(model, owlC).getBrowserText());

							}

						} else {
							OWLNamedClass MusicClass = model.getOWLNamedClass("CommonMusic");
							musicList.add(GetInstanceFromClass(model, MusicClass).getBrowserText());
						}
						musicList.add("Music");
						PlotAtom.add(3, musicList);

						PlotAtom.add(4, EnglishTopicList);
					}
				}
			}
		}

		return PlotAtom;
	}

	/**
	 * ����������õ�һ��ʵ��
	 * 
	 * @param OWLNamedClass
	 * @return OWLIndividual
	 */
	public OWLIndividual GetInstanceFromClass(OWLModel model, OWLNamedClass owlC) {
		OWLIndividual SelectedIndividual = null;
		Collection Instances = owlC.getInstances(true);
		int size = Instances.size();
		if (size > 0) {
			Random r = new Random();
			int i = r.nextInt(size);
			int j = 0;
			for (Iterator itM = Instances.iterator(); itM.hasNext();) {

				OWLIndividual indi = (OWLIndividual) itM.next();

				if (j == i) {
					SelectedIndividual = indi;
					break;
				}
				j++;

			}
			System.out.println("SelectedIndividual:" + SelectedIndividual.getBrowserText());
		}

		return SelectedIndividual;

	}

	/**
	 * ��������ʵ���¶����ģ�ͣ�hasModelFromTemplate��
	 * 
	 * @param model
	 * @param englishTemplate
	 * @param TopicIndiv
	 *            EnglishTopic
	 * @return individualLists [value,super1,super2][value2,super1...]
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public ArrayList<ArrayList> getIndividualFromPlotTemplate2(OWLModel model, OWLIndividual TopicIndiv,
			String factor) {

		ArrayList<ArrayList> individualLists = new ArrayList();
		OWLObjectProperty hasModelFTpProperty = model.getOWLObjectProperty(factor);

		int valueNum = TopicIndiv.getPropertyValueCount(hasModelFTpProperty);
		if (valueNum > 0)// ��Ӧ��model�����Ƿ����0
		{
			Collection templateModelVlaues = TopicIndiv.getPropertyValues(hasModelFTpProperty);

			for (Iterator<OWLIndividual> its2 = templateModelVlaues.iterator(); its2.hasNext();) {
				ArrayList<String> modelList = new ArrayList();
				OWLIndividual value = its2.next();

				System.out.println("value:Model=" + value.getBrowserText());
				modelList = getIndividualListFromClass(model, value);// ���ص�����������ĳһ���ֵ�ʵ��
				individualLists.add(modelList);
			}
		}

		return individualLists;
	}

	/**
	 * ��������ʵ���¶����ģ�ͣ�hasModelFromTemplate��
	 * 
	 * @param model
	 * @param englishTemplate
	 * @param TopicIndiv
	 *            EnglishTopic
	 * @return individualList [value,super1,super2]
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public ArrayList<String> getIndividualFromPlotTemplate(OWLModel model, OWLIndividual TopicIndiv, String factor) {

		ArrayList<String> individualList = new ArrayList();
		OWLObjectProperty hasModelFTpProperty = model.getOWLObjectProperty(factor);

		int valueNum = TopicIndiv.getPropertyValueCount(hasModelFTpProperty);
		if (valueNum > 0)// ��Ӧ��model�����Ƿ����0
		{
			Collection templateModelVlaues = TopicIndiv.getPropertyValues(hasModelFTpProperty);

			for (Iterator<OWLIndividual> its2 = templateModelVlaues.iterator(); its2.hasNext();) {
				OWLIndividual value = its2.next();
				System.out.println("value:Model" + value.getBrowserText());
				individualList = getIndividualListFromClass(model, value);
				/*
				 * individualList.add(value.getBrowserText()); Collection
				 * modelType=value.getDirectTypes(); OWLNamedClass
				 * ModelC=null;//��Ÿ��� Collection OtherModels=null;
				 * for(Iterator it=modelType.iterator();it.hasNext();){
				 * //������� ModelC=(OWLNamedClass) it.next();
				 * System.out.println("modelType:"+ModelC.getBrowserText());
				 * individualList.add(ModelC.getBrowserText());//����
				 * value,super1,super2 } //
				 * OtherModel=GetInstanceFromClass(model,ModelC);//�õ����������
				 * Model for(Iterator it=modelType.iterator();it.hasNext();){
				 * //������� ModelC=(OWLNamedClass) it.next();
				 * OtherModels=ModelC.getInstances();//�ĵ����������ʵ��
				 * //���Ҹ�������࣬���������������ģ�͡� //����
				 * �����е�����ʵ�������ж����ʵ���Ƿ������������࣬Ȼ����뵽����
				 * ArrayList<String> tempOtherModel=new ArrayList();
				 * for(Iterator iti=OtherModels.iterator();iti.hasNext();) {
				 * OWLIndividual Otherindividual=(OWLIndividual) iti.next();
				 * //System.out.println("modelIndividual="+Otherindividual.
				 * getBrowserText());
				 * if(JudgeClass(model,Otherindividual,individualList)) {
				 * //������ͬ����ķ�����ʱlist���档Ȼ�����ѡ��,individualList(0)Ϊ�����
				 * individual
				 * tempOtherModel.add(Otherindividual.getBrowserText());
				 * 
				 * System.out.println("modellist="+tempOtherModel); }
				 * 
				 * }//ÿһ��������� int modelsize=tempOtherModel.size()+1; Random
				 * r=new Random(); int selID=r.nextInt(modelsize); if(selID!=0){
				 * individualList.set(0, tempOtherModel.get(selID-1)); }
				 * 
				 * }
				 */
			}
		}

		return individualList;
	}

	/**
	 * �ж� �ƶϳ���ģ���Ƿ��ԭ��ģ�������������ͬ�� ture ����룬false ������
	 * 
	 */
	public boolean JudgeClass(OWLModel model, OWLIndividual Otherindividual, ArrayList<String> IndividualList) {

		int count = 0;

		if (Otherindividual.getDirectTypes().size() > 0) {
			Collection modelTypes = Otherindividual.getDirectTypes();
			for (Iterator it = modelTypes.iterator(); it.hasNext();) {
				OWLNamedClass modelClass = (OWLNamedClass) it.next();
				for (int i = 1; i < IndividualList.size(); i++) {
					if (IndividualList.get(i).equals(modelClass.getBrowserText())) {
						count++;
					}
				}
			}

		}
		if (count == IndividualList.size() - 1 && !IndividualList.get(0).equals(Otherindividual.getBrowserText()))

			return true;
		else
			return false;
	}

	/**
	 * Ϊ�����ɵ�space���ֵ
	 */

	public void setValueTospace(OWLIndividual spaceName, OWLModel model) {

		OWLObjectProperty hasLayout = model.getOWLObjectProperty("p3:hasLayout");
		OWLNamedClass outdoorGroundLayout = model.getOWLNamedClass("p3:OutdoorGroundLayout");
		OWLNamedClass SkyLayout = model.getOWLNamedClass("p3:SkyLayout");
		OWLDatatypeProperty Spacerotatex = model.getOWLDatatypeProperty("scenerotatex");
		OWLDatatypeProperty Spacerotatey = model.getOWLDatatypeProperty("scenerotatey");
		OWLDatatypeProperty Spacerotatez = model.getOWLDatatypeProperty("scenerotatez");
		spaceName.addPropertyValue(Spacerotatex, "0.0");// ��ת
		spaceName.addPropertyValue(Spacerotatey, "0.0");
		spaceName.addPropertyValue(Spacerotatez, "0.0");

		OWLDatatypeProperty Spacescalex = model.getOWLDatatypeProperty("spacescalex");
		OWLDatatypeProperty Spacescaley = model.getOWLDatatypeProperty("spacescaley");
		OWLDatatypeProperty Spacescalez = model.getOWLDatatypeProperty("spacescalez");// ����
		spaceName.addPropertyValue(Spacescalex, "1.0");// ?�ɱ䣬�ӱ���
		spaceName.addPropertyValue(Spacescaley, "1.0");
		spaceName.addPropertyValue(Spacescalez, "1.0");

		OWLDatatypeProperty Spacecenterx = model.getOWLDatatypeProperty("spacecenterx");// ���ĵ�
		OWLDatatypeProperty Spacecentery = model.getOWLDatatypeProperty("spacecentery");
		OWLDatatypeProperty Spacecenterz = model.getOWLDatatypeProperty("spacecenterz");
		String SpacecenterxValue = "";
		String SpacecenteryValue = "";
		String SpacecenterzValue = "";
		// System.out.println("SpaceName:"+spaceName.getBrowserText());

		// ��ʼplane��С��Ϊdepth and width ontology�洢��Ϊx.z�Ĳ�ֵ
		OWLDatatypeProperty Depth = model.getOWLDatatypeProperty("Depth");
		OWLDatatypeProperty Width = model.getOWLDatatypeProperty("Width");

		String depth = "";// �����ռ��y���ֵ
		String width = "";// �����ռ��x��ֵ
		// ���������������ĵ�
		if (spaceName.getBrowserText().contains("Plot_OutFloor")) {
			Random r = new Random();
			int d = r.nextInt(100) + 100;// (100-200)
			depth = Integer.toString(d);
			width = Integer.toString(d);

			SpacecenterxValue = "0.0";
			SpacecenteryValue = "0.0";
			SpacecenterzValue = "0.0";

			// OWLIndividual
			// LayoutIndividual=GetInstanceFromClass(model,outdoorGroundLayout
			// );
			// spaceName.addPropertyValue(hasLayout, LayoutIndividual);
		}
		// ���澲̬�����������ĵ�
		if (spaceName.getBrowserText().contains("Plot_OutOnLand")) {
			Random r = new Random();
			int d = r.nextInt(100) + 100;// (100-200)
			depth = Integer.toString(d);
			width = Integer.toString(d);

			int tempx = Integer.parseInt(depth) / 2;
			int tempz = Integer.parseInt(width) / 2;
			SpacecenterxValue = Integer.toString(tempx);
			SpacecenteryValue = "0.0";
			SpacecenterzValue = Integer.toString(tempz);
			OWLIndividual LayoutIndividual = GetInstanceFromClass(model, outdoorGroundLayout);
			spaceName.addPropertyValue(hasLayout, LayoutIndividual);
		}

		// ����Ϊ�����Ͽ��е����ĵ�
		if (spaceName.getBrowserText().contains("Plot_OutInAir")) {
			Random r = new Random();
			int d = r.nextInt(200) + 200;// (200-400)
			depth = Integer.toString(d);
			width = Integer.toString(d);
			int y = r.nextInt(20) + 180;

			SpacecenterxValue = "0.0";
			SpacecenteryValue = Integer.toString(y);
			SpacecenterzValue = "0.0";

			OWLIndividual LayoutIndividual = GetInstanceFromClass(model, SkyLayout);
			spaceName.addPropertyValue(hasLayout, LayoutIndividual);

		}

		// ����Ϊ�������е����ĵ�
		if (spaceName.getBrowserText().contains("Plot_OutInHalfAir")) {
			Random r = new Random();
			int d = r.nextInt(100) + 100;// (100-200)
			depth = Integer.toString(d);
			width = Integer.toString(d);
			int y = r.nextInt(20) + 50;
			SpacecenterxValue = "0.0";
			SpacecenteryValue = Integer.toString(y);
			SpacecenterzValue = "0.0";

			OWLIndividual LayoutIndividual = GetInstanceFromClass(model, SkyLayout);
			spaceName.addPropertyValue(hasLayout, LayoutIndividual);

		}

		// ��̬�����������ĵ�
		if (spaceName.getBrowserText().contains("Plot_ActiveSpaceOnGround")) {
			Random r = new Random();
			int d = r.nextInt(100) + 100;// (100-200)
			depth = Integer.toString(d);
			int w = r.nextInt(100) + 100;
			width = Integer.toString(w);

			int tempx = d / 2;
			int tempz = w / 2;
			SpacecenterxValue = Integer.toString(tempx);
			SpacecenteryValue = "0.0";
			SpacecenterzValue = Integer.toString(tempz);

			OWLIndividual LayoutIndividual = GetInstanceFromClass(model, outdoorGroundLayout);
			spaceName.addPropertyValue(hasLayout, LayoutIndividual);

		}

		spaceName.addPropertyValue(Depth, depth);
		spaceName.addPropertyValue(Width, width);

		spaceName.addPropertyValue(Spacecenterx, SpacecenterxValue);// ?���ݲ�ͬ��space�����ĵ㲻ͬ
		spaceName.addPropertyValue(Spacecentery, SpacecenteryValue);
		spaceName.addPropertyValue(Spacecenterz, SpacecenterzValue);

		OWLDatatypeProperty hasCamera = model.getOWLDatatypeProperty("hasCamera");
		spaceName.addPropertyValue(hasCamera, true);

		OWLObjectProperty hasShape = model.getOWLObjectProperty("hasShape");
		spaceName.addPropertyValue(hasShape, "Plane");

	}

	/**
	 * ����ma�ļ����������ֵ
	 * 
	 * @param maName
	 *            ���ɵ�ma����
	 * 
	 */
	@SuppressWarnings("deprecation")
	public void GenerateMa(String maName, OWLModel model) {
		OWLNamedClass PlotMa = model.getOWLNamedClass("PlotMa");
		OWLIndividual ma = PlotMa.createOWLIndividual(maName);
		OWLDatatypeProperty maFrameNum = model.getOWLDatatypeProperty("maFrameNumber");
		OWLDatatypeProperty hasSky = model.getOWLDatatypeProperty("hasSky");

		OWLObjectProperty hasSceneSpace = model.getOWLObjectProperty("hasSceneSpace");
		OWLObjectProperty usedSpaceInMa = model.getOWLObjectProperty("usedSpaceInMa");
		OWLDatatypeProperty scenerotatex = model.getOWLDatatypeProperty("scenerotatex");
		OWLDatatypeProperty scenerotatey = model.getOWLDatatypeProperty("scenerotatey");
		OWLDatatypeProperty scenerotatez = model.getOWLDatatypeProperty("scenerotatez");
		// ���ñ���ͼƬ����ҪΪ��ʵ������� 3�����ⳡ��
		OWLDatatypeProperty backgroundPictureType = model.getOWLDatatypeProperty("backgroundPictureType");
		OWLObjectProperty hasValueOfPlane = model.getOWLObjectProperty("hasValueOfPlace");// ���ڻ��������

		// =========2016.3.24 ���� ���=============
		OWLObjectProperty hasValueOfTime = model.getOWLObjectProperty("hasValueOfTime");

		// ���ñ���ͼƬ
		OWLObjectProperty hasBackgroundPicture = model.getOWLObjectProperty("hasBackgroundPicture");
		Random r = new Random();
		ma.addPropertyValue(maFrameNum, r.nextInt(200) + 300);
		ma.addPropertyValue(scenerotatez, "0");
		ma.addPropertyValue(scenerotatex, "0");
		ma.addPropertyValue(scenerotatey, "0");
		ma.addPropertyValue(hasSky, true);
		String spaceName = maName.substring(0, maName.length() - 3);

		// ��дspace����,��Ϊ��̬space �;�̬space
		OWLNamedClass OutsideonGroundspaceClass = model.getOWLNamedClass("PlaneSceneSpaceOutsideRoomOnGround");
		OWLNamedClass InsideRoomOnTablespaceClass = model.getOWLNamedClass("PlaneSceneSpaceInsideRoomOnTable");
		OWLNamedClass OutsideRoomInAirspaceClass = model.getOWLNamedClass("PlaneSceneSpaceOutsideRoomInAir");
		OWLNamedClass ActiveSpaceOnGroundClass = model.getOWLNamedClass("ActiveSpaceOnGround");
		OWLNamedClass OutsideRoomInHalfAirspaceClass = model.getOWLNamedClass("PlaneSceneSpaceOutsideRoomInHalfAir");

		OWLIndividual OutsideonGround = (OWLIndividual) OutsideonGroundspaceClass
				.createOWLIndividual("SP_" + spaceName + "_OutOnLand");
		OWLIndividual OutsideFloor = (OWLIndividual) OutsideonGroundspaceClass
				.createOWLIndividual("SP_" + spaceName + "_OutFloor");
		OWLIndividual OutsideRoomInAir = (OWLIndividual) OutsideRoomInAirspaceClass
				.createOWLIndividual("SP_" + spaceName + "_OutInAir");
		OWLIndividual InsideRoomOnTable = (OWLIndividual) InsideRoomOnTablespaceClass
				.createOWLIndividual("SP_" + spaceName + "_InRoomOnTable");
		OWLIndividual ActiveSpaceOnGround = (OWLIndividual) ActiveSpaceOnGroundClass
				.createOWLIndividual("SP_" + spaceName + "_ActiveSpaceOnGround");
		OWLIndividual OutsideRoomInHalfAir = (OWLIndividual) OutsideRoomInHalfAirspaceClass
				.createOWLIndividual("SP_" + spaceName + "_OutInHalfAir");

		setValueTospace(OutsideonGround, model);// ������space���ֵ
		setValueTospace(OutsideFloor, model);// ������Floorspace���ֵ
		setValueTospace(OutsideRoomInAir, model);// ������space���ֵ
		setValueTospace(ActiveSpaceOnGround, model);// ����������space���ֵ
		setValueTospace(OutsideRoomInHalfAir, model);// ����������ӿռ�

		// ���ڱ����������򵥶���

		ma.addPropertyValue(hasSceneSpace, OutsideonGround);
		ma.addPropertyValue(hasSceneSpace, OutsideFloor);
		ma.addPropertyValue(hasSceneSpace, OutsideRoomInAir);
		ma.addPropertyValue(hasSceneSpace, InsideRoomOnTable);
		ma.addPropertyValue(hasSceneSpace, ActiveSpaceOnGround);
		ma.addPropertyValue(hasSceneSpace, OutsideRoomInHalfAir);
		// usedSpaceInMa
		ma.addPropertyValue(usedSpaceInMa, OutsideonGround);
		ma.addPropertyValue(usedSpaceInMa, OutsideFloor);
		ma.addPropertyValue(usedSpaceInMa, OutsideRoomInAir);
		ma.addPropertyValue(usedSpaceInMa, InsideRoomOnTable);
		ma.addPropertyValue(usedSpaceInMa, OutsideRoomInHalfAir);
		ma.addPropertyValue(usedSpaceInMa, ActiveSpaceOnGround);
		// backgroundPictureType
		ma.addPropertyValue(backgroundPictureType, 3);// ����������������������λ��
		OWLIndividual outDoorDescription = model.getOWLIndividual("outDoorDescription");
		// System.out.println("outDoorDescription"+outDoorDescription+hasValueOfPlane);
		ma.addPropertyValue(hasValueOfPlane, outDoorDescription);

		// ===============2016.3.24 ���� ���
		OWLIndividual dayTimeDescription = model.getOWLIndividual("dayTimeDescription");
		ma.addPropertyValue(hasValueOfPlane, dayTimeDescription);

		// =============================
		// ���ñ���ͼƬ
		String bgPIC = "BackgroundScenePicture";
		OWLNamedClass bgPic = model.getOWLNamedClass(bgPIC);
		OWLIndividual backgroundPic = GetInstanceFromClass(model, bgPic);

		ma.addPropertyValue(hasBackgroundPicture, backgroundPic);

		// ��������panel
		//
		// String fileName =
		// "C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";
		//
		// JenaMethod.saveOWLFile((JenaOWLModel)model,fileName);
		// System.out.println("ma ��ش����ɹ�");

	}

	/**
	 * ��һ��owl�ļ�
	 * 
	 * @param url��owl�ļ����ڵ�·��
	 * @return
	 * @throws OntologyLoadException
	 */
	public static OWLModel createOWLFile1(String url) throws OntologyLoadException {
		try {
			OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI(url);
			return owlModel;
		} catch (OntologyLoadException zz) {
			OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI(url);
			return owlModel;

		}
	}

	public static OntModel createOWLModelFile2(String url) {
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		model.read(url);
		return model;
	}

	public static void main(String[] args) throws OntologyLoadException {
		System.gc();
		System.out.println("1");
		// String url =
		// "file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";
		String url = "file:///C:/ontologyOWL/sumoOWL2/sumo_phone3.owl";
		// ͨ��url���owlģ��
		OWLModel model = createOWLFile1(url);
		// OWLIndividual
		// sp=model.getOWLIndividual("SP_BalletDanceActionPlot_OutOnLand");
		// new PlotAddModelToMa().changeSpaceScale(model, sp);
		// ɾ��p14�е�ʵ��
		/*
		 * RDFSNamedClass html=model.getRDFSNamedClass("p14:html"); Collection
		 * in= html.getInstances(true); for(Iterator
		 * it=in.iterator();it.hasNext();){ RDFIndividual
		 * indi=(RDFIndividual)it.next(); indi.delete(); }
		 */
		// ��������panel

		// String fileName = "C:/ontologyOWL/sumoOWL2/sumo_phone3.owl";

		// JenaMethod.saveOWLFile((JenaOWLModel)model,fileName);
		// System.out.println("ma ��ش����ɹ�");

		// System.out.println("ma");
		ArrayList<String> c = new ArrayList();
		// c.add(0, new String
		// ("TimeTemplate:DayTemplate:TimeOfDayTemplate:DayTimeTemplate:1.0") );
		// System.out.println("1");
		// //ֻ��һ������ string topic (�봫���������⣬����������ֱ������plot��)
		// ArrayList <String> templateW
		PlotDesign p = new PlotDesign(model, "���", c);
		p.GenerateMa("DepartPlot.ma", model);

	}

}
