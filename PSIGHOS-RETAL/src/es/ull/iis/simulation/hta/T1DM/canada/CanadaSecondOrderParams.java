/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.canada;

import es.ull.iis.simulation.hta.T1DM.ComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.DeathSubmodel;
import es.ull.iis.simulation.hta.T1DM.MainComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
import es.ull.iis.simulation.hta.T1DM.params.ComplicationRR;
import es.ull.iis.simulation.hta.T1DM.params.CostCalculator;
import es.ull.iis.simulation.hta.T1DM.params.InterventionSpecificComplicationRR;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.T1DM.params.SubmodelCostCalculator;
import es.ull.iis.simulation.hta.T1DM.params.UtilityCalculator;
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
	
	private static final double C_HYPO_EPISODE = 3755;
	private static final double C_DNC = 2262;

	public static final double U_GENERAL_POP = 1.0;
	private static final double DU_HYPO_EPISODE = 0.0206; // From Canada
	public static final double DU_DNC = U_GENERAL_POP - 0.814;


	/**
	 * @param baseCase
	 */
	public CanadaSecondOrderParams() {
		super();
		
		CanadaRETSubmodel.registerSecondOrder(this);
		CanadaCHDSubmodel.registerSecondOrder(this);
		CanadaNPHSubmodel.registerSecondOrder(this);
		CanadaNEUSubmodel.registerSecondOrder(this);
		
		// Severe hypoglycemic episodes
		final double[] paramsHypo = betaParametersFromNormal(0.0982, sdFrom95CI(new double[]{0.0526, 0.1513}));
		final double[] paramsDeathHypo = betaParametersFromNormal(0.0063, sdFrom95CI(new double[]{0.0058, 0.0068}));
		addProbParam(new SecondOrderParam(STR_P_HYPO, "Annual probability of severe hypoglycemic episode", "Canada", 0.0982, RandomVariateFactory.getInstance("BetaVariate", paramsHypo[0], paramsHypo[1])));
		addProbParam(new SecondOrderParam(STR_P_DEATH_HYPO, "Probability of death after severe hypoglycemic episode", "Canada", 0.0063, RandomVariateFactory.getInstance("BetaVariate", paramsDeathHypo[0], paramsDeathHypo[1])));
		addOtherParam(new SecondOrderParam(STR_RR_HYPO, "Relative risk of severe hypoglycemic event in intervention branch", "Canada", 0.869, RandomVariateFactory.getInstance("RRFromLnCIVariate", 0.869, 0.476, 1.586, 1)));

//		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + MainComplications.CHD.name(), STR_RR_PREFIX + MainComplications.CHD.name(), "", RR_CHD, RandomVariateFactory.getInstance("ConstantVariate", RR_CHD)));
//		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + MainComplications.NPH.name(), STR_RR_PREFIX + MainComplications.NPH.name(), "", RR_NPH, RandomVariateFactory.getInstance("ConstantVariate", RR_NPH)));
//		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + MainComplications.NEU.name(), STR_RR_PREFIX + MainComplications.NEU.name(), "", RR_NEU, RandomVariateFactory.getInstance("ConstantVariate", RR_NEU)));
//		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + MainComplications.RET.name(), STR_RR_PREFIX + MainComplications.RET.name(), "", RR_RET, RandomVariateFactory.getInstance("ConstantVariate", RR_RET)));

		addCostParam(new SecondOrderCostParam(STR_COST_HYPO_EPISODE, "Cost of a severe hypoglycemic episode", "HTA Canada", 2018, C_HYPO_EPISODE));
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + STR_NO_COMPLICATIONS, "Cost of DNC", "HTA Canada", 2018, C_DNC));

		addUtilParam(new SecondOrderParam(STR_U_GENERAL_POPULATION, "Utility of general population", "", U_GENERAL_POP));
		addUtilParam(new SecondOrderParam(STR_DU_HYPO_EVENT, "Disutility of severe hypoglycemic episode", "", DU_HYPO_EPISODE));
		addUtilParam(new SecondOrderParam(STR_DISUTILITY_PREFIX + STR_NO_COMPLICATIONS, "Disutility of DNC", "", DU_DNC));

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
	public T1DMMonitoringIntervention[] getInterventions() {
		return new T1DMMonitoringIntervention[] {new CanadaIntervention1(0), new CanadaIntervention2(1)};
	}

	@Override
	public int getNInterventions() {
		return 2;
	}

	@Override
	public ComplicationRR getHypoRR() {
		final double[] rrValues = new double[getNInterventions()];
		rrValues[0] = 1.0;
		final SecondOrderParam param = otherParams.get(STR_RR_HYPO);
		final double rr = (param == null) ? 1.0 : param.getValue(baseCase);
		for (int i = 1; i < getNInterventions(); i++) {
			rrValues[i] = rr;
		}
		return new InterventionSpecificComplicationRR(rrValues);
	}

	@Override
	public ComplicationSubmodel[] getComplicationSubmodels() {
		final ComplicationSubmodel[] comps = new ComplicationSubmodel[MainComplications.values().length];
		
		// Adds nephropathy submodel
		comps[MainComplications.NPH.ordinal()] = new CanadaNPHSubmodel(this);
		
		// Adds neuropathy submodel
		comps[MainComplications.NEU.ordinal()] = new CanadaNEUSubmodel(this);
		
		// Adds retinopathy submodel
		comps[MainComplications.RET.ordinal()] = new CanadaRETSubmodel(this);
		
		// Adds major Cardiovascular disease submodel
		comps[MainComplications.CHD.ordinal()] = new CanadaCHDSubmodel(this);
		
		return comps;
	}

	@Override
	public DeathSubmodel getDeathSubmodel() {
		return new CanadaDeathSubmodel(nPatients);
	}

	@Override
	public CostCalculator getCostCalculator(ComplicationSubmodel[] submodels) {
		return new SubmodelCostCalculator(getAnnualNoComplicationCost(), getCostForSevereHypoglycemicEpisode(), submodels);
	}

	@Override
	public UtilityCalculator getUtilityCalculator(ComplicationSubmodel[] submodels) {
		return new CanadaUtilityCalculator(getNoComplicationDisutility(), getGeneralPopulationUtility(), getHypoEventDisutility());
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
