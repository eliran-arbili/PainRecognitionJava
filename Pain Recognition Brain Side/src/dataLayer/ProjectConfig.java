package dataLayer;


public class ProjectConfig {
	public static final int SERVER_PORT = 2222;
	public static final int NUMBER_OF_ACTION_UNITS = 17;
	enum ActionUnits{brow_raiser, nose_wrinkler /*....*/};
	public static final int HISTORY_CASES_SAVE_SIZE = 1000; // in MB
	public static final float FREQUENCY_CASE_SAMPLING = 5; // times per second
	public static String ANN_PARAMETERS_PATH;
	public static String INSTALL_PATH;
	public ProjectConfig()
	{
		INSTALL_PATH = System.getenv("PRS_INSTALL_PATH");
		if(INSTALL_PATH.isEmpty()){
			INSTALL_PATH = System.getProperty("user.dir"); // current work directory
		}
		ANN_PARAMETERS_PATH = INSTALL_PATH+"\\RBFneuralNetwork.eg";
	}
}
