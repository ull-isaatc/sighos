/**
 * 
 */
package es.ull.isaatc.simulation.test.WFP;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.common.SimulationTimeFunction;
import es.ull.isaatc.simulation.common.Resource;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.TimeDrivenGenerator;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.factory.SimulationFactory;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.InitializerFlow;

/**
 * The base class to create tests for Workflow patterns.
 * Since the checking needs to be automated, the models defined in the tests must adhere to the following restrictions:
 * - The simulation must be restricted to 1 day
 * - Simulation Time Unit must be MINUTE 
 * - All the model objects (resources, resource types, activities, element types) defined must have consecutive 
 * identifiers starting in 0. There can not be missing indexes.
 * - No random number generators should be used. 
 * - Resources must use a simple periodic cycle
 * - The preferred cycle is the TableCycle   
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class WFPTestSimulationFactory {
	public final static int DEFNELEMENTS = 3;
	public final static TimeStamp RESSTART = new TimeStamp(TimeUnit.HOUR, 8);
	public final static TimeStamp RESPERIOD = TimeStamp.getDay();
	public final static TimeStamp RESAVAILABLE = new TimeStamp(TimeUnit.HOUR, 7);
	public final static TimeStamp GENSTART = TimeStamp.getZero();
	public final static TimeStamp GENPERIOD = TimeStamp.getDay();
	public final static TimeStamp []DEFACTDURATION = new TimeStamp [] {
		new TimeStamp(TimeUnit.MINUTE, 5),
		new TimeStamp(TimeUnit.MINUTE, 10),
		new TimeStamp(TimeUnit.MINUTE, 15),
		new TimeStamp(TimeUnit.MINUTE, 20),
		new TimeStamp(TimeUnit.MINUTE, 25),
		new TimeStamp(TimeUnit.MINUTE, 30),
		new TimeStamp(TimeUnit.MINUTE, 120),
		};
	public final static TimeStamp SIMSTART = TimeStamp.getZero();
	public final static TimeStamp SIMEND = TimeStamp.getDay();
	public final static TimeUnit SIMUNIT = TimeUnit.MINUTE; 
	private int resCounter = 0;	
	private int rtCounter = 0;
	private int actCounter = 0;
	private int etCounter = 0;
	private int egCounter = 0;
	private int ecCounter = 0;
	protected boolean detailed;
	protected SimulationObjectFactory factory;
	
	public WFPTestSimulationFactory(SimulationType type, int id, String description, boolean detailed) {
		factory = SimulationFactory.getInstance(type, id, description, SIMUNIT, SIMSTART, SIMEND);
		createModel();
		this.detailed = detailed;
	}
	
	protected abstract void createModel();
	
	public Simulation getSimulation() {
		return factory.getSimulation();
	}
	
	public SimulationTimeFunction getActivityDefDuration() {
		return getActivityDefDuration(0);
	}

	public SimulationTimeFunction getActivityDefDuration(int nAct) {
		return new SimulationTimeFunction(SIMUNIT, "ConstantVariate", DEFACTDURATION[nAct]);		
	}

	public SimulationPeriodicCycle getResourceCycle() {
		return new SimulationPeriodicCycle(SIMUNIT, RESSTART, new SimulationTimeFunction(SIMUNIT, "ConstantVariate", RESPERIOD), 0);
	}
	
	public Resource getDefResource(String description, ResourceType rt) {
		Resource res = factory.getResourceInstance(resCounter++, description);
		res.addTimeTableEntry(getResourceCycle(), RESAVAILABLE, rt);
		return res;
	}
	
	public ResourceType getDefResourceType(String description) {
		return factory.getResourceTypeInstance(rtCounter++, description);
	}
	
	public TimeDrivenActivity getDefTimeDrivenActivity(String description, WorkGroup wg) {
		return getDefTimeDrivenActivity(description, 0, wg, true);
	}
	
	public TimeDrivenActivity getDefTimeDrivenActivity(String description, WorkGroup wg, boolean presential) {
		return getDefTimeDrivenActivity(description, 0, wg, presential);
	}
	
	public TimeDrivenActivity getDefTimeDrivenActivity(String description, int dur, WorkGroup wg) {
		return getDefTimeDrivenActivity(description, dur, wg, true);
	}
	
	public TimeDrivenActivity getDefTimeDrivenActivity(String description, int dur, WorkGroup wg, boolean presential) {
		TimeDrivenActivity act = null;
		if (!presential)
			act = factory.getTimeDrivenActivityInstance(actCounter++, description, 0, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		else
			act = factory.getTimeDrivenActivityInstance(actCounter++, description);
    	act.addWorkGroup(new SimulationTimeFunction(SIMUNIT, "ConstantVariate", DEFACTDURATION[dur]), wg);
		return act;
	}
	
	public ElementType getDefElementType(String description) {
		return factory.getElementTypeInstance(etCounter++, description);
	}
	
	public SimulationPeriodicCycle getGeneratorCycle() {
		return new SimulationPeriodicCycle(SIMUNIT, GENSTART, new SimulationTimeFunction(SIMUNIT, "ConstantVariate", GENPERIOD), 0);
	}
	
	public TimeDrivenGenerator getDefGenerator(ElementType et, InitializerFlow flow) {
		return getDefGenerator(DEFNELEMENTS, et, flow);
	}
	
	public TimeDrivenGenerator getDefGenerator(int elems, ElementType et, InitializerFlow flow) {
        return factory.getTimeDrivenGeneratorInstance(egCounter++, 
        		factory.getElementCreatorInstance(ecCounter++, TimeFunctionFactory.getInstance("ConstantVariate", elems), factory.getSimulation().getElementType(0), flow), getGeneratorCycle());
	}
}
