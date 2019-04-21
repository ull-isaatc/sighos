/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.interventions;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
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
public class UncontrolledSAPIntervention extends DiabetesIntervention {
	/** Average HbA1c reduction after 6 months for patients with < 70% [0] and >= 70% [1] usage of the sensor */
	private static final double[] HBA1C_REDUCTION_AVG = {0.24, 0.51};
	/** Variability in the HbA1c reduction after 6 months for patients with < 70% [0] and >= 70% [1] usage of the sensor */
	private static final double[] HBA1C_REDUCTION_SD = {1.11, 0.07};
	/** Minimum HbA1c level after the intervention has effect. Assumption */
	private static final double HBA1C_AFTER_REDUCTION_MIN = 6.0; 
	public final static String NAME = "SAP";
	/** Annual cost of the intervention */
	private final double annualCost;
	/** HbA1c reduction applied to each patient */
	private final double[] hba1cReduction;

	/**
	 * Creates the intervention
	 * @param id Unique identifier of the intervention
	 * @param annualCost Annual cost assigned to the intervention
	 * @param lowUsePercentage Proportion of patients with low use of the intervention
	 * @param yearsOfEffect Years of effect of the intervention
	 */
	public UncontrolledSAPIntervention(int id, SecondOrderParamsRepository secParams, double annualCost, double lowUsePercentage, double yearsOfEffect) {
		super(id, NAME, NAME, yearsOfEffect);
		this.annualCost = annualCost;
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
