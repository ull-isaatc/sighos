package es.ull.isaatc.simulation.threaded.inforeceiver.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationInfo;
import es.ull.isaatc.simulation.inforeceiver.VarView;
import es.ull.isaatc.simulation.threaded.Activity;
import es.ull.isaatc.simulation.threaded.Element;
import es.ull.isaatc.simulation.threaded.ElementType;
import es.ull.isaatc.simulation.threaded.Simulation;
import es.ull.isaatc.simulation.threaded.SimulationObject;
import es.ull.isaatc.simulation.threaded.VariableStoreSimulationObject;
import es.ull.isaatc.simulation.threaded.WorkItem;
import es.ull.isaatc.simulation.threaded.info.ElementActionInfo;

public class WaitTimeView extends VarView {

	private TreeMap<SimulObjectStore, Double> waitTimeStarts;
	private HashMap<Activity, Double> actWaitTime;
	private HashMap<Element, Double> elemWaitTime;
	private HashMap<ElementType, Double> etWaitTime;
	
	public WaitTimeView(Simulation simul) {
		super(simul, "waitTime");
		addEntrance(ElementActionInfo.class);
		addEntrance(SimulationEndInfo.class);
		waitTimeStarts = new TreeMap<SimulObjectStore, Double>();
		actWaitTime = new HashMap<Activity, Double>();
		elemWaitTime = new HashMap<Element, Double>();
		etWaitTime = new HashMap<ElementType, Double>();
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementActionInfo) {
			ElementActionInfo elemInfo = (ElementActionInfo) info;
			WorkItem item = elemInfo.getSf();
			Activity act = elemInfo.getActivity();
			Element elem = elemInfo.getElem();
			ElementType et = elem.getElementType();
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
				Double time = waitTimeStarts.get(id);
				if (time != null) {
					double waitTime = elemInfo.getTs() - time.doubleValue();
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
				Iterator<Entry<SimulObjectStore, Double>> iter = waitTimeStarts.entrySet().iterator();
				if (isDebugMode()) {
					String message = new String();
					message += info.toString() + "\n";
					debug(message);
				}
				while (iter.hasNext()) {
					Entry<SimulObjectStore, Double> entry = iter.next();
					SimulObjectStore id = entry.getKey();
					Activity act = id.act;
					Element elem = id.elem;
					ElementType et = elem.getElementType();
					Double time = entry.getValue();
					double waitTime = getSimul().getInternalEndTs() - time.doubleValue();
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

	private String waitTimeDebugMessage(double waitTime, Activity act, Element elem, ElementType et) {
		String message = new String();
		message += "Added " + waitTime + " to \t" + elem.toString() + " "
				+ " \t" + act.toString() + " " + act.getDescription() 
				+ " \t" + et.toString() + " " + et.getDescription() + "\n";
		message += act.toString() + " " + act.getDescription() + " \twait time = " + actWaitTime.get(act) + "\n";
		message += elem.toString() + " \twait time = " + elemWaitTime.get(elem) + "\n";
		message += et.toString() + " " + et.getDescription() + " \twait time = " + etWaitTime.get(et) + "\n";
		return(message);
	}
	
	private void calculateActWaitTime(Activity act, double waitTime) {
		Double time = actWaitTime.get(act);
		if (time != null)
			actWaitTime.put(act, time.doubleValue() + waitTime);
		else
			actWaitTime.put(act, waitTime);
	}
	
	private void calculateElemWaitTime(Element elem, double waitTime) {
		Double time = elemWaitTime.get(elem);
		if (time != null)
			elemWaitTime.put(elem, time.doubleValue() + waitTime);
		else
			elemWaitTime.put(elem, waitTime);	
	}
	
	private void calculateETWaitTime(ElementType et, double waitTime) {
		Double time = etWaitTime.get(et);
		if (time != null)
			etWaitTime.put(et, time.doubleValue() + waitTime);
		else
			etWaitTime.put(et, waitTime);	
	}
	
	private ArrayList<Double> getStoreSet(SimulationObject obj) {
		ArrayList<Double> storeList = new ArrayList<Double>();
		Iterator<Entry<SimulObjectStore, Double>> iter = waitTimeStarts.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<SimulObjectStore, Double> entry = iter.next();
			if (entry.getKey().contains(obj))
				storeList.add(entry.getValue());
		}
		return storeList;
	}
	
	private Double calculateActualWaitTime(ArrayList<Double> storeList, Double currentTime, Double acumulatedTime) {
		double acumulatorValue = 0.0;
		Iterator<Double> iter = storeList.iterator();
		while (iter.hasNext()) {
			Double start = (Double) iter.next();
			acumulatorValue += currentTime - start.doubleValue(); 
		}
		if (acumulatedTime != null)
			acumulatorValue += acumulatedTime.doubleValue();
		return acumulatorValue;
	}
	
	public Number getValue(Object... params) {
		String message = new String();
		Double currentTs = (Double) params[1];
		double result = 0.0;
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

	public class SimulObjectStore extends TreeSet<VariableStoreSimulationObject> implements Comparable<SimulObjectStore>  {

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
