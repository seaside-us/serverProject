package plot;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngineFactory;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLFactoryException;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;

public class CameraSWRL {
	static Logger logger = Logger.getLogger(CameraSWRL.class.getName());

	/**
	 * An instance of the SWRLFactory can be created by passing an OWL model to
	 * its constructor
	 */
	public static SWRLFactory createSWRLFactory(OWLModel model) {
		SWRLFactory factory = new SWRLFactory(model);
		return factory;
	}

	/**
	 * 获得每条规则
	 */
	public static void getSWRLRules(SWRLFactory factory) {
		Collection<?> rules = factory.getImps();
		for (Iterator<?> its = rules.iterator(); its.hasNext();) {
			SWRLImp swrlImp = (SWRLImp) its.next();
			logger.info("规则:" + swrlImp.getBrowserText());
		}
	}

	/**
	 * 创建rule engine
	 * 
	 * @throws SWRLRuleEngineException
	 */
	public static SWRLRuleEngine createRuleEngine(OWLModel model) throws SWRLRuleEngineException {
		SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create("SWRLJessBridge", model);
		// SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create(model);
		return ruleEngine;
	}

	/*
	 * 执行规则的方法二，主要是把规则分成几部分，分布运行规则 现在想的是规则可以分为4部分，1：使主题实例的hasMa属性都有值
	 * 2、根据主题和模板原子往相应的ma中做相应的变化：主要是添加一些东西，如节日，模板时晚上，可以添加烟花的效果
	 * 3、ma的全局规则（如帽子的颜色可以换，换个同种类型的帽子等）：主要用来对ma中一些模型进行变化
	 * 4、ma的局部规则，这是针对每个ma做的，对每个ma进行增删改 name 可以是主题的名字，ma全局规则的名字，也可以是ma局部规则的名字
	 * 
	 * @throws SWRLRuleEngineException
	 * 
	 * @throws SWRLFactoryException
	 */
	public static boolean executeSWRLEngine1(OWLModel model, String rulename) throws SWRLRuleEngineException,
			SWRLFactoryException {
		SWRLRuleEngine ruleEngine = createRuleEngine(model);
		ruleEngine.reset();
		SWRLFactory factory = createSWRLFactory(model);
		factory.disableAll();
		boolean temp = false;
		ArrayList<String> impName = new ArrayList<String>();// 用来存储有"_"的规则
		Iterator<SWRLImp> iter = factory.getImps().iterator();
		while (iter.hasNext()) {
			SWRLImp imp = (SWRLImp) iter.next();
			// 處理第一類規則
			if (imp.getLocalName().startsWith(rulename) == true) {
				imp.enable();
				impName.add(imp.getLocalName());
				logger.info("规则" + impName.size() + ":" + imp.getLocalName());
				temp = true;
			}
		}
		ruleEngine.infer();// 仅是推理，没有写入知识库
		ruleEngine.run();
		ruleEngine.writeInferredKnowledge2OWL();
		// saveOWLFile((JenaOWLModel) model,
		// "C://ontologyOWL//AllOwlFile//DxfCamera//camera.owl");
		saveOWLFile((JenaOWLModel) model, "C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl");
		ruleEngine.reset();

		return temp;
	}

	/**
	 * 保存owl文件
	 */
	public static void saveOWLFile(JenaOWLModel owlModel, String fileName) {
		Collection<Object> errors = new ArrayList<Object>();
		owlModel.save(new File(fileName).toURI(), FileUtils.langXMLAbbrev, errors);
		logger.info("File saved with " + errors.size() + " errors.");
	}

	/*
	 * 读取CameraSWRL_规则
	 */
	public static ArrayList<String> getSWRLRules(OWLModel model, String rulename) throws SWRLRuleEngineException {
		SWRLFactory factory = createSWRLFactory(model);
		Collection<?> rules = factory.getImps();
		ArrayList<String> CameraImpNames = new ArrayList<String>();// 用来存储有"CameraSWRL_"的规则
		for (Iterator<?> its = rules.iterator(); its.hasNext();) {
			SWRLImp swrlImp = (SWRLImp) its.next();
			if (swrlImp.getLocalName().startsWith(rulename) == true) {
				CameraImpNames.add(swrlImp.getLocalName());
				logger.info("读取规则" + CameraImpNames.size() + ":" + swrlImp.getLocalName());
			}
		}
		return CameraImpNames;
	}

