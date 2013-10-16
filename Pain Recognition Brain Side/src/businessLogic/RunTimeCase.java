package businessLogic;

import java.util.Arrays;

import dataLayer.ProjectConfig;

public class RunTimeCase {
	public static final double NO_SOLUTION = 99;
	private double actionUnits[];
	private double solutionOutput;
	


	public RunTimeCase(double actionUnits[]){
		this.actionUnits = Arrays.copyOf(actionUnits, actionUnits.length);
		solutionOutput = NO_SOLUTION;
	}
	
	public RunTimeCase(double actionUnits[], double solution){
		this.actionUnits = Arrays.copyOf(actionUnits, actionUnits.length);
		solutionOutput = solution;
	}
	
	public double getSolutionOutput() {
		return solutionOutput;
	}
	public void setSolutionOutput(double solutionOutput) {
		this.solutionOutput = solutionOutput;
	}
	public double getActionUnit(int i){
		return actionUnits[i];
	}
	public double [] getActionUnits()
	{
		return Arrays.copyOfRange(actionUnits, 0, actionUnits.length);
	}
	
	public String toString()
	{
		return Arrays.toString(actionUnits);
	}

	
	

	
	public double similarity(RunTimeCase rtCase) {
		double sum=0;
		for(int i=0;i<ProjectConfig.NUMBER_OF_ACTION_UNITS;i++)
		{
			sum+=ProjectConfig.auWeights[i]*(Math.abs(this.actionUnits[i]-rtCase.actionUnits[i]));
		}
		
		return sum;
	}
	
	
	
	
}
