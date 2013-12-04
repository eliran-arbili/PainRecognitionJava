package dataLayer;


import java.awt.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;

import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

import businessLogic.ProjectUtils;
import businessLogic.RunTimeCase;


public class DataBase {
	
	/*
	 * Instance Variables
	 */
	public File casesCSVFile ;
	

	/*
	 * Constructors
	 */

	public DataBase(File casesCSVFile) {
		this.casesCSVFile = casesCSVFile;
	}
	
	
	/*
	 * Member functions
	 */
	
	public void AddCase(Double[] actionUnits,Double [] solution) throws IOException{
		File temp = ProjectUtils.generateFile(casesCSVFile, "_temp");
		temp.createNewFile();
		File f = Files.copy(casesCSVFile.toPath(), temp.toPath(), StandardCopyOption.REPLACE_EXISTING).toFile();
		BufferedWriter 	writer		= Files.newBufferedWriter(f.toPath(), Charset.defaultCharset(),StandardOpenOption.APPEND);
		String 			caseId		= "RunTimeCase";
		writer.append(System.getProperty("line.separator"));
		writer.append(ProjectUtils.join(",", Arrays.asList(actionUnits)));
		writer.append(",");
		writer.append(ProjectUtils.join(",",Arrays.asList(solution)));
		writer.append(","+caseId);
		writer.close();
		Files.move(temp.toPath(), casesCSVFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
	
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
