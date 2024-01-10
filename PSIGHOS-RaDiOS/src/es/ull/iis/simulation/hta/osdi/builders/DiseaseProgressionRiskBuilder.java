/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import es.ull.iis.simulation.condition.AndCondition;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.osdi.OSDiGenericModel;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiDataProperties;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiDataItemTypes;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiClasses;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.ontology.ParameterWrapper;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiObjectProperties;
import es.ull.iis.simulation.hta.osdi.wrappers.ExpressionLanguageCondition;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.params.Parameter.ParameterType;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionPathway;
import es.ull.iis.simulation.hta.progression.calculator.TimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.condition.PreviousDiseaseProgressionCondition;

/**
 * @author Iván Castilla Rodríguez
 * @author David Prieto González
 * TODO: Process different types of combination of parameters (P + RR, TTE, ...) to create the time to event 
 */
public interface DiseaseProgressionRiskBuilder {

	/**
	 * Creates a {@link DiseaseProgressionPathway manifestation pathway}. If, for any reason, a manifestation pathway was already created for the specified name, returns the 
	 * previously created pathway.
	 * @param model
	 * @param progression
	 * @return
	 * @throws MalformedOSDiModelException 
	 */
	public static ArrayList<DiseaseProgressionPathway> getPathwayInstances(OSDiGenericModel model, DiseaseProgression progression) throws MalformedOSDiModelException {
		final ArrayList<DiseaseProgressionPathway> pathwayInstances = new ArrayList<>();
		final Set<String> riskIRIs = OSDiObjectProperties.HAS_RISK_CHARACTERIZATION.getValues(progression.name(), true);

		final OSDiWrapper wrap = ((OSDiGenericModel)model).getOwlWrapper();
		
		if (riskIRIs.size() == 0)
			throw new MalformedOSDiModelException(OSDiClasses.DISEASE_PROGRESSION, progression.name(), OSDiObjectProperties.HAS_RISK_CHARACTERIZATION, "At least one risk characterizations for a disease progression is required.");
		final ArrayList<ParameterWrapper> riskWrappers = new ArrayList<>();

		for (String riskIRI : riskIRIs) {
			final Set<String> superclazzes = wrap.getClassesForIndividual(riskIRI);
			if (superclazzes.contains(OSDiClasses.PARAMETER.getShortName())) {
				riskWrappers.add(wrap.getParameterWrapper(riskIRI, "Developing " + progression.name()));
			}
			else if (superclazzes.contains(OSDiClasses.PATHWAY.getShortName())) {
				pathwayInstances.add(getPathwayInstance(model, progression, riskIRI));
			}
			else 
				throw new MalformedOSDiModelException(OSDiClasses.DISEASE_PROGRESSION, progression.name(), OSDiObjectProperties.HAS_RISK_CHARACTERIZATION, "Unsupported risk characterizations for a disease progression: " + riskIRI);
		}
		
		if (riskWrappers.size() > 0) {
			final TimeToEventCalculator tte = TimeToEventCalculatorBuilder.getTimeToEventCalculator(model, progression, riskWrappers);
			pathwayInstances.add(new OSDiManifestationPathway(model, "PATH_" + progression.name(), "Progression to " + progression.getDescription(), progression, tte, new TrueCondition<DiseaseProgressionPathway.ConditionInformation>(), riskWrappers));
		}
		return pathwayInstances;
	}
	
	public static DiseaseProgressionPathway getPathwayInstance(OSDiGenericModel model, DiseaseProgression progression, String pathwayIRI) throws MalformedOSDiModelException {
		final Condition<DiseaseProgressionPathway.ConditionInformation> cond = createCondition(model, progression.getDisease(), pathwayIRI);
		final Set<String> riskIRIs = OSDiObjectProperties.HAS_RISK_CHARACTERIZATION.getValues(pathwayIRI, true);
		final ArrayList<ParameterWrapper> riskWrappers = new ArrayList<>();

		final OSDiWrapper wrap = ((OSDiGenericModel)model).getOwlWrapper();

		for (String riskIRI : riskIRIs) {
			final Set<String> superclazzes = wrap.getClassesForIndividual(riskIRI);
			if (superclazzes.contains(OSDiClasses.PARAMETER.getShortName())) {
				riskWrappers.add(wrap.getParameterWrapper(riskIRI, "Developing " + progression.name() + " with " + pathwayIRI));
			}
			else 
				throw new MalformedOSDiModelException(OSDiClasses.DISEASE_PROGRESSION, pathwayIRI, OSDiObjectProperties.HAS_RISK_CHARACTERIZATION, "Unsupported risk characterizations for a disease progression: " + riskIRI);
		}

		if (riskWrappers.size() == 0)
			throw new MalformedOSDiModelException(OSDiClasses.DISEASE_PROGRESSION, pathwayIRI, OSDiObjectProperties.HAS_RISK_CHARACTERIZATION, "At least one valid risk characterizations for a disease progression pathway is required.");

		final TimeToEventCalculator tte = TimeToEventCalculatorBuilder.getTimeToEventCalculator(model, progression, riskWrappers);
		return new OSDiManifestationPathway(model, pathwayIRI, OSDiDataProperties.HAS_DESCRIPTION.getValue(pathwayIRI, "Progression to " + progression.getDescription()), progression, tte, cond, riskWrappers);
	}

