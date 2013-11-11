package businessLogic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

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
		for(int i=0;i<ProjectConfig.NUMBER_OF_ACTION_UNITS;i++)
		{
			ProjectConfig.auWeights[i]=1;
		}
		RunTimeCase rt = new RunTimeCase(NeuralNetworkManager.NormalizeAUs(new double[]{0.232, -0.05, 1.136, 0.527, -0.778, 0.122, 0.127, 0.067, 0.0, -0.048, 0.128}));
		ArrayList<RunTimeCase> sim = rm.getKSimilarCases(rt);
		nm.trainKclosestCases(sim);
		System.out.println("NetSolution:"+nm.computeOutput(rt));
		for(RunTimeCase r: sim){
			System.out.println("case: "+r);
			System.out.println("sol: "+r.getSolutionOutput());
			System.out.println("sim: "+r.similarity(rt));
		}

	}
	
	
	
}
