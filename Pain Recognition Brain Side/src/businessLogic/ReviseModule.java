package businessLogic;
import java.io.IOException;

import dataLayer.DataBase;
import dataLayer.ProjectConfig;



public class ReviseModule {
	
	private DataBase caseDB;
	private NeuralNetworkManager painRecAnn;
	
	
	
	public boolean revise(RunTimeCase rtCase, double[] newSol){
		if(caseDB == null){
			caseDB = new DataBase(ProjectConfig.getCSVByTag(ProjectConfig.getCurrentTag()));
		}
		if(painRecAnn == null){
			painRecAnn = NeuralNetworkManager.createInstance(ProjectConfig.getANNFileByTag(ProjectConfig.getCurrentTag()));
		}
		
		if(rtCase.isNormalized() == false){
			rtCase.normalize();
		}
		rtCase.setSolutionOutput(newSol);
		
		try 
		{
			boolean isSuccess = caseDB.AddCase(rtCase);
			if(isSuccess == false){
				return false;
			}
			painRecAnn.trainByDataSet(caseDB.GetAllCases(), 2, 1);
			return true;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
		}	
	}
	
	

}
