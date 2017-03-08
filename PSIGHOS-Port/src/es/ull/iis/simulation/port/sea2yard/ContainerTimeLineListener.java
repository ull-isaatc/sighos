/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.Arrays;
import java.util.TreeMap;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;

/**
 * @author Rosi1
 *
 */
public class ContainerTimeLineListener extends Listener {
	private final TreeMap<Integer, Integer[]> tUnloadContainer;
	private long maxTs;

	public ContainerTimeLineListener(Simulation model) {
		super(model, "Time container");
		tUnloadContainer = new TreeMap<Integer, Integer[]>();
		maxTs = 0;
		addEntrance(ElementInfo.class);
		addEntrance(ElementActionInfo.class);
		addEntrance(SimulationEndInfo.class);
	}

	private String printPlan(int maxTs, StowagePlan plan) {
		final Ship ship = plan.getShip();
		final int[][] timeline = new int[ship.getNBays()][maxTs];
		for (int bayId = 0; bayId < ship.getNBays(); bayId++) {
			Arrays.fill(timeline[bayId], -1);
			for (int containerId : ship.getBay(bayId)) {
				for (int ts = tUnloadContainer.get(containerId)[0]; ts < tUnloadContainer.get(containerId)[1]; ts++) {
					timeline[bayId][ts] = containerId;
				}
			}
		}
		final StringBuilder str = new StringBuilder();
		for (int bayId = ship.getNBays() - 1; bayId >= 0 ; bayId--) {
			str.append(bayId);
			for (int ts = 0; ts < maxTs; ts++)
				str.append("\t" + timeline[bayId][ts]);
			str.append("\n");
		}
		return str.toString();
	}
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementActionInfo) {
			final ElementActionInfo eInfo = (ElementActionInfo) info;
			final long ts = eInfo.getTs();
			switch(eInfo.getType()) {
			case ACQ:
				if (eInfo.getActivity() instanceof RequestResourcesFlow) { 
					if (eInfo.getActivity().getParent() instanceof ActivityUnload) {
						final int containerId = ((ActivityUnload)eInfo.getActivity().getParent()).getContainerId();
						tUnloadContainer.put(containerId, new Integer[] {(int) ts, -1});					
					}
				}
			case END:
				break;
			case INTACT:
				break;
			case REL:
				if (eInfo.getActivity().getParent() instanceof ActivityUnload) {
					final int containerId = ((ActivityUnload)eInfo.getActivity().getParent()).getContainerId();
					tUnloadContainer.get(containerId)[1] = (int) ts;
					maxTs = Math.max(ts, maxTs);
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
			System.out.println(printPlan((int)maxTs, ((PortModel)model).getPlan()));
		}
	}

}
