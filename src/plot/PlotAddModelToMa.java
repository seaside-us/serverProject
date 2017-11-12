package plot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLFactoryException;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;

public class PlotAddModelToMa {

	/**
	 * 2014.9.17
	 * 
	 * @param maName��ma�ļ�������
	 *            Topic+plot
	 * @param topicName��Ӣ����������
	 *            Ϊplot�е�����
	 * @param model
	 * @return �������model��ӵ�AddRelatedModel ���С�������
	 * @throws SWRLRuleEngineException
	 * @throws SWRLFactoryException
	 * @throws IOException
	 * @throws SecurityException
	 */
	public OWLModel processSWRL2(String maName, String topicName, OWLModel model, ArrayList<String> englishTemplate,
			ArrayList<String> englishTemplatePlot)
			throws SWRLRuleEngineException, SWRLFactoryException, SecurityException, IOException {

		ArrayList<String> modelValuesGround = new ArrayList();
		ArrayList<String> modelActiveGround = new ArrayList();
		ArrayList<String> modelValuesAir = new ArrayList();
		ArrayList<String> modelValuesHalfAir = new ArrayList();
		ArrayList<String> modelValuesOnTable = new ArrayList();

		ArrayList<String> modelValuesGround1 = new ArrayList();
		ArrayList<String> modelActiveGround1 = new ArrayList();
		ArrayList<String> modelValuesAir1 = new ArrayList();
		ArrayList<String> modelValuesHalfAir1 = new ArrayList();
		ArrayList<String> modelValuesOnTable1 = new ArrayList();

		ArrayList<String> modelValuesGround0 = new ArrayList();
		ArrayList<String> modelActiveGround0 = new ArrayList();
		ArrayList<String> modelValuesAir0 = new ArrayList();
		ArrayList<String> modelValuesHalfAir0 = new ArrayList();
		ArrayList<String> modelValuesOnTable0 = new ArrayList();

		ArrayList<String> modelValues = new ArrayList();
		ArrayList<String> FloorValues = new ArrayList();
		ArrayList<String> allValues = new ArrayList();
		ArrayList<ArrayList> modelLists = new ArrayList(); // ���model����
		ArrayList<ArrayList> modelListFrTem = new ArrayList();// ���target=1
		ArrayList<ArrayList> plotModelListsFrHM = new ArrayList();// ���target=0����ֵ

		PlotDesign p = new PlotDesign();

		OWLObjectProperty hasModelFTpProperty = model.getOWLObjectProperty("hasModelFromTemplate");
		OWLObjectProperty addToMaProperty = model.getOWLObjectProperty("addToMa");
		OWLObjectProperty hasPutObjectProperty = model.getOWLObjectProperty("hasPutObjectInSpace");
		OWLIndividual PlotIndividual = model.getOWLIndividual(maName);
		OWLIndividual PlotGroundIndividual = model.getOWLIndividual("SP_" + topicName + "_OutOnLand");
		OWLIndividual PlotActiveGroundIndividual = model.getOWLIndividual("SP_" + topicName + "_ActiveSpaceOnGround");
		OWLIndividual PlotOnTableIndividual = model.getOWLIndividual("SP_" + topicName + "_InRoomOnTable");
		OWLIndividual PlotAirIndividual = model.getOWLIndividual("SP_" + topicName + "_OutInAir");
		OWLIndividual PlotHalfAirIndividual = model.getOWLIndividual("SP_" + topicName + "_OutInHalfAir");
		OWLIndividual PlotFloorIndividual = model.getOWLIndividual("SP_" + topicName + "_OutFloor");
		OWLDatatypeProperty degreeProperty = model.getOWLDatatypeProperty("degree");
		// ͨ��topicName�õ�topicʵ��
		// �ɹ�����ִʵ�����ԭ������Ӧ��ģ��
		if (englishTemplate.size() > 0) {

			for (Iterator<String> its = englishTemplate.iterator(); its.hasNext();)// �������е�ģ��ԭ��
			{
				String templateAllName = its.next();
				int iPostion = templateAllName.indexOf(":");
				// String templateAutmName=templateAllName.substring(iPostion+1,
				// templateAllName.length());
				String templateAutmName = templateAllName.substring(iPostion + 1);

				OWLIndividual templateIndividual = model.getOWLIndividual(templateAutmName);
				System.out.println("��ճ�������ӵ�ģ�ͣ�" + templateIndividual.getBrowserText());
				if (templateIndividual != null)// �鿴ģ��ԭ������Ӧ��ʵ���Ƿ����
				{
					int valueNum = templateIndividual.getPropertyValueCount(hasModelFTpProperty);
					if (valueNum > 0)// ��Ӧ��model�����Ƿ����0
					{
						Collection templateModelVlaues = templateIndividual.getPropertyValues(hasModelFTpProperty);

						System.out.println();
						for (Iterator<OWLIndividual> its2 = templateModelVlaues.iterator(); its2.hasNext();) {
							ArrayList<String> modelList = new ArrayList();
							OWLIndividual modelIndividual = its2.next();
							modelList = new PlotDesign().getIndividualListFromClass(model, modelIndividual);
							modelLists.add(modelList);

						}
					}
				}

			}

		}
		if (!modelLists.isEmpty()) {
			// System.out.println("IEModelList="+modelLists);
			modelListFrTem = modelLists;
			// System.out.println("modelListAll0="+modelListFrTem);
		}

		// ͨ����ڹ滮���������
		OWLNamedClass topicClass = model.getOWLNamedClass(topicName);
		if (topicClass != null) {
			System.out.print("������:");
			OWLIndividual topicIndividual = new PlotDesign().GetInstanceFromClass(model, topicClass);// �õ����ⶨ���һ��ʵ��
			if (topicIndividual != null) {
				String hasModelFromTemplate = "hasModelFromTemplate";
				ArrayList<ArrayList> plotModelLists1 = p.getIndividualFromPlotTemplate2(model, topicIndividual,
						"hasModelFromTemplate");
				plotModelListsFrHM = p.getIndividualFromPlotTemplate2(model, topicIndividual, "hasmodel");
				ArrayList<ArrayList> plotFloor = p.getIndividualFromPlotTemplate2(model, topicIndividual, "hasFloor");
				if (!plotModelLists1.isEmpty()) {
					System.out.println("plotModelLists:" + plotModelLists1);
					for (int i = 0; i < plotModelLists1.size(); i++)
						modelListFrTem.add(plotModelLists1.get(i)); // ����������ϲ������շ���һ��modelList��
				}

				if (!plotFloor.isEmpty()) {
					for (int i = 0; i < plotFloor.size(); i++)
						modelListFrTem.add(plotFloor.get(i));
				}
			}
		}
		System.out.println("modelListAll1=" + modelListFrTem);
		// ********************���plot�е�ģ�������ģ��*****************************//
		Map<String, ArrayList<ArrayList>> IEModelList = new HashMap<String, ArrayList<ArrayList>>();// 1
																									// ��hasmodelFromTemplate
																									// 2
																									// ��hasmodel
																									// 3
																									// hasFloor

		IEModelList = new PlotDesign().getIndividualFromEnglishTemplate(model, englishTemplatePlot);// ���IEģ���е�����ģ��
		System.out.println("IEModelListFromPlot" + IEModelList);
		for (Map.Entry<String, ArrayList<ArrayList>> entry : IEModelList.entrySet()) {
			String key = entry.getKey();
			ArrayList<ArrayList> value = entry.getValue();
			if (key.equals("1") || key.equals("3")) {
				for (int i = 0; i < value.size(); i++)
					modelListFrTem.add(value.get(i));
			}
			if (key.equals("2")) {
				for (int i = 0; i < value.size(); i++)
					plotModelListsFrHM.add(value.get(i));
			}

		}
		// ********************���plot�е�ģ�������ģ��*****************************//
		// target=1 ��ģ�ͼ���ȥ��
		HashSet h = new HashSet(modelListFrTem);
		modelListFrTem.clear();
		modelListFrTem.addAll(h);
		System.out.println("modelListAll:" + modelListFrTem);

		for (int j = 0; j < modelListFrTem.size(); j++) {
			ArrayList<String> modellist = modelListFrTem.get(j);
			for (int k = 0; k < modellist.size(); k++) {
				// �˴�ģ�ͷ��õص�Ӧ����space�ص����Ӧ��
				if (modellist.get(k).contains("Land") && !modellist.get(0).contains("M_floor.ma")) {
					modelValuesGround1.add(modellist.get(0));
					break;
				}
				if (modellist.get(k).contains("Young")) {
					modelActiveGround1.add(modellist.get(0));
					System.out.println("model" + modelActiveGround);
					break;
				}
				if (modellist.get(k).contains("InAir")) {
					modelValuesAir1.add(modellist.get(0));
					break;
				}
				if (modellist.get(k).contains("InhalfAir")) {
					modelValuesHalfAir1.add(modellist.get(0));
				}
				if (modellist.get(k).contains("OnTable")) {
					modelValuesOnTable1.add(modellist.get(0));
					break;
				}
				if (modellist.get(k).contains("M_floor.ma")) {
					FloorValues.add(modellist.get(k));
				}

			}
		}

		// target=0��ģ�Ͱ��ռ��ֿ�
		// target=0��ģ�Ͱ��ռ��ֿ�
		HashSet ht0 = new HashSet(plotModelListsFrHM);
		plotModelListsFrHM.clear();
		plotModelListsFrHM.addAll(ht0);
		System.out.println("plotModelListsFrHM=" + plotModelListsFrHM);
		for (int j = 0; j < plotModelListsFrHM.size(); j++) {
			ArrayList<String> modellist = plotModelListsFrHM.get(j);
			for (int k = 0; k < modellist.size(); k++) {
				// �˴�ģ�ͷ��õص�Ӧ����space�ص����Ӧ��
				if (modellist.get(k).contains("Land") && !modellist.get(0).contains("M_floor.ma")) {
					modelValuesGround0.add(modellist.get(0));
					break;
				}
				if (modellist.get(k).contains("Young")) {
					modelActiveGround0.add(modellist.get(0));
					System.out.println("model" + modelActiveGround);
					break;
				}
				if (modellist.get(k).contains("InAir")) {
					modelValuesAir0.add(modellist.get(0));
					break;
				}
				if (modellist.get(k).contains("InhalfAir")) {
					modelValuesHalfAir0.add(modellist.get(0));
					break;
				}
				if (modellist.get(k).contains("OnTable")) {
					modelValuesOnTable0.add(modellist.get(0));
					break;
				}
				if (modellist.get(k).contains("M_floor.ma")) {
					FloorValues.add(modellist.get(k));
				}

			}
		}

		// target=0 ��target=1����
		for (int i = 0; i < modelValuesGround1.size(); i++) {
			modelValuesGround.addAll(modelValuesGround0);
			modelValuesGround.add(modelValuesGround1.get(i));
		}
		for (int i = 0; i < modelActiveGround1.size(); i++) {
			modelActiveGround.addAll(modelActiveGround0);
			modelActiveGround.add(modelActiveGround1.get(i));
		}
		for (int i = 0; i < modelValuesAir1.size(); i++) {
			modelValuesAir.addAll(modelValuesAir0);
			modelValuesAir.add(modelValuesAir1.get(i));
		}
		for (int i = 0; i < modelValuesHalfAir1.size(); i++) {
			modelValuesHalfAir.addAll(modelValuesHalfAir0);
			modelValuesHalfAir.add(modelValuesHalfAir1.get(i));
		}
		for (int i = 0; i < modelValuesHalfAir1.size(); i++) {
			modelValuesHalfAir.addAll(modelValuesHalfAir0);
			modelValuesHalfAir.add(modelValuesHalfAir1.get(i));
		}
		for (int i = 0; i < modelValuesOnTable1.size(); i++) {
			modelValuesOnTable.addAll(modelValuesOnTable0);
			modelValuesOnTable.add(modelValuesOnTable1.get(i));
		}

		// �õ�����������ģ��
		// OWLObjectProperty hasSky=model.getOWLObjectProperty("hasSky");
		// OWLIndividual Sky=model.getOWLIndividual("tiankong.ma");
		// PlotAirIndividual.setPropertyValue(hasPutObjectProperty, Sky);
		// //�õ�����ģ��
		// OWLObjectProperty hasFloor=model.getOWLObjectProperty("hasFloor");

		if (!modelValuesGround.isEmpty())// ����ÿռ��д��
		{
			PlotGroundIndividual.setPropertyValues(hasPutObjectProperty, modelValuesGround);// �˴��ж���������Ĵ�С��Ȼ������scaleֵ
			// changeSpaceScale(model,PlotGroundIndividual);
		}
		if (!modelActiveGround.isEmpty()) {// ��̬�ռ���
			PlotActiveGroundIndividual.setPropertyValues(hasPutObjectProperty, modelActiveGround);
			// changeSpaceScale(model,PlotActiveGroundIndividual);
		}

		if (!modelValuesAir.isEmpty()) {
			PlotAirIndividual.setPropertyValues(hasPutObjectProperty, modelValuesAir);
			// changeSpaceScale(model,PlotAirIndividual);
		}
		if (!modelValuesHalfAir.isEmpty()) {
			PlotHalfAirIndividual.setPropertyValues(hasPutObjectProperty, modelValuesHalfAir);
			// changeSpaceScale(model, PlotHalfAirIndividual);
		}
		if (!FloorValues.isEmpty()) {
			PlotFloorIndividual.setPropertyValues(hasPutObjectProperty, FloorValues);
			// changeSpaceScale(model,PlotFloorIndividual);
		}
		ArrayList<OWLIndividual> modelIndividualsOnGround1 = getOWLIndividual(modelValuesGround1, model);
		ArrayList<OWLIndividual> modelIndividualsActiveOnGround1 = getOWLIndividual(modelActiveGround1, model);
		ArrayList<OWLIndividual> modelIndividualsInAir1 = getOWLIndividual(modelValuesAir1, model);
		ArrayList<OWLIndividual> modelIndividualsInHalfAir1 = getOWLIndividual(modelValuesHalfAir1, model);

		ArrayList<OWLIndividual> modelIndividualsOnGround0 = getOWLIndividual(modelValuesGround0, model);
		ArrayList<OWLIndividual> modelIndividualsActiveOnGround0 = getOWLIndividual(modelActiveGround0, model);
		ArrayList<OWLIndividual> modelIndividualsInAir0 = getOWLIndividual(modelValuesAir0, model);
		ArrayList<OWLIndividual> modelIndividualsInHalfAir0 = getOWLIndividual(modelValuesHalfAir0, model);

		ArrayList<OWLIndividual> modelIndividualsFloor = getOWLIndividual(FloorValues, model);

		int count = 0;
		int countFrom = 0;
		if (!modelValuesGround.isEmpty()) {
			countFrom = count + 1;
			count = JenaMethod.setNumberToAddModel(modelIndividualsOnGround1, modelIndividualsOnGround0, model,
					PlotGroundIndividual, 0, topicName);// ����ӵ�ʵ�����modelID
			changeSpaceScale(model, PlotGroundIndividual, countFrom, count);// ��ȡ��AddModelRelated
		}
		if (!modelActiveGround.isEmpty()) {
			countFrom = count + 1;
			count = JenaMethod.setNumberToAddModel(modelIndividualsActiveOnGround1, modelIndividualsActiveOnGround0,
					model, PlotActiveGroundIndividual, count, topicName);// ����ӵ�ʵ�����modelID
			changeSpaceScale(model, PlotActiveGroundIndividual, countFrom, count);
		}

		if (!modelValuesAir.isEmpty()) {
			countFrom = count + 1;
			count = JenaMethod.setNumberToAddModel(modelIndividualsInAir1, modelIndividualsInAir0, model,
					PlotAirIndividual, count, topicName);// ����ӵ�ʵ�����modelID
			changeSpaceScale(model, PlotAirIndividual, countFrom, count);
		}

		if (!modelValuesHalfAir.isEmpty()) {
			countFrom = count + 1;
			count = JenaMethod.setNumberToAddModel(modelIndividualsInHalfAir1, modelIndividualsInHalfAir0, model,
					PlotAirIndividual, count, topicName);// ����ӵ�ʵ�����modelID
			changeSpaceScale(model, PlotHalfAirIndividual, countFrom, count);
		}
		if (!FloorValues.isEmpty()) {
			countFrom = count + 1;
			count = JenaMethod.setNumberToAddModel(new ArrayList(), modelIndividualsFloor, model, PlotFloorIndividual,
					count, topicName);
			changeSpaceScale(model, PlotFloorIndividual, countFrom, count);
		}
		return model;
	}

