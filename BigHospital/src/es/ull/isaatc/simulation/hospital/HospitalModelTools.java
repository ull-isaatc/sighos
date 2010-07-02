/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import java.util.EnumSet;

import es.ull.isaatc.simulation.common.Resource;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.SimulationCycle;
import es.ull.isaatc.simulation.common.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.common.SimulationTimeFunction;
import es.ull.isaatc.simulation.common.SimulationWeeklyPeriodicCycle;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
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
	private static WorkGroup dummyWG = null;
	public static final TimeStamp DAYSTART = new TimeStamp(TimeUnit.HOUR, 8);
	public static final TimeStamp WORKHOURS = new TimeStamp(TimeUnit.HOUR, 8);
	
	public static TimeStamp getStdHumanResourceAvailability(Simulation simul) {
		if (stdHumanResourceAvailability == null)
			stdHumanResourceAvailability = WORKHOURS;
		return stdHumanResourceAvailability;
	}
	
	public static SimulationCycle getStdHumanResourceCycle(Simulation simul) {
		if (stdHumanResourceCycle == null) {
			stdHumanResourceCycle = new SimulationWeeklyPeriodicCycle(simul.getTimeUnit(), WeeklyPeriodicCycle.WEEKDAYS, 
					DAYSTART, 0);
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

	public static Resource getStdHumanResource(SimulationObjectFactory factory, String description, ResourceType rt) {
		Simulation simul = factory.getSimulation();
		Resource res = factory.getResourceInstance(description);
		res.addTimeTableEntry(getStdHumanResourceCycle(simul), getStdHumanResourceAvailability(simul), rt);
		return res;		
	}
	
	public static Resource getStdMaterialResource(SimulationObjectFactory factory, String description, ResourceType rt) {
		Simulation simul = factory.getSimulation();
		Resource res = factory.getResourceInstance(description);
		res.addTimeTableEntry(getStdMaterialResourceCycle(simul), getStdMaterialResourceAvailability(simul), rt);
		return res;		
	}
	
	public static ResourceType createNStdHumanResources(SimulationObjectFactory factory, String description, int nRes) {
		ResourceType rt = factory.getResourceTypeInstance(description);
		for (int i = 0; i < nRes; i++)
			getStdHumanResource(factory, description + " " + i, rt);
		return rt;
	}
	
	public static ResourceType createNStdMaterialResources(SimulationObjectFactory factory, String description, int nRes) {
		ResourceType rt = factory.getResourceTypeInstance(description);
		for (int i = 0; i < nRes; i++)
			getStdMaterialResource(factory, description + " " + i, rt);
		return rt;
	}
	
	public static SimulationTimeFunction getNextHighFunction(TimeUnit unit, TimeStamp scale, TimeStamp shift, String className, Object... parameters) {
		SimulationTimeFunction innerFunc = new SimulationTimeFunction(unit, className, parameters);
		return new SimulationTimeFunction(unit, "NextHighFunction", innerFunc, scale, shift);
	}
	
	public static TimeDrivenActivity createStdTimeDrivenActivity(SimulationObjectFactory factory, String description, SimulationTimeFunction duration, WorkGroup wg, boolean presential) {
		TimeDrivenActivity act = null;
		if (!presential)
			act = factory.getTimeDrivenActivityInstance(description, 0, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		else
			act = factory.getTimeDrivenActivityInstance(description);
		act.addWorkGroup(duration, wg);
		return act;
	}
	
	public static TimeDrivenActivity getDelay(SimulationObjectFactory factory, String description, SimulationTimeFunction duration, boolean presential) {
		if (dummyWG == null)
			dummyWG = factory.getWorkGroupInstance(new ResourceType[] {}, new int[] {});
		TimeDrivenActivity act = null;
		if (!presential)
			act = factory.getTimeDrivenActivityInstance(description, 0, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		else
			act = factory.getTimeDrivenActivityInstance(description);
		act.addWorkGroup(duration, dummyWG);
		return act;
	}
	
	public static TimeDrivenActivity getWaitTilNextDay(SimulationObjectFactory factory, String description, TimeStamp startNextDay) {
		if (dummyWG == null)
			dummyWG = factory.getWorkGroupInstance(new ResourceType[] {}, new int[] {});
		TimeDrivenActivity act  = factory.getTimeDrivenActivityInstance(description);
		act.addWorkGroup(getNextHighFunction(factory.getSimulation().getTimeUnit(),	
				TimeStamp.getDay(), startNextDay, "ConstantVariate", TimeStamp.getMinute()), dummyWG);
		return act;
	}
}
