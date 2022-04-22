/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.interventions.DoNothingIntervention;
import es.ull.iis.simulation.hta.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.outcomes.DiseaseCostCalculator;
import es.ull.iis.simulation.hta.outcomes.DiseaseUtilityCalculator;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.EmpiricalSpainDeathSubmodel;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class PBDRepository extends SecondOrderParamsRepository {
	private final CostCalculator costCalc;
	private final UtilityCalculator utilCalc;

	/**
	 * @param nRuns
	 * @param nPatients
	 */
	public PBDRepository(int nRuns, int nPatients, boolean allAffected) {
		super(nRuns, nPatients);
		costCalc = new DiseaseCostCalculator(this);
		utilCalc = new DiseaseUtilityCalculator(this, DisutilityCombinationMethod.MAX, 0.8861);
		Disease dis = new PBDDisease(this);
		setPopulation(new PBDPopulation(this, dis, allAffected));
		new DoNothingIntervention(this);
		new PBDNewbornScreening(this);
		setDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
	}

	@Override
	public CostCalculator getCostCalculator() {
		return costCalc;
	}

	@Override
	public UtilityCalculator getUtilityCalculator() {
		return utilCalc;
	}

}
