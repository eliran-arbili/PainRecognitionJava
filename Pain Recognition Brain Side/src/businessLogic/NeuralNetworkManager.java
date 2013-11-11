package businessLogic;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import dataLayer.ActionUnit;
import dataLayer.ProjectConfig;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.arrayutil.NormalizedField;

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
	
	private BasicNetwork MLPNeuralNet;
	
	/*
	 * Constructors
	 */
	private NeuralNetworkManager(File networkParamaters){
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
		MLData dataInput = new BasicMLData(rtCase.getActionUnits());
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
	
	public static  double [] NormalizeAUs(double [] actionUnits){
		double [] actionUnitsNorm = new double[actionUnits.length];
		ActionUnit[] aus = ActionUnit.values();

		for (int i = 0; i < actionUnits.length; i++) {
			NormalizedField norm = ProjectConfig.AURangeMap.get(aus[i]);	
			actionUnitsNorm[i] = norm.normalize(actionUnits[i]);
		}
		return actionUnitsNorm;
	}
	public static void main (String[] args){
		double [] testCase = new double[]{0.073, 0.962, 0.55, -0.676, 0.0, -0.006, 0.108, -0.029, 1.0, 0.0, 0.0};
		BasicNetwork net = (BasicNetwork) EncogDirectoryPersistence.loadObject(new File("C:\\Users\\user\\Desktop\\MLP_val0.1329_trn0.0290_te0.1329_it144.eg"));
		MLData dataInput = new BasicMLData(NormalizeAUs(testCase));
		System.out.println(Arrays.toString(NormalizeAUs(testCase)));
		MLData solutionOutput = net.compute(dataInput);
	
		System.out.println(solutionOutput.getData(0));
		//MLDataSet set = new BasicMLDataSet();
	}
	
}
