package es.ull.isaatc.simulation;

import java.util.ArrayList;

/**
 * Represents a node containing several subflows.
 * @author Iván Castilla Rodríguez
 */
public abstract class GroupFlow extends Flow {
    /** List of descendants */    
    protected ArrayList<Flow> descendants;
    /** Amount of already finished subflows */    
    protected int finishedFlows;
    
    /**
     * Creates a new groupflow, which is a root node.
     * @param elem Element which carries out this flow.
     */
    public GroupFlow(Element elem) {
        super(elem);
        descendants = new ArrayList<Flow>();
    }
    
    /**
     * Creates a new groupflow. This flow is added to the descendant list of its parent.
     * @param parent The parent node of this flow.
     * @param elem Element which carries out this flow.
     */
    public GroupFlow(GroupFlow parent, Element elem) {
        super(parent, elem);
        if (parent != null)
        	parent.add(this);
        descendants = new ArrayList<Flow>();
    }
    
    /**
     * Adds a flow to the descendant list.
     * @param newFlow Flow to be added.
     */    
    protected void add(Flow newFlow) {
        descendants.add(newFlow);
    }
    
	@Override
    protected int[] countActivities() {
        int []cont = new int[2];
        cont[0] = cont[1] = 0;
        for(Flow f : descendants) {
            int []contAux = f.countActivities();
            cont[0] += contAux[0];
            cont[1] += contAux[1];
        }
        return cont;
    }
       
	@Override
	protected SingleFlow search(int id) {
		for (Flow f : descendants) {
			SingleFlow sf = f.search(id);
			if (sf != null)
				return sf;
		}
		return null;
	}
	
	/**
	 * A group flow has finished when all its descendant flows have finished.
	 * @return True if all the descendant flows have finished. False in other case. 
	 */
	@Override
	protected boolean isFinished() {
		return (finishedFlows == descendants.size());
	}
}
