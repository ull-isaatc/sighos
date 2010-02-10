/**
 * 
 */
package es.ull.isaatc.simulation.groupedExtraPasiveThreads;

import java.util.ArrayDeque;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SlaveEventExecutor extends Thread implements EventExecutor {
	private ArrayDeque<BasicElement.DiscreteEvent> events = new ArrayDeque<BasicElement.DiscreteEvent>();
	private ArrayDeque<BasicElement.DiscreteEvent> extraWaitingEvents = new ArrayDeque<BasicElement.DiscreteEvent>();
	private Semaphore lock = new Semaphore(0);
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
		lock.release();
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
	
	public void notifyEnd() {
		lock.release();
	}
	
	@Override
	public void run() {
		long endTs = simul.getInternalEndTs();
		while (simul.getTs() < endTs) {
			try {
				lock.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (!events.isEmpty()) {
				events.pop().run();
			}
//			else
//				yield();
		}
		
	}

}
