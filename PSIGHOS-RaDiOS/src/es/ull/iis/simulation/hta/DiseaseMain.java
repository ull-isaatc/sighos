/**
 * 
 */
package es.ull.iis.simulation.hta;

import java.io.ByteArrayOutputStream;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import es.ull.iis.simulation.hta.diab.T1DMModel;
import es.ull.iis.simulation.hta.osdi.OSDiGenericModel;
import es.ull.iis.simulation.hta.outcomes.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.pbdmodel.PBDModel;
import es.ull.iis.simulation.hta.simpletest.TestSimpleRareDiseaseModel;

/**
 * Main class to launch simulation experiments
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public class DiseaseMain extends HTAExperiment {
	
	private final static int TEST_RARE_DISEASE1 = 1; 
	private final static int TEST_RARE_DISEASE2 = 2; 
	private final static int TEST_RARE_DISEASE3 = 3; 
	private final static int TEST_RARE_DISEASE4 = 4; 
	private final static int TEST_PBD = 5;
	private final static int TEST_SCD = 6;
	private final static int TEST_T1DM_DCCT1 = 10;
	private final static int TEST_T1DM_DCCT2 = 11;
	private final static int TEST_T1DM_DCCT1_CONV = 12;
	private final static int TEST_T1DM_DCCT1_INTENS = 13;
	
	private final static boolean REPLACE_DOT_WITH_COLON = false;
	private final static boolean ALL_AFFECTED = true;
	// private final static String PARAMS = "-n 1000 -r 0 -t 0 -dis 12 -y 2019 -q -ep cr -po"; // Testing diabetes
	private final static String PARAMS = "-n 1000 -r 0 -t 0 -dis " + TEST_T1DM_DCCT1_CONV + " -y 2019 -q -ep cr"; // Testing conventional DCCT
//	private final static String PARAMS = "-n 5000 -r 0 -t 0 -dis 4 -dr 0 -ep ia -q"; // Testing test diseases
//	private final static String PARAMS = "-n 100 -r 0 -dr 0 -q -t 0 -dis 1 -ps 3 -po"; // -o /tmp/result_david.txt
//	private final static String PARAMS = "-n 1000 -r 0 -dr 0 -q -t 1 -dis 1 -ps 3 -po"; // -o /tmp/result_david.txt

	public DiseaseMain(Arguments arguments, ByteArrayOutputStream simResult) throws MalformedSimulationModelException {
		super(arguments, simResult);
	}
	
	public static class Arguments extends CommonArguments {
		@Parameter(names = { "--type", "-t" }, description = "Selects an alternative scenario (0: Programmatic; 1: OSDi). If OSDi is selected, a model name must be specified with the -m option; otherwise, you should specify a disease", order = 8)
		public int type = 0;
		@Parameter(names = { "--disease", "-dis" }, description = "Disease to test with (1-4: Synthetic diseases; " 
			+ TEST_PBD + ": PBD; "
			+ TEST_SCD + ": SCD; "
			+ TEST_T1DM_DCCT1 + ": T1DM (DCCT Primary cohort)"
			+ TEST_T1DM_DCCT2 + ": T1DM (DCCT Secondary cohort)"
			+ TEST_T1DM_DCCT1_CONV + ": T1DM (DCCT Primary cohort conventional therapy)"
			+ TEST_T1DM_DCCT1_INTENS + ": T1DM (DCCT Primary cohort intensive therapy)"
			+ ")", order = 3)
		public int disease = 1;
		@Parameter(names = { "--model", "-mod" }, description = "The name of the OSDi model to test", order = 3)
		public String model = "";
		@Parameter(names = { "--prefix", "-pre" }, description = "The prefix used for all instances of the OSDi model to test", order = 3)
		public String prefix = "";
	}

	@Override
	public HTAModel createModel(CommonArguments arguments) throws MalformedSimulationModelException {
		HTAModel model = null;
		if (((Arguments)arguments).type == 1) {
			// TODO: Add arguments validation for OSDi
			try {
				model = new OSDiGenericModel(this, System.getProperty("user.dir") + "\\resources\\OSDi.owl", ((Arguments)arguments).model, ((Arguments)arguments).prefix);
				// This repository ignores potential study years passed as arguments
			} catch (OWLOntologyCreationException | MalformedSimulationModelException e) {
				MalformedSimulationModelException ex = new MalformedSimulationModelException("");
				ex.initCause(e);
				throw ex;
			}
		}
		else {
			final int disease = ((Arguments)arguments).disease;
			switch(disease) {
			case TEST_SCD:
				System.out.println("No programmatic test available for SCD\n\n");
				break;
			case TEST_RARE_DISEASE1:
			case TEST_RARE_DISEASE2:
			case TEST_RARE_DISEASE3:
			case TEST_RARE_DISEASE4:
				System.out.println(String.format("\n\nExecuting the PROGRAMMATIC test for the rare disease [%d] \n\n", disease));
				model = new TestSimpleRareDiseaseModel(this, disease);
				break;
			case TEST_T1DM_DCCT1:
				System.out.println(String.format("\n\nExecuting the PROGRAMMATIC test for T1DM (DCCT primary cohort) \n\n"));
				model = new T1DMModel(this, T1DMModel.ModelConfig.DCCT1);
				break;
			case TEST_T1DM_DCCT2:
				System.out.println(String.format("\n\nExecuting the PROGRAMMATIC test for T1DM (DCCT secondary cohort) \n\n"));
				model = new T1DMModel(this, T1DMModel.ModelConfig.DCCT2);
				break;
			case TEST_T1DM_DCCT1_CONV:
				System.out.println(String.format("\n\nExecuting the PROGRAMMATIC test for T1DM (DCCT primary cohort, conventional therapy) \n\n"));
				model = new T1DMModel(this, T1DMModel.ModelConfig.DCCT1_CONV);
				break;
			case TEST_T1DM_DCCT1_INTENS:
				System.out.println(String.format("\n\nExecuting the PROGRAMMATIC test for T1DM (DCCT primary cohort, intensive therapy) \n\n"));
				model = new T1DMModel(this, T1DMModel.ModelConfig.DCCT1_INTENS);
				break;
			case TEST_PBD:
			default:
				System.out.println(String.format("\n\nExecuting the PROGRAMMATIC test for the rare disease PBD \n\n"));
				model = new PBDModel(this, ALL_AFFECTED);
				setDisutilityCombinationMethod(DisutilityCombinationMethod.MAX);

				break;				
			}			
		}
		return model;
	}

	public static void main(String[] args) {
		final Arguments arguments = new Arguments();
		try {
			// ##############################################################################################################
			// Parameters definition
			// ##############################################################################################################

			final JCommander jc = JCommander.newBuilder().addObject(arguments).build();
			jc.parse((PARAMS == "") ? args : PARAMS.split(" "));

			// ##############################################################################################################

			final ByteArrayOutputStream baos = new ByteArrayOutputStream ();
			final DiseaseMain experiment = new DiseaseMain(arguments, baos);
			System.out.println("=====================================================================================================");
			System.out.println(es.ull.iis.simulation.hta.params.Parameter.prettyPrintAll(""));
			System.out.println();
			experiment.run();

			if (REPLACE_DOT_WITH_COLON) {
				System.out.println((new String (baos.toByteArray())).replace(".", ","));
			} else {
				System.out.println((new String (baos.toByteArray())));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
