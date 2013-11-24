package dataLayer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.TreeMap;

import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;


public class ProjectConfig {
	
	private static TreeMap<String,String> defaultConfigurations = initDefaultConfigurations();
	private final static ProjectConfig instance = new ProjectConfig();
	private Properties props;
	/*
	 * Default Configuration values
	 */
	public static final int CASE_OUTPUT_COUNT  = 1;
	public static final int NUMBER_OF_ACTION_UNITS = 11;
	public static final int AU_FUZZY_DEGREES = 10;
	public static String INSTALL_PATH = getInstallPath();
	public static double NORM_MIN_LIMIT = 0;
	public static double NORM_MAX_LIMIT = 1;
	public static double [] auWeights = initWeights(); 
	public static double PAIN_SENSITIVITY = 0.75; // Between 0 (always) and 1 (never)
	public static final int RUN_TIME_K_FOLD = 2;
	public static boolean fuzzyMode = false;
	
	
	public HashMap<String,NormalizedField> AURangeMap;
	
	/*
	 * The Following can be changed in start-up
	 */
	public static String ANN_PARAMETERS_PATH ;//= INSTALL_PATH+"\\PainNeuralNet.eg";
	public static int SERVER_PORT = 2222;
	public static int K_SIMILAR_CASES = 80;
	public static String DB_ADDRESS = "localhost";

	
	private ProjectConfig()
	{
		props = initProperties();
		AURangeMap = mapAuRanges();
	}
	
	private Properties initProperties(){
		Properties prop = new Properties();
		try {
			  InputStream inputStream = 
					    this.getClass().getClassLoader().getResourceAsStream("dataLayer/resources/config.properties");
			  prop.load(inputStream);
			  inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return prop;
	}
	private static TreeMap<String, String> initDefaultConfigurations() {
		TreeMap<String,String> defaultConf = new TreeMap<String,String>();
		defaultConf.put("AUS", 	
				"NoseWrinkler,Jawdrop,UpperLipRaiser,LipStretcher,"+
				"LipCornerDepressor,OuterBrowRaiser,InnerBrowRaiser,"+
				"BrowLowerer,EyesClosed,RotateEyesLeft,RotateEyesDown");
		defaultConf.put("similarityWeights", "1,1,1,1,1,1,1,1,1,1,1");
		defaultConf.put("CASE_OUTPUT_COUNT", "1");
		defaultConf.put("NUMBER_OF_ACTION_UNITS", "11");
		defaultConf.put("AU_FUZZY_DEGREES", "10");
		defaultConf.put("NORM_MIN_LIMIT", "0");
		defaultConf.put("NORM_MAX_LIMIT", "1");
		defaultConf.put("PAIN_SENSITIVITY", "0.75");
		defaultConf.put("RUN_TIME_K_FOLD", "2");
		defaultConf.put("FUZZY_MODE", "false");
		defaultConf.put("SERVER_PORT", "2222");
		defaultConf.put("K_SIMILAR_CASES", "80");
		defaultConf.put("DB_ADDRESS", "localhost");
		defaultConf.put("AUS_NORM_MIN", "-0.44,-0.05,-0.05,-0.94,-1.28,-0.5,-1.2,-0.52,0,-0.49,-0.79");
		defaultConf.put("AUS_NORM_MAX", "0.5,1.2,1.47,1,1.5,0.98,1,0.61,1,0.67,0.65");
		return defaultConf;
	}
	
	public String getOpt(String opt){
		String property;
		String defaultOpt = getDefaultOpt(opt);
		if(defaultOpt != null){
			property = props.getProperty(opt, defaultOpt);
		}
		else{
			property = props.getProperty(opt);
		}
		return property;
	}
	public String [] getOptArray(String opt){
		String rawProperty;
		String [] propertyArray = null;
		String defaultOpt = getDefaultOpt(opt);
		if(defaultOpt != null){
			rawProperty = props.getProperty(opt,defaultOpt);
		}
		else{
			rawProperty = props.getProperty(opt);
		}
		if(rawProperty != null){
			propertyArray = rawProperty.split(",");
		}
		return propertyArray;
	}
	
	public Double getOptDouble(String opt){
		Double property = null;
		try{
			property = Double.parseDouble(getOpt(opt));
			return property;
		}
		catch(NumberFormatException ex){
			return null;
		}
	}
	public Double[] getOptDoubleArray(String opt){
		Double [] property = null;
		try{
			String [] stringArrayProps = getOptArray(opt);
			property = new Double[stringArrayProps.length];
			for(int i = 0 ; i < property.length ; i++){
				property[i] = Double.parseDouble(stringArrayProps[i]);
			}
			return property;
		}catch(NumberFormatException ex){
			return null;
		}
	}
	
	public Boolean getOptBool(String opt){
		String rawProperty = getOpt(opt);
		Boolean property = null;
		if(rawProperty.equalsIgnoreCase("false")){
			property =  false;
		}
		else if (rawProperty.equalsIgnoreCase("true")){
			property =  true;
		}
		return property;
	}
	
	
	public static String getDefaultOpt(String opt){
		return defaultConfigurations.get(opt);
	}
	
	private HashMap<String,NormalizedField> mapAuRanges(){
		HashMap<String,NormalizedField> mapping = new HashMap<String,NormalizedField>();
		
		String [] aus = getOptArray("AUS");
		Double [] ausNormMin = getOptDoubleArray("AUS_NORM_MIN");
		Double [] ausNormMax = getOptDoubleArray("AUS_NORM_MAX");
		Double normMaxLimit = getOptDouble("NORM_MAX_LIMIT");
		Double normMinLimit = getOptDouble("NORM_MIN_LIMIT");
		for(int i = 0 ; i < aus.length ; i++){
			mapping.put(aus[i], new NormalizedField(NormalizationAction.Normalize, aus[i],
					ausNormMax[i],ausNormMin[i],normMaxLimit,normMinLimit));
		}
		return mapping;
	}
	
	/*private static HashMap<ActionUnit,NormalizedField> mapAuRanges(){
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
	}*/
	
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
	
	private static void writeDefaultPropertiesFile(){
		@SuppressWarnings("serial")
		Properties prop = new Properties() {
		    @Override
		    public synchronized java.util.Enumeration<Object> keys() {
		        return java.util.Collections.enumeration(new java.util.TreeSet<Object>(super.keySet()));
		    }
		};
		TreeMap<String, String> defaultProps = initDefaultConfigurations();
		prop.putAll(defaultProps);
		try{
			prop.store(new FileOutputStream("config.properties"),"Project Configurations");
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	
	private static String getInstallPath(){
		String instPath = System.getenv("PRS_INSTALL_PATH");
		if(instPath == null){
			instPath = System.getProperty("user.dir"); // current work directory
		}
		return instPath;
	}
	
	public static void main(String[] args){
		writeDefaultPropertiesFile();
	}

}
