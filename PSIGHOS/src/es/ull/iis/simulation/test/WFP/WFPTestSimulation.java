/**
 * 
 */
package es.ull.iis.simulation.test.WFP;

import java.util.ArrayList;
import java.util.TreeMap;

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
 * - All the elements must finish their tasks within the simulated time
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
	private final ArrayList<Long> actDuration;
	private final TreeMap<ActivityFlow, Integer> actIndex; 
	
	public WFPTestSimulation(int id, String description) {
		super(id, description, SIMSTART, SIMEND);
		listeners = new ArrayList<CheckerListener>();
		nElems = new ArrayList<Integer>();
		actDuration = new ArrayList<Long>();
		actIndex = new TreeMap<ActivityFlow, Integer>();
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
		if (WFPTestMain.ENABLE_CHECKACTIVITIES) {
			int n = 0;
			for (int count : nElems) 
				n += count;
			listeners.add(new CheckActivitiesListener(n, actIndex, actDuration));
		}
		for (final CheckerListener l : listeners) {
			addInfoReceiver(l);
		}
	}
	
	protected void addCustomChecker(final CheckerListener listener) {
		listeners.add(listener);
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
	
	public Resource getDefResource(String description, ResourceType rt) {
		final Resource res = new Resource(this, description);
		final SimulationPeriodicCycle cycle = new SimulationPeriodicCycle(SIMUNIT, RESSTART, new SimulationTimeFunction(SIMUNIT, "ConstantVariate", RESPERIOD), 0);
		res.newTimeTableOrCancelEntriesAdder(rt).withDuration(cycle, RESAVAILABLE).addTimeTableEntry();
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
	
	public ActivityFlow getDefActivity(String description, int dur, WorkGroup wg, boolean exclusive) {
		return new TestActivityFlow(description, dur, wg, exclusive);
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
	
	public class TestActivityFlow extends ActivityFlow {

		public TestActivityFlow(String description, int dur, WorkGroup wg, boolean exclusive) {
			super(WFPTestSimulation.this, description, exclusive, false);
	    	newWorkGroupAdder(wg).withDelay(DEFACTDURATION[dur]).add();
	    	actIndex.put(this, actDuration.size());
	    	actDuration.add(DEFACTDURATION[dur]);
		}
		
	}
}
