package es.ull.isaatc.simulation.common.inforeceiver.statistics;

import java.util.HashMap;

import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.Element;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.WorkItem;
import es.ull.isaatc.simulation.common.info.ElementActionInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.inforeceiver.VarView;

public class RequestCounterView extends VarView {

	HashMap<Element, Integer> elemReqCounter;
	HashMap<ElementType, Integer> etReqCounter;
	
	public RequestCounterView(Simulation simul) {
		super(simul, "requestCounter");
		addEntrance(ElementActionInfo.class);
		elemReqCounter = new HashMap<Element, Integer>();
		etReqCounter = new HashMap<ElementType, Integer>();
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		String message = new String();
		if (info instanceof ElementActionInfo) {
			ElementActionInfo elemInfo = (ElementActionInfo) info;
			switch(elemInfo.getType()) {
			case REQACT: {
				WorkItem sf = elemInfo.getWorkItem();
				Element elem = sf.getElement();
				ElementType et = elem.getType();
				String upElem = updateElemReqCounter(elem);
				String upEt = updateEtReqCounter(et);
				if (isDebugMode()) {
					message += elemInfo.toString() + "\n" + upElem + upEt + "\n";
					debug(message);
				}
			}
			default: break;
			}
		} else {
			Error err = new Error("Incorrect info recieved: " + info.toString());
			err.printStackTrace();
		}

	}

	private String updateElemReqCounter(Element elem) {
		String message = new String();
		Integer times = elemReqCounter.get(elem);
		if (times != null) { 
			elemReqCounter.put(elem, times.intValue() + 1);
			if (isDebugMode())
				message += elem.toString() + "\t " + (times.intValue() + 1) + " activities requested.\n";
		} else {
			elemReqCounter.put(elem, 1);
			if (isDebugMode())
				message += elem.toString() + "\t 1 activity requested.\n";
		}
		return message;
	}
	
	private String updateEtReqCounter(ElementType et) {
		String message = new String();
		Integer times = etReqCounter.get(et);
		if (times != null) {
			etReqCounter.put(et, times.intValue() + 1);
			if (isDebugMode())
				message += et.toString() + "\t " + (times.intValue() + 1) + " activities requested.\n";
		} else {
			etReqCounter.put(et, 1);
			if (isDebugMode())
				message += et.toString() + "\t 1 activity requested.\n";
		}
		return message;
	}
	
	public Number getValue(Object... params) {
		Integer time = null;
		if (params[0] instanceof Element) {
			Element elem = (Element) params[0];
			time = elemReqCounter.get(elem);
		} else {
			if (params[0] instanceof ElementType) {
				ElementType et = (ElementType) params[0];
				time = etReqCounter.get(et);
			}
		}
		if (time != null)
			return time.doubleValue();
		else
			return 0;
	}

}
