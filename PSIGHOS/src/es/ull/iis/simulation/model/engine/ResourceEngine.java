/**
 * 
 */
package es.ull.iis.simulation.model.engine;

import java.util.ArrayList;

import es.ull.iis.simulation.model.ActivityManager;
import es.ull.iis.simulation.model.FlowExecutor;
import es.ull.iis.simulation.model.SimulationCycle;
import es.ull.iis.simulation.model.ResourceType;

/**
 * A simulation resource whose availability is controlled by means of timetable entries.
 * A timetable entry us a trio &lt{@link ResourceTypeEngine}, {@link SimulationCycle}, long&gt which defines a 
 * resource type, an availability cycle, and the duration of each availability period. Timetable entries 
 * can be overlapped in time, thus allowing the resource for being potentially available for
 * different resource types simultaneously.
 * A resource finishes its execution when it has no longer valid timetable entries.
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface ResourceEngine extends EventSourceEngine {
	int incValidTimeTableEntries();
	int decValidTimeTableEntries();
	int getValidTimeTableEntries();
	void notifyCurrentManagers();
	ArrayList<ActivityManager> getCurrentManagers();
    /**
     * Returns the flow executor of the element which currently owns this resource.
     * @return The current flow executor .
     */
	FlowExecutor getCurrentFlowExecutor();
	boolean isAvailable(ResourceType rt);
	void addRole(ResourceType role, long ts);
	void removeRole(ResourceType role);
	void setNotCanceled(boolean available);
	boolean add2Solution(ResourceType rt, FlowExecutor fe);
	void removeFromSolution(FlowExecutor fe);
	long catchResource(FlowExecutor wt);
	boolean releaseResource();
}
