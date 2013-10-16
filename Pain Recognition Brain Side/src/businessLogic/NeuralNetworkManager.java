package businessLogic;

import java.io.File;
import java.util.ArrayList;
import dataLayer.ProjectConfig;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.rbf.RBFNetwork;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.arrayutil.NormalizeArray;

public class NeuralNetworkManager {
	
	// arrayNormalizer normalizing fuzzy action units to range -1 to 1
	 
	private NormalizeArray arrayNormalizer;
	
	 // A pointer to the saved neural network parameters
	 
	private File networkParameters;
	
	// Radial basis neural network
	
	private RBFNetwork neuralNet;
	
	private NeuralNetworkManager(File networkParamaters){
		arrayNormalizer = new NormalizeArray();
		arrayNormalizer.setNormalizedHigh(1);
		arrayNormalizer.setNormalizedLow(-1);
		if(networkParameters.exists()){
			neuralNet = (RBFNetwork)EncogDirectoryPersistence.loadObject(networkParamaters);
		}
		else{
			try {
				throw new Exception("Can't Find ANN parameters encog file: "+ networkParamaters.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	public void trainKclosestCases(ArrayList<RunTimeCase> kClosestCases){
		MLDataSet trainingSet = constructTrainingSetFromCases(kClosestCases);
		final MLTrain train = new ResilientPropagation(neuralNet, trainingSet);
		do
		{
			train.iteration();
		}while(train.getError() > 0.01);
	}
	public double computeOutput(RunTimeCase rtCase){
		MLData dataInput = new BasicMLData(arrayNormalizer.process(rtCase.getActionUnits()));
		MLData solutionOutput = neuralNet.compute(dataInput);
		return solutionOutput.getData(0);
	}
	public void resetNet(){
		/**
		 * TO-DO: complete implementation
		 */
	}
	public void saveNet(){
		EncogDirectoryPersistence.saveObject(networkParameters, neuralNet);
	}
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
