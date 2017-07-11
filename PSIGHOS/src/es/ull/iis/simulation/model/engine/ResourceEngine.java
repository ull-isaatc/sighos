/**
 * 
 */
package es.ull.iis.simulation.model.engine;

import java.util.ArrayDeque;
import java.util.ArrayList;

import es.ull.iis.simulation.model.ActivityManager;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.SimulationCycle;

/**
 * A simulation resource whose availability is controlled by means of timetable entries.
 * A timetable entry us a trio &lt{@link ResourceTypeEngine}, {@link SimulationCycle}, long&gt which defines a 
 * resource type, an availability cycle, and the duration of each availability period. Timetable entries 
 * can be overlapped in time, thus allowing the resource for being potentially available for
 * different resource types simultaneously.
 * A resource finishes its execution when it has no longer valid timetable entries.
 * @author Iván Castilla Rodríguez
 *
 */
public interface ResourceEngine extends EventSourceEngine {
	int incValidTimeTableEntries();
	int decValidTimeTableEntries();
	int getValidTimeTableEntries();
	void notifyCurrentManagers();
	ArrayList<ActivityManager> getCurrentManagers();
    /**
     * Returns the element which currently owns this resource.
     * @return The current element .
     */
	Element getCurrentElement();
	boolean isAvailable(ResourceType rt);
	void addRole(ResourceType role, long ts);
	void removeRole(ResourceType role);
	void setNotCanceled(boolean available);
	boolean add2Solution(ArrayDeque<Resource> solution, ResourceType rt, ElementInstance ei);
	void removeFromSolution(ArrayDeque<Resource> solution, ElementInstance ei);
	long catchResource(ElementInstance ei);
	boolean releaseResource(ElementInstance ei);
}
