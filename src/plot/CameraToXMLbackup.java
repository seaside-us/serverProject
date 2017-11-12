package plot;
import java.util.*;
import java.io.*;
import java.math.BigDecimal;

import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
//import org.jdom.Document;
import org.dom4j.Document;

import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLFactoryException;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;

import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngineFactory;


public class CameraToXMLbackup{
    static Random random = new Random(System.currentTimeMillis());
	
	public Document CreateCamera(OWLModel owlModel, String maName, Document doc)
	{
		System.out.println("===摄像机程序开始===");
		int framenum,bgtype,camerapara = 0;
		OWLIndividual ma = owlModel.getOWLIndividual(maName);
		OWLDatatypeProperty maframenumber = owlModel.getOWLDatatypeProperty("maFrameNumber");
		OWLDatatypeProperty mabgtype = owlModel.getOWLDatatypeProperty("backgroundPictureType");
		framenum = (Integer) ma.getPropertyValue(maframenumber);
		System.out.println("mabgtype"+mabgtype);
		if(mabgtype.equals(null)||mabgtype.equals(""))
			bgtype=2;
		else
		bgtype = (Integer) ma.getPropertyValue(mabgtype);
		
		Random rd = new Random();
		if(bgtype==3){  //3是户外场景
			if(framenum<200){
				camerapara=1;
			}
			else if(framenum>=200&&framenum<=300){
				camerapara=1+rd.nextInt(2);
			}
			else
				camerapara=1+rd.nextInt(framenum/120);
		}
		else
			return doc;
		
		Element root= doc.getRootElement();
	    Element name = root.element("maName");
	    
	    ArrayList al=Readxml.getAddinfo(name);
		//System.out.println(camerapara+"===户外类型===");
		if(al.size()<1)
			return doc;
		int all0=0,tt1=0,tt0=0;
		SceneSpace all[] = new SceneSpace[al.size()] ;
		SceneSpace t1[] = new SceneSpace[al.size()] ;
		SceneSpace t0[] = new SceneSpace[al.size()] ;
		String no="no.ma";
		for (int j = 0; j < al.size(); j++) {
			all[j] = (SceneSpace) al.get(j);
			if (!all[j].getModelname().equals(no)) {
				all0++;
				if (all[j].getisTarget() == 1) {
					t1[tt1++] = all[j];
				} else if (all[j].getisTarget() == 0) {
					t0[tt0++] = all[j];
				}
			}
			//System.out.println("modelname:" + all[j].getModelname());
			//System.out.println("isTarget:" + all[j].getisTarget());
			//System.out.println("空间名字:" + all[j].getSpname());
		}
		
		/*
		System.out.println("=======target=1:");
		for (int k=0;k<tt1;k++) {
			System.out.println("modelname:" + t1[k].getModelname());
			System.out.println("isTarget:" + t1[k].getisTarget());
			System.out.println("空间名字:" + t1[k].getSpname());
		}
		System.out.println("=======target=0:");
		for (int k=0;k<tt0;k++) {
			System.out.println("modelname:" + t0[k].getModelname());
			System.out.println("isTarget:" + t0[k].getisTarget());
			System.out.println("空间名字:" + t0[k].getSpname());
		}
		*/
		
		String tempwide[]={"Fix","Rotate","Push"};
		String tempmid[]={"Fix","Push","Pull"};
		double x=0,y=300,z=550;
		int startrotate,endrotate,randomnumber;
		int randomNum1 = random.nextInt(3);
		int randomNum2 = random.nextInt(3);
		startrotate=(random.nextInt(12)-6)*10; //随机-6~5
		endrotate=startrotate+150;
		doc=printCreateCameraRule(doc,all,all0,x,y,z);
		doc=printShotRule(doc,tempwide[randomNum1],all,all0,1,startrotate,endrotate,1,framenum/2);
		
		startrotate=(random.nextInt(12)-6)*10; //随机-6~5
		if(tt1>0){
			randomnumber=random.nextInt(tt1);
			if(tt1==1)randomnumber=0;
			doc=printShotRule2(doc,tempmid[randomNum2],t1[randomnumber],2,startrotate,startrotate,framenum/2+1,framenum);
		}
		else{
			randomnumber=random.nextInt(tt0+1);
			randomnumber=randomnumber-1;
			if(tt0==1||randomnumber<0)randomnumber=0;
			doc=printShotRule2(doc,tempmid[randomNum2],t0[randomnumber],2,startrotate,startrotate,framenum/2+1,framenum);			
		}
		return doc;
	}
	
