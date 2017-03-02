/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.TreeMap;

import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.Simulation;

/**
 * @author Rosi1
 *
 */
public class ContainerTimeListener extends Listener {
	private final TreeMap<Element, Long[]> tUnload;

	public ContainerTimeListener(Simulation model) {
		super(model, "Time container");
		tUnload = new TreeMap<Element, Long[]>();
		addEntrance(ElementInfo.class);
		addEntrance(SimulationEndInfo.class);
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
		else if (info instanceof SimulationEndInfo) {
			System.out.println("DAY\tT1\t\tT2\tDiff");
			for (Element containerId : tUnload.keySet()) {
				System.out.println(containerId.getIdentifier() + "\t" + tUnload.get(containerId)[0] + "\t" + tUnload.get(containerId)[1] + "\t" + (tUnload.get(containerId)[1] - tUnload.get(containerId)[0]));
			}
		}
	}

}
