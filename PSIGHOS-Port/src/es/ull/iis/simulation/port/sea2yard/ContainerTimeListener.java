/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.TreeMap;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;

/**
 * @author Rosi1
 *
 */
public class ContainerTimeListener extends Listener {
	private final TreeMap<Element, Long[]> tUnload;
	private final TreeMap<Integer, Integer[]> tUnloadContainer;

	public ContainerTimeListener(Simulation model) {
		super(model, "Time container");
		tUnload = new TreeMap<Element, Long[]>();
		tUnloadContainer = new TreeMap<Integer, Integer[]>();
		addEntrance(ElementInfo.class);
		addEntrance(ElementActionInfo.class);
		addEntrance(SimulationEndInfo.class);
		addEntrance(SimulationStartInfo.class);
	}

	private String printCaughtResources(ArrayDeque<Resource> caughtResources) {
		final StringBuilder str = new StringBuilder();
		for (Resource res: caughtResources)
			str.append("\t" + res.getDescription());
		return str.toString();
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
		if (info instanceof ElementInfo) {
			final ElementInfo eInfo = (ElementInfo)info;
			switch (eInfo.getType()) {
			case FINISH:
				tUnload.get(eInfo.getElement())[1] = eInfo.getTs();
				break;
			case START:
				tUnload.put(eInfo.getElement(), new Long[] {eInfo.getTs(), -1L});
				break;
			default:
				break;			
			}
			
		}
		else if (info instanceof ElementActionInfo) {
			final ElementActionInfo eInfo = (ElementActionInfo) info;
			final String crane = eInfo.getElement().getType().getDescription();
			final String act = eInfo.getActivity().getDescription();
			final long ts = eInfo.getTs();
			switch(eInfo.getType()) {
			case ACQ:
				if (eInfo.getActivity() instanceof RequestResourcesFlow) { 
					if (eInfo.getActivity().getParent() instanceof ActivityUnload) {
						final int containerId = ((ActivityUnload)eInfo.getActivity().getParent()).getContainerId();
						System.out.println(ts + "\t" + crane + "\t" + "START UNLOAD\t" + containerId + "[" + printCaughtResources(eInfo.getFlowExecutor().getCaughtResources()) + "]");
						tUnloadContainer.put(containerId, new Integer[] {(int) ts, -1});					
					}
					else if (act.contains(PortModel.ACT_GET_TO_BAY)) {
						System.out.println(ts + "\t" + crane + "\t" + "MOVING TO BAY\t" + act.substring(PortModel.ACT_GET_TO_BAY.length()));
					}
				}
			case END:
				break;
			case INTACT:
				break;
			case REL:
				if (eInfo.getActivity().getParent() instanceof ActivityUnload) {
					final int containerId = ((ActivityUnload)eInfo.getActivity().getParent()).getContainerId();
					System.out.println(ts + "\t" + crane + "\t" + "END UNLOAD\t" + containerId);
					tUnloadContainer.get(containerId)[1] = (int) ts;
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
		else if (info instanceof SimulationStartInfo) {
			System.out.println("Ship: ");
			System.out.println(((PortModel)info.getModel()).getPlan().getShip());
			System.out.println();
			System.out.println("Stowage plan:");
			System.out.println(((PortModel)info.getModel()).getPlan());
		}
		else if (info instanceof SimulationEndInfo) {
			System.out.println();
			System.out.println("CRANE\tT1\tT2\tDiff");
			long maxTs = 0L;
			for (Element containerId : tUnload.keySet()) {
				if (tUnload.get(containerId)[1] == -1) {
					System.out.println(containerId.getIdentifier() + "\t" + tUnload.get(containerId)[0] + "\tNO END\t" + (((SimulationEndInfo) info).getTs() - tUnload.get(containerId)[0]));
					maxTs = ((SimulationEndInfo) info).getTs();
				}
				else {
					System.out.println(containerId.getIdentifier() + "\t" + tUnload.get(containerId)[0] + "\t" + tUnload.get(containerId)[1] + "\t" + (tUnload.get(containerId)[1] - tUnload.get(containerId)[0]));
					maxTs = Math.max(maxTs, tUnload.get(containerId)[1]);
				}
			}
			System.out.println("MAX\t\t\t" + maxTs);
			System.out.println();
			System.out.println(printPlan((int)maxTs, ((PortModel)model).getPlan()));
		}
	}

}
