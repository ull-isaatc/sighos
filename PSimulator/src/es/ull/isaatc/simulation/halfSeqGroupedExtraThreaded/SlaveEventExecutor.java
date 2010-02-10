/**
 * 
 */
package es.ull.isaatc.simulation.halfSeqGroupedExtraThreaded;

import java.util.ArrayDeque;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SlaveEventExecutor extends Thread implements EventExecutor {
	private ArrayDeque<BasicElement.DiscreteEvent> events = new ArrayDeque<BasicElement.DiscreteEvent>();
	private ArrayDeque<BasicElement.DiscreteEvent> extraWaitingEvents = new ArrayDeque<BasicElement.DiscreteEvent>();
	private AtomicBoolean flag = new AtomicBoolean(false);
	private Simulation simul;
	
	public SlaveEventExecutor(Simulation simul, int index) {
		super("LPExec-" + index);
		this.simul = simul;
	}
	
	/**
	 * @param event the event to set
	 */
	public void addEvent(BasicElement.DiscreteEvent event) {
		events.push(event);
	}

	/**
	 * @param event the event to set
	 */
	public void addEvents(List<BasicElement.DiscreteEvent> eventList) {
		events.addAll(eventList);
		flag.set(true);
	}

	/**
	 * @param event the event to set
	 */
	public void addWaitingEvent(BasicElement.DiscreteEvent event) {
		extraWaitingEvents.push(event);
	}

	public ArrayDeque<BasicElement.DiscreteEvent> getWaitingEvents() {
		return extraWaitingEvents;
	}
	
	@Override
	public void run() {
		long endTs = simul.getInternalEndTs();
		while (simul.getTs() < endTs) {
			if (flag.compareAndSet(true, false)) {
				while (!events.isEmpty()) {
					events.pop().run();
				}
			}
//			else
//				yield();
		}
		
	}

}
