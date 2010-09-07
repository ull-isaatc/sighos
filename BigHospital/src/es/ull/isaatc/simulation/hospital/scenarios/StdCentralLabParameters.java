/**
 * 
 */
package es.ull.isaatc.simulation.hospital.scenarios;

import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.hospital.CentralLabModel;
import es.ull.isaatc.simulation.hospital.HospitalModelConfig;
import es.ull.isaatc.simulation.hospital.ModelParameterMap;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public final class StdCentralLabParameters extends ModelParameterMap {

	public StdCentralLabParameters() {
		super(CentralLabModel.Parameters.values().length);
		
		final TimeUnit unit = HospitalModelConfig.UNIT;
		put(CentralLabModel.Parameters.NTECH, 23);
		put(CentralLabModel.Parameters.N24HTECH, 5);
		put(CentralLabModel.Parameters.NNURSES, 16);
		put(CentralLabModel.Parameters.NXNURSES, 10);
		put(CentralLabModel.Parameters.NSLOTS, 150);
		put(CentralLabModel.Parameters.NCENT, 160);
		put(CentralLabModel.Parameters.LENGTH_SAMPLE, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"UniformVariate", 2, 3));
		// Centrifugation lasts until next 15 minutes 
		put(CentralLabModel.Parameters.LENGTH_CENT, HospitalModelConfig.getNextHighFunction(unit, 
				new TimeStamp(TimeUnit.MINUTE, 15), TimeStamp.getZero(), "ConstantVariate", 6)); 
		put(CentralLabModel.Parameters.LENGTH_TEST, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 8));
		put(CentralLabModel.Parameters.NHAETECH, 2);
		put(CentralLabModel.Parameters.NHAENURSES, 5);
		put(CentralLabModel.Parameters.NHAESLOTS, 40);
		put(CentralLabModel.Parameters.LENGTH_HAETEST, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 15));
		put(CentralLabModel.Parameters.NMICROTECH, 10);
		put(CentralLabModel.Parameters.NMICRONURSES, 0);
		put(CentralLabModel.Parameters.NMICROSLOTS, 50);
		put(CentralLabModel.Parameters.LENGTH_MICROTEST, HospitalModelConfig.getScaledSimulationTimeFunction(unit,
				"ConstantVariate", 20));
		put(CentralLabModel.Parameters.NPATTECH, 6);
		put(CentralLabModel.Parameters.NPATNURSES, 1);
		put(CentralLabModel.Parameters.NPATSLOTS, 50);
		put(CentralLabModel.Parameters.LENGTH_PATTEST, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 20));
	}

}
