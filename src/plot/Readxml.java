package plot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;



public class Readxml {
	public static Document read(String filePath) throws MalformedURLException {
		Document doc = null;

		try {
			File file = new File(filePath);
			SAXReader reader = new SAXReader();
			doc = reader.read(file);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * 读取xml文件和属性 将一条add规则的信息（模型名字，模型数量，模型放在的空间位置。存入类数组中）
	 * 
	 */

	public static ArrayList<SceneSpace> getAddinfo(Element name) {
		ArrayList<SceneSpace> al = new ArrayList<SceneSpace>();
		SceneSpace sp = null;
		// 遍历得到所有规则的所有属性
		for (Iterator<Element> it = name.elementIterator(); it.hasNext();) {
			Element element = (Element) it.next();
			sp = new SceneSpace();
			// 得到该元素的所有子元素
			// p(element.elements());
			// 循环获取子节点属性
			for (Iterator<Attribute> j = element.attributeIterator(); j.hasNext();) 
			{
				
				Attribute attribute = (Attribute) j.next();
				if("ruleType".equals(attribute.getName())&&!"addToMa".equals(attribute.getText()))
					{//如果ruleType不是addToMa，直接跳过
					break;
					}
				// 存放attribute的是map，getName获取属性名称，getValue获取属性值。
				// System.out.println("获取的属性值是" + attribute.getName());
				
				if ("addModel".equals(attribute.getName())) {
					if(attribute.getText().equals("M_floor.ma")
							||"M_floor.ma"==attribute.getText().toString()
							||"M_NoModel.ma"==attribute.getText().toString()
							||attribute.getText().equals("M_NoModel.ma"))
						continue;
					sp.setModelname(attribute.getText());
				} else if ("spaceName".equals(attribute.getName())) {
					sp.setSpname(attribute.getText());
				} else if ("isTarget".equals(attribute.getName())) {
					sp.setTarget(Integer.parseInt(attribute.getText()));
					sp.setIndex(sp.getIndex()+Integer.parseInt(attribute.getText()));
				} else if ("number".equals(attribute.getName())) {
					sp.setNumber(Integer.parseInt(attribute.getText()));
				}else if ("addModelID".equals(attribute.getName())) {
					sp.setModelid(attribute.getText());
				}else if ("type".equals(attribute.getName())) {
					sp.setType(attribute.getText());
					if("people".equals(attribute.getText()))
						sp.setIndex(sp.getIndex()+1);
				}
				
			}
			if (sp.getModelname() != null)
				al.add(sp);

		}
		//遍历所有的对模型的操作规则，进行模型的属性加分
		boolean isActDeform=false;
		for (Iterator<Element> it = name.elementIterator(); it.hasNext();) {
			Element element = (Element) it.next();
			// 得到该元素的所有子元素
			// p(element.elements());
			// 循环获取子节点属性
			isActDeform=false;
			for (Iterator<Attribute> j = element.attributeIterator(); j.hasNext();) 
			{
				
				Attribute attribute = (Attribute) j.next();
				if(!attribute.getName().equals("ruleType")){
					break;
				}
				// 存放attribute的是map，getName获取属性名称，getValue获取属性值。
				// System.out.println("获取的属性值是" + attribute.getName());
				if("ruleType".equals(attribute.getName())&&
						("addActionToMa".equals(attribute.getText())
						||"Deform".equals(attribute.getText()))){
					isActDeform=true;
					continue;
				}
				else if("ruleType".equals(attribute.getName())){
					isActDeform=false;
					break;
				}
				if("usedModelID".equals(attribute.getName())){
					if(isActDeform)
					for(int i=0;i<al.size();i++){
						if(al.get(i).getModelid().equals(attribute.getText())||
								al.get(i).getModelid()==(attribute.getText()))
							al.get(i).setIndex(al.get(i).getIndex()+1);
					}	
				}
				else continue;
			}
			

		}
		// 测试显示存入的数据是否正确

		for (int j = 0; j < al.size(); j++) {
			System.out.println(al.size());
			SceneSpace s = (SceneSpace) al.get(j);
			System.out.println("modelname:" + s.getModelname());
			System.out.println("isTarget:" + s.getisTarget());
			System.out.println("加分:" + s.getIndex());
			//System.out.println("空间名字:" + s.getSpname()+"\n"+"type:"+s.getType());
		}

		return al;
	}
	/**
	   * 将信息写入xml文件
	   * @param name
	   */
	public static void writexml(Element name, ArrayList a, ArrayList layOut,ArrayList layShape) 
	{
		 Element ruleName = name.addElement("rule");
	   	  ruleName.addAttribute("ruleType","setLayout");// type="model"
	   	  int j=layOut.size()-1;
	   	  for(int i=0;i<a.size();i++)
	   	  {
	   		SceneSpace ss=(SceneSpace)a.get(i);
	   		ruleName.addAttribute(ss.getSpname(),layOut.get(j).toString().substring(3));
	        ruleName.addAttribute(layOut.get(j).toString().substring(3)+"_shape",layShape.get(j).toString());
	   		j--;
	   	  }
		// ruleName.addAttribute("SP_restaurantTable_C","Lo_IndoorGroundLayout2");
		// ruleName.addAttribute("SP_restaurantTable_C_shape","rectangular");
		// ruleName.addAttribute("SP_restaurantTable_B","Lo_IndoorGroundLayout1");
		// ruleName.addAttribute("SP_restaurantTable_B_shape","rectangular");
		// ruleName.addAttribute("SP_restaurantTable_A","Lo_IndoorGroundLayout3");
		// ruleName.addAttribute("SP_restaurantTable_A_shape","rectangular");

	}
	public static void writexml2(Element name, SceneSpace ss, HashMap<String,String> testmap,String layeffect) 
	{
		Element ruleName = name.addElement("rule");
	   	ruleName.addAttribute("ruleType","setLayout");// type="model"
	   	ruleName.addAttribute("spaceName",ss.getSpname());
	   	ruleName.addAttribute("layoutEffect",layeffect);
	   	Iterator iter = testmap.entrySet().iterator();
	   	int areaNum=1;
	   	while (iter.hasNext()) {
	   	//	HashMap.Entry entry = (HashMap.Entry) iter.next();
	   		Entry entry = (Entry) iter.next();
	   		ruleName.addAttribute("area"+areaNum+"_name",(String)entry.getKey());
	   		ruleName.addAttribute("area"+areaNum+"_shape",(String)entry.getValue());
	   		areaNum++;
	   	}
	}
	/**
	 * 保存xml文件
	 */
	public static void savexml(String filePath, Document doc) {
		XMLWriter writer;
		try {
			writer = new XMLWriter(new FileWriter(filePath));
			writer.write(doc);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 处理中间信息 数组sp存储去掉空间重复的结果
	 * 如：原来al存的是("M_car.ma",1,"sp_car_A")("M_apple.ma",2,"sp_car_A")
	 * 处理后的结果放入sp中为("M_car.ma M_apple.ma",3,"sp_car_A")
	 * 
	 */
	public static ArrayList getSpace(ArrayList al) {
		ArrayList sp = new ArrayList();
		// int i=0;
		SceneSpace s1 = new SceneSpace();
		SceneSpace s = new SceneSpace();
		for (int j = 0; j < al.size(); j++) {
			int flag = 0;
			s = (SceneSpace) al.get(j);
			if (!("no.ma".equals(s.getModelname()))) {

				if (j == 0) {
					s1 = s;
					sp.add(s1);
				} else {
					for (int l = 0; l < sp.size(); l++) {
						SceneSpace stemp = (SceneSpace) sp.get(l);
						if (s.getSpname().equals(stemp.getSpname())) {
							System.out.println(s.getSpname()
									+ "!!!!!!!!!!!!!!!!!!!!!!!!!");
							stemp.setModelname(s.getModelname() + " "
									+ stemp.getModelname());
							stemp.setNumber(stemp.getNumber() + s.getNumber());
							flag = 1;
							break;
						}
					}
					if (flag != 1) {
						s1 = s;
						sp.add(s1);
					}

				}

			}
		}
		// 测试显示输出结果

		for (int i = 0; i < sp.size(); i++) {
			System.out.println(sp.size() + "aaaaa");
			SceneSpace ss = (SceneSpace) sp.get(i);
			System.out.println(ss.getModelname() + "aaaaaaaaaaaaa");
			System.out.println(ss.getNumber() + "aaaaaaaaaaaa");
			System.out.println(ss.getSpname() + "aaaaaaaaaaaaaa");

		}

		return sp;
	}
	
	public static ArrayList getSpace2(ArrayList al) {
		ArrayList sp = new ArrayList();
		SceneSpace s = new SceneSpace();
		for (int j = 0; j < al.size(); j++) {
			boolean flag = false;
			s = (SceneSpace) al.get(j);
			if (!("no.ma".equals(s.getModelname()))) {
				for (int l = 0; l < sp.size(); l++) {
					SceneSpace stemp = (SceneSpace) sp.get(l);
					if (s.getSpname().equals(stemp.getSpname())) {
						flag = true;
						stemp.addModellist(s.getModelname());
						stemp.addModelidlist(s.getModelid());
						stemp.addNumberlist(s.getNumber());
					}
				}
				if (flag == false) {
					s.addModellist(s.getModelname());
					s.addModelidlist(s.getModelid());
					s.addNumberlist(s.getNumber());
					sp.add(s);
				}
			}
		}
		return sp;
	}
	
	/**
	 * 
	 * @param filepath
	 * @return
	 * @throws DocumentException
	 * @throws MalformedURLException
	 */
//	public static ArrayList<CamName> getCamera(String filepath) throws DocumentException, MalformedURLException{
//    	//String filepath="E:\\eclipse\\new_eclipse\\test.xml";
//    	File file = new File(filepath);
//		SAXReader saxReader = new SAXReader();
//		Document document = saxReader.read(file);
//		ArrayList<CamName> cam=new ArrayList();
//		
//		Element root=(Element) document.getRootElement();
//		System.out.println(root.getName());
//		Iterator<?> it=root.elementIterator();
//		int i=0;
//		while(it.hasNext()){
//			Element el=(Element) it.next();
//			System.out.println(el.getName());
//			Iterator<?> itt=el.elementIterator();
//			while(itt.hasNext()){
//				Element el1=(Element) itt.next();
//				//System.out.println(el1.getName());
//				//System.out.println(el1.attributeValue("ruleType"));
//				if(!el1.attribute(0).getName().equals("ruleType")){
//					continue;
//				}
//				if(el1.attributeValue("ruleType").equals("SetCamera"))
//				{
//					System.out.println(el1.attributeValue("CameraName")+"/"
//							+el1.attributeValue("startframe")+"/"
//							+el1.attributeValue("endframe")+"/"
//							+el1.attributeValue("startinall")+"/"
//							+el1.attributeValue("endinall"));
//					CamName camname=new CamName("",0,0,0,0);
//					camname.setCamName(el1.attributeValue("CameraName"));
//					camname.setStartFrame(StrToInt(el1.attributeValue("startframe")));
//					camname.setEndFrame(StrToInt(el1.attributeValue("endframe")));
//					camname.setStartinall(StrToInt(el1.attributeValue("startinall")));
//					camname.setEndinall(StrToInt(el1.attributeValue("endinall")));
//					cam.add(i, camname);//将指定的元素camname插入到指定的位置i
//					i++;
//					System.out.println(i);
//				}
//				/*if(isHuman(el1.attributeValue("addModel"))){
//					System.out.println(el1.attributeValue("addModel"));
//				}*/
//			}
//			
//			//System.out.println(el.element("maName").attributeValue("rule"));
//		}
//    	return cam;
//    }
  public static int StrToInt(String frame){
		return Integer.parseInt(frame);
	   }
}
