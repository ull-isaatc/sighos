/**
 * 
 */
package es.ull.isaatc.simulation.groupedExtra3Phase;

import java.util.List;

/**
 * A class capable to run events. A class implementing this interface must include two local
 * buffers: a local execution queue and a local future event list. When an event being executed 
 * schedules a new event it is either added to one or the other buffer according to its timestamp.
 * Events with timestamp equal to the current simulation time are added to the local execution buffer
 * and subsequently executed. Events with timestamp higher than the current simulation time are added 
 * to the local future event buffer and later added to the global future event list.
 * @author Iván Castilla Rodríguez
 */
public interface EventExecutor {
	/**
	 * Adds a new event to the execution local buffer of this executor. 
	 * @param event New event to be added.
	 */
	void addLocalEvent(BasicElement.DiscreteEvent event);
	/**
	 * Adds a collection of events to this executor to be executed immediately 
	 * @param eventList A collection of events
	 */
	void addEvents(List<BasicElement.DiscreteEvent> eventList);
    /**
     * Sends an event to the local waiting queue. An event is added to the waiting queue if 
     * its timestamp is higher than the simulation time.
     * @param event Event to be added
     */
	void addWaitingEvent(BasicElement.DiscreteEvent event);
}
