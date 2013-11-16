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
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.mathutil.rbf.RBFEnum;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.end.EarlyStoppingStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.ContainsFlat;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.networks.training.strategy.RegularizationStrategy;
import org.encog.neural.networks.training.strategy.SmartLearningRate;
import org.encog.neural.networks.training.strategy.SmartMomentum;
import org.encog.neural.rbf.RBFNetwork;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ParseCSVLine;
import org.encog.util.normalize.DataNormalization;
import org.encog.util.normalize.input.InputFieldCSV;
import org.encog.util.normalize.output.OutputFieldRangeMapped;
import org.encog.util.normalize.target.NormalizationStorageCSV;

import businessLogic.ProjectUtils;


public class TrainingSession {
	
	public HashMap<ConfKeys,Object> confValues;
	public static final int RBF_TYPE = 0;
	public static final int MLP_TYPE = 1;
	public static final int BACK_PROP = 2;
	public static final int REAS_PROP = 3;


	public TrainingSession() {
	}
	public TrainingSession(HashMap<ConfKeys,Object> configurations){
		this.confValues = configurations;
	}
	
	
	public void configureTraining(HashMap<ConfKeys,Object> trainingConfigurations){
		this.confValues = trainingConfigurations;
	}
	
	/**
	 * 
	 * @param k-  for k-folds data sets
	 * @return report contains the details for each network 
	 */
	public String kFoldsCrossValidationTrain(File dataSet, int k){
		ProjectUtils.assertFalse((k>=2), "Input Error k<2");
		
		ArrayList<BasicMLDataSet> kFoldsDataSet = ProjectUtils.splitDataSet(dataSet, k, (int)confValues.get(ConfKeys.inputCount), (int)confValues.get(ConfKeys.outputCount));
		ArrayList<NeuralNetDesciptor> kNetworks = new ArrayList<NeuralNetDesciptor>();
		switch((int)confValues.get(ConfKeys.neyType)){
			case MLP_TYPE: 
				for(int i = 0 ; i < k ; i++){
					kNetworks.add(trainNewMLP(kFoldsDataSet,i));
				}
				break;
			case RBF_TYPE:
				for(int i = 0 ; i < k ; i++){
					kNetworks.add(trainNewRBF(kFoldsDataSet,i));
				}
				break;
		}
		String report = "";
		for(NeuralNetDesciptor ann:kNetworks){
			report += "-------------------\n";
			report += ann.toString();
			report += "-------------------\n";
			String netDir 			= (String)confValues.get(ConfKeys.saveDir);
			ann.saveAsEncogFile(netDir+"\\");
		}
		return report;
	}
	
	public void kFoldsCrossValidationTrain(ContainsFlat neuralNet, BasicMLDataSet dataSet,int k){
		ProjectUtils.assertFalse((k>=2), "Input Error k<2");
		ArrayList<BasicMLDataSet> kFoldsDataSet = ProjectUtils.splitDataSet(dataSet, k);
		doTraining(neuralNet,kFoldsDataSet,0);
		
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
		
		NeuralNetDesciptor mlpDescriptor = doTraining(mlpNetwork, kFoldsDataSet, validationSetIndex);

		return mlpDescriptor;
	}
	
	public NeuralNetDesciptor doTraining(ContainsFlat neuralNet,ArrayList<BasicMLDataSet> kFoldsDataSet, int validationSetIndex){
		BasicMLDataSet validationSet 		= kFoldsDataSet.get(validationSetIndex);
		BasicMLDataSet trainingSet 			= unionDataSets(kFoldsDataSet,validationSetIndex );
		int alpha = (int)confValues.get(ConfKeys.alpha);
		Double minEffiency = (Double)confValues.get(ConfKeys.minEffiency);
		int stripLength = (int)confValues.get(ConfKeys.stripLength);
		EarlyStoppingStrategy strategy 		= new EarlyStoppingStrategy(validationSet, validationSet,stripLength,alpha,minEffiency);
		
		MLTrain train;
		switch((int)confValues.get(ConfKeys.trainType)){
			case REAS_PROP:
				train 					= new ResilientPropagation(neuralNet,trainingSet); 
				break;
			case BACK_PROP:
				train 					= new Backpropagation(neuralNet, trainingSet);
				SmartLearningRate 	slr	= new SmartLearningRate();
				SmartMomentum 		sm	= new SmartMomentum();
				train.addStrategy(sm);
				train.addStrategy(slr);
				break;
			default: train = new Backpropagation(neuralNet, trainingSet);
		}

		train.addStrategy(strategy);
		int maxEpochs = (int)confValues.get(ConfKeys.maxEpochs);
		while(! train.isTrainingDone() && train.getIteration() < maxEpochs){
			train.iteration();	
		}
		
		double valError 		= strategy.getValidationError();
		double testError 		= strategy.getTestError();
		double trainError		= strategy.getTrainingError();
		NeuralNetDesciptor neuralNetDecriptor = new NeuralNetDesciptor(neuralNet);
		neuralNetDecriptor.setValidationSetError(valError);
		neuralNetDecriptor.setTesttingSetError(testError);
		neuralNetDecriptor.setTrainingSetError(trainError);
		neuralNetDecriptor.setTrainIterations(train.getIteration());
		return neuralNetDecriptor;
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

	private NeuralNetDesciptor trainNewRBF(ArrayList<BasicMLDataSet> kFoldsDataSet, int validationSetIndex){
		int inputCount 			= (int)confValues.get(ConfKeys.inputCount);
		int hiddenCount 		= (int)confValues.get(ConfKeys.hiddenCount);
		int outputCount 		= (int)confValues.get(ConfKeys.outputCount);
		RBFNetwork rbfNetwork = new RBFNetwork(inputCount, hiddenCount, outputCount, RBFEnum.Gaussian);
		NeuralNetDesciptor rbfDescriptor = doTraining(rbfNetwork, kFoldsDataSet, validationSetIndex);
		return rbfDescriptor;
	}
	
	public enum ConfKeys{
		neyType,
		trainType,
		inputCount,
		hiddenCount,
		outputCount,
		activationFunction,
		learnningRate,
		leaningMomentum,
		saveDir,
		maxEpochs,
		minEffiency,
		alpha,
		stripLength,
	};
	

	public static void main(String[] args) {		
		HashMap<ConfKeys,Object> conf = new HashMap<ConfKeys,Object>();
		conf = new HashMap<ConfKeys,Object>();
		
		conf.put(ConfKeys.inputCount, 11);
		conf.put(ConfKeys.hiddenCount, 5);
		conf.put(ConfKeys.outputCount, 1);
		conf.put(ConfKeys.activationFunction, new ActivationSigmoid());
		conf.put(ConfKeys.neyType, MLP_TYPE);
		conf.put(ConfKeys.maxEpochs, 15000);
		conf.put(ConfKeys.stripLength, 1);
		conf.put(ConfKeys.alpha, 20);
		conf.put(ConfKeys.minEffiency, 0.1);
		conf.put(ConfKeys.trainType, BACK_PROP);
		
		
		File dataSetNormalized = ProjectUtils.normalizeCSVFile(
				new File("C:\\Users\\earbili\\Desktop\\NeuralNets\\DataSet_FullAUS.csv"), 
				(int)conf.get(ConfKeys.inputCount), (int)conf.get(ConfKeys.outputCount));
		
		conf.put(ConfKeys.saveDir,dataSetNormalized.getParent());
		
		TrainingSession ts = new TrainingSession();
		ts.configureTraining(conf);
		
		String result = ts.kFoldsCrossValidationTrain(dataSetNormalized,4);
		System.out.println(result);
		System.exit(0);
	}

}
