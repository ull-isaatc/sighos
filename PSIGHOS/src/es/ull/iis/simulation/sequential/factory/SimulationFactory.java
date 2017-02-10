/**
 * 
 */
package es.ull.iis.simulation.sequential.factory;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.core.ElementType;
import es.ull.iis.simulation.core.Resource;
import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.SimulationCycle;
import es.ull.iis.simulation.core.TimeDrivenGenerator;
import es.ull.iis.simulation.core.TimeStamp;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.factory.ConditionFactory;
import es.ull.iis.simulation.core.factory.SimulationObjectFactory;
import es.ull.iis.simulation.core.factory.SimulationUserCode;
import es.ull.iis.simulation.core.factory.StandardCompilator;
import es.ull.iis.simulation.core.flow.Flow;
import es.ull.iis.simulation.core.flow.InitializerFlow;
import es.ull.iis.simulation.sequential.Simulation;
import es.ull.iis.simulation.sequential.WorkThread;
import es.ull.iis.simulation.sequential.ElementCreator;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class SimulationFactory implements SimulationObjectFactory<Flow<WorkThread>, WorkThread> {
	private final static String workingPkg = "es.ull.iis.simulation.sequential";
	private final Simulation simul;
	private int rtId;
	private int creId;
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
	public Simulation getSimulation() {
		return simul;
	}

	@Override
	public ElementCreator getElementCreatorInstance(TimeFunction elem) throws ClassCastException {
		return new es.ull.iis.simulation.sequential.ElementCreator(simul, elem);
	}

	@Override
	public ElementCreator getElementCreatorInstance(TimeFunction elem, ElementType et, InitializerFlow<WorkThread> flow) throws ClassCastException {
		return new ElementCreator(simul, elem, (es.ull.iis.simulation.sequential.ElementType) et, flow);
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
	public ElementCreator getElementCreatorInstance(TimeFunction elem, ElementType et, InitializerFlow<WorkThread> flow, SimulationUserCode userMethods) throws ClassCastException {
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
		return new es.ull.iis.simulation.sequential.ElementType(simul, description);
	}

	@Override
	public ElementType getElementTypeInstance(String description, int priority) throws ClassCastException {
		return new es.ull.iis.simulation.sequential.ElementType(simul, description, priority);
	}

	@Override
	public Resource getResourceInstance(String description) throws ClassCastException {
		return new es.ull.iis.simulation.sequential.Resource(simul, description);
	}

	@Override
	public ResourceType getResourceTypeInstance(String description) throws ClassCastException {
		return new es.ull.iis.simulation.sequential.ResourceType(simul, description);
	}

	@Override
	public ResourceType getResourceTypeInstance(String description, SimulationUserCode userMethods) throws ClassCastException {
		// Prepare the constructor call
		String constructorStr = "(int id, Simulation simul, String description) {super(id, simul, description);}";
		// Prepare the new params.
		Object obj = StandardCompilator.getInstance(workingPkg, "ResourceType", rtId++, constructorStr, userMethods, simul, description);
		if (obj != null)
			return (ResourceType)obj;
		return null;
	}

	@Override
	public TimeDrivenGenerator getTimeDrivenGeneratorInstance(es.ull.iis.simulation.core.ElementCreator<WorkThread> creator, SimulationCycle cycle)
			throws ClassCastException {
		return new es.ull.iis.simulation.sequential.TimeDrivenGenerator(simul, (es.ull.iis.simulation.sequential.ElementCreator)creator, cycle);
	}

	@Override
	public WorkGroup getWorkGroupInstance(ResourceType[] rts, int[] needed) throws ClassCastException {
		es.ull.iis.simulation.sequential.ResourceType[] temp = new es.ull.iis.simulation.sequential.ResourceType[rts.length];
		for (int i = 0; i < rts.length; i++)
			temp[i] = (es.ull.iis.simulation.sequential.ResourceType)rts[i];
		return new es.ull.iis.simulation.sequential.WorkGroup(temp, needed);
	}

	@Override
	public Flow<WorkThread> getFlowInstance(String flowType, Object... params) throws ClassCastException {
		return (Flow<WorkThread>) FlowFactory.getInstance(flowType, simul, params);
	}

	@Override
	public Flow<WorkThread> getFlowInstance(String flowType, SimulationUserCode userMethods, Object... params)
			throws ClassCastException {
		return (Flow<WorkThread>) FlowFactory.getInstance(flowType, userMethods, simul, params);
	}

	public Condition getCustomizedConditionInstance(String imports, String condition) {
		return ConditionFactory.getInstance(simul, condId++, imports, condition);
	}

}
