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
	 * ���ÿ������
	 */
	public static void getSWRLRules(SWRLFactory factory) {
		Collection rules = factory.getImps();
		for (Iterator its = rules.iterator(); its.hasNext();) {
			SWRLImp swrlImp = (SWRLImp) its.next();
			System.out.println(swrlImp.getBrowserText());
		}
	}

	/**
	 * ����rule engine
	 * @throws SWRLRuleEngineException 
	 */
	public static SWRLRuleEngine createRuleEngine(OWLModel model)
			throws SWRLRuleEngineException {
		SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create("SWRLJessBridge", model);
		//SWRLRuleEngine ruleEngine = SWRLRuleEngineFactory.create(model);
		return ruleEngine;
	}
	/**
	 * ִ�й���ķ���������Ҫ�ǰѹ���ֳɼ����֣��ֲ����й���
	 * ��������ǹ�����Է�Ϊ4���֣�1��ʹ����ʵ����hasMa���Զ���ֵ
	 *   2�����������ģ��ԭ������Ӧ��ma������Ӧ�ı仯����Ҫ�����һЩ����������գ�ģ��ʱ���ϣ���������̻���Ч��
	 *   3��ma��ȫ�ֹ�����ñ�ӵ���ɫ���Ի�������ͬ�����͵�ñ�ӵȣ�����Ҫ������ma��һЩģ�ͽ��б仯
	 *   4��ma�ľֲ������������ÿ��ma���ģ���ÿ��ma������ɾ��
	 * name ��������������֣�maȫ�ֹ�������֣�Ҳ������ma�ֲ����������
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
		ArrayList<String> impName=new ArrayList();//�����洢��"_"�Ĺ���
		Iterator<SWRLImp> iter = factory.getImps().iterator();	
		while (iter.hasNext()) {
			SWRLImp imp = (SWRLImp) iter.next();
			//̎���һ�Ҏ�t
			if (rulename.startsWith("topicIndividualAddMa")&&imp.getLocalName().startsWith(rulename) == true) {
				imp.enable();
			}
			//����ma�ĸ��Ĺ�������Ҫע����Ǹ��Ĺ���һ��ֻ����һ����Ҫ��Ȼ��ӡ��ʱ�����ֻ���
			else if (rulename.contains("ExchangeRule")&&imp.getLocalName().contains(maName)&&imp.getLocalName().contains("ExchangeRule") == true) {
				impName.add(imp.getLocalName());
				temp=true;
			}
			//����ma�ľֲ�����
			else if(rulename.contains("AddRule")&&imp.getLocalName().contains(maName)&&imp.getLocalName().contains("Add") == true)
			{				
				imp.enable();
				
			}
			/*//����getTopicFromTemplate�Ĺ���
			else if(rulename.contains("getTopicFromTemplate")&&imp.getLocalName().startsWith("getTopicFromTemplate"))
			{
				imp.enable();
			}*/
		}
		//��impName�Ĵ�С������0ʱ��˵���������ƵĹ�����Ҫ����
		if(impName.size()!=0)
		{
			
				Random random=new  Random();
				
				if(impName.size()!=0)
				{
					int k=random.nextInt(impName.size());
				    String name_=impName.get(k);//��ͬһ����"_"�Ĺ�����ѡ��һ��
				    SWRLImp impNamepp=factory.getImp(name_);
				    impNamepp.enable();
				}
				
			
				
		}
		ruleEngine.reset();
		ruleEngine.infer();
		return temp;
	}
	/**
	 * ��ģ��ԭ�Ӷ�Ӧ��ģ����ӵ��ճ����У������ǰ����û������������
	 * @param model
	 * @param name������ģ�����
	 * @param topicName�����������
	 * @param templateName��ģ�������
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
				for(Iterator<String> its=templateName.iterator();its.hasNext();)//�����������ma���ַ��ϵĹ���
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
	 * ��������Ƕ��ϸ����������أ���Ҫ��������е���ȫ�ֵĹ���ȫ�ֹ�����������ģ����Ϣ
	 * 
	 * @param model
	 * @param name����Ҫ�ǹ��������(name=topicTemplateGlobeRule)
	 * @param templateName��ģ�������
	 * @param topicName�����������
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
				while (iterator1.hasNext()) //����һ������
				{
					//isImpEnable=false;
					SWRLAtom atom1;
					atom1 = (SWRLAtom)iterator1.next();
					@SuppressWarnings("unused")
				    String text=atom1.getBrowserText();					
					if(text.contains("Template"))//�����к���ģ��auto����Ϣ����template�г���ʱ...
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
			else if(topicName.equals("")&&imp.getLocalName().startsWith(name) == true)//��������ͨ��ģ���Ƴ�����Ĺ���
			{
				boolean isImpEnable=false;				
				int count1=0,count2=0;
				while (iterator1.hasNext()) //����һ������
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
	 * ͨ����������������ͼƬ��Ŀǰ��Ҫ��Ա�������
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
	 * ���������Ҫ��������ma����ɾ�Ĺ���Ҳ��������ľֲ��������ԭ��û�й�ϵ
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
		logger.info("��ɾ�Ĺ������ܵĹ�������Ϊ:"+inferImp.size());
		Random rand=new Random();
		if(inferImp.size()>0){
		int kk=rand.nextInt(inferImp.size());//���ܵĹ����������ѡ��һ����kk
		HashSet<Integer> set = new HashSet<Integer>();//���ܵĹ��������ѡ��kk������
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
            logger.info("����ɾ�Ĺ��������еĹ�����Ϊ��"+inferImp.get(num).getLocalName());
        }
		}
		ruleEngine.infer();
	}
/**
 * ���������������������������Ӧ��ģ�ͻ���Ч
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
					logger.info("���еĹ�������Ϊ��"+imp.getLocalName());
					imp.enable();
				}
			}
	}
		ruleEngine.infer();
}	
	
	
	
	
	/**
	 * �����������������ģ�͵Ĺ��򣬰�������������ԭʼ��������
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
							logger.info("���еĹ�������Ϊ��"+imp.getLocalName());
							imp.enable();
						}
					
				}
			}
			
		}
	}
		ruleEngine.infer();
}
	/**
	 * ͨ��������ģ���Ƶ�����������Ma��
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
							logger.info("���еĹ�������Ϊ��"+imp.getLocalName());
							imp.enable();
						}
					
				}
			}
			
		}
	}
		ruleEngine.infer();
		
	}
	/**
	 * ����������ģ��ԭ�ӵĹ������������Ҫ��ʹ���������϶��ŵ�����
	 * @param model
	 * @param maName����ѡ������ma����
	 * @param topicName����������
	 * @param templateName��ģ������
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
				// ��鴦�������ȫ�ֹ��򣬼�����ʲô���⣬ֻҪ��������Ӧ��ԭ�ӣ��Ϳ�������Ӧ��ma�����ԭ������Ӧ����Ϣ
				
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
