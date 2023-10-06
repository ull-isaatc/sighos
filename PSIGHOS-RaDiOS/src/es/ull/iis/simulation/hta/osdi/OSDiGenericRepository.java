/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.osdi.builders.DiseaseBuilder;
import es.ull.iis.simulation.hta.osdi.builders.PopulationBuilder;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
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
	/**
	 * 
	 * @param nRuns
	 * @param nPatients
	 * @param path
	 * @param diseaseId
	 * @param populationId
	 * @param method
	 * @throws FileNotFoundException
	 * @throws OWLOntologyCreationException
	 * @throws MalformedSimulationModelException 
	 * TODO: Convert all the bernouilli parameters into BernouilliParam (e.g. Population parameters such as sex)
	 * TODO: Parse and create ParamWrappers in constructors (to be able to throw MalformedOSDiModelException when creating the model and avoid them during the creation of parameters in the repository).
	 */
	public OSDiGenericRepository(int nRuns, int nPatients, String path, String modelId, String instancePrefix) throws OWLOntologyCreationException, MalformedSimulationModelException {
		super(nRuns, nPatients);
		wrap = new OSDiWrapper(path, modelId, instancePrefix);
		setStudyYear(wrap.parseHasYearProperty(wrap.getWorkingModelInstance()));
		
		final ArrayList<String> methods = OSDiWrapper.DataProperty.HAS_DISUTILITY_COMBINATION_METHOD.getValues(wrap.getWorkingModelInstance());
		// Assuming that exactly one method was defined
		if (methods.size() != 1)
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.MODEL, wrap.getWorkingModelInstance(), OSDiWrapper.DataProperty.HAS_DISUTILITY_COMBINATION_METHOD, "Exactly one disutility combination method must be specified for the model. Instead, " + methods.size() + " defined.");
		try {
			setDisutilityCombinationMethod(DisutilityCombinationMethod.valueOf(methods.get(0)));
		}
		catch(IllegalArgumentException ex) {
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.MODEL, wrap.getWorkingModelInstance(), OSDiWrapper.DataProperty.HAS_DISUTILITY_COMBINATION_METHOD, "Disutility combination method not valid. \"" + methods.get(0) + "\" not found.");			
		}

		// Find the diseases that belong to the model
		final Set<String> diseaseNames = OSDiWrapper.Clazz.DISEASE.getIndividuals(true); 
		if (diseaseNames.size() == 0)
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.MODEL, wrap.getWorkingModelInstance(), OSDiWrapper.ObjectProperty.INCLUDES_MODEL_ITEM, "The model does not include any disease.");
		final String diseaseName = (String)diseaseNames.toArray()[0];
		if (diseaseNames.size() > 1)
			wrap.printWarning("Found " + diseaseNames.size() + " diseases included in the model. Only " + diseaseName + " will be used");
		
		// Find the populations that belong to the model
		final Set<String> populationNames = OSDiWrapper.Clazz.POPULATION.getIndividuals(true); 
		if (populationNames.size() == 0)
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.MODEL, wrap.getWorkingModelInstance(), OSDiWrapper.ObjectProperty.INCLUDES_MODEL_ITEM, "The model does not include any population.");
		final String populationName = (String)populationNames.toArray()[0];
		if (populationNames.size() > 1)
			wrap.printWarning("Found " + populationNames.size() + " populations included in the model. Only " + populationName + " will be used");
		
		final Disease disease = DiseaseBuilder.getDiseaseInstance(this, diseaseName, populationName);
		setPopulation(PopulationBuilder.getPopulationInstance(this, populationName, disease));
		
		// TODO: Adapt the rest to use the wrapper

		
		// TODO: Death submodel should be context specific, depending on the population
		setDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
		// Build interventions that belong to the model
//		final Set<String> interventionNames = OSDiWrapper.Clazz.INTERVENTION.getIndividuals(true); 
//		if (interventionNames.size() == 0)
//			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.MODEL, wrap.getWorkingModelInstance(), OSDiWrapper.ObjectProperty.INCLUDES_MODEL_ITEM, "The model does not include any intervention.");
//		for (String interventionName : interventionNames) {
//			InterventionBuilder.getInterventionInstance(this, interventionName);
//		}
		
	}

	public OSDiWrapper getOwlWrapper() {
		return wrap;
	}
	
	@Override
	public void registerAllSecondOrderParams() {
		super.registerAllSecondOrderParams();
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
			final SecondOrderParamsRepository secParams = new OSDiGenericRepository(1, 1000, System.getProperty("user.dir") + "\\resources\\OSDi_test.owl", "StdModelDES", "T1DM_");
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
