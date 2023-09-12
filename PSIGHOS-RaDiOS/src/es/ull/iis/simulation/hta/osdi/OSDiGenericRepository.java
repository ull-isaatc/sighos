/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.outcomes.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.EmpiricalSpainDeathSubmodel;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class OSDiGenericRepository extends SecondOrderParamsRepository {
	public static final JexlEngine JEXL = new JexlBuilder().create();
	private final OwlHelper helper; 
	private final OSDiWrapper wrap; 

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
	public OSDiGenericRepository(int nRuns, int nPatients, String path, String diseaseId, String populationId, List<String> interventionsToCompare, DisutilityCombinationMethod method) throws FileNotFoundException, JAXBException, IOException, TranspilerException, MalformedSimulationModelException {
		super(nRuns, nPatients);
		helper = new OwlHelper(path);
		wrap = null;
		setDisutilityCombinationMethod(method);

		Disease disease = DiseaseBuilder.getDiseaseInstance(this, diseaseId);
		setPopulation(PopulationBuilder.getPopulationInstance(this, disease, populationId));
		
		// TODO: Death submodel should be context specific, depending on the population
		setDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
		
		// Build interventions
		for (String interventionName : interventionsToCompare) {
			InterventionBuilder.getInterventionInstance(this, interventionName);
		}
		
	}

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
	 * @throws OWLOntologyCreationException 
	 */
	public OSDiGenericRepository(int nRuns, int nPatients, String path, String modelId) throws FileNotFoundException, JAXBException, IOException, TranspilerException, MalformedSimulationModelException, OWLOntologyCreationException {
		super(nRuns, nPatients);
		wrap = new OSDiWrapper(path);
		helper = null;
		ArrayList<String> methods = OSDiWrapper.DataProperty.HAS_DISUTILITY_COMBINATION_METHOD.getValues(wrap, modelId);
		// Assuming that exactly one method was defined
		if (methods.size() != 1)
			throw new MalformedSimulationModelException("Exactly one disutility combination method must be specified for the model. Instead, " + methods.size() + " defined.");
		try {
			setDisutilityCombinationMethod(DisutilityCombinationMethod.valueOf(methods.get(0)));
		}
		catch(IllegalArgumentException ex) {
			throw new MalformedSimulationModelException("Disutility combination method not valid. \"" + methods.get(0) + "\" not found.");			
		}
		
		final ArrayList<String> modelItems = OSDiWrapper.ObjectProperty.INCLUDES_MODEL_ITEM.getValues(wrap, modelId);

		// TODO: Adapt the rest to use the wrapper

//		Disease disease = DiseaseBuilder.getDiseaseInstance(this, diseaseId);
//		setPopulation(PopulationBuilder.getPopulationInstance(this, disease, populationId));
		
		// TODO: Death submodel should be context specific, depending on the population
		setDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
		
		// Build interventions
//		for (String interventionName : interventionsToCompare) {
//			InterventionBuilder.getInterventionInstance(this, interventionName);
//		}
		
	}

	public OwlHelper getOwlHelper() {
		return helper;
	}

	/**
	 * For testing (currently not working in the test package for unknown reasons)
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			final List<String> interventionsToCompare = new ArrayList<>();
			interventionsToCompare.add(InterventionBuilder.DO_NOTHING);

//			final SecondOrderParamsRepository secParams = new OSDiGenericRepository(1, 1000, System.getProperty("user.dir") + "\\resources\\OSDi.owl", "#PBD_ProfoundBiotinidaseDeficiency", "#PBD_BasePopulation", DisutilityCombinationMethod.ADD);
			final SecondOrderParamsRepository secParams = new OSDiGenericRepository(1, 1000, System.getProperty("user.dir") + "\\resources\\OSDi.owl", "#T1DM_Disease", "#T1DM_DCCTPopulation1", interventionsToCompare, DisutilityCombinationMethod.ADD);
			secParams.registerAllSecondOrderParams();
			for (Disease disease : secParams.getRegisteredDiseases()) {
				System.out.println(disease.prettyPrint(""));
			}
			for (Intervention interv : secParams.getRegisteredInterventions()) {
				System.out.println(interv.prettyPrint(""));
			}
			System.out.println(secParams.prettyPrint(""));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TranspilerException e) {
			e.printStackTrace();
		} catch (MalformedSimulationModelException e) {
			e.printStackTrace();
		}
	}


}
