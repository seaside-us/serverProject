package plot;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
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

public class SelectLayout2 {
	/**
	 * 根据上层的xml中输出的可用场景空间（SP_car_A）进行选择布局
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<String> selectLayout2(OWLModel owlmodel,String ieTopic,ArrayList a,ArrayList<HashMap<String,String>> areashape)
	{	
		//ArrayList a=getSpace(al);
		ArrayList<HashMap<String,Integer>> modelclass=new ArrayList();
		//ArrayList<HashMap<String,String>> areashape=new ArrayList();
		//String[] className={"p1:ArtWork","p1:Device","p1:Product","p1:StationaryArtifact","p1:Animal","p1:Plant","p1:Food","p1:Substance"};
		String[] class1={"p1:CorpuscularObject","p1:Food"};
		String[] class2={"p1:Artifact","p1:OrganicObject"};
		String[] class3={"p1:ArtWork","p1:Device","p1:Product","p1:StationaryArtifact","p1:OrganicObject"};
		String[] layoutEffect={"SymmetryLayout","RhythmLayout","ComparedLayout"};
		String[] comparedTopic={"AngryTopic","SadTopic","GriefTopic","CareForTopic","CryTopic","DisAgreeTopic","WorryTopi"};
		String[] rhythmTopic={"SmileTopic","GladTopic"};
		boolean hasEffect=false;
 		String str="p3:";
		String otherclass="others";
		ArrayList<String> layEffect=new ArrayList<String>();
		//判断主题
		for(int i=0;i<comparedTopic.length;i++){
			if(comparedTopic[i].equals(ieTopic)){
				hasEffect=true;
				for(int j=0;j<a.size();j++){
					layEffect.add("ComparedLayout");
				}
			}
		}
		if(!hasEffect){
			for(int i=0;i<rhythmTopic.length;i++){
				if(rhythmTopic[i].equals(ieTopic)){
					hasEffect=true;
					for(int j=0;j<a.size();j++){
						layEffect.add("RhythmLayout");
					}
				}
			}
		}
		//获取一个可用空间中所有的模型
		//获取所有空间
		for(int i=0;i<a.size();i++){
			if(!hasEffect){
				Random r=new Random();
				layEffect.add(layoutEffect[r.nextInt(layoutEffect.length)]);
			}
			ArrayList<String> area1=new ArrayList<String>();
			ArrayList<String> area2=new ArrayList<String>(); 
			ArrayList<String> area3=new ArrayList<String>(); 
			int num1=0;
			int num2=0;
			int num3=0;
			HashMap<String,Integer> map=new HashMap<String,Integer>();
			SceneSpace sp1=(SceneSpace)a.get(i);
			ArrayList<String> modellist=sp1.getModellist();
			ArrayList<String> modelidlist=sp1.getModelidlist();
			ArrayList<Integer> numberlist=sp1.getNumberlist(); 
			//对于空间中每一个模型，判断其所属类型
			for(int k=0;k<modellist.size();k++){
				//若在列表中，将此类型和模型个数添加到哈希表中
				if(SelectLayout2.modelBelongsClass(owlmodel,class1[0],modellist.get(k))){
					area1.add(modelidlist.get(k));
					num1+=numberlist.get(k);
				}
				/*
				else if(SelectLayout2.modelBelongsClass(owlmodel,class1[1],modellist.get(k))){
					area2.add(modelidlist.get(k));
					num2+=numberlist.get(k);
				}*/
				else{
					area2.add(modelidlist.get(k));
					num2+=numberlist.get(k);
				}
			}
			if(area2.isEmpty())
			{
				area1.clear();
				area2.clear();
				area3.clear();
				num1=0;
				num2=0;
				num3=0;
				
				for(int k=0;k<modellist.size();k++){
					//若在列表中，将此类型和模型个数添加到哈希表中
					if(SelectLayout2.modelBelongsClass(owlmodel,class2[0],modellist.get(k))){
						area1.add(modelidlist.get(k));
						num1+=numberlist.get(k);
					}
					/*
					else if(SelectLayout2.modelBelongsClass(owlmodel,class2[1],modellist.get(k))){
						area2.add(modelidlist.get(k));
						num2+=numberlist.get(k);
					}*/
					else{
						area2.add(modelidlist.get(k));
						num2+=numberlist.get(k);
					}
				}
				
			}
			/*
			if(area2.isEmpty() || area3.isEmpty())
			{
				area1.clear();
				area2.clear();
				area3.clear();
				num1=0;
				num2=0;
				num3=0;
				if(area2.isEmpty() && area3.isEmpty())
				{
					for(int k=0;k<modellist.size();k++){
						//若在列表中，将此类型和模型个数添加到哈希表中
						if(SelectLayout2.modelBelongsClass(owlmodel,class3[0],modellist.get(k)) ||
								SelectLayout2.modelBelongsClass(owlmodel,class3[1],modellist.get(k))){
							area1.add(modelidlist.get(k));
							num1+=numberlist.get(k);
						}
						else if(SelectLayout2.modelBelongsClass(owlmodel,class3[2],modellist.get(k)) || 
								SelectLayout2.modelBelongsClass(owlmodel,class3[3],modellist.get(k))){
							area2.add(modelidlist.get(k));
							num2+=numberlist.get(k);
						}
						else{
							area3.add(modelidlist.get(k));
							num3+=numberlist.get(k);
						}
					}
				}
				else{
					for(int k=0;k<modellist.size();k++){
						//若在列表中，将此类型和模型个数添加到哈希表中
						if(SelectLayout2.modelBelongsClass(owlmodel,class2[0],modellist.get(k))){
							area1.add(modelidlist.get(k));
							num1+=numberlist.get(k);
						}
						else if(SelectLayout2.modelBelongsClass(owlmodel,class2[1],modellist.get(k))){
							area2.add(modelidlist.get(k));
							num2+=numberlist.get(k);
						}
						else{
							area3.add(modelidlist.get(k));
							num3+=numberlist.get(k);
						}
					}
				}
				
			}*/
			if(!area1.isEmpty())
			{
				String s="";
				for(int k=0;k<area1.size();k++)
				{
					if(k==0)
					{
						s=area1.get(k);
					}
					else
					{
						s=s+" "+area1.get(k);
					}
				}
				map.put(s, num1);
			}
			if(!area2.isEmpty())
			{
				String s="";
				for(int k=0;k<area2.size();k++)
				{
					if(k==0)
					{
						s=area2.get(k);
					}
					else
					{
						s=s+" "+area2.get(k);
					}
				}
				map.put(s, num2);
			}
			if(!area3.isEmpty())
			{
				String s="";
				for(int k=0;k<area3.size();k++)
				{
					if(k==0)
					{
						s=area3.get(k);
					}
					else
					{
						s=s+" "+area3.get(k);
					}
				}
				map.put(s, num3);
			}
			modelclass.add(map);
		}
		/*
		for(int i=0;i<a.size();i++){
			if(!hasEffect){
				Random r=new Random();
				layEffect.add(layoutEffect[r.nextInt(layoutEffect.length)]);
			}
			HashMap<String,Integer> map=new HashMap();
			SceneSpace sp1=(SceneSpace)a.get(i);
			ArrayList<String> modellist=sp1.getModellist();
			ArrayList<Integer> numberlist=sp1.getNumberlist(); 
			//对于空间中每一个模型，判断其所属类型
			for(int k=0;k<modellist.size();k++){
				boolean isinclass=false;
				//判断此模型的类型是否在所列的表中
				for(int j=0;j<className.length;j++){
					//若在列表中，将此类型和模型个数添加到哈希表中
					if(SlectLayout2.modelBelongsClass(owlmodel,className[j],modellist.get(k))){
						isinclass=true;
						boolean hasValue=false;
						//查看哈希表中是否已经添加了此类
						for(int l=0;l<map.size();l++){
							//若已添加，修改模型个数
							if(map.containsKey(className[j])){
								hasValue=true;
								int newvalue=map.get(className[j])+numberlist.get(k);
								map.put(className[j], newvalue);
							}
						}
						if(!hasValue){
							map.put(className[j], numberlist.get(k));//若未添加，则添加到表中
						}
					}
				}
				if(!isinclass){
					boolean hasValue=false;
					//查看哈希表中是否已经添加了此类
					for(int l=0;l<map.size();l++){
						//若已添加，修改模型个数
						if(map.containsKey(otherclass)){
							hasValue=true;
							map.put(otherclass, map.get(otherclass)+numberlist.get(k));
						}
					}
					if(!hasValue){
						map.put(otherclass, numberlist.get(k));//若未添加，则添加到表中
					}
				}
			}
			modelclass.add(map);
		}
		*/
		//测试计算是否正确
		/*
		for(int i=0;i<a.size();i++){
			System.out.println("第"+(i+1)+"个可用空间");
			SceneSpace sp1=(SceneSpace)a.get(i);
			ArrayList<String> modellist=sp1.getModellist();
			ArrayList<Integer> numberlist=sp1.getNumberlist();
			for(int j=0;j<modellist.size();j++){
				System.out.println(modellist.get(j)+": "+numberlist.get(j));
			}
		}
		System.out.println("================================");
		
		for(int i=0;i<modelclass.size();i++){
			System.out.println("第"+(i+1)+"个可用空间");
			HashMap testmap=modelclass.get(i);
			Iterator iter = testmap.entrySet().iterator();
			while (iter.hasNext()) {
				HashMap.Entry entry = (HashMap.Entry) iter.next();
				System.out.println(entry.getKey()+": "+entry.getValue());
			}
		}
		*/
		//设置每个可用空间中每个区域的摆放形状
		for(int i=0;i<modelclass.size();i++){
			HashMap<String,Integer> testmap=modelclass.get(i);
			HashMap<String,String> newmap=new HashMap();
			Iterator iter = testmap.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, Integer> entry = (Entry) iter.next();
				int x=(Integer)entry.getValue();
				String shape;
				if(x==1){
					shape="point";
				}
				else if(x==2){
					shape="line";
				}
				else if(x==3){
					shape="triangle";
				}
				else{
					shape="othertype";
				}
				newmap.put((String)entry.getKey(), shape);
			}
			areashape.add(newmap);
		}
		/*
		for(int i=0;i<areashape.size();i++){
			System.out.println("第"+(i+1)+"个可用空间");
			HashMap testmap=areashape.get(i);
			Iterator iter = testmap.entrySet().iterator();
			while (iter.hasNext()) {
				HashMap.Entry entry = (HashMap.Entry) iter.next();
				System.out.println(entry.getKey()+": "+entry.getValue());
			}
		}
		*/
		return layEffect;
	}
	
	public static boolean modelBelongsClass(OWLModel owlmodel,String classname,String modelname)
	{
		ArrayList<OWLIndividual> Indivi=new ArrayList();
		OWLNamedClass owlClass = owlmodel.getOWLNamedClass(classname);
		Collection Individual=owlClass.getInstances();
		for(Iterator iNothing=Individual.iterator();iNothing.hasNext();)
		{
			OWLIndividual iindivi=(OWLIndividual)iNothing.next();
			String indiviname=iindivi.getName();
			if(modelname.equals(indiviname.substring(indiviname.lastIndexOf("#")+1))){
				return true;
			}
		}
		return false;
	}
}

