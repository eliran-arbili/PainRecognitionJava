package businessLogic;

import java.util.ArrayList;

import dataLayer.DataBase;

public class RetrieveModule {
	private DataBase caseDB;
	public RetrieveModule(){
		caseDB = DataBase.instance();
	}
	public ArrayList<RunTimeCase> getKSimilarCases(RunTimeCase rtCase)
	{
		/**
		 * TO-DO: complete implementation with caseDB generic query function implementation
		 */
		return null;
	}
}
