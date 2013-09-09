package businessLogic;

import java.util.Arrays;

public class RunTimeCase {
	public static final double NO_SOLUTION = 99;
	private double actionUnits[];
	private double solutionOutput;
	
	public RunTimeCase(double actionUnits[]){
		this.actionUnits = Arrays.copyOf(actionUnits, actionUnits.length);
		solutionOutput = NO_SOLUTION;
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
	
}
