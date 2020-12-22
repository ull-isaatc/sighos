package es.ull.iis.simulation.hta.radios;

import java.util.GregorianCalendar;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;

import es.ull.iis.ontology.radios.Constants;
import es.ull.iis.ontology.radios.json.schema4simulation.Cost;
import es.ull.iis.ontology.radios.json.schema4simulation.Manifestation;
import es.ull.iis.ontology.radios.json.schema4simulation.Utility;
import es.ull.iis.ontology.radios.utils.CollectionUtils;
import es.ull.iis.ontology.radios.xml.datatables.Datatable;
import es.ull.iis.simulation.hta.params.AgeBasedTimeToEventParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.radios.transforms.ValueTransform;
import es.ull.iis.simulation.hta.radios.transforms.XmlTransform;
import es.ull.iis.simulation.hta.radios.wrappers.ProbabilityDistribution;
import simkit.random.RandomNumberFactory;

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
		if (getManifestation() != null) {
			addParamProbability();
			addParamAnnualCosts();
			addParamAnnualDisutility();
			addParamAnnualIncreaseMortalityRate();
		}
	}

	public void setManifestation(es.ull.iis.ontology.radios.json.schema4simulation.Manifestation manifestation) {
		this.manifestation = manifestation;
	}
	
	public es.ull.iis.ontology.radios.json.schema4simulation.Manifestation getManifestation() {
		return manifestation;
	}
	
	private SecondOrderParamsRepository getRepository () {
		return secParams;
	}
	
	private void addParamProbability() throws JAXBException {
		String manifestationProbability = getManifestation().getProbability();
		ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(manifestationProbability);
		if (probabilityDistribution != null) {
			getRepository().addProbParam(this, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValue());
		} else {
			Datatable datatable = XmlTransform.getDataTable(manifestationProbability);
			AgeBasedTimeToEventParam ageBasedTimeToEventParam = 
					new AgeBasedTimeToEventParam(RandomNumberFactory.getInstance(), datatable.getPopulation().intValue(), ValueTransform.rangeDatatableToMatrix(datatable), 
							new RadiosRangeAgeMatrixRRCalculator(ValueTransform.rangeDatatableToMatrix(datatable)));
			//getRepository().addProbParam(ageBasedTimeToEventParam);			
		}
	}

	private void addParamAnnualIncreaseMortalityRate() {
		if (getManifestation().getMortalityFactor() != null) {			
			ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(getManifestation().getMortalityFactor());
			if (Type.CHRONIC == getType()) {
				// Se debe interpretar el valor como que aumenta tu riesgo de muerte * mortalityFactor 
			} else if (Type.ACUTE == getType()) {
				// Se debe interpretar el valor de mortalityFactor como la probabilidad de muerte
			}
			if (probabilityDistribution != null) {
				getRepository().addIMRParam(this, "Mortality factor for " + this, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValue());
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
				if (Constants.DATAPROPERTYVALUE_KIND_UTILITY_DISUTILITY.equals(utility.getKind()) && 
						Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ANNUAL_VALUE.equals(utility.getTemporalBehavior())) {
					disutility = utility.getValue();
					break;
				}
			}
			ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(disutility);
			if (probabilityDistribution != null) {
				getRepository().addDisutilityParam(this, "Utility for " + this, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValue());
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
				getRepository().addCostParam(this, "Cost for " + this, Constants.CONSTANT_EMPTY_STRING, year, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValue());
			}
		}
	}

	@Override
	public void registerSecondOrderParameters() {
		// FIXME: en el caso de RaDiOS este método no tiene sentido
	}
}
