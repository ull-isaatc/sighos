/**
 * 
 */
package es.ull.isaatc.simulation.sequential.factory;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.simulation.ElementCreator;
import es.ull.isaatc.simulation.ElementType;
import es.ull.isaatc.simulation.FlowDrivenActivity;
import es.ull.isaatc.simulation.Resource;
import es.ull.isaatc.simulation.ResourceType;
import es.ull.isaatc.simulation.SimulationCycle;
import es.ull.isaatc.simulation.TimeDrivenActivity;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.TimeStamp;
import es.ull.isaatc.simulation.TimeUnit;
import es.ull.isaatc.simulation.WorkGroup;
import es.ull.isaatc.simulation.TimeDrivenActivity.Modifier;
import es.ull.isaatc.simulation.condition.Condition;
import es.ull.isaatc.simulation.factory.ConditionFactory;
import es.ull.isaatc.simulation.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.factory.SimulationUserCode;
import es.ull.isaatc.simulation.factory.StandardCompilator;
import es.ull.isaatc.simulation.flow.Flow;
import es.ull.isaatc.simulation.flow.InitializerFlow;
import es.ull.isaatc.simulation.sequential.Simulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimulationFactory implements SimulationObjectFactory {
	private final static String workingPkg = "es.ull.isaatc.simulation.sequential";
	private final Simulation simul;
	private int rtId;
	private int resId;
	private int actId;
	private int flowId;
	private int creId;
	private int etId;
	private int condId;

	/**
	 * @param id
	 * @param description
	 * @param unit
	 */
	public SimulationFactory(int id, String description, TimeUnit unit) {
		simul = new Simulation(id, description, unit);
	}

	/**
	 * @param id
	 * @param description
	 * @param unit
	 * @param startTs
	 * @param endTs
	 */
	public SimulationFactory(int id, String description, TimeUnit unit,	TimeStamp startTs, TimeStamp endTs) {
		simul = new Simulation(id, description, unit, startTs, endTs);
	}

	/**
	 * @param id
	 * @param description
	 * @param unit
	 * @param startTs
	 * @param endTs
	 */
	public SimulationFactory(int id, String description, TimeUnit unit,	long startTs, long endTs) {
		simul = new Simulation(id, description, unit, startTs, endTs);
	}

	@Override
	public es.ull.isaatc.simulation.Simulation getSimulation() {
		return simul;
	}

	@Override
	public ElementCreator getElementCreatorInstance(TimeFunction elem) throws ClassCastException {
		return new es.ull.isaatc.simulation.sequential.ElementCreator(simul, elem);
	}

	@Override
	public ElementCreator getElementCreatorInstance(TimeFunction elem, ElementType et, InitializerFlow flow) throws ClassCastException {
		return new es.ull.isaatc.simulation.sequential.ElementCreator(simul, elem, 
				(es.ull.isaatc.simulation.sequential.ElementType)et, 
				(es.ull.isaatc.simulation.sequential.flow.InitializerFlow)flow);
	}

	@Override
	public ElementCreator getElementCreatorInstance(TimeFunction elem, SimulationUserCode userMethods) throws ClassCastException {
		// Prepare the constructor call
		String constructorStr = "(Simulation sim, TimeFunction nElem) {super(sim, nElem);}";
		// Prepare the new params.
		Object obj = StandardCompilator.getInstance(workingPkg, "ElementCreator", creId++, constructorStr, userMethods, simul, elem);
		if (obj != null)
			return (ElementCreator)obj;
		return null;
	}

	@Override
	public ElementCreator getElementCreatorInstance(TimeFunction elem, ElementType et, InitializerFlow flow, SimulationUserCode userMethods) throws ClassCastException {
		// Prepare the constructor call
		String constructorStr = "(Simulation sim, TimeFunction nElem, ElementType et, InitializerFlow flow) {super(sim, nElem, et, flow);}";
		// Prepare the new params.
		Object obj = StandardCompilator.getInstance(workingPkg, "ElementCreator", creId++, constructorStr, userMethods, simul, elem, et, flow);
		if (obj != null)
			return (ElementCreator)obj;
		return null;
	}

	@Override
	public ElementType getElementTypeInstance(String description) throws ClassCastException {
		return new es.ull.isaatc.simulation.sequential.ElementType(etId++, simul, description);
	}

	@Override
	public ElementType getElementTypeInstance(String description, int priority) throws ClassCastException {
		return new es.ull.isaatc.simulation.sequential.ElementType(etId++, simul, description, priority);
	}

	@Override
	public Resource getResourceInstance(String description) throws ClassCastException {
		return new es.ull.isaatc.simulation.sequential.Resource(resId++, simul, description);
	}

	@Override
	public ResourceType getResourceTypeInstance(String description) throws ClassCastException {
		return new es.ull.isaatc.simulation.sequential.ResourceType(rtId++, simul, description);
	}

	@Override
	public ResourceType getResourceTypeInstance(String description, SimulationUserCode userMethods) throws ClassCastException {
		// Prepare the constructor call
		String constructorStr = "(int id, Simulation simul, String description) {super(id, simul, description);}";
		// Prepare the new params.
		Object obj = StandardCompilator.getInstance(workingPkg, "ResourceType", rtId, constructorStr, userMethods, rtId++, simul, description);
		if (obj != null)
			return (ResourceType)obj;
		return null;
	}

	@Override
	public TimeDrivenActivity getTimeDrivenActivityInstance(String description) throws ClassCastException {
		return new es.ull.isaatc.simulation.sequential.TimeDrivenActivity(actId++, simul, description);
	}

	@Override
	public TimeDrivenActivity getTimeDrivenActivityInstance(String description, int priority, EnumSet<Modifier> modifiers) throws ClassCastException {
		return new es.ull.isaatc.simulation.sequential.TimeDrivenActivity(actId++, simul, description, priority, modifiers);
	}

	@Override
	public FlowDrivenActivity getFlowDrivenActivityInstance(String description) throws ClassCastException {
		return new es.ull.isaatc.simulation.sequential.FlowDrivenActivity(actId++, simul, description);
	}

	@Override
	public FlowDrivenActivity getFlowDrivenActivityInstance(String description, int priority) throws ClassCastException {
		return new es.ull.isaatc.simulation.sequential.FlowDrivenActivity(actId++, simul, description, priority);
	}

	@Override
	public TimeDrivenGenerator getTimeDrivenGeneratorInstance(ElementCreator creator, SimulationCycle cycle) throws ClassCastException {
		return new es.ull.isaatc.simulation.sequential.TimeDrivenGenerator(simul, (es.ull.isaatc.simulation.sequential.ElementCreator)creator, cycle);
	}

	@Override
	public WorkGroup getWorkGroupInstance(ResourceType[] rts, int[] needed) throws ClassCastException {
		es.ull.isaatc.simulation.sequential.ResourceType[] temp = new es.ull.isaatc.simulation.sequential.ResourceType[rts.length];
		for (int i = 0; i < rts.length; i++)
			temp[i] = (es.ull.isaatc.simulation.sequential.ResourceType)rts[i];
		return new es.ull.isaatc.simulation.sequential.WorkGroup(temp, needed);
	}

	@Override
	public Flow getFlowInstance(String flowType, Object... params) throws ClassCastException {
		return FlowFactory.getInstance(flowId++, flowType, simul, params);
	}

	@Override
	public Flow getFlowInstance(String flowType, SimulationUserCode userMethods, Object... params)
			throws ClassCastException {
		return FlowFactory.getInstance(flowId++, flowType, userMethods, simul, params);
	}

	public Condition getCustomizedConditionInstance(String imports, String condition) {
		return ConditionFactory.getInstance(simul, condId++, imports, condition);
	}
}
