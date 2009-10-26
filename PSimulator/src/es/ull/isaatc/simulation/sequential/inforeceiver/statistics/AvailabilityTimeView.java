package es.ull.isaatc.simulation.sequential.inforeceiver.statistics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationInfo;
import es.ull.isaatc.simulation.inforeceiver.VarView;
import es.ull.isaatc.simulation.sequential.Resource;
import es.ull.isaatc.simulation.sequential.ResourceType;
import es.ull.isaatc.simulation.sequential.Simulation;
import es.ull.isaatc.simulation.sequential.info.ResourceInfo;

public class AvailabilityTimeView extends VarView {

	TreeMap<Resource, TimeCounter> resAvTime;
	TreeMap<Resource, Set<IntervalInfo>> cancelIntervals;
	TreeMap<EntryId, StartCounter> resRoleActivation;
	TreeMap<Resource, StartCounter> resActivation;
	TreeMap<Resource, StartCounter> cancelStarts;
	
	public AvailabilityTimeView(Simulation simul) {
		super(simul, "availabilityTime");
		addEntrance(ResourceInfo.class);
		addEntrance(SimulationEndInfo.class);
		resAvTime = new TreeMap<Resource, TimeCounter>();
		resRoleActivation = new TreeMap<EntryId, StartCounter>();
		resActivation = new TreeMap<Resource, StartCounter>();
		cancelIntervals = new TreeMap<Resource, Set<IntervalInfo>>();
		cancelStarts = new TreeMap<Resource, StartCounter>();
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ResourceInfo) {
			ResourceInfo resInfo = (ResourceInfo) info;
			Resource res = resInfo.getRes();
			ResourceType rt = resInfo.getRt();
			double ts = resInfo.getTs();
			String message = new String();
			if (isDebugMode()) {
				message += resInfo.toString() + "\n";					
			}
			switch(resInfo.getType()) {
			case ROLON: {
				StartCounter counter = resActivation.get(res);
				if (counter == null) {
					resActivation.put(res, new StartCounter(ts));
					if (isDebugMode())
						message += "New activation start \t" + res.toString() + ": " + res.getDescription() + "\t" + ts + "\n"; 
				} else {
					counter.incTimes();
					if (isDebugMode())
						message += "Resource started before. Increment start times. \t" + res.toString() + ": " + res.getDescription() + "\t" + counter.times + " times\n";
				}
				EntryId entry = new EntryId(res, rt);
				counter = resRoleActivation.get(entry);
				if (counter == null) {
					resRoleActivation.put(entry, new StartCounter(ts));
					if (isDebugMode())
						message += "New activation start \tRES: " + res.getDescription() + "\t" + rt.toString() + ": " + rt.getDescription() + "\t" + ts + "\n";
				} else {
					counter.incTimes();
					if (isDebugMode())
						message += "Resource started before. Increment start times. \t" + res.toString() + ": " + res.getDescription() + "\t" + rt.toString() + ": " + rt.getDescription() + "\t" + counter.times + " times\n";
				}
				if (isDebugMode())
					debug(message);
				break;
			}
			case ROLOFF: {
				StartCounter counter = resActivation.get(res);
				int onTimes = counter.times;
				if (onTimes == 1) {
					IntervalInfo interval = new IntervalInfo(counter.startTime, ts);
					double availabilityTime = calculateAvailabilityTime(res, interval);
					TimeCounter timeCounter = resAvTime.get(res);
					if (timeCounter == null) {
						timeCounter = new TimeCounter();
						timeCounter.totalTime = availabilityTime;
						resAvTime.put(res, timeCounter);
					} else	
						timeCounter.totalTime += availabilityTime;
					resActivation.remove(res);
					if (isDebugMode()) {
						message += "Added \t" + availabilityTime + "\t " + res.toString() + ": " + res.getDescription() + "\n";
						message += "Total time \t" + timeCounter.totalTime + "\t " + res.toString() + ": " + res.getDescription() + "\n";
					}
				} else {
					counter.decTimes();
					if (isDebugMode()) {
						message += "Not a final ROLOFF. Espected " + counter.times + " ROLOFF more. \tRES: " + res.getDescription() + "\n";
					}
				}
				EntryId entry = new EntryId(res, rt);
				counter = resRoleActivation.get(entry);
				onTimes = counter.times;
				if (onTimes == 1) {
					IntervalInfo interval = new IntervalInfo(counter.startTime, ts);
					double availabilityTime = calculateAvailabilityTime(entry, interval);
					TimeCounter timeCounter = resAvTime.get(res);
					HashMap<ResourceType, Double> roleMap = timeCounter.roleAvTime;
					Double actualTime = roleMap.get(rt);
					if (actualTime == null)
						roleMap.put(rt, availabilityTime);
					else
						roleMap.put(rt, actualTime.doubleValue() + availabilityTime);
					resRoleActivation.remove(entry);
					if (isDebugMode()) {
						message += "Added \t" + availabilityTime + "\t " + res.toString() + ": " + res.getDescription() + "\t" + rt.toString() + ": " + rt.getDescription() + "\n";
						message += "Total time \t" + timeCounter.totalTime + "\t RES: " + res.getDescription() + "\t" + rt.toString() + ": " + rt.getDescription() + "\n";
					}
				} else {
					counter.decTimes();
					if (isDebugMode()) {
						message += "Not a final ROLOFF. Espected " + counter.times + " ROLOFF yet. \t" + res.toString() + ": " + res.getDescription() + "\t" + rt.toString() + ": " + rt.getDescription() + "\n";
					}
				}
				if (isDebugMode())
					debug(message);
				break;
			}
			case CANCELON: {
				StartCounter counter = cancelStarts.get(res);
				if (counter == null) {
					cancelStarts.put(res, new StartCounter(ts));
					if (isDebugMode())
						message += "New cancelation start \t" + res.toString() + ": " + res.getDescription() + "\t" + ts + "\n";
				} else {
					counter.incTimes();
					if (isDebugMode())
						message += "Resource cancelation started before. Increment cancelation times. \t" + res.toString() + ": " + res.getDescription() + "\t" + counter.times + " times\n";
				}
				if (isDebugMode())
					debug(message);
				break;
			}
			case CANCELOFF: {
				StartCounter counter = cancelStarts.get(res);
				int onTimes = counter.times;
				if (onTimes == 1) {
					IntervalInfo interval = new IntervalInfo(counter.startTime, ts);
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
				if (isDebugMode())
					debug(message);
				break;
			}
			default: break;
			}
		} else {
			if (info instanceof SimulationEndInfo) {
				SimulationEndInfo endInfo = (SimulationEndInfo) info;
				String message = new String();
				if (isDebugMode()) {
					message += endInfo.toString() + "\n";					
				}
				double ts = getSimul().getInternalEndTs();
				Iterator<Entry<Resource, StartCounter>> iter = resActivation.entrySet().iterator();
				while (iter.hasNext()) {
					Entry<Resource, StartCounter> entry = iter.next();
					double startTime = entry.getValue().startTime;
					Resource res = entry.getKey();
					IntervalInfo newInterval = new IntervalInfo(startTime, ts);
					TimeCounter timeCounter = resAvTime.get(res);
					double availabilityTime = calculateAvailabilityTime(res, newInterval);
					if (timeCounter == null) {
						TimeCounter tempCounter = new TimeCounter();
						tempCounter.totalTime = availabilityTime;
					} else	
						timeCounter.totalTime += availabilityTime;
					if (isDebugMode()) {
						message += "New activation interval \tStart: " + newInterval.start + "\tFinish: " + newInterval.finish + "\n";
						message += "Added \t" + availabilityTime + "\t RES: " + res.getDescription() + "\n";
						message += "Total time \t" + timeCounter.totalTime + "\t RES: " + res.getDescription() + "\n";
					}
				}
				Iterator<Entry<EntryId, StartCounter>> iter2 = resRoleActivation.entrySet().iterator();
				while (iter2.hasNext()) {
					Entry<EntryId, StartCounter> entry = iter2.next();
					double startTime = entry.getValue().startTime;
					EntryId id = entry.getKey();
					IntervalInfo newInterval = new IntervalInfo(startTime, ts);
					TimeCounter timeCounter = resAvTime.get(id.res);
					double availabilityTime = calculateAvailabilityTime(id.res, newInterval);
					HashMap<ResourceType, Double> roleMap = timeCounter.roleAvTime;
					Double actualTime = roleMap.get(id.rt);
					if (actualTime == null)
						roleMap.put(id.rt, availabilityTime);
					else
						roleMap.put(id.rt, actualTime.doubleValue() + availabilityTime);
					if (isDebugMode()) {
						message += "New activation interval \tStart: " + newInterval.start + "\tFinish: " + newInterval.finish + "\n";
						message += "Added \t" + availabilityTime + "\t RES: " + id.res.getDescription() + "\t" + id.rt.toString() + ": " + id.rt.getDescription() + "\n";
						message += "Total time \t" + timeCounter.totalTime + "\t RES: " + id.res.getDescription() + "\t" + id.rt.toString() + ": " + id.rt.getDescription() + "\n";
					}
				}
				if (isDebugMode()) {
					message += "FINAL RESULTS\n";
					Iterator<Entry<Resource, TimeCounter>> finalIter = resAvTime.entrySet().iterator();
					while (finalIter.hasNext()) {
						Entry<Resource, TimeCounter> entry = finalIter.next();
						Resource res = entry.getKey();
						TimeCounter finalCounter = entry.getValue();
						message += "RES: " + res.getDescription() + "\t TOTAL: " + finalCounter.totalTime + "\n";
						Iterator<Entry<ResourceType, Double>> finalRoleIter = finalCounter.roleAvTime.entrySet().iterator();
						while (finalRoleIter.hasNext()) {
							Entry<ResourceType, Double> roleTypeEntry = finalRoleIter.next();
							ResourceType finalRt = roleTypeEntry.getKey();
							Double avTime = roleTypeEntry.getValue();
							message += "\twith " + finalRt.toString() + ": " + finalRt.getDescription() + "\t" + avTime + "\n";
						}
					}
				}
				
				if(isDebugMode())
					debug(message);
			} else {
				Error err = new Error("Incorrect info recieved: " + info.toString());
				err.printStackTrace();
			}
		}
	}
	
