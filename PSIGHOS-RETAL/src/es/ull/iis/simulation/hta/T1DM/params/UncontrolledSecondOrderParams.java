/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.ComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.DeathSubmodel;
import es.ull.iis.simulation.hta.T1DM.MainComplications;
import es.ull.iis.simulation.hta.T1DM.SheffieldNPHSubmodel;
import es.ull.iis.simulation.hta.T1DM.SheffieldRETSubmodel;
import es.ull.iis.simulation.hta.T1DM.SimpleCHDSubmodel;
import es.ull.iis.simulation.hta.T1DM.SimpleNEUSubmodel;
import es.ull.iis.simulation.hta.T1DM.SimpleNPHSubmodel;
import es.ull.iis.simulation.hta.T1DM.SimpleRETSubmodel;
import es.ull.iis.simulation.hta.T1DM.StandardSpainDeathSubmodel;
import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.params.UtilityCalculator.DisutilityCombinationMethod;
import simkit.random.RandomNumber;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class UncontrolledSecondOrderParams extends SecondOrderParamsRepository {
	/** Duration of effect of the intervention (the same duration of Battelino 2012) */
	private static final double YEARS_OF_EFFECT = 0.5;
	private static final String STR_LOW_USE_PERCENTAGE = "LOW_USE_PERCENTAGE";
	
	// Parameters from Battelino 2012
	private static final double BASELINE_HBA1C_MIN = 7.5; // Battelino 2012
	private static final double BASELINE_HBA1C_MAX = 9.5; // Battelino 2012
	private static final double BASELINE_HBA1C_AVG = 8.5; // Battelino 2012
//	private static final double BASELINE_HBA1C_SD = 0.6; // Battelino 2012: Not used because it leads to a BETA(1,1), which, in fact, is like using a uniform
	private static final int BASELINE_AGE_MIN = 6; // Battelino 2012
	private static final int BASELINE_AGE_MAX = 70; // Battelino 2012
	private static final int BASELINE_AGE_AVG = 28; // Battelino 2012
	private static final int BASELINE_AGE_SD = 17; // Battelino 2012
	private static final double LOW_USAGE_PERCENTAGE_AVG = 0.8; // Battelino 2012
	/** Number of patients with < 70% [0] and >= 70% [1] usage of the sensor */
	private static final double[] LOW_USAGE_PERCENTAGE_N = new double[] {43, 110};  
	/** Average HbA1c reduction after 6 months for patients with < 70% [0] and >= 70% [1] usage of the sensor */
	private static final double[] HBA1C_REDUCTION_AVG = {0.24, 0.51};
	// TODO: Incorporate first order variability in the reduction
//	/** Variability in the HbA1c reduction after 6 months for patients with < 70% [0] and >= 70% [1] usage of the sensor, expresed as +- average */
//	private static final double[] HBA1C_REDUCTION_PLUS_MINUS = {1.11, 0.07};
	
	private static final double C_DNC = 2174.11;
	private static final double C_SAP = 7662.205833;
	private static final double C_CSII = 3013.335;

	private static final double P_HYPO = 0.0408163;
	private static final double[] P_HYPO_BETA = {3.94576334, 142.05423666};
	private static final double[] RR_HYPO_BETA = {0.68627430, 0.60401244};
	private static final double DU_HYPO_EPISODE = 0.0206; // From Canada
	private static final double DU_DNC = 0.0351;
	
	/**
	 * @param baseCase
	 */
	public UncontrolledSecondOrderParams() {
		super();
		BasicConfigParams.MIN_AGE = BASELINE_AGE_MIN;
		
		if (BasicConfigParams.USE_SIMPLE_MODELS) {
			SimpleRETSubmodel.registerSecondOrder(this);;
			SimpleNPHSubmodel.registerSecondOrder(this);
		}
		else {
			SheffieldRETSubmodel.registerSecondOrder(this);;
			SheffieldNPHSubmodel.registerSecondOrder(this);
		}
		SimpleCHDSubmodel.registerSecondOrder(this);
		SimpleNEUSubmodel.registerSecondOrder(this);


		// Severe hypoglycemic episodes
		final double[] paramsDeathHypo = betaParametersFromNormal(0.0063, sdFrom95CI(new double[]{0.0058, 0.0068}));
		addProbParam(new SecondOrderParam(STR_P_HYPO, "Annual probability of severe hypoglycemic episode (adjusted from n events of both arms; only events from injection therapy in probabilistic)", 
				"Battelino 2012", P_HYPO, RandomVariateFactory.getInstance("BetaVariate", P_HYPO_BETA[0], P_HYPO_BETA[1])));
		addProbParam(new SecondOrderParam(STR_P_DEATH_HYPO, "Probability of death after severe hypoglycemic episode", "Canada", 0.0063, RandomVariateFactory.getInstance("BetaVariate", paramsDeathHypo[0], paramsDeathHypo[1])));
		addOtherParam(new SecondOrderParam(STR_RR_HYPO, "Relative risk of severe hypoglycemic events (adjusted from rate/100 patient-month). Assumed 1 at base case; using difference at probabilistic", 
				"Battelino 2012", 1.0, RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", RR_HYPO_BETA[0], RR_HYPO_BETA[1]))));

		addCostParam(new SecondOrderCostParam(STR_COST_HYPO_EPISODE, "Cost of a severe hypoglycemic episode", "https://doi.org/10.1007/s13300-017-0285-0", 2017, 716.82, getRandomVariateForCost(716.82)));
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + STR_NO_COMPLICATIONS, "Cost of DNC", "", 2015, C_DNC, getRandomVariateForCost(C_DNC)));
		
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + CSIIIntervention.NAME, "Annual cost of CSII", 
				"Own calculations from data provided by medtronic (see Parametros.xls", 2018, C_CSII, SecondOrderParamsRepository.getRandomVariateForCost(C_CSII)));
		// REVISAR: Asumimos coste completo, incluso aunque no haya adherencia, ya que el SNS se los seguiría facilitando igualmente
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX +SAPIntervention.NAME, "Annual cost of SAP",  
				"Own calculations from data provided by medtronic (see Parametros.xls", 2018, C_SAP, SecondOrderParamsRepository.getRandomVariateForCost(C_SAP)));

		addUtilParam(new SecondOrderParam(STR_DU_HYPO_EVENT, "Disutility of severe hypoglycemic episode", "", DU_HYPO_EPISODE));
		addUtilParam(new SecondOrderParam(STR_DISUTILITY_PREFIX + STR_NO_COMPLICATIONS, "Disutility of DNC", "", DU_DNC));
		
		addOtherParam(new SecondOrderParam(STR_P_MAN, "Probability of sex = male", "https://doi.org/10.1016/j.endinu.2018.03.008", 0.5));
		addOtherParam(new SecondOrderParam(STR_DISCOUNT_RATE, "Discount rate", "Spanish guidelines", 0.03));
		addOtherParam(new SecondOrderParam(STR_LOW_USE_PERCENTAGE, "Percentage of patients with low use of the sensor", 
				"Battelino 2012", LOW_USAGE_PERCENTAGE_AVG, RandomVariateFactory.getInstance("BetaVariate", LOW_USAGE_PERCENTAGE_N[0], LOW_USAGE_PERCENTAGE_N[1]) ));
	}

	@Override
	public RandomVariate getBaselineHBA1c() {
		if (BasicConfigParams.USE_FIXED_BASELINE_HBA1C)
			return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_HBA1C_AVG);
		return RandomVariateFactory.getInstance("UniformVariate", BASELINE_HBA1C_MIN, BASELINE_HBA1C_MAX);
	}

	@Override
	public RandomVariate getBaselineAge() {
		if (BasicConfigParams.USE_FIXED_BASELINE_AGE)
			return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_AGE_AVG);
		final double[] initBetaParams = betaParametersFromNormal(BASELINE_AGE_AVG, BASELINE_AGE_SD);
		// k is used to simplify the operations
		final double k = ((initBetaParams[0] + initBetaParams[1])*(initBetaParams[0] + initBetaParams[1]))/initBetaParams[1];
		final double variance = BASELINE_AGE_SD * BASELINE_AGE_SD;
		final double mode = variance * k * (initBetaParams[0] - 1) / (initBetaParams[0] - 3 * variance * k);
		final double[] betaParams = betaParametersFromEmpiricData(BASELINE_AGE_AVG, mode, BASELINE_AGE_MIN, BASELINE_AGE_MAX);
		final RandomVariate rnd = RandomVariateFactory.getInstance("BetaVariate", betaParams[0], betaParams[1]); 
		return RandomVariateFactory.getInstance("ScaledVariate", rnd, BASELINE_AGE_MAX - BASELINE_AGE_MIN, BASELINE_AGE_MIN);
	}

	@Override
	public T1DMMonitoringIntervention[] getInterventions() {
		return new T1DMMonitoringIntervention[] {new CSIIIntervention(0, costParams.get(STR_COST_PREFIX + CSIIIntervention.NAME).getValue(baseCase)),
				new SAPIntervention(1, costParams.get(STR_COST_PREFIX + SAPIntervention.NAME).getValue(baseCase), 
						otherParams.get(STR_LOW_USE_PERCENTAGE).getValue(baseCase), YEARS_OF_EFFECT)};
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

		if (BasicConfigParams.USE_SIMPLE_MODELS) {
			dModel.addIMR(SimpleNPHSubmodel.NPH, getIMR(SimpleNPHSubmodel.NPH));
			dModel.addIMR(SimpleNPHSubmodel.ESRD, getIMR(SimpleNPHSubmodel.ESRD));
		}
		else {
			dModel.addIMR(SheffieldNPHSubmodel.ALB2, getIMR(SheffieldNPHSubmodel.ALB2));
			dModel.addIMR(SheffieldNPHSubmodel.ESRD, getIMR(SheffieldNPHSubmodel.ESRD));			
		}
		dModel.addIMR(SimpleNEUSubmodel.NEU, getIMR(SimpleNEUSubmodel.NEU));
		dModel.addIMR(SimpleNEUSubmodel.LEA, getIMR(SimpleNEUSubmodel.LEA));
		dModel.addIMR(SimpleCHDSubmodel.ANGINA, getIMR(MainComplications.CHD));
		dModel.addIMR(SimpleCHDSubmodel.STROKE, getIMR(MainComplications.CHD));
		dModel.addIMR(SimpleCHDSubmodel.HF, getIMR(MainComplications.CHD));
		dModel.addIMR(SimpleCHDSubmodel.MI, getIMR(MainComplications.CHD));
		return dModel;
	}
	
	@Override
	public ComplicationSubmodel[] getComplicationSubmodels() {
		final ComplicationSubmodel[] comps = new ComplicationSubmodel[MainComplications.values().length];
		
		// Adds neuropathy submodel
		comps[MainComplications.NEU.ordinal()] = new SimpleNEUSubmodel(this);
		
		// Adds nephropathy and retinopathy submodels
		if (BasicConfigParams.USE_SIMPLE_MODELS) {
			comps[MainComplications.NPH.ordinal()] = new SimpleNPHSubmodel(this);
			comps[MainComplications.RET.ordinal()] = new SimpleRETSubmodel(this);
		}
		else {
			comps[MainComplications.NPH.ordinal()] = new SheffieldNPHSubmodel(this);
			comps[MainComplications.RET.ordinal()] = new SheffieldRETSubmodel(this);
		}
		
		// Adds major Cardiovascular disease submodel
		comps[MainComplications.CHD.ordinal()] = new SimpleCHDSubmodel(this);
		
		return comps;
	}
	
	@Override
	public CostCalculator getCostCalculator(ComplicationSubmodel[] submodels) {
		return new SubmodelCostCalculator(getAnnualNoComplicationCost(), getCostForSevereHypoglycemicEpisode(), submodels);
	}
	
	@Override
	public UtilityCalculator getUtilityCalculator(ComplicationSubmodel[] submodels) {
		return new SubmodelUtilityCalculator(DisutilityCombinationMethod.ADD, getNoComplicationDisutility(), BasicConfigParams.U_GENERAL_POP, getHypoEventDisutility(), submodels);
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
		}

		@Override
		public double getAnnualCost(T1DMPatient pat) {
			return annualCost;
		}

	}

	public class SAPIntervention extends T1DMMonitoringIntervention {
		public final static String NAME = "SAP";
		private final double annualCost;
		private final double[] hba1cReduction;
		/**
		 * @param id
		 * @param shortName
		 * @param description
		 */
		public SAPIntervention(int id, double annualCost, double lowUsePercentage, double yearsOfEffect) {
			super(id, NAME, NAME, yearsOfEffect);
			this.annualCost = annualCost;
			this.hba1cReduction = new double[nPatients];
			final RandomNumber rnd = getRngFirstOrder();
			for (int i = 0; i < nPatients; i++) {
				hba1cReduction[i] = (rnd.draw() < lowUsePercentage) ? HBA1C_REDUCTION_AVG[0] : HBA1C_REDUCTION_AVG[1]; 
			}
		}

		@Override
		public double getHBA1cLevel(T1DMPatient pat) {
			return pat.getBaselineHBA1c() - (pat.isEffectActive() ? hba1cReduction[pat.getIdentifier()] : 0);
		}

		@Override
		public double getAnnualCost(T1DMPatient pat) {
			return annualCost;
		}

	}
	
}
