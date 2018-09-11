/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.MainChronicComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.params.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.T1DM.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.BattelinoSevereHypoglycemiaEvent;
import es.ull.iis.simulation.hta.T1DM.submodels.ChronicComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.DeathSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.SheffieldNPHSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.SheffieldRETSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.SimpleCHDSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.SimpleNEUSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.SimpleNPHSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.SimpleRETSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.StandardSpainDeathSubmodel;
import simkit.random.RandomNumber;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class UncontrolledSecondOrderParams extends SecondOrderParamsRepository {
	/** Duration of effect of the intervention (the same duration of Battelino 2012) */
	private static final double YEARS_OF_EFFECT = BasicConfigParams.MAX_AGE;
	/** A factor to reduce the cost of SAP in sensitivity analysis */
	private static final double C_SAP_REDUCTION = 1.0;

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
	private static final double LOW_USAGE_PERCENTAGE_AVG = 110 / 153; // Battelino 2012
	/** Number of patients with < 70% [0] and >= 70% [1] usage of the sensor */
	private static final double[] LOW_USAGE_PERCENTAGE_N = new double[] {43, 110};  
	/** Average HbA1c reduction after 6 months for patients with < 70% [0] and >= 70% [1] usage of the sensor */
	private static final double[] HBA1C_REDUCTION_AVG = {0.24, 0.51};
	// TODO: Incorporate first order variability in the reduction
//	/** Variability in the HbA1c reduction after 6 months for patients with < 70% [0] and >= 70% [1] usage of the sensor, expresed as +- average */
//	private static final double[] HBA1C_REDUCTION_PLUS_MINUS = {1.11, 0.07};
	
	private static final double C_SAP = 7662.205833 * C_SAP_REDUCTION;
	private static final double C_CSII = 3013.335;

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

		BattelinoSevereHypoglycemiaEvent.registerSecondOrder(this);

		// Severe hypoglycemic episodes
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + STR_NO_COMPLICATIONS, "Cost of Diabetes with no complications", 
				BasicConfigParams.DEF_C_DNC.SOURCE, BasicConfigParams.DEF_C_DNC.YEAR, 
				BasicConfigParams.DEF_C_DNC.VALUE, getRandomVariateForCost(BasicConfigParams.DEF_C_DNC.VALUE)));
		
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + CSIIIntervention.NAME, "Annual cost of CSII", 
				"Own calculations from data provided by medtronic (see Parametros.xls", 2018, C_CSII, SecondOrderParamsRepository.getRandomVariateForCost(C_CSII)));
		// REVISAR: Asumimos coste completo, incluso aunque no haya adherencia, ya que el SNS se los seguiría facilitando igualmente
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX +SAPIntervention.NAME, "Annual cost of SAP",  
				"Own calculations from data provided by medtronic (see Parametros.xls", 2018, C_SAP, SecondOrderParamsRepository.getRandomVariateForCost(C_SAP)));

		final double[] paramsDuDNC = SecondOrderParamsRepository.betaParametersFromNormal(BasicConfigParams.DEF_DU_DNC[0], BasicConfigParams.DEF_DU_DNC[1]);
		addUtilParam(new SecondOrderParam(STR_DISUTILITY_PREFIX + STR_NO_COMPLICATIONS, "Disutility of DNC", "", 
				BasicConfigParams.DEF_DU_DNC[0], "BetaVariate", paramsDuDNC[0], paramsDuDNC[1]));
		
		
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
		dModel.addIMR(SimpleCHDSubmodel.ANGINA, getIMR(MainChronicComplications.CHD));
		dModel.addIMR(SimpleCHDSubmodel.STROKE, getIMR(MainChronicComplications.CHD));
		dModel.addIMR(SimpleCHDSubmodel.HF, getIMR(MainChronicComplications.CHD));
		dModel.addIMR(SimpleCHDSubmodel.MI, getIMR(MainChronicComplications.CHD));
		return dModel;
	}
	
	@Override
	public ChronicComplicationSubmodel[] getComplicationSubmodels() {
		final ChronicComplicationSubmodel[] comps = new ChronicComplicationSubmodel[MainChronicComplications.values().length];
		
		// Adds neuropathy submodel
		comps[MainChronicComplications.NEU.ordinal()] = new SimpleNEUSubmodel(this);
		
		// Adds nephropathy and retinopathy submodels
		if (BasicConfigParams.USE_SIMPLE_MODELS) {
			comps[MainChronicComplications.NPH.ordinal()] = new SimpleNPHSubmodel(this);
			comps[MainChronicComplications.RET.ordinal()] = new SimpleRETSubmodel(this);
		}
		else {
			comps[MainChronicComplications.NPH.ordinal()] = new SheffieldNPHSubmodel(this);
			comps[MainChronicComplications.RET.ordinal()] = new SheffieldRETSubmodel(this);
		}
		
		// Adds major Cardiovascular disease submodel
		comps[MainChronicComplications.CHD.ordinal()] = new SimpleCHDSubmodel(this);
		
		return comps;
	}
	
	@Override
	public AcuteComplicationSubmodel[] getAcuteComplicationSubmodels() {
		return new AcuteComplicationSubmodel[] {new BattelinoSevereHypoglycemiaEvent(this)};
	}
	
	@Override
	public CostCalculator getCostCalculator(double cDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelCostCalculator(cDNC, submodels, acuteSubmodels);
	}
	
	@Override
	public UtilityCalculator getUtilityCalculator(double duDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelUtilityCalculator(DisutilityCombinationMethod.ADD, duDNC, BasicConfigParams.DEF_U_GENERAL_POP, submodels, acuteSubmodels);
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
