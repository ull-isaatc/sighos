/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import es.ull.isaatc.simulation.common.*;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class CentralServicesSubModel {
	
	public static void createModel(SimulationObjectFactory factory) {
		ResourceType rtTec = factory.getResourceTypeInstance(BigHospital.CENTRALSERVICESID, "Lab. Technician");
		
	}
	
	
}
