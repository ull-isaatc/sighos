/*
 * GestorActividades.java
 *
 * Created on 22 de junio de 2004, 9:34
 */

package es.ull.cyc.simulation;

import java.util.ArrayList;
import java.util.Iterator;
import es.ull.cyc.sync.Semaphore;
import es.ull.cyc.util.*;

/**
 * Partition of activities. Manages the access to a set of activities.
 * @author Iv�n Castilla Rodr�guez
 */
public class ActivityManager extends SimulationObject {
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
     * M�todo que informa a las actividades de que varias unidades de recursos
	 * han quedado disponibles.
	 * No realiza ningun control. Ni si el recurso pertenece a la tabla, ni si
	 * pone mas unidades o quita de las que realmente tiene.
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
                    
                    // Fin Sincronizaci�n hasta que el elemento deje de ser accedido
                    // MOD 26/01/06 Movida esta l�nea antes del e.sig...
                    // MOD 23/05/06 Vuelta a poner aqu�: �POR QU� LA MOVI?
                    e.signalSemaphore();
                    e.carryOutActivity(flow);
                }
                else
                    act.getElement(0).signalSemaphore();
            }
        }
        signalSemaphore();
    } // fin de RecursoDisponible

    /**
     * M�todo que informa de que un elemento que estaba realizando otra
     * actividad ha quedado disponible para realizar una actividad concreta.
     * @param act Actividad que puede realizar el elemento
     * @param e Elemento que ha quedado disponible.
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
        // MOD 9/01/06 Y esto a�adido
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
    } // fin de elementoDisponible

    /**
     * Funci�n para solicitar una actividad. Se comprueba que el elemento que 
     * la solicita no est� realizando otra actividad y que se disponga de los 
     * recursos necesarios para realizarla. Si no es as�, el elemento se pone a
     * la cola de esa actividad.
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
	 * El elemento, tras finalizar la actividad, devuelve los recursos que emple�.
	 * No se comprueba si ellos los ten�an o no. Se deja a cargo del usuario de 
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
     * M�todo mediante el cual se pone disponible un Recurso Activo que 
     * desempe�a un rol en un momento determinado
     * Adem�s avisa al gestor de actividades de este cambio para que compruebe
     * si alg�n elemento puede comenzar su ejecuci�n
     * @param mr Entrada que contiene el recurso y los roles quedesempe�a simult�neamente
     */
    protected void addAvailable(Resource res, ResourceType role) {
        waitSemaphore();
        role.incAvailable(res);
        signalSemaphore();
        availableResource(); // Se informa al gestor de actividades de que los recursos han quedado libres
    }
    
    /**
     * M�todo mediante el cual deja de estar disponible un Recurso Activo que 
     * desempe�aba como rol una Clase de Recurso concreta.
     * @param mr Entrada que contiene el recurso y los roles quedesempe�a simult�neamente
     */
    protected void removeAvailable(Resource res, ResourceType role) {
        waitSemaphore();
        role.decAvailable(res);
        signalSemaphore();        
    }
    
    /**
     * Frees all the activity queues
     */
    public void clearActivityQueues() {
        Iterator<Activity> iter = activityTable.iterator(false);
        while (iter.hasNext())
        	iter.next().clearQueue();
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
}
