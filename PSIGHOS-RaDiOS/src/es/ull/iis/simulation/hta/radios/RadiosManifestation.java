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
			RadiosTransition transition = new RadiosTransition(repository, disease.getNullManifestation(), this, Boolean.FALSE); 
			ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(manifestationProbability);
			if (probabilityDistribution != null) {
				getRepository().addProbParam(disease.getNullManifestation(), this, Constants.CONSTANT_EMPTY_STRING, 
						probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValue());
			} else {
				double[][] datatableMatrix = ValueTransform.rangeDatatableToMatrix(XmlTransform.getDataTable(manifestationProbability));
				transition.setCalculator(transition.new AgeBasedTimeToEventCalculator(datatableMatrix, new RadiosRangeAgeMatrixRRCalculator(datatableMatrix)));
			}
			disease.addTransition(transition);
		} else if (CollectionUtils.notIsEmpty(getManifestation().getPrecedingManifestations())) {
			for (PrecedingManifestation precedingManifestation : getManifestation().getPrecedingManifestations()) {
				es.ull.iis.simulation.hta.progression.Manifestation precManif = searchManifestationFromDisease(disease, precedingManifestation.getName());				
				disease.addTransition(new RadiosTransition(repository, precManif, this, Boolean.FALSE));
				String transitionProbability = precedingManifestation.getProbability();
				if (transitionProbability != null) {
					ProbabilityDistribution probabilityDistributionForTransition = ValueTransform.splitProbabilityDistribution(transitionProbability);
					repository.addProbParam(precManif, this,	Constants.CONSTANT_EMPTY_STRING, probabilityDistributionForTransition.getDeterministicValue(), probabilityDistributionForTransition.getProbabilisticValue());
				}
			}
		}
	}

	private void addParamAnnualIncreaseMortalityRate() {
		if (getManifestation().getMortalityFactor() != null) {
			ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(getManifestation().getMortalityFactor());
			if (Type.CHRONIC == getType()) {
				// TODO: Se debe interpretar el valor como que aumenta tu riesgo de muerte * mortalityFactor
			} else if (Type.ACUTE == getType()) {
				// TODO: Se debe interpretar el valor de mortalityFactor como la probabilidad de muerte
			}
			if (probabilityDistribution != null) {
				getRepository().addIMRParam(this, "Mortality factor for " + this, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(),
						probabilityDistribution.getProbabilisticValue());
			}
		}
	}

	/**
	 * TODO: es necesario tener en cuenta las utilidades que pueden ser UTILITY o DISUTILITY y su comportamiento temporal ser: ANNUAL, LIFETIME y ONETIME
	 */
	private void addParamAnnualDisutility() {
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
			if (probabilityDistribution != null) {
				getRepository().addCostParam(this, "Cost for " + this, Constants.CONSTANT_EMPTY_STRING, year, probabilityDistribution.getDeterministicValue(),
						probabilityDistribution.getProbabilisticValue());
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
			addParamAnnualCosts();
			addParamAnnualDisutility();
			addParamAnnualIncreaseMortalityRate();
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
