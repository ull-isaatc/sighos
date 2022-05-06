/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.util.ArrayList;
import java.util.List;

import org.w3c.xsd.owl2.Ontology;

import es.ull.iis.simulation.hta.osdi.utils.ValueParser;
import es.ull.iis.simulation.hta.osdi.wrappers.ExpressionLanguagePathwayCondition;
import es.ull.iis.simulation.hta.osdi.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.AgeBasedTimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.AnnualRiskBasedTimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.progression.ManifestationPathway;
import es.ull.iis.simulation.hta.progression.TimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.condition.AndCondition;
import es.ull.iis.simulation.hta.progression.condition.PathwayCondition;
import es.ull.iis.simulation.hta.progression.condition.PreviousManifestationCondition;
import es.ull.iis.simulation.hta.radios.RadiosRangeAgeMatrixRRCalculator;
import es.ull.iis.simulation.hta.radios.transforms.XmlTransform;

/**
 * @author Iván Castilla Rodríguez
 * @author David Prieto González
 *
 */
public class ManifestationPathwayBuilder {

	/**
	 * 
	 */
	private ManifestationPathwayBuilder() {
	}


	/**
	 * Creates a {@link ManifestationPathway manifestation pathway}
	 * @param ontology
	 * @param secParams
	 * @param disease
	 * @param manifestation
	 * @param pathwayName
	 * @return
	 */
	public static ManifestationPathway getManifestationPathwayInstance(Ontology ontology, SecondOrderParamsRepository secParams, Disease disease, Manifestation manifestation, String pathwayName) {
		ManifestationPathway pathway = null;
		final PathwayCondition cond = createCondition(ontology, secParams, disease, pathwayName);
		final TimeToEventCalculator tte = createTimeToEventCalculator(ontology, secParams, manifestation, pathwayName);
		pathway = new ManifestationPathway(secParams, manifestation, cond, tte);
		return pathway;
	}
	
	/**
	 * Creates a condition for the pathway. Conditions may be expressed as text in the "hasCondition" data property, or as object properties by means of "requiresPreviousManifestation".
	 * FIXME: A pathway may involve more than a condition, but currently, only one value is loaded for each data property. This change requires modifying DataStoreService.eTLDataPropertyValues().  
	 * @param ontology The preloaded ontology
	 * @param secParams Repository
	 * @param disease The disease
	 * @param pathwayName The name of the pathway instance in the ontology
	 * @return A condition for the pathway
	 */
	private static PathwayCondition createCondition(Ontology ontology, SecondOrderParamsRepository secParams, Disease disease, String pathwayName) {
		final String strCond = OwlHelper.getDataPropertyValue(pathwayName, OSDiNames.DataProperty.HAS_CONDITION.getDescription());
		final List<String> strPrevManifestations = OwlHelper.getObjectPropertiesByName(pathwayName, OSDiNames.ObjectProperty.REQUIRES_PREVIOUS_MANIFESTATION.getDescription());
		PathwayCondition cond = null;
		if (strCond == null && strPrevManifestations.size() == 0) {
			cond = PathwayCondition.TRUE_CONDITION;
		}
		else if (strPrevManifestations.size() == 0) {
			cond = new ExpressionLanguagePathwayCondition(strCond);
		}
		else {
			List<Manifestation> manifList = new ArrayList<>();
			for (String manifestationName: strPrevManifestations) {
				manifList.add(disease.getManifestation(manifestationName));
			}
			cond = new PreviousManifestationCondition(manifList);
			if (strCond != null) {
				cond = new AndCondition(cond, new ExpressionLanguagePathwayCondition(strCond));
			}
		}		
		return cond;
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
	private static TimeToEventCalculator createTimeToEventCalculator(Ontology ontology, SecondOrderParamsRepository secParams, Manifestation manifestation, String pathwayName) {
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
				Object[][] datatableMatrix = ValueParser.rangeDatatableToMatrix(XmlTransform.getDataTable(strPManif), secParams);
				tte = new AgeBasedTimeToEventCalculator(datatableMatrix, manifestation, new RadiosRangeAgeMatrixRRCalculator(datatableMatrix));
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
}
