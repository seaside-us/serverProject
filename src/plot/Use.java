/**
 * 
 */
//package protegeuse;
package plot;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.DefaultKnowledgeBase;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngineFactory;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParseException;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLQueryEngine;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLQueryEngineFactory;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLResult;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.SQWRLException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;

public class Use {

	/**
	 * @param args
	 * @throws OntologyLoadException 
	 */
	 public  Document getDatatype(OWLNamedClass supclass,OWLModel owlModelx, OWLIndividual deformIndividual,Document document,Element ruleElm)
	 {
		 if (supclass.getBrowserText().contains("bend"))
		 {
			 OWLDatatypeProperty curvature = owlModelx.getOWLDatatypeProperty("bend_curvature");
			 String defcurvature=(String)deformIndividual.getPropertyValue(curvature);
			 OWLDatatypeProperty bendmethods= owlModelx.getOWLDatatypeProperty("bend_methods");
			 String defmethord=(String)deformIndividual.getPropertyValue(bendmethods);
			 //Bend be=new Bend(defcurvature,defmethord);
			 ruleElm.addAttribute("curvature", defcurvature);
			 //ruleElm.addAttribute("direction", defmethord);
			 ruleElm.addAttribute("bend_methods", defmethord);
			 return document;
			 
		 }else if(supclass.getBrowserText().contains("wave"))
		 {
			 OWLDatatypeProperty curvature = owlModelx.getOWLDatatypeProperty("wave_Amplitude");
			 String defcurvature=(String)deformIndividual.getPropertyValue(curvature);
			 OWLDatatypeProperty wavelength= owlModelx.getOWLDatatypeProperty("wave_wavelength");
			 String defwavelength=(String)deformIndividual.getPropertyValue(wavelength);
			 //Wave wa=new Wave(defcurvature,defwavelength);
			 ruleElm.addAttribute("curvature", defcurvature);
			 //ruleElm.addAttribute("direction", "");
			 ruleElm.addAttribute("wavelength", defwavelength);
			 return document;
		 }
		 else if(supclass.getBrowserText().contains("sine"))
		 {
			 OWLDatatypeProperty curvature = owlModelx.getOWLDatatypeProperty("sine_ampitude");
			 String defcurvature=(String)deformIndividual.getPropertyValue(curvature);
			 OWLDatatypeProperty wavelength= owlModelx.getOWLDatatypeProperty("sine_wavelength");
			 String defwavelength=(String)deformIndividual.getPropertyValue(wavelength);
			 //Sine Si=new Sine(defcurvature,defwavelength);
			 ruleElm.addAttribute("curvature", defcurvature);
			 // ruleElm.addAttribute("direction", "");
			 ruleElm.addAttribute("wavelength", defwavelength);
			 return document;
		 			 
		 }
		 else if(supclass.getBrowserText().contains("flare"))
		 {
			 OWLDatatypeProperty curvature = owlModelx.getOWLDatatypeProperty("flare_curvature");
			 String defcurvature=(String)deformIndividual.getPropertyValue(curvature);
			 OWLDatatypeProperty flaremethod= owlModelx.getOWLDatatypeProperty("flare_methods");
			 String defmethord=(String)deformIndividual.getPropertyValue(flaremethod);
			 //Flare fl=new Flare(defcurvature,defmethord);
			 ruleElm.addAttribute("curvature", defcurvature);
			 //ruleElm.addAttribute("direction", defmethord);
			 ruleElm.addAttribute("flare_methods", defmethord);
			 return document;
		 }
		 else if(supclass.getBrowserText().contains("twist"))
		 {
			 OWLDatatypeProperty curvature = owlModelx.getOWLDatatypeProperty("twist_curvature");
			 String defcurvature=(String)deformIndividual.getPropertyValue(curvature);
			 //Twist tw=new Twist(defcurvature);
			 ruleElm.addAttribute("curvature", defcurvature);
			 return document;
		 }
		 else if(supclass.getBrowserText().contains("squash"))
		 {
			 OWLDatatypeProperty curvature = owlModelx.getOWLDatatypeProperty("squash_curvature");
			 String defcurvature=(String)deformIndividual.getPropertyValue(curvature);
			 //Squash sq=new Squash(defcurvature);
			 ruleElm.addAttribute("curvature", defcurvature);
			 return document;
		 }
		return document;

	 }
	 public  Document DeformReasing(OWLModel owlModelx,Document document,String animationsceneName) throws SWRLRuleEngineException, IOException
	 {
		 //	��ȡcartoon�ಢ�����д���ʵ��
	     OWLNamedClass cartoonClass =owlModelx.getOWLNamedClass("cartoon");
	     OWLIndividual cartoonIndividual = owlModelx.getOWLIndividual("cartoon_3");
	     //��ȡ����animationscene����Ϣ
	     OWLIndividual animationsceneIndividual = owlModelx.getOWLIndividual(animationsceneName);
	     //��ȡmodels
	     OWLObjectProperty hasmodel = owlModelx.getOWLObjectProperty("p2:hasmodel");
	     if(!animationsceneIndividual.hasPropertyValue(hasmodel))
	     {
	    	 System.out.println("there is no model in  "+animationsceneName);
	    	 return document;
	     }
	     Collection models= animationsceneIndividual.getPropertyValues(hasmodel);
	     OWLIndividual[] Idmodels = (OWLIndividual[]) models.toArray(new OWLIndividual[0]);
	     //��ȡtopics
	     OWLDatatypeProperty topic = owlModelx.getOWLDatatypeProperty("p2:topicName");
	     this.havetopic="p2:"+(String)animationsceneIndividual.getPropertyValue(topic);
	     OWLNamedClass topicClass=owlModelx.getOWLNamedClass(this.havetopic);
	     Collection topics=topicClass.getInstances();
	     OWLIndividual[] Idtopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);
	     //OWLIndividual topicIndividual = owlModelx.getOWLIndividual(this.havetopic);
	     //��Ϣ���Ƹ�cartoon ʵ��
	     OWLObjectProperty havetopic = owlModelx.getOWLObjectProperty("have_topic");
	     OWLObjectProperty haveobject = owlModelx.getOWLObjectProperty("have_object");
	     //Ϊcartoon haveobject ��������ֵ
	     for(int i=0;i<Idmodels.length;i++)
	     {
	    	 cartoonIndividual.addPropertyValue(haveobject, Idmodels[i]);
	     }
	     //cartoonIndividual.addPropertyValue(havetopic, topicIndividual);
	     //Ϊ havetopic ��������ֵ
	     for(int i=0;i<Idtopics.length;i++)
	     {
	    	 cartoonIndividual.addPropertyValue(havetopic, Idtopics[i]);
	     }
	     //Collection ttpopic= cartoonIndividual.getPropertyValues(havetopic);
	     //Collection tmodels= cartoonIndividual.getPropertyValues(haveobject);
	     
