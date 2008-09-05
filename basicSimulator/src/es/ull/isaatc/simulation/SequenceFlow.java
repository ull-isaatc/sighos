package es.ull.isaatc.simulation;

import java.util.ArrayList;

import es.ull.isaatc.simulation.state.*;

/**
 * A sequential group of flows. Each subflow can be considered a step. Thus, the 
 * (n+1)'th step is executed only when the n'th step has finished its execution.
 * @author Ivn Castilla Rodrguez
 */
public class SequenceFlow extends GroupFlow {
    
    /**
     * Creates a sequence flow.
     * @param parent The parent node of this flow.
     * @param elem Element which carries out this flow.
     */
    public SequenceFlow(SimultaneousFlow parent, Element elem) {
        super(parent, elem);
    }

    /**
     * Creates a sequence flow.
     * @param elem Element which carries out this flow.
     */
    public SequenceFlow(Element elem) {
        super(elem);
    }

    /**
     * Finishes the execution of the current subflow. If there are pending steps in the 
     * sequence, returns the list of the single flows that have to be executed. In other 
     * case, finishes the parent flow.
     * @return A list of single flows to request.
     */    
    protected ArrayList<SingleFlow> finish() {
    	ArrayList<SingleFlow> sfList = new ArrayList<SingleFlow>();
        finishedFlows++;
        if (finishedFlows < descendants.size())
            sfList.addAll(request());
        else if (parent != null)
            sfList.addAll(parent.finish());
        return sfList;
    }
    
    /**
     * Searches the next step of the sequence for the single flows that should be requested. 
     * @return A list of single flows to request.
     */    
    protected ArrayList<SingleFlow> request() {
    	ArrayList<SingleFlow> sfList = new ArrayList<SingleFlow>();
        sfList.addAll(descendants.get(finishedFlows).request());
        return sfList;
    }        

    /** 
     * Returns the state of this sequence flow, that is, the state of its descendants, and
     * the <code>finishedFlows</code> atribute.
     * @return The state of this sequence flow.
     */
	public FlowState getState() {
		SequenceFlowState state = new SequenceFlowState(finishedFlows);
		for(Flow f : descendants)
			state.add((FlowState)f.getState());
		return state;
	}

    /** 
     * Sets the state of this sequence flow. Uses the <code>finishedFlows</code> atribute,
     * creates the descendant flows, and sets their state.
     * @param state The state of this sequence flow.
     */	
	public void setState(FlowState state) {
		SequenceFlowState secState = (SequenceFlowState) state;
		finishedFlows = secState.getFinished();
		
		for (FlowState fState : secState.getDescendants()) {
			Flow f = null;
			if (fState instanceof SingleFlowState)
				f = new SingleFlow(this, elem, elem.getSimul().getActivity(((SingleFlowState)fState).getActId()));
			else if (fState instanceof SimultaneousFlowState)
				f = new SimultaneousFlow(this, elem);
			f.setState(fState);
		}
	}
}
