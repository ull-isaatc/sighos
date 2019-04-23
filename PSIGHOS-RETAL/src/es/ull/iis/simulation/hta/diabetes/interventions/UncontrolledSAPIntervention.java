/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.interventions;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import simkit.random.RandomNumber;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * An intervention with SAP with predictive low-glucose management. Applies an immediate reduction in HbA1c. The reduction
 * depends on whether the patient has low use of the device or not. If {@link BasicConfigParams#USE_FIXED_HBA1C_CHANGE} is <code>true</code>
 * applies a constant reduction of {@link #HBA1C_REDUCTION_AVG} to every patient; otherwise, the reduction is sampled from a
 * Gamma distribution with average {@link #HBA1C_REDUCTION_AVG} and standard deviation 
 * {@link #HBA1C_REDUCTION_SD}
 * @author Iván Castilla Rodríguez
 *
 */
public class UncontrolledSAPIntervention extends SecondOrderDiabetesIntervention {
	/** Average HbA1c reduction after 6 months for patients with < 70% [0] and >= 70% [1] usage of the sensor */
	private static final double[] HBA1C_REDUCTION_AVG = {0.24, 0.51};
	/** Variability in the HbA1c reduction after 6 months for patients with < 70% [0] and >= 70% [1] usage of the sensor */
	private static final double[] HBA1C_REDUCTION_SD = {1.11, 0.07};
	/** Minimum HbA1c level after the intervention has effect. Assumption */
	private static final double HBA1C_AFTER_REDUCTION_MIN = 6.0; 
	public final static String NAME = "SAP";
	/** A factor to reduce the cost of SAP in sensitivity analysis */
	private static final double C_SAP_REDUCTION = 1.0;
	/** Annual cost of the treatment with SAP. Based on microcosts from Medtronic. */
	private static final double C_SAP = 7662.205833 * C_SAP_REDUCTION;
	/** A string to define the percentage of low use of the new pump */
	private static final String STR_LOW_USE_PERCENTAGE = "LOW_USE_PERCENTAGE";

	/** Average proportion of patients with < 70% usage of the sensor. From Battelno 2012 */
	private static final double LOW_USAGE_PERCENTAGE_AVG = 43d / 153d; 
	/** Number of patients with < 70% [0] and >= 70% [1] usage of the sensor */
	private static final double[] LOW_USAGE_PERCENTAGE_N = new double[] {43, 110};  

	/** The duration (in years) of the effect of the intervention */
	final private double yearsOfEffect;

	/**
	 * Creates the intervention
	 * @param yearsOfEffect Years of effect of the intervention
	 */
	public UncontrolledSAPIntervention(double yearsOfEffect) {
		super(NAME, NAME);
		this.yearsOfEffect = yearsOfEffect;
	}

	/**
	 * Creates the intervention, and supposes lifetime effect.
	 */
	public UncontrolledSAPIntervention() {
		this(BasicConfigParams.DEF_MAX_AGE);
	}

	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
		// REVISAR: Asumimos coste completo, incluso aunque no haya adherencia, ya que el SNS se los seguiría facilitando igualmente
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + UncontrolledSAPIntervention.NAME, "Annual cost of SAP",  
				"Own calculations from data provided by medtronic (see Parametros.xls", 2018, C_SAP, SecondOrderParamsRepository.getRandomVariateForCost(C_SAP)));
		
		secParams.addOtherParam(new SecondOrderParam(STR_LOW_USE_PERCENTAGE, "Percentage of patients with low use of the sensor", 
				"Battelino 2012", LOW_USAGE_PERCENTAGE_AVG, RandomVariateFactory.getInstance("BetaVariate", LOW_USAGE_PERCENTAGE_N[0], LOW_USAGE_PERCENTAGE_N[1]) ));
	}
	
	@Override
	public DiabetesIntervention getInstance(int id, SecondOrderParamsRepository secParams) {
		return new Instance(id, secParams);
	}

	public class Instance extends DiabetesIntervention {
		/** Annual cost of the intervention */
		private final double annualCost;
		/** HbA1c reduction applied to each patient */
		private final double[] hba1cReduction;
		/** Proportion of patients with low use of the intervention */
		private final double lowUsePercentage;
		

		/**
		 * Creates the intervention
		 * @param id Unique identifier of the intervention
		 * @param lowUsePercentage 
		 */
		public Instance(int id, SecondOrderParamsRepository secParams) {
			super(id, yearsOfEffect);
			final double auxCost = secParams.getCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + UncontrolledSAPIntervention.NAME);
			this.annualCost = Double.isNaN(auxCost) ? 0.0 : auxCost;
			this.lowUsePercentage = secParams.getOtherParam(STR_LOW_USE_PERCENTAGE);
			this.hba1cReduction = new double[secParams.getnPatients()];
			final RandomNumber rnd = secParams.getRngFirstOrder();
			if (BasicConfigParams.USE_FIXED_HBA1C_CHANGE) {
				for (int i = 0; i < secParams.getnPatients(); i++) {
					hba1cReduction[i] = (rnd.draw() < lowUsePercentage) ? HBA1C_REDUCTION_AVG[0] : HBA1C_REDUCTION_AVG[1]; 
				}
			}
			else {
				final RandomVariate[]rndReduction = new RandomVariate[2];
				final double [] lowParams = SecondOrderParamsRepository.gammaParametersFromNormal(HBA1C_REDUCTION_AVG[0], HBA1C_REDUCTION_SD[0]);
				final double [] highParams = SecondOrderParamsRepository.gammaParametersFromNormal(HBA1C_REDUCTION_AVG[1], HBA1C_REDUCTION_SD[1]);
				rndReduction[0] = RandomVariateFactory.getInstance("GammaVariate", lowParams[0], lowParams[1]);
				rndReduction[1] = RandomVariateFactory.getInstance("GammaVariate", highParams[0], highParams[1]);
				for (int i = 0; i < secParams.getnPatients(); i++) {
					hba1cReduction[i] = (rnd.draw() < lowUsePercentage) ? rndReduction[0].generate() : rndReduction[1].generate(); 
				}
			}
		}

		@Override
		public double getHBA1cLevel(DiabetesPatient pat) {
			return Math.max(HBA1C_AFTER_REDUCTION_MIN, pat.getBaselineHBA1c() - (pat.isEffectActive() ? hba1cReduction[pat.getIdentifier()] : 0));
		}

		@Override
		public double getAnnualCost(DiabetesPatient pat) {
			return annualCost;
		}
	}
}
