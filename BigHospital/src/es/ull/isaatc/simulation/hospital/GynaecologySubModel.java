/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.common.SimulationTimeFunction;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class GynaecologySubModel extends SurgicalSubModel {
	private static final int BEDS = 15;
	private static final int SURGERIES = 3; 
	private static final int SURGEONS = 3; 
	private static final int DOCTORS = 3; 
	private static final int NURSES = 3; 
	private static final int NPATIENTS = 100;

	public GynaecologySubModel(SimulationObjectFactory factory) {
		super(factory, "Gynaecology", "GYN", BigHospital.GYNAECOLOGYID, BEDS, SURGERIES, SURGEONS, DOCTORS, NURSES,
				new SimulationTimeFunction(factory.getSimulation().getTimeUnit(), "ConstantVariate", new TimeStamp(TimeUnit.HOUR, 2)),
				new SimulationTimeFunction(factory.getSimulation().getTimeUnit(), "ConstantVariate", new TimeStamp(TimeUnit.HOUR, 12)),
				new SimulationTimeFunction(factory.getSimulation().getTimeUnit(), "ConstantVariate", new TimeStamp(TimeUnit.HOUR, 12)),
				new SimulationTimeFunction(factory.getSimulation().getTimeUnit(), "ConstantVariate", new TimeStamp(TimeUnit.HOUR, 12)),
				TimeFunctionFactory.getInstance("ConstantVariate", NPATIENTS), 
				SimulationPeriodicCycle.newDailyCycle(factory.getSimulation().getTimeUnit(), new TimeStamp(TimeUnit.MINUTE, 479))); 
	}

}
