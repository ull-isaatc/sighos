/**
 * 
 */
package es.ull.isaatc.simulation.common.factory;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.simulation.common.ElementCreator;
import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.FlowDrivenActivity;
import es.ull.isaatc.simulation.common.SimulationCycle;
import es.ull.isaatc.simulation.common.Resource;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.TimeDrivenGenerator;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.TimeDrivenActivity.Modifier;
import es.ull.isaatc.simulation.common.flow.Flow;
import es.ull.isaatc.simulation.common.flow.InitializerFlow;
import es.ull.isaatc.simulation.common.condition.Condition;


/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface SimulationObjectFactory {
	Simulation getSimulation();
	Condition getCustomizedConditionInstance(int id, String imports, String condition);
	ElementType getElementTypeInstance(int id, String description) throws ClassCastException;
	ElementType getElementTypeInstance(int id, String description, int priority) throws ClassCastException;
	ResourceType getResourceTypeInstance(int id, String description) throws ClassCastException;
	WorkGroup getWorkGroupInstance(int id, ResourceType[] rts, int[] needed) throws ClassCastException;
	Resource getResourceInstance(int id, String description) throws ClassCastException;
	TimeDrivenActivity getTimeDrivenActivityInstance(int id, String description) throws ClassCastException;
	TimeDrivenActivity getTimeDrivenActivityInstance(int id, String description, int priority, EnumSet<Modifier> modifiers) throws ClassCastException;
	FlowDrivenActivity getFlowDrivenActivityInstance(int id, String description) throws ClassCastException;
	FlowDrivenActivity getFlowDrivenActivityInstance(int id, String description, int priority) throws ClassCastException;
	ElementCreator getElementCreatorInstance(int id, TimeFunction nElem) throws ClassCastException;
	ElementCreator getElementCreatorInstance(int id, TimeFunction nElem, ElementType et, InitializerFlow flow) throws ClassCastException;
	TimeDrivenGenerator getTimeDrivenGeneratorInstance(int id, ElementCreator creator, SimulationCycle cycle) throws ClassCastException;
	Flow getFlowInstance(int id, String flowType, Object... params) throws ClassCastException;
	ResourceType getResourceTypeInstance(int id, String description, SimulationUserCode userMethods) throws ClassCastException;
	ElementCreator getElementCreatorInstance(int id, TimeFunction nElem, SimulationUserCode userMethods) throws ClassCastException;
	ElementCreator getElementCreatorInstance(int id, TimeFunction nElem, ElementType et, InitializerFlow flow, SimulationUserCode userMethods) throws ClassCastException;
	Flow getFlowInstance(int id, String flowType, SimulationUserCode userMethods, Object... params) throws ClassCastException;
}
