/**
 * 
 */
package es.ull.iis.simulation.hta.tests;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.osdi.InterventionBuilder;
import es.ull.iis.simulation.hta.osdi.OSDiGenericModel;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;

/**
 * @author Iván Castilla
 *
 */
public class TestLoadOWL {

	/**
	 * 
	 */
	public TestLoadOWL() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			final List<String> interventionsToCompare = new ArrayList<>();
			interventionsToCompare.add(InterventionBuilder.DO_NOTHING);

//			final SecondOrderParamsRepository secParams = new OSDiGenericRepository(1, 1000, System.getProperty("user.dir") + "\\resources\\OSDi.owl", "#PBD_ProfoundBiotinidaseDeficiency", "#PBD_BasePopulation", DisutilityCombinationMethod.ADD);
			final SecondOrderParamsRepository secParams = new OSDiGenericModel(1, 1000, System.getProperty("user.dir") + "\\resources\\OSDi.owl", "StdModel", "T1DM_");
			secParams.registerAllSecondOrderParams();
			for (Disease disease : secParams.getRegisteredDiseases()) {
				System.out.println(disease.prettyPrint(""));
			}
			for (Intervention interv : secParams.getRegisteredInterventions()) {
				System.out.println(interv.prettyPrint(""));
			}
			System.out.println(secParams.prettyPrint(""));
		} catch (MalformedSimulationModelException e) {
			e.printStackTrace();
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
	}

}
