package es.ull.isaatc.simulation.info;

import java.util.HashMap;

import es.ull.isaatc.simulation.ResourceType;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.info.TimeChangeInfo;

/**
 * Stores the resource usage time.
 * 
 * @author Roberto Muñoz
 */
public class ResourceStdUsageListener extends PeriodicListener {
	/** Resource not used. */
	private static int NOTUSED = -1;	
	/** Stores the time each resource has been used for each rol. */
	private HashMap<Integer, ResourceUsageTime> resUsage;
	/** Stores how much time has been dedicated in each rol. */
	HashMap<Integer, double[]> rolTime;


	@Override
	protected void changeCurrentPeriod(double ts) {
		for (ResourceUsageTime rUsageTime : resUsage.values())
			rUsageTime.changePeriod(ts);
	}

	@Override
	protected void initializeStorages() {
		resUsage = new HashMap<Integer, ResourceUsageTime>();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationComponentInfo)
	 */
	public void infoEmited(SimulationObjectInfo info) {
		super.infoEmited(info);
		if (info instanceof ResourceUsageInfo) {
			ResourceUsageInfo rInfo = (ResourceUsageInfo) info;
			switch (rInfo.getType()) {
				case CAUGHT:
					resUsage.get(rInfo.getIdentifier()).caught(rInfo);
					break;
				case RELEASED:
					resUsage.get(rInfo.getIdentifier()).released(rInfo);
					break;
			}
		}
		else if (info instanceof ResourceInfo) {
			ResourceInfo rInfo = (ResourceInfo) info;
			if (rInfo.getType() == ResourceInfo.Type.START) {
				resUsage.put(rInfo.getIdentifier(), new ResourceUsageTime(rInfo.getIdentifier()));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationEndInfo)
	 */
	public void infoEmited(SimulationEndInfo info) {
		// Analize how much time has been dedicated in each rol
		rolTime = new HashMap<Integer, double[]>();
		for (ResourceType rt : simul.getResourceTypeList().values()) {
			double[] time = new double[nPeriods];
			for (ResourceUsageTime resUsageTime : resUsage.values()) {
				double[] resTime = resUsageTime.getUsageTime(rt.getIdentifier());
				if (resTime != null)  // the resource has been used as rol rt
					for (int j = 0; j < nPeriods; j++)
						time[j] += resTime[j];
			}
			rolTime.put(rt.getIdentifier(), time);
		}
		
		System.out.println(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.TimeChangeInfo)
	 */
	public void infoEmited(TimeChangeInfo info) {

	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append("\nResources usage time (PERIOD: " + period + ")\n");
		for (ResourceUsageTime rUsage : resUsage.values()) {
			str.append(rUsage.toString());
		}
		str.append("Resources grouped in rols\n");
		for (int rtId : rolTime.keySet()) {
			str.append(rtId + "\t");
			for (int j = 0; j < nPeriods; j++)
				str.append(rolTime.get(rtId)[j] + "\t");
			str.append("\n");
		}
		return str.toString();
	}

	/**
	 * Stores how much time a resource has been used for each rol.
	 * @author Roberto Muñoz
	 */
	private class ResourceUsageTime {
		/** Stores the time this resource is used for each rol. */
		private HashMap<Integer, double[]> usageTime;
		/** Current rol this resource is used for. */
		private int caughtRT;
		/** Simulation time this resource has been caught. */
		private double caughtTs;
		/** Resource identifier. */
		private int resId;
		
		/**
		 * Creates the object for storing the information.
		 * @param resId Resource identifier
		 */
		public ResourceUsageTime(int resId) {
			this.resId = resId;
			usageTime = new HashMap<Integer, double[]>();
			caughtRT = ResourceStdUsageListener.NOTUSED;
		}
		
		/**
		 * The resource is caught for carring out an activity.
		 * @param rInfo Resource info event
		 */
		public void caught(ResourceUsageInfo rInfo) {
			// check if the resource has previously been used for this rol
			if (usageTime.get(rInfo.getRtId()) == null) {
				usageTime.put(rInfo.getRtId(), new double[nPeriods]);
			}
			caughtRT = rInfo.getRtId();
			caughtTs = rInfo.getTs();
		}
		
		/**
		 * The resource is freed when the activity has been performed.
		 * @param rInfo Resource info event
		 */
		public void released(ResourceUsageInfo rInfo) {
			double[] rolTime =  usageTime.get(caughtRT);
			rolTime[currentPeriod] += rInfo.getTs() - caughtTs;
			caughtRT = ResourceStdUsageListener.NOTUSED;
		}
		
		/**
		 * Perfomrs the oparations neeed when the listener period change.
		 * @param ts Simulation time where the period change
		 */
		public void changePeriod(double ts) {
			if (caughtRT != ResourceStdUsageListener.NOTUSED) {
				double[] rolTime =  usageTime.get(caughtRT);
				rolTime[currentPeriod - 1] += ts - caughtTs;
				caughtTs = ts;
			}
		}
		
		/**
		 * @return the usageTime this resource has been working for a rol.
		 */
		public double[] getUsageTime(int rtId) {
			return usageTime.get(rtId);
		}

		@Override
		public String toString() {
			StringBuffer str = new StringBuffer();
			str.append("RES : " + resId + "\n");
			for (Integer rtId : usageTime.keySet()) {
				str.append(rtId + "\t");
				for (double time : usageTime.get(rtId)) {
					str.append(time + "\t");
				}
				str.append("\n");
			}			
			return str.toString();
		}
	}
}
