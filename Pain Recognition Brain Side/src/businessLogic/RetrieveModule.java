package businessLogic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

import dataLayer.DataBase;
import dataLayer.ProjectConfig;

/**
 * RetriveModule is  class that responsible to retrieve cases from casebase  
 * @author Eliran Arbili , Arie Gaon
 */
public class RetrieveModule {

	/*
	 * Member variables 
	 */
	private ArrayList<RunTimeCase> allCases;
	private DataBase caseDB;
	
	/*
	 * Constructors 
	 */
	/**
	 * Create new RetriveModule that can be used to access casebase   
	 */
    public RetrieveModule()
    {
    	caseDB  	= new DataBase(new File(ProjectConfig.getOpt("CSV_CASES_PATH")));
		allCases	= caseDB.GetAllCases();
	}
    /**
     * Create new RetriveModule with initial cases that can be used to access casebase
     * @param allCases - array list that contain all cases from casebase
     */
	public RetrieveModule( ArrayList<RunTimeCase> allCases)
	{
		caseDB 			= new DataBase(new File(ProjectConfig.getOpt("CSV_CASES_PATH")));
		this.allCases 	= allCases;
	}
	
	/*
	 * Member functions
	 */
	/**
	 * Get k similar cases from casebase for a given run time case
	 * The retrieval is done by exaine the similarity between the given case and the cases in casebase
	 * Those with lowest similarity to this case will be in the final queue
	 * @param rtCase - runtime case 
	 * @return Priority Queue that contain k closest cases from casebase
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
	
	
	/**
	 * Add new run time case to casebase
	 * @param rtCase - runtime case
	 * @return True if Added case success , false else
	 */
	public boolean addNewCase(RunTimeCase rtCase){
		try 
		{
			caseDB.AddCase(rtCase);
			if(ProjectConfig.getOptBool("FUZZY_MODE") == true){
				rtCase.fuzzify();
			}
			allCases.add(rtCase);
			return true;
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * Get all cases from casebase
	 * @return Array List that contain all cases
	 */
	public ArrayList<RunTimeCase> getAllCases(){
		return allCases;
	}
	
	/*public static void main(String[] args){
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

	}*/
	
	
	
}
