/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.ArrayDeque;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.Simulation;

/**
 * @author Rosi1
 *
 */
public class ContainerTraceListener extends Listener {

	public ContainerTraceListener(Simulation model) {
		super(model, "Time container");
		addEntrance(ElementActionInfo.class);
		addEntrance(SimulationStartInfo.class);
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
			final long ts = eInfo.getTs();
			switch(eInfo.getType()) {
			case ACQ:
				if (act.contains(UnloadActivity.FIRST_FLOW_NAME)) {
					final int containerId = ((UnloadActivity)eInfo.getActivity().getParent()).getContainerId();
					System.out.println(ts + "\t" + crane + "\t" + "START UNLOAD\t" + containerId + "[" + printCaughtResources(eInfo.getFlowExecutor().getCaughtResources()) + "]");
				}
				else if (act.contains(PortModel.ACT_GET_TO_BAY)) {
					System.out.println(ts + "\t" + crane + "\t" + "MOVING TO BAY\t" + act.substring(PortModel.ACT_GET_TO_BAY.length()));
				}
			case END:
				break;
			case INTACT:
				break;
			case REL:
				if (eInfo.getActivity().getDescription().contains(UnloadActivity.LAST_FLOW_NAME)) {
					final int containerId = ((UnloadActivity)eInfo.getActivity().getParent()).getContainerId();
					System.out.println(ts + "\t" + crane + "\t" + "END UNLOAD\t" + containerId);
				}
				else if (act.contains(PortModel.ACT_LEAVE_BAY)) {
					System.out.println(ts + "\t" + crane + "\t" + "LEFT BAY\t" + act.substring(PortModel.ACT_LEAVE_BAY.length()));					
				}
				break;
			case REQ:
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
