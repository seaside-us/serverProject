package plot;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import org.apache.log4j.Logger;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngineFactory;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLFactoryException;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLAtomList;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;


public class SWRLMethod {
	static Logger logger = Logger.getLogger(SWRLMethod.class.getName());
	/**
	 * An instance of the SWRLFactory can be created 
	 * by passing an OWL model to its constructor
	 */
	public static SWRLFactory createSWRLFactory(OWLModel model) {
		SWRLFactory factory = new SWRLFactory(model);
		return factory;
	}

	/**
	 * 获得每条规则
	 */
	public static void getSWRLRules(SWRLFactory factory) {
		Collection rules = factory.getImps();
		for (Iterator its = rules.iterator(); its.hasNext();) {
			SWRLImp swrlImp = (SWRLImp) its.next();
			System.out.println(swrlImp.getBrowserText());
		}
	}

	/**
	 * 创建rule engine
	 * @throws SWRLRuleEngineException 
	 */
	public static SWRLRuleEngine createRuleEngine(OWLModel model)
			throws SWRLRuleEngineException {
		SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create("SWRLJessBridge", model);
		//SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create(model);
		return ruleEngine;
	}
	/**
	 * 执行规则的方法二，主要是把规则分成几部分，分布运行规则
	 * 现在想的是规则可以分为4部分，1：使主题实例的hasMa属性都有值
	 *   2、根据主题和模板原子往相应的ma中做相应的变化：主要是添加一些东西，如节日，模板时晚上，可以添加烟花的效果
	 *   3、ma的全局规则（如帽子的颜色可以换，换个同种类型的帽子等）：主要用来对ma中一些模型进行变化
	 *   4、ma的局部规则，这是针对每个ma做的，对每个ma进行增删改
	 * name 可以是主题的名字，ma全局规则的名字，也可以是ma局部规则的名字
	 * @throws SWRLRuleEngineException 
	 * @throws SWRLFactoryException 
	 */
	public static boolean executeSWRLEngine1(OWLModel model, String rulename,String maName)
			throws SWRLRuleEngineException, SWRLFactoryException {
		SWRLRuleEngine ruleEngine = createRuleEngine(model);
		ruleEngine.reset();
		SWRLFactory factory = createSWRLFactory(model);
		factory.disableAll();
		boolean temp=false;
		ArrayList<String> impName=new ArrayList();//用来存储有"_"的规则
		Iterator<SWRLImp> iter = factory.getImps().iterator();	
		while (iter.hasNext()) {
			SWRLImp imp = (SWRLImp) iter.next();
			//處理第一類規則
			if (rulename.startsWith("topicIndividualAddMa")&&imp.getLocalName().startsWith(rulename) == true) {
				imp.enable();
			}
			//处理ma的更改规则，这里要注意的是更改规则一次只处理一条，要不然打印的时候会出现混乱
			else if (rulename.contains("ExchangeRule")&&imp.getLocalName().contains(maName)&&imp.getLocalName().contains("ExchangeRule") == true) {
				impName.add(imp.getLocalName());
				temp=true;
			}
			//处理ma的局部规则
			else if(rulename.contains("AddRule")&&imp.getLocalName().contains(maName)&&imp.getLocalName().contains("Add") == true)
			{				
				imp.enable();
				
			}
			/*//处理getTopicFromTemplate的规则
			else if(rulename.contains("getTopicFromTemplate")&&imp.getLocalName().startsWith("getTopicFromTemplate"))
			{
				imp.enable();
			}*/
		}
		//当impName的大小不等于0时，说明有相类似的规则需要处理
		if(impName.size()!=0)
		{
			
				Random random=new  Random();
				
				if(impName.size()!=0)
				{
					int k=random.nextInt(impName.size());
				    String name_=impName.get(k);//在同一组有"_"的规则中选择一条
				    SWRLImp impNamepp=factory.getImp(name_);
				    impNamepp.enable();
				}
				
			
				
		}
		ruleEngine.reset();
		ruleEngine.infer();
		return temp;
	}
	/**
	 * 把模板原子对应的模型添加到空场景中，这个的前提是没有主题的情况下
	 * @param model
	 * @param name：规则的，名字
	 * @param topicName：主题的名字
	 * @param templateName：模板的名字
	 * @return(OKKKK)
	 * @throws SWRLRuleEngineException
	 */
	public static boolean executeSWRLEnginetoEmptyMa(OWLModel model, String name,
			String topicName, ArrayList<String> templateName) throws SWRLRuleEngineException
	{
		boolean isOK=false;
		SWRLRuleEngine ruleEngine = createRuleEngine(model);
		ruleEngine.reset();
		SWRLFactory factory = createSWRLFactory(model);
		factory.disableAll();		
		Iterator<SWRLImp> iter = factory.getImps().iterator();
		while (iter.hasNext()) {
			SWRLImp imp = (SWRLImp) iter.next();	
			boolean isImpEnable=false;
			if(topicName.equals("")&&imp.getLocalName().startsWith(name) == true)
			{
				SWRLAtomList atomList = imp.getBody();
				//atomList.remove
				String bodyName = atomList.getBrowserText();
				for(Iterator<String> its=templateName.iterator();its.hasNext();)//查找与主题和ma名字符合的规则
				{
					String templateValue=its.next();
					templateValue=templateValue.replace(":", "(")+")";
					if(bodyName.contains(templateValue))
					{
						isImpEnable=true;
						break;
						
					}					
				}
				
				
			}
			if(isImpEnable==true)
			{
				imp.enable();
				isOK=true;
			}
			
		}
		ruleEngine.infer();
		return isOK;
	}

