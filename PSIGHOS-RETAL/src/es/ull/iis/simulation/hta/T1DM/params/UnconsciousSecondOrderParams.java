/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.ComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.DeathSubmodel;
import es.ull.iis.simulation.hta.T1DM.MainComplications;
import es.ull.iis.simulation.hta.T1DM.SheffieldRETSubmodel;
import es.ull.iis.simulation.hta.T1DM.SimpleCHDSubmodel;
import es.ull.iis.simulation.hta.T1DM.SimpleNEUSubmodel;
import es.ull.iis.simulation.hta.T1DM.SimpleNPHSubmodel;
import es.ull.iis.simulation.hta.T1DM.SimpleRETSubmodel;
import es.ull.iis.simulation.hta.T1DM.StandardSpainDeathSubmodel;
import es.ull.iis.simulation.hta.T1DM.T1DMComorbidity;
import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.params.UtilityCalculator.CombinationMethod;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class UnconsciousSecondOrderParams extends SecondOrderParamsRepository {
//	public enum Population {
//		UNCONTROLLED(27, 8.3, 1.0, "https://doi.org/10.1056/NEJMoa1002853"),
//		UNCONSCIOUS(18.6, 7.5, 0.0, "https://doi.org/10.1016/j.endinu.2018.03.008");
//		
//		private Population(double baselineAge, double baselineHbA1c, double hba1cAfterIntervention, String source) {
//		}
//	}
	/** Duration of effect of the intervention (supposed as in Canada) */
	private static final double YEARS_OF_EFFECT = 1.0;
	private static final String STR_SENSOR_ADHERENCE_LOWER_LIMIT = "SENSOR_ADHERENCE LOWER LIMIT";
	private static final String STR_SENSOR_ADHERENCE_UPPER_LIMIT = "SENSOR_ADHERENCE UPPER LIMIT";
	private static final String STR_AVG_HBA1C_AFTER = "AVG_HBA1C_AFTER_";

	private static final double C_DNC = 2174.11;
	private static final double U_GENERAL_POP = 0.911400915;
	private static final double DU_HYPO_EPISODE = 0.0206; // From Canada
	private static final double DU_DNC = 0.0351;

	private final boolean useSimpleModels;
	
	/**
	 * @param baseCase
	 */
	public UnconsciousSecondOrderParams(boolean baseCase, int nPatients, boolean useSimpleModels) {
		super(baseCase, nPatients);
		this.useSimpleModels = useSimpleModels; 
		if (useSimpleModels) {
			SimpleRETSubmodel.registerSecondOrder(this);;
		}
		else {
			SheffieldRETSubmodel.registerSecondOrder(this);;
		}
		SimpleCHDSubmodel.registerSecondOrder(this);
		SimpleNPHSubmodel.registerSecondOrder(this);
		SimpleNEUSubmodel.registerSecondOrder(this);


		// Severe hypoglycemic episodes
		final double[] paramsDeathHypo = betaParametersFromNormal(0.0063, sdFrom95CI(new double[]{0.0058, 0.0068}));
		addProbParam(new SecondOrderParam(STR_P_HYPO, "Annual probability of severe hypoglycemic episode (adjusted from rate/100 patient-month)", "Ly et al.", 0.234286582, RandomVariateFactory.getInstance("BetaVariate", 23.19437163, 75.80562837)));
		addProbParam(new SecondOrderParam(STR_P_DEATH_HYPO, "Probability of death after severe hypoglycemic episode", "Canada", 0.0063, RandomVariateFactory.getInstance("BetaVariate", paramsDeathHypo[0], paramsDeathHypo[1])));
		addOtherParam(new SecondOrderParam(STR_RR_HYPO, "Relative risk of severe hypoglycemic event in intervention branch (adjusted from rate/100 patient-month)", "Ly et al.", 0.020895447, RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", -3.868224010, 1.421931924))));

		addCostParam(new SecondOrderCostParam(STR_COST_HYPO_EPISODE, "Cost of a severe hypoglycemic episode", "https://doi.org/10.1007/s13300-017-0285-0", 2017, 716.82, getRandomVariateForCost(716.82)));
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + STR_NO_COMPLICATIONS, "Cost of DNC", "", 2015, C_DNC, getRandomVariateForCost(C_DNC)));
		
		// FIXME: Costes de Canada (en dolares canadienses)
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + CSIIIntervention.NAME, "Cost of " + CSIIIntervention.NAME, "HTA Canada", 2018, 6817));
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX +SAPIntervention.NAME, "Cost of " + SAPIntervention.NAME, "HTA Canada", 2018, 9211));

		addUtilParam(new SecondOrderParam(STR_U_GENERAL_POPULATION, "Utility of general population", "", U_GENERAL_POP));
		addUtilParam(new SecondOrderParam(STR_DU_HYPO_EVENT, "Disutility of severe hypoglycemic episode", "", DU_HYPO_EPISODE));
		addUtilParam(new SecondOrderParam(STR_DISUTILITY_PREFIX + STR_NO_COMPLICATIONS, "Disutility of DNC", "", DU_DNC));
		
		addOtherParam(new SecondOrderParam(STR_P_MAN, "Probability of sex = male", "https://doi.org/10.1016/j.endinu.2018.03.008", 0.5));
		addOtherParam(new SecondOrderParam(STR_DISCOUNT_RATE, "Discount rate", "Spanish guidelines", 0.03));
		addOtherParam(new SecondOrderParam(STR_AVG_BASELINE_AGE, "Average baseline age", "https://doi.org/10.1016/j.endinu.2018.03.008",18.6));
		addOtherParam(new SecondOrderParam(STR_AVG_BASELINE_HBA1C, "Average baseline level of HBA1c", "https://doi.org/10.1016/j.endinu.2018.03.008", 7.5));
		addOtherParam(new SecondOrderParam(STR_AVG_HBA1C_AFTER + SAPIntervention.NAME, "Average level of HBA1c after " + SAPIntervention.NAME, "https://doi.org/10.1016/j.endinu.2018.03.008", 7.5));
		addOtherParam(new SecondOrderParam(STR_SENSOR_ADHERENCE_LOWER_LIMIT, "Lower limit for weekly sensor adherence", "", 5));
		addOtherParam(new SecondOrderParam(STR_SENSOR_ADHERENCE_UPPER_LIMIT, "Upper limit for weekly sensor adherence", "", 7));
	}

	@Override
	public RandomVariate getBaselineHBA1c() {
		return RandomVariateFactory.getInstance("ConstantVariate", otherParams.get(STR_AVG_BASELINE_HBA1C).getValue(baseCase));
	}

	@Override
	public RandomVariate getBaselineAge() {
		return RandomVariateFactory.getInstance("ConstantVariate", otherParams.get(STR_AVG_BASELINE_AGE).getValue(baseCase));
	}

	@Override
	public RandomVariate getWeeklySensorUsage() {
		return RandomVariateFactory.getInstance("UniformVariate", otherParams.get(STR_SENSOR_ADHERENCE_LOWER_LIMIT).getValue(baseCase), otherParams.get(STR_SENSOR_ADHERENCE_UPPER_LIMIT).getValue(baseCase));
	}

	@Override
	public T1DMMonitoringIntervention[] getInterventions() {
		return new T1DMMonitoringIntervention[] {new CSIIIntervention(0, costParams.get(STR_COST_PREFIX + CSIIIntervention.NAME).getValue(baseCase)),
				new SAPIntervention(1, costParams.get(STR_COST_PREFIX + SAPIntervention.NAME).getValue(baseCase), 
						otherParams.get(STR_AVG_HBA1C_AFTER + SAPIntervention.NAME).getValue(baseCase), YEARS_OF_EFFECT)};
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
	public DeathSubmodel getDeathSubmodel() {
		final StandardSpainDeathSubmodel dModel = new StandardSpainDeathSubmodel(getRngFirstOrder(), nPatients);

		dModel.addIMR(SimpleNEUSubmodel.NEU, getIMR(SimpleNEUSubmodel.NEU));
		dModel.addIMR(SimpleNEUSubmodel.LEA, getIMR(SimpleNEUSubmodel.LEA));
		dModel.addIMR(SimpleNPHSubmodel.NPH, getIMR(SimpleNPHSubmodel.NPH));
		dModel.addIMR(SimpleNPHSubmodel.ESRD, getIMR(SimpleNPHSubmodel.ESRD));
		dModel.addIMR(SimpleCHDSubmodel.ANGINA, getIMR(MainComplications.CHD));
		dModel.addIMR(SimpleCHDSubmodel.STROKE, getIMR(MainComplications.CHD));
		dModel.addIMR(SimpleCHDSubmodel.HF, getIMR(MainComplications.CHD));
		dModel.addIMR(SimpleCHDSubmodel.MI, getIMR(MainComplications.CHD));
		return dModel;
	}
	
	@Override
	public ComplicationSubmodel[] getComplicationSubmodels() {
		final ComplicationSubmodel[] comps = new ComplicationSubmodel[MainComplications.values().length];
		
		// Adds nephropathy submodel
		comps[MainComplications.NPH.ordinal()] = new SimpleNPHSubmodel(this);
		
		// Adds neuropathy submodel
		comps[MainComplications.NEU.ordinal()] = new SimpleNEUSubmodel(this);
		
		// Adds retinopathy submodel
		if (useSimpleModels) {
			comps[MainComplications.RET.ordinal()] = new SimpleRETSubmodel(this);
		}
		else {
			comps[MainComplications.RET.ordinal()] = new SheffieldRETSubmodel(this);
		}
		
		// Adds major Cardiovascular disease submodel
		comps[MainComplications.CHD.ordinal()] = new SimpleCHDSubmodel(this);
		
		return comps;
	}
	
	@Override
	public CostCalculator getCostCalculator() {
		final AdjustedCostCalculator calc = new AdjustedCostCalculator(getAnnualNoComplicationCost(), getCostForSevereHypoglycemicEpisode(), useSimpleModels);
		for (final T1DMComorbidity subst : availableHealthStates) {
			final double[] costs = getCostsForHealthState(subst);
			calc.addCostForHealthState(subst, costs);
		}
		return calc;
	}
	
	@Override
	public UtilityCalculator getUtilityCalculator() {
		final StdUtilityCalculator calc = new StdUtilityCalculator(CombinationMethod.ADD, getNoComplicationDisutility(), getGeneralPopulationUtility(), getHypoEventDisutility());
		for (final T1DMComorbidity subst : availableHealthStates) {
			calc.addDisutilityForHealthState(subst, getDisutilityForHealthState(subst));
		}
		return calc;
	}
	

	public class CSIIIntervention extends T1DMMonitoringIntervention {
		public final static String NAME = "CSII";
		private final double annualCost;
		/**
		 * @param id
		 * @param shortName
		 * @param description
		 */
		public CSIIIntervention(int id, double annualCost) {
			super(id, NAME, NAME, BasicConfigParams.MAX_AGE);
			this.annualCost = annualCost;
		}

		@Override
		public double getHBA1cLevel(T1DMPatient pat) {
			return pat.getBaselineHBA1c();
//			return 2.206 + (0.744*pat.getBaselineHBA1c()) - (0.003*pat.getAge()); // https://www.ncbi.nlm.nih.gov/pmc/articles/PMC3131116/
		}

		@Override
		public double getAnnualCost(T1DMPatient pat) {
			return annualCost;
		}

	}

	public class SAPIntervention extends T1DMMonitoringIntervention {
		public final static String NAME = "SAP";
		private final double annualCost;
		private final double hba1cAfterIntervention;
		/**
		 * @param id
		 * @param shortName
		 * @param description
		 */
		public SAPIntervention(int id, double annualCost, double hba1cAfterIntervention, double yearsOfEffect) {
			super(id, NAME, NAME, yearsOfEffect);
			this.annualCost = annualCost;
			this.hba1cAfterIntervention = hba1cAfterIntervention;
		}

		@Override
		public double getHBA1cLevel(T1DMPatient pat) {
			return pat.isEffectActive() ? hba1cAfterIntervention : pat.getBaselineHBA1c();
//			return 2.206 + 1.491 + (0.618*pat.getBaselineHBA1c()) - (0.150 * Math.max(0, pat.getWeeklySensorUsage() - MIN_WEEKLY_USAGE)) - (0.005*pat.getAge());
		}

		@Override
		public double getAnnualCost(T1DMPatient pat) {
			return annualCost;
		}

	}
	
}
