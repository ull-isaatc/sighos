/**
 * 
 */
package es.ull.iis.simulation.hta;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import es.ull.iis.ontology.radios.Constants;
import es.ull.iis.simulation.hta.diab.T1DMRepository;
import es.ull.iis.simulation.hta.osdi.OSDiGenericRepository;
import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.pbdmodel.PBDRepository;
import es.ull.iis.simulation.hta.radios.RadiosExperimentResult;
import es.ull.iis.simulation.hta.radios.RadiosRepository;
import es.ull.iis.simulation.hta.radios.exceptions.TransformException;
import es.ull.iis.simulation.hta.simpletest.TestSimpleRareDiseaseRepository;
import javax.xml.bind.JAXBException;

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
	private final static String PARAMS = "-n 1000 -r 0 -t 2 -y 2019 -q -ep cr"; // Testing OSDi PBD
//	private final static String PARAMS = "-n 20000 -r 0 -t 0 -dis 12 -y 2019 -q -ep cr"; // Testing diabetes
//	private final static String PARAMS = "-n 5000 -r 0 -t 0 -dis 4 -dr 0 -ep ia -q"; // Testing test diseases
//	private final static String PARAMS = "-n 100 -r 0 -dr 0 -q -t 0 -dis 1 -ps 3 -po"; // -o /tmp/result_david.txt
//	private final static String PARAMS = "-n 1000 -r 0 -dr 0 -q -t 1 -dis 1 -ps 3 -po"; // -o /tmp/result_david.txt

	public DiseaseMain(Arguments arguments, ByteArrayOutputStream simResult) throws MalformedSimulationModelException {
		super(arguments, simResult);
	}
	
	public static class Arguments extends CommonArguments {
		/*
		 * -n 100 -r 5 -dr 0 -q -pop 1 -ps 3 -po -dis 1: 100 pacientes, 5 ejecuciones probabilisticas, sin descuento, sin mostrar el progreso de ejecución, para RaDiOS, con el progreso para el tercer
		 * paciente, habilitada la salida individual por paciente y para la enfermedad test1
		 */
		@Parameter(names = { "--type", "-t" }, description = "Selects an alternative scenario (0: Programmatic; 1: RaDiOS; 2: OSDi)", order = 8)
		public int type = 0;
		@Parameter(names = { "--disease", "-dis" }, description = "Disease to test with (1-4: Synthetic diseases; 10:SCD; 11: PBD; 12: T1DM)", order = 3)
		public int disease = 1;
		@DynamicParameter(names = { "--iniprop", "-I" }, description = "Initial proportion for complication stages")
		public Map<String, String> initProportions = new TreeMap<String, String>();
	}

	@Override
	public SecondOrderParamsRepository createRepository(CommonArguments arguments) throws MalformedSimulationModelException {
		SecondOrderParamsRepository secParams = null;
		List<String> interventionsToCompare = new ArrayList<>();
		final int disease = ((Arguments)arguments).disease;
		final int nRuns = ((Arguments)arguments).nRuns;
		final int nPatients = ((Arguments)arguments).nPatients;
		final int timeHorizon = ((Arguments)arguments).timeHorizon;
		switch (((Arguments)arguments).type) {			
		case 1:
			String path = "";
			switch(disease) {
			case TEST_SCD:
				System.out.println(String.format("\n\nExecuting the RaDiOS test for the rare disease SCD \n\n"));
				path = "resources/radios_SCD.json";
				break;
			case TEST_RARE_DISEASE1:
			case TEST_RARE_DISEASE2:
			case TEST_RARE_DISEASE3:
			case TEST_RARE_DISEASE4:
				System.out.println(String.format("\n\nExecuting the RaDiOS test for the rare disease [%d] \n\n", disease));
				interventionsToCompare.add(Constants.CONSTANT_DO_NOTHING);
				interventionsToCompare.add("#RD1_Intervention_Effective");
				path = "resources/radios-test_disease" + disease + ".json";
				break;
			case TEST_T1DM:
				System.out.println("No RaDiOS test available for T1DM\n\n");
				break;
			case TEST_PBD:
			default:
				System.out.println(String.format("\n\nExecuting the RaDiOS test for the rare disease PBD \n\n"));
				interventionsToCompare.add(Constants.CONSTANT_DO_NOTHING);
				interventionsToCompare.add("#PBD_InterventionScreening");
				path = "resources/radios_PBD.json";
				break;				
			}
			try {
				if (!path.equals(""))
					secParams = new RadiosRepository(nRuns, nPatients, path, timeHorizon, ALL_AFFECTED, interventionsToCompare);
			} catch (IOException | TransformException | JAXBException e) {
				MalformedSimulationModelException ex = new MalformedSimulationModelException("");
				ex.initCause(e);
				throw ex;
			}
			break;
		case 2:
			try {
				secParams = new OSDiGenericRepository(nRuns, nPatients, System.getProperty("user.dir") + "\\resources\\OSDi.owl", "#PBD_ProfoundBiotinidaseDeficiency", "#PBD_FakePopulation", DisutilityCombinationMethod.ADD);
			} catch (IOException | TranspilerException | JAXBException e) {
				e.printStackTrace();
			}
        	break;
		case 0:
		default:
			switch(disease) {
			case TEST_SCD:
				System.out.println("No programmatic test available for SCD\n\n");
				break;
			case TEST_RARE_DISEASE1:
			case TEST_RARE_DISEASE2:
			case TEST_RARE_DISEASE3:
			case TEST_RARE_DISEASE4:
				System.out.println(String.format("\n\nExecuting the PROGRAMMATIC test for the rare disease [%d] \n\n", disease));
				secParams = new TestSimpleRareDiseaseRepository(nRuns, nPatients, disease);
				break;
			case TEST_T1DM:
				System.out.println(String.format("\n\nExecuting the PROGRAMMATIC test for T1DM \n\n"));
				secParams = new T1DMRepository(nRuns, nPatients);
				break;
			case TEST_PBD:
			default:
				System.out.println(String.format("\n\nExecuting the PROGRAMMATIC test for the rare disease PBD \n\n"));
				secParams = new PBDRepository(nRuns, nPatients, ALL_AFFECTED);
				break;				
			}			
			break;
		}

		return secParams;
	}

	public static void main(String[] args) {
		final Arguments arguments = new Arguments();
		try {
			// ##############################################################################################################
			// Parameters definition
			// ##############################################################################################################

			final JCommander jc = JCommander.newBuilder().addObject(arguments).build();
			jc.parse((PARAMS == "") ? args : PARAMS.split(" "));

			for (final Map.Entry<String, String> pInit : arguments.initProportions.entrySet()) {
				BasicConfigParams.INIT_PROP.put(pInit.getKey(), Double.parseDouble(pInit.getValue()));
			}
			// ##############################################################################################################

			final ByteArrayOutputStream baos = new ByteArrayOutputStream ();
			final DiseaseMain experiment = new DiseaseMain(arguments, baos);
			experiment.run();
			RadiosExperimentResult result = new RadiosExperimentResult(baos, experiment.getRepository().prettyPrint(""), arguments.nRuns);

			System.out.println("=====================================================================================================");
			System.out.println(result.getPrettySavedParams());
			System.out.println();
			if (REPLACE_DOT_WITH_COLON) {
				System.out.println((new String (result.getSimResult().toByteArray())).replace(".", ","));
			} else {
				System.out.println((new String (result.getSimResult().toByteArray())));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
