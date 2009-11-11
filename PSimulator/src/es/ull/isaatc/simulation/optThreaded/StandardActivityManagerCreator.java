/**
 * 
 */
package es.ull.isaatc.simulation.optThreaded;

import java.util.Iterator;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class StandardActivityManagerCreator extends ActivityManagerCreator {

	/**
	 * @param simul
	 */
	public StandardActivityManagerCreator(Simulation simul) {
		super(simul);
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
		
		TreeMap<Integer, Integer> marks = new TreeMap<Integer, Integer>();
		// This counter lets us mark each partition
		int nManagers = graph.DFS(marks);
		// The activity managers are created
		for (int i = 0; i < nManagers; i++)
			new ActivityManager(simul);
		// The activities are associated to the activity managers
		for (Activity a : simul.getActivityList().values()) {
			Iterator<Activity.ActivityWorkGroup> iter = a.iterator();
			// This step is for non-resource-types activities
			boolean found = false;
			while (iter.hasNext() && !found) {
				WorkGroup wg = iter.next();
				if (wg.size() > 0) {
					a.setManager(simul.getActivityManagerList().get(marks.get(wg.getResourceType(0).getIdentifier())));
					found = true;
				}
			}
			if (!found) {
				nManagers++;
				a.setManager(new ActivityManager(simul));
			}
		}
		for (ResourceType rt : simul.getResourceTypeList().values())
			rt.setManager(simul.getActivityManagerList().get(marks.get(rt.getIdentifier())));
	}

	/**
	 * A graph which represents the activities and resource types of the model. 
	 * The graph G=(V, E) is created as follows: each vertex is a resource type 
	 * and each edge is an activity that is associated with the resource types 
	 * represented by the connected vertex.
	 * @author Iván Castilla Rodríguez
	 *
	 */
	class SimulationGraph extends TreeMap<Integer, TreeSet<Integer>> {
		private static final long serialVersionUID = 1L;

		/**
		 * Creates a graph representing the activities and resource types of the model.
		 */
		SimulationGraph() {
			int ind1 = -1, ind2 = -1;
			// Starts by creating one node per resource type
			for (Integer key : simul.getResourceTypeList().keySet())
				put(key, new TreeSet<Integer>());
			// Goes through the activity list to built the adyacent list 
			for (Activity a : simul.getActivityList().values()) {
				Iterator<Activity.ActivityWorkGroup> iter = a.iterator();
				// Looks for the first WorkGroup that contains at least one resource type
				int firstWG = 1;
				while (iter.hasNext()) {
					WorkGroup wg = iter.next();
					if (wg.size() > 0) {
						if (firstWG == 1)
							ind1 = wg.getResourceType(0).getIdentifier();
						for (; firstWG < wg.size(); firstWG++) {
							ind2 = wg.getResourceType(firstWG).getIdentifier();
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
		int DFS(TreeMap<Integer, Integer> marks) {
			int nManagers = 0;
			Stack<Integer> toVisit = new Stack<Integer>();
			for (ResourceType rt : simul.getResourceTypeList().values())
				marks.put(rt.getIdentifier(), -1);// Not-visited mark
			for (Integer key : marks.keySet())
				if (marks.get(key) == -1) {
					toVisit.push(key);
					while (!toVisit.isEmpty()) {
						Integer node = toVisit.pop();
						marks.put(node, nManagers);
						for (Integer nnode : get(node))
							if (marks.get(nnode) == -1)
								toVisit.push(nnode);					
					}
					nManagers++;
				}
			return nManagers;
		}
		
		/**
		 * Prints a graph, where the resource types are nodes and the activities are
		 * the links.
		 */
		void debug() {
			if (simul.isDebugEnabled()) {
				StringBuffer str = new StringBuffer();
				// Pinto el graph para chequeo
				for (Integer key : keySet()) {
					ResourceType rt = simul.getResourceType(key);
					str.append("Resource Type (" + key + "): " + rt.getDescription()
							+ "\r\n");
					str.append("\tNeighbours: ");
					for (Integer nodo : get(key))
						str.append(nodo + "\t");
					str.append("\r\n");
				}
				simul.debug("Graph created\r\n" + str.toString());
			}
		}
	}

}
