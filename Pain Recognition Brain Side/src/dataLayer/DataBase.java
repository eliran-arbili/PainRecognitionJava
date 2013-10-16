package dataLayer;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import businessLogic.RunTimeCase;


public class DataBase {
	public static Connection conn ;
	private final static DataBase instance = new DataBase();
	private DataBase()
	{
		try 
		{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {/* handle the error*/}
	

	    try 
	    {
	         conn = DriverManager.getConnection("jdbc:mysql://localhost/painrecognizedb","root","");
	        System.out.println("SQL connection succeed");
	 	} 
	    catch (SQLException ex) 
 	    {/* handle any errors*/
        	System.out.println("SQLException: " + ex.getMessage());
        	System.out.println("SQLState: " + ex.getSQLState());
        	System.out.println("VendorError: " + ex.getErrorCode());
        }
	}
	
	private DataBase(String user,String password)
	{
		try 
		{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } 
		catch (Exception ex) {/* handle the error*/}
	

	    try 
	    {
	         	conn = DriverManager.getConnection("jdbc:mysql://localhost/painrecognizedb",user,password);
	         	System.out.println("SQL connection succeed");
	 	} 
	    catch (SQLException ex) 
 	    {/* handle any errors*/
    		System.out.println("SQLException: " + ex.getMessage());
    		System.out.println("SQLState: " + ex.getSQLState());
    		System.out.println("VendorError: " + ex.getErrorCode());
        }
	}
	
	public static DataBase instance(){
		return instance;
	}
	
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
		Statement stmt1;
		Statement stmt2;
		ResultSet rsCases = null;
		ResultSet rsActionUnits = null;
		ResultSet rsCaseSolution = null;
		ArrayList<RunTimeCase> allCases = new ArrayList<RunTimeCase>();
		
		double[] actionUnits = new double[ProjectConfig.NUMBER_OF_ACTION_UNITS];
		
		try
		{
			int i=0;
			stmt = conn.createStatement();//Creates a Statement object for sending SQL statements to the database.
			stmt1=conn.createStatement();
			stmt2=conn.createStatement();
			
			rsCases=stmt.executeQuery("SELECT Case_Id from casetbl");
			while(rsCases.next())
			{
				rsCaseSolution=stmt1.executeQuery("SELECT Case_Solution from casetbl where Case_Id="+rsCases.getInt("Case_Id"));
				rsActionUnits=stmt2.executeQuery("SELECT AU_Value from auincasetbl where Case_Id=7");
				
				while (rsActionUnits.next())
				{
					actionUnits[i]=rsActionUnits.getDouble("AU_Value");
					System.out.println(actionUnits[i]);
					i++;
				}
				i=0;
				actionUnits = new double[ProjectConfig.NUMBER_OF_ACTION_UNITS];
				rsCaseSolution.first();
				allCases.add(new RunTimeCase(actionUnits,rsCaseSolution.getDouble("Case_Solution")));
			}
			
			return allCases;
			

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;

		}
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		DataBase db= DataBase.instance();
		double [] actionUnits={0.2,0.4,0.6,0.5,0.7};
		db.AddCase(actionUnits,0.7);

	}
	

}
