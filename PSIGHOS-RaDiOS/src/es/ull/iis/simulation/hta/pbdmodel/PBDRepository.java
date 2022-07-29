/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.effectiveness.DiseaseUtilityCalculator;
import es.ull.iis.simulation.hta.effectiveness.UtilityCalculator;
import es.ull.iis.simulation.hta.effectiveness.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.interventions.DoNothingIntervention;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.EmpiricalSpainDeathSubmodel;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class PBDRepository extends SecondOrderParamsRepository {
	private final UtilityCalculator utilCalc;

	/**
	 * @param nRuns
	 * @param nPatients
	 */
	public PBDRepository(int nRuns, int nPatients, boolean allAffected) {
		super(nRuns, nPatients);
		utilCalc = new DiseaseUtilityCalculator(this, DisutilityCombinationMethod.MAX);
		Disease dis = new PBDDisease(this);
		setPopulation(new PBDPopulation(this, dis, allAffected));
		new DoNothingIntervention(this);
		new PBDNewbornScreening(this);
		setDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
	}

	@Override
	public UtilityCalculator getUtilityCalculator() {
		return utilCalc;
	}

}