	/**
	 * ��String ת��OWLIndividual
	 * 
	 */

	public ArrayList<OWLIndividual> getOWLIndividual(ArrayList<String> modelLists, OWLModel model) {
		OWLIndividual modelIndividual = null;
		ArrayList<OWLIndividual> modelIndividuals = new ArrayList();
		for (int i = 0; i < modelLists.size(); i++) {

			modelIndividual = model.getOWLIndividual(modelLists.get(i));
			modelIndividuals.add(modelIndividual);
		}

		return modelIndividuals;
	}

	/**
	 * ��space�л��Ӧ�÷��������width and
	 * depth,Ȼ���жϿռ�Ĵ�С���������ռ�Ĵ�С������scaleֵ
	 * 
	 * @param hasPutObjectProperty
	 * @version 2014.10.11
	 */
	public void changeSpaceScale(OWLModel model, OWLIndividual individual, int countFrom, int countTo) {
		OWLNamedClass AddModelRelated = model.getOWLNamedClass("AddModelRelated");
		OWLDatatypeProperty addmodelNum = model.getOWLDatatypeProperty("addModelNumber");
		OWLObjectProperty hasModelName = model.getOWLObjectProperty("hasModelName");
		OWLObjectProperty hasPutObjectInSpace = model.getOWLObjectProperty("hasPutObjectInSpace");
		OWLDatatypeProperty depthP = model.getOWLDatatypeProperty("Depth");
		OWLDatatypeProperty widthP = model.getOWLDatatypeProperty("Width");
		OWLDatatypeProperty scalex = model.getOWLDatatypeProperty("spacescalex");// width
		OWLDatatypeProperty scalez = model.getOWLDatatypeProperty("spacescalez");// depth
		String depth = (String) individual.getPropertyValue(depthP);
		String width = (String) individual.getPropertyValue(widthP);
		String obInSpacedepth = "";
		String obInSpacewidth = "";
		float allObjDepth = 0;
		float allObjWidth = 0;
		for (int i = countFrom; i <= countTo; i++) {
			OWLIndividual value = model.getOWLIndividual("addModelID" + i);
			System.out.println("addModelId= " + value.getBrowserText());
			OWLIndividual modelss = (OWLIndividual) value.getPropertyValue(hasModelName);
			String modelName = modelss.getBrowserText();
			Object modelName1 = value.getPropertyValue(addmodelNum);

			if (!modelName1.equals("null") || modelName1 != null) {
				int modelNum = Integer.parseInt((String) (value.getPropertyValue(addmodelNum)));
				System.out.println("changeSpaceScalemodelName=" + modelName + "modelNum" + modelNum);
				OWLIndividual modelIndiv = model.getOWLIndividual(modelName);
				obInSpacedepth = (String) (modelIndiv.getPropertyValue(depthP));
				obInSpacewidth = String.valueOf(modelIndiv.getPropertyValue(widthP));
				allObjDepth += (Float.parseFloat((obInSpacedepth)) * modelNum);
				allObjWidth += (Float.parseFloat((obInSpacewidth)) * modelNum);
			}

		}
		/*
		 * Collection
		 * ObjectInSpace=individual.getPropertyValues(hasPutObjectInSpace);
		 * for(Iterator it=ObjectInSpace.iterator();it.hasNext();){ String
		 * ob=it.next().toString(); OWLIndividual value= model.getOWLIndividual(
		 * ob); System.out.println("objectInSpace:"+value.getBrowserText());
		 * obInSpacedepth=(String)(value.getPropertyValue(depthP));
		 * obInSpacewidth=String.valueOf( value.getPropertyValue(widthP));
		 * allObjDepth+=Float.parseFloat((obInSpacedepth));
		 * allObjWidth+=Float.parseFloat((obInSpacewidth)); }
		 * System.out.println("depthֵ"+depth);
		 */
		if (allObjDepth > Integer.parseInt(depth)) {

			individual.setPropertyValue(scalez, String.valueOf((allObjDepth) / Integer.parseInt(depth) + 0.1));
			System.out.println("SpaceScale=" + String.valueOf((allObjDepth) / Integer.parseInt(depth) + 0.1));
		}
		if (allObjWidth > Integer.parseInt(width)) {
			individual.setPropertyValue(scalex, String.valueOf((allObjWidth) / Integer.parseInt(width) + 0.1));
			System.out.println("SpaceScalex=" + String.valueOf((allObjWidth) / Integer.parseInt(width) + 0.1));
		}
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

	public static void main(String args[]) throws OntologyLoadException {
		PlotAddModelToMa p = new PlotAddModelToMa();
		System.gc();
		System.out.println("1");
		String url = "file:///C:/ontologyOWL/sumoOWL2/sumo_phone3.owl";
		// ͨ��url���owlģ��
		OWLModel model = createOWLFile1(url);

		System.out.println("ma");

		// p.changeSpaceScale(model,
		// model.getOWLIndividual("SP_BalletDanceActionPlot_OutOnLand"),3);
	}

}
