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
import es.ull.iis.simulation.hta.radios.transforms.ValueTransform;
import es.ull.iis.simulation.hta.radios.transforms.XmlTransform;
import es.ull.iis.simulation.hta.radios.wrappers.ProbabilityDistribution;

/**
 * @author David Prieto Gonz�lez
 */
public class RadiosManifestation extends es.ull.iis.simulation.hta.progression.Manifestation {
	private Manifestation manifestation;

	/**
	 * @param secParams
	 * @param name
	 * @param description
	 * @param disease
	 * @param type
	 * @throws JAXBException
	 */
	public RadiosManifestation(SecondOrderParamsRepository repository, Disease disease, Manifestation manifestation) throws JAXBException {
		super(repository, manifestation.getName(), Constants.CONSTANT_EMPTY_STRING, disease, manifestation.getKind() != null ? Type.valueOf(manifestation.getKind()) : Type.ACUTE);
		setManifestation(manifestation);
	}

	private es.ull.iis.simulation.hta.progression.Manifestation searchManifestationFromDisease(Disease disease, String manifestationName) {
		for (es.ull.iis.simulation.hta.progression.Manifestation manifestation : disease.getManifestations()) {
			if (manifestationName.equals(manifestation.name())) {
				return manifestation;
			}
		}
		return null;
	}

	private void addParamProbabilities(SecondOrderParamsRepository repository, Disease disease) throws JAXBException {
		String manifestationProbability = getManifestation().getProbability();
		if (manifestationProbability != null) {
			Boolean replacePrevious = Type.CHRONIC == getType() ? true : false;
			RadiosTransition transition = new RadiosTransition(repository, disease.getNullManifestation(), this, replacePrevious); 
			ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(manifestationProbability);
			if (probabilityDistribution != null) {
				getRepository().addProbParam(disease.getNullManifestation(), this, Constants.CONSTANT_EMPTY_STRING, 
						probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForProbability());
			} else {
				double[][] datatableMatrix = ValueTransform.rangeDatatableToMatrix(XmlTransform.getDataTable(manifestationProbability));
				transition.setCalculator(transition.new AgeBasedTimeToEventCalculator(datatableMatrix, new RadiosRangeAgeMatrixRRCalculator(datatableMatrix)));
			}
			disease.addTransition(transition);
		}
		if (CollectionUtils.notIsEmpty(getManifestation().getPrecedingManifestations())) {
			for (PrecedingManifestation precedingManifestation : getManifestation().getPrecedingManifestations()) {
				es.ull.iis.simulation.hta.progression.Manifestation precManif = searchManifestationFromDisease(disease, precedingManifestation.getName());				
				disease.addTransition(new RadiosTransition(repository, precManif, this, 
						(precedingManifestation.getReplacePrevious() != null && !precedingManifestation.getReplacePrevious().isEmpty()) ? Boolean.valueOf(precedingManifestation.getReplacePrevious()) : Boolean.FALSE));
				String transitionProbability = precedingManifestation.getProbability();
				if (transitionProbability != null) {
					ProbabilityDistribution probabilityDistributionForTransition = ValueTransform.splitProbabilityDistribution(transitionProbability);
					repository.addProbParam(precManif, this,	Constants.CONSTANT_EMPTY_STRING, probabilityDistributionForTransition.getDeterministicValue(), probabilityDistributionForTransition.getProbabilisticValueInitializedForProbability());
				}
			}
		}
	}

	private void addParamMortalityFactorOrProbability() {
		if (getManifestation().getMortalityFactor() != null) {
			ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(getManifestation().getMortalityFactor());
			if (probabilityDistribution != null) {
				if (Type.CHRONIC == getType()) { // Se debe interpretar el valor como que aumenta tu riesgo de muerte * mortalityFactor				
					getRepository().addIMRParam(this, "Mortality factor for " + this, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForProbability());
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
			String disutility = null;
			for (Utility utility : getManifestation().getUtilities()) {
				if (Constants.DATAPROPERTYVALUE_KIND_UTILITY_DISUTILITY.equals(utility.getKind()) && Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ANNUAL_VALUE.equals(utility.getTemporalBehavior())) {
					disutility = utility.getValue();
					break;
				}
			}
			ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(disutility);
			if (probabilityDistribution != null) { 
				getRepository().addDisutilityParam(this, "Utility for " + this, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(),
						probabilityDistribution.getProbabilisticValue());
			}
		}
	}

	private void addParamProbabilityDiagnosis() {
		if (getManifestation().getProbabilityOfLeadingToDiagnosis() != null) {
			ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(getManifestation().getProbabilityOfLeadingToDiagnosis());
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
							probabilityDistribution.getProbabilisticValueInitializedForProbability());
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
				addParamProbabilities(this.getRepository(), this.getDisease());
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
	public void setManifestation(es.ull.iis.ontology.radios.json.schema4simulation.Manifestation manifestation) {
		this.manifestation = manifestation;
	}

	/**
	 * @return
	 */
	public es.ull.iis.ontology.radios.json.schema4simulation.Manifestation getManifestation() {
		return manifestation;
	}

	/**
	 * @return
	 */
	private SecondOrderParamsRepository getRepository() {
		return secParams;
	}
}
