/**
 * 
 */
package es.ull.isaatc.simulation.hospital.scenarios;

import es.ull.isaatc.simulation.hospital.ModelParameterMap;
import es.ull.isaatc.simulation.hospital.SurgicalDptCommonModel;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public final class StdSurgicalParameters extends ModelParameterMap {

	public StdSurgicalParameters() {
		super(SurgicalDptCommonModel.Parameters.values().length);
		
		put(SurgicalDptCommonModel.Parameters.NBEDS_ICU, 15);
		put(SurgicalDptCommonModel.Parameters.NBEDS_PACU, 10);
		put(SurgicalDptCommonModel.Parameters.NANAESTHETISTS, 4);
	}

}
