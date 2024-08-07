package es.ull.iis.simulation.tests.WFP;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.beust.jcommander.Parameter;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestWFP {

    private final CommonArguments arguments = new CommonArguments();
    @BeforeEach
    public void setUp() {
        arguments.checkActivities = true;
        arguments.checkElements = true;
        arguments.checkResources = true;
        arguments.stdOutput = true;
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 17, 19, 21, 211, 212, 28, 30, 40})
    @DisplayName("Test all WFP")
    public void testAllWFP(int wfp) {
        arguments.wfp = wfp;
        final WFPTestExperiment exp = new WFPTestExperiment(arguments);
        exp.start();
    }

    @Test
    @DisplayName("Test WFP 1")
    public void testWFP1() {
        arguments.wfp = 1;
        final WFPTestExperiment exp = new WFPTestExperiment(arguments);
        exp.start();
    }
    
    public static class CommonArguments {
    	@Parameter(names = { "--output", "-o" }, description = "Enables printing to the standard output", order = 1)
    	public boolean stdOutput = false;
    	@Parameter(names = { "--checkresources", "-cr" }, description = "Enables checking resources", order = 2)
    	public boolean checkResources = false;
    	@Parameter(names = { "--checkelements", "-ce" }, description = "Enables checking elements", order = 3)
    	public boolean checkElements = false;
    	@Parameter(names = { "--checkactivities", "-ca" }, description = "Enables checking activities", order = 4)
    	public boolean checkActivities = false;
    	@Parameter(names = { "--wfp", "-wfp" }, description = "Selects a specific WFP to test with", order = 5)
    	public int wfp = -1;
    }
    
}
