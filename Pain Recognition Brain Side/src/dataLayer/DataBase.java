package dataLayer;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

import businessLogic.ProjectUtils;
import businessLogic.RunTimeCase;

/**
 * Implement the casebase   
 * @author Eliran Arbili , Arie Gaon
 *
 */
public class DataBase {
	
	/*
	 * Instance Variables
	 */
	private File casesCSVFile ;
	

	/*
	 * Constructors
	 */
	/**
	 * Create new DataBase from CSV file
	 * The CSV file should contain a collection of cases and all data access is done from and to this file
	 * @param casesCSVFile file that contain all cases
	 */
	public DataBase(File casesCSVFile) {
		this.casesCSVFile = casesCSVFile;
	}
	
	
	/*
	 * Member functions
	 */
	/**
	 * Add new RunTimeCase to casebase
	 * The case will be added to the CSV file 
	 * it's critical that the CSV file will not have blank like at end of file.
	 * @param rtCase - run time case
	 * @return True if added case success , false else
	 * @throws IOException
	 */
	public boolean AddCase(RunTimeCase rtCase) throws IOException{
		if(rtCase.getSolutionOutput() == null){
			return false;
		}
		File temp = ProjectUtils.generateFile(casesCSVFile, "_temp");
		temp.createNewFile();
		File f = Files.copy(casesCSVFile.toPath(), temp.toPath(), StandardCopyOption.REPLACE_EXISTING).toFile();
		BufferedWriter 	writer		= Files.newBufferedWriter(f.toPath(), Charset.defaultCharset(),StandardOpenOption.APPEND);
		String 			caseId		= "RunTimeCase";
		writer.append(System.getProperty("line.separator"));
		writer.append(ProjectUtils.joinDoubles(",", rtCase.getActionUnits(),null));
		writer.append(",");
		writer.append(ProjectUtils.joinDoubles(",",rtCase.getSolutionOutput(),null));
		writer.append(","+caseId);
		writer.close();
		Files.move(temp.toPath(), casesCSVFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		return true;
	}
	
	
	/**
	 * Get all cases from casebase 
	 * @return - all cases within the CSV file
	 */
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
