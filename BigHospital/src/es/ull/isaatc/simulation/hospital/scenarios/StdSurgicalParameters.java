/**
 * 
 */
package es.ull.isaatc.simulation.hospital.scenarios;

import es.ull.isaatc.simulation.hospital.ModelParameterMap;
import es.ull.isaatc.simulation.hospital.SurgicalSubModel;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public final class StdSurgicalParameters extends ModelParameterMap {

	public StdSurgicalParameters() {
		super(SurgicalSubModel.Parameters.values().length);
		
		put(SurgicalSubModel.Parameters.NBEDS_ICU, 15);
		put(SurgicalSubModel.Parameters.NBEDS_PACU, 10);
		put(SurgicalSubModel.Parameters.NANAESTHETISTS, 4);
	}

}
