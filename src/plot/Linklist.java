package plot;

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


class linknode{
		int a;
		int b;
		linknode next;
		 public linknode(){
			 a=-1;
			 b=-1;
			next=null;
			}
		 public linknode(int m,int n){
			a=m;
			b=n;
			next=null;
			}
	}
class sample {
	int maFileClass;
	int maFileNum;
	int TopicClass;
	int TopicNum;
	int ActionTemplateNum;
	int AnimalTemplateNum;
	int FineryTemplateNum;
	int DailyThingsTemplateNum;
	int FoodTemplateNum;
	int PlaceTemplateNum;
	int SeasonTemplateNum;
	int VehicleTemplateNum;
	int WeatherTemplateNum;
	int EntertainmentTemplateNum;
	int CharacterTemplateNum;
	int MoodTemplateNum;
	int TimeTemplateNum;
	int FlowerGrassTemplateNum;
	public sample(){
		 maFileClass=0;
		 maFileNum=0;
		 TopicClass=0;
		 TopicNum=0;
		 ActionTemplateNum=0;
		 AnimalTemplateNum=0;
		 FineryTemplateNum=0;
		 DailyThingsTemplateNum=0;
		 FoodTemplateNum=0;
		 PlaceTemplateNum=0;
		 SeasonTemplateNum=0;
		 VehicleTemplateNum=0;
		 WeatherTemplateNum=0;
		 EntertainmentTemplateNum=0;
		 CharacterTemplateNum=0;
		 MoodTemplateNum=0;
		 TimeTemplateNum=0;
		 FlowerGrassTemplateNum=0;		
		
	}
	public sample(int a,int b,int c,int d,int e,int f,int g,int h,int i,int j,int k,int l,int m,int n,int o,int p,int q,int r){
		 maFileClass=a;
		 maFileNum=b;
		 TopicClass=c;
		 TopicNum=d;
		 ActionTemplateNum=e;
		 AnimalTemplateNum=f;
		 FineryTemplateNum=g;
		 DailyThingsTemplateNum=h;
		 FoodTemplateNum=i;
		 PlaceTemplateNum=j;
		 SeasonTemplateNum=k;
		 VehicleTemplateNum=l;
		 WeatherTemplateNum=m;
		 EntertainmentTemplateNum=n;
		 CharacterTemplateNum=p;
		 MoodTemplateNum=p;
		 TimeTemplateNum=q;
		 FlowerGrassTemplateNum=r;		
		
	}
	public void Filesample(){
			
	   
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; 
		try 
		{
	    String str = "";
		fis = new FileInputStream("d:\\1.txt");
		isr = new InputStreamReader(fis);
	    br = new BufferedReader(isr); 	
	    while ((str = br.readLine()) != null) 
	     {
	    	String[] temp = str.split(",");
	 		try 
	 		{ 
	 		
	 	   	 maFileClass=Integer.parseInt(temp[0]);
			 maFileNum=Integer.parseInt(temp[1]);
			 TopicClass=Integer.parseInt(temp[2]);
			 TopicNum=Integer.parseInt(temp[3]);
			 ActionTemplateNum=Integer.parseInt(temp[4]);
			 AnimalTemplateNum=Integer.parseInt(temp[5]);
			 FineryTemplateNum=Integer.parseInt(temp[6]);;
			 DailyThingsTemplateNum=Integer.parseInt(temp[7]);
			 FoodTemplateNum=Integer.parseInt(temp[8]);
			 PlaceTemplateNum=Integer.parseInt(temp[9]);
			 SeasonTemplateNum=Integer.parseInt(temp[10]);
			 VehicleTemplateNum=Integer.parseInt(temp[11]);
			 WeatherTemplateNum=Integer.parseInt(temp[12]);
			 EntertainmentTemplateNum=Integer.parseInt(temp[13]);
			 CharacterTemplateNum=Integer.parseInt(temp[14]);
			 MoodTemplateNum=Integer.parseInt(temp[15]);
			 TimeTemplateNum=Integer.parseInt(temp[16]);
			 FlowerGrassTemplateNum=Integer.parseInt(temp[17]);		
	 		} 
		    	 		
	 		catch (NumberFormatException e) {e.printStackTrace();}
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
				     br.close();
				     isr.close();
				     fis.close();
				     }
			   catch (IOException e) 
			   {
				    e.printStackTrace();
				}
			   }
		}
	public int similarindex(sample a,sample b){
		
		int i;
		i=20*(a.maFileClass-b.maFileClass)*(a.maFileClass-b.maFileClass)+			
		  15*(a.TopicClass-b.TopicClass)*(a.TopicClass-b.TopicClass)+
			5*(a.maFileNum-b.maFileNum)*(a.maFileNum-b.maFileNum)+
				3*(a.TopicNum-b.TopicNum)*(a.TopicNum-b.TopicNum)+
				14*(a.ActionTemplateNum-b.ActionTemplateNum)*(a.ActionTemplateNum-b.ActionTemplateNum)+
				6*(a.AnimalTemplateNum-b.AnimalTemplateNum)*(a.AnimalTemplateNum-b.AnimalTemplateNum)+
				5*(a.FineryTemplateNum-b.FineryTemplateNum)*(a.FineryTemplateNum-b.FineryTemplateNum)+
				4*(a.DailyThingsTemplateNum-b.DailyThingsTemplateNum)*(a.DailyThingsTemplateNum-b.DailyThingsTemplateNum)+
				3*(a.FoodTemplateNum-b.FoodTemplateNum)*(a.FoodTemplateNum-b.FoodTemplateNum)+
				2*(a.PlaceTemplateNum-b.PlaceTemplateNum)*(a.PlaceTemplateNum-b.PlaceTemplateNum)+
				2*(a.SeasonTemplateNum-b.SeasonTemplateNum)*(a.SeasonTemplateNum-b.SeasonTemplateNum)+
				2*(a.VehicleTemplateNum-b.VehicleTemplateNum)*(a.VehicleTemplateNum-b.VehicleTemplateNum)+
				2*(a.WeatherTemplateNum-b.WeatherTemplateNum)*(a.WeatherTemplateNum-b.WeatherTemplateNum)+
				2*(a.EntertainmentTemplateNum-b.EntertainmentTemplateNum)*(a.EntertainmentTemplateNum-b.EntertainmentTemplateNum)+
				(a.CharacterTemplateNum-b.CharacterTemplateNum)*(a.CharacterTemplateNum-b.CharacterTemplateNum)+
				(a.MoodTemplateNum-b.MoodTemplateNum)*(a.MoodTemplateNum-b.MoodTemplateNum)+
				(a.TimeTemplateNum-b.TimeTemplateNum)*(a.TimeTemplateNum-b.TimeTemplateNum)+
				(a.FlowerGrassTemplateNum-b.FlowerGrassTemplateNum)*(a.FlowerGrassTemplateNum-b.FlowerGrassTemplateNum);								
			return i;									
	}
	public int minsimilarindex(sample s[],sample exs,int t){
		
		int i;
		int a[]=new int[t];
		
		for(i=0;i<t;i++){
			
			a[i]=exs.similarindex(s[i], exs);
			
			
		}	
		int min=a[0];
		int m=0;
		for(i=0;i<t;i++)
		{
		if(min>a[i]) {min=a[i];	m=i;}		
		}
		return m;
	}
    public void Displaysample(){	
    	System.out.println("["+
      		   	maFileClass+","+
    			 maFileNum+","+
    			 TopicClass+","+
    			 TopicNum+","+
    			 ActionTemplateNum+","+
    			 AnimalTemplateNum+","+
    			 FineryTemplateNum+","+
    			 DailyThingsTemplateNum+","+
    			 FoodTemplateNum+","+
    			 PlaceTemplateNum+","+
    			 SeasonTemplateNum+","+
    			 VehicleTemplateNum+","+
    			 WeatherTemplateNum+","+
    			 EntertainmentTemplateNum+","+
    			 CharacterTemplateNum+","+
    			 MoodTemplateNum+","+
    			 TimeTemplateNum+","+
    			 FlowerGrassTemplateNum+"]");
    		  }
	int Getfinleaf(int flistnum,Linklist newlist[])
	{
		int fin,i;
		int finleaf=0;
		sample standardsam=new sample();
		standardsam.Filesample();
		if(flistnum==1)
	     {	
	      sample sam=new sample();	
	      newlist[0].TurnLinklistTOsample(sam);
	       sam.Displaysample();
	       finleaf=newlist[0].Getleaf();
	      System.out.println(finleaf);
	      }
	
	
	if(flistnum>1)
	{
	sample sam[]=new sample[flistnum];
	for(i=0;i<flistnum;i++)
	{			
		sam[i]=new sample();
		newlist[i].TurnLinklistTOsample(sam[i]);
		sam[i].Displaysample();
	}		
	int simi[]=new int[flistnum];	
	fin=standardsam.minsimilarindex(sam,standardsam,flistnum);
	finleaf=newlist[fin].Getleaf();
	System.out.println("最终的返回等级是："+finleaf);	
	}
	
	return finleaf;
  }
}
public class Linklist {
	public linknode first;
	public linknode last;
	
	
	public Linklist(){
		first=new linknode();
		last=new linknode();
	}
	
	
	public void TurnLinklistTOsample(sample sam)
	{		
		
		linknode p=new linknode();
		
		if(first.a!=-1)
		{
			p=first;
			while(p.b!=-1)
			{
				switch(p.a){				
				case 500:
					sam.maFileClass=p.b;
					break;
				case 501:
					sam.maFileNum=p.b;
					break;
				case 502:
					sam.TopicClass=p.b;
					break;
                 case 503:
                	 sam.TopicNum=p.b;
                		break;
                case 504:
                		sam.ActionTemplateNum=p.b;
                		break;
                case 505:
                		sam.AnimalTemplateNum=p.b;
                		break;
                case 506:
                		sam.FineryTemplateNum=p.b;
                		break;
                case 507:
                		sam.DailyThingsTemplateNum=p.b;
                		break;
                case 508:
                		sam.FoodTemplateNum=p.b;
                		break;
                case 509:
                		sam.PlaceTemplateNum=p.b;
                		break;
                case 510:
                		sam.SeasonTemplateNum=p.b;
                		break;
                case 511:
                		sam.VehicleTemplateNum=p.b;
                		break;
                case 512:
                		sam.WeatherTemplateNum=p.b;
                		break;
                case 513:
                		sam.EntertainmentTemplateNum=p.b;
                		break;
                case 514:
                		sam.CharacterTemplateNum=p.b;
                		break;
                case 515:
                		sam.MoodTemplateNum=p.b;
                		break;
                case 516:
                		sam.TimeTemplateNum=p.b;
                		break;
                case 517:
                		sam.FlowerGrassTemplateNum=p.b;
                		break;
                	}	
				p=p.next;	
			}		
		}
			
		}
		
		
		
	
	public void Insert(int m,int n){
		
		if(first.a==-1) {first.a=m;first.b=n;first.next=last;}
		else
		{
		linknode p=new linknode();
		last.a=m;
		last.b=n;		
		last.next=p;
		last=p;
		}
		
	}
	public void AddLinklist(Linklist one,Linklist two ){
		linknode p=new linknode();	
		
		if(one.first.a==-1&&two.first.a==-1)  System.out.println("两条链都为空，没有链接的必要性");
		else if(one.first.a==-1)
		{
			p=two.first;		
			while(p.next!=null)
			{
			this.Insert(p.a,p.b);
			p=p.next;
			}
			
		}
		else if(two.first.a==-1)
		{
			p=one.first;
			while(p.next!=null)
			{
			this.Insert(p.a,p.b);
			p=p.next;
			}
			
		}
		else 
			{
			p=one.first;
			while(p.next!=null)
			{
			this.Insert(p.a,p.b);
			p=p.next;
			}
						
			p=two.first;
			while(p.next!=null)
			{
			this.Insert(p.a,p.b);
			p=p.next;
			}
			
		
			}
		
	}
	
