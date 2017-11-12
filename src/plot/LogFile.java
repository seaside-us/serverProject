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
	/** 存放的文件夹 **/
    private static String file_name = "logFile";
   
    /**
     * 得到要记录的日志的路径及文件名称
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
     * 配置Logger对象输出日志文件路径
     * @param logger
     * @throws SecurityException
     * @throws IOException
     */
    public static void setLogingProperties(Logger logger) throws SecurityException, IOException {
        setLogingProperties(logger,Level.ALL);
    }
   
    /**
     * 配置Logger对象输出日志文件路径
     * @param logger
     * @param level 在日志文件中输出level级别以上的信息
     * @throws SecurityException
     * @throws IOException
     */
    public static void setLogingProperties(Logger logger,Level level) {
        FileHandler fh;
        try {
            fh = new FileHandler(getLogName(),true);
            logger.addHandler(fh);//日志输出文件
            //logger.setLevel(level);
            fh.setFormatter(new SimpleFormatter());//输出格式
            //logger.addHandler(new ConsoleHandler());//输出到控制台
        } catch (SecurityException e) {
            logger.log(Level.SEVERE, "安全性错误", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE,"读取文件日志错误", e);
        }
    }
    public static void printInfo(String message,Logger logger) throws SecurityException, IOException
    {
    	LogFile.setLogingProperties(logger);
        logger.log(Level.INFO, message);
    }
	
	

}
