/*
 * FlujoSimple.java
 *
 * Created on 17 de junio de 2005, 12:47
 */

package es.ull.isaatc.simulation;

import es.ull.isaatc.simulation.state.FlowState;
import es.ull.isaatc.simulation.state.SingleFlowState;

/**
 * A single-activity flow. This flow represents a leaf node in the flow tree. 
 * @author Iván Castilla Rodríguez
 */
public class SingleFlow extends Flow {
	/** Single flows' Counter. Useful for identying each single flow */
	private static int counter = 0;
	/** Single flow's identifier */
	protected int id;
    /** Activity wrapped with this flow */
    protected Activity act;
    /** Indicates if the activity has been already executed */
    protected boolean finished = false;
    
    /** 
     * Creates a new parent single flow which wraps an activity. 
     * @param elem Element that executes this flow.
     * @param act Activity wrapped with this flow.
     */
    public SingleFlow(Element elem, Activity act) {
        super(elem);
        this.act = act;
        id = counter++;
    }
    
    /** 
     * Creates a new single flow which wraps an activity. 
     * @param parent This flow's parent.
     * @param elem Element that executes this flow.
     * @param act Activity wrapped with this flow.
     */
    public SingleFlow(GroupFlow parent, Element elem, Activity act) {
        super(parent, elem);
        if (parent != null)
        	parent.add(this);
        this.act = act;
        id = counter++;
    }

    /** 
     * Creates a new single flow for a non presential activity. This flow's parent 
     * is the single flow of the original element. 
     * @param parent This flow's parent.
     * @param elem Non-presential element that is going to carry out the activity.
     * @param act Non-presential activity.
     */
    public SingleFlow(SingleFlow parent, NPElement elem, Activity act) {
        super(parent, elem);
        this.act = act;
    }
    
    /**
     * Getter for property act.
     * @return Value of property act.
     */
    public Activity getActivity() {
        return act;
    }
    
    /**
     * Setter for property act.
     * @param act New value of property act.
     */
    public void setActivity(Activity act) {
        this.act = act;
    }

    /**
     * This flow is marked as "finished" and removed from the element's requested list. 
     * The parent flow is finished too.
     */
    protected void finish() {
        finished = true;
        if (parent != null)
            parent.finish();
        elem.decRequested(this);
    }
    
    /**
     * Requests this activity, taking into account the presenciality.
     */
    protected void request() {
        if (isPresential()) {
            elem.requestActivity(this);
        }
        else {
            NPElement eNP = new NPElement(elem);
            eNP.setFlow(new SingleFlow(this, eNP, act));
            eNP.start(act.getManager().getLp());
        }
        // MOD 24/01/06 Movido al evento
        //elem.incRequested(this);
    }
    
    /**
     * Devuelve si la actividad es presencial (requiere de la dedicación 
     * exclusiva del elemento que la solicitó).
     * @return Verdadero (true) si es presencial; Falso (false) si no.
     */
    protected boolean isPresential() {
        if (parent != null)
            if (elem != parent.getElement())
                return true;
        return act.isPresential();
    }
    
    /**
     * Devuelve un array con dos componentes: una de ellas vale 1 y la otra 0. 
     * Si la actividad es presencial, la componente 0 es la que vale 1. Si es 
     * no presencial será la componente 1.
     * @return El número de actividades de este flujo.
     */
    protected int[] countActivities() {
        int [] cont = new int[2];
        if (act.isPresential())
            cont[0]++;
        else
            cont[1]++;
        return cont;
    }

	/**
	 * @return Returns the id.
	 */
	public int getIdentifier() {
		return id;
	}

	/**
	 * @param counter The counter to set.
	 */
	public static void setCounter(int counter) {
		SingleFlow.counter = counter;
	}
	
	/**
	 * 
	 * @return The single flows' counter
	 */
	public static int getCounter() {
		return counter;
	}

	public FlowState getState() {
		return new SingleFlowState(id, act.getIdentifier(), finished);
	}

	public void setState(FlowState state) {
		SingleFlowState sfState = (SingleFlowState)state;
		finished = sfState.isFinished();
		id = sfState.getFlowId();
	}

	@Override
	protected SingleFlow search(int id) {
		if (this.id == id)
			return this;
		return null;
	}

	@Override
	protected boolean isFinished() {
		return finished;
	}
}
