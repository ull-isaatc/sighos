package es.ull.isaatc.simulation.listener;

import java.util.TreeMap;

import es.ull.isaatc.simulation.ResourceType;
import es.ull.isaatc.simulation.info.ResourceInfo;
import es.ull.isaatc.simulation.info.ResourceUsageInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;

/**
 * Stores the resource usage and availability time.
 * 
 * @author Roberto Muñoz
 */
public class ResourceStdUsageListener extends PeriodicListener {
	/** Resource not used. */
	private static int NOTUSED = -1;	
	/** Stores the time each resource has been used for each rol. */
	private TreeMap<Integer, ResourceUsage> resUsage;
	/** Stores how much time has been dedicated in each rol. */
	TreeMap<Integer, double[]> rolTime;
	/** Stores the availavility time each rol has had. */
	TreeMap<Integer, double[]> avalTime;

	public ResourceStdUsageListener() {
		super();
	}

	public ResourceStdUsageListener(double period) {
		super(period);
	}

	/**
	 * @return the avalTime
	 */
	public TreeMap<Integer, double[]> getAvalTime() {
		return avalTime;
	}

	/**
	 * @return the resUsage
	 */
	public TreeMap<Integer, ResourceUsage> getResUsage() {
		return resUsage;
	}

	/**
	 * @return the rolTime
	 */
	public TreeMap<Integer, double[]> getRolTime() {
		return rolTime;
	}

	@Override
	protected void changeCurrentPeriod(double ts) {
		for (ResourceUsage rUsageTime : resUsage.values())
			rUsageTime.changePeriod(ts);		
	}

	@Override
	protected void initializeStorages() {
		resUsage = new TreeMap<Integer, ResourceUsage>();
	}
	
