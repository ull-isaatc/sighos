/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import javax.xml.bind.JAXBException;

import org.w3c.xsd.owl2.Ontology;

import es.ull.iis.ontology.radios.json.schema4simulation.PrecedingManifestation;
import es.ull.iis.ontology.radios.utils.CollectionUtils;
import es.ull.iis.simulation.hta.osdi.utils.ValueParser;
import es.ull.iis.simulation.hta.osdi.wrappers.ExpressionLanguagePathwayCondition;
import es.ull.iis.simulation.hta.osdi.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.AgeBasedTimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.progression.ManifestationPathway;
import es.ull.iis.simulation.hta.progression.PathwayCondition;
import es.ull.iis.simulation.hta.progression.PreviousManifestationCondition;
import es.ull.iis.simulation.hta.progression.ProportionBasedTimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.StandardDisease;
import es.ull.iis.simulation.hta.progression.TimeToEventCalculator;
import es.ull.iis.simulation.hta.radios.RadiosRangeAgeMatrixRRCalculator;
import es.ull.iis.simulation.hta.radios.transforms.ValueTransform;
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


	public static ManifestationPathway getManifestationPathwayInstance(Ontology ontology, SecondOrderParamsRepository secParams, Manifestation manifestation, String pathwayName) {
		ManifestationPathway pathway = null;
		final PathwayCondition cond = createCondition(ontology, secParams, pathwayName);
		final TimeToEventCalculator tte = createTimeToEventCalculator(ontology, secParams, pathwayName);
		pathway = new ManifestationPathway(secParams, manifestation, cond, tte);
		return pathway;
	}
	
	private static PathwayCondition createCondition(Ontology ontology, SecondOrderParamsRepository secParams, String pathwayName) {
		final String strCond = OwlHelper.getDataPropertyValue(pathwayName, OSDiNames.DataProperty.HAS_CONDITION.getDescription());
		PathwayCondition cond = null;
		if (strCond == null) {
			cond = PathwayCondition.TRUE_CONDITION;
		}
		else {
			cond = new ExpressionLanguagePathwayCondition(strCond);
		}
		return cond;
	}
	
	private static TimeToEventCalculator createTimeToEventCalculator(Ontology ontology, SecondOrderParamsRepository secParams, Manifestation manifestation, String pathwayName) {
		TimeToEventCalculator tte = null;
		// FIXME: Currently not using time to
		// final String strTimeTo = OwlHelper.getDataPropertyValue(pathwayName, OSDiNames.DataProperty.HAS_TIME_TO.getDescription());
		// FIXME: Assuming that the time to event is described always as an annual risk
		final String strPManif = OwlHelper.getDataPropertyValue(pathwayName, OSDiNames.DataProperty.HAS_PROBABILITY.getDescription());
		if (strPManif != null) {
			ProbabilityDistribution probabilityDistribution = ValueParser.splitProbabilityDistribution(strPManif);
			if (probabilityDistribution != null) {
				tte = new ProportionBasedTimeToEventCalculator(SecondOrderParamsRepository.getProbString(manifestation), secParams, manifestation);
			} else {
				Object[][] datatableMatrix = ValueTransform.rangeDatatableToMatrix(XmlTransform.getDataTable(manifestationProbability), secParams);
				tte = new AgeBasedTimeToEventCalculator(datatableMatrix, manifestation, new RadiosRangeAgeMatrixRRCalculator(datatableMatrix));
			}
		}

		if (CollectionUtils.notIsEmpty(manifJSON.getPrecedingManifestations())) {
			for (PrecedingManifestation precedingManifestation : manifJSON.getPrecedingManifestations()) {
				// Looks for the preceding manifestation
				Manifestation precManif = null;
				for (Manifestation mm : mappings.keySet()) {
					if (mm.getDescription().equals(precedingManifestation.getName())) {
						precManif = mm;
					}
				}
				String transitionProbability = precedingManifestation.getProbability();
				if (transitionProbability != null) {
					ProbabilityDistribution probabilityDistributionForTransition = ValueTransform.splitProbabilityDistribution(transitionProbability);
					TimeToEventCalculator tte;
					if (probabilityDistributionForTransition != null) {
						secParams.addProbParam(precManif, manifestation, Constants.CONSTANT_EMPTY_STRING, probabilityDistributionForTransition.getDeterministicValue(), probabilityDistributionForTransition.getProbabilisticValueInitializedForProbability());
						tte = new ProportionBasedTimeToEventCalculator(SecondOrderParamsRepository.getProbString(precManif, manifestation), secParams, manifestation);
					} else {
						Object[][] datatableMatrix = ValueTransform.rangeDatatableToMatrix(XmlTransform.getDataTable(transitionProbability), secParams);
						tte = new AgeBasedTimeToEventCalculator(datatableMatrix, manifestation, new RadiosRangeAgeMatrixRRCalculator(datatableMatrix));
					}
					final PathwayCondition cond = new PreviousManifestationCondition(precManif);
					new ManifestationPathway(secParams, manifestation, cond, tte);
				}
			}
		}
		return tte;
	}
	
}