	private double calculateAvailabilityTime(Resource res, IntervalInfo interval) {
		StartCounter counter = cancelStarts.get(res);
		HashSet<IntervalInfo> resourceCancelIntervals = (HashSet<IntervalInfo>) cancelIntervals.get(res);
		return calculateTime(counter, interval, resourceCancelIntervals);
	}
	
	private double calculateAvailabilityTime(EntryId id, IntervalInfo interval) {
		StartCounter counter = cancelStarts.get(id.res);
		HashSet<IntervalInfo> resourceCancelIntervals = (HashSet<IntervalInfo>) cancelIntervals.get(id.res);
		return calculateTime(counter, interval, resourceCancelIntervals);
	}
	
	private double calculateTime (StartCounter counter, IntervalInfo interval, HashSet<IntervalInfo> resourceCancelIntervals) {
		if (counter != null) {
			if (counter.startTime <= interval.start)
				return 0.0;
			else
				interval.finish = counter.startTime;
		}
		HashSet<IntervalInfo> unchekedIntervals = resourceCancelIntervals;
		HashSet<IntervalInfo> intervalsSet = new HashSet<IntervalInfo>();
		HashSet<IntervalInfo> swapSet = new HashSet<IntervalInfo>();
		if (unchekedIntervals != null)
			while (intervalsSet.size() < unchekedIntervals.size()) {
				intervalsSet.addAll(unchekedIntervals);
				unchekedIntervals.addAll(swapSet);
				Iterator<IntervalInfo> iter = unchekedIntervals.iterator();
				while (iter.hasNext()) {
					IntervalInfo cancelationInterval = iter.next();
					if (cancelationInterval.start >= interval.start)
						if (cancelationInterval.finish < interval.finish) {
							intervalsSet.add(cancelationInterval);
						} else 
							interval.finish = cancelationInterval.start;
					else
						if ( (cancelationInterval.finish <= interval.finish) &&  (cancelationInterval.finish > interval.start) )
							interval.start = cancelationInterval.finish;
						else
							if (cancelationInterval.finish > interval.finish)
								return 0.0;
				}
				swapSet.addAll(intervalsSet);
			}
		Iterator<IntervalInfo> iter = swapSet.iterator();
		double cancelTime = 0.0;
		while (iter.hasNext()) {
			IntervalInfo cancelInterval = iter.next();
			cancelTime += cancelInterval.finish - cancelInterval.start;
		}
		return (interval.finish - interval.start - cancelTime);	
	}
	
