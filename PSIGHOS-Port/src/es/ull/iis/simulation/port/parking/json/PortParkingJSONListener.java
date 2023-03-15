/**
 * 
 */
package es.ull.iis.simulation.port.parking.json;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.EntityLocationInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.info.TimeChangeInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.port.parking.PortInfo;

/**
 * @author Iván Castilla
 *
 */
public class PortParkingJSONListener extends Listener {
	private long lastTs;

	/**
	 * @param description
	 */
	public PortParkingJSONListener(String description) {
		super("JSON listener for Port parking model");
		addEntrance(EntityLocationInfo.class);
		addEntrance(ElementActionInfo.class);
		addEntrance(SimulationStartStopInfo.class);
		addEntrance(TimeChangeInfo.class);
		addGenerated(PortInfo.class);
		addEntrance(PortInfo.class);
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationStartStopInfo) {
			final SimulationStartStopInfo sInfo = (SimulationStartStopInfo)info;
			if (SimulationStartStopInfo.Type.START.equals(sInfo.getType())) {
				lastTs = sInfo.getTs();
			}
			else {
				
			}
		}

	}

}
