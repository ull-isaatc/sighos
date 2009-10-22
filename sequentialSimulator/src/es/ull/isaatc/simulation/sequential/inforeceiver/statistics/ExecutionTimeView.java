package es.ull.isaatc.simulation.sequential.inforeceiver.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import es.ull.isaatc.simulation.sequential.SimulationObject;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationInfo;
import es.ull.isaatc.simulation.inforeceiver.VarView;
import es.ull.isaatc.simulation.sequential.Activity;
import es.ull.isaatc.simulation.sequential.Element;
import es.ull.isaatc.simulation.sequential.ElementType;
import es.ull.isaatc.simulation.sequential.Resource;
import es.ull.isaatc.simulation.sequential.ResourceType;
import es.ull.isaatc.simulation.sequential.Simulation;
import es.ull.isaatc.simulation.sequential.WorkItem;
import es.ull.isaatc.simulation.sequential.info.ElementActionInfo;
import es.ull.isaatc.simulation.sequential.info.ResourceUsageInfo;

public class ExecutionTimeView extends VarView {

	HashMap<Activity, ActivityTimeCounters> activityExTime;
	HashMap<ResourceType, Double> rtExTime;
	HashMap<ElementType, Double> etExTime;
	HashMap<Resource, ResourceTimeCounters> resExTime;
	HashMap<Element, Double> elemExTime;
	TreeMap<SimulObjectStore, SfResources> startActivityTimes;
	
