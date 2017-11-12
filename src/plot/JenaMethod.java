package plot;

import java.awt.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.BitSet;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.FrameID;
//import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.OWLHasValue;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLDataProperty;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLFactoryException;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;

public class JenaMethod {
	/**
	 * 鍒涘缓涓�涓┖owl鏂囦欢
	 */
	static String maName = "";
	static OWLNamedClass englishTopicClass = null;
	static String englishTopicStr = "";
	static String englishTopicIE = "";
	static String englishTopicMG = "";
	static String englishTopicQiu = "";
	static ArrayList<String> englishTemplate = new ArrayList();
	static ArrayList<String> TemplateName = new ArrayList();
	static ArrayList<String> colorTemplate = new ArrayList();
	static ArrayList<String> modelWithColor = new ArrayList();
	static ArrayList<String> modelWithColors = new ArrayList();
	static Logger logger = Logger.getLogger(JenaMethod.class.getName());
	static ArrayList<SceneCase> sceneList = new ArrayList();
	public static ArrayList<SceneCase> sceneListTopic = new ArrayList();
	public static String maNameWord = "";
	static BitSet sCaseDataUsable = new BitSet(30);
	static ArrayList<String> actionTemplateAttr = new ArrayList();// 鐢ㄦ潵淇濆瓨鍔ㄤ綔妯℃澘鍙婂叾鍘熷瓙淇℃伅
	static ArrayList<String> usedModelAttr = new ArrayList();// 鐢ㄦ潵淇濆瓨鍦ㄦ坊鍔犲拰鏇存敼瑙勫垯涓敤鍒扮殑妯″瀷锛屼负鍒犻櫎瑙勫垯鎵�鐢�
	static ArrayList<String> colorChangeAttr = new ArrayList();// 淇濆瓨浼犻�掔粰鍒樼晠鐨勫彉鑹叉暟缁�
	static int colorModelNum = 0;// 淇濆瓨浼犻�掔粰鍒樼晠鐨勫彉鑹叉暟缁勬暟鐩�
	static ArrayList<String> timeweatherandfog = new ArrayList();// 淇濆瓨浼犻�掔粰鍒樼晠鐨勬椂闂村弬鏁�
	public static ArrayList<String> timeweatherandfog1 = new ArrayList();// 鐢ㄤ簬瀛楀箷鐨勮幏鍙�
	static ArrayList<String> moodTemplateAttr = new ArrayList();// 鐢ㄦ潵淇濆瓨鎯呯华妯℃澘鍙婂叾鍘熷瓙淇℃伅
	static ArrayList<String> weatherAndmoodAttr = new ArrayList();
	static ArrayList<ArrayList> windRainSnowNeedAttr = new ArrayList();// 鐢ㄦ潵淇濆瓨LHH闇�瑕佺殑妯℃澘淇℃伅
	static ArrayList<String> ExpressionList = new ArrayList();// 鐢ㄦ潵淇濆瓨璁稿悜杈夌殑妯℃澘淇℃伅
	static boolean bIsBackgroundScene = false;
	static boolean hasWeatherTeplate = false;
	static boolean hasTimeTemplate = false;
	static boolean people = false;
	static ArrayList<String> WindAttr = new ArrayList();
	static ArrayList<String> RainAttr = new ArrayList();
	static ArrayList<String> SnowAttr = new ArrayList();
	static ArrayList<String> ActionNeedPeople = new ArrayList();
	static ArrayList<String> LightList = new ArrayList();// 鐢ㄦ潵淇濆瓨HL闇�瑕佺殑妯℃澘淇℃伅
	static ArrayList<String> SeasonList = new ArrayList();
	static ArrayList<String> WeatherList = new ArrayList();
	static ArrayList<String> topiclist = new ArrayList();
	static ArrayList<String> topictemplate = new ArrayList();
	static int[] num = new int[2];
	static boolean ifActionOrExpression = false;

	public JenaMethod() {

	}

	/**
	 * 涓荤▼搴忥紝澶勭悊鍚勭瑙勫垯
	 * 
	 * @param topic锛欼E鎶藉彇鐨勪富棰�
	 * @param templateAttr:涓枃妯℃澘
	 * @throws OntologyLoadException
	 * @throws SWRLFactoryException
	 * @throws SWRLRuleEngineException
	 * @throws IOException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */

	@SuppressWarnings({ "deprecation", "unchecked" })
	public static void processMaFile(ArrayList<String> topic, ArrayList<String> templateAttr,
			ArrayList<String> templateWithColor, ArrayList<String> colorMark, ArrayList<String> topicFromMG,
			ArrayList<String> topicFromQiu, String strNegType, int count[], ArrayList<String> TopicAndTemplate)
			throws OntologyLoadException, SWRLFactoryException, SWRLRuleEngineException, SecurityException, IOException,
			ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {

		//鎵撳紑owl鏂囦欢,閫氳繃url鑾峰緱owl妯″瀷
		String url = "file:///C:/ontologyOWL/sumoOWL2/sumo_phone3.owl";
		OWLModel model = createOWLFile1(url);

		ArrayList<String> englishTemplateW = new ArrayList();
		ArrayList<String> englishTopicAndTemplate = new ArrayList();

		englishTemplateW = chineseTemplate3English(TopicAndTemplate, model);
		
		windRainSnowNeedAttr.add(0, WindAttr);
		windRainSnowNeedAttr.add(1, RainAttr);
		windRainSnowNeedAttr.add(2, SnowAttr);

		if (ProgramEntrance.isMiddleMessage) {
			//"鍙湁涓棿缁撴灉锛岀洿鎺ュ皢鐭俊淇℃伅浠ユ枃瀛楀舰寮忔墦鍏ョ┖鍦烘櫙涓�"
			maName = "empty.ma";
		}
		
		/**
		 * 鎴彇鍘焑nglishTemplate
		 */
		for (int i = 0; i < englishTemplateW.size(); i++) {// 鍘绘帀鏈�鍚庣殑鍒嗗��
			int iP = englishTemplateW.get(i).lastIndexOf(":");
			englishTemplate.add(englishTemplateW.get(i).substring(0, iP));
		}
		
		System.out.println("englishTemplate=" + englishTemplate);// 甯︽鐜囧�煎悗鎴彇鍗充笉甯︽鐜囧��
		// ***********************浠巔lotTemplate涓敱涓枃鍒拌嫳鏂�***************************8//
		ArrayList englishTemplatePlot = new ArrayList();
		englishTemplatePlot = chineseTemplate2EnglishFromPlot(templateAttr, model);
		System.out.println("englishTemplatePlot:" + englishTemplatePlot);
		// ***********************浠巔lotTemplate涓敱涓枃鍒拌嫳鏂�***************************8//
		num = count;
		SceneCollect(model, topic, topicFromMG, topicFromQiu, englishTemplateW, TemplateName, englishTemplatePlot, num);// 鑾峰彇鍏ㄩ儴鐨勫満鏅�,鍖呮嫭瀵归闆ㄩ洩鍔ㄤ綔鐨勪綔鐢�

		TemplateCollect(model, englishTemplate, templateWithColor, colorMark);// 鏀堕泦妯℃澘
		CountSceneCaseBitMap(topicFromMG, topicFromQiu, topic, templateWithColor);

		maName = SceneSelected();
		
		System.out.println("maName=" + maName);
		if (maName == null || maName.length() == 0) {
			ifActionOrExpression = true;
			System.out.println(num[0] + "\t" + num[1]);
			num[1] = 0;// num[1]瀛樻斁鐨勬槸鏄惁鍚湁闈炰汉鐗╂ā鏉匡紝
			if (num[0] != 0) {
				SceneCollect(model, topic, topicFromMG, topicFromQiu, englishTemplateW, TemplateName,
						englishTemplatePlot, num);// 鑾峰彇鍏ㄩ儴鐨勫満鏅�,鍖呮嫭瀵归闆ㄩ洩鍔ㄤ綔鐨勪綔鐢�
				TemplateCollect(model, englishTemplate, templateWithColor, colorMark);// 鏀堕泦妯℃澘
				CountSceneCaseBitMap(topicFromMG, topicFromQiu, topic, templateWithColor);
				maName = SceneSelected();
			} else {

				SceneCollect(model, topic, topicFromMG, topicFromQiu, englishTemplateW, TemplateName,
						englishTemplatePlot, num);// 鑾峰彇鍏ㄩ儴鐨勫満鏅�,鍖呮嫭瀵归闆ㄩ洩鍔ㄤ綔鐨勪綔鐢�
				TemplateCollect(model, englishTemplate, templateWithColor, colorMark);// 鏀堕泦妯℃澘
				CountSceneCaseBitMap(topicFromMG, topicFromQiu, topic, templateWithColor);
				maName = SceneSelected();
			}

		}
		
		PlotResGenerate(model, topic, templateAttr, templateWithColor, colorMark, topicFromMG, strNegType,
				englishTemplatePlot);
		
		int sceneCount = sceneList.size();
		
		for (int i = 0; i < sceneCount; i++) {
			SceneCase temp = sceneList.get(i);
//			System.out.println("SceneName:" + temp.sceneName);
//			System.out.println("MGProb:" + temp.MGProb);
//			System.out.println("IEProb:" + temp.IEProb);
//			System.out.println("ruleReason:" + temp.ruleReason);
//			System.out.println("TemplateRelated:" + temp.templateRelated);
//			System.out.println("placable/templateModelNum:" + temp.placableModelNum + "/" + temp.templateModelNum);
//			System.out.println("isWeatherable锛�" + temp.isWeatherable);
//			System.out.println("fullScore锛�" + temp.fullScore);
//			System.out.println("score锛�" + temp.score);
//			System.out.println("maName锛�" + maName);
		}
		PrintSceneCase();
		for (int j = 0; j < sceneCount; j++) {
			sceneListTopic.add(sceneList.get(j));
		}
		maNameWord = maName;

		InitAllStaticValue();
	}
	
	/**
	 * 鎵撳紑涓�涓猳wl鏂囦欢
	 * @param url锛歰wl鏂囦欢瀛樺湪鐨勮矾寰�
	 * @return OWL妯″瀷瀵硅薄
	 * @throws OntologyLoadException
	 */
	public static OWLModel createOWLFile1(String url) throws OntologyLoadException {
		try {
			OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI(url);
			return owlModel;
		} catch (Exception zz) {
			OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI(url);
			return owlModel;
		}
	}
	public static void InitAllStaticValue() {
		maName = "";
		englishTopicClass = null;
		topiclist.clear();
		topictemplate.clear();
		englishTopicStr = "";
		englishTopicIE = "";
		englishTopicMG = "";
		englishTopicQiu = "";
		englishTemplate.clear();
		TemplateName.clear();
		colorTemplate.clear();
		modelWithColor.clear();
		modelWithColors.clear();
		logger = Logger.getLogger(JenaMethod.class.getName());
		sceneList.clear();
		// sceneListTopic.clear();
		// maNameWord="";
		sCaseDataUsable.clear();
		actionTemplateAttr.clear();
		usedModelAttr.clear();
		colorChangeAttr.clear();
		colorModelNum = 0;// 淇濆瓨浼犻�掔粰鍒樼晠鐨勫彉鑹叉暟缁勬暟鐩�
		timeweatherandfog.clear();

		moodTemplateAttr.clear();
		weatherAndmoodAttr.clear();
		windRainSnowNeedAttr.clear();
		ExpressionList.clear();
		WindAttr.clear();
		RainAttr.clear();
		SnowAttr.clear();
		LightList.clear();
		ActionNeedPeople.clear();
		bIsBackgroundScene = false;
		hasWeatherTeplate = false;
		hasTimeTemplate = false;
		ifActionOrExpression = false;
		SeasonList.clear();
		people = false;

	}



	
	public static OntModel createOWLModelFile2(String url) {
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		model.read(url);
		return model;
	}

	/**
	 * 淇濆瓨owl鏂囦欢
	 */
	@SuppressWarnings("unchecked")
	public static void saveOWLFile(JenaOWLModel owlModel, String fileName) {
		Collection errors = new ArrayList();
		owlModel.save(new File(fileName).toURI(), FileUtils.langXMLAbbrev, errors);
		System.out.println("File saved with " + errors.size() + " errors.");
	}

	public static int isOldSceneCase(String sname) {
		if (sceneList.isEmpty())
			return -1;
		int sCount = sceneList.size();
		for (int i = 0; i < sCount; i++) {
			if (sceneList.get(i).sceneName.equals(sname))
				return i;
		}

		return -1;
	}

	public static String chineseTemplateEnglish(String chiname, String classname, OWLModel model) {
		OWLNamedClass temp = model.getOWLNamedClass(classname);
		OWLDatatypeProperty chinesename = model.getOWLDatatypeProperty("chineseName");
		Collection clo = temp.getSubclasses(true);
		String str = null;
		if (clo.size() != 0) {
			for (Iterator in = clo.iterator(); in.hasNext();) {
				OWLNamedClass ols = (OWLNamedClass) in.next();
				Object ob = ols.getHasValue(chinesename);
				if (ob != null && ob.toString().equals(chiname)) {
					str = ols.getBrowserText().toString();
					break;
				}
			}
		}
		return str;
	}

	
	private static ArrayList<String> chineseTemplate3English(ArrayList<String> topicAndTemplate, OWLModel model) {
		
		ArrayList<String> englishTemplate = new ArrayList();
		OWLDatatypeProperty chinesename = model.getOWLDatatypeProperty("chineseName");

		for (Iterator it = topicAndTemplate.iterator(); it.hasNext();) {
			String str = (String) it.next();
			String hasvalue[] = str.split("-");
			String stemplate = "";
			String strin = "";
			String strin1 = "";
			String strin2 = "";
			String temp = "";
			String temp2 = "";
			String template = "";
			
			for (int i = 0; i < hasvalue.length; i++) {
				temp = hasvalue[i];

				if (i == 0) {
					strin = chineseTemplateEnglish(temp, "Topic", model);// strin鏄富棰橈細鎻愰啋
					if (strin == null)
						strin = "";
				} else {
					temp2 = "";
					strin2 = "";
					template = "";
					String[] hasvla = temp.split(":");// hasvla=[澶╂皵, 闆�, 涓洩]
					String temp1 = hasvla[0];// temp1=澶╂皵

					strin1 = chineseTemplateEnglish(hasvla[0], "Template", model);// 鍖归厤鑻辨枃妯℃澘
					if (strin1 != null) {
						stemplate = strin1;// stemplate=澶╂皵

						if (strin1.equals("CharacterTemplate")) {
							people = true;
						}

						strin2 = chineseTemplateEnglish(hasvla[1], strin1, model);
						boolean flage_inver = false;
						if (strin2 != null) {
							OWLNamedClass temps = model.getOWLNamedClass(strin2);
							if (hasvla[hasvla.length - 1].equals("1")) {

								OWLObjectProperty mood_inver = model.getOWLObjectProperty("Mood_Inverse");

								RDFResource obj = temps.getSomeValuesFrom(mood_inver);

								System.out.println("鍙嶄箟锛堝惁瀹氾級=" + obj);
								if (obj != null) {
									flage_inver = true;
									strin2 = obj.getBrowserText();
									temps = model.getOWLNamedClass(strin2);
								}
							}
							strin1 = strin1 + ":" + strin2;
							template = strin2;

							if (temps.getInstanceCount() != 0) {
								Collection ind = temps.getInstances(true);

								boolean isget = false;
								if (flage_inver) {
									for (Iterator ina = ind.iterator(); ina.hasNext();) {
										OWLIndividual in = (OWLIndividual) ina.next();
										isget = true;
										strin2 = in.getBrowserText();
										temp2 = template + ":" + strin2;//
										template = template + ":" + strin2 + ":1.0";
										strin1 = strin1 + ":" + strin2;
										break;
									}
								} else {
									loop: for (Iterator ina = ind.iterator(); ina.hasNext();) {
										OWLIndividual in = (OWLIndividual) ina.next();
										if (in.getPropertyValueCount(chinesename) > 0) {
											Collection chineseValues = in.getPropertyValues(chinesename);
											for (Iterator its = chineseValues.iterator(); its.hasNext();)// 寰幆瀹炰緥鎵�瀵瑰簲鐨勫涓腑鏂囧悕绉�
											{
												String cValue = its.next().toString();
												if (cValue.trim().equals(hasvla[hasvla.length - 2].trim())) {
													isget = true;
													strin2 = in.getBrowserText();
													temp2 = template + ":" + strin2;//
													template = template + ":" + strin2 + ":1.0";
													strin1 = strin1 + ":" + strin2;
													break loop;
												}

											}
										}

									}
								}

								if (isget == false) {
									Collection instance = temps.getInstances(false);
									System.out.println(instance.size());
									if (instance.size() != 0) {
										Random r = new Random();
										int k = r.nextInt(instance.size());
										ArrayList<OWLIndividual> arrlist1 = new ArrayList<OWLIndividual>();
										for (Iterator insr = instance.iterator(); insr.hasNext();) {
											arrlist1.add((OWLIndividual) insr.next());
										}
										strin2 = arrlist1.get(k).getBrowserText();
										temp2 = template + ":" + strin2;//
										template = template + ":" + strin2 + ":0.5";
										strin1 = strin1 + ":" + strin2;
									} else {
										ArrayList<OWLIndividual> ils = new ArrayList<OWLIndividual>();
										for (Iterator ir = ind.iterator(); ir.hasNext();) {
											ils.add((OWLIndividual) ir.next());
										}
										Random ran = new Random();
										int k = ran.nextInt(ils.size());
										strin2 = ils.get(k).getBrowserText().toString();

										temp2 = template + ":" + strin2;

										template = template + ":" + strin2 + ":0.5";
										strin1 = strin1 + ":" + strin2;
									}

								}
							}

						}

						else if (!stemplate.equals("WeatherTemplate") && strin2 == null) {
							OWLNamedClass superclass1 = model.getOWLNamedClass(strin1);
							if (superclass1.getInstanceCount(false) > 0) {

								Collection instance = superclass1.getInstances(false);
								System.out.println("size=" + instance.size());
								Random r = new Random();
								int k = r.nextInt(instance.size());
								/*
								 * ArrayList<OWLNamedClass> arrlist1=new
								 * ArrayList<OWLNamedClass>(); for(Iterator
								 * insr=instance.iterator();insr.hasNext();){
								 * arrlist1.add((OWLNamedClass) insr.next()); }
								 */
								ArrayList<OWLIndividual> arrlist1 = new ArrayList<OWLIndividual>();
								for (Iterator insr = instance.iterator(); insr.hasNext();) {
									arrlist1.add((OWLIndividual) insr.next());
								}
								strin2 = arrlist1.get(k).getBrowserText();
								if (template == null || template.equals("")) {
									Collection Insuperclass = arrlist1.get(k).getDirectTypes();
									Random r1 = new Random();
									int m = r1.nextInt(Insuperclass.size());
									ArrayList<OWLNamedClass> arrlist2 = new ArrayList<OWLNamedClass>();
									for (Iterator insr1 = Insuperclass.iterator(); insr1.hasNext();) {
										arrlist2.add((OWLNamedClass) insr1.next());
									}
									template = arrlist2.get(m).getBrowserText().toString();

								}

								temp2 = template + ":" + strin2;//
								strin1 = strin1 + ":" + temp2;
								template = template + ":" + strin2 + ":0.5";

								System.out.println("strin1beizhu=" + strin1);
								System.out.println("template=" + temp2);
							} else {
								OWLNamedClass superclass = model.getOWLNamedClass(strin1);
								if (superclass.getSubclassCount() > 0) {

									Collection clo = superclass.getSubclasses(true);
									ArrayList<OWLNamedClass> arrlist = new ArrayList<OWLNamedClass>();
									for (Iterator ins = clo.iterator(); ins.hasNext();) {
										arrlist.add((OWLNamedClass) ins.next());
									}
									Random ran1 = new Random();
									int k1 = ran1.nextInt(arrlist.size());
									OWLNamedClass temps = arrlist.get(k1);
									strin2 = temps.getBrowserText().toString();
									strin1 = strin1 + ":" + strin2;
									template = strin2;
									if (temps.getInstanceCount() != 0) {
										boolean isget = false;
										Collection ind = temps.getInstances(true);
										loop: for (Iterator ina = ind.iterator(); ina.hasNext();) {
											OWLIndividual in = (OWLIndividual) ina.next();
											if (in.getPropertyValueCount(chinesename) > 0) {
												Collection chineseValues = in.getPropertyValues(chinesename);
												for (Iterator its = chineseValues.iterator(); its.hasNext();)// 寰幆瀹炰緥鎵�瀵瑰簲鐨勫涓腑鏂囧悕绉�
												{
													String cValue = its.next().toString();
													if (cValue.trim().equals(hasvla[hasvla.length - 2].trim())) {
														isget = true;
														strin2 = in.getBrowserText();
														temp2 = template + ":" + strin2;//
														template = template + ":" + strin2 + ":1.0";
														strin1 = strin1 + ":" + strin2;
														break loop;
													}

												}
											}

										}
										if (isget == false) {
											ArrayList<OWLIndividual> ils = new ArrayList<OWLIndividual>();
											for (Iterator ir = ind.iterator(); ir.hasNext();) {
												ils.add((OWLIndividual) ir.next());
											}
											Random ran = new Random();
											int k = ran.nextInt(ils.size());
											strin2 = ils.get(k).getBrowserText().toString();

											temp2 = template + ":" + strin2;

											template = template + ":" + strin2 + ":0.5";
											strin1 = strin1 + ":" + strin2;
										}
									}

								}
							}
						}

					}
					String temp3 = "";
					if (hasvla[0].equals("鏃堕棿") && !temp2.isEmpty()) {
						LightList.add(temp2);
					}
					if (hasvla[0].equals("鍔ㄤ綔") && !temp2.isEmpty()) {
						actionTemplateAttr.add(temp2);
					}
					if (hasvla[0].equals("浜虹墿") && !temp2.isEmpty()) {
						ActionNeedPeople.add(temp2);
					}
					if (hasvla[0].equals("鎯呯华") && !temp2.isEmpty()) {
						temp3 = temp2 + ":" + "0";
						// temp3=temp2+":"+hasvla[hasvla.length-1];
						moodTemplateAttr.add(temp3);
						weatherAndmoodAttr.add(temp3);
						ExpressionList.add(temp2);
						actionTemplateAttr.add(temp2);
						WindAttr.add(temp3);
						RainAttr.add(temp3);
						SnowAttr.add(temp3);
						LightList.add(temp2);
					}
					if ((hasvla[0].equals("澶╂皵") || hasvla[0].equals("瀛ｈ妭")) && !temp2.isEmpty()) {
						temp3 = temp2 + ":" + hasvla[hasvla.length - 1];
						weatherAndmoodAttr.add(temp3);
						RainAttr.add(temp3);
						WindAttr.add(temp3);
						SnowAttr.add(temp3);
						LightList.add(temp2);
						SeasonList.add(temp2);
					}
					if (hasvla[1].equals("澶╂皵娓╁害") && !temp2.isEmpty()) {
						actionTemplateAttr.add(temp2);
					}
				}
				System.out.println("strin1=" + strin1);
				if (template != null && template.length() != 0 && template.contains(":"))

				{
					englishTemplate.add(template);
					System.out.println(template);
				}

				if (strin1 != null && strin1.length() != 0 && strin1.contains(":"))
					strin = strin + "-" + strin1;
				System.out.println("strin=" + strin);
			}
			if (!actionTemplateAttr.isEmpty() && ActionNeedPeople.isEmpty()) {// 娣诲姞浜虹墿
																				// people=true;
				Random r = new Random();
				int sel = r.nextInt(2);
				if (sel == 1) {
					strin = strin + "-" + "CharacterTemplate:SingleCharacterTemplate:singlePersonTemplate";// random
																											// one
					englishTemplate.add("SingleCharacterTemplate:singlePersonTemplate:1.0");
				} else {
					strin = strin + "-" + "CharacterTemplate:ManCharacterTemplate:botherTemplate";
					englishTemplate.add("ManCharacterTemplate:botherTemplate:1.0");
				}

			}
			topictemplate.add(strin);

		}

		System.out.println("topictemplate=" + topictemplate);
		System.out.println("englishTemplate=" + englishTemplate);
		System.out.println("SeasonList=" + SeasonList);
		System.out.println("weatherAndmoodAttr=" + weatherAndmoodAttr);
		return englishTemplate;
	}

