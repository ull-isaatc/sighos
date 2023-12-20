/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.github.jsonldjava.shaded.com.google.common.reflect.Parameter;

import es.ull.iis.simulation.condition.AndCondition;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.hta.osdi.OSDiGenericModel;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.wrappers.ExpressionLanguageCondition;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.ParameterWrapper;
import es.ull.iis.simulation.hta.params.RiskParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.calculators.ParameterCalculator;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionPathway;
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
	 * @param ontology
	 * @param secParams
	 * @param disease
	 * @param progression
	 * @param riskIRI
	 * @return
	 * @throws MalformedOSDiModelException 
	 */
	public static DiseaseProgressionPathway getPathwayInstance(OSDiGenericModel secParams, DiseaseProgression progression, Set<String> riskIRIs) throws MalformedOSDiModelException {
		final Disease disease = progression.getDisease();
		final OSDiWrapper wrap = ((OSDiGenericModel)secParams).getOwlWrapper();
		
		if (riskIRIs.size() == 0)
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.DISEASE_PROGRESSION, progression.name(), OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION, "At least one risk characterizations for a disease progression is required.");
		if (riskIRIs.size() > 2)
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.DISEASE_PROGRESSION, progression.name(), OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION, "More than two risk characterizations for a disease progression not supported. Currently " + riskIRIs.size());
		final ArrayList<ParameterWrapper> riskWrappers = new ArrayList<>();
		
		if (riskIRIs.size() == 1) {
			final String riskIRI = (String) riskIRIs.toArray()[0];
			Set<String> superclazzes = wrap.getClassesForIndividual(riskIRI);
			
			// If the risk is expressed as a pathway
			if (superclazzes.contains(OSDiWrapper.Clazz.PATHWAY.getShortName())) {			
				final Condition<DiseaseProgressionPathway.ConditionInformation> cond = createCondition(secParams, disease, riskIRI);
				// TODO: Process parameters when a parameter requires another one or use complex expressions
				riskWrappers.add(createRiskWrapper(secParams, progression, riskIRI));
				final Parameter tte = TimeToEventCalculatorBuilder.getTimeToEventCalculator(secParams, progression, riskWrappers);
				return new OSDiManifestationPathway(secParams, progression, cond, tte, riskWrapper);
			}
			else if (superclazzes.contains(OSDiWrapper.Clazz.PARAMETER.getShortName())) {
				final ParameterWrapper riskWrapper = new ParameterWrapper(wrap, riskIRI, "Developing " + progression.name());
				final Parameter tte = TimeToEventCalculatorBuilder.getTimeToEventCalculator(secParams, progression, riskWrapper);
				return new OSDiManifestationPathway(secParams, progression, new TrueCondition<DiseaseProgressionPathway.ConditionInformation>(), tte, riskWrapper);			
			}
			else 
				throw new MalformedOSDiModelException(OSDiWrapper.Clazz.DISEASE_PROGRESSION, progression.name(), OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION, "Unsupported risk characterizations for a disease progression: " + riskIRI);
		}
		else {
			final String riskIRI1 = (String) riskIRIs.toArray()[0];
			Set<String> superclazzes1 = wrap.getClassesForIndividual(riskIRI1);
			final String riskIRI2 = (String) riskIRIs.toArray()[1];
			Set<String> superclazzes2 = wrap.getClassesForIndividual(riskIRI2);
			if (!superclazzes1.contains(OSDiWrapper.Clazz.PARAMETER.getShortName()) || !superclazzes2.contains(OSDiWrapper.Clazz.PARAMETER.getShortName()))
				throw new MalformedOSDiModelException(OSDiWrapper.Clazz.DISEASE_PROGRESSION, progression.name(), OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION, "Unsupported risk characterizations for a disease progression. Both instances should be Parameters: " + riskIRI1 + " and " + riskIRI2);
			final ParameterWrapper[] riskWrappers = new ParameterWrapper[2]; 
			riskWrappers[0] = new ParameterWrapper(wrap, riskIRI1, "Developing " + progression.name());
			riskWrappers[1] = new ParameterWrapper(wrap, riskIRI2, "Developing " + progression.name());
			final ParameterCalculator tte = TimeToEventCalculatorBuilder.getTimeToEventCalculator(secParams, progression, riskWrappers);
			// TODO: See how to handle RR and probabilities together
			//return new OSDiManifestationPathway(secParams, progression, new TrueCondition<DiseaseProgressionPathway.ConditionInformation>(), tte, riskWrapper);			
			
		}
		return null;			
	}
	
	/**
	 * Creates a condition for the pathway. Conditions may be expressed by one or more strings in a "hasCondition" data property, or as object properties by means of "requiresPreviousManifestation".
	 * @param secParams Repository
	 * @param disease The disease
	 * @param pathwayName The name of the pathway instance in the ontology
	 * @return A condition for the pathway
	 */
	private static Condition<DiseaseProgressionPathway.ConditionInformation> createCondition(OSDiGenericModel secParams, Disease disease, String pathwayName) {
		final List<String> strConditions = OSDiWrapper.DataProperty.HAS_CONDITION.getValues(pathwayName);
		
		final Set<String> strRequiredStuff = OSDiWrapper.ObjectProperty.REQUIRES.getValues(pathwayName);
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
			condList.add(new ExpressionLanguageCondition(strCond));
		// After going through for previous manifestations and other conditions, checks how many conditions were created
		if (condList.size() == 0)
			return new TrueCondition<DiseaseProgressionPathway.ConditionInformation>();
		if (condList.size() == 1)
			return condList.get(0);
		return new AndCondition<DiseaseProgressionPathway.ConditionInformation>(condList);
	}
	
	
	private static ParameterWrapper createRiskWrapper(OSDiGenericModel secParams, DiseaseProgression progression, String pathwayName) throws MalformedOSDiModelException {
		final OSDiWrapper wrap = ((OSDiGenericModel)secParams).getOwlWrapper();
		
		// Gets the manifestation pathway parameters related to the working model
		final Set<String> pathwayParams = OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION.getValues(pathwayName, true);
		if (pathwayParams.size() == 0) {
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.MANIFESTATION_PATHWAY, pathwayName, OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION, "Manifestation pathways require a risk characterization");
		}
		final String pathwayParam = (String)pathwayParams.toArray()[0];
		if (pathwayParams.size() > 1) {
			wrap.printWarning(pathwayName, OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION, "Manifestation pathways should define a single risk characterization. Using " + pathwayParam);
		}
		return new ParameterWrapper(wrap, pathwayParam, "Developing " + progression + " due to " + pathwayName);
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
	
	// TODO: Requires refactoring to be consistent with TimeToEventCalculatorBuilder
	static class OSDiManifestationPathway extends DiseaseProgressionPathway {
		private final ParameterWrapper riskWrapper;

		public OSDiManifestationPathway(SecondOrderParamsRepository secParams, DiseaseProgression destManifestation,
				Condition<DiseaseProgressionPathway.ConditionInformation> condition, ParameterCalculator timeToEvent, ParameterWrapper riskWrapper) throws MalformedOSDiModelException {
			super(secParams, destManifestation, condition, timeToEvent);
			this.riskWrapper = riskWrapper;
		}
		
		@Override
		public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
			final Set<OSDiWrapper.DataItemType> dataItems = riskWrapper.getDataItemTypes();
			if (dataItems.contains(OSDiWrapper.DataItemType.DI_PROBABILITY)) {
				RiskParamDescriptions.PROBABILITY.addUsedParameter(secParams, riskWrapper.getOriginalIndividualIRI(), riskWrapper.getDescription(), riskWrapper.getSource(),
						riskWrapper.getDeterministicValue(), riskWrapper.getProbabilisticValue());
			}
			else if (dataItems.contains(OSDiWrapper.DataItemType.DI_PROPORTION)) {
				RiskParamDescriptions.PROPORTION.addUsedParameter(secParams, riskWrapper.getOriginalIndividualIRI(), riskWrapper.getDescription(), riskWrapper.getSource(),
						riskWrapper.getDeterministicValue(), riskWrapper.getProbabilisticValue());
			}
		}
		
	}
}
