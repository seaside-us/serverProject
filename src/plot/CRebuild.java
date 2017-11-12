package plot;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.Element;
import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngineFactory;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//�����ṩһЩ���������߶����
class CRand{
	// �Ը���ʵ����������÷���ʹ�������г�ȡĳ���������������в���
	
	public OWLIndividual[] RandomDeform(OWLIndividual[] tempdeform)
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
	//���ò�������ʼ��˫��ķ��������������ȡ��M��Ԫ��
	 public OWLIndividual[] RandomObject(OWLIndividual[] tempObject)
	 {
		 Random rnd = new Random();
		 //OWLIndividual[] object=tempObject;
		 int p;
		 int s;
		 do
		 {
			 p=rnd.nextInt(tempObject.length+1);
			 //System.out.println(p);
			 s=rnd.nextInt(tempObject.length-p+1);
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
	 public OWLIndividual[] RandomDe2(OWLIndividual[] tempDeform)
	 {
		 Random rnd = new Random();
		 //OWLIndividual[] object=tempObject;
		 int p;
		 //int ns;
		 int s=2;	
		 p=rnd.nextInt(11);
		 //ns=rnd.nextInt(5); 
		 System.out.println(p);
		 //System.out.println(s);
		 OWLIndividual[] object=new OWLIndividual[s] ;
		 for(int i=0;i<s;i++)
		 {
			 object[i]=tempDeform[p+i*p];
			 System.out.println(object[i].getBrowserText());
		 }
		 return object;	 
	 }
}
//��ȡanimationsceneName����������
class CGetAndReasoning{
	public static Log log = LogFactory.getLog(CGetAndReasoning.class);
	 @SuppressWarnings({ "deprecation", "unchecked" })
	public  OWLModel DeformReasing(OWLModel owlModelx,String animationsceneName,ArrayList<String> template ) throws SWRLRuleEngineException, IOException
	 {	String str="p6:";
		 
		 //��ȡcartoon�ಢ�����д���ʵ��
	     OWLIndividual cartoonIndividual = owlModelx.getOWLIndividual(str+"cartoon_3");
	     //��ȡ����animationscene����Ϣ
	     OWLIndividual animationsceneIndividual = owlModelx.getOWLIndividual(animationsceneName);
	     //��ʼ�����Ƹ�cartoon ʵ������Ϣ
	     OWLObjectProperty havetopicpro = owlModelx.getOWLObjectProperty(str+"have_topic");
	     OWLObjectProperty haveobjectpro = owlModelx.getOWLObjectProperty(str+"have_object");
	     OWLObjectProperty havetemplatepro = owlModelx.getOWLObjectProperty(str+"have_template");
	     OWLDatatypeProperty frameNumberspro = owlModelx.getOWLDatatypeProperty("maFrameNumber");
	     OWLObjectProperty hasModelNamepro = owlModelx.getOWLObjectProperty("hasModelName");
	     OWLNamedClass AddModelClass = owlModelx.getOWLNamedClass("AddModelRelated");
	     Integer  frames=(Integer)animationsceneIndividual.getPropertyValue(frameNumberspro);
	     cartoonIndividual.addPropertyValue(frameNumberspro, frames);
	     //template �Ĵ���
	     
	     if(template.size()!=0)
	     {
	    	 String[] templates=new String[template.size()];
	    	 OWLIndividual[] templatesind=new OWLIndividual[template.size()];
	    	 for(int i=0;i<template.size();i++)
	    	 {
	    		 String tempt=template.get(i);
	    		// int realstart=tempt.indexOf(":");
	    		 templatesind[i]=owlModelx.getOWLIndividual(tempt.substring(tempt.lastIndexOf(":")+1));
    		 
	    	 }
		     for(int i=0;i<templatesind.length;i++)
		     {
		    	 log.debug("ģ��ԭ������"+template.get(i));
		    	 if(template.get(i).equals(""))
		    	 {
		    		 break;
		    	 }
		    	 System.out.println("zheg"+havetemplatepro+templatesind[i]);
		    	 cartoonIndividual.addPropertyValue(havetemplatepro, templatesind[i]);
		     }
	     }
	     //��ȡmodels
	     OWLObjectProperty hasmodel = owlModelx.getOWLObjectProperty("hasmodel");
	     //OWLObjectProperty addmodel = owlModelx.getOWLObjectProperty("p2:addToMa");
	     Collection modelsadded=AddModelClass.getInstances(true);//(null)
	     System.out.println("���볡���е�ģ�ͣ�"+modelsadded.size());
	     if(!animationsceneIndividual.hasPropertyValue(hasmodel)&&modelsadded.size()==0)
	     {
	    	 log.debug("���볡����û��ģ��"+animationsceneName);
	    	 return owlModelx;
	     }
		Collection models= animationsceneIndividual.getPropertyValues(hasmodel);
		//Collection modelsadded= animationsceneIndividual.getPropertyValues(addmodel);
		// Collection<OWLIndividual> Imodelsadded = new ArrayList<OWLIndividual>();
		 /*
		 System.out.println("the modelsadded size "+modelsadded.size());
		 for (Iterator jt = modelsadded.iterator(); jt.hasNext();) {
			  Object d = (Object) jt.next();
			  System.out.println("the modelsadded first one is "+d.toString());
			  OWLIndividual def;
			  def = (OWLIndividual) d;
			  Imodelsadded.add(def);
		  }
		  */
	     OWLIndividual[] Idmodels = (OWLIndividual[]) models.toArray(new OWLIndividual[0]);
	     OWLIndividual[] Idmodelsadded=(OWLIndividual[])modelsadded.toArray(new OWLIndividual[0]);
	     //Ϊcartoon haveobject ��������ֵ
	     for(int i=0;i<Idmodels.length;i++)
	     {
	    	 cartoonIndividual.addPropertyValue(haveobjectpro, Idmodels[i]);
	     }
	     for(int i=0;i<Idmodelsadded.length;i++)
	     {
	    	 cartoonIndividual.addPropertyValue(haveobjectpro, Idmodelsadded[i]);
	     }
	     //��ȡtopics
	     /*
	     OWLObjectProperty proHastopic = owlModelx.getOWLObjectProperty("p2:hasTopic");
	     Collection topics= animationsceneIndividual.getPropertyValues(proHastopic);
	     OWLIndividual[] IdTopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);
	     if(!animationsceneIndividual.hasPropertyValue(proHastopic))
	     {
	    	 System.out.println("����TopicΪ��  "+animationsceneName);
	     }else
	     {
	    	 for(int i=0;i<IdTopics.length;i++)
	    	 {
	    		 cartoonIndividual.addPropertyValue(havetopicpro, IdTopics[i]);
	    	 }
	     }
	     */
	     OWLDatatypeProperty topicdata = owlModelx.getOWLDatatypeProperty("topicName");
	     String topicClassPart=(String)animationsceneIndividual.getPropertyValue(topicdata);
	     //ѭ����ȡ��ʱ����topic
	     Collection topicNames = animationsceneIndividual.getPropertyValues(topicdata);
	     for(Iterator it2=topicNames.iterator();it2.hasNext();)
	     {
	    	 topicClassPart=(String)it2.next();
	    	 System.out.println("��������: "+topicClassPart+"��ȡ");
	     }
	     OWLNamedClass topicClass=owlModelx.getOWLNamedClass(topicClassPart);
	     Collection topics;
	     OWLIndividual[] Idtopics;
	     if(topicClass!=null)
	     {
	    	 topics=topicClass.getInstances();
	    	 Idtopics = (OWLIndividual[]) topics.toArray(new OWLIndividual[0]);
		     //Ϊcartoon havetopic ��������ֵ
		     for(int i=0;i<Idtopics.length;i++)
		     {
		    	 cartoonIndividual.addPropertyValue(havetopicpro, Idtopics[i]);
		     }
	     }
	     if(topicClass==null&&template.size()==0)
	     {
	    	 log.debug("ģ��ԭ�Ӻ����ⶼΪ��");
	    	 //return owlModelx;
	     }
	     //������
	     //SWRLFactory factory = new SWRLFactory(owlModelx);
	     //SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create(owlModelx);
	     //Collection rules = factory.getImps();
	     //ruleEngine.infer();
	     //SWLRULdepart(owlModelx);
	     //	  
	     InferWithOrder( owlModelx,str+"templateRule");
	     OWLDatatypeProperty sentiment = owlModelx.getOWLDatatypeProperty(str+"sentiment");
	     if(topicClass==null&&(cartoonIndividual.hasPropertyValue(sentiment)==false))
	     {
	    	 log.debug("�����Ƴ��κζ�������");
	     }
	     InferWithOrder( owlModelx,str+"sentimentRule");
	     InferWithOrder( owlModelx,str+"cartoonRule");
	     InferWithOrder( owlModelx,str+"deformRule");
	     OWLObjectProperty beComposed = owlModelx.getOWLObjectProperty(str+"be_composed");
	    	//
	     if(!cartoonIndividual.hasPropertyValue(beComposed))
		  	{
	    	 	log.debug("û��������κα���������ӱ���");
	    	 	
	    	 	OWLNamedClass deformClass=owlModelx.getOWLNamedClass(str+"deform");
		 	    OWLObjectProperty deformLocate = owlModelx.getOWLObjectProperty(str+"deform_locate");
	    	 	Collection deformInds=deformClass.getInstances(true);
	    	 	OWLIndividual[] idDeforms= (OWLIndividual[]) deformInds.toArray(new OWLIndividual[0]);
	    	 	//OWLIndividual boyid=owlModelx.getOWLIndividual("p2:boy.ma");
	    	 	//OWLIndividual girlid=owlModelx.getOWLIndividual("p2:girl.ma");
	    	 	CRand random=new CRand();
	    	 	idDeforms=random.RandomDe2(idDeforms);
	    	 	Random rnd = new Random();
	    	 	for(int i=0;i<idDeforms.length;i++)
	    	 	{
	    	 		cartoonIndividual.addPropertyValue(beComposed,idDeforms[i]);
	    	 		
	    	 		if(Idmodels.length!=0)
	    	 		{
		    	 		int j=rnd.nextInt(Idmodels.length);
		    			//int realstart=Idmodels[j].getBrowserText().indexOf(":");
		    			//String realobjname=Idmodels [j].getBrowserText().substring(realstart+1);

		    			//5.20
		    			//String  realobjname=Idmodels [j].getRDFType().getBrowserText();
		    			//CGetAndReasoning.log.debug("realobjname: " + realobjname);
		    			Collection objectnames=Idmodels[j].getPropertyValues(hasModelNamepro);
		    			OWLIndividual[] Iobjectnames = (OWLIndividual[]) objectnames.toArray(new OWLIndividual[0]);
		    			String realobjname;
		    			if(Iobjectnames.length==0)
    	    	    	{
		    				System.out.println("����������ģ��ID��");
    	    	    		System.out.println(Idmodels[j].getBrowserText());
    	    	    		//int realstartin=Idmodels[j].getBrowserText().indexOf(":");
    	    	    		realobjname=Idmodels[j].getBrowserText();
    	    	    	}
    	    	    	else
    	    	    	{
    	    	    		System.out.println("ʵ���������");
    	    	    		System.out.println(Iobjectnames[0].getBrowserText());
    	    	    		//int realstartin=Iobjectnames[0].getBrowserText().indexOf(":");
    	    	    		realobjname=Iobjectnames[0].getBrowserText();
		    				//5.20
							//String  classNameStr1=boyid.getRDFType().getBrowserText();
							//String  classNameStr2=girlid.getRDFType().getBrowserText();
							//��ȡ���常�༯��
							//OWLNamedClass classNameStrSce=owlModelx.getOWLNamedClass("p2:Asia");
							OWLNamedClass classNameStrBac=owlModelx.getOWLNamedClass("BackgroundScene");
							//OWLNamedClass classNameMan=owlModelx.getOWLNamedClass("p2:Man");
							//OWLNamedClass classNameWoman=owlModelx.getOWLNamedClass("p2:Woman");
							String fatherClassname=Iobjectnames[0].getRDFType().getBrowserText();
							OWLNamedClass fatherClasses=owlModelx.getOWLNamedClass(fatherClassname);
							Collection  facollection= new ArrayList();
							facollection = fatherClasses.getSuperclasses(true);
							//OWLNamedClass[] supfatherClasses=(OWLNamedClass[])facollection.toArray(new OWLNamedClass[0]);
							//System.out.println(supfatherClasses[1].getBrowserText());
							//CGetAndReasoning.log.debug("boyobjname: " + classNameStr1);
							//CGetAndReasoning.log.debug("girlobjname: " + classNameStr1);
							CGetAndReasoning.log.debug("��������"+fatherClassname);
							if(fatherClassname.contains("Man")||fatherClassname.contains("man") ||fatherClassname.contains("bike")||realobjname.contains("floor")||fatherClassname.contains("Girl")||facollection.contains(classNameStrBac))
							{
								CGetAndReasoning.log.debug("Ҫ���͵������� " + fatherClassname+"����");
							}else
							{
								if(idDeforms[i].getBrowserText().contains("wave"))
								{
									CGetAndReasoning.log.debug("Ҫ���͵����Ͳ�����Ҫ��");
								}else{
									idDeforms[i].addPropertyValue(deformLocate, Idmodels[j]);
								}
							}
    	    	    	}
	    	 		}
	    	 		if(Idmodelsadded.length!=0)
	    	 		{
		    	 		int k=rnd.nextInt(Idmodelsadded.length);
		    			//int realstart=Idmodelsadded[k].getBrowserText().indexOf(":");
		    			//String realobjname=Idmodelsadded [k].getBrowserText().substring(realstart+1);
		    			//String  realobjname=Idmodelsadded[k].getRDFType().getBrowserText();
		    			//CGetAndReasoning.log.debug("realobjname: " + realobjname);
		    			//5.20
		    			//String  realobjname=Idmodels [j].getRDFType().getBrowserText();
		    			//CGetAndReasoning.log.debug("realobjname: " + realobjname);
		    			Collection objectnames=Idmodelsadded [k].getPropertyValues(hasModelNamepro);
		    			OWLIndividual[] Iobjectnames = (OWLIndividual[]) objectnames.toArray(new OWLIndividual[0]);
		    			String realobjname;
		    			if(Iobjectnames.length==0)
    	    	    	{
		    				System.out.println("����������ģ��ID��");
    	    	    		System.out.println(Idmodelsadded [k].getBrowserText());
    	    	    		//int realstartin=Idmodelsadded [k].getBrowserText().indexOf(":");
    	    	    		realobjname=Idmodelsadded [k].getBrowserText();
    	    	    	}
    	    	    	else
    	    	    	{
    	    	    		System.out.println("ʵ���������");
    	    	    		System.out.println(Iobjectnames[0].getBrowserText());
    	    	    	//	int realstartin=Iobjectnames[0].getBrowserText().indexOf(":");
    	    	    		realobjname=Iobjectnames[0].getBrowserText();
		    				//5.20
							//String  classNameStr1=boyid.getRDFType().getBrowserText();
							//String  classNameStr2=girlid.getRDFType().getBrowserText();
							//��ȡ������
							//OWLNamedClass classNameStrSce=owlModelx.getOWLNamedClass("p2:Asia");
							OWLNamedClass classNameStrBac=owlModelx.getOWLNamedClass("BackgroundScene");
							String fatherClassname=Iobjectnames[0].getRDFType().getBrowserText();
							OWLNamedClass fatherClasses=owlModelx.getOWLNamedClass(fatherClassname);
							Collection  facollection= new ArrayList();;
							facollection = fatherClasses.getSuperclasses(true);
				    		//OWLNamedClass[] supfatherClasses=(OWLNamedClass[])facollection.toArray(new OWLNamedClass[0]);
							//System.out.println(supfatherClasses[1].getBrowserText());
							//CGetAndReasoning.log.debug("boyobjname: " + classNameStr1);
							//CGetAndReasoning.log.debug("girlobjname: " + classNameStr1);
							CGetAndReasoning.log.debug("��������"+fatherClassname);
							if(fatherClassname.contains("Man") ||fatherClassname.contains("man") || fatherClassname.contains("bike")||realobjname.contains("floor")||fatherClassname.contains("Girl")||facollection.contains(classNameStrBac))
							{
								CGetAndReasoning.log.debug("Ҫ���͵������� " + fatherClassname+"����");
							}else
							{
								if(idDeforms[i].getBrowserText().contains("wave"))
								{
									CGetAndReasoning.log.debug("Ҫ���͵����Ͳ�����Ҫ��");
								}else{
									idDeforms[i].addPropertyValue(deformLocate, Idmodelsadded[k]);
								}
							}
    	    	    	}
	    	 		}
	    	 	}
		  	}
	     return  owlModelx;		     
	 } 
	 public boolean InferWithOrder(OWLModel owlModelx,String rulename) throws SWRLRuleEngineException
	 {
		  SWRLRuleEngine ruleEngine =   SWRLRuleEngineFactory.create(owlModelx);
		  ruleEngine.reset();
		  SWRLFactory factory = new SWRLFactory(owlModelx);
		  factory.disableAll();
		  boolean temp=true;
		  Iterator<SWRLImp> iter = factory.getImps().iterator();	
		  while (iter.hasNext())
		  {
			SWRLImp imp = (SWRLImp) iter.next();
			if (imp.getLocalName().contains(rulename) == true)
			{
				imp.enable();
				//System.out.println(imp.getLocalName());
			}
		  }  
		  ruleEngine.infer();
		  return temp;
	 }

}
//Deform���������
class CBaseOutPut{
	public static Log log = LogFactory.getLog(CBaseOutPut.class);
	protected OWLModel owlModelx;//�����OWLModel
	protected CRand random=new CRand();//����������
	protected Document document ;//�����DOC
	protected Element modelsElm;
	private CDeformOutPut deformoutput;
	private int truenumber=0;
	//��ȡ�������Ҫ��cartoon��ʵ��
	public OWLModel GetModel()
	{
		return this.owlModelx;
	}
	public Document GetDocument()
	{
		return this.document;
	}
	public Element GetmodelsElm()
	{
		return this.modelsElm;
	}
	//��ȡ������Ϣ
	public void SetInformation(OWLModel owlModelx,Document document)
	{
		this.owlModelx=owlModelx;
		this.document=document;
	}
	//Doc�������������Ϣ��ȡ�����
	@SuppressWarnings("deprecation")
	public Document BaseOutput() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException
	{
	    String str="p6:";
		OWLIndividual cartoonIndividual = owlModelx.getOWLIndividual(str+"cartoon_3");;
	    //cartoon ��Ҫ��������Դ���
	    OWLObjectProperty beComposed = owlModelx.getOWLObjectProperty(str+"be_composed");
	    OWLObjectProperty deformLocate = owlModelx.getOWLObjectProperty(str+"deform_locate");
	    OWLDatatypeProperty frequency = owlModelx.getOWLDatatypeProperty(str+"frequency");
	    OWLDatatypeProperty frameNumberspro = owlModelx.getOWLDatatypeProperty("maFrameNumber");
	    OWLObjectProperty hasModelNamepro = owlModelx.getOWLObjectProperty("hasModelName");
	    Integer  frames=(Integer )cartoonIndividual.getPropertyValue(frameNumberspro);
	    int framescalculation=frames/100;
	    if(!cartoonIndividual.hasPropertyValue(beComposed))
	    {
	    	log.debug("������û�й��ɵı���ʵ��");
	    	return document;
	    }
	    Collection<?> deforms= cartoonIndividual.getPropertyValues(beComposed);
	    OWLIndividual[] Iddeforms = (OWLIndividual[]) deforms.toArray(new OWLIndividual[0]);
	    // Iddeforms=random.RandomDeform(Iddeforms);//�Զ����еı������ѡȡ
	    //Element[] times=this.ElementNumbers(Iddeforms);
	     for(int i=0;i<Iddeforms.length;i++)
         {	
	    	//��ȡfrequency����
	    	String deffrequency=(String)Iddeforms[i].getPropertyValue(frequency);
	    	//��ȡÿ������ʩ�ӵ�����
	    	Collection objects=Iddeforms[i].getPropertyValues(deformLocate);
	    	OWLIndividual[] Idobjects = (OWLIndividual[]) objects.toArray(new OWLIndividual[0]);
	    	//Idobjects=random.RandomObject(Idobjects);//���ѡ���������
	    	//��ȡ�ñ��εĸ���
	    	RDFSClass fatherClasses=Iddeforms[i].getRDFType();
	    	Collection  facollection= fatherClasses.getSuperclasses();
	    	OWLNamedClass[] supfatherClasses=(OWLNamedClass[])facollection.toArray(new OWLNamedClass[0]);
	    	//XML��ӡ����		
    	    Element rootElm = document.getRootElement();//��ȡ�����ڵ�
    	    Element maNameElm=rootElm.element("maName");//��ȡҪ�����Ϣ�Ľڵ�
    	    truenumber=Idobjects.length;
	    	if(Idobjects.length!=0)
	    	{
	    		Element[] timex=new Element[Idobjects.length];//�����ڸ�defrom�е��������ȷ����ӵĽڵ�
	    	    if(truenumber<3)
	    	    {
	    	    	for(int j=0;j<timex.length;j++)
    	    		{
	    	    		timex[j]=maNameElm.addElement("rule");	
    	    			timex[j].addAttribute("ruleType","Deform" );
    	    			timex[j].addAttribute("startFrame","0");
    	    			timex[j].addAttribute("endFrame",framescalculation+"00" );
    	    	    	Collection objectnames=Idobjects[j].getPropertyValues(hasModelNamepro);
    	    	    	OWLIndividual[] Iobjectnames = (OWLIndividual[]) objectnames.toArray(new OWLIndividual[0]);
    	    	    	String realobjname;
    	    	    	//5.20
    	    	    	//int realstartID=Idobjects[j].getBrowserText().indexOf(":");
    	    	    	String realobjnameID=Idobjects [j].getBrowserText();
	    	    		timex[j].addAttribute("usedModelID", realobjnameID);
	    	    		//5.20
    	    	    	if(Iobjectnames.length==0)
    	    	    	{
    	    	    		System.out.println("IobjectNames[j]"+Idobjects[j].getBrowserText());
    	    	    		//int realstart=Idobjects[j].getBrowserText().indexOf(":");
    	    	    		realobjname=Idobjects[j].getBrowserText();
    	    	    	}
    	    	    	else
    	    	    	{
    	    	    		System.out.println("ʵ���������");
    	    	    		System.out.println(Iobjectnames[0].getBrowserText());
    	    	    		//int realstart=Iobjectnames[0].getBrowserText().indexOf(":");
    	    	    		realobjname=Iobjectnames[0].getBrowserText();
    	    	    	}
	    	    		timex[j].addAttribute("usedModelInMa", realobjname);
	    	    		timex[j].addAttribute("type", supfatherClasses[0].getBrowserText().substring(3));
	    	    		timex[j].addAttribute("frequency", deffrequency);
	    	    		//�ж�ģ�ͳߴ��Ƿ���huge�������򽫱��η��ȵ�ΪС
	    	    		boolean ifHuge = false;
	    	    		try{
	    	    			OWLIndividual individual = owlModelx.getOWLIndividual(realobjname);
		    	    		OWLDatatypeProperty modelScale = owlModelx.getOWLDatatypeProperty("modelVolumeScale");
		    	    		String scale = individual.getPropertyValue(modelScale).toString();
		    	    		if(scale == "huge"){
		    	    			ifHuge = true;
		    	    		}
	    	    		} catch (Exception ex){
	    					System.out.println(realobjname + " don't have property: modelVolumeScale");
	    					log.debug(realobjname + "û�б�עscale����");
	    				}
	    	    		
	    	    		//��ȡ������ε�ר����Ϣ
	    	    		CDeformManager manager=new CDeformManager();
	    	    		String sname=supfatherClasses[0].getBrowserText().substring(3);
	    	    		System.out.println("sname"+sname);
	    	    		this.deformoutput=manager.GetDeform(sname);
	    	    		System.out.println("deformoutput"+deformoutput);
	    	    		deformoutput.SetInformation(owlModelx, document, timex[j]);
	    	    		deformoutput.DeformOutPut(Iddeforms[i], timex[j], ifHuge);	
    	    		}
	    	    }
	    	    else
	    	    {
	    	    	for(int j=0;j<timex.length;j++)
	    	    	{
	    	    		timex[j]=maNameElm.addElement("rule");	
	    	    		timex[j].addAttribute("ruleType","Deform" );
	    	    		if (i%framescalculation==0)
	    	    		{
	    	    			timex[j].addAttribute("startFrame","0");
	    	    		}else
	    	    		{
	    	    			timex[j].addAttribute("startFrame",i%framescalculation+"00" );
	    	    		}
	    	    		timex[j].addAttribute("endFrame",i%framescalculation+1+"00" );//����deform������ȷ����ؼ�֡����	
    	    	    	Collection objectnames=Idobjects[j].getPropertyValues(hasModelNamepro);
    	    	    	OWLIndividual[] Iobjectnames = (OWLIndividual[]) objectnames.toArray(new OWLIndividual[0]);
    	    	    	String realobjname;
    	    	    	//5.20 
    	    	    	//int realstartID=Idobjects[j].getBrowserText().indexOf(":");
    	    	    	String realobjnameID=Idobjects [j].getBrowserText();
	    	    		timex[j].addAttribute("usedModelID", realobjnameID);
    	    	    	if(Iobjectnames.length==0)
    	    	    	{
    	    	    		System.out.println(Idobjects[j].getBrowserText());
    	    	    		//int realstart=Idobjects[j].getBrowserText().indexOf(":");
    	    	    		realobjname=Idobjects[j].getBrowserText();
    	    	    	}
    	    	    	else
    	    	    	{
    	    	    		System.out.println("ʵ���������");
    	    	    		System.out.println(Iobjectnames[0].getBrowserText());
    	    	    	//	int realstart=Iobjectnames[0].getBrowserText().indexOf(":");
    	    	    		realobjname=Iobjectnames[0].getBrowserText();
    	    	    	}
	    	    		timex[j].addAttribute("usedModelInMa", realobjname);
	    	    		timex[j].addAttribute("type", supfatherClasses[0].getBrowserText().substring(3));
	    	    		timex[j].addAttribute("frequency", deffrequency);
	    	    		//�ж�ģ�ͳߴ��Ƿ���huge�������򽫱��η��ȵ�ΪС
	    	    		boolean ifHuge = false;
	    	    		try{
	    	    			OWLIndividual individual = owlModelx.getOWLIndividual(realobjname);
		    	    		OWLDatatypeProperty modelScale = owlModelx.getOWLDatatypeProperty("modelVolumeScale");
		    	    		String scale = individual.getPropertyValue(modelScale).toString();
		    	    		if(scale == "huge"){
		    	    			ifHuge = true;
		    	    		}
	    	    		} catch (Exception ex){
	    					System.out.println(realobjname + " don't have property: modelVolumeScale");
	    					log.debug(realobjname + "û�б�עscale����");
	    				}
	    	    		//��ȡ������ε�ר����Ϣ
	    	    		CDeformManager manager=new CDeformManager();
	    	    		String sname=supfatherClasses[0].getBrowserText();
	    	    		this.deformoutput=manager.GetDeform(sname);
	    	    		deformoutput.SetInformation(owlModelx, document, timex[j]);
	    	    		deformoutput.DeformOutPut(Iddeforms[i], timex[j], ifHuge);	
	    	    	}
	    	    }
	    	}
	    	else
	    	{
	    		System.out.println(Iddeforms[i].getBrowserText()+"������û�����壡");
	    	} 
         }
		return document;	
	};
}
abstract class CDeformOutPut
{
	protected Document document ;//�����DOC
	protected Element modelsElm;
	protected OWLModel owlModelx;//�����OWLModel
	public void SetInformation(OWLModel owlModelx,Document document, Element modelsElm)
	{
		this.owlModelx=owlModelx;
		this.document=document;
		this.modelsElm=modelsElm;
	}
	abstract public Document DeformOutPut(OWLIndividual deformIndividual,Element ruleElm, boolean isHugeModel);
	}
class CbendOutPut extends CDeformOutPut
{
	public Document DeformOutPut(OWLIndividual deformIndividual,Element ruleElm, boolean isHugeModel){
		OWLDatatypeProperty curvature = owlModelx.getOWLDatatypeProperty("p6:bend_curvature");
		String defcurvature=(String)deformIndividual.getPropertyValue(curvature);
		
		if(isHugeModel && defcurvature == "big"){
			defcurvature = "middle";
		}
		
		OWLDatatypeProperty bendmethods= owlModelx.getOWLDatatypeProperty("p6:bend_methods");
		String defmethord=(String)deformIndividual.getPropertyValue(bendmethods);
		//Bend be=new Bend(defcurvature,defmethord);
		ruleElm.addAttribute("curvature", defcurvature);
		//ruleElm.addAttribute("direction", defmethord);
		ruleElm.addAttribute("bend_methods", defmethord);
		return document;
	}
}
class CwaveOutPut extends CDeformOutPut
{
	public Document DeformOutPut(OWLIndividual deformIndividual,Element ruleElm, boolean isHugeModel)
	{
		OWLDatatypeProperty curvature = owlModelx.getOWLDatatypeProperty("p6:wave_Amplitude");
		String defcurvature=(String)deformIndividual.getPropertyValue(curvature);
		OWLDatatypeProperty wavelength= owlModelx.getOWLDatatypeProperty("p6:wave_wavelength");
		String defwavelength=(String)deformIndividual.getPropertyValue(wavelength);
		if(isHugeModel && defwavelength == "big"){
			defwavelength = "middle";
		}
		//Wave wa=new Wave(defcurvature,defwavelength);
		ruleElm.addAttribute("curvature", defcurvature);
		//ruleElm.addAttribute("direction", "");
		ruleElm.addAttribute("wavelength", defwavelength);
		return document;
	}
}
class CflareOutPut extends CDeformOutPut
{
	public Document DeformOutPut(OWLIndividual deformIndividual,Element ruleElm, boolean isHugeModel)
	{
		OWLDatatypeProperty curvature = owlModelx.getOWLDatatypeProperty("p6:flare_curvature");
		String defcurvature=(String)deformIndividual.getPropertyValue(curvature);
		if(isHugeModel && defcurvature == "big"){
			defcurvature = "middle";
		}
		OWLDatatypeProperty flaremethod= owlModelx.getOWLDatatypeProperty("p6:flare_methods");
		String defmethord=(String)deformIndividual.getPropertyValue(flaremethod);
		//Flare fl=new Flare(defcurvature,defmethord);
		ruleElm.addAttribute("curvature", defcurvature);
		//ruleElm.addAttribute("direction", defmethord);
		ruleElm.addAttribute("flare_methods", defmethord);
		return document;
	}
	}
class CsineOutPut extends CDeformOutPut
{
	public Document DeformOutPut(OWLIndividual deformIndividual,Element ruleElm, boolean isHugeModel)
	{
		OWLDatatypeProperty curvature = owlModelx.getOWLDatatypeProperty("p6:sine_ampitude");
		String defcurvature=(String)deformIndividual.getPropertyValue(curvature);
		OWLDatatypeProperty wavelength= owlModelx.getOWLDatatypeProperty("p6:sine_wavelength");
		String defwavelength=(String)deformIndividual.getPropertyValue(wavelength);
		if(isHugeModel && defwavelength == "big"){
			defwavelength = "middle";
		}
		//Sine Si=new Sine(defcurvature,defwavelength);
		ruleElm.addAttribute("curvature", defcurvature);
		// ruleElm.addAttribute("direction", "");
		ruleElm.addAttribute("wavelength", defwavelength);
		return document;
	}
	}
class CtwistOutPut extends CDeformOutPut
{
	public Document DeformOutPut(OWLIndividual deformIndividual,Element ruleElm, boolean isHugeModel)
	{
		OWLDatatypeProperty curvature = owlModelx.getOWLDatatypeProperty("p6:twist_curvature");
		String defcurvature=(String)deformIndividual.getPropertyValue(curvature);
		if(isHugeModel && defcurvature == "big"){
			defcurvature = "middle";
		}
		//Twist tw=new Twist(defcurvature);
		ruleElm.addAttribute("curvature", defcurvature);
		return document;
	}
	}
class CsquashOutPut extends CDeformOutPut
{
	public Document DeformOutPut(OWLIndividual deformIndividual,Element ruleElm, boolean isHugeModel)
	{ String str="p6:";
		OWLDatatypeProperty curvature = owlModelx.getOWLDatatypeProperty(str+"squash_curvature");
		String defcurvature=(String)deformIndividual.getPropertyValue(curvature);
		if(isHugeModel && defcurvature == "big"){
			defcurvature = "middle";
		}
		//Squash sq=new Squash(defcurvature);
		ruleElm.addAttribute("curvature", defcurvature);
		return document;
	}
	}
//����ʵ�־���CDeformOutPut �Ĵ���
class CDeformManager{
	private CDeformOutPut deform;
	public CDeformOutPut GetDeform(String name) throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{ System.out.println("deform"+deform);
		Class<?> cls=Class.forName("plot.C"+name+"OutPut");
		this.deform=(CDeformOutPut)cls.newInstance();
		System.out.println("deform"+deform);
		return deform;
	}
}
public class CRebuild {
	/**
	 * @param args
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @throws OntologyLoadException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @throws SWRLRuleEngineException 
	 */
	public static void main(String[] args) throws Exception {
		  
		  //Properties props = System.getProperties();
		  //props.setProperty("proxySet", "true");
		  //�ڲ�ͬ�Ļ����ϣ�д�ϸû�����ip��ַ
		 // props.setProperty("http.proxyHost", "172.21.13.170");
		 // props.setProperty("http.proxyPort", "808");
		ArrayList<String> moodTemplateAttr=new ArrayList();
		String uri = "file:///C:/ontologyOWL/sumoOWL2//sumo_phone3.owl";
		OWLModel model=ProtegeOWL.createJenaOWLModelFromURI(uri);
		String filePath = "d:\\test.xml";
		Document document=Readxml.read(filePath);
		CRebuild deform=new CRebuild();
        Document document1=deform.FinalOutPut(model,"schoolroomOut.ma",moodTemplateAttr,document);
        Readxml.savexml(filePath, document1);
        System.out.println("����");
       
      }
	public Document FinalOutPut( OWLModel owlModelx,String animationsceneName,ArrayList<String> template,Document document) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SWRLRuleEngineException, IOException
	{
	    CGetAndReasoning getandreansoning=new CGetAndReasoning();
	    CBaseOutPut basoutput=new CBaseOutPut();
	    basoutput.SetInformation(getandreansoning.DeformReasing(owlModelx, animationsceneName, template), document);
        document=basoutput.BaseOutput();
        return document;
	}
}

