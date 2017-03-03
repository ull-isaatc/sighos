/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.ArrayDeque;
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
			final long ts = eInfo.getTs();
			switch(eInfo.getType()) {
			case ACQ:
				System.out.println(ts + "\t" + crane + "\t" + "START UNLOAD\t" + printCaughtResources(eInfo.getFlowExecutor().getCaughtResources()));
				break;
			case END:
				System.out.println(ts + "\t" + crane + "\t" + "END UNLOAD\t" + printCaughtResources(eInfo.getFlowExecutor().getCaughtResources()));
				break;
			case INTACT:
				break;
			case REL:
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
			System.out.println(((PortModel)info.getModel()).getShip());
			System.out.println();
			System.out.println("Stowage plan:");
			System.out.println(((PortModel)info.getModel()).getPlan());
		}
		else if (info instanceof SimulationEndInfo) {
			System.out.println();
			System.out.println("CRANE\tT1\tT2\tDiff");
			for (Element containerId : tUnload.keySet()) {
				if (tUnload.get(containerId)[1] == -1)
					System.out.println(containerId.getIdentifier() + "\t" + tUnload.get(containerId)[0] + "\tNO END\t" + (((SimulationEndInfo) info).getTs() - tUnload.get(containerId)[0]));
				else
					System.out.println(containerId.getIdentifier() + "\t" + tUnload.get(containerId)[0] + "\t" + tUnload.get(containerId)[1] + "\t" + (tUnload.get(containerId)[1] - tUnload.get(containerId)[0]));
			}
		}
	}

}
