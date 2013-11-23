package businessLogic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.encog.ConsoleStatusReportable;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ParseCSVLine;
import org.encog.util.normalize.DataNormalization;
import org.encog.util.normalize.input.InputFieldCSV;
import org.encog.util.normalize.output.OutputFieldRangeMapped;
import org.encog.util.normalize.target.NormalizationStorageCSV;

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

	
	public static ArrayList<File> splitDataSet(File dataSet, int k, int inputCount, int outputCount, boolean headers, boolean idForEachLine) {
		ArrayList<File> kDataSets = new ArrayList<File>();
		try {
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

			if(headers){
				reader.readLine();
			}
			while((line = reader.readLine()) != null){	
				List<String> lineStrings = csvParser.parse(line);
				RunTimeCase rtCase = resolveCase(lineStrings, inputCount, outputCount);
				if(ProjectConfig.fuzzyMode)
					rtCase.fuzzify();
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
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
	public static BasicMLDataSet convertCSVToDateSet(File csvDataSet, int inputCount, int outputCount, boolean headers) throws IOException{
		FileReader 		fr 			= new FileReader(csvDataSet);
		BufferedReader 	reader 		= new BufferedReader(fr);
		String 			line		= null;
		ParseCSVLine 	csvParser 	= new ParseCSVLine(CSVFormat.ENGLISH);
		List<String> 	lineStrings = null;
		BasicMLDataSet	dataSet		= new BasicMLDataSet();
		if(headers){
			line = reader.readLine();			
		}
		while((line = reader.readLine()) != null){
			lineStrings = csvParser.parse(line);
			RunTimeCase rtCase = resolveCase(lineStrings, inputCount, outputCount);
            BasicMLData dataInput = new BasicMLData(rtCase.getActionUnits());
            BasicMLData dataOutput = new BasicMLData(rtCase.getSolutionOutput());
            dataSet.add(new BasicMLDataPair(dataInput,dataOutput));
		}
		reader.close();
		fr.close();	
		return dataSet;
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

	
    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)"+regex+"(?!.*?"+regex+")", replacement);
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
	
	public static File normalizeCSVFile(File dataSetFile, int inputCount, int outputCount){
		DataNormalization norm = new DataNormalization();
		for(int i = 0; i < inputCount + outputCount ; i++){
			InputFieldCSV inputField = new InputFieldCSV(true,dataSetFile,i);
			norm.addInputField(inputField);
			norm.addOutputField(new OutputFieldRangeMapped(inputField,0,1));
		}
		norm.setCSVFormat(CSVFormat.ENGLISH);
		String targetFileName = dataSetFile.getName();
		String targetFileDir = dataSetFile.getParent();
		targetFileName = targetFileName.substring(0,targetFileName.lastIndexOf("."));
		targetFileName += "_norm.csv";
		File targetFile = new File(targetFileDir+"//"+targetFileName);
		norm.setTarget(new NormalizationStorageCSV(CSVFormat.ENGLISH,targetFile));
		norm.setReport(new ConsoleStatusReportable());
		norm.process();
		return targetFile;
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
	
	public static void main(String[] args){
		try {
			File f =removeDuplicateLines(new File("C:\\Users\\earbili\\Desktop\\NeuralNets\\NEW_DataSet_FullAUS.csv"),11,1,true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
