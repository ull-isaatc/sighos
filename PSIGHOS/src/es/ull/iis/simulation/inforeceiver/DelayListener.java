/**
 * 
 */
package es.ull.iis.simulation.inforeceiver;

import java.util.concurrent.TimeUnit;

import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationTimeInfo;

/**
 * When used within a simulation, creates a delay each time the simulation clock is updated. The delay lasts {@link DelayListener#timeout} seconds.
 * @author Iván Castilla Rodríguez
 */
public class DelayListener extends Listener {
	private final long timeout;
	
	/**
	 * Creates a listener that delays the simulation {@link DelayListener#timeout} seconds every time the simulation clock is updated.
	 * @param timeout Seconds of delay.
	 */
	public DelayListener(long timeout) {
		super("Delay listener");
		this.timeout = timeout;
		addEntrance(SimulationTimeInfo.class);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (SimulationTimeInfo.Type.TICK.equals(((SimulationTimeInfo)info).getType())) {
			try {
				TimeUnit.SECONDS.sleep(timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
	}

}
