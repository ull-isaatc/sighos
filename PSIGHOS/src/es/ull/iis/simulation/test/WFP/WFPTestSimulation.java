/**
 * 
 */
package es.ull.iis.simulation.test.WFP;

import java.util.ArrayList;

import es.ull.iis.simulation.inforeceiver.StdInfoView;
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
 * - Simulation Time Unit must be MINUTE 
 * - No random number generators should be used. 
 * - Resources must use a simple periodic cycle
 * - The preferred cycle is the TableCycle   
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class WFPTestSimulation extends Simulation {
	public final static int DEFNELEMENTS = 3;
	public final static TimeUnit SIMUNIT = TimeUnit.MINUTE; 
	public final static long RESSTART = 8 * 60;
	public final static long RESPERIOD = 24 * 60;
	public final static long RESAVAILABLE = 7 * 60;
	public final static TimeStamp GENSTART = TimeStamp.getZero();
	public final static TimeStamp GENPERIOD = TimeStamp.getDay();
	public final static long []DEFACTDURATION = new long [] {5, 10, 15, 20, 25, 30, 120};
	public final static long SIMSTART = 0L;
	public final static long SIMEND = 1440L;
	private final ArrayList<CheckerListener> listeners;
	private final ArrayList<Integer> nElems;
	
	public WFPTestSimulation(int id, String description) {
		super(id, description, SIMSTART, SIMEND);
		listeners = new ArrayList<CheckerListener>();
		nElems = new ArrayList<Integer>();
		createModel();
		addCheckers();
	}
	
	protected abstract void createModel();
	
	private void addCheckers() {
		if (WFPTestMain.ENABLE_STD_OUTPUT)
			addInfoReceiver(new StdInfoView());
		if (WFPTestMain.ENABLE_CHECKRESOURCES) {
			listeners.add(new CheckResourcesListener(this.getResourceList().size()));
		}
		if (WFPTestMain.ENABLE_CHECKELEMENTS) {
			listeners.add(new CheckElementsListener(nElems));
		}
		for (final CheckerListener l : listeners) {
			addInfoReceiver(l);
		}
	}
	
	@Override
	public void init() {
		super.init();
		System.out.println("Testing " + description + "...");
	}
	
	@Override
	public void end() {
		super.end();
		for (final CheckerListener l : listeners) {
			if (l.testPassed()) {
				System.out.println(l + "\tPassed");
			}
			else {
				System.out.println(l + "\tErrors");
				System.out.println(l.testProblems());
			}
		}
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
		final Resource res = new Resource(this, description);
		res.newTimeTableOrCancelEntriesAdder(rt).withDuration(getResourceCycle(), RESAVAILABLE).addTimeTableEntry();
		return res;
	}
	
	public ResourceType getDefResourceType(String description) {
		return new ResourceType(this, description);
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
		ActivityFlow act = new ActivityFlow(this, description, presential, false);
    	act.newWorkGroupAdder(wg).withDelay(DEFACTDURATION[dur]).add();
		return act;
	}
	
	public ElementType getDefElementType(String description) {
		// Adds a new element type but, until not used within a generator, it will create 0 elements
		nElems.add(0);
		return new ElementType(this, description);
	}
	
	public SimulationPeriodicCycle getGeneratorCycle() {
		return new SimulationPeriodicCycle(SIMUNIT, GENSTART, new SimulationTimeFunction(SIMUNIT, "ConstantVariate", GENPERIOD), 0);
	}
	
	public TimeDrivenElementGenerator getDefGenerator(ElementType et, InitializerFlow flow) {
		return getDefGenerator(DEFNELEMENTS, et, flow);
	}
	
	public TimeDrivenElementGenerator getDefGenerator(int elems, ElementType et, InitializerFlow flow) {
		nElems.set(et.getIdentifier(), nElems.get(et.getIdentifier()) + elems);
        return new TimeDrivenElementGenerator(this, elems, et, flow, getGeneratorCycle());
	}
}
