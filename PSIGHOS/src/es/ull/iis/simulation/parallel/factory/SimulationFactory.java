/**
 * 
 */
package es.ull.iis.simulation.parallel.factory;

import java.util.EnumSet;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.core.ElementCreator;
import es.ull.iis.simulation.core.ElementType;
import es.ull.iis.simulation.core.TimeDrivenGenerator;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.factory.ConditionFactory;
import es.ull.iis.simulation.core.factory.SimulationObjectFactory;
import es.ull.iis.simulation.core.factory.SimulationUserCode;
import es.ull.iis.simulation.core.factory.StandardCompilator;
import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.simulation.core.flow.Flow;
import es.ull.iis.simulation.core.flow.InitializerFlow;
import es.ull.iis.simulation.model.SimulationCycle;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.engine.ResourceEngine;
import es.ull.iis.simulation.model.engine.ResourceTypeEngine;
import es.ull.iis.simulation.parallel.ParallelSimulationEngine;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimulationFactory implements SimulationObjectFactory {
	private final static String workingPkg = ParallelSimulationEngine.class.getPackage().getName();
	private ParallelSimulationEngine simul;
	private int rtId;
	private int flowId;
	private int creId;
	private int condId;

	/**
	 * @param id
	 * @param description
	 * @param unit
	 * @param startTs
	 * @param endTs
	 */
	public SimulationFactory(int id, String description, TimeUnit unit,	TimeStamp startTs, TimeStamp endTs) {
		simul = new ParallelSimulationEngine(id, description, unit, startTs, endTs);
	}

	/**
	 * @param id
	 * @param description
	 * @param unit
	 * @param startTs
	 * @param endTs
	 */
	public SimulationFactory(int id, String description, TimeUnit unit,	long startTs, long endTs) {
		simul = new ParallelSimulationEngine(id, description, unit, startTs, endTs);
	}

	@Override
	public es.ull.iis.simulation.model.engine.SimulationEngine getSimulation() {
		return simul;
	}

	@Override
	public ElementCreator getElementCreatorInstance(TimeFunction elem) throws ClassCastException {
		return new es.ull.iis.simulation.parallel.ElementCreator(simul, elem);
	}

	@Override
	public ElementCreator getElementCreatorInstance(TimeFunction elem, ElementType et, InitializerFlow flow) throws ClassCastException {
		return new es.ull.iis.simulation.parallel.ElementCreator(simul, elem, 
				(es.ull.iis.simulation.parallel.ElementType)et, 
				(es.ull.iis.simulation.parallel.flow.InitializerFlow)flow);
	}

	@Override
	public ElementCreator getElementCreatorInstance(TimeFunction elem, SimulationUserCode userMethods) throws ClassCastException {
		// Prepare the constructor call
		String constructorStr = "(ParallelSimulationEngine sim, TimeFunction nElem) {super(sim, nElem);}";
		// Prepare the new params.
		Object obj = StandardCompilator.getInstance(workingPkg, "ElementCreator", creId++, constructorStr, userMethods, simul, elem);
		if (obj != null)
			return (ElementCreator)obj;
		return null;
	}

	@Override
	public ElementCreator getElementCreatorInstance(TimeFunction elem, ElementType et, InitializerFlow flow, SimulationUserCode userMethods) throws ClassCastException {
		// Prepare the constructor call
		String constructorStr = "(ParallelSimulationEngine sim, TimeFunction nElem, ElementType et, InitializerFlow flow) {super(sim, nElem, et, flow);}";
		// Prepare the new params.
		Object obj = StandardCompilator.getInstance(workingPkg, "ElementCreator", creId++, constructorStr, userMethods, simul, elem, et, flow);
		if (obj != null)
			return (ElementCreator)obj;
		return null;
	}

	@Override
	public ElementType getElementTypeInstance(String description) throws ClassCastException {
		return new es.ull.iis.simulation.parallel.ElementType(simul, description);
	}

	@Override
	public ElementType getElementTypeInstance(String description, int priority) throws ClassCastException {
		return new es.ull.iis.simulation.parallel.ElementType(simul, description, priority);
	}

	@Override
	public ResourceEngine getResourceInstance(String description) throws ClassCastException {
		return new es.ull.iis.simulation.parallel.Resource(simul, description);
	}

	@Override
	public ResourceTypeEngine getResourceTypeInstance(String description) throws ClassCastException {
		return new es.ull.iis.simulation.parallel.ResourceType(simul, description);
	}

	@Override
	public ResourceTypeEngine getResourceTypeInstance(String description, SimulationUserCode userMethods) throws ClassCastException {
		// Prepare the constructor call
		String constructorStr = "(ParallelSimulationEngine simul, String description) {super(simul, description);}";
		// Prepare the new params.
		Object obj = StandardCompilator.getInstance(workingPkg, "ResourceType", rtId++, constructorStr, userMethods, simul, description);
		if (obj != null)
			return (ResourceTypeEngine)obj;
		return null;
	}

	@Override
	public ActivityFlow getActivityInstance(String description) throws ClassCastException {
		return new es.ull.iis.simulation.parallel.Activity(simul, description);
	}

	@Override
	public ActivityFlow getActivityInstance(String description, int priority,
			EnumSet<es.ull.iis.simulation.core.flow.ActivityFlow.Modifier> modifiers) throws ClassCastException {
		return new es.ull.iis.simulation.parallel.Activity(simul, description, priority, modifiers);
	}

	@Override
	public ActivityFlow getActivityInstance(String description, int priority) throws ClassCastException {
		return new es.ull.iis.simulation.parallel.Activity(simul, description, priority);
	}

	@Override
	public TimeDrivenGenerator getTimeDrivenGeneratorInstance(ElementCreator creator, SimulationCycle cycle) throws ClassCastException {
		return new es.ull.iis.simulation.parallel.TimeDrivenGenerator(simul, (es.ull.iis.simulation.parallel.ElementCreator)creator, cycle);
	}

	@Override
	public WorkGroup getWorkGroupInstance(ResourceTypeEngine[] rts, int[] needed) throws ClassCastException {
		es.ull.iis.simulation.parallel.ResourceType[] temp = new es.ull.iis.simulation.parallel.ResourceType[rts.length];
		for (int i = 0; i < rts.length; i++)
			temp[i] = (es.ull.iis.simulation.parallel.ResourceType)rts[i];
		return new es.ull.iis.simulation.parallel.WorkGroup(temp, needed);
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