	@Override
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
			switch (rInfo.getType()) {
			case START:
				resUsage.put(rInfo.getIdentifier(), new ResourceUsage(rInfo.getIdentifier()));
				break;
			case ROLON:
				resUsage.get(rInfo.getIdentifier()).rolOn(rInfo);
				break;
			case ROLOFF:
				resUsage.get(rInfo.getIdentifier()).rolOff(rInfo);
				break;
			}
		}
	}

	public void infoEmited(SimulationEndInfo info) {
		for (ResourceUsage rUsageTime : resUsage.values())
			rUsageTime.simulationEnd(info.getSimulation().getEndTs());		

		// Analize how much time has been dedicated in each rol
		rolTime = new TreeMap<Integer, double[]>();
		for (ResourceType rt : simul.getResourceTypeList().values()) {
			double[] time = new double[nPeriods];
			for (ResourceUsage resUsageTime : resUsage.values()) {
				double[] resTime = resUsageTime.getUsageTime(rt.getIdentifier());
				if (resTime != null)  // the resource has been used as rol rt
					for (int j = 0; j < nPeriods; j++)
						time[j] += resTime[j];
			}
			rolTime.put(rt.getIdentifier(), time);
		}
		// Analize how much time has been available in each rol
		avalTime = new TreeMap<Integer, double[]>();
		for (ResourceType rt : simul.getResourceTypeList().values()) {
			double[] time = new double[nPeriods];
			for (ResourceUsage resUsageTime : resUsage.values()) {
				double[] resTime = resUsageTime.getAvalTime(rt.getIdentifier());
				if (resTime != null)  // the resource has been used as rol rt
					for (int j = 0; j < nPeriods; j++)
						time[j] += resTime[j];
			}
			avalTime.put(rt.getIdentifier(), time);
		}

	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append("\nResources usage time (PERIOD: " + period + ")\n");
		for (ResourceUsage rUsage : resUsage.values()) {
			str.append(rUsage.getUsageString());
		}
		str.append("\nResources availability time (PERIOD: " + period + ")\n");
		for (ResourceUsage rUsage : resUsage.values()) {
			str.append(rUsage.getAvalString());
		}
		str.append("Resources usage time grouped in rols\n");
		for (int rtId : rolTime.keySet()) {
			str.append(rtId + "\t");
			for (int j = 0; j < nPeriods; j++)
				str.append(rolTime.get(rtId)[j] + "\t");
			str.append("\n");
		}
		str.append("Resources availability time grouped in rols\n");
		for (int rtId : rolTime.keySet()) {
			str.append(rtId + "\t");
			for (int j = 0; j < nPeriods; j++)
				str.append(avalTime.get(rtId)[j] + "\t");
			str.append("\n");
		}
		return str.toString();
	}

	/**
	 * Stores how much time a resource has been used for each rol.
	 * @author Roberto Muñoz
	 */
	public class ResourceUsage {
		/** Stores the time this resource is used for each rol. */
		private TreeMap<Integer, double[]> usageTime;
		/** Stores the time this resource is available for each rol. */
		private TreeMap<Integer, double[]> avalTime;
		/** Stores if the resource has benn switched off for a rol. */
		private TreeMap<Integer, Integer> rtRolOff;
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
		public ResourceUsage(int resId) {
			this.resId = resId;
			usageTime = new TreeMap<Integer, double[]>();
			avalTime = new TreeMap<Integer, double[]>();
			rtRolOff = new TreeMap<Integer, Integer>();
			for (int id : simul.getResourceTypeList().keySet()) {
				avalTime.put(id, new double[nPeriods]);
				rtRolOff.put(id, 0);
			}
			caughtRT = ResourceStdUsageListener.NOTUSED;
		}
		
		/**
		 * The resource is available for a rol.
		 * @param rInfo Resource info event
		 */
		public void rolOn(ResourceInfo rInfo) {
			avalTime.get(rInfo.getValue())[currentPeriod] -= rInfo.getTs();
			rtRolOff.put(rInfo.getValue(), 1);
		}

		/**
		 * The resource is not available for a rol.
		 * @param rInfo Resource info event
		 */
		public void rolOff(ResourceInfo rInfo) {
			avalTime.get(rInfo.getValue())[currentPeriod] += rInfo.getTs();
			rtRolOff.put(rInfo.getValue(), 0);
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
			for (int id : avalTime.keySet()) {
				// when the period changes the resources availavility time must be actualized for the
				// last period and for the new period
				if (rtRolOff.get(id) == 1) {
					avalTime.get(id)[currentPeriod - 1] += currentPeriod * period;
					if (currentPeriod < nPeriods)
						avalTime.get(id)[currentPeriod] -= currentPeriod * period;
				}
			}
		}
		
		public void simulationEnd(double ts) {
			if (caughtRT != ResourceStdUsageListener.NOTUSED) {
				double[] rolTime =  usageTime.get(caughtRT);
				rolTime[currentPeriod] += ts - caughtTs;
			}
			for (int id : avalTime.keySet()) {
				// when the period changes the resources availavility time must be actualized for the
				// last period and for the new period
				if (rtRolOff.get(id) == 1) {
					avalTime.get(id)[currentPeriod] += ts;
				}
			}
		}
		
		/**
		 * @return the avalTime
		 */
		public TreeMap<Integer, double[]> getAvalTime() {
			return avalTime;
		}

		/**
		 * @return the usageTime
		 */
		public TreeMap<Integer, double[]> getUsageTime() {
			return usageTime;
		}

		/**
		 * @return the usageTime this resource has been working for a rol.
		 */
		public double[] getUsageTime(int rtId) {
			return usageTime.get(rtId);
		}

		/**
		 * @return the avalTime this resource has been working for a rol.
		 */
		public double[] getAvalTime(int rtId) {
			return avalTime.get(rtId);
		}
		
		public String getUsageString() {
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
		
		public String getAvalString() {
			StringBuffer str = new StringBuffer();
			str.append("RES : " + resId + "\n");
			for (Integer rtId : avalTime.keySet()) {
				str.append(rtId + "\t");
				for (double time : avalTime.get(rtId)) {
					str.append(time + "\t");
				}
				str.append("\n");
			}			
			return str.toString();
		}

	}
}
