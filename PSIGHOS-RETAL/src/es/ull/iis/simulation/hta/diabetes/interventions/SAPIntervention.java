package es.ull.iis.simulation.hta.diabetes.interventions;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;

/**
 * An intervention with SAP with predictive low-glucose management. For this population, it has no effect on the 
 * HbA1c level.
 * @author Iván Castilla Rodríguez
 *
 */
public class SAPIntervention extends DiabetesIntervention {
	public final static String NAME = "SAP";
	/** Annual cost of the intervention */
	private final double annualCost;
	/** Annual disutility due to the treatment */
	private final double disutility;

	/**
	 * Creates the intervention
	 * @param id Unique identifier of the intervention
	 * @param annualCost Annual cost assigned to the intervention
	 * @param yearsOfEffect Years of effect of the intervention
	 */
	public SAPIntervention(int id, double annualCost, double yearsOfEffect, double disutility) {
		super(id, NAME, NAME, yearsOfEffect);
		this.annualCost = annualCost;
		this.disutility = disutility;
	}

	/**
	 * Creates the intervention
	 * @param id Unique identifier of the intervention
	 * @param annualCost Annual cost assigned to the intervention
	 * @param yearsOfEffect Years of effect of the intervention
	 */
	public SAPIntervention(int id, double annualCost, double yearsOfEffect) {
		this(id, annualCost, yearsOfEffect, 0.0);
	}

	@Override
	public double getHBA1cLevel(DiabetesPatient pat) {
		return pat.getBaselineHBA1c();
//			return 2.206 + 1.491 + (0.618*pat.getBaselineHBA1c()) - (0.150 * Math.max(0, pat.getWeeklySensorUsage() - MIN_WEEKLY_USAGE)) - (0.005*pat.getAge());
	}

	@Override
	public double getAnnualCost(DiabetesPatient pat) {
		return annualCost;
	}

	@Override
	public double getDisutility(DiabetesPatient pat) {
		return disutility;
	}

}