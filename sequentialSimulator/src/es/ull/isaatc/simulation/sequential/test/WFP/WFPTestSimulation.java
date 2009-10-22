/**
 * 
 */
package es.ull.isaatc.simulation.sequential.test.WFP;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.SimulationTimeFunction;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.sequential.ElementCreator;
import es.ull.isaatc.simulation.sequential.ElementType;
import es.ull.isaatc.simulation.sequential.Resource;
import es.ull.isaatc.simulation.sequential.ResourceType;
import es.ull.isaatc.simulation.sequential.StandAloneLPSimulation;
import es.ull.isaatc.simulation.sequential.TimeDrivenActivity;
import es.ull.isaatc.simulation.sequential.TimeDrivenGenerator;
import es.ull.isaatc.simulation.sequential.WorkGroup;
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
	public final static SimulationTime RESSTART = new SimulationTime(SimulationTimeUnit.HOUR, 8);
	public final static SimulationTime RESPERIOD = SimulationTime.getDay();
	public final static SimulationTime RESAVAILABLE = new SimulationTime(SimulationTimeUnit.HOUR, 7);
	public final static SimulationTime GENSTART = SimulationTime.getZero();
	public final static SimulationTime GENPERIOD = SimulationTime.getDay();
	public final static SimulationTime []DEFACTDURATION = new SimulationTime [] {
		new SimulationTime(SimulationTimeUnit.MINUTE, 5),
		new SimulationTime(SimulationTimeUnit.MINUTE, 10),
		new SimulationTime(SimulationTimeUnit.MINUTE, 15),
		new SimulationTime(SimulationTimeUnit.MINUTE, 20),
		};
	public final static SimulationTime SIMSTART = SimulationTime.getZero();
	public final static SimulationTime SIMEND = SimulationTime.getDay();
	public final static SimulationTimeUnit SIMUNIT = SimulationTimeUnit.MINUTE; 
	private int resCounter = 0;	
	private int rtCounter = 0;
	private int actCounter = 0;
	private int etCounter = 0;
	protected boolean detailed;
	
	public WFPTestSimulation(int id, String description, boolean detailed) {
		super(id, description, SIMUNIT, SIMSTART, SIMEND);
		this.detailed = detailed;
	}
	
	public SimulationTimeFunction getActivityDefDuration() {
		return getActivityDefDuration(0);
	}

	public SimulationTimeFunction getActivityDefDuration(int nAct) {
		return new SimulationTimeFunction(this, "ConstantVariate", DEFACTDURATION[nAct]);		
	}

	public SimulationPeriodicCycle getResourceCycle() {
		return new SimulationPeriodicCycle(this, RESSTART, new SimulationTimeFunction(this, "ConstantVariate", RESPERIOD), 0);
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
    	act.addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", DEFACTDURATION[dur]), wg);
		return act;
	}
	
	public ElementType getDefElementType(String description) {
		return new ElementType(etCounter++, this, description);
	}
	
	public SimulationPeriodicCycle getGeneratorCycle() {
		return new SimulationPeriodicCycle(this, GENSTART, new SimulationTimeFunction(this, "ConstantVariate", GENPERIOD), 0);
	}
	
	public TimeDrivenGenerator getDefGenerator(ElementType et, InitializerFlow flow) {
		return getDefGenerator(DEFNELEMENTS, et, flow);
	}
	
	public TimeDrivenGenerator getDefGenerator(int elems, ElementType et, InitializerFlow flow) {
        return new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", elems), getElementType(0), flow), getGeneratorCycle());
	}
}
