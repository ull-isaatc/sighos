package es.ull.isaatc.simulation.common.inforeceiver.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import es.ull.isaatc.simulation.common.SimulationObject;
import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.Activity;
import es.ull.isaatc.simulation.common.Element;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.WorkItem;
import es.ull.isaatc.simulation.common.info.ElementActionInfo;
import es.ull.isaatc.simulation.common.info.SimulationEndInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.inforeceiver.VarView;

public class WaitTimeView extends VarView {

	private TreeMap<SimulObjectStore, Long> waitTimeStarts;
	private HashMap<Activity, Long> actWaitTime;
	private HashMap<Element, Long> elemWaitTime;
	private HashMap<ElementType, Long> etWaitTime;
	
	public WaitTimeView(Simulation simul) {
		super(simul, "waitTime");
		addEntrance(ElementActionInfo.class);
		addEntrance(SimulationEndInfo.class);
		waitTimeStarts = new TreeMap<SimulObjectStore, Long>();
		actWaitTime = new HashMap<Activity, Long>();
		elemWaitTime = new HashMap<Element, Long>();
		etWaitTime = new HashMap<ElementType, Long>();
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementActionInfo) {
			ElementActionInfo elemInfo = (ElementActionInfo) info;
			WorkItem item = elemInfo.getSf();
			Activity act = elemInfo.getActivity();
			Element elem = elemInfo.getElem();
			ElementType et = elem.getType();
			SimulObjectStore id = new SimulObjectStore(item, act, elem, et);
			switch(elemInfo.getType()) {
			case INTACT:
			case REQACT: {
				waitTimeStarts.put(id, elemInfo.getTs());
				if(isDebugMode()) {
					String message = new String();
					message += elemInfo.toString() + "\n";
					message += "Wait start at " + elemInfo.getTs() + " for \t" + elem.toString() + " \t" + act.toString() + " " + act.getDescription() + " \t" + et.toString() + " " + et.getDescription() + "\n";
					debug(message);
				}
				break;
			}
			case RESACT:
			case STAACT: {
				Long time = waitTimeStarts.get(id);
				if (time != null) {
					long waitTime = elemInfo.getTs() - time.longValue();
					calculateActWaitTime(act, waitTime);
					calculateElemWaitTime(elem, waitTime);
					calculateETWaitTime(et, waitTime);
					waitTimeStarts.remove(id);
					if(isDebugMode()) {
						String message = new String();
						message += elemInfo.toString() + "\n";
						message += waitTimeDebugMessage(waitTime, act, elem, et);
						debug(message);
					}
				} else {
					Error err = new Error("Start time not initialized");
					err.printStackTrace();
				}
				break;
			}			 
			}
		} else
			if (info instanceof SimulationEndInfo) {
				Iterator<Entry<SimulObjectStore, Long>> iter = waitTimeStarts.entrySet().iterator();
				if (isDebugMode()) {
					String message = new String();
					message += info.toString() + "\n";
					debug(message);
				}
				while (iter.hasNext()) {
					Entry<SimulObjectStore, Long> entry = iter.next();
					SimulObjectStore id = entry.getKey();
					Activity act = id.act;
					Element elem = id.elem;
					ElementType et = elem.getType();
					Long time = entry.getValue();
					long waitTime = getSimul().getInternalEndTs() - time.longValue();
					calculateActWaitTime(act, waitTime);
					calculateElemWaitTime(elem, waitTime);
					calculateETWaitTime(et, waitTime);
					if (isDebugMode()) {
						String message = new String();
						message += waitTimeDebugMessage(waitTime, act, elem, et);
						debug(message);
					}
				}
			} else {
				Error err = new Error("Incorrect info recieved: " + info.toString());
				err.printStackTrace();
			}
	}

	private String waitTimeDebugMessage(long waitTime, Activity act, Element elem, ElementType et) {
		String message = new String();
		message += "Added " + waitTime + " to \t" + elem.toString() + " "
				+ " \t" + act.toString() + " " + act.getDescription() 
				+ " \t" + et.toString() + " " + et.getDescription() + "\n";
		message += act.toString() + " " + act.getDescription() + " \twait time = " + actWaitTime.get(act) + "\n";
		message += elem.toString() + " \twait time = " + elemWaitTime.get(elem) + "\n";
		message += et.toString() + " " + et.getDescription() + " \twait time = " + etWaitTime.get(et) + "\n";
		return(message);
	}
	
	private void calculateActWaitTime(Activity act, long waitTime) {
		Long time = actWaitTime.get(act);
		if (time != null)
			actWaitTime.put(act, time.longValue() + waitTime);
		else
			actWaitTime.put(act, waitTime);
	}
	
	private void calculateElemWaitTime(Element elem, long waitTime) {
		Long time = elemWaitTime.get(elem);
		if (time != null)
			elemWaitTime.put(elem, time.longValue() + waitTime);
		else
			elemWaitTime.put(elem, waitTime);	
	}
	
	private void calculateETWaitTime(ElementType et, long waitTime) {
		Long time = etWaitTime.get(et);
		if (time != null)
			etWaitTime.put(et, time.longValue() + waitTime);
		else
			etWaitTime.put(et, waitTime);	
	}
	
	private ArrayList<Long> getStoreSet(SimulationObject obj) {
		ArrayList<Long> storeList = new ArrayList<Long>();
		Iterator<Entry<SimulObjectStore, Long>> iter = waitTimeStarts.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<SimulObjectStore, Long> entry = iter.next();
			if (entry.getKey().contains(obj))
				storeList.add(entry.getValue());
		}
		return storeList;
	}
	
	private Long calculateActualWaitTime(ArrayList<Long> storeList, Long currentTime, Long acumulatedTime) {
		long acumulatorValue = 0;
		Iterator<Long> iter = storeList.iterator();
		while (iter.hasNext()) {
			Long start = (Long) iter.next();
			acumulatorValue += currentTime - start.longValue(); 
		}
		if (acumulatedTime != null)
			acumulatorValue += acumulatedTime.longValue();
		return acumulatorValue;
	}
	
	public Number getValue(Object... params) {
		String message = new String();
		Long currentTs = (Long) params[1];
		long result = 0;
		if (isDebugMode())
			message += currentTs + "\tGetValue request\t";
		if (params[0] instanceof Activity) {
			Activity act = (Activity) params[0];
			if (isDebugMode())
				message += act.toString() + "\t" + act.getDescription();
			result = calculateActualWaitTime(getStoreSet(act), currentTs, actWaitTime.get(act));
		} else {
			if (params[0] instanceof Element) {
				Element elem = (Element) params[0];
				if (isDebugMode())
					message += elem.toString();
				result = calculateActualWaitTime(getStoreSet(elem), currentTs, elemWaitTime.get(elem));
			} else {
				if (params[0] instanceof ElementType) {
					ElementType et = (ElementType) params[0];
					if (isDebugMode())
						message += et.toString() + "\t" + et.getDescription();
					result = calculateActualWaitTime(getStoreSet(et), currentTs, etWaitTime.get(et));
				}
			}
		}
		if (isDebugMode()) {
			message += "\nReturned " + result + "\n";
			debug(message);
		}
		return 0;
	}

	public class SimulObjectStore extends TreeSet<SimulationObject> implements Comparable<SimulObjectStore>  {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8571610935099002931L;
		public Activity act;
		public Element elem;
		public ElementType et;
		public WorkItem item;
		
		public SimulObjectStore(WorkItem item, Activity act, Element elem, ElementType et) {
			this.item = item;
			this.act = act;
			this.elem = elem;
			this.et = et;
		}
		
		public int compareTo(SimulObjectStore arg0) {
			return(item.compareTo(arg0.item));
		}
		
		public boolean contains(SimulationObject obj) {
			if (obj instanceof Activity)
				return(act.compareTo((Activity)obj) == 0);
			else
				if (obj instanceof Element)
					return (elem.compareTo((Element)obj) == 0);
				else
					if (obj instanceof ElementType)
						return (et.compareTo((ElementType)obj) == 0);
			return false;
		}
	}
}
