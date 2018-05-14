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
	/** 场景中没有添加模型 默认摄像机 **/
	NOTHING,
	/** 定性中的场景为 摄像机不可以调整的背景场景 **/
	DEFAULT,
	/** 知识库中没有相应的规则，转为第一版摄像机规划 **/
	NO_RULE,
	/** 知识库中有相应的规则，并执行 **/
	EXEC_RULE_SUCESS,
	/** 知识库中有规则但是执行失败 **/
	EXEC_RULE_FAIL;
}

public class CameraToXML {

	private final String PREFIX_OWL = "p13:";
	/** Define the global classify prefix of camera 定义与camera 相关的规则的前缀，以及分类 */
	private final String CameraSWRL_HLCP_ = "CameraSWRL_HLCP_";// camera
																// primitives
	private final String CameraSWRL_Shot_ = "CameraSWRL_Shot_";// specific shot
	/** 摄像机定性规划的执行结果反馈枚举字符串 **/
	private static String CameraADLExeState = "";
	private static boolean CameraForExpression = false;

	private final double SHORT = 1 / 4.0;
	private final double MEDIUM = 2 / 4.0;
	private final double LONG = 3 / 4.0;
	private final double ALL = 1.0;

	private Logger logger = Logger.getLogger(CameraToXML.class.getName());

	private int spaceType = 0;// 可用空间类型

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
	 * 摄像机定性部分调用入口
	 * 
	 * @param owlModel
	 * @param maName
	 * @param doc
	 * @return 返回添加camera规则的doc
	 */
	public Document CreateCamera(OWLModel owlModel, String maName, Document doc) {
		logger.info("===摄像机程序开始===");
		this.owlModel = owlModel;
		if (maName.equals("nothing.ma")) {
			return doc;
		}
		int bgtype = 0;
		OWLIndividual ma = owlModel.getOWLIndividual(maName);
		OWLDatatypeProperty maframenumber = owlModel.getOWLDatatypeProperty("maFrameNumber");
		OWLDatatypeProperty mabgtype = owlModel.getOWLDatatypeProperty("backgroundPictureType");
		// maFrame:1，先从ADL里读取，若没有:2，读取知识库
		int frameNumInAdl = this.getMaFrameInADL(doc);
		if (frameNumInAdl != 0) {
			this.maFrame = frameNumInAdl;
		} else {
			this.maFrame = (Integer) ma.getPropertyValue(maframenumber);// 300
		}

		bgtype = (Integer) ma.getPropertyValue(mabgtype);// 3
		// if (bgtype != 3 && bgtype != 0 && bgtype != 1) //
		// 0室内;1背景场景（外加emptyPlot.ma，该场景中bgtype=1）;2 mm三个场景;3户外场景；
		// return doc;
		// 背景场景处理，定性设置，定量并未读取
		if (bgtype == 1 || bgtype == 2) {// 20180315 加入happy_mm类似场景
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
			if (bgtype == 3) // 室外场景
				doc = NewCameraPlan(shots, owlModel, "newCameraA", doc, listall, 1, this.maFrame);
			else { // 室内场景
				ArrayList<String> spacelist = getSpaceList(listall);
				// ArrayList<String> groundSpaceList=new ArrayList<String>();
				ArrayList<String> targetSpaceList = new ArrayList<String>();
				targetSpaceList = getTargetInSpaceList(listall);
				if (shots != null && shots.size() != 0) {
					doc = NewCameraPlan(shots, owlModel, "newCameraA", doc, listall, 1, this.maFrame);// 主要根据的是知识库是否推断出shots，而不是根据
				} else
					SpaceCameraPlan(shots, listall, spacelist, targetSpaceList, owlModel, doc, startFrame, endFrame);
			}
		}
		logger.info("===摄像机程序结束===");
		return doc;
	}

