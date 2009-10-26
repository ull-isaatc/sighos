package es.ull.isaatc.simulation.sequential.inforeceiver.statistics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import es.ull.isaatc.simulation.sequential.SimulationObject;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationInfo;
import es.ull.isaatc.simulation.inforeceiver.VarView;
import es.ull.isaatc.simulation.sequential.Resource;
import es.ull.isaatc.simulation.sequential.ResourceType;
import es.ull.isaatc.simulation.sequential.Simulation;
import es.ull.isaatc.simulation.sequential.WorkItem;
import es.ull.isaatc.simulation.sequential.info.ElementActionInfo;
import es.ull.isaatc.simulation.sequential.info.ResourceInfo;
import es.ull.isaatc.simulation.sequential.info.ResourceUsageInfo;

public class UnavailabilityUseTimeView extends VarView {

	TreeMap<Resource, Double> resUnUseTime;
	TreeMap<ResourceType, Double> rtUnUseTime;
	TreeMap<SimulObjectStore, Double> unUseStarts;
	TreeMap<WorkItem, Double> actStarts;
	TreeMap<WorkItem, SfResources> resCaughted;
	
	
	public UnavailabilityUseTimeView(Simulation simul) {
		super(simul, "unavailabilityUse");
		addEntrance(ResourceInfo.class);
		addEntrance(SimulationEndInfo.class);
		addEntrance(ElementActionInfo.class);
		addEntrance(ResourceUsageInfo.class);
		resUnUseTime = new TreeMap<Resource, Double>();
		rtUnUseTime = new TreeMap<ResourceType, Double>();
		unUseStarts = new TreeMap<SimulObjectStore, Double>();
		resCaughted = new TreeMap<WorkItem, SfResources>();
		actStarts = new TreeMap<WorkItem, Double>();
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		String message = new String();
		if (info instanceof ResourceInfo) {
			ResourceInfo resInfo = (ResourceInfo) info;
			Resource res = resInfo.getRes();
			ResourceType rt = resInfo.getRt();
			Double requestTs = resInfo.getTs();
			SimulObjectStore id = new SimulObjectStore(res,rt);
			switch(resInfo.getType()) {
			case ROLON: {
				if (unUseStarts.get(id) != null) {
					if (isDebugMode()) 
						message += "Removed from unUseStarts:\t" + id.res.toString() + "\t" + id.res.getDescription() + "\t" + id.rt.toString() + "\t" + id.rt.getDescription() + "\n";
					unUseStarts.remove(id);
				}
				if (isDebugMode()) {
					message += resInfo.toString() + "\n" + message;
					debug(message);
				}
				break;
			}
			case ROLOFF: {
				unUseStarts.put(id, requestTs);
				if (isDebugMode()) {
					message += resInfo.toString() + "\n";
					message += "Put in unUseStarts:\t" + id.res.toString() + "\t" + id.res.getDescription() + "\t" + id.rt.toString() + "\t" + id.rt.getDescription() + "\n";
					debug(message);
				}
				break;
			}
			}
		} else
			if (info instanceof ElementActionInfo) {
				ElementActionInfo elemInfo = (ElementActionInfo) info;
				WorkItem item = elemInfo.getSf();
				Double requestTime = elemInfo.getTs();
				switch(elemInfo.getType()) {
				case ENDACT: {
					SfResources sfRes = resCaughted.get(item);
					if (sfRes != null) {
						Iterator<Entry<Resource, ResourceType>> iter = sfRes.resList.entrySet().iterator();
						while (iter.hasNext()) {
							Entry<Resource, ResourceType> entry = iter.next();
							SimulObjectStore id = new SimulObjectStore(entry.getKey(), entry.getValue());
							Double startTime = unUseStarts.get(id);
							if (startTime != null) {
								IntervalInfo interval = new IntervalInfo(startTime, requestTime);
								double newTime = calculateUnUseTime(interval, resUnUseTime.get(id.res));
								if (isDebugMode()) {
									if (resUnUseTime.get(id.res) != null)
										message += "Added " + (newTime - resUnUseTime.get(id.res)) + "\t" + id.res.toString() + "\t" + id.res.getDescription() + "\n";
									else
										message += "Added " + newTime + "\t" + id.res.toString() + "\t" + id.res.getDescription() + "\n";
									message += "\ttotal time \t" + newTime + "\n";
								}
								resUnUseTime.put(id.res, newTime);
								newTime = calculateUnUseTime(interval, rtUnUseTime.get(id.rt));
								if (isDebugMode()) {
									if (rtUnUseTime.get(id.rt) != null)
										message += "Added " + (newTime - rtUnUseTime.get(id.rt)) + "\t" + id.rt.toString() + "\t" + id.rt.getDescription() + "\n";
									else
										message += "Added " + newTime + "\t" + id.rt.toString() + "\t" + id.rt.getDescription() + "\n";
									message += "\ttotal time \t" + newTime + "\n";
								}
								rtUnUseTime.put(id.rt, newTime);
							}
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
					actStarts.put(item, requestTime);
					break;
				}
				}
			} else
				if (info instanceof SimulationEndInfo) {
					SimulationEndInfo endInfo = (SimulationEndInfo) info;
					unUseStarts.clear();
					if (isDebugMode()) {
						message = endInfo.toString() + "\n";
						debug(message);
					}
				} else 
					if (info instanceof ResourceUsageInfo){
						ResourceUsageInfo resUsInfo = (ResourceUsageInfo) info;
						switch(resUsInfo.getType()) {
						case CAUGHT: {
							WorkItem item = resUsInfo.getSf();
							Resource res = resUsInfo.getRes();
							ResourceType rt = resUsInfo.getRt();
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
		Double requestTime = (Double) params[1];
		Double acumulatedTime = null;
		ArrayList<Double> startsList = obtainUnUseTimeLeft((SimulationObject)params[0]);
		Iterator<Double> iter = startsList.iterator();
		double remainingTime = 0;
		while(iter.hasNext()) {
			Double startTime = (Double) iter.next();
			IntervalInfo interval = new IntervalInfo(startTime, requestTime);
			remainingTime += interval.finish - interval.start;
		}
		if (params[0] instanceof Resource) {
			Resource res = (Resource) params[0];
			acumulatedTime = resUnUseTime.get(res);
			if (isDebugMode())
				message += "\t" + res.toString() + "\t" + res.getDescription();
		} else {
			if (params[0] instanceof ResourceType) {
				ResourceType rt = (ResourceType) params[0];
				acumulatedTime = resUnUseTime.get(rt);
				if (isDebugMode())
					message += "\t" + rt.toString() + "\t" + rt.getDescription();
			}
		}
		double result = 0.0;
		if (acumulatedTime != null) 
			result = remainingTime + acumulatedTime.doubleValue();
		else
			result = remainingTime;
		if (isDebugMode()) {
			message = requestTime + "\tGetValue request " + message + "returned " + result + "\n";
			debug(message);
		}
		return result;
	}
	
	private double calculateUnUseTime(IntervalInfo interval, Double acumulatedTime) {
		if (acumulatedTime != null) {
			return (interval.finish - interval.start + acumulatedTime.doubleValue());
		} else
			return (interval.finish - interval.start);
	}

	private ArrayList<Double> obtainUnUseTimeLeft(SimulationObject obj) {
		ArrayList<Double> startsList = new ArrayList<Double>();
		if ((obj instanceof Resource) || (obj instanceof ResourceType)) {
			Iterator<Entry<WorkItem, Double>> iter = actStarts.entrySet().iterator();
			while(iter.hasNext()) {
				Entry<WorkItem, Double> entry = iter.next();
				WorkItem item = entry.getKey();
				Double actStart = entry.getValue();
				SfResources sfRes = resCaughted.get(item);
				Iterator<Entry<Resource, ResourceType>> iter2 = sfRes.resList.entrySet().iterator();
				while(iter2.hasNext()) {
					Entry<Resource, ResourceType> entry2 = iter2.next();
					Resource res = entry2.getKey();
					ResourceType rt = entry2.getValue();
					SimulObjectStore id = new SimulObjectStore(res, rt);
					Double startTime = unUseStarts.get(id);
					if ((startTime != null) && ((res.compareTo(obj) == 0) || (rt.compareTo(obj) == 0)) && (actStart < startTime)) {
						startsList.add(startTime);
					}
				}
			}
		}
		return startsList;
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
		public double start;
		public double finish;
		
		public IntervalInfo(double start, double finish) {
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
}
