package plot;

public class SceneCase {
	public String sceneName="";
	public double decisionvalue=0;
	public double MGProb=0;
	public double IEProb=0;
	public double ruleReason=0;
	public double templateRelated=0;
	public double templateModelNum=0;
	public double placableModelNum=0;
	public double isBackgroundScene=0;
	
	public double colorModelNum=0;
	public double placableColorModelNum=0;
	public double isWeatherable=0;
	public double timeable=0;
	public double indualScore=0;
	public double score=0;
	public double fullScore=0;
	public double ActionScore=0;
	
	public double QProb=0;
	
	
	public SceneCase(String sName)
	{
		sceneName = sName;
	}
	
	public SceneCase(	
			String sName,
			double MProb,
			double ieProb,
			double rReason,
			double tRelated,
			double tModelNum,
			double pModelNum0,
			double cModelNum,
			double pColorModelNum,			
			double isW,
			double QP,
			double isT,
			double actionScore,
			double isBG)
	{   
		
		sceneName = sName;
		MGProb =MProb;
		IEProb = ieProb;
		ruleReason =rReason;
		templateRelated = tRelated;

		templateModelNum=tModelNum;
		placableModelNum=pModelNum0;
		
		colorModelNum=cModelNum;
		placableColorModelNum=pColorModelNum;
		isWeatherable = isW;
		timeable=isT;
		QProb = QP;
		ActionScore=actionScore;
		isBackgroundScene=isBG;
	}
	

}
