/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesProgressionPair;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;

/**
 * @author icasrod
 *
 */
public abstract class SecondOrderAcuteComplicationSubmodel extends SecondOrderComplicationSubmodel {
	private final DiabetesAcuteComplications type;
	/**
	 * @param diabetesTypes
	 */
	public SecondOrderAcuteComplicationSubmodel(final DiabetesAcuteComplications type, EnumSet<DiabetesType> diabetesTypes) {
		super(diabetesTypes);
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
		public DiabetesProgressionPair getProgression(DiabetesPatient pat) {
			return new DiabetesProgressionPair(type, Long.MAX_VALUE);
		}
		
		@Override
		public void reset() {
		}
		
		@Override
		public double getCostOfComplication(DiabetesPatient pat) {
			return 0;
		}

		@Override
		public double getDisutility(DiabetesPatient pat) {
			return 0;
		}
		
	}
}
