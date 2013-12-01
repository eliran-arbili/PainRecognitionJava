package businessLogic;

import java.io.File;
import java.util.PriorityQueue;

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
		File annFile = new File(ProjectConfig.getOpt("ANN_PARAMETERS_PATH"));
		ProjectUtils.assertFalse(annFile.exists(),"Cannot find the neural network file path");
		retrieveModule	= new RetrieveModule();
		painRecAnn 		= NeuralNetworkManager.createInstance(annFile);
	}
	
	/*
	 * Member functions
	 */
	
	/**
	 * Activate Case Based Reasoning cycle: retrieve, reuse.
	 * @param rtCase
	 * @return pain measure
	 */
	public double[] doCycle(RunTimeCase rtCase){
		if(ProjectConfig.fuzzyMode)
			rtCase.fuzzify();
		PriorityQueue<RunTimeCase> kClosestCases = retrieveModule.getKSimilarCases(rtCase);
		painRecAnn.trainKclosestCases(kClosestCases);
		double [] caseResult = painRecAnn.computeOutput(rtCase);
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
