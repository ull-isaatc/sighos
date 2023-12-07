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
import es.ull.iis.simulation.hta.osdi.OSDiGenericRepository;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.wrappers.ExpressionLanguageCondition;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.ParameterWrapper;
import es.ull.iis.simulation.hta.params.RiskParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.calculators.AnnualRiskBasedTimeToEventCalculator;
import es.ull.iis.simulation.hta.params.calculators.ParameterCalculator;
import es.ull.iis.simulation.hta.params.calculators.ProportionBasedTimeToEventCalculator;
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
	public static DiseaseProgressionPathway getPathwayInstance(OSDiGenericRepository secParams, DiseaseProgression progression, Set<String> riskIRIs) throws MalformedOSDiModelException {
		final Disease disease = progression.getDisease();
		final OSDiWrapper wrap = ((OSDiGenericRepository)secParams).getOwlWrapper();
		
		if (riskIRIs.size() == 0)
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.DISEASE_PROGRESSION, progression.name(), OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION, "At least one risk characterizations for a disease progression is required.");
		if (riskIRIs.size() > 2)
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.DISEASE_PROGRESSION, progression.name(), OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION, "More than two risk characterizations for a disease progression not supported. Currently " + riskIRIs.size());
		if (riskIRIs.size() == 1) {
			final String riskIRI = (String) riskIRIs.toArray()[0];
			Set<String> superclazzes = wrap.getClassesForIndividual(riskIRI);
			
			// If the risk is expressed as a pathway
			if (superclazzes.contains(OSDiWrapper.Clazz.PATHWAY.getShortName())) {			
				final Condition<DiseaseProgressionPathway.ConditionInformation> cond = createCondition(secParams, disease, riskIRI);
				// TODO: Process parameters when a parameter requires another one or use complex expressions
				final ParameterWrapper riskWrapper = createRiskWrapper(secParams, progression, riskIRI);
				final ParameterCalculator tte = createTimeToEventCalculator(secParams, progression, riskWrapper);
				return new OSDiManifestationPathway(secParams, progression, cond, tte, riskWrapper);
			}
			else if (superclazzes.contains(OSDiWrapper.Clazz.PARAMETER.getShortName())) {
				final ParameterWrapper riskWrapper = new ParameterWrapper(wrap, riskIRI, "Developing " + progression.name());
				final ParameterCalculator tte = createTimeToEventCalculator(secParams, progression, riskWrapper);
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
			final ParameterCalculator tte = createTimeToEventCalculator(secParams, progression, riskWrappers);
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
	private static Condition<DiseaseProgressionPathway.ConditionInformation> createCondition(OSDiGenericRepository secParams, Disease disease, String pathwayName) {
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
	
	
	private static ParameterWrapper createRiskWrapper(OSDiGenericRepository secParams, DiseaseProgression progression, String pathwayName) throws MalformedOSDiModelException {
		final OSDiWrapper wrap = ((OSDiGenericRepository)secParams).getOwlWrapper();
		
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
	 * Creates the calculator for the time to event associated to this pathway. Currently only allows the time to be expressed as an annual risk and, consequently, uses 
	 * a {@link AnnualRiskBasedTimeToEventCalculator}. 
	 * @param secParams Repository
	 * @param progression The destination progression for this pathway
	 * @return
	 * @throws MalformedOSDiModelException 
	 */
	public static ParameterCalculator createTimeToEventCalculator(OSDiGenericRepository secParams, DiseaseProgression progression, ParameterWrapper riskWrapper) throws MalformedOSDiModelException {
		
		final Set<OSDiWrapper.DataItemType> dataItems = riskWrapper.getDataItemTypes();
		
		if (dataItems.contains(OSDiWrapper.DataItemType.DI_PROBABILITY)) {
			return new AnnualRiskBasedTimeToEventCalculator(RiskParamDescriptions.PROBABILITY.getParameterName(riskWrapper.getParamId()), secParams, progression);
			// FIXME: Currently not using anything more complex than a value
//			final String strRRManif = OSDiNames.DataProperty.HAS_RELATIVE_RISK.getValue(pathwayName);
//			if (strRRManif == null)
//				return new AnnualRiskBasedTimeToEventCalculator(ProbabilityParamDescriptions.PROBABILITY.getParameterName(getProbString(manifestation, pathwayName)), secParams, manifestation);
		}
		else if (dataItems.contains(OSDiWrapper.DataItemType.DI_PROPORTION)) {
			return new ProportionBasedTimeToEventCalculator(RiskParamDescriptions.PROPORTION.getParameterName(riskWrapper.getParamId()), secParams, progression);
		}
		else if (dataItems.contains(OSDiWrapper.DataItemType.DI_TIME_TO_EVENT)) {
			// FIXME: Currently not using time to
			// final String strTimeTo = OwlHelper.getDataPropertyValue(pathwayName, OSDiNames.DataProperty.HAS_TIME_TO.getDescription());
			// FIXME: Still not processing data tables
//			Object[][] datatableMatrix = ValueParser.rangeDatatableToMatrix(XmlTransform.getDataTable(strPManif), secParams);
//			tte = new AgeBasedTimeToEventCalculator(datatableMatrix, manifestation, new RadiosRangeAgeMatrixRRCalculator(datatableMatrix));
		}
		else {
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.PARAMETER, riskWrapper.getParamId(), OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "Unsupported data item types");			
		}
		return null;
	}

	/**
	 * Creates the calculator for the time to event associated to this pathway. Currently only allows the time to be expressed as an annual risk and, consequently, uses 
	 * a {@link AnnualRiskBasedTimeToEventCalculator}. 
	 * @param secParams Repository
	 * @param progression The destination progression for this pathway
	 * @return
	 * @throws MalformedOSDiModelException 
	 */
	public static ParameterCalculator createTimeToEventCalculator(OSDiGenericRepository secParams, DiseaseProgression progression, ParameterWrapper[] riskWrapper) throws MalformedOSDiModelException {
		
		final Set<OSDiWrapper.DataItemType> dataItems = riskWrapper.getDataItemTypes();
		
		if (dataItems.contains(OSDiWrapper.DataItemType.DI_PROBABILITY)) {
			return new AnnualRiskBasedTimeToEventCalculator(RiskParamDescriptions.PROBABILITY.getParameterName(riskWrapper.getParamId()), secParams, progression);
			// FIXME: Currently not using anything more complex than a value
//			final String strRRManif = OSDiNames.DataProperty.HAS_RELATIVE_RISK.getValue(pathwayName);
//			if (strRRManif == null)
//				return new AnnualRiskBasedTimeToEventCalculator(ProbabilityParamDescriptions.PROBABILITY.getParameterName(getProbString(manifestation, pathwayName)), secParams, manifestation);
		}
		else if (dataItems.contains(OSDiWrapper.DataItemType.DI_PROPORTION)) {
			return new ProportionBasedTimeToEventCalculator(RiskParamDescriptions.PROPORTION.getParameterName(riskWrapper.getParamId()), secParams, progression);
		}
		else if (dataItems.contains(OSDiWrapper.DataItemType.DI_TIME_TO_EVENT)) {
			// FIXME: Currently not using time to
			// final String strTimeTo = OwlHelper.getDataPropertyValue(pathwayName, OSDiNames.DataProperty.HAS_TIME_TO.getDescription());
			// FIXME: Still not processing data tables
//			Object[][] datatableMatrix = ValueParser.rangeDatatableToMatrix(XmlTransform.getDataTable(strPManif), secParams);
//			tte = new AgeBasedTimeToEventCalculator(datatableMatrix, manifestation, new RadiosRangeAgeMatrixRRCalculator(datatableMatrix));
		}
		else {
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.PARAMETER, riskWrapper.getParamId(), OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "Unsupported data item types");			
		}
		return null;
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
				RiskParamDescriptions.PROBABILITY.addParameter(secParams, riskWrapper.getParamId(), riskWrapper.getDescription(), riskWrapper.getSource(),
						riskWrapper.getDeterministicValue(), riskWrapper.getProbabilisticValue());
			}
			else if (dataItems.contains(OSDiWrapper.DataItemType.DI_PROPORTION)) {
				RiskParamDescriptions.PROPORTION.addParameter(secParams, riskWrapper.getParamId(), riskWrapper.getDescription(), riskWrapper.getSource(),
						riskWrapper.getDeterministicValue(), riskWrapper.getProbabilisticValue());
			}
		}
		
	}
}
