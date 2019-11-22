/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;

/**
 * @author Iván Castilla
 *
 */
public abstract class SecondOrderDeathSubmodel extends SecondOrderComplicationSubmodel {

	/**
	 * @param diabetesTypes
	 */
	public SecondOrderDeathSubmodel(EnumSet<DiabetesType> diabetesTypes) {
		super(diabetesTypes);
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
		public long getTimeToDeath(DiabetesPatient pat) {
			return Long.MAX_VALUE;
		}
		
	}
	
}
