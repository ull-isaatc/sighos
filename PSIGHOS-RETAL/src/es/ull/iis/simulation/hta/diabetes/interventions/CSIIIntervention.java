package es.ull.iis.simulation.hta.diabetes.interventions;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;

/**
 * An intervention that represents the usual pump treatment used in Spain. Assumes no changes in the HbA1c level of the 
 * patient during the simulation
 * @author Iván Castilla Rodríguez
 *
 */
public class CSIIIntervention extends DiabetesIntervention {
	public final static String NAME = "CSII";
	/** Annual cost of the intervention */
	private final double annualCost;
	/** Annual disutility due to the treatment */
	private final double disutility;
	
	/**
	 * Creates the intervention
	 * @param id Unique identifier of the intervention
	 * @param annualCost Annual cost assigned to the intervention
	 */
	public CSIIIntervention(int id, double annualCost, double disutility) {
		super(id, NAME, NAME, BasicConfigParams.DEF_MAX_AGE);
		this.annualCost = annualCost;
		this.disutility = disutility;
	}

	/**
	 * Creates the intervention
	 * @param id Unique identifier of the intervention
	 * @param annualCost Annual cost assigned to the intervention
	 */
	public CSIIIntervention(int id, double annualCost) {
		this(id, annualCost, 0.0);
	}

	@Override
	public double getHBA1cLevel(DiabetesPatient pat) {
		return pat.getBaselineHBA1c();
//			return 2.206 + (0.744*pat.getBaselineHBA1c()) - (0.003*pat.getAge()); // https://www.ncbi.nlm.nih.gov/pmc/articles/PMC3131116/
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