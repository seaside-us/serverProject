package plot;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//import java.util.logging.Logger;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLFactoryException;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;

public class ProgramEntrance {
	
	//保存IE输出内容用，getXMLDate(String IE_out.xml)中填充值
	static ArrayList<String> topicName = new ArrayList<String>();// 用来保存Topic的名字
	static ArrayList<String> topicFromQiu = new ArrayList<String>();// 用来保存Topic的名字
	static ArrayList<String> topicFromMG = new ArrayList<String>();// 用来保存马更信息抽取的主题及其概率值
	static ArrayList<String> templateWithColor = new ArrayList<String>();
	static ArrayList<String> colorMark = new ArrayList<String>();
	static ArrayList<String> TopicAndTemplate = new ArrayList<String>();
	static String topicProbability = "";// 用来保存Topic的Key值
	public static String messageValue = "";// 保存短信内容
	public static boolean isMiddleMessage = false;// 判断
	static String strNegType = "";// 保存否定
	static int count[] = new int[2];
	static boolean isDouble = false;
	static Logger logger = Logger.getLogger(ProgramEntrance.class.getName());
	static ArrayList<String> templateAttr = new ArrayList<String>();// 用来保存模板及模板值
	private static Object strTopic;

	/*
	 * 程序入口，定性主程序调用
	 */
	public static void Entrance()
			throws OntologyLoadException, SWRLFactoryException, SWRLRuleEngineException, SecurityException, IOException,
			ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {

		logger.info("程序开始***************************");

		try {
			// 读取IE部分输出文件
			getXMLData("OntologyInPutFile\\result.xml");
		} catch (Exception e) {
			isMiddleMessage = true;
		}
		JenaMethod.processMaFile(topicName, templateAttr, templateWithColor, colorMark, topicFromMG, // 带概率值
				topicFromQiu, strNegType, count, TopicAndTemplate);
		topicName.clear();// 用来保存Topic的名字
		topicFromMG.clear();// 用来保存马更信息抽取的主题及其概率值
		topicProbability = "";// 用来保存Topic的Key值
		messageValue = "";// 保存短信内容
		isMiddleMessage = false;// 判断
		strNegType = "";// 保存否定
		templateAttr.clear();// 用来保存模板及模板值
		topicFromQiu.clear();
		TopicAndTemplate.clear();
		logger.info("程序结束***************************");
	}

