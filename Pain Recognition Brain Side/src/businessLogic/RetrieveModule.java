package businessLogic;

import java.util.ArrayList;

import businessLogic.casesServer.CaseFactory;

public class RetrieveModule {
	private CaseFactory caseDB;
	public RetrieveModule(){
		caseDB = CaseFactory.instance();
	}
	public ArrayList<RunTimeCase> getKSimilarCases(RunTimeCase rtCase)
	{
		return caseDB.loadCasesByQuery("TO-DO:complete implementation");
	}
}
