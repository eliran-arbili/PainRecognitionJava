package businessLogic.training;
import java.io.File;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.ContainsFlat;
import org.encog.neural.rbf.RBFNetwork;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.Format;

import businessLogic.ProjectUtils;

/**
 * Wrapping a description for training ANN and hold the trained ANN for later use.
 * @author Eliran Arbili , Arie Gaon
 */
public class NeuralTrainDesciptor {
	
	/*
	 * Instance Variables
	 */
	private ContainsFlat neuralNet;
	private double validationSetError;
	private double trainingSetError;
	private double testtingSetError;
	private int trainIterations;
	
	/**
	 * Create new NeuralNetworkDescriptor wrapping a trained neural net
	 * @param neuralNet
	 */
	public NeuralTrainDesciptor(ContainsFlat neuralNet) {
		this.neuralNet = neuralNet;
	}
	/**
	 * Get the number of train iterations that this network trained with
	 * @return number of training iterations done
	 */
	public int getTrainIterations() {
		return trainIterations;
	}

	/**
	 * Set the train Iteration value
	 * @param trainIterations
	 */
	public void setTrainIterations(int trainIterations) {
		this.trainIterations = trainIterations;
	}

	
	/**
	 * Save a copy of neural network in Encog format
	 * @param dirToSave- the folder which will contain the new file
	 */
	public void saveAsEncogFile(String dirToSave){
		String fileName = produceFileName();
		EncogDirectoryPersistence.saveObject(new File(ProjectUtils.combine(dirToSave, fileName)), neuralNet);
	}
	
	/**
	 * Get the validation error value
	 * @return training validation  error
	 */
	public double getValidationSetError() {
		return validationSetError;
	}
	
	/**
	 * Set the validation error value
	 * @param validation set error
	 */
	public void setValidationSetError(double validationSetError) {
		this.validationSetError = validationSetError;
	}
	
	/**
	 * Get the training set error value
	 * @return training set error
	 */
	public double getTrainingSetError() {
		return trainingSetError;
	}
	
	/**
	 * Set the training error value
	 * @param trainingSetError
	 */
	public void setTrainingSetError(double trainingSetError) {
		this.trainingSetError = trainingSetError;
	}
	
	/**
	 * Get the testing set error value
	 * @return testing set error
	 */
	public double getTesttingSetError() {
		return testtingSetError;
	}
	
	/**
	 * Set the testing error value
	 * @param testtingSetError
	 */
	public void setTesttingSetError(double testtingSetError) {
		this.testtingSetError = testtingSetError;
	}
	
	/**
	 * Produce a short report description for the training
	 * @return Training report
	 */
	public String toString(){
		String dataString =
				"Nework Status Report\n"+
				"--------------------\n"+
				"Training error		: " +Format.formatPercent(trainingSetError)+"\n"+
				"Validation error	: " +Format.formatPercent(validationSetError)+"\n"+
				"Testing error		: " +Format.formatPercent(testtingSetError)+"\n"+
				"Iterations 		: " + trainIterations + "\n";		
		
		return dataString;
	}
	
	/**
	 * Produce unified file name that contain training information 
	 * @return file name
	 */
	public String produceFileName(){
		String strValError 		= String.format("%.4f", getValidationSetError());
		String strTestError		= String.format("%.4f", getTesttingSetError());
		String strTrainError	= String.format("%.4f", getTrainingSetError());
		Integer numOfIterations = getTrainIterations();
		strValError.replaceAll(".", "_");
		strTestError.replaceAll(".","_");
		strTrainError.replaceAll(".", "_");
		String netName 			= "val"+strValError+"_trn"+strTrainError+"_te"+strTestError+"_it"+numOfIterations.toString()+".eg";
		if(neuralNet instanceof BasicNetwork){
			netName = "MLP_"+netName;
		}
		else if(neuralNet instanceof RBFNetwork){
			netName = "RBF_" + netName;
		}
		return netName;
	}
	
}
