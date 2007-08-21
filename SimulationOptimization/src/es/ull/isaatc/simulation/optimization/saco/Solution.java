/**
 * 
 */
package es.ull.isaatc.simulation.optimization.saco;

import java.util.ArrayList;

import es.ull.isaatc.simulation.optimization.saco.ConstructionGraph.Node;

/**
 * This class is used for defining a solution.
 * 
 * @author Roberto Muñoz
 */
public class Solution {

	/** the walk through the construction graph */
	private ArrayList<Node> walk = new ArrayList<Node>();
	
	public void addNode(Node u) {
		walk.add(u);
	}

	/**
	 * @return the walk
	 */
	public ArrayList<Node> getWalk() {
		return walk;
	}
	
	public Node getLast() {
		return walk.get(walk.size() - 1);
	}
}
