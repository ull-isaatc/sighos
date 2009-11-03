/**
 * 
 */
package es.ull.isaatc.simulation.sequential.test.WFP;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.ModelPeriodicCycle;
import es.ull.isaatc.simulation.common.ModelTimeFunction;
import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.sequential.ElementType;
import es.ull.isaatc.simulation.sequential.WorkGroup;
import es.ull.isaatc.simulation.sequential.ElementCreator;
import es.ull.isaatc.simulation.sequential.Resource;
import es.ull.isaatc.simulation.sequential.ResourceType;
import es.ull.isaatc.simulation.sequential.StandAloneLPSimulation;
import es.ull.isaatc.simulation.sequential.TimeDrivenActivity;
import es.ull.isaatc.simulation.sequential.TimeDrivenGenerator;
import es.ull.isaatc.simulation.sequential.flow.InitializerFlow;

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
public abstract class WFPTestSimulation extends StandAloneLPSimulation {
	public final static int DEFNELEMENTS = 3;
	public final static Time RESSTART = new Time(TimeUnit.HOUR, 8);
	public final static Time RESPERIOD = Time.getDay();
	public final static Time RESAVAILABLE = new Time(TimeUnit.HOUR, 7);
	public final static Time GENSTART = Time.getZero();
	public final static Time GENPERIOD = Time.getDay();
	public final static Time []DEFACTDURATION = new Time [] {
		new Time(TimeUnit.MINUTE, 5),
		new Time(TimeUnit.MINUTE, 10),
		new Time(TimeUnit.MINUTE, 15),
		new Time(TimeUnit.MINUTE, 20),
		};
	public final static Time SIMSTART = Time.getZero();
	public final static Time SIMEND = Time.getDay();
	public final static TimeUnit SIMUNIT = TimeUnit.MINUTE; 
	private int resCounter = 0;	
	private int rtCounter = 0;
	private int actCounter = 0;
	private int etCounter = 0;
	protected boolean detailed;
	
	public WFPTestSimulation(int id, String description, boolean detailed) {
		super(id, description, SIMUNIT, SIMSTART, SIMEND);
		this.detailed = detailed;
	}
	
	public ModelTimeFunction getActivityDefDuration() {
		return getActivityDefDuration(0);
	}

	public ModelTimeFunction getActivityDefDuration(int nAct) {
		return new ModelTimeFunction(unit, "ConstantVariate", DEFACTDURATION[nAct]);		
	}

	public ModelPeriodicCycle getResourceCycle() {
		return new ModelPeriodicCycle(unit, RESSTART, new ModelTimeFunction(unit, "ConstantVariate", RESPERIOD), 0);
	}
	
	public Resource getDefResource(String description, ResourceType rt) {
		Resource res = new Resource(resCounter++, this, description);
		res.addTimeTableEntry(getResourceCycle(), RESAVAILABLE, rt);
		return res;
	}
	
	public ResourceType getDefResourceType(String description) {
		return new ResourceType(rtCounter++, this, description);
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
			act = new TimeDrivenActivity(actCounter++, this, description, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		else
			act = new TimeDrivenActivity(actCounter++, this, description);
    	act.addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", DEFACTDURATION[dur]), wg);
		return act;
	}
	
	public ElementType getDefElementType(String description) {
		return new ElementType(etCounter++, this, description);
	}
	
	public ModelPeriodicCycle getGeneratorCycle() {
		return new ModelPeriodicCycle(unit, GENSTART, new ModelTimeFunction(unit, "ConstantVariate", GENPERIOD), 0);
	}
	
	public TimeDrivenGenerator getDefGenerator(ElementType et, InitializerFlow flow) {
		return getDefGenerator(DEFNELEMENTS, et, flow);
	}
	
	public TimeDrivenGenerator getDefGenerator(int elems, ElementType et, InitializerFlow flow) {
        return new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", elems), getElementType(0), flow), getGeneratorCycle());
	}
}
