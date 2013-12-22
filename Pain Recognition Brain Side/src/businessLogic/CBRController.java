package businessLogic;

import java.io.File;
import java.util.ArrayList;
import java.util.PriorityQueue;

import dataLayer.ProjectConfig;

/**
 * Responsible to manage all Case Base Reasoning and Neural Network cycle  
 * @author Eliran Arbili , Arie Gaon
 *
 */

public class CBRController {
	
	
	private RetrieveModule retrieveModule;
	private NeuralNetworkManager painRecAnn;
	
	
	/**
	 * Create CBRController with initialization of all needed components
	 */
	public CBRController()
	{
		File annFile = new File(ProjectConfig.getOpt("ANN_PARAMETERS_PATH"));
		ProjectUtils.assertFalse(annFile.exists(),"Cannot find the neural network file path");
		retrieveModule	= new RetrieveModule();
		painRecAnn 		= NeuralNetworkManager.createInstance(annFile);
	}
	
	/*
	 * Member functions
	 */
	
	/**
	 * Performing a CBR cycle: retrieve, reuse(train neural network) and suggest solution as pain measure
	 * @param rtCase  - runtime case , the current case that come from user
	 * @return  array of network solution output
	 */
	public double[] doCycle(RunTimeCase rtCase){
		if(ProjectConfig.getOptBool("FUZZY_MODE") == true)
			rtCase.fuzzify();
		PriorityQueue<RunTimeCase> kClosestCases = retrieveModule.getKSimilarCases(rtCase);
		painRecAnn.trainKclosestCases(kClosestCases);
		double [] caseResult = painRecAnn.computeOutput(rtCase);
		painRecAnn.ResetWeights();
		return caseResult;
	}
	
	/**
	 * Add new RunTimeCase to casebase together with training the network
	 * @param newCase - runtime case , the current case that come from user
	 * @param newSol - solution for rtcase
	 * @return True if Revise success , false else
	 */
	public boolean revise(RunTimeCase newCase, double[] newSol){
		newCase.setSolutionOutput(newSol);
		
		if(newCase.isNormalized() == false){
			newCase.normalize();
		}
		boolean isSuccess = retrieveModule.addNewCase(newCase);
		if(isSuccess == false){
			return false;
		}
		
		ArrayList<RunTimeCase> allCases = retrieveModule.getAllCases();
		painRecAnn.trainByDataSet(allCases, 2, 1);
		return true;
	}
	
	/**
	 * Call this method before instance goes out of scope to save current state
	 */
	public void handleShutDown(){
		//painRecAnn.saveNet();
	}
	
}
