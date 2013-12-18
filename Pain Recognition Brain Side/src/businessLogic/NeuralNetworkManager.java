package businessLogic;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;

import dataLayer.*;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.ContainsFlat;
import org.encog.persist.EncogDirectoryPersistence;
import businessLogic.training.TrainingSession;
import businessLogic.training.TrainingSession.ConfKeys;

/**
 * Handles all neural network definitions and functionalities
 * @author Eliran Arbili , Arie Gaon
 *
 */
public class NeuralNetworkManager {
	
	
	/*
	 * Class variables 
	 */
	private static HashMap<String,NeuralNetworkManager> activeNetworks = new HashMap<String,NeuralNetworkManager>();
	
	/*
	 * Instance variables
	 */
	
	 // A pointer to the saved neural network parameters
	 
	private File networkParameters;
	
	// The neural network
	private ContainsFlat neuralNet;
	
	// save the initial weights here
	private double [] originalWeights;
	
	/*
	 * handle training
	 */
	TrainingSession trainingSession;
	
	/*
	 * Constructors
	 */
	/**
	 * Create NeuralNetworkManager by a persistence Encog neural network file 
	 * @param networkParamaters - Encog .eg cross-platform file that can be read to construct ContainsFlat object.
	 */
	private NeuralNetworkManager(File networkParamaters){
		neuralNet = (ContainsFlat)EncogDirectoryPersistence.loadObject(networkParamaters);
		HashMap<ConfKeys,Object> conf = getDefaultTrainConfigurations();
		trainingSession = new TrainingSession(conf);
		originalWeights=Arrays.copyOf(neuralNet.getFlat().getWeights(), neuralNet.getFlat().getWeights().length);
	
	}
	
	

	/*
	 * Member functions
	 */
	/**
	 * Restart the neural network weights with original weights
	 */
	public void ResetWeights()
	{
		 for(int i=0;i<originalWeights.length;i++)
			 neuralNet.getFlat().getWeights()[i] = originalWeights[i];
	}
	
	/**
	 * Train neural network with k closest cases.
	 * The k closest cases should retrieved by the retrieve module 
	 * @param kClosestCases - Priority Queue that contain k closest cases from cases base after retrieve phase 
	 */
	public void trainKclosestCases(PriorityQueue<RunTimeCase> kClosestCases){
		trainByDataSet(kClosestCases,ProjectConfig.getOptInt("RUN_TIME_K_FOLD"),0);
	}
	
	/**
	 * Get neural network solution output  
	 * @param rtCase - run time case
	 * @return array of neural network solution output
	 */
	public double[] computeOutput(RunTimeCase rtCase){
		Integer outputCount = ProjectConfig.getOptInt("CASE_OUTPUT_COUNT");		
		double [] output = new double[outputCount];
		neuralNet.getFlat().compute(rtCase.getActionUnits(), output);
		return output;
	}
	
	/**
	 * Save the neural network
	 */
	public void saveNet(){
		EncogDirectoryPersistence.saveObject(networkParameters, neuralNet);
	}
	
	/**
	 * Get neural network instance
	 * @return instance of neural network 
	 */
	public ContainsFlat getNeuralNet() {
		return neuralNet;
	}
	
	/**
	 * Train neural network 
	 * @param dataSetCases - array list of all cases 
	 * @param partitions - number of  partitions for cross validation training approach  
	 * @param validationSetIndex - index of  validation group 
	 */
	public void trainByDataSet(Iterable<RunTimeCase> dataSetCases, int partitions, int validationSetIndex){

		BasicMLDataSet trainingSet = constructTrainingSetFromCases(dataSetCases);
		
		ProjectUtils.assertFalse(
				trainingSet.getInputSize() == neuralNet.getFlat().getInputCount(), 	
				"train input different from ANN input");
		
		ProjectUtils.assertFalse(
				trainingSet.getIdealSize() == neuralNet.getFlat().getOutputCount(), 	
				"train output different from ANN output");
		
		trainingSession.crossValidationTrain(neuralNet, trainingSet,partitions,validationSetIndex);
	}
	
	/**
	 * Get neural network instance
	 * @param networkParameters - file that contain user definitions for neural network
	 * @return instance of neural network
	 */
	public static NeuralNetworkManager createInstance(File networkParameters){
		if(activeNetworks.containsKey(networkParameters.getAbsolutePath())){
			return activeNetworks.get(networkParameters.getAbsolutePath());
		}
		else{
			if(networkParameters.exists()){
				NeuralNetworkManager curNet = new NeuralNetworkManager(networkParameters);
				activeNetworks.put(networkParameters.getAbsolutePath(), curNet);
				return curNet;
			}
			else{
				return null;
			}
		}
	}
	
	
	/*
	 * Auxiliary methods
	 */
	private BasicMLDataSet constructTrainingSetFromCases(Iterable<RunTimeCase> kClosestCases){
		ArrayList<MLDataPair> trainingPairs = new ArrayList<MLDataPair>();
		for(RunTimeCase rtCase: kClosestCases){
			trainingPairs.add(new BasicMLDataPair(
							new BasicMLData(rtCase.getActionUnits()), 
							new BasicMLData(rtCase.getSolutionOutput())
			));
		}
		return new BasicMLDataSet(trainingPairs);
	}
	
	/**
	 * Get neural network default definitions
	 * @return hash map that contain all neural network definitions
	 */
	private HashMap<ConfKeys, Object> getDefaultTrainConfigurations() {
		HashMap<ConfKeys,Object> conf = new HashMap<ConfKeys,Object>();
		conf.put(ConfKeys.activationFunction, new ActivationSigmoid());
		conf.put(ConfKeys.neyType, TrainingSession.MLP_TYPE);
		conf.put(ConfKeys.maxEpochs, 100);
		conf.put(ConfKeys.stripLength, 1);
		conf.put(ConfKeys.alpha, 20.0);
		conf.put(ConfKeys.minEffiency, 0.1);
		conf.put(ConfKeys.trainType, TrainingSession.BACK_PROP);
		return conf;
	}

	public static void main (String[] args){
		
		RunTimeCase rt = new RunTimeCase(new double[]{0.073, 0.962, 0.55, -0.676, 0.0, -0.006, 0.108, -0.029, 1.0, 0.0, 0.0});
		rt.normalize();
		org.encog.neural.networks.BasicNetwork net = (org.encog.neural.networks.BasicNetwork) EncogDirectoryPersistence.loadObject(new File("C:\\Users\\earbili\\Desktop\\NeuralNets\\mlp.eg"));
		BasicMLData dataInput = new BasicMLData(rt.getActionUnits());
		org.encog.ml.data.MLData solutionOutput = net.compute(dataInput);
		System.out.println(solutionOutput.getData(0));
	}
	
}
