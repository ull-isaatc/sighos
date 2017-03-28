/**
 * 
 */
package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.TimeDrivenElementGenerator;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.InitializerFlow;

/**
 * The base class to create tests for Workflow patterns.
 * Since the checking needs to be automated, the models defined in the tests must adhere to the following restrictions:
 * - The simulation must be restricted to 1 day
 * - ParallelSimulationEngine Time Unit must be MINUTE 
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
	public final static long []DEFACTDURATION = new long [] {5, 10, 15, 20, 25, 30, 120};
	public final static long SIMSTART = 0L;
	public final static long SIMEND = 1440L;
	public final static TimeUnit SIMUNIT = TimeUnit.MINUTE; 
	protected boolean detailed;
	protected Simulation simul;
	protected final int id;
	protected final String description;
	
	public WFPTestSimulationFactory(int id, String description, boolean detailed, int nThreads) {
		this.id = id;
		this.description = description;
		this.detailed = detailed;
		simul = createModel();
	}
	
	public WFPTestSimulationFactory(int id, String description, boolean detailed) {
		this (id, description, detailed, 1);
	}
	
	protected abstract Simulation createModel();
	
	public Simulation getModel() {
		return simul;
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
		final Resource res = new Resource(simul, description);
		res.addTimeTableEntry(getResourceCycle(), RESAVAILABLE, rt);
		return res;
	}
	
	public ResourceType getDefResourceType(String description) {
		return new ResourceType(simul, description);
	}
	
	public ActivityFlow getDefActivity(String description, WorkGroup wg) {
		return getDefActivity(description, 0, wg, true);
	}
	
	public ActivityFlow getDefActivity(String description, WorkGroup wg, boolean presential) {
		return getDefActivity(description, 0, wg, presential);
	}
	
	public ActivityFlow getDefActivity(String description, int dur, WorkGroup wg) {
		return getDefActivity(description, dur, wg, true);
	}
	
	public ActivityFlow getDefActivity(String description, int dur, WorkGroup wg, boolean presential) {
		ActivityFlow act = new ActivityFlow(simul, description, presential, false);
    	act.addWorkGroup(0, wg, DEFACTDURATION[dur]);
		return act;
	}
	
	public ElementType getDefElementType(String description) {
		return new ElementType(simul, description);
	}
	
	public SimulationPeriodicCycle getGeneratorCycle() {
		return new SimulationPeriodicCycle(SIMUNIT, GENSTART, new SimulationTimeFunction(SIMUNIT, "ConstantVariate", GENPERIOD), 0);
	}
	
	public TimeDrivenElementGenerator getDefGenerator(ElementType et, InitializerFlow flow) {
		return getDefGenerator(DEFNELEMENTS, et, flow);
	}
	
	public TimeDrivenElementGenerator getDefGenerator(int elems, ElementType et, InitializerFlow flow) {
        return new TimeDrivenElementGenerator(simul, elems, et, flow, getGeneratorCycle());
	}
}