	public void Display(){
		if(first.a==-1)  System.out.println("此链为空");
		else{
		linknode p=first;
		while(p.next!=null)
		{
		System.out.println( p.a+","+p.b);
		p=p.next;
		}
		}
	}
	
	public int Getleaf(){
		int leaf=0;
		if(first.a==-1)  System.out.println("此链为空");
		else
		{
		 linknode p=first;
		 while(p.next!=null)
		 {
		  if(p.b==10000) leaf=p.a;
		  p=p.next;
		 }
		}
		return leaf;
	}
	
	
	public int GetIDCutvalue(int b,int c,Connection con)throws SQLException{
		
		
		Statement stmt=null;
		stmt=con.createStatement();
		ResultSet rs;
		String sql;
		int i;
		int t=0;
		/*sql="select * from tree where 属性="+b+" and 取值="+c;
		rs = stmt.executeQuery(sql);
		rs.next();
		t=rs.getInt(6);*/
		
		for(i=1;i<25;i++){
					
			sql="select * from tree where 属性="+b+" and 取值="+(c+i);
			rs = stmt.executeQuery(sql);
			if(rs.next()) 
				{
				t=rs.getInt(6);
				break;
				}
		}
		
		if(t!=0) return t;   
		else return 10000;  //如果是本身的话，不设置限制
		
		
	}
	public int min(int idnum[])
	{
		
		int i=0,min;
		min=idnum[0];
		while(idnum[i]!=0){
			
			if(min>idnum[i])  min=idnum[i];
			i++; 
			
		}
		return min;
		
	}
	public static int max(int leaf[],int len)
	{
		
		int i=0,max,flag=0;
		max=leaf[0];
		for(i=0;i<len;i++){
			
			if(max<leaf[i]) { max=leaf[i];flag=i;}
			
			
		}
		return flag;
		
	}
	public void Creatlistlink(int b,int c,int t,Connection con,Linklist list[],int idnum[])throws SQLException
	{    
	   
		int i=0;
		int IDcut=0;
		int[] m = new int[10];
		int[] n = new int[10];
		Statement stmt=null;
		stmt=con.createStatement();
		ResultSet rs;
		idnum[t]=GetIDCutvalue(b,c,con);	
		IDcut=min(idnum);
		String sql="select * from tree where 属性="+b+" and 取值="+c+" and ID<="+IDcut;	
		rs = stmt.executeQuery(sql);
		
		list[t].Insert(b, c);
		
		while(rs.next())
		{
			if(rs.getInt(4)!=10000)
			{
			
			Linklist newlist=new Linklist();
			newlist.Insert(rs.getInt(3), rs.getInt(4));
			m[i]=rs.getInt(3);
	        n[i]=rs.getInt(4); 
	        list[t+1+i].AddLinklist(list[t],newlist);	               
	        list[t].Creatlistlink(m[i],n[i],t+1+i,con,list,idnum);	        
	        
			}
			else
			{   
				list[t].Insert(rs.getInt(3), rs.getInt(4));
				list[t].last.a=10000;
				break;
				
			}
			i++;
						
			
		}
		
	}
	
