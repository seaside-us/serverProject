package plot;

import org.dom4j.Document;
import org.dom4j.Node;

public interface XMLTest {
	/**
	 * 利用node1和node2对xml文件docu进行操作，寻找....
	 * @param docu
	 * @param node1
	 * @param node2
	 * @return
	 */
	public String find1(Document docu, String node1, String node2);
	/**
	 * 根据模板名字对xml文件docu进行操作，寻找nodeName下面的原子节点
	 * 最后返回的一定要 包括模板名，nodeName和原子信息，这个nodeName也有可能是原子信息的父亲节点，如人物模板中，noedeName=“情侣”，得出的结果是“情侣：原子信息”
	 * 注：返回的不一定是String，待确定
	 * （如地点模板可以用此方法，如nodeName=“地理地点”，得到是“地理地点：原子信息”时间模板中的“数字”，“时分秒”，当nodeName="数字"，希望得到“数字：1001”等；当nodeName=“时分秒”时，希望得到的结果是	“时分秒：12点50分30秒”）
	 * @param docu
	 * @param templateName
	 * @param nodeName
	 * @return  返回的node结果应该类似：<root name="地点" flag="" value="学校:学校"/>
	 */
	public Node find2(Document docu, String templateName, String nodeName);
	/**
	 * 根据模板名字对xml文件docu进行操作，寻找nodeName1节点下面的nodeName2下面的原子节点
	 * 如人物模板中，nodeName=“人称代词”，nodeName2="第一人称单数"，返回的结果是“人称代词：第一人称单数：原子信息”
	 * @param docu
	 * @param templateName
	 * @param nodeName1
	 * @param nodeName2
	 * @return
	 */
	public String find3(Document docu, String templateName, String nodeName1,String nodeName2);
	
	

}
