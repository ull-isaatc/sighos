/**
 * 
 */
package es.ull.iis.simulation.factory;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationCycle;
import es.ull.iis.simulation.model.TimeDrivenElementGenerator;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.InitializerFlow;


/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimulationFactory {
	private final static String workingPkg = Simulation.class.getPackage().getName();
	private final Simulation simul;
	private int rtId;
	private int flowId;
	private int creId;
	private int condId;

	/**
	 * @param id
	 * @param description
	 * @param startTs
	 * @param endTs
	 */
	public SimulationFactory(int id, String description, long startTs, long endTs) {
		simul = new Simulation(id, description, startTs, endTs);
	}

	/**
	 * @param id
	 * @param description
	 * @param unit
	 * @param startTs
	 * @param endTs
	 */
	public SimulationFactory(int id, String description, TimeUnit unit, long startTs, long endTs) {
		simul = new Simulation(id, description, unit, startTs, endTs);
	}

	/**
	 * @param id
	 * @param description
	 * @param unit
	 * @param startTs
	 * @param endTs
	 */
	public SimulationFactory(int id, String description, TimeUnit unit, TimeStamp startTs, TimeStamp endTs) {
		simul = new Simulation(id, description, unit, startTs, endTs);
	}

	public Simulation getSimulation() {
		return simul;
	}

	public TimeDrivenElementGenerator getTimeDrivenElementGeneratorInstance(TimeFunction elem, SimulationCycle cycle) throws ClassCastException {
		return new TimeDrivenElementGenerator(simul, elem, cycle);
	}

	public TimeDrivenElementGenerator getTimeDrivenElementGeneratorInstance(TimeFunction elem, ElementType et, InitializerFlow flow, SimulationCycle cycle) throws ClassCastException {
		return new TimeDrivenElementGenerator(simul, elem,  et, flow, cycle);
	}

	public TimeDrivenElementGenerator getTimeDrivenElementGeneratorInstance(TimeFunction elem, SimulationCycle cycle, SimulationUserCode userMethods) throws ClassCastException {
		// Prepare the constructor call
		final String constructorStr = "(Simulation sim, TimeFunction nElem, SimulationCycle cycle) {super(sim, nElem, cycle);}";
		// Prepare the new params.
		final Object obj = StandardCompilator.getInstance(workingPkg, TimeDrivenElementGenerator.class.getName(), creId++, constructorStr, userMethods, simul, elem, cycle);
		if (obj != null)
			return (TimeDrivenElementGenerator)obj;
		return null;
	}

	public TimeDrivenElementGenerator getTimeDrivenElementGeneratorInstance(TimeFunction elem, ElementType et, InitializerFlow flow, SimulationCycle cycle, SimulationUserCode userMethods) throws ClassCastException {
		// Prepare the constructor call
		final String constructorStr = "(Simulation sim, TimeFunction nElem, ElementType et, InitializerFlow flow, SimulationCycle cycle) {super(sim, nElem, et, flow, cycle);}";
		// Prepare the new params.
		final Object obj = StandardCompilator.getInstance(workingPkg, TimeDrivenElementGenerator.class.getName(), creId++, constructorStr, userMethods, simul, elem, et, flow, cycle);
		if (obj != null)
			return (TimeDrivenElementGenerator)obj;
		return null;
	}
	public ElementType getElementTypeInstance(String description) throws ClassCastException {
		return new ElementType(simul, description);
	}

	public ElementType getElementTypeInstance(String description, int priority) throws ClassCastException {
		return new ElementType(simul, description, priority);
	}

	public Resource getResourceInstance(String description) throws ClassCastException {
		return new Resource(simul, description);
	}

	public ResourceType getResourceTypeInstance(String description) throws ClassCastException {
		return new ResourceType(simul, description);
	}

	public ResourceType getResourceTypeInstance(String description, SimulationUserCode userMethods) throws ClassCastException {
		// Prepare the constructor call
		String constructorStr = "(int id, Simulation simul, String description) {super(id, simul, description);}";
		// Prepare the new params.
		Object obj = StandardCompilator.getInstance(workingPkg, "ResourceType", rtId, constructorStr, userMethods, rtId++, simul, description);
		if (obj != null)
			return (ResourceType)obj;
		return null;
	}

	public WorkGroup getWorkGroupInstance(ResourceType[] rts, int[] needed) throws ClassCastException {
		ResourceType[] temp = new ResourceType[rts.length];
		for (int i = 0; i < rts.length; i++)
			temp[i] = (ResourceType)rts[i];
		return new WorkGroup(simul, temp, needed);
	}

	public Flow getFlowInstance(String flowType, Object... params) throws ClassCastException {
		return FlowFactory.getInstance(flowId++, flowType, simul, params);
	}

	public Flow getFlowInstance(String flowType, SimulationUserCode userMethods, Object... params) throws ClassCastException {
		return FlowFactory.getInstance(flowId++, flowType, userMethods, simul, params);
	}

	public Condition getCustomizedConditionInstance(String imports, String condition) {
		return ConditionFactory.getInstance(condId++, imports, condition);
	}
}	
