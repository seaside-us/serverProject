package plot;

import org.dom4j.Document;
import org.dom4j.Node;

public interface XMLTest {
	/**
	 * ����node1��node2��xml�ļ�docu���в�����Ѱ��....
	 * @param docu
	 * @param node1
	 * @param node2
	 * @return
	 */
	public String find1(Document docu, String node1, String node2);
	/**
	 * ����ģ�����ֶ�xml�ļ�docu���в�����Ѱ��nodeName�����ԭ�ӽڵ�
	 * ��󷵻ص�һ��Ҫ ����ģ������nodeName��ԭ����Ϣ�����nodeNameҲ�п�����ԭ����Ϣ�ĸ��׽ڵ㣬������ģ���У�noedeName=�����¡����ó��Ľ���ǡ����£�ԭ����Ϣ��
	 * ע�����صĲ�һ����String����ȷ��
	 * ����ص�ģ������ô˷�������nodeName=������ص㡱���õ��ǡ�����ص㣺ԭ����Ϣ��ʱ��ģ���еġ����֡�����ʱ���롱����nodeName="����"��ϣ���õ������֣�1001���ȣ���nodeName=��ʱ���롱ʱ��ϣ���õ��Ľ����	��ʱ���룺12��50��30�롱��
	 * @param docu
	 * @param templateName
	 * @param nodeName
	 * @return  ���ص�node���Ӧ�����ƣ�<root name="�ص�" flag="" value="ѧУ:ѧУ"/>
	 */
	public Node find2(Document docu, String templateName, String nodeName);
	/**
	 * ����ģ�����ֶ�xml�ļ�docu���в�����Ѱ��nodeName1�ڵ������nodeName2�����ԭ�ӽڵ�
	 * ������ģ���У�nodeName=���˳ƴ��ʡ���nodeName2="��һ�˳Ƶ���"�����صĽ���ǡ��˳ƴ��ʣ���һ�˳Ƶ�����ԭ����Ϣ��
	 * @param docu
	 * @param templateName
	 * @param nodeName1
	 * @param nodeName2
	 * @return
	 */
	public String find3(Document docu, String templateName, String nodeName1,String nodeName2);
	
	

}