	/**
	 * 打印规则
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

		// ///////////////////////////人物表情判断////////////////////////////
		
		int indexExpressionModel = hasExpressAndOneModel(targets, doc);
		if (-1 != indexExpressionModel) {// 若存在含有表情的人物，随机表现
			targetid = targetIDs.get(indexExpressionModel);
			targetname = targets.get(indexExpressionModel);
			CameraForExpression = true;
		}
		// ///////////////////////////人物表情判断结束//////////////////////////
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
		if (shot.getOrder() != -1 && shot.getOrder() == 1)// 20180104,只有在第一个镜头的开始时是1，其他均为0：即所有的镜头均是连续的
			ruleName.addAttribute("isStart", "1");
		else
			ruleName.addAttribute("isStart", "0");
		ruleName.addAttribute("depthOfField", "focus");
		ruleName.addAttribute("HLCP", shot.getHlcp());// 忘记记录
		ArrayList<String> models = shot.getTarget();
		// ///////////在此判断定镜头的拍摄目标是否有 有位移的动作////////////////
		// 确认是定镜头
		if (shot.getShotPlan().equalsIgnoreCase("Fix")) {
			// 确认有位移动作
			if (moveActionInModel(models, doc)) {
				shot.setShotPlan("Follow");
			}
		}
		// ///////////////////////////位移动作判断结束////////////////////////////

		ruleName.addAttribute("shotPlan", shot.getShotPlan());
		String targetid = "";
		String targetname = "";

		ArrayList<String> targetidsInOwl = new ArrayList<String>();
		ArrayList<String> targetnameInOwl = new ArrayList<String>();
		if (targetidsInOwl != null && targetnameInOwl != null && targetidsInOwl.size() == targetnameInOwl.size()) {
			// ///////////////////////////人物表情判断////////////////////////////
			
			int indexExpressionModelOWL = hasExpressAndOneModel(models, doc);
			if (-1 != indexExpressionModelOWL) {// 若存在含有表情的人物，随机表现
				targetidsInOwl.add(shot.getUsedModelID().get(indexExpressionModelOWL));
				targetnameInOwl.add(shot.getTarget().get(indexExpressionModelOWL));
			} else {
				targetidsInOwl = shot.getUsedModelID();
				targetnameInOwl = shot.getTarget();
			}
			// ///////////////////////////人物表情判断结束//////////////////////////
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
	 * doc2XmlFile 将Document对象保存为一个xml文件到本地
	 * 
	 * @return true:保存成功 flase:失败
	 * @param filename
	 *            保存的文件名
	 * @param document
	 *            需要保存的document对象
	 */
	public boolean doc2XmlFile(Document document, String filename) {
		boolean flag = true;
		try {
			/* 将document中的内容写入文件中 */
			// 默认为UTF-8格式，指定为"GB2312"
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("GB2312");
			XMLWriter writer = new XMLWriter(new FileWriter(new File(filename)), format);
			writer.write(document);
			writer.close();
		} catch (Exception ex) {
			flag = false;
			ex.printStackTrace();
			System.out.println("doc2XmlFile函数写入失败");
		}
		return flag;
	}

	/**
	 * 所有添加模型中选取人物类的模型
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
	 * 所有添加模型中选取模型model类的模型
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
	 * 获取list中的所有可用空间
	 * 
	 * @param list
	 * @return
	 */
	public ArrayList<String> getSpaceList(ArrayList<SceneSpace> list) {
		ArrayList<String> spacelist = new ArrayList<String>();
		logger.info("getSpaceList函数中得到spacelist：");
		for (int i = 0; i < list.size(); i++) {
			if (!isStrinList(list.get(i).getSpname(), spacelist)) {
				spacelist.add(list.get(i).getSpname());
				logger.info(list.get(i).getSpname() + "    ");
			}
		}
		return spacelist;
	}

	/**
	 * 判断某一段字符串是否在list 集合中
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
	 * 整体摄像机规划 函数参数为场景中所有adl添加的模型
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
	 * 判断可用空间实例属于那个类 1：OnGround；2：InAir；3：others；0：不存在
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
			 * 判断该实例是否属于OnGround类下的实例
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
			 * 判断该实例是否属于InAir类下的实例
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
			 * 缺少判断其他类
			 */
			return 3;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 获取isTarget的目标组
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
	 * 获取特定可用空间上的目标(模型)组
	 * 
	 * @param list
	 * @param owlModel
	 * @param spaceType
	 *            =1：OnGround；2：InAir；3：others；0：不存在
	 * @return
	 */
	public ArrayList<SceneSpace> getSpaceScene(ArrayList<SceneSpace> list, String space) {

		ArrayList<SceneSpace> spaceIDs = new ArrayList<SceneSpace>();
		for (int i = 0; i < list.size(); i++) {
			if (space.indexOf(list.get(i).getSpname()) != -1) {// SELF:确定该模型在该空间上
				spaceIDs.add(list.get(i));
			}
		}
		return spaceIDs;
	}

