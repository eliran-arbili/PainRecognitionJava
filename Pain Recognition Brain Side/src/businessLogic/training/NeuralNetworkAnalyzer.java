package businessLogic.training;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.encog.neural.networks.ContainsFlat;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.Format;

import businessLogic.ProjectUtils;
import businessLogic.RunTimeCase;
import dataLayer.ProjectConfig;

public class NeuralNetworkAnalyzer {
	
	/*
	 * Instance Variables
	 */
	private ContainsFlat neuralNet;
	private File targetFile;
	private Workbook wbHandle;
	
	/*
	 * Constructors
	 */
	
	public NeuralNetworkAnalyzer(ContainsFlat neuralNet, File targetDirectory) {
		this.neuralNet = neuralNet;
		targetFile = new File(ProjectUtils.combine(targetDirectory.getAbsolutePath(), "ann_analys.xls"));
		wbHandle = new HSSFWorkbook();	
	}
	
	/*
	 * Member Functions
	 */
	public boolean saveWork(){
	    try 
	    {
		    FileOutputStream fileOut = new FileOutputStream(targetFile);
			wbHandle.write(fileOut);
		    fileOut.close();
		    return true;
		} 
	    catch (IOException e) 
	    {
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	public void analyzeCombinations(RunTimeCase neutralCase, int combinations) throws Exception{

		String [] auNames = ProjectConfig.getOptArray("AUS");
		ProjectUtils.assertFalse(combinations < 5 && combinations > 0, "Combinations Must Be Less Than 5 and Bigger than 0");

		if(neutralCase.isNormalized() == false){
			neutralCase.normalize();
		}
		double [] auNeutralValues = neutralCase.getActionUnits();
		Sheet sheet = wbHandle.createSheet(sheetName(combinations));
		CreationHelper createHelper = wbHandle.getCreationHelper();
		int sheetRowIndex = 0;
	    double [] neutralOutput = new double [ProjectConfig.getOptInt("CASE_OUTPUT_COUNT")] ;
		neuralNet.getFlat().compute(auNeutralValues, neutralOutput);
		Row infoRow = sheet.createRow(sheetRowIndex++);
		infoRow.createCell(0).setCellValue("Neutral Case");
		infoRow.createCell(2).setCellValue("Output Pain");
		CellStyle style = wbHandle.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		infoRow.getCell(0).setCellStyle(style);
		infoRow.getCell(2).setCellStyle(style);
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
				
		for(int i = 0 ; i < auNames.length ; i++){
			Row row = sheet.createRow(sheetRowIndex++);
			row.createCell(0).setCellValue(auNames[i]);
			row.createCell(1).setCellValue(auNeutralValues[i]);
			row.getCell(0).setCellStyle(style);
			row.getCell(1).setCellStyle(style);
			if(i == 0){
				row.createCell(2).setCellValue(neutralOutput[0]);
				row.getCell(2).setCellStyle(style);
			}
		}
		sheet.addMergedRegion(new CellRangeAddress(1, auNames.length, 2, 2));


		Row headers = sheet.createRow(sheetRowIndex++);
		headers.createCell(0).setCellValue("Action Units");
		headers.createCell(1).setCellValue(createHelper.createRichTextString("Added [%]"));
		headers.createCell(2).setCellValue(createHelper.createRichTextString("Output Effect [%]"));
		headers.createCell(3).setCellValue(createHelper.createRichTextString("Output Pain"));

		for(int au1 = 0 ; au1 < auNames.length ; au1++){
			if(combinations > 1){

				for(int au2 = au1+1; au2 < auNames.length ; au2++){
					if(combinations > 2){

						for(int au3 = au2+1; au3 < auNames.length ; au3++){
							if(combinations > 3){

								for(int au4 = au3+1; au4< auNames.length; au4++){
									sheetRowIndex = produceNewBlock(auNeutralValues,sheet,sheetRowIndex,au1,au2,au3,au4);
								}
							}
							else
							{
								sheetRowIndex = produceNewBlock(auNeutralValues,sheet,sheetRowIndex,au1,au2,au3);
							}
						}
					}
					else
					{
						sheetRowIndex = produceNewBlock(auNeutralValues,sheet,sheetRowIndex,au1,au2);
					}	
				}
			}
			else
			{
				sheetRowIndex = produceNewBlock(auNeutralValues,sheet,sheetRowIndex,au1);

			}
		}
		
		sheet.setColumnWidth(0, 5500);
		for(int i = 1 ; i < sheet.getRow(0).getLastCellNum(); i++){
			sheet.autoSizeColumn(i);
		}

	}
	
	public void printAnalyzeByArray(RunTimeCase neutralCase, double [] addPercents){
		String [] auNames = ProjectConfig.getOptArray("AUS");
		double [] auNeutralValues = neutralCase.getActionUnits();
		ProjectUtils.assertFalse(auNames.length == addPercents.length && auNames.length == auNeutralValues.length, "Number Of AU's Must be Same size as Config File");
		double adders[] = new double[auNames.length];
		double [] newCase = new double[auNames.length];
		for(int i = 0 ; i < auNames.length; i++){
			adders[i] = addPercents[i] * auNeutralValues[i];
		}
		addArray(newCase, auNeutralValues, adders);
		double[] newOutput 		= new double[ProjectConfig.getOptInt("CASE_OUTPUT_COUNT")];
		double[] neutralOutput 	= new double[ProjectConfig.getOptInt("CASE_OUTPUT_COUNT")];
		neuralNet.getFlat().compute(auNeutralValues,neutralOutput);
		neuralNet.getFlat().compute(newCase,newOutput);
		double changeRatio = (newOutput[0] - neutralOutput[0]) / neutralOutput[0];
		System.out.println("Neutral Output:" + neutralOutput[0]);
		System.out.println("New Output:" + newOutput[0]);
		System.out.println("Effect :"+ Format.formatPercent(changeRatio));
		System.out.println("Raise info:");
		for(int i = 0 ; i < auNames.length; i++){
			if(adders[i] != 0){
				double addedRatio = addPercents[i];
				String added = Format.formatPercent(addedRatio);
				System.out.println(auNames[i]+": added "+added);
			}
		}
	}
	
	/*
	 * Auxiliary Methods
	 */
	private String sheetName(int combinations){
		switch(combinations){
		case 1: return "Singles";
		case 2: return "Pairs";
		default:return "Groups of" + combinations; 
		}
	}


	private static void addArray(double[] target, double [] source, double [] toAdd){
		for(int i = 0 ; i < toAdd.length; i++){
			target[i] = source[i] + toAdd[i];
		}
	}

	private int produceNewBlock(double[] auNeutralValues, Sheet sheet ,int sheetRowIndex,int... args) {
		String [] auNames 	= ProjectConfig.getOptArray("AUS");
		double adders [] 	= new double[auNames.length];
	    double [] neutralOutput = new double [ProjectConfig.getOptInt("CASE_OUTPUT_COUNT")] ;
		neuralNet.getFlat().compute(auNeutralValues, neutralOutput);
		StringBuilder ausStr = new StringBuilder();
		for(int au: args){
			ausStr.append(auNames[au]);
			ausStr.append("\n");
		}
		ausStr.deleteCharAt(ausStr.length()-1);
	    CreationHelper createHelper = wbHandle.getCreationHelper();
	    int initRowIndex = sheetRowIndex;
		for(int i = -3 ; i <= 7 ; i +=2){
			Row row = sheet.createRow(sheetRowIndex++);
			if(i == -3){
			    CellStyle cs = wbHandle.createCellStyle();
			    cs.setWrapText(true);
			    cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			    row.createCell(0).setCellStyle(cs);
			    row.getCell(0).setCellValue(createHelper.createRichTextString(ausStr.toString()));
			}
				
			double addedRatio = (double)i/10;
			Cell cell = row.createCell(1);
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellValue(createHelper.createRichTextString(Format.formatPercent(addedRatio)));
			for(int au: args){
				adders[au] = addedRatio*auNeutralValues[au];
			}
			double [] newCase = new double[auNames.length];
			addArray(newCase,auNeutralValues,adders);
			double newOutput[] = new double[1];
			neuralNet.getFlat().compute(newCase,newOutput);
			double changeRatio = (newOutput[0] - neutralOutput[0]) / neutralOutput[0];
			row.createCell(2).setCellValue(createHelper.createRichTextString(Format.formatPercentWhole(changeRatio)));
			row.createCell(3).setCellValue(newOutput[0]);
		}
		sheet.addMergedRegion(new CellRangeAddress(initRowIndex,sheetRowIndex-1,0,0));

		return sheetRowIndex;
	}
	

	
	public static void main(String[] args){
		//writeDefaultPropertiesFile();
		File egAnn = ProjectConfig.getANNFileByTag(new File(ProjectUtils.combine(ProjectConfig.TRAINING_TAGS_PATH, "sample"))); 
		ContainsFlat myNet = (ContainsFlat)EncogDirectoryPersistence.loadObject(egAnn);
		NeuralNetworkAnalyzer analyzer = new NeuralNetworkAnalyzer(myNet,new File("C:\\Users\\earbili\\Desktop"));
		double [] neutralValues = new double[]
{0.4482758620689655,0.024,0.03289473684210526,0.4690721649484536,0.4568345323741007,0.3445945945945946,0.5590909090909091,0.47787610619469034,0,0.37068965517241376,0.647887323943662};
		double [] addPercents = new double[]{0,0,0,0,0,0,0,0.2,0,0,0};
		RunTimeCase neutralCase = new RunTimeCase(neutralValues,true);
		try 
		{
			analyzer.printAnalyzeByArray(neutralCase, addPercents);
			//analyzer.analyzeCombinations(neutralCase,1);
			//analyzer.analyzeCombinations(neutralCase,2);
			//analyzer.analyzeCombinations(neutralCase,3);
			//analyzer.saveWork();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
}
