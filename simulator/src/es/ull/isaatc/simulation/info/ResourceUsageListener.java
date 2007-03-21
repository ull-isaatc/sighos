/**
 * 
 */
package es.ull.isaatc.simulation.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Listens the usage and availability of the resources.
 * @author Iván Castilla Rodríguez
 *
 */
public class ResourceUsageListener implements SimulationListener {
	/** Resource usage and availability. */
	private HashMap<Integer, TreeMap<Integer, ResourceUsage>> resources;

	public ResourceUsageListener() {
		resources = new HashMap<Integer, TreeMap<Integer, ResourceUsage>>();
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationComponentInfo)
	 */
	public void infoEmited(SimulationObjectInfo info) {
		if (info instanceof ResourceInfo) {
			ResourceInfo rInfo = (ResourceInfo) info;
			TreeMap<Integer, ResourceUsage> list = resources.get(rInfo.getIdentifier());
			if (rInfo.getType() == ResourceInfo.Type.ROLON) {
				if (list == null) {
					list = new TreeMap<Integer, ResourceUsage>();
					list.put(rInfo.getValue(), new ResourceUsage(rInfo.getValue()));
				}
				ResourceUsage aux = list.get(rInfo.getValue());
				aux.addRolOn(rInfo.getTs());
				resources.put(rInfo.getIdentifier(), list);					
			}
			else if (rInfo.getType() == ResourceInfo.Type.ROLOFF) {
				ResourceUsage aux = list.get(rInfo.getValue());
				aux.addRolOff(rInfo.getTs());
				resources.put(rInfo.getIdentifier(), list);
			}
		}
		else if (info instanceof ResourceUsageInfo) {
			ResourceUsageInfo rInfo = (ResourceUsageInfo) info;
			TreeMap<Integer, ResourceUsage> list = resources.get(rInfo.getIdentifier());
			if (rInfo.getType() == ResourceUsageInfo.Type.CAUGHT) {
				ResourceUsage aux = list.get(rInfo.getRtId());
				aux.addCaught(rInfo.getTs());
				resources.put(rInfo.getIdentifier(), list);
			}
			else if (rInfo.getType() == ResourceUsageInfo.Type.RELEASED) {
				ResourceUsage aux = list.get(rInfo.getRtId());
				aux.addReleased(rInfo.getTs());
				resources.put(rInfo.getIdentifier(), list);
			}
		}
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationStartInfo)
	 */
	public void infoEmited(SimulationStartInfo info) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationEndInfo)
	 */
	public void infoEmited(SimulationEndInfo info) {
		// FIXME: temporal
		System.out.println(this);
	}

	// Nothing to do
	public void infoEmited(TimeChangeInfo info) {
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("Resource usage and availability\r\n");
		for (Map.Entry<Integer, TreeMap<Integer, ResourceUsage>> entry : resources.entrySet()) {
			str.append("R" + entry.getKey() + "\r\n");
			for (ResourceUsage ru : entry.getValue().values())
				str.append(ru);
		}
		return str.toString();
	}

	/**
	 * Stores the usage and availability of a single resource as a resorce type.  
	 * @author Iván Castilla Rodríguez
	 *
	 */
	class ResourceUsage implements Comparable<ResourceUsage> {
		/** Resource type which this resource has been used for */
		int rtId;
		/** This value is increased every time a resource becomes available and decreased 
		 * every time the resource becomes unavailable. */
		int count = 0;
		/** Indicates the current availability entry. */
		private int current = -1;
		/** Availability entries. */
		ArrayList<double[]> availability;
		/** Usage entries. */
		ArrayList<double[]> usage;
		
		/**
		 * @param rtId Resource type identifier.
		 */
		public ResourceUsage(int rtId) {
			this.rtId = rtId;
			availability = new ArrayList<double[]>();
			usage = new ArrayList<double[]>();
		}

		/**
		 * The resource has become available at the specified timestamp. If the resource is
		 * currently being used for this resource type, the <code>count</code> value is incremented.
		 * In other case, a new availability entry is created.
		 * @param ts Timestamp whem the resource has become available.
		 */
		public void addRolOn(double ts) {
			if (count == 0) {
				current++;
				double []aux = {ts, Double.NaN};
				availability.add(aux);
			}
			count++;
		}
		
		/**
		 * The resource has become unavailable at the specified timestamp. The <code>count</code> value 
		 * is decremented.
		 * @param ts Timestamp whem the resource has become unavailable.
		 */
		public void addRolOff(double ts) {
			availability.get(current)[1] = ts;
			count--;
		}
		
		/**
		 * The resource has been taken to carry out an activity at the specified timestamp.
		 * @param ts Timestamp whem the resource has been caught.
		 */
		public void addCaught(double ts) {
			double []aux = {ts, Double.NaN};
			usage.add(aux);
		}
		
		/**
		 * The resource has been freed to carry out an activity at the specified timestamp.
		 * @param ts Timestamp whem the resource has been released.
		 */
		public void addReleased(double ts) {
			usage.get(usage.size() - 1)[1] = ts;
		}

		public int compareTo(ResourceUsage o) {
			if (rtId < o.rtId)
				return -1;
			if (rtId > o.rtId)
				return 1;
			return 0;
		}
		
		@Override
		public String toString() {
			StringBuffer str = new StringBuffer();
			str.append("RT" + rtId + " Availability:\t");
			for (double [] val : availability)
				str.append("[" + val[0] + "-" + val[1] + "]\t");
			str.append("\r\nRT" + rtId + " Usage:\t");
			for (double [] val : usage)
				str.append("[" + val[0] + "-" + val[1] + "]\t");
			str.append("\r\n");
			return str.toString();
			
		}
	}

}
