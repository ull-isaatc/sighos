/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.util.GregorianCalendar;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.xsd.owl2.Ontology;

import es.ull.iis.ontology.radios.Constants;
import es.ull.iis.ontology.radios.json.schema4simulation.Cost;
import es.ull.iis.ontology.radios.json.schema4simulation.PrecedingManifestation;
import es.ull.iis.ontology.radios.json.schema4simulation.Utility;
import es.ull.iis.ontology.radios.utils.CollectionUtils;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.osdi.utils.ValueParser;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.AcuteManifestation;
import es.ull.iis.simulation.hta.progression.AgeBasedTimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.ChronicManifestation;
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
import es.ull.iis.simulation.hta.radios.wrappers.ProbabilityDistribution;

/**
 * @author David Prieto Gonz�lez
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class ManifestationBuilder {

	/**
	 * 
	 */
	private ManifestationBuilder() {
	}

	public static Manifestation getManifestationInstance(Ontology ontology, SecondOrderParamsRepository secParams, StandardDisease disease, String manifestationName) {
		Manifestation manifestation = null;
		final String type = OwlHelper.getDataPropertyValue(manifestationName, OSDiNames.DataProperty.HAS_MANIFESTATION_KIND.getName(), OSDiNames.DataPropertyRange.KIND_MANIFESTATION_CHRONIC.getName());
		final Double onsetAge = ValueParser.toDoubleValue(OwlHelper.getDataPropertyValue(manifestationName, OSDiNames.DataProperty.HAS_ONSET_AGE.getName(), "0.0"));
		final Double endAge = ValueParser.toDoubleValue(OwlHelper.getDataPropertyValue(manifestationName, OSDiNames.DataProperty.HAS_END_AGE.getName(), "" + BasicConfigParams.DEF_MAX_AGE));
		final String description = OwlHelper.getDataPropertyValue(manifestationName, OSDiNames.DataProperty.HAS_DESCRIPTION.getName(), "");
		if (OSDiNames.DataPropertyRange.KIND_MANIFESTATION_CHRONIC.equals(type)) {
			manifestation = new ChronicManifestation(secParams, manifestationName, description,	disease, onsetAge, endAge) {
					@Override
					public void registerSecondOrderParameters() {
						createParams(secParams, manifestation);
					}
			};
		}
		else {
			manifestation = new AcuteManifestation(secParams, manifestationName, description, disease, onsetAge, endAge) {
					@Override
					public void registerSecondOrderParameters() {
						createParams(secParams, manifestation);
					}
			};			
		}

		manifestation.setDuration(OwlHelper.getDataPropertyValue(manifestationName, Constants.DATAPROPERTY_DURATION));
		manifestation.setFrequency(OwlHelper.getDataPropertyValue(manifestationName, Constants.DATAPROPERTY_FREQUENCY));
		
		manifestation.setMortalityFactor(SecondOrderParamsBuilder.recalculatePropabilityField(manifestationName, Constants.DATAPROPERTY_MORTALITY_FACTOR, Constants.DATAPROPERTY_MORTALITY_FACTOR_DISTRIBUTION));
		manifestation.setProbability(SecondOrderParamsBuilder.recalculatePropabilityField(manifestationName, Constants.DATAPROPERTY_PROBABILITY, Constants.DATAPROPERTY_PROBABILITY_DISTRIBUTION));

		manifestation.setProbabilityOfDiagnosis(OwlHelper.getDataPropertyValue(manifestationName, Constants.DATAPROPERTY_PROBABILITYOFLEADINGTODIAGNOSIS));
		manifestation.setRelativeRisk(OwlHelper.getDataPropertyValue(manifestationName, Constants.DATAPROPERTY_RELATIVE_RISK));

		manifestation.setPrecedingManifestations(getPrecedingManifestations(manifestationName));
		manifestation.setCosts(SecondOrderParamsBuilder.getCosts(manifestationName));		
		manifestation.setUtilities(SecondOrderParamsBuilder.getUtilities(manifestationName));		
		manifestation.setTreatmentStrategies(TreatmentBuilder.getTreatmentStrategies(manifestationName));		
		manifestation.setFollowUpStrategies(FollowUpBuilder.getFollowUpStrategies(manifestationName));
		return manifestation;
	}

	private static void createParams(SecondOrderParamsRepository secParams, Named instance) {
		SecondOrderParamsBuilder.createCostParam(secParams, instance);
		addParamProbabilities(instance);
		addParamMortalityFactorOrProbability(instance);
		addParamDisutility(instance);
		addParamProbabilityDiagnosis(instance);
		
	}
	/**
	 * @param repository
	 * @param disease
	 * @throws JAXBException
	 */
	private static void addParamProbabilities(Manifestation manif) throws JAXBException {
		final es.ull.iis.ontology.radios.json.schema4simulation.Manifestation manifJSON = mappings.get(manif);
		final SecondOrderParamsRepository secParams = manif.getParamsRepository();
		String manifestationProbability = manifJSON.getProbability();
		if (manifestationProbability != null) {
			ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(manifestationProbability);
			TimeToEventCalculator tte;
			if (probabilityDistribution != null) {
				secParams.addProbParam(manif, Constants.CONSTANT_EMPTY_STRING, 
						probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForProbability());
				tte = new ProportionBasedTimeToEventCalculator(SecondOrderParamsRepository.getProbString(manif), secParams, manif);
			} else {
				Object[][] datatableMatrix = ValueTransform.rangeDatatableToMatrix(XmlTransform.getDataTable(manifestationProbability), secParams);
				tte = new AgeBasedTimeToEventCalculator(datatableMatrix, manif, new RadiosRangeAgeMatrixRRCalculator(datatableMatrix));
			}
			new ManifestationPathway(secParams, manif, tte);
		}

		if (CollectionUtils.notIsEmpty(manifJSON.getPrecedingManifestations())) {
			for (PrecedingManifestation precedingManifestation : manifJSON.getPrecedingManifestations()) {
				// Looks for the preceding manifestation
				Manifestation precManif = null;
				for (Manifestation mm : mappings.keySet()) {
					if (mm.getName().equals(precedingManifestation.getName())) {
						precManif = mm;
					}
				}
				String transitionProbability = precedingManifestation.getProbability();
				if (transitionProbability != null) {
					ProbabilityDistribution probabilityDistributionForTransition = ValueTransform.splitProbabilityDistribution(transitionProbability);
					TimeToEventCalculator tte;
					if (probabilityDistributionForTransition != null) {
						secParams.addProbParam(precManif, manif, Constants.CONSTANT_EMPTY_STRING, probabilityDistributionForTransition.getDeterministicValue(), probabilityDistributionForTransition.getProbabilisticValueInitializedForProbability());
						tte = new ProportionBasedTimeToEventCalculator(SecondOrderParamsRepository.getProbString(precManif, manif), secParams, manif);
					} else {
						Object[][] datatableMatrix = ValueTransform.rangeDatatableToMatrix(XmlTransform.getDataTable(transitionProbability), secParams);
						tte = new AgeBasedTimeToEventCalculator(datatableMatrix, manif, new RadiosRangeAgeMatrixRRCalculator(datatableMatrix));
					}
					final PathwayCondition cond = new PreviousManifestationCondition(precManif);
					new ManifestationPathway(secParams, manif, cond, tte);
				}
			}
		}
	}
	
	/**
	 * 
	 */
	private static void addParamMortalityFactorOrProbability(Manifestation manif) {
		es.ull.iis.ontology.radios.json.schema4simulation.Manifestation manifJSON = mappings.get(manif);
		if (manifJSON.getMortalityFactor() != null) {
			ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(manifJSON.getMortalityFactor());
			if (probabilityDistribution != null) {
				if (Manifestation.Type.CHRONIC == manif.getType()) { // Se debe interpretar el valor como que aumenta tu riesgo de muerte * mortalityFactor
					if (probabilityDistribution.getDeterministicValue() > 0) {
						manif.getParamsRepository().addIMRParam(manif, "Mortality factor for " + manif, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForProbability());
					} else {
						manif.getParamsRepository().addLERParam(manif, "Life expectancy reduction for " + manif, Constants.CONSTANT_EMPTY_STRING, Math.abs(probabilityDistribution.getDeterministicValue()), probabilityDistribution.getProbabilisticValueInitializedForProbability());
					}
				} else if (Manifestation.Type.ACUTE == manif.getType()) { // Se debe interpretar el valor de mortalityFactor como la probabilidad de muerte				
					manif.getParamsRepository().addDeathProbParam(manif, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForProbability());
				}
			}
		}
	}

	/**
	 * 
	 */
	private static void addParamProbabilityDiagnosis(Manifestation manif) {
		es.ull.iis.ontology.radios.json.schema4simulation.Manifestation manifJSON = mappings.get(manif);
		if (manifJSON.getProbabilityOfDiagnosis() != null) {
			ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(manifJSON.getProbabilityOfDiagnosis());
			if (probabilityDistribution != null) {
				manif.getParamsRepository().addDiagnosisProbParam(manif, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForProbability());
			}
		}
	}

	/**
	 * TODO: es necesario tener en cuenta las utilidades que pueden ser UTILITY o DISUTILITY y su comportamiento temporal ser: ANNUAL, LIFETIME y ONETIME
	 */
	private static void addParamDisutility(Manifestation manif) {
		es.ull.iis.ontology.radios.json.schema4simulation.Manifestation manifJSON = mappings.get(manif);
		if (CollectionUtils.notIsEmpty(manifJSON.getUtilities())) {
			String value = null;
			Boolean isDisutility = false;
			for (Utility utility : manifJSON.getUtilities()) {
				if (Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ANNUAL_VALUE.equals(utility.getTemporalBehavior())) {
					if (Constants.DATAPROPERTYVALUE_KIND_UTILITY_DISUTILITY.equals(utility.getKind())) {
						value = utility.getValue();
						isDisutility = true;
						break;
					} else if (Constants.DATAPROPERTYVALUE_KIND_UTILITY_UTILITY.equals(utility.getKind())) {
						value = utility.getValue();
						isDisutility = false;
						break;
					}
				}
			}
			ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(value);
			if (probabilityDistribution != null) {
				if (isDisutility) {
					manif.getParamsRepository().addDisutilityParam(manif, "Disutility for " + manif, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValue());
				} else {
					manif.getParamsRepository().addUtilityParam(manif, "Utility for " + manif, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValue());
				}
			}
		}
	}
}
