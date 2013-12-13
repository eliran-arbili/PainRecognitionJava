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
import org.encog.util.Format;

import businessLogic.training.TrainingSession;
import businessLogic.training.TrainingSession.ConfKeys;

/**
 * NeuralNetworkManager is the class that responsible on  neural network definitions
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
	
	// Radial basis neural network
	
	//private RBFNetwork neuralNet; // Currently Using MLP
	
	private ContainsFlat neuralNet;
	
	private double [] originalWeights;
	
	/*
	 * handle training with our configurations
	 */
	TrainingSession trainingSession;
	
	/*
	 * Constructors
	 */
	/**
	 * The Constructor - get file with parameters and create instance of neural network 
	 * @param networkParamaters - file that contain definition for create network
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
	 * this function restart the network weights to original weight
	 */
	public void ResetWeights()
	{
		 for(int i=0;i<originalWeights.length;i++)
			 neuralNet.getFlat().getWeights()[i] = originalWeights[i];
	}
	
	/**
	 * this function get queue of k closest cases from casebase and  train the network 
	 * @param kClosestCases - is the k closest cases in casebase that similar to current runtime case 
	 */
	public void trainKclosestCases(PriorityQueue<RunTimeCase> kClosestCases){
		BasicMLDataSet trainingSet = constructTrainingSetFromCases(kClosestCases);
		ProjectUtils.assertFalse(
				trainingSet.getInputSize() == neuralNet.getFlat().getInputCount(), 	
				"train input different from ANN input");
		
		ProjectUtils.assertFalse(
				trainingSet.getIdealSize() == neuralNet.getFlat().getOutputCount(), 	
				"train output different from ANN output");
		trainingSession.crossValidationTrain(neuralNet, trainingSet,ProjectConfig.getOptInt("RUN_TIME_K_FOLD"));
	}
	
	/**
	 * this function get runtime case as input for network and return the output 
	 * @param rtCase - runtime case , the current case that come from user
	 * @return an array of network results output
	 */
	public double[] computeOutput(RunTimeCase rtCase){
		Integer outputCount = ProjectConfig.getOptInt("CASE_OUTPUT_COUNT");		
		double [] output = new double[outputCount];
		neuralNet.getFlat().compute(rtCase.getActionUnits(), output);
		return output;
	}
	
	/**
	 * this function save the network with all new definitions 
	 */
	public void saveNet(){
		EncogDirectoryPersistence.saveObject(networkParameters, neuralNet);
	}
	
	/**
	 * this function return neural network instance
	 * @return instance of neural network 
	 */
	public ContainsFlat getNeuralNet() {
		return neuralNet;
	}
	
	/*
	 * Class functions
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
	private BasicMLDataSet constructTrainingSetFromCases(PriorityQueue<RunTimeCase> kClosestCases){
		ArrayList<MLDataPair> trainingPairs = new ArrayList<MLDataPair>();
		for(RunTimeCase rtCase: kClosestCases){
			trainingPairs.add(new BasicMLDataPair(
							new BasicMLData(rtCase.getActionUnits()), 
							new BasicMLData(rtCase.getSolutionOutput())
			));
		}
		return new BasicMLDataSet(trainingPairs);
	}
	
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
