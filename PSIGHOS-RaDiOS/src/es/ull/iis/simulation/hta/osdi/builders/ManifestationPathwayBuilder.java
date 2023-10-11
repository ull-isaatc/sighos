/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.builders;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import es.ull.iis.simulation.condition.AndCondition;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.osdi.OSDiGenericRepository;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.wrappers.ExpressionLanguageCondition;
import es.ull.iis.simulation.hta.osdi.wrappers.ExpressionWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.ParameterWrapper;
import es.ull.iis.simulation.hta.params.ProbabilityParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.AnnualRiskBasedTimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.progression.ManifestationPathway;
import es.ull.iis.simulation.hta.progression.ProportionBasedTimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.TimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.condition.PreviousManifestationCondition;

/**
 * @author Iván Castilla Rodríguez
 * @author David Prieto González
 *
 */
public interface ManifestationPathwayBuilder {

	/**
	 * Creates a {@link ManifestationPathway manifestation pathway}. If, for any reason, a manifestation pathway was already created for the specified name, returns the 
	 * previously created pathway.
	 * @param ontology
	 * @param secParams
	 * @param disease
	 * @param manifestation
	 * @param pathwayName
	 * @return
	 * @throws MalformedOSDiModelException 
	 */
	public static ManifestationPathway getManifestationPathwayInstance(OSDiGenericRepository secParams, Manifestation manifestation, String pathwayName) throws MalformedOSDiModelException {
		final Disease disease = manifestation.getDisease();
		final Condition<Patient> cond = createCondition(secParams, disease, pathwayName);
		// TODO: Process parameters when a parameter requires another one or use complex expressions
		final ParameterWrapper riskWrapper = createRiskWrapper(secParams, manifestation, pathwayName);
		final TimeToEventCalculator tte = createTimeToEventCalculator(secParams, manifestation, pathwayName, riskWrapper);
		final ManifestationPathway pathway = new OSDiManifestationPathway(secParams, manifestation, cond, tte, pathwayName, riskWrapper);
		return pathway;
	}
	
