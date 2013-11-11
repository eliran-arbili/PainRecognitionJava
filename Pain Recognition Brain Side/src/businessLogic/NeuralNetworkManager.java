package businessLogic;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import dataLayer.*;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.arrayutil.NormalizedField;

import training.TrainingSession;
import training.TrainingSession.ConfKeys;

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
	 * handle training with our configurations
	 */
	TrainingSession trainingSession;
	
	/*
	 * Constructors
	 */
	private NeuralNetworkManager(File networkParamaters){
		MLPNeuralNet = (BasicNetwork)EncogDirectoryPersistence.loadObject(networkParamaters);
		HashMap<ConfKeys,Object> conf = getDefaultTrainConfigurations();
		trainingSession = new TrainingSession(conf);
	}
	

	/*
	 * Member functions
	 */
	
	public void trainKclosestCases(ArrayList<RunTimeCase> kClosestCases){
		BasicMLDataSet trainingSet = constructTrainingSetFromCases(kClosestCases);
		ProjectUtils.assertFalse(
				trainingSet.getInputSize() == MLPNeuralNet.getInputCount(), 	
				"train input different from ANN input");
		
		ProjectUtils.assertFalse(
				trainingSet.getIdealSize() == MLPNeuralNet.getOutputCount(), 	
				"train output different from ANN output");
		
		
		trainingSession.kFoldsCrossValidationTrain(MLPNeuralNet, trainingSet, ProjectConfig.RUN_TIME_K_FOLD);

		/*		final MLTrain train = new ResilientPropagation(MLPNeuralNet, trainingSet);
		do
		{
			train.iteration();
		}while(train.getError() > ProjectConfig.MAX_ANN_ERROR);*/
		
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

	public static  double [] NormalizeAUs(double [] actionUnits){
		double [] actionUnitsNorm = new double[actionUnits.length];
		ActionUnit[] aus = ActionUnit.values();

		for (int i = 0; i < actionUnits.length; i++) {
			NormalizedField norm = ProjectConfig.AURangeMap.get(aus[i]);	
			actionUnitsNorm[i] = norm.normalize(actionUnits[i]);
		}
		return actionUnitsNorm;
	}
	
	/*
	 * Auxiliary methods
	 */
	private BasicMLDataSet constructTrainingSetFromCases(ArrayList<RunTimeCase> trainingCases){
		ArrayList<MLDataPair> trainingPairs = new ArrayList<MLDataPair>();
		for(RunTimeCase rtCase: trainingCases){
			trainingPairs.add(new BasicMLDataPair(
							new BasicMLData(rtCase.getActionUnits()), 
							new BasicMLData(new double [] {rtCase.getSolutionOutput()})));
		}
		return new BasicMLDataSet(trainingPairs);
	}
	
	private HashMap<ConfKeys, Object> getDefaultTrainConfigurations() {
		HashMap<ConfKeys,Object> conf = new HashMap<ConfKeys,Object>();
		conf.put(ConfKeys.activationFunction, new ActivationSigmoid());
		conf.put(ConfKeys.neyType, TrainingSession.MLP_TYPE);
		conf.put(ConfKeys.maxEpochs, 1000);
		conf.put(ConfKeys.stripLength, 1);
		conf.put(ConfKeys.alpha, 20);
		conf.put(ConfKeys.minEffiency, 0.1);
		return conf;
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