	/**
	 * 读取IE部分输出文件
	 * @param location IE输出文件路径
	 * @throws SecurityException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private static void getXMLData(String location) throws SecurityException, IOException
	{
		Document doc = XMLInfoFromIEDom4j.readXMLFile(location);
		Element element = doc.getRootElement();
		Attribute attrNeg = element.attribute("negType");
		//判断是否有否定词
		if (attrNeg != null)
			strNegType = attrNeg.getText();//获取否定词
		Element ele = element.element("message");//获取节点名为“message”的对象
		Attribute attr = ele.attribute("value");//获取对象‘value’元素值
		messageValue = attr.getText(); //获取短信内容
		List<?> list = doc.selectNodes("//result/topic");
		
		
		// 对邱雄的程序输出的结果解析，如果是“主观的”情感，则判断是“喜怒哀惧”四中里边的哪一种
		Element textT = element.element("textType");
		if (textT.attribute("attribute").getText().equals("subjective")) {//是主观情感
			double prob = 0.0;
			if (textT.element("happy") != null) {//喜
				Element happy = textT.element("happy");
				prob = Double.parseDouble(happy.attribute("attribute").getText()); //感情概率值
				if (prob > 0.3) {//概率超过临界点，则加入主题
					topicFromQiu.add("喜悦");
					topicFromQiu.add(Double.toString(prob));
				}

			} else {
				if (textT.element("angry") != null) {
					Element angry = textT.element("angry");
					prob = Double.parseDouble(angry.attribute("attribute").getText());
					System.out.println("angry prab:" + prob);
					if (prob > 0.3) {
						topicFromQiu.add("生气");
						topicFromQiu.add(Double.toString(prob));
					}
				} else {
					if (textT.element("sad") != null) {
						Element sad = textT.element("sad");
						prob = Double.parseDouble(sad.attribute("attribute").getText());
						System.out.println("sad prob:" + prob);
						if (prob > 0.3) {
							topicFromQiu.add("悲");
							topicFromQiu.add(Double.toString(prob));
						}
					} else {
						if (textT.element("fear") != null) {
							Element fear = textT.element("fear");
							prob = Double.parseDouble(fear.attribute("attribute").getText());
							System.out.println("fear prab:" + prob);
							if (prob > 0.3) {
								topicFromQiu.add("恐惧");
								topicFromQiu.add(Double.toString(prob));
							}
						}
					} // fear else end
				} // sad else end
			} // angry else end
		}

		if (list.isEmpty()) {//根据推理没有获取主题：result\\下的topic节点放进List
			isMiddleMessage = true;	//没有主题
		} 
		else {//如果有推理的主题
			
			Iterator<?> its1 = list.iterator();//将List信息放到Iterator<E>中
			while (its1.hasNext()) {//hasNext()+next();
				String str = "";
				String strTopic = "";
				Element topic = (Element) its1.next();//next()获取topic对象
				
				// 获得Topic节点的相应属性
				for (Iterator<Attribute> topicAttribute = topic.attributeIterator(); topicAttribute.hasNext();) {
					Attribute topicName0 = (Attribute) topicAttribute.next();
					if (topicName0.getName().equals("name")) {
						String aa = topicName0.getText().toString();
						// 获得topic的名字
						if (topicName0.getText().toString() != "") {
							strTopic = topicName0.getText().toString();
						}

					} else if (topicName0.getName().equals("probability")) { // 获得Topic的key值
						topicProbability = topicName0.getText().toString();
						System.out.println("TopicKey:" + topicName0.getText());
					}
				}
				if (strTopic != "") {
					if (topicProbability != "") {
						topicFromMG.add(strTopic);
						topicFromMG.add(topicProbability);
					} else
						topicName.add(strTopic);
				}
				List<Element> root = topic.elements();
				System.out.println("element节点名字：" + root.size());
				logger.info("此主题拥有的模板节点个数" + root.size());
				String d = "";
				for (Iterator<Element> its = root.iterator(); its.hasNext();) {
					Element template = (Element) its.next();// 取每个template的属性值

					String templateName1 = "";
					String templatevalue = "";
					String templatename = "";
					for (Iterator<Attribute> templateAttribute = template.attributeIterator(); templateAttribute
							.hasNext();) {

						Attribute templateName0 = (Attribute) templateAttribute.next();
						if (templateName0.getName().equals("name")) {
							// 获得template的名字
							templateName1 = templateName0.getText();
							templateAttr.add(templateName0.getText());
							templatename = templateName0.getText();
							logger.info("模板名字:" + templateName0.getText());
							if (templateName0.getText().equals("人物") || templateName0.getText().equals("经典人物角色")) {
								count[0]++;
							} else
								count[1]++;
						}

						else if (templateName0.getName().equals("value")) {
							// 获得template的value值
							templatevalue = templateName0.getText();
							logger.info("模板值:" + templatevalue);
							///////////////////////////////////////// 将模板值传递给模板处理函数，进入处理流程/////////////////////////////////////////////
							ArrayList<String> templateV = processTemplateValue(templatevalue);// 将模板值传递给模板处理函数，进入处理流程///
							for (Iterator<String> its5 = templateV.iterator(); its5.hasNext();) {
								String templateVV = its5.next();
								templateAttr.add(templateVV);
								templatevalue = templateVV;
								templatename = templatename + ":" + templatevalue;
								if (its5.hasNext()) {
									templateAttr.add(templateName1);
									templatename = templatename + "-" + templateName1;
								}
							}
							logger.info("模板值经过处理后的值(有些模板值有中间信息)：" + templateV);
						} else if (templateName0.getName().equals("color") && (!templateName0.getText().isEmpty())) {
							templateWithColor.add(templateName1);
							templateWithColor.add(templatevalue);
							colorMark.add(templateName0.getText());
						} else if (templateName0.getName().equals("negFlag")) {
							System.out.println(templatename);
							if (isDouble) {
								int position = templatename.indexOf("-");
								String[] split = templatename.split("-");
								StringBuilder sb = new StringBuilder();
								for (int j = 0; j < split.length; j++) {
									sb.append(split[j]);
									if (j == split.length - 1)
										sb.append(":" + templateName0.getText());
									else
										sb.append(":" + templateName0.getText() + "-");
								}
								templatename = sb.toString();

							} else
								templatename = templatename + ":" + templateName0.getText();
						}
					}
					d = d + "-" + templatename;
				}
				str = strTopic + d;
				if (root.size() != 0)
					TopicAndTemplate.add(str);
			}
			templateAttr = deleteRepeatemplateValue(templateAttr);

		}
		for (Iterator<String> its5 = templateAttr.iterator(); its5.hasNext();) {
			System.out.println("templateAttr:" + its5.next());
		}
	}

	/*
	 * 被getXMLData使用
	 */
	private static ArrayList<String> deleteRepeatemplateValue(ArrayList<String> aa) {
		logger.info("开始删除重复的原子信息");
		ArrayList<String> template = new ArrayList<String>();
		Iterator<String> its = aa.iterator();
		while (its.hasNext()) {
			boolean isRepeat = false;
			String templateName = (String) its.next();// 获得模板名
			String templateAutoName = (String) its.next();// 获得原子信息
			if (template.size() == 0) {
				template.add(templateName);
				template.add(templateAutoName);
			} else {
				for (Iterator<String> its1 = template.iterator(); its1.hasNext();) {
					String temp = (String) its1.next();
					if (templateAutoName.equals(temp)) {
						isRepeat = true;
						break;
					}
				}
				if (!isRepeat) {
					template.add(templateName);
					template.add(templateAutoName);
				}
			}
		}
		return template;
	}