	/**
	 * Creates a condition for the pathway. Conditions may be expressed by one or more strings in a "hasCondition" data property, or as object properties by means of "requiresPreviousManifestation".
	 * @param secParams Repository
	 * @param disease The disease
	 * @param pathwayName The name of the pathway instance in the ontology
	 * @return A condition for the pathway
	 */
	private static Condition<Patient> createCondition(OSDiGenericRepository secParams, Disease disease, String pathwayName) {
		final List<String> strConditions = OSDiWrapper.DataProperty.HAS_CONDITION.getValues(pathwayName);
		
		final Set<String> strRequiredStuff = OSDiWrapper.ObjectProperty.REQUIRES.getValues(pathwayName);
		final ArrayList<Condition<Patient>> condList = new ArrayList<>();
		// FIXME: Assuming that all preconditions refer to manifestations though they could be diseases, developments, stages or even interventions
		if (strRequiredStuff.size() > 0) {
			final List<Manifestation> manifList = new ArrayList<>();
			for (String manifestationName: strRequiredStuff) {
				manifList.add(disease.getManifestation(manifestationName));
			}
			condList.add(new PreviousManifestationCondition(manifList));
		}
		for (String strCond : strConditions)
			condList.add(new ExpressionLanguageCondition(strCond));
		// After going through for previous manifestations and other conditions, checks how many conditions were created
		if (condList.size() == 0)
			return new TrueCondition<Patient>();
		if (condList.size() == 1)
			return condList.get(0);
		return new AndCondition<Patient>(condList);
	}
	
	
	private static ParameterWrapper createRiskWrapper(OSDiGenericRepository secParams, Manifestation manifestation, String pathwayName) throws MalformedOSDiModelException {
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
		return new ParameterWrapper(wrap, pathwayParam, "Developing " + manifestation + " due to " + pathwayName, EnumSet.of(ExpressionWrapper.SupportedType.CONSTANT));
	}
	/**
	 * Creates the calculator for the time to event associated to this pathway. Currently only allows the time to be expressed as an annual risk and, consequently, uses 
	 * a {@link AnnualRiskBasedTimeToEventCalculator}. 
	 * @param ontology The preloaded ontology
	 * @param secParams Repository
	 * @param manifestation The destination manifestation for this pathway
	 * @param pathwayName The name of the pathway instance in the ontology
	 * @return
	 * @throws MalformedOSDiModelException 
	 */
	public static TimeToEventCalculator createTimeToEventCalculator(OSDiGenericRepository secParams, Manifestation manifestation, String pathwayName, ParameterWrapper riskWrapper) throws MalformedOSDiModelException {
		
		final Set<OSDiWrapper.DataItemType> dataItems = riskWrapper.getDataItemTypes();
		
		if (dataItems.contains(OSDiWrapper.DataItemType.DI_PROBABILITY)) {
			return new AnnualRiskBasedTimeToEventCalculator(ProbabilityParamDescriptions.PROBABILITY.getParameterName(getProbString(manifestation, pathwayName)), secParams, manifestation);
			// FIXME: Currently not using anything more complex than a value
//			final String strRRManif = OSDiNames.DataProperty.HAS_RELATIVE_RISK.getValue(pathwayName);
//			if (strRRManif == null)
//				return new AnnualRiskBasedTimeToEventCalculator(ProbabilityParamDescriptions.PROBABILITY.getParameterName(getProbString(manifestation, pathwayName)), secParams, manifestation);
		}
		else if (dataItems.contains(OSDiWrapper.DataItemType.DI_PROPORTION)) {
			return new ProportionBasedTimeToEventCalculator(ProbabilityParamDescriptions.PROPORTION.getParameterName(getProbString(manifestation, pathwayName)), secParams, manifestation);
		}
		else if (dataItems.contains(OSDiWrapper.DataItemType.DI_TIMETOEVENT)) {
			// FIXME: Currently not using time to
			// final String strTimeTo = OwlHelper.getDataPropertyValue(pathwayName, OSDiNames.DataProperty.HAS_TIME_TO.getDescription());
			// FIXME: Still not processing data tables
//			Object[][] datatableMatrix = ValueParser.rangeDatatableToMatrix(XmlTransform.getDataTable(strPManif), secParams);
//			tte = new AgeBasedTimeToEventCalculator(datatableMatrix, manifestation, new RadiosRangeAgeMatrixRRCalculator(datatableMatrix));
		}
		else {
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.MANIFESTATION_PATHWAY, pathwayName, OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "Unsupported data item types");			
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
	public static String getProbString(Manifestation manifestation, String pathwayName) {
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
	public static String getDescriptionString(Manifestation manifestation, String pathwayName) {
		return "Probability of developing " + manifestation + " due to " + pathwayName; 
	}
	
	static class OSDiManifestationPathway extends ManifestationPathway {
		private final String pathwayName; 
		private final ParameterWrapper riskWrapper;

		public OSDiManifestationPathway(SecondOrderParamsRepository secParams, Manifestation destManifestation,
				Condition<Patient> condition, TimeToEventCalculator timeToEvent, String pathwayName, ParameterWrapper riskWrapper) {
			super(secParams, destManifestation, condition, timeToEvent);
			this.pathwayName = pathwayName;
			this.riskWrapper = riskWrapper;
		}
		
		@Override
		public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
			final Manifestation manifestation = this.getDestManifestation();
			final Set<OSDiWrapper.DataItemType> dataItems = riskWrapper.getDataItemTypes();
			if (dataItems.contains(OSDiWrapper.DataItemType.DI_PROBABILITY)) {
				ProbabilityParamDescriptions.PROBABILITY.addParameter(secParams, getProbString(manifestation, pathwayName), riskWrapper.getDescription(), riskWrapper.getSource(),
						riskWrapper.getExpression().getConstantValue(), riskWrapper.getProbabilisticValue());
			}
			else if (dataItems.contains(OSDiWrapper.DataItemType.DI_PROPORTION)) {
				ProbabilityParamDescriptions.PROPORTION.addParameter(secParams, getProbString(manifestation, pathwayName), riskWrapper.getDescription(), riskWrapper.getSource(),
						riskWrapper.getExpression().getConstantValue(), riskWrapper.getProbabilisticValue());
			}
			else {
				final Exception ex = new MalformedOSDiModelException(OSDiWrapper.Clazz.MANIFESTATION_PATHWAY, pathwayName, OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "Unsupported data item types");
				System.err.println(ex.getMessage());
			}
		}
		
	}
}
