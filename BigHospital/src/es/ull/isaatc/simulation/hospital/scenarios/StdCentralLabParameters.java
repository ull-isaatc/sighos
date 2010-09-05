/**
 * 
 */
package es.ull.isaatc.simulation.hospital.scenarios;

import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.hospital.CentralLabSubModel;
import es.ull.isaatc.simulation.hospital.HospitalModelConfig;
import es.ull.isaatc.simulation.hospital.ModelParameterMap;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public final class StdCentralLabParameters extends ModelParameterMap {

	public StdCentralLabParameters() {
		super(CentralLabSubModel.Parameters.values().length);
		
		final TimeUnit unit = HospitalModelConfig.UNIT;
		put(CentralLabSubModel.Parameters.NTECH, 23);
		put(CentralLabSubModel.Parameters.N24HTECH, 5);
		put(CentralLabSubModel.Parameters.NNURSES, 16);
		put(CentralLabSubModel.Parameters.NXNURSES, 10);
		put(CentralLabSubModel.Parameters.NSLOTS, 150);
		put(CentralLabSubModel.Parameters.NCENT, 160);
		put(CentralLabSubModel.Parameters.LENGTH_SAMPLE, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"UniformVariate", 2, 3));
		// Centrifugation lasts until next 15 minutes 
		put(CentralLabSubModel.Parameters.LENGTH_CENT, HospitalModelConfig.getNextHighFunction(unit, 
				new TimeStamp(TimeUnit.MINUTE, 15), TimeStamp.getZero(), "ConstantVariate", 6)); 
		put(CentralLabSubModel.Parameters.LENGTH_TEST, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 8));
		put(CentralLabSubModel.Parameters.NHAETECH, 2);
		put(CentralLabSubModel.Parameters.NHAENURSES, 5);
		put(CentralLabSubModel.Parameters.NHAESLOTS, 40);
		put(CentralLabSubModel.Parameters.LENGTH_HAETEST, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 15));
		put(CentralLabSubModel.Parameters.NMICROTECH, 10);
		put(CentralLabSubModel.Parameters.NMICRONURSES, 0);
		put(CentralLabSubModel.Parameters.NMICROSLOTS, 50);
		put(CentralLabSubModel.Parameters.LENGTH_MICROTEST, HospitalModelConfig.getScaledSimulationTimeFunction(unit,
				"ConstantVariate", 20));
		put(CentralLabSubModel.Parameters.NPATTECH, 6);
		put(CentralLabSubModel.Parameters.NPATNURSES, 1);
		put(CentralLabSubModel.Parameters.NPATSLOTS, 50);
		put(CentralLabSubModel.Parameters.LENGTH_PATTEST, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 20));
	}

}
