package es.ull.isaatc.simulation.threaded.inforeceiver.statistics;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationInfo;
import es.ull.isaatc.simulation.inforeceiver.VarView;
import es.ull.isaatc.simulation.threaded.Resource;
import es.ull.isaatc.simulation.threaded.Simulation;
import es.ull.isaatc.simulation.threaded.info.ResourceInfo;

public class CancelTimeView extends VarView {

	private TreeMap<Resource, StartCounter> cancelStarts;
	private TreeMap<Resource, Double> cancelTime;
	
	public CancelTimeView(Simulation simul) {
		super(simul, "cancelTime");
		addEntrance(ResourceInfo.class);
		addEntrance(SimulationEndInfo.class);
		cancelStarts = new TreeMap<Resource, StartCounter>();
		cancelTime = new TreeMap<Resource, Double>();
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		String message = new String();
		if (info instanceof ResourceInfo) {
			ResourceInfo resInfo = (ResourceInfo) info;
			Resource res = resInfo.getRes();
			Double requestTime = resInfo.getTs();
			StartCounter counter = cancelStarts.get(res);
			if (isDebugMode())
				message += resInfo.toString() + "\n";
			switch(resInfo.getType()) {
			case CANCELON: {
				if (counter == null) {
					cancelStarts.put(res, new StartCounter(requestTime));
					if (isDebugMode())
						message += "New cancelation start \t" + res.toString() + ": " + res.getDescription() + "\t" + requestTime + "\n";
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
				if (counter.times == 1) {
					IntervalInfo interval = new IntervalInfo(counter.startTime, requestTime);
					double newTime = calculateCancelTime(interval, cancelTime.get(res));
					if (isDebugMode()) {
						message += "New cancelation interval \tStart: " + interval.start + "\tFinish: " + interval.finish + "\n";
						Double acumulatedTime = cancelTime.get(res);
						if (acumulatedTime != null)
							message += "\tadded " + (newTime - acumulatedTime) + "\t" + res.toString() + "\n";
						else
							message += "\tadded " + newTime + "\t" + res.toString() + "\n";
						message += "\ttotal cancel time " + newTime + "\n"; 
					}
					cancelTime.put(res, newTime);
				} else {
					counter.decTimes();
					if (isDebugMode())
						message += "Not a final CANCELOFF. Espected " + counter.times  + " CANCELOFF yet. \t" + res.toString() + ": " + res.getDescription() + "\n";
				}
				if (isDebugMode())
					debug(message);
				break;
			}
			}
		} else {
			if (info instanceof SimulationEndInfo) {
				SimulationEndInfo endInfo = (SimulationEndInfo) info;
				if (isDebugMode())
					message += endInfo.toString() + "\n";
				Double endTime = endInfo.getTs();
				Iterator<Entry<Resource, StartCounter>> iter = cancelStarts.entrySet().iterator();
				while(iter.hasNext()) {
					Entry<Resource, StartCounter> entry = iter.next();
					Resource res = entry.getKey();
					StartCounter counter = entry.getValue();
					IntervalInfo interval = new IntervalInfo(counter.startTime, endTime);
					double newTime = calculateCancelTime(interval, cancelTime.get(res));
					if (isDebugMode()) {
						message += "New cancelation interval \tStart: " + interval.start + "\tFinish: " + interval.finish + "\n";
						message += "\tadded " + (newTime - cancelTime.get(res).doubleValue()) + "\t" + res.toString() + "\n";
						message += "\ttotal cancel time " + newTime + "\n";
						debug(message);
					}
					cancelTime.put(res, newTime);
				}
			} else {
				Error err = new Error("Incorrect info recieved: " + info.toString());
				err.printStackTrace();
			}
		}

	}

	private double calculateCancelTime(IntervalInfo interval, Double acumulatedTime) {
		if (acumulatedTime == null)
			return (interval.finish - interval.start);
		else
			return (interval.finish - interval.start + acumulatedTime.doubleValue());
	}
	
	public Number getValue(Object... params) {
		Double result = null;
		if (params[0] instanceof Resource) {
			Resource res = (Resource) params[0];
			Double currentTs = (Double) params[1];
			StartCounter counter = cancelStarts.get(res);
			Double acumulatedTime = cancelTime.get(res);
			if (counter == null)
				if (acumulatedTime == null)
					result = 0.0;
				else
					result = acumulatedTime;
			else {
				IntervalInfo interval = new IntervalInfo(counter.startTime, currentTs);
				result = calculateCancelTime(interval, acumulatedTime);
			}
			if (isDebugMode()) {
				String message = new String();
				message += "GetValue request\t" + currentTs + "\t" + res.toString() + "\t" + "\treturned " + result + "\n";
				debug(message);
			}
		}
		return result;
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
	
	class IntervalInfo {
		public double start;
		public double finish;
		
		public IntervalInfo(double start, double finish) {
			this.start = start;
			this.finish = finish;
		}
	}
}
