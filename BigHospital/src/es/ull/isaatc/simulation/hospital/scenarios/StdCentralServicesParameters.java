/**
 * 
 */
package es.ull.isaatc.simulation.hospital.scenarios;

import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.hospital.CentralServicesModel;
import es.ull.isaatc.simulation.hospital.HospitalModelConfig;
import es.ull.isaatc.simulation.hospital.ModelParameterMap;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public final class StdCentralServicesParameters extends ModelParameterMap {

	public StdCentralServicesParameters() {
		super(CentralServicesModel.Parameters.values().length);
		
		final TimeUnit unit = HospitalModelConfig.UNIT;
		put(CentralServicesModel.Parameters.NTECHUSS, 4);
		put(CentralServicesModel.Parameters.NTECHRAD, 10);
		put(CentralServicesModel.Parameters.LENGTH_USSTEST, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"UniformVariate", 4, 12));
		put(CentralServicesModel.Parameters.LENGTH_RADTEST, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"UniformVariate", 4, 12));
		put(CentralServicesModel.Parameters.LENGTH_USSREPORT, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"UniformVariate", 8, 16));
		put(CentralServicesModel.Parameters.LENGTH_RADREPORT, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"UniformVariate", 7, 14));
	}

}
