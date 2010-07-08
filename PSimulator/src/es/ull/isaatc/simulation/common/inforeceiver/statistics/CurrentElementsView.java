package es.ull.isaatc.simulation.common.inforeceiver.statistics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.info.ElementInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.inforeceiver.VarView;

public class CurrentElementsView extends VarView {
	
	HashMap<ElementType, Integer> etActiveElem;

	public CurrentElementsView(Simulation simul) {
		super(simul, "currentElements");
		addEntrance(ElementInfo.class);
		etActiveElem = new HashMap<ElementType, Integer>();
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementInfo) {
			ElementInfo elemInfo = (ElementInfo) info;
			ElementType et = elemInfo.getElement().getType();
			switch(elemInfo.getType()) {
			case START: {
				Integer times = etActiveElem.get(et);
				if (times != null)
					etActiveElem.put(et, times.intValue() + 1);
				else
					etActiveElem.put(et, 1);
				if (isDebugMode()) {
					String message = new String();
					message += elemInfo.toString() + "\n";
					message += activeElementsDebugMessage(et);
					debug(message);
				}
				break;
			}
			case FINISH: {
				Integer times = etActiveElem.get(et);
				if (times != null)
					etActiveElem.put(et, times.intValue() - 1);
				else {
					Error err = new Error("Element finalization not espected");
					err.printStackTrace();
				}
				if (isDebugMode()) {
					String message = new String();
					message += elemInfo.toString() + "\n";
					message += activeElementsDebugMessage(et);
					debug(message);
				}
				break;
			}
			}
		} else {
			Error err = new Error("Incorrect info recieved: " + info.toString());
			err.printStackTrace();
		}
	}

	private String activeElementsDebugMessage(ElementType et) {
		String message = new String();
		Iterator<Entry<ElementType, Integer>> iter = etActiveElem.entrySet().iterator();
		while(iter.hasNext()) {
			Entry<ElementType, Integer> entry = iter.next();
			ElementType entryEt = entry.getKey(); 
			Integer entryTimes = entry.getValue();
			message += "Active elements with type " + entryEt.getDescription() + ": " + entryTimes + "\n";
		}
		return(message);
	}
	
	public Number getValue(Object... params) {
		Integer times = null;
		if (params[0] instanceof ElementType) {
			ElementType et = (ElementType) params[0];
			times = etActiveElem.get(et);
		}
		if (times != null)
			return times;
		else
			return 0;
	}

}
