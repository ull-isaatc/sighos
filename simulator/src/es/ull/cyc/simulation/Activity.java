package es.ull.cyc.simulation;

import java.util.Vector;

import es.ull.cyc.random.RandomNumber;
import es.ull.cyc.simulation.results.ActivityStatistics;
import es.ull.cyc.util.*;

/**
 * A task which could be carry out by an element. An activity is characterized by its priority,
 * presentiality, and a set of workgropus. Each workgroup represents a combination of resource 
 * types required for carrying out the activity, and the duration of the activity when performed
 * with this workgroup. 
 * @author Carlos Martín Galán
 */
public class Activity extends DescSimulationObject implements Prioritizable {
    /** Priority of the activity */
    protected int priority = 0;
    /** Indicates if the activity is presential (an element carrying out this activity could
     * not make anything else) or not presential (the element could perform other activities at
     * the same time) */
    protected boolean presential = true;
    /** This queue contains the single flows that are waiting for this activity */
    protected Vector<SingleFlow> elementQueue;
    /** Activity manager which this activity is associated to */
    protected ActivityManager manager = null;
    /** Work Group Pool */
    protected PrioritizedTable workGroupTable;

    // constructores
    /**
     * Crea una nueva actividad con una descripción, su prioridad e indicando
     * si es presencial
     * @param modelAct Actividad modelo
     * @param simul Associated simulation
     */
    public Activity(int id, Simulation simul, String description) {
        this(id, simul, description, 0, true);
    }

    /**
     * Creates a new activity.
     * @param id Activity identifier.
     * @param simul Simulation which this activity is attached to.
     * @param description Description of the activity.
     * @param priority Activity priority.
     */
    public Activity(int id, Simulation simul, String description, int priority) {
        this(id, simul, description, priority, true);
    }
    
    /**
     * Creates a new activity.
     * @param id Activity identifier.
     * @param simul Simulation which this activity is attached to.
     * @param description Description of the activity.
     * @param priority Activity priority.
     * @param presential
     */
    public Activity(int id, Simulation simul, String description, int priority, boolean presential) {
        super(id, simul, description);
        this.priority = priority;
        this.presential = presential;
        elementQueue = new Vector<SingleFlow>();
        workGroupTable = new PrioritizedTable();
    }

    /**
     * Permite saber si la actividad requiere de la dedicación exclusiva del
     * elemento que la solicita, o si este puede estar realizando otras 
     * actividades mientras tanto.
     * @return Devuelve si es o no presencial.
     */
    public boolean isPresential() {
        return presential;
    }
    
    /**
     * Getter for property prioridad.
     * @return Value of property prioridad.
     */
    public int getPriority() {
        return priority;
    }
    
    /**
     * Devuelve el gestor de actividades al que está asociada la actividad.
     * @return Gestor de actividades al que está asociada la actividad
     */
    public ActivityManager getManager() {
        return manager;
    }

    /**
     * Setter for property manager.
     * @param manager New value of property manager.
     */
    public void setManager(ActivityManager manager) {
        this.manager = manager;
        manager.add(this);
    }
    
    /**
     * Añade una nueva opción de realización a la actividad
     */
    public WorkGroup getNewWorkGroup(int wgId, RandomNumber duration, int priority, double cost) {
    	WorkGroup wg = new WorkGroup(wgId, this, duration, priority, cost);
        workGroupTable.add(wg);
        return wg;
    }
    
    /**
     * Añade una nueva opción de realización a la actividad
     */
    public WorkGroup getNewWorkGroup(int wgId, RandomNumber duration, int priority) {    	
        return getNewWorkGroup(wgId, duration, priority, 0.0);
    }
    
    /**
     * Añade una nueva opción de realización a la actividad
     */
    public WorkGroup getNewWorkGroup(int wgId, RandomNumber duration) {    	
        return getNewWorkGroup(wgId, duration, 0, 0.0);
    }
    
