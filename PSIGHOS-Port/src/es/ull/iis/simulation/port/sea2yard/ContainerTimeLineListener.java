/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.Arrays;
import java.util.TreeMap;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.info.SimulationTimeInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Rosi1
 *
 */
public class ContainerTimeLineListener extends Listener {
	private final TreeMap<Integer, Integer[]> tUnloadContainer;
	private long maxTs;
	private final StowagePlan plan;
	private final TimeUnit unit;

	public ContainerTimeLineListener(StowagePlan plan, TimeUnit unit) {
		super("Time container");
		this.plan = plan;
		tUnloadContainer = new TreeMap<Integer, Integer[]>();
		maxTs = 0;
		this.unit = unit;
		addEntrance(ElementInfo.class);
		addEntrance(ElementActionInfo.class);
		addEntrance(SimulationTimeInfo.class);
	}

	private String printPlan(int maxTs, TimeUnit simulUnit) {
		final Vessel ship = plan.getVessel();
		final int[][] timeline = new int[ship.getNBays()][maxTs];
		for (int bayId = 0; bayId < ship.getNBays(); bayId++) {
			Arrays.fill(timeline[bayId], -1);
			for (int containerId : ship.getBay(bayId)) {
				final int start = (int)unit.convert(tUnloadContainer.get(containerId)[0], simulUnit);
				final int end = (int)unit.convert(tUnloadContainer.get(containerId)[1], simulUnit);
				for (int ts = start; ts < end; ts++) {
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
				if (eInfo.getActivity().getDescription().contains(PortModel.ACT_UNLOAD)) {
					final int containerId = ((UnloadTask)eInfo.getActivity().getParent()).getContainerId();
					tUnloadContainer.put(containerId, new Integer[] {(int) ts, -1});					
				}
			case END:
				break;
			case INTACT:
				break;
			case REL:
				if (eInfo.getActivity().getDescription().contains(PortModel.ACT_UNLOAD)) {
					final int containerId = ((UnloadTask)eInfo.getActivity().getParent()).getContainerId();
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
		else if (info instanceof SimulationTimeInfo) {
			final SimulationTimeInfo tInfo = (SimulationTimeInfo) info;
			if (SimulationTimeInfo.Type.END.equals(tInfo.getType()))  {
				System.out.println(printPlan((int)maxTs, info.getSimul().getTimeUnit()));
			}
		}
	}

}
