package es.ull.iis.simulation.port.parking;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.EntityLocationInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

class PortParkingListener extends Listener {

	public PortParkingListener() {
		super("Port parking listener");
		addEntrance(EntityLocationInfo.class);
		addEntrance(ElementActionInfo.class);
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		System.out.println(info);
	}
	
}