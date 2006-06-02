/*
 * FlujoSimple.java
 *
 * Created on 17 de junio de 2005, 12:47
 */

package es.ull.cyc.simulation;

import es.ull.cyc.simulation.results.PendingFlowStatistics;

/**
 * Flujo compuesto de una única actividad. Es un nodo hoja en el árbol de flujos. 
 * @author Iván Castilla Rodríguez
 */
public class SingleFlow extends Flow {
	/** Single flows' Counter. Useful for identying each single flow */
	private static int counter = 0;
	/** Single flow's identifier */
	protected int id;
    /** Actividad que conforma el flujo */
    protected Activity act;
    /** Indicador de si la actividad ha sido realizada o no */
    protected boolean terminada = false;
    
    /** 
     * Crea un nuevo FlujoSimple 
     * @param elem Elemento al que se asocia este flujo.
     * @param act Actividad que conforma el flujo simple.
     */
    public SingleFlow(Element elem, Activity act) {
        super(elem);
        this.act = act;
        id = counter++;
    }
    
    /** 
     * Crea un nuevo FlujoSimple 
     * @param parent Padre de este flujo.
     * @param elem Elemento al que se asocia este flujo.
     * @param act Actividad que conforma el flujo simple.
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
     * Termina la ejecución de la actividad asociada a este flujo y después 
     * llama a finalizar el padre.
     */
    protected void finish() {
        terminada = true;
        if (parent != null)
            parent.finish();
        elem.decRequested(this);
    }
    
    /**
     * Solicita la actividad actual, teniendo en cuenta si es o no presencial.
     */
    protected void request() {
        if (isPresential()) {
            elem.requestActivity(this);
        }
        else {
            NPElement eNP = new NPElement(elem, this);
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
     * Salva la estructura del flujo actual.
     */
    public void saveState() {
        if (!terminada) {
        	elem.getSimul().addStatistic(new PendingFlowStatistics(elem.getIdentifier(), 
        			PendingFlowStatistics.SINFLOW, id));
        	elem.getSimul().addStatistic(new PendingFlowStatistics(elem.getIdentifier(), 
        			PendingFlowStatistics.ACTFLOW, act.getIdentifier()));
        }
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
	public int getId() {
		return id;
	}

	/**
	 * @param id The id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}
    
	/**
	 * @param counter The counter to set.
	 */
	public static void setCounter(int counter) {
		SingleFlow.counter = counter;
	}

}
