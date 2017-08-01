package es.ull.iis.simulation.model;

import java.util.TreeSet;

import es.ull.iis.simulation.model.flow.Flow;

/**
 * The information of the current state of an {@link ElementInstance element instance}. In short, it indicates whether 
 * the current element instance is valid or not, and records the visited flows to avoid infinite loops. 
 * @author Yeray Callero
 */
public class WorkToken {
	/** The list of flows already visited during the current timestamp */ 
	private final TreeSet<Flow> path = new TreeSet<Flow>();
	/** Validity of the element instance containing this token */
	private boolean state;
	
	/**
	 * Creates a work token.
	 * @param state The initial state of the work token
	 */
	public WorkToken (boolean state) {
		this.state = state;
	}

	/**
	 * Creates a work token.
	 * @param state The initial state of the work token
	 * @param startPoint The first flow that this token passes by
	 */
	public WorkToken(boolean state, Flow startPoint) {
		this(state);
		path.add(startPoint);
	}
	
	/**
	 * Creates a work token copy of a previously created one.
	 * @param token Previously created token
	 */
	public WorkToken(WorkToken token) {
		this(token.isExecutable());
		path.addAll(token.getPath());
	}
	
	/**
	 * Resets this token by setting its state to <code>false</code> and clearing the list of 
	 * visited flows.
	 */
	public void reset() {
		state = false;
		path.clear();
	}
	
	/**
	 * Adds a flow to the list of visited ones.
	 * @param visited New flow visited by the element instance containing this token
	 */
	public void addFlow(Flow visited) {
		path.add(visited);
	}
	
	/**
	 * Adds a collection of flows to the list of visited ones.
	 * @param path Collection of new flow visited by the element instance containing this token
	 */
	public void addFlow(TreeSet<Flow> path) {
		this.path.addAll(path);
	}
	
	/**
	 * Returns true if the specified flow was already visited by the element instance containing this token.
	 * @param flow Flow that has to be checked
	 * @return True of the specified flow was already visited by the element instance containing this token;
	 * false in other case.
	 */
	public boolean wasVisited(Flow flow) {
		return path.contains(flow);
	}
	
	/**
	 * Returns true if the element instance containing this token is valid; false in other case.
	 * @return True if the element instance containing this token is valid; false in other case.
	 */
	public boolean isExecutable() {
		return state;
	}

	/**
	 * Sets the state of this work token.
	 * @param state New state of this work token
	 */
	public void setState(boolean state) {
		this.state = state;
	}

	/**
	 * Returns the list of flows already visited by the element instance containing this token.
	 * @return The list of flows already visited by the element instance containing this token
	 */
	public TreeSet<Flow> getPath() {
		return path;
	}
}
