package businessLogic;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.encog.util.Format;

import dataLayer.DataBase;
import dataLayer.ProjectConfig;


public class RetrieveModule {

	/*
	 * Member variables 
	 */
	private ArrayList<RunTimeCase> allCases;
	private DataBase caseDB;
	
	/*
	 * Constructors 
	 */
    public RetrieveModule()
    {
    	caseDB  = DataBase.instance();
		allCases = caseDB.GetAllCases();
	}
    
	public RetrieveModule( ArrayList<RunTimeCase> allCases)
	{
		caseDB = DataBase.instance();
		this.allCases = allCases;
	}
	
	/*
	 * Member functions
	 */
	public ArrayList<RunTimeCase> getKSimilarCases(RunTimeCase rtCase)
	{
		HashMap<RunTimeCase,Double> kSimilarCases = new HashMap<RunTimeCase,Double>();
		
		Iterator<RunTimeCase> allCasesIterator = allCases.iterator();
		ProjectUtils.assertFalse(ProjectConfig.K_SIMILAR_CASES <= allCases.size() , "Number of cases in DataBase is less then configuered k similars");
		for(int i = 0; i< ProjectConfig.K_SIMILAR_CASES ; i++){
			RunTimeCase rt = allCasesIterator.next();
			kSimilarCases.put(rt,rt.similarity(rtCase));
		}
		while(allCasesIterator.hasNext()){
			updateSimilarCases(kSimilarCases,rtCase,allCasesIterator.next());
		}
		return new ArrayList<RunTimeCase>(kSimilarCases.keySet());
	}
	
	/*
	 * Auxiliary Methods
	 */
	private void updateSimilarCases(HashMap<RunTimeCase,Double> kSimilarCases,RunTimeCase theCase,RunTimeCase checkCase)
	{
		double similarity 			= theCase.similarity(checkCase);
		Iterator<RunTimeCase> it 	= kSimilarCases.keySet().iterator();
		RunTimeCase maxCase 		= null;
		double maxSim 				=-1;
		while(it.hasNext())
		{
			RunTimeCase currCase = it.next();
			double currSim = kSimilarCases.get(currCase);
			if(currSim > maxSim)
			{
				maxSim = currSim;
				maxCase = currCase;
			}
		}
		if(maxSim > similarity)
		{
			kSimilarCases.remove(maxCase);
			kSimilarCases.put(checkCase,similarity);
		}
	}
	
	
	/*
	 * Testing Unit
	 */
	/*public static void main(String[] args) {
		int numOfCases = 50000;
		Random rand = new Random();
		ArrayList<RunTimeCase> allCasesDemo = new ArrayList<RunTimeCase>();
		for(int i = 0 ; i < numOfCases; i++){
			double[] aus = new double[5];
			for (int j=0; j<5 ;j++){
				aus[j] = (double)rand.nextInt(200)/100 -1;
			}
			RunTimeCase c = new RunTimeCase(aus);
			c.setSolutionOutput((double)rand.nextInt(200)/100 -1);
			allCasesDemo.add(c);
		}
		
		double[] testAus = new double[]{0.1,0.2,0.3,0.4,0.5};
		RunTimeCase testCase = new RunTimeCase(testAus);
		RetrieveModule rm = new RetrieveModule(allCasesDemo);
		long startTime = System.nanoTime(); 
		ArrayList<RunTimeCase> kSimilar = rm.getKSimilarCases(testCase);
		long estimatedTime = System.nanoTime() - startTime;
		System.out.println("Time in nano seconds: "+estimatedTime);
		System.out.println("Time in seconds = "+ estimatedTime/(double)1000000000);
		System.out.println("Similar Cases:" +kSimilar.size());
		for(RunTimeCase c: kSimilar){
			System.out.println("similarity: "+c.similarity(testCase)+ "  "+c.toString());
		}


	}*/
	
	public static void main(String[] args){
		RetrieveModule rm = new RetrieveModule();
		NeuralNetworkManager nm = NeuralNetworkManager.createInstance(new File("C:\\Users\\user\\Desktop\\MLP_val0.1329_trn0.0290_te0.1329_it144.eg"));
		ProjectConfig.initWeights();
		RunTimeCase rt = new RunTimeCase(new double[]{0.8478723404255318, 0.17839999999999998, 1.0, 0.6932989690721649, 0.3420863309352518, 0.4533783783783784, 0.6581818181818181, 0.4707964601769912, 0.0, 0.37672413793103443, 0.5659722222222223});
		ArrayList<RunTimeCase> sim = rm.getKSimilarCases(rt);
		double [] weightsBefore = Arrays.copyOf(nm.getNeuralNet().getFlat().getWeights(), nm.getNeuralNet().getFlat().getWeights().length);
		nm.trainKclosestCases(sim);


		System.out.println("NetSolution:"+nm.computeOutput(rt));
		for(RunTimeCase r: sim){
			System.out.println("case: "+Arrays.toString(r.getOrigActionUnits()));
			System.out.println("sol: "+r.getSolutionOutput());
			System.out.println("sim: "+r.similarity(rt));
		}
		double [] weightsAfter = nm.getNeuralNet().getFlat().getWeights();

		double max = 0;
		int maxIndex = 0;
		double averageRatio = 0;
		for(int i = 0 ; i < weightsAfter.length; i++){
			if(weightsBefore[i] != 0){
				double diffRatio = Math.abs(weightsBefore[i] - weightsAfter[i]) / Math.abs(weightsBefore[i]);
				averageRatio += diffRatio;
				if(diffRatio > max){
					max = diffRatio;
					maxIndex = i;
				}
			}
		}
		averageRatio = averageRatio /  weightsAfter.length;
		System.out.println(Arrays.toString(weightsBefore));
		System.out.println(Arrays.toString(weightsAfter));

		System.out.println("\nmax: " + Format.formatPercent(max) +"   Index:" + maxIndex);
		System.out.println("Aaverage: " + Format.formatPercent(averageRatio));
		System.out.println(weightsBefore[maxIndex]);
		System.out.println(weightsAfter[maxIndex]);


	}
	
	
	
}