	/**
	 * 这个方法是对上个方法的重载，主要是这个运行的是全局的规则，全局规则包括主题和模板信息
	 * 
	 * @param model
	 * @param name：主要是规则的名字(name=topicTemplateGlobeRule)
	 * @param templateName：模板的名字
	 * @param topicName：主题的名字
	 * @throws SWRLRuleEngineException(okkkk)
	 */
	@SuppressWarnings("unchecked")
	public static void executeSWRLEngine1(OWLModel model, String name,
			String topicName, ArrayList<String> templateName)
			throws SWRLRuleEngineException {
		SWRLRuleEngine ruleEngine = createRuleEngine(model);
		ruleEngine.reset();
		SWRLFactory factory = createSWRLFactory(model);
		factory.disableAll();
		Iterator<SWRLImp> iter = factory.getImps().iterator();
		while (iter.hasNext()) {
			SWRLImp imp = (SWRLImp) iter.next();	
			Iterator<SWRLAtom> iterator1;
			iterator1 = imp.getBody().getValues().iterator();
			if (topicName != "" && imp.getLocalName().startsWith(name) == true) {
				int count1=0,count2=0;
				while (iterator1.hasNext()) //处理一条规则
				{
					//isImpEnable=false;
					SWRLAtom atom1;
					atom1 = (SWRLAtom)iterator1.next();
					@SuppressWarnings("unused")
				    String text=atom1.getBrowserText();					
					if(text.contains("Template"))//规则中含有模板auto的信息都在template中出现时...
					{
						count1++;
						for (Iterator<String> its = templateName.iterator(); its
								.hasNext();) {
							String templateValue = its.next();
							templateValue = templateValue.replace(":", "(")
									+ ")";
							if (text.equals(templateValue)) {
								//isImpEnable = true;
								count2++;
								break;
							}
						}
					}
					else
						continue;
					//if(isImpEnable=false)
						//break;
				}
				if(count1==count2)
				{
					System.out.println("LocalName:"+imp.getLocalName());
					imp.enable();
				}
			}
			else if(topicName.equals("")&&imp.getLocalName().startsWith(name) == true)//用来处理通过模板推出主题的规则
			{
				boolean isImpEnable=false;				
				int count1=0,count2=0;
				while (iterator1.hasNext()) //处理一条规则
				{
					isImpEnable=false;
					SWRLAtom atom1;
					atom1 = (SWRLAtom)iterator1.next();
					@SuppressWarnings("unused")
				    String text=atom1.getBrowserText();					
					if(text.contains("Template"))
					{
						count1++;
						for (Iterator<String> its = templateName.iterator(); its
								.hasNext();) {
							String templateValue = its.next();
							templateValue = templateValue.replace(":", "(")
									+ ")";
							if (text.equals(templateValue)) {
								//isImpEnable = true;
								count2++;
								break;
							}
						}
					}
					else
						continue;
					//if(isImpEnable=false)
						//break;
				}
				if(count1==count2)
				{
					System.out.println("LocalName:"+imp.getLocalName());
					imp.enable();
				}

			}
		}
		ruleEngine.infer();
	}
	/**
	 * 通过规则来更换背景图片，目前主要针对背景场景
	 * @param model
	 * @param maName
	 * @param templateName
	 * @throws SWRLRuleEngineException
	 */
	public static void changeBackgroundPictureSky(OWLModel model, String maName,ArrayList<String> templateName) throws SWRLRuleEngineException
	{
		SWRLRuleEngine ruleEngine = createRuleEngine(model);
		ruleEngine.reset();
		SWRLFactory factory = createSWRLFactory(model);
		factory.disableAll();
		Iterator<SWRLImp> iter = factory.getImps().iterator();
		while(iter.hasNext())
		{
			SWRLImp imp = (SWRLImp) iter.next();
			if(imp.getLocalName().contains("changeBackgroundPicture"))
			{
				for (Iterator<String> its = templateName.iterator(); its
				.hasNext();) 
				{
					String templateValue=its.next();
					int iPostion=templateValue.indexOf(":");
					String strTemplateName=templateValue.substring(0,iPostion);
					if(imp.getBody().getBrowserText().contains(strTemplateName))
						imp.enable();
				}
			}
		}
		ruleEngine.infer();
	}
	
