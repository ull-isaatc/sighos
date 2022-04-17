/**
 * 
 */
package es.ull.iis.simulation.hta.radios;

import java.util.GregorianCalendar;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import es.ull.iis.ontology.radios.Constants;
import es.ull.iis.ontology.radios.json.schema4simulation.Cost;
import es.ull.iis.ontology.radios.json.schema4simulation.PrecedingManifestation;
import es.ull.iis.ontology.radios.json.schema4simulation.Utility;
import es.ull.iis.ontology.radios.utils.CollectionUtils;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.AcuteManifestation;
import es.ull.iis.simulation.hta.progression.AgeBasedTimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.ChronicManifestation;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.progression.ManifestationPathway;
import es.ull.iis.simulation.hta.progression.PathwayCondition;
import es.ull.iis.simulation.hta.progression.PreviousManifestationCondition;
import es.ull.iis.simulation.hta.progression.ProportionBasedTimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.TimeToEventCalculator;
import es.ull.iis.simulation.hta.radios.transforms.ValueTransform;
import es.ull.iis.simulation.hta.radios.transforms.XmlTransform;
import es.ull.iis.simulation.hta.radios.wrappers.ProbabilityDistribution;
import jakarta.xml.bind.JAXBException;

/**
 * @author Iván Castilla Rodríguez
 * @author David Prieto González
 *
 */
public class ManifestationFactory {
	private static TreeMap<Manifestation, es.ull.iis.ontology.radios.json.schema4simulation.Manifestation> mappings = new TreeMap<>();
	
	private ManifestationFactory() {		
	}
	
	public static Manifestation getManifestationInstance(SecondOrderParamsRepository secParams, Disease disease, es.ull.iis.ontology.radios.json.schema4simulation.Manifestation manifJSON) throws JAXBException {
		Manifestation newManif = null;
		if (manifJSON.getKind() != null) {
			if (Manifestation.Type.CHRONIC.equals(Manifestation.Type.valueOf(manifJSON.getKind()))) {
				newManif = new ChronicManifestation(secParams, manifJSON.getName(), Constants.CONSTANT_EMPTY_STRING, disease,  
						ValueTransform.toDoubleValue(manifJSON.getOnSetAge()), ValueTransform.toDoubleValue(manifJSON.getEndAge())) {
					
					@Override
					public void registerSecondOrderParameters() {
						addParametersToRepository(this);
					}
				};
			}
			else {
				newManif = new AcuteManifestation(secParams, manifJSON.getName(), Constants.CONSTANT_EMPTY_STRING, disease, 
						ValueTransform.toDoubleValue(manifJSON.getOnSetAge()), ValueTransform.toDoubleValue(manifJSON.getEndAge())) {
					
					@Override
					public void registerSecondOrderParameters() {
						addParametersToRepository(this);
					}
				};
			}
		}
		// If no type is specified at the instance, the manifestation is assumed to be chronic
		else {
			newManif = new ChronicManifestation(secParams, manifJSON.getName(), Constants.CONSTANT_EMPTY_STRING, disease,  
					ValueTransform.toDoubleValue(manifJSON.getOnSetAge()), ValueTransform.toDoubleValue(manifJSON.getEndAge())) {
				
				@Override
				public void registerSecondOrderParameters() {
					addParametersToRepository(this);
				}
			};
		}
		mappings.put(newManif, manifJSON);
		return newManif;
	}
	

	/**
	 * @param repository
	 * @param disease
	 * @throws JAXBException
	 */
	private static void addParamProbabilities(Manifestation manif) throws JAXBException {
		final es.ull.iis.ontology.radios.json.schema4simulation.Manifestation manifJSON = mappings.get(manif);
		final SecondOrderParamsRepository secParams = manif.getSecParamsRepository();
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
						manif.getSecParamsRepository().addIMRParam(manif, "Mortality factor for " + manif, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForProbability());
					} else {
						manif.getSecParamsRepository().addLERParam(manif, "Life expectancy reduction for " + manif, Constants.CONSTANT_EMPTY_STRING, Math.abs(probabilityDistribution.getDeterministicValue()), probabilityDistribution.getProbabilisticValueInitializedForProbability());
					}
				} else if (Manifestation.Type.ACUTE == manif.getType()) { // Se debe interpretar el valor de mortalityFactor como la probabilidad de muerte				
					manif.getSecParamsRepository().addDeathProbParam(manif, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForProbability());
				}
			}
		}
	}


	/**
	 * TODO: es necesario tener en cuenta los costos de los tipos: ANNUAL, LIFETIME y ONETIME
	 */
	private static void addParamCosts(Manifestation manif) {
		es.ull.iis.ontology.radios.json.schema4simulation.Manifestation manifJSON = mappings.get(manif);
		if (CollectionUtils.notIsEmpty(manifJSON.getCosts())) {
			String annualCost = null;
			String onetimeCost = null;
			Integer yearAnnualCost = null;
			Integer yearOnetimeCost = null;
			for (Cost cost : manifJSON.getCosts()) {
				if (annualCost == null && Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ANNUAL_VALUE.equals(cost.getTemporalBehavior())) {
					annualCost = cost.getAmount();
					yearAnnualCost = !StringUtils.isEmpty(cost.getYear()) ? Integer.parseInt(cost.getYear()) : (new GregorianCalendar()).get(GregorianCalendar.YEAR);
				} else if (onetimeCost == null && Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ONETIME_VALUE.equals(cost.getTemporalBehavior())) {
					onetimeCost = cost.getAmount();
					yearOnetimeCost = !StringUtils.isEmpty(cost.getYear()) ? Integer.parseInt(cost.getYear()) : (new GregorianCalendar()).get(GregorianCalendar.YEAR);
				}
			}		
			
			if (annualCost != null) {
				ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(annualCost);
				if (probabilityDistribution != null) {
					manif.getSecParamsRepository().addCostParam(manif, "Cost for " + manif, Constants.CONSTANT_EMPTY_STRING, yearAnnualCost, 
							probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForCost());
				}
			}
			
			if (onetimeCost != null) {
				ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(onetimeCost);
				if (probabilityDistribution != null) {
					manif.getSecParamsRepository().addTransitionCostParam(manif, "Punctual cost for " + manif, Constants.CONSTANT_EMPTY_STRING, yearOnetimeCost, probabilityDistribution.getDeterministicValue(),
							probabilityDistribution.getProbabilisticValueInitializedForCost());
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
				manif.getSecParamsRepository().addDiagnosisProbParam(manif, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForProbability());
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
					manif.getSecParamsRepository().addDisutilityParam(manif, "Disutility for " + manif, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValue());
				} else {
					manif.getSecParamsRepository().addUtilityParam(manif, "Utility for " + manif, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValue());
				}
			}
		}
	}
	
	/**
	 * 
	 */
	public static void addParametersToRepository(Manifestation manif) {
		// FIXME: ¿Para qué se comprueba esto?
//		if (getManifestation() != null) {
			try {
				addParamProbabilities(manif);
			} catch (JAXBException e) {
				e.printStackTrace();
			}
			addParamCosts(manif);
			addParamMortalityFactorOrProbability(manif);
			addParamDisutility(manif);
			addParamProbabilityDiagnosis(manif);
//		}
	}

}
