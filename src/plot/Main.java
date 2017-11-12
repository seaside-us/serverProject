package plot;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

import org.apache.log4j.PropertyConfigurator;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLFactoryException;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;


public class Main {

	/**
	 * @param args
	 * @throws SWRLRuleEngineException 
	 * @throws OntologyLoadException 
	 * @throws SWRLFactoryException 
	 * @throws IOException 
	 * @throws SecurityException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SWRLFactoryException, OntologyLoadException, SWRLRuleEngineException, SecurityException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
		// TODO catch中恢复知识库（覆盖）
		 try{
			 ProgramEntrance.Entrance();
		 }
		 catch(Exception e)
		 {
			e.printStackTrace();
		 }
	}	
	public static void logConfig() {
		URL url = ClassLoader.getSystemResource("log4j.properties");//必须在类路径下
		PropertyConfigurator.configure(url);
	}

}
