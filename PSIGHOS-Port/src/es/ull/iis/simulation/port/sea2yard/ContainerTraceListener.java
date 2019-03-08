/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.ArrayDeque;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Rosi1
 *
 */
public class ContainerTraceListener extends Listener {
	final private TimeUnit unit;

	public ContainerTraceListener(TimeUnit unit) {
		super("Time container");
		this.unit = unit;
		addEntrance(ElementActionInfo.class);
	}

	private String printCaughtResources(ArrayDeque<Resource> caughtResources) {
		final StringBuilder str = new StringBuilder();
		for (Resource res: caughtResources)
			str.append("\t" + res.getDescription());
		return str.toString();
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementActionInfo) {
			final ElementActionInfo eInfo = (ElementActionInfo) info;
			final String crane = eInfo.getElement().getType().getDescription();
			final String act = eInfo.getActivity().getDescription();
			final long ts = unit.convert(eInfo.getTs(), eInfo.getSimul().getTimeUnit());
			switch(eInfo.getType()) {
			case ACQ:
				if (act.contains(PortModel.ACT_UNLOAD)) {
					final int containerId = ((UnloadTask)eInfo.getActivity().getParent()).getContainerId();
					System.out.println(ts + "\t" + crane + "\t" + "START UNLOAD\t" + containerId + printCaughtResources(eInfo.getResources()));
				}
				else if (act.contains(PortModel.ACT_GET_TO_BAY)) {
					System.out.println(ts + "\t" + crane + "\t" + "MOVING TO BAY\t" + act.substring(PortModel.ACT_GET_TO_BAY.length()) + printCaughtResources(eInfo.getResources()));
				}
				else if (act.contains(PortModel.ACT_PLACE)) {
					System.out.println(ts + "\t" + crane + "\t" + "PLACE AT BAY\t" + act.substring(PortModel.ACT_PLACE.length()) + printCaughtResources(eInfo.getResources()));					
				}
				else if (act.contains(PortModel.SECURITY_TOKEN)) {
					System.out.println(ts + "\t" + crane + "\t" + "PROCEED WITH\t" + act.substring(PortModel.SECURITY_TOKEN.length() + 4));										
				}
			case END:
				break;
			case INTACT:
				break;
			case REL:
				if (eInfo.getActivity().getDescription().contains(PortModel.ACT_UNLOAD)) {
					final int containerId = ((UnloadTask)eInfo.getActivity().getParent()).getContainerId();
					System.out.println(ts + "\t" + crane + "\t" + "END UNLOAD\t" + containerId + printCaughtResources(eInfo.getResources()));
				}
				else if (act.contains(PortModel.ACT_LEAVE_BAY)) {
					System.out.println(ts + "\t" + crane + "\t" + "LEFT BAY\t" + act.substring(PortModel.ACT_LEAVE_BAY.length()) + printCaughtResources(eInfo.getResources()));					
				}
				else if (act.contains(PortModel.END_WORK)) {
					System.out.println(ts + "\t" + crane + "\t" + "END SCHEDULE\t" + printCaughtResources(eInfo.getResources()));					
				}
				else if (act.contains(PortModel.SECURITY_TOKEN)) {
					System.out.println(ts + "\t" + crane + "\t" + "CONTINUE AFTER\t" + act.substring(PortModel.SECURITY_TOKEN.length() + 4));										
				}
				break;
			case REQ:
				if (act.contains(PortModel.SECURITY_TOKEN)) {
					System.out.println(ts + "\t" + crane + "\t" + "WAIT FOR\t" + act.substring(PortModel.SECURITY_TOKEN.length() + 4));										
				}
				break;
			case RESACT:
				break;
			case START:
				break;
			default:
				break;
			
			}
		}
	}

}
