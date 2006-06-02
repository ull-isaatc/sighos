package es.ull.cyc.simulation;

import java.util.ArrayList;

import es.ull.cyc.util.Cycle;
import es.ull.cyc.util.CycleIterator;
import es.ull.cyc.util.OrderedList;
import es.ull.cyc.util.Output;


/**
 * Un recurso activo puede desempe�ar diferentes roles, o lo que es lo mismo,
 * tener varias ClaseRecurso.
 * Por este motivo ya no hay un atributo. Si se desea saber que roles desempe�a
 * es necesario recorrer su tabla horaria.
 * HISTORY
 * 22/05/06 No more Multiple Roles
 * 23/05/06 Resource is now a BasicElement. No TimeTableManagers required. �Para qu� quiero un TTM por
 * entrada horaria, si un elemento puede lanzar m�s de un evento por instante de tiempo de simulaci�n?
 * @author Carlos Mart�n Gal�n
 */
public class Resource extends BasicElement {
	/** Timetable that contains all the pairs role - time-cycle */
    protected ArrayList<TimeTableEntry> timeTable;
    /** A brief description of the resource */
    protected String description;
    /** Element which has got this resource */
    protected Element currentElement = null;
    /** The resource type which this resource is being booked for */
    protected ResourceType currentResourceType = null;
    /** If true, indicates that this resource is being used after its availability time has expired */
    protected boolean timeOut = false;
    /** List of currently active roles */
    protected ArrayList<ResourceType> currentRoles;
    /** List of elements trying to book this resource */
    protected OrderedList<Element> bookList;

    /**
     * Creates a new instance of Resource
     * @param resModel Correspondign model resource
     * @param simul Associated simulation
     */
	public Resource(int id, Simulation simul, String description) {
		super(id, simul);
		this.description = description;
        timeTable = new ArrayList<TimeTableEntry>();
        currentRoles = new ArrayList<ResourceType>();
        bookList = new OrderedList<Element>();
	}

    /**
     * Arranca todos los timeManageres de actividad del recurso
     * Supone ya que se han introducido ya todas las entradas de su horario
     */
    protected void startEvents() {
		for (int i = 0 ; i < timeTable.size(); i++) {
			TimeTableEntry tte = timeTable.get(i);
	        CycleIterator iter = tte.getIterator(tte.getRole().getTs(), simul.getEndTs());
	        double nextTs = iter.next();
	        if (!Double.isNaN(nextTs)) {
	            RoleOnEvent rEvent = new RoleOnEvent(nextTs, tte.getRole(), iter, tte.getDuration());
	            addEvent(rEvent);
	        }
		}
	}

    /**
     * Add a new entry with a single role.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle
     * @param role Role that the resource plays during this cycle
     */
    public void addTimeTableEntry(Cycle cycle, double dur, ResourceType role) {
        timeTable.add(new TimeTableEntry(cycle, dur, role));
    }  

    /**
     * Add a new entry with a several roles.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle
     * @param roleList Roles that the resource play during this cycle
     */
    public void addTimeTableEntry(Cycle cycle, double dur, ArrayList<ResourceType> roleList) {
    	for (int i = 0; i < roleList.size(); i++)
            addTimeTableEntry(cycle, dur, roleList.get(i));
    }  
    
    /**
     * Returns the amount of time table entries.
     * @return Size of the time table.
     */
    public int getTimeTableSize() {
        return timeTable.size();
    }
    
    /**
     * Returns the TimeTableEntry object at the specified position of the time 
     * table.
     * @param index of object to return.
     * @return The TimeTableEntry object at the specified position.
     */
    public TimeTableEntry getTimeTableEntry(int index) {
        return timeTable.get(index);
    }
    
