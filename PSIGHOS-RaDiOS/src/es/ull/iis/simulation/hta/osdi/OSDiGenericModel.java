/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.ull.iis.simulation.hta.DiseaseMain.Arguments;
import es.ull.iis.simulation.hta.CommonArguments;
import es.ull.iis.simulation.hta.HTAExperiment;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.osdi.builders.DiseaseBuilder;
import es.ull.iis.simulation.hta.osdi.builders.PopulationBuilder;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiDataProperties;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiClasses;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiObjectProperties;
import es.ull.iis.simulation.hta.outcomes.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.Parameter;
import es.ull.iis.simulation.hta.progression.Disease;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class OSDiGenericModel extends HTAModel {
	public static final JexlEngine JEXL = new JexlBuilder().create();
	private final OSDiWrapper wrap; 
	private final HashMap<String, String> ontologyParameterMapping;
	
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
	public OSDiGenericModel(HTAExperiment experiment, String path, String modelId, String instancePrefix) throws OWLOntologyCreationException, MalformedSimulationModelException {
		super(experiment);
		ontologyParameterMapping = new HashMap<>();
		wrap = new OSDiWrapper(path, modelId, instancePrefix);
		setStudyYear(wrap.parseHasYearProperty(wrap.getWorkingModelInstance()));
		
		final ArrayList<String> methods = OSDiDataProperties.HAS_DISUTILITY_COMBINATION_METHOD.getValues(wrap.getWorkingModelInstance());
		// Assuming that exactly one method was defined
		if (methods.size() != 1)
			throw new MalformedOSDiModelException(OSDiClasses.MODEL, wrap.getWorkingModelInstance(), OSDiDataProperties.HAS_DISUTILITY_COMBINATION_METHOD, "Exactly one disutility combination method must be specified for the model. Instead, " + methods.size() + " defined.");
		try {
			experiment.setDisutilityCombinationMethod(DisutilityCombinationMethod.valueOf(methods.get(0)));
		}
		catch(IllegalArgumentException ex) {
			throw new MalformedOSDiModelException(OSDiClasses.MODEL, wrap.getWorkingModelInstance(), OSDiDataProperties.HAS_DISUTILITY_COMBINATION_METHOD, "Disutility combination method not valid. \"" + methods.get(0) + "\" not found.");			
		}

		// Find the diseases that belong to the model
		final Set<String> diseaseNames = OSDiClasses.DISEASE.getIndividuals(true); 
		if (diseaseNames.size() == 0)
			throw new MalformedOSDiModelException(OSDiClasses.MODEL, wrap.getWorkingModelInstance(), OSDiObjectProperties.INCLUDES_MODEL_ITEM, "The model does not include any disease.");
		final String diseaseName = (String)diseaseNames.toArray()[0];
		if (diseaseNames.size() > 1)
			wrap.printWarning("Found " + diseaseNames.size() + " diseases included in the model. Only " + diseaseName + " will be used");
		
		// Find the populations that belong to the model
		final Set<String> populationNames = OSDiClasses.POPULATION.getIndividuals(true); 
		if (populationNames.size() == 0)
			throw new MalformedOSDiModelException(OSDiClasses.MODEL, wrap.getWorkingModelInstance(), OSDiObjectProperties.INCLUDES_MODEL_ITEM, "The model does not include any population.");
		final String populationName = (String)populationNames.toArray()[0];
		if (populationNames.size() > 1)
			wrap.printWarning("Found " + populationNames.size() + " populations included in the model. Only " + populationName + " will be used");
		
		final Disease disease = DiseaseBuilder.getDiseaseInstance(this, diseaseName, populationName);
		PopulationBuilder.getPopulationInstance(this, populationName, disease);
		
		// TODO: Adapt the rest to use the wrapper

		
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
	
	/**
	 * For testing (currently not working in the test package for unknown reasons)
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			final List<String> interventionsToCompare = new ArrayList<>();
			interventionsToCompare.add(InterventionBuilder.DO_NOTHING);

			final HTAExperiment exp = new HTAExperiment(new Arguments(), new ByteArrayOutputStream ()) {
				@Override
				public HTAModel createModel(CommonArguments arguments) throws MalformedSimulationModelException {
					HTAModel model = null;
					try {
						model = new OSDiGenericModel(this, System.getProperty("user.dir") + "\\resources\\OSDi_test.owl", "StdModelDES", "T1DM_");
					} catch (OWLOntologyCreationException e) {
						e.printStackTrace();
					}
					return model;
				}
			};
			exp.run();
			for (Disease disease : exp.getModel().getRegisteredDiseases()) {
				System.out.println(disease.prettyPrint(""));
			}
			for (Intervention interv : exp.getModel().getRegisteredInterventions()) {
				System.out.println(interv.prettyPrint(""));
			}
			System.out.println(Parameter.prettyPrintAll(""));
		} catch (MalformedSimulationModelException e) {
			e.printStackTrace();
		}
	}

	public void addParameterMapping(String ontologyId, String paramId) {
		ontologyParameterMapping.put(ontologyId, paramId);
	}

}
