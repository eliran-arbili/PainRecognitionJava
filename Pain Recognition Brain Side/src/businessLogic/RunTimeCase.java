package businessLogic;

import java.util.Arrays;

import org.encog.util.arrayutil.NormalizedField;

import dataLayer.ProjectConfig;

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
	public RunTimeCase(double actionUnits[]){
		this.origActionUnits = Arrays.copyOf(actionUnits, actionUnits.length);
		this.actionUnits = Arrays.copyOf(actionUnits, actionUnits.length);
		solutionOutput = null;
		setNormalized(false);
	}
	
	public RunTimeCase(double actionUnits[], double [] solution){
		this.actionUnits = Arrays.copyOf(actionUnits, actionUnits.length);
		solutionOutput = Arrays.copyOf(solution, solution.length);
		setNormalized(false);
	}
	public RunTimeCase(double actionUnits[], double [] solution, boolean normalized){
		this(actionUnits,solution);
		setNormalized(normalized);
	}
	public RunTimeCase(double actionUnits[], boolean normalized){
		this(actionUnits);
		setNormalized(normalized);
	}
	
	/*
	 * Member functions
	 */
	public double[] getSolutionOutput() {
		return Arrays.copyOf(solutionOutput, solutionOutput.length);
	}
	public void setSolutionOutput(double[] solutionOutput) {
		this.solutionOutput = Arrays.copyOf(solutionOutput, solutionOutput.length);
	}
	public double getActionUnit(int i){
		return actionUnits[i];
	}
	public double [] getActionUnits()
	{
		return Arrays.copyOf(actionUnits,actionUnits.length);
	}
	
	public double [] getOrigActionUnits(){
		return Arrays.copyOf(origActionUnits,origActionUnits.length);
	}
	
	public String toString()
	{
		return Arrays.toString(actionUnits);
	}
	
	public void fuzzify()
	{	
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
	 
	
	public Double similarity(RunTimeCase rtCase) {
		double sum	= 0;
		Double [] auWeights = ProjectConfig.getOptDoubleArray("SIMILARITY_WEIGHTS");
		for(int i = 0; i < actionUnits.length; i++)
		{
			sum  +=  auWeights[i] * (Math.abs(this.actionUnits[i] - rtCase.actionUnits[i]));
		}
		
		return sum;
	}
	
	public boolean isNormalized() {
		return normalized;
	}

	public void setNormalized(boolean normalized) {
		this.normalized = normalized;
	}
	
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
