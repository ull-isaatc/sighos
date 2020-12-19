package es.ull.iis.simulation.hta.radios;

import java.util.GregorianCalendar;

import org.apache.commons.lang3.StringUtils;

import es.tenerife.ull.ontology.radios.Constants;
import es.tenerife.ull.ontology.radios.json.schema4simulation.Cost;
import es.tenerife.ull.ontology.radios.json.schema4simulation.Manifestation;
import es.tenerife.ull.ontology.radios.json.schema4simulation.Utility;
import es.tenerife.ull.ontology.radios.utils.CollectionUtils;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.radios.transforms.ValueTransform;
import es.ull.iis.simulation.hta.radios.wrappers.ProbabilityDistribution;

/**
 * @author David Prieto González
 */
public class RadiosManifestation extends es.ull.iis.simulation.hta.progression.Manifestation {
	private Manifestation manifestation;

	/**
	 * @param secParams
	 * @param name
	 * @param description
	 * @param disease
	 * @param type
	 */
	public RadiosManifestation(SecondOrderParamsRepository repository, Disease disease, Manifestation manifestation) {
		super(repository, manifestation.getName(), Constants.CONSTANT_EMPTY_STRING, disease, manifestation.getKind() != null ? Type.valueOf(manifestation.getKind()) : Type.ACUTE);

		setManifestation(manifestation);		
		if (getManifestation() != null) {
			addParamProbability();
			addParamAnnualCosts();
			addParamAnnualDisutility();
			addParamAnnualIncreaseMortalityRate();
		}
	}

	public void setManifestation(es.tenerife.ull.ontology.radios.json.schema4simulation.Manifestation manifestation) {
		this.manifestation = manifestation;
	}
	
	public es.tenerife.ull.ontology.radios.json.schema4simulation.Manifestation getManifestation() {
		return manifestation;
	}
	
	private SecondOrderParamsRepository getRepository () {
		return secParams;
	}
	
	private void addParamProbability() {
		ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(getManifestation().getProbability());
		getRepository().addProbParam(this, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValue());
	}

	private void addParamAnnualIncreaseMortalityRate() {
		if (getManifestation().getMortalityFactor() != null) {			
			ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(getManifestation().getMortalityFactor());
			if (Type.CHRONIC == getType()) {
				// Se debe interpretar el valor como que aumenta tu riesgo de muerte * mortalityFactor 
			} else if (Type.ACUTE == getType()) {
				// Se debe interpretar el valor de mortalityFactor como la probabilidad de muerte
			}
			getRepository().addIMRParam(this, "Mortality factor for " + this, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValue());
		}			 
	}

	/**
	 * TODO: es necesario tener en cuenta las utilidades que pueden ser UTILITY o DISUTILITY y su comportamiento temporal ser: ANNUAL, LIFETIME y ONETIME
	 */
	private void addParamAnnualDisutility() {
		if (CollectionUtils.notIsEmpty(getManifestation().getUtilities())) {
			String disutility = null;
			for (Utility utility : getManifestation().getUtilities()) {
				if (Constants.DATAPROPERTYVALUE_KIND_UTILITY_DISUTILITY.equals(utility.getKind()) && 
						Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ANNUAL_VALUE.equals(utility.getTemporalBehavior())) {
					disutility = utility.getValue();
					break;
				}
			}
			ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(disutility);
			getRepository().addDisutilityParam(this, "Utility for " + this, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValue());
		}			 
	}

	/**
	 * TODO: es necesario tener en cuenta los costos de los tipos: ANNUAL, LIFETIME y ONETIME
	 */
	private void addParamAnnualCosts() {
		if (CollectionUtils.notIsEmpty(getManifestation().getCosts())) {
			String annualCost = null;
			Integer year = null;
			for (Cost cost : getManifestation().getCosts()) {
				if (Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ANNUAL_VALUE.equals(cost.getTemporalBehavior())) {
					annualCost = cost.getAmount();
					year = !StringUtils.isEmpty(cost.getYear()) ? new Integer(cost.getYear()) : (new GregorianCalendar()).get(GregorianCalendar.YEAR);
					break;
				}
			}
			ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(annualCost);
			getRepository().addCostParam(this, "Cost for " + this, Constants.CONSTANT_EMPTY_STRING, year, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValue());
		}
	}

	@Override
	public void registerSecondOrderParameters() {
		// FIXME: en el caso de RaDiOS este método no tiene sentido
	}
}
