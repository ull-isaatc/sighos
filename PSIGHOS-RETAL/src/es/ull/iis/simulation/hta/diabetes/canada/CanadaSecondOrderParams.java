/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.canada;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesPatientGenerator.DiabetesPatientGenerationInfo;
import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.interventions.DiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.SubmodelCostCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.ChronicComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.DeathSubmodel;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CanadaSecondOrderParams extends SecondOrderParamsRepository {
	/** Duration of effect of the intervention */
	private static final double YEARS_OF_EFFECT = 1.0;
	private static final double DISCOUNT_RATE = 0.015; 
	
	private static final double C_DNC = 2262;

	protected static final double U_DNC = 0.814;


	/**
	 * @param nPatients Number of patients to create
	 */
	public CanadaSecondOrderParams(int nPatients) {
		super(nPatients);
		addPopulation(new DiabetesPatientGenerationInfo(new CanadaPopulation(this)));
		BasicConfigParams.DEF_U_GENERAL_POP = 1.0;
		
		CanadaRETSubmodel.registerSecondOrder(this);
		CanadaCHDSubmodel.registerSecondOrder(this);
		CanadaNPHSubmodel.registerSecondOrder(this);
		CanadaNEUSubmodel.registerSecondOrder(this);
		
		CanadaSevereHypoglycemiaEvent.registerSecondOrder(this);
//		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + MainChronicComplications.CHD.name(), STR_RR_PREFIX + MainChronicComplications.CHD.name(), "", RR_CHD, RandomVariateFactory.getInstance("ConstantVariate", RR_CHD)));
//		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + MainChronicComplications.NPH.name(), STR_RR_PREFIX + MainChronicComplications.NPH.name(), "", RR_NPH, RandomVariateFactory.getInstance("ConstantVariate", RR_NPH)));
//		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + MainChronicComplications.NEU.name(), STR_RR_PREFIX + MainChronicComplications.NEU.name(), "", RR_NEU, RandomVariateFactory.getInstance("ConstantVariate", RR_NEU)));
//		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + MainChronicComplications.RET.name(), STR_RR_PREFIX + MainChronicComplications.RET.name(), "", RR_RET, RandomVariateFactory.getInstance("ConstantVariate", RR_RET)));

		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + STR_NO_COMPLICATIONS, "Cost of DNC", "HTA Canada", 2018, C_DNC));

		addUtilParam(new SecondOrderParam(STR_DISUTILITY_PREFIX + STR_NO_COMPLICATIONS, "Disutility of DNC", "", BasicConfigParams.DEF_U_GENERAL_POP - U_DNC));

		addOtherParam(new SecondOrderParam(STR_DISCOUNT_RATE, "Discount rate", "Spanish guidelines", DISCOUNT_RATE));
		
	}

	@Override
	public DiabetesIntervention[] getInterventions() {
		return new DiabetesIntervention[] {new CanadaIntervention1(0), new CanadaIntervention2(1)};
	}

	@Override
	public int getNInterventions() {
		return 2;
	}

	@Override
	public ChronicComplicationSubmodel[] getComplicationSubmodels() {
		final ChronicComplicationSubmodel[] comps = new ChronicComplicationSubmodel[DiabetesChronicComplications.values().length];
		
		// Adds nephropathy submodel
		comps[DiabetesChronicComplications.NPH.ordinal()] = new CanadaNPHSubmodel(this);
		
		// Adds neuropathy submodel
		comps[DiabetesChronicComplications.NEU.ordinal()] = new CanadaNEUSubmodel(this);
		
		// Adds retinopathy submodel
		comps[DiabetesChronicComplications.RET.ordinal()] = new CanadaRETSubmodel(this);
		
		// Adds major Cardiovascular disease submodel
		comps[DiabetesChronicComplications.CHD.ordinal()] = new CanadaCHDSubmodel(this);
		
		return comps;
	}

	@Override
	public AcuteComplicationSubmodel[] getAcuteComplicationSubmodels() {
		return new AcuteComplicationSubmodel[] {new CanadaSevereHypoglycemiaEvent(this)};
	}

	@Override
	public DeathSubmodel getDeathSubmodel() {
		return new CanadaDeathSubmodel(this);
	}

	@Override
	public CostCalculator getCostCalculator(double cDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelCostCalculator(cDNC, submodels, acuteSubmodels);
	}
	
	@Override
	public UtilityCalculator getUtilityCalculator(double duDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new CanadaUtilityCalculator(duDNC, BasicConfigParams.DEF_U_GENERAL_POP, acuteSubmodels[DiabetesAcuteComplications.SEVERE_HYPO.ordinal()].getDisutility(null));
	}
	
	
	public class CanadaIntervention1 extends DiabetesIntervention {
		public final static String NAME = "SMBG plus multiple daily injections";
		/**
		 * @param id
		 * @param shortName
		 * @param description
		 */
		public CanadaIntervention1(int id) {
			super(id, NAME, NAME, BasicConfigParams.DEF_MAX_AGE);
		}

		/* (non-Javadoc)
		 * @see es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention#getHBA1cLevel(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
		 */
		@Override
		public double getHBA1cLevel(DiabetesPatient pat) {
			return 8.3;
		}

		/* (non-Javadoc)
		 * @see es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention#getAnnualCost(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
		 */
		@Override
		public double getAnnualCost(DiabetesPatient pat) {
			return 3677;
		}
	}

	public class CanadaIntervention2 extends DiabetesIntervention {
		public final static String NAME = "Sensor-augmented pump";
		/**
		 * @param id
		 * @param shortName
		 * @param description
		 */
		public CanadaIntervention2(int id) {
			super(id, NAME, NAME, YEARS_OF_EFFECT);
		}

		/* (non-Javadoc)
		 * @see es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention#getHBA1cLevel(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
		 */
		@Override
		public double getHBA1cLevel(DiabetesPatient pat) {
			return pat.isEffectActive() ? 7.3 : 8.3;
		}

		/* (non-Javadoc)
		 * @see es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention#getAnnualCost(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
		 */
		@Override
		public double getAnnualCost(DiabetesPatient pat) {
			return 9211;
		}
	}

}
