/**
 * 
 */
package es.ull.iis.simulation.hta.submodels;

import es.ull.iis.simulation.hta.Patient;

/**
 * @author Iván Castilla
 *
 */
public abstract class SecondOrderDeathSubmodel extends SecondOrderComplicationSubmodel {

	/**
	 * @param diabetesTypes
	 */
	public SecondOrderDeathSubmodel() {
		super();
	}

	/**
	 * The death instance that should be returned when the complication is disabled.
	 * This instance ensures that no actions are performed.
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public class DisabledDeathInstance extends DeathSubmodel {

		public DisabledDeathInstance(SecondOrderDeathSubmodel secOrder) {
			super(secOrder);
		}

		@Override
		public long getTimeToDeath(Patient pat) {
			return Long.MAX_VALUE;
		}
		
	}
	
}
