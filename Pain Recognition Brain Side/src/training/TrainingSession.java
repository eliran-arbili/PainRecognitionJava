package training;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.encog.ConsoleStatusReportable;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.strategy.end.EarlyStoppingStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.rbf.RBFNetwork;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ParseCSVLine;
import org.encog.util.normalize.DataNormalization;
import org.encog.util.normalize.input.InputFieldCSV;
import org.encog.util.normalize.output.OutputFieldRangeMapped;
import org.encog.util.normalize.target.NormalizationStorageCSV;

import businessLogic.ProjectUtils;


public class TrainingSession {
	
	boolean normalized;
	private File  dataSetNormFile;
	private File dataSetFile;
	public HashMap<ConfKeys,Object> confValues;
	public static final int RBF_TYPE = 0;
	public static final int MLP_TYPE = 1;
	

	public TrainingSession(File dataSet) {
		this.dataSetFile = dataSet;
		this.normalized = false;

	}
	
	public File getDataSetFile() {
		return dataSetFile;
	}
	
	public void setConfValues(HashMap<ConfKeys,Object> trainingConfigurations){
		this.confValues = trainingConfigurations;
	}
	
	/**
	 * 
	 * @param k-  for k-folds
	 * @return the path to Encog .eg file with minimum error
	 * @throws Exception (if k<2)
	 */
	public String kFoldsCrossValidationTrain(int k){
		ProjectUtils.assertFalse((k>=2), "Input Error k<2");
		
		switch((int)confValues.get(ConfKeys.neyType)){
			case MLP_TYPE: 
				ArrayList<BasicMLDataSet> kFoldsDataSet = splitDataSet(dataSetNormFile, k, (int)confValues.get(ConfKeys.inputCount), (int)confValues.get(ConfKeys.outputCount));
				ArrayList<NeuralNetDesciptor> kNetworks = new ArrayList<NeuralNetDesciptor>();
				for(int i = 0 ; i < k ; i++){
					kNetworks.add(trainNewMLP(kFoldsDataSet,i));
				}
				
				double minTrainError = 100;
				int minNetErrorIndex = 0;
				for(int i = 0 ; i < k ; i++){
					double curTrainError = kNetworks.get(i).getTrainingSetError();
					if(minTrainError > curTrainError){
						minTrainError = curTrainError;
						minNetErrorIndex = i;
					}
				}
				String report = "";
				for(NeuralNetDesciptor ann:kNetworks){
					report += "-------------------\n";
					report += ann.toString();
					report += "-------------------\n";
					
				}
				return report;
			case RBF_TYPE: return null ;
			default: return null;
		}
	}

	
	private NeuralNetDesciptor trainNewMLP(ArrayList<BasicMLDataSet> kFoldsDataSet, int validationSetIndex) {
		BasicNetwork mlpNetwork = new BasicNetwork();
		int inputCount 			= (int)confValues.get(ConfKeys.inputCount);
		int hiddenCount 		= (int)confValues.get(ConfKeys.hiddenCount);
		int outputCount 		= (int)confValues.get(ConfKeys.outputCount);
		ActivationFunction func = (ActivationFunction)confValues.get(ConfKeys.activationFunction);
		
		mlpNetwork.addLayer(new BasicLayer(null,true,inputCount));
		mlpNetwork.addLayer(new BasicLayer(func,true,hiddenCount));
		mlpNetwork.addLayer(new BasicLayer(func,false,outputCount));
		mlpNetwork.getStructure().finalizeStructure();
		mlpNetwork.reset();
		BasicMLDataSet validationSet 		= kFoldsDataSet.get(validationSetIndex);
		BasicMLDataSet trainingSet 			= unionDataSets(kFoldsDataSet,validationSetIndex );
		EarlyStoppingStrategy strategy 		= new EarlyStoppingStrategy(validationSet, validationSet,1,20,0.1);
		final ResilientPropagation train 	= new ResilientPropagation(mlpNetwork,trainingSet);

		train.addStrategy(strategy);
		int maxEpochs = (int)confValues.get(ConfKeys.maxEpochs);
		while(! train.isTrainingDone() && train.getIteration() < maxEpochs){
			train.iteration();			
		}
		
		double valError 		= strategy.getValidationError();
		double testError 		= strategy.getTestError();
		double trainError		= strategy.getTrainingError();
		String strValError 		= String.format("%.4f", valError);
		String strTestError		= String.format("%.4f",testError);
		String strTrainError	= String.format("%.4f",trainError);
		Integer numOfIterationsDone = train.getIteration();
		strValError.replaceAll(".", "_");
		strTestError.replaceAll(".","_");
		strTrainError.replaceAll(".", "_");
		
		String netName 			= "MLP_val"+strValError+"_trn"+strTrainError+"_te"+strTestError+"_it"+numOfIterationsDone.toString()+".eg";
		String netDir 			= (String)confValues.get(ConfKeys.saveDir);
		
		NeuralNetDesciptor mlpDescriptor = new NeuralNetDesciptor(mlpNetwork);
		mlpDescriptor.setAnnFile(new File(netDir+"//"+netName));
		mlpDescriptor.setValidationSetError(valError);
		mlpDescriptor.setTesttingSetError(testError);
		mlpDescriptor.setTrainingSetError(trainError);
		return mlpDescriptor;
	}
	
	public void normalize(File dataSetFile){
		DataNormalization norm = new DataNormalization();
		for(int i = 0; i < (int)confValues.get(ConfKeys.inputCount)+1 ; i++){
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
		this.dataSetNormFile = targetFile;
		this.normalized = true;

	}

	private ArrayList<BasicMLDataSet> splitDataSet(File dataSet, int k, int inputCount, int outputCount) {
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
	
	private BasicMLDataSet unionDataSets(ArrayList<BasicMLDataSet> dataSets,int indexException){
		BasicMLDataSet newSet = new BasicMLDataSet();
		for(int i = 0 ; i < dataSets.size(); i++){
			if(i == indexException){
				continue;
			}
			for(MLDataPair pair: dataSets.get(i)){
				newSet.add(pair);
			}
		}
		return newSet;
	}

	private RBFNetwork trainNewRBF(ArrayList<MLDataSet> kFoldsDataSet, int validationSetIndex){
		return null;
	}
	
	public enum ConfKeys{
		neyType,
		inputCount,
		hiddenCount,
		outputCount,
		activationFunction,
		learnningRate,
		leaningMomentum,
		saveDir,
		normHigh,
		normLow,
		maxEpochs,
	};
	

	public static void main(String[] args) {
		File dataSet = new File("C:\\Users\\earbili\\Desktop\\DataSet_FullAUS.csv");
		TrainingSession ts = new TrainingSession(dataSet);
		HashMap<ConfKeys,Object> conf = new HashMap<ConfKeys,Object>();
		conf = new HashMap<ConfKeys,Object>();
		
		conf.put(ConfKeys.inputCount, 11);
		conf.put(ConfKeys.hiddenCount, 7);
		conf.put(ConfKeys.outputCount, 1);
		conf.put(ConfKeys.activationFunction, new ActivationSigmoid());
		conf.put(ConfKeys.neyType, MLP_TYPE);
		conf.put(ConfKeys.saveDir,dataSet.getParent());
		conf.put(ConfKeys.normLow, 0);
		conf.put(ConfKeys.normHigh, 1);
		conf.put(ConfKeys.maxEpochs, 15000);
		
		ts.setConfValues(conf);
		ts.normalize(dataSet);
		String result = ts.kFoldsCrossValidationTrain(4);
		System.out.println(result);
		System.exit(0);
	}

}
