/**
 * 
 */
package es.ull.iis.simulation.hta;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import es.ull.iis.simulation.hta.diab.T1DMRepository;
import es.ull.iis.simulation.hta.osdi.OSDiGenericRepository;
import es.ull.iis.simulation.hta.outcomes.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
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
	private final static int TEST_PBD = 11;
	private final static int TEST_T1DM = 12;
	
	private final static int TEST_SCD = 10;
	private final static boolean REPLACE_DOT_WITH_COLON = false;
	private final static boolean ALL_AFFECTED = true;
//	private final static String PARAMS = "-n 10000 -r 0 -t 1 -dis 11 -y 2019 -dr 0 -q -ep ia"; // Testing OSDi PBD
	private final static String PARAMS = "-n 1000 -r 0 -t 0 -dis 12 -y 2019 -q -ep cr -po"; // Testing diabetes
//	private final static String PARAMS = "-n 10 -r 0 -t 1 -dis 12 -y 2019 -q -dr 0 -ep ca -po -ps 13"; // Testing OSDi diabetes
//	private final static String PARAMS = "-n 5000 -r 0 -t 0 -dis 4 -dr 0 -ep ia -q"; // Testing test diseases
//	private final static String PARAMS = "-n 100 -r 0 -dr 0 -q -t 0 -dis 1 -ps 3 -po"; // -o /tmp/result_david.txt
//	private final static String PARAMS = "-n 1000 -r 0 -dr 0 -q -t 1 -dis 1 -ps 3 -po"; // -o /tmp/result_david.txt

	private final TreeMap<String, Double> initProportions;
	
	public DiseaseMain(Arguments arguments, ByteArrayOutputStream simResult) throws MalformedSimulationModelException {
		super(arguments, simResult);
		initProportions = new TreeMap<>();
	}
	
	public static class Arguments extends CommonArguments {
		@Parameter(names = { "--type", "-t" }, description = "Selects an alternative scenario (0: Programmatic; 1: OSDi). If OSDi is selected, a model name must be specified with the -m option; otherwise, you should specify a disease", order = 8)
		public int type = 0;
		@Parameter(names = { "--disease", "-dis" }, description = "Disease to test with (1-4: Synthetic diseases; 10:SCD; 11: PBD; 12: T1DM)", order = 3)
		public int disease = 1;
		@Parameter(names = { "--model", "-mod" }, description = "The name of the OSDi model to test", order = 3)
		public String model = "";
		@Parameter(names = { "--prefix", "-pre" }, description = "The prefix used for all instances of the OSDi model to test", order = 3)
		public String prefix = "";
		@DynamicParameter(names = { "--iniprop", "-I" }, description = "Initial proportion for complication stages")
		public Map<String, String> initProportions = new TreeMap<String, String>();
	}

	@Override
	public HTAModel createModel(CommonArguments arguments) throws MalformedSimulationModelException {
		HTAModel model = null;
		final List<String> interventionsToCompare = new ArrayList<>();
		final int nRuns = ((Arguments)arguments).nRuns;
		final int nPatients = ((Arguments)arguments).nPatients;
		// FIXME: Currently, do not doing anything with this initial proportions
		for (final Map.Entry<String, String> pInit : ((Arguments)arguments).initProportions.entrySet()) {
			initProportions.put(pInit.getKey(), Double.parseDouble(pInit.getValue()));
		}
		if (((Arguments)arguments).type == 1) {
//			String path = "";
//			switch(disease) {
//			case TEST_SCD:
//				System.out.println("No OSDi test available for SCD\n\n");
////				System.out.println(String.format("\n\nExecuting the RaDiOS test for the rare disease SCD \n\n"));
////				path = "resources/radios_SCD.json";
//				break;
//			case TEST_RARE_DISEASE1:
//			case TEST_RARE_DISEASE2:
//			case TEST_RARE_DISEASE3:
//			case TEST_RARE_DISEASE4:
//				System.out.println("No OSDi test available for test diseases\n\n");
////				System.out.println(String.format("\n\nExecuting the RaDiOS test for the rare disease [%d] \n\n", disease));
////				interventionsToCompare.add(Constants.CONSTANT_DO_NOTHING);
////				interventionsToCompare.add("#RD1_Intervention_Effective");
////				path = "resources/radios-test_disease" + disease + ".json";
//				break;
//			case TEST_T1DM:
//				System.out.println(String.format("\n\nExecuting the OSDi test for the T1DM \n\n"));
//				interventionsToCompare.add(InterventionBuilder.DO_NOTHING);
//				interventionsToCompare.add("#T1DM_InterventionDCCTIntensive");
//				strDisease = "#T1DM_Disease";
//				strPopulation = "#T1DM_PopulationDCCT1";
//				path = System.getProperty("user.dir") + "\\resources\\OSDi.owl";
//				break;
//			case TEST_PBD:
//			default:
//				System.out.println(String.format("\n\nExecuting the OSDi test for the rare disease PBD \n\n"));
//				interventionsToCompare.add("#PBD_InterventionNoScreening");
//				interventionsToCompare.add("#PBD_InterventionScreening");
//				strDisease = "#PBD_ProfoundBiotinidaseDeficiency";
//				strPopulation = "#PBD_FakePopulation";
//				path = System.getProperty("user.dir") + "\\resources\\OSDi.owl";
//				break;				
//			}
			// TODO: Add arguments validation for OSDi
			try {
				model = new OSDiGenericRepository(nRuns, nPatients, System.getProperty("user.dir") + "\\resources\\OSDi.owl", ((Arguments)arguments).model, ((Arguments)arguments).prefix);
				// This repository ignores potential study years passed as arguments
			} catch (OWLOntologyCreationException | MalformedSimulationModelException e) {
				MalformedSimulationModelException ex = new MalformedSimulationModelException("");
				ex.initCause(e);
				throw ex;
			}
		}
		else {
			final int disease = ((Arguments)arguments).disease;
			HTAModel.setStudyYear(arguments.year);
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
			case TEST_T1DM:
				System.out.println(String.format("\n\nExecuting the PROGRAMMATIC test for T1DM \n\n"));
				model = new T1DMRepository(nRuns, nPatients);
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
