package plot;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngineFactory;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;



//赵蒙
public class Expression {

	public static void main(String[] args)throws OntologyLoadException, DocumentException, SWRLRuleEngineException, IOException {
		// TODO Auto-generated method stub
		String owlPath = "file:///C:/ontologyOWL/sumoOWL2/sumo_phone3.owl" ;
		OWLModel model = ProtegeOWL.createJenaOWLModelFromURI(owlPath);
		ArrayList<String> alist =new ArrayList<String>(); 
		alist.add("GladnessTemplate:gladnessTemplate");//测试用，无主题有模板
		
		File file = new File("E:\\Shock\\adl_result.xml");
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(file);
		
		System.out.println("开始");
		Expression shock=new Expression();
		ArrayList<String>  topic = new ArrayList<String> ();
		topic.add("SmileTopic");
		Document document1=shock.ShockXml(alist,topic, model,"room2.ma",document);
		XMLWriter writer = new XMLWriter(new FileWriter("E:\\Shock\\testExpression.xml"));
        writer.write(document1);
        System.out.println("结束");
        writer.close();
	}
	static Logger logger = Logger.getLogger(Expression.class.getName());
	public static SWRLFactory createSWRLFactory(OWLModel model)
	{
		SWRLFactory factory = new SWRLFactory(model);
		return factory;
	}
    //创建规则执行器（这个是我瞎胡理解的啊）
	public static SWRLRuleEngine createRuleEngine(OWLModel model) throws SWRLRuleEngineException
	{
			SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create("SWRLJessBridge", model);
			return ruleEngine;
	}
	
