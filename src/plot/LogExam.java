package plot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ����һ��ʹ��Log4j��ʾ��
 * Log4j�������ļ��ǹ��̸�Ŀ¼�µ�log4j.properties
 * �������־�ļ�ʱ���̸�Ŀ¼�µ�3dsma.log
 * ����Ҫ��������jar����log4j-1.2.14.jar��commons-logging-1.0.4.jar��commons-logging-api-1.1.jar
 * @author Administrator
 *
 */
public class LogExam {

	public Log log;
	/**
	 * @param args
	 */
	
	public LogExam() {
		log = LogFactory.getLog(this.getClass());
	}
	
	//public static void main(String[] args) {
		//LogExam  = new LogExam();
		//le.log.debug("This is a log using example");
	//}

}