    public Prioritizable[] getWorkGroupTable() {
        return (Prioritizable[])workGroupTable.toArray();    	
    }

	/**
     * Indica si una actividad tiene disponibles los recursos necesarios
     * @param e Elemento con el que se quiere saber si la actividad puede realizarse.
     * @return Verdadero (true) si tiene los recursos; Falso (false) en otro caso
     */
    protected boolean isFeasible(Element e) {
    	// FIXME Debería ser aleatorio
        PrioritizedTableIterator iter = new PrioritizedTableIterator(workGroupTable);
        WorkGroup opc;
        while ((opc = (WorkGroup)iter.next()) != null) {
            if (opc.isFeasible(e)) {
                e.setCurrentWG(opc);
                return true;
            }            
        }
        return false;
    }

	/**
	 * Indica si una actividad tiene elementos pendientes de ejecución en su 
     * cola. Si el primer elemento de la cola es válido no se hace nada más; si 
     * no, se lleva al primer elemento válido a la cabeza de la cola.
	 * @return Verdadero (true) si tiene elementos; Falso (false) en otro caso
	 */
    protected boolean hasPendingElements() {
        // Si la lista está vacía no hay elementos pendientes (obvio)
        if (elementQueue.isEmpty())
            return false;
        
        // Si el primer elemento de la lista me vale no hago nada más
        SingleFlow flow = elementQueue.get(0);
        Element e = flow.getElement();
        // Sincronización hasta que el elemento deje de ser accedido
        e.waitSemaphore();
        
        // MOD 26/01/06 Añadido
        e.setTs(getTs());

        if (e.getCurrentWG() == null)
            return true;
        else 
            e.signalSemaphore();
        
        // Sigo revisando hasta encontrar el primer elemento válido
        for (int i = 1; i < elementQueue.size(); i++) {
        	flow = elementQueue.get(i);
            e = flow.getElement();
            // Sincronización hasta que el elemento deje de ser accedido
            e.waitSemaphore();

			if (e.getCurrentWG() == null) {
			    // Muevo el elemento al primero de la lista
				flow = elementQueue.remove(i);
			    elementQueue.add(0, flow);
			    return true;
			}
			else 
			    e.signalSemaphore();
        }
        return false;
    }

    /**
     * Add a single flow to the element queue.
     * @param flow Single Flow added
     */
    protected void addElement(SingleFlow flow) {
        elementQueue.add(flow);
    }
    
    /**
     * Remove the first single flow from the element queue.
     * @return The first singler flow of the element queue
     */
    protected SingleFlow removeElement() {
        return elementQueue.remove(0);
    }

    /**
     * Remove a specific single flow from the element queue.
     * @param flow Single flow that must be removed from the element queue.
     * @return True if the flow belongs to the queue; false in other case.
     */
    protected boolean removeElement(SingleFlow flow) {
        return elementQueue.remove(flow);
    }
    
    /**
     * Permite acceder a un elemento en concreto de la cola de elementos pendientes
     * @param ind Indice del elemento
     * @return El elemento de la cola con el índice indicado o null si el índice
     * es inválido
     */
    protected Element getElement(int ind) {
        if (ind < 0 || ind >= elementQueue.size())
            return null;
        return elementQueue.get(ind).getElement();
    }

	/**
	 * El siguiente método sirve para ir vaciando la cola de elementos que 
	 * esperan para realizar la actividad e ir desbloqueándolos. Este método lo 
	 * usa el PL para que los distintos elementos finalicen su ejecución en el 
	 * caso que haya concluido el tiempo de simulacion.
	 */
    protected void clearQueue() {
    	for (SingleFlow sf : elementQueue) {
            simul.addStatistic(new ActivityStatistics(this.id, sf.getId(), sf.getElement().getIdentifier()));
            sf.getElement().notifyEndSimulation();
        }
        elementQueue.clear();
    } 

	public String getObjectTypeIdentifier() {
		return "ACT";
	}

	public double getTs() {
		return manager.getTs();
	}

} // fin Actividad
