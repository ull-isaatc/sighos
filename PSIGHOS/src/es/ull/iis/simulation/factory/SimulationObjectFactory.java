/**
 * 
 */
package es.ull.iis.simulation.factory;

import java.util.EnumSet;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.core.Activity;
import es.ull.iis.simulation.core.ElementCreator;
import es.ull.iis.simulation.core.ElementType;
import es.ull.iis.simulation.core.Resource;
import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.core.SimulationCycle;
import es.ull.iis.simulation.core.Activity.Modifier;
import es.ull.iis.simulation.core.TimeDrivenGenerator;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.Flow;
import es.ull.iis.simulation.core.flow.InitializerFlow;


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
	Activity getActivityInstance(String description) throws ClassCastException;
	Activity getActivityInstance(String description, int priority, EnumSet<Modifier> modifiers) throws ClassCastException;
	Activity getActivityInstance(String description, int priority) throws ClassCastException;
	ElementCreator getElementCreatorInstance(TimeFunction nElem) throws ClassCastException;
	ElementCreator getElementCreatorInstance(TimeFunction nElem, ElementType et, InitializerFlow flow) throws ClassCastException;
	ElementCreator getElementCreatorInstance(TimeFunction nElem, SimulationUserCode userMethods) throws ClassCastException;
	ElementCreator getElementCreatorInstance(TimeFunction nElem, ElementType et, InitializerFlow flow, SimulationUserCode userMethods) throws ClassCastException;
	TimeDrivenGenerator getTimeDrivenGeneratorInstance(ElementCreator creator, SimulationCycle cycle) throws ClassCastException;
	Flow getFlowInstance(String flowType, Object... params) throws ClassCastException;
	Flow getFlowInstance(String flowType, SimulationUserCode userMethods, Object... params) throws ClassCastException;
}
