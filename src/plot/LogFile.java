package plot;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
public class LogFile {
	/** ��ŵ��ļ��� **/
    private static String file_name = "logFile";
   
    /**
     * �õ�Ҫ��¼����־��·�����ļ�����
     * @return
     */
 
    
    private static String getLogName() {
        StringBuffer logPath = new StringBuffer();
        logPath.append(System.getProperty("user.dir"));
        logPath.append("\\"+file_name);
        File file = new File(logPath.toString());
        if (!file.exists())
            file.mkdir();
       
      //  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      //  logPath.append("\\"+sdf.format(new Date())+".log");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        System.out.println("\n" + sdf.format(new Date()) + ".log");
        logPath.append("\\"+sdf.format(new Date())+".log");

        return logPath.toString();
    }
   
    /**
     * ����Logger���������־�ļ�·��
     * @param logger
     * @throws SecurityException
     * @throws IOException
     */
    public static void setLogingProperties(Logger logger) throws SecurityException, IOException {
        setLogingProperties(logger,Level.ALL);
    }
   
    /**
     * ����Logger���������־�ļ�·��
     * @param logger
     * @param level ����־�ļ������level�������ϵ���Ϣ
     * @throws SecurityException
     * @throws IOException
     */
    public static void setLogingProperties(Logger logger,Level level) {
        FileHandler fh;
        try {
            fh = new FileHandler(getLogName(),true);
            logger.addHandler(fh);//��־����ļ�
            //logger.setLevel(level);
            fh.setFormatter(new SimpleFormatter());//�����ʽ
            //logger.addHandler(new ConsoleHandler());//���������̨
        } catch (SecurityException e) {
            logger.log(Level.SEVERE, "��ȫ�Դ���", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE,"��ȡ�ļ���־����", e);
        }
    }
    public static void printInfo(String message,Logger logger) throws SecurityException, IOException
    {
    	LogFile.setLogingProperties(logger);
        logger.log(Level.INFO, message);
    }
	
	

}
