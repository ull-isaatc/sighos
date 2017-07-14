/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

/**
 * @author Rosi1
 *
 */
public class CheckSolutionListener extends Listener {
	final private StowagePlan plan;
	private boolean error = false; 

	public CheckSolutionListener(StowagePlan plan) {
		super("Time container");
		this.plan = plan;
		addEntrance(ElementActionInfo.class);
		addEntrance(SimulationEndInfo.class);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementActionInfo) {
			final ElementActionInfo eInfo = (ElementActionInfo) info;
			final String act = eInfo.getActivity().getDescription();
			final long ts = eInfo.getTs() / PortModel.T_OPERATION;
			switch(eInfo.getType()) {
			case ACQ:
				if (act.contains(PortModel.ACT_UNLOAD)) {
					final int containerId = ((UnloadTask)eInfo.getActivity().getParent()).getContainerId();
					if (ts != plan.getOptStartTime(containerId)) {
						System.out.println("ERROR: task " + containerId + " scheduled at " + ts + "; expected at " + plan.getOptStartTime(containerId));
						error = true;
					}
				}
				break;
			case END:
				break;
			case INTACT:
				break;
			case REL:
				if (act.contains(PortModel.ACT_UNLOAD)) {
					final int containerId = ((UnloadTask)eInfo.getActivity().getParent()).getContainerId();
					if ((ts - plan.getVessel().getContainerProcessingTime(containerId)) != plan.getOptStartTime(containerId)) {
						System.out.println("ERROR: task " + containerId + " finished at " + ts + "; expected at " + (plan.getOptStartTime(containerId) + plan.getVessel().getContainerProcessingTime(containerId)));
						error = true;
					}
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
		else if (info instanceof SimulationEndInfo) {
			if (error) {
				System.out.println("ERRORS DETECTED IN SCHEDULE!");
			}
			else {
				System.out.println("CHECKED WITH NO ERRORS");				
			}
		}
	}

}
