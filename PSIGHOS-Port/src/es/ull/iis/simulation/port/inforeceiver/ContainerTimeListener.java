/**
 * 
 */
package es.ull.iis.simulation.port.inforeceiver;

import java.util.TreeMap;

import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.port.PortSimulation;

/**
 * @author Rosi1
 *
 */
public class ContainerTimeListener extends Listener {
	private final TreeMap<Integer, Long[]> tContainer;

	public ContainerTimeListener(Simulation simul) {
		super(simul, "Time container");
		tContainer = new TreeMap<Integer, Long[]>();
		addEntrance(ElementActionInfo.class);
		addEntrance(SimulationEndInfo.class);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementActionInfo) {
			final ElementActionInfo eInfo = (ElementActionInfo)info;
			final int containerId = eInfo.getElement().getIdentifier(); 
			switch (eInfo.getType()) {
			case ENDACT:
				if (eInfo.getActivity().getDescription().equals(PortSimulation.ACT_SEA_TO_YARD)) {
					tContainer.get(containerId)[1] = eInfo.getTs();
				}
				break;
			case INTACT:
				break;
			case REQACT:
				break;
			case RESACT:
				break;
			case STAACT:
				if (eInfo.getActivity().getDescription().equals(PortSimulation.ACT_SEA_TO_YARD)) {
					Long[] time = new Long[2];
					time[0] = eInfo.getTs();
					time[1] = (long)-1;
					tContainer.put(containerId, time);
				}
				break;
			default:
				break;			
			}
			
		}
		else if (info instanceof SimulationEndInfo) {
			System.out.println("CONTAINER\tT1\t\tT2");
			for (Integer containerId : tContainer.keySet()) {
				System.out.println(containerId + "\t" + tContainer.get(containerId)[0] + "\t" + tContainer.get(containerId)[1]);
			}
		}
	}

}