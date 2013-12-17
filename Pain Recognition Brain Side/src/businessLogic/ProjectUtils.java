package businessLogic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.encog.app.analyst.AnalystFileFormat;
import org.encog.app.analyst.AnalystGoal;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.csv.normalize.AnalystNormalizeCSV;
import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.app.analyst.wizard.AnalystWizard;
import org.encog.app.analyst.wizard.NormalizeRange;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.data.specific.CSVNeuralDataSet;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ParseCSVLine;

import dataLayer.ProjectConfig;

/**
 * ProjectUtils is  class that provide utilities for project by general functions that necessary preliminary phases  
 * @author Eliran Arbili , Arie Gaon
 */
public class ProjectUtils {
	
	/**
	 * Assertion statement used to enforce integrity and semantics
	 * @param statement to check
	 * @param description- what to report when statement does not hold
	 */
	public static void assertFalse(boolean statement, String description){
		if(statement == true){
			return;
		}
		else{
			Exception ex = new Exception("Assertion:" + description);
			ex.printStackTrace();
			System.exit(1);
		}
	}
	/**
	 * Given a BasicMLDataSet Object split it to k subsets
	 * @param dataSet to split  
	 * @param k- number of split partitions
	 * @return list of complementary subsets data sets BasicMLDataSet Objects
	 */
	public static ArrayList<BasicMLDataSet> splitDataSet(BasicMLDataSet dataSet, int k) {
		ArrayList<BasicMLDataSet> kDataSets = new ArrayList<BasicMLDataSet>();
		int index 			= 1;
		int dataSetIndex 	= 0;
		int remainder 		= dataSet.size() % k;
		int numOfLines		= dataSet.size() / k; 
		int remainderAddition = (remainder == 0)? 0:1; 
		kDataSets.add(new BasicMLDataSet());
		for(MLDataPair pair: dataSet){
			if(index > numOfLines + remainderAddition){
				index = 1;
				dataSetIndex++;
				if(remainder > 1)
					remainder--;
				else
					remainderAddition = 0;
				kDataSets.add(new BasicMLDataSet());
			}	
			kDataSets.get(dataSetIndex).add(pair);
			index++;
		}
		
		return kDataSets;
	}

	/**
	 * Given datSet file, split the it into k complementary subsets files
	 * The files saved in the same directory with sub<index> addition to file name.
	 * @param dataSet to split
	 * @param k- number of split partitions
	 * @param inputCount- number of inputs to neural network that targeted for this data set
	 * @param outputCount- number of outputs to neural network that targeted for this data set
	 * @param headers- weather file contain headers
	 * @return list of complementary subsets data sets files
	 * @throws IOException if any IO failure occur
	 */
	public static ArrayList<File> splitDataSet(File dataSet, int k, boolean headers) throws IOException {
		ArrayList<File> kDataSets = new ArrayList<File>();
		BufferedReader reader 				= Files.newBufferedReader(dataSet.toPath(),Charset.defaultCharset());
		int 			numberOfFileLines 	= getNumberOFLines(dataSet);
		int 			numOfSetLines 		= numberOfFileLines/ k;
		int 			remainder 			= numberOfFileLines % k;
		int 			remainderAddition 	= (remainder == 0)? 0:1; 
		int 			index 				= 1;
		int 			dataSetIndex 		= 0;
		String 			line 				= null;
		String 			prevLineId			= null;
		ParseCSVLine 	csvParser 			= new ParseCSVLine(CSVFormat.ENGLISH);
		kDataSets.add(generateFile(dataSet,"_sub"+(dataSetIndex+1)));
		BufferedWriter 	writer				= Files.newBufferedWriter(kDataSets.get(0).toPath(),Charset.defaultCharset());
		String 			lineHeaders				= "";
		if(headers){
			lineHeaders = reader.readLine();
			writer.write(lineHeaders + System.getProperty("line.separator"));
		}
		while((line = reader.readLine()) != null){	
			List<String> lineStrings = csvParser.parse(line);
			if(index > numOfSetLines + remainderAddition){
				if(! lineStrings.get(lineStrings.size() -1 ).equals(prevLineId)){
					index = 1;
					dataSetIndex++;
					if(remainder > 1)
						remainder--;
					else
						remainderAddition = 0;
					writer.close();
					kDataSets.add(generateFile(dataSet,"_sub"+(dataSetIndex+1)));
					writer		= Files.newBufferedWriter(kDataSets.get(dataSetIndex).toPath(),Charset.defaultCharset());
					if(headers){
						writer.write(lineHeaders + System.getProperty("line.separator"));
					}
				}

			}	
			writer.write(line+System.getProperty("line.separator"));
			index++;
			prevLineId = lineStrings.get(lineStrings.size() - 1);
		}

		reader.close();
		writer.close();
		return kDataSets;
	}

