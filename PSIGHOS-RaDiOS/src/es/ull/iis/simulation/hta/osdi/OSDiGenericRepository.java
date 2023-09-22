/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper;
import es.ull.iis.simulation.hta.outcomes.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.EmpiricalSpainDeathSubmodel;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class OSDiGenericRepository extends SecondOrderParamsRepository {
	public static final JexlEngine JEXL = new JexlBuilder().create();
	private final OSDiWrapper wrap; 
	private final int year;

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
	public OSDiGenericRepository(int nRuns, int nPatients, String path, String modelId, String instancePrefix) throws FileNotFoundException, JAXBException, IOException, TranspilerException, MalformedSimulationModelException, OWLOntologyCreationException, MalformedOSDiModelException {
		super(nRuns, nPatients);
		wrap = new OSDiWrapper(path, modelId, instancePrefix);
		
		year = wrap.parseHasYearProperty(modelId);
		
		final ArrayList<String> methods = OSDiWrapper.DataProperty.HAS_DISUTILITY_COMBINATION_METHOD.getValues(wrap, modelId);
		// Assuming that exactly one method was defined
		if (methods.size() != 1)
			throw new MalformedSimulationModelException("Exactly one disutility combination method must be specified for the model. Instead, " + methods.size() + " defined.");
		try {
			setDisutilityCombinationMethod(DisutilityCombinationMethod.valueOf(methods.get(0)));
		}
		catch(IllegalArgumentException ex) {
			throw new MalformedSimulationModelException("Disutility combination method not valid. \"" + methods.get(0) + "\" not found.");			
		}

		// Find the diseases that belong to the model
		final Set<String> diseaseName = wrap.getIndividuals(OSDiWrapper.Clazz.DISEASE.getShortName(), true); 
		if (diseaseName.size() == 0)
			throw new MalformedSimulationModelException("The model does not include any disease.");
		else if (diseaseName.size() > 1)
			wrap.printWarning("Found " + diseaseName.size() + " diseases included in the model. Only " + diseaseName.toArray()[0] + " will be used");
		final Disease disease = DiseaseBuilder.getDiseaseInstance(this, (String)diseaseName.toArray()[0]);
		// TODO: Adapt the rest to use the wrapper

//		setPopulation(PopulationBuilder.getPopulationInstance(this, disease, populationId));
		
		// TODO: Death submodel should be context specific, depending on the population
		setDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
		
		// Build interventions
//		for (String interventionName : interventionsToCompare) {
//			InterventionBuilder.getInterventionInstance(this, interventionName);
//		}
		
	}

	public OSDiWrapper getOwlWrapper() {
		return wrap;
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
			final SecondOrderParamsRepository secParams = new OSDiGenericRepository(1, 1000, System.getProperty("user.dir") + "\\resources\\OSDi.owl", "StdModel", "T1DM_");
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
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		} catch (MalformedOSDiModelException e) {
			e.printStackTrace();
		}
	}


}
