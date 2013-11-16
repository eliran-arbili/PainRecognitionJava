package businessLogic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

	
	public static ArrayList<BasicMLDataSet> splitDataSet(File dataSet, int k, int inputCount, int outputCount) {
		ArrayList<BasicMLDataSet> kDataSets = new ArrayList<BasicMLDataSet>();
		try {
			FileReader frLines = new FileReader(dataSet);
			LineNumberReader lnr = new LineNumberReader(frLines);
			lnr.skip(Long.MAX_VALUE);
			int numOfSetLines =  (lnr.getLineNumber())/k;
			int remainder = lnr.getLineNumber() % k;
			int remainderAddition = (remainder == 0)? 0:1; 
			lnr.close();
			frLines.close();
			FileReader fr = new FileReader(dataSet);
			BufferedReader reader = new BufferedReader(fr);
			int index = 1;
			int dataSetIndex = 0;
			String line = null;
			kDataSets.add(new BasicMLDataSet());
			while((line = reader.readLine()) != null){
				if(index > numOfSetLines + remainderAddition){
					index = 1;
					dataSetIndex++;
					if(remainder > 1)
						remainder--;
					else
						remainderAddition = 0;
					
					kDataSets.add(new BasicMLDataSet());
				}
				
				ParseCSVLine csvParser = new ParseCSVLine(CSVFormat.ENGLISH);
				List<String> lineStrings = csvParser.parse(line);
				BasicMLData dataInput = new BasicMLData(inputCount);
				BasicMLData dataOutput = new BasicMLData(outputCount);
				for(int i = 0 ; i < inputCount + outputCount; i++){
					if(i < inputCount){
						dataInput.add(i, Double.parseDouble(lineStrings.get(i)));
					}
					else{
						dataOutput.add(i-inputCount, Double.parseDouble(lineStrings.get(i)));
					}
				}
				kDataSets.get(dataSetIndex).add(new BasicMLDataPair(dataInput,dataOutput));
				index++;
			}
			reader.close();
			fr.close();
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
}
