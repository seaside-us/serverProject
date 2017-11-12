package plot;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


public class XMLInfoFromIEDom4j {
	/**
	    * load
	    * ����һ��xml�ĵ�
	    * @return �ɹ�����Document����ʧ�ܷ���null
	    * @param uri �ļ�·��
	    */
	   public static Document readXMLFile(String filename)
	   {
	      Document document = null;
	      try 
	      { 
	          SAXReader saxReader = new SAXReader();
	          document = saxReader.read(new File(filename));
	      }
	      catch (Exception ex){
	          ex.printStackTrace();
	      }  
	      return document;
	   }
	   /**
	    * string2XmlFile
	    * ��xml��ʽ���ַ�������Ϊ�����ļ�������ַ�����ʽ������xml�����򷵻�ʧ��
	    * @return true:����ɹ�  flase:ʧ��
	    * @param filename ������ļ���
	    * @param str ��Ҫ������ַ���
	    */
	   public static boolean string2XmlFile(String str,String filename)
	   {
	      boolean flag = true;
	      try
	      {
	         Document doc =  DocumentHelper.parseText(str);       
	         flag = doc2XmlFile(doc,filename);
	      }catch (Exception ex)
	      {
	         flag = false;
	         ex.printStackTrace();
	      }
	      return flag;
	   } 
	   /**
	    * doc2String
	    * ��xml�ĵ�����תΪString
	    * @return �ַ���
	    * @param document
	    */
	   public static String doc2String(Document document)
	   {
	      String s = "";
	      try
	      {
	           //ʹ�������������ת��
	           ByteArrayOutputStream out = new ByteArrayOutputStream();
	           //ʹ��GB2312����
	           OutputFormat format = new OutputFormat("  ", true, "GB2312");
	           XMLWriter writer = new XMLWriter(out, format);
	           writer.write(document);
	           s = out.toString("GB2312");
	      }catch(Exception ex)
	      {            
	           ex.printStackTrace();
	      }      
	      return s;
	   } 
	   /**
	    * doc2XmlFile
	    * ��Document���󱣴�Ϊһ��xml�ļ�������
	    * @return true:����ɹ�  flase:ʧ��
	    * @param filename ������ļ���
	    * @param document ��Ҫ�����document����
	    */
	   public static boolean doc2XmlFile(Document document,String filename)
	   {
	      boolean flag = true;
	      try
	      {
	            /* ��document�е�����д���ļ��� */
	            //Ĭ��ΪUTF-8��ʽ��ָ��Ϊ"GB2312"
	            OutputFormat format = OutputFormat.createPrettyPrint();
	            format.setEncoding("GB2312");
	            XMLWriter writer = new XMLWriter(new FileWriter(new File(filename)),format);
	            writer.write(document);
	            writer.close();            
	        }catch(Exception ex)
	        {
	            flag = false;
	            ex.printStackTrace();
	        } 
	        return flag;
	   }
	   /*
	    * ����һ��XML�ļ�
	    */
	   
	   public static String  writeXML(String xmlName)
	   {
		   Document doc = org.dom4j.DocumentHelper.createDocument();  
		   Element rootElement = doc.addElement("result"); 
		   //String xmlName="adl_result.xml";
		   String xmlPath=null;
		  // Element name = rootElement.addElement("maName");  
		   //name.addAttribute("name", "happy_mm.ma");
		   //Element hasModel=name.addElement("hasModel");
		   //name.addText("happy_mm.ma");
		   //����ӽڵ�  
		    try {		    	
			    OutputFormat fmt = new OutputFormat();  
			        //���������ʽ���� 
			    fmt.setEncoding("gb2312");  
			    XMLWriter writer = new XMLWriter(fmt);  
			   //�������ʽΪ����,����XML�ļ�������� 
			   xmlPath="PlotDataOut\\"+xmlName;
			   OutputStream out = new FileOutputStream(xmlPath);  
			   //���������..  
			   writer.setOutputStream(out);  
			  //���������  
			    writer.write(doc); 
			    
			   //���doc����,���γ�XML�ļ�  
			   } catch (Exception e) 
			   { 
				   e.printStackTrace();  
			    }
			   return xmlPath;
		   
	   }

}