	/**
	 * 通过直接操作总库来复原之前被修改过属性的HLCP和Shot实例；
	 * <p>
	 * 防止该次规则跑出的属性数据与之前的残留属性值 混淆
	 * <p>
	 * 但是效率问题未考虑，需部署到服务器上测试
	 * 
	 * @return
	 * @throws OntologyLoadException
	 */
	public static boolean refreshHLCPandShot(String prefix, OWLModel owlModel, String filepath)
			throws OntologyLoadException {
		// String owlpath =
		// "file:///C://ontologyOWL//AllOwlFile//DxfCamera//camera.owl";
		// OWLModel CameraowlModel =
		// ProtegeOWL.createJenaOWLModelFromURI(owlpath);

		OWLNamedClass hlcpclass = owlModel.getOWLNamedClass(prefix + "HLCPSet");// 得到高级摄影原语类
		Collection<?> hlcpclo = hlcpclass.getInstances(true);

		OWLNamedClass shotclass = owlModel.getOWLNamedClass(prefix + "camera_shot");// 得到高级摄影原语类
		Collection<?> shotclo = shotclass.getInstances(true);

		for (Iterator<?> hlcps = hlcpclo.iterator(); hlcps.hasNext();) {
			OWLIndividual hlcpindi = (OWLIndividual) hlcps.next();
			String hlcpindiname = hlcpindi.getLocalName();
			@SuppressWarnings("deprecation")
			OWLNamedClass hlcpindiClass = (OWLNamedClass) hlcpindi.getDirectType();// 得到实例的所属类
			logger.info("HLCP实例：" + hlcpindiname);
			hlcpindi.delete();// 删除被操作的实例
			hlcpindiClass.createOWLIndividual(hlcpindiname);
		}

		for (Iterator<?> shots = shotclo.iterator(); shots.hasNext();) {
			OWLIndividual shotindi = (OWLIndividual) shots.next();
			String shotindiname = shotindi.getLocalName();
			@SuppressWarnings("deprecation")
			OWLNamedClass shotindiClass = (OWLNamedClass) shotindi.getDirectType();// 得到实例的所属类
			logger.info("Shot实例:" + shotindiname);
			shotindi.delete();// 删除被操作的实例
			shotindiClass.createOWLIndividual(shotindiname);
		}
		// CameraSWRL.saveOWLFile((JenaOWLModel) owlModel,
		// "C://ontologyOWL//AllOwlFile//DxfCamera//camera.owl");
		CameraSWRL.saveOWLFile((JenaOWLModel) owlModel, filepath);
		return true;

	}

	/**
	 * 通过直接操作库来重建被修改的HLCP实例
	 * 
	 * @return
	 * @throws OntologyLoadException
	 */
	public static boolean refreshHLCP(String prefix, OWLModel owlModel, String filepath) throws OntologyLoadException {
		OWLNamedClass hlcpclass = owlModel.getOWLNamedClass(prefix + "HLCPSet");// 得到高级摄影原语类
		Collection<?> hlcpclo = hlcpclass.getInstances(true);
		for (Iterator<?> hlcps = hlcpclo.iterator(); hlcps.hasNext();) {
			OWLIndividual hlcpindi = (OWLIndividual) hlcps.next();
			String hlcpindiname = hlcpindi.getLocalName();
			@SuppressWarnings("deprecation")
			OWLNamedClass hlcpindiClass = (OWLNamedClass) hlcpindi.getDirectType();// 得到实例的所属类
			logger.info("HLCP实例：" + hlcpindiname);
			hlcpindi.delete();// 删除被操作的实例
			hlcpindiClass.createOWLIndividual(hlcpindiname);// 再次创建被删除的实例
		}
		CameraSWRL.saveOWLFile((JenaOWLModel) owlModel, filepath);
		return true;

	}

	/**
	 * 通过直接操作库来重建被修改的Shot实例
	 * 
	 * @return
	 * @throws OntologyLoadException
	 */
	public static boolean refreshShot(String prefix, OWLModel owlModel, String filepath) throws OntologyLoadException {
		OWLNamedClass shotclass = owlModel.getOWLNamedClass(prefix + "camera_shot");// 得到高级摄影原语类
		Collection<?> shotclo = shotclass.getInstances(true);
		for (Iterator<?> shots = shotclo.iterator(); shots.hasNext();) {
			OWLIndividual shotindi = (OWLIndividual) shots.next();
			String shotindiname = shotindi.getLocalName();
			@SuppressWarnings("deprecation")
			OWLNamedClass shotindiClass = (OWLNamedClass) shotindi.getDirectType();// 得到实例的所属类
			logger.info("Shot实例:" + shotindiname);
			shotindi.delete();// 删除被操作的实例
			shotindiClass.createOWLIndividual(shotindiname);
		}
		CameraSWRL.saveOWLFile((JenaOWLModel) owlModel, filepath);
		return true;

	}
}
