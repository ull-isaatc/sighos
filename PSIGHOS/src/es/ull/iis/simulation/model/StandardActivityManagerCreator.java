/**
 * 
 */
package es.ull.iis.simulation.model;

import java.util.Iterator;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import es.ull.iis.simulation.model.flow.RequestResourcesFlow;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class StandardActivityManagerCreator extends ActivityManagerCreator {

	/**
	 * @param simul
	 */
	public StandardActivityManagerCreator(Simulation model) {
		super(model);
	}

	/**
	 * Creates the activity managers which are a partition of the model. This is
	 * equivalent to finding the connected components of a graph G=(V, E) where
	 * each vertex is a resource type and each edge is an activity that is
	 * associated with the resource types represented by the connected vertex.
	 */
	@Override
	protected void createActivityManagers() {
		// The graph is an array consisting on sets of resource types
		SimulationGraph graph = new SimulationGraph();
		
		TreeMap<ResourceType, ActivityManager> marks = new TreeMap<ResourceType, ActivityManager>();
		// This counter lets us mark each partition
		int nManagers = graph.DFS(marks);
		// The activity managers are created
		for (int i = 0; i < nManagers; i++)
			new ActivityManager(model);
		// The activities are associated to the activity managers
		for (RequestResourcesFlow f : model.getActivityList()) {
			Iterator<ActivityWorkGroup> iter = (f).iterator();
			// This step is for non-resource-types activities
			boolean found = false;
			while (iter.hasNext() && !found) {
				ActivityWorkGroup wg = iter.next();
				if (wg.size() > 0) {
					f.setManager(marks.get(wg.getResourceType(0)));
					found = true;
				}
			}
			if (!found) {
				nManagers++;
				f.setManager(new ActivityManager(model));
			}
		}
		for (ResourceType rt : model.getResourceTypeList())
			rt.setManager(marks.get(rt));
	}

	/**
	 * A graph which represents the activities and resource types of the model. 
	 * The graph G=(V, E) is created as follows: each vertex is a resource type 
	 * and each edge is an activity that is associated with the resource types 
	 * represented by the connected vertex.
	 * @author Iván Castilla Rodríguez
	 *
	 */
	class SimulationGraph extends TreeMap<ResourceType, TreeSet<ResourceType>> {
		private static final long serialVersionUID = 1L;

		/**
		 * Creates a graph representing the activities and resource types of the model.
		 */
		SimulationGraph() {
			ResourceType ind1 = null, ind2 = null;
			// Starts by creating one node per resource type
			for (ResourceType rt : model.getResourceTypeList())
				put(rt, new TreeSet<ResourceType>());
			// Goes through the activity list to built the adyacent list 
			for (RequestResourcesFlow f : model.getActivityList()) {
				Iterator<ActivityWorkGroup> iter = f.iterator();
				// Looks for the first WorkGroup that contains at least one resource type
				int firstWG = 1;
				while (iter.hasNext()) {
					ActivityWorkGroup wg = iter.next();
					if (wg.size() > 0) {
						if (firstWG == 1)
							ind1 = wg.getResourceType(0);
						for (; firstWG < wg.size(); firstWG++) {
							ind2 = wg.getResourceType(firstWG);
							get(ind1).add(ind2);
							get(ind2).add(ind1);
							ind1 = ind2;
						}
						firstWG = 0;
					}
				}
			}
			debug();
			
		}

		/**
		 * Makes a depth first search on a graph.
		 * 
		 * @param marks
		 *            Mark array that's used for determining the partition of each
		 *            node.
		 * @return
		 *            The amount of activity managers to be created.
		 */
		int DFS(TreeMap<ResourceType, ActivityManager> marks) {
			ActivityManager am = new ActivityManager(model);
			int nManagers = 0;
			Stack<ResourceType> toVisit = new Stack<ResourceType>();
			for (ResourceType rt : model.getResourceTypeList())
				marks.put(rt, null);// Not-visited mark
			for (ResourceType key : marks.keySet())
				if (marks.get(key) == null) {
					toVisit.push(key);
					while (!toVisit.isEmpty()) {
						ResourceType node = toVisit.pop();
						marks.put(node, am);
						for (ResourceType nnode : get(node))
							if (marks.get(nnode) == null)
								toVisit.push(nnode);					
					}
					am = new ActivityManager(model);
					nManagers++;
				}
			return nManagers;
		}
		
		/**
		 * Prints a graph, where the resource types are nodes and the activities are
		 * the links.
		 */
		void debug() {
			if (Simulation.isDebugEnabled()) {
				StringBuffer str = new StringBuffer();
				// Pinto el graph para chequeo
				for (ResourceType rt : keySet()) {
					str.append("Resource Type (" + rt.getIdentifier() + "): " + rt.getDescription()
							+ "\r\n");
					str.append("\tNeighbours: ");
					for (ResourceType nodo : get(rt))
						str.append(nodo + "\t");
					str.append("\r\n");
				}
				Simulation.debug("Graph created\r\n" + str.toString());
			}
		}
	}

}
