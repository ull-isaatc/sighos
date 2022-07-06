/**
 * 
 */
package es.ull.iis.simulation.hta.tests;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.w3c.xsd.owl2.Ontology;

import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.osdi.OSDiGenericRepository;
import es.ull.iis.simulation.hta.osdi.OwlHelper;
import es.ull.iis.simulation.hta.osdi.utils.OntologyUtils;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;

/**
 * @author Iván Castilla
 *
 */
public class TestLoadOWL {
	private final static double GENERAL_POPULATION_UTILITY = 0.8861;

	/**
	 * 
	 */
	public TestLoadOWL() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Ontology testOntology = OntologyUtils.loadOntology(System.getProperty("user.dir") + "\\resources\\OSDi.owl");
			OwlHelper.initilize(testOntology);
			final SecondOrderParamsRepository secParams = new OSDiGenericRepository(1, 1000, "#PBD_ProfoundBiotinidaseDeficiency", "#PBD_BasePopulation", DisutilityCombinationMethod.ADD, GENERAL_POPULATION_UTILITY);
			secParams.registerAllSecondOrderParams();
			for (Disease disease : secParams.getRegisteredDiseases()) {
				System.out.println(disease.prettyPrint(""));
			}
			for (Intervention interv : secParams.getRegisteredInterventions()) {
				System.out.println(interv.prettyPrint(""));
			}
			System.out.println(secParams.prettySavedParams());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
