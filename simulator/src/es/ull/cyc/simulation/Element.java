package es.ull.cyc.simulation;

import java.util.*;

import es.ull.cyc.simulation.results.ElementStatistics;
import es.ull.cyc.sync.Semaphore;
import es.ull.cyc.util.*;

/**
 * Represents elements that make use of activitiy flows in order to carry out 
 * their events.
 * @author Iván Castilla Rodríguez
 */
public class Element extends BasicElement {
    /** Workgroup that is currently being used. A null value indicates that this 
     element is not currently performing any activity. */
    protected WorkGroup currentWG = null;
    /** SingleFlow that is currently being used. A null value indicates that this 
     element is not currently performing any activity.
     NOTA: Me hace falta este atributo para algo? */
    protected SingleFlow currentSF = null;  
    /** Activity flow of the element */
    protected Flow flow = null;
    /** Stores the requested presential activities (requested[0]) and non-presential 
     ones (requested[1]) */
    protected ArrayList<SingleFlow> []requested = new ArrayList[2];
    /** Amount of pending presential activities (pending[0]) and non-presential 
     ones (pending[1]) */
    protected int []pending;
    /** List of caught resources */
    protected ArrayList<Resource> caughtResources;
    // Avoiding deadlocks (time-overlapped resources)
    /** List of conflictive elements */
    protected ConflictZone conflicts;
    /** Stack of nested semaphores */
	protected ArrayList<Semaphore> semStack;

    /**
     * Constructor del elemento
     * @param simul Simulation object
     * @param id Identificador del elemento
     */
	public Element(int id, Simulation simul) {
        super(id, simul);
        requested[0] = new ArrayList<SingleFlow>();
        requested[1] = new ArrayList<SingleFlow>();
        caughtResources = new ArrayList<Resource>();
	}

    /**
     * Devuelve el equipo de trabajo que está siendo usado por el elemento para
     * realizar una actividad o null si no es ninguna actividad.
     * @return Valor de equipoActual.
     */
    protected WorkGroup getCurrentWG() {
        return currentWG;
    }
    
    /**
     * Establece el valor del equipo de trabajo de la actividad actual que está
     * utilizando el elemento.
     * @param currentWG Equipo de trabajo actual del elemento. Si se pone a null quiere decir
     * que el elemento no está realizando ninguna actividad.
     */
    protected void setCurrentWG(WorkGroup currentWG) {
        this.currentWG = currentWG;
    }

    /**
	 * @return Returns the caughtResources.
	 */
	protected ArrayList<Resource> getCaughtResources() {
		return caughtResources;
	}

	/**
	 * @param res
	 */
	protected void addCaughtResource(Resource res) {
		caughtResources.add(res);
	}
	
	protected void resetConflictList() {
        conflicts = new ConflictZone(this);
	}
	
	/**
	 * @param list
	 */
	protected void setConflictList(ConflictZone list) {
		conflicts = list;
	}
	
	protected boolean removeConflictList() {
		return conflicts.remove(this);
	}
	/**
	 * @return
	 */
	protected ConflictZone getConflictList() {
		return conflicts;
	}
	
	protected void mergeConflictList(Element e) {
		// If it's the same list there's no need of merge
		if (conflicts != e.getConflictList()) {
			int result = this.compareTo(e); 
			if (result < 0)
				conflicts.merge(e.getConflictList());
			else if (result > 0)
				e.getConflictList().merge(conflicts);
		}
	}
	
	protected void waitConflictSemaphore() {
		semStack = conflicts.getSemaphores();
		for (Semaphore sem : semStack)
			sem.waitSemaphore();
	}
	
	protected void signalConflictSemaphore() {
		for (Semaphore sem : semStack)
			sem.signalSemaphore();
	}
	
	/**
     * Returns the single flow which is being currently executed. 
     * @return Single flow attached to the current activity, or null if there is
     * no current activity.
	 */
	public SingleFlow getCurrentSF() {
		return currentSF;
	}

	/**
	 * @param currentSF The currentSF to set.
	 */
	public void setCurrentSF(SingleFlow currentSF) {
		this.currentSF = currentSF;
	}

	/**
     * Devuelve el flujo de ejecución asociado a este elemento.
     * @return Flujo de ejecución del elemento.
     */
    public es.ull.cyc.simulation.Flow getFlow() {
        return flow;
    }
    
    /**
     * Sets an activity flow.
     * @param flow New value of property flow.
     */
    public void setFlow(es.ull.cyc.simulation.Flow flow) {
        this.flow = flow;
        if (flow != null)
        	pending = flow.countActivities();
    }
    
    /**
     * The element starts requesting its activities.
     */
    protected void startEvents() {
    	simul.addStatistic(new ElementStatistics(id, ElementStatistics.START, ts, 0));
        if (flow != null)
        	flow.request();
        else
        	notifyEnd();
    }
    
    public void saveState() {
    	flow.saveState();
    }
    
    /**
     * Devuelve las actividades solicitadas por el elemento. El primer valor del 
     * array representa las actividades presenciales, y el segundo las no
     * presenciales.
     * @return Lista de actividades solicitadas.
     */
    protected ArrayList<SingleFlow> []getRequested() {
        return requested;
    }
    
