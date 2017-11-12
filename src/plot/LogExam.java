package plot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 这是一个使用Log4j的示例
 * Log4j的配置文件是工程根目录下的log4j.properties
 * 输出的日志文件时工程根目录下的3dsma.log
 * 还需要引入三个jar包，log4j-1.2.14.jar、commons-logging-1.0.4.jar、commons-logging-api-1.1.jar
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
