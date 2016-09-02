/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.ArrayList;

import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RETALSimulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class VAProgressionParam extends Param {

	/**
	 * @param simul
	 * @param baseCase
	 */
	public VAProgressionParam(RETALSimulation simul, boolean baseCase) {
		super(simul, baseCase);
	}

	/**
	 * Returns the progression of the visual acuity according to the current state of the specified eye. The progression
	 * is defined as a list of pairs <time to change, visual acuity value>. Visual acuity can be at most 1.6, as defined in
	 * {@link VisualAcuity.MAX_LOGMAR}
	 * The last step in the progression is the worst possible visual acuity between the expected VA and the one (potentially) 
	 * computed within this method.
	 * @param pat A patient
	 * @param eyeIndex Index of the affected eye (0 for first eye, 1 for second eye)
	 * @param expectedVA The expected visual acuity if no changes are derived from this progression
	 * @return The progression of the visual acuity according to the current state of the specified eye
	 */
	public abstract ArrayList<VAProgressionPair> getVAProgression(Patient pat, int eyeIndex, double expectedVA);
}
