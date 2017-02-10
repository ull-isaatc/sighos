/**
 * 
 */
package es.ull.iis.simulation.core.factory;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.core.ElementCreator;
import es.ull.iis.simulation.core.ElementType;
import es.ull.iis.simulation.core.Resource;
import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.core.SimulationCycle;
import es.ull.iis.simulation.core.TimeDrivenGenerator;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.WorkThread;
import es.ull.iis.simulation.core.flow.Flow;
import es.ull.iis.simulation.core.flow.InitializerFlow;


/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface SimulationObjectFactory<F extends Flow<WT>, WT extends WorkThread<F>> {
	Simulation<WT> getSimulation();
	Condition getCustomizedConditionInstance(String imports, String condition);
	ElementType getElementTypeInstance(String description) throws ClassCastException;
	ElementType getElementTypeInstance(String description, int priority) throws ClassCastException;
	ResourceType getResourceTypeInstance(String description) throws ClassCastException;
	ResourceType getResourceTypeInstance(String description, SimulationUserCode userMethods) throws ClassCastException;
	WorkGroup getWorkGroupInstance(ResourceType[] rts, int[] needed) throws ClassCastException;
	Resource getResourceInstance(String description) throws ClassCastException;
	ElementCreator<WT> getElementCreatorInstance(TimeFunction nElem) throws ClassCastException;
	ElementCreator<WT> getElementCreatorInstance(TimeFunction nElem, ElementType et, InitializerFlow<WT> flow) throws ClassCastException;
	ElementCreator<WT> getElementCreatorInstance(TimeFunction nElem, SimulationUserCode userMethods) throws ClassCastException;
	ElementCreator<WT> getElementCreatorInstance(TimeFunction nElem, ElementType et, InitializerFlow<WT> flow, SimulationUserCode userMethods) throws ClassCastException;
	TimeDrivenGenerator getTimeDrivenGeneratorInstance(ElementCreator<WT> creator, SimulationCycle cycle) throws ClassCastException;
	F getFlowInstance(String flowType, Object... params) throws ClassCastException;
	F getFlowInstance(String flowType, SimulationUserCode userMethods, Object... params) throws ClassCastException;
}
