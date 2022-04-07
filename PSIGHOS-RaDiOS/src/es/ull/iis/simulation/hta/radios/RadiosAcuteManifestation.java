package es.ull.iis.simulation.hta.radios;

import java.util.GregorianCalendar;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;

import es.ull.iis.ontology.radios.Constants;
import es.ull.iis.ontology.radios.json.schema4simulation.Cost;
import es.ull.iis.ontology.radios.json.schema4simulation.Manifestation;
import es.ull.iis.ontology.radios.json.schema4simulation.PrecedingManifestation;
import es.ull.iis.ontology.radios.json.schema4simulation.Utility;
import es.ull.iis.ontology.radios.utils.CollectionUtils;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.StagedDisease;
import es.ull.iis.simulation.hta.radios.transforms.ValueTransform;
import es.ull.iis.simulation.hta.radios.transforms.XmlTransform;
import es.ull.iis.simulation.hta.radios.wrappers.ProbabilityDistribution;

/**
 * @author David Prieto Gonzalez
 */
public class RadiosAcuteManifestation extends es.ull.iis.simulation.hta.progression.AcuteManifestation implements RadiosManifestation {
	private Manifestation manifestation;

	/**
	 * @param secParams
	 * @param name
	 * @param description
	 * @param disease
	 * @param type
	 * @throws JAXBException
	 */
	public RadiosAcuteManifestation(SecondOrderParamsRepository repository, Disease disease, Manifestation manifestation) throws JAXBException {
		super(repository, manifestation.getName(), Constants.CONSTANT_EMPTY_STRING, disease, 
						ValueTransform.toDoubleValue(manifestation.getOnSetAge()), ValueTransform.toDoubleValue(manifestation.getEndAge()));
		setManifestation(manifestation);
	}
	
	/**
	 * @param disease
	 * @param manifestationName
	 * @return
	 */
	private es.ull.iis.simulation.hta.progression.Manifestation searchManifestationFromDisease(StagedDisease disease, String manifestationName) {
		for (es.ull.iis.simulation.hta.progression.Manifestation manifestation : disease.getManifestations()) {
			if (manifestationName.equals(manifestation.name())) {
				return manifestation;
			}
		}
		return null;
	}

	/**
	 * @param repository
	 * @param disease
	 * @throws JAXBException
	 */
	private void addParamProbabilities(SecondOrderParamsRepository repository, StagedDisease disease) throws JAXBException {
		String manifestationProbability = getManifestation().getProbability();
		if (manifestationProbability != null) {
			RadiosTransition transition = new RadiosTransition(repository, disease.getAsymptomaticManifestation(), this); 
			ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(manifestationProbability);
			if (probabilityDistribution != null) {
				getRepository().addProbParam(disease.getAsymptomaticManifestation(), this, Constants.CONSTANT_EMPTY_STRING, 
						probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForProbability());
			} else {
				Object[][] datatableMatrix = ValueTransform.rangeDatatableToMatrix(XmlTransform.getDataTable(manifestationProbability), repository);
				transition.setCalculator(transition.new AgeBasedTimeToEventCalculator(datatableMatrix, new RadiosRangeAgeMatrixRRCalculator(datatableMatrix)));
			}
			disease.addTransition(transition);
		}

		if (CollectionUtils.notIsEmpty(getManifestation().getPrecedingManifestations())) {
			for (PrecedingManifestation precedingManifestation : getManifestation().getPrecedingManifestations()) {
				es.ull.iis.simulation.hta.progression.Manifestation precManif = searchManifestationFromDisease(disease, precedingManifestation.getName());				
				String transitionProbability = precedingManifestation.getProbability();
				if (transitionProbability != null) {
					RadiosTransition transition = new RadiosTransition(repository, precManif, this);
					ProbabilityDistribution probabilityDistributionForTransition = ValueTransform.splitProbabilityDistribution(transitionProbability);
					if (probabilityDistributionForTransition != null) {
						repository.addProbParam(precManif, this,	Constants.CONSTANT_EMPTY_STRING, probabilityDistributionForTransition.getDeterministicValue(), probabilityDistributionForTransition.getProbabilisticValueInitializedForProbability());
					} else {
						Object[][] datatableMatrix = ValueTransform.rangeDatatableToMatrix(XmlTransform.getDataTable(transitionProbability), repository);
						transition.setCalculator(transition.new AgeBasedTimeToEventCalculator(datatableMatrix, new RadiosRangeAgeMatrixRRCalculator(datatableMatrix)));
					}
					disease.addTransition(transition);
				}
			}
		}
	}

