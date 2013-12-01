package businessLogic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
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

public class ProjectUtils {
	
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

	
	public static ArrayList<File> splitDataSet(File dataSet, int k, int inputCount, int outputCount) throws IOException {
		ArrayList<File> kDataSets = new ArrayList<File>();
		FileReader 		fr 					= new FileReader(dataSet);
		BufferedReader reader 				= new BufferedReader(fr);
		int 			numberOfFileLines 	= getNumberOFLines(dataSet);
		int 			numOfSetLines 		= numberOfFileLines/ k;
		int 			remainder 			= numberOfFileLines % k;
		int 			remainderAddition 	= (remainder == 0)? 0:1; 
		int 			index 				= 1;
		int 			dataSetIndex 		= 0;
		String 			line 				= null;
		String 			prevLineId			= null;
		ParseCSVLine 	csvParser 			= new ParseCSVLine(CSVFormat.ENGLISH);
		String			fileName			= dataSet.getName().substring(0,dataSet.getName().lastIndexOf(".")) + "_sub";
		kDataSets.add(new File(dataSet.getParent()+"//"+fileName+(dataSetIndex+1)+".csv"));
		FileWriter 		fw					= new FileWriter(kDataSets.get(0));
		BufferedWriter 	writer				= new BufferedWriter(fw);

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
					fw.close();
					kDataSets.add(new File(dataSet.getParent()+"\\"+fileName+(dataSetIndex+1)+".csv"));
					fw			= new FileWriter(kDataSets.get(dataSetIndex));
					writer		= new BufferedWriter(fw);
				}

			}	
			writer.write(line+"\n");
			index++;
			prevLineId = lineStrings.get(lineStrings.size() - 1);
		}

		reader.close();
		fr.close();
		writer.close();
		fw.close();
		return kDataSets;
	}

	public static BasicMLDataSet convertCSVToDateSet(File csvDataSet, int inputCount, int outputCount, boolean headers) throws IOException{
		
		CSVNeuralDataSet csvNeuralDataSet = new CSVNeuralDataSet(csvDataSet.getAbsolutePath(), inputCount, outputCount, headers);
		BasicMLDataSet dataSet = new BasicMLDataSet(csvNeuralDataSet.getData());
		csvNeuralDataSet.close();
		return dataSet;
	}
	

	
    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)"+regex+"(?!.*?"+regex+")", replacement);
    }
    
    
	
	public static File normalizeCSVFile(File dataSetFile, int inputCount, int outputCount, boolean headers) throws IOException{

		int  numCols				= getNumberOFHeaderColumns(dataSetFile);
		String targetFileName 		= dataSetFile.getName();
		String targetFileDir 		= dataSetFile.getParent();
		targetFileName 				= targetFileName.substring(0,targetFileName.lastIndexOf("."));
		targetFileName += "_norm.csv";
		File targetFile 			= new File(combine(targetFileDir,targetFileName));
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
	
	public static List<AnalystField> getAnalystFieldsCSV(File dataSetFile){
		EncogAnalyst analyst 	= new EncogAnalyst();
		AnalystWizard wizard 	= new AnalystWizard(analyst);
		wizard.setGoal(AnalystGoal.Regression);
		wizard.setRange(NormalizeRange.Zero2One);
		wizard.wizard(dataSetFile, true, AnalystFileFormat.DECPNT_COMMA);
		
		fixEncogAnalystNormActionBug(analyst); /* Bug fix*/
		
		return analyst.getScript().getNormalize().getNormalizedFields();	
	}

	public static File removeDuplicateLines(File dataSet, int inputCount, int outputCount, boolean headers) throws IOException{
		FileReader 		fr 			= new FileReader(dataSet);
		BufferedReader 	reader 		= new BufferedReader(fr);
		String			fileSuffix	= dataSet.getName().substring(dataSet.getName().lastIndexOf("."));
		String 			fileName	= replaceLast(dataSet.getName(), fileSuffix, "_nodup"+fileSuffix);
		File 			newFile		= new File(dataSet.getParent()+"\\"+fileName);
		FileWriter 		fw			= new FileWriter(newFile);
		BufferedWriter 	writer		= new BufferedWriter(fw);
		ParseCSVLine 	csvParser 	= new ParseCSVLine(CSVFormat.ENGLISH);
		String 			line 		= null;
		if(headers){
			line = reader.readLine();
			writer.write(line+"\n");
			
		}
		
		ArrayList<RunTimeCase> existedCases = new ArrayList<RunTimeCase>();
		List<String> lineStrings = null;
		while((line = reader.readLine()) != null){
			lineStrings = csvParser.parse(line);
			RunTimeCase rtCase = resolveCase(lineStrings, inputCount, outputCount);
			if(! existedCases.contains(rtCase)){
				existedCases.add(rtCase);
				writer.append(line+"\n");
			}
		}
		writer.close();
		reader.close();
		fw.close();
		fr.close();
		return newFile;
	}
	
	public static String combine (String path1, String path2)
	{
	    File file1 = new File(path1);
	    File file2 = new File(file1, path2);
	    return file2.getPath();
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
		FileReader 				fr			= new FileReader(fromFile);
		BufferedReader	 		reader 		= new BufferedReader(fr);
		ParseCSVLine    		csvParser	= new ParseCSVLine(CSVFormat.ENGLISH);
		File					tempFile	= new File(toFile.getAbsoluteFile()+".temp");
		String 					line		= "";
		ArrayList<String>	toAdd			= new ArrayList<String>();
		while((line = reader.readLine()) != null){
			String addRow = "";
			List<String> lineStrings = csvParser.parse(line);
			for(int i = 0; i < startColumn - endColumn +1; i++){
				addRow += "," + lineStrings.get(startColumn + i -1);
			}
			toAdd.add(addRow);
		}
		reader.close();
		fr.close();
		fr							= new FileReader(toFile);
		reader 						= new BufferedReader(fr);
		FileWriter 		fw			= new FileWriter(tempFile);
		BufferedWriter 	writer		= new BufferedWriter(fw);
		int index					= 0;
		while((line = reader.readLine()) != null){
			line += toAdd.get(index);
			writer.append(line + "\n");
			index++;
		}
		writer.close();
		fw.close();
		reader.close();
		fr.close();
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
		return numOFLines;
	}
	
	/* This method is hard-coded workaround in order to fix Encog Bug*/

	private static void fixEncogAnalystNormActionBug(EncogAnalyst analyst){
		for(AnalystField af : analyst.getScript().getNormalize().getNormalizedFields()){
			if(af.getAction() != NormalizationAction.Ignore){
				af.setAction(NormalizationAction.Normalize);
			}
			if(af.getName().equals("eyesclosed")){ 
				af.setActualHigh(1.0);
			}
		}
	}
	
	public static void main(String[] args){
		File f = new File("C:\\Users\\earbili\\Desktop\\NeuralNets\\NEW_DataSet_FullAUS.csv");
		try {
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