	/**
	 * Creates a condition for the pathway. Conditions may be expressed by one or more strings in a "hasCondition" data property, or as object properties by means of "requiresPreviousManifestation".
	 * @param model Repository
	 * @param disease The disease
	 * @param pathwayName The name of the pathway instance in the ontology
	 * @return A condition for the pathway
	 */
	private static Condition<DiseaseProgressionPathway.ConditionInformation> createCondition(OSDiGenericModel model, Disease disease, String pathwayName) {
		final Set<String> strConditions = OSDiObjectProperties.HAS_CONDITION_EXPRESSION.getValues(pathwayName);
		
		final Set<String> strRequiredStuff = OSDiObjectProperties.REQUIRES.getValues(pathwayName);
		final ArrayList<Condition<DiseaseProgressionPathway.ConditionInformation>> condList = new ArrayList<>();
		// FIXME: Assuming that all preconditions refer to manifestations though they could be diseases, developments, stages or even interventions
		if (strRequiredStuff.size() > 0) {
			final List<DiseaseProgression> manifList = new ArrayList<>();
			for (String manifestationName: strRequiredStuff) {
				manifList.add(disease.getDiseaseProgression(manifestationName));
			}
			condList.add(new PreviousDiseaseProgressionCondition(manifList));
		}
		for (String strCond : strConditions)
			// FIXME: Conditions are now expressions may be expressed in different languages. See OSDiDataProperties.HAS_CONDITION_LANGUAGE
			condList.add(new ExpressionLanguageCondition(strCond));
		// After going through for previous manifestations and other conditions, checks how many conditions were created
		if (condList.size() == 0)
			return new TrueCondition<DiseaseProgressionPathway.ConditionInformation>();
		if (condList.size() == 1)
			return condList.get(0);
		return new AndCondition<DiseaseProgressionPathway.ConditionInformation>(condList);
	}
	
	/**
	 * Creates a proper name for the second-order parameter that represents the probability associated to this pathway. To ensure unique name, and as a rule of thumb, 
	 * if the name of the pathway instance already includes the name of the destination manifestation, uses the name of the pathway instance. Otherwise, suffixes the 
	 * name of the destination manifestation to the name of the pathway instance. In any case, includes a prefix to indicate that the parameter is a probability. 
	 * @param manifestation The destination manifestation for this pathway
	 * @param pathwayName The name of the pathway instance in the ontology
	 * @return a proper name for the second-order parameter that represents the probability associated to this pathway
	 */
	public static String getProbString(DiseaseProgression manifestation, String pathwayName) {
		if (pathwayName.contains(manifestation.name())) {
			return pathwayName; 
		}
		else {
			return pathwayName + "_" + manifestation.name(); 			
		}
	}

	/**
	 * Creates a proper description for the second-order parameter that represents the probability associated to this pathway.  
	 * @param manifestation The destination manifestation for this pathway
	 * @param pathwayName The name of the pathway instance in the ontology
	 * @return a proper description for the second-order parameter that represents the probability associated to this pathway
	 */
	public static String getDescriptionString(DiseaseProgression manifestation, String pathwayName) {
		return "Probability of developing " + manifestation + " due to " + pathwayName; 
	}
	
	static class OSDiManifestationPathway extends DiseaseProgressionPathway {
		private final ArrayList<ParameterWrapper> riskWrappers;

		public OSDiManifestationPathway(HTAModel model, String name, String description, DiseaseProgression destManifestation,
				TimeToEventCalculator timeToEvent, Condition<DiseaseProgressionPathway.ConditionInformation> condition, ArrayList<ParameterWrapper> riskWrappers) throws MalformedOSDiModelException {
			super(model, name, description, destManifestation, timeToEvent, condition);
			this.riskWrappers = riskWrappers;
		}
		
		@Override
		public void createParameters() {
			for (ParameterWrapper riskWrapper : riskWrappers) {
				final OSDiDataItemTypes dataItems = riskWrapper.getDataItemType();
				if (dataItems.equals(OSDiDataItemTypes.DI_PROBABILITY)) {
					StandardParameter.PROBABILITY.addToModel(model, riskWrapper.createParameter(model, ParameterType.RISK));
				}
				else if (dataItems.equals(OSDiDataItemTypes.DI_PROPORTION)) {
					StandardParameter.PROPORTION.addToModel(model, riskWrapper.createParameter(model, ParameterType.RISK));
				}
				else if (dataItems.equals(OSDiDataItemTypes.DI_RELATIVE_RISK)) {
					StandardParameter.RELATIVE_RISK.addToModel(model, riskWrapper.createParameter(model, ParameterType.RISK));
				}

			}
		}
		
	}
}
