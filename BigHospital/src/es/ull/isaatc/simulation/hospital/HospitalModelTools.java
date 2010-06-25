/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.SimulationCycle;
import es.ull.isaatc.simulation.common.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.common.SimulationTimeFunction;
import es.ull.isaatc.simulation.common.SimulationWeeklyPeriodicCycle;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.util.WeeklyPeriodicCycle;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class HospitalModelTools {
	private static TimeStamp stdHumanResourceAvailability = null;
	private static SimulationCycle stdHumanResourceCycle = null;
	private static TimeStamp stdMaterialResourceAvailability = null;
	private static SimulationCycle stdMaterialResourceCycle = null;
	public static final long DAYSTART = 8;
	public static final long WORKHOURS = 8;
	
	public static TimeStamp getStdHumanResourceAvailability(Simulation simul) {
		if (stdHumanResourceAvailability == null)
			stdHumanResourceAvailability = new TimeStamp(TimeUnit.HOUR, 8);
		return stdHumanResourceAvailability;
	}
	
	public static SimulationCycle getStdHumanResourceCycle(Simulation simul) {
		if (stdHumanResourceCycle == null) {
//			TimeStamp[] st = new TimeStamp[5];
//			for (int i = 0; i < 5; i++)
//				st[i] = new TimeStamp(TimeUnit.HOUR, 8 + 24 * i);
//			stdHumanResourceCycle = new SimulationTableCycle(simul.getTimeUnit(), st);
			stdHumanResourceCycle = new SimulationWeeklyPeriodicCycle(simul.getTimeUnit(), WeeklyPeriodicCycle.WEEKDAYS, 
					new TimeStamp(TimeUnit.HOUR, 8), 0);
		}
		return stdHumanResourceCycle;
	}

	public static TimeStamp getStdMaterialResourceAvailability(Simulation simul) {
		if (stdMaterialResourceAvailability == null)
			stdMaterialResourceAvailability = simul.getEndTs();
		return stdMaterialResourceAvailability;
	}
	
	public static SimulationCycle getStdMaterialResourceCycle(Simulation simul) {
		if (stdMaterialResourceCycle == null) {
			SimulationTimeFunction tf = new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", simul.getEndTs());
			stdMaterialResourceCycle = new SimulationPeriodicCycle(simul.getTimeUnit(), simul.getStartTs(), tf, simul.getEndTs());
		}
		return stdMaterialResourceCycle;
	}

	public static SimulationTimeFunction getNextHighFunction(TimeUnit unit, TimeStamp scale, TimeStamp shift, String className, Object... parameters) {
		SimulationTimeFunction innerFunc = new SimulationTimeFunction(unit, className, parameters);
		return new SimulationTimeFunction(unit, "NextHighFunction", innerFunc, scale, shift);
	}
}
