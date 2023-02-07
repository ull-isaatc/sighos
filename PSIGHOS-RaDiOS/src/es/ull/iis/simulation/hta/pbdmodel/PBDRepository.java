/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.interventions.DoNothingIntervention;
import es.ull.iis.simulation.hta.outcomes.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.EmpiricalSpainDeathSubmodel;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class PBDRepository extends SecondOrderParamsRepository {

	/**
	 * @param nRuns
	 * @param nPatients
	 */
	public PBDRepository(int nRuns, int nPatients, boolean allAffected) {
		super(nRuns, nPatients);
		setDisutilityCombinationMethod(DisutilityCombinationMethod.MAX);
		Disease dis = new PBDDisease(this);
		setPopulation(new PBDPopulation(this, dis, allAffected));
		new DoNothingIntervention(this);
		new PBDNewbornScreening(this);
		setDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
	}

}
