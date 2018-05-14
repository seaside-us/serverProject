package protege;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
//import org.jdom.Document;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLFactoryException;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;

enum State {
	/** ������û�����ģ�� Ĭ������� **/
	NOTHING,
	/** �����еĳ���Ϊ ����������Ե����ı������� **/
	DEFAULT,
	/** ֪ʶ����û����Ӧ�Ĺ���תΪ��һ��������滮 **/
	NO_RULE,
	/** ֪ʶ��������Ӧ�Ĺ��򣬲�ִ�� **/
	EXEC_RULE_SUCESS,
	/** ֪ʶ�����й�����ִ��ʧ�� **/
	EXEC_RULE_FAIL;
}

public class CameraToXML {

	private final String PREFIX_OWL = "p13:";
	/** Define the global classify prefix of camera ������camera ��صĹ����ǰ׺���Լ����� */
	private final String CameraSWRL_HLCP_ = "CameraSWRL_HLCP_";// camera
																// primitives
	private final String CameraSWRL_Shot_ = "CameraSWRL_Shot_";// specific shot
	/** ��������Թ滮��ִ�н������ö���ַ��� **/
	private static String CameraADLExeState = "";
	private static boolean CameraForExpression = false;

	private final double SHORT = 1 / 4.0;
	private final double MEDIUM = 2 / 4.0;
	private final double LONG = 3 / 4.0;
	private final double ALL = 1.0;

	private Logger logger = Logger.getLogger(CameraToXML.class.getName());

	private int spaceType = 0;// ���ÿռ�����

	private OWLModel owlModel;
	private int maFrame;

	public int getSpaceType() {
		return spaceType;
	}

	public void setSpaceType(int spaceType) {
		this.spaceType = spaceType;
	}

	static Random random = new Random(System.currentTimeMillis());

	/**
	 * ��������Բ��ֵ������
	 * 
	 * @param owlModel
	 * @param maName
	 * @param doc
	 * @return �������camera�����doc
	 */
	public Document CreateCamera(OWLModel owlModel, String maName, Document doc) {
		logger.info("===���������ʼ===");
		this.owlModel = owlModel;
		if (maName.equals("nothing.ma")) {
			return doc;
		}
		int bgtype = 0;
		OWLIndividual ma = owlModel.getOWLIndividual(maName);
		OWLDatatypeProperty maframenumber = owlModel.getOWLDatatypeProperty("maFrameNumber");
		OWLDatatypeProperty mabgtype = owlModel.getOWLDatatypeProperty("backgroundPictureType");
		// maFrame:1���ȴ�ADL���ȡ����û��:2����ȡ֪ʶ��
		int frameNumInAdl = this.getMaFrameInADL(doc);
		if (frameNumInAdl != 0) {
			this.maFrame = frameNumInAdl;
		} else {
			this.maFrame = (Integer) ma.getPropertyValue(maframenumber);// 300
		}

		bgtype = (Integer) ma.getPropertyValue(mabgtype);// 3
		// if (bgtype != 3 && bgtype != 0 && bgtype != 1) //
		// 0����;1�������������emptyPlot.ma���ó�����bgtype=1��;2 mm��������;3���ⳡ����
		// return doc;
		// �������������������ã�������δ��ȡ
		if (bgtype == 1 || bgtype == 2) {// 20180315 ����happy_mm���Ƴ���
			logger.info("===bgscene camera plan start===");
			if (isReadyBgSceneCamera(maName)) {
				logger.info("===execute bgscene camera plan===");
				printRule(doc, "bgSceneCamera", 1, 1, this.maFrame, 1, this.maFrame);
			}
			// int rantemp = random.nextInt(10);
			// if (rantemp > 6) {
			// printRule(doc, "newCameraA", 1, 1, this.maFrame, 1,
			// this.maFrame);
			// } else {
			// int tmp1 = this.maFrame / 2;
			// int rantemp2 = tmp1 + (int) (Math.random() * (this.maFrame / 3));
			// printRule(doc, "newCameraA", 1, 1, rantemp2, 1, rantemp2);
			// printRule(doc, "newCameraA", 0, rantemp2 + 1, this.maFrame,
			// rantemp2 + 1, this.maFrame);
			// }
			logger.info("===bgscene camera plan end===");
			return doc;
		}
		Element root = doc.getRootElement();
		Element name = root.element("maName");

		ArrayList<SceneSpace> listall = Readxml.getAddinfo(name);

		int startFrame = 1, endFrame = this.maFrame;
		if (listall.size() == 0)
			return doc;
		else {
			ArrayList<ADLRule> shots = getADLRulesFromOWL(0, this.maFrame, doc);
			if (bgtype == 3) // ���ⳡ��
				doc = NewCameraPlan(shots, owlModel, "newCameraA", doc, listall, 1, this.maFrame);
			else { // ���ڳ���
				ArrayList<String> spacelist = getSpaceList(listall);
				// ArrayList<String> groundSpaceList=new ArrayList<String>();
				ArrayList<String> targetSpaceList = new ArrayList<String>();
				targetSpaceList = getTargetInSpaceList(listall);
				if (shots != null && shots.size() != 0) {
					doc = NewCameraPlan(shots, owlModel, "newCameraA", doc, listall, 1, this.maFrame);// ��Ҫ���ݵ���֪ʶ���Ƿ��ƶϳ�shots�������Ǹ���
				} else
					SpaceCameraPlan(shots, listall, spacelist, targetSpaceList, owlModel, doc, startFrame, endFrame);
			}
		}
		logger.info("===������������===");
		return doc;
	}

	/**
	 * ��ӡ����
	 * 
	 * @param doc
	 * @param cameraName
	 * @param isStart
	 * @param shotPlan
	 * @param targetIDs
	 * @param startPitch
	 * @param endPitch
	 * @param startRotate
	 * @param endRotate
	 * @param startSceneType
	 * @param endSceneType
	 * @param startframe
	 * @param endframe
	 * @param startinall
	 * @param endinall
	 * @return
	 */
	public Document printRule(Document doc, String cameraName, int isStart, String shotPlan,
			ArrayList<String> targetIDs, ArrayList<String> targets, String startPitch, String endPitch,
			String startRotate, String endRotate, String startSceneType, String endSceneType, int startframe,
			int endframe, int startinall, int endinall) {
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "SetCamera");
		ruleName.addAttribute("CameraName", cameraName);
		if (targetIDs.size() > 1) {
			ruleName.addAttribute("Range", "all");
		} else {
			ruleName.addAttribute("Range", "single");
		}

		ruleName.addAttribute("isStart", Integer.toString(isStart));
		ruleName.addAttribute("depthOfField", getDepthofField(targets.size(), startPitch, isStart, shotPlan));
		ruleName.addAttribute("shotPlan", shotPlan);
		String targetid = "";
		String targetname = "";

		for (int i = 0; i < targetIDs.size(); i++) {
			targetid = targetid + targetIDs.get(i) + " ";
			targetname = targetname + targets.get(i) + " ";
		}

		// ///////////////////////////��������ж�////////////////////////////
		
		int indexExpressionModel = hasExpressAndOneModel(targets, doc);
		if (-1 != indexExpressionModel) {// �����ں��б��������������
			targetid = targetIDs.get(indexExpressionModel);
			targetname = targets.get(indexExpressionModel);
			CameraForExpression = true;
		}
		// ///////////////////////////��������жϽ���//////////////////////////
		ruleName.addAttribute("usedModelID", targetid);
		ruleName.addAttribute("target", targetname);

