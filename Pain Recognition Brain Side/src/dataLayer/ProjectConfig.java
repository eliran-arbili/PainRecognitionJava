package dataLayer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.TreeMap;

import org.encog.util.ResourceLoader;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;


public class ProjectConfig {
	
	private static TreeMap<String,String> defaultConfigurations = initDefaultConfigurations();
	public  static String INSTALL_PATH =  System.getProperty("user.home") + "\\PainRecogntion";
	private static String PROPERTIES_PATH = INSTALL_PATH + "\\config.properties";
	private static Properties props = initProperties();
	public static HashMap<String,NormalizedField> AUNormFields = mapAuRanges();

	/*
	 * Default Configuration values
	 */
	public static boolean fuzzyMode = false;
	
	
	
	/*
	 * The Following can be changed in start-up
	 */
	
	private ProjectConfig()
	{
	}
	

	
	private static Properties initProperties(){
		Properties prop = new Properties();
		try {
			  prop.load(new FileInputStream(PROPERTIES_PATH));
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
		defaultConf.put("SIMILARITY_WEIGHTS", "1,1,1,1,1,1,1,1,1,1,1");
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
		defaultConf.put("OUTPUT_FIELDS","Result");
		return defaultConf;
	}
	
	public static String getOpt(String opt){
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
	public static String [] getOptArray(String opt){
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
	
	public static Double getOptDouble(String opt){
		Double property = null;
		try{
			property = Double.parseDouble(getOpt(opt));
			return property;
		}
		catch(NumberFormatException ex){
			return null;
		}
	}
	public static Double[] getOptDoubleArray(String opt){
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
	
	public static Integer getOptInt(String opt){
		Integer property = null;
		try{
			property = Integer.parseInt(getOpt(opt));
			return property;
		}
		catch(NumberFormatException ex){
			return null;
		}
	}
	
	public static Boolean getOptBool(String opt){
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
	
	public static void setOpt(String opt, String value){
		props.setProperty(opt, value);
	}
	
	
	public static String getDefaultOpt(String opt){
		return defaultConfigurations.get(opt);
	}
	
	private static HashMap<String,NormalizedField> mapAuRanges(){
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
			String propertiesLocation = INSTALL_PATH + "\\config.properties";
			prop.store(new FileOutputStream(propertiesLocation),"Project Configurations");
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
