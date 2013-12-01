package dataLayer;


import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

import businessLogic.RunTimeCase;


public class DataBase {
	
	/*
	 * Instance Variables
	 */
	public File casesCSVFile ;
	
	
	public DataBase(File casesCSVFile) {
		this.casesCSVFile = casesCSVFile;
	}

	/*
	 * Constructors
	 */


	
	
	/*
	 * Member functions
	 */
	
/*	public void AddCase(double[] actionUnits,double solution){
		Statement stmt;
		ResultSet rs = null;

		try
		{
			
			stmt = conn.createStatement();//Creates a Statement object for sending SQL statements to the database.
			stmt.executeUpdate("insert into casetbl(Case_Resulte)values("+solution+");",Statement.RETURN_GENERATED_KEYS);
			rs=stmt.getGeneratedKeys();
			rs.first();
			String caseId=rs.getString(1);
			for(int i=0;i<actionUnits.length;i++)
			{
				stmt.executeUpdate("insert into AUInCaseTbl(Case_Id,AU_Num,AU_Value)values("+caseId+","+(i+1)+","+actionUnits[i]+");");
			}
			rs.close();

		}
		catch (SQLException e)
		{
			e.printStackTrace();

		}
	}*/
	
	public ArrayList<RunTimeCase> GetAllCases(){
		
		try{
			
			ArrayList<RunTimeCase> 	allCases 		= new ArrayList<RunTimeCase>();
			String [] 				auNames			= ProjectConfig.getOptArray("AUS");
			double [] 				actionUnits 	= new double[auNames.length];
			String []				outputFields 	= ProjectConfig.getOptArray("OUTPUT_FIELDS");
			double [] 				result 			= new double[outputFields.length];

			ReadCSV csv = new ReadCSV(new FileInputStream(casesCSVFile), true, CSVFormat.ENGLISH);
			while(csv.next()){
				for(int i = 0 ; i < actionUnits.length; i++){
					actionUnits[i] = csv.getDouble(auNames[i]);
				}
				for(int i = 0 ; i < outputFields.length ; i++){
					result[i]		= csv.getDouble(outputFields[i]);
				}
				RunTimeCase rs = new RunTimeCase(actionUnits,result);
				if(ProjectConfig.getOptBool("FUZZY_MODE") == true)
					rs.fuzzify();
				if(!allCases.contains(rs))
					allCases.add(rs);
			}
			csv.close();
			return allCases;
		}
		catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}

}
