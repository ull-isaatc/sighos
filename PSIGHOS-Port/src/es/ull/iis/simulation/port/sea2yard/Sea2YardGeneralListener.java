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
	private final TreeMap<Element, Long[]> totalTime;
	private final TreeMap<Element, Long[]> usageTime;
	private final TreeMap<Element, Long[]> movingTime;
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
		totalTime = new TreeMap<Element, Long[]>();
		movingTime = new TreeMap<Element, Long[]>();
		usageTime = new TreeMap<Element, Long[]>();
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
				totalTime.get(eInfo.getElement())[1] = eInfo.getTs();
				break;
			case START:
				totalTime.put(eInfo.getElement(), new Long[] {eInfo.getTs(), -1L});
				movingTime.put(eInfo.getElement(),  new Long[] {-1L, 0L});
				usageTime.put(eInfo.getElement(), new Long[] {-1L, 0L});
				break;
			default:
				break;			
			}			
		}
		else if (info instanceof ElementActionInfo) {
			final ElementActionInfo eInfo = (ElementActionInfo)info;
			switch (eInfo.getType()) {
			case REQ:
				break;
			case ACQ:
				break;
			case REL:
				break;
			case START:
				usageTime.get(eInfo.getElement())[0] = eInfo.getTs();
				break;
			case END:
				final long duration = eInfo.getTs() - usageTime.get(eInfo.getElement())[0];
				usageTime.get(eInfo.getElement())[1] += duration;
				usageTime.get(eInfo.getElement())[0] = -1L;
				if (!eInfo.getActivity().getDescription().contains(PortModel.ACT_UNLOAD)) {
					movingTime.get(eInfo.getElement())[1] += duration;
					movingTime.get(eInfo.getElement())[0] = -1L;
				}				
				break;
			case INTACT:
				break;
			case RESACT:
				break;
			default:
				break;
			
			}
		}
		else if (info instanceof SimulationEndInfo) {
			if (experiment == 0) {
				System.out.print("EXP\tTRUCKS\tOBJ");
				for (int i = 1; i <= plan.getNCranes(); i++) {
					System.out.print("\tT_TOT" + i + "\tT_USE" + i + "\tT_OP" + i + "\tT_MOV" + i);
				}
				System.out.println(printSeeds ? "\tSEED" : "");
			}
			System.out.print("" + experiment);
			long maxTs = 0L;
			final long []ts = new long[plan.getNCranes()];
			final long []useTime = new long[plan.getNCranes()];
			final long []opTime = new long[plan.getNCranes()];
			final long []movTime = new long[plan.getNCranes()];
			final long currentTs = ((SimulationEndInfo) info).getTs();
			for (Element craneId : totalTime.keySet()) {
				if (totalTime.get(craneId)[1] == -1) {
					ts[craneId.getIdentifier()] = currentTs;
					maxTs = currentTs;
				}
				else {
					ts[craneId.getIdentifier()] = totalTime.get(craneId)[1] - totalTime.get(craneId)[0];
					maxTs = Math.max(maxTs, ts[craneId.getIdentifier()]);
				}
				useTime[craneId.getIdentifier()] = usageTime.get(craneId)[1];
				movTime[craneId.getIdentifier()] = movingTime.get(craneId)[1];
				if (usageTime.get(craneId)[0] != -1) {
					useTime[craneId.getIdentifier()] += (currentTs - usageTime.get(craneId)[0]);
				}
				if (movingTime.get(craneId)[0] != -1) {
					movTime[craneId.getIdentifier()] += (currentTs - movingTime.get(craneId)[0]);
				}
				opTime[craneId.getIdentifier()] = useTime[craneId.getIdentifier()] - movTime[craneId.getIdentifier()];
			}
			final TimeUnit modelUnit = info.getSimul().getTimeUnit();
			System.out.print("\t" + ((PortModel)info.getSimul()).getNTrucks() + "\t" + unit.convert(maxTs, modelUnit));
			for (int i = 0; i < plan.getNCranes(); i++) {
				System.out.print("\t" + unit.convert(ts[i], modelUnit) + "\t" + unit.convert(useTime[i], modelUnit) + "\t" + unit.convert(opTime[i], modelUnit) + "\t" + unit.convert(movTime[i], modelUnit));
			}
			System.out.println(printSeeds ? ("\t" + ((PortModel)info.getSimul()).getCurrentSeed()) : "");
		}
	}

}