    /**
     * Devuelve el número de actividades presenciales solicitadas por el 
     * elemento. 
     * @return Número de actividades presenciales solicitadas.
     */
    protected int getRequestedP() {
        return requested[0].size();
    }
    
    /**
     * Devuelve el número de actividades presenciales pendientes
     * @return Número de actividades presenciales pendientes.
     */
    protected synchronized int getPendingP() {
        return pending[0];
    }
    
    /**
     * Devuelve el número de actividades no presenciales solicitadas por el 
     * elemento. 
     * @return Número de actividades no presenciales solicitadas.
     */
    protected int getRequestedNP() {
        return requested[1].size();
    }
    
    /**
     * Devuelve el número de actividades no presenciales pendientes
     * @return Número de actividades no presenciales pendientes.
     */
    protected synchronized int getPendingNP() {
        return pending[1];
    }
    
    /**
     * Adds a new activity (single flow) to the requested list.
     * @param f Single flow added to the requested list.
     */
    protected synchronized void incRequested(SingleFlow f) {
        if (f.isPresential())
            requested[0].add(f);
        else
            requested[1].add(f);
    }
    
    /**
     * Removes an activity (single flow) from the requested (and the pending) 
     * list. If there are no more pending activities, the element produces a 
     * finalize event and finish its execution.
     * @param f Single flow removed from the requested list.
     */
    protected synchronized void decRequested(SingleFlow f) {
        if (f.isPresential()) {
            requested[0].remove(f);
            pending[0]--;
        }
        else {
            requested[1].remove(f);
            pending[1]--;
        }
        if ((pending[1] + pending[0]) == 0) 
            notifyEnd();
    }

    /**
     * Produces a "RequestActivityEvent".
     * @param f Single flow requested.
     */
    protected void requestActivity(SingleFlow f) {
        RequestActivityEvent e = new RequestActivityEvent(ts, f);
        addEvent(e);
    }

    /**
     * Updates the element timestamp, catch the corresponding resources and produces 
     * a finalize activity event.
     * @param lp Logical Process where the activity will be carried out.
     */
    protected void carryOutActivity(SingleFlow f) {
    	LogicalProcess lp = f.getActivity().getManager().getLp();
        setTs(lp.getTs());
        currentWG.catchResources(this);
    	simul.addStatistic(new ElementStatistics(id, ElementStatistics.STAACT, ts, f.getActivity().getIdentifier()));
        print(Output.MessageType.DEBUG, "Starts\t" + f.getActivity(), 
        		"Starts\t" + f.getActivity() + "\t" + f.getActivity().getDescription());
        // MOD 25/01/06 Puesto aquí      
        currentSF = f;
        FinalizeActivityEvent e = new FinalizeActivityEvent(ts + currentWG.getDuration(), f);
        addEvent(e);
    }
    
	public String getObjectTypeIdentifier() {
		return "E";
	}
	
    /**
     * Acción que solicita una actividad.
     */
    public class RequestActivityEvent extends BasicElement.Event {
        SingleFlow flow;
        /**
         * Constructor de la acción.
         */
        public RequestActivityEvent(double ts, SingleFlow flow) {
            super(ts, flow.getActivity().getManager().getLp());
            this.flow = flow;
        }
        
        /**
         * Solicita una actividad y comprueba la siguiente acción a realizar.
         */
        public void event() {
        	simul.addStatistic(new ElementStatistics(id, ElementStatistics.REQACT, ts, flow.getActivity().getIdentifier()));
            print(Output.MessageType.DEBUG, "Requests\t" + flow.getActivity(), 
            		"Requests\t" + flow.getActivity() + "\t" + flow.getActivity().getDescription());
            flow.getActivity().getManager().requestActivity(flow);
        }
    }
    
    public class AvailableElementEvent extends BasicElement.Event {
        SingleFlow flow;
        
        public AvailableElementEvent(double ts, SingleFlow flow) {
            super(ts, flow.getActivity().getManager().getLp());
            this.flow = flow;            
        }
        
        public void event() {
            flow.getActivity().getManager().availableElement(flow);
        }        
    }
    
    /**
     * Acción que se finaliza una actividad.
     */
    public class FinalizeActivityEvent extends BasicElement.Event {
        SingleFlow flow;
        
        /**
         * Constructor de la acción.
         */        
        public FinalizeActivityEvent(double ts, SingleFlow flow) {
            super(ts, flow.getActivity().getManager().getLp());
            this.flow = flow;
        }
        
        /**
         * Termina una actividad y comprueba la siguiente acción a realizar.
         */
        public void event() {
        	simul.addStatistic(new ElementStatistics(id, ElementStatistics.ENDACT, ts, flow.getActivity().getIdentifier()));
            print(Output.MessageType.DEBUG, "Finishes\t" + flow.getActivity(), 
            		"Finishes\t" + flow.getActivity() + "\t" + flow.getActivity().getDescription());
            flow.getActivity().getManager().finalizeActivity(Element.this);
            flow.finish();
            // Checks if there are pending activities that haven't noticed the element availability
            // MOD 9/01/06 REVISAR
            for (int i = 0; (currentWG == null) && (i < requested[0].size()); i++) {
                AvailableElementEvent e = new AvailableElementEvent(ts, requested[0].get(i));
                addEvent(e);
            }
        }
    }
    
}
