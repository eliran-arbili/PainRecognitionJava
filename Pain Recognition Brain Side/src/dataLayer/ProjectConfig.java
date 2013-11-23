package dataLayer;

import java.util.HashMap;

import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;


public class ProjectConfig {
	/*
	 * Default Configuration values
	 */
	public static final int CASE_OUTPUT_COUNT  = 1;
	public static final int NUMBER_OF_ACTION_UNITS = 11;
	public static final int AU_FUZZY_DEGREES = 10;
	public static final int HISTORY_CASES_SAVE_SIZE = 1000; // in MB
	public static String INSTALL_PATH = getInstallPath();
	public static double NORM_MIN_LIMIT = 0;
	public static double NORM_MAX_LIMIT = 1;
	public static double [] auWeights = initWeights(); 
	public static double MAX_ANN_ERROR = 0.02;
	public static HashMap<ActionUnit,NormalizedField> AURangeMap = mapAuRanges();
	public static double PAIN_SENSITIVITY = 0.75; // Between 0 (always) and 1 (never)
	public static final int RUN_TIME_K_FOLD = 2;
	public static boolean fuzzyMode=false;

	/*
	 * Config the workingMemory parameters
	 */
	public static final int WM_SIZE = 3;
	public static final int WM_SIMILARITY_THRESHOLD = 2;
	
	/*
	 * The Following can be changed in start-up
	 */
	public static String ANN_PARAMETERS_PATH ;//= INSTALL_PATH+"\\PainNeuralNet.eg";
	public static int SERVER_PORT = 2222;
	public static int K_SIMILAR_CASES = 80;
	public static String DB_ADDRESS = "localhost";
	
/**
 * TO-DO: remove static weights initializations	
 */

	
	public ProjectConfig()
	{
	}
	
	private static HashMap<ActionUnit,NormalizedField> mapAuRanges(){
		HashMap<ActionUnit,NormalizedField> mapping = new HashMap<ActionUnit,NormalizedField>();
		
		mapping.put(ActionUnit.NoseWrinkler, 		
				new NormalizedField(NormalizationAction.Normalize, "noseWrinkler", 
						0.5,-0.44, NORM_MAX_LIMIT,NORM_MIN_LIMIT));
		mapping.put(ActionUnit.Jawdrop,
				new NormalizedField(NormalizationAction.Normalize, "Jawdrop", 
						1.2,-0.05, NORM_MAX_LIMIT, NORM_MIN_LIMIT));
		mapping.put(ActionUnit.UpperLipRaiser,
				new NormalizedField(NormalizationAction.Normalize, "UpperLipRaiser", 
						1.47,-0.05,NORM_MAX_LIMIT, NORM_MIN_LIMIT));
		mapping.put(ActionUnit.LipStretcher,
				new NormalizedField(NormalizationAction.Normalize, "LipStretcher", 
						1,-0.94, NORM_MAX_LIMIT, NORM_MIN_LIMIT));	
		mapping.put(ActionUnit.LipCornerDepressor,
				new NormalizedField(NormalizationAction.Normalize, "LipCornerDepressor", 
						1.5,-1.28, NORM_MAX_LIMIT, NORM_MIN_LIMIT));
		mapping.put(ActionUnit.OuterBrowRaiser,
				new NormalizedField(NormalizationAction.Normalize, "OuterBrowRaiser", 
						0.98,-0.5, NORM_MAX_LIMIT, NORM_MIN_LIMIT));
		mapping.put(ActionUnit.InnerBrowRaiser,
				new NormalizedField(NormalizationAction.Normalize, "InnerBrowRaiser", 
						1,-1.2, NORM_MAX_LIMIT, NORM_MIN_LIMIT));
		mapping.put(ActionUnit.BrowLowerer,
				new NormalizedField(NormalizationAction.Normalize, "BrowLowerer", 
						0.61, -0.52,NORM_MAX_LIMIT, NORM_MIN_LIMIT));
		mapping.put(ActionUnit.EyesClosed,
				new NormalizedField(NormalizationAction.Normalize, "EyesClosed", 
						1,0, NORM_MAX_LIMIT, NORM_MIN_LIMIT));
		mapping.put(ActionUnit.RotateEyesLeft,
				new NormalizedField(NormalizationAction.Normalize, "RotateEyesLeft", 
						0.67,-0.49, NORM_MAX_LIMIT, NORM_MIN_LIMIT));
		mapping.put(ActionUnit.RotateEyesDown,
				new NormalizedField(NormalizationAction.Normalize, "RotateEyesDown", 
						0.65,-0.79, NORM_MAX_LIMIT, NORM_MIN_LIMIT));
		
		return mapping;
	}
	
	public static double []  initWeights(){
		double [] weights = new double[NUMBER_OF_ACTION_UNITS];
		weights[0] 		= 1.4;
		weights[1] 		= 1.1;
		weights[2] 		= 1.2;
		weights[3]		= 0.9;
		weights[4] 		= 0.9;
		weights[5]		= 1.3;
		weights[6]		= 1.3;
		weights[7]		= 1.4;
		weights[8]		= 1.2;
		weights[9]		= 0.5;
		weights[10]		= 0.5;
		
		for(int i=0;i<ProjectConfig.NUMBER_OF_ACTION_UNITS;i++)
		{
			weights[i]=1;
		}
		return weights;
	}
	
	private static String getInstallPath(){
		String instPath = System.getenv("PRS_INSTALL_PATH");
		if(instPath == null){
			instPath = System.getProperty("user.dir"); // current work directory
		}
		return instPath;
	}

}
