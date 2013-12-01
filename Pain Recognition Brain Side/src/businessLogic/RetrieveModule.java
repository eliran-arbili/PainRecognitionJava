package businessLogic;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
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
	public PriorityQueue<RunTimeCase> getKSimilarCases(final RunTimeCase rtCase)
	{
		PriorityQueue<RunTimeCase> kSimilarCases = new PriorityQueue<RunTimeCase>(ProjectConfig.getOptInt("K_SIMILAR_CASES"), 
				new Comparator<RunTimeCase>() {
					@Override
					public int compare(RunTimeCase o1, RunTimeCase o2) {
						return o2.similarity(rtCase).compareTo(o1.similarity(rtCase));
					}
				});
		
		Iterator<RunTimeCase> allCasesIterator = allCases.iterator();
		Integer kNumber = ProjectConfig.getOptInt("K_SIMILAR_CASES");
		ProjectUtils.assertFalse(kNumber <= allCases.size() , "Number of cases in DataBase is less then configuered k similars");
		for(int i = 0; i< kNumber ; i++){
			RunTimeCase rt = allCasesIterator.next();
			kSimilarCases.offer(rt);
		}
		while(allCasesIterator.hasNext()){
			RunTimeCase currCase = allCasesIterator.next();
			if(kSimilarCases.peek().similarity(rtCase) > currCase.similarity(rtCase)){
				kSimilarCases.poll();
				kSimilarCases.offer(currCase);
			}
			
		}
		
		return kSimilarCases;
	}
	
	
	/*
	 * Auxiliary Methods
	 */

	
	
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
		RunTimeCase rt = new RunTimeCase(new double[]{0.8478723404255318, 0.17839999999999998, 1.0, 0.6932989690721649, 0.3420863309352518, 0.4533783783783784, 0.6581818181818181, 0.4707964601769912, 0.0, 0.37672413793103443, 0.5659722222222223});
		
		long start1 = System.nanoTime();
		PriorityQueue<RunTimeCase> sim = rm.getKSimilarCases(rt);
		long elapsed = System.nanoTime() - start1;

		
		
		System.out.println("Regular cases");

		for(RunTimeCase r: sim){
			ProjectUtils.assertFalse(sim.contains(r), "Older retrival does not contain new Retrieval");
		}
		
		System.out.println("first: "+elapsed);

		/*double [] weightsBefore = Arrays.copyOf(nm.getNeuralNet().getFlat().getWeights(), nm.getNeuralNet().getFlat().getWeights().length);
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
		System.out.println(weightsAfter[maxIndex]);*/


	}
	
	
	
}
