/**
 * 
 */
package es.ull.isaatc.simulation.groupedExtraPasiveThreads;

import java.util.List;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface EventExecutor {
	void addEvent(BasicElement.DiscreteEvent event);
	void addEvents(List<BasicElement.DiscreteEvent> eventList);
	void addWaitingEvent(BasicElement.DiscreteEvent event);

}
