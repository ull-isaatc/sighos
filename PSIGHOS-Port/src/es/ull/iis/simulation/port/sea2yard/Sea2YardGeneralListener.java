/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.TreeMap;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Rosi1
 *
 */
public class Sea2YardGeneralListener extends Listener {
	private final TreeMap<Element, Long[]> tUnload;
	private final TreeMap<Element, Long[]> tWorking;
	private final int experiment;
	private final StowagePlan plan;
	private final TimeUnit unit;
	private boolean printSeeds;

	public Sea2YardGeneralListener(StowagePlan plan, int experiment, TimeUnit unit) {
		this(plan, experiment, unit, false);
	}
	public Sea2YardGeneralListener(StowagePlan plan, int experiment, TimeUnit unit, boolean printSeeds) {
		super("Time container");
		this.printSeeds = printSeeds;
		tUnload = new TreeMap<Element, Long[]>();
		tWorking = new TreeMap<Element, Long[]>();
		this.experiment = experiment;
		this.plan = plan;
		this.unit = unit;
		addEntrance(ElementInfo.class);
		addEntrance(SimulationEndInfo.class);
		addEntrance(ElementActionInfo.class);
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
				tWorking.put(eInfo.getElement(), new Long[] {-1L, -1L});
				break;
			default:
				break;			
			}			
		}
		else if (info instanceof ElementActionInfo) {
			final ElementActionInfo eInfo = (ElementActionInfo)info;
			if (eInfo.getActivity().getDescription().contains(PortModel.ACT_UNLOAD)) {
				switch (eInfo.getType()) {
				case REQ:
					break;
				case ACQ:
					break;
				case REL:
					break;
				case START:
//					tWorking.get(eInfo.getElement(), new Long[] {eInfo.getTs(), -1L});
					break;
				case END:
					break;
				case INTACT:
					break;
				case RESACT:
					break;
				default:
					break;
				
				}
			}
		}
		else if (info instanceof SimulationEndInfo) {
			if (experiment == 0) {
				System.out.print("EXP\tTRUCKS\tMAX");
				for (int i = 1; i <= plan.getNCranes(); i++) {
					System.out.print("\tCRANE " + i);
				}
				System.out.println(printSeeds ? "\tSEED" : "");
			}
			System.out.print("" + experiment);
			long maxTs = 0L;
			long []ts = new long[plan.getNCranes()];
			for (Element containerId : tUnload.keySet()) {
				if (tUnload.get(containerId)[1] == -1) {
					ts[containerId.getIdentifier()] = -1;
					maxTs = ((SimulationEndInfo) info).getTs();
				}
				else {
					ts[containerId.getIdentifier()] = tUnload.get(containerId)[1] - tUnload.get(containerId)[0];
					maxTs = Math.max(maxTs, ts[containerId.getIdentifier()]);
				}
			}
			final TimeUnit modelUnit = info.getSimul().getTimeUnit();
			System.out.print("\t" + ((PortModel)info.getSimul()).getNTrucks() + "\t" + unit.convert(maxTs, modelUnit));
			for (int i = 0; i < plan.getNCranes(); i++) {
				System.out.print("\t" + unit.convert(ts[i], modelUnit));
			}
			System.out.println(printSeeds ? ("\t" + ((PortModel)info.getSimul()).getCurrentSeed()) : "");
		}
	}

}
