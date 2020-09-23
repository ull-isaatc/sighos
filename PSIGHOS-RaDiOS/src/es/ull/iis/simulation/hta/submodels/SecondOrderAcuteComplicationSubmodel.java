/**
 * 
 */
package es.ull.iis.simulation.hta.submodels;

import es.ull.iis.simulation.hta.DiseaseProgressionPair;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;

/**
 * @author icasrod
 *
 */
public abstract class SecondOrderAcuteComplicationSubmodel extends SecondOrderComplicationSubmodel {
	private final DiabetesAcuteComplications type;
	/**
	 * @param diabetesTypes
	 */
	public SecondOrderAcuteComplicationSubmodel(final DiabetesAcuteComplications type) {
		super();
		this.type = type;
	}

	public final DiabetesAcuteComplications getComplicationType() {
		return type;
	}

	/**
	 * The complication instance that should be returned when the complication is disabled.
	 * This instance ensures that no actions are performed.
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public class DisabledAcuteComplicationInstance extends AcuteComplicationSubmodel {

		public DisabledAcuteComplicationInstance() {
			super(type, null, null);
		}
		
		@Override
		public DiseaseProgressionPair getProgression(Patient pat) {
			return new DiseaseProgressionPair(type, Long.MAX_VALUE);
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
