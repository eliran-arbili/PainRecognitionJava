package businessLogic;

import java.util.Arrays;

import dataLayer.DataBase;
import dataLayer.ProjectConfig;

public class CBRController {
	public CBRController()
	{
		/**
		 * TO-DO: complete implementation
		 */
	}
	public double[] fuzzification(double [] actionUnits)
	{
		int rangeDistributionNum = 10;
		double[] fuzzyActionUnits = new double[actionUnits.length];
		double [] fuzzy=new double[rangeDistributionNum/2+1];
		double factor = (ProjectConfig.FUZZY_AU_MAX_LIMIT-ProjectConfig.FUZZY_AU_MIN_LIMIT)/rangeDistributionNum;
		fuzzy[0] = 0;
		for(int i = 1; i < fuzzy.length; i++){
			fuzzy[i]=fuzzy[i-1]+factor;
		}
		
		for(int i=0;i<actionUnits.length;i++)
		{
			int index=(int)Math.round((Math.abs(actionUnits[i])/factor));
			fuzzyActionUnits[i]= fuzzy[index] * Math.signum(actionUnits[i]);
		}
		
		return fuzzyActionUnits;
	}
	public static void main(String[] args) {
		double [] actionUnitsInput={-0.938,-0.75,-0.6,-0.397,-0.116,0,0.114,0.588,0.799,0.9};
		double [] fuzzyExpectedOutput = {-1,-0.8,-0.6,-0.4,-0.2,0,0.2,0.6,0.8,1};
		CBRController cbr = new CBRController();
		double [] testResult = cbr.fuzzification(actionUnitsInput);
		double epsilon = 0.0001;
		for(int i = 0 ; i< testResult.length ; i++){
			ProjectUtils.assertFalse((Math.abs(testResult[i] - fuzzyExpectedOutput[i]) < epsilon), "Test Failed");
		}
	}
}
