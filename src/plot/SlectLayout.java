package plot;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

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
import edu.stanford.smi.protegex.owl.model.RDFProperty;

public class SlectLayout {
	/**
	 * 根据上层的xml中输出的可用场景空间（SP_car_A）进行选择布局
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<String> selectLayout(OWLModel owlmodel,ArrayList<OWLIndividual> spIndividual,ArrayList a)
	{	String str="p3:";
		HashMap map=new HashMap();
		int numtemp=0;
		Layout lay=new Layout();
		ArrayList layName=new ArrayList();
		ArrayList<Integer> layNameNumtemp=new ArrayList<Integer>();
		OWLIndividual indiual=null;
		OWLObjectProperty hasLayoutObjectProperty=owlmodel.getOWLObjectProperty("p3:hasLayout");
		String objectP=hasLayoutObjectProperty.getBrowserText();
		System.out.println("objectP:"+objectP+hasLayoutObjectProperty);
		OWLDatatypeProperty areaNum=owlmodel.getOWLDatatypeProperty(str+"AreaNum");
		String objectname=null;
		ArrayList<OWLIndividual> s=SlectLayout.selectInstance(spIndividual,a);
		for(int i=0;i<s.size();i++)
		{
    		indiual=s.get(i);
    		for(int l=0;l<a.size();l++)
    		{
    			
    			  SceneSpace ss=(SceneSpace)a.get(l);
    			  System.out.println("ss"+ss);
    			  if(indiual.getBrowserText().contains(ss.getSpname()))
      			   {
    			        numtemp=ss.getNumber();
    			        System.out.println("可用空间的名字"+indiual.getBrowserText()+"模型的数量"+numtemp);
    			        break;
    			   }
    		}
    		
    		System.out.println("zhi "+"indual"+indiual+indiual.getPropertyValues(hasLayoutObjectProperty));
    		int valueNum=indiual.getPropertyValueCount(hasLayoutObjectProperty);
    		System.out.println("valueNum+"+valueNum);
    		if(valueNum>1)//某一个布局实例下面有多个布局
			 {
				 Collection templateModelVlaues=indiual.getPropertyValues(hasLayoutObjectProperty);
				 //对于某一个布局实例下面有多个布局时，随机选择一个布局
				 for(Iterator<OWLIndividual> its2=templateModelVlaues.iterator();its2.hasNext();)
				 {
					 OWLIndividual its2indiual=its2.next();
					 String value=its2indiual.getBrowserText();
					 System.out.println("当含有多个布局时"+value+"获得对应的布局名字");
					String areaNums= (String) ReadOwl.getDataProperty(its2indiual, areaNum);
					System.out.println("布局对应的区域数量为："+areaNums);
					 layNameNumtemp.add(Integer.parseInt(areaNums));
					 map.put( areaNums,value);
				 }
				 //测试输出map的值
				 Iterator iterator = map.keySet().iterator();                
		            while (iterator.hasNext()) {    
		             Object key = iterator.next();    
		             System.out.println("key"+key);
		             System.out.println("map.get(key) is :"+map.get(key));    
		            }
				 Collections.sort(layNameNumtemp);//对应知识库值布局可放的模型数量进行排序
				 int flag=0;
				 for(int l=0;l<layNameNumtemp.size();l++)
				 {
					if(numtemp<=layNameNumtemp.get(l))
					{
						System.out.println("123456789564"+layNameNumtemp.get(l));
						String temp=layNameNumtemp.get(l).toString();
						layName.add(map.get(temp));
						flag=1;
						break;
					}
				 }
				 if(flag!=1)
				 {
					 System.out.println("jieguoshi"+layNameNumtemp.get(layNameNumtemp.size()-1));
					 String sname=(String)map.get(layNameNumtemp.get(layNameNumtemp.size()-1).toString());
					 System.out.println("nidde"+sname);
					 layName.add(sname);
				 }
				// Random rd1 = new Random();
				// int j=rd1.nextInt(layNametemp.size());
				// layName.add(layNametemp.get(j));
			 }
    		else
    		{
    		  objectname=(String)ReadOwl.getObjectProProperty(indiual, hasLayoutObjectProperty);
   			   System.out.println(objectname+"获得对应的布局名字");
   			   layName.add(objectname);
    		}
    		
			
		}
		//遍历查看选择的布局是否正确
		for(int i1=0;i1<layName.size();i1++)
		{
			System.out.println("最后选择的布局是"+layName.get(i1));
		}
		
	return layName;
	
	}
	/**
	 * //获取布局的形状
	 */
	public static ArrayList<String> getLayShap(OWLModel owlmodel,ArrayList<String> layName)
	{
		String str="p3:";
		//获取布局的形状
		ArrayList<String> layShape=new ArrayList<String>();
		OWLDatatypeProperty data=owlmodel.getOWLDatatypeProperty(str+"LayoutShape");
		ArrayList<OWLIndividual> layNameowl=ReadOwl.getInstance(owlmodel, str+"Layout");
		for(int i=0;i<layName.size();i++)
		{
			for(int j=0;j<layNameowl.size();j++)
			{
				if(layName.get(i).equals(layNameowl.get(j).getBrowserText()))
				{
					String shape =(String)ReadOwl.getDataProperty(layNameowl.get(j), data);
					System.out.println("形状是"+shape);
					layShape.add(shape);
				}
			}
			
		}
		return layShape;
	}
	
	
	/**
	 * 从owl文件可用空间场景中查找与xml中输出的可用空间场景名字相同的实例，返回实例的名字
	 * 
	 */
	public static ArrayList <OWLIndividual> selectInstance(ArrayList<OWLIndividual> spIndividual,ArrayList a)
	{
		ArrayList <OWLIndividual> owlindiual=new ArrayList();;
		String spname=null;
		OWLIndividual indiual=null;
		for(int i=0;i<spIndividual.size();i++)
		{
			spname=spIndividual.get(i).getBrowserText();
			//System.out.println("节点的名字"+spname);
			for(int j=0;j<a.size();j++)
			{
				SceneSpace ss=(SceneSpace)a.get(j);
			   if(spname.equals(ss.getSpname()))
			   {
			    indiual=spIndividual.get(i);
			    System.out.println("aaaaaaaaaaa"+indiual.getBrowserText());
			    owlindiual.add(indiual);
			   }//break;
			}
			
		}
		for(int i=0;i<owlindiual.size();i++)
		{
			indiual=owlindiual.get(i);
			System.out.println(indiual.getBrowserText()+"++++++++++++++++++++++++++");
		}
		
		
		return owlindiual;
		
	}
	
}