	public Linklist[] Modifylistlink(Linklist list[],int t){
		
		int i,m=0;
		int truenewlist=0;
		Linklist newlink[]=new Linklist[t];
		for(i=0;i<t;i++)
		{
						
			newlink[i]=new Linklist();
		}
		
		for(i=0;i<t;i++)
		{
			if(list[i].last.a==10000)
			{
				newlink[m]=list[i];
				m++;
				truenewlist++;
			}
		}
		
		linknode p=new linknode();
		linknode q=new linknode();
		for(i=0;i<truenewlist;i++)
		{
			if(newlink[i].first.a!=-1)
			{
			p=newlink[i].first;
			q=newlink[i].first.next;
			while(q.a!=10000)
			 {
				if(p.a==q.a&&p.b==q.b)
				{
				p.next=q.next;
				p=q.next;
				q=q.next.next;
				}
				else
				{
					p=q;
					q=q.next;
				}
				
			}
			
		  }
		}
		
		return newlink;		
	}
	public static int GetmaFileClass(int ma[]){
    		
		    
		    int i=0;
			FileInputStream fis = null;
			InputStreamReader isr = null;
			BufferedReader br = null; 
			try 
			{
		    String str = "";
			fis = new FileInputStream("d:\\1.txt");
			isr = new InputStreamReader(fis);
		    br = new BufferedReader(isr); 	
		    while ((str = br.readLine()) != null) 
		     {
		    	String[] temp = str.split(",");
		 		try 
		 		{ 	 		
		 	   	ma[i]=Integer.parseInt(temp[0]);
		 		} 
		 		catch (NumberFormatException e) {e.printStackTrace();}
		 	    i++;
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
					     br.close();
					     isr.close();
					     fis.close();
					     }
				   catch (IOException e) 
				   {
					    e.printStackTrace();
					}
				   }
		return i;
	}
	
