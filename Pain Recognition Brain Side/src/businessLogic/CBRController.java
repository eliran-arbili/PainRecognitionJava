package businessLogic;

import java.io.File;
import java.util.ArrayList;
import dataLayer.ProjectConfig;

public class CBRController {
	
	/*
	 * Instance variables
	 */
	private RetrieveModule retrieveModule;
	private NeuralNetworkManager painRecAnn;
	
	/*
	 * Constructors
	 */
	public CBRController()
	{
		retrieveModule=new RetrieveModule();
		painRecAnn = NeuralNetworkManager.createInstance(new File(ProjectConfig.ANN_PARAMETERS_PATH));
	}
	
	/*
	 * Member functions
	 */
	
	/**
	 * Activate Case Based Reasoning cycle: retrieve, reuse.
	 * @param rtCase
	 * @return pain measure
	 */
	public double doCycle(RunTimeCase rtCase){
		ArrayList<RunTimeCase> kClosestCases = retrieveModule.getKSimilarCases(rtCase);
		painRecAnn.trainKclosestCases(kClosestCases);
		double caseResult = painRecAnn.computeOutput(rtCase);
		return caseResult;
	}
	
	public void handleShutDown(){
		painRecAnn.saveNet();
	}
	
}
