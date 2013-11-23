package businessLogic.training;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.mathutil.rbf.RBFEnum;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.end.EarlyStoppingStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.ContainsFlat;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.networks.training.strategy.SmartLearningRate;
import org.encog.neural.networks.training.strategy.SmartMomentum;
import org.encog.neural.rbf.RBFNetwork;

import businessLogic.ProjectUtils;


public class TrainingSession {
	/*
	 * Class variables
	 */
	public static final int RBF_TYPE = 0;
	public static final int MLP_TYPE = 1;
	public static final int BACK_PROP = 2;
	public static final int REAS_PROP = 3;
	
	/*
	 * Instance variables
	 */
	public HashMap<ConfKeys,Object> confValues;

	/*
	 * configurations used to 
	 */
	public enum ConfKeys{
		neyType,
		trainType,
		inputCount,
		hiddenCount,
		outputCount,
		activationFunction,
		maxEpochs,
		minEffiency,
		alpha,
		stripLength,
	};
	
	/*
	 * Constructors
	 */
	public TrainingSession() {
	}
	public TrainingSession(HashMap<ConfKeys,Object> configurations){
		this.confValues = configurations;
	}
	
	
	/*
	 * Member functions
	 */
	public void configureTraining(HashMap<ConfKeys,Object> trainingConfigurations){
		this.confValues = trainingConfigurations;
	}
	
	/**
	 * @param dataSet - CSV file contains data
	 * @param k-  for k-folds data sets  
	 * @return report contains the details for each network 
	 * @throws IOException 
	 */
	public String kFoldsCrossValidationTrain(File dataSet, int k) throws IOException{
		ProjectUtils.assertFalse((k>=2), "Input Error k<2");
		int inputCount 							= (int)confValues.get(ConfKeys.inputCount);
		int outputCount							= (int)confValues.get(ConfKeys.outputCount);
		File noDuplicationsFileSet					= ProjectUtils.removeDuplicateLines(dataSet, inputCount, outputCount, true);
		ArrayList<File> kFoldsFiles 			= ProjectUtils.splitDataSet(noDuplicationsFileSet, k, inputCount, outputCount,true,true);
		ArrayList<BasicMLDataSet> kFoldsDataSet = new ArrayList<BasicMLDataSet>();
		for(File f: kFoldsFiles){
			kFoldsDataSet.add(ProjectUtils.convertCSVToDateSet
					(ProjectUtils.normalizeCSVFile(f, inputCount, outputCount),
							inputCount,
							outputCount,
							false
				));
		}
		ArrayList<NeuralTrainDesciptor> kNetworks = new ArrayList<NeuralTrainDesciptor>();
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
		for(NeuralTrainDesciptor ann:kNetworks){
			report += "-------------------\n";
			report += ann.toString();
			report += "-------------------\n";
			String netDir 			= dataSet.getParent();
			ann.saveAsEncogFile(netDir+"\\");
		}
		return report;
	}
	
	public void crossValidationTrain(ContainsFlat neuralNet, BasicMLDataSet dataSet,int partitions){
		ProjectUtils.assertFalse((partitions>=2), "Input Error k<2");
		ArrayList<BasicMLDataSet> kFoldsDataSet = ProjectUtils.splitDataSet(dataSet, partitions);
		doTraining(neuralNet,kFoldsDataSet,0);
		
	}
	
	
	public NeuralTrainDesciptor doTraining(ContainsFlat neuralNet,ArrayList<BasicMLDataSet> kFoldsDataSet, int validationSetIndex){
		BasicMLDataSet validationSet 			= kFoldsDataSet.get(validationSetIndex);
		BasicMLDataSet trainingSet 				= unionDataSets(kFoldsDataSet,validationSetIndex );
		int alpha 								= (int)confValues.get(ConfKeys.alpha);
		Double minEffiency 						= (Double)confValues.get(ConfKeys.minEffiency);
		int stripLength 						= (int)confValues.get(ConfKeys.stripLength);
		int trainType							= (int)confValues.get(ConfKeys.trainType);
		int maxEpochs 							= (int)confValues.get(ConfKeys.maxEpochs);
		EarlyStoppingStrategy earlyStopping 	= new EarlyStoppingStrategy(validationSet, validationSet,stripLength,alpha,minEffiency);
		
		MLTrain train;
		switch(trainType){
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

		train.addStrategy(earlyStopping);
		while(! train.isTrainingDone() && train.getIteration() < maxEpochs){
			train.iteration();	
		}
		
		double valError 		= earlyStopping.getValidationError();
		double testError 		= earlyStopping.getTestError();
		double trainError		= earlyStopping.getTrainingError();
		NeuralTrainDesciptor neuralNetDecriptor = new NeuralTrainDesciptor(neuralNet);
		neuralNetDecriptor.setValidationSetError(valError);
		neuralNetDecriptor.setTesttingSetError(testError);
		neuralNetDecriptor.setTrainingSetError(trainError);
		neuralNetDecriptor.setTrainIterations(train.getIteration());
		return neuralNetDecriptor;
	}
	
	private NeuralTrainDesciptor trainNewMLP(ArrayList<BasicMLDataSet> kFoldsDataSet, int validationSetIndex) {
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
		
		NeuralTrainDesciptor mlpDescriptor = doTraining(mlpNetwork, kFoldsDataSet, validationSetIndex);

		return mlpDescriptor;
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

	private NeuralTrainDesciptor trainNewRBF(ArrayList<BasicMLDataSet> kFoldsDataSet, int validationSetIndex){
		
		int inputCount 						= (int)confValues.get(ConfKeys.inputCount);
		int hiddenCount 					= (int)confValues.get(ConfKeys.hiddenCount);
		int outputCount 					= (int)confValues.get(ConfKeys.outputCount);
		RBFNetwork rbfNetwork 				= new RBFNetwork(inputCount, hiddenCount, outputCount, RBFEnum.Gaussian);
		NeuralTrainDesciptor rbfDescriptor 	= doTraining(rbfNetwork, kFoldsDataSet, validationSetIndex);
		return rbfDescriptor;
	}
	
	

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
		
		
		File dataSetFile = new File("C:\\Users\\earbili\\Desktop\\NeuralNets\\NEW_DataSet_FullAUS- Edited.csv");
		
		TrainingSession ts = new TrainingSession();
		ts.configureTraining(conf);
		
		String resultReport;
		try {
			resultReport = ts.kFoldsCrossValidationTrain(dataSetFile,4);
			System.out.println(resultReport);
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
