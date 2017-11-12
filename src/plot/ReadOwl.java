package plot;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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




public class ReadOwl {
	/*
	 * 获取实例individual的hasObjectProperty对象属性的值
	 */
	public static String getObjectProProperty(OWLIndividual individual,OWLObjectProperty hasObjectProperty)
	{
		String objetvalue=null;
		objetvalue=((OWLIndividual)individual.getPropertyValue(hasObjectProperty)).getBrowserText();
		return objetvalue;
	}
	/*
	 * 获取实例individual的数据对象dataproperty的值
	 */
	public static Object getDataProperty(OWLIndividual individual,OWLDatatypeProperty dataproperty)
	{
		Object datavalue=null;
		datavalue=individual.getPropertyValue(dataproperty);
		return datavalue;
		
	}
	/*
	 * 获取类classname下面的所有实例
	 */
	public static ArrayList<OWLIndividual> getInstance(OWLModel owlmodel,String classname)
	{
		ArrayList<OWLIndividual> Indivi=new ArrayList();
		OWLNamedClass owlClass = owlmodel.getOWLNamedClass(classname);
		Collection Individual=owlClass.getInstances();
		for(Iterator iNothing=Individual.iterator();iNothing.hasNext();)
		{
			//System.out.println("ffffffffffffffffff"+pizzaIndividual.size());
			OWLIndividual iindivi=(OWLIndividual)iNothing.next();
			Indivi.add(iindivi);
		}
		 //测试显示从owl中读取的所有实例
	    /* 
	        String name1=null;
	     for(int i=0;i<Indivi.size();i++)
		 {
			name1=Indivi.get(i).getBrowserText();
			System.out.println(name1+"++++++++++++++");
			if(name1.equals("p2:SP_Hills14_C"))
			{
				System.out.println("+++++++"+name1+"++++++++++++++");
			}
		 }
	     */
		return Indivi;
		
	}
	
	

}
