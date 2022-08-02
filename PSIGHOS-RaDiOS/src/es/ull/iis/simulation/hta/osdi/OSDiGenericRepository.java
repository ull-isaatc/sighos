/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.w3c.xsd.owl2.Ontology;

import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.utils.OwlHelper;
import es.ull.iis.simulation.hta.outcomes.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.EmpiricalSpainDeathSubmodel;

/**
 * @author masbe
 *
 */
public class OSDiGenericRepository extends SecondOrderParamsRepository {

	/**
	 * 
	 * @param nRuns
	 * @param nPatients
	 * @param path
	 * @param diseaseId
	 * @param populationId
	 * @param method
	 * @throws FileNotFoundException
	 * @throws JAXBException
	 * @throws IOException
	 * @throws TranspilerException 
	 */
	public OSDiGenericRepository(int nRuns, int nPatients, String path, String diseaseId, String populationId, DisutilityCombinationMethod method) throws FileNotFoundException, JAXBException, IOException, TranspilerException {
		super(nRuns, nPatients);
		Ontology testOntology = OwlHelper.loadOntology(path);
		OwlHelper.initilize(testOntology);
		setDisutilityCombinationMethod(method);

		Disease disease = DiseaseBuilder.getDiseaseInstance(this, diseaseId);
		setPopulation(PopulationBuilder.getPopulationInstance(this, disease, populationId));
		
		// TODO: Death submodel should be context specific, depending on the population
		setDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
		
		// Build interventions
		List<String> interventions = OSDiNames.Class.INTERVENTION.getDescendantsOf(disease.name());
		for (String interventionName : interventions) {
			InterventionBuilder.getInterventionInstance(this, interventionName);
		}
		
	}

}