    public Document printCreateCameraRule(Document doc,SceneSpace all[],int num,double x,double y,double z) 
    {  
		 Element rootName = (Element) doc.getRootElement();
		 Element name = rootName.element("maName");
		 Element ruleName = name.addElement("rule");
		 ruleName.addAttribute("ruleType", "CreateCamera");
		 ruleName.addAttribute("CameraName", "newCamera");
		 ruleName.addAttribute("WideShotPointX", Double.toString(x));
		 ruleName.addAttribute("WideShotPointY", Double.toString(y));
		 ruleName.addAttribute("WideShotPointZ", Double.toString(z));	
	/*	 for(int i=0;i<num;i++){
			 String targetname;
			 targetname="target"+Integer.toString(i);
			 ruleName.addAttribute(targetname, all[i].getModelname());
		 }*/
		 return doc;
    }
	
    public Document printShotRule(Document doc,String shottype,SceneSpace target[],int num,int shootingscale,int startrotate,int endrotate,int startframe,int endframe) 
	{
    	Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
	    ruleName.addAttribute("ruleType", "SetCamera");
	    ruleName.addAttribute("CameraName", "newCamera");
		ruleName.addAttribute("type", shottype);
		String targetname="";
		String targetid="";
		for(int i=0;i<num;i++){
			targetname=targetname+target[i].getModelname()+" ";
			targetid=targetid+target[i].getModelid()+" ";
			//ruleName.addAttribute(targetname,target[i].getModelname());
		}
		ruleName.addAttribute("target",targetname);
		ruleName.addAttribute("usedModelID", targetid);
		String ss=ShootingScaleword(shootingscale);
		ruleName.addAttribute("shootingscale", ss);
		ruleName.addAttribute("shootingscaletype", Integer.toString(shootingscale));
		ruleName.addAttribute("startrotate",Integer.toString(startrotate));
		ruleName.addAttribute("endrotate",Integer.toString(endrotate));
		ruleName.addAttribute("startframe",Integer.toString(startframe));
		ruleName.addAttribute("endframe",Integer.toString(endframe));
		ruleName.addAttribute("startinall",Integer.toString(startframe));
		ruleName.addAttribute("endinall",Integer.toString(endframe));
		return doc;
    }
  
//下面是只对一个目标进行特写拍摄    
    public Document printShotRule2(Document doc,String shottype,SceneSpace target,int shootingscale,int startrotate,int endrotate,int startframe,int endframe) 
	{
    	Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
	    ruleName.addAttribute("ruleType", "SetCamera");
	    ruleName.addAttribute("CameraName", "newCamera");
		ruleName.addAttribute("type", shottype);
		String targetname;
		targetname=target.getModelname();
		
		ruleName.addAttribute("target",targetname);
		ruleName.addAttribute("usedModelID", target.getModelid());
		String ss=ShootingScaleword(shootingscale);
		ruleName.addAttribute("shootingscale", ss);
		ruleName.addAttribute("shootingscaletype", Integer.toString(shootingscale));
		ruleName.addAttribute("startrotate",Integer.toString(startrotate));
		ruleName.addAttribute("endrotate",Integer.toString(endrotate));
		ruleName.addAttribute("startframe",Integer.toString(startframe));
		ruleName.addAttribute("endframe",Integer.toString(endframe));
		ruleName.addAttribute("startinall",Integer.toString(startframe));
		ruleName.addAttribute("endinall",Integer.toString(endframe));
		return doc;
    }
    
    public String ShootingScaleword(int type){
    	switch(type){ 
    	case 0:  return "Very Wide Shot";
    	case 1:  return "Wide Shot";
    	case 2:  return "Mid Shot";
    	case 3:  return "Mid Close Up";
    	case 4:  return "Close Up";
    	default: return "nothing";
    	}
    }
	
    /**
	    * doc2XmlFile
	    * 将Document对象保存为一个xml文件到本地
	    * @return true:保存成功  flase:失败
	    * @param filename 保存的文件名
	    * @param document 需要保存的document对象
	    */
	public boolean doc2XmlFile(Document document, String filename) {
		boolean flag = true;
		try {
			/* 将document中的内容写入文件中 */
			// 默认为UTF-8格式，指定为"GB2312"
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("GB2312");
			XMLWriter writer = new XMLWriter(
					new FileWriter(new File(filename)), format);
			writer.write(document);
			writer.close();
		} catch (Exception ex) {
			flag = false;
			ex.printStackTrace();
		}
		return flag;
	}
}