	/**
	 * 这个函数主要用来处理ma的增删改规则，也包括主题的局部规则，这跟原子没有关系
	 * @param model
	 * @param maName
	 * @param topicName
	 
	 * @throws SWRLRuleEngineException  (OKKK)
	 */
	public static void executeAddDeleteChangeSWRLEngine(OWLModel model, String maName,
			String topicName) throws SWRLRuleEngineException
	{
		SWRLRuleEngine ruleEngine = createRuleEngine(model);
		ruleEngine.reset();
		SWRLFactory factory = createSWRLFactory(model);
		factory.disableAll();
		Iterator<SWRLImp> iter = factory.getImps().iterator();
		ArrayList<SWRLImp> inferImp=new ArrayList();
		while (iter.hasNext()) {
			SWRLImp imp = (SWRLImp) iter.next();
			//if(imp.getLocalName().contains("AddRule")&&imp.getBody().getBrowserText().contains(maName))
			//{
			//	inferImp.add(imp);
			//}
           if(imp.getLocalName().contains("deleteRule")&&imp.getBody().getBrowserText().contains(maName))
			{
				inferImp.add(imp);
			}
			else if(imp.getLocalName().contains("ExchangeRule")&&imp.getBody().getBrowserText().contains(maName))
			{
				inferImp.add(imp);
			}
			else if(imp.getLocalName().contains("topicPrivateRule")&&imp.getBody().getBrowserText().contains(topicName)&&!topicName.equals(""))
			{
				inferImp.add(imp);
			}
		}
		logger.info("增删改规则中总的规则条数为:"+inferImp.size());
		Random rand=new Random();
		if(inferImp.size()>0){
		int kk=rand.nextInt(inferImp.size());//在总的规则数中随机选着一个数kk
		HashSet<Integer> set = new HashSet<Integer>();//在总的规则中随机选着kk条规则
        for (int i = 0; i <=kk; i++)
        {
            int t = (int) (Math.random() * inferImp.size());
            set.add(t);
        }
        Iterator iterator = set.iterator();
        while (iterator.hasNext())
        {
        	Integer num=(Integer)iterator.next();
        	SWRLImp impss = (SWRLImp)inferImp.get(num);
        	impss.enable();
            logger.info("在增删改规则中运行的规则名为："+inferImp.get(num).getLocalName());
        }
		}
		ruleEngine.infer();
	}
/**
 * 根据主题往场景中添加主题所对应的模型或特效
 * @param model
 * @param templateName
 * @throws SWRLRuleEngineException
 */	
	
