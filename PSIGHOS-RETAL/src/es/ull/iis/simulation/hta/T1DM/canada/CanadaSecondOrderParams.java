/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.canada;

import es.ull.iis.simulation.hta.T1DM.T1DMAcuteComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMChronicComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.T1DM.outcomes.SubmodelCostCalculator;
import es.ull.iis.simulation.hta.T1DM.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.T1DM.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.ChronicComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.DeathSubmodel;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CanadaSecondOrderParams extends SecondOrderParamsRepository {
	private static final double P_MAN = 0.5;
	/** Duration of effect of the intervention */
	private static final double YEARS_OF_EFFECT = 1.0;
	private static final double DISCOUNT_RATE = 0.015; 
	
	private static final double C_DNC = 2262;

	protected static final double U_DNC = 0.814;


	/**
	 * @param baseCase
	 */
	public CanadaSecondOrderParams() {
		super();
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

		addOtherParam(new SecondOrderParam(STR_P_MAN, "Probability of havig sex = male", "Assumption", P_MAN));
		addOtherParam(new SecondOrderParam(STR_DISCOUNT_RATE, "Discount rate", "Spanish guidelines", DISCOUNT_RATE));
		
	}

	@Override
	public RandomVariate getBaselineHBA1c() {
		return RandomVariateFactory.getInstance("ConstantVariate", 8.8);
	}

	@Override
	public RandomVariate getBaselineAge() {
		return RandomVariateFactory.getInstance("ConstantVariate", 27);
	}

	@Override
	public RandomVariate getBaselineDurationOfDiabetes() {
		return RandomVariateFactory.getInstance("ConstantVariate", 0.0);
	}
	

	@Override
	public T1DMMonitoringIntervention[] getInterventions() {
		return new T1DMMonitoringIntervention[] {new CanadaIntervention1(0), new CanadaIntervention2(1)};
	}

	@Override
	public int getNInterventions() {
		return 2;
	}

	@Override
	public ChronicComplicationSubmodel[] getComplicationSubmodels() {
		final ChronicComplicationSubmodel[] comps = new ChronicComplicationSubmodel[T1DMChronicComplications.values().length];
		
		// Adds nephropathy submodel
		comps[T1DMChronicComplications.NPH.ordinal()] = new CanadaNPHSubmodel(this);
		
		// Adds neuropathy submodel
		comps[T1DMChronicComplications.NEU.ordinal()] = new CanadaNEUSubmodel(this);
		
		// Adds retinopathy submodel
		comps[T1DMChronicComplications.RET.ordinal()] = new CanadaRETSubmodel(this);
		
		// Adds major Cardiovascular disease submodel
		comps[T1DMChronicComplications.CHD.ordinal()] = new CanadaCHDSubmodel(this);
		
		return comps;
	}

	@Override
	public AcuteComplicationSubmodel[] getAcuteComplicationSubmodels() {
		return new AcuteComplicationSubmodel[] {new CanadaSevereHypoglycemiaEvent(this)};
	}

	@Override
	public DeathSubmodel getDeathSubmodel() {
		return new CanadaDeathSubmodel(nPatients);
	}

	@Override
	public CostCalculator getCostCalculator(double cDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelCostCalculator(cDNC, submodels, acuteSubmodels);
	}
	
	@Override
	public UtilityCalculator getUtilityCalculator(double duDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new CanadaUtilityCalculator(duDNC, BasicConfigParams.DEF_U_GENERAL_POP, acuteSubmodels[T1DMAcuteComplications.SEVERE_HYPO.ordinal()].getDisutility(null));
	}
	
	
	public class CanadaIntervention1 extends T1DMMonitoringIntervention {
		public final static String NAME = "SMBG plus multiple daily injections";
		/**
		 * @param id
		 * @param shortName
		 * @param description
		 */
		public CanadaIntervention1(int id) {
			super(id, NAME, NAME, BasicConfigParams.MAX_AGE);
		}

		/* (non-Javadoc)
		 * @see es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention#getHBA1cLevel(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
		 */
		@Override
		public double getHBA1cLevel(T1DMPatient pat) {
			return 8.3;
		}

		/* (non-Javadoc)
		 * @see es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention#getAnnualCost(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
		 */
		@Override
		public double getAnnualCost(T1DMPatient pat) {
			return 3677;
		}
	}

	public class CanadaIntervention2 extends T1DMMonitoringIntervention {
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
		public double getHBA1cLevel(T1DMPatient pat) {
			return pat.isEffectActive() ? 7.3 : 8.3;
		}

		/* (non-Javadoc)
		 * @see es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention#getAnnualCost(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
		 */
		@Override
		public double getAnnualCost(T1DMPatient pat) {
			return 9211;
		}
	}
	
}
