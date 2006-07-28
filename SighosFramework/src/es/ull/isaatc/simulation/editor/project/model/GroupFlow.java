package es.ull.isaatc.simulation.editor.project.model;

import java.util.ArrayList;
import java.util.List;

import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem;

public abstract class GroupFlow extends Flow implements ContainerFlow {

	/** descendants */
	protected ArrayList<Flow> descendants;

	/**
	 * Creates a new Flow
	 * @param componentType
	 */
	public GroupFlow() {
		super(ComponentType.FLOW);
	}
	
	/**
	 * @return the packageFlowList
	 */
	public ArrayList<Flow> getFlowList() {
		if (descendants == null)
			descendants = new ArrayList<Flow>();
		return descendants;
	}

	/**
	 * @param packageFlowList
	 *            the packageFlowList to set
	 */
	public void setFlowList(ArrayList<Flow> flowList) {
		this.descendants = flowList;
	}

	/**
	 * 
	 * @param flow
	 *            the flow to add to the descendant list
	 */
	public void addFlow(Flow flow) {
		getFlowList().add(flow);
	}

	@Override
	public List<ProblemTableItem> validate() {
		super.validate();
		for (Flow flow : getFlowList())
			problems.addAll(flow.validate());
		return problems;
	}	
}
