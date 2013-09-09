package businessLogic.casesServer;

import java.util.ArrayList;

import businessLogic.RunTimeCase;

public class CaseFactory {
	
	private final static CaseFactory instance = new CaseFactory();
	private CaseFactory()
	{
		/** 
		 * TO-DO: configure connection to DB
		 */
	}
	public static CaseFactory instance(){
		return instance;
	}
	public ArrayList<RunTimeCase> loadCasesByQuery(String query)
	{
		/** 
		 * TO-DO: configure connection to DB
		 */
		return null;
	}
}
