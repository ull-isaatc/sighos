package es.ull.cyc.simulation;

import java.util.*;

import mjr.heap.HeapAscending;
import es.ull.cyc.sync.*;
import es.ull.cyc.util.*;

/** 
 * Clase para representar los procesos lógicos de eventos discretos. Un proceso 
 * lógico puede contener actividades, clases de recursos y recursos activos. 
 * Cuando contiene actividades se subdivide internamente en gestores de 
 * actividades.
 * @author Carlos Martín Galán
 */
public class LogicalProcess extends SimulationObject implements Runnable {
	/** Logical Process' counter */
	private static int nextId = 0;
    /** Mecanismo de sincronismo con los elementos */
	protected Lock lpLock;
    /** Tiempo global del sistema */
	protected double lvt;
    /** Tiempo máximo que durará la simulación para este PL */
    protected double maxgvt; 
    /** Mecanismo de control de los elementos en ejecución */
    protected ExecutionQueue execQueue;
	/** Heap para implementar la cola en espera */
	protected HeapAscending waitQueue;
    /** Lista de Gestores de Actividades del proceso lógico */
    protected ArrayList<ActivityManager> managerList;
    /** Thread where the logical process function is implemented */
    private Thread lpThread = null;

	/**
     * Constructor del ProcesoLogico
     */
	public LogicalProcess(Simulation simul) {
        this(simul, 0.0, 0.0);
	}

	/**
     * Constructor del ProcesoLogico en el que se anade el tiempo de simulacion 
     * final
     * @param t Tiempo de simulación final
     */
	public LogicalProcess(Simulation simul, double t) {
        this(simul, 0.0, t);
	}

	/**
     * Constructor del ProcesoLogico en el que se anade el tiempo de simulacion 
     * en el que parte y el tiempo de simulación final
     * @param startT Tiempo de simulación de partida
     * @param endT Tiempo de simulación final
     */
	public LogicalProcess(Simulation simul, double startT, double endT) {
		super(nextId++, simul);
        execQueue = new ExecutionQueue(this);
        waitQueue = new HeapAscending();
        lpLock = new Lock();
        maxgvt = endT;
        lvt = startT;
        managerList = new ArrayList<ActivityManager>();
	}
    
    /**
     * Método para establecer el maximo tiempo de simulacion a ejecutar
     * por el proceso logico
     * @param t Máximo tiempo de simulación
     */
    public void setSimulationEnd(double t) {
        maxgvt = t;
    }

    /**
     * Indica si es el final del tiempo de simulación
     * @return Verdadero si ya se acabo el tiempo de simulación
     */
    public boolean isSimulationEnd() {
        return(lvt >= maxgvt);
    }

    /** 
     * Gets the associated simulation object
     * @return Associated simulation object
     */
    public Simulation getSimul() {
        return simul;
    }
    
	public double getTs() {
		return lvt;
	}

    /**
     * Sets the local time of this Logical Process
     * @param newt New local time.
     */
	protected void setLVT(double newt) {
		lvt = newt;
	}

	// procedimientos que controlan la ejecucion del pl
    /**
     * Envía un elemento a la cola de ejecución
     * @param e Elemento a insertar
     * @return true si todo fue correcto
     */
	public synchronized boolean addExecution(BasicElement.Event e) {
		return execQueue.addEvent(e);
	}

    /**
     * Quita un elemento de la cola de ejecución
     * @param e Elemento a quitar
     * @return true si todo fue correcto
     */
    protected synchronized boolean removeExecution(BasicElement.Event e) {
        return execQueue.removeEvent(e);
    }
    
    /**
     * Quita un elemento de la cola de ejecución realizando una sincronización
     * previa. La sincronización consiste en esperar a que el PL se bloquee o
     * que haya expirado el tiempo de simulación
     * @param e Elemento a quitar
     * @return true si todo fue correcto
     */
    protected boolean removeExecutionSync(BasicElement.Event e) {
        while(!isSimulationEnd() && !locked());
        return removeExecution(e);
    }
    
    /**
     * Envía un elemento a la cola de espera
     * @param e Elemento a insertar
     */
	public synchronized void addWait(BasicElement.Event e) {
		waitQueue.insert(e);
	}

    /**
     * Quita un elemento a la cola de espera
     * @return El primer elemento del Heap
     */
    protected synchronized BasicElement.Event removeWait() {
        return (BasicElement.Event) waitQueue.extractMin();
    }

    /**
     * Comprueba si un elemento pertenece a la cola de espera
     * @param e Elemento a comprobar
     * @return true si el elemento pertenece a la cola
     */
    protected synchronized boolean inWait(BasicElement.Event e) {
        return waitQueue.contains(e);
    }
    
    // Funciones para el control del bloqueo del Proceso Lógico
    /**
     * Bloquea el proceso lógico
     * @throws InterruptedException Cuando ocurió algún error al bloquear.
     */
    protected void lock() throws InterruptedException {
        lpLock.lock();
    }
    
    /**
     * Desbloquea el proceso lógico
     */
    protected void unlock() {
        lpLock.unlock();
    }
    
    /**
     * Comprueba si el proceso lógico está bloqueado
     * @return Verdadero (true) si el proceso lógico está bloqueado, falso 
     * (false) en otro caso.
     */
    protected boolean locked() {
        return lpLock.locked();
    }

