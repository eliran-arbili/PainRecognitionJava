package businessLogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import dataLayer.DataBase;
import dataLayer.ProjectConfig;

public class RetrieveModule {
	private DataBase db;
	private ArrayList<RunTimeCase> allCases;
	public RetrieveModule(){
		db=new DataBase();
		allCases = new ArrayList<RunTimeCase>();
		allCases=db.GetAllCases();
		
	}
	public RetrieveModule( ArrayList<RunTimeCase> allCases){
		db=new DataBase();
		this.allCases = allCases;
		
	}
	public ArrayList<RunTimeCase> getKSimilarCases(RunTimeCase rtCase)
	{
		
		HashMap<RunTimeCase,Double> kSimilarCases = new HashMap<RunTimeCase,Double>();
		
		Iterator<RunTimeCase> allCasesIterator = allCases.iterator();
		ProjectUtils.assertFalse(ProjectConfig.K_Similarity_Cases <= allCases.size() , "Number of cases in DataBase is less then configuered k similars");
		for(int i = 0; i< ProjectConfig.K_Similarity_Cases ; i++){
			RunTimeCase rt= allCasesIterator.next();
			kSimilarCases.put(rt,rt.similarity(rtCase));
		}
		while(allCasesIterator.hasNext()){
			updateSimilarCases(kSimilarCases,rtCase,allCasesIterator.next());
		}
		return new ArrayList<RunTimeCase>(kSimilarCases.keySet());
		
	}
	
	private void updateSimilarCases(HashMap<RunTimeCase,Double> kSimilarCases,RunTimeCase theCase,RunTimeCase checkCase)
	{
		double similarity = theCase.similarity(checkCase);
		Iterator<RunTimeCase> it= kSimilarCases.keySet().iterator();
		RunTimeCase maxCase=null;
		double maxSim=-1;
		while(it.hasNext())
		{
			RunTimeCase currCase=it.next();
			double currSim=kSimilarCases.get(currCase);
			if(currSim>maxSim)
			{
				maxSim=currSim;
				maxCase=currCase;
			}
		}
		if(maxSim>similarity)
		{
			kSimilarCases.remove(maxCase);
			kSimilarCases.put(checkCase,similarity);
		}
	}
	
	
	public static void main(String[] args) {
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
/*		System.out.println("All Cases:"+allCasesDemo.size());
		for(RunTimeCase c: allCasesDemo){
			System.out.println(similarity(c,testCase));
		}*/

	}
	
	
	
}
