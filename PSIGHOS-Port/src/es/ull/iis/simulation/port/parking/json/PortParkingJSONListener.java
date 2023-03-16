/**
 * 
 */
package es.ull.iis.simulation.port.parking.json;

import java.util.ArrayList;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.info.EntityLocationInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.info.TimeChangeInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.location.Node;
import es.ull.iis.simulation.port.parking.Locations;
import es.ull.iis.simulation.port.parking.PortInfo;
import es.ull.iis.simulation.port.parking.Truck;
import es.ull.iis.simulation.port.parking.Vessel;

/**
 * @author Iván Castilla
 *
 */
public class PortParkingJSONListener extends Listener {
	private final ArrayList<DataMessage> dataMessages;
	private DataMessage currentDataMessage;
	private final static String STR_TRUCK = "truck"; 
	private final static String STR_VESSEL = "vessel"; 
	private final static String STR_X = "x";
	private final static String STR_Y = "y";

	/**
	 * @param description
	 */
	public PortParkingJSONListener() {
		super("JSON listener for Port parking model");
		dataMessages = new ArrayList<>();
		addEntrance(EntityLocationInfo.class);
		addEntrance(ElementActionInfo.class);
		addEntrance(ElementInfo.class);
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
				currentDataMessage = new DataMessage("" + sInfo.getTs());
			}
			else {
				if (currentDataMessage.hasEvents()) {
					dataMessages.add(currentDataMessage);
				}
				System.out.println(this);
			}
		} else if (info instanceof TimeChangeInfo) {
			if (currentDataMessage.hasEvents()) {
				dataMessages.add(currentDataMessage);
			}
			currentDataMessage = new DataMessage("" + ((TimeChangeInfo) info).getTs());
		} else if (info instanceof EntityLocationInfo) {
			final EntityLocationInfo lInfo = (EntityLocationInfo) info;
			switch(lInfo.getType()) {
			case ARRIVE:
				if (lInfo.getLocation() instanceof Node) {
					final Locations loc = Locations.getLocationForNode((Node)lInfo.getLocation());
					if (loc != null) {
						int id = -1;
						if (lInfo.getEntity() instanceof Element) {
							id = ((Element)lInfo.getEntity()).getIdentifier();
						}
						final UpdateEvent ev = new UpdateEvent(id);
						ev.addProperty(STR_X, loc.getLongitude());
						ev.addProperty(STR_Y, loc.getLatitude());
						currentDataMessage.addEvent(ev);
					}
				}
				break;
			case COND_WAIT:
				break;
			case LEAVE:
				break;
			case START:
				int id = -1;
				String type = "";
				if (lInfo.getEntity() instanceof Truck) {
					id = ((Truck)lInfo.getEntity()).getIdentifier();
					type = STR_TRUCK;
				}
				else if (lInfo.getEntity() instanceof Vessel) {
					id = ((Vessel)lInfo.getEntity()).getIdentifier();
					type = STR_VESSEL;
				}
				Locations loc = Locations.getLocationForNode((Node)lInfo.getLocation());
				final NewEvent ev = new NewEvent(id, type);
				ev.addProperty(STR_X, loc.getLongitude());
				ev.addProperty(STR_Y, loc.getLatitude());
				currentDataMessage.addEvent(ev);
				break;
			case WAIT_FOR:
				break;
			default:
				break;			
			}
		} else if (info instanceof ElementInfo) {
			final ElementInfo eInfo = (ElementInfo)info;
			if (ElementInfo.Type.FINISH.equals(eInfo)) {
				int id = eInfo.getElement().getIdentifier();
				final DeleteEvent ev = new DeleteEvent(id);
				currentDataMessage.addEvent(ev);				
			}
		}

	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		for (DataMessage message : dataMessages) {
			str.append(message.toJson()).append(System.lineSeparator());
		}
		return str.toString();
	}
}
