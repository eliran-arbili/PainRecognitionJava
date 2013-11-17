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
		if(ProjectConfig.fuzzyMode)
			rtCase.fuzzify();
		ArrayList<RunTimeCase> kClosestCases = retrieveModule.getKSimilarCases(rtCase);
		painRecAnn.trainKclosestCases(kClosestCases);
		double caseResult = painRecAnn.computeOutput(rtCase);
		painRecAnn.ResetWeights();
/*		if(caseResult > ProjectConfig.PAIN_SENSITIVITY){
			System.out.println("NetSolution:"+caseResult);
			for(RunTimeCase r: kClosestCases){
				System.out.println("case: "+r);
				System.out.println("sol: "+r.getSolutionOutput());
				System.out.println("sim: "+r.similarity(rtCase));
			}
		}*/
		return caseResult;
	}
	
	public void handleShutDown(){
		//painRecAnn.saveNet();
	}
	
}
