package dataLayer;


public class ProjectConfig {
	/*
	 * Default Configuration values
	 */
	
	public static final int NUMBER_OF_ACTION_UNITS = 11;
	public static final int AU_FUZZY_DEGREES = 10;
	enum ActionUnit{brow_raiser, nose_wrinkler /*....*/};
	public static final int HISTORY_CASES_SAVE_SIZE = 1000; // in MB
	public static final float FREQUENCY_CASE_SAMPLING = 5; // times per second
	public static String INSTALL_PATH = getInstallPath();
	public static double FUZZY_AU_MIN_LIMIT = -1;
	public static double FUZZY_AU_MAX_LIMIT = 1;
	public static double [] auWeights= new double[NUMBER_OF_ACTION_UNITS];
	public static double MAX_ANN_ERROR = 0.02;
	
	/*
	 * The Following can be changed in start-up
	 */
	public static String ANN_PARAMETERS_PATH = INSTALL_PATH+"\\PainNeuralNet.eg";
	public static int SERVER_PORT = 2222;
	public static int K_SIMILAR_CASES = 5;
	public static String DB_ADDRESS = "localhost";
	
/**
 * TO-DO: remove static weights initializations	
 */
	{
		for(int i=0;i<ProjectConfig.NUMBER_OF_ACTION_UNITS;i++)
		{
			auWeights[i]=1;
		}
	}
	
	public ProjectConfig()
	{
	}
	
	private static String getInstallPath(){
		String instPath = System.getenv("PRS_INSTALL_PATH");
		if(instPath == null){
			instPath = System.getProperty("user.dir"); // current work directory
		}
		return instPath;
	}

}
