/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

import es.ull.iis.simulation.hta.T1DM.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.T1DM.outcomes.SubmodelCostCalculator;
import es.ull.iis.simulation.hta.T1DM.outcomes.SubmodelUtilityCalculator;
import es.ull.iis.simulation.hta.T1DM.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.T1DM.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.T1DM.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.ChronicComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.DeathSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.EmpiricalSpainDeathSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.LyNPHSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.LySevereHypoglycemiaEvent;
import es.ull.iis.simulation.hta.T1DM.submodels.SheffieldRETSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.SimpleCHDSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.SimpleNEUSubmodel;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * A repository with data used to parameterize a model where:
 * <ul>
 * <li>Population: Spanish T1DM patients at risk of episodes of severe hypoglycemia (based on Conget et al. https://doi.org/10.1016/j.endinu.2018.03.008)
 * <ul>
 * <li>Age at start is defined constant (value = {@link #BASELINE_AGE_AVG}) if {@link BasicConfigParams#USE_FIXED_BASELINE_AGE} is set to <code>true</code>. 
 * Otherwise, is obtained according to the proportions defined in {@link #BASELINE_AGE_PROPORTIONS} for the age ranges defined in {@link #BASELINE_AGE_RANGES}</li>
 * <li>HbA1c level at start is defined constant (value = {@link #BASELINE_HBA1C_AVG}) if {@link BasicConfigParams#USE_FIXED_BASELINE_HBA1C} is set to <code>true</code>. Otherwise, it is set following
 *  a uniform distribution between {@link #BASELINE_HBA1C_MIN} and {@link #BASELINE_HBA1C_MAX}</li>
 * </ul>
 * </li>
 * 
 * <li>Interventions: SAP with predictive low-glucose management vs the standard insulin pump</li>
 * <li>Discount rate: 3%</li>
 * <li>Complications included in the model: Depending on the value of {@link BasicConfigParams#USE_SIMPLE_MODELS}, the model uses
 * the following submodels
 * <ul>
 * <li>Retinopathy: {@link SheffieldRETSubmodel}.</li>
 * <li>Nephropathy: {@link LyNPHSubmodel}.</li>
 * <li>Neuropathy: {@link SimpleNEUSubmodel}</li>
 * <li>Coronary heart disease: {@link SimpleCHDSubmodel}</li>
 * <li>Episode of severe hypoglycemia (acute event): {@link LySevereHypoglycemiaEvent}</li>
 * </ul></li>
 * <li>Costs calculated by using {@link SubmodelCostCalculator}</li>
 * <li>Utilities calculated by using {@link SubmodelUtilityCalculator}</li>
 * </ul>
 * @author Iván Castilla Rodríguez
 *
 */
public class LySecondOrderParams extends SecondOrderParamsRepository {
	/** Duration of effect of the intervention (supposed lifetime) */
	private static final double YEARS_OF_EFFECT = BasicConfigParams.MAX_AGE;

	/** Annual cost of the treatment with SAP. Based on microcosts from Medtronic. */
	private static final double C_SAP = 3484.56;
	/** Annual cost of the treatment with CSII. Based on microcosts from Medtronic */
	private static final double C_CSII = 377.38;//3013.335;
	
	/** Average and SD HbA1c in the population at baseline. Avg from https://doi.org/10.1016/j.endinu.2018.03.008; SD from core Model */
	private static final double[] BASELINE_HBA1C = {7.5, 0.25}; 
	/** The average age at baseline, according to https://doi.org/10.1016/j.endinu.2018.03.008 */
	private static final double[] BASELINE_AGE = {18.6, 11.79}; 
	/** Duration of diabetes at baseline, according to https://doi.org/10.1016/j.endinu.2018.03.008 */
	private static final double[] BASELINE_DURATION = {12.0, 8.74}; 
	
	/**
	 * Initializes the parameters for the population defined in this class. With respect to the cost of the treatments,
	 * we apply full costs independently of the adherence, by assuming that the NHS would continue providing the treatment
	 * even if not used.
	 */
	public LySecondOrderParams() {
		super();
		SheffieldRETSubmodel.registerSecondOrder(this);;
		LyNPHSubmodel.registerSecondOrder(this);
		SimpleCHDSubmodel.registerSecondOrder(this);
		SimpleNEUSubmodel.registerSecondOrder(this);

		// Acute complication submodels
		LySevereHypoglycemiaEvent.registerSecondOrder(this);

		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + STR_NO_COMPLICATIONS, "Cost of Diabetes with no complications", 
				BasicConfigParams.DEF_C_DNC.SOURCE, BasicConfigParams.DEF_C_DNC.YEAR, 
				BasicConfigParams.DEF_C_DNC.VALUE, getRandomVariateForCost(BasicConfigParams.DEF_C_DNC.VALUE)));
		
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + CSIIIntervention.NAME, "Annual cost of CSII", 
				"Own calculations from data provided by medtronic (see Parametros.xls)", 2018, C_CSII, SecondOrderParamsRepository.getRandomVariateForCost(C_CSII)));
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX +SAPIntervention.NAME, "Annual cost of SAP",  
				"Own calculations from data provided by medtronic (see Parametros.xls", 2018, C_SAP, SecondOrderParamsRepository.getRandomVariateForCost(C_SAP)));
		
		final double[] paramsDuDNC = SecondOrderParamsRepository.betaParametersFromNormal(BasicConfigParams.DEF_DU_DNC[0], BasicConfigParams.DEF_DU_DNC[1]);
		addUtilParam(new SecondOrderParam(STR_DISUTILITY_PREFIX + STR_NO_COMPLICATIONS, "Disutility of DNC", "", 
				BasicConfigParams.DEF_DU_DNC[0], "BetaVariate", paramsDuDNC[0], paramsDuDNC[1]));
		
		addOtherParam(new SecondOrderParam(STR_P_MAN, "Probability of sex = male", "https://doi.org/10.1016/j.endinu.2018.03.008", 0.49));
		addOtherParam(new SecondOrderParam(STR_DISCOUNT_RATE, "Discount rate", "Spanish guidelines", 0.03));
	}

	@Override
	public RandomVariate getBaselineHBA1c() {
		if (BasicConfigParams.USE_FIXED_BASELINE_HBA1C)
			return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_HBA1C[0]);
		return RandomVariateFactory.getInstance("NormalVariate", BASELINE_HBA1C[0], BASELINE_HBA1C[1]);
	}

	@Override
	public RandomVariate getBaselineAge() {
		if (BasicConfigParams.USE_FIXED_BASELINE_AGE)
			return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_AGE[0]);
		return RandomVariateFactory.getInstance("NormalVariate", BASELINE_AGE[0], BASELINE_AGE[1]);
	}

	@Override
	public RandomVariate getBaselineDurationOfDiabetes() {
		if (BasicConfigParams.USE_FIXED_BASELINE_DURATION_OF_DIABETES)
			return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_DURATION[0]);
		return RandomVariateFactory.getInstance("NormalVariate", BASELINE_DURATION[0], BASELINE_DURATION[1]);
	}
	
	@Override
	public T1DMMonitoringIntervention[] getInterventions() {
		return new T1DMMonitoringIntervention[] {
				new CSIIIntervention(0, costParams.get(STR_COST_PREFIX + CSIIIntervention.NAME).getValue(baseCase)),
				new SAPIntervention(1, costParams.get(STR_COST_PREFIX + SAPIntervention.NAME).getValue(baseCase), YEARS_OF_EFFECT)};
	}

	@Override
	public int getNInterventions() {
		return 2;
	}
	
	@Override
	public DeathSubmodel getDeathSubmodel() {
		final EmpiricalSpainDeathSubmodel dModel = new EmpiricalSpainDeathSubmodel(getRngFirstOrder(), nPatients);
//		final StandardSpainDeathSubmodel dModel = new StandardSpainDeathSubmodel(getRngFirstOrder(), nPatients);

		dModel.addIMR(LyNPHSubmodel.ALB2, getIMR(LyNPHSubmodel.ALB2));
		dModel.addIMR(LyNPHSubmodel.ESRD, getIMR(LyNPHSubmodel.ESRD));			
		dModel.addIMR(SimpleNEUSubmodel.NEU, getIMR(SimpleNEUSubmodel.NEU));
		dModel.addIMR(SimpleNEUSubmodel.LEA, getIMR(SimpleNEUSubmodel.LEA));
		dModel.addIMR(SimpleCHDSubmodel.ANGINA, getIMR(T1DMChronicComplications.CHD));
		dModel.addIMR(SimpleCHDSubmodel.STROKE, getIMR(T1DMChronicComplications.CHD));
		dModel.addIMR(SimpleCHDSubmodel.HF, getIMR(T1DMChronicComplications.CHD));
		dModel.addIMR(SimpleCHDSubmodel.MI, getIMR(T1DMChronicComplications.CHD));
		return dModel;
	}
	
	@Override
	public ChronicComplicationSubmodel[] getComplicationSubmodels() {
		final ChronicComplicationSubmodel[] comps = new ChronicComplicationSubmodel[T1DMChronicComplications.values().length];
		
		// Adds neuropathy submodel
		comps[T1DMChronicComplications.NEU.ordinal()] = new SimpleNEUSubmodel(this);
		
		// Adds nephropathy and retinopathy submodels
		comps[T1DMChronicComplications.NPH.ordinal()] = new LyNPHSubmodel(this);
		comps[T1DMChronicComplications.RET.ordinal()] = new SheffieldRETSubmodel(this);
		
		// Adds major Cardiovascular disease submodel
		comps[T1DMChronicComplications.CHD.ordinal()] = new SimpleCHDSubmodel(this);
		
		return comps;
	}
	
	@Override
	public AcuteComplicationSubmodel[] getAcuteComplicationSubmodels() {
		return new AcuteComplicationSubmodel[] {new LySevereHypoglycemiaEvent(this)};
	}
	
	@Override
	public CostCalculator getCostCalculator(double cDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelCostCalculator(cDNC, submodels, acuteSubmodels);
	}
	
	@Override
	public UtilityCalculator getUtilityCalculator(double duDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelUtilityCalculator(DisutilityCombinationMethod.MAX, duDNC, BasicConfigParams.DEF_U_GENERAL_POP, submodels, acuteSubmodels);
	}

	/**
	 * An intervention that represents the usual pump treatment used in Spain. Assumes no changes in the HbA1c level of the 
	 * patient during the simulation
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public class CSIIIntervention extends T1DMMonitoringIntervention {
		public final static String NAME = "CSII";
		/** Annual cost of the intervention */
		private final double annualCost;

		/**
		 * Creates the intervention
		 * @param id Unique identifier of the intervention
		 * @param annualCost Annual cost assigned to the intervention
		 */
		protected CSIIIntervention(int id, double annualCost) {
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

		@Override
		public double getDisutility(T1DMPatient pat) {
			return 0.035;
		}
	}

	/**
	 * An intervention with SAP with predictive low-glucose management. For this population, it has no effect on the 
	 * HbA1c level.
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public class SAPIntervention extends T1DMMonitoringIntervention {
		public final static String NAME = "SAP";
		/** Annual cost of the intervention */
		private final double annualCost;

		/**
		 * Creates the intervention
		 * @param id Unique identifier of the intervention
		 * @param annualCost Annual cost assigned to the intervention
		 * @param yearsOfEffect Years of effect of the intervention
		 */
		public SAPIntervention(int id, double annualCost, double yearsOfEffect) {
			super(id, NAME, NAME, yearsOfEffect);
			this.annualCost = annualCost;
		}

		@Override
		public double getHBA1cLevel(T1DMPatient pat) {
			return pat.getBaselineHBA1c();
//			return 2.206 + 1.491 + (0.618*pat.getBaselineHBA1c()) - (0.150 * Math.max(0, pat.getWeeklySensorUsage() - MIN_WEEKLY_USAGE)) - (0.005*pat.getAge());
		}

		@Override
		public double getAnnualCost(T1DMPatient pat) {
			return annualCost;
		}

		@Override
		public double getDisutility(T1DMPatient pat) {
			return -0.038;
		}
	}

}
