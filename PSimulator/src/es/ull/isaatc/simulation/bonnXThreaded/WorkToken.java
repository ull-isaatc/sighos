package es.ull.isaatc.simulation.bonnXThreaded;

import java.util.HashSet;

import es.ull.isaatc.simulation.bonnXThreaded.flow.Flow;

public class WorkToken {
	
	HashSet<Flow> path;
	boolean state;
	
	public WorkToken (boolean state) {
		this.state = state;
		path = new HashSet<Flow>();
	}

	public WorkToken(boolean state, Flow startPoint) {
		this(state);
		path.add(startPoint);
	}
	
	public WorkToken(WorkToken token) {
		this(token.isExecutable());
		path.addAll(token.getPath());
	}
	
	public void reset() {
		state = false;
		path.clear();
	}
	
	public void addFlow(Flow visited) {
		path.add(visited);
	}
	
	public void addFlow(HashSet<Flow> path) {
		this.path.addAll(path);
	}
	
	public boolean wasVisited(Flow flow) {
		return path.contains(flow);
	}
	
	public boolean isExecutable() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public HashSet<Flow> getPath() {
		return path;
	}
}
