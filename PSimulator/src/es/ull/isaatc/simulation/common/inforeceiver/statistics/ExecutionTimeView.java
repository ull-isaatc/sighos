package es.ull.isaatc.simulation.common.inforeceiver.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import es.ull.isaatc.simulation.common.SimulationObject;
import es.ull.isaatc.simulation.common.ActivityWorkGroup;
import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.Activity;
import es.ull.isaatc.simulation.common.Element;
import es.ull.isaatc.simulation.common.Resource;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.WorkItem;
import es.ull.isaatc.simulation.common.info.ElementActionInfo;
import es.ull.isaatc.simulation.common.info.ResourceUsageInfo;
import es.ull.isaatc.simulation.common.info.SimulationEndInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.inforeceiver.VarView;

public class ExecutionTimeView extends VarView {

	HashMap<Activity, ActivityTimeCounters> activityExTime;
	HashMap<ResourceType, Long> rtExTime;
	HashMap<ElementType, Long> etExTime;
	HashMap<Resource, ResourceTimeCounters> resExTime;
	HashMap<Element, Long> elemExTime;
	TreeMap<SimulObjectStore, SfResources> startActivityTimes;
	
	public ExecutionTimeView(Simulation simul) {
		super(simul, "executionTime");
		addEntrance(ElementActionInfo.class);
		addEntrance(SimulationEndInfo.class);
		addEntrance(ResourceUsageInfo.class);
		activityExTime = new HashMap<Activity, ActivityTimeCounters>();
		rtExTime = new HashMap<ResourceType, Long>();
		etExTime = new HashMap<ElementType, Long>();
		resExTime = new HashMap<Resource, ResourceTimeCounters>();
		elemExTime = new HashMap<Element, Long>();
		startActivityTimes = new TreeMap<SimulObjectStore, SfResources>();
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementActionInfo) {
			ElementActionInfo elemActInfo = (ElementActionInfo) info;
			WorkItem item = elemActInfo.getWorkItem();
			Activity act = elemActInfo.getActivity();
			ActivityWorkGroup wg = elemActInfo.getWorkGroup();
			Element elem = elemActInfo.getElement();
			ElementType et = elem.getType();
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
				long execTime = 0;
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
				long execTime = 0;
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
			Long finalTs = endInfo.getSimul().getInternalEndTs();		
			Iterator<Entry<SimulObjectStore, SfResources>> iter = startActivityTimes.entrySet().iterator();
			while(iter.hasNext()) {
				Entry<SimulObjectStore, SfResources> entry = iter.next();
				SimulObjectStore key = entry.getKey();
				SfResources sfRes = entry.getValue();
				Activity act = key.act;
				Element elem = key.elem;
				ElementType et = elem.getType();
				ActivityWorkGroup wg = key.wg;
				HashMap<Resource, ResourceType> resList = sfRes.resList;
				long execTime = finalTs - sfRes.startTime;
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
					WorkItem item = ruInfo.getWorkItem();
					Resource res = ruInfo.getResource();
					ResourceType rt = ruInfo.getResourceType();
					Activity act = ruInfo.getActivity();
					SimulObjectStore id = new SimulObjectStore(item, act, item.getElement(), item.getElement().getType(), item.getExecutionWG());
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

	private String debugExTimeMessage(long execTime, SfResources sfRes, Activity act, ActivityWorkGroup wg, Element elem, ElementType et) {
		String message = new String();
		ActivityTimeCounters counters = activityExTime.get(act);
		message += "Added " + execTime + " to \t" + act.getDescription() + " execution time with wg:\t" + wg.getDescription() + "\n"; 
		message += act.getDescription() + " \texecution time = " + counters.totalTime + "\n";
		Iterator<Entry<ActivityWorkGroup, Long>> iter = counters.wgTime.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<ActivityWorkGroup, Long> subentry = iter.next();
			ActivityWorkGroup entryWg = subentry.getKey();
			Long entryTime = subentry.getValue();
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
			Iterator<Entry<ResourceType, Long>> iter2 = resCounters.roleTime.entrySet().iterator();
			while (iter2.hasNext()) {
				Entry<ResourceType, Long> subEntry2 = iter2.next();
				ResourceType subEntryRt = subEntry2.getKey();
				Long subEntryTime = subEntry2.getValue();
				message += "\twith rt: " + subEntryRt.getDescription() + " " + subEntryTime + "\n";
			}
			message += entryRt.getDescription() + " \texecution time = " + rtExTime.get(entryRt) + "\n";
		}
		message += "Added " + execTime + " to \t" + elem.toString() + " with type \t" + et.getDescription() + "\n";
		message += elem.toString() + " \texecution time = " + elemExTime.get(elem) + "\n";
		message += et.getDescription() + " \texecution time = " + etExTime.get(et) + "\n";
		return (message);
	}
	
	private ArrayList<Long> getStoreSet(Object obj) {
		Boolean resource = null;
		if ((obj instanceof Resource) || (obj instanceof ResourceType))
			resource = new Boolean(true);
		else
			resource = new Boolean(false);
		ArrayList<Long> storeList = new ArrayList<Long>();
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
		Long currentTs = null;
		Long time = new Long(0);
		String message = new String();
		if (params[0] instanceof Activity) {
			Activity act = (Activity) params[0];
			if ((params.length > 2) && (params[1] instanceof ActivityWorkGroup)) {
				ActivityWorkGroup wg = (ActivityWorkGroup) params[1];
				currentTs = (Long) params[2];
				time = calculateActualWaitTime(getStoreSet(wg), currentTs, activityExTime.get(act).getWgTime(wg));
				if (isDebugMode()) 
					message += "GetValue request\t" + currentTs + "\t" + wg.toString() + "\treturned " + time + "\n";
			} else {
				currentTs = (Long) params[1];
				time = calculateActualWaitTime(getStoreSet(act), currentTs, activityExTime.get(act).getTotalTime());
				if (isDebugMode()) 
					message += "GetValue request\t" + currentTs + "\t" + act.toString() + "\treturned " + time + "\n";
			}
		} else {
			if (params[0] instanceof Element) {
				Element elem = (Element) params[0];
				currentTs = (Long) params[1];
				time = calculateActualWaitTime(getStoreSet(elem), currentTs, elemExTime.get(elem));
				if (isDebugMode()) 
					message += "GetValue request\t" + currentTs + "\t" + elem.toString() + "\treturned " + time + "\n";
			} else {
				if (params[0] instanceof ElementType) {
					ElementType et = (ElementType) params[0];
					currentTs = (Long) params[1];
					time = calculateActualWaitTime(getStoreSet(et), currentTs, etExTime.get(et));
					if (isDebugMode()) 
						message += "GetValue request\t" + currentTs + "\t" + et.toString() + "\treturned " + time + "\n";
				} else {
					if (params[0] instanceof Resource) {
						Resource res = (Resource) params[0];
						if ((params.length > 2) && (params[1] instanceof ResourceType)) {
							ResourceType rt = (ResourceType) params[1];
							currentTs = (Long) params[2];
							time = calculateActualWaitTime(getStoreSet(rt), currentTs, resExTime.get(res).roleTime.get(rt));
							if (isDebugMode()) 
								message += "GetValue request\t" + currentTs + "\t" + res.toString() + "\t" + rt.toString() + "\treturned " + time + "\n";
						} else {
							currentTs = (Long) params[1];
							time = calculateActualWaitTime(getStoreSet(res), currentTs, resExTime.get(res).totalTime);
							if (isDebugMode()) 
								message += "GetValue request\t" + currentTs + "\t" + res.toString() + "\treturned " + time + "\n";
						}
					} else {
						if (params[0] instanceof ResourceType) {
							ResourceType rt = (ResourceType) params[0];
							currentTs = (Long) params[1];
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

	private void calculateActivityTime(Activity act, long execTime, ActivityWorkGroup wg) {
		ActivityTimeCounters actCounter = activityExTime.get(act);
		if (actCounter != null)
			actCounter.addWgTime(wg, execTime);
		else
			activityExTime.put(act, new ActivityTimeCounters(wg, execTime));
	}

	private void calculateElementTime(Element elem, long execTime) {
		Long elemTime = elemExTime.get(elem);
		if (elemTime != null) 
			elemExTime.put(elem, new Long(elemTime.longValue() + execTime));
		else {
			elemExTime.put(elem, execTime);
		}
	}

	private void calculateElementTypeTime(ElementType et, long execTime) {
		Long elemTypeTime = etExTime.get(et);
		if (elemTypeTime != null) 
			etExTime.put(et, new Long(elemTypeTime.longValue() + execTime));
		else {
			etExTime.put(et, execTime);
		}
	}

	private void calculateResourceTime(HashMap<Resource, ResourceType> resList, long execTime) {
		for(Resource res: resList.keySet()) {
			ResourceTimeCounters resTime = resExTime.get(res);
			ResourceType rt = resList.get(res);
			if (resTime != null) 
				resTime.addRoleTime(rt, execTime);
			else {
				resExTime.put(res, new ResourceTimeCounters(rt, execTime));
			}
			Long rtTime = rtExTime.get(rt);
			if (rtTime != null)
				rtExTime.put(rt, rtTime.longValue() + execTime);
			else
				rtExTime.put(rt, execTime);
		}
	}

	class ResourceTimeCounters {
		Long totalTime;
		HashMap<ResourceType, Long> roleTime;

		public ResourceTimeCounters(ResourceType role, Long time) {
			totalTime = time;
			roleTime = new HashMap<ResourceType, Long>();
			roleTime.put(role, new Long(time));
		}

		public void addRoleTime(ResourceType role, Long time) {
			totalTime = new Long(totalTime.longValue() + time.longValue());
			Long counter = roleTime.get(role);
			if (counter != null)
				roleTime.put(role, new Long(counter.longValue() + time.longValue()));
			else
				roleTime.put(role, new Long(time));
		}

		public Long getRoleTime(ResourceType role) {
			return roleTime.get(role);
		}

		public Long getTotalTime() {
			return totalTime;
		}
	}

	class ActivityTimeCounters {
		Long totalTime;
		TreeMap<ActivityWorkGroup, Long> wgTime;

		public ActivityTimeCounters(ActivityWorkGroup wg, Long time) {
			totalTime = time;
			wgTime = new TreeMap<ActivityWorkGroup, Long>();
			wgTime.put(wg, new Long(time));
		}

		public void addWgTime(ActivityWorkGroup role, Long time) {
			totalTime = new Long(totalTime.longValue() + time.longValue());
			Long counter = wgTime.get(role);
			if (counter != null)
				wgTime.put(role, new Long(counter.longValue() + time.longValue()));
			else
				wgTime.put(role, new Long(time));
		}

		public Long getWgTime(ActivityWorkGroup wg) {
			Long time = wgTime.get(wg);
			if (time != null)
				return time;
			else
				return (long)0;
		}

		public Long getTotalTime() {
			return totalTime;
		}
	}

	class SfResources extends TreeSet<SimulationObject> implements Comparable<SfResources>{
		/**
		 * 
		 */
		private static final long serialVersionUID = -3771098905860206726L;
		public HashMap<Resource, ResourceType> resList;
		public long startTime;

		public SfResources(long startTime) {
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
		public ActivityWorkGroup wg;
		public WorkItem item;
		
		public SimulObjectStore(WorkItem item, Activity act, Element elem, ElementType et, ActivityWorkGroup wg) {
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
