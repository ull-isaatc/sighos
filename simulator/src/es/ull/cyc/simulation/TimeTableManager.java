/**
 * 
 */
package es.ull.cyc.simulation;

import es.ull.cyc.util.CycleIterator;
import es.ull.cyc.util.Output;

/**
 * @author Iván Castilla Rodríguez
 * HISTORY:
 * 12/04/06 Uses cycle iterators
 */
public class TimeTableManager extends BasicElement {
    /** Resource controlled by this entry */
	protected Resource res; 
    /** Managed time table entry */
    protected TimeTableEntry tte;

	/**
	 * @param id
	 * @param res
	 * @param tte
	 */
	public TimeTableManager(int id, Resource res, TimeTableEntry tte) {
		super(id, res.getSimul());
		this.tte = tte;
		this.res = res;
	}

	/* (non-Javadoc)
	 * @see es.ull.cyc.simulation.BasicElement#startEvents()
	 */
    protected void startEvents() {
        if (tte.getRoleList().size() > 1) {
            MultipleRole mr = new MultipleRole(tte.getRoleList(), res);
            for (int i = 0; i < tte.getRoleList().size(); i++) {
                CycleIterator iter = tte.getIterator(defLP.getTs(), simul.getEndTs());
                double nextTs = iter.next();
                if (!Double.isNaN(nextTs)) {
	                MRoleOnEvent rEvent = new MRoleOnEvent(nextTs, mr, i, iter);
	                addEvent(rEvent);
                }
            }
        }
        else {
            ResourceType role = (ResourceType) tte.getRoleList().get(0);
            LogicalProcess lp = role.getManager().getLp();
            // MOD 11/01/06 Hago que el PL por defecto sea éste
            defLP = lp;
            CycleIterator iter = tte.getIterator(defLP.getTs(), simul.getEndTs());
            double nextTs = iter.next();
            if (!Double.isNaN(nextTs)) {
	            RoleOnEvent rEvent = new RoleOnEvent(nextTs, role, iter);
	            addEvent(rEvent);
            }
        }        
    }    

	/* (non-Javadoc)
	 * @see es.ull.cyc.simulation.BasicElement#saveState()
	 */
	protected void saveState() {

	}

	public String getObjectTypeIdentifier() {
		return "TT";
	}
	
    public String toString() {
    	return new String("(" + res + ")" + super.toString());
    }

    /**
     * Makes available a single-role resource 
     */
    public class RoleOnEvent extends Event {
        /** Available role */
        ResourceType role;
        /** Cycle iterator */
        CycleIterator iter;
        
        /**
         * Creates a new event
         * @param ts Timestamp when the resource will  be available.
         * @param role Role played by the resource.
         */        
        RoleOnEvent(double ts, ResourceType role, CycleIterator iter) {
            super(ts, defLP);
            this.iter = iter;
            this.role = role;
        }
        
        /**
         * Pone disponible el elemento y espera el tiempo indicado.
         */        
        public void event() {
            print(Output.MessageType.DEBUG, "Resource available\t" + role);
            role.getManager().addAvailable(role);
            RoleOffEvent rEvent = new RoleOffEvent(ts + tte.getDuration(), role, iter);
            addEvent(rEvent);
        }
    }
    
    /**
     * Makes unavailable a single-role resource 
     */
    public class RoleOffEvent extends Event {
        /** Unavailable role */
        ResourceType role;
        /** Cycle iterator */
        CycleIterator iter;
        
        /**
         * Creates a new event
         * @param ts Timestamp when the resource will be unavailable.
         * @param role Role played by the resource.
         */        
        RoleOffEvent(double ts, ResourceType role, CycleIterator iter) {
            super(ts, defLP);
            this.role = role;
            this.iter = iter;
        }
        
        /**
         * Hace que el rol deje de estar disponible. Si había que repetir pone 
         * las acciones correspondientes en la lista de acciones para continuar 
         * tras la espera en tiempo.
         */
        public void event() {
            role.getManager().removeAvailable(role);
            print(Output.MessageType.DEBUG, "Resource unavailable\t" + role);
            double nextTs = iter.next();
            if (Double.isNaN(nextTs))
                notifyEnd();
            else {
                RoleOnEvent rEvent = new RoleOnEvent(nextTs, role, iter);
                addEvent(rEvent);
            }
        }        
    }
    
    /**
     * Makes available a multiple-role resource 
     */
    public class MRoleOnEvent extends Event {
        MultipleRole mr;
        int index;
        /** Cycle iterator */
        CycleIterator iter;
        
        /**
         * Creates a new event
         * @param ts Timestamp when the resource will  be available.
         * @param role Role played by the resource.
         */        
        MRoleOnEvent(double ts, MultipleRole mr, int ind, CycleIterator iter) {
            super(ts, mr.getResourceType(ind).getManager().getLp());
            this.mr = mr;
            this.index = ind;
            this.iter = iter;
        }
        
        /**
         * Pone disponible el elemento y espera el tiempo indicado.
         */        
        public void event() {
            print(Output.MessageType.DEBUG, "Resource available\t" + mr.getResourceType(index));
            mr.getResourceType(index).getManager().addAvailable(mr, index);
            MRoleOffEvent rEvent = new MRoleOffEvent(ts + tte.getDuration(), mr, index, iter);
            addEvent(rEvent);
        }
    }
    
    /**
     * Makes unavailable a multiple-role resource 
     */
    public class MRoleOffEvent extends Event {
        MultipleRole mr;
        int index;
        /** Cycle iterator */
        CycleIterator iter;
        
        /**
         * Creates a new event
         * @param ts Timestamp when the resource will be unavailable.
         * @param role Role played by the resource.
         */        
        MRoleOffEvent(double ts, MultipleRole mr, int ind, CycleIterator iter) {
            super(ts, mr.getResourceType(ind).getManager().getLp());
            this.mr = mr;
            this.index = ind;
            this.iter = iter;
        }
        
        /**
         * Hace que el conjunto de roles deje de estar disponible. Si había que 
         * repetir pone las acciones correspondientes en la lista de acciones 
         * para continuar tras la espera en tiempo.
         */
        public void event() {
            mr.getResourceType(index).getManager().removeAvailable(mr, index);
            print(Output.MessageType.DEBUG, "Resource unavailable\t" + mr.getResourceType(index));
            double nextTs = iter.next();
            if (Double.isNaN(nextTs))
                notifyEnd();
            else {
                MRoleOnEvent rEvent = new MRoleOnEvent(nextTs, mr, index, iter);
                addEvent(rEvent);
            }
        }        
    }
    
}
