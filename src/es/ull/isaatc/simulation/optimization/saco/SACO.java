/**
 * 
 */
package es.ull.isaatc.simulation.optimization.saco;

import java.util.List;

import es.ull.isaatc.simulation.optimization.saco.ConstructionGraph.Edge;
import es.ull.isaatc.simulation.optimization.saco.ConstructionGraph.Node;

/**
 * This class is a template for implementing SACO methods.
 * 
 * @author Roberto Muñoz
 */
public abstract class SACO {
	/** the construction graph */
	protected ConstructionGraph C = new ConstructionGraph();
	/** number of rounds */
	protected int m;
	/** number of ants */
	protected int s;
	
	/**
	 * Runs the method
	 */
	public void run() {
		Solution delta;
		double diff;
		Node k, l;

		
		init();
		for (int round = 0; round < m; round++) {
			Solution[] x = new Solution[s];
			for (int ant = 0; ant < s; ant++) {
				k = C.getStartNode();
				Solution walk = new Solution();
				walk.addNode(k);
				while (isSolutionFeasible(walk.getLast())) {
					
					k = l;
					walk.addNode(l);
				}
				x[ant] = walk;
			}
			
			simulate(x);
			
			if (round == 1) // the best solution is chosen for the first round
				delta = x; // delta is the candidate for the best solution
			else {
				double diff;
				
				if (diff < 0)
					delta = x;
			}
			
			// evaporation
			
			// global-best reinforcement
			
			// round-best reinforcement
		}
	}
	
	/**
	 * Check if there is an edge u, v feasible
	 * @param u 
	 * @return true if the solution is feasible, false in other case
	 */
	private boolean isSolutionFeasible(Node u) {
		return true;
	}
	
	/**
	 * Initialize the construction graph.
	 */
	protected abstract void init();
	
	/**
	 * Performs a set of simulation experiments with random scenarios.
	 * The best solution x is selected out of the solutions x1,...,xs
	 */
	protected abstract void simulate(Solution sol[]);
	

}
