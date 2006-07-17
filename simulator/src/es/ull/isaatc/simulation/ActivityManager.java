/*
 * GestorActividades.java
 *
 * Created on 22 de junio de 2004, 9:34
 */

package es.ull.isaatc.simulation;

import java.util.ArrayList;
import java.util.Iterator;

import es.ull.isaatc.simulation.state.ActivityManagerState;
import es.ull.isaatc.simulation.state.ActivityState;
import es.ull.isaatc.simulation.state.ResourceTypeState;
import es.ull.isaatc.simulation.state.RecoverableState;
import es.ull.isaatc.sync.Semaphore;
import es.ull.isaatc.util.*;

/**
 * Partition of activities. Manages the access to a set of activities.
 * @author Iván Castilla Rodríguez
 */
public class ActivityManager extends SimulationObject implements RecoverableState<ActivityManagerState> {
    /** Static counter for assigning each new id */
	private static int nextid = 0;
	/** A prioritized table of activities */
	protected PrioritizedTable<Activity> activityTable;
    /** A list of resorce types */
    protected ArrayList<ResourceType> resourceTypeList;
    /** Semaphore for mutual exclusion control */
	protected Semaphore sem;
    /** Logical process */
    protected LogicalProcess lp;
    
   /**
	* Creates a new instance of ActivityManager.
    */
    public ActivityManager(Simulation simul) {
        super(nextid++, simul);
        sem = new Semaphore(1);
        resourceTypeList = new ArrayList<ResourceType>();
        activityTable = new PrioritizedTable<Activity>();
        simul.add(this);
    }

    /**
     * Returns the logical process where this Act. manager is included.
	 * @return Returns the lp.
	 */
	public LogicalProcess getLp() {
		return lp;
	}

	/**
	 * Assigns a logical process to this Act. manager. 
	 * @param lp The lp to set.
	 */
	public void setLp(LogicalProcess lp) {
		this.lp = lp;
		lp.add(this);

	}

    public void add(Activity a) {
        activityTable.add(a);
    }
    
    public void add(ResourceType rt) {
        resourceTypeList.add(rt);
    }
    
	/**
     * Sends a "wait" signal to the semaphore.
     */    
    protected void waitSemaphore() {
        sem.waitSemaphore();
    }
    
    /**
     * Sends a "continue" signal to the semaphore.
     */    
    protected void signalSemaphore() {
        sem.signalSemaphore();
    }
        
    /**
     * Informs the activities of new available resources. 
     */
    protected void availableResource() {
        waitSemaphore();
        Iterator<Activity> iter = activityTable.iterator(true);
        while (iter.hasNext()) {
        	Activity act = iter.next();
            act.print(Output.MessageType.DEBUG, "Testing pool activity (availableResource)");
            if (act.hasPendingElements()) {
                if (act.isFeasible(act.getElement(0))) {
                	SingleFlow flow = act.removeElement(); 
                    Element e = flow.getElement();

                    e.print(Output.MessageType.DEBUG, "Can carry out (available resource)\t" + act, 
                    		"Can carry out (available resource)\t" + act + "\t" + act.getDescription());
                    
                    // Fin Sincronización hasta que el elemento deje de ser accedido
                    // MOD 26/01/06 Movida esta línea antes del e.sig...
                    // MOD 23/05/06 Vuelta a poner aquí: ¿POR QUÉ LA MOVI?
                    e.signalSemaphore();
                    e.carryOutActivity(flow);
                }
                else
                    act.getElement(0).signalSemaphore();
            }
        }
        signalSemaphore();
    } 

    /**
     * Informs an activity about an available element in its queue.
     * @param flow Single flow that contains the activity and the available element
     */
    protected void availableElement(SingleFlow flow) {
        waitSemaphore();

		Element e = flow.getElement();
		Activity act = flow.getActivity();
        // MOD 22/11/05 Quitado
        // MOD 9/01/06 Puesto otra vez
        e.waitSemaphore();
        e.print(Output.MessageType.DEBUG, "Calling availableElement()\t" + act, 
        		"Calling availableElement()\t" + act + "\t" + act.getDescription());
        // MOD 9/01/06 Y esto añadido
        if (e.getCurrentWG() == null) {
            if (act.isFeasible(e)) {
            	e.print(Output.MessageType.DEBUG, "Can carry out\t" + act, "Can carry out\t" + act + "\t" + e.getCurrentWG());
                if (!act.removeElement(flow)) { // saco el elemento de la cola
                	e.print(Output.MessageType.ERROR, "Unexpected error. Element MUST BE in the activity queue",
                			"Unexpected error. Element MUST BE in the activity queue\t" + act);
                }
            // MOD 22/11/05 Quitado
            // MOD 9/01/06 Puesto otra vez
                e.signalSemaphore();
                
                e.carryOutActivity(flow);
            }
            // MOD 22/11/05 Quitado
            // MOD 9/01/06 Puesto otra vez
            else
                e.signalSemaphore();
        }
        else 
            e.signalSemaphore();
        signalSemaphore();
    } 

