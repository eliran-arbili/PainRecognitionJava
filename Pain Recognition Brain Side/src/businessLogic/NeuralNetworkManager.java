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
import org.encog.neural.networks.ContainsFlat;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.Format;
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
	
	private ContainsFlat neuralNet;
	
	/*
	 * handle training with our configurations
	 */
	TrainingSession trainingSession;
	
	/*
	 * Constructors
	 */
	private NeuralNetworkManager(File networkParamaters){
		neuralNet = (ContainsFlat)EncogDirectoryPersistence.loadObject(networkParamaters);
		HashMap<ConfKeys,Object> conf = getDefaultTrainConfigurations();
		trainingSession = new TrainingSession(conf);
	}
	

	/*
	 * Member functions
	 */
	
	public void trainKclosestCases(ArrayList<RunTimeCase> kClosestCases){
		BasicMLDataSet trainingSet = constructTrainingSetFromCases(kClosestCases);
		ProjectUtils.assertFalse(
				trainingSet.getInputSize() == neuralNet.getFlat().getInputCount(), 	
				"train input different from ANN input");
		
		ProjectUtils.assertFalse(
				trainingSet.getIdealSize() == neuralNet.getFlat().getOutputCount(), 	
				"train output different from ANN output");
		
		
		trainingSession.kFoldsCrossValidationTrain(neuralNet, trainingSet, ProjectConfig.RUN_TIME_K_FOLD);

		/*		final MLTrain train = new ResilientPropagation(MLPNeuralNet, trainingSet);
		do
		{
			train.iteration();
		}while(train.getError() > ProjectConfig.MAX_ANN_ERROR);*/
		
	}
	public double computeOutput(RunTimeCase rtCase){
		//MLData dataInput = new BasicMLData(rtCase.getActionUnits());
		double [] output = new double[1];
		neuralNet.getFlat().compute(rtCase.getActionUnits(), output);
		return output[0];
	}

	public void saveNet(){
		EncogDirectoryPersistence.saveObject(networkParameters, neuralNet);
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
		conf.put(ConfKeys.maxEpochs, 100);
		conf.put(ConfKeys.stripLength, 1);
		conf.put(ConfKeys.alpha, 20);
		conf.put(ConfKeys.minEffiency, 0.1);
		conf.put(ConfKeys.trainType, TrainingSession.BACK_PROP);
		return conf;
	}

	public static void main (String[] args){
		//double [] testCase = new double[]{0.073, 0.962, 0.55, -0.676, 0.0, -0.006, 0.108, -0.029, 1.0, 0.0, 0.0};
		//BasicNetwork net = (BasicNetwork) EncogDirectoryPersistence.loadObject(new File("C:\\Users\\earbili\\Desktop\\mlp.eg"));
		//MLData dataInput = new BasicMLData(NormalizeAUs(testCase));
		//System.out.println(Arrays.toString(NormalizeAUs(testCase)));
		RunTimeCase r1 = new RunTimeCase(new double[]{0.02,-0.02,0,-0.03,-0.01,0.01,0.03,0.02,0,-0.06,0.13},0);
		RunTimeCase r2 = new RunTimeCase(new double[]{0.03,-0.02,-0.02,-0.07,-0.02,-0.01,0.02,0.02,0,-0.1,0.13},0);
		RunTimeCase r3 = new RunTimeCase(new double[]{0,0,0.07,0.01,0.01,0.04,0.03,0.02,0,-0.12,0.11},0);
		RunTimeCase r4 = new RunTimeCase(new double[]{0.02,-0.01,0.12,-0.01,0.02,0.01,0.01,0.05,0,-0.09,0.12},0);
		RunTimeCase r5 = new RunTimeCase(new double[]{0.01,0,0.09,0.06,0.03,-0.01,0.02,0,0,-0.07,0.21},0);
		RunTimeCase r6 = new RunTimeCase(new double[]{0.0,-0.01,0,-0.04,-0.05,0.01,0.03,-0.02,0,-0.06,0.1},0);
		RunTimeCase r7 = new RunTimeCase(new double[]{-0.03,-0.01,-0.05,-0.01,0.01,-0.02,0.03,0.05,0,-0.05,0.09},0);
		RunTimeCase r8 = new RunTimeCase(new double[]{0.02,0,0.06,0.02,-0.01,0.02,0.04,0.05,0,-0.1,0.08},0);
		RunTimeCase r9 = new RunTimeCase(new double[]{0.01,-0.02,0.1,-0.02,-0.02,-0.03,0.04,0.02,0,-0.06,0.11},0);
		RunTimeCase r10 = new RunTimeCase(new double[]{0.0,0,-0.04,0.07,0.05,-0.02,0.01,0,0,-0.04,0.19},0);
		
		ArrayList<RunTimeCase> cases = new ArrayList<RunTimeCase>();
		r1.normalize();r2.normalize();r3.normalize();r4.normalize();r5.normalize();
		r6.normalize();r7.normalize();r8.normalize();r9.normalize();r10.normalize();
		
		cases.add(r1);cases.add(r2);cases.add(r3);cases.add(r4);cases.add(r5);
		cases.add(r6);cases.add(r7);cases.add(r8);cases.add(r9);cases.add(r10);
		NeuralNetworkManager nm = new NeuralNetworkManager(new File("C:\\Users\\earbili\\Desktop\\NeuralNets\\mlp.eg"));
		
		double [] weightsBefore = Arrays.copyOf(nm.neuralNet.getFlat().getWeights(), nm.neuralNet.getFlat().getWeights().length);;
		
		nm.trainKclosestCases(cases);
		
		double [] weightsAfter = nm.neuralNet.getFlat().getWeights();

		double max = 0;
		int maxIndex = 0;
		double averageRatio = 0;
		for(int i = 0 ; i < weightsAfter.length; i++){
			if(weightsBefore[i] != 0){
				double diffRatio = Math.abs(weightsBefore[i] - weightsAfter[i]) / Math.abs(weightsBefore[i]);
				averageRatio += diffRatio;
				if(diffRatio > max){
					max = diffRatio;
					maxIndex = i;
				}
			}
		}
		averageRatio = averageRatio /  weightsAfter.length;
		System.out.println(Arrays.toString(weightsBefore));
		System.out.println(Arrays.toString(weightsAfter));

		System.out.println("\nmax: " + Format.formatPercent(max) +"   Index:" + maxIndex);
		System.out.println("Aaverage: " + Format.formatPercent(averageRatio));
		System.out.println(weightsBefore[maxIndex]);
		System.out.println(weightsAfter[maxIndex]);

		//MLData solutionOutput = net.compute(dataInput);
		double [] sol = new double[1];
		nm.neuralNet.getFlat().compute(r1.getActionUnits(), sol);
		System.out.println(sol[0]);
		//System.out.println(solutionOutput.getData(0));
		//MLDataSet set = new BasicMLDataSet();
	}
	
}
