/**
 * 
 */
package es.ull.iis.simulation.hta.retal.params;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.params.Param;
import es.ull.iis.simulation.hta.retal.RetalPatient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class VAProgressionParam extends Param {

	/**
	 * @param simul
	 * @param 
	 */
	public VAProgressionParam() {
		super();
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
	public abstract ArrayList<VAProgressionPair> getVAProgression(RetalPatient pat, int eyeIndex, double expectedVA);
}