	/**
	 * 获取加权值最大的ID
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
				// 因为包括等于号，所以在遇到两个模型，分数index相等时，会取第二个模型为maxID
				max = list.get(i).getIndex();
				maxIndexId = list.get(i);
			}
		}
		if (maxIndexId != null)
			ssList.add(maxIndexId);
		return ssList;
	}

	/**
	 * 获取类型为people或者model的目标组
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
	 * 获取目标类的ID组
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
	 * 获取目标类的name组 需要从doc中获得name
	 * 
	 * @param list
	 * 
	 * @return
	 */
	public ArrayList<String> getNamesByID(ArrayList<String> ids, Document doc) {
		ArrayList<String> targetnames = new ArrayList<String>();
		for (int i = 0; i < ids.size(); i++) {// 知识库中取出一个镜头的多个目标id
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
	 * Fix拍摄手法，只有单独的拍摄
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
		 * 根据拍摄目标选取俯仰角度属性 随机选择旋转角度属性与景别
		 */
		String idsPitch = getPitch(list);
		String startRotate = getRotate(list);
		String startSceneType = getSceneType(list, random.nextInt(33) % 4);
		/**
		 * 拍摄手法组合方式
		 */
		doc = printRule(doc, cameraName, 1, "Fix", IDs, getTargetName(list, IDs), idsPitch, idsPitch, startRotate,
				startRotate, startSceneType, startSceneType, startframe, endframe, startall, endall);
		return doc;
	}

	/**
	 * Rotate拍摄组合，Rotate、Fix-Rotate、Rotate-Fix
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
		 * 根据拍摄目标选取俯仰角度属性 随机选择旋转角度属性与景别
		 */
		String idsPitch = getPitch(list);
		String startRotate = getRotate(list);
		String endRotate = getRotate(list);
		String startSceneType = getSceneType(list, random.nextInt(33) % 4);
		/**
		 * 拍摄手法组合方式
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
	 * Push组合拍摄，Push、Fix-Push、Push-Fix
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
		 * 根据拍摄目标选取俯仰角度属性 随机选择旋转角度属性与景别
		 */
		String idsPitch = getPitch(list);
		String maxIndexPitch = getPitch(getMaxIndexID(list));
		String listStartRotate = getRotate(list);
		String maxStartRotate = getRotate(getMaxIndexID(list));

		// 20161230,保证景别所需变化，否则maybe,start==end
		int rantmp = random.nextInt(33);
		String startSceneType = getSceneType(list, rantmp % 2 + 2);
		String endSceneType = getSceneType(list, rantmp);
		/**
		 * 拍摄手法组合方式
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
	 * Push组合拍摄，Pull、Fix-Pull、Pull-Fix
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
		 * 根据拍摄目标选取俯仰角度属性 随机选择旋转角度属性与景别
		 */
		String idsPitch = getPitch(list);
		String maxIndexPitch = getPitch(getMaxIndexID(list));
		String listStartRotate = getRotate(list);
		String maxStartRotate = getRotate(getMaxIndexID(list));

		// 20161230,保证景别所需变化，否则maybe,start==end
		int rantmp = random.nextInt(33);
		String startSceneType = getSceneType(list, rantmp % 2);
		String endSceneType = getSceneType(list, rantmp + 2);
		/**
		 * 拍摄手法组合方式
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
	 * Complex 拍摄组合
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
		 * 根据拍摄目标选取俯仰角度属性 随机选择旋转角度属性与景别
		 */
		String idsPitch = getPitch(list);
		String maxIndexPitch = getPitch(getMaxIndexID(list));
		String listStartRotate = getRotate(list);
		String listEndRotate = getRotate(list);
		String maxStartRotate = getRotate(getMaxIndexID(list));
		String maxEndRotate = getRotate(getMaxIndexID(list));

		// 20161230,保证景别所需变化，否则maybe start==end
		int rantmp = random.nextInt(33);
		int medium_status = rantmp % 2;
		String startSceneType = getSceneType(list, medium_status);
		String endSceneType = getSceneType(list, medium_status + 2);

		/**
		 * 拍摄手法组合方式
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
	 * 根据拍摄目标得到俯仰角度属性
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
			str = "lookdown";// 目前舍去lookup，20170602
			break;
		default:
			str = "lookdown";
		}
		return str;
	}

	/**
	 * 根据拍摄目标个数与类型，按比例获得旋转角度属性
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
					str = "forward";// -30°到30度
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
	// i)，根据模型来判断拍摄景别
	public String getSceneType(ArrayList<SceneSpace> list, int i) {

		String str = "";
		if (getTypeIDs(list, "people").size() != 0) {
			// 如果单独拍摄人物，景别须不同考虑
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
				if (num != 1) {// 拍摄景物比较多的时候，才有虚实转化的方式，由虚转实或者由实转虚
					return "transform";
				} else {// 拍摄单一物体时，采用焦点方式进行拍摄，即拍摄目标处于清晰，背景处于模糊
					return "focus";
				}
			}

		}

		return "false";
	}

	/**
	 * 一个全拍摄镜头
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
			 * 若规则推理出合适的HLCP镜头，则走规则；否则继续执行之前的随机
			 */
			if (shots != null && shots.size() > 0) {
				if (shots.size() != 1) {// 镜头个数大于一个时候，需要按可用空间分配拍摄目标

					// 处理镜头的拍摄目标，将模型按可用空间分配给镜头
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
						String orig = sortedlist.get(i % spSize).trim();// 循环取，可能会存在镜头不连续的情况
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
					// 即知识库推出只有一个镜头，不管有几个可用空间
					for (int i = 0; i < shots.size(); i++) {// 目前是一个手法里的镜头们按规则分配帧数，打印
						ADLRule shot = shots.get(i);
						logger.info("actual plan,focus on camera targets:");
						doc = printRule(doc, shot);
					}
				return doc;
			} else {
				// 保证“规则推理”和“定性随机”并存
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
	 * 新版----推拍
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
		 * 根据拍摄目标选取俯仰角度属性 随机选择旋转角度属性与景别
		 */
		String idsPitch = getPitch(list);
		String maxIndexPitch = getPitch(getMaxIndexID(list));
		String listStartRotate = getRotate(list);
		String maxStartRotate = getRotate(getMaxIndexID(list));

		// 20161230,保证景别所需变化，否则maybe,start==end
		int rantmp = random.nextInt(33);
		int mudium_status = rantmp % 2;
		String startSceneType = getSceneType(list, mudium_status + 2);
		String endSceneType = getSceneType(list, mudium_status);

		ArrayList<String> IDs = getIDs(list);
		ArrayList<String> maxIndexID = getIDs(getMaxIndexID(list));
		ArrayList<String> targetIDs = getIDs(targetLists);
		ArrayList<String> unTargetIDs = getIDs(unTargetLists);

		logger.info("newpush中list中          模型个数：" + list.size());
		logger.info("newpush中list中目标模型个数：" + targetSize);

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
	 * 新版---拉拍
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
		logger.info("newpull中list中          模型个数：" + list.size());
		logger.info("newpull中list中目标模型个数：" + targetSize);
		/**
		 * 根据拍摄目标选取俯仰角度属性 随机选择旋转角度属性与景别 均是两个
		 */
		String idsPitch = getPitch(list);
		String maxIndexPitch = getPitch(getMaxIndexID(list));

		String listStartRotate = getRotate(list);
		String maxStartRotate = getRotate(getMaxIndexID(list));

		// 20161230,保证景别所需变化，否则maybe,start==end
		int rantmp = random.nextInt(33);
		int medium_status = rantmp % 2;
		String startSceneType = getSceneType(list, medium_status);
		String endSceneType = getSceneType(list, medium_status + 2);

		ArrayList<String> IDs = getIDs(list);
		ArrayList<String> maxIndexID = getIDs(getMaxIndexID(list));// 遇到两个模型，分数index相等时，会取第二个模型为maxID
		ArrayList<String> targetIDs = getIDs(targetLists);
		ArrayList<String> unTargetIDs = getIDs(unTargetLists);
		/**
		 * 拍摄手法组合方式
		 */

		if (targetSize > 0 && unTargetSize > 0) {
			// 存在非目标模型，先挑选可用空间上的istartget=1的模型拍摄，再给可用空间上的所有模型ID一个pull镜头
			doc = printRule(doc, cameraName, 1, "Fix", targetIDs, getTargetName(list, targetIDs), maxIndexPitch,
					maxIndexPitch, maxStartRotate, maxStartRotate, startSceneType, startSceneType, startframe,
					startframe + (endframe - startframe) / 2, startall + startframe, startall + startframe
							+ (endframe - startframe) / 2);
			doc = printRule(doc, cameraName, 0, "Pull", IDs, getTargetName(list, IDs), idsPitch, idsPitch,
					listStartRotate, listStartRotate, startSceneType, endSceneType, startframe
							+ (endframe - startframe) / 2 + 1, endframe, startall + startframe
							+ (endframe - startframe) / 2 + 1, startall + endframe);
		} else if (targetSize > 0) {// 即存在模型istartget=1
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
	 * 新版--旋转拍摄
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
		 * 根据拍摄目标选取俯仰角度属性 随机选择旋转角度属性与景别
		 */
		String idsPitch = getPitch(list);
		String startRotate = getRotate(list);
		String endRotate = getRotate(list);
		String startSceneType = getSceneType(list, random.nextInt(33) % 4);

		ArrayList<String> IDs = getIDs(list);
		ArrayList<String> targetIDs = getIDs(targetLists);
		/**
		 * 拍摄手法组合方式
		 */

		logger.info("newrotate中list中          模型个数：" + list.size());
		logger.info("newrotate中list中目标模型个数：" + targetSize);
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
		ArrayList<SceneSpace> targetLists = getTargetID(list, 1);// 其实确定已经是目标模型了
		int targetSize = targetLists.size();
		/**
		 * 根据拍摄目标选取俯仰角度属性 随机选择旋转角度属性与景别
		 */
		String idsPitch = getPitch(list);// 根据拍摄目标选取俯仰角度属性
		String startRotate = getRotate(list);// 根据拍摄目标选择旋转角度属性
		String startSceneType = getSceneType(list, random.nextInt(33) % 4);// 随机选择景别属性，fix：start
																			// end相同

		ArrayList<String> IDs = getIDs(list);
		ArrayList<String> targetIDs = getIDs(targetLists);
		/**
		 * 拍摄手法组合方式
		 */

		if (targetSize > 0) {
			// 可能不大于0吗？。。。
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
	 * 取得存在模型isTarget=1的可用空间
	 * 
	 * @param list
	 * @return
	 */
	public ArrayList<String> getTargetInSpaceList(ArrayList<SceneSpace> list) {
		ArrayList<String> targetSpaceList = new ArrayList<String>();
		logger.info("场景中存在isTarget=1的空间有：");
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
					(endFrame - startFrame) / 2 + startFrame);// 得分最大的，99%是istarget=1
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
	 * 判断模型列表中模型是否有位移
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

			// 如果该条规则有actionName参数，则获取该参数值，进行正则匹配
			if (null != rule.attributeValue("actionName")) {
				match1 = pattern1.matcher(rule.attributeValue("actionName"));
				match2 = pattern1.matcher(rule.attributeValue("actionName"));
				flag = match1.find() || match2.find();
			}
			// 如果由上if判断出，该条规则是walk类有位移的动作，则获取该条规则的 modelName
			if (null != rule.attributeValue("ruleType") && rule.attributeValue("ruleType").equals("addActionToMa")
					&& flag) {
				// 取得动作这条有位移的语句后，判断该model是否在modellist中
				String modelnameAction = rule.attributeValue("usedModelInMa");// eg.
																				// M_boy.ma
				if (null != modelnameAction) {
					String modelname = modelnameAction;
					if (modelNamelist.contains(modelname))// 摄像机需要拍摄的系列目标中是否包含该有位移的model
						return true;
					else
						continue;
				}
			}
		}
		return false;
	}

	/**
	 * 从adl里读取maFrame属性值
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
	 * dxf.新版
	 * <p>
	 * 从知识库获取规则
	 * 
	 * @return
	 */
	@SuppressWarnings("finally")
	public ArrayList<ADLRule> getADLRulesFromOWL(int startframe, int endframe, Document doc) {
		ArrayList<ADLRule> ADLRules = new ArrayList<ADLRule>();
		try {
			logger.info("清理知识库中camera相关实例的干扰属性：start");
			CameraSWRL.refreshHLCPandShot(this.PREFIX_OWL, this.owlModel,
					"C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl");
			// 程序中更新的是总库，因此我们是在操作总库，而不是camera库，camera只要定义好相关类和规则即可
			logger.info("清理知识库中camera相关实例的干扰属性:end");

			OWLNamedClass superclass = owlModel.getOWLNamedClass(PREFIX_OWL + "HLCPSet");// 得到高级摄影原语类
			OWLDatatypeProperty usedHLCP = owlModel.getOWLDatatypeProperty(PREFIX_OWL + "usedHLCP");
			OWLObjectProperty HLCPShots = owlModel.getOWLObjectProperty(PREFIX_OWL + "HLCPShots");
			// 拍摄手法的全局镜头方向，每个镜头围绕这个方向,但是该意图在自定义hlcp的对应镜头规则时，即已经体现，在此为多此一举
			// OWLDatatypeProperty HLCPDirection =
			// owlModel.getOWLDatatypeProperty(PREFIX_OWL + "HLCPDirection");
			// OWLNamedClass camera_shot = owlModel.getOWLNamedClass(PREFIX_OWL
			// + "camera_shot");// 得到镜头实例类

			logger.info("读取知识库中CameraSWRL_HLCP_开头规则：start");
			ArrayList<String> CameraRules = CameraSWRL.getSWRLRules(owlModel, CameraSWRL_HLCP_);
			logger.info("读取知识库中CameraSWRL_HLCP_开头规则：end");
			ArrayList<String> rulesName = new ArrayList<String>();// 获得得分的hlcp规则集合，即匹配当前条件的规则

			/**
			 * 第一层 执行所有CameraSWRL_HLCP_规则----开始
			 */
			for (int i = 0; i < CameraRules.size(); i++) {
				logger.info("执行第" + i + "条CameraSWRL_HLCP_开头规则：start");
				CameraSWRL.executeSWRLEngine1(owlModel, CameraRules.get(i));// 每条单独执行
				logger.info("执行第" + i + "条CameraSWRL_HLCP_开头规则：end");
				Collection<?> clo = superclass.getInstances(true);
				/**
				 * 得到被该条规则推理出的HLCP实例,包含推理出该hlcp包含的 镜头实例 目标实例，拍摄大致方向，若在对应shot
				 * rule中修改 镜头实例，报错，20170827
				 */
				for (Iterator<?> hlcps = clo.iterator(); hlcps.hasNext();) {// 对于所有HLCP实例，不为0即可跳出，因为一条规则只会推出一条HLCP
					OWLIndividual hlcpindi = (OWLIndividual) hlcps.next();
					int curMax = hlcpindi.getPropertyValueCount(usedHLCP);
					if (curMax > 0) {
						// maxHLCP = curMax;
						rulesName.add(CameraRules.get(i));
						break;
					}
				}
				// 执行完，删除写到知识库实例的hlcp属性值；
				logger.info("执行完CameraSWRL_HLCP_规则后，重置实例属性值：start");
				CameraSWRL.refreshHLCP(this.PREFIX_OWL, this.owlModel,
						"C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl");
				logger.info("执行完CameraSWRL_HLCP_规则后，重置实例属性值：end");
			}
			/**
			 * 执行所有CameraSWRL_HLCP_规则-----结束
			 */

			/**
			 * 找到usedhlcp得分!=0 的规则集合rulesName里的任意一条，执行，并找到再次执行单条规则后的HLCP实例----开始
			 */
			String hlcpRuleSelected = "";// 最终随机选定的hlcp规则
			OWLIndividual hlcpindiSelected = null;// hlcpRuleSelected推理出的hlcp实例
			if (!rulesName.isEmpty()) {
				Random random = new Random(System.currentTimeMillis());
				int pos = random.nextInt(rulesName.size());
				hlcpRuleSelected = rulesName.get(pos);
				// hlcpRuleSelected = "CameraSWRL_HLCP_Coaxile_3";//
				// 测试CameraSWRL_HLCP_Coaxile_3

				logger.info("执行选定的CameraSWRL_HLCP_规则：start");
				CameraSWRL.executeSWRLEngine1(owlModel, hlcpRuleSelected);
				logger.info("执行选定的CameraSWRL_HLCP_规则：end");

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
			 * 找到usedhlcp得分!=0 的规则集合rulesName里的任意一条，执行，并找到再次执行单条规则后的HLCP实例----结束
			 * 第一层结束
			 */

			/**
			 * 第二层，执行上面选取的HLCP对应的多条规则中的随机一个；
			 * 但是怎么确定刚才执行的hlcp是哪一类？hlcp取名字是对应着自己的作用即可
			 * 例如：获取所有相关CameraSWRL_Shot_OverShoulder
			 * （CameraSWRL_Shot_OverShoulder_3
			 * ，CameraSWRL_Shot_OverShoulder_2，CameraSWRL_Shot_OverShoulder_1）
			 * 随机挑选一条规则
			 */

			// 第一层没有推出任何相应的HLCP规则情况
			if (hlcpRuleSelected == null || hlcpRuleSelected.length() < 16) {
				logger.info("没有相应HLCP规则，转为第一版规划");
				return ADLRules;
			}

			String hlcpMethod = hlcpRuleSelected.substring(16);// CameraSWRL_HLCP_In_Contra---In_Contra
																// //
																// CameraSWRL_HLCP_In_Contra_1---In_Contra_1
			logger.info("读取选定的CameraSWRL_HLCP_规则对应的多条CameraSWRL_Shot_规则：start"); // CameraSWRL_Shot_In_Contra_3
			ArrayList<String> shotRules = CameraSWRL.getSWRLRules(owlModel, CameraSWRL_Shot_ + hlcpMethod);
			logger.info("读取选定的CameraSWRL_HLCP_规则对应的多条CameraSWRL_Shot_规则：end");

			String shotRuleSelected = "";
			if (shotRules.size() != 0) {
				Random random = new Random(System.currentTimeMillis());
				int pos = random.nextInt(shotRules.size());
				shotRuleSelected = shotRules.get(pos);
				logger.info("执行选定的一条CameraSWRL_Shot_规则：start");
				CameraSWRL.executeSWRLEngine1(owlModel, shotRuleSelected); // 只执行一条没必要刷新
				logger.info("执行选定的一条CameraSWRL_Shot_规则：end");
			}
			/**
			 * 第二层结束
			 */

			if (hlcpindiSelected != null) {
				logger.info("hlcp手法：" + hlcpMethod + "；具体hlcp实例：" + hlcpindiSelected.getBrowserText() + ";shotRule实例："
						+ shotRuleSelected);
				Collection<?> HLCPshots = (Collection<?>) hlcpindiSelected.getPropertyValues(HLCPShots);
				OWLObjectProperty HLCPDirection = owlModel.getOWLObjectProperty(PREFIX_OWL + "HLCPDirection");// 拍摄手法的全局镜头方向，每个镜头围绕这个方向
				OWLObjectProperty ShotPitch = owlModel.getOWLObjectProperty(PREFIX_OWL + "ShotPitch");
				OWLObjectProperty ShotRotate = owlModel.getOWLObjectProperty(PREFIX_OWL + "ShotRotate");
				OWLObjectProperty ShotScale = owlModel.getOWLObjectProperty(PREFIX_OWL + "ShotScale");
				OWLObjectProperty ShotTime = owlModel.getOWLObjectProperty(PREFIX_OWL + "ShotTime");
				OWLDatatypeProperty ShotTargets = owlModel.getOWLDatatypeProperty(PREFIX_OWL + "ShotTargets");
				OWLObjectProperty first_shot = owlModel.getOWLObjectProperty(PREFIX_OWL + "first_shot");
				OWLObjectProperty next_shot = owlModel.getOWLObjectProperty(PREFIX_OWL + "next_shot");

				// 上述对象属性所代表的 数据属性的具体值 ，eg,
				// Pitch类下子类LookupPitch的属性PitchAttr=lookup
				OWLDatatypeProperty HLCPDirectionattr = owlModel.getOWLDatatypeProperty(PREFIX_OWL + "DirectionAttr");
				OWLDatatypeProperty ShotPitchattr = owlModel.getOWLDatatypeProperty(PREFIX_OWL + "PitchAttr");
				OWLDatatypeProperty ShotRotateattr = owlModel.getOWLDatatypeProperty(PREFIX_OWL + "YawAttr");
				OWLDatatypeProperty ShotScaleattr = owlModel.getOWLDatatypeProperty(PREFIX_OWL + "ScaleAttr");
				OWLDatatypeProperty ShotTimeattr = owlModel.getOWLDatatypeProperty(PREFIX_OWL + "TimeAttr");

				int countShot = 0;
				// 对推荐的HLCP实例所含镜头的iterator
				// startFrame从0开始
				int start = startframe, end = startframe;
				int remainder = endframe - startframe;// 未分配的余下帧数
				for (Iterator<?> it = HLCPshots.iterator(); it.hasNext();) {
					ADLRule rule = new ADLRule();// 该镜头的ADLRule
					OWLIndividual shot = (OWLIndividual) it.next();// 知识库中的镜头实例
					Collection<?> shotTargets = shot.getPropertyValues(ShotTargets);// shot实例的targets
																					// object
					ArrayList<String> TargetIDList = new ArrayList<String>();// shot实例的targets
																				// object
																				// 名称
																				// 换！！改为id

					/**
					 * 极易为null:
					 * <p>
					 * 1,对应hlcp规则的shot规则 两类规则中的拍摄手法的名称一定要一样！！
					 * 2,HLCP规则中概括的该手法的镜头类别与个数，是否在相应的shot规则中进行了设置(不可新建镜头)；
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
					String time = (String) timeIndi.getPropertyValue(ShotTimeattr);// 知识库中的
																					// “镜头实例”
																					// 相关联的
																					// “时间实例”
																					// 的时间值
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

					// shot内的target集合
					for (Iterator<?> target = shotTargets.iterator(); target.hasNext();) {
						String id = (String) target.next();
						TargetIDList.add(id);
					}

					Collections.sort(TargetIDList);

					@SuppressWarnings("deprecation")
					OWLNamedClass shotClass = (OWLNamedClass) shot.getDirectType();// 得到shot实例的所属类类名
					String shotInOWL = shotClass.getLocalName();

					rule.setShotPlan(shotInOWL);// 得到镜头实例所属shot类的名称
					ArrayList<String> target = getNamesByID(TargetIDList, doc);
					String topicInDoc = getTopicFromDoc(doc);
					rule.setTarget(target);// 每个镜头实例的target
					rule.setUsedModelID(TargetIDList);
					rule.setTopic(topicInDoc);// 短信的主题，可以为空串
					String plan = shotInOWL;

					char postfix = (char) ('A' + countShot);
					String shotName = "newCamera" + postfix;
					rule.setShotName(shotName);
					countShot++;// 镜头个数增加

					rule.setStartPitch(pitch);
					rule.setStartYaw(rotate);
					rule.setStartShotType(scale);

					// 根据plan，合理设置俯仰，旋转，景别的 start ,end值
					setEnd(plan, rule);

					// 一个镜头可能会有多个target,所以输出是个集合
					// 镜头时间设置暂时没有按规则的比例分配，目前是平均分配，已修改按比例分配
					rule.setStartframe(start);
					rule.setEndframe(end);
					// CameraSWRL_HLCP_In_Contra_1---In_Contra_1
					rule.setHlcp(hlcpMethod);
					rule.setOrder(ADLRules.size() + 1);
					ADLRules.add(rule);

					logger.info(rule.toString());
				}
				if (remainder != 0) {
					ADLRule rule = ADLRules.get(ADLRules.size() - 1).clone();// 注意，此处是浅复制
					rule.setStartframe(rule.getEndframe() + 1);
					rule.setEndframe(this.maFrame);
					rule.setOrder(ADLRules.size() + 1);

					char postfix = (char) ('A' + countShot);
					String shotName = "newCamera" + postfix;
					rule.setShotName(shotName);// eg,Coaxile_3（push,fix,fix）第二个fix，即在此处添加
					countShot++;// 镜头个数增加

					ADLRules.add(rule);

					logger.info("If the SWRL rule include two same shots,we necessarily add an random rule to patch the time remainder:");
					logger.info("规则中包含相同镜头,增加下行镜头：");
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

	// 根据plan，合理设置俯仰，旋转，景别的 start ,end值

	public void setEnd(String plan, ADLRule rule) {
		String pitch = rule.getStartPitch();
		String rotate = rule.getStartYaw();
		String scale = rule.getStartShotType();
		if (plan.equalsIgnoreCase("Fix")) {
			rule.setEndPitch(pitch);
			rule.setEndYaw(rotate);
			rule.setEndShotType(scale);
		} else if (plan.equalsIgnoreCase("Push")) {
			rule.setEndPitch(pitch);// 随意
			rule.setEndYaw(rotate);// 不变
			rule.setEndShotType(randomScale(scale, false));// 随机减小
		} else if (plan.equalsIgnoreCase("Pull")) {
			rule.setEndPitch(pitch);// 随意
			rule.setEndYaw(rotate);// 不变
			rule.setEndShotType(randomScale(scale, true));// 随机增大
		} else if (plan.equalsIgnoreCase("Transform")) {
			rule.setEndPitch(pitch);// 随意
			rule.setEndYaw(rotate);// 不变
			rule.setEndShotType(randomScale(scale, true));// 随机增大
		} else if (plan.equalsIgnoreCase("Rotate")) {
			rule.setEndPitch(pitch);// 随意
			rule.setEndYaw(randomRotate(rotate));// 随机，但不是本身
			rule.setEndShotType(randomScale(scale, false));// 随机减小
		} else if (plan.equalsIgnoreCase("Follow")) {
			rule.setEndPitch(pitch);// 随意
			rule.setEndYaw(rotate);// 不变
			rule.setEndShotType(scale);
		}
	}

	/**
	 * 由flag判断随机返回较大，较小值；flag为true，返回较大； false，返回较小
	 * 
	 * @param pitch
	 * @param flag
	 * @return
	 */
	public String randomScale(String scale, boolean flag) {
		int pos = 0;
		ArrayList<String> scales = new ArrayList<String>();
		// scales.add("closeup");//closeup最好不用，拍摄效果极差，尤其是拍摄人物时
		scales.add("medium");
		scales.add("full");
		scales.add("establish");
		if (flag) {
			pos = scales.size() - 1;
		}
		if (scales.contains(scale)) {
			int index = scales.indexOf(scale);
			if (flag) {// 返回较大景别
				int add = new Random(System.currentTimeMillis()).nextInt(scales.size() - index);
				if (add == 0) {
					pos = index + 1;// 防止景别不变
				} else
					pos = index + add;
			} else {// 返回较小景别
				if (index == 0)// 景别已是最小
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
	 * 由flag判断随机返回较大，较小值；flag为true，返回上； false，返回下
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
			if (flag) {// 返回较大
				int add = new Random(System.currentTimeMillis()).nextInt(pitches.size() - index);
				if (add == 0) {
					pos = index + 1;// 防止不变
				} else
					pos = index + add;
			} else {// 返回较小
				if (index == 0)// 已是最小
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
		 * 镜头帧数设置 start
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
			logger.error("shotTime比例设置错误");
			throw new Exception("shotTime比例设置错误");
		}
		/**
		 * 镜头帧数设置 end
		 */
		// 解决因仅剩几帧而引起的多分配一个镜头
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
					if (modelid != null && modelid.equals(IDs.get(i))) {// 定性中的
																		// id
																		// ==规则中id时
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
	 * 判断该 modelNamelist中是否存在在doc中规划表情，若存在规划表情，将拍摄目标替换为表情人物
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
			// 找出ADL中添加的表情，检测表情人物模型是否在modelNamelist中，在则跳出，返回该模型的名字（一个ADL最多分配一个这样的镜头）
			if (null != rule.attributeValue("ruleType") && rule.attributeValue("ruleType").equals("addExpressionToMa")) {
				String modelnameExpresison = rule.attributeValue("usedModelInMa");// eg.
																					// M_boy.ma
				if (null != modelnameExpresison) {
					if (modelNamelist.contains(modelnameExpresison)) {// 摄像机需要拍摄的系列目标中是否包含该有表情的model
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
	 * CameraToXML的内部类
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
		 * 完成ADLRule类的 shadow copy 浅复制
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
