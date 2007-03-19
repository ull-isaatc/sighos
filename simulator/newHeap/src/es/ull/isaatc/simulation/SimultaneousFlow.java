package es.ull.isaatc.simulation;

import java.util.ArrayList;

import es.ull.isaatc.simulation.state.FlowState;
import es.ull.isaatc.simulation.state.SequenceFlowState;
import es.ull.isaatc.simulation.state.SimultaneousFlowState;
import es.ull.isaatc.simulation.state.SingleFlowState;

/**
 * A set of flows that are requested simultaneously.
 * @author Iván Castilla Rodríguez
 */
public class SimultaneousFlow extends GroupFlow {
    
    /**
     * Creates a simultaneous flow.
     * @param parent The parent node of this flow.
     * @param elem Element which carries out this flow.
     */
    public SimultaneousFlow(SequenceFlow parent, Element elem) {
        super(parent, elem);
    }

    /**
     * Creates a simultaneous flow.
     * @param elem Element which carries out this flow.
     */
    public SimultaneousFlow(Element elem) {
        super(elem);
    }

    /**
     * Finishes the execution of the current subflow. If there are pending descendants, 
     * returns the list of the single flows that have to be executed. In other case, 
     * finishes the parent flow.
     * @return A list of single flows to request.
     */    
    protected synchronized ArrayList<SingleFlow> finish() {
    	ArrayList<SingleFlow> sfList = new ArrayList<SingleFlow>();
        finishedFlows++;
        if ((finishedFlows == descendants.size()) && (parent != null))
            sfList.addAll(parent.finish());
        return sfList;
    }
    
    /**
     * Requests each descendant flow, thus returning the single flows that should be requested. 
     * @return A list of single flows to request.
     */    
    protected ArrayList<SingleFlow> request() {
    	ArrayList<SingleFlow> sfList = new ArrayList<SingleFlow>();
        for (int i = 0; i < descendants.size(); i++) 
            sfList.addAll(descendants.get(i).request());
        return sfList;
    }       

    /** 
     * Returns the state of this simultaneous flow, that is, the state of its descendants, 
     * and the <code>finishedFlows</code> atribute.
     * @return The state of this simultaneous flow.
     */
	public FlowState getState() {
		SimultaneousFlowState state = new SimultaneousFlowState(finishedFlows);
		for(Flow f : descendants)
			state.add((FlowState)f.getState());
		return state;
	}

    /** 
     * Sets the state of this simultaneous flow. Uses the <code>finishedFlows</code> atribute,
     * creates the descendant flows, and sets their state.
     * @param state The state of this simultaneous flow.
     */	
	public void setState(FlowState state) {
		SimultaneousFlowState simState = (SimultaneousFlowState) state;
		finishedFlows = simState.getFinished();
		
		for (FlowState fState : simState.getDescendants()) {
			Flow f = null;
			if (fState instanceof SingleFlowState)
				f = new SingleFlow(this, elem, elem.getSimul().getActivity(((SingleFlowState)fState).getActId()));
			else if (fState instanceof SequenceFlowState)
				f = new SequenceFlow(this, elem);
			f.setState(fState);
		}
	}
}
