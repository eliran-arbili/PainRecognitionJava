package dataLayer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

public class DataBase {
	public static Connection conn ;

	public DataBase()
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
	
	public DataBase(String user,String password)
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
	
	public void AddCase(String [][] ActionUnits){
	Statement stmt;
    ResultSet rs = null;
    ResultSet rs2 = null;
    int i=0;
		try
		{
			
		    stmt = conn.createStatement();//Creates a Statement object for sending SQL statements to the database.
			rs=stmt.executeQuery("insert into casetbl(Case_Resulte)values(2);");
			rs=stmt.executeQuery("select Case_Id  from casetbl order by Case_Id desc limit 1;");

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			
		}
	}

}
