/**
 * 
 */
package es.ull.isaatc.simulation.optimization.saco;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * This class represents the construction graph used for representing each problem instance.
 * 
 * @author Roberto Muñoz
 */
public class ConstructionGraph {
	
	/** the start node */
	private Node startNode;
	
	/** the end node */
	private Node endNode;
	
	/** the graph */
	private TreeMap<Node, List<Edge>> graph;
	
	/**
	 * 
	 */
	public ConstructionGraph() {
		startNode = new Node();
		endNode = new Node();
		graph = new TreeMap<Node, List<Edge>>();
		
		addNode(startNode);
		addNode(endNode);
	}

	/**
	 * @return the endNode
	 */
	public Node getEndNode() {
		return endNode;
	}

	/**
	 * @return the startNode
	 */
	public Node getStartNode() {
		return startNode;
	}


	/**
	 * Inserts a node in the graph. If the node exists the edge list is cleared.
	 * @param uç the node to insert
	 */
	public void addNode(Node u) {
		graph.put(u, new ArrayList<Edge>());
	}

	/**
	 * Inserts an edge in the graph.
	 * @param v the edge to insert
	 */
	public void addEdge(Edge e) {
		graph.get(e.getSource()).add(e);
	}
	
	/**
	 * Returns the succesors of node u.
	 * @return the edge list where u is the source node
	 */
	public List<Edge> getSuccList(Node u) {
		return graph.get(u);
	}
	
	/**
	 * Represents an edge between two nodes in the construction graph. The source node is u
	 * and the destination node is v.
	 * 
	 * @author Roberto Muñoz
	 */
	public class Edge {
		/** source node */
		private Node u;
		/** destination node */
		private Node v;
		/** memory value that stores the goodness of arc (u, v) */
		private double pheromone = 1;

		/**
		 * @return the pheromone
		 */
		public double getPheromone() {
			return pheromone;
		}

		/**
		 * @param pheromone the pheromone to set
		 */
		public void setPheromone(double pheromone) {
			this.pheromone = pheromone;
		}

		/**
		 * @return the source node
		 */
		public Node getSource() {
			return u;
		}

		/**
		 * @param u the source node to set
		 */
		public void setSource(Node u) {
			this.u = u;
		}

		/**
		 * @return the destination node
		 */
		public Node getV() {
			return v;
		}

		/**
		 * @param l the destination node to set
		 */
		public void setV(Node v) {
			this.v = v;
		}
	}
	
	/**
	 * Represents a node in the construction graph.
	 * 
	 * @author Roberto Muñoz
	 */
	public class Node {
		
	}
}
