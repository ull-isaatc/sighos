/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.HTAExperiment;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.interventions.DoNothingIntervention;
import es.ull.iis.simulation.hta.progression.Disease;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class PBDModel extends HTAModel {

	/**
	 * @param nRuns
	 * @param nPatients
	 */
	public PBDModel(HTAExperiment experiment, boolean allAffected) {
		super(experiment);
		Disease dis = new PBDDisease(this);
		try {
			new PBDPopulation(this, dis, allAffected);
			new DoNothingIntervention(this);
			new PBDNewbornScreening(this);
		} catch (MalformedSimulationModelException e) {
			e.printStackTrace();
		}
	}

}
