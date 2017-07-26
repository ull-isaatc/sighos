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
	private final TimeUnit unit;
	private long objectiveValue = -1;
	private final long []objTime;
	private final long []useTime;
	private final long []opTime;
	private final long []movTime;

	public Sea2YardGeneralListener(StowagePlan plan, TimeUnit unit) {
		super("Time container");
		totalTime = new TreeMap<Element, Long[]>();
		movingTime = new TreeMap<Element, Long[]>();
		usageTime = new TreeMap<Element, Long[]>();
		objTime = new long[plan.getNCranes()];
		useTime = new long[plan.getNCranes()];
		opTime = new long[plan.getNCranes()];
		movTime = new long[plan.getNCranes()];
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
				else {
					final int containerId = ((UnloadTask)eInfo.getActivity().getParent()).getContainerId();
					if (((QuayCrane)eInfo.getElement()).getLastTask() == containerId) {
						objTime[eInfo.getElement().getIdentifier()] = eInfo.getTs();
					}
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
			final long currentTs = ((SimulationEndInfo) info).getTs();
			final TimeUnit modelUnit = info.getSimul().getTimeUnit();
			for (Element crane : totalTime.keySet()) {
				final int craneId = crane.getIdentifier(); 
				if (totalTime.get(crane)[1] == -1) {
					objTime[craneId] = currentTs;
					objectiveValue = currentTs;
				}
				else {
//					objTime[craneId] = totalTime.get(crane)[1] - totalTime.get(crane)[0];
					objectiveValue = Math.max(objectiveValue, objTime[craneId]);
				}
				useTime[craneId] = usageTime.get(crane)[1];
				movTime[craneId] = movingTime.get(crane)[1];
				if (usageTime.get(crane)[0] != -1) {
					useTime[craneId] += (currentTs - usageTime.get(crane)[0]);
				}
				if (movingTime.get(crane)[0] != -1) {
					movTime[craneId] += (currentTs - movingTime.get(crane)[0]);
				}
				opTime[craneId] = useTime[craneId] - movTime[craneId];
				objTime[craneId] = unit.convert(objTime[craneId], modelUnit);
				useTime[craneId] = unit.convert(useTime[craneId], modelUnit);
				movTime[craneId] = unit.convert(movTime[craneId], modelUnit);
				opTime[craneId] = unit.convert(opTime[craneId], modelUnit);
			}
		}
	}

	public long getObjectiveValue() {
		return objectiveValue;
	}

	/**
	 * @return the objTime
	 */
	public long[] getObjTime() {
		return objTime;
	}
	/**
	 * @return the useTime
	 */
	public long[] getUseTime() {
		return useTime;
	}
	/**
	 * @return the opTime
	 */
	public long[] getOpTime() {
		return opTime;
	}
	/**
	 * @return the movTime
	 */
	public long[] getMovTime() {
		return movTime;
	}
}
