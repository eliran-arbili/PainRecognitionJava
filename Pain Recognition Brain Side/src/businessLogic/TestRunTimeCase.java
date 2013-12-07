package businessLogic;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import dataLayer.ProjectConfig;

public class TestRunTimeCase extends TestCase{
	
	private RunTimeCase rt1;
	private RunTimeCase rt2;
	private RunTimeCase rt3;
	private RunTimeCase rt4;
	private RunTimeCase rt5;
	private RunTimeCase rt6;
	private RunTimeCase rt7;
	
	@Before
	public void setUp() throws Exception {
		rt1 = new RunTimeCase(new double[]{0,0,0,0,0,0,0,0,0,0,0}, new double[]{0});
		rt2 = new RunTimeCase(new double[]{0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0,1.1}, new double[]{0.5});
		rt3 = new RunTimeCase(new double[]{0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0,1.1}, new double[]{0.5});
		rt4 = new RunTimeCase(new double[]{-0.1,-0.2,-0.3,-0.4,-0.5,-0.6,-0.7,-0.8,-0.9,-1.0,-1.1}, new double[]{-0.5});
		rt5 = new RunTimeCase(new double[]{0,0,0,0,0,0,0,0,0,0,0}, new double[]{0},true);
		rt6 = new RunTimeCase(new double[]{0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0,1.0}, new double[]{0.5},true);
		rt7 = new RunTimeCase(new double[]{0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1}, new double[]{0.1},false);
		rt5.fuzzify();
		rt6.fuzzify();
		rt7.normalize();
	}

	@Test
	public void testFuzzify() {
		double [] expectedFuzzify_5 = new double[]{0,0,0,0,0,0,0,0,0,0,0}; 
		double [] expectedFuzzify_6 = new double[]{0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0,1.0};
		assertArrayEquals(expectedFuzzify_5, rt5.getActionUnits(), 0.00001);
		assertArrayEquals(expectedFuzzify_6, rt6.getActionUnits(), 0.001);

	}

	@Test
	public void testSimilarity() {
		double expectedSimilarity_1_2 = 6.6;
		double expectedSimilarity_2_1 = 6.6;
		double expectedSimilarity_3_4 = 13.2;
		double expectedSimilarity_1_4 = 6.6;

		assertEquals(expectedSimilarity_1_2, rt1.similarity(rt2));
		assertEquals(expectedSimilarity_2_1, rt2.similarity(rt1));
		assertEquals(expectedSimilarity_3_4, rt3.similarity(rt4));
		assertEquals(expectedSimilarity_1_4, rt1.similarity(rt4));
	}

	@Test
	public void testEqualsObject() {
		assertTrue(rt2.equals(rt3));
		assertFalse(rt1.equals(rt2));
		assertFalse(rt3.equals(rt4));
	}

	@Test
	public void testNormalize() {
		try{
		double [] expectedActionUnits = rt7.getOrigActionUnits();
		Double [] ausMinNormLimits 	= ProjectConfig.getOptDoubleArray("AUS_NORM_MIN");
		Double [] ausMaxNormLimits 	= ProjectConfig.getOptDoubleArray("AUS_NORM_MAX");
		double normMinLimit			= ProjectConfig.getOptDouble("NORM_MIN_LIMIT");
		double normMaxLimit			= ProjectConfig.getOptDouble("NORM_MAX_LIMIT");
		
		for(int i = 0 ; i < expectedActionUnits.length; i++){
			double dl = ausMinNormLimits[i];
			double dh = ausMaxNormLimits[i];
			expectedActionUnits[i] = normMinLimit + 
					((expectedActionUnits[i] - dl)*(normMaxLimit - normMinLimit))/(dh - dl);
		}
		assertArrayEquals(expectedActionUnits, rt7.getActionUnits(), 0.0000000001);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