	     //��ȡmessage�ಢ�����д���ʵ��
	     /***
	     ArrayList SubjectContent = new ArrayList();
	     for(int i=0;i<subject.length;i++){
	    	 SubjectContent.add(owlModelx.getOWLIndividual(subject[i]));
	     }
	     ArrayList  CartoonObject = new ArrayList();
	     for(int i=0;i<name.length;i++){
	    	 CartoonObject.add(owlModelx.getOWLIndividual(name[i]));
	     }
	     */////������������
	     SWRLFactory factory = new SWRLFactory(owlModelx);
	     SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create(owlModelx);
	     Collection rules = factory.getImps();
	     ruleEngine.infer(); 
	     //cartoon ��Ҫ��������Դ���
	     OWLObjectProperty beComposed = owlModelx.getOWLObjectProperty("be_composed");
	     OWLObjectProperty deformLocate = owlModelx.getOWLObjectProperty("deform_locate");
	     OWLDatatypeProperty frequency = owlModelx.getOWLDatatypeProperty("frequency");
	     OWLDatatypeProperty frames = owlModelx.getOWLDatatypeProperty("frames");
	     if(!cartoonIndividual.hasPropertyValue(beComposed))
	     {
	    	 System.out.println("there is no need to add deform in  "+animationsceneName);
	    	 return document;
	     }
	     Collection deforms= cartoonIndividual.getPropertyValues(beComposed);
	     OWLIndividual[] Iddeforms = (OWLIndividual[]) deforms.toArray(new OWLIndividual[0]);
	     Iddeforms=RandomDeform(Iddeforms);
	     // doc �л�ȡ���ڵ�
	     Element rootElm = document.getRootElement();
	     Element maNameElm=rootElm.element("maName");
	     Element times1=maNameElm.addElement("rule");
	     Element times2=maNameElm.addElement("rule");
	     Element times3=maNameElm.addElement("rule");
	     times1.addAttribute("ruleType", "Deform");
	     times2.addAttribute("ruleType", "Deform");
	     times3.addAttribute("ruleType", "Deform");
	     times1.addAttribute("startframe", "1");
	     times1.addAttribute("endframe", "100");
	     times2.addAttribute("startframe", "100");
	     times2.addAttribute("endframe", "200");
	     times3.addAttribute("startframe", "200");
	     times3.addAttribute("endframe", "300");
	     /*
	     Element times1=maNameElm.addElement("ruletype");
	     Element times2=maNameElm.addElement("ruletype");
	     Element times3=maNameElm.addElement("ruletype");
	     times1.addAttribute("startframe", "1");
	     times1.addAttribute("endframe", "100");
	     times2.addAttribute("startframe", "100");
	     times2.addAttribute("endframe", "200");
	     times3.addAttribute("startframe", "200");
	     times3.addAttribute("endframe", "300");
	     */
	     // ��ӡ�������ֵ
	     for(int i=0;i<Iddeforms.length;i++)
          {	
	    	//��ȡfrequency����
	    	String deffrequency=(String)Iddeforms[i].getPropertyValue(frequency);
	    	//��ȡÿ������ʩ�ӵ�����
	    	Collection objects=Iddeforms[i].getPropertyValues(deformLocate);
	    	OWLIndividual[] Idobjects = (OWLIndividual[]) objects.toArray(new OWLIndividual[0]);
	    	Idobjects=this.RandomObject(Idobjects);
	    	//��ȡ�ñ��εĸ���
	    	RDFSClass fatherClasses=Iddeforms[i].getRDFType();
	    	Collection  facollection= fatherClasses.getSuperclasses();
	    	OWLNamedClass[] supfatherClasses=(OWLNamedClass[])facollection.toArray(new OWLNamedClass[0]);
	    	//XML��ӡ����
	    	int m=i%3;
	    	Element ruleElmx ;
	    	switch(m)
	    	{
	    	case 0:
	    		ruleElmx = times1;//����rull�ڵ�
	    		break;
	    	case 1:
	    		ruleElmx = times2;//����rull�ڵ�
	    		break;
	    	case 2:
	    		ruleElmx = times3;//����rull�ڵ�
	    		break;
	    		default:
	    			ruleElmx=times1;
	    	}
	    	if(Idobjects.length!=0)
	    	{
	    		for(int j=0;j<Idobjects.length;j++)
			    {	
	    			Element modelsElm=ruleElmx;
	    			int realstart=Idobjects[j].getBrowserText().indexOf(":");
	    			String realobjname=Idobjects [j].getBrowserText().substring(realstart+1);
	    			modelsElm.addAttribute("usedModelInMa", realobjname);
	    			modelsElm.addAttribute("type", supfatherClasses[0].getBrowserText());
	    			modelsElm.addAttribute("frequency", deffrequency);
		 		    //��ȡ������ε�ר����Ϣ
		 		    document=getDatatype(supfatherClasses[0],owlModelx, Iddeforms[i],document,modelsElm);
			    	//ruleElmx.addAttribute("addMode", Idobjects [j].getBrowserText());
			    }
	    		//ruleElmx.addAttribute("type", supfatherClasses[0].getBrowserText());
	    		//ruleElmx.addAttribute("frequency", deffrequency);
	 		    //��ȡ������ε�ר����Ϣ
	 		   // document=getDatatype(supfatherClasses[0],owlModelx, Iddeforms[i],document,ruleElmx);
	    	}
	    	else
	    	{
	    		System.out.println(Iddeforms[i].getBrowserText()+"have no objects!");
	    	}
	    	/**
		    Element ruleElm = maNameElm.addElement("rule");//����rull�ڵ�
		    //Ϊrule ����type ���ԣ����ֱ��Σ�
		    ruleElm.addAttribute("type", supfatherClasses[0].getBrowserText());
		    for(int j=0;j<Idobjects.length;j++)
		    {	
		    	ruleElm.addAttribute("addMode"+j, Idobjects [j].getBrowserText());
		    }
		    //����frequency����
		    ruleElm.addAttribute("frequency", deffrequency);
		    //��ȡ������ε�ר����Ϣ
		    document=getDatatype(supfatherClasses[0],owlModelx, Iddeforms[i],document,ruleElm);
		    **/
          	//System.out.println(" - " + Iddeforms [i].getBrowserText()); 
          	//System.out.println(" -- " +  Idobjects [i].getBrowserText());  
          	//System.out.println(" -- " +  fatherClasses.getBrowserText());
          	//System.out.println(" -- " +  supfatherClasses[0].getBrowserText());
          	//System.out.println(" -- " +  deffrequency); 			
          }
	    // XMLWriter writer = new XMLWriter(new FileWriter("output.xml"));
	    // writer.write(document);			
	    // writer.close();
	     return document;
	 }
	 private String havetopic;
	 public  OWLIndividual[] RandomDeform(OWLIndividual[] tempdeform)
	 {
		 Random rnd = new Random();
		 OWLIndividual[] deform=new OWLIndividual[tempdeform.length];
		 for(int i=1;i<tempdeform.length+1;i++)
	        {
	        	int p=rnd.nextInt(tempdeform.length);
	        	if (deform[p] != null)
	        	{
	                   i--;
	        	}
	        	else
	        	{
	        		deform[p] = tempdeform[i-1];
	        	}
	        }
		 return deform;
	 }
	 public OWLIndividual[] RandomObject(OWLIndividual[] tempObject)
	 {
		 Random rnd = new Random();
		 //OWLIndividual[] object=tempObject;
		 int p;
		 int s;
		 do
		 {
			 p=rnd.nextInt(tempObject.length);
			 //System.out.println(p);
			 s=rnd.nextInt(tempObject.length-p);
			// System.out.println(s);
		 }
		 while(s==0);
		 //System.out.println(s);
		 OWLIndividual[] object=new OWLIndividual[s] ;
		 for(int i=0;i<s;i++)
		 {
			 object[i]=tempObject[p+i];
			 System.out.println(object[i].getBrowserText());
		 }
		 return object;	 
	 }
	/* public static void main(String[] args) throws OntologyLoadException, SWRLParseException, SWRLRuleEngineException, DocumentException, IOException {	
		// TODO �Զ����ɷ������
		/**********/
		//���빤��Ŀ¼
       /* String urlx="file:///H:/protege/6.25(test).owl";
        OWLModel owlModelx =ProtegeOWL.createJenaOWLModelFromURI(urlx);
        //����
        //SWRLFactory factory = new SWRLFactory(owlModelx);
        //SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create(owlModelx);
        OWLModel owlModels;
        SAXReader reader = new SAXReader();
        Document  document = reader.read(new File("input.xml"));
	    XMLWriter writer = new XMLWriter(new FileWriter("output.xml"));
        Use duse=new Use();
        document=duse.DeformReasing(owlModelx,document, "p2:winter.ma");
	    writer.write(document);			
	    writer.close();
        //�����ļ�
        //String fileName = "second-saved.owl";
        //Collection errors = new ArrayList();
        //((JenaOWLModel) owlModelx).save(new File(fileName).toURI(), FileUtils.langXMLAbbrev, errors);
	    // System.out.println("File saved with " + errors.size() + " errors.");
        //Jena.dumpRDF(owlModel.getOntModel());  
	    System.out.println("Deform have been added");*/
	//}
	
}
