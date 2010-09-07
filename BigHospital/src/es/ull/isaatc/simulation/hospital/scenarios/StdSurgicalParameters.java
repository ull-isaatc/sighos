/**
 * 
 */
package es.ull.isaatc.simulation.hospital.scenarios;

import es.ull.isaatc.simulation.hospital.ModelParameterMap;
import es.ull.isaatc.simulation.hospital.SurgicalDptSharedModel;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public final class StdSurgicalParameters extends ModelParameterMap {

	public StdSurgicalParameters() {
		super(SurgicalDptSharedModel.Parameters.values().length);
		
		put(SurgicalDptSharedModel.Parameters.NBEDS_ICU, 15);
		put(SurgicalDptSharedModel.Parameters.NBEDS_PACU, 10);
		put(SurgicalDptSharedModel.Parameters.NANAESTHETISTS, 4);
	}

}
