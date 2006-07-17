/*
 * ElementoFicticioNP.java
 *
 * Created on 8 de noviembre de 2004, 18:28
 */

package es.ull.isaatc.simulation;

import es.ull.isaatc.simulation.state.ElementState;
import es.ull.isaatc.simulation.state.NPElementState;

/**
 * Element that carries out the non-presential activities instead of the "real"
 * elemnent.
 * @author Iván Castilla Rodríguez
 */
public class NPElement extends Element {
	/** Non-presential elements' counter */
	private static int count = -1;
    /** "Real" element */
    protected Element parent;

    /**
     * Creates a "non-presential" element. 
     * @param parent The presential element
     */
    public NPElement(Element parent) {
        super(count--, parent.getSimul(), parent.getSimul().getNPElementType());
        this.parent = parent;    	
    }
    
    public NPElement(int id, Element parent) {
    	super(id, parent.getSimul(), parent.getSimul().getNPElementType());
    	this.parent = parent;
    }

    @Override
    public void setTs(double ts) {
        super.setTs(ts);
        if (parent.getTs() < ts)
            parent.setTs(ts);
    }
    
    /**
     * Getter for property parent.
     * @return Value of property parent.
     */
    public es.ull.isaatc.simulation.Element getParent() {
        return parent;
    }
    
    @Override
    public String getObjectTypeIdentifier() {
        return "NPE";
    }
    
    /**
	 * @return Returns the count.
	 */
	public static int getCounter() {
		return count;
	}

	/**
	 * @param count The count to set.
	 */
	public static void setCounter(int count) {
		NPElement.count = count;
	}

	@Override
    public String toString() {
    	return new String("(" + parent + ")" + super.toString());
    }

    @Override
    public ElementState getState() {
    	return new NPElementState(super.getState(), parent.getIdentifier(), ((SingleFlow)flow.getParent()).getIdentifier());
    }
    
    @Override
	public void setState(ElementState state) {
    	super.setState(state);
    	flow.setParent(parent.searchSingleFlow(((NPElementState)state).getParentSFId()));
	}

}
