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
 * Contains the main utilities and generic methods used to build a model of a hospital. The typical methods
 * to create activities, human and material resources, etc have been defined here.
 *   
 * @author Iván Castilla Rodríguez
 */
public class HospitalModelConfig {
	/** The typical cycle to define a human resource availability */
	private static SimulationCycle stdHumanResourceCycle = null;
	/** The scale to be used in the simulation */
	private static TimeStamp scale = null;
	/** The time unit of the model */
	public static final TimeUnit UNIT = TimeUnit.MINUTE;
	/** The time when human resources start working every day */
	public static final TimeStamp DAYSTART = new TimeStamp(TimeUnit.HOUR, 8);
	/** How long human resources work every day */
	public static final TimeStamp WORKHOURS = new TimeStamp(TimeUnit.HOUR, 8);
	/** Patients arrive 10 minutes before 8 am */
	public static final TimeStamp PATIENTARRIVAL = new TimeStamp(TimeUnit.MINUTE, 470);
	
	/**
	 * Returns the amount of time a human resource is typically available, that is, 8 hours. 
	 * @return the amount of time a human resource is typically available
	 */
	public static TimeStamp getStdHumanResourceAvailability() {
		return WORKHOURS;
	}
	
	/**
	 * Creates and returns the typical cycle to define the availability of a human resource. 
	 * A human resource is typically available at 8 am every weekday. 
	 * @return the typical cycle to define the availability of a human resource
	 */
	public static SimulationCycle getStdHumanResourceCycle() {
		if (stdHumanResourceCycle == null) {
			stdHumanResourceCycle = new SimulationWeeklyPeriodicCycle(UNIT, WeeklyPeriodicCycle.WEEKDAYS, 
					DAYSTART, 0);
		}
		return stdHumanResourceCycle;
	}

	/**
	 * Returns the amount of time a material resource is typically available, which is the total length 
	 * of the simulation. 
	 * @param simul Simulation where this resource is to be declared
	 * @return the amount of time a material resource is typically available
	 */
	public static TimeStamp getStdMaterialResourceAvailability(Simulation simul) {
		return simul.getEndTs();
	}
	
	/**
	 * Creates and returns the typical cycle to define the availability of a material resource. Since 
	 * a material resource is always available, simply defines a cycle starting and ending together with
	 * the simulation.   
	 * @param simul Simulation where this resource is to be declared
	 * @return the typical cycle to define the availability of a material resource
	 */
	public static SimulationCycle getStdMaterialResourceCycle(Simulation simul) {
		SimulationTimeFunction tf = new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", simul.getEndTs());
		return new SimulationPeriodicCycle(simul.getTimeUnit(), simul.getStartTs(), tf, simul.getEndTs());
	}

	/**
	 * Returns a human resource with the specified description and available for the specified resource 
	 * type according to the typical availability. 
	 * @param factory The simulation factory used to create the components of this simulation
	 * @param description A brief text describing this resource
	 * @param rt The role this resource type plays during its availability
	 * @return a human resource with the specified description and available for the specified resource 
	 * type according to the typical availability
	 */
	public static Resource getStdHumanResource(SimulationObjectFactory factory, String description, ResourceType rt) {
		Resource res = factory.getResourceInstance(description);
		res.addTimeTableEntry(getStdHumanResourceCycle(), getStdHumanResourceAvailability(), rt);
		return res;		
	}
	
	/**
	 * Returns a material resource with the specified description and available for the specified resource 
	 * type according to the typical availability. 
	 * @param factory The simulation factory used to create the components of this simulation
	 * @param description A brief text describing this resource
	 * @param rt The role this resource type plays during its availability
	 * @return a material resource with the specified description and available for the specified resource 
	 * type according to the typical availability
	 */
	public static Resource getStdMaterialResource(SimulationObjectFactory factory, String description, ResourceType rt) {
		Simulation simul = factory.getSimulation();
		Resource res = factory.getResourceInstance(description);
		res.addTimeTableEntry(getStdMaterialResourceCycle(simul), getStdMaterialResourceAvailability(simul), rt);
		return res;		
	}
	
	/**
	 * Creates a resource type with the specified description and <tt>nRes</tt> resources belonging to 
	 * such resource type. 
	 * @param factory
	 * @param description
	 * @param nRes
	 * @return
	 */
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
	
	public static void setScale(TimeStamp scale) {
		HospitalModelConfig.scale = scale;
	}
	
	public static SimulationTimeFunction getScaledSimulationTimeFunction(TimeUnit unit, String className, Object... parameters) {
		if (scale == null) {
			return new SimulationTimeFunction(unit, className, parameters);
		}
		else {
			return getNextHighFunction(unit, scale, TimeStamp.getZero(), className, parameters);
		}			
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
		WorkGroup dummyWG = factory.getWorkGroupInstance(new ResourceType[] {}, new int[] {});
		TimeDrivenActivity act = null;
		if (!presential)
			act = factory.getTimeDrivenActivityInstance(description, 0, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		else
			act = factory.getTimeDrivenActivityInstance(description);
		act.addWorkGroup(duration, dummyWG);
		return act;
	}
	
	public static TimeDrivenActivity getWaitTilNextDay(SimulationObjectFactory factory, String description, TimeStamp startNextDay) {
		return getWaitTilNext(factory, description, TimeStamp.getDay(), startNextDay);
	}

	public static TimeDrivenActivity getWaitTilNext(SimulationObjectFactory factory, String description, TimeStamp waitScale, TimeStamp waitShift) {
		WorkGroup dummyWG = factory.getWorkGroupInstance(new ResourceType[] {}, new int[] {});
		TimeDrivenActivity act  = factory.getTimeDrivenActivityInstance(description, 0, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		final TimeUnit unit = factory.getSimulation().getTimeUnit();
		act.addWorkGroup(getNextHighFunction(unit, waitScale, waitShift, "ConstantVariate", new TimeStamp(unit, 1)), dummyWG);
		return act;
	}
}