	public String getObjectTypeIdentifier() {
		return "RES";
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#add(E)
	 */
	protected boolean addRole(ResourceType role) {
		return currentRoles.add(role);
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#remove(java.lang.Object)
	 */
	protected boolean removeRole(ResourceType role) {
		return currentRoles.remove(role);
	}

	/**
	 * @return Returns the currentManagers.
	 */
	public ArrayList<ActivityManager> getCurrentManagers() {
		ArrayList <ActivityManager> currentManagers = new ArrayList<ActivityManager>();
		// FIXME: �No incluir repetidos?
		for (int i = 0; i < currentRoles.size(); i++)
			currentManagers.add(currentRoles.get(i).getManager());
		return currentManagers;
	}

	@Override
	protected void saveState() {
	}
    
	/**
	 * An element books this resource. The element is simply included in the book list
	 * of this resource.
	 * @param e The element booking this resource
	 * @return False if the element has already booked this resource (in the same activity).
	 * True in other case. 
	 */
	protected boolean addBook(Element e) {
		waitSemaphore();
		// First I complete the conflicts list
		if (bookList.size() > 0)
			e.mergeConflictList(bookList.get(0));
		boolean result = bookList.add(e);
		signalSemaphore();
		return result;
	}
	
	protected boolean removeBook(Element e) {
		waitSemaphore();
		boolean result = bookList.remove(e); 
		signalSemaphore();
		return result;
	}
	
	// El sincronismo habr� que hacerlo por fuera
	protected OrderedList<Element> getBookList() {
		return bookList;
	}
	
	protected void catchResource(Element e, ResourceType rt) {
		waitSemaphore();
		currentElement = e;
		e.addCaughtResource(this);
		currentResourceType = rt;
		bookList.clear();
		signalSemaphore();
	}
	
    /**
     * Releases this resource. If the resource has already expired its availability time, 
     * the flag is set off.
     * @return True if the resource could be correctly released. False if the availability
     * time of the resource had already expired.
     */
    protected boolean releaseResource() {
		waitSemaphore();
        currentElement = null;
        currentResourceType = null;        
        if (timeOut) {
        	timeOut = false;
    		signalSemaphore();
        	return false;
        }
		signalSemaphore();
        return true;
    }
    
    /**
     * Getter for property currentElement.
     * @return Value of property currentElement.
     */
    protected BasicElement getCurrentElement() {
        return currentElement;
    }
    
    /**
     * Setter for property currentElement.
     * @param e New value of property currentElement.
     */
    protected void setCurrentElement(Element e) {
        this.currentElement = e;
    }
    
    /**
     * Getter for property currentResourceType.
     * @return Value of property currentResourceType.
     */
    protected ResourceType getCurrentResourceType() {
        return currentResourceType;
    }
    
    /**
     * Setter for property currentResourceType.
     * @param cr New value of property currentResourceType.
     */
    protected void setCurrentResourceType(ResourceType rt) {
        this.currentResourceType = rt;
    }
    
    /**
     * Getter for property timeOut.
     * @return Value of property timeOut.
     */
    protected boolean isTimeOut() {
        return timeOut;
    }
    
    /**
     * Setter for property timeOut.
     * @param fueraTiempo New value of property timeOut.
     */
    protected void setTimeOut(boolean timeOut) {
        this.timeOut = timeOut;
    }
    
    /**
     * Makes available a single-role resource 
     */
    public class RoleOnEvent extends BasicElement.Event {
        /** Available role */
        ResourceType role;
        /** Cycle iterator */
        CycleIterator iter;
        /** Duration */
        double duration;
        
        /**
         * Creates a new event
         * @param ts Timestamp when the resource will  be available.
         * @param role Role played by the resource.
         */        
        RoleOnEvent(double ts, ResourceType role, CycleIterator iter, double duration) {
            super(ts, role.getManager().getLp());
            this.iter = iter;
            this.role = role;
            this.duration = duration;
        }
        
        /**
         * Pone disponible el elemento y espera el tiempo indicado.
         */        
        public void event() {
            print(Output.MessageType.DEBUG, "Resource available\t" + role);
            role.getManager().addAvailable(Resource.this, role);
            // MOD 22/05/06
            addRole(role);
            RoleOffEvent rEvent = new RoleOffEvent(ts + duration, role, iter, duration);
            addEvent(rEvent);
        }
    }
    
    /**
     * Makes unavailable a single-role resource 
     */
    public class RoleOffEvent extends BasicElement.Event {
        /** Unavailable role */
        ResourceType role;
        /** Cycle iterator */
        CycleIterator iter;
        /** Duration */
        double duration;
        
        /**
         * Creates a new event
         * @param ts Timestamp when the resource will be unavailable.
         * @param role Role played by the resource.
         */        
        RoleOffEvent(double ts, ResourceType role, CycleIterator iter, double duration) {
            super(ts, role.getManager().getLp());
            this.role = role;
            this.iter = iter;
            this.duration = duration;
        }
        
        /**
         * Hace que el rol deje de estar disponible. Si hab�a que repetir pone 
         * las acciones correspondientes en la lista de acciones para continuar 
         * tras la espera en tiempo.
         */
        public void event() {
            role.getManager().removeAvailable(Resource.this, role);
            // MOD 22/05/06
            removeRole(role);
            print(Output.MessageType.DEBUG, "Resource unavailable\t" + role);
            double nextTs = iter.next();
            if (Double.isNaN(nextTs))
                notifyEnd();
            else {
                RoleOnEvent rEvent = new RoleOnEvent(nextTs, role, iter, duration);
                addEvent(rEvent);
            }
        }        
    }

} // fin Resource
