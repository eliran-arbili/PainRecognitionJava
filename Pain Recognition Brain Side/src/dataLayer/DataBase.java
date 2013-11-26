package dataLayer;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import businessLogic.RunTimeCase;


public class DataBase {
	
	/*
	 * Instance Variables
	 */
	public static Connection conn ;
	private final static DataBase instance = new DataBase();
	
	/*
	 * Constructors
	 */
	private DataBase()
	{
		this("root","");
	}
	
	private DataBase(String user,String password)
	{
		try 
		{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } 
		catch (Exception ex) 
		{
			System.exit(1);
		}
	

	    try 
	    {
	    	conn = DriverManager.getConnection("jdbc:mysql://" + ProjectConfig.getOpt("DB_ADDRESS") + "/painrecognizedb",user,password);
	    	System.out.println("SQL connection succeed");
	 	} 
	    catch (SQLException ex) 
 	    {
    		System.out.println("SQLException: " + ex.getMessage());
    		System.out.println("SQLState: " + ex.getSQLState());
    		System.out.println("VendorError: " + ex.getErrorCode());
			System.exit(1);

        }
	}
	
	/*
	 * Class functions
	 */
	public static DataBase instance(){
		return instance;
	}
	
	/*
	 * Member functions
	 */
	
	public void PrintAUs() {
		
		Statement stmt;
	    ResultSet rs = null;
	
	    try 
		{
			stmt = conn.createStatement();//Creates a Statement object for sending SQL statements to the database.
			rs=stmt.executeQuery("SELECT *  FROM autbl");
			
	 		while(rs.next())
	 		{
				
	 	        String AU_Num = rs.getString("AU_Num");
	 	        String AU_Name = rs.getString("AU_Name");
	 	        System.out.format("AU%s -  %s\n", AU_Num, AU_Name);
			} 
	 		rs.close();
		}
	 		 
		catch (SQLException e)
		{
			e.printStackTrace();
			
		}

	}
	
	public void AddCase(double[] actionUnits,double solution){
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
	}
	
	public ArrayList<RunTimeCase> GetAllCases(){
		Statement stmt;
		ResultSet 				rsCases 		= null;
		ArrayList<RunTimeCase> 	allCases 		= new ArrayList<RunTimeCase>();
		String [] 				auNames			= ProjectConfig.getOptArray("AUS");
		double [] 				actionUnits 	= new double[auNames.length];
		String []				outputFields 	= ProjectConfig.getOptArray("OUTPUT_FIELDS");
		double [] 				result 			= new double[outputFields.length];

		try
		{
			stmt 	= 	conn.createStatement();//Creates a Statement object for sending SQL statements to the database.
			rsCases = stmt.executeQuery("SELECT * from norm_CasesTbl");
			while(rsCases.next())
			{
				for(int i = 0 ; i < actionUnits.length ; i++){
					actionUnits[i] 	= rsCases.getDouble(auNames[i]);
				}
				for(int i = 0 ; i < outputFields.length ; i++){
					result[i]		= rsCases.getDouble(outputFields[i]);
				}
				RunTimeCase rs = new RunTimeCase(actionUnits,result);
				if(ProjectConfig.fuzzyMode)
					rs.fuzzify();
				if(!allCases.contains(rs))
					allCases.add(rs);
			}
			return allCases;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * Testing Unit
	 */
	public static void main(String[] args) {
		DataBase db = DataBase.instance();
		double [] actionUnits={0.2,0.4,0.6,0.5,0.7};
		db.AddCase(actionUnits,0.7);

	}
	

}