	/**
	 * Convert a CSV file to a BasicMLDataSet Object.
	 * @param csvDataSet to convert
	 * @param inputCount- number of inputs to neural network that targeted for this data set
	 * @param outputCount- number of outputs to neural network that targeted for this data set
	 * @param headers- weather file contain headers
	 * @return A BasicMLDataSet Object that contain a pairs of input,output for each case
	 * @throws IOException if any IO failure occur
	 */
	public static BasicMLDataSet convertCSVToDateSet(File csvDataSet, int inputCount, int outputCount, boolean headers) throws IOException{
		
		CSVNeuralDataSet csvNeuralDataSet = new CSVNeuralDataSet(csvDataSet.getAbsolutePath(), inputCount, outputCount, headers);
		BasicMLDataSet dataSet = new BasicMLDataSet(csvNeuralDataSet.getData());
		csvNeuralDataSet.close();
		return dataSet;
	}
	

	/**
	 * Replace last matching of regex in the text with replacement.
	 * @param text for replacing
	 * @param regex- the last pattern to replace
	 * @param replacement for pattern
	 * @return String with replacement
	 */
    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)"+regex+"(?!.*?"+regex+")", replacement);
    }
    
    
	/**
	 * Normalize CSV file according to the normalization configuration in config.properties file.
	 * The range of normalization and the minimum and maximum values for each AU is determined in config file
	 * @param dataSetFile to normalize
	 * @param inputCount- number of inputs to neural network that targeted for this data set
	 * @param outputCount- number of outputs to neural network that targeted for this data set
	 * @param headers- weather file contain headers
	 * @return Normalized file that can be used to train neural network
	 * @throws IOException if any IO failure occur
	 */
	public static File normalizeCSVFile(File dataSetFile, int inputCount, int outputCount, boolean headers) throws IOException{

		int  numCols				= getNumberOFHeaderColumns(dataSetFile);
		File targetFile 			= generateFile(dataSetFile, "_norm");
		EncogAnalyst analyst 		= new EncogAnalyst();
		AnalystWizard wizard 		= new AnalystWizard(analyst);
		wizard.setGoal(AnalystGoal.Regression);
		wizard.setTargetField(ProjectConfig.getOptArray("OUTPUT_FIELDS")[0]);
		if(ProjectConfig.getOptInt("NORM_MAX_LIMIT") == 1 && ProjectConfig.getOptInt("NORM_MIN_LIMIT") == 0){
			wizard.setRange(NormalizeRange.Zero2One);
		}
		else{
			wizard.setRange(NormalizeRange.NegOne2One);
		}
		wizard.wizard(dataSetFile, true, AnalystFileFormat.DECPNT_COMMA);
		
		setNormLimits(analyst);
		
		fixEncogAnalystNormActionBug(analyst); /* Bug fix*/
		
		final AnalystNormalizeCSV norm = new AnalystNormalizeCSV();
		norm.analyze(dataSetFile, true, CSVFormat.ENGLISH, analyst);
		norm.setInputFormat(CSVFormat.ENGLISH);
		norm.setProduceOutputHeaders(headers);
		norm.normalize(targetFile);

		if(numCols > inputCount + outputCount){
			addColumnsToCSV(dataSetFile, targetFile, inputCount + outputCount +1, numCols );
		}
		return targetFile;
	}
	
	/**
	 * Used to get the analyzed fields of a given CSV data set file
	 * Those fields can be used to extract min/max values of each column
	 * @param dataSetFile to analyze fields on
	 * @return list of AnalystField objects
	 */
	public static List<AnalystField> getAnalystFieldsCSV(File dataSetFile){
		EncogAnalyst analyst 	= new EncogAnalyst();
		AnalystWizard wizard 	= new AnalystWizard(analyst);
		wizard.setGoal(AnalystGoal.Regression);
		wizard.setRange(NormalizeRange.Zero2One);
		wizard.wizard(dataSetFile, true, AnalystFileFormat.DECPNT_COMMA);
		
		fixEncogAnalystNormActionBug(analyst); /* Bug fix*/
		
		return analyst.getScript().getNormalize().getNormalizedFields();	
	}

	/**
	 * Given a CSV dataSet file. use the RunTimeCase equality function to decide weather lines exists more than once.
	 * @param dataSet to remove duplicate lines from
	 * @param inputCount- number of inputs to neural network that targeted for this data set
	 * @param outputCount- number of outputs to neural network that targeted for this data set
	 * @param headers- weather file contain headers
	 * @return File without duplicated cases
	 * @throws IOException if any IO failure occur
	 */
	public static File removeDuplicateLines(File dataSet, int inputCount, int outputCount, boolean headers) throws IOException{
		BufferedReader 	reader 		= Files.newBufferedReader(dataSet.toPath(),Charset.defaultCharset());
		File 			newFile		= generateFile(dataSet,"_nodup");
		BufferedWriter 	writer		= Files.newBufferedWriter(newFile.toPath(),Charset.defaultCharset());
		ParseCSVLine 	csvParser 	= new ParseCSVLine(CSVFormat.ENGLISH);
		String 			line 		= null;
		if(headers){
			line = reader.readLine();
			writer.write(line+System.getProperty("line.separator"));
		}
		
		ArrayList<RunTimeCase> existedCases = new ArrayList<RunTimeCase>();
		List<String> lineStrings = null;
		while((line = reader.readLine()) != null){
			lineStrings = csvParser.parse(line);
			RunTimeCase rtCase = resolveCase(lineStrings, inputCount, outputCount);
			if(! existedCases.contains(rtCase)){
				existedCases.add(rtCase);
				writer.append(line+System.getProperty("line.separator"));
			}
		}
		writer.close();
		reader.close();
		return newFile;
	}
	
	/**
	 * Concatenate paths into 1 single legal path.
	 * @param path1- first path
	 * @param path2- second path
	 * @return concatenated path String
	 */
	public static String combine (String path1, String path2)
	{
	    File file1 = new File(path1);
	    File file2 = new File(file1, path2);
	    return file2.getPath();
	}
	
	/**
	 * Creates a single string that join elements with delimiter
	 * @param delimiter- will be joined between any 2 elements 
	 * @param elements to join into single String
	 * @return String joined by the delimiter
	 */
	public static String join(CharSequence delimiter, Iterable<? extends Object> elements){
	    StringBuilder builder = new StringBuilder();

	    if (elements != null)
	    {
	        java.util.Iterator<? extends Object> iter = elements.iterator();
	        if(iter.hasNext())
	        {
	            builder.append( String.valueOf( iter.next() ) );
	            while(iter.hasNext())
	            {
	                builder
	                    .append( delimiter )
	                    .append( String.valueOf( iter.next() ) );
	            }
	        }
	    }
	    return builder.toString();
	}
	
	/**
	 * Creates a single string that join double primitive array elements with delimiter
	 * @param delimiter- will be joined between any 2 elements 
	 * @param elements to join into single string
	 * @return String joined by the delimiter
	 */
	public static String joinDoubles(CharSequence delimiter, double [] elements){
	    StringBuilder builder = new StringBuilder();
	    if (elements != null && elements.length > 0)
	    {
            builder.append( String.valueOf( elements[0] ) );
	    	for(int i = 1 ; i < elements.length;i++){
	                
	    		builder.append( delimiter )
	                   .append( String.valueOf( elements[i] ) );
	        }
	    }
	    return builder.toString();
	}
	
	
	/**
	 * Concatenate 'addition' param to file name before suffix.
	 * @param source file
	 * @param addition to file name
	 * @return new File with the name addition
	 */
	public static File generateFile(File source, String addition){
		String targetFileName 		= source.getName();
		String targetFileDir 		= source.getParent();
		int dotIndex 				= targetFileName.lastIndexOf(".");
		String extension			= targetFileName.substring(dotIndex);
		targetFileName 				= targetFileName.substring(0,dotIndex);
		targetFileName 				+= addition + extension;
		return new File(combine(targetFileDir,targetFileName));
	}
	
	private static RunTimeCase resolveCase(List<String> lineStrings, int inputCount, int outputCount){
		double [] input=new  double[inputCount];
		double [] output=new  double[outputCount];
		for(int i = 0 ; i < inputCount + outputCount; i++){
			if(i < inputCount){
				input[i] = Double.parseDouble(lineStrings.get(i));
			}
			else{
				output[i - inputCount] = Double.parseDouble(lineStrings.get(i));
			}
		}
		return new RunTimeCase(input,output);
	}
	
	private static int getNumberOFHeaderColumns(File dataSet) throws IOException{
		FileReader 		fr 			= new FileReader(dataSet);
		BufferedReader 	reader 		= new BufferedReader(fr);
		String firstLine 			= reader.readLine();
		reader.close();
		fr.close();
		return firstLine.split(",").length;
	}
	
	private static void addColumnsToCSV(File fromFile, File toFile,int startColumn, int endColumn) throws IOException {
		BufferedReader	 		reader 		= Files.newBufferedReader(fromFile.toPath(),Charset.defaultCharset());
		ParseCSVLine    		csvParser	= new ParseCSVLine(CSVFormat.ENGLISH);
		File					tempFile	= new File(toFile.getAbsoluteFile()+".temp");
		String 					line		= "";
		ArrayList<String>		toAdd		= new ArrayList<String>();
		int 					numOfLines	= getNumberOFLines(fromFile);
		while((line = reader.readLine()) != null){
			String addRow = "";
			List<String> lineStrings = csvParser.parse(line);
			for(int i = 0; i < startColumn - endColumn +1; i++){
				addRow += "," + lineStrings.get(startColumn + i -1);
			}
			toAdd.add(addRow);
		}
		reader.close();
		reader 						= Files.newBufferedReader(toFile.toPath(),Charset.defaultCharset());
		BufferedWriter 	writer		= Files.newBufferedWriter(tempFile.toPath(),Charset.defaultCharset());
		int index					= 0;
		while((line = reader.readLine()) != null){
			line += toAdd.get(index);
			writer.append(line);
			index++;
			if(index < numOfLines){
				 writer.append(System.getProperty("line.separator"));
			}
		}
		writer.close();
		reader.close();
		toFile.delete();
		if(! tempFile.renameTo(toFile)){
			throw new IOException("Renaming Of temp file failed!");
		}
	}
	
	private static int getNumberOFLines(File file) throws IOException{
		FileReader frLines 		= new FileReader(file);
		LineNumberReader lnr 	= new LineNumberReader(frLines);
		lnr.skip(Long.MAX_VALUE);
		int numOFLines = lnr.getLineNumber();
		lnr.close();
		frLines.close();
		return numOFLines +1;
	}
	
	/* This method is hard-coded workaround in order to fix Encog Bug*/

	private static void fixEncogAnalystNormActionBug(EncogAnalyst analyst){
		for(AnalystField af : analyst.getScript().getNormalize().getNormalizedFields()){
			if(af.getAction() != NormalizationAction.Ignore){
				af.setAction(NormalizationAction.Normalize);
			}
		}
	}
	
	private static void setNormLimits(EncogAnalyst analyst){
		Double minLimits[] 	= ProjectConfig.getOptDoubleArray("AUS_NORM_MIN");
		Double maxLimits[] 	= ProjectConfig.getOptDoubleArray("AUS_NORM_MAX");
		String auNames[] 	= ProjectConfig.getOptArray("AUS");
		for(AnalystField af : analyst.getScript().getNormalize().getNormalizedFields()){
			int nameIndex = findNameIndex(auNames,af.getName());
			if(nameIndex != -1){
				af.setActualLow(minLimits[nameIndex]);
				af.setActualHigh(maxLimits[nameIndex]);
			}
		}
	}
	
	private static int findNameIndex(String[] names, String toFind) {
		for(int index = 0 ; index < names.length; index++){
			if(toFind.equalsIgnoreCase(names[index])){
				return index;
			}
		}
		return -1;
	}

	public static void main(String[] args){
		File f = new File("C:\\Users\\earbili\\Desktop\\NeuralNets\\NEW_DataSet_FullAUS- Edited.csv");
		
		try {
			System.out.println(getNumberOFLines(f));
/*			List<AnalystField> l = getAnalystFieldsCSV(f);
			for(AnalystField af: l){
				System.out.println(af);
			}*/
			File norm = normalizeCSVFile(f, 11, 1, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
