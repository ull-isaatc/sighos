/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.osdi.utils.ValueParser;
import es.ull.iis.simulation.hta.osdi.wrappers.ExpressionLanguagePathwayCondition;
import es.ull.iis.simulation.hta.osdi.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.hta.params.SecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.AnnualRiskBasedTimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.progression.ManifestationPathway;
import es.ull.iis.simulation.hta.progression.TimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.condition.AndCondition;
import es.ull.iis.simulation.hta.progression.condition.PathwayCondition;
import es.ull.iis.simulation.hta.progression.condition.PreviousManifestationCondition;

/**
 * @author Iván Castilla Rodríguez
 * @author David Prieto González
 *
 */
public class ManifestationPathwayBuilder {
	private static final Map<String, ManifestationPathway> createdPathways = new TreeMap<>();
	private static final Map<ManifestationPathway, String> inverseMap = new HashMap<>();

	/**
	 * 
	 */
	private ManifestationPathwayBuilder() {
	}


	/**
	 * Creates a {@link ManifestationPathway manifestation pathway}. If, for any reason, a manifestation pathway was already created for the specified name, returns the 
	 * previously created pathway.
	 * @param ontology
	 * @param secParams
	 * @param disease
	 * @param manifestation
	 * @param pathwayName
	 * @return
	 */
	public static ManifestationPathway getManifestationPathwayInstance(SecondOrderParamsRepository secParams, Manifestation manifestation, String pathwayName) {
		if (createdPathways.containsKey(pathwayName))
			return createdPathways.get(pathwayName);
		final Disease disease = manifestation.getDisease();
		final PathwayCondition cond = createCondition(secParams, disease, pathwayName);
		final TimeToEventCalculator tte = createTimeToEventCalculator(secParams, manifestation, pathwayName);
		final ManifestationPathway pathway = new ManifestationPathway(secParams, manifestation, cond, tte) {
			@Override
			public void registerSecondOrderParameters() {
				createParameters(secParams, this);
			}
		};
		createdPathways.put(pathwayName, pathway);
		inverseMap.put(pathway, pathwayName);
		return pathway;
	}
	
	private static void createParameters(SecondOrderParamsRepository secParams, ManifestationPathway pathway) {
		final String pathwayName = inverseMap.get(pathway);
		final Manifestation manifestation = pathway.getDestManifestation();
		final String strPManif = OwlHelper.getDataPropertyValue(pathwayName, OSDiNames.DataProperty.HAS_PROBABILITY.getDescription());
		if (strPManif != null) {
			ProbabilityDistribution probabilityDistribution = ValueParser.splitProbabilityDistribution(strPManif);
			if (probabilityDistribution != null) {
				SecondOrderParam param = new SecondOrderParam(secParams, getProbString(manifestation, pathwayName), getDescriptionString(manifestation, pathwayName), Constants.CONSTANT_EMPTY_STRING, 
						probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForProbability());
				secParams.addProbParam(param);
			} 
		}
	}
	
	/**
	 * Creates a condition for the pathway. Conditions may be expressed by one or more strings in a "hasCondition" data property, or as object properties by means of "requiresPreviousManifestation".
	 * @param secParams Repository
	 * @param disease The disease
	 * @param pathwayName The name of the pathway instance in the ontology
	 * @return A condition for the pathway
	 */
	private static PathwayCondition createCondition(SecondOrderParamsRepository secParams, Disease disease, String pathwayName) {
		final List<String> strConditions = OwlHelper.getDataPropertyValues(pathwayName, OSDiNames.DataProperty.HAS_CONDITION.getDescription());
		final List<String> strPrevManifestations = OwlHelper.getObjectPropertiesByName(pathwayName, OSDiNames.ObjectProperty.REQUIRES_PREVIOUS_MANIFESTATION.getDescription());
		final ArrayList<PathwayCondition> condList = new ArrayList<>();
		if (strPrevManifestations.size() > 0) {
			final List<Manifestation> manifList = new ArrayList<>();
			for (String manifestationName: strPrevManifestations) {
				manifList.add(disease.getManifestation(manifestationName));
			}
			condList.add(new PreviousManifestationCondition(manifList));
		}
		for (String strCond : strConditions)
			condList.add(new ExpressionLanguagePathwayCondition(strCond));
		// After going through for previous manifestations and other conditions, checks how many conditions were created
		if (condList.size() == 0)
			return PathwayCondition.TRUE_CONDITION;
		if (condList.size() == 1)
			return condList.get(0);
		return new AndCondition(condList);
	}
	
	/**
	 * Creates the calculator for the time to event associated to this pathway. Currently only allows the time to be expressed as an annual risk and, consequently, uses 
	 * a {@link AnnualRiskBasedTimeToEventCalculator}. 
	 * @param ontology The preloaded ontology
	 * @param secParams Repository
	 * @param manifestation The destination manifestation for this pathway
	 * @param pathwayName The name of the pathway instance in the ontology
	 * @return
	 */
	private static TimeToEventCalculator createTimeToEventCalculator(SecondOrderParamsRepository secParams, Manifestation manifestation, String pathwayName) {
		TimeToEventCalculator tte = null;
		// FIXME: Currently not using time to
		// final String strTimeTo = OwlHelper.getDataPropertyValue(pathwayName, OSDiNames.DataProperty.HAS_TIME_TO.getDescription());
		// FIXME: Assuming that the time to event is described always as an annual risk
		final String strPManif = OwlHelper.getDataPropertyValue(pathwayName, OSDiNames.DataProperty.HAS_PROBABILITY.getDescription());
		if (strPManif != null) {
			ProbabilityDistribution probabilityDistribution = ValueParser.splitProbabilityDistribution(strPManif);
			if (probabilityDistribution != null) {
				// FIXME: Still not capable of processing RR 
				final String strRRManif = OwlHelper.getDataPropertyValue(pathwayName, OSDiNames.DataProperty.HAS_RELATIVE_RISK.getDescription());
				tte = new AnnualRiskBasedTimeToEventCalculator(getProbString(manifestation, pathwayName), secParams, manifestation);
				// FIXME: Still not capable of distinguish among different types of parameters
				// tte = new ProportionBasedTimeToEventCalculator(SecondOrderParamsRepository.getProbString(manifestation), secParams, manifestation);
			} else {
//				Object[][] datatableMatrix = ValueParser.rangeDatatableToMatrix(XmlTransform.getDataTable(strPManif), secParams);
//				tte = new AgeBasedTimeToEventCalculator(datatableMatrix, manifestation, new RadiosRangeAgeMatrixRRCalculator(datatableMatrix));
			}
		}
		return tte;
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
			return SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + pathwayName; 
		}
		else {
			return SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + pathwayName + "_" + manifestation.name(); 			
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
}
