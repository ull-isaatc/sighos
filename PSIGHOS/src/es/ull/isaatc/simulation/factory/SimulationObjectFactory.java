/**
 * 
 */
package es.ull.isaatc.simulation.factory;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.simulation.ElementCreator;
import es.ull.isaatc.simulation.ElementType;
import es.ull.isaatc.simulation.FlowDrivenActivity;
import es.ull.isaatc.simulation.Resource;
import es.ull.isaatc.simulation.ResourceType;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.SimulationCycle;
import es.ull.isaatc.simulation.TimeDrivenActivity;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.WorkGroup;
import es.ull.isaatc.simulation.TimeDrivenActivity.Modifier;
import es.ull.isaatc.simulation.condition.Condition;
import es.ull.isaatc.simulation.flow.Flow;
import es.ull.isaatc.simulation.flow.InitializerFlow;


/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface SimulationObjectFactory {
	Simulation getSimulation();
	Condition getCustomizedConditionInstance(String imports, String condition);
	ElementType getElementTypeInstance(String description) throws ClassCastException;
	ElementType getElementTypeInstance(String description, int priority) throws ClassCastException;
	ResourceType getResourceTypeInstance(String description) throws ClassCastException;
	ResourceType getResourceTypeInstance(String description, SimulationUserCode userMethods) throws ClassCastException;
	WorkGroup getWorkGroupInstance(ResourceType[] rts, int[] needed) throws ClassCastException;
	Resource getResourceInstance(String description) throws ClassCastException;
	TimeDrivenActivity getTimeDrivenActivityInstance(String description) throws ClassCastException;
	TimeDrivenActivity getTimeDrivenActivityInstance(String description, int priority, EnumSet<Modifier> modifiers) throws ClassCastException;
	FlowDrivenActivity getFlowDrivenActivityInstance(String description) throws ClassCastException;
	FlowDrivenActivity getFlowDrivenActivityInstance(String description, int priority) throws ClassCastException;
	ElementCreator getElementCreatorInstance(TimeFunction nElem) throws ClassCastException;
	ElementCreator getElementCreatorInstance(TimeFunction nElem, ElementType et, InitializerFlow flow) throws ClassCastException;
	ElementCreator getElementCreatorInstance(TimeFunction nElem, SimulationUserCode userMethods) throws ClassCastException;
	ElementCreator getElementCreatorInstance(TimeFunction nElem, ElementType et, InitializerFlow flow, SimulationUserCode userMethods) throws ClassCastException;
	TimeDrivenGenerator getTimeDrivenGeneratorInstance(ElementCreator creator, SimulationCycle cycle) throws ClassCastException;
	Flow getFlowInstance(String flowType, Object... params) throws ClassCastException;
	Flow getFlowInstance(String flowType, SimulationUserCode userMethods, Object... params) throws ClassCastException;
}
