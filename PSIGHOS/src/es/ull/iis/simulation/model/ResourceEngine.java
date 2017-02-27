/**
 * 
 */
package es.ull.iis.simulation.model;

import java.util.ArrayList;

/**
 * A simulation resource whose availability is controlled by means of timetable entries.
 * A timetable entry us a trio &lt{@link ResourceTypeEngine}, {@link ModelCycle}, long&gt which defines a 
 * resource type, an availability cycle, and the duration of each availability period. Timetable entries 
 * can be overlapped in time, thus allowing the resource for being potentially available for
 * different resource types simultaneously.
 * A resource finishes its execution when it has no longer valid timetable entries.
 * @author Iván Castilla Rodríguez
 *
 */
public interface ResourceEngine {
	int incValidTimeTableEntries();
	int decValidTimeTableEntries();
	int getValidTimeTableEntries();
	void notifyCurrentManagers();
	ArrayList<ActivityManager> getCurrentManagers();
	ResourceType getCurrentResourceType();
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
