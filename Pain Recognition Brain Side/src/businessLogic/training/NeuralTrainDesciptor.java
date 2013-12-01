package businessLogic.training;
import java.io.File;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.ContainsFlat;
import org.encog.neural.rbf.RBFNetwork;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.Format;

import businessLogic.ProjectUtils;

public class NeuralTrainDesciptor {
	private ContainsFlat neuralNet;
	private double validationSetError;
	private double trainingSetError;
	private double testtingSetError;
	private int trainIterations;
	
	public int getTrainIterations() {
		return trainIterations;
	}

	public void setTrainIterations(int trainIterations) {
		this.trainIterations = trainIterations;
	}

	public NeuralTrainDesciptor(ContainsFlat neuralNet) {
		this.neuralNet = neuralNet;
	}
	
	public void saveAsEncogFile(String dirToSave){
		String fileName = produceFileName();
		EncogDirectoryPersistence.saveObject(new File(ProjectUtils.combine(dirToSave, fileName)), neuralNet);
	}
	public double getValidationSetError() {
		return validationSetError;
	}
	public void setValidationSetError(double validationSetError) {
		this.validationSetError = validationSetError;
	}
	public double getTrainingSetError() {
		return trainingSetError;
	}
	public void setTrainingSetError(double trainingSetError) {
		this.trainingSetError = trainingSetError;
	}
	public double getTesttingSetError() {
		return testtingSetError;
	}
	public void setTesttingSetError(double testtingSetError) {
		this.testtingSetError = testtingSetError;
	}
	
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
