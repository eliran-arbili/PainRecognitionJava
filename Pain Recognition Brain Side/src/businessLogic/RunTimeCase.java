package businessLogic;

import java.util.Arrays;

import org.encog.util.arrayutil.NormalizedField;

import dataLayer.ProjectConfig;

/**
 * Describe case that used in RunTime manipulations, a case contain action units and solution output   
 * @author Eliran Arbeli , Arie Gaon
 */
public class RunTimeCase {
	
	/*
	 * Class Variables
	 */

	/*
	 * Instance variables
	 */
	private double [] actionUnits;
	private double [] origActionUnits;
	private double [] solutionOutput;
	private boolean normalized;

	
	/*
	 * Constructors
	 */
	/**
	 * Create new RunTimeCase with given action units
	 * @param actionUnits -  array that contain action units
	 */
	public RunTimeCase(double actionUnits[]){
		this.origActionUnits = Arrays.copyOf(actionUnits, actionUnits.length);
		this.actionUnits = Arrays.copyOf(actionUnits, actionUnits.length);
		solutionOutput = null;
		setNormalized(false);
	}
	
	/**
	 * Create new RunTimeCase with action units and ready solution
	 * @param actionUnits - array that contain action units 
	 * @param solution - array that contain case solution output
	 */
	public RunTimeCase(double actionUnits[], double [] solution){
		this(actionUnits);
		solutionOutput = Arrays.copyOf(solution, solution.length);
	}
	
	/**
	 * Create new RunTimeCase with given action units, ready solution and specify normalization status 
	 * @param actionUnits - array that contain action units
	 * @param solution -  array that contain case solution output
	 * @param normalized - indicator to know if action units are normalized or not
	 */
	public RunTimeCase(double actionUnits[], double [] solution, boolean normalized){
		this(actionUnits,solution);
		setNormalized(normalized);
	}
	
	/**
	 * Create new RunTimeCase with given action units and specify normalization status
	 * @param actionUnits - array that contain action units
	 * @param normalized - indicator for normalization of action units status
	 */
	public RunTimeCase(double actionUnits[], boolean normalized){
		this(actionUnits);
		setNormalized(normalized);
	}
	
	/*
	 * Member functions
	 */
	/**
	 * Get solution output
	 * @return copy of solution output array member
	 */
	public double[] getSolutionOutput() {
		if(solutionOutput == null){
			return null;
		}
		return Arrays.copyOf(solutionOutput, solutionOutput.length);
	}
	/**
	 * Set solution output
	 * @param solutionOutput -  array that contain case solution output
	 */
	public void setSolutionOutput(double[] solutionOutput) {
		this.solutionOutput = Arrays.copyOf(solutionOutput, solutionOutput.length);
	}
	
	/**
	 * Get specific action unit value
	 * @param i - index for action units array
	 * @return specific action unit value
	 */
	public double getActionUnit(int i){
		return actionUnits[i];
	}
	
	/**
	 * Get action units 
	 * @return action units array 
	 */
	public double [] getActionUnits()
	{
		return Arrays.copyOf(actionUnits,actionUnits.length);
	}
	
	/**
	 * Get original action units 
	 * @return originl action units array
	 */
	public double [] getOrigActionUnits(){
		return Arrays.copyOf(origActionUnits,origActionUnits.length);
	}
	
	/**
	 * Return String representation of action units
	 */
	public String toString()
	{
		return Arrays.toString(actionUnits);
	}
	
	
	/**
	 * Perform fuzzification process on action units
	 */
	public void fuzzify()
	{
		if(! isNormalized())
			return;
		
		int rangeDistributionNum 	= 	ProjectConfig.getOptInt("AU_FUZZY_DEGREES");
		double [] fuzzy				=	new double[rangeDistributionNum+1];
		double factor 				= 	(ProjectConfig.getOptDouble("NORM_MAX_LIMIT") - ProjectConfig.getOptDouble("NORM_MIN_LIMIT"))/rangeDistributionNum;
		
		
		fuzzy[0] = 0;
		for(int i = 1; i < fuzzy.length; i++){
			fuzzy[i]  =  fuzzy[i-1] + factor;
		}
		
		for(int i = 0; i < actionUnits.length; i++)
		{
			int index  =  (int)Math.round( Math.abs(actionUnits[i]) / factor );
			actionUnits[i] 	=  fuzzy[index] ;
		}		
	}
	 
	/**
	 * Get similarity value between cases   
	 * @param rtCase - runtime case
	 * @return  value that represent the similarity between cases 
	 */
	public Double similarity(RunTimeCase rtCase) {
		double sum	= 0;
		Double [] auWeights = ProjectConfig.getOptDoubleArray("SIMILARITY_WEIGHTS");
		for(int i = 0; i < actionUnits.length; i++)
		{
			sum  +=  auWeights[i] * (Math.abs(this.actionUnits[i] - rtCase.actionUnits[i]));
		}
		
		return sum;
	}
	
	
	/**
	 *  Get action units normalization indicator
	 * @return true if this case is isNormalized, false else
	 */
	public boolean isNormalized() {
		return normalized;
	}

	/**
	 * Set action units normalization indicator
	 * @param True if this case is normalized, false else
	 */
	public void setNormalized(boolean normalized) {
		this.normalized = normalized;
	}
	
	/**
	 * Check cases equality by comparing  action units and solution
	 */
	public boolean equals(Object obj)
	{
		if(obj == null){
			return false;
		}
		if(getClass() != obj.getClass()){
			return false;
		}
		final RunTimeCase r = (RunTimeCase)obj;
		for(int i=0;i<r.getActionUnits().length;i++)
		{
			if(this.actionUnits[i] != r.actionUnits[i])
				return false;
		}
		if(this.solutionOutput != null && r.solutionOutput != null){
			for(int i = 0 ; i < solutionOutput.length; i++){
				if(this.solutionOutput[i] != r.solutionOutput[i]){
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Normalize this case
	 */
	public  void normalize(){
		if(isNormalized()){
			return;
		}
		String [] aus = ProjectConfig.getOptArray("AUS");
		
		ProjectUtils.assertFalse(aus.length == actionUnits.length, "Number of AU's configuered is different from ehat in RunTimeCase");
		
		for (int i = 0; i < actionUnits.length; i++) {
			NormalizedField norm = ProjectConfig.AUNormFields.get(aus[i]);	
			actionUnits[i] = norm.normalize(actionUnits[i]);
		}
		setNormalized(true);
	}
	
	/*
	 * Testing Unit
	 */
	public static void main(String[] args) {
		
		double [] actionUnitsInput		=	{0.938,0.75,0.6,0.397,0.116,0,0.114,0.588,0.799,0.9,0.98};
		double [] fuzzyExpectedOutput 	= 	{1,    0.8, 0.6,0.4,  0.2,    0,0.2,  0.6,0.8,  1,1};    
		RunTimeCase rtCase 				= 	new RunTimeCase(actionUnitsInput);
		rtCase.fuzzify();
		double [] testResult 			= 	rtCase.getActionUnits();
		double epsilon 					= 	0.0001;
		for(int i = 0 ; i< testResult.length ; i++){
			ProjectUtils.assertFalse((Math.abs(testResult[i] - fuzzyExpectedOutput[i]) < epsilon), "Test Failed");
		}
		System.out.println("Test Passed!");
		
	}
	
	
	
}
