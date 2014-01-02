package businessLogic;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.junit.Before;
import org.junit.Test;

import dataLayer.ProjectConfig;

public class TestProjectUtils extends TestCase{

	
	private BasicMLDataSet dataSetToSplit;
	private String windowsPath1;
	private String windowsPath2;
	private List<String> toJoin1;
	private List<Double> toJoin2;
	private File forGenerate1;
	private File forGenerate2;
	@Before
	public void setUp() throws Exception {
		windowsPath1 = "C:\\blahblah\\Blahblah\\blahlala\\csv.csv";
		windowsPath2 = "D:\\hello world";
		toJoin1 = Arrays.asList(new String[]{"C:","dir1","dir2","dir3","file.file"});
		toJoin2 = Arrays.asList(new Double[]{0.123,0.234,0.345,0.456,0.567});
		forGenerate1 = new File("C:\\Users\\user\\Desktop\\myFile - Edited.csv");
		forGenerate2 = new File("C:\\Users\\user\\Desktop\\stam-file-1.suffix");

		dataSetToSplit = new BasicMLDataSet();
		for(int i = 0 ; i < 50 ; i++){
			dataSetToSplit.add(generateRandomDataPair());
		}
	}

	@Test
	public void testSplitDataSetBasicMLDataSetInt() {
		
		for(int k = 2 ; k < 10 ; k++){
			ArrayList<BasicMLDataSet> splittedSets = ProjectUtils.splitDataSet(dataSetToSplit, k);
			BasicMLDataSet currSubSet = splittedSets.get(0);
			int dataSetIndex = 0;
			for(int i = 0,j = 0 ; j < dataSetToSplit.size() ;i++, j++){
				if(i == currSubSet.size()){
					i = 0;
					dataSetIndex++;
					currSubSet = splittedSets.get(dataSetIndex);
				}
				assertEquals(currSubSet.get(i), dataSetToSplit.get(j));
			}
		}
	}

	@Test
	public void testReplaceLast() {
		String expected1 = "C:\\blahblah\\Blahblah\\blahlala\\csv.blah";
		String expected2 = "D:\\hello my name is";
		assertEquals(expected1, ProjectUtils.replaceLast(windowsPath1, "csv", "blah"));
		assertEquals(expected2, ProjectUtils.replaceLast(windowsPath2, "world", "my name is"));
	}

	@Test
	public void testCombine() {
		String path 	= "C:\\Users\\earbili";
		String fileName = "stam.csv";
		assertEquals("C:\\Users\\earbili\\stam.csv", ProjectUtils.combine(path, fileName));
	}

	@Test
	public void testJoin() {
		String expected1 = "C:\\dir1\\dir2\\dir3\\file.file";
		String expected2 = "0.123,0.234,0.345,0.456,0.567";
		assertEquals(expected1, ProjectUtils.join("\\", toJoin1));
		assertEquals(expected2, ProjectUtils.join(",", toJoin2));
	}

	@Test
	public void testGenerateFile() {
		File expected1 = new File("C:\\Users\\user\\Desktop\\myFile - Edited_addition.csv");
		File expected2 = new File("C:\\Users\\user\\Desktop\\stam-file-1_123.suffix");
		
		assertEquals(expected1, ProjectUtils.generateFile(forGenerate1, "_addition"));
		assertEquals(expected2, ProjectUtils.generateFile(forGenerate2, "_123"));

	}
	
	private BasicMLDataPair generateRandomDataPair(){
		Random rand = new Random();
		double [] randInputDoubles = new double[ProjectConfig.getOptInt("NUMBER_OF_ACTION_UNITS")];
		double [] randOutputDoubles = new double[ProjectConfig.getOptInt("CASE_OUTPUT_COUNT")];
		for(int i = 0; i < randInputDoubles.length; i++){
			randInputDoubles[i] = rand.nextGaussian();
		}
		BasicMLDataPair pair = new BasicMLDataPair(new BasicMLData(randInputDoubles), new BasicMLData(randOutputDoubles));
		return pair;
	}

}
