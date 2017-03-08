/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.TreeMap;

import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.Simulation;

/**
 * @author Rosi1
 *
 */
public class Sea2YardGeneralListener extends Listener {
	private final TreeMap<Element, Long[]> tUnload;

	public Sea2YardGeneralListener(Simulation model) {
		super(model, "Time container");
		tUnload = new TreeMap<Element, Long[]>();
		addEntrance(ElementInfo.class);
		addEntrance(SimulationStartInfo.class);
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
		}
	}

}
