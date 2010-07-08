package es.ull.isaatc.simulation.common.inforeceiver.statistics;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import es.ull.isaatc.simulation.common.SimulationObject;
import es.ull.isaatc.simulation.common.Resource;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.WorkItem;
import es.ull.isaatc.simulation.common.info.ElementActionInfo;
import es.ull.isaatc.simulation.common.info.ResourceInfo;
import es.ull.isaatc.simulation.common.info.ResourceUsageInfo;
import es.ull.isaatc.simulation.common.info.SimulationEndInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.inforeceiver.VarView;

public class CancelUseTimeView extends VarView {

	TreeMap<Resource, Long> resCanUseTime;
	TreeMap<ResourceType, Long> rtCanUseTime;
	TreeMap<Resource, StartCounter> cancelStarts;
	TreeMap<WorkItem, SfResources> resCaughted;
	TreeMap<WorkItem, Long> actStarts;
	TreeMap<Resource, Set<IntervalInfo>> cancelIntervals;
	
	public CancelUseTimeView(Simulation simul) {
		super(simul, "cancelUse");
		addEntrance(ResourceInfo.class);
		addEntrance(SimulationEndInfo.class);
		addEntrance(ElementActionInfo.class);
		addEntrance(ResourceUsageInfo.class);
		resCanUseTime = new TreeMap<Resource, Long>();
		rtCanUseTime = new TreeMap<ResourceType, Long>();
		cancelStarts = new TreeMap<Resource, StartCounter>();
		resCaughted = new TreeMap<WorkItem, SfResources>();
		actStarts = new TreeMap<WorkItem, Long>();
		cancelIntervals = new TreeMap<Resource, Set<IntervalInfo>>();
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		String message = new String();
		if (info instanceof ResourceInfo) {
			ResourceInfo resInfo = (ResourceInfo) info;
			Resource res = resInfo.getResource();
			Long requestTs = resInfo.getTs();
			switch(resInfo.getType()) {
			case CANCELON: {
				StartCounter counter = cancelStarts.get(res);
				if (counter == null) {
					cancelStarts.put(res, new StartCounter(requestTs));
					if (isDebugMode())
						message += "New cancelation start \t" + res.toString() + ": " + res.getDescription() + "\t" + requestTs + "\n";
				} else {
					counter.incTimes();
					if (isDebugMode())
						message += "Resource cancelation started before. Increment cancelation times. \t" + res.toString() + ": " + res.getDescription() + "\t" + counter.times + " times\n";
				}
				if (isDebugMode()) {
					message += resInfo.toString() + "\n" + message;
					debug(message);
				}
				break;
			}
			case CANCELOFF: {
				StartCounter counter = cancelStarts.get(res);
				int onTimes = counter.times;
				if (onTimes == 1) {
					IntervalInfo interval = new IntervalInfo(counter.startTime, requestTs);
					Set<IntervalInfo> setIntervals = cancelIntervals.get(res);
					if (setIntervals == null) {
						HashSet<IntervalInfo> tempSet = new HashSet<IntervalInfo>();
						tempSet.add(interval);
						cancelIntervals.put(res, tempSet);
					} else
						setIntervals.add(interval);
					cancelStarts.remove(res);
					if (isDebugMode()) {
						message += "New cancelation interval \tStart: " + interval.start + "\tFinish: " + interval.finish + "\n";
					}
				} else {
					counter.decTimes();
					if (isDebugMode()) {
						message += "Not a final CANCELOFF. Espected " + counter.times  + " CANCELOFF yet. \tRES: " + res.getDescription() + "\n";
					}
				}
				if (isDebugMode()) {
					message += resInfo.toString() + "\n" + message;
					debug(message);
				}
				break;
			}
			}
		} else
			if (info instanceof ElementActionInfo) {
				ElementActionInfo elemInfo = (ElementActionInfo) info;
				WorkItem item = elemInfo.getWorkItem();
				switch(elemInfo.getType()) {
				case ENDACT: {
					Long actStart = actStarts.get(item);
					Long requestTime = elemInfo.getTs();
					SfResources sfRes = resCaughted.get(item);
					if (sfRes != null) {
						Iterator<Entry<Resource, ResourceType>> iter = sfRes.resList.entrySet().iterator();
						while (iter.hasNext()) {
							Entry<Resource, ResourceType> entry = iter.next();
							Resource res = entry.getKey();
							ResourceType rt = entry.getValue();
							HashSet<IntervalInfo> intervalList = new HashSet<IntervalInfo>();
							HashSet<IntervalInfo> tempSet = (HashSet<IntervalInfo>) cancelIntervals.get(res);
							if (tempSet != null)
								intervalList.addAll(tempSet);
							StartCounter canStart = cancelStarts.get(res);
							if (canStart != null) {
								IntervalInfo tempInterval = new IntervalInfo(canStart.startTime, requestTime);
								intervalList.add(tempInterval);
							}
							IntervalInfo exInterval = new IntervalInfo(actStart, requestTime);
							long newTime = calculateCanUseTime(exInterval, intervalList, resCanUseTime.get(res));
							if (isDebugMode()) {
								if (resCanUseTime.get(res) != null)
									message += "Added " + (newTime - resCanUseTime.get(res)) + "\t" + res.toString() + "\t" + res.getDescription() + "\n";
								else
									message += "Added " + newTime + "\t" + res.toString() + "\t" + res.getDescription() + "\n";
								message += "\ttotal time \t" + newTime + "\n";
							}
							resCanUseTime.put(res, newTime);
							newTime = calculateCanUseTime(exInterval, intervalList, rtCanUseTime.get(rt));
							if (isDebugMode()) {
								if (rtCanUseTime.get(rt) != null)
									message += "Added " + (newTime - rtCanUseTime.get(rt)) + "\t" + rt.toString() + "\t" + rt.getDescription() + "\n";
								else
									message += "Added " + newTime + "\t" + rt.toString() + "\t" + rt.getDescription() + "\n";
								message += "\ttotal time \t" + newTime + "\n";
							}
							rtCanUseTime.put(rt, newTime);
						}
						resCaughted.remove(item);
						actStarts.remove(item);
					}
					if (isDebugMode()) {
						message = elemInfo.toString() + "\n" + message;
						debug(message);
					}
					break;
				}
				case STAACT: {
					actStarts.put(item, elemInfo.getTs());
					if (isDebugMode()) {
						message = elemInfo.toString() + "\n";
						message += "Added new item start\t " + item.toString() + "\t" + elemInfo.getTs() + "\n"; 
						debug(message);
					}
					break;
				}
				}
			} else
				if (info instanceof SimulationEndInfo) {
					SimulationEndInfo endInfo = (SimulationEndInfo) info;
					Long endTime = endInfo.getTs();
					Iterator<Entry<WorkItem, Long>> iter = actStarts.entrySet().iterator();
					while(iter.hasNext()) {
						Entry<WorkItem, Long> entry = iter.next();
						WorkItem item = entry.getKey();
						Long startTime = entry.getValue();
						SfResources sfRes = resCaughted.get(item);
						Iterator<Entry<Resource, ResourceType>> iterRes = sfRes.resList.entrySet().iterator();
						while (iterRes.hasNext()) {
							Entry<Resource, ResourceType> entryRes = iterRes.next();
							Resource res = entryRes.getKey();
							ResourceType rt = entryRes.getValue();
							HashSet<IntervalInfo> intervalList = (HashSet<IntervalInfo>) cancelIntervals.get(res);
							StartCounter cancelStart = cancelStarts.get(res);
							if (cancelStart != null) 
								intervalList.add(new IntervalInfo(cancelStart.startTime, endTime));
							IntervalInfo interval = new IntervalInfo(startTime, endTime);
							long newTime = calculateCanUseTime(interval, intervalList, resCanUseTime.get(res));
							if (isDebugMode()) {
								message += "Added " + (newTime - resCanUseTime.get(res)) + "\t" + res.toString() + "\t" + res.getDescription() + "\n";
								message += "\ttotal time \t" + newTime + "\n";
							}
							resCanUseTime.put(res, newTime);
							newTime = calculateCanUseTime(interval, intervalList, rtCanUseTime.get(rt));
							if (isDebugMode()) {
								message += "Added " + (newTime - rtCanUseTime.get(rt)) + "\t" + rt.toString() + "\t" + rt.getDescription() + "\n";
								message += "\ttotal time \t" + newTime + "\n";
							}
							rtCanUseTime.put(rt, newTime);
						}
					}
					if (isDebugMode())
						debug(message);
					cancelStarts.clear();
					if (isDebugMode()) {
						message = endInfo.toString() + "\n" + message;
						debug(message);
					}
				} else 
					if (info instanceof ResourceUsageInfo){
						ResourceUsageInfo resUsInfo = (ResourceUsageInfo) info;
						switch(resUsInfo.getType()) {
						case CAUGHT: {
							WorkItem item = resUsInfo.getWorkItem();
							Resource res = resUsInfo.getResource();
							ResourceType rt = resUsInfo.getResourceType();
							SfResources sfRes = resCaughted.get(item);
							if (sfRes != null) 
								sfRes.resList.put(res, rt);
							else {
								sfRes = new SfResources();
								sfRes.resList.put(res, rt);
								resCaughted.put(item, sfRes);
							}
							if (isDebugMode()) {
								message += resUsInfo.toString() + "\n";
								message += "Added pair \t" + res.toString() + "\t" + res.getDescription() + "\t" + rt.toString() + "\t" + rt.getDescription() + "\tTo: " + item.toString() + "\n";
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

	public Number getValue(Object... params) {
		String message = new String();
		Long requestTime = (Long) params[1];
		if (isDebugMode())
			message += requestTime + "\tGetValue request\t";
		Iterator<Entry<WorkItem, Long>> iter = actStarts.entrySet().iterator();
		long result = 0;
		while(iter.hasNext()) {
			Entry<WorkItem, Long> entry = iter.next();
			WorkItem item = entry.getKey();
			Long actStart = entry.getValue();
			IntervalInfo exInterval = new IntervalInfo(actStart, requestTime);
			SfResources sfRes = resCaughted.get(item);
			Iterator<Entry<Resource, ResourceType>> iterRes = sfRes.resList.entrySet().iterator();
			if (params[0] instanceof Resource) {
				if (isDebugMode())
					message += requestTime + "\t" + ((Resource) params[0]).toString() + "\t";
				while (iterRes.hasNext()) {
					Entry<Resource, ResourceType> entryRes = iterRes.next();
					Resource res = entryRes.getKey();
					if (res.compareTo((Resource)params[0]) == 0) {
						HashSet<IntervalInfo> intervalList = (HashSet<IntervalInfo>) cancelIntervals.get(res);
						StartCounter cancelStart = cancelStarts.get(res);
						if (cancelStart != null && intervalList != null) 
							intervalList.add(new IntervalInfo(cancelStart.startTime, requestTime));
						result += calculateCanUseTime(exInterval, intervalList, resCanUseTime.get(res));
					}
				}
			} else
				if (params[0] instanceof ResourceType) {
					if (isDebugMode())
						message += requestTime + "\t" + ((ResourceType) params[0]).toString() + "\t";
					while (iterRes.hasNext()) {
						Entry<Resource, ResourceType> entryRes = iterRes.next();
						ResourceType rt = entryRes.getValue();
						if (rt.compareTo((ResourceType) params[0]) == 0) {
							Resource res = entryRes.getKey();
							HashSet<IntervalInfo> intervalList = new HashSet<IntervalInfo>();
							if (cancelIntervals.containsKey(res))
								intervalList.addAll(cancelIntervals.get(res));
							StartCounter cancelStart = cancelStarts.get(res);
							if (cancelStart != null) 
								intervalList.add(new IntervalInfo(cancelStart.startTime, requestTime));
							result += calculateCanUseTime(exInterval, intervalList, resCanUseTime.get(rt));
						}
					}
				}
		}
		if (isDebugMode()) {
			message += "\treturned " + result + "\n";
			debug(message);
		}
		return result;
	}
	
	private long calculateCanUseTime(IntervalInfo exInterval, HashSet<IntervalInfo> cancelIntervalList, Long acumulatedTime) {
		
		HashSet<IntervalInfo> validIntervals = new HashSet<IntervalInfo>();
		if (cancelIntervalList != null) {		
			Iterator<IntervalInfo> iter = cancelIntervalList.iterator();
			while (iter.hasNext()) {
				IntervalInfo cancelationInterval = (IntervalInfo) iter.next();
				if (cancelationInterval.start >= exInterval.start)
					if (cancelationInterval.finish <= exInterval.finish) {
						validIntervals.add(cancelationInterval);
					} else {
						IntervalInfo temp = new IntervalInfo(cancelationInterval.start, exInterval.finish);
						validIntervals.add(temp);
					}
				else
					if ( (cancelationInterval.finish <= exInterval.finish) &&  (cancelationInterval.finish > exInterval.start) ) {
						IntervalInfo temp = new IntervalInfo(exInterval.start, cancelationInterval.finish);
						validIntervals.add(temp);
					}
			}
		}
		Iterator<IntervalInfo> iter = validIntervals.iterator();
		long cancelTime = 0;
		while (iter.hasNext()) {
			IntervalInfo cancelInterval = (IntervalInfo) iter.next();
			cancelTime += cancelInterval.finish - cancelInterval.start;
		}
		if (acumulatedTime != null) {
			return (cancelTime + acumulatedTime.longValue());
		} else
			return (cancelTime);
	}
	
	public class SimulObjectStore extends TreeSet<SimulationObject> implements Comparable<SimulObjectStore> {

		private static final long serialVersionUID = -1560435105451464053L;
		public Resource res;
		public ResourceType rt;
		
		public SimulObjectStore(Resource res, ResourceType rt) {
			this.res = res;
			this.rt = rt;
		}
		
		public int compareTo(SimulObjectStore arg0) {
			int result = res.compareTo(arg0.res);
			if (result == 0)
				return rt.compareTo(arg0.rt);
			return result;
			
		}
		
		public boolean contains(SimulationObject obj) {
			if (obj instanceof Resource)
				return(res.compareTo((Resource)obj) == 0);
			else
				if (obj instanceof ResourceType)
					return (rt.compareTo((ResourceType)obj) == 0);
			return false;
		}
	}
	
	class IntervalInfo {
		public long start;
		public long finish;
		
		public IntervalInfo(long start, long finish) {
			this.start = start;
			this.finish = finish;
		}
	}
	
	class SfResources {
		TreeMap<Resource, ResourceType> resList;
		
		public SfResources() {
			resList = new TreeMap<Resource, ResourceType>();
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
	}

	class StartCounter {
		public final long startTime;
		public int times;
		
		public StartCounter(long start) {
			startTime = start;
			times = 1;
		}
		
		public void incTimes() {
			times++;
		}
		
		public void decTimes() {
			times--;
		}
		
		public boolean isIntervalEnd() {
			return (times == 0);
		}
	}
}
