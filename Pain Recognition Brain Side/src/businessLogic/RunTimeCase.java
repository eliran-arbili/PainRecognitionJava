package businessLogic;

import java.util.Arrays;

import org.encog.util.arrayutil.NormalizedField;

import dataLayer.ProjectConfig;

/**
 * RunTimeCase is  class that describe case , case contain action units and solution output   
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
	 * Constructor - get array of action units and initialize class  members
	 * @param actionUnits -  array that contain values that  describes the movement of muscles in face
	 */
	public RunTimeCase(double actionUnits[]){
		this.origActionUnits = Arrays.copyOf(actionUnits, actionUnits.length);
		this.actionUnits = Arrays.copyOf(actionUnits, actionUnits.length);
		solutionOutput = null;
		setNormalized(false);
	}
	
	/**
	 * Constructor - get array of action units and array of solution output and initialize class  members 
	 * @param actionUnits - array that contain values that  describes the movement of muscles in face 
	 * @param solution - Value represent the level of pain
	 */
	public RunTimeCase(double actionUnits[], double [] solution){
		this.actionUnits = Arrays.copyOf(actionUnits, actionUnits.length);
		solutionOutput = Arrays.copyOf(solution, solution.length);
		setNormalized(false);
	}
	
	/**
	 * Constructor - get array of action units,array of solution output and boolean normalize and  initialize class  members 
	 * @param actionUnits - array that contain values that  describes the movement of muscles in face
	 * @param solution -  value represent the level of pain
	 * @param normalized -indicator  to know if action units are normalized or not
	 */
	public RunTimeCase(double actionUnits[], double [] solution, boolean normalized){
		this(actionUnits,solution);
		setNormalized(normalized);
	}
	
	/**
	 * Constructor - get array of action units and boolean normalize and  initialize class  members 
	 * @param actionUnits - array that contain values that  describes the movement of muscles in face
	 * @param normalized -indicator  to know if action units are normalized or not
	 */
	public RunTimeCase(double actionUnits[], boolean normalized){
		this(actionUnits);
		setNormalized(normalized);
	}
	
	/*
	 * Member functions
	 */
	/**
	 * this function return solution output
	 * @return copy of solution output array member
	 */
	public double[] getSolutionOutput() {
		return Arrays.copyOf(solutionOutput, solutionOutput.length);
	}
	/**
	 * this function get solution output and initialize solution output class member
	 * @param solutionOutput -  value represent the level of pain
	 */
	public void setSolutionOutput(double[] solutionOutput) {
		this.solutionOutput = Arrays.copyOf(solutionOutput, solutionOutput.length);
	}
	
	/**
	 * function that get index and return action unit
	 * @param i - index 
	 * @return specific action unit
	 */
	public double getActionUnit(int i){
		return actionUnits[i];
	}
	/**
	 * function that return array action units
	 * @returncopy of action units array 
	 */
	public double [] getActionUnits()
	{
		return Arrays.copyOf(actionUnits,actionUnits.length);
	}
	
	/**
	 * Get copy of original action units 
	 * @return copy of originl action units
	 */
	public double [] getOrigActionUnits(){
		return Arrays.copyOf(origActionUnits,origActionUnits.length);
	}
	
	public String toString()
	{
		return Arrays.toString(actionUnits);
	}
	
	
	/**
	 * perform fuzzify process on action units
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
	 * get runtime case and compute the value that represent the similarity between cases   
	 * @param rtCase - runtime case , the current case that come from user
	 * @return sum - value that represent the similarity between cases 
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
	 *  return true if action units are normalized or false if not
	 * @return normalized - value that used as indicator to know if action units are normalized
	 */
	public boolean isNormalized() {
		return normalized;
	}

	/**
	 *  set normalized parameter
	 * @param normalized - value that used as indicator to know if action units are normalized
	 */
	public void setNormalized(boolean normalized) {
		this.normalized = normalized;
	}
	
	/**
	 * this function compare action units of two cases and return true if equals and false else
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
