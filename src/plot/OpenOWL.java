package plot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.FrameID;
//import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLFactoryException;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;

public class OpenOWL {
	public static OWLModel OpenZhangOWL() throws OntologyLoadException {
		String url = "file:///C:/ontologyOWL/rootOWL/sumoOWL2/sumo_phone3.owl";
		OWLModel model = null;
		try {
			model = ProtegeOWL.createJenaOWLModelFromURI(url);// ͨ��url���owlģ��
																// }
		} catch (Exception OpenMyOWL) {
			model = ProtegeOWL.createJenaOWLModelFromURI(url);
		}
		return model;
	}

	public static OWLModel OpenQiuOWL() throws OntologyLoadException {
		String urlq = "file:///C:/ontologyOWL/AllOwlFile/ActionOWL/ActionPart.owl";
		OWLModel owlModelQ = null;
		try {
			owlModelQ = ProtegeOWL.createJenaOWLModelFromURI(urlq);// ͨ��url���owlģ��
		} catch (Exception OpenQiuOWL) {
			owlModelQ = ProtegeOWL.createJenaOWLModelFromURI(urlq);
		}
		return owlModelQ;

	}

	public static OWLModel OpenZhengOWL() throws OntologyLoadException {
		String urlZheng = "file:///C:/ontologyOWL/AllOwlFile/zhenOWL/10.1(test).owl";
		OWLModel owlModelZheng = null;
		try {
			owlModelZheng = ProtegeOWL.createJenaOWLModelFromURI(urlZheng);
		} catch (Exception OpenZhengOWL) {
			owlModelZheng = ProtegeOWL.createJenaOWLModelFromURI(urlZheng);
		}
		return owlModelZheng;
	}

	public static OWLModel OpenZhaoOWL() throws OntologyLoadException {

		String urlZ = "file:///C:/ontologyOWL/AllOwlFile/zhaoOWL/ColorAndLight.owl";
		OWLModel owlModelzhao = null;
		try {
			owlModelzhao = ProtegeOWL.createJenaOWLModelFromURI(urlZ);
		} catch (Exception OpenZhaoOWL) {
			owlModelzhao = ProtegeOWL.createJenaOWLModelFromURI(urlZ);
		}
		return owlModelzhao;
	}
}
