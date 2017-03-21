/**
 * 
 */
package es.ull.iis.simulation.parallel;

import es.ull.iis.simulation.model.DiscreteEvent;

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
	 * Adds a new event to this executor. The event is either added to the execution local buffer or 
	 * the future event local buffer. 
	 * @param event New event to be added.
	 */
	void addEvent(DiscreteEvent event);

}
