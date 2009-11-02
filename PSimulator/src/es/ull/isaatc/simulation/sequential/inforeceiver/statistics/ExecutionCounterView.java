package es.ull.isaatc.simulation.sequential.inforeceiver.statistics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

import es.ull.isaatc.simulation.info.SimulationInfo;
import es.ull.isaatc.simulation.inforeceiver.VarView;
import es.ull.isaatc.simulation.model.ElementType;
import es.ull.isaatc.simulation.sequential.Activity;
import es.ull.isaatc.simulation.sequential.Element;
import es.ull.isaatc.simulation.sequential.Simulation;
import es.ull.isaatc.simulation.sequential.info.ElementActionInfo;

public class ExecutionCounterView extends VarView {

	HashMap<Activity, ActivityCounters> actExCounter;
	HashMap<Element, Integer> elemExCounter;
	HashMap<ElementType, Integer> etExCounter;
	
	public ExecutionCounterView(Simulation simul) {
		super(simul, "executionCounter");
		addEntrance(ElementActionInfo.class);
		actExCounter = new HashMap<Activity, ActivityCounters>();
		elemExCounter = new HashMap<Element, Integer>();
		etExCounter = new HashMap<ElementType, Integer>();
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementActionInfo) {
			ElementActionInfo elemInfo = (ElementActionInfo) info;
			switch(elemInfo.getType()) {
			case ENDACT: {
				Activity act = elemInfo.getActivity();
				Element elem = elemInfo.getElem();
				ElementType et = elem.getType();
				updateActExCounters(act, elemInfo.getWg());
				updateElemExCounter(elem);
				updateEtExCounter(et);
				if (isDebugMode()) {
					String message = new String();
					message += elemInfo.toString() + "\n";
					ActivityCounters counters = actExCounter.get(act);
					message += act.getDescription() + " executed " + counters.exCounter + " times\n";
					Iterator<Entry<Activity.ActivityWorkGroup, Integer>> iter = counters.wgExCounter.entrySet().iterator();
					while(iter.hasNext()) {
						Entry<Activity.ActivityWorkGroup, Integer> entry = iter.next();
						Activity.ActivityWorkGroup wg = entry.getKey();
						Integer exValue = entry.getValue();
						message += "\t with workgroup " + wg.getDescription() + " " + exValue.intValue() + " times\n"; 
					}					
					message += elem.toString() + " executed " + elemExCounter.get(elem).intValue() + " times\n";
					message += "Type " + et.getDescription() + " executed " + etExCounter.get(et).intValue() + " times\n";
					debug(message);
				}
				break;
			}
			default: break;
			}

		} else {
			Error err = new Error("Incorrect info recieved: " + info.toString());
			err.printStackTrace();
		}
	}
	
	public Number getValue(Object... params) {
		Integer time = null;
		if (params[0] instanceof Activity) {
			Activity act = (Activity) params[0];
			ActivityCounters counters = actExCounter.get(act);
			if (counters != null)
				if ((params.length > 2) && (params[1] instanceof Activity.ActivityWorkGroup)) {
					Activity.ActivityWorkGroup wg = (Activity.ActivityWorkGroup) params[1];
					time = actExCounter.get(act).wgExCounter.get(wg);
				} else
					time = actExCounter.get(act).exCounter;
		} else {
			if (params[0] instanceof Element) {
				Element elem = (Element) params[0];
				time = elemExCounter.get(elem);
			} else {
				if (params[0] instanceof ElementType) {
					ElementType et = (ElementType) params[0];
					time = etExCounter.get(et);
				}
			}
		}
		if (time != null)
			return time.doubleValue();
		else
			return 0;
	}

	private void updateActExCounters(Activity act, Activity.ActivityWorkGroup wg) {
		ActivityCounters counter = actExCounter.get(act);
		if (counter != null)
			counter.addExecution(wg);
		else
			actExCounter.put(act, new ActivityCounters(wg));
	}
	
	private void updateElemExCounter(Element elem) {
		Integer times = elemExCounter.get(elem);
		if (times != null)
			elemExCounter.put(elem, times.intValue() + 1);
		else
			elemExCounter.put(elem, 1);
	}
	
	private void updateEtExCounter(ElementType et) {
		Integer times = etExCounter.get(et);
		if (times != null) 
			etExCounter.put(et, times.intValue() + 1);
		else
			etExCounter.put(et, 1);
	}
	
	class ActivityCounters {
		public Integer exCounter;
		public TreeMap<Activity.ActivityWorkGroup, Integer> wgExCounter;
		
		public ActivityCounters(Activity.ActivityWorkGroup wg) {
			exCounter = new Integer(1);
			wgExCounter = new TreeMap<Activity.ActivityWorkGroup, Integer>();
			wgExCounter.put(wg, 1);
		}
		
		public void addExecution(Activity.ActivityWorkGroup wg) {
			exCounter = new Integer(exCounter.intValue() + 1);
			Integer times = wgExCounter.get(wg);
			if (times != null)
				wgExCounter.put(wg, times.intValue()+1);
			else
				wgExCounter.put(wg, 1);
		}
	}
}
