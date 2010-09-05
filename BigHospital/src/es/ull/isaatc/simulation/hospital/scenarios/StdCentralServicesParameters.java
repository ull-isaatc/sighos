/**
 * 
 */
package es.ull.isaatc.simulation.hospital.scenarios;

import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.hospital.CentralServicesSubModel;
import es.ull.isaatc.simulation.hospital.HospitalModelConfig;
import es.ull.isaatc.simulation.hospital.ModelParameterMap;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public final class StdCentralServicesParameters extends ModelParameterMap {

	public StdCentralServicesParameters() {
		super(CentralServicesSubModel.Parameters.values().length);
		
		final TimeUnit unit = HospitalModelConfig.UNIT;
		put(CentralServicesSubModel.Parameters.NTECHNUC, 4);
		put(CentralServicesSubModel.Parameters.NTECHRAD, 10);
		put(CentralServicesSubModel.Parameters.LENGTH_NUCTEST, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"UniformVariate", 4, 12));
		put(CentralServicesSubModel.Parameters.LENGTH_RADTEST, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"UniformVariate", 4, 12));
		put(CentralServicesSubModel.Parameters.LENGTH_NUCREPORT, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"UniformVariate", 8, 16));
		put(CentralServicesSubModel.Parameters.LENGTH_RADREPORT, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"UniformVariate", 7, 14));
	}

}
