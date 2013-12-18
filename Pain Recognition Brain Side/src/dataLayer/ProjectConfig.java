package dataLayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.TreeMap;

import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;

import businessLogic.ProjectUtils;

/**
 * Responsible on the definition of project configuration. 
 * Most of configurations loaded from properties file that exist in installation folder in user home   
 * @author Eliran Arbili , Arie Gaon
 *
 */

public class ProjectConfig {
	
	/*
	 * Variables holds the path locations of major project components
	 */
	public  static String INSTALL_PATH  = createInstallationFolders() ;
	public 	static String TRAINING_TAGS_PATH;
	public  static String DATASETS_PATH;
	private static String PROPERTIES_PATH;

	private static TreeMap<String,String> defaultConfigurations = initDefaultConfigurations();
	private static Properties props = initProperties();
	public  static HashMap<String,NormalizedField> AUNormFields = mapAuRanges();
	private static File currentTag;

	/*
	 * Member functions
	 */
	
	/**
	 * Get configuration as string given key opt
	 * @param opt property key
	 * @return property value as string or null if not exists
	 */
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
	/**
	 * Get configuration as string array given key opt
	 * @param opt property key
	 * @return property value as array string or null if not exists
	 */
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
	
	/**
	 * Get configuration as Double given key opt
	 * @param opt property key
	 * @return property value as Double or null if not exists
	 */
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
	
	/**
	 * Get configuration as Double array given key opt
	 * @param opt property key
	 * @return property value as Double array or null if not exists
	 */
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
	
	/**
	 * Get configuration as Integer given key opt
	 * @param opt property key
	 * @return property value as Integer or null if not exists
	 */
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
	
	/**
	 * Get configuration as Boolean given key opt
	 * @param opt property key
	 * @return property value as Boolean or null if not exist
	 */
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
	
	/**
	 * Set configuration value given a key opt
	 * @param opt property key
	 * @param value property value
	 */
	public static void setOpt(String opt, String value){
		props.setProperty(opt, value);
	}
	
	/**
	 * Store  properties file  
	 * @return true if the store success , false if not
	 */
	public static boolean saveCurrentConfig() {
		try 
		{
			props.store(new FileOutputStream(PROPERTIES_PATH),getPropertiesRules());
			return true;
		} 
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Get property key and return default value defined within the default project configurations
	 * @param opt property key
	 * @return default value that generate in code
	 */
	public static String getDefaultOpt(String opt){
		return defaultConfigurations.get(opt);
	}
	
	/**
	 * Get the current training Tags defined by the User earlier.
	 * @return list of tag files
	 */
	public static File [] getTrainingTags(){
		File tagsDir = new File(TRAINING_TAGS_PATH);
		return tagsDir.listFiles();
	}
	
	/**
	 * Get the persistence Encog Neural network file correspond to a tag folder
	 * The network can be loaded later to an actual Object
	 * @param tagDir
	 * @return Encog Persistence neural network file
	 */
	public static File getANNFileByTag(File tagDir){
		for(File f: tagDir.listFiles()){
			if(f.getName().endsWith(".eg")){
				return f;
			}
		}
		return null;
	}
	
	/**
	 * Get the CSV dataset correspond to a tag folder.
	 * @param tagDir
	 * @return CSV dataset file
	 */
	public static File getCSVByTag(File tagDir){
		for(File f: tagDir.listFiles()){
			if(f.getName().endsWith(".csv")){
				return f;
			}
		}
		return null;
	}
	
	/**
	 * Get the current used tag
	 * @return tag folder
	 */
	public static File getCurrentTag() {
		return currentTag;
	}
	
	/**
	 * update the current used tag
	 * @param trainingTag
	 */
	public static void setCurrentTag(File trainingTag) {
		currentTag = trainingTag;
	}
	
	/*
	 * Auxiliary methods
	 */
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
		defaultConf.put("AUS","au_nose_wrinkler,au_jaw_drop,au_upper_lip_raiser,au_lip_stretcher,au_lip_corner_depressor,au_outer_brow_raiser,au_inner_brows_raiser,au_brow_lowerer,au_eyes_closed,au_rotate_eyes_left,au_rotate_eyes_down");
		defaultConf.put("SIMILARITY_WEIGHTS", "1,1,1,1,1,1,1,1,1,1,1");
		defaultConf.put("CASE_OUTPUT_COUNT", "1");
		defaultConf.put("CYCLES_FOR_ALARM", "3");
		defaultConf.put("NUMBER_OF_ACTION_UNITS", "11");
		defaultConf.put("AU_FUZZY_DEGREES", "10");
		defaultConf.put("NORM_MIN_LIMIT", "0");
		defaultConf.put("NORM_MAX_LIMIT", "1");
		defaultConf.put("PAIN_SENSITIVITY", "0.75");
		defaultConf.put("RUN_TIME_K_FOLD", "2");
		defaultConf.put("FUZZY_MODE", "false");
		defaultConf.put("SERVER_PORT", "2222");
		defaultConf.put("K_SIMILAR_CASES", "80");
		defaultConf.put("AUS_NORM_MIN", "-0.44,-0.05,-0.05,-0.94,-1.28,-0.5,-1.2,-0.52,0,-0.49,-0.79");
		defaultConf.put("AUS_NORM_MAX", "0.5,1.2,1.47,1,1.5,0.98,1,0.61,1,0.67,0.65");
		defaultConf.put("OUTPUT_FIELDS","Result");
		defaultConf.put("VENDOR_AUS",	
				"au_nose_wrinkler,au_jaw_z_push,au_jaw_x_push,au_jaw_drop,au_lower_lip_drop," +
				"au_upper_lip_raiser,au_lip_stretcher,au_lip_corner_depressor,au_lip_presser,"+
				"au_outer_brow_raiser,au_inner_brows_raiser,au_brow_lowerer,au_eyes_closed,"+
				"au_lid_tightener,au_upper_lid_raiser,au_rotate_eyes_left,au_rotate_eyes_down");
		defaultConf.put("DEBUG_MODE", "true");
		defaultConf.put("CASES_SAVE_HISTORY","20");
		return defaultConf;
	}
	
