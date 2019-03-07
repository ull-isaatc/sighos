/**
 * 
 */
package es.ull.iis.simulation.whellChairs.listener;

import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

/**
 * A listener to determine if every patient/entity created has finished
 * @author Iván Castilla
 *
 */
public class EveryoneOutListener extends Listener {
	private int counter;

	/**
	 * 
	 */
	public EveryoneOutListener() {
		super("Listener para determinar si todos los pacientes se atendieron");
		addEntrance(ElementInfo.class);
		counter = 0;
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementInfo) {
			final ElementInfo eInfo = (ElementInfo)info;
			
			if (eInfo.getType() == ElementInfo.Type.START) {
				counter++;
			}
			else {
				counter--;
			}			
		}
		
	}

	public boolean isEveryoneOut() {
		return (counter == 0);
	}
}
