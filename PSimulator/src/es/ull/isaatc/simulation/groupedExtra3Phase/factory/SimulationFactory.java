/**
 * 
 */
package es.ull.isaatc.simulation.groupedExtra3Phase.factory;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.simulation.common.ElementCreator;
import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.FlowDrivenActivity;
import es.ull.isaatc.simulation.common.SimulationCycle;
import es.ull.isaatc.simulation.common.Resource;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.TimeDrivenGenerator;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.TimeDrivenActivity.Modifier;
import es.ull.isaatc.simulation.common.condition.Condition;
import es.ull.isaatc.simulation.common.factory.ConditionFactory;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.factory.SimulationUserCode;
import es.ull.isaatc.simulation.common.factory.StandardCompilator;
import es.ull.isaatc.simulation.common.flow.Flow;
import es.ull.isaatc.simulation.common.flow.InitializerFlow;
import es.ull.isaatc.simulation.groupedExtra3Phase.Simulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimulationFactory implements SimulationObjectFactory {
	private final static String workingPkg = Simulation.class.getPackage().getName();
	private Simulation simul;
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
	public es.ull.isaatc.simulation.common.Simulation getSimulation() {
		return simul;
	}

	@Override
	public ElementCreator getElementCreatorInstance(TimeFunction elem) throws ClassCastException {
		return new es.ull.isaatc.simulation.groupedExtra3Phase.ElementCreator(simul, elem);
	}

	@Override
	public ElementCreator getElementCreatorInstance(TimeFunction elem, ElementType et, InitializerFlow flow) throws ClassCastException {
		return new es.ull.isaatc.simulation.groupedExtra3Phase.ElementCreator(simul, elem, 
				(es.ull.isaatc.simulation.groupedExtra3Phase.ElementType)et, 
				(es.ull.isaatc.simulation.groupedExtra3Phase.flow.InitializerFlow)flow);
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
		return new es.ull.isaatc.simulation.groupedExtra3Phase.ElementType(etId++, simul, description);
	}

	@Override
	public ElementType getElementTypeInstance(String description, int priority) throws ClassCastException {
		return new es.ull.isaatc.simulation.groupedExtra3Phase.ElementType(etId++, simul, description, priority);
	}

	@Override
	public Resource getResourceInstance(String description) throws ClassCastException {
		return new es.ull.isaatc.simulation.groupedExtra3Phase.Resource(resId++, simul, description);
	}

	@Override
	public ResourceType getResourceTypeInstance(String description) throws ClassCastException {
		return new es.ull.isaatc.simulation.groupedExtra3Phase.ResourceType(rtId++, simul, description);
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
		return new es.ull.isaatc.simulation.groupedExtra3Phase.TimeDrivenActivity(actId++, simul, description);
	}

	@Override
	public TimeDrivenActivity getTimeDrivenActivityInstance(String description, int priority, EnumSet<Modifier> modifiers) throws ClassCastException {
		return new es.ull.isaatc.simulation.groupedExtra3Phase.TimeDrivenActivity(actId++, simul, description, priority, modifiers);
	}

	@Override
	public FlowDrivenActivity getFlowDrivenActivityInstance(String description) throws ClassCastException {
		return new es.ull.isaatc.simulation.groupedExtra3Phase.FlowDrivenActivity(actId++, simul, description);
	}

	@Override
	public FlowDrivenActivity getFlowDrivenActivityInstance(String description, int priority) throws ClassCastException {
		return new es.ull.isaatc.simulation.groupedExtra3Phase.FlowDrivenActivity(actId++, simul, description, priority);
	}

	@Override
	public TimeDrivenGenerator getTimeDrivenGeneratorInstance(ElementCreator creator, SimulationCycle cycle) throws ClassCastException {
		return new es.ull.isaatc.simulation.groupedExtra3Phase.TimeDrivenGenerator(simul, (es.ull.isaatc.simulation.groupedExtra3Phase.ElementCreator)creator, cycle);
	}

	@Override
	public WorkGroup getWorkGroupInstance(ResourceType[] rts, int[] needed) throws ClassCastException {
		es.ull.isaatc.simulation.groupedExtra3Phase.ResourceType[] temp = new es.ull.isaatc.simulation.groupedExtra3Phase.ResourceType[rts.length];
		for (int i = 0; i < rts.length; i++)
			temp[i] = (es.ull.isaatc.simulation.groupedExtra3Phase.ResourceType)rts[i];
		return new es.ull.isaatc.simulation.groupedExtra3Phase.WorkGroup(temp, needed);
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
