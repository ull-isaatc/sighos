package es.ull.isaatc.simulation;

import java.util.ArrayList;

import es.ull.isaatc.simulation.state.RecoverableState;
import es.ull.isaatc.simulation.state.FlowState;

/**
 * Activity flow of an element. A flow has a tree-like structure, and each node can be a single flow
 * or a group flow. A single flow is a leaf node which contains an activity.
 * @author Iván Castilla Rodríguez
 */
public abstract class Flow implements RecoverableState<FlowState> {
    /** The parent node of this flow. */    
    protected Flow parent = null;
    /** Element which carries out this flow. */    
    protected Element elem;
    
    /**
     * Creates a new node of a flow.
     * @param parent The parent node of this flow.
     * @param elem Element which carries out this flow.
     */
    public Flow(Flow parent, Element elem) {
        this.parent = parent;
        this.elem = elem;
    }
    
    /**
     * Creates a new root node of a flow. A root node is a node without parent.
     * @param elem Element which carries out this flow.
     */
    public Flow(Element elem) {
        this.elem = elem;
    }
    
    /**
     * Returns the parent of this flow.
     * @return Parent of this flow.
     */
    public es.ull.isaatc.simulation.Flow getParent() {
        return parent;
    }
    
    /**
     * Returns the element which carries out this flow.
     * @return The associated element.
     */
    public Element getElement() {
        return elem;
    }
    
    /**
     * Returns the next set of single flows that should be requested.<p> 
     * The implementation iterates over the descendant flows searching for single flows
     * which have not been requested yet.
     * @return A list of single flows to request.
     */    
    protected abstract ArrayList<SingleFlow> request();
    
    /**
     * Finishes the flow execution and returns a list of single flows that must
     * be executed after this flow is finished.
     * @return A list of single flows to request.
     */    
    protected abstract ArrayList<SingleFlow> finish();

    /**
     * Returns the amount of activities this flow contains. This value is returned as
     * a two components array: the first component is for the presential activities,
     * and the second component, for the non-presential ones. 
     * @return The amount of activities this flow contains.
     */    
    protected abstract int[]countActivities();
 
    /**
     * Searches a single flow in the flow structure
     * @param id single flow's identifier
     * @return The single flow with identifier id.
     */
    protected abstract SingleFlow search(int id);
    
    /**
     * Returns true if the element has finished with this flow.
     * @return True if the flow is finished; false in other case.
     */
    protected abstract boolean isFinished();
}
