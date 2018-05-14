package plot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLModel;

public class CameraToXMl_test {
	public static void testCameraStart() {
		CameraToXML camera = new CameraToXML();
		String filepath = "F:\\eclipse_64\\8690.xml";
		// String
		// OWLpath="file:///C://ontologyOWL//AllOwlFile//DxfCamera//camera.owl";
		String OWLpath = "file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";
		OWLModel owlModel;
		try {
			owlModel = ProtegeOWL.createJenaOWLModelFromURI(OWLpath);// model本体库模型sumo_phone3.owl
			// String maName="happy_mm.ma";
			String maName = "cry_mm.ma";
			File file = new File(filepath);
			SAXReader saxReader = new SAXReader();
			Document doc = saxReader.read(file);
			Document document1 = camera.CreateCamera(owlModel, maName, doc);
			XMLWriter writer = new XMLWriter(new FileWriter(filepath));
			writer.write(document1);
			writer.close();// 忘记关闭写入，竟然是0kb

		} catch (OntologyLoadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		testCameraStart();
	}

}