	public  static void addModelFromTopicToScene(OWLModel model,String topicName) throws SWRLRuleEngineException
	{
		SWRLRuleEngine ruleEngine = createRuleEngine(model);
		ruleEngine.reset();
		SWRLFactory factory = createSWRLFactory(model);
		factory.disableAll();
		Iterator<SWRLImp> iter = factory.getImps().iterator();
		while(iter.hasNext())
		{
			SWRLImp imp = (SWRLImp) iter.next();
			if(imp.getLocalName().contains("addModelFromTopicToScene"))
			{
				if(imp.getBody().getBrowserText().contains(topicName))
				{
					logger.info("运行的规则名字为："+imp.getLocalName());
					imp.enable();
				}
			}
	}
		ruleEngine.infer();
}	
	
	
	
	
	/**
	 * 运行往场景里面添加模型的规则，包括背景场景和原始动画场景
	 * @param model
	 * @param templateName
	 * @throws SWRLRuleEngineException (OKKKK)
	 */
	public  static void executeTemplateToBackgroundSceneSWRLEngine(OWLModel model,ArrayList<String> templateName) throws SWRLRuleEngineException
	{
		SWRLRuleEngine ruleEngine = createRuleEngine(model);
		ruleEngine.reset();
		SWRLFactory factory = createSWRLFactory(model);
		factory.disableAll();
		Iterator<SWRLImp> iter = factory.getImps().iterator();
		int num=0;
		while(iter.hasNext())
		{
		//	System.out.println(++num);
			SWRLImp imp = (SWRLImp) iter.next();
			if (templateName.size() != 0) {
				if (imp.getLocalName().contains("addModelToBackground")) {
				//	int count=0;
					for (Iterator<String> its = templateName
							.iterator(); its.hasNext();) {
					//	System.out.println(++count);
						String templateValue = its.next();
						templateValue = templateValue.replace(":",
								"(")
								+ ")";
						templateValue=templateValue.substring(0, templateValue.indexOf("("));				
						if(imp.getBody().getBrowserText().contains(templateValue))
						{
							logger.info("运行的规则名字为："+imp.getLocalName());
							imp.enable();
						}
					
				}
			}
			
		}
	}
		ruleEngine.infer();
}
	/**
	 * 通过规则由模板推导动画场景（Ma）
	 * @param model
	 * @param templateName
	 * @throws SWRLRuleEngineException
	 */
	public static void executeGetMaFromTemplateUsingSWRLEngine(OWLModel model,ArrayList<String> templateName) throws SWRLRuleEngineException
	{
		SWRLRuleEngine ruleEngine = createRuleEngine(model);
		ruleEngine.reset();
		SWRLFactory factory = createSWRLFactory(model);
		factory.disableAll();
		Iterator<SWRLImp> iter = factory.getImps().iterator();
		while(iter.hasNext())
		{
			SWRLImp imp = (SWRLImp) iter.next();
			if (templateName.size() != 0) {
				if (imp.getLocalName().contains("getMaFromTemplate")) {
					for (Iterator<String> its = templateName
							.iterator(); its.hasNext();) {
						String templateValue = its.next();
						templateValue = templateValue.replace(":",
								"(")
								+ ")";
						if(imp.getBody().getBrowserText().contains(templateValue))
						{
							logger.info("运行的规则名字为："+imp.getLocalName());
							imp.enable();
						}
					
				}
			}
			
		}
	}
		ruleEngine.infer();
		
	}
	/**
	 * 用来处理有模板原子的规则，这类规则主要是使动画更符合短信的内容
	 * @param model
	 * @param maName：所选动画的ma名字
	 * @param topicName：主题名字
	 * @param templateName：模板名字
	 * @throws SWRLRuleEngineException(OKKKK)
	 */
	public static void executeTemplateSWRLEngine(OWLModel model, String maName,
			String topicName, ArrayList<String> templateName) throws SWRLRuleEngineException
	{
		SWRLRuleEngine ruleEngine = createRuleEngine(model);
		ruleEngine.reset();
		SWRLFactory factory = createSWRLFactory(model);
		factory.disableAll();
		Iterator<SWRLImp> iter = factory.getImps().iterator();
		while (iter.hasNext()) {
			SWRLImp imp = (SWRLImp) iter.next();
			if (templateName.size() != 0&&imp.getLocalName().contains("topicTemplate")) {
				// 这块处理主题的全局规则，即不管什么主题，只要里面有相应的原子，就可以往相应的ma中添加原子所对应的信息
				
					Iterator<SWRLAtom> iterator1;
					iterator1 = imp.getBody().getValues().iterator();
					int count1 = 0, count2 = 0;
					while (iterator1.hasNext()) {

						SWRLAtom atom1;
						atom1 = (SWRLAtom) iterator1.next();
						@SuppressWarnings("unused")
						String text = atom1.getBrowserText();
						if (text.contains("Template")) {
							count1++;
							for (Iterator<String> its = templateName.iterator(); its
									.hasNext();) {
								String templateValue = its.next();
								templateValue = templateValue.replace(":", "(")
										+ ")";
								if (text.equals(templateValue)) {
									// isImpEnable = true;
									count2++;
									break;
								} else
									continue;
							}

						}
					}
					if (count1 == count2) {
						System.out.println("LocalName:" + imp.getLocalName());
						imp.enable();
					}
			}
				
				 if (imp.getLocalName().contains("topicIndividualAddMa")
						&& imp.getBody().getBrowserText().contains(topicName)) {
					imp.enable();
				} else
					continue;
			
		}
		ruleEngine.infer();
	}
}