	public ExecutionTimeView(Simulation simul) {
		super(simul, "executionTime");
		addEntrance(ElementActionInfo.class);
		addEntrance(SimulationEndInfo.class);
		addEntrance(ResourceUsageInfo.class);
		activityExTime = new HashMap<Activity, ActivityTimeCounters>();
		rtExTime = new HashMap<ResourceType, Double>();
		etExTime = new HashMap<ElementType, Double>();
		resExTime = new HashMap<Resource, ResourceTimeCounters>();
		elemExTime = new HashMap<Element, Double>();
		startActivityTimes = new TreeMap<SimulObjectStore, SfResources>();
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementActionInfo) {
			ElementActionInfo elemActInfo = (ElementActionInfo) info;
			WorkItem item = elemActInfo.getSf();
			Activity act = elemActInfo.getActivity();
			Activity.ActivityWorkGroup wg = elemActInfo.getWg();
			Element elem = elemActInfo.getElem();
			ElementType et = elem.getElementType();
			SimulObjectStore id = new SimulObjectStore(item, act, elem, et, wg);
			switch(elemActInfo.getType()) {
			case STAACT: {
				SfResources sfRes = startActivityTimes.get(id);
				if (sfRes != null)
					sfRes.startTime = elemActInfo.getTs();
				else {
					sfRes = new SfResources(elemActInfo.getTs());				
					startActivityTimes.put(id, sfRes);
				}
				if (isDebugMode()) {
					String message = new String();
					message += elemActInfo.toString() + "\n";
					message += "START TIME \t[" + startActivityTimes.get(id).startTime + "] \tstored for " + id.act.getDescription() + "\n";
					debug(message);
				}
				break;
			}
			case ENDACT: {
				SfResources sfRes = startActivityTimes.get(id);
				double execTime = 0.0;
				if (sfRes != null) {
					execTime = elemActInfo.getTs() - sfRes.startTime;
					calculateActivityTime(act, execTime, wg);
					calculateElementTime(elem, execTime);
					calculateElementTypeTime(et, execTime);
					calculateResourceTime(sfRes.resList, execTime);
				}
				startActivityTimes.remove(id);
				if (isDebugMode()) {
					String message = new String();
					message += elemActInfo.toString() + "\n";
					message += debugExTimeMessage(execTime, sfRes, act, wg, elem, et);
					debug(message);
				}
				break;
			}
			case INTACT: {
				SfResources sfRes = startActivityTimes.get(id);
				double execTime = 0.0;
				if (sfRes != null) {
					execTime = elemActInfo.getTs() - sfRes.startTime;
					calculateActivityTime(act, execTime, wg);
					calculateElementTime(elem, execTime);
					calculateElementTypeTime(et, execTime);
					calculateResourceTime(sfRes.resList, execTime);
					if (isDebugMode()) {
						String message = new String();
						message += elemActInfo.toString() + "\n";
						message += debugExTimeMessage(execTime, sfRes, act, wg, elem, et);
						debug(message);
					}
				}
				startActivityTimes.remove(id);
				break;
			}
			case RESACT: {
				SfResources sfRes = startActivityTimes.get(id);
				if (sfRes != null)
					sfRes.startTime = elemActInfo.getTs();
				else {
					sfRes = new SfResources(elemActInfo.getTs());				
					startActivityTimes.put(id, sfRes);
				}
				if (isDebugMode()) {
					String message = new String();
					message += elemActInfo.toString() + "\n";
					message += "START TIME \t[" + startActivityTimes.get(id).startTime + "] \tstored for " + id.act.getDescription() + "\n";
					debug(message);
				}
				break;
			}
			default: break;
			}
		} else if (info instanceof SimulationEndInfo) {
			SimulationEndInfo endInfo = (SimulationEndInfo) info;
			Double finalTs = endInfo.getSimul().getInternalEndTs();		
			Iterator<Entry<SimulObjectStore, SfResources>> iter = startActivityTimes.entrySet().iterator();
			while(iter.hasNext()) {
				Entry<SimulObjectStore, SfResources> entry = iter.next();
				SimulObjectStore key = entry.getKey();
				SfResources sfRes = entry.getValue();
				Activity act = key.act;
				Element elem = key.elem;
				ElementType et = elem.getElementType();
				Activity.ActivityWorkGroup wg = key.wg;
				HashMap<Resource, ResourceType> resList = sfRes.resList;
				double execTime = finalTs - sfRes.startTime;
				calculateActivityTime(act, execTime, wg);
				calculateElementTime(elem, execTime);
				calculateElementTypeTime(et, execTime);
				calculateResourceTime(resList, execTime);
				if (isDebugMode()) {
					String message = new String();
					message += endInfo.toString() + "\n";
					message += debugExTimeMessage(execTime, sfRes, act, wg, elem, et);
					debug(message);
				}
			}
		} else {
			if (info instanceof ResourceUsageInfo) {
				ResourceUsageInfo ruInfo = (ResourceUsageInfo) info;
				switch(ruInfo.getType()) {
				case CAUGHT: {
					WorkItem item = ruInfo.getSf();
					Resource res = ruInfo.getRes();
					ResourceType rt = ruInfo.getRt();
					Activity act = ruInfo.getActivity();
					SimulObjectStore id = new SimulObjectStore(item, act, item.getElement(), item.getElement().getElementType(), item.getExecutionWG());
					SfResources sfRes = startActivityTimes.get(id);
					if (sfRes != null)
						sfRes.resList.put(res, rt);
					else {
						sfRes = new SfResources(0);
						sfRes.resList.put(res, rt);
						startActivityTimes.put(id, sfRes);
					}
					if (isDebugMode()) {
						String message = new String();
						message += ruInfo.toString() + "\n";
						message += "RESOURCE \t" + res.getDescription() + " with type \t" + rt.getDescription() + " used in \t" + act.getDescription() + "\n";
						debug(message);
					}
					break;
				}
				}
			}  else {
				Error err = new Error("Incorrect info recieved: " + info.toString());
				err.printStackTrace();
			}
		}
	}

	private String debugExTimeMessage(double execTime, SfResources sfRes, Activity act, Activity.ActivityWorkGroup wg, Element elem, ElementType et) {
		String message = new String();
		ActivityTimeCounters counters = activityExTime.get(act);
		message += "Added " + execTime + " to \t" + act.getDescription() + " execution time with wg:\t" + wg.getDescription() + "\n"; 
		message += act.getDescription() + " \texecution time = " + counters.totalTime + "\n";
		Iterator<Entry<Activity.ActivityWorkGroup, Double>> iter = counters.wgTime.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Activity.ActivityWorkGroup, Double> subentry = iter.next();
			Activity.ActivityWorkGroup entryWg = subentry.getKey();
			Double entryTime = subentry.getValue();
			message += "\t with wg: " + entryWg.getDescription() + " " + entryTime + "\n";
		}
		Iterator<Entry<Resource, ResourceType>> iter1 = sfRes.resList.entrySet().iterator();
		while (iter1.hasNext()) {
			Entry<Resource, ResourceType> subentry = iter1.next();
			Resource entryRes = subentry.getKey();
			ResourceType entryRt = subentry.getValue();
			message += "Added " + execTime + " to \t" + entryRes.getDescription() + " with type \t" + entryRt.getDescription() + "\n";
			ResourceTimeCounters resCounters = resExTime.get(entryRes);
			message += entryRes.getDescription() + " \texecution time = " + resCounters.totalTime + "\n";
			Iterator<Entry<ResourceType, Double>> iter2 = resCounters.roleTime.entrySet().iterator();
			while (iter2.hasNext()) {
				Entry<ResourceType, Double> subEntry2 = iter2.next();
				ResourceType subEntryRt = subEntry2.getKey();
				Double subEntryTime = subEntry2.getValue();
				message += "\twith rt: " + subEntryRt.getDescription() + " " + subEntryTime + "\n";
			}
			message += entryRt.getDescription() + " \texecution time = " + rtExTime.get(entryRt) + "\n";
		}
		message += "Added " + execTime + " to \t" + elem.toString() + " with type \t" + et.getDescription() + "\n";
		message += elem.toString() + " \texecution time = " + elemExTime.get(elem) + "\n";
		message += et.getDescription() + " \texecution time = " + etExTime.get(et) + "\n";
		return (message);
	}
	
	private ArrayList<Double> getStoreSet(Object obj) {
		Boolean resource = null;
		if ((obj instanceof Resource) || (obj instanceof ResourceType))
			resource = new Boolean(true);
		else
			resource = new Boolean(false);
		ArrayList<Double> storeList = new ArrayList<Double>();
		Iterator<Entry<SimulObjectStore, SfResources>> iter = startActivityTimes.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<SimulObjectStore, SfResources> entry = iter.next();
			if (resource) {
				if (entry.getValue().contains(obj))
					for (int i = 0; i < entry.getValue().getCaughtedNumber((SimulationObject)obj); i++)
						storeList.add(entry.getValue().startTime);
			} else {
				if (entry.getKey().contains(obj))
					storeList.add(entry.getValue().startTime);
			}
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
		Double currentTs = null;
		Double time = new Double(0);
		String message = new String();
		if (params[0] instanceof Activity) {
			Activity act = (Activity) params[0];
			if ((params.length > 2) && (params[1] instanceof Activity.ActivityWorkGroup)) {
				Activity.ActivityWorkGroup wg = (Activity.ActivityWorkGroup) params[1];
				currentTs = (Double) params[2];
				time = calculateActualWaitTime(getStoreSet(wg), currentTs, activityExTime.get(act).getWgTime(wg));
				if (isDebugMode()) 
					message += "GetValue request\t" + currentTs + "\t" + wg.toString() + "\treturned " + time + "\n";
			} else {
				currentTs = (Double) params[1];
				time = calculateActualWaitTime(getStoreSet(act), currentTs, activityExTime.get(act).getTotalTime());
				if (isDebugMode()) 
					message += "GetValue request\t" + currentTs + "\t" + act.toString() + "\treturned " + time + "\n";
			}
		} else {
			if (params[0] instanceof Element) {
				Element elem = (Element) params[0];
				currentTs = (Double) params[1];
				time = calculateActualWaitTime(getStoreSet(elem), currentTs, elemExTime.get(elem));
				if (isDebugMode()) 
					message += "GetValue request\t" + currentTs + "\t" + elem.toString() + "\treturned " + time + "\n";
			} else {
				if (params[0] instanceof ElementType) {
					ElementType et = (ElementType) params[0];
					currentTs = (Double) params[1];
					time = calculateActualWaitTime(getStoreSet(et), currentTs, etExTime.get(et));
					if (isDebugMode()) 
						message += "GetValue request\t" + currentTs + "\t" + et.toString() + "\treturned " + time + "\n";
				} else {
					if (params[0] instanceof Resource) {
						Resource res = (Resource) params[0];
						if ((params.length > 2) && (params[1] instanceof ResourceType)) {
							ResourceType rt = (ResourceType) params[1];
							currentTs = (Double) params[2];
							time = calculateActualWaitTime(getStoreSet(rt), currentTs, resExTime.get(res).roleTime.get(rt));
							if (isDebugMode()) 
								message += "GetValue request\t" + currentTs + "\t" + res.toString() + "\t" + rt.toString() + "\treturned " + time + "\n";
						} else {
							currentTs = (Double) params[1];
							time = calculateActualWaitTime(getStoreSet(res), currentTs, resExTime.get(res).totalTime);
							if (isDebugMode()) 
								message += "GetValue request\t" + currentTs + "\t" + res.toString() + "\treturned " + time + "\n";
						}
					} else {
						if (params[0] instanceof ResourceType) {
							ResourceType rt = (ResourceType) params[0];
							currentTs = (Double) params[1];
							time = calculateActualWaitTime(getStoreSet(rt), currentTs, rtExTime.get(rt));
							if (isDebugMode()) 
								message += "GetValue request\t" + currentTs + "\t" + rt.toString() + "\treturned " + time + "\n";
						}
					}
				}
			}
		}
		if (isDebugMode())
			debug(message);
		return time;
	}

	private void calculateActivityTime(Activity act, double execTime, Activity.ActivityWorkGroup wg) {
		ActivityTimeCounters actCounter = activityExTime.get(act);
		if (actCounter != null)
			actCounter.addWgTime(wg, execTime);
		else
			activityExTime.put(act, new ActivityTimeCounters(wg, execTime));
	}

	private void calculateElementTime(Element elem, double execTime) {
		Double elemTime = elemExTime.get(elem);
		if (elemTime != null) 
			elemExTime.put(elem, new Double(elemTime.doubleValue() + execTime));
		else {
			elemExTime.put(elem, execTime);
		}
	}

	private void calculateElementTypeTime(ElementType et, double execTime) {
		Double elemTypeTime = etExTime.get(et);
		if (elemTypeTime != null) 
			etExTime.put(et, new Double(elemTypeTime.doubleValue() + execTime));
		else {
			etExTime.put(et, execTime);
		}
	}

	private void calculateResourceTime(HashMap<Resource, ResourceType> resList, double execTime) {
		for(Resource res: resList.keySet()) {
			ResourceTimeCounters resTime = resExTime.get(res);
			ResourceType rt = resList.get(res);
			if (resTime != null) 
				resTime.addRoleTime(rt, execTime);
			else {
				resExTime.put(res, new ResourceTimeCounters(rt, execTime));
			}
			Double rtTime = rtExTime.get(rt);
			if (rtTime != null)
				rtExTime.put(rt, rtTime.doubleValue() + execTime);
			else
				rtExTime.put(rt, execTime);
		}
	}

	class ResourceTimeCounters {
		Double totalTime;
		HashMap<ResourceType, Double> roleTime;

		public ResourceTimeCounters(ResourceType role, Double time) {
			totalTime = time;
			roleTime = new HashMap<ResourceType, Double>();
			roleTime.put(role, new Double(time));
		}

		public void addRoleTime(ResourceType role, Double time) {
			totalTime = new Double(totalTime.doubleValue() + time.doubleValue());
			Double counter = roleTime.get(role);
			if (counter != null)
				roleTime.put(role, new Double(counter.doubleValue() + time.doubleValue()));
			else
				roleTime.put(role, new Double(time));
		}

		public Double getRoleTime(ResourceType role) {
			return roleTime.get(role);
		}

		public Double getTotalTime() {
			return totalTime;
		}
	}

	class ActivityTimeCounters {
		Double totalTime;
		TreeMap<Activity.ActivityWorkGroup, Double> wgTime;

		public ActivityTimeCounters(Activity.ActivityWorkGroup wg, Double time) {
			totalTime = time;
			wgTime = new TreeMap<Activity.ActivityWorkGroup, Double>();
			wgTime.put(wg, new Double(time));
		}

		public void addWgTime(Activity.ActivityWorkGroup role, Double time) {
			totalTime = new Double(totalTime.doubleValue() + time.doubleValue());
			Double counter = wgTime.get(role);
			if (counter != null)
				wgTime.put(role, new Double(counter.doubleValue() + time.doubleValue()));
			else
				wgTime.put(role, new Double(time));
		}

		public Double getWgTime(Activity.ActivityWorkGroup wg) {
			Double time = wgTime.get(wg);
			if (time != null)
				return time;
			else
				return 0.0;
		}

		public Double getTotalTime() {
			return totalTime;
		}
	}

	class SfResources extends TreeSet<SimulationObject> implements Comparable<SfResources>{
		/**
		 * 
		 */
		private static final long serialVersionUID = -3771098905860206726L;
		public HashMap<Resource, ResourceType> resList;
		public double startTime;

		public SfResources(double startTime) {
			resList = new HashMap<Resource, ResourceType>();
			this.startTime = startTime;
		}

		public int compareTo(SfResources obj) {
			if (resList.equals(obj.resList))
				return 0;
			else
				return -1;
		}
		
		public boolean contains(SimulationObject obj) {
			if (obj instanceof Resource)
				return(resList.containsKey((Resource)obj));
			else
				if (obj instanceof ResourceType)
					return(resList.containsValue((ResourceType)obj));
			return false;
		}
		
		public int getCaughtedNumber(SimulationObject obj) {
			int counter = 0;
			Boolean resource = null;
			if (obj instanceof Resource)
				resource = new Boolean(true);
			else
				resource = new Boolean(false);
			Iterator<Entry<Resource, ResourceType>> iter = resList.entrySet().iterator();
			while(iter.hasNext()) {
				Entry<Resource, ResourceType> entry = iter.next();
				if (resource) { 
					if (entry.getKey().compareTo((Resource)obj) == 0)
						counter++;
				} else {
					if (entry.getValue().compareTo((ResourceType)obj) == 0)
						counter++;
				}
			}
			return counter;
		}
	}
	
	public class SimulObjectStore extends TreeSet<SimulationObject> implements Comparable<SimulObjectStore> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6017371270007871974L;
		public Activity act;
		public Element elem;
		public ElementType et;
		public Activity.ActivityWorkGroup wg;
		public WorkItem item;
		
		public SimulObjectStore(WorkItem item, Activity act, Element elem, ElementType et, Activity.ActivityWorkGroup wg) {
			this.item = item;
			this.act = act;
			this.elem = elem;
			this.et = et;
			this.wg = wg;
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
