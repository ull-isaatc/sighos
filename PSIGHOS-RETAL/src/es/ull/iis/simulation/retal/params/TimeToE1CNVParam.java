/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.OphthalmologicPatient;

/**
 * @author Iván Castilla Rodríguez
 * TODO Create class correctly
 */
public class TimeToE1CNVParam extends CompoundEmpiricTimeToEventParam {

	/**
	 * @param baseCase
	 */
	public TimeToE1CNVParam(boolean baseCase) {
		super(baseCase, TimeUnit.YEAR);
		// TODO Auto-generated constructor stub
	}

	@Override
	public long getValidatedTimeToEvent(OphthalmologicPatient pat, boolean firstEye) {
		// TODO Auto-generated method stub
		return 0;
	}

}
