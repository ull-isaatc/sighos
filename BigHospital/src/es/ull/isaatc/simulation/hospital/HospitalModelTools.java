/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import es.ull.isaatc.simulation.common.*;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class HospitalModelTools {
	private static TimeStamp stdHumanResourceAvailability = null;
	private static SimulationCycle stdHumanResourceCycle = null;
	private static TimeStamp stdMaterialResourceAvailability = null;
	private static SimulationCycle stdMaterialResourceCycle = null;
	
	public static TimeStamp getStdHumanResourceAvailability(Simulation simul) {
		if (stdHumanResourceAvailability == null)
			stdHumanResourceAvailability = new TimeStamp(TimeUnit.HOUR, 8);
		return stdHumanResourceAvailability;
	}
	
	public static SimulationCycle getStdHumanResourceCycle(Simulation simul) {
		if (stdHumanResourceCycle == null) {
			TimeStamp[] st = new TimeStamp[5];
			for (int i = 0; i < 5; i++)
				st[i] = new TimeStamp(TimeUnit.HOUR, 8 + 24 * i);
			stdHumanResourceCycle = new SimulationTableCycle(simul.getTimeUnit(), st);
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
			TimeStamp[] st = new TimeStamp[5];
			for (int i = 0; i < 5; i++)
				st[i] = new TimeStamp(TimeUnit.HOUR, 8 + 24 * i);
			SimulationTimeFunction tf = new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", simul.getEndTs());
			stdMaterialResourceCycle = new SimulationPeriodicCycle(simul.getTimeUnit(), simul.getStartTs(), tf, simul.getEndTs());
		}
		return stdMaterialResourceCycle;
	}

}