	public Number getValue(Object... params) {
		String message = new String();
		double result = 0.0;
		if (isDebugMode())
			message += "GetValue request\t"; 
		if (params[0] instanceof Resource) {
			Resource res = (Resource) params[0];
			TimeCounter counter = resAvTime.get(res);
			if ((params.length > 2) && (params[1] instanceof ResourceType)) {
				ResourceType rt = (ResourceType) params[1];
				Double ts = (Double) params[2];
				if (isDebugMode())
					message += ts + "\t" + message; 
				EntryId id = new EntryId(res, rt);
				StartCounter startCounter = resRoleActivation.get(id);
				if (counter != null) {
					Double acumulatedAvTime = counter.roleAvTime.get(rt);
					if (acumulatedAvTime == null)
						acumulatedAvTime = 0.0;
					if (startCounter == null)
						result = acumulatedAvTime;
					else
						result = acumulatedAvTime + calculateAvailabilityTime(id, new IntervalInfo(startCounter.startTime, ts));
				} else
					if (startCounter != null)
						result = calculateAvailabilityTime(id, new IntervalInfo(startCounter.startTime, ts));
				if (isDebugMode())
					message += res.toString() + "\t" + res.getDescription() + "\t" + rt.toString() + "\t" + rt.getDescription() + "\n";
			} else {
				Double ts = (Double) params[1];
				if (isDebugMode())
					message += ts + "\t" + message;
				if (counter != null) {
					Double acumulatedAvTime = counter.totalTime;
					StartCounter startCounter = resActivation.get(res);
					if (startCounter == null)
						result = acumulatedAvTime;
					else {
						result = acumulatedAvTime + calculateAvailabilityTime(res, new IntervalInfo(startCounter.startTime, ts));
					}
				} else {
					StartCounter startCounter = resActivation.get(res);
					if (startCounter != null)
						result = calculateAvailabilityTime(res, new IntervalInfo(startCounter.startTime, ts));
				}
				if (isDebugMode())
					message += res.toString() + "\t" + res.getDescription() + "\t" + "\n";
			}
		} 
		if (isDebugMode()) {
			message += "Returned: " + result + "\n";
			debug(message);
		}
		return result;
	}

	class TimeCounter {
		public double totalTime;
		public final HashMap<ResourceType, Double> roleAvTime;
		
		public TimeCounter() {
			totalTime = 0.0;
			roleAvTime = new HashMap<ResourceType, Double>();
		}
	}
	
	class EntryId implements Comparable<EntryId> {
		
		public final Resource res;
		public final ResourceType rt;
		
		public EntryId(Resource res, ResourceType rt) {
			this.res = res;
			this.rt = rt;
		}

		public int compareTo(EntryId arg0) {
			int result = res.compareTo(arg0.res);
			if (result == 0)
				return rt.compareTo(arg0.rt);
			else
				return result;
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
	
	class StartCounter {
		public final double startTime;
		public int times;
		
		public StartCounter(double start) {
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