	private static String getPropertiesRules(){
		String rules = "# General Rules:\n---------------\n";
		rules +=	"# 1) AUS must be a subset of VENDOR_AUS\n";
		rules +=	"# 2) NUMBER_OF_ACTION_UNITS must be the number of defined AUS\n";
		rules +=	"# 3) Number of AUS_NORM_MAX and AUS_NORM_MIN must be NUMBER_OF_ACTION_UNITS\n";
		rules +=	"# 4) CASE_OUTPUT_COUNT must be the number of defined OUTPUT_FIELDS\n";
		rules +=	"# 4) NORM_MIN_LIMIT should be either 0 or -1\n";
		rules +=	"# 5) NORM_MAX_LIMIT shouldn't be changed\n";
		return rules;
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
			prop.store(new FileOutputStream(PROPERTIES_PATH),getPropertiesRules());
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	
	private static String createInstallationFolders(){
	     javax.swing.JFileChooser fr = new javax.swing.JFileChooser();
	     javax.swing.filechooser.FileSystemView fw = fr.getFileSystemView();
	     INSTALL_PATH = ProjectUtils.combine(fw.getDefaultDirectory().getAbsolutePath(), "PainRecognition");
	     TRAINING_TAGS_PATH	= ProjectUtils.combine(INSTALL_PATH, "Training_Tags");
	     DATASETS_PATH	= ProjectUtils.combine(INSTALL_PATH,"Data_Sets");
	     PROPERTIES_PATH = ProjectUtils.combine(INSTALL_PATH,"config.properties");
	     checkAndCreateFolders(INSTALL_PATH,TRAINING_TAGS_PATH,DATASETS_PATH);
	     
	     if(! new File(PROPERTIES_PATH).exists()){
	    	 writeDefaultPropertiesFile();
	     }
	     return INSTALL_PATH;
	}
	
	
	private static void checkAndCreateFolders(String ...paths) {
		for(String p:paths){
			File f = new File(p);
			if(! f.isDirectory()){
				f.mkdirs();
			}
		}
		
	}
	public static void main(String[] args){
		writeDefaultPropertiesFile();
	}



}
