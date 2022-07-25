/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.util.ArrayList;
import java.util.List;

import es.ull.iis.simulation.condition.AndCondition;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.utils.Constants;
import es.ull.iis.simulation.hta.osdi.utils.ValueParser;
import es.ull.iis.simulation.hta.osdi.wrappers.ExpressionLanguageCondition;
import es.ull.iis.simulation.hta.osdi.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.hta.params.DefaultProbabilitySecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParam;
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
	 * @throws TranspilerException 
	 */
	public static ManifestationPathway getManifestationPathwayInstance(SecondOrderParamsRepository secParams, Manifestation manifestation, String pathwayName) throws TranspilerException {
		final Disease disease = manifestation.getDisease();
		final Condition<Patient> cond = createCondition(secParams, disease, pathwayName);
		final TimeToEventCalculator tte = createTimeToEventCalculator(secParams, manifestation, pathwayName);
		final ManifestationPathway pathway = new OSDiManifestationPathway(secParams, manifestation, cond, tte, pathwayName);
		return pathway;
	}
	
	/**
	 * Creates a condition for the pathway. Conditions may be expressed by one or more strings in a "hasCondition" data property, or as object properties by means of "requiresPreviousManifestation".
	 * @param secParams Repository
	 * @param disease The disease
	 * @param pathwayName The name of the pathway instance in the ontology
	 * @return A condition for the pathway
	 */
	private static Condition<Patient> createCondition(SecondOrderParamsRepository secParams, Disease disease, String pathwayName) {
		final List<String> strConditions = OSDiNames.DataProperty.HAS_CONDITION.getValues(pathwayName);
		final List<String> strPrevManifestations = OSDiNames.ObjectProperty.REQUIRES_PREVIOUS_MANIFESTATION.getValues(pathwayName);
		final ArrayList<Condition<Patient>> condList = new ArrayList<>();
		if (strPrevManifestations.size() > 0) {
			final List<Manifestation> manifList = new ArrayList<>();
			for (String manifestationName: strPrevManifestations) {
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
	
	/**
	 * Creates the calculator for the time to event associated to this pathway. Currently only allows the time to be expressed as an annual risk and, consequently, uses 
	 * a {@link AnnualRiskBasedTimeToEventCalculator}. 
	 * @param ontology The preloaded ontology
	 * @param secParams Repository
	 * @param manifestation The destination manifestation for this pathway
	 * @param pathwayName The name of the pathway instance in the ontology
	 * @return
	 * @throws TranspilerException 
	 */
	private static TimeToEventCalculator createTimeToEventCalculator(SecondOrderParamsRepository secParams, Manifestation manifestation, String pathwayName) throws TranspilerException {
		// TODO: Check the order to process these parameters or think in a different solution
		// First check if the pathway is defined as a proportion
		final String strPropManif = OSDiNames.DataProperty.HAS_PROPORTION.getValue(pathwayName);
		if (strPropManif != null) {
			return new ProportionBasedTimeToEventCalculator(getProbString(manifestation, pathwayName), secParams, manifestation);
		}
		final String strPManif = OSDiNames.DataProperty.HAS_PROBABILITY.getValue(pathwayName);
		// FIXME: Assuming that the time to event is described always as an annual risk
		if (strPManif != null) {
				// FIXME: Still not capable of processing RR 
				final String strRRManif = OSDiNames.DataProperty.HAS_RELATIVE_RISK.getValue(pathwayName);
				return new AnnualRiskBasedTimeToEventCalculator(getProbString(manifestation, pathwayName), secParams, manifestation);
		}			
		// FIXME: Currently not using time to
		// final String strTimeTo = OwlHelper.getDataPropertyValue(pathwayName, OSDiNames.DataProperty.HAS_TIME_TO.getDescription());
		// FIXME: Still not processing data tables
//		Object[][] datatableMatrix = ValueParser.rangeDatatableToMatrix(XmlTransform.getDataTable(strPManif), secParams);
//		tte = new AgeBasedTimeToEventCalculator(datatableMatrix, manifestation, new RadiosRangeAgeMatrixRRCalculator(datatableMatrix));
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

		public OSDiManifestationPathway(SecondOrderParamsRepository secParams, Manifestation destManifestation,
				Condition<Patient> condition, TimeToEventCalculator timeToEvent, String pathwayName) {
			super(secParams, destManifestation, condition, timeToEvent);
			this.pathwayName = pathwayName;
		}
		
		@Override
		public void registerSecondOrderParameters() {
			final Manifestation manifestation = this.getDestManifestation();
			try {
				// TODO: Check the order to process these parameters or think in a different solution
				final String strPropManif = OSDiNames.DataProperty.HAS_PROPORTION.getValue(pathwayName);
				if (strPropManif != null) {
					final ProbabilityDistribution probabilityDistribution = ValueParser.splitProbabilityDistribution(strPropManif);
					if (probabilityDistribution == null) {
						throw new TranspilerException("Error parsing regular expression \"" + strPropManif + "\" for data property 'hasProportion' of instance \"" + pathwayName + "\"");
					}
					DefaultProbabilitySecondOrderParam.PROPORTION.addParameter(secParams, getProbString(manifestation, pathwayName), "patients developing " + manifestation + " due to " + pathwayName, Constants.CONSTANT_EMPTY_STRING,
							probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForProbability());
				}
				else {
					final String strPManif = OSDiNames.DataProperty.HAS_PROBABILITY.getValue(pathwayName);
					if (strPManif != null) {
						ProbabilityDistribution probabilityDistribution = ValueParser.splitProbabilityDistribution(strPManif);
						if (probabilityDistribution == null) {
							throw new TranspilerException("Error parsing regular expression \"" + strPManif + "\" for data property 'hasProbability' of instance \"" + pathwayName + "\"");
						} 
						DefaultProbabilitySecondOrderParam.PROBABILITY.addParameter(secParams, getProbString(manifestation, pathwayName), "developing " + manifestation + " due to " + pathwayName, Constants.CONSTANT_EMPTY_STRING,
								probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForProbability());
					}
				}
			} catch(TranspilerException ex) {
				System.err.println(ex.getMessage());
			}
		}
		
	}
}
