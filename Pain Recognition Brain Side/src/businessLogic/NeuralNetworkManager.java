package businessLogic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import dataLayer.ProjectConfig;

import org.encog.ml.BasicML;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.rbf.RBFNetwork;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.arrayutil.NormalizeArray;

public class NeuralNetworkManager {
	
	
	/*
	 * Class variables 
	 */
	private static HashMap<String,NeuralNetworkManager> activeNetworks;
	
	/*
	 * Instance variables
	 */
	
	// arrayNormalizer normalizing fuzzy action units to range -1 to 1
	 
	private NormalizeArray arrayNormalizer;
	
	 // A pointer to the saved neural network parameters
	 
	private File networkParameters;
	
	// Radial basis neural network
	
	//private RBFNetwork neuralNet; // Currently Using MLP
	
	private BasicNetwork MLPNeuralNet;
	
	/*
	 * Constructors
	 */
	private NeuralNetworkManager(File networkParamaters){
		arrayNormalizer = new NormalizeArray();
		arrayNormalizer.setNormalizedHigh(1);
		arrayNormalizer.setNormalizedLow(-1);
		MLPNeuralNet = (BasicNetwork)EncogDirectoryPersistence.loadObject(networkParamaters);
	}
	
	/*
	 * Member functions
	 */
	public void trainKclosestCases(ArrayList<RunTimeCase> kClosestCases){
		MLDataSet trainingSet = constructTrainingSetFromCases(kClosestCases);
		final MLTrain train = new ResilientPropagation(MLPNeuralNet, trainingSet);
		do
		{
			train.iteration();
		}while(train.getError() > ProjectConfig.MAX_ANN_ERROR);
	}
	public double computeOutput(RunTimeCase rtCase){
		MLData dataInput = new BasicMLData(arrayNormalizer.process(rtCase.getActionUnits()));
		MLData solutionOutput = MLPNeuralNet.compute(dataInput);
		return solutionOutput.getData(0);
	}

	public void saveNet(){
		EncogDirectoryPersistence.saveObject(networkParameters, MLPNeuralNet);
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
	private MLDataSet constructTrainingSetFromCases(ArrayList<RunTimeCase> trainingCases){
		ArrayList<MLDataPair> trainingPairs = new ArrayList<MLDataPair>();
		for(RunTimeCase rtCase: trainingCases){
			trainingPairs.add(new BasicMLDataPair(
							new BasicMLData(rtCase.getActionUnits()), 
							new BasicMLData(new double [] {rtCase.getSolutionOutput()})));
		}
		return new BasicMLDataSet(trainingPairs);
	}
	
}
