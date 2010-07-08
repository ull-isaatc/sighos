package es.ull.isaatc.simulation.common.inforeceiver.statistics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import es.ull.isaatc.simulation.common.ActivityWorkGroup;
import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.Activity;
import es.ull.isaatc.simulation.common.Element;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.info.ElementActionInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.inforeceiver.VarView;

public class ExecutionCounterView extends VarView {

	final private Map<Activity, ActivityCounters> actExCounter;
	final private Map<Element, Integer> elemExCounter;
	final private Map<ElementType, Integer> etExCounter;
	
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
			final ElementActionInfo elemInfo = (ElementActionInfo) info;
			switch(elemInfo.getType()) {
			case ENDACT: {
				final Activity act = elemInfo.getActivity();
				final Element elem = elemInfo.getElement();
				final ElementType et = elem.getType();
				updateActExCounters(act, elemInfo.getWorkGroup());
				updateElemExCounter(elem);
				updateEtExCounter(et);
				if (isDebugMode()) {
					String message = new String();
					message += elemInfo.toString() + "\n";
					ActivityCounters counters = actExCounter.get(act);
					message += act.getDescription() + " executed " + counters.exCounter + " times\n";
					Iterator<Entry<ActivityWorkGroup, Integer>> iter = counters.wgExCounter.entrySet().iterator();
					while(iter.hasNext()) {
						final Entry<ActivityWorkGroup, Integer> entry = iter.next();
						message += "\t with workgroup " + entry.getKey().getDescription() + " " + entry.getValue() + " times\n"; 
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
			Error err = new Error("Incorrect info received: " + info.toString());
			err.printStackTrace();
		}
	}
	
	public Number getValue(Object... params) {
		Integer time = null;
		if (params[0] instanceof Activity) {
			Activity act = (Activity) params[0];
			ActivityCounters counters = actExCounter.get(act);
			if (counters != null)
				if ((params.length > 2) && (params[1] instanceof ActivityWorkGroup)) {
					ActivityWorkGroup wg = (ActivityWorkGroup) params[1];
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

	private void updateActExCounters(Activity act, ActivityWorkGroup wg) {
		final ActivityCounters counter = actExCounter.get(act);
		if (counter != null)
			counter.addExecution(wg);
		else
			actExCounter.put(act, new ActivityCounters(wg));
	}
	
	private void updateElemExCounter(Element elem) {
		final Integer times = elemExCounter.get(elem);
		if (times != null)
			elemExCounter.put(elem, times.intValue() + 1);
		else
			elemExCounter.put(elem, 1);
	}
	
	private void updateEtExCounter(ElementType et) {
		final Integer times = etExCounter.get(et);
		if (times != null) 
			etExCounter.put(et, times.intValue() + 1);
		else
			etExCounter.put(et, 1);
	}
	
	class ActivityCounters {
		public Integer exCounter;
		final public Map<ActivityWorkGroup, Integer> wgExCounter;
		
		public ActivityCounters(ActivityWorkGroup wg) {
			exCounter = new Integer(1);
			wgExCounter = new TreeMap<ActivityWorkGroup, Integer>();
			wgExCounter.put(wg, 1);
		}
		
		public void addExecution(ActivityWorkGroup wg) {
			exCounter = new Integer(exCounter.intValue() + 1);
			final Integer times = wgExCounter.get(wg);
			if (times != null)
				wgExCounter.put(wg, times.intValue()+1);
			else
				wgExCounter.put(wg, 1);
		}
	}
}