    /**
     * Adds an activity manager to the manager list
     * @param am New Activity manager.
     */
    protected void add(ActivityManager am) {
        managerList.add(am);
    }
    
    /**
     * Saca todos los elementos de la cola de espera que deben ejecutarse en un 
     * instante de simulación.
     */
    private void execWaitingElements() {
        // saca el primer elemento de la lista
        if (! waitQueue.isEmpty()) {
            BasicElement.Event e = removeWait();
            setLVT(e.getTs());
            double tiempo = e.getTs();
            // MOD 4/07/05 No se deben ejecutar de forma normal los elementos de ts >= maxGVT
            if (tiempo >= maxgvt)
                addWait(e);
            else {
                // MOD 11/01/06 Se actualiza el reloj del elemento => Quitado
                //e.getElement().setTs(tiempo);
                addExecution(e);
                // saca de la cola todos los que estan en este tiempo de simulacion
                boolean flag = false;
                do {
                    if (! waitQueue.isEmpty()) {
                        e = removeWait();
                        if ( e.getTs() == tiempo ) {
                            // ponlo a ejecutar
                            addExecution(e);
                            flag = true;
                        }
                        else {  // sacó uno, pero ya tiene un tiempo posterior
                            flag = false;
                            addWait(e);
                        }
                    }
                    else {  // se vació la cola, y por tanto hay que salir del while
                        flag = false;
                    }
                } while ( flag );
            }
        } // del if ! en_espera        
    }
    
    /**
     * Controla la ejecución del proceso lógico
     */
	private void lpExecution() {

		while (!isSimulationEnd() && (simul.getElements() > 0)) {
			// Espera hasta que no haya nadie ejecutandose
			try {				
                lock();
            } catch (InterruptedException ex) {
            	ex.printStackTrace();
           }
            // en este momento se supone que no hay nadie en ejecucion, lo que implica
			// que no habrá nadie poniendolo en espera
            execWaitingElements();

		} // fin del while principal
        
        if (isSimulationEnd()) { // se acabo el tiempo de simulacion
        	print(Output.MessageType.DEBUG, "SIMULATION TIME FINISHES",
        			"SIMULATION TIME FINISHES\r\nSimulation time = " +
                	lvt + "\r\nPreviewed simulation time = " + maxgvt);
        	printState();
        }
        else {
        	print(Output.MessageType.DEBUG, "ALL ELEMENTS FINISHED",
        			"ALL ELEMENTS FINISHED\r\nSimulation time = " +
                	lvt + "\r\nPreviewed simulation time = " + maxgvt);
        	printState();
            // Actualizamos el tiempo de simulación para que termine la simulación
            lvt = maxgvt;
        }
        finish();
	} // fin EjecucionPL

    /**
     * Libera todos los elementos que aún están en alguna cola 
     */
    protected void finish() {
        // Paro el resto de elementos que estaban en espera para ejecutarse
        // con un tiempo de simulacion futura
        while (! waitQueue.isEmpty()) {
            // FIXME: ECHAR UN OJO!!!! Esto no debería tirar
            BasicElement e = removeWait().getElement();
            // MOD 4/07/05 Se notifica del fin de la simulación
            e.notifyEndSimulation();
            //insertaEjecucion(e);
        }
        // Paro aquellos elementos que estaban en la cola de todas las actividades
        for (ActivityManager am : managerList) {
            am.clearActivityQueues();
        }
    }

    class DummyElement extends BasicElement {

		public DummyElement(int id, Simulation simul) {
			super(id, simul);
		}

		protected void startEvents() {
			addEvent(new DummyEvent(maxgvt));
		}

		protected void saveState() {
		}
    	class DummyEvent extends BasicElement.Event {

    		DummyEvent(double ts) {
    			super(ts, LogicalProcess.this);
    		}
			public void event() {
				
			}
    		
    	}
    }
    /**
     * Método que se llama cuando comienza la ejecución del thread proceso lógico. 
     * Arranca todos los recursos activos y comienza la ejecución de los elementos
     */
	public void run() {
        // MOD 14/04/06 Parche para que funcione de momento
        new DummyElement(0, simul).start(this);
        
        lpExecution(); // ejecuto el PL
	}

	public void start() {
		if (lpThread == null) {
	        lpThread = new Thread(this);
	        lpThread.start();
		}
	}

	public String getObjectTypeIdentifier() {
		return "LP";
	}

	protected void printState() {
		StringBuffer strLong = new StringBuffer("------    LP STATE    ------");
		strLong.append("GVT: " + lvt + "\r\n");
        strLong.append(waitQueue.size() + " waiting elements: ");
        for (int i = 0; i < waitQueue.size(); i++) {
            BasicElement.Event e = (BasicElement.Event) waitQueue.get(i);            
            strLong.append(e.getElement() + " ");
        }
        strLong.append("\r\n" + execQueue.size() + " executing elements:");
        for (int i = 0; i < execQueue.size(); i++) {
            strLong.append(execQueue.getEvent(i));
        }
        strLong.append("\r\n");
        for (ActivityManager am : managerList)
        	strLong.append("Activity Manager " + am.getIdentifier() + "\r\n");
        strLong.append("\r\n------ LP STATE FINISHED ------\r\n");
		print(Output.MessageType.DEBUG, "Waiting\t" + waitQueue.size() + "\tExecuting\t" + execQueue.size(), strLong.toString());
	}
}