	public static void executeTopicToBackgroundSceneSWRLEngine(OWLModel model,ArrayList<String> topicName) throws SWRLRuleEngineException
	{
		SWRLRuleEngine ruleEngine = createRuleEngine(model);
		ruleEngine.reset();
		SWRLFactory factory = createSWRLFactory(model);
		factory.disableAll();
		Iterator<SWRLImp> iter = factory.getImps().iterator();
		while(iter.hasNext())
		{
			SWRLImp imp = (SWRLImp) iter.next();
			if (topicName.size() != 0) 
			{
				if (imp.getLocalName().contains("addFaceActionToSceneRule")) //找到名为addFaceActionToSceneRule的规则
				{
					for (Iterator<String> its = topicName.iterator(); its.hasNext();) //用迭代器逐条执行addFaceActionToSceneRule规则
					{
						String templateValue = its.next();
						String templateValue1=templateValue.replaceAll("Individual", "");
						if(imp.getBody().getBrowserText().contains(templateValue1))//再找到以templateValue开头的规则
						{
							logger.info("运行的规则名字为："+imp.getLocalName());
							imp.enable();//执行此规则，它会给所有场景添加规则里的结果
						}	
						
					}
				}
			}
		}
		ruleEngine.infer();
	}
	public static void executeTemplateToBackgroundSceneSWRLEngine(OWLModel model,ArrayList<String> templateName) throws SWRLRuleEngineException
	{
		SWRLRuleEngine ruleEngine = createRuleEngine(model);
		ruleEngine.reset();
		SWRLFactory factory = createSWRLFactory(model);
		factory.disableAll();
		Iterator<SWRLImp> iter = factory.getImps().iterator();
		while(iter.hasNext())
		{
			SWRLImp imp = (SWRLImp) iter.next();
			if (templateName.size() != 0) 
			{
				if (imp.getLocalName().contains("addFaceActionToSceneRule")) //找到名字包含“addFaceActionToSceneRule”的规则
				{
					for (Iterator<String> its = templateName.iterator(); its.hasNext();) //用迭代器逐条执行addFaceActionToSceneRule规则
					{
						String templateValue = its.next();
						String templateValue1="p2:"+templateValue;
						String templateValue2=templateValue1.substring(0, templateValue1.indexOf(":",templateValue1.indexOf(":")+1 ));
						if(imp.getBody().getBrowserText().contains(templateValue2))
						{
							logger.info("运行的规则名字为："+imp.getLocalName());
							imp.enable();//执行此规则
						}	
					}
				}
			}
		}
		ruleEngine.infer();
	}
	public Document ShockXml(ArrayList<String> list,ArrayList<String> topic,OWLModel model,String sceneName,Document document) throws DocumentException, IOException, OntologyLoadException, SWRLRuleEngineException{
		if(sceneName.equals("empty.ma")||sceneName.equals("nothing.ma")){ 
			System.out.println("场景为空，不添加表情");
			return document;
			}
		ArrayList<String> humanList= new ArrayList();
		ArrayList<String> modelList=new ArrayList();
		Element root=(Element) document.getRootElement();
		System.out.println(root.getName()+"----root");//根节点
		Iterator it=root.elementIterator();
		while(it.hasNext()){
			Element el=(Element)it.next();
			System.out.println(el.getName()+"----el");//取各条记录
			Iterator itt=el.elementIterator();
			while(itt.hasNext()){
				Element el1=(Element)itt.next();//取得记录中的各字段
				if(el1.attribute(0).getName().equals("ruleType")&&el1.attribute(1).getName().equals("addModel")){
					if(el1.attributeValue("ruleType").equals("addToMa")&&isHuman(el1.attributeValue("addModel"))){//找到addtoma规则，看add的ma是不是人物模型
						System.out.println(el1.attributeValue("addModel"));
						humanList.add(el1.attributeValue("addModel"));
						modelList.add(el1.attributeValue("addModelID"));
					}
				}
			}
		}
		if(!humanList.isEmpty())//添加了人物模型，即可对模型表情处理
		{
			System.out.println(humanList.size());
			for(int i=0;i<humanList.size();i++){//添加了几个人，就对几个人做表情
				document=Shock(list,topic,model,humanList.get(i),modelList.get(i),document);
			}
		}
		else System.out.println("没有添加人物模型，不添加表情！");
    	return document;
	}
public static boolean isHuman(String temp){
    	
    	if(temp.equals("M_boy.ma"))//暂时只处理场景中的M_boy
    		return true;
    	else
    		return false;
    }
	public static int randomNumber(int i){ 
		int p = (int) Math.random()*i;
		return p;
	}
	public static String changeExpression(String expression){
		int len=expression.length();
		if(expression.charAt(3)=='b')
		{
			return expression.substring(7, len-3);
		}
		if(expression.charAt(3)=='g')
		{
			return expression.substring(8, len-3);
		}
		return null;
	}
	public Document Shock(ArrayList<String> list,ArrayList<String> topic,OWLModel model, String hasma,String modelId,Document doc) throws OntologyLoadException, SWRLRuleEngineException{
		String str="p2:";
		String expression="";
		String ex="";
		boolean iftopic=true;
		int index=0;
		Document doc1=doc;
		//用到的各种属性
		OWLObjectProperty hasTopicProperty=model.getOWLObjectProperty("hasTopic");
		OWLObjectProperty topicForExpression=model.getOWLObjectProperty(str+"topicForExpression");
		OWLObjectProperty templateForExpression=model.getOWLObjectProperty(str+"templateForExpression");
		OWLObjectProperty addExpressionToMaProperty=model.getOWLObjectProperty("addExpressionToMa");
		//z最后抽取到的信息
		ArrayList<String> topicList= new ArrayList();//用于存放主题下的表情
		ArrayList<String> templateList= new ArrayList();//用于存放模板下的表情
		ArrayList<String>   tempList   =   new   ArrayList();//临时存放表情实例
		char ma=hasma.charAt(2);//判断xml中add的ma是男是女
		
		Collection temp = null;//实例缓存
		if(!topic.isEmpty()&&iftopic){
			for(;index<topic.size();index++){
				String temptopic=topic.get(index);
				System.out.println("场景主题处理---------------");
				//执行主题规则
				ArrayList<String>   topicrule  =   new   ArrayList();
			 
			   //executeTopicToBackgroundSceneSWRLEngine(model,topicList);
				
			   System.out.println("抽取的主题有：");
			   System.out.println(temptopic);
			   OWLNamedClass topicClass=model.getOWLNamedClass(temptopic);
			   Collection curCls = topicClass.getInstances(true);
			   OWLIndividual indi = null;
			   String topicName="";
			   for (Iterator itIns = curCls.iterator(); itIns.hasNext();){
				   indi = (OWLIndividual) itIns.next();
				   topicName=indi.getBrowserText();
				   System.out.println(topicName);
				   if(topicName!=""&&!topicName.isEmpty())
					   break;
			   } 
			   OWLIndividual TopIndividual=model.getOWLIndividual(topicName);
			   temp=TopIndividual.getPropertyValues(topicForExpression);	
			   OWLIndividual[] expre = (OWLIndividual[]) temp.toArray(new OWLIndividual[0]);//将表情实例转换为数组
			   if(expre.length!=0){
				   System.out.println("该主题对应的表情实例有：");
				   for(int j=0;j<expre.length;j++){
					   tempList.add(expre[j].getBrowserText());//在添加每一个表情实例
					   //System.out.println(expre[j].getBrowserText());
					   System.out.println(tempList.get(j).toString());
					   }
					}
			   else
				   System.out.println("该主题不适合添加表情....");
			   if(!tempList.isEmpty()){
				   for(int i=0;i<tempList.size();i++){
					   if(tempList.get(i).toString().charAt(3)==ma)//实例中的与hasma性别相同的取出
							topicList.add(tempList.get(i));
				   }
//				   for(int i=0;i<topicList.size();i++)
//				   {
//					   System.out.println(topicList.get(i).toString());
//				   }
				   System.out.println(randomNumber(topicList.size()));
				   expression=topicList.get(randomNumber(topicList.size())).toString();//随机取出一个合适的表情实例
				   ex=changeExpression(expression);
				   if(ex!=null){
					   System.out.println("最终选取的表情实例为主题进入："+ex);
				   }
				   tempList.clear();
				   topicList.clear();
				   iftopic=false;
				   String order=String.valueOf(index+1);
				   doc1 = printExpressionRule(doc, ex,hasma,modelId,order);
				   continue;
				}
			   else{
				   System.out.println("根据抽取的主题不适合添加表情！");
				   System.out.println("场景主题处理完毕！---------------");
				   tempList.clear();
				   topicList.clear();
				   iftopic=true;
			   }
			}
		}
		//////////////////////////无主题，处理模板////////////////////////////////////
		if((!topic.isEmpty()&&iftopic)||topic.isEmpty()){
			System.out.println("场景模板处理---------------"); 
			String templist="";
			String template[]=new String[30];//存储模板实例名
			if(!list.isEmpty()){
				System.out.println("传递进来的模板有：");
				for(int i=0;i<list.size();i++){
					templist=list.get(i).toString();
					int pos = templist.indexOf(":");
					template[i]=(String) templist.subSequence(pos+1, templist.length());
					System.out.println("模板："+template[i]);
					OWLIndividual TemIndividual=model.getOWLIndividual(template[i]);
					temp=TemIndividual.getPropertyValues(templateForExpression);//获取templateForExpression的实例
					OWLIndividual[] expre = (OWLIndividual[]) temp.toArray(new OWLIndividual[0]);//将表情实例转换为数组
					if(expre.length!=0){
						System.out.println("该模板对应的表情实例有：");
					for(int j=0;j<expre.length;j++){
						tempList.add(expre[j].getBrowserText());//在添加每一个表情实例
						System.out.println(expre[j].getBrowserText());
						}
					}
					else
						System.out.println("该模板不适合添加表情....");
				}
			}
			if(!tempList.isEmpty()){
				for(int i=0;i<tempList.size();i++){
					if(tempList.get(i).toString().charAt(3)==ma){
						templateList.add((String) tempList.get(i));
					}
				}
				System.out.println(randomNumber(templateList.size()));
				expression=templateList.get(randomNumber(templateList.size())).toString();//随机取出一个合适的表情实例
				ex=changeExpression(expression);
				if(ex!=null){
					System.out.println("最终选取的表情实例为模板进入："+ex);
				}
				doc1 = printExpressionRule(doc,ex,hasma,modelId,"0");
			}
			else
				System.out.println("根据进来的模板不适合添加表情！");
			tempList.clear();
			templateList.clear();
			System.out.println("场景模板处理完毕！---------------");
			
			if(expression.length()==0)
			{
				System.out.println("没有合适的表情...");
				return doc;
			} 
		}
		return doc1;
	}
	public static Document printExpressionRule(Document doc,String expression,String hasma,String modelId,String order){
		System.out.println("开始生成xml—rule");
		Element rootName = (Element) doc.getRootElement();
		Element name = rootName.element("maName");
		Element ruleName = name.addElement("rule");
		ruleName.addAttribute("ruleType", "addExpressionToMa");
		ruleName.addAttribute("type", "FacialExpression");
		ruleName.addAttribute("usedModelInMa",hasma);
		ruleName.addAttribute("usedModelID",modelId);
		ruleName.addAttribute("expression", expression);
		ruleName.addAttribute("expressionID",order);
		System.out.println("xml—rule生成完毕");
		return doc;
	}

}
