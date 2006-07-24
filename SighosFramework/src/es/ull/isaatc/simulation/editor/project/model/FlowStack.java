/**
 * 
 */
package es.ull.isaatc.simulation.editor.project.model;

import java.util.Stack;

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

	public ContainerElement push(int openedBranches, Flow flow) {
		push(new ContainerElement(openedBranches, flow));
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
	public boolean branchFinished() {
		// if all branches are closed remove the top container
		if (top().decOpenenBranches() == 0) {
			Flow flow = pop().getFlow();
			if ((flow instanceof DecisionBranchFlow) || (flow instanceof TypeBranchFlow))
				return branchFinished();
			return true;
		}
		return false;
	}
	
	public class ContainerElement {
		private int openedBranches;
		
		private Flow flow;

		/**
		 * @param openedBranches
		 * @param flow
		 */
		public ContainerElement(int branches, Flow flow) {
			super();
			this.openedBranches = branches;
			this.flow = flow;
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
	}
}
