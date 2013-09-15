package businessLogic;

public class CBRController {
	public CBRController()
	{
		/**
		 * TO-DO: complete implementation
		 */
	}
	public double[] fuzzification(double [] actionUnits)
	{
		double rangeNum=1;
		int rangeSize=10;
		double error=0.01; 
		double [] fuzzy=new double[rangeSize];
		double factor=rangeNum/rangeSize;
		for(int i=0;i<rangeSize;i++)
			fuzzy[i]=fuzzy[i]+factor;
		for(int i=0;i<actionUnits.length;i++)
		{
			int index=(int)Math.round((actionUnits[i]-error)/factor);
			actionUnits[i]=(index>0)?index-1:0;
		}
		
		return actionUnits;
	}
}