		if (startPitch != null || startPitch != "")
			ruleName.addAttribute("startPitch", startPitch);
		ruleName.addAttribute("endPitch", endPitch);
		if (startRotate != null || startRotate != "")
			ruleName.addAttribute("startYaw", startRotate);
		ruleName.addAttribute("endYaw", endRotate);
		if (startSceneType != null || startSceneType != "")
			ruleName.addAttribute("startShotType", startSceneType);
		ruleName.addAttribute("endShotType", endSceneType);
		ruleName.addAttribute("startframe", Integer.toString(startframe));
		ruleName.addAttribute("endframe", Integer.toString(endframe));
		ruleName.addAttribute("startinall", Integer.toString(startframe));
		ruleName.addAttribute("endinall", Integer.toString(endframe));
		// HLCPPlan
		// ruleName.addAttribute("HLCPPlan", Integer.toString(endframe));
		return doc;

	}

	public Document printRule(Document doc, ADLRule shot) {
		System.out.println("____________________________________________________________________________");
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "SetCamera");
		ruleName.addAttribute("CameraName", shot.getShotName());
		ruleName.addAttribute("Range", "all");
		if (shot.getOrder() != -1 && shot.getOrder() == 1)// 20180104,ֻ���ڵ�һ����ͷ�Ŀ�ʼʱ��1��������Ϊ0�������еľ�ͷ����������
			ruleName.addAttribute("isStart", "1");
		else
			ruleName.addAttribute("isStart", "0");
		ruleName.addAttribute("depthOfField", "focus");
		ruleName.addAttribute("HLCP", shot.getHlcp());// ���Ǽ�¼
		ArrayList<String> models = shot.getTarget();
		// ///////////�ڴ��ж϶���ͷ������Ŀ���Ƿ��� ��λ�ƵĶ���////////////////
		// ȷ���Ƕ���ͷ
		if (shot.getShotPlan().equalsIgnoreCase("Fix")) {
			// ȷ����λ�ƶ���
			if (moveActionInModel(models, doc)) {
				shot.setShotPlan("Follow");
			}
		}
		// ///////////////////////////λ�ƶ����жϽ���////////////////////////////

		ruleName.addAttribute("shotPlan", shot.getShotPlan());
		String targetid = "";
		String targetname = "";

		ArrayList<String> targetidsInOwl = new ArrayList<String>();
		ArrayList<String> targetnameInOwl = new ArrayList<String>();
		if (targetidsInOwl != null && targetnameInOwl != null && targetidsInOwl.size() == targetnameInOwl.size()) {
			// ///////////////////////////��������ж�////////////////////////////
			
			int indexExpressionModelOWL = hasExpressAndOneModel(models, doc);
			if (-1 != indexExpressionModelOWL) {// �����ں��б��������������
				targetidsInOwl.add(shot.getUsedModelID().get(indexExpressionModelOWL));
				targetnameInOwl.add(shot.getTarget().get(indexExpressionModelOWL));
			} else {
				targetidsInOwl = shot.getUsedModelID();
				targetnameInOwl = shot.getTarget();
			}
			// ///////////////////////////��������жϽ���//////////////////////////
			for (int i = 0; i < targetidsInOwl.size(); i++) {
				targetid = targetid + targetidsInOwl.get(i) + " ";
				targetname = targetname + targetnameInOwl.get(i) + " ";
			}
		} else {
			logger.debug("printRule() function has different nums in targetid and targetname");
		}

		String startPitch = shot.getStartPitch();
		String endPitch = shot.getEndPitch();
		String startRotate = shot.getStartYaw();
		String endRotate = shot.getEndYaw();
		String startSceneType = shot.getStartShotType();
		String endSceneType = shot.getEndShotType();
		int startframe = shot.getStartframe();
		int endframe = shot.getEndframe();

		if (startPitch != null || startPitch != "")
			ruleName.addAttribute("startPitch", startPitch);
		ruleName.addAttribute("endPitch", endPitch);
		if (startRotate != null || startRotate != "")
			ruleName.addAttribute("startYaw", startRotate);
		ruleName.addAttribute("endYaw", endRotate);
		if (startSceneType != null || startSceneType != "")
			ruleName.addAttribute("startShotType", startSceneType);
		ruleName.addAttribute("endShotType", endSceneType.toString());
		ruleName.addAttribute("startframe", String.valueOf(startframe));
		ruleName.addAttribute("endframe", String.valueOf(endframe));
		ruleName.addAttribute("startinall", String.valueOf(startframe));
		ruleName.addAttribute("endinall", String.valueOf(endframe));
		// HLCPPlan
		// ruleName.addAttribute("HLCPPlan", Integer.toString(endframe));
		logger.info(shot.toString());
		return doc;

	}

	public Document printRule(Document doc, String cameraName, int isStart, int startframe, int endframe,
			int startinall, int endinall) {
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "SetCamera");
		ruleName.addAttribute("CameraName", cameraName);
		ruleName.addAttribute("isStart", Integer.toString(isStart));
		ruleName.addAttribute("startframe", Integer.toString(startframe));
		ruleName.addAttribute("endframe", Integer.toString(endframe));
		ruleName.addAttribute("startinall", Integer.toString(startframe));
		ruleName.addAttribute("endinall", Integer.toString(endframe));
		return doc;
	}

	public String ShootingScaleword(int type) {
		switch (type) {
		case 0:
			return "high";
		case 1:
			return "straight";
		case 2:
			return "low";
		default:
			return "auto";
		}
	}

	/**
	 * doc2XmlFile ��Document���󱣴�Ϊһ��xml�ļ�������
	 * 
	 * @return true:����ɹ� flase:ʧ��
	 * @param filename
	 *            ������ļ���
	 * @param document
	 *            ��Ҫ�����document����
	 */
	public boolean doc2XmlFile(Document document, String filename) {
		boolean flag = true;
		try {
			/* ��document�е�����д���ļ��� */
			// Ĭ��ΪUTF-8��ʽ��ָ��Ϊ"GB2312"
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("GB2312");
			XMLWriter writer = new XMLWriter(new FileWriter(new File(filename)), format);
			writer.write(document);
			writer.close();
		} catch (Exception ex) {
			flag = false;
			ex.printStackTrace();
			System.out.println("doc2XmlFile����д��ʧ��");
		}
		return flag;
	}

	/**
	 * �������ģ����ѡȡ�������ģ��
	 * 
	 * @param list
	 * @return
	 */
	public ArrayList<SceneSpace> getPeopleList(ArrayList<SceneSpace> list) {//
		ArrayList<SceneSpace> peoplelist = new ArrayList<SceneSpace>();
		for (int i = 0; i < list.size(); i++) {
			if ("people" == (list.get(i)).getType())
				peoplelist.add(list.get(i));
		}
		return peoplelist;
	}

	/**
	 * �������ģ����ѡȡģ��model���ģ��
	 * 
	 * @param list
	 * @return
	 */
	public ArrayList<SceneSpace> getModelList(ArrayList<SceneSpace> list) {
		ArrayList<SceneSpace> modellist = new ArrayList<SceneSpace>();
		for (int i = 0; i < list.size(); i++) {
			if ("model" == (list.get(i)).getType())
				modellist.add(list.get(i));
		}
		return modellist;
	}

	/**
	 * ��ȡlist�е����п��ÿռ�
	 * 
	 * @param list
	 * @return
	 */
	public ArrayList<String> getSpaceList(ArrayList<SceneSpace> list) {
		ArrayList<String> spacelist = new ArrayList<String>();
		logger.info("getSpaceList�����еõ�spacelist��");
		for (int i = 0; i < list.size(); i++) {
			if (!isStrinList(list.get(i).getSpname(), spacelist)) {
				spacelist.add(list.get(i).getSpname());
				logger.info(list.get(i).getSpname() + "    ");
			}
		}
		return spacelist;
	}

	/**
	 * �ж�ĳһ���ַ����Ƿ���list ������
	 * 
	 * @param str
	 * @param list
	 * @return
	 */
	public boolean isStrinList(String str, ArrayList<String> list) {
		for (String strtemp : list) {
			if (str == strtemp || str.equals(strtemp)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * ����������滮 ��������Ϊ����������adl��ӵ�ģ��
	 * 
	 * @param doc
	 * @param listAll
	 * @param frameAll
	 * @return
	 */
	public Document cameraPlan(OWLModel owlModel, String cameraName, Document doc, ArrayList<SceneSpace> listAll,
			int startFrame, int endFrame) {
		try {
			ArrayList<SceneSpace> targetIDs = getTargetID(listAll, 1);
			ArrayList<SceneSpace> unTargetIDs = getTargetID(listAll, 0);
			int listAllSize = listAll.size();
			int targetIDSize = targetIDs.size();
			int unTargetIDSize = unTargetIDs.size();

			ArrayList<SceneSpace> maxIndexId = getMaxIndexID(listAll);
			ArrayList<String> spaceList = getSpaceList(listAll);
			String groundSpace = "", airSpace = "", otherSpace = "";
			for (int i = 0; i < spaceList.size(); i++) {
				if (getSpaceClass(owlModel, spaceList.get(i)) == 1) {
					groundSpace += spaceList.get(i);
				} else if (getSpaceClass(owlModel, spaceList.get(i)) == 2) {
					airSpace += spaceList.get(i);
				} else {
					otherSpace += spaceList.get(i);
				}
			}
			ArrayList<SceneSpace> groundList = getSpaceScene(listAll, groundSpace);
			ArrayList<SceneSpace> airList = getSpaceScene(listAll, airSpace);

			if (listAllSize < 5) {

				if (targetIDSize > 0 && unTargetIDSize > 0) {
					int i = random.nextInt(2);
					if (i == 1)
						doc = Push(doc, cameraName, listAll, startFrame, endFrame, startFrame, endFrame);
					else
						doc = Pull(doc, cameraName, listAll, startFrame, endFrame, startFrame, endFrame);
				} else if (unTargetIDSize == 0) {
					int i = random.nextInt(2);
					if (i == 1)
						doc = Push(doc, cameraName, listAll, startFrame, endFrame, startFrame, endFrame);
					else
						doc = Pull(doc, cameraName, listAll, startFrame, endFrame, startFrame, endFrame);
				} else if (targetIDSize == 0) {
					doc = Rotate(doc, cameraName, listAll, startFrame, endFrame, startFrame, endFrame);
				} else {
					doc = Complex(doc, cameraName, listAll, startFrame, endFrame, startFrame, endFrame);
				}
			} else if (listAllSize < 10) {
				if (targetIDSize > 0 && unTargetIDSize > 0) {
					int i = random.nextInt(2);
					if (i == 1)
						doc = Push(doc, cameraName, targetIDs, startFrame, endFrame, startFrame, endFrame);
					else
						doc = Pull(doc, cameraName, targetIDs, startFrame, endFrame, startFrame, endFrame);
				} else if (targetIDSize > 0) {
					doc = Rotate(doc, cameraName, targetIDs, startFrame, endFrame, startFrame, endFrame);
				} else {
					doc = Rotate(doc, cameraName, listAll, startFrame, endFrame, startFrame, endFrame);
				}
			} else {
				if (groundList.size() > 0 && airList.size() > 0) {
					int i = random.nextInt(2);
					if (i == 1)
						doc = Push(doc, cameraName, groundList, startFrame, (endFrame - startFrame) / 2 + startFrame,
								startFrame, (endFrame - startFrame) / 2 + startFrame);
					else
						doc = Pull(doc, cameraName, groundList, startFrame, (endFrame - startFrame) / 2 + startFrame,
								startFrame, (endFrame - startFrame) / 2 + startFrame);
					doc = Fix(doc, cameraName, airList, (endFrame - startFrame) / 2 + startFrame + 1, endFrame,
							(endFrame - startFrame) / 2 + startFrame + 1, endFrame);
				} else if (groundList.size() > 0) {
					if (targetIDSize > 0 && unTargetIDSize > 0) {
						int i = random.nextInt(2);
						if (i == 1)
							doc = Push(doc, cameraName, targetIDs, startFrame,
									(endFrame - startFrame) / 2 + startFrame, startFrame, (endFrame - startFrame) / 2
											+ startFrame);
						else
							doc = Pull(doc, cameraName, targetIDs, startFrame,
									(endFrame - startFrame) / 2 + startFrame, startFrame, (endFrame - startFrame) / 2
											+ startFrame);
						doc = Rotate(doc, cameraName, unTargetIDs, (endFrame - startFrame) / 2 + startFrame + 1,
								endFrame, (endFrame - startFrame) / 2 + startFrame + 1, endFrame);
					} else if (targetIDSize > 0) {
						doc = Rotate(doc, cameraName, targetIDs, startFrame, (endFrame - startFrame) / 2 + startFrame,
								startFrame, (endFrame - startFrame) / 2 + startFrame);
						int i = random.nextInt(2);
						if (i == 1)
							doc = Push(doc, cameraName, maxIndexId, (endFrame - startFrame) / 2 + startFrame + 1,
									endFrame, (endFrame - startFrame) / 2 + startFrame + 1, endFrame);
						else
							doc = Pull(doc, cameraName, maxIndexId, (endFrame - startFrame) / 2 + startFrame + 1,
									endFrame, (endFrame - startFrame) / 2 + startFrame + 1, endFrame);
					} else {
						int i = random.nextInt(2);
						if (i == 1)
							doc = Push(doc, cameraName, airList, startFrame, (endFrame - startFrame) / 2 + startFrame,
									startFrame, (endFrame - startFrame) / 2 + startFrame);
						else
							doc = Pull(doc, cameraName, airList, startFrame, (endFrame - startFrame) / 2 + startFrame,
									startFrame, (endFrame - startFrame) / 2 + startFrame);
						doc = Fix(doc, cameraName, maxIndexId, (endFrame - startFrame) / 2 + startFrame + 1, endFrame,
								(endFrame - startFrame) / 2 + startFrame + 1, endFrame);
					}
				} else {
					doc = Complex(doc, cameraName, listAll, startFrame, endFrame, startFrame, endFrame);
				}
			}
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * �жϿ��ÿռ�ʵ�������Ǹ��� 1��OnGround��2��InAir��3��others��0��������
	 * 
	 * @param str
	 * @return
	 */
	public int getSpaceClass(OWLModel owlModel, String str) {
		try {

			Collection<?> spaceList = null;
			OWLIndividual indi = null;
			OWLNamedClass spaceClass = null;
			/**
			 * �жϸ�ʵ���Ƿ�����OnGround���µ�ʵ��
			 */
			spaceClass = owlModel.getOWLNamedClass("PlaneSceneSpaceOnGround");
			spaceList = spaceClass.getInstances(true);
			for (Iterator<?> itIns = spaceList.iterator(); itIns.hasNext();) {
				indi = (OWLIndividual) itIns.next();
				if (str.equals(indi.getBrowserText()))
					return 1;
				else
					continue;
			}
			spaceList.clear();
			/**
			 * �жϸ�ʵ���Ƿ�����InAir���µ�ʵ��
			 */
			spaceClass = owlModel.getOWLNamedClass("PlaneSceneSpaceInAir");
			spaceList = spaceClass.getInstances(true);
			for (Iterator<?> itIns = spaceList.iterator(); itIns.hasNext();) {
				indi = (OWLIndividual) itIns.next();
				if (str.equals(indi.getBrowserText()))
					return 2;
				else
					continue;
			}
			spaceList.clear();

			/**
			 * ȱ���ж�������
			 */
			return 3;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * ��ȡisTarget��Ŀ����
	 * 
	 * @param list
	 * @param isTarget
	 * @return
	 */
	public ArrayList<SceneSpace> getTargetID(ArrayList<SceneSpace> list, int isTarget) {

		ArrayList<SceneSpace> targetIDs = new ArrayList<SceneSpace>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getisTarget() == isTarget) {
				targetIDs.add(list.get(i));
			}
		}
		return targetIDs;
	}

	/**
	 * ��ȡ�ض����ÿռ��ϵ�Ŀ��(ģ��)��
	 * 
	 * @param list
	 * @param owlModel
	 * @param spaceType
	 *            =1��OnGround��2��InAir��3��others��0��������
	 * @return
	 */
	public ArrayList<SceneSpace> getSpaceScene(ArrayList<SceneSpace> list, String space) {

		ArrayList<SceneSpace> spaceIDs = new ArrayList<SceneSpace>();
		for (int i = 0; i < list.size(); i++) {
			if (space.indexOf(list.get(i).getSpname()) != -1) {// SELF:ȷ����ģ���ڸÿռ���
				spaceIDs.add(list.get(i));
			}
		}
		return spaceIDs;
	}

	/**
	 * ��ȡ��Ȩֵ����ID
	 * 
	 * @param list
	 * @return
	 */
	public ArrayList<SceneSpace> getMaxIndexID(ArrayList<SceneSpace> list) {
		ArrayList<SceneSpace> ssList = new ArrayList<SceneSpace>();
		SceneSpace maxIndexId = null;
		int max = 0;
		for (int i = 0; i < list.size(); i++) {
			if (max <= list.get(i).getIndex()) {
				// ��Ϊ�������ںţ���������������ģ�ͣ�����index���ʱ����ȡ�ڶ���ģ��ΪmaxID
				max = list.get(i).getIndex();
				maxIndexId = list.get(i);
			}
		}
		if (maxIndexId != null)
			ssList.add(maxIndexId);
		return ssList;
	}

	/**
	 * ��ȡ����Ϊpeople����model��Ŀ����
	 * 
	 * @param list
	 * @param type
	 * @return
	 */
	public ArrayList<SceneSpace> getTypeIDs(ArrayList<SceneSpace> list, String type) {
		ArrayList<SceneSpace> typeIDs = new ArrayList<SceneSpace>();
		for (int i = 0; i < list.size(); i++) {
			if (type.equals(list.get(i).getType())) {
				typeIDs.add(list.get(i));
			}
		}
		return typeIDs;
	}

	/**
	 * ��ȡĿ�����ID��
	 * 
	 * @param list
	 * @return
	 */
	public ArrayList<String> getIDs(ArrayList<SceneSpace> list) {
		ArrayList<String> ids = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			ids.add(list.get(i).getModelid());
		}
		return ids;
	}

	/*
	 * ��ȡĿ�����name�� ��Ҫ��doc�л��name
	 * 
	 * @param list
	 * 
	 * @return
	 */
	public ArrayList<String> getNamesByID(ArrayList<String> ids, Document doc) {
		ArrayList<String> targetnames = new ArrayList<String>();
		for (int i = 0; i < ids.size(); i++) {// ֪ʶ����ȡ��һ����ͷ�Ķ��Ŀ��id
			String modelIDOWL = ids.get(i);
			Element rootName = (Element) doc.getRootElement();
			Element name = rootName.element("maName");
			for (@SuppressWarnings("unchecked")
			Iterator<Element> it = name.elementIterator(); it.hasNext();) {
				Element rule = it.next();
				if (null != rule.attributeValue("ruleType") && rule.attributeValue("ruleType").equals("addToMa")) {
					String modelidADL = rule.attributeValue("addModelID");
					String modelnameADL = rule.attributeValue("addModel");
					if (modelidADL.equals(modelIDOWL) && modelnameADL != null) {
						targetnames.add(modelnameADL);
					}
				}
			}
		}
		return targetnames;
	}

	/**
	 * Fix�����ַ���ֻ�е���������
	 * 
	 * @param doc
	 * @param cameraName
	 * @param IDs
	 * @param startframe
	 * @param endframe
	 * @param startall
	 * @param endall
	 * @return
	 */
	public Document Fix(Document doc, String cameraName, ArrayList<SceneSpace> list, int startframe, int endframe,
			int startall, int endall) {
		ArrayList<String> IDs = getIDs(list);
		/**
		 * ��������Ŀ��ѡȡ�����Ƕ����� ���ѡ����ת�Ƕ������뾰��
		 */
		String idsPitch = getPitch(list);
		String startRotate = getRotate(list);
		String startSceneType = getSceneType(list, random.nextInt(33) % 4);
		/**
		 * �����ַ���Ϸ�ʽ
		 */
		doc = printRule(doc, cameraName, 1, "Fix", IDs, getTargetName(list, IDs), idsPitch, idsPitch, startRotate,
				startRotate, startSceneType, startSceneType, startframe, endframe, startall, endall);
		return doc;
	}

	/**
	 * Rotate������ϣ�Rotate��Fix-Rotate��Rotate-Fix
	 * 
	 * @param doc
	 * @param cameraName
	 * @param IDs
	 * @param startframe
	 * @param endframe
	 * @param startall
	 * @param endall
	 * @return
	 */
	public Document Rotate(Document doc, String cameraName, ArrayList<SceneSpace> list, int startframe, int endframe,
			int startall, int endall) {
		ArrayList<String> IDs = getIDs(list);
		/**
		 * ��������Ŀ��ѡȡ�����Ƕ����� ���ѡ����ת�Ƕ������뾰��
		 */
		String idsPitch = getPitch(list);
		String startRotate = getRotate(list);
		String endRotate = getRotate(list);
		String startSceneType = getSceneType(list, random.nextInt(33) % 4);
		/**
		 * �����ַ���Ϸ�ʽ
		 */
		int randomnum = random.nextInt(3);
		switch (randomnum) {
		case 0:
			doc = printRule(doc, cameraName, 1, "Rotate", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
					startRotate, endRotate, startSceneType, startSceneType, startframe, endframe, startall, endall);
			break;
		case 1:
			doc = printRule(doc, cameraName, 1, "Fix", IDs, getTargetName(list, IDs), idsPitch, idsPitch, startRotate,
					startRotate, startSceneType, startSceneType, startframe, startframe + (endframe - startframe) / 2,
					startall + startframe, startall + startframe + (endframe - startframe) / 2);
			doc = printRule(doc, cameraName, 0, "Rotate", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
					startRotate, endRotate, startSceneType, startSceneType, startframe + (endframe - startframe) / 2
							+ 1, endframe, startall + startframe + (endframe - startframe) / 2 + 1, startall + endframe);
			break;
		case 2:
			doc = printRule(doc, cameraName, 1, "Rotate", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
					startRotate, endRotate, startSceneType, startSceneType, startframe, startframe
							+ (endframe - startframe) / 2, startall + startframe, startall + startframe
							+ (endframe - startframe) / 2);
			doc = printRule(doc, cameraName, 0, "Fix", IDs, getTargetName(list, IDs), idsPitch, idsPitch, endRotate,
					endRotate, startSceneType, startSceneType, startframe + (endframe - startframe) / 2 + 1, endframe,
					startall + startframe + (endframe - startframe) / 2 + 1, startall + endframe);
			break;
		default:
			doc = printRule(doc, cameraName, 1, "Rotate", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
					startRotate, endRotate, startSceneType, startSceneType, startframe, endframe, startall, endall);
			break;
		}
		return doc;
	}

	/**
	 * Push������㣬Push��Fix-Push��Push-Fix
	 * 
	 * @param doc
	 * @param cameraName
	 * @param IDs
	 * @param startframe
	 * @param endframe
	 * @param startall
	 * @param endall
	 * @return
	 */
	public Document Push(Document doc, String cameraName, ArrayList<SceneSpace> list, int startframe, int endframe,
			int startall, int endall) {
		ArrayList<String> IDs = getIDs(list);
		ArrayList<String> maxIndexID = getIDs(getMaxIndexID(list));
		/**
		 * ��������Ŀ��ѡȡ�����Ƕ����� ���ѡ����ת�Ƕ������뾰��
		 */
		String idsPitch = getPitch(list);
		String maxIndexPitch = getPitch(getMaxIndexID(list));
		String listStartRotate = getRotate(list);
		String maxStartRotate = getRotate(getMaxIndexID(list));

		// 20161230,��֤��������仯������maybe,start==end
		int rantmp = random.nextInt(33);
		String startSceneType = getSceneType(list, rantmp % 2 + 2);
		String endSceneType = getSceneType(list, rantmp);
		/**
		 * �����ַ���Ϸ�ʽ
		 */
		int randomnum = random.nextInt(3);
		if (IDs.size() > 0)
			switch (randomnum) {
			case 0:
				doc = printRule(doc, cameraName, 1, "Push", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
						listStartRotate, listStartRotate, startSceneType, endSceneType, startframe, endframe, startall,
						endall);
				break;
			case 1:
				if (maxIndexID.size() > 0) {
					doc = printRule(doc, cameraName, 1, "Fix", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
							listStartRotate, listStartRotate, startSceneType, startSceneType, startframe, startframe
									+ (endframe - startframe) / 2, startall + startframe, startall + startframe
									+ (endframe - startframe) / 2);
					doc = printRule(doc, cameraName, 0, "Push", maxIndexID, getTargetName(list, maxIndexID),
							maxIndexPitch, maxIndexPitch, maxStartRotate, maxStartRotate, startSceneType, endSceneType,
							startframe + (endframe - startframe) / 2 + 1, endframe, startall + startframe
									+ (endframe - startframe) / 2 + 1, startall + endframe);
					break;
				}
			case 2:
				doc = printRule(doc, cameraName, 1, "Push", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
						listStartRotate, listStartRotate, startSceneType, endSceneType, startframe, startframe
								+ (endframe - startframe) / 2, startall + startframe, startall + startframe
								+ (endframe - startframe) / 2);
				doc = printRule(doc, cameraName, 0, "Fix", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
						listStartRotate, listStartRotate, endSceneType, endSceneType, startframe
								+ (endframe - startframe) / 2 + 1, endframe, startall + startframe
								+ (endframe - startframe) / 2 + 1, startall + endframe);
				break;
			default:
				doc = printRule(doc, cameraName, 1, "Push", maxIndexID, getTargetName(list, maxIndexID), maxIndexPitch,
						maxIndexPitch, maxStartRotate, maxStartRotate, startSceneType, endSceneType, startframe,
						endframe, startall, endall);
				break;
			}
		return doc;
	}

	/**
	 * Push������㣬Pull��Fix-Pull��Pull-Fix
	 * 
	 * @param doc
	 * @param cameraName
	 * @param list
	 * @param startframe
	 * @param endframe
	 * @param startall
	 * @param endall
	 * @return
	 */
	public Document Pull(Document doc, String cameraName, ArrayList<SceneSpace> list, int startframe, int endframe,
			int startall, int endall) {
		ArrayList<String> IDs = getIDs(list);
		ArrayList<String> maxIndexID = getIDs(getMaxIndexID(list));
		/**
		 * ��������Ŀ��ѡȡ�����Ƕ����� ���ѡ����ת�Ƕ������뾰��
		 */
		String idsPitch = getPitch(list);
		String maxIndexPitch = getPitch(getMaxIndexID(list));
		String listStartRotate = getRotate(list);
		String maxStartRotate = getRotate(getMaxIndexID(list));

		// 20161230,��֤��������仯������maybe,start==end
		int rantmp = random.nextInt(33);
		String startSceneType = getSceneType(list, rantmp % 2);
		String endSceneType = getSceneType(list, rantmp + 2);
		/**
		 * �����ַ���Ϸ�ʽ
		 */
		int randomnum = random.nextInt(3);
		if (IDs.size() > 0)
			switch (randomnum) {
			case 0:
				doc = printRule(doc, cameraName, 1, "Pull", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
						listStartRotate, listStartRotate, startSceneType, endSceneType, startframe, endframe, startall,
						endall);
				break;
			case 1:
				if (maxIndexID.size() > 0) {
					doc = printRule(doc, cameraName, 1, "Fix", maxIndexID, getTargetName(list, maxIndexID),
							maxIndexPitch, maxIndexPitch, maxStartRotate, maxStartRotate, startSceneType,
							startSceneType, startframe, startframe + (endframe - startframe) / 2,
							startall + startframe, startall + startframe + (endframe - startframe) / 2);
					doc = printRule(doc, cameraName, 0, "Pull", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
							listStartRotate, listStartRotate, startSceneType, endSceneType, startframe
									+ (endframe - startframe) / 2 + 1, endframe, startall + startframe
									+ (endframe - startframe) / 2 + 1, startall + endframe);
					break;
				}
			case 2:
				doc = printRule(doc, cameraName, 1, "Pull", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
						listStartRotate, listStartRotate, startSceneType, endSceneType, startframe, startframe
								+ (endframe - startframe) / 2, startall + startframe, startall + startframe
								+ (endframe - startframe) / 2);
				doc = printRule(doc, cameraName, 0, "Fix", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
						listStartRotate, listStartRotate, endSceneType, endSceneType, startframe
								+ (endframe - startframe) / 2 + 1, endframe, startall + startframe
								+ (endframe - startframe) / 2 + 1, startall + endframe);
				break;
			default:
				doc = printRule(doc, cameraName, 1, "Pull", maxIndexID, getTargetName(list, maxIndexID), maxIndexPitch,
						maxIndexPitch, maxStartRotate, maxStartRotate, startSceneType, endSceneType, startframe,
						endframe, startall, endall);
				break;
			}
		return doc;
	}

	/**
	 * Complex �������
	 * 
	 * @param doc
	 * @param cameraName
	 * @param list
	 * @param startframe
	 * @param endframe
	 * @param startall
	 * @param endall
	 * @return
	 */
	public Document Complex(Document doc, String cameraName, ArrayList<SceneSpace> list, int startframe, int endframe,
			int startall, int endall) {
		ArrayList<String> IDs = getIDs(list);
		ArrayList<String> maxIndexID = getIDs(getMaxIndexID(list));
		/**
		 * ��������Ŀ��ѡȡ�����Ƕ����� ���ѡ����ת�Ƕ������뾰��
		 */
		String idsPitch = getPitch(list);
		String maxIndexPitch = getPitch(getMaxIndexID(list));
		String listStartRotate = getRotate(list);
		String listEndRotate = getRotate(list);
		String maxStartRotate = getRotate(getMaxIndexID(list));
		String maxEndRotate = getRotate(getMaxIndexID(list));

		// 20161230,��֤��������仯������maybe start==end
		int rantmp = random.nextInt(33);
		int medium_status = rantmp % 2;
		String startSceneType = getSceneType(list, medium_status);
		String endSceneType = getSceneType(list, medium_status + 2);

		/**
		 * �����ַ���Ϸ�ʽ
		 */
		int randomnum = random.nextInt(5);
		if (IDs.size() > 0 && maxIndexID.size() > 0)
			switch (randomnum) {
			case 0:
				doc = printRule(doc, cameraName, 1, "Pull", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
						listStartRotate, listStartRotate, startSceneType, endSceneType, startframe, endframe, startall,
						endall);
				break;
			case 1:
				doc = printRule(doc, cameraName, 1, "Rotate", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
						listStartRotate, listEndRotate, startSceneType, startSceneType, startframe, startframe
								+ (endframe - startframe) / 2, startall + startframe, startall + startframe
								+ (endframe - startframe) / 2);
				doc = printRule(doc, cameraName, 0, "Pull", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
						listEndRotate, listEndRotate, startSceneType, endSceneType, startframe
								+ (endframe - startframe) / 2 + 1, endframe, startall + startframe
								+ (endframe - startframe) / 2 + 1, startall + endframe);
				break;
			case 2:
				doc = printRule(doc, cameraName, 1, "Rotate", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
						listStartRotate, listEndRotate, endSceneType, endSceneType, startframe, startframe
								+ (endframe - startframe) / 2, startall + startframe, startall + startframe
								+ (endframe - startframe) / 2);
				doc = printRule(doc, cameraName, 0, "Push", maxIndexID, getTargetName(list, maxIndexID), maxIndexPitch,
						maxIndexPitch, maxEndRotate, maxEndRotate, endSceneType, startSceneType, startframe
								+ (endframe - startframe) / 2 + 1, endframe, startall + startframe
								+ (endframe - startframe) / 2 + 1, startall + endframe);
				break;
			case 3:
				doc = printRule(doc, cameraName, 1, "Fix", maxIndexID, getTargetName(list, maxIndexID), maxIndexPitch,
						maxIndexPitch, maxStartRotate, maxStartRotate, startSceneType, startSceneType, startframe,
						startframe + (endframe - startframe) / 3, startall + startframe, startall + startframe
								+ (endframe - startframe) / 3);
				doc = printRule(doc, cameraName, 0, "Pull", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
						listStartRotate, listStartRotate, startSceneType, endSceneType, startframe
								+ (endframe - startframe) / 3 + 1, startframe + (endframe - startframe) * 2 / 3,
						startall + startframe + (endframe - startframe) / 3 + 1, startall + startframe
								+ (endframe - startframe) * 2 / 3);
				doc = printRule(doc, cameraName, 0, "Rotate", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
						listStartRotate, listEndRotate, endSceneType, endSceneType, startframe
								+ (endframe - startframe) * 2 / 3 + 1, endframe, startall + startframe
								+ (endframe - startframe) * 2 / 3 + 1, startall + endframe);
				break;
			case 4:
				doc = printRule(doc, cameraName, 1, "Fix", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
						listStartRotate, listStartRotate, endSceneType, endSceneType, startframe, startframe
								+ (endframe - startframe) / 3, startall + startframe, startall + startframe
								+ (endframe - startframe) / 3);
				doc = printRule(doc, cameraName, 0, "Push", maxIndexID, getTargetName(list, maxIndexID), maxIndexPitch,
						maxIndexPitch, maxStartRotate, maxStartRotate, endSceneType, startSceneType, startframe
								+ (endframe - startframe) / 3 + 1, startframe + (endframe - startframe) * 2 / 3,
						startall + startframe + (endframe - startframe) / 3 + 1, startall + startframe
								+ (endframe - startframe) * 2 / 3);
				doc = printRule(doc, cameraName, 0, "Rotate", maxIndexID, getTargetName(list, maxIndexID),
						maxIndexPitch, maxIndexPitch, maxStartRotate, maxEndRotate, startSceneType, startSceneType,
						startframe + (endframe - startframe) * 2 / 3 + 1, endframe, startall + startframe
								+ (endframe - startframe) * 2 / 3 + 1, startall + endframe);
				break;
			default:
				doc = printRule(doc, cameraName, 1, "Push", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
						listStartRotate, listStartRotate, endSceneType, startSceneType, startframe, endframe, startall,
						endall);
				break;
			}
		else {
			doc = printRule(doc, cameraName, 1, "Rotate", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
					listStartRotate, listEndRotate, endSceneType, endSceneType, startframe, startframe
							+ (endframe - startframe) / 2, startall + startframe, startall + startframe
							+ (endframe - startframe) / 2);
			doc = printRule(doc, cameraName, 0, "Push", maxIndexID, getTargetName(list, maxIndexID), maxIndexPitch,
					maxIndexPitch, maxEndRotate, maxEndRotate, endSceneType, startSceneType, startframe
							+ (endframe - startframe) / 2 + 1, endframe, startall + startframe
							+ (endframe - startframe) / 2 + 1, startall + endframe);

		}
		return doc;
	}

	/**
	 * ��������Ŀ��õ������Ƕ�����
	 * 
	 * @param list
	 * @return
	 */
	public String getPitch(ArrayList<SceneSpace> list) {

		ArrayList<String> spaceList = getSpaceList(list);
		String str = "";
		switch (spaceList.size()) {
		case 0:
			str = getPitch(0);// 0: lookdown
			break;
		case 1:
			if (list.size() == 1 && getTypeIDs(list, "people").size() == 1) {
				str = getPitch(random.nextInt(3));
			} else {
				str = getPitch(0);
			}
			break;
		default:
			str = getPitch(0);
		}
		int spType = getSpaceType();
		if (spType == 1) {
			int i = random.nextInt(3);
			str = getPitch(i);
		} else if (spType == 2) {
			int i = random.nextInt(2);
			str = getPitch(i + 1);
		}
		return str;
	}

	public String getPitch(int i) {
		String str = "";
		switch (i) {
		case 0:
			str = "lookdown";
			break;
		case 1:
			str = "forehand";
			break;
		case 2:
			str = "lookdown";// Ŀǰ��ȥlookup��20170602
			break;
		default:
			str = "lookdown";
		}
		return str;
	}

	/**
	 * ��������Ŀ����������ͣ������������ת�Ƕ�����
	 * 
	 * @param i
	 * @return
	 */
	public String getRotate(ArrayList<SceneSpace> list) {
		int listSize = list.size();
		String str = "forward";
		int i = random.nextInt(100);
		if (listSize > 1) {
			if (i <= 50)
				str = "forward";
			else if (i <= 70 && i > 50)
				str = "nearside";
			else if (i <= 90 && i > 70)
				str = "offside";
			else
				str = "backside";
		} else if (listSize == 1) {
			if ("people" == list.get(0).getType()) {
				if (i <= 80)
					str = "forward";
				else if (i <= 90 && i > 80)
					str = "nearside";
				else if (i <= 95 && i > 90)
					str = "offside";
				else
					str = "backside";
			} else {
				if (i <= 40)
					str = "forward";// -30�㵽30��
				else if (i <= 60 && i > 40)
					str = "nearside";
				else if (i <= 80 && i > 60)
					str = "offside";
				else
					str = "backside";
			}
		}
		return str;
	}

	// beforeDXF:public String getSceneType(int i)
	// DXF:public String getSceneType(ArrayList<SceneSpace> list,int
	// i)������ģ�����ж����㾰��
	public String getSceneType(ArrayList<SceneSpace> list, int i) {

		String str = "";
		if (getTypeIDs(list, "people").size() != 0) {
			// �������������������벻ͬ����
			switch (i) {
			case 1:
				str = "medium";
				break;
			case 2:
				str = "full";
				break;
			case 3:
				str = "establish";
				break;
			default:
				str = "full";
				break;
			}
		} else {
			switch (i) {
			case 0:
				str = "closeup";
				break;
			case 1:
				str = "medium";
				break;
			case 2:
				str = "full";
				break;
			case 3:
				str = "establish";
				break;
			}
		}
		return str;
	}

	public ArrayList<String> getTargetName(ArrayList<SceneSpace> listall, ArrayList<String> IDs) {
		ArrayList<String> targetname = new ArrayList<String>();
		for (int j = 0; j < IDs.size(); j++) {
			for (int i = 0; i < listall.size(); i++) {
				if (listall.get(i).getModelid() == IDs.get(j)) {
					targetname.add(listall.get(i).getModelname());
					break;
				}
			}
		}
		return targetname;
	}

	public String getDepthofField(int num, String pitch, int isStart, String shotPlan) {
		if ("Fix" == shotPlan || "Rotate" == shotPlan) {
			if (isStart == 1) {
				if (num != 1) {// ���㾰��Ƚ϶��ʱ�򣬲�����ʵת���ķ�ʽ������תʵ������ʵת��
					return "transform";
				} else {// ���㵥һ����ʱ�����ý��㷽ʽ�������㣬������Ŀ�괦����������������ģ��
					return "focus";
				}
			}

		}

		return "false";
	}

	/**
	 * һ��ȫ���㾵ͷ
	 * 
	 * @param doc
	 * @param cameraName
	 * @param list
	 * @param startframe
	 * @param endframe
	 * @param startall
	 * @param endall
	 * @return
	 */
	public Document FullShot(ArrayList<ADLRule> shots, Document doc, String cameraName, ArrayList<SceneSpace> list,
			int startframe, int endframe, int startall, int endall) {
		try {

			/**
			 * ��������������ʵ�HLCP��ͷ�����߹��򣻷������ִ��֮ǰ�����
			 */
			if (shots != null && shots.size() > 0) {
				if (shots.size() != 1) {// ��ͷ��������һ��ʱ����Ҫ�����ÿռ��������Ŀ��

					// ����ͷ������Ŀ�꣬��ģ�Ͱ����ÿռ�������ͷ
					ArrayList<String> tar = shots.get(0).getUsedModelID();
					HashMap<String, String> id_sp = getspByID(doc, tar);
					Set<String> sps = new HashSet<String>();
					for (String key : id_sp.values()) {
						sps.add(key);
					}

					ArrayList<String> sortedlist = new ArrayList<String>();
					for (String sp : sps) {
						String tmp = "";
						for (Map.Entry<String, String> entry : id_sp.entrySet()) {
							String key = entry.getKey();
							String value = entry.getValue();
							if (value.equals(sp)) {
								tmp = tmp + key + " ";
							}
							// sps.add(key);
						}
						tmp = tmp.trim();
						sortedlist.add(tmp);
					}

					int shotSize = shots.size();
					int spSize = sortedlist.size();
					for (int i = 0; i < shotSize; i++) {
						ADLRule shot = shots.get(i);
						String orig = sortedlist.get(i % spSize).trim();// ѭ��ȡ�����ܻ���ھ�ͷ�����������
						String[] splitedId = orig.split(" ");
						ArrayList<String> usedModelID = new ArrayList<String>();
						ArrayList<String> usedModelName = new ArrayList<String>();
						for (int j = 0; j < splitedId.length; j++) {
							usedModelID.add(splitedId[j]);
						}
						Collections.sort(usedModelID);
						shot.setUsedModelID(usedModelID);
						usedModelName = getNamesByID(usedModelID, doc);
						shot.setTarget(usedModelName);
						logger.info("actual plan,focus on camera targets:");
						doc = printRule(doc, shot);
					}
				} else
					// ��֪ʶ���Ƴ�ֻ��һ����ͷ�������м������ÿռ�
					for (int i = 0; i < shots.size(); i++) {// Ŀǰ��һ���ַ���ľ�ͷ�ǰ��������֡������ӡ
						ADLRule shot = shots.get(i);
						logger.info("actual plan,focus on camera targets:");
						doc = printRule(doc, shot);
					}
				return doc;
			} else {
				// ��֤�����������͡��������������
				int randnum = random.nextInt(4);
				switch (randnum) {
				case 0:
					doc = NewFix(doc, cameraName, list, startframe, endframe, startall, endall);
					break;
				case 1:
					doc = NewPull(doc, cameraName, list, startframe, endframe, startall, endall);
					break;
				case 2:
					doc = NewPush(doc, cameraName, list, startframe, endframe, startall, endall);
					break;
				case 3:
					doc = NewRotate(doc, cameraName, list, startframe, endframe, startall, endall);
					break;
				default:
					doc = Complex(doc, cameraName, list, startframe, endframe, startall, endall);
				}
			}

		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			logger.error(e.getMessage());
		}

		return doc;
	}

	/**
	 * �°�----����
	 * 
	 * @param doc
	 * @param cameraName
	 * @param list
	 * @param startframe
	 * @param endframe
	 * @param startall
	 * @param endall
	 * @return
	 */
	public Document NewPush(Document doc, String cameraName, ArrayList<SceneSpace> list, int startframe, int endframe,
			int startall, int endall) {

		ArrayList<SceneSpace> targetLists = getTargetID(list, 1);
		ArrayList<SceneSpace> unTargetLists = getTargetID(list, 0);
		int targetSize = targetLists.size();
		int unTargetSize = unTargetLists.size();
		/**
		 * ��������Ŀ��ѡȡ�����Ƕ����� ���ѡ����ת�Ƕ������뾰��
		 */
		String idsPitch = getPitch(list);
		String maxIndexPitch = getPitch(getMaxIndexID(list));
		String listStartRotate = getRotate(list);
		String maxStartRotate = getRotate(getMaxIndexID(list));

		// 20161230,��֤��������仯������maybe,start==end
		int rantmp = random.nextInt(33);
		int mudium_status = rantmp % 2;
		String startSceneType = getSceneType(list, mudium_status + 2);
		String endSceneType = getSceneType(list, mudium_status);

		ArrayList<String> IDs = getIDs(list);
		ArrayList<String> maxIndexID = getIDs(getMaxIndexID(list));
		ArrayList<String> targetIDs = getIDs(targetLists);
		ArrayList<String> unTargetIDs = getIDs(unTargetLists);

		logger.info("newpush��list��          ģ�͸�����" + list.size());
		logger.info("newpush��list��Ŀ��ģ�͸�����" + targetSize);

		if (targetSize > 0 && unTargetSize > 0) {
			doc = printRule(doc, cameraName, 1, "Fix", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
					listStartRotate, listStartRotate, startSceneType, startSceneType, startframe, startframe
							+ (endframe - startframe) / 2, startall + startframe, startall + startframe
							+ (endframe - startframe) / 2);
			doc = printRule(doc, cameraName, 0, "Push", targetIDs, getTargetName(list, targetIDs), maxIndexPitch,
					maxIndexPitch, maxStartRotate, maxStartRotate, startSceneType, endSceneType, startframe
							+ (endframe - startframe) / 2 + 1, endframe, startall + startframe
							+ (endframe - startframe) / 2 + 1, startall + endframe);
		} else if (targetSize > 0) {
			int randomnum = random.nextInt(5);
			if (randomnum < 2) {
				doc = printRule(doc, cameraName, 1, "Fix", targetIDs, getTargetName(list, targetIDs), idsPitch,
						idsPitch, listStartRotate, listStartRotate, startSceneType, startSceneType, startframe,
						startframe + (endframe - startframe) / 2, startall + startframe, startall + startframe
								+ (endframe - startframe) / 2);
				doc = printRule(doc, cameraName, 0, "Push", maxIndexID, getTargetName(list, maxIndexID), maxIndexPitch,
						maxIndexPitch, maxStartRotate, maxStartRotate, startSceneType, endSceneType, startframe
								+ (endframe - startframe) / 2 + 1, endframe, startall + startframe
								+ (endframe - startframe) / 2 + 1, startall + endframe);
			} else if (randomnum < 4) {
				doc = printRule(doc, cameraName, 1, "Fix", targetIDs, getTargetName(list, targetIDs), idsPitch,
						idsPitch, listStartRotate, listStartRotate, startSceneType, startSceneType, startframe,
						startframe + (endframe - startframe) / 2, startall + startframe, startall + startframe
								+ (endframe - startframe) / 2);
				doc = printRule(doc, cameraName, 0, "Push", targetIDs, getTargetName(list, targetIDs), maxIndexPitch,
						maxIndexPitch, maxStartRotate, maxStartRotate, startSceneType, endSceneType, startframe
								+ (endframe - startframe) / 2 + 1, endframe, startall + startframe
								+ (endframe - startframe) / 2 + 1, startall + endframe);

			} else {
				doc = printRule(doc, cameraName, 1, "Push", targetIDs, getTargetName(list, targetIDs), maxIndexPitch,
						maxIndexPitch, maxStartRotate, maxStartRotate, startSceneType, endSceneType, startframe,
						endframe, startall, endall);
			}
		} else {
			doc = printRule(doc, cameraName, 1, "Fix", unTargetIDs, getTargetName(list, unTargetIDs), idsPitch,
					idsPitch, listStartRotate, listStartRotate, startSceneType, startSceneType, startframe, startframe
							+ (endframe - startframe) / 2, startall + startframe, startall + startframe
							+ (endframe - startframe) / 2);
			doc = printRule(doc, cameraName, 0, "Push", maxIndexID, getTargetName(list, maxIndexID), maxIndexPitch,
					maxIndexPitch, maxStartRotate, maxStartRotate, startSceneType, endSceneType, startframe
							+ (endframe - startframe) / 2 + 1, endframe, startall + startframe
							+ (endframe - startframe) / 2 + 1, startall + endframe);

		}

		return doc;
	}

	/**
	 * �°�---����
	 * 
	 * @param doc
	 * @param cameraName
	 * @param list
	 * @param startframe
	 * @param endframe
	 * @param startall
	 * @param endall
	 * @return
	 */
	public Document NewPull(Document doc, String cameraName, ArrayList<SceneSpace> list, int startframe, int endframe,
			int startall, int endall) {
		ArrayList<SceneSpace> targetLists = getTargetID(list, 1);
		ArrayList<SceneSpace> unTargetLists = getTargetID(list, 0);
		int targetSize = targetLists.size();
		int unTargetSize = unTargetLists.size();
		logger.info("newpull��list��          ģ�͸�����" + list.size());
		logger.info("newpull��list��Ŀ��ģ�͸�����" + targetSize);
		/**
		 * ��������Ŀ��ѡȡ�����Ƕ����� ���ѡ����ת�Ƕ������뾰�� ��������
		 */
		String idsPitch = getPitch(list);
		String maxIndexPitch = getPitch(getMaxIndexID(list));

		String listStartRotate = getRotate(list);
		String maxStartRotate = getRotate(getMaxIndexID(list));

		// 20161230,��֤��������仯������maybe,start==end
		int rantmp = random.nextInt(33);
		int medium_status = rantmp % 2;
		String startSceneType = getSceneType(list, medium_status);
		String endSceneType = getSceneType(list, medium_status + 2);

		ArrayList<String> IDs = getIDs(list);
		ArrayList<String> maxIndexID = getIDs(getMaxIndexID(list));// ��������ģ�ͣ�����index���ʱ����ȡ�ڶ���ģ��ΪmaxID
		ArrayList<String> targetIDs = getIDs(targetLists);
		ArrayList<String> unTargetIDs = getIDs(unTargetLists);
		/**
		 * �����ַ���Ϸ�ʽ
		 */

		if (targetSize > 0 && unTargetSize > 0) {
			// ���ڷ�Ŀ��ģ�ͣ�����ѡ���ÿռ��ϵ�istartget=1��ģ�����㣬�ٸ����ÿռ��ϵ�����ģ��IDһ��pull��ͷ
			doc = printRule(doc, cameraName, 1, "Fix", targetIDs, getTargetName(list, targetIDs), maxIndexPitch,
					maxIndexPitch, maxStartRotate, maxStartRotate, startSceneType, startSceneType, startframe,
					startframe + (endframe - startframe) / 2, startall + startframe, startall + startframe
							+ (endframe - startframe) / 2);
			doc = printRule(doc, cameraName, 0, "Pull", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
					listStartRotate, listStartRotate, startSceneType, endSceneType, startframe
							+ (endframe - startframe) / 2 + 1, endframe, startall + startframe
							+ (endframe - startframe) / 2 + 1, startall + endframe);
		} else if (targetSize > 0) {// ������ģ��istartget=1
			int randomnum = random.nextInt(5);
			if (randomnum < 2) {
				doc = printRule(doc, cameraName, 1, "Fix", maxIndexID, getTargetName(list, maxIndexID), maxIndexPitch,
						maxIndexPitch, maxStartRotate, maxStartRotate, startSceneType, startSceneType, startframe,
						startframe + (endframe - startframe) / 2, startall + startframe, startall + startframe
								+ (endframe - startframe) / 2);
				doc = printRule(doc, cameraName, 0, "Pull", targetIDs, getTargetName(list, targetIDs), idsPitch,
						idsPitch, listStartRotate, listStartRotate, startSceneType, endSceneType, startframe
								+ (endframe - startframe) / 2 + 1, endframe, startall + startframe
								+ (endframe - startframe) / 2 + 1, startall + endframe);
			} else if (randomnum < 4) {
				doc = printRule(doc, cameraName, 1, "Fix", targetIDs, getTargetName(list, targetIDs), maxIndexPitch,
						maxIndexPitch, maxStartRotate, maxStartRotate, startSceneType, startSceneType, startframe,
						startframe + (endframe - startframe) / 2, startall + startframe, startall + startframe
								+ (endframe - startframe) / 2);
				doc = printRule(doc, cameraName, 0, "Pull", targetIDs, getTargetName(list, targetIDs), idsPitch,
						idsPitch, listStartRotate, listStartRotate, startSceneType, endSceneType, startframe
								+ (endframe - startframe) / 2 + 1, endframe, startall + startframe
								+ (endframe - startframe) / 2 + 1, startall + endframe);

			} else {
				doc = printRule(doc, cameraName, 1, "Pull", targetIDs, getTargetName(list, targetIDs), maxIndexPitch,
						maxIndexPitch, maxStartRotate, maxStartRotate, startSceneType, endSceneType, startframe,
						endframe, startall, endall);
			}
		} else {
			doc = printRule(doc, cameraName, 1, "Fix", maxIndexID, getTargetName(list, maxIndexID), maxIndexPitch,
					maxIndexPitch, maxStartRotate, maxStartRotate, startSceneType, startSceneType, startframe,
					startframe + (endframe - startframe) / 2, startall + startframe, startall + startframe
							+ (endframe - startframe) / 2);
			doc = printRule(doc, cameraName, 0, "Pull", unTargetIDs, getTargetName(list, unTargetIDs), idsPitch,
					idsPitch, listStartRotate, listStartRotate, startSceneType, endSceneType, startframe
							+ (endframe - startframe) / 2 + 1, endframe, startall + startframe
							+ (endframe - startframe) / 2 + 1, startall + endframe);

		}
		return doc;
	}

	/**
	 * �°�--��ת����
	 * 
	 * @param doc
	 * @param cameraName
	 * @param list
	 * @param startframe
	 * @param endframe
	 * @param startall
	 * @param endall
	 * @return
	 */
	public Document NewRotate(Document doc, String cameraName, ArrayList<SceneSpace> list, int startframe,
			int endframe, int startall, int endall) {
		ArrayList<SceneSpace> targetLists = getTargetID(list, 1);
		int targetSize = targetLists.size();
		/**
		 * ��������Ŀ��ѡȡ�����Ƕ����� ���ѡ����ת�Ƕ������뾰��
		 */
		String idsPitch = getPitch(list);
		String startRotate = getRotate(list);
		String endRotate = getRotate(list);
		String startSceneType = getSceneType(list, random.nextInt(33) % 4);

		ArrayList<String> IDs = getIDs(list);
		ArrayList<String> targetIDs = getIDs(targetLists);
		/**
		 * �����ַ���Ϸ�ʽ
		 */

		logger.info("newrotate��list��          ģ�͸�����" + list.size());
		logger.info("newrotate��list��Ŀ��ģ�͸�����" + targetSize);
		if (targetSize > 0) {
			int randomnum = random.nextInt(4);
			switch (randomnum) {
			case 0:
				boolean ismove0 = moveActionInModel(getTargetName(list, targetIDs), doc);
				if (!ismove0)
					doc = printRule(doc, cameraName, 1, "Rotate", targetIDs, getTargetName(list, targetIDs), idsPitch,
							idsPitch, startRotate, endRotate, startSceneType, startSceneType, startframe, endframe,
							startall, endall);
				else
					doc = printRule(doc, cameraName, 1, "Fix", targetIDs, getTargetName(list, targetIDs), idsPitch,
							idsPitch, startRotate, endRotate, startSceneType, startSceneType, startframe, endframe,
							startall, endall);
				break;
			case 1:
				doc = printRule(doc, cameraName, 1, "Fix", targetIDs, getTargetName(list, targetIDs), idsPitch,
						idsPitch, startRotate, startRotate, startSceneType, startSceneType, startframe, startframe
								+ (endframe - startframe) / 2, startall + startframe, startall + startframe
								+ (endframe - startframe) / 2);
				boolean ismove1 = moveActionInModel(getTargetName(list, targetIDs), doc);
				if (!ismove1)
					doc = printRule(doc, cameraName, 0, "Rotate", targetIDs, getTargetName(list, targetIDs), idsPitch,
							idsPitch, startRotate, endRotate, startSceneType, startSceneType, startframe
									+ (endframe - startframe) / 2 + 1, endframe, startall + startframe
									+ (endframe - startframe) / 2 + 1, startall + endframe);
				else
					doc = printRule(doc, cameraName, 0, "Fix", targetIDs, getTargetName(list, targetIDs), idsPitch,
							idsPitch, startRotate, endRotate, startSceneType, startSceneType, startframe
									+ (endframe - startframe) / 2 + 1, endframe, startall + startframe
									+ (endframe - startframe) / 2 + 1, startall + endframe);
				break;
			case 2:
				boolean ismove2 = moveActionInModel(getTargetName(list, targetIDs), doc);
				if (!ismove2)
					doc = printRule(doc, cameraName, 1, "Rotate", targetIDs, getTargetName(list, targetIDs), idsPitch,
							idsPitch, startRotate, endRotate, startSceneType, startSceneType, startframe, startframe
									+ (endframe - startframe) / 2, startall + startframe, startall + startframe
									+ (endframe - startframe) / 2);
				else
					doc = printRule(doc, cameraName, 1, "Fix", targetIDs, getTargetName(list, targetIDs), idsPitch,
							idsPitch, startRotate, endRotate, startSceneType, startSceneType, startframe, startframe
									+ (endframe - startframe) / 2, startall + startframe, startall + startframe
									+ (endframe - startframe) / 2);
				doc = printRule(doc, cameraName, 0, "Fix", targetIDs, getTargetName(list, targetIDs), idsPitch,
						idsPitch, endRotate, endRotate, startSceneType, startSceneType, startframe
								+ (endframe - startframe) / 2 + 1, endframe, startall + startframe
								+ (endframe - startframe) / 2 + 1, startall + endframe);
				break;
			default:
				boolean ismove3 = moveActionInModel(getTargetName(list, IDs), doc);
				if (!ismove3)
					doc = printRule(doc, cameraName, 1, "Rotate", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
							startRotate, endRotate, startSceneType, startSceneType, startframe, endframe, startall,
							endall);
				else
					doc = printRule(doc, cameraName, 1, "Fix", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
							startRotate, endRotate, startSceneType, startSceneType, startframe, endframe, startall,
							endall);
				break;
			}
		} else {
			int randomnum = random.nextInt(4);
			switch (randomnum) {
			case 0:
				doc = printRule(doc, cameraName, 1, "Rotate", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
						startRotate, endRotate, startSceneType, startSceneType, startframe, endframe, startall, endall);
				break;
			case 1:
				doc = printRule(doc, cameraName, 1, "Fix", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
						startRotate, startRotate, startSceneType, startSceneType, startframe, startframe
								+ (endframe - startframe) / 2, startall + startframe, startall + startframe
								+ (endframe - startframe) / 2);
				doc = printRule(doc, cameraName, 0, "Rotate", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
						startRotate, endRotate, startSceneType, startSceneType, startframe + (endframe - startframe)
								/ 2 + 1, endframe, startall + startframe + (endframe - startframe) / 2 + 1, startall
								+ endframe);
				break;
			case 2:
				doc = printRule(doc, cameraName, 1, "Rotate", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
						startRotate, endRotate, startSceneType, startSceneType, startframe, startframe
								+ (endframe - startframe) / 2, startall + startframe, startall + startframe
								+ (endframe - startframe) / 2);
				doc = printRule(doc, cameraName, 0, "Fix", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
						endRotate, endRotate, startSceneType, startSceneType, startframe + (endframe - startframe) / 2
								+ 1, endframe, startall + startframe + (endframe - startframe) / 2 + 1, startall
								+ endframe);
				break;
			default:
				doc = printRule(doc, cameraName, 1, "Rotate", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
						startRotate, endRotate, startSceneType, startSceneType, startframe, endframe, startall, endall);
				break;
			}
		}
		return doc;
	}

	public Document NewFix(Document doc, String cameraName, ArrayList<SceneSpace> list, int startframe, int endframe,
			int startall, int endall) {
		ArrayList<SceneSpace> targetLists = getTargetID(list, 1);// ��ʵȷ���Ѿ���Ŀ��ģ����
		int targetSize = targetLists.size();
		/**
		 * ��������Ŀ��ѡȡ�����Ƕ����� ���ѡ����ת�Ƕ������뾰��
		 */
		String idsPitch = getPitch(list);// ��������Ŀ��ѡȡ�����Ƕ�����
		String startRotate = getRotate(list);// ��������Ŀ��ѡ����ת�Ƕ�����
		String startSceneType = getSceneType(list, random.nextInt(33) % 4);// ���ѡ�񾰱����ԣ�fix��start
																			// end��ͬ

		ArrayList<String> IDs = getIDs(list);
		ArrayList<String> targetIDs = getIDs(targetLists);
		/**
		 * �����ַ���Ϸ�ʽ
		 */

		if (targetSize > 0) {
			// ���ܲ�����0�𣿡�����
			doc = printRule(doc, cameraName, 1, "Fix", targetIDs, getTargetName(list, IDs), idsPitch, idsPitch,
					startRotate, startRotate, startSceneType, startSceneType, startframe, endframe, startall, endall);

		} else
			doc = printRule(doc, cameraName, 1, "Fix", IDs, getTargetName(list, IDs), idsPitch, idsPitch, startRotate,
					startRotate, startSceneType, startSceneType, startframe, endframe, startall, endall);
		return doc;
	}

	public Document NewCameraPlan(ArrayList<ADLRule> shots, OWLModel owlModel, String cameraName, Document doc,
			ArrayList<SceneSpace> listAll, int startFrame, int endFrame) {

		ArrayList<SceneSpace> targetIDs = getTargetID(listAll, 1);
		int targetIDSize = targetIDs.size();

		if (targetIDSize > 0) {
			doc = FullShot(shots, doc, cameraName, targetIDs, startFrame, endFrame, startFrame, endFrame);
		} else
			doc = cameraPlan(owlModel, cameraName, doc, listAll, startFrame, endFrame);
		return doc;

	}

	/**
	 * ȡ�ô���ģ��isTarget=1�Ŀ��ÿռ�
	 * 
	 * @param list
	 * @return
	 */
	public ArrayList<String> getTargetInSpaceList(ArrayList<SceneSpace> list) {
		ArrayList<String> targetSpaceList = new ArrayList<String>();
		logger.info("�����д���isTarget=1�Ŀռ��У�");
		for (int i = 0; i < list.size(); i++) {
			if (!isStrinList(list.get(i).getSpname(), targetSpaceList)) {

				if (list.get(i).getisTarget() == 1) {
					targetSpaceList.add(list.get(i).getSpname());
					logger.info(list.get(i).getSpname());
				}

			}
		}
		return targetSpaceList;

	}

	public void SpaceCameraPlan(ArrayList<ADLRule> shots, ArrayList<SceneSpace> listall, ArrayList<String> spacelist,
			ArrayList<String> targetSpacelist, OWLModel owlModel, Document doc, int startFrame, int endFrame) {
		int framenum = endFrame - startFrame + 1;
		switch (targetSpacelist.size()) {
		case 0:
			switch (spacelist.size()) {
			case 0:
				doc = NewCameraPlan(shots, owlModel, "newCameraA", doc, listall, 1, framenum);
				break;
			case 1:
				doc = NewCameraPlan(shots, owlModel, "newCameraA", doc, getSpaceScene(listall, spacelist.get(0)), 1,
						framenum);
				break;
			default:
				doc = NewCameraPlan(shots, owlModel, "newCameraA", doc, getSpaceScene(listall, spacelist.get(0)),
						startFrame, (endFrame - startFrame) / 2 + startFrame);
				doc = NewCameraPlan(shots, owlModel, "newCameraB", doc, getSpaceScene(listall, spacelist.get(1)),
						(endFrame - startFrame) / 2 + startFrame + 1, endFrame);
			}
			break;
		case 1:
			doc = NewCameraPlan(shots, owlModel, "newCameraA", doc, getMaxIndexID(listall), startFrame,
					(endFrame - startFrame) / 2 + startFrame);// �÷����ģ�99%��istarget=1
			if (spacelist != null)
				doc = NewCameraPlan(shots, owlModel, "newCameraB", doc,
						getSpaceScene(listall, spacelist.get(spacelist.size() - 1)), (endFrame - startFrame) / 2
								+ startFrame + 1, endFrame);
			break;
		case 2:
			doc = NewCameraPlan(shots, owlModel, "newCameraA", doc, getSpaceScene(listall, targetSpacelist.get(0)),
					startFrame, (endFrame - startFrame) / 2 + startFrame);
			doc = NewCameraPlan(shots, owlModel, "newCameraB", doc, getSpaceScene(listall, targetSpacelist.get(1)),
					(endFrame - startFrame) / 2 + startFrame + 1, endFrame);
			break;
		default:
			doc = NewCameraPlan(shots, owlModel, "newCameraA", doc, getSpaceScene(listall, targetSpacelist.get(0)),
					startFrame, (endFrame - startFrame) / 2 + startFrame);
			doc = NewCameraPlan(shots, owlModel, "newCameraB", doc, getSpaceScene(listall, targetSpacelist.get(1)),
					(endFrame - startFrame) / 2 + startFrame + 1, endFrame);
		}
	}

	/**
	 * �ж�ģ���б���ģ���Ƿ���λ��
	 * 
	 * @param modellist
	 * @param doc
	 * @return
	 */
	public boolean moveActionInModel(ArrayList<String> modelNamelist, Document doc) {
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		for (Iterator<Element> it = name.elementIterator(); it.hasNext();) {
			Element rule = it.next();
			Pattern pattern1 = Pattern.compile(".*walk.*", Pattern.CASE_INSENSITIVE);
			Pattern pattern2 = Pattern.compile(".*run.*", Pattern.CASE_INSENSITIVE);
			Matcher match1, match2;
			boolean flag = false;

			// �������������actionName���������ȡ�ò���ֵ����������ƥ��
			if (null != rule.attributeValue("actionName")) {
				match1 = pattern1.matcher(rule.attributeValue("actionName"));
				match2 = pattern1.matcher(rule.attributeValue("actionName"));
				flag = match1.find() || match2.find();
			}
			// �������if�жϳ�������������walk����λ�ƵĶ��������ȡ��������� modelName
			if (null != rule.attributeValue("ruleType") && rule.attributeValue("ruleType").equals("addActionToMa")
					&& flag) {
				// ȡ�ö���������λ�Ƶ������жϸ�model�Ƿ���modellist��
				String modelnameAction = rule.attributeValue("usedModelInMa");// eg.
																				// M_boy.ma
				if (null != modelnameAction) {
					String modelname = modelnameAction;
					if (modelNamelist.contains(modelname))// �������Ҫ�����ϵ��Ŀ�����Ƿ��������λ�Ƶ�model
						return true;
					else
						continue;
				}
			}
		}
		return false;
	}

	/**
	 * ��adl���ȡmaFrame����ֵ
	 * 
	 * @param doc
	 * @return int
	 */
	public int getMaFrameInADL(Document doc) {
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		String maframe = name.attributeValue("maFrame");
		if (null != maframe && !maframe.equals("")) {
			int frame = Integer.parseInt(name.attributeValue("maFrame"));
			logger.info(frame);
			return frame;
		}
		return 0;
	}

	/**
	 * dxf.�°�
	 * <p>
	 * ��֪ʶ���ȡ����
	 * 
	 * @return
	 */
	@SuppressWarnings("finally")
	public ArrayList<ADLRule> getADLRulesFromOWL(int startframe, int endframe, Document doc) {
		ArrayList<ADLRule> ADLRules = new ArrayList<ADLRule>();
		try {
			logger.info("����֪ʶ����camera���ʵ���ĸ������ԣ�start");
			CameraSWRL.refreshHLCPandShot(this.PREFIX_OWL, this.owlModel,
					"C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl");
			// �����и��µ����ܿ⣬����������ڲ����ܿ⣬������camera�⣬cameraֻҪ����������͹��򼴿�
			logger.info("����֪ʶ����camera���ʵ���ĸ�������:end");

			OWLNamedClass superclass = owlModel.getOWLNamedClass(PREFIX_OWL + "HLCPSet");// �õ��߼���Ӱԭ����
			OWLDatatypeProperty usedHLCP = owlModel.getOWLDatatypeProperty(PREFIX_OWL + "usedHLCP");
			OWLObjectProperty HLCPShots = owlModel.getOWLObjectProperty(PREFIX_OWL + "HLCPShots");
			// �����ַ���ȫ�־�ͷ����ÿ����ͷΧ���������,���Ǹ���ͼ���Զ���hlcp�Ķ�Ӧ��ͷ����ʱ�����Ѿ����֣��ڴ�Ϊ���һ��
			// OWLDatatypeProperty HLCPDirection =
			// owlModel.getOWLDatatypeProperty(PREFIX_OWL + "HLCPDirection");
			// OWLNamedClass camera_shot = owlModel.getOWLNamedClass(PREFIX_OWL
			// + "camera_shot");// �õ���ͷʵ����

			logger.info("��ȡ֪ʶ����CameraSWRL_HLCP_��ͷ����start");
			ArrayList<String> CameraRules = CameraSWRL.getSWRLRules(owlModel, CameraSWRL_HLCP_);
			logger.info("��ȡ֪ʶ����CameraSWRL_HLCP_��ͷ����end");
			ArrayList<String> rulesName = new ArrayList<String>();// ��õ÷ֵ�hlcp���򼯺ϣ���ƥ�䵱ǰ�����Ĺ���

			/**
			 * ��һ�� ִ������CameraSWRL_HLCP_����----��ʼ
			 */
			for (int i = 0; i < CameraRules.size(); i++) {
				logger.info("ִ�е�" + i + "��CameraSWRL_HLCP_��ͷ����start");
				CameraSWRL.executeSWRLEngine1(owlModel, CameraRules.get(i));// ÿ������ִ��
				logger.info("ִ�е�" + i + "��CameraSWRL_HLCP_��ͷ����end");
				Collection<?> clo = superclass.getInstances(true);
				/**
				 * �õ������������������HLCPʵ��,�����������hlcp������ ��ͷʵ�� Ŀ��ʵ����������·������ڶ�Ӧshot
				 * rule���޸� ��ͷʵ��������20170827
				 */
				for (Iterator<?> hlcps = clo.iterator(); hlcps.hasNext();) {// ��������HLCPʵ������Ϊ0������������Ϊһ������ֻ���Ƴ�һ��HLCP
					OWLIndividual hlcpindi = (OWLIndividual) hlcps.next();
					int curMax = hlcpindi.getPropertyValueCount(usedHLCP);
					if (curMax > 0) {
						// maxHLCP = curMax;
						rulesName.add(CameraRules.get(i));
						break;
					}
				}
				// ִ���꣬ɾ��д��֪ʶ��ʵ����hlcp����ֵ��
				logger.info("ִ����CameraSWRL_HLCP_���������ʵ������ֵ��start");
				CameraSWRL.refreshHLCP(this.PREFIX_OWL, this.owlModel,
						"C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl");
				logger.info("ִ����CameraSWRL_HLCP_���������ʵ������ֵ��end");
			}
			/**
			 * ִ������CameraSWRL_HLCP_����-----����
			 */

			/**
			 * �ҵ�usedhlcp�÷�!=0 �Ĺ��򼯺�rulesName�������һ����ִ�У����ҵ��ٴ�ִ�е���������HLCPʵ��----��ʼ
			 */
			String hlcpRuleSelected = "";// �������ѡ����hlcp����
			OWLIndividual hlcpindiSelected = null;// hlcpRuleSelected�������hlcpʵ��
			if (!rulesName.isEmpty()) {
				Random random = new Random(System.currentTimeMillis());
				int pos = random.nextInt(rulesName.size());
				hlcpRuleSelected = rulesName.get(pos);
				// hlcpRuleSelected = "CameraSWRL_HLCP_Coaxile_3";//
				// ����CameraSWRL_HLCP_Coaxile_3

				logger.info("ִ��ѡ����CameraSWRL_HLCP_����start");
				CameraSWRL.executeSWRLEngine1(owlModel, hlcpRuleSelected);
				logger.info("ִ��ѡ����CameraSWRL_HLCP_����end");

				Collection<?> clo = superclass.getInstances(true);
				for (Iterator<?> hlcps = clo.iterator(); hlcps.hasNext();) {
					OWLIndividual hlcpindi = (OWLIndividual) hlcps.next();
					int score = hlcpindi.getPropertyValueCount(usedHLCP);
					if (score > 0) {
						hlcpindiSelected = hlcpindi;
						break;
					}
				}
			}
			/**
			 * �ҵ�usedhlcp�÷�!=0 �Ĺ��򼯺�rulesName�������һ����ִ�У����ҵ��ٴ�ִ�е���������HLCPʵ��----����
			 * ��һ�����
			 */

			/**
			 * �ڶ��㣬ִ������ѡȡ��HLCP��Ӧ�Ķ��������е����һ����
			 * ������ôȷ���ղ�ִ�е�hlcp����һ�ࣿhlcpȡ�����Ƕ�Ӧ���Լ������ü���
			 * ���磺��ȡ�������CameraSWRL_Shot_OverShoulder
			 * ��CameraSWRL_Shot_OverShoulder_3
			 * ��CameraSWRL_Shot_OverShoulder_2��CameraSWRL_Shot_OverShoulder_1��
			 * �����ѡһ������
			 */

			// ��һ��û���Ƴ��κ���Ӧ��HLCP�������
			if (hlcpRuleSelected == null || hlcpRuleSelected.length() < 16) {
				logger.info("û����ӦHLCP����תΪ��һ��滮");
				return ADLRules;
			}

			String hlcpMethod = hlcpRuleSelected.substring(16);// CameraSWRL_HLCP_In_Contra---In_Contra
																// //
																// CameraSWRL_HLCP_In_Contra_1---In_Contra_1
			logger.info("��ȡѡ����CameraSWRL_HLCP_�����Ӧ�Ķ���CameraSWRL_Shot_����start"); // CameraSWRL_Shot_In_Contra_3
			ArrayList<String> shotRules = CameraSWRL.getSWRLRules(owlModel, CameraSWRL_Shot_ + hlcpMethod);
			logger.info("��ȡѡ����CameraSWRL_HLCP_�����Ӧ�Ķ���CameraSWRL_Shot_����end");

			String shotRuleSelected = "";
			if (shotRules.size() != 0) {
				Random random = new Random(System.currentTimeMillis());
				int pos = random.nextInt(shotRules.size());
				shotRuleSelected = shotRules.get(pos);
				logger.info("ִ��ѡ����һ��CameraSWRL_Shot_����start");
				CameraSWRL.executeSWRLEngine1(owlModel, shotRuleSelected); // ִֻ��һ��û��Ҫˢ��
				logger.info("ִ��ѡ����һ��CameraSWRL_Shot_����end");
			}
			/**
			 * �ڶ������
			 */

			if (hlcpindiSelected != null) {
				logger.info("hlcp�ַ���" + hlcpMethod + "������hlcpʵ����" + hlcpindiSelected.getBrowserText() + ";shotRuleʵ����"
						+ shotRuleSelected);
				Collection<?> HLCPshots = (Collection<?>) hlcpindiSelected.getPropertyValues(HLCPShots);
				OWLObjectProperty HLCPDirection = owlModel.getOWLObjectProperty(PREFIX_OWL + "HLCPDirection");// �����ַ���ȫ�־�ͷ����ÿ����ͷΧ���������
				OWLObjectProperty ShotPitch = owlModel.getOWLObjectProperty(PREFIX_OWL + "ShotPitch");
				OWLObjectProperty ShotRotate = owlModel.getOWLObjectProperty(PREFIX_OWL + "ShotRotate");
				OWLObjectProperty ShotScale = owlModel.getOWLObjectProperty(PREFIX_OWL + "ShotScale");
				OWLObjectProperty ShotTime = owlModel.getOWLObjectProperty(PREFIX_OWL + "ShotTime");
				OWLDatatypeProperty ShotTargets = owlModel.getOWLDatatypeProperty(PREFIX_OWL + "ShotTargets");
				OWLObjectProperty first_shot = owlModel.getOWLObjectProperty(PREFIX_OWL + "first_shot");
				OWLObjectProperty next_shot = owlModel.getOWLObjectProperty(PREFIX_OWL + "next_shot");

				// ������������������� �������Եľ���ֵ ��eg,
				// Pitch��������LookupPitch������PitchAttr=lookup
				OWLDatatypeProperty HLCPDirectionattr = owlModel.getOWLDatatypeProperty(PREFIX_OWL + "DirectionAttr");
				OWLDatatypeProperty ShotPitchattr = owlModel.getOWLDatatypeProperty(PREFIX_OWL + "PitchAttr");
				OWLDatatypeProperty ShotRotateattr = owlModel.getOWLDatatypeProperty(PREFIX_OWL + "YawAttr");
				OWLDatatypeProperty ShotScaleattr = owlModel.getOWLDatatypeProperty(PREFIX_OWL + "ScaleAttr");
				OWLDatatypeProperty ShotTimeattr = owlModel.getOWLDatatypeProperty(PREFIX_OWL + "TimeAttr");

				int countShot = 0;
				// ���Ƽ���HLCPʵ��������ͷ��iterator
				// startFrame��0��ʼ
				int start = startframe, end = startframe;
				int remainder = endframe - startframe;// δ���������֡��
				for (Iterator<?> it = HLCPshots.iterator(); it.hasNext();) {
					ADLRule rule = new ADLRule();// �þ�ͷ��ADLRule
					OWLIndividual shot = (OWLIndividual) it.next();// ֪ʶ���еľ�ͷʵ��
					Collection<?> shotTargets = shot.getPropertyValues(ShotTargets);// shotʵ����targets
																					// object
					ArrayList<String> TargetIDList = new ArrayList<String>();// shotʵ����targets
																				// object
																				// ����
																				// ��������Ϊid

					/**
					 * ����Ϊnull:
					 * <p>
					 * 1,��Ӧhlcp�����shot���� ��������е������ַ�������һ��Ҫһ������
					 * 2,HLCP�����и����ĸ��ַ��ľ�ͷ�����������Ƿ�����Ӧ��shot�����н���������(�����½���ͷ)��
					 */
					OWLIndividual pitchIndi = (OWLIndividual) shot.getPropertyValue(ShotPitch);

					if (pitchIndi == null) {
						logger.error("exception:" + shot.getBrowserText());
					}
					String pitch = (String) pitchIndi.getPropertyValue(ShotPitchattr);

					OWLIndividual rotateIndi = (OWLIndividual) shot.getPropertyValue(ShotRotate);
					String rotate = (String) rotateIndi.getPropertyValue(ShotRotateattr);

					OWLIndividual scaleIndi = (OWLIndividual) shot.getPropertyValue(ShotScale);
					String scale = (String) scaleIndi.getPropertyValue(ShotScaleattr);

					OWLIndividual timeIndi = (OWLIndividual) shot.getPropertyValue(ShotTime);
					String time = (String) timeIndi.getPropertyValue(ShotTimeattr);// ֪ʶ���е�
																					// ����ͷʵ����
																					// �������
																					// ��ʱ��ʵ����
																					// ��ʱ��ֵ
					int[] startAndEnd = new int[2];
					try {
						startAndEnd = setStartEndTimeByRatio(time, start, end, remainder, startframe, endframe);
						start = startAndEnd[0];
						end = startAndEnd[1];
						remainder = startAndEnd[2];
					} catch (Exception e) {
						logger.error(e.getMessage());
						break;
					}

					// shot�ڵ�target����
					for (Iterator<?> target = shotTargets.iterator(); target.hasNext();) {
						String id = (String) target.next();
						TargetIDList.add(id);
					}

					Collections.sort(TargetIDList);

					@SuppressWarnings("deprecation")
					OWLNamedClass shotClass = (OWLNamedClass) shot.getDirectType();// �õ�shotʵ��������������
					String shotInOWL = shotClass.getLocalName();

					rule.setShotPlan(shotInOWL);// �õ���ͷʵ������shot�������
					ArrayList<String> target = getNamesByID(TargetIDList, doc);
					String topicInDoc = getTopicFromDoc(doc);
					rule.setTarget(target);// ÿ����ͷʵ����target
					rule.setUsedModelID(TargetIDList);
					rule.setTopic(topicInDoc);// ���ŵ����⣬����Ϊ�մ�
					String plan = shotInOWL;

					char postfix = (char) ('A' + countShot);
					String shotName = "newCamera" + postfix;
					rule.setShotName(shotName);
					countShot++;// ��ͷ��������

					rule.setStartPitch(pitch);
					rule.setStartYaw(rotate);
					rule.setStartShotType(scale);

					// ����plan���������ø�������ת������� start ,endֵ
					setEnd(plan, rule);

					// һ����ͷ���ܻ��ж��target,��������Ǹ�����
					// ��ͷʱ��������ʱû�а�����ı������䣬Ŀǰ��ƽ�����䣬���޸İ���������
					rule.setStartframe(start);
					rule.setEndframe(end);
					// CameraSWRL_HLCP_In_Contra_1---In_Contra_1
					rule.setHlcp(hlcpMethod);
					rule.setOrder(ADLRules.size() + 1);
					ADLRules.add(rule);

					logger.info(rule.toString());
				}
				if (remainder != 0) {
					ADLRule rule = ADLRules.get(ADLRules.size() - 1).clone();// ע�⣬�˴���ǳ����
					rule.setStartframe(rule.getEndframe() + 1);
					rule.setEndframe(this.maFrame);
					rule.setOrder(ADLRules.size() + 1);

					char postfix = (char) ('A' + countShot);
					String shotName = "newCamera" + postfix;
					rule.setShotName(shotName);// eg,Coaxile_3��push,fix,fix���ڶ���fix�����ڴ˴����
					countShot++;// ��ͷ��������

					ADLRules.add(rule);

					logger.info("If the SWRL rule include two same shots,we necessarily add an random rule to patch the time remainder:");
					logger.info("�����а�����ͬ��ͷ,�������о�ͷ��");
					logger.info(rule.toString());
				}
			}
		} catch (SWRLFactoryException e) {
			// TODO Auto-generated catch block
			logger.info(e.getMessage());
			e.printStackTrace();
		} catch (SWRLRuleEngineException e) {
			// TODO Auto-generated catch block
			logger.info(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.info(e.getMessage());
			e.printStackTrace();
		} finally {
			return ADLRules;
		}
	}

	// ����plan���������ø�������ת������� start ,endֵ

	public void setEnd(String plan, ADLRule rule) {
		String pitch = rule.getStartPitch();
		String rotate = rule.getStartYaw();
		String scale = rule.getStartShotType();
		if (plan.equalsIgnoreCase("Fix")) {
			rule.setEndPitch(pitch);
			rule.setEndYaw(rotate);
			rule.setEndShotType(scale);
		} else if (plan.equalsIgnoreCase("Push")) {
			rule.setEndPitch(pitch);// ����
			rule.setEndYaw(rotate);// ����
			rule.setEndShotType(randomScale(scale, false));// �����С
		} else if (plan.equalsIgnoreCase("Pull")) {
			rule.setEndPitch(pitch);// ����
			rule.setEndYaw(rotate);// ����
			rule.setEndShotType(randomScale(scale, true));// �������
		} else if (plan.equalsIgnoreCase("Transform")) {
			rule.setEndPitch(pitch);// ����
			rule.setEndYaw(rotate);// ����
			rule.setEndShotType(randomScale(scale, true));// �������
		} else if (plan.equalsIgnoreCase("Rotate")) {
			rule.setEndPitch(pitch);// ����
			rule.setEndYaw(randomRotate(rotate));// ����������Ǳ���
			rule.setEndShotType(randomScale(scale, false));// �����С
		} else if (plan.equalsIgnoreCase("Follow")) {
			rule.setEndPitch(pitch);// ����
			rule.setEndYaw(rotate);// ����
			rule.setEndShotType(scale);
		}
	}

	/**
	 * ��flag�ж�������ؽϴ󣬽�Сֵ��flagΪtrue�����ؽϴ� false�����ؽ�С
	 * 
	 * @param pitch
	 * @param flag
	 * @return
	 */
	public String randomScale(String scale, boolean flag) {
		int pos = 0;
		ArrayList<String> scales = new ArrayList<String>();
		// scales.add("closeup");//closeup��ò��ã�����Ч�������������������ʱ
		scales.add("medium");
		scales.add("full");
		scales.add("establish");
		if (flag) {
			pos = scales.size() - 1;
		}
		if (scales.contains(scale)) {
			int index = scales.indexOf(scale);
			if (flag) {// ���ؽϴ󾰱�
				int add = new Random(System.currentTimeMillis()).nextInt(scales.size() - index);
				if (add == 0) {
					pos = index + 1;// ��ֹ���𲻱�
				} else
					pos = index + add;
			} else {// ���ؽ�С����
				if (index == 0)// ����������С
					pos = 0;
				else
					pos = new Random(System.currentTimeMillis()).nextInt(index);
			}

		}
		return scales.get(pos);
	}

	/**
	 * 
	 * @param rotate
	 * @return
	 */
	public String randomRotate(String rotate) {
		int pos = 0;
		ArrayList<String> rotates = new ArrayList<String>();
		rotates.add("nearside");
		rotates.add("forward");
		rotates.add("offside");
		rotates.add("backside");
		if (rotates.contains(rotate)) {
			int index = rotates.indexOf(rotate);
			int ran = Math.abs(new Random(System.currentTimeMillis()).nextInt());
			pos = ran % rotates.size();
			if (pos == index) {
				pos = (index + 1) % rotates.size();
			}
		}
		return rotates.get(pos);
	}

	/**
	 * ��flag�ж�������ؽϴ󣬽�Сֵ��flagΪtrue�������ϣ� false��������
	 * 
	 * @param pitch
	 * @param flag
	 * @return
	 */
	public String randomPitch(String pitch, boolean flag) {
		int pos = 0;
		ArrayList<String> pitches = new ArrayList<String>();
		pitches.add("lookdown");
		pitches.add("forehead");
		pitches.add("lookup");
		if (flag) {
			pos = pitches.size() - 1;
		}
		if (pitches.contains(pitch)) {
			int index = pitches.indexOf(pitch);
			if (flag) {// ���ؽϴ�
				int add = new Random(System.currentTimeMillis()).nextInt(pitches.size() - index);
				if (add == 0) {
					pos = index + 1;// ��ֹ����
				} else
					pos = index + add;
			} else {// ���ؽ�С
				if (index == 0)// ������С
					pos = 0;
				else
					pos = new Random(System.currentTimeMillis()).nextInt(index);
			}

		}
		return pitches.get(pos);
	}

	public int[] setStartEndTimeByRatio(String ratio, int start, int end, int remainder, int hlcpStartFrame,
			int hlcpEndFrame) throws Exception {
		int[] ret = new int[3];
		int hlcpCameraFrame = hlcpEndFrame - hlcpStartFrame + 1;

		/**
		 * ��ͷ֡������ start
		 */
		double timeRatio = 0.0;
		ratio = ratio.trim();
		if (ratio.equalsIgnoreCase("short")) {
			timeRatio = this.SHORT;
		} else if (ratio.equalsIgnoreCase("long")) {
			timeRatio = this.LONG;
		} else if (ratio.equalsIgnoreCase("medium")) {
			timeRatio = this.MEDIUM;
		} else if (ratio.equalsIgnoreCase("all")) {
			timeRatio = this.ALL;
		}
		start = end + 1;
		int per = (int) (hlcpCameraFrame * timeRatio);
		if (per <= remainder) {
			end = end + per;
			remainder -= per;
		} else {
			end = end + remainder;
			remainder = 0;
		}
		if (start >= end) {
			logger.error("shotTime�������ô���");
			throw new Exception("shotTime�������ô���");
		}
		/**
		 * ��ͷ֡������ end
		 */
		// ������ʣ��֡������Ķ����һ����ͷ
		if (remainder > 0 && remainder <= 5) {
			end = hlcpEndFrame;
			remainder = 0;
		}

		ret[0] = start;
		ret[1] = end;
		ret[2] = remainder;
		return ret;
	}

	public HashMap<String, String> getspByID(Document doc, ArrayList<String> IDs) {

		HashMap<String, String> ret = new HashMap<String, String>();
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		for (@SuppressWarnings("unchecked")
		Iterator<Element> it = name.elementIterator(); it.hasNext();) {
			Element rule = it.next();
			if (null != rule.attributeValue("ruleType") && rule.attributeValue("ruleType").equals("addToMa")) {
				for (int i = 0; i < IDs.size(); i++) {
					String modelid = rule.attributeValue("addModelID");
					if (modelid != null && modelid.equals(IDs.get(i))) {// �����е�
																		// id
																		// ==������idʱ
						String sp = rule.attributeValue("spaceName");
						ret.put(modelid, sp);
					}
				}
			}
		}
		return ret;
	}

	public String getTopicFromDoc(Document doc) {
		String ret = "";
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		if (name != null && name.attributeValue("topic") != null) {
			ret = name.attributeValue("topic");
		}
		return ret;
	}

	public boolean isReadyBgSceneCamera(String maName) {
		Set<String> maNames = new HashSet<String>();
		maNames.add("windbell.ma");
		maNames.add("seal.ma");
		maNames.add("hurry.ma");
		maNames.add("danceGirl.ma");
		maNames.add("clock.ma");
		maNames.add("snake.ma");
		maNames.add("questionMark.ma");
		maNames.add("flowerSmile.ma");
		maNames.add("fallingStar.ma");
		maNames.add("balloon.ma");
		maNames.add("comeBack.ma");
		maNames.add("Valentineday.ma");
		maNames.add("undersea_3.ma");
		maNames.add("sunrise.ma");
		maNames.add("festival.ma");
		maNames.add("map.ma");
		maNames.add("disagree_boy.ma");
		maNames.add("giveRose.ma");
		maNames.add("happy_mm.ma");
		maNames.add("agree_flower.ma");
		maNames.add("butterfly.ma");
		maNames.add("butterflyGrass.ma");
		maNames.add("waterfall.ma");
		maNames.add("happy_mm.ma");
		maNames.add("angry_mm.ma");
		maNames.add("cry_mm.ma");
		if (maNames.contains(maName)) {
			return true;
		}
		return false;
	}

	/**
	 *
	 * �жϸ� modelNamelist���Ƿ������doc�й滮���飬�����ڹ滮���飬������Ŀ���滻Ϊ��������
	 * 
	 * @param modelNamelist
	 * @param doc
	 * @return
	 */
	public int hasExpressAndOneModel(ArrayList<String> modelNamelist, Document doc) {
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		for (Iterator<Element> it = name.elementIterator(); it.hasNext() && !CameraForExpression;) {
			Element rule = it.next();
			// �ҳ�ADL����ӵı��飬����������ģ���Ƿ���modelNamelist�У��������������ظ�ģ�͵����֣�һ��ADL������һ�������ľ�ͷ��
			if (null != rule.attributeValue("ruleType") && rule.attributeValue("ruleType").equals("addExpressionToMa")) {
				String modelnameExpresison = rule.attributeValue("usedModelInMa");// eg.
																					// M_boy.ma
				if (null != modelnameExpresison) {
					if (modelNamelist.contains(modelnameExpresison)) {// �������Ҫ�����ϵ��Ŀ�����Ƿ�������б����model
						CameraForExpression = true;
						return modelNamelist.indexOf(modelnameExpresison);
					} else
						continue;
				}
			}
		}
		return -1;
	}

	public static void main(String[] str) {
		CameraToXML camera = new CameraToXML();
		System.out.println(camera.randomRotate("nearside"));
	}

	/**
	 * CameraToXML���ڲ���
	 * 
	 * @author Administrator
	 *
	 */
	class ADLRule implements Cloneable {
		private String shotName;
		private String range;
		private int isStart;
		private String depthOfField;
		private String shotPlan;
		private ArrayList<String> usedModelID;
		private ArrayList<String> target;
		private String startPitch;
		private String endPitch;
		private String startYaw;
		private String endYaw;
		private String startShotType;
		private String endShotType;
		private int startframe;
		private int endframe;
		private int startinall;
		private int endinallinall;
		private String hlcp;
		private int order = -1;
		private String topic;

		public String getShotName() {
			return shotName;
		}

		public void setShotName(String shotName) {
			this.shotName = shotName;
		}

		public String getRange() {
			return range;
		}

		public void setRange(String range) {
			this.range = range;
		}

		public String getDepthOfField() {
			return depthOfField;
		}

		public void setDepthOfField(String depthOfField) {
			this.depthOfField = depthOfField;
		}

		public String getShotPlan() {
			return shotPlan;
		}

		public void setShotPlan(String shotPlan) {
			this.shotPlan = shotPlan;
		}

		public ArrayList<String> getUsedModelID() {
			return usedModelID;
		}

		public void setUsedModelID(ArrayList<String> usedModelID) {
			this.usedModelID = usedModelID;
		}

		public String getStartPitch() {
			return startPitch;
		}

		public void setStartPitch(String startPitch) {
			this.startPitch = startPitch;
		}

		public String getEndPitch() {
			return endPitch;
		}

		public void setEndPitch(String endPitch) {
			this.endPitch = endPitch;
		}

		public String getStartYaw() {
			return startYaw;
		}

		public void setStartYaw(String startYaw) {
			this.startYaw = startYaw;
		}

		public String getEndYaw() {
			return endYaw;
		}

		public void setEndYaw(String endYaw) {
			this.endYaw = endYaw;
		}

		public String getStartShotType() {
			return startShotType;
		}

		public void setStartShotType(String startShotType) {
			this.startShotType = startShotType;
		}

		public String getEndShotType() {
			return endShotType;
		}

		public void setEndShotType(String endShotType) {
			this.endShotType = endShotType;
		}

		public ArrayList<String> getTarget() {
			return target;
		}

		public void setTarget(ArrayList<String> target) {
			this.target = target;
		}

		public int getIsStart() {
			return isStart;
		}

		public void setIsStart(int isStart) {
			this.isStart = isStart;
		}

		public int getStartframe() {
			return startframe;
		}

		public void setStartframe(int startframe) {
			this.startframe = startframe;
		}

		public int getEndframe() {
			return endframe;
		}

		public void setEndframe(int endframe) {
			this.endframe = endframe;
		}

		public int getStartinall() {
			return startinall;
		}

		public void setStartinall(int startinall) {
			this.startinall = startinall;
		}

		public int getEndinallinall() {
			return endinallinall;
		}

		public void setEndinallinall(int endinallinall) {
			this.endinallinall = endinallinall;
		}

		public String getHlcp() {
			return hlcp;
		}

		public void setHlcp(String hlcp) {
			this.hlcp = hlcp;
		}

		public int getOrder() {
			return order;
		}

		public void setOrder(int order) {
			this.order = order;
		}

		/**
		 * ���ADLRule��� shadow copy ǳ����
		 */
		public ADLRule clone() {
			ADLRule rule = new ADLRule();
			try {
				rule = (ADLRule) super.clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return rule;
		}

		public String getTopic() {
			return topic;
		}

		public void setTopic(String topic) {
			this.topic = topic;
		}

		public String toString() {
			String ret = "\"Topic\":" + this.getTopic() + ";\"CameraName\":" + this.getShotName() + ";\"HLCPPlan\":"
					+ this.getHlcp() + ";\"ShotPlan\":" + this.getShotPlan() + ";\"target\":"
					+ this.getTarget().toString() + ";\"usedModelID\":" + this.getUsedModelID().toString()
					+ ";\"StartPitch\":" + this.getStartPitch() + ";\"EndPitch\":" + this.getEndPitch()
					+ ";\"StartYaw\":" + this.getStartYaw() + ";\"EndYaw\":" + this.getEndYaw() + ";\"StartShotType\":"
					+ this.getStartShotType() + ";\"EndShotType:" + this.getEndShotType() + ";\"Startframe\":"
					+ this.getStartframe() + ";\"Endframe\":" + this.getEndframe() + ";\"Order\":" + this.getOrder();
			return ret;
		}

	}

}
