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
	    * 载入一个xml文档
	    * @return 成功返回Document对象，失败返回null
	    * @param uri 文件路径
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
	    * 将xml格式的字符串保存为本地文件，如果字符串格式不符合xml规则，则返回失败
	    * @return true:保存成功  flase:失败
	    * @param filename 保存的文件名
	    * @param str 需要保存的字符串
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
	    * 将xml文档内容转为String
	    * @return 字符串
	    * @param document
	    */
	   public static String doc2String(Document document)
	   {
	      String s = "";
	      try
	      {
	           //使用输出流来进行转化
	           ByteArrayOutputStream out = new ByteArrayOutputStream();
	           //使用GB2312编码
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
	    * 将Document对象保存为一个xml文件到本地
	    * @return true:保存成功  flase:失败
	    * @param filename 保存的文件名
	    * @param document 需要保存的document对象
	    */
	   public static boolean doc2XmlFile(Document document,String filename)
	   {
	      boolean flag = true;
	      try
	      {
	            /* 将document中的内容写入文件中 */
	            //默认为UTF-8格式，指定为"GB2312"
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
	    * 构造一个XML文件
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
		   //添加子节点  
		    try {		    	
			    OutputFormat fmt = new OutputFormat();  
			        //创建输出格式对象 
			    fmt.setEncoding("gb2312");  
			    XMLWriter writer = new XMLWriter(fmt);  
			   //以输出格式为参数,创建XML文件输出对象 
			   xmlPath="PlotDataOut\\"+xmlName;
			   OutputStream out = new FileOutputStream(xmlPath);  
			   //创建输出流..  
			   writer.setOutputStream(out);  
			  //设置输出流  
			    writer.write(doc); 
			    
			   //输出doc对象,即形成XML文件  
			   } catch (Exception e) 
			   { 
				   e.printStackTrace();  
			    }
			   return xmlPath;
		   
	   }

}
