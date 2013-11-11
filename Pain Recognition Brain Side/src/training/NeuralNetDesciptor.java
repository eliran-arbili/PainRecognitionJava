package training;
import java.io.File;

import org.encog.ml.MLMethod;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.Format;

public class NeuralNetDesciptor {
	private MLMethod neuralNet;
	private File annFile;
	private double validationSetError;
	private double trainingSetError;
	private double testtingSetError;
	
	public NeuralNetDesciptor(MLMethod neuralNet) {
		this.neuralNet = neuralNet;
	}
	
	public File getAnnFile() {
		return annFile;
	}
	public void setAnnFile(File annFile) {
		this.annFile = annFile;
		EncogDirectoryPersistence.saveObject(annFile, this.neuralNet);
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
				"path			: " +annFile+"\n"+
				"Training error		: " +Format.formatPercent(trainingSetError)+"\n"+
				"Validation error	: " +Format.formatPercent(validationSetError)+"\n"+
				"Testing error		: " +Format.formatPercent(testtingSetError)+"\n";
		return dataString;
	}
	
}