    /**
     * An element requests an activity. Checks if the activity is feasible and the
     * element is not performing another activity. 
     * @param flow Requested single flow
     */
    protected void requestActivity(SingleFlow flow) {
		waitSemaphore();

		Element e = flow.getElement();
		Activity act = flow.getActivity();
        e.waitSemaphore();
        if (e.getCurrentWG() == null) {
            if (act.isFeasible(e)) { // hay recursos para hacer la actividad
                e.signalSemaphore();
                e.carryOutActivity(flow);
            }
            else {// en estos momentos no hay recursos necesarios
                act.addElement(flow); // meto el elemento en la cola de pendientes
                e.signalSemaphore();
            }
        }
        else {
            act.addElement(flow); // meto el elemento en la cola de pendientes
            e.signalSemaphore();
        }
    	e.incRequested(flow);
        signalSemaphore();
    }

	/**
	 * El elemento, tras finalizar la actividad, devuelve los recursos que empleó.
	 * No se comprueba si ellos los tenían o no. Se deja a cargo del usuario de 
	 * la libreria
	 * @param e Elemento que quiere finalizar la actividad
	 */
    protected void finalizeActivity(Element e) {
        waitSemaphore();
        ArrayList<ActivityManager> amList = e.getCurrentWG().releaseResources(e); 
        e.setCurrentWG(null);
        signalSemaphore();
        for (ActivityManager am : amList) 
        	am.availableResource();
    }
 
    /**
     * Método mediante el cual se pone disponible un Recurso Activo que 
     * desempeña un rol en un momento determinado
     * Además avisa al gestor de actividades de este cambio para que compruebe
     * si algún elemento puede comenzar su ejecución
     * @param mr Entrada que contiene el recurso y los roles quedesempeña simultáneamente
     */
    protected void addAvailable(Resource res, ResourceType role) {
        waitSemaphore();
        role.incAvailable(res);
        signalSemaphore();
        availableResource(); // Se informa al gestor de actividades de que los recursos han quedado libres
    }
    
    /**
     * Método mediante el cual deja de estar disponible un Recurso Activo que 
     * desempeñaba como rol una Clase de Recurso concreta.
     * @param mr Entrada que contiene el recurso y los roles quedesempeña simultáneamente
     */
    protected void removeAvailable(Resource res, ResourceType role) {
        waitSemaphore();
        role.decAvailable(res);
        signalSemaphore();        
    }
    
	public String getObjectTypeIdentifier() {
		return "AM";
	}

	public double getTs() {
		return lp.getTs();
	}

	public String getDescription() {
        StringBuffer str = new StringBuffer();
        str.append("Activity Manager " + id + "\r\n(Activity[priority]):");
        Prioritizable actividades[] = activityTable.toArray();
        for (int i = 0; i < actividades.length; i++) {
            Activity a = (Activity)actividades[i];
            str.append("\t\"" + a + "\"[" + a.getPriority() + "]");
        }
        str.append("\r\nResource Types: ");
        for (ResourceType rt : resourceTypeList)
            str.append("\t\"" + rt + "\"");
        return str.toString();
	}

	public ActivityManagerState getState() {
		ActivityManagerState amState = new ActivityManagerState(id);
        Iterator<Activity> iter = activityTable.iterator(false);
        while (iter.hasNext())
        	amState.add(iter.next().getState());
        for (ResourceType rt : resourceTypeList)
        	amState.add(rt.getState());
		return amState;
	}

	public void setState(ActivityManagerState state) {
		for (ActivityState aState : state.getAStates()) {
			Activity act = simul.getActivity(aState.getActId());
			act.setManager(this);
			act.setState(aState);			
		}
		for (ResourceTypeState rtState : state.getRtStates()) {
			ResourceType rt = simul.getResourceType(rtState.getRtId());
			rt.setManager(this);
			rt.setState(rtState);
		}
	}

}
