/**
 * 
 */
package es.ull.isaatc.simulation.editor.project.model;

import java.util.Stack;

import es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.SighosCell;

/**
 * @author Roberto Muñoz
 *
 */
public class FlowStack {
	
	/** Stack */
	private Stack<ContainerElement> stack = new Stack<ContainerElement>();
	
	public ContainerElement pop() {
		return stack.pop();
	}
	
	public ContainerElement push(ContainerElement ce) {
		stack.push(ce);
		return ce;
	}

	public ContainerElement push(int openedBranches, Flow flow, SighosCell cell) {
		push(new ContainerElement(openedBranches, flow, cell));
		return top();
	}
	
	public ContainerElement top() {
		if (empty())
			return null;
		return stack.lastElement();
	}
	
	public boolean empty() {
		return stack.empty();
	}
	
	/**
	 * Actualize the stack top with a closed branch
	 * @return true if container is completed. false elsewhere
	 */
	public ContainerElement branchFinished() {
		// if all branches are closed remove the top container
		if (top().decOpenenBranches() == 0) {
			ContainerElement ce = pop();
			Flow flow = ce.getFlow();
			if ((flow instanceof DecisionBranchFlow) || (flow instanceof TypeBranchFlow))
				return branchFinished();
			return ce;
		}
		return top();
	}
	
	/**
	 * Clears the stack
	 */
	public void reset() {
		stack.clear();
	}
	
	public class ContainerElement {
		private int openedBranches;
		
		private Flow flow;
		
		private SighosCell cell;

		/**
		 * @param openedBranches
		 * @param flow
		 */
		public ContainerElement(int branches, Flow flow, SighosCell cell) {
			super();
			this.openedBranches = branches;
			this.flow = flow;
			this.cell = cell;
		}

		/**
		 * @return the openedBranches
		 */
		public int getOpenedBranches() {
			return openedBranches;
		}

		/**
		 * @param openedBranches the openedBranches to set
		 */
		public void setOpenedBranches(int openedBranches) {
			this.openedBranches = openedBranches;
		}
		
		/**
		 * Decrements the opened branches for this container
		 * @return the openedBranches
		 */
		public int decOpenenBranches() {
			return (--openedBranches);
		}

		/**
		 * @return the flow
		 */
		public Flow getFlow() {
			return flow;
		}

		/**
		 * @param flow the flow to set
		 */
		public void setFlow(Flow flow) {
			this.flow = flow;
		}

		/**
		 * @return the cell
		 */
		public SighosCell getCell() {
			return cell;
		}

		/**
		 * @param cell the cell to set
		 */
		public void setCell(SighosCell cell) {
			this.cell = cell;
		}
	}
}
