/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author Iván Castilla
 *
 */
public abstract class SecondOrderDeathSubmodel {
	/** A flag to enable or disable this complication during the simulation run */
	private boolean enable;

	/**
	 * 
	 */
	public SecondOrderDeathSubmodel() {
		enable = true;
	}

	/**
	 * Disables this complication
	 */
	public void disable() {
		enable = false;
	}

	public boolean isEnabled() {
		return enable;
	}

	public abstract void addSecondOrderParams(SecondOrderParamsRepository secParams);


	public abstract DeathSubmodel getInstance(SecondOrderParamsRepository secParams);
	
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
