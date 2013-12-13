package businessLogic;

import java.io.File;
import java.util.PriorityQueue;

import dataLayer.ProjectConfig;

/**
 * CBRController is the class that responsible on the connection between neural network and casebase  
 * @author Eliran Arbili , Arie Gaon
 *
 */


public class CBRController {
	
	
	private RetrieveModule retrieveModule;
	private NeuralNetworkManager painRecAnn;
	
	/*
	 * Constructors
	 */
	
	/**
	 * Constructor  - initialize instance of RetriveModule class for get cases from casebase , NeuralNetworkManager class for get neural network 
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
	 * The main function, this function get runtime case, train network by k closest cases from casebase, and return the network output
	 * @param rtCase  - runtime case , the current case that come from user
	 * @return  - an array of network results output
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
