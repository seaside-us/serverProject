package plot;

import java.sql.Connection;

import java.sql.SQLException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream; 
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;
import java.util.regex.Matcher;



public class sceneselectbyDecisionTree {
	
public static void ReadTemplate (Connection con, ArrayList<String> englishTemplate,int scenecount) throws SQLException{
	
	Statement stmt=null;
    stmt=con.createStatement();
    
    FileOutputStream os = null;
	BufferedWriter bw = null;
	
	FileOutputStream os1 = null;
	BufferedWriter bw1 = null;
	
	String str="";
	
	 String s="";
	 int lengh=englishTemplate.size();	
	 try {
			
		 os = new FileOutputStream("D:\\Template.txt");		  
		 bw = new BufferedWriter(new OutputStreamWriter(os));
		
		 os1 = new FileOutputStream("D:\\1111111.txt");		  
		 bw1 = new BufferedWriter(new OutputStreamWriter(os1));
		 
   if(lengh==0)   
		 {  
			 s="0,";  
		     bw.write(s);		     
			 for(int j=1;j<scenecount;j++) bw.write("\r\n"+s);
		 }
		 
	else{ 
		 
       for(int j=0;j<lengh;j++) {	 
	 
	       str=englishTemplate.get(j);
	 
	       String[] temp1 = str.split(":");
	
	       int count=temp1.length;		 
	
	       for(int n=0;n<count;n++){
	    	  
	    	   bw1.write("\r\n"+temp1[n]); 	 
			   String sql="select * from template where templatename = '"+temp1[n]+"'";
			   ResultSet rs=stmt.executeQuery(sql);
	    
	           int str1=0;
	           
	           if(rs.next()){
	    	                 
	    	                 str1=rs.getInt(4);	 		    	
	    	                 bw.write(str1+",");
	    	                 s=s+str1+",";
	    	                 }
	           else {s=s+"0,";   bw.write("0,");}
	              
		   }
       }
		   		 
		 for(int n=1;n<scenecount;n++) bw.write("\r\n"+s);
		   
		   stmt.close();
		   
		 }
	
	 
	 
	 }
		   
		   
		   
		   
		   
	
	  catch (FileNotFoundException e) {
			   System.out.println("找不到指定文件");
			  } 
		catch (IOException e) {
			   System.out.println("读取文件失败");
			  }
		finally {
			   try {
				 bw.close();
				 os.close();
				 bw1.close();
				 os1.close();
			   
			   
			    // 关闭的时候最好按照先后顺序关闭最后开的先关闭所以先关s,再关n,最后关m
			   } 
			   catch (IOException e) {
			    e.printStackTrace();
			   }
			  }
	 
	}
	
public static void ReadTopic (Connection con, String englishTopicStr,int scenecount) throws SQLException{
	
	Statement stmt=null;
    stmt=con.createStatement();
    
    FileOutputStream os = null;
	BufferedWriter bw = null;
	
	
    try {
		   String str = "";
		   os = new FileOutputStream("D:\\Topic.txt");
		   bw = new BufferedWriter(new OutputStreamWriter(os));
		   
		   str=englishTopicStr;
			   
			String sql="select * from Topic where topicname = '"+str+"'";
			    
	        ResultSet rs=stmt.executeQuery(sql);
	         String str1="";
	    
	         if(rs.next()){
	    	           
	    	           str1=rs.getInt(2)+","+rs.getInt(3)+",";	 	    	         
	    	           bw.write(str1);}
	         else {str1="0,0";bw.write(str1);}                  
	         for(int j=1;j<scenecount;j++) bw.write("\r\n"+str1);
	         stmt.close(); 
	              
		   }
		   
		   
		    
		   
    
		   catch (FileNotFoundException e) {
			   System.out.println("找不到指定文件");
			  } 
		catch (IOException e) {
			   System.out.println("读取文件失败");
			  }
		finally {
			   try {
				 bw.close();
				 os.close();
			   
			    // 关闭的时候最好按照先后顺序关闭最后开的先关闭所以先关s,再关n,最后关m
			   } 
			   catch (IOException e) {
			    e.printStackTrace();
			   }
			  }
}

public static void ReadmaFile (Connection con,ArrayList<SceneCase> sceneList) throws SQLException{
	
	
	Statement stmt=null;
    stmt=con.createStatement();
	FileOutputStream os = null;
	BufferedWriter bw = null;
	
	try {
	   String str = "";
	   
	   
	    os = new FileOutputStream("D:\\mafile.txt");
	    bw = new BufferedWriter(new OutputStreamWriter(os));
	    int i=sceneList.size();
	    for(int j=0;j<i;j++){
	    	
		   
		    str=sceneList.get(j).sceneName;
	    	String sql2="select * from mafile where maFilename = '"+str+"'";
		    
		    ResultSet rs=stmt.executeQuery(sql2);
				    
		    if(rs.next()){
		    	String str1;
		    	str1=rs.getInt(2)+","+rs.getInt(3)+",";
		    	bw.write(str1+"\r\n");
		        System.out.println(str1);	            
		                 }		
		    else { str="0,0";  bw.write(str+"\r\n");}
	  }
	        
	    stmt.close();
	    
	}
	catch (FileNotFoundException e) {
		   System.out.println("找不到指定文件");
		  } 
	catch (IOException e) {
		   System.out.println("读取文件失败");
		  }
	finally {
		   try {
			 bw.close();
			 os.close();
		    
		    // 关闭的时候最好按照先后顺序关闭最后开的先关闭所以先关s,再关n,最后关m
		   } 
		   catch (IOException e) {
		    e.printStackTrace();
		   }
		  }
	}
public static void integration () {
	
	FileInputStream fis1 = null;
	InputStreamReader isr1 = null;
	BufferedReader br1 = null; 		
	FileInputStream fis2 = null;
	InputStreamReader isr2 = null;
	BufferedReader br2 = null; 
	FileInputStream fis3 = null;
	InputStreamReader isr3 = null;
	BufferedReader br3 = null; 
	
	FileOutputStream os = null;
	BufferedWriter bw = null;
	BufferedWriter bw1 = null;
	
	try {
		   String str1 = "",str2 = "",str3 = "";
		   fis1 = new FileInputStream("D:\\Topic.txt");// FileInputStream
		   isr1 = new InputStreamReader(fis1);// InputStreamReader 是字节流通向字符流的桥梁,
		   br1 = new BufferedReader(isr1);// 从字符输入流中读取文件中的内容,封装了一个new InputStreamReader的对象
		   
		   fis2 = new FileInputStream("D:\\mafile.txt");// FileInputStream
		   isr2 = new InputStreamReader(fis2);// InputStreamReader 是字节流通向字符流的桥梁,
		   br2 = new BufferedReader(isr2);// 从字符输入流中读取文件中的内容,封装了一个new InputStreamReader的对象
		 
		   fis3 = new FileInputStream("D:\\Template.txt");// FileInputStream
		   isr3 = new InputStreamReader(fis3);// InputStreamReader 是字节流通向字符流的桥梁,
		   br3 = new BufferedReader(isr3);// 从字符输入流中读取文件中的内容,封装了一个new InputStreamReader的对象
		   
		   
		   os = new FileOutputStream("D:\\1.txt");
		   bw = new BufferedWriter(new OutputStreamWriter(os));
		   bw1  = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:\\2.txt", true)));                        
		   bw1.write("/***************************************/"+"\r\n");
		   while ((str1 = br1.readLine())!= null&&(str2 = br2.readLine())!= null&&(str3 = br3.readLine()) != null) 
		   {
	
	
			 	      
			   String[] temp1 = str1.split(",");
			   String[] temp2 = str2.split(",");
			   String[] temp3 = str3.split(",");
			   int i[]=new int[18];
			   for(int a=0;a<18;a++){i[a]=0;}
			   
			   i[0]=Integer.parseInt(temp2[0]);
			   i[1]=Integer.parseInt(temp2[1]);
			   i[2]=Integer.parseInt(temp1[0]);
			   i[3]=Integer.parseInt(temp1[1]);
			   int len=temp3.length;
			  
			   
			   
			  
			   for(int a=0;a<len;a++){
				
				  int b=Integer.parseInt(temp3[a]);					   
				  
				  if( (b>=1)&&(b<=17)) i[4]=b;
				  if( (b>=18)&&(b<=24)) i[5]=b;
				  if( (b>=25)&&(b<=29)) i[6]=b;
				  if( (b>=30)&&(b<=40)) i[7]=b;
				  if( (b>=41)&&(b<=46)) i[8]=b;
				  if( (b>=47)&&(b<=62)) i[9]=b;
				  if( (b>=63)&&(b<=67)) i[10]=b;
				  if( (b>=68)&&(b<=77)) i[11]=b;
				  if( (b>=78)&&(b<=85)) i[12]=b;
				  if( (b>=86)&&(b<=87)) i[13]=b;
				  if( (b>=88)&&(b<=98)) i[14]=b;
				  if( (b>=99)&&(b<=119)) i[15]=b;
				  if( (b>=120)&&(b<=131)) i[16]=b;
				  if( (b>=132)&&(b<=135)) i[17]=b;}
		  
			      for(int a=0;a<18;a++) {bw.write(i[a]+",");bw1.write(i[a]+",");}
		   
		   	      bw.write("\r\n");	bw1.write("\r\n");	}  
				   
	}		   
	
	catch (FileNotFoundException e) {
		   System.out.println("找不到指定文件");
		  } 
	catch (IOException e) {
		   System.out.println("读取文件失败");
		  }
	finally {
		   try {
			 bw.close();
			 bw1.close();
			os.close();
		     br3.close();
		     isr3.close();
		     fis3.close();
		     br2.close();
		     isr2.close();
		     fis2.close();
		     br1.close();
		     isr1.close();
		     fis1.close();
		    // 关闭的时候最好按照先后顺序关闭最后开的先关闭所以先关s,再关n,最后关m
		   } 
		   catch (IOException e) {
		    e.printStackTrace();
		   }
		  }

}
public  double[] tmain (String englishTopicStr,ArrayList<String> englishTemplate,ArrayList<SceneCase> sceneList,int scenecount,double leafrate[])throws SQLException{
		  
	    
	    if(englishTopicStr=="") englishTopicStr="NULL";
	  
	   /* String s="";
	    int count=englishTemplate.size();
		for(int i=0;i<count;i++){
			   
			   s=englishTemplate.get(i);
			   System.out.println(i+s);
			   
		}*/
	    
	    
		Connection con=null;	
		try {
			  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");   
			    }
			
			catch (Exception e) 
			{
	        e.printStackTrace();
	      }  
			
			String url = "jdbc:sqlserver://localhost:1433;DatabaseName=decision";
		        String user = "sa";
		        String password = "123456";
			con=DriverManager.getConnection(url, user, password);	
			
			
			
			
			ReadTemplate (con, englishTemplate,scenecount);			
			ReadTopic(con, englishTopicStr,scenecount);
			ReadmaFile ( con,sceneList);
			integration ();
			
			
			Linklist list=new Linklist();
			leafrate=list.Linklistmain(leafrate);
			
			con.close(); 
			return  leafrate;
		
		
		
		
	}

}