	public static void getSceneFromTopic(OWLModel model, OWLNamedClass englishTopicClass, String sceneType,
			double value) {
		OWLObjectProperty hasMa = model.getOWLObjectProperty("hasMa");
		RDFResource resource = englishTopicClass.getSomeValuesFrom(hasMa);
		if (resource != null) {
			String hasValues = resource.getBrowserText();// 鑾峰緱涓婚瀵瑰簲鐨勫満鏅殑绫诲悕
			String[] hasValuesSplit = hasValues.split(" or ");// 鍙兘瀵瑰簲澶氫釜 鍦烘櫙绫�;
			int sceneCount = hasValuesSplit.length;
			for (int i = 0; i < sceneCount; i++) {
				String sceneNameCls = hasValuesSplit[i];
				OWLNamedClass sceneClass = model.getOWLNamedClass(sceneNameCls);
				Collection curCls = sceneClass.getInstances(true);
				OWLIndividual indi = null;
				for (Iterator itIns = curCls.iterator(); itIns.hasNext();) {
					// res = new Random().nextDouble();
					indi = (OWLIndividual) itIns.next();
					String sceneName = indi.getBrowserText();
					int readyScene = isOldSceneCase(sceneName);// 鍒ゆ柇涓哄凡瀛樺湪瀹炰緥
					if (readyScene < 0) {
						if (sceneType.equals("MGTopic")) {
							sceneList.add(new SceneCase(sceneName, value > 0.6 ? value : 0.1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
									0, 0, 0));
							System.out.println("MGTopic--Scene: " + sceneName);
						}
						if (sceneType.equals("IETopic")) {
							sceneList.add(new SceneCase(sceneName, 0, value, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
							System.out.println("IETopic--Scene: " + sceneName);
						}
						if (sceneType.equals("RuleTopic")) {
							System.out.println("RuleTopic--Scene: " + sceneName);
						}
					} else {
						if (sceneType.equals("MGTopic") && sceneList.get(readyScene).MGProb == 0.0)
							sceneList.get(readyScene).MGProb += value;
						if (sceneType.equals("IETopic") && sceneList.get(readyScene).IEProb == 0.0)
							sceneList.get(readyScene).IEProb += value;
						if (sceneType.equals("RuleTopic") && sceneList.get(readyScene).ruleReason == 0.0)
							sceneList.get(readyScene).ruleReason += value;
					}
				}

			}
		}

	}

	public static void getSceneFromClass(OWLModel model, String sceneCls, String sceneType, double value) {

		if (sceneType.equals("BackGroundScene") | sceneType.equals("TemplateScene")) {

			OWLNamedClass sceneClass = model.getOWLNamedClass(sceneCls);
			Collection curCls = sceneClass.getInstances(true);
			OWLIndividual indi = null;
			double res = 0;

			for (Iterator itIns = curCls.iterator(); itIns.hasNext();) {
				// res = new Random().nextDouble();
				indi = (OWLIndividual) itIns.next();
				// 鍒ゆ柇鍦烘櫙鏄惁閲嶅
				int readyScene = isOldSceneCase(indi.getBrowserText());

				if (readyScene < 0 && sceneType.equals("BackGroundScene")) {
					sceneList.add(new SceneCase(indi.getBrowserText(), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1));
					System.out.println("BackGroundScene--Scene: " + indi.getBrowserText());
				}
				if (readyScene < 0 && sceneType.equals("TemplateScene")) {
					sceneList.add(new SceneCase(indi.getBrowserText(), 0, 0, 0, value, 0, 0, 0, 0, 0, 0, 0, 0, 0));
					System.out.println("TemplateScene-Scene: " + indi.getBrowserText());
				}
			}

		} else {// 妯℃澘鎴栦富棰樺搴旂殑ma
			OWLObjectProperty hasMa = model.getOWLObjectProperty("hasMa");
			OWLIndividual indi = null;
			OWLNamedClass sceneClass = model.getOWLNamedClass(sceneCls);
			Collection curCls = sceneClass.getInstances(true);
			for (Iterator itIns = curCls.iterator(); itIns.hasNext();) {
				indi = (OWLIndividual) itIns.next();
				if (indi.hasPropertyValue(hasMa)) {
					if (indi.getPropertyValueCount(hasMa) > 0) {
						Collection collection = indi.getPropertyValues(hasMa);
						for (Iterator iValues = collection.iterator(); iValues.hasNext();) {
							OWLIndividual animationIndividual = (OWLIndividual) iValues.next();
							String sceneName = animationIndividual.getBrowserText();
							int readyScene = isOldSceneCase(sceneName);
							if (readyScene < 0) {
								if (sceneType.equals("MGTopic")) {

									sceneList.add(new SceneCase(sceneName, (value > 0.6 ? value : 0.1), 0, 0, 0, 0, 0,
											0, 0, 0, 0, 0, 0, 0));
									System.out.println("MGTopic--Scene: " + sceneName);
								}
								if (sceneType.equals("QTopic")) {

									sceneList.add(new SceneCase(sceneName, 0, 0, 0, 0, 0, 0, 0, 0, 0, value, 0, 0, 0));
									System.out.println("QTopic--Scene: " + sceneName);
								}
								if (sceneType.equals("IETopic")) {
									sceneList.add(new SceneCase(sceneName, 0, value, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
									System.out.println("IETopic--Scene: " + sceneName);
								}
								if (sceneType.equals("RuleTopic"))

								{
									sceneList.add(new SceneCase(sceneName, 0, 0, value, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
									System.out.println("RuleTopic--Scene: " + sceneName);
								}
								if (sceneType.equals("TemplateScene")) {
									sceneList.add(new SceneCase(sceneName, 0, 0, 0, value, 0, 0, 0, 0, 0, 0, 0, 0, 0));
									System.out.println("Template--Scene: " + sceneName);
								}
							} else {
								if (sceneType.equals("MGTopic") && sceneList.get(readyScene).MGProb == 0.0)
									sceneList.get(readyScene).MGProb += value;
								if (sceneType.equals("IETopic") && sceneList.get(readyScene).IEProb == 0.0)
									sceneList.get(readyScene).IEProb += value;
								if (sceneType.equals("RuleTopic") && sceneList.get(readyScene).ruleReason == 0.0)
									sceneList.get(readyScene).ruleReason += value;
							}

						}
					}
				}
			}
		}
	}

	public static void SceneCollect(OWLModel model, ArrayList<String> topic, ArrayList<String> topicFromMG,
			ArrayList<String> topicFromQiu, ArrayList<String> englishTemplate, ArrayList<String> englishTempW,
			ArrayList<String> englishTemplatePlot, int count[])
			throws SecurityException, IOException, SWRLRuleEngineException, OntologyLoadException {
		sceneList.clear();
		// OWLNamedClass englishTopicClass;
		// getSceneFromClass(model,"BackgroundScene","BackGroundScene",1);
		// //2014.4.16 鏃ヤ笉杩愯鑳屾櫙鍦烘櫙
		// 璇诲彇璇ョ被鏄惁瀹氫箟浜嗚儗鏅満鏅�
		System.out.println(count[0] + "\t" + count[1]);
		System.out.println();
		if (count[0] != 0 && count[1] == 0) {
			OWLNamedClass bg1 = model.getOWLNamedClass("BackgroundLandScene");
			OWLNamedClass bg2 = model.getOWLNamedClass("BackgroundRoomScene");
			OWLNamedClass bg3 = model.getOWLNamedClass("BackgroundSnowScene");
			OWLDatatypeProperty chineseNameProperty = model.getOWLDatatypeProperty("chineseName");
			OWLNamedClass superclass = model.getOWLNamedClass("CharacterTemplate");
			// OWLNamedClass
			// superc=model.getOWLNamedClass("SutraCharacterRoleTemplate");
			Collection clo = superclass.getSubclasses();
			// Collection clo1=superc.getSubclasses();
			for (int c = 0; c < englishTemplate.size(); c++) {
				String str = englishTemplate.get(c);
				String str1 = str.substring(0, str.indexOf(":"));
				if (clo.size() != 0) {
					for (Iterator ii = clo.iterator(); ii.hasNext();) {
						OWLNamedClass subclass = (OWLNamedClass) ii.next();
						String str2 = subclass.getBrowserText().toString();

						if (str2.equals(str1)) {
							System.out.println("hasBGClass+" + bg1.getBrowserText());
							getSceneFromClass(model, bg1.getBrowserText().toString(), "BackGroundScene", 1);
							System.out.println("hasBGClass+" + bg2.getBrowserText());
							getSceneFromClass(model, bg2.getBrowserText().toString(), "BackGroundScene", 1);
							System.out.println("hasBGClass+" + bg3.getBrowserText());
							getSceneFromClass(model, bg3.getBrowserText().toString(), "BackGroundScene", 1);
							break;
						}
					}
				}
				/*
				 * if(clo1.size()!=0) { for(Iterator
				 * iii=clo1.iterator();iii.hasNext();) { OWLNamedClass
				 * subclass1=(OWLNamedClass) iii.next(); String
				 * str23=subclass1.getBrowserText().toString();
				 * 
				 * if(str23.equals(str1)) {
				 * System.out.println("hasBGClass+"+bg1.getBrowserText());
				 * getSceneFromClass(model,bg1.getBrowserText().toString(),
				 * "BackGroundScene",1);
				 * System.out.println("hasBGClass+"+bg2.getBrowserText());
				 * getSceneFromClass(model,bg2.getBrowserText().toString(),
				 * "BackGroundScene",1);
				 * System.out.println("hasBGClass+"+bg3.getBrowserText());
				 * getSceneFromClass(model,bg3.getBrowserText().toString(),
				 * "BackGroundScene",1); break; }
				 * 
				 * } }
				 */
			}

		} else {
			for (int i = 0; i < englishTemplate.size(); i++) {
				String Temp = englishTemplate.get(i);
				String subTemp = Temp.split(":")[0];
				OWLObjectProperty hasBG = model.getOWLObjectProperty("hasBackgroundScene");
				OWLObjectProperty hasBGMa = model.getOWLObjectProperty("hasMa");
				OWLNamedClass templateClass = model.getOWLNamedClass("Template");// 寰楀埌template绫�
				Collection templateClas = templateClass.getSubclasses(true);
				Collection BGIndividual = null;
				for (Iterator it = templateClas.iterator(); it.hasNext();) {

					OWLNamedClass cls = (OWLNamedClass) it.next();
					if (cls.getBrowserText().trim().equals(subTemp.trim())) {
						// 瀛樺湪璇ュ睘鎬с�傚垯鍙栧嚭璇ュ睘鎬х殑鍊�

						Object hasBGScene;
						if (cls.getSomeValuesFrom(hasBG) != null) {
							hasBGScene = cls.getSomeValuesFrom(hasBG);
							System.out.println("hasBGScene锛�" + hasBGScene.getClass().getName());

							if (hasBGScene.getClass().getName()
									.equals("edu.stanford.smi.protegex.owl.model.impl.DefaultOWLUnionClass")) {
								OWLUnionClass hasBGUnion = (OWLUnionClass) cls.getSomeValuesFrom(hasBG);// 鑾峰彇骞堕泦绫�
								Collection<?> hasBG_C = hasBGUnion.getNamedOperands();
								for (Iterator<?> itBG = hasBG_C.iterator(); itBG.hasNext();) {
									OWLNamedClass hasBGClass = (OWLNamedClass) itBG.next();
									System.out.println("hasBGClass+" + hasBGClass.getBrowserText());
									// BGIndividual=hasBGClass.getInstances(true);//鑾峰彇鑳屾櫙鍦烘櫙瀹炰緥
									getSceneFromClass(model, hasBGClass.getBrowserText().toString(), "BackGroundScene",
											1);

								}
							} else {

								getSceneFromClass(model, ((RDFResource) hasBGScene).getBrowserText(), "BackGroundScene",
										1);
							}
						}
						// 鐢辨ā鏉縣asMa鏈夋剰涔夌殑鑳屾櫙鍦烘櫙

						if (cls.getSomeValuesFrom(hasBGMa) != null) {
							Object hasMa = cls.getSomeValuesFrom(hasBGMa);
							getSceneFromClass(model, ((RDFResource) hasMa).getBrowserText(), "TemplateScene", 1);
						}

					}
				}
			}
		}
		double topicProbabilityMG = 0.0;
		if (topicFromMG.size() != 0) {
			int topicCount = topicFromMG.size();
			for (int i = 0; i < topicCount; i += 2) {
				String topicNameFromMG = topicFromMG.get(i);
				String topicProbabilityStr = topicFromMG.get(i + 1).trim();
				topicProbabilityMG = Float.parseFloat(topicProbabilityStr);
				if (topicProbabilityMG > 0.6) {
					OWLNamedClass englishTopicClass = getEnglishTopic(model, topicNameFromMG);
					if (englishTopicClass != null) {
						englishTopicMG = englishTopicClass.getBrowserText();
						System.out.println("锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒englishTopicMg:" + englishTopicMG);
						getSceneFromClass(model, englishTopicMG, "MGTopic", topicProbabilityMG);
						getSceneFromTopic(model, englishTopicClass, "MGTopic", topicProbabilityMG);
					}
				}

			}
		}
		if (topicFromQiu.size() > 0) {
			int topicCount = topicFromQiu.size();
			for (int i = 0; i < topicCount; i += 2) {
				String topicNameFromQ = topicFromQiu.get(i);
				String topicProbabilityStr = topicFromQiu.get(i + 1).trim();
				double topicProbability = Float.parseFloat(topicProbabilityStr);
				if (topicProbability > 0.3) {
					OWLNamedClass englishTopicClass = getEnglishTopic(model, topicNameFromQ);
					if (englishTopicClass != null) {
						englishTopicQiu = englishTopicClass.getBrowserText();

						System.out.println("锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒englishTopicQiu:" + englishTopicQiu);
						getSceneFromClass(model, englishTopicQiu, "QTopic", topicProbability);
						getSceneFromTopic(model, englishTopicClass, "QiuTopic", topicProbability);
					}
				}

			}
		}

		if (topic.size() != 0)// IEc鎶藉埌鐨則opic
		{
			int topicCount = topic.size();
			for (int i = 0; i < topicCount; i++) {
				String topicName = topic.get(i);
				OWLNamedClass englishTopicClass = getEnglishTopic(model, topicName);
				if (englishTopicClass != null) {
					englishTopicIE = englishTopicClass.getBrowserText();
					System.out.println("englishTopicIE" + englishTopicIE);
					getSceneFromClass(model, englishTopicIE, "IETopic", 1);
					getSceneFromTopic(model, englishTopicClass, "IETopic", 1);
				}
			}
		}
		if (englishTemplate.size() != 0)// 妯℃澘鎺ㄥ満鏅�
		{

			ArrayList<String> englishTemplateW = new ArrayList();
			for (int j = 0; j < englishTemplate.size(); j++) {
				int iP = englishTemplate.get(j).lastIndexOf(":");
				englishTemplateW.add(englishTemplate.get(j).substring(0, iP));
			}
			logger.info("鍒欏埄鐢ㄦā鏉夸俊鎭帹鍔ㄧ敾鍦烘櫙");
			SWRLMethod.executeSWRLEngine1(model, "getTopicFromTemplate", "", englishTemplateW);// 閫氳繃妯℃澘鎺ㄥ嚭涓婚
			logger.info("杩愯瑙勫垯锛岀湅鏄惁鍙互閫氳繃妯℃澘淇℃伅鎺ㄥ嚭涓婚");
			String topicIndividual = getTopicFromTemplateAfterSWRL(model, englishTemplateW);
			System.out.println("????ruleTopic:" + topicIndividual);
			if (!topicIndividual.isEmpty()) {
				OWLIndividual indi = model.getOWLIndividual(topicIndividual);
				String clasName = indi.getDirectType().getBrowserText();
				getSceneFromClass(model, clasName, "RuleTopic", 1);

			}
		}
		/**
		 * 鐢眘ceneCollet 寰楀埌鍚勪釜鑻辨枃涓婚 璁剧疆涓婚englishTopicStr
		 */

		// 娣诲姞ADL涓婇潰鐨凾opic鍊�
		if (!englishTopicIE.equals("")) {
			englishTopicStr = englishTopicIE;
		} else {
			if (topicProbabilityMG > 0.6) {
				englishTopicStr = englishTopicMG;
			}
		}
		System.out.println("EnlishTopicStr=" + englishTopicStr);
		if (topicProbabilityMG > 0.6) {
			if (topiclist.size() != 0) {
				boolean topicflage = false;
				for (int k = 0; k < topiclist.size(); k++) {
					if (topiclist.get(k).equals(englishTopicMG)) {
						topicflage = true;
						break;
					}
				}
				if (topicflage == false) {
					topiclist.add(englishTopicMG);
				}
			} else {
				topiclist.add(englishTopicMG);
			}

		}
		if (englishTemplate.size() != 0) {

			// 棣栧厛鍒ゆ柇妯℃澘鍔ㄤ綔涓庝汉鐗╂槸鍚﹀尮閰嶏紝濡傛灉鍖归厤鍒欏皢閫氳繃鍔ㄤ綔妯℃澘鍜屼汉鐗╂ā鏉跨殑鍔ㄧ敾鍔犲叆锛屽惁鍒欎粠妯℃澘涓幓鎺夎繖涓や釜妯℃澘鍊�
			Action action = new Action();
			try {
				action.actionInfer(actionTemplateAttr, model, englishTopicStr);
				if (!action.isActionFlag()) {
					englishTemplate = delActionAndPeeople(englishTemplate);
					logger.info("Action and people no match, delete the templte");
				} else {
					if (ifActionOrExpression == false)
						ifActionOrExpression = true;
				}
			} catch (OntologyLoadException e) {
				logger.info("Action is error");
			}
			getAnim(englishTemplate, model);// 姝ゅ灏嗘鐜囧�煎幓鎺�

		}
		// 2014.10.30鍒犻櫎涓嶈兘浠庢ā鏉垮�肩洿鎺ュ緱鏉ワ紝
		if (englishTempW.size() != 0) {// 妯℃澘鍊煎緱鍒板疄渚�
			ArrayList tempIndual = new ArrayList();

			for (int i = 0; i < englishTempW.size(); i++) {
				String Temp = englishTempW.get(i);

				OWLNamedClass temp = model.getOWLNamedClass(Temp);
				Collection induals = temp.getDirectInstances();
				for (Iterator it = induals.iterator(); it.hasNext();) {
					OWLIndividual tempInd = (OWLIndividual) it.next();
					tempIndual.add(tempInd.getBrowserText());

				}

			}
			System.out.println("妯℃澘瀹炰緥锛�" + tempIndual);
			getAnim(tempIndual, model);
		}

		/**
		 * plot design wjj 鏂拌璁＄殑鐢ㄦ柊鐨勪腑鏂囦富棰樸�� 鍏朵腑鍦烘櫙鐨勫悕瀛椾互plot涓缃殑涓婚+鈥�.ma鈥濓紝缁勬垚鏂扮殑鍦烘櫙鍚嶅瓧 model
		 * 涓篛WLAllFile澶栭潰鐨剆um2 璺緞,鍏堥�夊畾涓婚锛屽涓婚璁捐锛屾病鏈変富棰樻墠浠ユā鍨嬪懡鍚嶆柊鍦烘櫙
		 */
		if (topic.size() > 0) {
			for (int i = 0; i < topic.size(); i++) {
				String topicIE = topic.get(i);
				OWLNamedClass topicNamedCls = getEnglishTopicFromPlot(model, topicIE);
				if (topicNamedCls != null) {
					String topicName = topicNamedCls.getBrowserText();
					if (englishTopicStr.equals("")) {
						englishTopicStr = topicName;
					}
					sceneList.add(new SceneCase(topicName + ".ma", 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
				}
			}
		} else {
			if (topicFromMG.size() != 0) {
				int topicCount = topicFromMG.size();
				for (int i = 0; i < topicCount; i += 2) {
					String topicNameFromMG = topicFromMG.get(i);
					OWLNamedClass topicNameMGCls = getEnglishTopicFromPlot(model, topicNameFromMG);
					if (topicNameMGCls != null) {
						String topicNameMG = topicNameMGCls.getBrowserText();

						String topicProbabilityStr = topicFromMG.get(i + 1).trim();
						double topicProbability = Float.parseFloat(topicProbabilityStr);
						if (englishTopicStr.equals("") && topicProbability > 0.6) {
							englishTopicStr = topicNameMG;
						}
						sceneList.add(new SceneCase(topicNameMG + ".ma",
								topicProbability > 0.6 ? topicProbability : 0.1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
					}
				}
			} else {
				// *******************************plot design
				// 妯″瀷閮ㄥ垎**14.12.11****************************************//

				if (englishTemplatePlot.size() > 0) {
					System.out.println("templatePlot" + englishTemplatePlot.size());
					sceneList.add(new SceneCase("TemplatePlot.ma", 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0));

				}
				// *******************************plot design 妯″瀷閮ㄥ垎 end
				// ******************************************//

			}
		}

		int sceneCount = sceneList.size();
		for (int i = 0; i < sceneCount; i++) {
			fogInsert fog = new fogInsert();
			Action action = new Action();
			try {
				action.actionInfer(actionTemplateAttr, model, englishTopicStr);
			} catch (Exception ex) {
				logger.info("ERROR: Action judge Exception");
			}
			if (action.isActionFlag()) {
				sceneList.get(i).ActionScore = 1;// 鍒嗗瓙
			}
			System.out.println("sceneName=" + sceneList.get(i).sceneName + "\t" + "scenelist.size=" + sceneList.size());
			if (sceneList.get(i).sceneName.contains("Plot.ma")) {
				System.out.println("action." + action.isActionFlag());
				if (action.isActionFlag()) {
					sceneList.get(i).ActionScore = 1;// 鍒嗗瓙
				}
			} else {
				OWLIndividual temp = model.getOWLIndividual(sceneList.get(i).sceneName);
				/*
				 * OWLDatatypeProperty hasSky =
				 * model.getOWLDatatypeProperty("hasSky"); 2014.10.6鏃ユ敞閲婃帀锛屽洜鍚庢湡娌℃湁瀹炵幇
				 * temp.hasPropertyValue(hasSky)|
				 */
				OWLObjectProperty time = model.getOWLObjectProperty("time");
				// hasWeatherTeplate
				System.out.println("sceneName=" + temp.getBrowserText());
				if (temp.hasPropertyValue(time)) {
					sceneList.get(i).timeable = 1;
					System.out.println("timeable=" + sceneList.get(i).timeable + temp.hasPropertyValue(time));
				}
				try {
					logger.info("fog num");
					fog.fogInfer2(weatherAndmoodAttr, model, sceneList.get(i).sceneName);
					if (fog.getFog() > 0) {
						sceneList.get(i).isWeatherable = 1;// 鍒嗗瓙
					}
					logger.info("jiali finish");
				} catch (Exception exJiali) {
					logger.info("ERROR: Jiali Exception");
				}
				logger.info("wind rain snow" + windRainSnowNeedAttr);
				Effect effect = new Effect();
				ArrayList<String> list = new ArrayList();
				for (int m = 0; m < windRainSnowNeedAttr.size(); m++) {
					for (int j = 0; j < windRainSnowNeedAttr.get(m).size(); j++) {
						list.add(windRainSnowNeedAttr.get(m).get(j).toString());
					}

				}
				boolean weatherflag = effect.IsWeather(list, model, sceneList.get(i).sceneName, englishTopicStr);
				if (weatherflag == true) {
					sceneList.get(i).isWeatherable = 1;
					System.out.println("Weather:" + sceneList.get(i).isWeatherable);
				}
			}

		}

		System.gc();
	}

	/**
	 * 鍒ゆ柇妯℃澘鍔ㄤ綔涓庝汉鐗╂槸鍚﹀尮閰嶏紝濡傛灉鍖归厤鍒欏皢閫氳繃鍔ㄤ綔妯℃澘鍜屼汉鐗╂ā鏉跨殑鍔ㄧ敾鍔犲叆锛屽惁鍒欎粠妯℃澘涓幓鎺夎繖涓や釜妯℃澘鍊�
	 * 
	 * @param englishTemplate
	 *            鎵�鏈夌殑鑻辫妯℃澘鍊�,鐢变簬鏂规硶涓尮閰嶇殑鑻辨枃鍚嶅瓧锛屾墍浠ュ湪浜虹墿涓庡姩鐗╁仛妯″瀷鐨勬椂鍊欙紝鐖剁被蹇呴』甯︽湁Character涓嶢nimal瀛楁牱
	 * @return boolean 2014.12.8
	 */

	public static ArrayList delActionAndPeeople(ArrayList englishTemplate) {
		ArrayList englishTemplateExActionP = new ArrayList();
		for (int i = 0; i < englishTemplate.size(); i++) {
			String tempTem = (String) englishTemplate.get(i);
			if (!tempTem.contains("Action") && !tempTem.contains("Character") && !tempTem.contains("Animal")) {
				englishTemplateExActionP.add(englishTemplate.get(i));
			}
		}
		return englishTemplateExActionP;
	}

	/**
	 * OWL涓幏鍙栦竴涓被鐨勫疄渚�
	 * 
	 * @author AI
	 * @param cls
	 *            owl涓被
	 * @return ArrayList IndividualList
	 */

	public static ArrayList getIndividualFromCls(OWLNamedClass cls) {
		ArrayList IndividualList = new ArrayList();

		Collection individual = cls.getInstances();
		for (Iterator it = individual.iterator(); it.hasNext();) {
			OWLIndividual tempIndividual = (OWLIndividual) it.next();

			IndividualList.add(tempIndividual.getBrowserText());
		}
		System.out.println("瀹炰緥锛�" + IndividualList);
		return IndividualList;
	}

	/**
	 * 鐢辨ā鏉挎垨妯″瀷涓殑灞炴�ц幏寰楀姩鐢诲満鏅�
	 * 
	 */
	public static void getAnim(ArrayList englishTemplate, OWLModel model) {

		double value = 0.0;
		int tempCount = englishTemplate.size();
		// System.out.println("?????TemplateRelated--Scene: ");

		for (int i = 0; i < tempCount; i++) {// System.out.println("?????TemplateRelated--Scene:
												// ");
			OWLObjectProperty hasMa = model.getOWLObjectProperty("hasAnimationNameFromTemplate");
			String engTemp = "";
			if (((String) englishTemplate.get(i)).contains(":")) {
				int size = ((String) englishTemplate.get(i)).split(":").length;
				engTemp = ((String) englishTemplate.get(i)).split(":")[size - 2];// 鍙栧�掓暟绗簩涓负妯℃澘鍊�
				value = Double.parseDouble(((String) englishTemplate.get(i)).split(":")[size - 1]);// 鍙栨渶鍚庝竴涓�
																									// 涓烘ā鏉跨浉鍏冲��
				System.out.println("engTemp and value:" + value + engTemp);
				// 鏈�鍚庣殑鍊煎簲涓烘ā鏉跨浉鍏硋alue

			} else {
				engTemp = (String) englishTemplate.get(i);
				value = 0.5;
			}
			OWLIndividual indi = model.getOWLIndividual(engTemp);

			if (indi != null && indi.hasPropertyValue(hasMa)) {
				System.out.println(".....TemplateRelated--Individual: " + indi.getBrowserText());
				if (indi.getPropertyValueCount(hasMa) > 0) {
					Collection collection = indi.getPropertyValues(hasMa);
					for (Iterator iValues = collection.iterator(); iValues.hasNext();) {
						OWLIndividual animationIndividual = (OWLIndividual) iValues.next();
						String sceneName = animationIndividual.getBrowserText();
						// System.out.println(".....TemplateRelated--Scene:
						// "+sceneName);

						int readyScene = isOldSceneCase(sceneName);

						if (readyScene < 0) {
							sceneList.add(new SceneCase(sceneName, 0, 0, 0, value, 0, 0, 0, 0, 0, 0, 0, 0, 0));// value鍊间负1or0.5
																												// 1琛ㄧず涓虹洿鎺ョ炕璇戣繃鏉ョ殑妯″瀷銆�0.5涓洪殢鏈洪�夋嫨
							System.out.println("TemplateRelated--Scene: " + sceneName);
						} else {
							sceneList.get(readyScene).templateRelated += value;
						}

					}
				}
			}

		}

	}

	public static void addModelToTemplate(OWLModel model, ArrayList<String> englishTemplate) {

		for (int i = 0; i < englishTemplate.size(); i++) {
			ArrayList clo = new ArrayList();
			String[] temp = englishTemplate.get(i).split(":");
			OWLObjectProperty obj = model.getOWLObjectProperty("hasModelFromTemplate");
			OWLDatatypeProperty ifchangemodel = model.getOWLDatatypeProperty("ifChangeModel");
			OWLIndividual ind = null;

			if (temp.length > 1) {
				ind = model.getOWLIndividual(temp[temp.length - 1]);
				if (ind.getPropertyValueCount(obj) == 0) {
					OWLNamedClass namedclass = model.getOWLNamedClass(temp[0]);
					ArrayList<String> modelclass = new ArrayList<String>();
					RDFResource resource = namedclass.getSomeValuesFrom(obj);
					if (resource != null) {
						String hasValues = resource.getBrowserText();
						if (hasValues.indexOf("or") >= 0 && hasValues.contains(" ")) {
							String[] hasValuesSplit = hasValues.split("or");
							if (hasValuesSplit.length > 0) {
								for (int ii = 0; ii < hasValuesSplit.length; ii++) {
									modelclass.add(hasValuesSplit[i].toString().trim());
								}
							}
						} else {
							modelclass.add(resource.getBrowserText());
						}
					}
					if (modelclass.size() != 0) {
						for (int jj = 0; jj < modelclass.size(); jj++) {
							OWLNamedClass nam = model.getOWLNamedClass(modelclass.get(jj));
							if (nam.getInstanceCount(true) != 0) {
								int count = nam.getInstanceCount(true);
								Random r = new Random();
								int k = 0;
								if (count >= 5)
									k = r.nextInt(3) + 1;
								else
									k = r.nextInt(count) + 1;
								System.out.println("鍏宠仈妯″瀷涓暟=" + k);
								Collection incollect = nam.getInstances(true);

								ArrayList<OWLIndividual> array = new ArrayList();
								for (Iterator ite = incollect.iterator(); ite.hasNext();) {
									array.add((OWLIndividual) ite.next());
								}

								int n = 0;
								while (n < k) {
									Random r1 = new Random();
									int length = array.size();
									int k1 = r1.nextInt(length);
									OWLIndividual indivi = (OWLIndividual) array.get(k1);
									array.remove(k1);
									clo.add(indivi);
									n++;
									System.out.print(indivi.getBrowserText().toString() + "\t");
									System.out.println();
								}

							}
						}

					}
				} else if (ind.getPropertyValueCount(obj) != 0) {
					// if(ind.getPropertyValueCount(ifchangemodel)==0){

					Collection linkt1 = ind.getPropertyValues(obj);
					for (Iterator ites = linkt1.iterator(); ites.hasNext();) {
						OWLIndividual linkt = (OWLIndividual) ites.next();
						Collection col = linkt.getDirectTypes();
						for (Iterator iter = col.iterator(); iter.hasNext();) {
							OWLNamedClass linkc = (OWLNamedClass) iter.next();
							if (linkc.getInstanceCount() > 1) {
								int count = linkc.getInstanceCount();
								Random r = new Random();
								int k = 0;
								if (count >= 3)
									k = r.nextInt(2) + 1;
								else
									k = r.nextInt(count) + 1;
								System.out.println("鍏宠仈妯″瀷涓暟=" + k);
								Collection incollect = linkc.getInstances();

								ArrayList array = new ArrayList();
								for (Iterator ite = incollect.iterator(); ite.hasNext();) {
									array.add(ite.next());
								}
								int n = 0;
								// OWLIndividual arg1=(OWLIndividual)
								// ind.getPropertyValue(obj);
								ind.removePropertyValue(obj, linkt);

								while (n < k) {
									Random r1 = new Random();
									int length = array.size();
									int k1 = r1.nextInt(length);
									OWLIndividual indivi = (OWLIndividual) array.get(k1);
									array.remove(k1);
									clo.add(indivi);
									n++;
									System.out.print(indivi.getBrowserText().toString() + "\t");
									System.out.println();
								}

							}

						}
					}
					// }

				}
				if (clo.size() != 0) {

					ind.setPropertyValues(obj, clo);
				}
			}
			System.out.println(ind.getPropertyValueCount(obj));

		}
	}

	public static void CoutModelPlacable(String maName, int sceneNO, OWLModel model, ArrayList<String> englishTemplate,
			ArrayList<String> templateWithColor)
			throws SWRLFactoryException, SecurityException, SWRLRuleEngineException, IOException {

		System.out.println("qqqqqqqqqqqqqqqqqqqq maName:" + maName);
		System.out.println(englishTemplate);
		OWLObjectProperty obj = model.getOWLObjectProperty("hasModelFromTemplate");

		OWLModel owlModel = processSWRL(maName, "", model, englishTemplate);
		OWLIndividual maIndividual = model.getOWLIndividual(maName);
		OWLObjectProperty usedSpaceInMaProperty = model.getOWLObjectProperty("usedSpaceInMa");
		OWLObjectProperty hasPutObjectInSpaceProperty = model.getOWLObjectProperty("hasPutObjectInSpace");

		OWLObjectProperty usedModelInMaProperty = model.getOWLObjectProperty("usedModelInMa");

		ArrayList<OWLIndividual> individualList = new ArrayList();
		ArrayList<String> individualListFromTemplate = getIndividualFromEnglishTemplate(model, englishTemplate);
		ArrayList<String> colorIndividualList = getIndividualFromEnglishTemplate(model, templateWithColor);
		sceneList.get(sceneNO).templateModelNum = individualListFromTemplate.size();
		System.out.println("鍜屾ā鐗堢浉鍏崇殑妯″瀷鏁版槸锛�" + individualListFromTemplate.size());
		sceneList.get(sceneNO).colorModelNum = colorIndividualList.size();

		System.out.println("1111111individualListFromTemplate:" + individualListFromTemplate);
		System.out.println("colorIndividualList:" + colorIndividualList);
		// @SuppressWarnings("unused")
		int count = 0;
		if (maName.contains("Plot.ma")) {
			sceneList.get(sceneNO).templateModelNum = individualListFromTemplate.size();
			sceneList.get(sceneNO).placableModelNum = individualListFromTemplate.size();
			sceneList.get(sceneNO).placableColorModelNum = colorIndividualList.size();
			System.out.println("鍙斁鍏ラ鑹叉ā鍨嬫暟閲�" + sceneList.get(sceneNO).placableColorModelNum);
		} else {
			String name = maIndividual.getBrowserText();
			System.out.println("maName:" + name);
			// 浠巙sedSpaceInMa鍏ユ墜锛屼富瑕佹洿鏀筧ddTomMa灞炴�.
			System.out.println("鍙斁鍏ユā鍨嬬殑鍙敤绌洪棿鐨勪釜鏁�" + maIndividual.getPropertyValueCount(usedSpaceInMaProperty));
			if (maIndividual.getPropertyValueCount(usedSpaceInMaProperty) > 0) {
				ArrayList<OWLIndividual> innerIndividualList = new ArrayList();// 鐢ㄦ潵瀛樻斁姣忎釜space涓婂瓨鏀剧殑妯″瀷涓綋鐨凮WLIndividual
				ArrayList<OWLIndividual> colorIndi = new ArrayList();
				Collection usedSpaceValues = maIndividual.getPropertyValues(usedSpaceInMaProperty);
				// 娴嬭瘯鍙敤绌洪棿
				System.out.println("鍦烘櫙鍙敤绌洪棿" + usedSpaceValues);
				for (Iterator iValues = usedSpaceValues.iterator(); iValues.hasNext();) {// iVlaues鏄痵pace鐨勫悕瀛�

					OWLIndividual spaceIndividual = (OWLIndividual) iValues.next();
					Collection objectInSpaceValues = spaceIndividual.getPropertyValues(hasPutObjectInSpaceProperty);
					System.out.println("objectInSpaceValues:" + objectInSpaceValues.size());
					System.out.println("objectInSpaceValues:" + objectInSpaceValues);
					// objectInSpaceValues鍙互鏀惧叆绌洪棿鐨勬ā鍨嬪垪琛�

					// 閫氳繃Map鐨�<key锛寁alue>瀵瑰�兼潵澶勭悊娣诲姞鏌愪釜绫讳笅闈㈢殑澶氫釜瀹炰緥鐨勯棶棰�
					Map<OWLNamedClass, ArrayList<OWLIndividual>> map = new HashMap<OWLNamedClass, ArrayList<OWLIndividual>>();

					for (Iterator iiValues = objectInSpaceValues.iterator(); iiValues.hasNext();) {// iiValues姣忎釜space涓婇潰鏀剧殑鐗╀綋
						OWLIndividual objectIndividual = null;
						// 绌哄満鏅负浠�涔堝崟鐙嬁鍑烘潵锛�
						if (name.equals("empty.ma"))
							objectIndividual = model.getOWLIndividual(iiValues.next().toString());
						else
							objectIndividual = (OWLIndividual) iiValues.next();
						System.out.println("鍦烘櫙鍙敤绌洪棿涓彲鏀剧殑妯″瀷" + objectIndividual.getBrowserText());
						Iterator itd = individualListFromTemplate.iterator();
						Iterator itd1 = colorIndividualList.iterator();
						boolean isEqualTemplate = false;
						while (itd.hasNext()) {
							String individualStr = (String) itd.next();
							System.out.println("鍜屾ā鏉跨浉鍏崇殑妯″瀷鍚嶅瓧" + individualStr);
							System.out.println("鍦烘櫙鍙敤绌洪棿涓彲鏀剧殑妯″瀷" + objectIndividual.getBrowserText());
							if (individualStr.equals(objectIndividual.getBrowserText())) {
								if (innerIndividualList.size() == 0) {
									innerIndividualList.add(objectIndividual);
								} else {// 灏嗛噸澶嶇殑鍘绘帀锛屾ā鍨嬪凡缁忓彲浠ユ斁鍏ワ紝灏变笉鍦ㄩ噸澶嶈绠�
									int nflag = 0;
									for (int ni = 0; ni < innerIndividualList.size(); ni++) {
										System.out.println(
												"innerIndividualList" + innerIndividualList.get(ni).getBrowserText());
										System.out.println("objectIndividual" + objectIndividual.getBrowserText());
										if (innerIndividualList.get(ni).getBrowserText()
												.equals(objectIndividual.getBrowserText())) {
											nflag = 1;
											break;

										}

									}
									if (nflag == 0) {
										innerIndividualList.add(objectIndividual);
									}
								}
							}

						}
						while (itd1.hasNext()) {
							String individualStr = (String) itd1.next();
							if (individualStr.equals(objectIndividual.getBrowserText())
									&& !colorIndi.contains(objectIndividual)) {

								colorIndi.add(objectIndividual);
							}

						}
					}
				}
				sceneList.get(sceneNO).placableModelNum = innerIndividualList.size();
				System.out.println("鍙斁鍏ヨ鍦烘櫙涓殑妯″瀷鏁伴噺" + innerIndividualList.size());
				sceneList.get(sceneNO).placableColorModelNum = colorIndi.size();
				System.out.println("鍙斁鍏ラ鑹叉ā鍨嬫暟閲�" + sceneList.get(sceneNO).placableColorModelNum);
				// placableModelNum = innerIndividualList.size();
			}
		}

	}

	public static void TemplateCollect(OWLModel model, ArrayList<String> englishTemplate,
			ArrayList<String> templateWithColor, ArrayList<String> colorMark)
			throws SWRLFactoryException, SecurityException, SWRLRuleEngineException, IOException {
		int sceneCount = sceneList.size();
		int templateModelNum = 0;
		int placableModelNum = 0;
		int placableColorModelNum = 0;
		boolean time = false;
		timeweatherandfog.add(0, "");
		timeweatherandfog.add(1, "");
		timeweatherandfog.add(2, "");
		timeweatherandfog.add(3, "");
		// W淇敼5.4
		ArrayList<String> colorTemplateW = new ArrayList();
		colorTemplateW = chineseTemplate2English(templateWithColor, model);
		System.out.println("锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒colorTemplateW:" + colorTemplateW);
		/*
		 * for(int i=0;i<colorTemplateW.size();i++){ int
		 * iP=colorTemplateW.lastIndexOf(":");
		 * colorTemplate.add(colorTemplateW.get(i).substring(0,iP)); }
		 */
		for (int i = 0; i < colorTemplateW.size(); i++) {// 鍘绘帀鏈�鍚庣殑鍒嗗��
			int iP = colorTemplateW.get(i).lastIndexOf(":");
			colorTemplate.add(colorTemplateW.get(i).substring(0, iP));
		}
		System.out.println("锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒colorTemplate:" + colorTemplate);
		addModelToTemplate(model, englishTemplate);
		// String fileName =
		// "C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";//淇敼鐢盇llOWL杞洖

		// saveOWLFile((JenaOWLModel)model,fileName);
		for (int i = 0; i < sceneCount; i++) {
			CoutModelPlacable(sceneList.get(i).sceneName, i, model, englishTemplate, colorTemplate);
		}
		if (englishTemplate.size() != 0) {
			////////////////////////////////// 灏嗕腑鏂囨ā鏉跨炕璇戞垚鑻辨枃///////////////////////////////////////////////

			// englishTemplate = chineseTemplate2English(templateAttr, model);
			for (Iterator<String> ist = englishTemplate.iterator(); ist.hasNext();) {
				String tempWord = ist.next();
				if (tempWord.contains("Time") || tempWord.contains("DayTemplate")) {
					time = true;
				}
				if (tempWord.contains("EveningNightTemplate"))
					timeweatherandfog.set(0, (String) "Evening");// 鍌嶆櫄
				else if (tempWord.contains("LateNightTemplate"))
					timeweatherandfog.set(0, (String) "Night");// 娣卞
				else if (tempWord.contains("MorningTemplate"))
					timeweatherandfog.set(0, (String) "EarlyMorning");// 娓呮櫒
				else if (tempWord.contains("NightTemplate"))
					timeweatherandfog.set(0, (String) "Night");// 鏅氫笂锛屾繁澶滄垨鑰呭倣鏅氶兘鍙紝鍙鏄櫄涓婂氨琛�
				else if (tempWord.contains("daybreakTemplate"))
					timeweatherandfog.set(0, (String) "EarlyMorning");
				else if (tempWord.contains("forenoonTemplate"))
					timeweatherandfog.set(0, (String) "Morning");
				else if (tempWord.contains("noonTemplate"))
					timeweatherandfog.set(0, (String) "Noon");
				else if (tempWord.contains("afternoonTemplate"))
					timeweatherandfog.set(0, (String) "Afternoon");
			}
			for (Iterator<String> ist = englishTemplate.iterator(); ist.hasNext();) {
				String tempWord = ist.next();
				if (tempWord.contains("sunshineTemplate"))
					timeweatherandfog.set(1, (String) "Sunshine");// 鏅�
				else if (tempWord.contains("cloudyTemplate"))
					timeweatherandfog.set(1, (String) "Overcast");// 澶氫簯

			}
			for (Iterator<String> ist = englishTemplate.iterator(); ist.hasNext();) {
				String tempWord = ist.next();
				if (tempWord.contains("sunshineTemplate"))
					timeweatherandfog.set(2, (String) "NoCloud");// 鏅�
				else if (tempWord.contains("cloudyTemplate"))
					timeweatherandfog.set(2, (String) "Cloudy");// 澶氫簯
			}
			for (Iterator<String> ist = englishTemplate.iterator(); ist.hasNext();) {
				String tempWord = ist.next();
				if (tempWord.contains("strongFogTemplate"))
					timeweatherandfog.set(3, (String) "Heavyfog");// 澶ч浘
				else if (tempWord.contains("fogTemplate"))
					timeweatherandfog.set(3, (String) "Lightfog");// 娣￠浘
			}

			for (int i = 0; i < timeweatherandfog.size(); i++) {
				// System.out.println
				String temp = timeweatherandfog.get(i);
				timeweatherandfog1.add(i, temp);// 鐢ㄤ簬瀛楀箷鐨勪娇鐢�
			}

			if (time | !(timeweatherandfog.get(0).isEmpty()))// 0 鏃堕棿,Time鏃堕棿妯℃澘

				hasTimeTemplate = true;// 鍒嗘瘝
		}
		if (!(timeweatherandfog.get(1).isEmpty()) || // 1銆�2銆�3澶╂皵
				(!timeweatherandfog.get(2).isEmpty()) || (!timeweatherandfog.get(3).isEmpty())) {
			hasWeatherTeplate = true;
		}

		System.out.println("锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒englishTemplate:" + englishTemplate);
		System.out.println("锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒timeweatherandfog:" + timeweatherandfog.size() + timeweatherandfog
				+ hasTimeTemplate + hasWeatherTeplate);
		// colorTemplate = chineseTemplate2English(templateWithColor, model);
		// System.out.println("锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒colorTemplate:"+colorTemplate);
		modelWithColor = colorTemplate2Individual(colorTemplate, colorMark, model);
		System.out.println("锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒modelWithColor:" + modelWithColor);
	}

	public static void CountSceneCaseBitMap(ArrayList<String> topicFromMG, ArrayList<String> topicFromQiu,
			ArrayList<String> topic, ArrayList<String> templateWithColor) {
		sCaseDataUsable.clear();
		int sceneCount = sceneList.size();
		SceneCase temp;
		for (int i = 0; i < sceneCount; i++) {
			temp = sceneList.get(i);
			if (topicFromMG.size() > 0)
				sCaseDataUsable.set(1);
			if (topic.size() > 0)
				sCaseDataUsable.set(2);
			if (temp.ruleReason > 0)
				sCaseDataUsable.set(3);
			if (temp.templateRelated > 0)
				sCaseDataUsable.set(4);
			if (temp.templateModelNum > 0)
				sCaseDataUsable.set(5);
			if (templateWithColor.size() > 0)
				sCaseDataUsable.set(6);
			if (hasWeatherTeplate)
				sCaseDataUsable.set(7);
			if (topicFromQiu.size() > 0)
				sCaseDataUsable.set(8);
			if (hasTimeTemplate) {
				sCaseDataUsable.set(9);
			}
			if (temp.isBackgroundScene > 0)
				sCaseDataUsable.set(10);
		}
	}

	/*
	 * static public void SceneListSort() {
	 * 
	 * for (int i = 0; i < sceneList.size(); i++) {
	 * 
	 * // 淇濊瘉鍓峣+1涓暟鎺掑ソ搴�
	 * 
	 * SceneCase temp = sceneList.get(i); int j; for (j = i; j > 0 &&
	 * sceneList.get(j-1).score < temp.score; j--) { sceneList.set(j,
	 * sceneList.get(j-1)); } sceneList.set(j, temp); } }
	 */
	static public ArrayList<SceneCase> SceneListSort(ArrayList<SceneCase> s) {

		for (int i = 0; i < s.size(); i++) {

			// 淇濊瘉鍓峣+1涓暟鎺掑ソ搴�

			SceneCase temp = s.get(i);
			int j;
			for (j = i; j > 0 && s.get(j - 1).score < temp.score; j--) {
				s.set(j, s.get(j - 1));
			}
			s.set(j, temp);
		}
		return s;
	}

	static double randomtemp = 0;// 鐢ㄤ簬xml杈撳嚭
	// 钂嬪瓱棣�

	public static String SceneSelected() throws SQLException {

		int sceneCount = sceneList.size();

		SceneCase temp;
		ArrayList topicscene = new ArrayList();
		ArrayList templatescene = new ArrayList();
		ArrayList backscene = new ArrayList();
		ArrayList scene = new ArrayList();
		int weather = hasWeatherTeplate ? 1 : 0;
		System.out.println("weather=" + weather);
		for (int i = 0; i < windRainSnowNeedAttr.size(); i++) {

			if (windRainSnowNeedAttr.get(i).isEmpty() == false) {
				System.out.println("windRainSnowNeedAttr.isEmpty()" + windRainSnowNeedAttr.size() + windRainSnowNeedAttr
						+ windRainSnowNeedAttr.isEmpty());
				weather = 1;
			}
		}
		int time = hasTimeTemplate ? 1 : 0;
		double modelNum, cModelNum, templateRelated;
		if (ExpressionList.size() != 0) {
			ifActionOrExpression = true;
		}
		System.out.println("鏄惁鎶藉埌鐩稿簲鐨勫姩浣滄垨琛ㄦ儏锛�" + ifActionOrExpression);
		int k = 0;
		while (k < sceneCount) {
			if (sceneList.get(k).isBackgroundScene == 1.0 && ifActionOrExpression == false && people == true) {// 鍔ㄤ綔涓嶆槸闅忔満娣诲姞鐨�
				sceneList.remove(k);
				sceneCount = sceneList.size();
			} else {
				k++;
			}
		}

		sceneCount = sceneList.size();
		System.out.println("鏈�鍚庤幏寰楃殑鍦烘櫙鏁伴噺涓�" + sceneCount); // 鏈�缁堢殑鍊欓�夊満鏅�
		// 濡傛灉绾┖鐧藉満鏅笉鑳芥坊鍔犱换浣曚笢瑗匡紝鍒欎笉鍔犲叆璁＄畻
		double actionScore = 5;

//TODO 娉ㄩ噴钂嬪瓱棣ㄥ笀濮愮▼搴忥紝姣忔鏇存柊绋嬪簭閮借鎾ら攢娉ㄩ噴
//		sceneselectbyDecisionTree Tree = new sceneselectbyDecisionTree();
//		
//		double leafrate[] = new double[sceneCount];
//		leafrate = Tree.tmain(englishTopicStr, englishTemplate, sceneList, sceneCount, leafrate);
//		
//		for (int i = 0; i < sceneCount; i++) {
//			sceneList.get(i).decisionvalue = leafrate[i];
//		}

		for (int i = 0; i < sceneCount; i++) {
			temp = sceneList.get(i);
			System.out.println(temp.sceneName + "\t" + temp.MGProb + "\t" + temp.IEProb + "\t" + temp.ruleReason + "\t"
					+ temp.templateRelated + "\t" + temp.placableModelNum + "\t" + temp.placableColorModelNum + "\t"
					+ temp.QProb + "\t" + temp.isBackgroundScene);
			modelNum = temp.templateModelNum > 1 ? temp.templateModelNum : 1;
			cModelNum = temp.colorModelNum > 1 ? temp.colorModelNum : 1;
			templateRelated = temp.templateRelated > 1 ? temp.templateRelated : 1;
			templateRelated = Math.round(templateRelated);

			if (sceneCount <= 4)
				temp.decisionvalue = temp.decisionvalue * 80;
			if (sceneCount > 4 && sceneCount <= 9)
				temp.decisionvalue = temp.decisionvalue * 100;
			if (sceneCount > 9)
				temp.decisionvalue = temp.decisionvalue * 150;

			sceneList.get(i).indualScore = temp.decisionvalue + temp.MGProb * 15 + temp.IEProb * 20
					+ temp.ruleReason * 15 + temp.templateRelated * 10 + (temp.placableModelNum) * 10
					+ (temp.placableColorModelNum) * 10 + temp.isWeatherable * weather * 5 + temp.timeable * time * 5
					+ temp.QProb * 10 + temp.ActionScore * 5;

			sceneList.get(i).fullScore = 15 + 15 + 15 + templateRelated * 10 + modelNum * 10 + cModelNum * 10 + 5 + 5
					+ actionScore + 10;

			sceneList.get(i).score = // 姣忎釜鍦烘櫙鐨勬鐜囧��
					sceneList.get(i).indualScore / sceneList.get(i).fullScore;

			System.out.print("sceneScore锛�" + sceneList.get(i).indualScore);
			System.out.print("\t" + sceneList.get(i).fullScore);
			System.out.println("\t" + sceneList.get(i).score);

			if ((temp.MGProb != 0 || temp.IEProb != 0 || temp.ruleReason != 0 || temp.QProb != 0) && temp.score != 0)
				topicscene.add(temp);
			else if (temp.templateRelated != 0 && temp.score != 0)
				templatescene.add(temp);
			else if (temp.isBackgroundScene != 0 && temp.score != 0)
				backscene.add(temp);
		}
		SceneListSort(sceneList);
		SceneListSort(topicscene);
		SceneListSort(templatescene);
		SceneListSort(backscene);

		if (!(topicscene.isEmpty()) && !(templatescene.isEmpty()) && !(backscene.isEmpty())) {
			int randomscore = (int) (Math.random() * 100);
			System.out.println(randomscore);
			if (randomscore < 20 && randomscore >= 0) {
				scene = backscene;
				System.out.println("褰撳墠閫夋嫨鐨勫満鏅槸鑳屾櫙鍦烘櫙");
			}
			if (randomscore < 50 && randomscore >= 20) {
				System.out.println("褰撳墠閫夋嫨鐨勫満鏅拰妯℃澘鐩稿叧");
				scene = templatescene;
			}
			if (randomscore < 100 && randomscore >= 50) {
				System.out.println("褰撳墠閫夋嫨鐨勫満鏅拰涓婚鐩稿叧");
				scene = topicscene;
			}

		}

		else if (!(topicscene.isEmpty()) && !(templatescene.isEmpty()) && (backscene.isEmpty())) {
			int randomscore = (int) (Math.random() * 100);
			System.out.println(randomscore);
			if (randomscore < 40 && randomscore >= 0) {
				System.out.println("褰撳墠閫夋嫨鐨勫満鏅拰妯℃澘鐩稿叧");
				scene = templatescene;
			}
			if (randomscore < 100 && randomscore >= 40) {
				System.out.println("褰撳墠閫夋嫨鐨勫満鏅拰涓婚鐩稿叧");
				scene = topicscene;
			}

		}

		else if (!(topicscene.isEmpty()) && (templatescene.isEmpty()) && !(backscene.isEmpty())) {
			int randomscore = (int) (Math.random() * 100);
			System.out.println(randomscore);

			if (randomscore < 30 && randomscore >= 0) {
				scene = backscene;
				System.out.println("褰撳墠閫夋嫨鐨勫満鏅槸鑳屾櫙鍦烘櫙");
			}

			if (randomscore < 100 && randomscore >= 30) {
				System.out.println("褰撳墠閫夋嫨鐨勫満鏅拰涓婚鐩稿叧");
				scene = topicscene;
			}

		}

		else if ((topicscene.isEmpty()) && !(templatescene.isEmpty()) && !(backscene.isEmpty())) {
			int randomscore = (int) (Math.random() * 100);
			System.out.println(randomscore);
			if (randomscore < 40 && randomscore >= 0) {
				scene = backscene;
				System.out.println("褰撳墠閫夋嫨鐨勫満鏅槸鑳屾櫙鍦烘櫙");
			}
			if (randomscore < 100 && randomscore >= 40) {
				scene = backscene;
				System.out.println("褰撳墠閫夋嫨鐨勫満鏅拰妯℃澘鐩稿叧");
			}

		}

		else if ((topicscene.isEmpty()) && (templatescene.isEmpty()) && !(backscene.isEmpty())) {
			scene = backscene;
			System.out.println("褰撳墠閫夋嫨鐨勫満鏅槸鑳屾櫙鍦烘櫙");
		}

		else if ((topicscene.isEmpty()) && !(templatescene.isEmpty()) && (backscene.isEmpty())) {
			scene = templatescene;
			System.out.println("褰撳墠閫夋嫨鐨勫満鏅拰妯℃澘鐩稿叧");
		}

		else if (!(topicscene.isEmpty()) && (templatescene.isEmpty()) && (backscene.isEmpty())) {
			scene = topicscene;
			System.out.println("褰撳墠閫夋嫨鐨勫満鏅拰涓婚鐩稿叧");
		}

		/**
		 * 瀵规墍鏈夊満鏅墍寰楀垎鏁拌繘琛屽姞鍜岋紝鍋氶殢鏈烘暟閫夋嫨
		 * 
		 * @author WJJ 寰楀埌sceneListScore鍊间负銆恗a1,1,20,ma2,21,40銆�
		 */

		/*
		 * ArrayList<String> sceneListScore=new ArrayList();
		 * 
		 * SceneCase temp2 = null; double tempScore=0; double allfullScore=0;
		 * for(int i=0;i<sceneList.size();i++){ if(sceneList.get(i).score!=0){
		 * temp2=sceneList.get(i); tempScore=allfullScore;
		 * allfullScore=allfullScore+temp2.score;//灏嗘瘡涓満鏅殑鍊煎姞鍜�
		 * sceneListScore.add(temp2.sceneName);
		 * sceneListScore.add(Double.toString((tempScore)));
		 * sceneListScore.add(Double.toString(allfullScore));
		 * System.out.println("allScore="+allfullScore); } }
		 * System.out.println("allScore="+allfullScore);
		 */
		System.out.println("寮�濮嬮�夋嫨鍦烘櫙,鍦烘櫙鐨勯暱搴︿负锛�" + scene.size());
		ArrayList<String> sceneListScore1 = new ArrayList();
		SceneCase temp3 = null;
		double tempScore1 = 0;
		double allfullScore1 = 0;

		for (int ii = 0; ii < scene.size(); ii++) {
			temp3 = (SceneCase) scene.get(ii);
			System.out.println(temp3.score);
			if (temp3.score != 0) {
				tempScore1 = allfullScore1;
				allfullScore1 = allfullScore1 + temp3.score;
				sceneListScore1.add(temp3.sceneName);
				sceneListScore1.add(Double.toString((tempScore1)));
				sceneListScore1.add(Double.toString(allfullScore1));
				System.out.println("allScore1=" + allfullScore1);
			}
		}

		/**
		 * 杩涜鍦烘櫙鐨勯�夋嫨
		 */
		if (allfullScore1 > 0) {
			randomtemp = Math.random() * allfullScore1;
			System.out.println("randomtemp 鍦烘櫙鍊�=" + randomtemp);
			for (int j = 0; j < sceneListScore1.size();) {
				double score1 = Double.parseDouble(sceneListScore1.get(j + 1));
				double score2 = Double.parseDouble(sceneListScore1.get(j + 2));
				if (score1 <= randomtemp && randomtemp < score2) {
					System.out.println("鏈�鍚庨�夋嫨鍦烘櫙鍊间负锛�" + sceneListScore1.get(j));
					return (String) sceneListScore1.get(j);// 杩斿洖鍦烘櫙鍚嶇О
				}
				if (j == sceneListScore1.size() && score2 == randomtemp) {
					return (String) sceneListScore1.get(j);
				}
				j = j + 3;
			}
		}
		/*
		 * else{ int m=(int) (Math.random()*(sceneList.size())); return
		 * sceneList.get(m).sceneName; }
		 */

		return "";
	}

	public static void getEnglishTopic(ArrayList<String> topic, OWLModel model) {
		ArrayList<String> englishTopic = new ArrayList();
		OWLNamedClass topicn = model.getOWLNamedClass("Topic");
		OWLDatatypeProperty chineseTopic = model.getOWLDatatypeProperty("chineseName");
		OWLNamedClass cls = null;
		Collection clo = topicn.getSubclasses(true);
		for (Iterator in = clo.iterator(); in.hasNext();) {
			cls = (OWLNamedClass) in.next();
			Object hasvali = cls.getHasValue(chineseTopic);
			for (int i = 0; i < topic.size(); i++) {
				if (hasvali != null && hasvali.toString().equals(topic.get(i))) {
					if (topiclist.size() != 0) {
						boolean topicflage = false;
						for (int k = 0; k < topiclist.size(); k++) {
							if (topiclist.get(k).equals(cls.getBrowserText().toString())) {
								topicflage = true;
								break;
							}

						}
						if (topicflage == false) {
							topiclist.add(cls.getBrowserText().toString());
						}
					} else {
						topiclist.add(cls.getBrowserText().toString());
					}

				}
			}
		}

		System.out.println("topicllist=" + topiclist);

	}

	// 钂嬪瓱棣�0903
	public static void PlotResGenerate(OWLModel model, ArrayList<String> topic, ArrayList<String> templateAttr,
			ArrayList<String> templateWithColor, ArrayList<String> colorMark, ArrayList<String> topicFromMG,
			String strNegType, ArrayList<String> englishTemplatePlot)
			throws OntologyLoadException, SWRLFactoryException, SWRLRuleEngineException, SecurityException, IOException,
			ClassNotFoundException, InstantiationException, IllegalAccessException {
		logger.info("寮�濮嬭繘琛屾儏鑺傝鍒掔殑涓荤▼搴忕殑澶勭悊");
		System.out.println("topic:" + topic);
		System.out.println("templateAttr:" + templateAttr);
		System.out.println("templateWithColor:" + templateWithColor);
		System.out.println("colorMark:" + colorMark);
		System.out.println("topicFromMG:" + topicFromMG);
		System.out.println("strNegType:" + strNegType);
		System.out.println("englishTemplate:" + englishTemplate);
		getEnglishTopic(topic, model);

		if (ProgramEntrance.isMiddleMessage) {
			logger.info("鍙湁涓棿缁撴灉锛岀洿鎺ュ皢鐭俊淇℃伅浠ユ枃瀛楀舰寮忔墦鍏ョ┖鍦烘櫙涓�");
			maName = "empty.ma";
		}
		////////////////////////////////// 澶勭悊褰撲富棰樺拰妯℃澘閮戒负绌烘椂锛岀洿鎺ヨ緭鍑篘othing.ma鏂囦欢,濡傛灉鏈夊涓狽othingScene锛屽垯闅忔満浠庨噷闈㈤�変竴涓猰a鏂囦欢/////////////////////////////////////////////
		else if (maName == "" && topic.size() == 0 && templateAttr.size() == 0 && topicFromMG.size() == 0) {
			OWLNamedClass nothingClass = model.getOWLNamedClass("NothingScene");
			Collection nothingIndividual = nothingClass.getInstances();
			ArrayList<OWLIndividual> nothingIndivi = new ArrayList();
			for (Iterator iNothing = nothingIndividual.iterator(); iNothing.hasNext();) {
				OWLIndividual iindivi = (OWLIndividual) iNothing.next();
				nothingIndivi.add(iindivi);
			}
			Random rand = new Random();
			Date date = new Date();
			rand.setSeed(date.getTime());
			int kk = rand.nextInt(nothingIndivi.size());
			maName = nothingIndivi.get(kk).getBrowserText();
			logger.info("娌℃湁鎶藉埌涓婚鍜屾ā鏉夸俊鎭紝鐩存帴杈撳嚭nothing_0.ma鏂囦欢锛�" + maName);

		}
		// END澶勭悊褰撲富棰樺拰妯℃澘閮戒负绌烘椂锛岀洿鎺ヨ緭鍑篘othing.ma鏂囦欢锛屽鏋滄湁////////////////////////////////////////////////////////////////////
		////////////////////////////////// 澶勭悊褰撲富棰樺拰妯℃澘閮戒负绌烘椂锛岀洿鎺ヨ緭鍑篘othing.ma鏂囦欢,濡傛灉鏈夊涓狽othingScene锛屽垯闅忔満浠庨噷闈㈤�変竴涓猰a鏂囦欢/////////////////////////////////////////////
		if (true) {
			//////////////////////// 缁欐墍鏈夋病鏈夊疄渚嬬殑涓婚鍒涘缓涓�涓疄渚�

			OWLNamedClass topicClass = model.getOWLNamedClass("Topic");
			Collection topicList1 = topicClass.getSubclasses(true);
			OWLIndividual individual = null;
			for (Iterator itTopic = topicList1.iterator(); itTopic.hasNext();) {
				OWLNamedClass classOne = (OWLNamedClass) itTopic.next();

				if (classOne.getSubclassCount() == 0) {
					if (classOne.getInstanceCount() == 0) {
						String individualName = classOne.getBrowserText() + "Individual";
						individual = classOne.createOWLIndividual(individualName);
						System.out.println("锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒锛侊紒individualName:" + individualName);
					}
				}
			}
			// END//////////////缁欐墍鏈夋病鏈夊疄渚嬬殑涓婚鍒涘缓涓�涓疄渚�
			logger.info("鐩墠鎵�閫夌殑maName涓猴細" + maName);
			if (maName == "")
				maName = "empty.ma";
			// maName="Bridge04.ma"; 钂嬪瓱棣�0903
			logger.info("鐩墠鎵�閫夌殑maName涓猴細" + maName);
			OWLModel owlModel = null;
			if (maName.contains("Plot.ma")) {
				// 鐢熸垚ma鏂囦欢鍜宮a涓殑space鍊�

				PlotDesign p = new PlotDesign();

				p.GenerateMa(maName, model);

				System.out.println("plot 涓殑topic:" + maName.substring(0, maName.length() - 3));

				// System.out.println("plot 涓殑topic:"+plotTopic);

				owlModel = new PlotAddModelToMa().processSWRL2(maName, maName.substring(0, maName.length() - 3), model,
						englishTemplate, englishTemplatePlot);// 鑾峰緱plot涓殑妯″瀷銆傛斁杩沷ntology涓弿杩�
				// owlModel=new
				// PlotAddModelToMa().processSWRL2(maName,plotTopic,model,englishTemplate,englishTemplatePlot);//鑾峰緱plot涓殑妯″瀷銆傛斁杩沷ntology涓弿杩�

				String fileName = "C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";// 淇敼鐢盇llOWL杞洖

				saveOWLFile((JenaOWLModel) model, fileName);
				String url = "C:/ontologyOWL/AllOwlFile/sumoOWL2";
				CopyFile c = new CopyFile();
				c.createFile(url);
			} else {
				System.out.println(englishTopicStr);
				// addModelToTemplate(model,englishTemplate);
				owlModel = processSWRL(maName, englishTopicStr, model, englishTemplate);
				owlModel = perProcessBeforePrint(maName, model, topictemplate);
				String fileName = "C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";// 14.5.4
																						// 鏃ヤ慨鏀圭敱AllOWL杞洖
				saveOWLFile((JenaOWLModel) owlModel, fileName);
			}
			logger.info("record SceneName " + maName);
			FileWriter SceneRec = new FileWriter("C:/ontologyOWL/SceneRecord.txt", true);
			SceneRec.write(maName + " ");
			SceneRec.close();
			printToXML(owlModel, maName, englishTopicStr, strNegType);

		}
	}

	// 钂嬪瓱棣�
	public static void PrintSceneCase() {
		int njnum = 0;
		double allfullScore1 = 0;
		String SceneResPath = XMLInfoFromIEDom4j.writeXML("SceneCasePath.xml");
		Document doc = XMLInfoFromIEDom4j.readXMLFile(SceneResPath);// 鑾峰緱瑕佽緭鍑虹殑XML鏂囦欢鐨勫ご閮�
		colorModelNum = 0;
		colorChangeAttr.clear();
		Element rootName = doc.getRootElement();

		Element SceneCondition = rootName.addElement("SceneOptionCondition");
		String allScene = "";
		for (int i = 0; i < sceneList.size(); i++) {
			if (sceneList.get(i).score != 0) {
				njnum = njnum + 1;
				allScene += sceneList.get(i).sceneName;
				allScene += ";";
				allfullScore1 = allfullScore1 + sceneList.get(i).score;
			}
		}
		SceneCondition.addAttribute("SceneNum", String.valueOf(njnum));
		SceneCondition.addAttribute("AllOption", allScene);
		SceneCondition.addAttribute("allFullScore", Double.toString(allfullScore1));// 灏嗘�诲垎鍊煎啓鍏ュ満鏅�
		SceneCondition.addAttribute("SelectedScore", Double.toString(randomtemp));
		// 娣诲姞鏄剧ず鏉冨�肩殑灞炴��
		Element WeightCondition = rootName.addElement("Weight");

		WeightCondition.addAttribute("DTree", "30");
		WeightCondition.addAttribute("MGTopic", "15");
		WeightCondition.addAttribute("IETopic", "15");
		WeightCondition.addAttribute("RuleTopic", "15");
		WeightCondition.addAttribute("TemplateRelated", "10");
		WeightCondition.addAttribute("PlacableModel", "10");
		WeightCondition.addAttribute("PlacableColorModel", "10");
		WeightCondition.addAttribute("Weather", "5");
		WeightCondition.addAttribute("Time", "5");
		WeightCondition.addAttribute("Action", "5");
		WeightCondition.addAttribute("QTopic", "10");
		// WeightCondition.addAttribute("BackGroundScene", "5");

		Element ScoreCondition = rootName.addElement("ScoreOptionCondition");

		if (sCaseDataUsable.get(1))
			ScoreCondition.addAttribute("MGTopic", "1");
		else
			ScoreCondition.addAttribute("MGTopic", "0");
		if (sCaseDataUsable.get(2))
			ScoreCondition.addAttribute("IETopic", "1");
		else
			ScoreCondition.addAttribute("IETopic", "0");
		if (sCaseDataUsable.get(3))
			ScoreCondition.addAttribute("RuleTopic", "1");
		else
			ScoreCondition.addAttribute("RuleTopic", "0");
		if (sCaseDataUsable.get(4))
			ScoreCondition.addAttribute("TemplateRelated", "1");
		else
			ScoreCondition.addAttribute("TemplateRelated", "0");
		if (sCaseDataUsable.get(5))
			ScoreCondition.addAttribute("PlacableModel", "1");
		else
			ScoreCondition.addAttribute("PlacableModel", "0");
		if (sCaseDataUsable.get(6))
			ScoreCondition.addAttribute("PlacableColorModel", "1");
		else
			ScoreCondition.addAttribute("PlacableColorModel", "0");
		if (sCaseDataUsable.get(7) | !windRainSnowNeedAttr.get(0).isEmpty() | !windRainSnowNeedAttr.get(1).isEmpty()
				| !windRainSnowNeedAttr.get(2).isEmpty())
			ScoreCondition.addAttribute("Weather", "1");
		else
			ScoreCondition.addAttribute("Weather", "0");
		if (sCaseDataUsable.get(8))
			ScoreCondition.addAttribute("QTopic", "1");
		else
			ScoreCondition.addAttribute("QTopic", "0");
		if (sCaseDataUsable.get(9))
			ScoreCondition.addAttribute("Time", "1");
		else
			ScoreCondition.addAttribute("Time", "0");
		if (sCaseDataUsable.get(10))
			ScoreCondition.addAttribute("Action", "1");
		else
			ScoreCondition.addAttribute("Action", "0");

		double allFullScore = 0;// 鍦烘櫙寰楀垎鍜�
		double tempScore = 0;
		double actionAttr = 0;

		for (int i = 0; i < sceneList.size(); i++) {

			if (sceneList.get(i).score != 0) {
				float weather = hasWeatherTeplate ? 1 : 0;
				for (int j = 0; j < windRainSnowNeedAttr.size(); j++)
					if (!windRainSnowNeedAttr.get(j).isEmpty()) {
						weather = 1;
					}
				if (!actionTemplateAttr.isEmpty() || sceneList.get(i).ActionScore > 0) {
					actionAttr = 1;
				}
				float time = hasTimeTemplate ? 1 : 0;

				Element scene = ScoreCondition.addElement(sceneList.get(i).sceneName);

				// Element decisionvalue =
				// ScoreCondition.addElement(Double.toString(sceneList.get(i).decisionvalue));

				/****** 钂嬫ⅵ棣� ******/
				Element decisionvalue = scene.addElement("decisionvalue");
				decisionvalue.addAttribute("decisionvalue", Double.toString(sceneList.get(i).decisionvalue));
				/****** 钂嬫ⅵ棣� ******/

				Element MGTopic = scene.addElement("MGTopic");
				MGTopic.addAttribute("MGTopic", Double.toString(sceneList.get(i).MGProb));
				Element IETopic = scene.addElement("IETopic");
				IETopic.addAttribute("IETopic", Double.toString(sceneList.get(i).IEProb));
				Element RuleTopic = scene.addElement("RuleTopic");
				RuleTopic.addAttribute("RuleTopic", Double.toString(sceneList.get(i).ruleReason));
				Element TemplateRelated = scene.addElement("TemplateRelated");
				TemplateRelated.addAttribute("TemplateRelated", Double.toString(sceneList.get(i).templateRelated));
				Element PlacableModel = scene.addElement("PlacableModel");
				PlacableModel.addAttribute("PlacableModel", Double.toString(sceneList.get(i).placableModelNum) + "/"
						+ Double.toString(sceneList.get(i).templateModelNum));
				Element PlacableColorModel = scene.addElement("PlacableColorModel");
				PlacableColorModel.addAttribute("PlacableColorModel",
						Double.toString(sceneList.get(i).placableColorModelNum) + "/"
								+ Double.toString(sceneList.get(i).colorModelNum));
				Element Weather = scene.addElement("Weather");
				Weather.addAttribute("Weather", Double.toString(sceneList.get(i).isWeatherable) + "/" + weather);
				Element Time = scene.addElement("Time");
				Time.addAttribute("Time", Double.toString(sceneList.get(i).timeable) + "/" + time);
				Element QProb = scene.addElement("QProb");
				QProb.addAttribute("Probably", Double.toString(sceneList.get(i).QProb));
				Element Action = scene.addElement("Action");
				Action.addAttribute("Action", Double.toString(sceneList.get(i).ActionScore) + "/" + actionAttr);

				// double fscore=sceneList.get(i).MGProb*15+
				Element indualScore = scene.addElement("IndualScore");
				indualScore.addAttribute("IndualScore", Double.toString(sceneList.get(i).indualScore));
				Element FullScore = scene.addElement("FullScore");
				FullScore.addAttribute("FullScore", Double.toString(sceneList.get(i).fullScore));
				Element Probably = scene.addElement("Probably");
				Probably.addAttribute("Probably", Double.toString(sceneList.get(i).score));

				Element ScoreSegment = scene.addElement("ScoreSegment");// 鍒嗗�兼
				double allFullScoretemp = sceneList.get(i).score;
				allFullScore = tempScore + allFullScoretemp;

				ScoreSegment.addAttribute("ScoreSegment",
						"[" + (Double.toString(tempScore)) + "," + Double.toString(allFullScore) + ")");
				tempScore = allFullScore;
			}
		}
		boolean yesNo = XMLInfoFromIEDom4j.doc2XmlFile(doc, SceneResPath);
	}

	

	public static int addModelFromTopic(OWLModel model, String maName, String topicName, int count)
			throws SWRLRuleEngineException {
		System.out.println("count=" + count);
		if (maName.startsWith("nothing") || maName.startsWith("empty.ma")) {
			return count;
		} else {
			logger.info("涓庝富棰樺尮閰嶅瀷");
			OWLObjectProperty hasModelFromTopicProperty = model.getOWLObjectProperty("hasModelFromTopic");
			OWLObjectProperty hasSceneSpaceProperty = model.getOWLObjectProperty("hasSceneSpace");
			OWLObjectProperty hasPutObjectInSpaceProperty = model.getOWLObjectProperty("hasPutObjectInSpace");
			OWLIndividual maIndividual = model.getOWLIndividual(maName);
			OWLNamedClass englishTopicClass = model.getOWLNamedClass(topicName);
			if (englishTopicClass.getInstanceCount() != 0) {
				Collection individualList = englishTopicClass.getInstances();
				OWLIndividual topicIndividualValue = null;
				ArrayList addModelToSpaceList = new ArrayList();
				if (individualList.size() > 0) {
					Iterator its = individualList.iterator();
					while (its.hasNext()) {
						topicIndividualValue = (OWLIndividual) its.next();
						if (topicIndividualValue.getPropertyValueCount(hasModelFromTopicProperty) > 0) {
							SWRLMethod.addModelFromTopicToScene(model, topicName);
							Collection col = topicIndividualValue.getPropertyValues(hasModelFromTopicProperty);
							for (Iterator i = col.iterator(); i.hasNext();) {
								OWLIndividual olw = (OWLIndividual) i.next();
								addModelToSpaceList.add(olw);
							}
						}
					}

				}
				// System.out.println("鐢变富棰樺緱鍒扮殑妯″瀷:"+addModelToSpaceList);
				boolean flage = false;
				if (maIndividual.getPropertyValueCount(hasSceneSpaceProperty) > 0) {
					Collection cols = maIndividual.getPropertyValues(hasSceneSpaceProperty);
					for (Iterator is = cols.iterator(); is.hasNext();) {
						OWLIndividual ind = (OWLIndividual) is.next();
						if (ind.getPropertyValueCount(hasPutObjectInSpaceProperty) > 0) {
							Collection cla = ind.getPropertyValues(hasPutObjectInSpaceProperty);

							for (Iterator it = cla.iterator(); it.hasNext();) {
								OWLIndividual spaceIndividual = (OWLIndividual) it.next();

								ArrayList arr = new ArrayList();
								for (int ig = 0; ig < addModelToSpaceList.size(); ig++) {

									if (addModelToSpaceList.get(ig).equals(spaceIndividual)) {
										arr.add(addModelToSpaceList.get(ig));
									}

								}
								System.out.println("涓婚寰楀埌鐨勬ā鍨嬪彲浠ユ斁鍒板彲鐢ㄧ┖闂寸殑鏄細" + ind.getBrowserText());
								count = setNumberToAddModel(new ArrayList(), arr, model, ind, count, topicName);
							}

						}
					}
				}

			}
		}
		return count;
	}

	/**
	 * 閫氳繃鏋氫妇绫绘潵缁欐墍閫夊満鏅殑绌洪棿娣诲姞妯″瀷锛� 浣垮満鏅湁澶氬彉鎬ф晥鏋滐紝涓庢ā鏉垮拰涓婚閮芥棤鍏�
	 * 
	 * @param model
	 * @param maName
	 * @return
	 */

	@SuppressWarnings("unchecked")
	public static OWLModel addModelFromEnumerateClass(OWLModel model, String maName, int count) {
		System.out.println("count=" + count);
		if (maName.startsWith("nothing") || maName.startsWith("empty.ma")) {
			return model;
		} else {
			logger.info("AxiomClass寰楀埌鐨勬ā鍨�");
			OWLObjectProperty hasSceneSpaceProperty = model.getOWLObjectProperty("hasSceneSpace");
			OWLObjectProperty hasUsableModelProperty = model.getOWLObjectProperty("hasUsableModel");
			OWLIndividual maIndividual = model.getOWLIndividual(maName);
			// 鑾峰緱鎵�鏈夌殑space
			Collection maSpaces = maIndividual.getPropertyValues(hasSceneSpaceProperty);
			Iterator its = maSpaces.iterator();
			OWLEnumeratedClass enumeratedClass = null;
			while (its.hasNext()) {
				boolean isHasEnumClass = false;
				ArrayList<OWLIndividual> allIndividualCollection = new ArrayList();
				OWLIndividual spaceIndividual = (OWLIndividual) its.next();

				OWLNamedClass spaceClass = model.getOWLNamedClass("AxiomClass");
				Collection subSpaceClassList = spaceClass.getSubclasses(true);
				Iterator spaceIts = subSpaceClassList.iterator();
				RDFSClass subSpaceClass = null;
				loop: while (spaceIts.hasNext()) {
					subSpaceClass = (RDFSClass) spaceIts.next();
					String strSubSpaceClass = subSpaceClass.getBrowserText();
					if (strSubSpaceClass.startsWith("Axiom")) {
						Collection enumeList = subSpaceClass.getEquivalentClasses();

						Iterator enumeListIts = enumeList.iterator();
						while (enumeListIts.hasNext()) {
							enumeratedClass = (OWLEnumeratedClass) enumeListIts.next();

							Collection enumOneOfList = enumeratedClass.getOneOfValues();
							Iterator enumOneOfListIts = enumOneOfList.iterator();
							while (enumOneOfListIts.hasNext()) {
								OWLIndividual oneOfIndividual = (OWLIndividual) enumOneOfListIts.next();
								if (oneOfIndividual.getBrowserText().equals(spaceIndividual.getBrowserText())) {
									isHasEnumClass = true;
									System.out.println("subSpaceClass:" + subSpaceClass.getBrowserText());
									// System.out.println("enumeratedClass:"+enumeratedClass.getBrowserText());
									break loop;
								}
							}

						}

					}

				}
				if (isHasEnumClass) {
					OWLNamedClass aa = (OWLNamedClass) subSpaceClass;
					int random = 1;
					if (aa.getBrowserText().toString().contains("Air")) {
						random = (int) Math.random();
					}
					if (random == 1) {
						RDFResource hasUsableModelClass = aa.getAllValuesFrom(hasUsableModelProperty);
						String strHasUsableModelClass = hasUsableModelClass.getBrowserText();
						String[] strHasUsableModelClassSplit = strHasUsableModelClass.split(" or ");
						for (int i = 0; i < strHasUsableModelClassSplit.length; i++) {
							String modelClassStr = strHasUsableModelClassSplit[i].trim();
							System.out.println("modelClassStr: " + modelClassStr);
							OWLNamedClass modelClass = model.getOWLNamedClass(modelClassStr.trim());
							// modelClass.getins
							if (modelClass != null) {
								Collection modelClassIndividuals = modelClass.getInstances(true);
								if (modelClassIndividuals.size() > 0) {
									allIndividualCollection.addAll(modelClassIndividuals);
									System.out.print("ok");
								}
							} // if modelClss缁撴潫
						}
						int kk = 0;
						if (allIndividualCollection.size() > 5)
							kk = 5;
						else
							kk = allIndividualCollection.size();
						Random rand = new Random();
						int kkk = rand.nextInt(kk);
						System.out.println("kkk:" + kkk);
						HashSet<Integer> set = new HashSet<Integer>();// 鍦ㄦ�荤殑瑙勫垯涓殢鏈洪�夌潃kk鏉¤鍒�
						for (int i = 0; i <= kkk; i++) {
							int t = (int) (Math.random() * allIndividualCollection.size());
							set.add(t);
						}
						ArrayList<OWLIndividual> addModelToSpaceList = new ArrayList();
						Iterator iterator = set.iterator();
						while (iterator.hasNext()) {
							Integer num = (Integer) iterator.next();
							if (allIndividualCollection.get(num).getBrowserText().contains(".ma")) {
								// 鍒ゆ柇妯″瀷鏄惁灞炰簬瀹ゅ唴瀹ゅ
								boolean flage = isOwdToMa(model, maName, allIndividualCollection.get(num));
								System.out.println(flage);
								if (flage == true) {
									addModelToSpaceList.add(allIndividualCollection.get(num));
									System.out
											.println("add model:" + allIndividualCollection.get(num).getBrowserText());
								}

							}

						}
						count = setNumberToAddModel(new ArrayList(), addModelToSpaceList, model, spaceIndividual, count,
								"");
					}
				}

			}
		}
		return model;
	}

	/**
	 * 閫氳繃鍥炴函鍙栧緱ma鍦烘櫙锛屽綋鎵�閫夌殑鑳屾櫙鍦烘櫙娌℃湁鎵�闇�鐨勭┖闂存椂锛屽垯閫夋嫨鍏朵粬鐨勫満鏅�
	 * 
	 * @param model
	 * @param maName
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getMaThroughBack(OWLModel model, String maName) {
		// usedSpaceInMa
		String maName1 = "";
		OWLObjectProperty usedSpaceInMaProperty = model.getOWLObjectProperty("usedSpaceInMa");
		OWLIndividual maIndividual = model.getOWLIndividual(maName);
		OWLNamedClass fatherClass = getClassFromIndividual(model, maIndividual);
		OWLNamedClass backGroundClass = model.getOWLNamedClass("BackgroundScene");
		Collection individualList = backGroundClass.getInstances(true);// 鑾峰緱绫讳笅鐨勬墍鏈夊疄渚�

		Collection parentClassList = fatherClass.getSuperclasses(true);// 鑾峰緱绫荤殑鎵�鏈夌埗绫�
		Iterator<OWLNamedClass> its = parentClassList.iterator();
		boolean isBackground = false;
		while (its.hasNext()) {
			OWLNamedClass ppClass = its.next();
			if (ppClass.getBrowserText().equals("BackgroundScene")) {
				isBackground = true;
				break;
			}
		}

		// s鎵�閫夌殑鑳屾櫙鍦烘櫙鎵�鐢ㄧ殑鍦烘櫙绌洪棿涓�0
		Collection spaceList = maIndividual.getPropertyValues(usedSpaceInMaProperty);
		if (isBackground) {
			logger.info("鎵�閫夌殑鍦烘櫙涓鸿儗鏅満鏅�");
			ArrayList<OWLIndividual> individualList2 = new ArrayList();
			if (spaceList.size() == 0 && individualList.size() > 1) {
				logger.info("鎵�閫夌殑鑳屾櫙鍦烘櫙  " + maName + "  娌℃湁閫傜敤鐨勫彲鏀剧墿绌洪棿锛屽苟鏈夊叾浠栧厔寮熷疄渚�");
				Iterator<OWLIndividual> its1 = individualList.iterator();
				while (its1.hasNext()) {
					OWLIndividual individualP = its1.next();
					Collection usedSpaceList1 = individualP.getPropertyValues(usedSpaceInMaProperty);
					if (!individualP.getBrowserText().equals(maName) && usedSpaceList1.size() > 0) {
						individualList2.add(individualP);
					}
				}
				if (individualList2.size() > 0) {
					logger.info("鍏勫紵瀹炰緥鏈夐�傜敤鐨勫彲鏀剧墿绌洪棿");
					Random rand = new Random();
					int k = rand.nextInt(individualList2.size());
					maName1 = individualList2.get(k).getBrowserText();
					return maName1;
				} else {
					logger.info("铏界劧鏈夊厔寮熷疄渚嬶紝浣嗗厔寮熷疄渚嬩篃娌℃湁閫傜敤鐨勫彲鏀剧墿绌洪棿锛屽垯閫夋嫨绌哄満鏅�");
					return "empty.ma";
				}
			} else if (spaceList.size() == 0 && individualList.size() == 1) {
				logger.info("娌℃湁鍏勫紵瀹炰緥锛屾湰韬張娌℃湁閫傜敤鐨勫彲鏀剧墿绌洪棿锛屽垯閫夋嫨绌哄満鏅�");
				return "empty.ma";
			} else {
				logger.info("鎵�閫夌殑鑳屾櫙鍦烘櫙鏈夊彲鐢ㄧ殑绌洪棿");
				return maName;
			}

		} else
			return maName;

	}

	/**
	 * 閫氳繃妯℃澘鍘熷瓙鏉ユ煡鎵炬ā鏉垮師瀛愭墍瀵瑰簲鐨勬ā鍨嬶紙hasModelFromTemplate锛�
	 * 
	 * @param model
	 * @param englishTemplate
	 * @return
	 */
	public static ArrayList<String> getIndividualFromEnglishTemplate(OWLModel model,
			ArrayList<String> englishTemplate) {
		ArrayList<String> individualList = new ArrayList();
		OWLObjectProperty hasModelFTpProperty = model.getOWLObjectProperty("hasModelFromTemplate");
		for (Iterator<String> its = englishTemplate.iterator(); its.hasNext();)// 閬嶅巻鎵�鏈夌殑妯℃澘鍘熷瓙
		{
			String templateAllName = its.next();
			int iPostion = templateAllName.indexOf(":");
			String[] temp = templateAllName.split(":");

			String templateAutmName = templateAllName.split(":")[temp.length - 1];
			// String templateAutmName1=temp.get();
			if (!templateAllName.equals(templateAutmName)) {
				OWLIndividual templateIndividual = model.getOWLIndividual(templateAutmName);
				if (!templateIndividual.equals(null))// 鏌ョ湅妯℃澘鍘熷瓙鎵�瀵瑰簲鐨勫疄渚嬫槸鍚﹀瓨鍦�
														// if(!templateIndividual.equals(null))//鏌ョ湅妯℃澘鍘熷瓙鎵�瀵瑰簲鐨勫疄渚嬫槸鍚﹀瓨鍦�
				{
					OWLObjectProperty hs = model.getOWLObjectProperty("hasModelFromTemplate");
					int k = templateIndividual.getPropertyValueCount(hs);
					// System.out.println(k);
					int valueNum = templateIndividual.getPropertyValueCount(hasModelFTpProperty);
					if (valueNum > 0)// 瀵瑰簲鐨刴odel鏁伴噺鏄惁澶т簬0
					{
						Collection templateModelVlaues = templateIndividual.getPropertyValues(hasModelFTpProperty);

						for (Iterator<OWLIndividual> its2 = templateModelVlaues.iterator(); its2.hasNext();) {
							String value = its2.next().getBrowserText();

							individualList.add(value);

						}
					}
				} else
					continue;
			}

		}
		HashSet h = new HashSet();
		h.addAll(individualList);
		individualList.clear();
		individualList.addAll(h);
		return individualList;
	}

	/**
	 * 鐢辨ā鏉垮緱鍒板姩鐢诲満鏅�
	 * 
	 * @param model
	 * @param englishTemplate
	 * @return
	 * @throws SWRLRuleEngineException
	 */
	public static String getMaFromTemplate(OWLModel model, ArrayList<String> englishTemplate)
			throws SWRLRuleEngineException {
		String maName = "";
		logger.info("濡傛灉妯℃澘鏈夊搴旂殑鍔ㄧ敾鍦烘櫙銆傚垯閫氳繃妯℃澘閫夋嫨鍔ㄧ敾鍦烘櫙");
		maName = getAnimationSceneFromTemplateUsingSWRL(model, englishTemplate);
		if (maName.equals("") || maName.equals(null)) {
			maName = getAnimationSceneFromTemplate(model, englishTemplate);
			if (maName.equals("") || maName.equals(null)) {
				logger.info("杩愯瑙勫垯锛岀敱妯℃澘鍘熷瓙娌℃湁鎺ㄥ嚭涓婚锛屽垯閫夋嫨鑳屾櫙鍦烘櫙鎴栫┖鍦烘櫙");
				boolean bHasModelFromTemplate = false;
				OWLObjectProperty hasModelFromTemplateProperty = model.getOWLObjectProperty("hasModelFromTemplate");
				OWLObjectProperty hasBackgroundSceneProperty = model.getOWLObjectProperty("hasBackgroundScene");
				for (Iterator<String> its = englishTemplate.iterator(); its.hasNext();)// 閬嶅巻鎵�鏈夌殑妯℃澘鍘熷瓙
				{
					String templateAllName = its.next();
					int iPostion = templateAllName.indexOf(":");
					String templateAutmName = templateAllName.substring(iPostion + 1, templateAllName.length());

					OWLIndividual templateIndividual = model.getOWLIndividual(templateAutmName);
					if (!templateIndividual.equals(null))// 鏌ョ湅妯℃澘鍘熷瓙鎵�瀵瑰簲鐨勫疄渚嬫槸鍚﹀瓨鍦�
					{
						int num = templateIndividual.getPropertyValueCount(hasModelFromTemplateProperty);
						if (num > 0) {
							bHasModelFromTemplate = true;
							break;
						}

					}
				}
				if (bHasModelFromTemplate)// 褰撴ā鏉垮師瀛愭湁瀵瑰簲鐨勬ā鍨嬫椂锛屽垯閫夋嫨鑳屾櫙鍦烘櫙
				{
					OWLNamedClass backgroundClass = null;
					ArrayList<String> BackgroundSceneName = new ArrayList();// 鐢ㄦ潵淇濆瓨姣忔鐭俊鎵�鎶藉埌妯℃澘鎵�瀵瑰簲鐨勬墍鏈夎儗鏅満鏅被
					for (Iterator<String> its = englishTemplate.iterator(); its.hasNext();) {
						String templateAllName = its.next();
						int iPostion = templateAllName.indexOf(":");
						String templateTemp = templateAllName.substring(0, iPostion);
						OWLNamedClass templateNClass = model.getOWLNamedClass(templateTemp);//
						logger.info("鑾峰緱妯℃澘鍚�:" + templateNClass.getBrowserText() + "鎵�瀵瑰簲鐨刪asBackgroundScene瀵硅薄灞炴�у��");

						RDFResource resource = templateNClass.getSomeValuesFrom(hasBackgroundSceneProperty);
						if (resource != null)// 鏈変簺妯℃澘鍚嶆病鏈塰asBackgroundScene鐨勫��
						{
							String hasValues = resource.getBrowserText();// 鑾峰緱涓婚瀵瑰簲鐨勯煶涔愮殑绫诲悕
							String[] hasValuesSplit = hasValues.split("or");
							if (hasValuesSplit.length > 1) {// 褰撴湁澶氫釜闊充箰绫绘椂锛屽厛鍒ゆ柇姣忎釜闊充箰绫绘槸鍚﹂兘鏈夊疄渚�
								for (int i = 0; i < hasValuesSplit.length; i++) {
									BackgroundSceneName.add(hasValuesSplit[i].trim());
								}

							} else
								BackgroundSceneName.add(hasValuesSplit[0].trim());
						}
					}
					if (BackgroundSceneName.size() > 1) {
						Random rand = new Random();
						Date date = new Date();
						rand.setSeed(date.getTime());
						int kk = rand.nextInt(BackgroundSceneName.size());
						backgroundClass = model.getOWLNamedClass(BackgroundSceneName.get(kk));
					} else
						backgroundClass = model.getOWLNamedClass(BackgroundSceneName.get(0));
					logger.info("鏈�鍚庨�夌殑鑳屾櫙鍦烘櫙绫绘槸:" + backgroundClass.getBrowserText());
					if (backgroundClass != null) {
						if (backgroundClass.getInstanceCount(false) > 0) {
							ArrayList<OWLIndividual> backgroundIndividualList = new ArrayList();
							Collection backgroundIndividualList1 = backgroundClass.getInstances();
							for (Iterator<OWLIndividual> itt = backgroundIndividualList1.iterator(); itt.hasNext();)
								backgroundIndividualList.add(itt.next());
							Random rand = new Random();
							Date date = new Date();
							rand.setSeed(date.getTime());
							int kk = rand.nextInt(backgroundIndividualList.size());
							OWLIndividual backgroundIndividual = backgroundIndividualList.get(kk);
							maName = backgroundIndividual.getBrowserText();
							bIsBackgroundScene = true;
						} else {
							maName = "Tropical45.ma";
							bIsBackgroundScene = true;
						}
						// OWLIndividual
						// emptyIndividual=model.getOWLIndividual("empty.ma");

					} else
						maName = "empty.ma";

				} else
					maName = "empty.ma";
			}
		}
		return maName;
	}

	/**
	 * 閫氳繃妯℃澘鍒╃敤瑙勫垯鎺ㄥ鍑虹鍚堟ā鏉夸俊鎭殑鍔ㄧ敾鍦烘櫙
	 * 
	 * @param model
	 * @param englishTemplate
	 * @return
	 * @throws SWRLRuleEngineException
	 */
	public static String getAnimationSceneFromTemplateUsingSWRL(OWLModel model, ArrayList<String> englishTemplate)
			throws SWRLRuleEngineException {
		OWLObjectProperty hasAnimationNameFProperty = model.getOWLObjectProperty("hasMaFromTemplateUsingSWRL");// usedSpaceInMa
		ArrayList<OWLIndividual> animationList = new ArrayList();
		Random rand = new Random();
		Iterator its = englishTemplate.iterator();
		while (its.hasNext()) {
			String templateAllName = (String) its.next();
			int iPostion = templateAllName.indexOf(":");
			String templateAutmName = templateAllName.substring(iPostion + 1, templateAllName.length());
			OWLIndividual templateIndividual = model.getOWLIndividual(templateAutmName);
			if (templateIndividual.getPropertyValueCount(hasAnimationNameFProperty) > 0) {
				Collection collection = templateIndividual.getPropertyValues(hasAnimationNameFProperty);
				for (Iterator iValues = collection.iterator(); iValues.hasNext();) {
					OWLIndividual animationIndividual = (OWLIndividual) iValues.next();
					animationList.add(animationIndividual);
				}
			}
		}
		if (animationList.size() > 0) {
			return animationList.get(animationList.size() - 1).getBrowserText();
		}
		return "";
	}

	/**
	 * 褰撲俊鎭娊鍙栨病鏈夋娊鍒颁富棰橈紝妯℃澘涔熸病鏈夋帹鍑轰富棰橈紝鍒欑敱妯℃澘閫氳繃hasAnimationNameFromTemplate
	 * 鐪嬪彲鍚﹀緱鍒扮浉搴旂殑鍔ㄧ敾鍦烘櫙锛堢洿鎺ョ敱灞炴�у�煎緱鍑猴級
	 * 
	 * @param model
	 * @param englishTemplate
	 * @return
	 */

	/**
	 * copy
	 * ma涔嬪墠鍏堝鐞哸ddToMa,exChangedModelInMa灞炴�э紝鍥犱负閫氳繃瑙勫垯鎺ㄥ鍙兘鏌愪釜绫讳笅闈㈡湁澶氫釜瀹炰緥锛岃繖鏍风粡杩囨坊鍔犺鍒欐帹瀵煎悗锛屼細
	 * 鎶婃墍鏈夌殑瀹炰緥閮芥坊鍔犱笂鍘伙紝鑰屽疄闄呬笂鍙鎵�鏈夊疄渚嬩腑鐨勯殢鏈虹殑鏌愪釜
	 * 
	 * @throws SWRLRuleEngineException
	 */
	public static OWLModel perProcessBeforePrint(String maName, OWLModel model, ArrayList<String> englishTemplate1)
			throws SWRLRuleEngineException {
		System.out.println("88888888888888888888maName:" + maName);
		System.out.println("88888888888888888888englishTemplate:" + englishTemplate1);
		int count = 0;
		int sum = 0;
		// ArrayList<String> englishtemplate=new ArrayList<String>();
		String topicName = "";
		for (Iterator in = englishTemplate1.iterator(); in.hasNext();) {
			System.out.println(++sum);
			ArrayList<String> englishtemplate = new ArrayList<String>();
			String str = (String) in.next();
			String[] hasvalue = str.split("-");
			if (topiclist.size() != 0) {
				boolean flage = false;
				for (int l = 0; l < topiclist.size(); l++) {
					topicName = topiclist.get(l);
					if (hasvalue[0].equals(topicName)) {
						flage = true;
						for (int is = 1; is < hasvalue.length; is++) {
							int i = hasvalue[is].indexOf(":");
							englishtemplate.add(hasvalue[is].substring(i + 1));
						}
						break;
					}
				}
				if (flage == false) {
					topicName = "";
					for (int is = 1; is < hasvalue.length; is++) {
						int i = hasvalue[is].indexOf(":");
						englishtemplate.add(hasvalue[is].substring(i + 1));
					}
				}

			} else {
				for (int it = 1; it < hasvalue.length; it++) {
					int k = hasvalue[it].indexOf(":");
					englishtemplate.add(hasvalue[it].substring(k + 1));
				}
			}
			System.out.println("88888888888888888888englishtemplate:" + englishtemplate);
			OWLIndividual maIndividual = model.getOWLIndividual(maName);
			OWLObjectProperty usedSpaceInMaProperty = model.getOWLObjectProperty("usedSpaceInMa");
			OWLObjectProperty hasPutObjectInSpaceProperty = model.getOWLObjectProperty("hasPutObjectInSpace");
			OWLObjectProperty addToMaProperty = model.getOWLObjectProperty("addToMa");
			OWLObjectProperty usedModelInMaProperty = model.getOWLObjectProperty("usedModelInMa");
			OWLObjectProperty exchangedModelInMaProperty = model.getOWLObjectProperty("exchangedModelInMa");
			OWLObjectProperty hasModelProperty = model.getOWLObjectProperty("hasmodel");
			OWLObjectProperty deleteProperty = model.getOWLObjectProperty("subtractFromMa");
			OWLDatatypeProperty topicNameProperty = model.getOWLDatatypeProperty("topicName");

			// 鐢ㄦ潵澶勭悊update瑙勫垯锛屼娇鍦烘櫙涓殑鏌愪釜objet鍙兘琚敼涓烘墍鏈夎閫夋嫨鍙互鏀圭殑瀹炰緥褰撲腑鐨勪竴涓�
			if (maIndividual.getPropertyValueCount(usedModelInMaProperty) > 0)// 鍒ゆ柇usedModelInMa灞炴��
			{
				Collection usedModelValues = maIndividual.getPropertyValues(usedModelInMaProperty);
				for (Iterator iValues = usedModelValues.iterator(); iValues.hasNext();) {
					OWLIndividual usedModelIndividual = (OWLIndividual) iValues.next();
					// 鍒ゆ柇姣忎釜usedModelInMa灞炴�х殑exChangedModelInMa灞炴��
					Collection exChangedModelInMaValues = usedModelIndividual
							.getPropertyValues(exchangedModelInMaProperty);
					ArrayList<OWLIndividual> innerIndividualList = new ArrayList();// 鐢ㄦ潵瀛樻斁姣忎釜space涓婂瓨鏀剧殑鐗╀綋
					// 閫氳繃Map鐨�<key锛寁alue>瀵瑰�兼潵澶勭悊娣诲姞鏌愪釜绫讳笅闈㈢殑澶氫釜瀹炰緥鐨勯棶棰�
					Map<OWLNamedClass, ArrayList<OWLIndividual>> map = new HashMap<OWLNamedClass, ArrayList<OWLIndividual>>();
					for (Iterator iiValues = exChangedModelInMaValues.iterator(); iiValues.hasNext();) {
						OWLIndividual objectIndividual = (OWLIndividual) iiValues.next();
						OWLNamedClass classN = getClassFromIndividual(model, objectIndividual);
						ArrayList<OWLIndividual> values = map.get(classN);
						if (values == null) {
							values = new ArrayList<OWLIndividual>();
							map.put(classN, values);
						}
						values.add(objectIndividual);

					}
					Set<OWLNamedClass> keys = map.keySet();// 閫愪釜澶勭悊姣忎釜绫�
					Object[] choosedClassNums = keys.toArray();
					int length = choosedClassNums.length;
					Random rand = new Random();
					Date date = new Date();
					rand.setSeed(date.getTime());
					int kk = rand.nextInt(length);
					OWLNamedClass classNmae = (OWLNamedClass) choosedClassNums[kk];// 澶氫釜绫婚殢鏈虹殑閫変竴涓�
					ArrayList<OWLIndividual> values2 = map.get(classNmae);// 鑾峰緱exChandedModelInMa涓煇涓被涓嬮潰鐨勬墍鏈夊疄渚�
					int k2 = rand.nextInt(values2.size());
					OWLIndividual changedIndividual = values2.get(k2);
					usedModelIndividual.setPropertyValue(exchangedModelInMaProperty, changedIndividual);

				}
			}
			Collection hasModelList = maIndividual.getPropertyValues(hasModelProperty);

			// ArrayList hasModelList=(ArrayList)hasModelList2.;
			if (maIndividual.getPropertyValueCount(deleteProperty) > 0)// 鍓帀鍒犻櫎瑙勫垯涓殑妯″瀷
			{
				Collection deleteModelList = maIndividual.getPropertyValues(deleteProperty);
				Iterator<OWLIndividual> its = deleteModelList.iterator();
				while (its.hasNext()) {
					ArrayList<OWLIndividual> hasModelList2 = new ArrayList();
					OWLIndividual deleteModel = its.next();
					for (Iterator<OWLIndividual> its2 = hasModelList.iterator(); its2.hasNext();) {
						OWLIndividual hasModelValue = its2.next();
						if (!deleteModel.getBrowserText().equals(hasModelValue.getBrowserText())) {
							// hasModelList.remove(deleteModel);
							// its2.remove();
							hasModelList2.add(hasModelValue);
						}
					}
					hasModelList = hasModelList2;
					hasModelList2 = null;
				}
			}
			// 浠巋asModel涓垹闄ゆ洿鏀硅鍒欎腑鐨勬ā鍨�
			if (maIndividual.getPropertyValueCount(usedModelInMaProperty) > 0) {
				Collection usedModelInMaPropertyList = maIndividual.getPropertyValues(usedModelInMaProperty);
				Iterator<OWLIndividual> its = usedModelInMaPropertyList.iterator();
				while (its.hasNext()) {
					ArrayList<OWLIndividual> hasModelList3 = new ArrayList();
					OWLIndividual deleteModel = its.next();
					for (Iterator<OWLIndividual> its2 = hasModelList.iterator(); its2.hasNext();) {
						OWLIndividual hasModelValue = its2.next();
						if (!deleteModel.getBrowserText().equals(hasModelValue.getBrowserText())) {
							// hasModelList.remove(deleteModel);
							// its2.remove();
							hasModelList3.add(hasModelValue);
						}
					}
					hasModelList = hasModelList3;
					hasModelList3 = null;
				}
			}
			if (maIndividual.getPropertyValueCount(usedModelInMaProperty) > 0
					|| maIndividual.getPropertyValueCount(deleteProperty) > 0)
				maIndividual.setPropertyValues(hasModelProperty, hasModelList);
			ArrayList<OWLIndividual> individualList = new ArrayList();
			ArrayList<OWLNamedClass> classList = new ArrayList();
			ArrayList<String> individualListFromTemplate = getIndividualFromEnglishTemplate(model, englishtemplate);
			System.out.println("qqqqqqqqqqqqqqqqqqqq individualListFromTemplate:" + individualListFromTemplate);
			// @SuppressWarnings("unused")

			String name = maIndividual.getBrowserText();
			System.out.println("maName:" + name);
			// 浠巙sedSpaceInMa鍏ユ墜锛屼富瑕佹洿鏀筧ddTomMa灞炴��
			if (maIndividual.getPropertyValueCount(usedSpaceInMaProperty) > 0) {

				Collection usedSpaceValues = maIndividual.getPropertyValues(usedSpaceInMaProperty);
				for (Iterator iValues = usedSpaceValues.iterator(); iValues.hasNext();) {// iVlaues鏄痵pace鐨勫悕瀛�
					OWLIndividual spaceIndividual = (OWLIndividual) iValues.next();
					Collection objectInSpaceValues = spaceIndividual.getPropertyValues(hasPutObjectInSpaceProperty);
					System.out.println("qqqqqqqqqqqqqqqqqqqq spaceIndividual:" + spaceIndividual);
					System.out.println("qqqqqqqqqqqqqqqqqqqq objectInSpaceValues:" + objectInSpaceValues);

					// objectInSpaceValues鍙互鏀惧叆绌洪棿鐨勬ā鍨嬪垪琛�
					ArrayList<OWLIndividual> innerIndividualList = new ArrayList();// 鐢ㄦ潵瀛樻斁姣忎釜space涓婂瓨鏀剧殑妯″瀷涓綋鐨凮WLIndividual
					ArrayList<OWLIndividual> outterIndividualList = new ArrayList();// 鐢ㄦ潵瀛樻斁涓嶆槸鐢辨ā鏉胯鍒欐帹鍑烘潵鐨勬墍娣诲姞鐨勬ā鍨�

					// 閫氳繃Map鐨�<key锛寁alue>瀵瑰�兼潵澶勭悊娣诲姞鏌愪釜绫讳笅闈㈢殑澶氫釜瀹炰緥鐨勯棶棰�
					Map<OWLNamedClass, ArrayList<OWLIndividual>> map = new HashMap<OWLNamedClass, ArrayList<OWLIndividual>>();

					for (Iterator iiValues = objectInSpaceValues.iterator(); iiValues.hasNext();) {// iiValues姣忎釜space涓婇潰鏀剧殑鐗╀綋
						OWLIndividual objectIndividual = null;
						if (name.equals("empty.ma"))
							objectIndividual = model.getOWLIndividual(iiValues.next().toString());
						else
							objectIndividual = (OWLIndividual) iiValues.next();

						Iterator itd = individualListFromTemplate.iterator();// 妯℃澘瀵瑰簲鐨勬ā鍨嬪垪琛�
						boolean isEqualTemplate = false;
						// 姣斿妯″瀷鍚嶅瓧锛屾槸妯℃澘瀵瑰簲鐨勬ā鍨嬫斁鍏nnerIndividualList锛屼笉鏄垯鏀惧叆
						while (itd.hasNext()) {
							// isEqualTemplate=false;
							// 璺熸ā鏉垮搴旂殑妯″瀷涓�瀹氳鍔犲埌addToMa涓�
							String individualStr = (String) itd.next();
							if (individualStr.equals(objectIndividual.getBrowserText())) {
								innerIndividualList.add(objectIndividual);
								individualList.add(objectIndividual);
								isEqualTemplate = true;
							} else
								continue;
						}

						// 濡傛灉涓嶆槸涓庢ā鏉跨浉鍏崇殑妯″瀷锛屽垯鍔犲埌outterIndividualList
						if (!isEqualTemplate)
							outterIndividualList.add(objectIndividual);
					}
					System.out.println("qqqqqqqqqqqqqqqqqqqq outterIndividualList:" + outterIndividualList);
					HashSet h = new HashSet(innerIndividualList);
					innerIndividualList.clear();
					innerIndividualList.addAll(h);
					System.out.println("qqqqqqqqqqqqqqqqqqqq individualList:" + innerIndividualList);
					System.out.println(topicName);
					count = setNumberToAddModel(innerIndividualList, new ArrayList(), model, spaceIndividual, count,
							topicName);
					System.out.println("qqqqqqqqqqqqqqqqqqqq count:" + count);
				}
				maIndividual.setPropertyValues(addToMaProperty, individualList);
			}
			if (!topicName.equals("")) {
				count = addModelFromTopic(model, maName, topicName, count);
				maIndividual.setPropertyValue(topicNameProperty, topicName);
				System.out
						.println("****************#topicName#####:" + maIndividual.getPropertyValue(topicNameProperty));
			}

		}
		model = addModelFromEnumerateClass(model, maName, count);// axiom绫婚�夋嫨妯″瀷
		return model;
	}

	/**
	 * 缁欐瘡涓猻pace涓婃坊鍔犵殑妯″瀷杩涜缂栧彿, 缂栧彿鍚庡悓鏃舵坊鍔犲埌鈥淎ddModelRelated鈥濈被涓�
	 * 
	 * @param individualList锛氭瘡涓猻pace涓婃墍娣诲姞鐨勬ā鍨�
	 * @param model
	 */
	public static int setNumberToAddModel(ArrayList<OWLIndividual> individualList,
			ArrayList<OWLIndividual> randIndiList, OWLModel model, OWLIndividual spaceIndividual, int count,
			String topicName) {
		System.out.println(topicName);
		OWLNamedClass addModelRelatedClass = model.getOWLNamedClass("AddModelRelated");
		OWLObjectProperty addModelRelatedSpaceProperty = model.getOWLObjectProperty("addModelRelatedSpace");
		OWLObjectProperty hasModelNameProperty = model.getOWLObjectProperty("hasModelName");
		OWLDatatypeProperty isTempObject = model.getOWLDatatypeProperty("isTemplateObject");
		OWLDatatypeProperty modelIDProperty = model.getOWLDatatypeProperty("modelID");
		OWLDatatypeProperty addModelNumberProperty = model.getOWLDatatypeProperty("addModelNumber");
		OWLDatatypeProperty addModelTypeProperty = model.getOWLDatatypeProperty("addModelType");
		OWLDatatypeProperty topicNameProperty = model.getOWLDatatypeProperty("topicName");
		// 20170508 Yangyong
		OWLDatatypeProperty isDeal = model.getOWLDatatypeProperty("isUsed");
		/////////////// end
		Iterator its = individualList.iterator();
		Iterator its2 = randIndiList.iterator();
		while (its.hasNext()) {
			count++;
			OWLIndividual addModelValue = (OWLIndividual) its.next();
			System.out.println("addModelValue:" + addModelValue);
			// OWLDatatypeProperty
			// addModelNumProperty=model.getOWLDatatypeProperty("massNum");
			OWLObjectProperty addModelNumProperty1 = model.getOWLObjectProperty("massScale");
			OWLDatatypeProperty massNumber = model.getOWLDatatypeProperty("massNumber");
			OWLIndividual addNumIndi = (OWLIndividual) addModelValue.getPropertyValue(addModelNumProperty1);
			String addNum = "";
			if (addNumIndi != null) {
				OWLNamedClass massNum = (OWLNamedClass) addNumIndi.getDirectType();
				int maxNumber = massNum.getMaxCardinality(massNumber);
				int minNumber = massNum.getMinCardinality(massNumber);
				if (maxNumber == minNumber) {
					addNum = String.valueOf(minNumber);
				} else {
					Random r = new Random();
					addNum = String.valueOf(r.nextInt(maxNumber - minNumber) + minNumber);
				}

			} else {
				addNum = "1";
			}
			String modelIdStr = "addModelID" + count;
			System.out.println("modelID=" + modelIdStr + "\t" + "addModelNum=" + addNum);
			OWLIndividual addIndividual = addModelRelatedClass.createOWLIndividual(modelIdStr);
			addIndividual.setPropertyValue(modelIDProperty, modelIdStr);

			addIndividual.setPropertyValue(addModelRelatedSpaceProperty, spaceIndividual);
			addIndividual.setPropertyValue(hasModelNameProperty, addModelValue);
			addIndividual.setPropertyValue(isTempObject, "1");
			if (!topicName.equals(""))
				addIndividual.setPropertyValue(topicNameProperty, topicName);
			if (addModelValue.getBrowserText().contains("ParticleEffect"))
				addIndividual.setPropertyValue(addModelTypeProperty, "ParticleEffect");
			else if (addModelValue.getBrowserText().contains(".ma")) {

				OWLNamedClass classN = getClassFromIndividual(model, addModelValue);
				Collection parentClassList = classN.getSuperclasses(true);
				Iterator its1 = parentClassList.iterator();
				boolean isHuman = false;
				while (its1.hasNext()) {
					Object obj = its1.next();
					if (obj.toString().contains("NamedClass")) {
						RDFResource parentClass = (RDFResource) obj;
						String temp = parentClass.getBrowserText();
						if (parentClass.getBrowserText().equals("p1:Woman")
								|| parentClass.getBrowserText().equals("p1:Man")) {
							isHuman = true;
							break;
						}
					}

				}
				if (isHuman) {

					addIndividual.setPropertyValue(addModelTypeProperty, "people");
					addIndividual.setPropertyValue(addModelNumberProperty, "1");
					addIndividual.setPropertyValue(isTempObject, "1");
					// 20170508 Yangyong
					addIndividual.setPropertyValue(isDeal, "false");
					///////// end
				} else {
					addIndividual.setPropertyValue(addModelNumberProperty, addNum);
					addIndividual.setPropertyValue(addModelTypeProperty, "model");
					if (addModelValue.getBrowserText().equalsIgnoreCase("m_floor.ma")) {// 灏嗗湴闈㈣涓�0
						addIndividual.setPropertyValue(isTempObject, "0");
					} else {
						addIndividual.setPropertyValue(isTempObject, "1");
					}
				}
			}
		}

		while (its2.hasNext()) {
			count++;
			OWLIndividual addModelValue = (OWLIndividual) its2.next();
			System.out.println("addModelValue:" + addModelValue);
			// OWLDatatypeProperty
			// addModelNumProperty=model.getOWLDatatypeProperty("massNum");
			// String
			// addNum=(String)addModelValue.getPropertyValue(addModelNumProperty);
			// String
			// addNum=(String)addModelValue.getPropertyValue(addNumProperty);
			OWLObjectProperty addModelNumProperty1 = model.getOWLObjectProperty("massScale");
			OWLDatatypeProperty massNumber = model.getOWLDatatypeProperty("massNumber");
			OWLIndividual addNumIndi = (OWLIndividual) addModelValue.getPropertyValue(addModelNumProperty1);
			String addNum = "";
			if (addNumIndi != null)

			{
				OWLNamedClass massNum = (OWLNamedClass) addNumIndi.getDirectType();
				int maxNumber = massNum.getMaxCardinality(massNumber);
				int minNumber = massNum.getMinCardinality(massNumber);
				if (maxNumber == minNumber) {
					addNum = String.valueOf(minNumber);
				} else {
					Random r = new Random();
					addNum = String.valueOf(r.nextInt(maxNumber - minNumber) + minNumber);
				}

			} else {
				addNum = "1";
			}
			String modelIdStr = "addModelID" + count;
			System.out.println("addModelID=" + count + "\tmodelNum=" + addNum);
			// if(modelIdStr.e)
			OWLIndividual addIndividual = addModelRelatedClass.createOWLIndividual(modelIdStr);

			addIndividual.setPropertyValue(modelIDProperty, modelIdStr);
			addIndividual.setPropertyValue(addModelRelatedSpaceProperty, spaceIndividual);
			addIndividual.setPropertyValue(hasModelNameProperty, addModelValue);
			addIndividual.setPropertyValue(isTempObject, "0");
			if (addModelValue.getBrowserText().contains("ParticleEffect"))
				addIndividual.setPropertyValue(addModelTypeProperty, "ParticleEffect");
			else if (addModelValue.getBrowserText().contains(".ma")) {

				OWLNamedClass classN = getClassFromIndividual(model, addModelValue);
				Collection parentClassList = classN.getSuperclasses(true);
				Iterator its1 = parentClassList.iterator();
				boolean isHuman = false;
				while (its1.hasNext()) {
					Object obj = its1.next();
					if (obj.toString().contains("NamedClass")) {
						RDFResource parentClass = (RDFResource) obj;
						String temp = parentClass.getBrowserText();
						if (parentClass.getBrowserText().equals("p1:Woman")
								|| parentClass.getBrowserText().equals("p1:Man")) {
							isHuman = true;
							break;
						}
					}

				}
				if (isHuman) {
					addIndividual.setPropertyValue(addModelTypeProperty, "people");
					addIndividual.setPropertyValue(addModelNumberProperty, "1");
					addIndividual.setPropertyValue(isTempObject, "0");
					// 20170508 Yangyong
					addIndividual.setPropertyValue(isDeal, "false");
				} else {
					addIndividual.setPropertyValue(addModelNumberProperty, addNum);
					addIndividual.setPropertyValue(addModelTypeProperty, "model");
					addIndividual.setPropertyValue(isTempObject, "0");
				}
			}
		}
		return count;
	}

	/**
	 * 缁欐坊鍔犵殑妯″瀷娣诲姞ID
	 * 
	 * @param individualList:娣诲姞鐨勬ā鍨媗ist
	 * @param model
	 */

	public static void setIDToAddModel(ArrayList<OWLIndividual> individualList, OWLModel model) {
		OWLDatatypeProperty modelIDProperty = model.getOWLDatatypeProperty("modelID");
		Iterator its = individualList.iterator();
		int count = 0;
		while (its.hasNext()) {
			count++;
			OWLIndividual addModelValue = (OWLIndividual) its.next();
			String modelIdStr = "addModelID" + count;
			addModelValue.setPropertyValue(modelIDProperty, modelIdStr);

		}
	}

	/**
	 * 鏍规嵁瑙勫垯鍜屼富棰樼殑鍚嶅瓧鏉ラ�夋嫨杩愯鍝被瑙勫垯
	 * 
	 * @param maName锛歮a鏂囦欢鐨勫悕瀛�
	 * @param topicName锛氫富棰樼殑鍚嶅瓧
	 * @param model
	 * @return
	 * @throws SWRLRuleEngineException
	 * @throws SWRLFactoryException
	 * @throws IOException
	 * @throws SecurityException
	 */

	public static OWLModel processSWRL(String maName, String topicName, OWLModel model,
			ArrayList<String> englishTemplate)
			throws SWRLRuleEngineException, SWRLFactoryException, SecurityException, IOException {

		if (maName == "empty.ma") {
			// 褰撻�夋嫨鐨刴a鏄痚mpty.ma,鏃讹紝棣栧厛鐪嬬湅鏄笉鏄敱妯℃澘鍙互寰�empty.ma涓坊鍔犲搴旂殑妯″瀷
			boolean isOK = SWRLMethod.executeSWRLEnginetoEmptyMa(model, "addModelToEmpty.ma", topicName,
					englishTemplate);
			// 灏嗗師瀛愭墍瀵瑰簲鐨勬ā鍨嬫坊鍔犲埌绌哄満鏅腑
			if (englishTemplate.size() > 0) {
				ArrayList<String> modelValues = new ArrayList();
				ArrayList<String> effectValues = new ArrayList();
				ArrayList<String> allValues = new ArrayList();
				OWLObjectProperty hasModelFTpProperty = model.getOWLObjectProperty("hasModelFromTemplate");
				OWLObjectProperty addToMaProperty = model.getOWLObjectProperty("addToMa");
				OWLObjectProperty hasPutObjectProperty = model.getOWLObjectProperty("hasPutObjectInSpace");
				OWLIndividual emptyIndividual = model.getOWLIndividual("empty.ma");
				OWLIndividual emptyGroundIndividual = model.getOWLIndividual("emptySceneSpaceA");
				OWLIndividual emptyAirIndividual = model.getOWLIndividual("emptySceneSpaceB");
				OWLDatatypeProperty degreeProperty = model.getOWLDatatypeProperty("degree");
				for (Iterator<String> its = englishTemplate.iterator(); its.hasNext();)// 閬嶅巻鎵�鏈夌殑妯℃澘鍘熷瓙
				{
					String templateAllName = its.next();
					int iPostion = templateAllName.indexOf(":");
					// String
					// templateAutmName=templateAllName.substring(iPostion+1,
					// templateAllName.length());
					String templateAutmName = templateAllName.substring(iPostion + 1, templateAllName.length() - 4);
					OWLIndividual templateIndividual = model.getOWLIndividual(templateAutmName);

					if (templateIndividual != null)// 鏌ョ湅妯℃澘鍘熷瓙鎵�瀵瑰簲鐨勫疄渚嬫槸鍚﹀瓨鍦�
													// if(!templateIndividual.equals(null))//鏌ョ湅妯℃澘鍘熷瓙鎵�瀵瑰簲鐨勫疄渚嬫槸鍚﹀瓨鍦�
					{
						int valueNum = templateIndividual.getPropertyValueCount(hasModelFTpProperty);
						if (valueNum > 0)// 瀵瑰簲鐨刴odel鏁伴噺鏄惁澶т簬0
						{
							Collection templateModelVlaues = templateIndividual.getPropertyValues(hasModelFTpProperty);

							for (Iterator<OWLIndividual> its2 = templateModelVlaues.iterator(); its2.hasNext();) {
								String value = its2.next().getBrowserText();
								allValues.add(value);
								if (value.contains(".ma"))
									modelValues.add(value);
								else {
									effectValues.add(value);
									OWLIndividual effectIndividual = model.getOWLIndividual(value);
									effectIndividual.setPropertyValue(degreeProperty, "normal");
								}
							}
						}
					}

				}
				emptyGroundIndividual.setPropertyValues(hasPutObjectProperty, modelValues);
				emptyAirIndividual.setPropertyValues(hasPutObjectProperty, effectValues);
				emptyIndividual.setPropertyValues(addToMaProperty, allValues);

			}

		} else if (maName != "nothing.ma" && maName != "empty.ma") {

			// 娣诲姞hasMa,hasTopic灞炴�у�硷紝涔熷寘鎷窡妯℃澘鍘熷瓙鏈夊叧鐨勮鍒�
			if (bIsBackgroundScene) {
				logger.info("杩愯寰�鑳屾櫙鍦烘櫙涓坊鍔犳ā鍨嬬殑瑙勫垯");
				SWRLMethod.executeTemplateToBackgroundSceneSWRLEngine(model, englishTemplate);
				try {
					try {
						maName = getMaThroughBack(model, maName);
					} catch (Exception exGetBack) {
						maName = getMaThroughBack(model, maName);
					}
				} catch (Exception exGetBack2) {
					System.out.print("ERROR: exGetBack");
				}
			} else {

				SWRLMethod.executeTemplateToBackgroundSceneSWRLEngine(model, englishTemplate);
			}

		}
		if (bIsBackgroundScene) {
			SWRLMethod.changeBackgroundPictureSky(model, maName, englishTemplate);
		}

		return model;

	}

	/**
	 * 杈撳嚭鎽勫儚鏈虹増鏈�2锛�
	 * 
	 * @param doc
	 * @param model
	 * @param copyIndividual
	 * @return
	 */
	public static Document printCameraVersion2(Document doc, OWLModel model, OWLIndividual copyIndividual) {
		Element rootName = doc.getRootElement();
		Element name = rootName.element("maName");
		OWLNamedClass addModelClass = model.getOWLNamedClass("AddModelRelated");
		if (addModelClass.getInstanceCount() == 0)
			return doc;
		else {
			OWLNamedClass parentClassName = getClassFromIndividual(model, copyIndividual);
			Collection parentClassList = parentClassName.getSuperclasses(true);
			Iterator its = parentClassList.iterator();
			boolean isBackgroundScene = false;// first锛氭墍闄愬垽鏂槸鍚︽槸鑳屾櫙鍦烘櫙
			while (its.hasNext()) {
				Object obj = its.next();
				if (obj.toString().contains("NamedClass")) {
					RDFResource parentClass = (RDFResource) obj;
					String temp = parentClass.getBrowserText();
					if (parentClass.getBrowserText().equals("BackgroundScene")) {
						isBackgroundScene = true;
						break;
					}
				}
			}

			OWLDatatypeProperty addModelTypeProperty = model.getOWLDatatypeProperty("addModelType");
			ArrayList<String> allList = new ArrayList();
			ArrayList<String> humanList = new ArrayList();
			Collection addModelList = addModelClass.getInstances();
			Iterator its1 = addModelList.iterator();
			while (its1.hasNext()) {
				OWLIndividual addModel = (OWLIndividual) its1.next();
				allList.add(addModel.getBrowserText());
				if (addModel.getPropertyValue(addModelTypeProperty).toString().equals("people"))
					humanList.add(addModel.getBrowserText());
			}

			if (isBackgroundScene) {
				Element ruleName = name.addElement("rule");
				ruleName.addAttribute("ruleType", "Camera");
				String perName = "name";
				int count = 0;
				for (Iterator its4 = allList.iterator(); its4.hasNext();) {
					count++;
					String modelName = (String) its4.next();
					String fullName = perName + count;
					ruleName.addAttribute(fullName, modelName);
				}

			} else {
				if (humanList.size() > 0) {
					Element ruleName = name.addElement("rule");
					ruleName.addAttribute("ruleType", "Camera");
					String perName = "name";
					int count = 0;
					for (Iterator its4 = humanList.iterator(); its4.hasNext();) {
						count++;
						String modelName = (String) its4.next();
						String fullName = perName + count;
						ruleName.addAttribute(fullName, modelName);
					}
				}
			}
		}

		return doc;
	}

	/**
	 * 杈撳嚭鎽勫儚鏈虹被
	 * 
	 * @param doc
	 * @param model
	 * @param copyIndividual
	 * @return
	 */
	public static Document printCamera(Document doc, OWLModel model, OWLIndividual copyIndividual) {
		Element rootName = doc.getRootElement();
		Element name = rootName.element("maName");
		OWLObjectProperty hasModelProperty = model.getOWLObjectProperty("hasmodel");
		OWLObjectProperty hasRelationProperty = model.getOWLObjectProperty("hasRelation");
		OWLObjectProperty hasHumanProperty = model.getOWLObjectProperty("hasHuman");
		OWLObjectProperty addToMaProperty = model.getOWLObjectProperty("addToMa");
		OWLNamedClass parentClassName = getClassFromIndividual(model, copyIndividual);
		Collection parentClassList = parentClassName.getSuperclasses(true);
		Iterator its = parentClassList.iterator();
		boolean isBackgroundScene = false;// first锛氭墍闄愬垽鏂槸鍚︽槸鑳屾櫙鍦烘櫙
		while (its.hasNext()) {
			Object obj = its.next();
			if (obj.toString().contains("NamedClass")) {
				RDFResource parentClass = (RDFResource) obj;
				String temp = parentClass.getBrowserText();
				if (parentClass.getBrowserText().equals("BackgroundScene")) {
					isBackgroundScene = true;
					break;
				}
			}
		}
		if (isBackgroundScene && copyIndividual.getPropertyValueCount(addToMaProperty) > 0) {// 濡傛灉鏄┖鍦烘櫙锛屽垯鎵撳嵃绌哄満鏅腑addToMa鎵�鏈夌殑鍊�
			Element ruleName = name.addElement("rule");
			ruleName.addAttribute("ruleType", "Camera");
			String perName = "name";
			int count = 0;
			Collection addToMaList = copyIndividual.getPropertyValues(addToMaProperty);
			Iterator its1 = addToMaList.iterator();
			while (its1.hasNext()) {
				count++;
				OWLIndividual individualValue = (OWLIndividual) its1.next();
				String fullName = perName + count;
				ruleName.addAttribute(fullName, individualValue.getBrowserText());
			}
			return doc;

		} else {// 涓嶆槸鑳屾櫙鍦烘櫙鐨勮瘽锛屽垯瑕佽�冭檻hasmodel鍜宎ddToMa閲岄潰鐨勪汉鐗�
			ArrayList<String> humanList = new ArrayList();

			// 鍒ゆ柇娣诲姞灞炴��
			if (copyIndividual.getPropertyValueCount(addToMaProperty) > 0) {
				Collection hasmodelList = copyIndividual.getPropertyValues(addToMaProperty);
				for (Iterator its2 = hasmodelList.iterator(); its2.hasNext();) {
					OWLIndividual indicidualV = (OWLIndividual) its2.next();
					OWLNamedClass parentClassName1 = getClassFromIndividual(model, indicidualV);
					Collection parentClassList1 = parentClassName1.getSuperclasses(true);
					Iterator its3 = parentClassList1.iterator();
					while (its3.hasNext()) {
						Object obj = its3.next();
						if (obj.toString().contains("NamedClass")) {
							RDFResource parentClass = (RDFResource) obj;
							String temp = parentClass.getBrowserText();
							if (parentClass.getBrowserText().equals("p1:Woman")
									|| parentClass.getBrowserText().equals("p1:Man")) {
								humanList.add(indicidualV.getBrowserText());
								break;

							}
						}
					}
				}
			}
			if (humanList.size() > 0) {
				// 鍒ゆ柇hasmodel灞炴��
				if (copyIndividual.getPropertyValueCount(hasRelationProperty) > 0) {
					Collection hasmodelList = copyIndividual.getPropertyValues(hasRelationProperty);
					for (Iterator its2 = hasmodelList.iterator(); its2.hasNext();) {
						OWLIndividual indicidualV = (OWLIndividual) its2.next();
						if (indicidualV.getPropertyValueCount(hasHumanProperty) > 0) {
							Collection hasHumanList = indicidualV.getPropertyValues(hasHumanProperty);
							for (Iterator its3 = hasHumanList.iterator(); its3.hasNext();) {
								OWLIndividual indicidualV2 = (OWLIndividual) its3.next();
								OWLNamedClass parentClassName1 = getClassFromIndividual(model, indicidualV2);
								Collection parentClassList1 = parentClassName1.getSuperclasses(true);
								Iterator its4 = parentClassList.iterator();
								while (its4.hasNext()) {
									Object obj = its4.next();
									if (obj.toString().contains("NamedClass")) {
										RDFResource parentClass = (RDFResource) obj;
										String temp = parentClass.getBrowserText();
										if (parentClass.getBrowserText().equals("p1:Woman")
												|| parentClass.getBrowserText().equals("p1:Man")) {
											humanList.add(indicidualV2.getBrowserText());
											break;

										}
									}
								}
							}
						}

					}
				}

				Element ruleName = name.addElement("rule");
				ruleName.addAttribute("ruleType", "Camera");
				String perName = "name";
				int count = 0;
				for (Iterator its4 = humanList.iterator(); its4.hasNext();) {
					count++;
					String modelName = (String) its4.next();
					String fullName = perName + count;
					ruleName.addAttribute(fullName, modelName);
				}
				return doc;
			}
		}

		return doc;
	}

	/**
	 * 閫氳繃涓婚閫夐煶涔恗usic,
	 * 
	 * @param englishTopic:涓婚
	 * @param model
	 * @param doc
	 * @return
	 * @throws IOException
	 * @throws SecurityException
	 */
	public static String getMusic(String englishTopic, OWLModel model) throws SecurityException, IOException {
		OWLIndividual chooseMusic = null;
		String choosedMusic = "";
		OWLObjectProperty hasMusicProperty = model.getOWLObjectProperty("hasMusic");
		Random rand = new Random();
		Date date = new Date();

		if (englishTopic.equals("") || englishTopic == null)// 娌℃湁閫夊埌涓婚
		{
			OWLNamedClass commonMusicClass = model.getOWLNamedClass("CommonMusic");
			if (commonMusicClass.getInstanceCount() != 0) {
				Collection musicClass = commonMusicClass.getInstances();
				ArrayList<OWLIndividual> musicIndividual = new ArrayList();
				for (Iterator itInstance = musicClass.iterator(); itInstance.hasNext();) {
					OWLIndividual individual1 = (OWLIndividual) itInstance.next();
					musicIndividual.add(individual1);
				}
				rand.setSeed(date.getTime());
				int kk = rand.nextInt(musicIndividual.size());
				chooseMusic = musicIndividual.get(kk);
			}
		} else {
			OWLNamedClass englishTopicClass = model.getOWLNamedClass(englishTopic);
			RDFResource resource = englishTopicClass.getSomeValuesFrom(hasMusicProperty);
			logger.info("閫氳繃涓婚鏉ヨ幏寰椾富棰樻墍瀵瑰簲鐨刴a鍦烘櫙鐨勫悕瀛楋紝璇ヤ富棰樻墍瀵瑰簲鐨勯煶涔愮被鍚嶏細" + resource.getBrowserText());
			String hasValues = resource.getBrowserText();// 鑾峰緱涓婚瀵瑰簲鐨勯煶涔愮殑绫诲悕
			String[] hasValuesSplit = hasValues.split("or");// 鍙兘瀵瑰簲澶氫釜闊充箰绫�
			ArrayList<String> hasValuesClass = new ArrayList();
			OWLNamedClass resourceClass = null;
			if (hasValuesSplit.length > 1) {// 褰撴湁澶氫釜闊充箰绫绘椂锛屽厛鍒ゆ柇姣忎釜闊充箰绫绘槸鍚﹂兘鏈夊疄渚�
				for (int i = 0; i < hasValuesSplit.length; i++) {
					OWLNamedClass resourceClass0 = model.getOWLNamedClass(hasValuesSplit[i].trim());
					int instanceCount0 = resourceClass0.getInstanceCount();
					if (instanceCount0 > 0)
						hasValuesClass.add(hasValuesSplit[i].trim());
				}
				if (hasValuesClass.size() > 0)// 褰撳涓煶涔愮被閮芥湁瀹炰緥鏃讹紝鍒欓殢鏈洪�夋嫨涓�涓�
				{
					rand.setSeed(date.getTime());
					int kk = rand.nextInt(hasValuesClass.size());
					resourceClass = model.getOWLNamedClass(hasValuesClass.get(kk));
				} else// 褰撳涓煶涔愮被閮芥病鏈夊疄渚嬫椂锛屽垯闅忔満閫夋嫨涓�涓暱鍦烘櫙绫�
				{
					int kk = rand.nextInt(hasValuesSplit.length);
					resourceClass = model.getOWLNamedClass(hasValuesSplit[kk].trim());

				}
			} else
				// 澶勭悊鍙湁涓�涓満鏅被鐨勬儏鍐�
				resourceClass = model.getOWLNamedClass(hasValuesSplit[0].trim());
			int instanceCount = resourceClass.getInstanceCount();
			if (instanceCount != 0) {
				Collection musicClass = resourceClass.getInstances();
				ArrayList<OWLIndividual> musicIndividual = new ArrayList();
				for (Iterator itInstance = musicClass.iterator(); itInstance.hasNext();) {
					OWLIndividual individual1 = (OWLIndividual) itInstance.next();
					musicIndividual.add(individual1);
				}
				rand.setSeed(date.getTime());
				int kk = rand.nextInt(musicIndividual.size());
				chooseMusic = musicIndividual.get(kk);
			}
		}
		if (chooseMusic != null)
			choosedMusic = chooseMusic.getBrowserText();
		if (choosedMusic.startsWith("a00"))
			choosedMusic = choosedMusic.substring(1);
		return choosedMusic;
	}

	/**
	 * 鎵撳嵃绫�
	 * 
	 * @param model
	 * @throws IOException
	 * @throws SWRLRuleEngineException
	 * @throws OntologyLoadException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @throws SWRLFactoryException
	 */
	public static void printToXML(OWLModel model, String maName, String topicName, String strNegType)
			throws SWRLRuleEngineException, IOException, OntologyLoadException, ClassNotFoundException,
			InstantiationException, IllegalAccessException, SWRLFactoryException {
		// OWLModel owlModel=copyMaIndividual(maName,model,topicName);
		String xmlPath = XMLInfoFromIEDom4j.writeXML("adl_result.xml");
		Document doc = XMLInfoFromIEDom4j.readXMLFile(xmlPath);// 鑾峰緱瑕佽緭鍑虹殑XML鏂囦欢鐨勫ご閮�
		Element rootElement = doc.getRootElement();
		Element name = rootElement.addElement("maName");
		OWLIndividual copyIndividual = model.getOWLIndividual(maName);
		OWLDatatypeProperty maSenceNameProperty = model.getOWLDatatypeProperty("maSceneName");
		OWLDatatypeProperty topicNameProperty = model.getOWLDatatypeProperty("topicName");

		// String
		// maName=(String)copyIndividual.getPropertyValue(maSenceNameProperty);
		// String
		// topicClassName=(String)copyIndividual.getPropertyValue(topicNameProperty);
		name.addAttribute("name", maName);
		name.addAttribute("topic", topicName);
		String musicName = getMusic(topicName, model);
		name.addAttribute("music", musicName);

		if (maName.equals("nothing.ma"))// 2017.5.31锛涗骇鐢熼敊璇煭淇�8042鈥�8044鍚庢坊鍔�
			name.addAttribute("maFrame", "300");

		// *******************鎵撳嵃娣诲姞瑙勫垯*****************
		OWLObjectProperty addToMaProperty = model.getOWLObjectProperty("addToMa");

		doc = printCycleAddRuleVersion2(doc, model, copyIndividual, englishTemplate, colorModelNum, colorChangeAttr);
		// 鎵撳嵃娣诲姞瑙勫垯
		doc = printExchangeRule(doc, model, copyIndividual);
		doc = printDeleteRule(doc, model, copyIndividual);
		doc = printTimeToClock(doc, model, copyIndividual, englishTemplate);

		// doc=printCameraVersion2(doc,model,copyIndividual);
		// doc=printNegType(doc,strNegType);
		// doc=printBackgroundPicture(doc,model,copyIndividual);

		String individualName = maName;
		/* 鍚勯儴鍒嗘暣鍚堣捣鏉� */
		String uri = "file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";
		// String uri = "file:///C:/ontologyOWL/sumoOWL2/sumo_phone3.owl";
		OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI(uri);
		boolean flage = false;
		boolean flage1 = false;
		boolean flage2 = false;
		boolean ifInterFlage = false;
		ArrayList expressiontopic = new ArrayList();
		expressiontopic = topiclist;

		ArrayList scene = new ArrayList();
		OWLObjectProperty hasSceneSpace = model.getOWLObjectProperty("hasSceneSpace");
		if (copyIndividual.getPropertyValueCount(hasSceneSpace) != 0) {
			if (copyIndividual.getPropertyValueCount(hasSceneSpace) == 1) {
				scene.add(copyIndividual.getPropertyValue(hasSceneSpace));
			} else if (copyIndividual.getPropertyValueCount(hasSceneSpace) > 1) {
				Collection clo = copyIndividual.getPropertyValues(hasSceneSpace);
				for (Iterator it = clo.iterator(); it.hasNext();) {
					scene.add(it.next());
				}
			}
		}
		for (int k = 0; k < scene.size(); k++) {
			OWLIndividual scenespace = (OWLIndividual) scene.get(k);
			OWLNamedClass place = (OWLNamedClass) scenespace.getDirectType();
			String s = place.getBrowserText().toString();
			if (s.contains("Ground")) {
				flage2 = true;
				break;

			}
		}
		if (flage2 == false) {
			System.out.println("娌℃湁鍦伴潰鍙敤绌洪棿");
		}

		try {
			logger.info("Expressionbegin");
			Expression shock = new Expression();
			System.out.println("XUXH闇�瑕佸疄渚嬶細" + individualName + "妯℃澘鍚嶇О锛�" + ExpressionList);
			System.out.println(topiclist.size());
			doc = shock.ShockXml(ExpressionList, expressiontopic, model, maName, doc);
			logger.info("Expression finish");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("ERROR: Expression Exception");
		}
		// 20170508 Yangyong

		if (!maName.equals("clock.ma") && flage2 == true) {
			try {
				logger.info("InterAction begin");
				InterAction interaction = new InterAction();
				String interflage = interaction.hasInterActionInfer(actionTemplateAttr, owlModel, maName, doc);
				if (interflage.equals("interActionIsOk")) {
					// ifInterFlage=true;

					doc = interaction.InterActionInfer(actionTemplateAttr, owlModel, maName, doc);
				}
				logger.info("InterAction end");
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("ERROR: InterAction Exception");
			}
			System.out.println(ifInterFlage);
			// if(ifInterFlage==false){
			try {
				logger.info("Eventbegin");
				Plot plotplan = new Plot();
				System.out.println("topiclist=" + topiclist);
				System.out.println("actionTemplate=" + actionTemplateAttr);

				doc = plotplan.EventInfer(topiclist, actionTemplateAttr, owlModel, maName, doc);
				flage1 = plotplan.ifContainEvent();
				System.out.println("鏄惁鎶藉埌瀵瑰簲鐨勪簨浠讹細" + flage1);
				logger.info("Event finish");
			} catch (Exception e) {
				flage = true;
				e.printStackTrace();
				logger.info("ERRPR: Event Exception");
			}

			// }
			// else{
			// flage=true;
			// }

			///////////////////////////////////////////////////////////////////////////////////
			try {
				logger.info("changeMaFrame begin");
				doc = Plot.changeMaFrame(owlModel, maName, doc);
				logger.info("changeMaFrame finish");
				String fileName = "C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl";
				saveOWLFile((JenaOWLModel) model, fileName);
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("ERROR: Frame Exception");
			}

			///////////////////////////////////////////////////////////////////////////////////
			// QIU p2
			if (flage == true || flage1 == false) {
				try {
					logger.info("Action begin");

					Action action = new Action();
					ArrayList actionTemplate = new ArrayList();

					ArrayList list = windRainSnowNeedAttr.get(0);

					System.out.println("actionTemplateAttr" + actionTemplateAttr);
					doc = action.actionInfer(actionTemplateAttr, owlModel, individualName, doc);
					logger.info("Action finish");
				} catch (Exception exQiu) {
					logger.info("ERROR: Action Exception");
				}
			}

		}

		/////////////////////// end
		// zheng p6
		try {
			logger.info("deform begin");
			// String
			// urlx="file:///C:/ontologyOWL/AllOwlFile/zhenOWL/10.1(test).owl";
			// OWLModel owlModelx =ProtegeOWL.createJenaOWLModelFromURI(urlx);
			CRebuild deform = new CRebuild();
			doc = deform.FinalOutPut(owlModel, individualName, moodTemplateAttr, doc);
			logger.info("deform finish");
		} catch (Exception exZheng) {
			logger.info("ERROR: deform Exception");
		}
		//////
		// zhao
		ArrayList ss = new ArrayList();
		for (int ii = 0; ii < SeasonList.size(); ii++) {
			String str = SeasonList.get(ii);
			String str2 = str.substring(0, str.indexOf("Template"));
			ss.add(str2);
		}
		System.out.println("seasonlist=" + ss);
		System.out.println("modelWithColors=" + modelWithColors + "and" + modelWithColor);
		try {
			logger.info("Color begin");
			MaToXML usez = new MaToXML();
			doc = usez.setColorAndLight(owlModel, individualName, doc, modelWithColors, SeasonList);
			logger.info("Color finish");
		} catch (Exception exZhao) {
			logger.info("ERROR: Color Exception");
		}
		//////////////////////////////////////////////////////////////////////////////
		// jiali
		try {
			// System.out.println("妯℃澘"+moodTemplateAttr.get(0));
			logger.info("fog begin");

			fogInsert tt = new fogInsert();

			doc = tt.fogInfer(weatherAndmoodAttr, owlModel, individualName, doc);
			logger.info("fog finish");
		} catch (Exception exJiali) {
			logger.info("ERROR: fog Exception");
		}
		////////////////////////////////////////////////////////////////////////
		// 鏋楁捣鍗庡姞椋庣殑绋嬪簭 p2
		WRSGernate(owlModel, individualName, doc);

		try {

			logger.info("Light begin");
			LightInsert light = new LightInsert();
			// doc=light.LightInfer(englishTemplate, model, maName,doc);
			doc = light.LightInfer(LightList, model, individualName, doc);

			logger.info("Light finish");
		} catch (Exception exHL) {
			logger.info("ERROR: Light Exception");
		}

		////////////////////////////////////////////////////////////////////////
		// nidejuan甯冨眬 p
		try {
			logger.info("Layout begin");
			// String
			// urlNDJ="file:///C:/ontologyOWL/AllOwlFile/Layout/sumo_phone3.owl";
			// OWLModel
			// owlModelndj=ProtegeOWL.createJenaOWLModelFromURI(urlNDJ);
			Layout lo = new Layout();

			doc = lo.setLayout(owlModel, individualName, doc);
			logger.info("Layout finish");
		} catch (Exception nidejuan) {
			logger.info("ERROR: Layout Exception");
		}
		///////////////////////////////////////////////////////////////////////////////////
		// 鍒樼晠 鏍囧彿p5

		// 缃楁槑 鐑熻姳
		try {

			logger.info("Firework begin");
			FireworkInsert firework = new FireworkInsert();
			// doc=light.LightInfer(englishTemplate, model, maName,doc);
			doc = firework.fireworkInfer(englishTemplate, model, individualName, doc);

			logger.info("Firework finish");
		} catch (Exception exHL) {
			logger.info("ERROR: Firework Exception");
		}
		// 娴锋磱 鍒樹繆鍚�
		try {

			logger.info("MakeBoatsCheck begin");
			MakeBoats makeBoats = new MakeBoats();
			doc = makeBoats.makeBoatsInfer(englishTemplate, model, individualName, doc);
			logger.info("MakeBoatsCheck end");
		} catch (Exception ex) {
			logger.info("Error: MakeBoatsCheck Exception.");

		}

		try {
			logger.info("Camera begin");
			// String uri =
			// "file:///C:/ontologyOWL/AllOwlFile/zhaoOWL/ColorAndLight.owl";
			// OWLModel owlModel=ProtegeOWL.createJenaOWLModelFromURI(uri);
			// String fileName = "3688.xml";
			System.out.println("鎽勫儚鏈哄疄渚�" + maName);
			CameraToXML chang = new CameraToXML();
			doc = chang.CreateCamera(owlModel, maName, doc);
			// chang.doc2XmlFile(doc, fileName);
			logger.info("Camera finish");

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("ERROR: Camera Exception");
		}

		/////////////////////////////////////////////////////////////////////////////////
		boolean yesNo = XMLInfoFromIEDom4j.doc2XmlFile(doc, xmlPath);
		model = null;
		System.gc();
	}

	/**
	 * 灏嗘灄娴峰崕鐨勬娊鍙栧嚭鏉ワ紝浣滀负鏈�缁堥�夊彇weatherable鏃剁殑渚濇嵁锛屽鏋滆兘娣诲姞澶╂皵锛屽垯weatherable璁句负true鍚﹀垯璁句负false
	 * owlModel 鍦ˋLLOWLFile涓殑sumowl2
	 * 
	 * @param individualName
	 *            閫夊彇鐨勫満鏅殑鍚嶅瓧
	 * @param doc
	 *            鐢熸垚鐨凙DL鏂囨。
	 */

	public static void WRSGernate(OWLModel owlModel, String individualName, Document doc) {
		// 鏋楁捣鍗庡姞椋庣殑绋嬪簭 p2
		ArrayList<String> list = new ArrayList();
		for (int i = 0; i < windRainSnowNeedAttr.size(); i++) {

			for (int j = 0; j < windRainSnowNeedAttr.get(i).size(); j++) {
				list.add(windRainSnowNeedAttr.get(i).get(j).toString());
			}

		}
		System.out.println(list);
		try {

			logger.info("WindAndRainAndSnow begin");
			Effect effect = new Effect();
			Document document1 = effect.runEffect(list, owlModel, individualName, doc);
			logger.info("WindAndRainAndSnow finish");
		} catch (Exception exLHH) {
			logger.info("ERROR:WindAndRainAndSnow Exception");
		}

	}

	public static Document printNegType(Document doc, String strNegType) {
		String ma = "SP_" + maName.substring(0, maName.length() - 3) + "_A";
		if (strNegType.equals(""))
			return doc;
		else {
			Element rootName = doc.getRootElement();
			Element name = rootName.element("maName");
			Element ruleName = name.addElement("rule");
			ruleName.addAttribute("ruleType", "addToMa");
			ruleName.addAttribute("addModel", "M_NoModel.ma");
			ruleName.addAttribute("spaceName", ma);
			ruleName.addAttribute("type", "noModel");
			ruleName.addAttribute("degree", "");
			ruleName.addAttribute("number", "1");
			ruleName.addAttribute("addModelID", "NoModelId");
			return doc;
		}
	}

	/**
	 * 褰揑E鎶藉彇鏈夋娊鍒版椂闂存椂锛屽垯寰�clock_clock杩欎釜妯″瀷涓婃坊鍔犳椂闂�
	 * 
	 * @param doc
	 * @param model
	 * @param copyIndividual
	 * @param templateAttr
	 * @return
	 */
	public static Document printTimeToClock(Document doc, OWLModel model, OWLIndividual copyIndividual,
			ArrayList<String> templateAttr) {
		if (templateAttr.size() == 0)
			return doc;
		else {
			Iterator its = templateAttr.iterator();
			boolean hasTime = false;
			String strTime = "";
			while (its.hasNext()) {
				String str = (String) its.next();
				if (str.equals("鏃堕棿")) {
					hasTime = true;
					strTime = (String) its.next();
					break;
				}
			}
			if (!hasTime)
				return doc;
			else {

				int iPostion = strTime.indexOf(":");
				String timeNodeName = strTime.substring(0, iPostion);
				String subStrTimeName = strTime.substring(iPostion + 1);
				Element rootName = doc.getRootElement();
				Element name = rootName.element("maName");

				if (copyIndividual.getBrowserText().equals("clock.ma") && timeNodeName.equals("鏃跺垎绉�")) {
					Element ruleName = name.addElement("rule");
					ruleName.addAttribute("ruleType", "addTimeToMa");
					ruleName.addAttribute("usedModelInMa", "clock_clock.ma");
					ruleName.addAttribute("addTime", subStrTimeName);
					ruleName.addAttribute("type", "time");

				} else {
					OWLObjectProperty hasSceneSpaceProperty = model.getOWLObjectProperty("hasSceneSpace");
					Collection spaceList = copyIndividual.getPropertyValues(hasSceneSpaceProperty);
					if (spaceList.size() == 0)
						return doc;
					else {
						Iterator its1 = spaceList.iterator();
						OWLNamedClass spaceParent = null;
						boolean hasGroundSpace = false;
						OWLIndividual spaceName = null;
						while (its1.hasNext()) {
							spaceName = (OWLIndividual) its1.next();
							spaceParent = getClassFromIndividual(model, spaceName);
							if (spaceParent.getBrowserText().equals("PlaneSceneSpaceIntsideRoomOnGround")
									|| spaceParent.getBrowserText().equals("PlaneSceneSpaceOutsideRoomOnGround")) {
								hasGroundSpace = true;
								break;
							}
						}
						if (hasGroundSpace && timeNodeName.equals("鏃跺垎绉�")) {
							Element ruleName1 = name.addElement("rule");
							ruleName1.addAttribute("ruleType", "addToMa");
							ruleName1.addAttribute("addModel", "clock_clock.ma");
							ruleName1.addAttribute("spaceName", spaceName.getBrowserText());
							ruleName1.addAttribute("degree", "");
							ruleName1.addAttribute("type", "model");
							ruleName1.addAttribute("number", "1");

							Element ruleName = name.addElement("rule");
							ruleName.addAttribute("ruleType", "addTimeToMa");
							ruleName.addAttribute("usedModelInMa", "clock_clock.ma");
							ruleName.addAttribute("addTime", subStrTimeName);
							ruleName.addAttribute("type", "time");
						}
					}
				}
				return doc;
			}
		}
	}

	/**
	 * 鎵撳嵃鍒犲噺绫昏鍒�
	 * 
	 * @param doc
	 * @param model
	 * @param copyIndividual
	 * @return
	 */
	public static Document printDeleteRule(Document doc, OWLModel model, OWLIndividual copyIndividual) {
		Element rootName = doc.getRootElement();
		Element name = rootName.element("maName");
		OWLObjectProperty subtractFromMaProperty = model.getOWLObjectProperty("subtractFromMa");
		OWLObjectProperty usedModelProperty = model.getOWLObjectProperty("usedModelInMa");
		if (copyIndividual.getPropertyValueCount(subtractFromMaProperty) > 0) {
			Collection deleteValues = copyIndividual.getPropertyValues(subtractFromMaProperty);
			for (Iterator its = deleteValues.iterator(); its.hasNext();) {
				OWLIndividual values = (OWLIndividual) its.next();
				boolean isDelete = false;
				if (copyIndividual.getPropertyValueCount(usedModelProperty) > 0) {
					Collection usedValue = copyIndividual.getPropertyValues(usedModelProperty);
					for (Iterator usedV = usedValue.iterator(); usedV.hasNext();) {
						OWLIndividual uv = (OWLIndividual) usedV.next();
						if (values.getBrowserText().equals(uv.getBrowserText())) {
							isDelete = true;
							break;
						}
					}
				}
				if (!isDelete) {// 纭繚鍒犻櫎鐨勬ā鍨嬫病鏈夊湪鏇存敼瑙勫垯涓娇鐢�
					Element ruleName = name.addElement("rule");
					ruleName.addAttribute("ruleType", "deleteFromMa");// type="model"
					ruleName.addAttribute("type", "model");// type="model"
					ruleName.addAttribute("usedModelID", values.getBrowserText());// type="model"
				}

			}
		}
		return doc;

	}

	public static Document printBackgroundPicture(Document doc, OWLModel model, OWLIndividual copyIndividual) {
		OWLObjectProperty changeBackgroundPictureProperty = model.getOWLObjectProperty("changeBackgroundPicture");

		Random rand = new Random();
		Date date = new Date();
		rand.setSeed(date.getTime());
		if (copyIndividual.getPropertyValueCount(changeBackgroundPictureProperty) > 0) {
			Collection pictureList = copyIndividual.getPropertyValues(changeBackgroundPictureProperty);
			int size = pictureList.size();
			if (bIsBackgroundScene && size > 0) {
				ArrayList<OWLIndividual> pictureArryList = new ArrayList();
				for (Iterator itss = pictureList.iterator(); itss.hasNext();) {
					OWLIndividual fatherV = (OWLIndividual) itss.next();
					pictureArryList.add(fatherV);
				}
				int k = rand.nextInt(pictureArryList.size());
				String backgroundPictureName = pictureArryList.get(k).getBrowserText();
				Element rootName = doc.getRootElement();
				Element name = rootName.element("maName");
				Element ruleName = name.addElement("rule");
				ruleName.addAttribute("ruleType", "changeBackgroundPicture");
				ruleName.addAttribute("pictureType", "3");
				ruleName.addAttribute("changeName", "0");
				ruleName.addAttribute("pictureName", backgroundPictureName);

			}
			return doc;
		} else {
			int kk = rand.nextInt(2);
			if (kk == 0) {
				logger.info("閫氳繃闅忔満閫夋嫨锛屼笉鏀瑰彉鑳屾櫙鍥剧墖");
				return doc;
			} else {
				OWLObjectProperty hasBackgroundPictureProperty = model.getOWLObjectProperty("hasBackgroundPicture");
				OWLDatatypeProperty backgroundPictureChangeNameProperty = model
						.getOWLDatatypeProperty("backgroundPictureChangeName");
				OWLDatatypeProperty backgroundPictureTypeProperty = model
						.getOWLDatatypeProperty("backgroundPictureType");
				if (copyIndividual.getPropertyValueCount(hasBackgroundPictureProperty) > 0) {
					logger.info("闅忔満閫夋嫨锛屾敼鍙樿儗鏅浘鐗囷紝骞舵湁鑳屾櫙鍥剧墖鏀瑰彉");
					Element rootName = doc.getRootElement();
					Element name = rootName.element("maName");
					Collection hasBackgroundPictureValues = copyIndividual
							.getPropertyValues(hasBackgroundPictureProperty);
					ArrayList<OWLIndividual> fInstance = new ArrayList();
					OWLIndividual lastFatherInstance = null;
					for (Iterator itss = hasBackgroundPictureValues.iterator(); itss.hasNext();) {
						OWLIndividual fatherV = (OWLIndividual) itss.next();
						fInstance.add(fatherV);
					}
					rand.setSeed(date.getTime());
					int k = rand.nextInt(fInstance.size());
					String backgroundPictureName = fInstance.get(k).getBrowserText();
					String pictureChangeName = copyIndividual.getPropertyValue(backgroundPictureChangeNameProperty)
							.toString();
					String pictureChangeType = copyIndividual.getPropertyValue(backgroundPictureTypeProperty)
							.toString();
					Element ruleName = name.addElement("rule");
					ruleName.addAttribute("ruleType", "changeBackgroundPicture");
					if (bIsBackgroundScene) {
						ruleName.addAttribute("pictureType", "3");
						ruleName.addAttribute("changeName", "0");
					} else {
						ruleName.addAttribute("pictureType", pictureChangeType);
						ruleName.addAttribute("changeName", pictureChangeName);
					}
					ruleName.addAttribute("pictureName", backgroundPictureName);
					return doc;
				} else {
					logger.info("闅忔満閫夋嫨鏀瑰彉鑳屾櫙鍥剧墖锛屼絾鎵�閫夌殑ma鏂囦欢娌℃湁鑳屾櫙鍥剧墖鍘绘敼鍙�");
					return doc;
				}
			}
		}
	}

	/**
	 * 鐢ㄤ簬鎵撳嵃鏇存敼瑙勫垯
	 * 
	 * @param doc
	 * @param model
	 * @param copyIndividual
	 * @return
	 */
	public static Document printExchangeRule(Document doc, OWLModel model, OWLIndividual copyIndividual) {
		Element rootName = doc.getRootElement();
		Element name = rootName.element("maName");
		OWLObjectProperty usedModelProperty = model.getOWLObjectProperty("usedModelInMa");
		OWLObjectProperty exchangedModelProperty = model.getOWLObjectProperty("exchangedModelInMa");
		OWLDatatypeProperty exchangedTypeProperty = model.getOWLDatatypeProperty("exchangedType");
		OWLObjectProperty modelForAddProperty = model.getOWLObjectProperty("modelForAdd");
		// 鎵撳嵃鏇存敼瑙勫垯
		if (copyIndividual.getPropertyValueCount(usedModelProperty) > 0) {
			Collection usedModelValues = copyIndividual.getPropertyValues(usedModelProperty);
			if (usedModelValues.size() > 0) {
				for (Iterator iModel = usedModelValues.iterator(); iModel.hasNext();) {
					OWLIndividual values = (OWLIndividual) iModel.next();
					boolean isUsedForAdd = false;
					if (modelForAddProperty.getPropertyValueCount(modelForAddProperty) > 0) {
						Collection modelForAddValue = modelForAddProperty.getPropertyValues(modelForAddProperty);
						for (Iterator modelAddV = modelForAddValue.iterator(); modelAddV.hasNext();) {
							OWLIndividual mav = (OWLIndividual) modelAddV.next();
							if (values.getBrowserText().equals(mav.getBrowserText())) {
								isUsedForAdd = true;
								break;
							}
						}
					}
					if (!isUsedForAdd) {// 鍗冲綋瑕佹敼鍙樼殑杩欎釜妯″瀷娌℃湁鍦ㄦ坊鍔犺鍒欎腑鏈夋搷浣滄椂锛屽氨瀵瑰畠杩涜鐩稿簲鐨勫彉鍖�
						if (values.getPropertyValueCount(exchangedTypeProperty) > 0) {
							Collection typeValues = values.getPropertyValues(exchangedTypeProperty);
							for (Iterator iType = typeValues.iterator(); iType.hasNext();) {
								Object type = iType.next();
								if (type.toString().equals("model")) {
									Collection modelValues = values.getPropertyValues(exchangedModelProperty);
									for (Iterator imodelV = modelValues.iterator(); imodelV.hasNext();) {
										OWLIndividual values2 = (OWLIndividual) imodelV.next();
										Element ruleName = name.addElement("rule");
										ruleName.addAttribute("ruleType", "exchangeToMa");// type="model"

										ruleName.addAttribute("usedModelID", values.getBrowserText());
										ruleName.addAttribute("addModel", values2.getBrowserText());
										ruleName.addAttribute("type", type.toString());

									}
								}
							}
						}
					}

				}
			}
		}

		return doc;
	}

	/**
	 * 閫氳繃鑱旀兂鐨勬柟娉曞線绌哄満鏅腑娣诲姞鐗╀綋锛屽寘鎷旱鍚戠殑娣诲姞锛屼篃鍖呮嫭鍒╃敤宸叉湁鍦烘櫙涓殑object鏉ュ線绌哄満鏅腑娣诲姞
	 * 
	 * @param model
	 * @param individual
	 * @return
	 */
	public static ArrayList<OWLIndividual> processAddToEmptyMa(OWLModel model, OWLIndividual individual) {
		ArrayList<OWLIndividual> addIndividuals = new ArrayList();
		OWLObjectProperty hasSceneProperty = model.getOWLObjectProperty("hasScene");
		Random rand = new Random();
		Date dt = new Date();
		rand.setSeed(dt.getTime());
		if (individual.getPropertyValueCount(hasSceneProperty) > 0) {
			int kk = rand.nextInt(2);// 鍦�0鍜�1涔嬮棿浜х敓闅忔満鏁�
			if (kk == 0) {
				addIndividuals = processAddToEmptyMaThroughZongxiang(model, individual);
			} else
				addIndividuals = processAddToEmptyMaThroughHasScene(model, individual);
		} else
			addIndividuals = processAddToEmptyMaThroughZongxiang(model, individual);
		return addIndividuals;
	}

	/**
	 * 閫氳繃绾靛悜寰�empty.ma涓坊鍔爋bject
	 * 
	 * @param model
	 * @param individual
	 * @return
	 */
	public static ArrayList<OWLIndividual> processAddToEmptyMaThroughZongxiang(OWLModel model,
			OWLIndividual individual) {
		ArrayList<OWLIndividual> addIndividuals = new ArrayList();
		OWLObjectProperty hasSceneProperty = model.getOWLObjectProperty("hasScene");
		OWLNamedClass className = getClassFromIndividual(model, individual);
		Collection fatherClassNameCollection = null;// 鐢ㄦ潵鎵撳嵃绾靛悜鐨勪竴浜涗俊鎭�
		OWLNamedClass fatherClassName = null;
		Random rand = new Random();
		Date dt = new Date();
		rand.setSeed(dt.getTime());
		int parentFloorNum = rand.nextInt(2) + 1;// 闅忔満閫夋嫨涓�涓埗灞傛
		for (int i = 0; i < parentFloorNum; i++) {
			fatherClassNameCollection = className.getSuperclasses(false);
			System.out.println("father count:" + fatherClassNameCollection.size());
			int count = 0;
			for (Iterator fc = fatherClassNameCollection.iterator(); fc.hasNext();) {
				fatherClassName = (OWLNamedClass) fc.next();
				count++;
				if (count == 1)
					break;

			}
			className = fatherClassName;

		}
		System.out.println("the father name:" + fatherClassName.getBrowserText());
		Collection fatherInstance = fatherClassName.getInstances(true);
		ArrayList<OWLIndividual> fInstance = new ArrayList();
		OWLIndividual lastFatherInstance = null;
		for (Iterator itss = fatherInstance.iterator(); itss.hasNext();) {
			OWLIndividual fatherV = (OWLIndividual) itss.next();
			fInstance.add(fatherV);
		}
		if (fInstance.size() > 0) {
			int fNum = rand.nextInt(fInstance.size()); // 闅忔満閫夋嫨鐖剁被涓殑鏌愪竴瀹炰緥
			lastFatherInstance = fInstance.get(fNum);
			addIndividuals.add(lastFatherInstance);
			if (lastFatherInstance.getPropertyValueCount(hasSceneProperty) > 0) {
				int kk2 = rand.nextInt(2);
				if (kk2 == 1)// 褰撻殢鏈烘暟涓�1鏃讹紝璇存槑鎶婃鐗╀綋鎵�灞炵殑鍦烘櫙鐨刼nject鍔犲叆鍒扮┖鍦烘櫙涓�
				{
					ArrayList<OWLIndividual> hasSceneObject = processAddToEmptyMaThroughHasScene(model,
							lastFatherInstance);
					for (Iterator tt = hasSceneObject.iterator(); tt.hasNext();)// 鎶婂満鏅腑鐨勬煇浜涚墿浣撳姞鍏ュ埌addIndividuals涓紱
					{
						OWLIndividual aa = (OWLIndividual) tt.next();
						addIndividuals.add(aa);
					}
				}
			}
		}
		return addIndividuals;
	}

	/**
	 * 閫氳繃hasScene灞炴�у線empty.ma涓坊鍔爋bject
	 * 
	 * @param model
	 * @param individual
	 * @return
	 */
	public static ArrayList<OWLIndividual> processAddToEmptyMaThroughHasScene(OWLModel model,
			OWLIndividual individual) {
		ArrayList<OWLIndividual> addIndividuals = new ArrayList();// 淇濆瓨鎵�鏈塖cenes
		ArrayList<OWLIndividual> addIndividuals2 = new ArrayList();// 淇濆瓨鏌愪竴
																	// 鍦烘櫙涓殑鎵�鏈塐bject
		ArrayList<OWLIndividual> addIndividuals3 = new ArrayList();// 淇濆瓨鏌愪竴
																	// 鍦烘櫙涓殑鏌愪簺Object
		OWLObjectProperty hasSceneProperty = model.getOWLObjectProperty("hasScene");
		OWLObjectProperty hasModelProperty = model.getOWLObjectProperty("hasmodel");
		Random rand = new Random();
		Date dt = new Date();
		rand.setSeed(dt.getTime());
		Collection sceneIndividuals = individual.getPropertyValues(hasSceneProperty);
		for (Iterator its = sceneIndividuals.iterator(); its.hasNext();) {
			OWLIndividual individualss = (OWLIndividual) its.next();
			addIndividuals.add(individualss);
		}
		int kk = rand.nextInt(addIndividuals.size());
		OWLIndividual sceneName = addIndividuals.get(kk);
		if (sceneName.getPropertyValueCount(hasModelProperty) > 0) {
			Collection modelObject = sceneName.getPropertyValues(hasModelProperty);
			for (Iterator t2 = modelObject.iterator(); t2.hasNext();) {
				OWLIndividual sObject = (OWLIndividual) t2.next();
				addIndividuals2.add(sObject);
			}
		}
		if (addIndividuals2.size() > 0) {
			int kkk = rand.nextInt(addIndividuals2.size());
			int i = 0;
			while (i <= kkk) {
				int k4 = rand.nextInt(addIndividuals2.size());
				OWLIndividual object2 = addIndividuals2.get(k4);
				addIndividuals3.add(object2);
			}
		}

		return addIndividuals3;
	}

	public static Document printCycleAddRuleVersion2(Document doc, OWLModel model, OWLIndividual copyIndividual,
			ArrayList<String> englishTemplate, int colorModelNum, ArrayList<String> colorChangeAttr) {
		colorModelNum = 0;
		colorChangeAttr.clear();
		Element rootName = doc.getRootElement();
		Element name = rootName.element("maName");
		String maName = name.attributeValue("name");
		String topicName = name.attributeValue("topic");
		if (maName.contains("nothing.ma"))// 鎵撳嵃nothing鍔ㄧ敾
		{
			doc = doc;
		} else if (maName.contains("empty.ma")) {
			String messValue = ProgramEntrance.messageValue;
			Element addRule = name.addElement("rule");
			addRule.addAttribute("ruleType", "addToMa");// type="model"
			addRule.addAttribute("addWord", messValue);
			addRule.addAttribute("spaceName", "");
			addRule.addAttribute("degree", "");
			addRule.addAttribute("type", "word");
			addRule.addAttribute("number", "1");
			addRule.addAttribute("class", "");
		} else {
			OWLNamedClass addModelRelatedClass = model.getOWLNamedClass("AddModelRelated");

			OWLObjectProperty addModelRelatedSpaceProperty = model.getOWLObjectProperty("addModelRelatedSpace");
			OWLObjectProperty hasModelNameProperty = model.getOWLObjectProperty("hasModelName");
			OWLDatatypeProperty isTarget = model.getOWLDatatypeProperty("isTemplateObject");
			OWLDatatypeProperty setColor = model.getOWLDatatypeProperty("setColor");
			OWLDatatypeProperty modelIDProperty = model.getOWLDatatypeProperty("modelID");
			OWLDatatypeProperty addModelNumberProperty = model.getOWLDatatypeProperty("addModelNumber");
			OWLDatatypeProperty addModelTypeProperty = model.getOWLDatatypeProperty("addModelType");

			OWLObjectProperty hasColorProperty = model.getOWLObjectProperty("hasColor");
			OWLObjectProperty heightProperty = model.getOWLObjectProperty("height");
			if (addModelRelatedClass.getInstanceCount() > 0) {
				OWLDatatypeProperty maFrameNumberProperty = model.getOWLDatatypeProperty("maFrameNumber");
				OWLDatatypeProperty topicNameProperty = model.getOWLDatatypeProperty("topicName");
				OWLDatatypeProperty degreeProperty = model.getOWLDatatypeProperty("degree");
				int maFrameNum = Integer.parseInt(copyIndividual.getPropertyValue(maFrameNumberProperty).toString());
				Collection<OWLIndividual> addModelList = addModelRelatedClass.getInstances();
				Iterator its = addModelList.iterator();
				while (its.hasNext()) {
					OWLIndividual addIndividual = (OWLIndividual) its.next();
					String modelID = (String) addIndividual.getPropertyValue(modelIDProperty);
					OWLIndividual relatedSpce = (OWLIndividual) addIndividual
							.getPropertyValue(addModelRelatedSpaceProperty);
					OWLIndividual modelName = (OWLIndividual) addIndividual.getPropertyValue(hasModelNameProperty);
					Object modelType = addIndividual.getPropertyValue(addModelTypeProperty);
					Object topicname = addIndividual.getPropertyValue(topicNameProperty);
					String modelNumber = "";
					String isTar = "";
					modelNumber = (String) addIndividual.getPropertyValue(addModelNumberProperty);
					isTar = (String) addIndividual.getPropertyValue(isTarget);

					Object degreeStr = null;
					String degree = "";

					OWLIndividual particleIndividual = (OWLIndividual) modelName;
					Element addRule = name.addElement("rule");
					addRule.addAttribute("ruleType", "addToMa");// type="model"
					addRule.addAttribute("addModel", modelName.getBrowserText());
					// 瀵圭煭淇′腑鎻愬埌鐨勫叿浣撻鑹插拰妯″瀷鐨処D鍋氬鐞嗭紝娣诲姞ID
					int flags = 0;

					for (int n = 0; n < modelWithColor.size();) {
						System.out.println("modelWithColorsize=" + modelWithColor.size());
						String mname = modelWithColor.get(n);
						String color = modelWithColor.get(n + 1);
						n = n + 2;

						if (mname.equals(modelName.getBrowserText()) && isTar.equals("1")) {
							modelWithColors.add(mname.toString());
							modelWithColors.add(modelID.toString());
							modelWithColors.add(color.toString());
						}

					}
					Collection temp = modelName.getDirectTypes();
					Iterator it = temp.iterator();
					while (it.hasNext()) {
						OWLNamedClass cls = (OWLNamedClass) it.next();
						System.out.println("class:" + cls.getBrowserText());
						if (!cls.getBrowserText().contains("Model"))
							addRule.addAttribute("class", cls.getBrowserText());
					}

					if (modelName.hasPropertyValue(setColor)) {
						addRule.addAttribute("color", (String) modelName.getPropertyValue(setColor));
						colorChangeAttr.add((String) modelName.getBrowserText());
						colorChangeAttr.add((String) modelName.getPropertyValue(setColor));
						colorModelNum++;
					}

					// String browserText = relatedSpce.getBrowserText();
					// System.out.println("+++"+browserText);

					if(relatedSpce==null){
						System.out.println("no");
					}
					addRule.addAttribute("spaceName", relatedSpce.getBrowserText());
					// if(addIndividual.getBrowserText().equals("boy.ma")||
					// addIndividual.getBrowserText().equals("girl.ma"))
					// addRule.addAttribute("type", "people");
					// else
					addRule.addAttribute("type", modelType.toString());
					addRule.addAttribute("isTarget", isTar);
					if (modelType.toString().equals("ParticleEffect")) {
						degreeStr = particleIndividual.getPropertyValue(degreeProperty);
						if (degreeStr == null || degreeStr.toString() == "")
							degree = "normal";
						else
							degree = degreeStr.toString();
						if (particleIndividual.getBrowserText().contains("rain")
								|| particleIndividual.getBrowserText().contains("snow")) {
							modelNumber = "1";
						}
						Random rand = new Random();
						if (modelName.toString().contains("fireWork")) {
							addRule.addAttribute("frameNumber", "");
							int startF = rand.nextInt(maFrameNum / 4) + 1;
							addRule.addAttribute("startFrame", Integer.toString(startF));
							addRule.addAttribute("endFrame", "");
						} else {
							addRule.addAttribute("frameNumber", "2");
							int startF = rand.nextInt(maFrameNum / 4) + 1;
							int endF = rand.nextInt(maFrameNum / 4) + maFrameNum / 2;
							addRule.addAttribute("startFrame", Integer.toString(startF));
							addRule.addAttribute("endFrame", Integer.toString(endF));
						}

						if (modelName.toString().contains("smoke")) {
							Object smokeColor = particleIndividual.getPropertyValue(hasColorProperty);
							String smokeC = "";
							if (smokeColor == null)
								smokeC = "white";
							else
								smokeC = smokeColor.toString();
							addRule.addAttribute("color", smokeC);
							Object smokeHeight = particleIndividual.getPropertyValue(heightProperty);
							String smokeH = "";
							if (smokeHeight == null)
								smokeH = "normal";
							else
								smokeH = smokeHeight.toString();
							addRule.addAttribute("height", smokeH);
						}

					}
					addRule.addAttribute("degree", degree);
					addRule.addAttribute("number", modelNumber);
					// 澶勭悊鐑熺殑棰滆壊锛岄珮搴�
					addRule.addAttribute("addModelID", modelID.toString());
					System.out.println(topicname);
					/*
					 * if(topicname!=null){ addRule.addAttribute("topicname",
					 * topicname.toString()); }
					 */
				}

				/*
				 * Element time = name.addElement("TimeWeatherAndFog");
				 * time.addAttribute("time", timeweatherandfog.get(0));
				 * if(!(timeweatherandfog.get(1).equals("")))
				 * time.addAttribute("weather", timeweatherandfog.get(1)); else
				 * time.addAttribute("weather", timeweatherandfog.get(2));
				 */
				// time.addAttribute("cloud", timeweatherandfog.get(2));
				// time.addAttribute("fog", timeweatherandfog.get(3));
			}
		}
		return doc;
	}

	/**
	 * 瀵规湁鑳屾櫙鐨刴a鏂囦欢杩涜鑳屾櫙鍥剧墖鐨勬洿鎹�
	 * 
	 * @param model
	 * @param individualName
	 * @return
	 */
	public String exchangeMaBackground(OWLModel model, OWLIndividual individualName) {
		String picture = null;
		OWLDatatypeProperty hasBPictureProperty = model.getOWLDatatypeProperty("maHasBackgroundPicutre");

		return picture;
	}

	/**
	 * 褰撲俊鎭娊鍙栨病鏈夋娊鍒颁富棰橈紝妯℃澘涔熸病鏈夋帹鍑轰富棰橈紝鍒欑敱妯℃澘閫氳繃hasAnimationNameFromTemplate
	 * 鐪嬪彲鍚﹀緱鍒扮浉搴旂殑鍔ㄧ敾鍦烘櫙锛堢洿鎺ョ敱灞炴�у�煎緱鍑猴級
	 * 
	 * @param model
	 * @param englishTemplate
	 * @return
	 */
	public static String getAnimationSceneFromTemplate(OWLModel model, ArrayList<String> englishTemplate) {
		OWLObjectProperty hasAnimationNameFProperty = model.getOWLObjectProperty("hasAnimationNameFromTemplate");// usedSpaceInMa
		ArrayList<OWLIndividual> animationList = new ArrayList();
		Random rand = new Random();
		Iterator its = englishTemplate.iterator();
		while (its.hasNext()) {
			String templateAllName = (String) its.next();
			int iPostion = templateAllName.indexOf(":");
			String templateAutmName = templateAllName.substring(iPostion + 1, templateAllName.length());
			OWLIndividual templateIndividual = model.getOWLIndividual(templateAutmName);
			if (templateIndividual.getPropertyValueCount(hasAnimationNameFProperty) > 0) {
				Collection collection = templateIndividual.getPropertyValues(hasAnimationNameFProperty);
				for (Iterator iValues = collection.iterator(); iValues.hasNext();) {
					OWLIndividual animationIndividual = (OWLIndividual) iValues.next();
					animationList.add(animationIndividual);
				}
			}
		}
		if (animationList.size() > 0) {
			return animationList.get(animationList.size() - 1).getBrowserText();
		}
		return "";
	}

	/**
	 * 鑾峰緱瀹炰緥鐨勭被鍚�
	 * 
	 * @param model
	 * @param individualName
	 * @return
	 */
	public static OWLNamedClass getClassFromIndividual(OWLModel model, OWLIndividual individualName) {
		OWLNamedClass className = null;
		RDFResource resource = individualName;
		String classNameStr = resource.getRDFType().getBrowserText();
		className = model.getOWLNamedClass(classNameStr);
		return className;

	}

	/**
	 * 澶嶅埗涓�涓猰a鍦烘櫙鏂囦欢鐨勫疄渚�
	 * 
	 * @param maName锛氬凡缁忛�夊ソ鐨刴a鏂囦欢
	 * @param model
	 * @return
	 */

	@SuppressWarnings("deprecation")
	public static OWLModel copyMaIndividual(String maName, OWLModel model, String topicName) {
		if (!maName.equals("")) {
			OWLIndividual i = model.getOWLIndividual(maName);
			OWLIndividual shalowCopy = (OWLIndividual) i.copy(null, null, false);
			// OWLIndividual shalowCopy = (OWLIndividual) i.deepCopy(null,
			// null);
			OWLDatatypeProperty maSenceNameProperty = model.getOWLDatatypeProperty("maSceneName");
			OWLDatatypeProperty selectedMaProperty = model.getOWLDatatypeProperty("selectedMa");
			OWLDatatypeProperty topicNameProperty = model.getOWLDatatypeProperty("topicName");
			shalowCopy.setPropertyValue(maSenceNameProperty, maName);
			shalowCopy.setPropertyValue(topicNameProperty, topicName);
			String maName11 = (String) shalowCopy.getPropertyValue(maSenceNameProperty);
			System.out.println("manEEE:" + maName11);
			shalowCopy.rename(shalowCopy.getNamespace() + "copyMaSceneIndividual");
			OWLIndividual ii = model.getOWLIndividual("copyMaSceneIndividual");
			ii.setPropertyValue(selectedMaProperty, new Integer(1));// 璁剧疆鎵�閫夌殑ma鐨剆electedMa灞炴�у�间负1
			// ii.c
			System.out.println("copyIndividual:" + ii.getBrowserText());
		}
		return model;
	}

	/**
	 * 閫氳繃涓枃topic鍒皁ntology涓鎵剧浉搴旂殑鑻辨枃topic绫�
	 * 
	 * @param owlModel锛歰wl
	 *            model
	 * @param chineseTopic:涓枃topic绫伙紙閫氳繃IE鎶藉彇鐨勶級
	 * @return
	 */
	public static OWLNamedClass getEnglishTopicFromPlot(OWLModel owlModel, String chineseTopic) {

		OWLNamedClass TopicClass = owlModel.getOWLNamedClass("TopicRelatedPlot");
		OWLDatatypeProperty chineseNameProperty = owlModel.getOWLDatatypeProperty("chineseName");
		OWLNamedClass cls = null;
		Collection subTopicClass = TopicClass.getSubclasses(true);// 鎵撳嵃瀛愮被锛屽苟鎵撳嵃瀛愮被鐨勫瓙绫�
		for (Iterator itTopic = subTopicClass.iterator(); itTopic.hasNext();) {
			cls = (OWLNamedClass) itTopic.next();
			// System.out.println("LCS:"+cls.getBrowserText());
			if (cls.getDirectSubclassCount() == 0)// 鍒ゆ柇杩欎釜绫诲凡缁忔病鏈夊瓙绫讳簡
			{

				Object hasValueName = cls.getHasValue(chineseNameProperty);
				if (hasValueName != null && hasValueName.toString().equals(chineseTopic)) {
					break;
				}
				cls = null;
			}

		}
		return cls;
	}

	/**
	 * 鎶婁腑鏂囨ā鏉垮彉鎴愯嫳鏂囩殑,閫氳繃杩欎釜鏂规硶澶勭悊瀹屽悗锛岃嫳鏂囨ā鏉夸腑宸茬粡 娌℃湁妯℃澘鐨勫悕瀛楀彧鏈夌浉搴旂殑鍊硷紝濡傗�滃ぉ姘斺�濓紝鈥滄椂闂粹�濈瓑璇嶅凡缁忔病鏈� 鍙湁鈥滈洩锛氫腑闆�濈瓑瀛楁牱
	 * 
	 * @param chineseTemplate
	 * @param model
	 * @return
	 * @throws IOException
	 * @throws SecurityException
	 */
	public static ArrayList<String> colorTemplate2Individual(ArrayList<String> colorTemplate,
			ArrayList<String> colorMark, OWLModel model) {
		ArrayList<String> individualWithColor = new ArrayList();
		ArrayList<String> modelWithColor = new ArrayList();
		OWLObjectProperty modelFromTemplateProperty = model.getOWLObjectProperty("hasModelFromTemplate");
		OWLDatatypeProperty modelColorProperty = model.getOWLDatatypeProperty("setColor");
		Iterator<String> itsCol = colorMark.iterator();
		for (Iterator<String> itsTemp = colorTemplate.iterator(); itsTemp.hasNext();) {
			String color = itsCol.next();
			String templateValue = itsTemp.next();
			int postion = templateValue.indexOf(":");
			String individual = templateValue.substring(postion + 1);
			OWLIndividual indi = model.getOWLIndividual(individual);
			Iterator temp = indi.getPropertyValues(modelFromTemplateProperty).iterator();
			while (temp.hasNext()) {
				String modelName = temp.next().toString();
				int p1 = modelName.indexOf("#");
				int p2 = modelName.indexOf(" of");
				modelName = modelName.substring(p1 + 1, p2);
				logger.info("~~~~~~modelName:" + modelName);
				OWLIndividual modelIndi = model.getOWLIndividual(modelName);
				modelIndi.setPropertyValue(modelColorProperty, color);
				// 瀵圭壒娈婄殑妯″瀷娣诲姞鐗规畩棰滆壊鍋氬鐞嗐��
				modelWithColor.add(modelName);
				modelWithColor.add(color);
			}
		}
		return modelWithColor;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<String> chineseTemplate2English(ArrayList<String> chineseTemplate, OWLModel model)
			throws SecurityException, IOException {
		ArrayList<String> englishTemplate = new ArrayList();
		OWLNamedClass templateClass = model.getOWLNamedClass("Template");
		OWLDatatypeProperty chineseNameProperty = model.getOWLDatatypeProperty("chineseName");
		OWLNamedClass cls = null;
		Collection subTemplateClass = templateClass.getSubclasses(true);// 鎵撳嵃瀛愮被锛屽苟鎵撳嵃瀛愮被鐨勫瓙绫�
		for (Iterator<String> ist = chineseTemplate.iterator(); ist.hasNext();) {// 寰幆澶勭悊涓枃鍘熷瓙妯℃澘瀵瑰簲鐨勫師瀛愪俊鎭�
			String tempName = ist.next();

			boolean isStop = false;
			boolean findCls = false;
			A: for (Iterator itTemplate = subTemplateClass.iterator(); itTemplate// 寰幆Template涓嬮潰鐨勬墍鏈夊瓙绫�
					.hasNext();) {
				if (isStop)
					break;
				cls = (OWLNamedClass) itTemplate.next();// 鍒皁ntology涓鎵炬ā鏉匡紝閫氳繃chineseName杩欎釜灞炴�ф潵鏌ユ壘
				Object cc = cls.getHasValue(chineseNameProperty);
				// System.out.println("hhhh:"+cls.getBrowserText()+"
				// test111:"+tempName);
				if (cls.getHasValue(chineseNameProperty).toString().trim().equals(tempName.trim()))// 閫氳繃hasValue灞炴�ф潵鑾峰緱妯℃澘鐨勫悕瀛�
																									// 锛岃〃鏄庢槸浠�涔堟ā鏉匡紙鏃堕棿鎴栧湴鐐癸級
				{// 鍏堟煡鎵惧搴旂殑妯℃澘

					String tempNameW = cls.getBrowserText();
					// englishTemplate.add(tempNameW);
					String templateVlaue = "";
					if (ist.hasNext())
						templateVlaue = ist.next();
					// String[] splitTempName = new String[2];
					String[] splitTempName = templateVlaue.split(":");
					Collection subsubTemplateClass = cls.getSubclasses(true);// 鎵撳嵃鍑哄凡鏌ユ壘鐨勬ā鏉跨殑瀛愮被
					B: for (Iterator itsTemplate = subsubTemplateClass.iterator(); itsTemplate.hasNext();) {
						if (isStop)
							break;

						OWLNamedClass clss = (OWLNamedClass) itsTemplate.next();
						Collection clssHasValues = clss.getHasValues(chineseNameProperty);

						C: for (Iterator itValue = clssHasValues.iterator(); itValue// 鏌ユ壘鎵�瀵瑰簲妯℃澘涓嬬殑瀛愮被
								.hasNext();) {
							if (isStop)
								break;
							Object value = itValue.next();
							if (value.toString().trim().equals(splitTempName[0].trim()))// 鐢ㄦ潵鍖归厤妯℃澘鍊肩殑鍐掑彿鍓嶉潰鐨勫瓧娈碉細濡傚鏍★細灏忓锛岃繖閲屽氨鏄尮閰嶁�滃鏍♀�濈殑
							{
								findCls = true;
								isStop = true;
								String templateName = clss.getBrowserText() + ":";
								if (clss.getInstanceCount() != 0) {
									ArrayList<String> templateInstan = new ArrayList();
									String autoValue = "";
									Collection templateInstances = clss.getInstances();// 褰撴壘鍒颁簡妯℃澘鎵�瀵瑰簲鐨勭被锛屽垯澶勭悊妯℃澘绫诲搴旂殑瀹炰緥锛屼篃灏辨槸鍘熷瓙淇℃伅
									D: for (Iterator it = templateInstances.iterator(); it.hasNext();) {
										OWLIndividual templateIndividual = (OWLIndividual) it.next();// 鎵撳嵃鍑哄疄渚�
										templateInstan.add(templateIndividual.getBrowserText());// 鐢ㄦ潵瀛樺偍鎵�鏈夊疄渚嬶紝涓昏鏄负浜嗗綋娌℃湁鎵惧埌绗﹀悎瑕佹眰鐨勫疄渚嬫椂灏变粠涓殢鏈虹殑閫夋嫨涓�涓�
										if (templateIndividual.getPropertyValueCount(chineseNameProperty) > 0) {
											Collection chineseValues = templateIndividual
													.getPropertyValues(chineseNameProperty);
											for (Iterator its = chineseValues.iterator(); its.hasNext();)// 寰幆瀹炰緥鎵�瀵瑰簲鐨勫涓腑鏂囧悕绉�
											{
												String cValue = its.next().toString();
												if (cValue.trim()
														.equals(splitTempName[splitTempName.length - 1].trim())) {
													// templateIndividual.getpro
													autoValue = templateIndividual.getBrowserText();

													break D;
												} // 鎵�瀵瑰簲瀹炰緥鐨勪腑鏂囧悕绉�
											} // 缁撴潫瀹炰緥鐨勪腑鏂囧悕瀛楃殑寰幆
										} // 缁撴潫瀹炰緥鏄惁鏈変腑鏂囧悕瀛�

									} // 缁撴潫瀵瑰簲绫荤殑瀹炰緥
									if (autoValue != "")// 妯℃澘瀹炰緥涓壘鍒颁簡鐩稿簲鐨勬ā鏉垮師瀛愬搴旂殑淇℃伅
										templateName = templateName + autoValue + ":1.0";

									else // 褰撳疄渚嬩腑鎵句笉鍒扮浉搴旂殑妯℃澘鍘熷瓙淇℃伅锛屽氨浠庡凡缁忔湁鐨勬ā鏉垮疄渚嬪師瀛愪腑闅忔満鎸戦�変竴涓�
									{
										Random rand = new Random();
										int kk = rand.nextInt(templateInstan.size());
										templateName = templateName + templateInstan.get(kk) + ":0.5";
									}

								} // 缁撴潫瀹炰緥鐨勫垽鏂�

								if (!englishTemplate.contains(templateName)) {
									englishTemplate.add(templateName);
								}
								int i = templateName.lastIndexOf(":");// 鍘绘帀鏈�鍚庣殑鍒嗗��
								String temp = templateName.substring(0, i);

								if (tempName.equals("鍔ㄤ綔")) {

									actionTemplateAttr.add(temp);// to qiu
								}
								if (tempName.equals("浜虹墿") || tempName.equals("缁忓吀浜虹墿瑙掕壊")) {
									ActionNeedPeople.add(temp);
								}
								if (tempName.equals("鎯呯华")) {
									moodTemplateAttr.add(temp);
									weatherAndmoodAttr.add(temp);
									ExpressionList.add(temp);
									actionTemplateAttr.add(temp);
									WindAttr.add(temp);
									RainAttr.add(temp);
									SnowAttr.add(temp);
									LightList.add(temp);
								}
								if (tempName.equals("澶╂皵") | tempName.equals("瀛ｈ妭")) {
									weatherAndmoodAttr.add(temp);
									RainAttr.add(temp);
									WindAttr.add(temp);
									SnowAttr.add(temp);
									LightList.add(temp);
									SeasonList.add(temp);
								}
								// if(tempName.equals("鏃堕棿"))
								// WindAttr.add(templateName);
								// if(tempName.equals("鎯呯华"))

								System.out.println("test:" + templateName);
								logger.info("妯℃澘淇℃伅缈昏瘧鎴愯嫳鏂囧悗鐨勫�硷細" + templateName);
							}

						}
						// Object ccc=cls.getHasValue(chineseNameProperty);

					} // 妯℃澘鍚嶄笅瀛愮被鐨勭粨鏉�
					if (findCls == false)
						TemplateName.add(tempNameW);// 鍗曠嫭娣诲姞妯℃澘鍚嶇О
				} // 鍒ゆ柇鏄惁鏈夌浉瀵逛簬鐨勬ā鏉�
			} // template绫讳笅鐨勬墍鏈夊瓙绫�
		}

		System.out.println("鍚勭妯℃澘englishiTemplate=" + englishTemplate + windRainSnowNeedAttr);
		return englishTemplate;
	}

	// ****************************瀵筽lot涓殑涓枃妯″瀷**************************************//
	@SuppressWarnings("unchecked")
	public static ArrayList<String> chineseTemplate2EnglishFromPlot(ArrayList<String> chineseTemplate, OWLModel model)
			throws SecurityException, IOException {
		ArrayList<String> englishTemplate = new ArrayList();

		OWLNamedClass templateClass = model.getOWLNamedClass("ModelRelatedPlot");
		OWLDatatypeProperty chineseNameProperty = model.getOWLDatatypeProperty("chineseName");
		OWLNamedClass cls = null;
		Collection subTemplateClass = templateClass.getSubclasses(true);// 鎵撳嵃瀛愮被锛屽苟鎵撳嵃瀛愮被鐨勫瓙绫�
		for (Iterator<String> ist = chineseTemplate.iterator(); ist.hasNext();) {// 寰幆澶勭悊涓枃鍘熷瓙妯℃澘瀵瑰簲鐨勫師瀛愪俊鎭�
			String tempName = ist.next();

			boolean isStop = false;
			boolean findCls = false;
			A: for (Iterator itTemplate = subTemplateClass.iterator(); itTemplate// 寰幆Template涓嬮潰鐨勬墍鏈夊瓙绫�
					.hasNext();) {
				if (isStop)
					break;
				cls = (OWLNamedClass) itTemplate.next();// 鍒皁ntology涓鎵炬ā鏉匡紝閫氳繃chineseName杩欎釜灞炴�ф潵鏌ユ壘
				Object cc = cls.getHasValue(chineseNameProperty);
				// System.out.println("hhhh:"+cls.getBrowserText()+"
				// test111:"+tempName);
				if (cc != null && cls.getHasValue(chineseNameProperty).toString().trim().equals(tempName.trim()))// 閫氳繃hasValue灞炴�ф潵鑾峰緱妯℃澘鐨勫悕瀛�
																													// 锛岃〃鏄庢槸浠�涔堟ā鏉匡紙鏃堕棿鎴栧湴鐐癸級
				{// 鍏堟煡鎵惧搴旂殑妯℃澘
					String tempNameW = cls.getBrowserText();
					// englishTemplate.add(tempNameW);
					String templateVlaue = "";
					if (ist.hasNext())
						templateVlaue = ist.next();
					// String[] splitTempName = new String[2];
					String[] splitTempName = templateVlaue.split(":");
					Collection subsubTemplateClass = cls.getSubclasses(true);// 鎵撳嵃鍑哄凡鏌ユ壘鐨勬ā鏉跨殑瀛愮被
					B: for (Iterator itsTemplate = subsubTemplateClass.iterator(); itsTemplate.hasNext();) {
						if (isStop)
							break;

						OWLNamedClass clss = (OWLNamedClass) itsTemplate.next();
						Collection clssHasValues = clss.getHasValues(chineseNameProperty);

						C: for (Iterator itValue = clssHasValues.iterator(); itValue// 鏌ユ壘鎵�瀵瑰簲妯℃澘涓嬬殑瀛愮被
								.hasNext();) {
							if (isStop)
								break;
							Object value = itValue.next();
							if (value.toString().trim().equals(splitTempName[0].trim()))// 鐢ㄦ潵鍖归厤妯℃澘鍊肩殑鍐掑彿鍓嶉潰鐨勫瓧娈碉細濡傚鏍★細灏忓锛岃繖閲屽氨鏄尮閰嶁�滃鏍♀�濈殑
							{
								findCls = true;
								isStop = true;
								String templateName = clss.getBrowserText() + ":";
								if (clss.getInstanceCount() != 0) {
									ArrayList<String> templateInstan = new ArrayList();
									String autoValue = "";
									Collection templateInstances = clss.getInstances();// 褰撴壘鍒颁簡妯℃澘鎵�瀵瑰簲鐨勭被锛屽垯澶勭悊妯℃澘绫诲搴旂殑瀹炰緥锛屼篃灏辨槸鍘熷瓙淇℃伅
									D: for (Iterator it = templateInstances.iterator(); it.hasNext();) {
										OWLIndividual templateIndividual = (OWLIndividual) it.next();// 鎵撳嵃鍑哄疄渚�
										templateInstan.add(templateIndividual.getBrowserText());// 鐢ㄦ潵瀛樺偍鎵�鏈夊疄渚嬶紝涓昏鏄负浜嗗綋娌℃湁鎵惧埌绗﹀悎瑕佹眰鐨勫疄渚嬫椂灏变粠涓殢鏈虹殑閫夋嫨涓�涓�
										if (templateIndividual.getPropertyValueCount(chineseNameProperty) > 0) {
											Collection chineseValues = templateIndividual
													.getPropertyValues(chineseNameProperty);
											for (Iterator its = chineseValues.iterator(); its.hasNext();)// 寰幆瀹炰緥鎵�瀵瑰簲鐨勫涓腑鏂囧悕绉�
											{
												String cValue = its.next().toString();
												if (cValue.trim()
														.equals(splitTempName[splitTempName.length - 1].trim())) {
													// templateIndividual.getpro
													autoValue = templateIndividual.getBrowserText();

													break D;
												} // 鎵�瀵瑰簲瀹炰緥鐨勪腑鏂囧悕绉�
											} // 缁撴潫瀹炰緥鐨勪腑鏂囧悕瀛楃殑寰幆
										} // 缁撴潫瀹炰緥鏄惁鏈変腑鏂囧悕瀛�

									} // 缁撴潫瀵瑰簲绫荤殑瀹炰緥
									if (autoValue != "")// 妯℃澘瀹炰緥涓壘鍒颁簡鐩稿簲鐨勬ā鏉垮師瀛愬搴旂殑淇℃伅
										templateName = templateName + autoValue + ":1.0";

									else // 褰撳疄渚嬩腑鎵句笉鍒扮浉搴旂殑妯℃澘鍘熷瓙淇℃伅锛屽氨浠庡凡缁忔湁鐨勬ā鏉垮疄渚嬪師瀛愪腑闅忔満鎸戦�変竴涓�
									{
										Random rand = new Random();
										int kk = rand.nextInt(templateInstan.size());
										templateName = templateName + templateInstan.get(kk) + ":0.5";
									}

								} // 缁撴潫瀹炰緥鐨勫垽鏂�

								if (!englishTemplate.contains(templateName)) {
									englishTemplate.add(templateName);
								}
								logger.info("妯℃澘淇℃伅缈昏瘧鎴愯嫳鏂囧悗鐨勫�硷細" + templateName);
							}

						}
						// Object ccc=cls.getHasValue(chineseNameProperty);

					} // 妯℃澘鍚嶄笅瀛愮被鐨勭粨鏉�

				} // 鍒ゆ柇鏄惁鏈夌浉瀵逛簬鐨勬ā鏉�
			} // template绫讳笅鐨勬墍鏈夊瓙绫�
		}

		System.out.println("鍚勭妯℃澘englishiTemplate=" + englishTemplate);
		return englishTemplate;
	}

	// ********************************************涓枃缈昏瘧鎴愯嫳鏂囩粨鏉�**********************************************************//

	/**
	 * 閫氳繃涓枃topic鍒皁ntology涓鎵剧浉搴旂殑鑻辨枃topic绫�
	 * 
	 * @param owlModel锛歰wl
	 *            model
	 * @param chineseTopic:涓枃topic绫伙紙閫氳繃IE鎶藉彇鐨勶級
	 * @return
	 */
	public static OWLNamedClass getEnglishTopic(OWLModel owlModel, String chineseTopic) {

		OWLNamedClass TopicClass = owlModel.getOWLNamedClass("Topic");
		OWLDatatypeProperty chineseNameProperty = owlModel.getOWLDatatypeProperty("chineseName");
		OWLNamedClass cls = null;
		Collection subTopicClass = TopicClass.getSubclasses(true);// 鎵撳嵃瀛愮被锛屽苟鎵撳嵃瀛愮被鐨勫瓙绫�

		for (Iterator itTopic = subTopicClass.iterator(); itTopic.hasNext();) {
			cls = (OWLNamedClass) itTopic.next();
			if (cls.getDirectSubclassCount() == 0)// 鍒ゆ柇杩欎釜绫诲凡缁忔病鏈夊瓙绫讳簡
			{
				Object hasValueName = cls.getHasValue(chineseNameProperty);
				if (hasValueName != null && hasValueName.toString().equals(chineseTopic)) {
					break;
				}
				cls = null;
			}

		}
		return cls;
	}

	/**
	 * 浠巓wl涓瑽ackgroundScene 绫讳笅闈㈤�夊彇涓�涓疄渚�
	 * 
	 * @param model
	 * @return
	 */
	public static String chooseBackgroundScene(OWLModel model) {
		String strBackgroundScene = "";
		OWLNamedClass backgrounScene = model.getOWLNamedClass("BackgroundScene");
		if (backgrounScene.getInstanceCount() > 0) {
			Collection backgroundList = backgrounScene.getInstances();
			ArrayList<OWLIndividual> backgroundList2 = (ArrayList) backgroundList;
			Random rand = new Random();
			int kk = rand.nextInt(backgroundList2.size());
			strBackgroundScene = backgroundList2.get(kk).getBrowserText();

		}
		return strBackgroundScene;
	}

	/**
	 * 閫氳繃addModelFromTopic绫昏鍒欐潵閫夋嫨鍦烘櫙鏂囦欢
	 * 
	 * @param model
	 * @param englishTopicClass
	 * @return
	 * @throws SWRLRuleEngineException
	 */
	public static String getMaFromAddModelFromTopicRule(OWLModel model, OWLNamedClass englishTopicClass)
			throws SWRLRuleEngineException {
		String choosedMaName = "";
		OWLObjectProperty hasModelFromTopicProperty = model.getOWLObjectProperty("hasModelFromTopic");
		Collection individualList = englishTopicClass.getInstances();
		OWLIndividual topicIndividualValue = null;
		if (individualList.size() > 0) {
			Iterator its = individualList.iterator();
			while (its.hasNext()) {
				topicIndividualValue = (OWLIndividual) its.next();
			}

		}
		if (topicIndividualValue.getPropertyValueCount(hasModelFromTopicProperty) > 0) {
			SWRLMethod.addModelFromTopicToScene(model, englishTopicClass.getBrowserText());
			choosedMaName = chooseBackgroundScene(model);
		}
		return choosedMaName;

	}

	/**
	 * 閫氳繃涓婚鏉ヨ幏寰梞a锛屽墠鎻愭槸涓婚涓嶄负绌�
	 * 
	 * @param owlModel
	 * @param EnglishTopic:閫氳繃涓枃topic鑾峰緱鐨勮嫳鏂噒opic
	 * @return
	 * @throws IOException
	 * @throws SecurityException
	 * @throws SWRLRuleEngineException
	 */
	@SuppressWarnings("deprecation")
	public static String getMaFromTopic(OWLModel model, OWLNamedClass englishTopicClass)
			throws SecurityException, IOException, SWRLRuleEngineException {
		String choosedMaName = "";
		int zeroCount = 0;// 鐢ㄦ潵璁板綍鏍囪涓�0鐨勫姩鐢诲満鏅殑涓暟
		int oneCount = 0;// 鐢ㄦ潵璁板綍鏍囪涓�1鐨勫姩鐢诲満鏅殑涓暟
		ArrayList<String> instanceSave = new ArrayList();// 鐢ㄦ潵瀛樺偍瀹炰緥
		ArrayList<Integer> instanceMarkSave = new ArrayList();// 鐢ㄦ潵瀛樺偍瀹炰緥鐨勬爣蹇楋紝鐪嬩箣鍓嶆槸鍚﹁閫変腑杩�
		OWLObjectProperty hasMaProperty = model.getOWLObjectProperty("hasMa");
		OWLDatatypeProperty senceMarkProperty = model.getOWLDatatypeProperty("animationSceneMark");
		RDFResource resource = englishTopicClass.getSomeValuesFrom(hasMaProperty);
		logger.info("閫氳繃涓婚鏉ヨ幏寰椾富棰樻墍瀵瑰簲鐨刴a鍦烘櫙鐨勫悕瀛楋紝璇ヤ富棰樻墍瀵瑰簲鐨勫満鏅被鍚嶏細" + resource.getBrowserText());
		String hasValues = resource.getBrowserText();// 鑾峰緱涓婚瀵瑰簲鐨勫満鏅殑绫诲悕
		String[] hasValuesSplit = hasValues.split(" or ");// 鍙兘瀵瑰簲澶氫釜 鍦烘櫙绫�;

		ArrayList<String> hasValuesClass = new ArrayList();
		OWLNamedClass resourceClass = null;
		System.out.println("geshu:" + hasValuesSplit.length);
		if (hasValuesSplit.length > 1) {// 褰撴湁澶氫釜鍦烘櫙绫绘椂锛屽厛鍒ゆ柇姣忎釜鍦烘櫙绫绘槸鍚﹂兘鏈夊疄渚�
			for (int i = 0; i < hasValuesSplit.length; i++) {
				OWLNamedClass resourceClass0 = model.getOWLNamedClass(hasValuesSplit[i].trim());
				System.out.println("name::" + hasValuesSplit[i].trim());
				int instanceCount0 = resourceClass0.getInstanceCount();
				if (instanceCount0 > 0)
					hasValuesClass.add(hasValuesSplit[i].trim());
			}
			Random rand = new Random();
			if (hasValuesClass.size() > 0)// 褰撳涓満鏅被閮芥湁瀹炰緥鏃讹紝鍒欓殢鏈洪�夋嫨涓�涓�
			{
				int kk = rand.nextInt(hasValuesClass.size());
				resourceClass = model.getOWLNamedClass(hasValuesClass.get(kk));
			} else// 褰撳涓満鏅被閮芥病鏈夊疄渚嬫椂锛屽垯闅忔満閫夋嫨涓�涓暱鍦烘櫙绫�
			{
				int kk = rand.nextInt(hasValuesSplit.length);
				resourceClass = model.getOWLNamedClass(hasValuesSplit[kk].trim());

			}
		} else// 澶勭悊鍙湁涓�涓満鏅被鐨勬儏鍐�
			resourceClass = model.getOWLNamedClass(hasValuesSplit[0].trim());

		logger.info("鏈�缁堣涓婚鎵�瀵瑰簲鐨勫満鏅被鍚嶏細" + resourceClass.getBrowserText());
		int instanceCount = resourceClass.getInstanceCount();
		System.out.print("姝ゅ満鏅被鎷ユ湁鐨勫疄渚嬩釜鏁版槸锛�" + instanceCount);

		logger.info("鏈�缁堣涓婚鎵�瀵瑰簲鐨勫満鏅被鍚嶇殑瀹炰緥涓暟锛�" + instanceCount);
		if (instanceCount != 0) {// 鍗硉opic瀵瑰簲鐨勫満鏅被涓湁瀹炰緥
			System.out.println(";鍒嗗埆涓猴細");
			Collection resourceInstance = resourceClass.getInstances(true);
			for (Iterator it = resourceInstance.iterator(); it.hasNext();) {
				OWLIndividual individual = (OWLIndividual) it.next();// 鎵撳嵃鍑哄疄渚�
				System.out.println("瀹炰緥涓猴細" + individual.getBrowserText());
				instanceSave.add(individual.getBrowserText());// 瀛樺偍姣忎釜瀹炰緥
				if (individual.getPropertyValue(senceMarkProperty) == null)
					individual.setPropertyValue(senceMarkProperty, new Integer(0));
				int instanceMark = Integer.parseInt(individual.getPropertyValue(senceMarkProperty).toString());
				instanceMarkSave.add(instanceMark);// 瀛樺偍姣忎釜瀹炰緥鐨勬爣蹇�
				System.out.println("寮曠敤浣嶄负锛�" + instanceMark);
			}
			/// ************鈥滃娆℃満浼氣�濈畻娉�(start)*********
			for (int i = 0; i < instanceMarkSave.size(); i++)// 缁熻0鍜�1鐨勪釜鏁�
			{
				int temp = instanceMarkSave.get(i);
				if (temp == 0)
					zeroCount++;
				else
					oneCount++;
			}
			// oneCount==instanceMarkSave.size()
			if (zeroCount == 0)// 褰撴瘡涓姩鐢婚兘琚�夎繃鏃讹紝灏嗘湰浣撲腑senceMark娓呴浂
			{
				for (Iterator it = resourceInstance.iterator(); it.hasNext();) {
					OWLIndividual individual = (OWLIndividual) it.next();
					individual.setPropertyValue(senceMarkProperty, new Integer(0));
				}
				Random random = new Random();
				int k = random.nextInt(instanceMarkSave.size());
				choosedMaName = instanceSave.get(k);
				OWLIndividual individual = model.getOWLIndividual(choosedMaName);
				individual.setPropertyValue(senceMarkProperty, new Integer(1));
			} else if (oneCount == 0) {
				Random random = new Random();
				int k = random.nextInt(instanceMarkSave.size());
				choosedMaName = instanceSave.get(k);
				OWLIndividual individual = model.getOWLIndividual(choosedMaName);
				individual.setPropertyValue(senceMarkProperty, new Integer(1));

			} else {
				float scale = oneCount / zeroCount;
				choosedMaName = multiChanceArithmetic(scale, instanceSave, instanceMarkSave, model);

			}
			/// ************鈥滃娆℃満浼氣�濈畻娉�(end)*********

		} else// 褰撴湁涓婚锛屼絾涓婚鎵�瀵瑰簲鐨勫満鏅被娌℃湁瀹炰緥,鍒欐煡鎵惧満鏅被鐖惰妭鐐逛笅鐨勫叾浠栧満鏅被锛堝彲浜掓崲锛�
		{
			ArrayList<OWLNamedClass> otherClass = new ArrayList();
			logger.info("涓婚锛�" + englishTopicClass.getBrowserText() + "鎵�瀵瑰簲鐨刴a鍦烘櫙鏂囦欢涓�0涓紝鍒欐壘鍏剁埗鑺傜偣涓嬬殑鍏朵粬鍦烘櫙绫�");
			Collection resourceSuperClass = resourceClass.getSuperclasses(false);
			// 鏌ユ壘鐖剁被鍏朵粬瀹炰緥涓嶄负0鐨勫満鏅被
			int classCount = 0;
			for (Iterator itTopic = resourceSuperClass.iterator(); itTopic.hasNext();) {
				classCount++;
				Object rdfclass = itTopic.next();
				// NamedClass
				if (rdfclass.toString().contains("NamedClass")) {
					OWLNamedClass cls = (OWLNamedClass) rdfclass;
					if (cls.getInstanceCount(true) != 0)
						otherClass.add(cls);
					System.out.println("subTopic:" + cls.getBrowserText());
					if (classCount == 1)
						break;
				} else
					continue;
				System.out.println("subTopic:" + rdfclass.toString());
			}
			if (otherClass.size() != 0) {
				ArrayList<OWLIndividual> lastClassInstanceName = new ArrayList();
				Random rand = new Random();
				Date date = new Date();
				rand.setSeed(date.getTime());
				int kk = rand.nextInt(otherClass.size());
				OWLNamedClass lastClass = otherClass.get(kk);
				Collection lastClassInstance = lastClass.getInstances(true);
				for (Iterator itTopic = lastClassInstance.iterator(); itTopic.hasNext();) {
					OWLIndividual individual = (OWLIndividual) itTopic.next();
					lastClassInstanceName.add(individual);
				}

				rand.setSeed(date.getTime());
				int k = rand.nextInt(lastClassInstanceName.size());
				OWLIndividual lastIndividualMa = lastClassInstanceName.get(k);
				choosedMaName = lastIndividualMa.getBrowserText();
				int instanceMark = Integer.parseInt(lastIndividualMa.getPropertyValue(senceMarkProperty).toString());
				if (instanceMark == 0)
					lastIndividualMa.setPropertyValue(senceMarkProperty, new Integer(1));
				else
					lastIndividualMa.setPropertyValue(senceMarkProperty, new Integer(0));

			} else {

				logger.info("涓婚锛�" + englishTopicClass.getBrowserText() + "鎵�瀵瑰簲鐨刴a鍦烘櫙鏂囦欢涓�0涓紝鍒欐壘鍏剁埗鑺傜偣涓嬬殑鍏朵粬鍦烘櫙绫讳篃娌℃湁瀹炰緥");
				choosedMaName = "";
			}

		}
		JenaOWLModel owlModel = (JenaOWLModel) model;
		// String fileName = "sumoOWL2/sumo_phone3.owl";//淇濆瓨ma鏍囪
		// String fileName = "C:/ontologyOWL/rootOWL/sumoOWL2/sumo_phone3.owl";
		String fileName = "C:/ontologyOWL/sumoOWL2/sumo_phone3.owl";//
		saveOWLFile(owlModel, fileName);
		// System.out.println("鏈�缁堥�夌殑ma鍚嶅瓧鏄細" + choosedMaName);
		logger.info("瀵瑰簲涓婚锛�" + englishTopicClass.getBrowserText() + "鎵�閫夌殑ma鏂囦欢涓猴細" + choosedMaName);
		return choosedMaName;
	}

	/**
	 * 澶氭鏈轰細绠楁硶
	 */
	public static String multiChanceArithmetic(float scale, ArrayList<String> instanceSave,
			ArrayList<Integer> instanceMarkSave, OWLModel model) {
		int temp = 0;
		if (scale > 2)
			temp = 8;
		else if (1 < scale && scale <= 2)
			temp = 4;
		else
			temp = 2;
		String choosedMaName = null;
		for (int i = 0; i < temp; i++) {
			Random random = new Random();
			int k = random.nextInt(instanceMarkSave.size());
			choosedMaName = instanceSave.get(k);
			OWLDatatypeProperty senceMarkProperty = model.getOWLDatatypeProperty("animationSceneMark");
			OWLIndividual individual = model.getOWLIndividual(choosedMaName);
			int instanceMark = Integer.parseInt(individual.getPropertyValue(senceMarkProperty).toString());
			if (instanceMark == 0)// 鍦�2娆′箣鍐咃紝濡傛灉閫夊埌娌¤閫夌殑鍔ㄧ敾锛屽垯寰幆鍋滄
			{
				OWLIndividual individual1 = model.getOWLIndividual(choosedMaName);
				individual1.setPropertyValue(senceMarkProperty, new Integer(1));
				break;
			}
		}
		return choosedMaName;

	}

	/**
	 * 閫氳繃妯℃澘鏉ユ帹鍑轰富棰�
	 * 
	 * @param model
	 * @param templateAttr
	 * @return
	 */

	public static String getTopicFromTemplateAfterSWRL(OWLModel model, ArrayList<String> templateAttr) {
		String topicName = "";
		OWLObjectProperty hasTopicFromTemplateProperty = model.getOWLObjectProperty("hasTopicFromTemplate");
		ArrayList<String> topicN = new ArrayList();// 褰撴湁澶氫釜妯℃澘鏃讹紝鍙兘姣忎釜妯℃澘鍊奸兘鎺ㄥ嚭涓�涓垨澶氫釜涓婚锛�
		// ArrayList<String>
		// templateA=[DayTemplate:daybreakTemplate,PlayTemplate:playTemplate,PlayTemplate:playTemplate,PromisesTemplate:promisesTemplate,ManCharacterTemplate:botherTemplate];

		for (Iterator its = templateAttr.iterator(); its.hasNext();)// 鎵�鏈夌殑妯℃澘鍊�
		{
			String templateValue = (String) its.next();
			String[] splitTempName = new String[2];
			splitTempName = templateValue.split(":");
			System.out.println("鏈�缁堥�夌殑妯℃澘鍚嶅瓧鏄細" + templateAttr);
			OWLIndividual individual = model.getOWLIndividual(splitTempName[1]);
			int count = individual.getPropertyValueCount(hasTopicFromTemplateProperty);
			boolean isAdd = false;
			if (count > 0) {
				Collection topicValue = individual.getPropertyValues(hasTopicFromTemplateProperty);
				for (Iterator its1 = topicValue.iterator(); its1.hasNext();)// 瑙勫垯鎺ㄥ鍚庢瘡涓ā鏉垮�间腑鍙兘瀛樺湪澶氫釜妯℃澘鍊�
				{
					OWLIndividual value = (OWLIndividual) its1.next();
					if (topicN.size() > 0) {
						isAdd = true;
						for (Iterator<String> its2 = topicN.iterator(); its2.hasNext();)// 闃叉topicN涓嚭鐜伴噸澶嶇殑鍊�
						{
							String its2Value = its2.next();
							if (its2Value.equals(value.getBrowserText())) {
								isAdd = false;
								break;
							}
						}
						if (isAdd)
							topicN.add(value.getBrowserText());
					} else
						topicN.add(value.getBrowserText());
				}

			}

		}
		if (topicN.size() > 0) {
			Random rand = new Random();
			int kk = rand.nextInt(topicN.size());
			topicName = topicN.get(kk);
		}
		return topicName;
	}

	public static boolean isOwdToMa(OWLModel model, String maName, OWLIndividual individual) {
		OWLIndividual maname = model.getOWLIndividual(maName);
		System.out.println(maname.getBrowserText());
		OWLObjectProperty hasValueOfPlace = model.getOWLObjectProperty("hasValueOfPlace");
		OWLObjectProperty Location = model.getOWLObjectProperty("Location");
		OWLObjectProperty isEquivalOf = model.getOWLObjectProperty("isEquivalOf");
		// OWLIndividual place=(OWLIndividual)
		// maname.getPropertyValue(hasValueOfPlace);

		Collection clo = individual.getPropertyValues(Location);

		if (clo.size() != 0) {
			for (Iterator i = clo.iterator(); i.hasNext();) {
				OWLIndividual local = (OWLIndividual) i.next();
				OWLIndividual place = (OWLIndividual) maname.getPropertyValue(hasValueOfPlace);
				if (local.getPropertyValue(isEquivalOf).equals(place)) {
					return true;
				}

			}
		}
		return false;
	}

}
