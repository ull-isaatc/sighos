/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import es.ull.iis.simulation.hta.AcuteComplication;
import es.ull.iis.simulation.hta.Patient;

/**
 * @author icasrod
 *
 */
public abstract class SecondOrderAcuteComplicationSubmodel extends SecondOrderDisease {
	private final AcuteComplication comp;
	/**
	 * @param diabetesTypes
	 */
	public SecondOrderAcuteComplicationSubmodel(final AcuteComplication comp) {
		super();
		this.comp = comp;
	}

	public final AcuteComplication getComplication() {
		return comp;
	}

	/**
	 * The complication instance that should be returned when the complication is disabled.
	 * This instance ensures that no actions are performed.
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public class DisabledAcuteComplicationInstance extends AcuteComplicationSubmodel {

		public DisabledAcuteComplicationInstance() {
			super(comp, null, null);
		}
		
		@Override
		public DiseaseProgressionPair getProgression(Patient pat) {
			return new DiseaseProgressionPair(comp, Long.MAX_VALUE);
		}
		
		@Override
		public void reset() {
		}
		
		@Override
		public double getCostOfComplication(Patient pat) {
			return 0;
		}

		@Override
		public double getDisutility(Patient pat) {
			return 0;
		}
		
	}
}