	public static double[] GetfinmaFileRcommend(int leaf[],int len){
		
		  double leafrate[]=new double[len];
		  int sum=0;    
	      for(int i=0;i<len;i++){sum=sum+leaf[i]; }
	      for(int i=0;i<len;i++){leafrate[i]=(double)leaf[i]/sum;}
	      return leafrate;
	
}
	public  double[]  Linklistmain(double leafrate[]) throws SQLException {
		
		//public static void main (String srg[])	throws SQLException {
				
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
		
		int c[]=new int[30];//获取候选场景的场景类别
		int leaf[]=new int[30];
		int m,len;
		
		len=GetmaFileClass(c);//有几个候选场景
		
		for(m=0;m<len;m++)
		{
		int MAX=50;
		int idnum[]=new int[MAX];
		for(int i=0;i<MAX;i++)
		{
						
			idnum[i]=0;
		}
		Linklist list[]=new Linklist[MAX];//原始的得到的链
		for(int i=0;i<MAX;i++)
		{
						
			list[i]=new Linklist();
		}
		Linklist g=new Linklist();
		g.Creatlistlink(500,c[m],0,con,list,idnum);	
		int truelist=0;
		int i;
		for(i=0;i<MAX;i++)
		{
			if(list[i].first.a!=-1)
			{
		      truelist++;
		     }
		}
		
		Linklist newlist[]=new Linklist[truelist];//把list处理后得到的链
		for( i=0;i<truelist;i++)
		{
						
			newlist[i]=new Linklist();
		}
		
		newlist=g.Modifylistlink(list, truelist);
		
		int flistnum=0;
		for(i=0;i<truelist;i++)
		{
			if(newlist[i].first.a!=-1)
		    {
		    System.out.println(i+"条");
		    newlist[i].Display();
		    flistnum++;
		    }
		}
		
	    sample s=new sample();
		leaf[m]=s.Getfinleaf(flistnum,newlist);
		}
		
		
		int maxleaf=max(leaf,len);
		
	
		
		leafrate=GetfinmaFileRcommend(leaf,len);
		
		for(int i=0;i<len;i++){
		System.out.print(leafrate[i]+",");}
		return  leafrate;
 }
}
