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
	 * ���ÿ������
	 */
	public static void getSWRLRules(SWRLFactory factory) {
		Collection<?> rules = factory.getImps();
		for (Iterator<?> its = rules.iterator(); its.hasNext();) {
			SWRLImp swrlImp = (SWRLImp) its.next();
			logger.info("����:" + swrlImp.getBrowserText());
		}
	}

	/**
	 * ����rule engine
	 * 
	 * @throws SWRLRuleEngineException
	 */
	public static SWRLRuleEngine createRuleEngine(OWLModel model) throws SWRLRuleEngineException {
		SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create("SWRLJessBridge", model);
		// SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create(model);
		return ruleEngine;
	}

	/*
	 * ִ�й���ķ���������Ҫ�ǰѹ���ֳɼ����֣��ֲ����й��� ��������ǹ�����Է�Ϊ4���֣�1��ʹ����ʵ����hasMa���Զ���ֵ
	 * 2�����������ģ��ԭ������Ӧ��ma������Ӧ�ı仯����Ҫ�����һЩ����������գ�ģ��ʱ���ϣ���������̻���Ч��
	 * 3��ma��ȫ�ֹ�����ñ�ӵ���ɫ���Ի�������ͬ�����͵�ñ�ӵȣ�����Ҫ������ma��һЩģ�ͽ��б仯
	 * 4��ma�ľֲ������������ÿ��ma���ģ���ÿ��ma������ɾ�� name ��������������֣�maȫ�ֹ�������֣�Ҳ������ma�ֲ����������
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
		ArrayList<String> impName = new ArrayList<String>();// �����洢��"_"�Ĺ���
		Iterator<SWRLImp> iter = factory.getImps().iterator();
		while (iter.hasNext()) {
			SWRLImp imp = (SWRLImp) iter.next();
			// ̎���һ�Ҏ�t
			if (imp.getLocalName().startsWith(rulename) == true) {
				imp.enable();
				impName.add(imp.getLocalName());
				logger.info("����" + impName.size() + ":" + imp.getLocalName());
				temp = true;
			}
		}
		ruleEngine.infer();// ��������û��д��֪ʶ��
		ruleEngine.run();
		ruleEngine.writeInferredKnowledge2OWL();
		// saveOWLFile((JenaOWLModel) model,
		// "C://ontologyOWL//AllOwlFile//DxfCamera//camera.owl");
		saveOWLFile((JenaOWLModel) model, "C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl");
		ruleEngine.reset();

		return temp;
	}

	/**
	 * ����owl�ļ�
	 */
	public static void saveOWLFile(JenaOWLModel owlModel, String fileName) {
		Collection<Object> errors = new ArrayList<Object>();
		owlModel.save(new File(fileName).toURI(), FileUtils.langXMLAbbrev, errors);
		logger.info("File saved with " + errors.size() + " errors.");
	}

	/*
	 * ��ȡCameraSWRL_����
	 */
	public static ArrayList<String> getSWRLRules(OWLModel model, String rulename) throws SWRLRuleEngineException {
		SWRLFactory factory = createSWRLFactory(model);
		Collection<?> rules = factory.getImps();
		ArrayList<String> CameraImpNames = new ArrayList<String>();// �����洢��"CameraSWRL_"�Ĺ���
		for (Iterator<?> its = rules.iterator(); its.hasNext();) {
			SWRLImp swrlImp = (SWRLImp) its.next();
			if (swrlImp.getLocalName().startsWith(rulename) == true) {
				CameraImpNames.add(swrlImp.getLocalName());
				logger.info("��ȡ����" + CameraImpNames.size() + ":" + swrlImp.getLocalName());
			}
		}
		return CameraImpNames;
	}

	/**
	 * ͨ��ֱ�Ӳ����ܿ�����ԭ֮ǰ���޸Ĺ����Ե�HLCP��Shotʵ����
	 * <p>
	 * ��ֹ�ôι����ܳ�������������֮ǰ�Ĳ�������ֵ ����
	 * <p>
	 * ����Ч������δ���ǣ��貿�𵽷������ϲ���
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

		OWLNamedClass hlcpclass = owlModel.getOWLNamedClass(prefix + "HLCPSet");// �õ��߼���Ӱԭ����
		Collection<?> hlcpclo = hlcpclass.getInstances(true);

		OWLNamedClass shotclass = owlModel.getOWLNamedClass(prefix + "camera_shot");// �õ��߼���Ӱԭ����
		Collection<?> shotclo = shotclass.getInstances(true);

		for (Iterator<?> hlcps = hlcpclo.iterator(); hlcps.hasNext();) {
			OWLIndividual hlcpindi = (OWLIndividual) hlcps.next();
			String hlcpindiname = hlcpindi.getLocalName();
			@SuppressWarnings("deprecation")
			OWLNamedClass hlcpindiClass = (OWLNamedClass) hlcpindi.getDirectType();// �õ�ʵ����������
			logger.info("HLCPʵ����" + hlcpindiname);
			hlcpindi.delete();// ɾ����������ʵ��
			hlcpindiClass.createOWLIndividual(hlcpindiname);
		}

		for (Iterator<?> shots = shotclo.iterator(); shots.hasNext();) {
			OWLIndividual shotindi = (OWLIndividual) shots.next();
			String shotindiname = shotindi.getLocalName();
			@SuppressWarnings("deprecation")
			OWLNamedClass shotindiClass = (OWLNamedClass) shotindi.getDirectType();// �õ�ʵ����������
			logger.info("Shotʵ��:" + shotindiname);
			shotindi.delete();// ɾ����������ʵ��
			shotindiClass.createOWLIndividual(shotindiname);
		}
		// CameraSWRL.saveOWLFile((JenaOWLModel) owlModel,
		// "C://ontologyOWL//AllOwlFile//DxfCamera//camera.owl");
		CameraSWRL.saveOWLFile((JenaOWLModel) owlModel, filepath);
		return true;

	}

	/**
	 * ͨ��ֱ�Ӳ��������ؽ����޸ĵ�HLCPʵ��
	 * 
	 * @return
	 * @throws OntologyLoadException
	 */
	public static boolean refreshHLCP(String prefix, OWLModel owlModel, String filepath) throws OntologyLoadException {
		OWLNamedClass hlcpclass = owlModel.getOWLNamedClass(prefix + "HLCPSet");// �õ��߼���Ӱԭ����
		Collection<?> hlcpclo = hlcpclass.getInstances(true);
		for (Iterator<?> hlcps = hlcpclo.iterator(); hlcps.hasNext();) {
			OWLIndividual hlcpindi = (OWLIndividual) hlcps.next();
			String hlcpindiname = hlcpindi.getLocalName();
			@SuppressWarnings("deprecation")
			OWLNamedClass hlcpindiClass = (OWLNamedClass) hlcpindi.getDirectType();// �õ�ʵ����������
			logger.info("HLCPʵ����" + hlcpindiname);
			hlcpindi.delete();// ɾ����������ʵ��
			hlcpindiClass.createOWLIndividual(hlcpindiname);// �ٴδ�����ɾ����ʵ��
		}
		CameraSWRL.saveOWLFile((JenaOWLModel) owlModel, filepath);
		return true;

	}

	/**
	 * ͨ��ֱ�Ӳ��������ؽ����޸ĵ�Shotʵ��
	 * 
	 * @return
	 * @throws OntologyLoadException
	 */
	public static boolean refreshShot(String prefix, OWLModel owlModel, String filepath) throws OntologyLoadException {
		OWLNamedClass shotclass = owlModel.getOWLNamedClass(prefix + "camera_shot");// �õ��߼���Ӱԭ����
		Collection<?> shotclo = shotclass.getInstances(true);
		for (Iterator<?> shots = shotclo.iterator(); shots.hasNext();) {
			OWLIndividual shotindi = (OWLIndividual) shots.next();
			String shotindiname = shotindi.getLocalName();
			@SuppressWarnings("deprecation")
			OWLNamedClass shotindiClass = (OWLNamedClass) shotindi.getDirectType();// �õ�ʵ����������
			logger.info("Shotʵ��:" + shotindiname);
			shotindi.delete();// ɾ����������ʵ��
			shotindiClass.createOWLIndividual(shotindiname);
		}
		CameraSWRL.saveOWLFile((JenaOWLModel) owlModel, filepath);
		return true;

	}
}
