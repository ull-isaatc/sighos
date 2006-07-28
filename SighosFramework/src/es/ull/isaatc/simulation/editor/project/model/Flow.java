package es.ull.isaatc.simulation.editor.project.model;

import java.util.List;

import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;


public abstract class Flow extends ModelComponent {

	private static int nextId = 0;
	
	/** iterations this flow is done */
	protected String iterations;
	
	/** parent of this flow */
	protected Flow parent;

	/**
	 * Creates a new Flow
	 * @param componentType
	 */
	public Flow(ComponentType componentType) {
		super(ComponentType.FLOW);
		setId(nextId++);
		setDescription("Flow");
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
	
	/**
	 * @return the parent
	 */
	public Flow getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(Flow parent) {
		this.parent = parent;
	}
	
	@Override
	public String getComponentString() {
		return ResourceLoader.getMessage("flow");
	}

	@Override
	public List<ProblemTableItem> validate() {
		super.validate();
		
		return problems;
	}
	
	public String toString() {
		return null;
	}
}
