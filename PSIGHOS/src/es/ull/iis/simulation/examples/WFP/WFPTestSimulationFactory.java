/**
 * 
 */
package es.ull.iis.simulation.examples.WFP;

import java.util.EnumSet;

import es.ull.iis.simulation.core.ElementType;
import es.ull.iis.simulation.core.Resource;
import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.core.TimeDrivenGenerator;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.factory.SimulationFactory;
import es.ull.iis.simulation.core.factory.SimulationObjectFactory;
import es.ull.iis.simulation.core.factory.SimulationUserCode;
import es.ull.iis.simulation.core.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.simulation.core.flow.InitializerFlow;
import es.ull.iis.simulation.model.ModelPeriodicCycle;
import es.ull.iis.simulation.model.ModelTimeFunction;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.function.TimeFunctionFactory;

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
 * @author Iv�n Castilla Rodr�guez
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
	
	public ModelTimeFunction getActivityDefDuration() {
		return getActivityDefDuration(0);
	}

	public ModelTimeFunction getActivityDefDuration(int nAct) {
		return new ModelTimeFunction(SIMUNIT, "ConstantVariate", DEFACTDURATION[nAct]);		
	}

	public ModelPeriodicCycle getResourceCycle() {
		return new ModelPeriodicCycle(SIMUNIT, RESSTART, new ModelTimeFunction(SIMUNIT, "ConstantVariate", RESPERIOD), 0);
	}
	
	public Resource getDefResource(String description, ResourceType rt) {
		Resource res = factory.getResourceInstance(description);
		res.addTimeTableEntry(getResourceCycle(), RESAVAILABLE, rt);
		return res;
	}
	
	public ResourceType getDefResourceType(String description) {
		return factory.getResourceTypeInstance(description);
	}
	
	public ActivityFlow<?,?> getDefActivity(String description, WorkGroup wg) {
		return getDefActivity(description, 0, wg, true);
	}
	
	public ActivityFlow<?,?> getDefActivity(String description, WorkGroup wg, boolean presential) {
		return getDefActivity(description, 0, wg, presential);
	}
	
	public ActivityFlow<?,?> getDefActivity(String description, int dur, WorkGroup wg) {
		return getDefActivity(description, dur, wg, true);
	}
	
	public ActivityFlow<?,?> getDefActivity(String description, int dur, WorkGroup wg, boolean presential) {
		ActivityFlow<?,?> act = null;
		if (!presential)
			act = (ActivityFlow<?,?>)factory.getFlowInstance("ActivityFlow", description, 0, EnumSet.of(ActivityFlow.Modifier.NONPRESENTIAL));
		else
			act = (ActivityFlow<?,?>)factory.getFlowInstance("ActivityFlow", description);
    	act.addWorkGroup(new ModelTimeFunction(SIMUNIT, "ConstantVariate", DEFACTDURATION[dur]), 0, wg);
		return act;
	}
	
	public ActivityFlow<?,?> getDefActivity(SimulationUserCode code, String description, WorkGroup wg) {
		return getDefActivity(code, description, 0, wg, true);
	}
	
	public ActivityFlow<?,?> getDefActivity(SimulationUserCode code, String description, WorkGroup wg, boolean presential) {
		return getDefActivity(code, description, 0, wg, presential);
	}
	
	public ActivityFlow<?,?> getDefActivity(SimulationUserCode code, String description, int dur, WorkGroup wg) {
		return getDefActivity(code, description, dur, wg, true);
	}
	
	public ActivityFlow<?,?> getDefActivity(SimulationUserCode code, String description, int dur, WorkGroup wg, boolean presential) {
		ActivityFlow<?,?> act = null;
		if (!presential)
			act = (ActivityFlow<?,?>)factory.getFlowInstance("ActivityFlow", code, description, 0, EnumSet.of(ActivityFlow.Modifier.NONPRESENTIAL));
		else
			act = (ActivityFlow<?,?>)factory.getFlowInstance("ActivityFlow", code, description);
    	act.addWorkGroup(new ModelTimeFunction(SIMUNIT, "ConstantVariate", DEFACTDURATION[dur]), 0, wg);
		return act;
	}
	
	public ElementType getDefElementType(String description) {
		return factory.getElementTypeInstance(description);
	}
	
	public ModelPeriodicCycle getGeneratorCycle() {
		return new ModelPeriodicCycle(SIMUNIT, GENSTART, new ModelTimeFunction(SIMUNIT, "ConstantVariate", GENPERIOD), 0);
	}
	
	public TimeDrivenGenerator getDefGenerator(ElementType et, InitializerFlow flow) {
		return getDefGenerator(DEFNELEMENTS, et, flow);
	}
	
	public TimeDrivenGenerator getDefGenerator(int elems, ElementType et, InitializerFlow flow) {
        return factory.getTimeDrivenGeneratorInstance(
        		factory.getElementCreatorInstance(TimeFunctionFactory.getInstance("ConstantVariate", elems), factory.getSimulation().getElementType(0), flow), getGeneratorCycle());
	}
}
