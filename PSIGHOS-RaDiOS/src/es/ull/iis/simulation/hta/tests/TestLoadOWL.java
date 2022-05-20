/**
 * 
 */
package es.ull.iis.simulation.hta.tests;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.w3c.xsd.owl2.Ontology;

import es.ull.iis.simulation.hta.osdi.DiseaseBuilder;
import es.ull.iis.simulation.hta.osdi.OwlHelper;
import es.ull.iis.simulation.hta.osdi.PopulationBuilder;
import es.ull.iis.simulation.hta.osdi.utils.OntologyUtils;
import es.ull.iis.simulation.hta.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.outcomes.DiseaseCostCalculator;
import es.ull.iis.simulation.hta.outcomes.DiseaseUtilityCalculator;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.EmpiricalSpainDeathSubmodel;

/**
 * @author Iván Castilla
 *
 */
public class TestLoadOWL {
	private final static double GENERAL_POPULATION_UTILITY = 0.8861;

	public static class TestOWLRepository extends SecondOrderParamsRepository {
		final CostCalculator costCalc;
		final UtilityCalculator utilCalc;
		
		public TestOWLRepository(Ontology ontology, int nRuns, int nPatients) {
			super(nRuns, nPatients);
			costCalc = new DiseaseCostCalculator(this);
			utilCalc = new DiseaseUtilityCalculator(this, DisutilityCombinationMethod.ADD, GENERAL_POPULATION_UTILITY);
			Disease disease = DiseaseBuilder.getDiseaseInstance(ontology, this, "#PBD_ProfoundBiotinidaseDeficiency");
			System.out.println(disease.prettyPrint(""));
			setPopulation(PopulationBuilder.getPopulationInstance(ontology, this, disease, "#PBD_BasePopulation"));
			setDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
		}

		@Override
		public CostCalculator getCostCalculator() {
			return costCalc; 
		}

		@Override
		public UtilityCalculator getUtilityCalculator() {
			return utilCalc;
		}
		
	}
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
			final SecondOrderParamsRepository secParams = new TestOWLRepository(testOntology, 1, 1000);
			secParams.registerAllSecondOrderParams();
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