	/**
	 * 
	 */
	private void addParamMortalityFactorOrProbability() {
		if (getManifestation().getMortalityFactor() != null) {
			ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(getManifestation().getMortalityFactor());
			if (probabilityDistribution != null) {
				if (Type.CHRONIC == getType()) { // Se debe interpretar el valor como que aumenta tu riesgo de muerte * mortalityFactor
					if (probabilityDistribution.getDeterministicValue() > 0) {
						getRepository().addIMRParam(this, "Mortality factor for " + this, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForProbability());
					} else {
						getRepository().addLERParam(this, "Life expectancy reduction for " + this, Constants.CONSTANT_EMPTY_STRING, Math.abs(probabilityDistribution.getDeterministicValue()), probabilityDistribution.getProbabilisticValueInitializedForProbability());
					}
				} else if (Type.ACUTE == getType()) { // Se debe interpretar el valor de mortalityFactor como la probabilidad de muerte				
					getRepository().addDeathProbParam(this, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForProbability());
				}
			}
		}
	}

	/**
	 * TODO: es necesario tener en cuenta las utilidades que pueden ser UTILITY o DISUTILITY y su comportamiento temporal ser: ANNUAL, LIFETIME y ONETIME
	 */
	private void addParamDisutility() {
		if (CollectionUtils.notIsEmpty(getManifestation().getUtilities())) {
			String value = null;
			Boolean isDisutility = false;
			for (Utility utility : getManifestation().getUtilities()) {
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
					getRepository().addDisutilityParam(this, "Disutility for " + this, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValue());
				} else {
					getRepository().addUtilityParam(this, "Utility for " + this, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValue());
				}
			}
		}
	}

	/**
	 * 
	 */
	private void addParamProbabilityDiagnosis() {
		if (getManifestation().getProbabilityOfDiagnosis() != null) {
			ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(getManifestation().getProbabilityOfDiagnosis());
			if (probabilityDistribution != null) {
				getRepository().addDiagnosisProbParam(this, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForProbability());
			}
		}
	}

	/**
	 * TODO: es necesario tener en cuenta los costos de los tipos: ANNUAL, LIFETIME y ONETIME
	 */
	private void addParamCosts() {
		if (CollectionUtils.notIsEmpty(getManifestation().getCosts())) {
			String annualCost = null;
			String onetimeCost = null;
			Integer yearAnnualCost = null;
			Integer yearOnetimeCost = null;
			for (Cost cost : getManifestation().getCosts()) {
				if (annualCost == null && Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ANNUAL_VALUE.equals(cost.getTemporalBehavior())) {
					annualCost = cost.getAmount();
					yearAnnualCost = !StringUtils.isEmpty(cost.getYear()) ? new Integer(cost.getYear()) : (new GregorianCalendar()).get(GregorianCalendar.YEAR);
				} else if (onetimeCost == null && Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ONETIME_VALUE.equals(cost.getTemporalBehavior())) {
					onetimeCost = cost.getAmount();
					yearOnetimeCost = !StringUtils.isEmpty(cost.getYear()) ? new Integer(cost.getYear()) : (new GregorianCalendar()).get(GregorianCalendar.YEAR);
				}
			}		
			
			if (annualCost != null) {
				ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(annualCost);
				if (probabilityDistribution != null) {
					getRepository().addCostParam(this, "Cost for " + this, Constants.CONSTANT_EMPTY_STRING, yearAnnualCost, 
							probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForCost());
				}
			}
			
			if (onetimeCost != null) {
				ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(onetimeCost);
				if (probabilityDistribution != null) {
					getRepository().addTransitionCostParam(this, "Punctual cost for " + this, Constants.CONSTANT_EMPTY_STRING, yearOnetimeCost, probabilityDistribution.getDeterministicValue(),
							probabilityDistribution.getProbabilisticValueInitializedForCost());
				}
			}
		}
	}

	/**
	 * 
	 */
	public void addParametersToRepository () {
		if (getManifestation() != null) {
			try {
				addParamProbabilities(this.getRepository(), (StagedDisease)this.getDisease());
			} catch (JAXBException e) {
				e.printStackTrace();
			}
			addParamCosts();
			addParamMortalityFactorOrProbability();
			addParamDisutility();
			addParamProbabilityDiagnosis();
		}
	}
	
	@Override
	public void registerSecondOrderParameters() {
	}
	
	/**
	 * @param manifestation
	 */
	private void setManifestation(es.ull.iis.ontology.radios.json.schema4simulation.Manifestation manifestation) {
		this.manifestation = manifestation;
	}

	/**
	 * @return
	 */
	private es.ull.iis.ontology.radios.json.schema4simulation.Manifestation getManifestation() {
		return manifestation;
	}

	/**
	 * @return
	 */
	private SecondOrderParamsRepository getRepository() {
		return secParams;
	}
}
