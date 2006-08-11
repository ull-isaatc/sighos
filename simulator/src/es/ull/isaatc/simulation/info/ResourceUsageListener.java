/**
 * 
 */
package es.ull.isaatc.simulation.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.ull.isaatc.util.Orderable;
import es.ull.isaatc.util.OrderedList;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ResourceUsageListener implements SimulationListener {
	private HashMap<Integer, OrderedList<ResourceUsage>> resources;

	public ResourceUsageListener() {
		resources = new HashMap<Integer, OrderedList<ResourceUsage>>();
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationComponentInfo)
	 */
	public void infoEmited(SimulationObjectInfo info) {
		if (info instanceof ResourceInfo) {
			ResourceInfo rInfo = (ResourceInfo) info;
			OrderedList<ResourceUsage> list = resources.get(rInfo.getIdentifier());
			if (rInfo.getType() == ResourceInfo.Type.ROLON) {
				if (list == null) {
					list = new OrderedList<ResourceUsage>();
					list.add(new ResourceUsage(rInfo.getValue()));
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
			OrderedList<ResourceUsage> list = resources.get(rInfo.getIdentifier());
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

	public void infoEmited(TimeChangeInfo info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("Resource usage and availability\r\n");
		for (Map.Entry<Integer, OrderedList<ResourceUsage>> entry : resources.entrySet()) {
			str.append("R" + entry.getKey() + "\r\n");
			for (ResourceUsage ru : entry.getValue())
				str.append(ru);
		}
		return str.toString();
	}
	
	class ResourceUsage implements Orderable {
		int rtId;
		int count = 0;
		private int current = -1;
		ArrayList<double[]> availability;
		ArrayList<double[]> usage;
		
		/**
		 * @param rtId
		 * @param count
		 */
		public ResourceUsage(int rtId) {
			this.rtId = rtId;
			availability = new ArrayList<double[]>();
			usage = new ArrayList<double[]>();
		}

		public void addRolOn(double ts) {
			if (count == 0) {
				current++;
				double []aux = {ts, Double.NaN};
				availability.add(aux);
			}
			count++;
		}
		
		public void addRolOff(double ts) {
			availability.get(current)[1] = ts;
			count--;
		}
		
		public void addCaught(double ts) {
			double []aux = {ts, Double.NaN};
			usage.add(aux);
		}
		
		public void addReleased(double ts) {
			usage.get(usage.size() - 1)[1] = ts;
		}
		
		public Comparable getKey() {
			return rtId;
		}

		public int compareTo(Orderable o) {
			return compareTo(o.getKey());
		}

		public int compareTo(Object o) {
			return getKey().compareTo(o);
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