	/*
	 * 被getXMLData使用
	 * 用来处理当一个模板下有多个原子信息时
	 */
	private static ArrayList<String> processTemplateValue(String templateValue) {
		ArrayList<String> tempAtom = new ArrayList<String>();
		ArrayList<String> tempAtom2 = new ArrayList<String>();
		ArrayList<ArrayList<String>> tempAtom3 = new ArrayList<ArrayList<String>>();
		/////////////////////// 以“|”为界限，把模板值分解出来，一一用函数processSameTemplateMuchValue进行处理////////////////////
		if (templateValue.contains("|"))// 当一个模板中的原子信息存在多个，进行随机处理
		{
			isDouble = true;
			while (templateValue.contains("|")) {
				int iPostion = templateValue.indexOf("|");
				String subAtom = templateValue.substring(0, iPostion);
				tempAtom = processSameTemplateMuchValue(subAtom);
				tempAtom3.add(tempAtom);
				templateValue = templateValue.substring(iPostion + 1, templateValue.length());

			}
			tempAtom = processSameTemplateMuchValue(templateValue);
			tempAtom3.add(tempAtom);
		} else {
			tempAtom = processSameTemplateMuchValue(templateValue);
			tempAtom3.add(tempAtom);
		}
		// END以“|”为界限，把模板值分解出来，一一用函数processSameTemplateMuchValue进行处理////////////////////
		ArrayList<String> temp4 = new ArrayList<String>();
		// 用来取得中间模板的情况
		for (Iterator<ArrayList<String>> its2 = tempAtom3.iterator(); its2.hasNext();) {
			ArrayList<String> tempW = its2.next();
			for (Iterator<String> its3 = tempW.iterator(); its3.hasNext();) {
				temp4.add(its3.next());
			}
		}
		System.out.println("temp4=" + temp4);
		// 用来处理模板值中有中间模板的情况
		for (Iterator<String> its = temp4.iterator(); its.hasNext();) {
			templateValue = its.next();
			int postion = templateValue.indexOf(":");
			String value1 = templateValue.substring(0, postion);// 取第一个
			String value2 = "";
			String temp = templateValue.substring(postion + 1);
			if (temp.contains(":")) {
				int iipostion = 0;
				iipostion = temp.indexOf(":");
				value2 = temp.substring(0, iipostion);
				templateValue = value1 + ":" + value2;
			}
			tempAtom2.add(templateValue);
			logger.info("此模板下的模板原子有：" + templateValue);
		}
		return tempAtom2;
	}

	/**
	 * 被getXMLData使用
	 * 处理一个模板下一个节点下的多个原子信息
	 * @param value
	 * @return
	 */
	private static ArrayList<String> processSameTemplateMuchValue(String value) {
		ArrayList<String> tempAtom = new ArrayList<String>();
		if (value.contains(";")) {
			isDouble = true;
			int count = 0;
			String nodeName = "";
			while (value.contains(";")) {
				int iPostion = value.indexOf(";");
				String subAtom = value.substring(0, iPostion);
				count++;
				if (count == 1) {
					int iiPostion = subAtom.lastIndexOf(":");
					//////////////////////////// 找出第一个元素的最终节点名///////////////////////////////////////////////////////////
					nodeName = subAtom.substring(0, iiPostion);
				} else {
					subAtom = nodeName + ":" + subAtom;
				}
				tempAtom.add(subAtom);

				value = value.substring(iPostion + 1, value.length());
			}
			value = nodeName + ":" + value;
			tempAtom.add(value);
		} else
			tempAtom.add(value);
		return tempAtom;
	}

}
