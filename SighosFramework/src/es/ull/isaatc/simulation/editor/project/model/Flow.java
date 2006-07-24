package es.ull.isaatc.simulation.editor.project.model;

import es.ull.isaatc.simulation.editor.util.ModelComponent;

public abstract class Flow extends ModelComponent {

	private static int nextId = 0;
	
	/** iterations this flow is done */
	protected String iterations;

	/**
	 * Creates a new Flow
	 * @param componentType
	 */
	public Flow(ComponentType componentType) {
		super(ComponentType.FLOW);
		setId(nextId++);
	}

	/**
	 * @return the iterations
	 */
	public String getIterations() {
		return iterations;
	}

	/**
	 * @param iterations the iterations to set
	 */
	public void setIterations(String iterations) {
		if (iterations.length() == 0)
			iterations = null;
		this.iterations = iterations;
	}
	
	public String toString() {
		return null;
	}
}
