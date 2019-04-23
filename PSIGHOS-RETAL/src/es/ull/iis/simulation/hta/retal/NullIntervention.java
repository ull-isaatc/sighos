/**
 * 
 */
package es.ull.iis.simulation.hta.retal;

/**
 * @author icasrod
 *
 */
public class NullIntervention implements Intervention {

	/**
	 * 
	 */
	public NullIntervention() {
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public String getDescription() {
		return "Clinical detection";
	}

	@Override
	public String getShortName() {
		return "CLI";
	}

}
