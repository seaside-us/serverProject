package plot;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;

public class Layout {
	
	public static void main(String[] args) throws Exception {
		  
		  //Properties props = System.getProperties();
		  //props.setProperty("proxySet", "true");
		  //在不同的机器上，写上该机器的ip地址
		 // props.setProperty("http.proxyHost", "172.21.13.170");
		 // props.setProperty("http.proxyPort", "808");

		  String uri = "file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";
		  OWLModel model=ProtegeOWL.createJenaOWLModelFromURI(uri);
		  String filePath = "F:\\test.xml";
		  Document document=Readxml.read(filePath);
          Layout lo=new Layout();
          Document document1=lo.setLayout(model,"p2:DepartPlot.ma",document);
          Readxml.savexml(filePath, document1);
          System.out.println("结束");
         
        }
	public static Document setLayout(OWLModel layoutowl,String maName,Document doc) throws OntologyLoadException 
	{
		if("nothing.ma".equals(maName)||"empty.ma".equals(maName)||"".equals(maName))
			return doc;
		ArrayList<HashMap<String,String>> areashape=new ArrayList();
		//读取上面输出的xml文件中的add规则中的信息存到类数组中
		 Element root= doc.getRootElement();
	     Element name = root.element("maName");
	     String ieTopic = doc.getRootElement().element("maName").attributeValue("topic");
	     
	     ArrayList al=Readxml.getAddinfo(name);//获取xml中add中的信息
	     ArrayList a=Readxml.getSpace2(al);//处理上面输出的信息将重复的合并后去掉其中一个   
	  
	     ArrayList<OWLIndividual> spIndividual=ReadOwl.getInstance(layoutowl, "SceneSpace");	//读取owl文件中sencespace下面的所有实例
	    // ArrayList<OWLIndividual> s=SlectLayout.selectInstance(spIndividual,a);//从sencespace下面的所有实例spIndividual中选出，出现在xml中的实例并保存到数组S中。
	    //System.out.println("spIndividual="+spIndividual);
	     //remove the Floor space  because ,floor don't need the space
	     
	     for(int i=0;i<a.size();i++){
	    	 SceneSpace sp=(SceneSpace) a.get(i);
	    	 if(sp.getSpname().contains("Floor")){
	    		 a.remove(i);
	    	 }
	     }
	     //ArrayList<String> layOut=SlectLayout.selectLayout(layoutowl, spIndividual, a);
	     ArrayList<String> layEffect=SelectLayout2.selectLayout2(layoutowl, ieTopic, a,areashape);
	     
		 for(int i=0;i<areashape.size();i++){
			 System.out.println("第"+(i+1)+"个可用空间");
			 System.out.println("layoutEffect: "+layEffect.get(i));
			 HashMap testmap=areashape.get(i);
			 Iterator iter = testmap.entrySet().iterator();
			 while (iter.hasNext()) {
				 Entry<String, String> entry = (Entry) iter.next();
				 System.out.println(entry.getKey()+": "+entry.getValue());
			 }
		 }
	     
		 for(int i=0;i<a.size();i++){
			 SceneSpace sp=(SceneSpace)a.get(i);
			 HashMap<String,String> testmap=areashape.get(i);
			 String layeffect=layEffect.get(i);
			 Readxml.writexml2(name,sp,testmap,layeffect);
		 }
	     
	     //System.out.println("layOut"+layOut);
	     //ArrayList<String> layShape=SlectLayout.getLayShap(layoutowl, layOut);
	     //System.out.println("layOut"+layShape);
	     //Readxml.writexml(name,a,layOut,layShape);
	     //Readxml.writexml2(name,al,layEffect,areashape);
	     System.out.println("1");
        return  doc;	 
		 
    }



}
