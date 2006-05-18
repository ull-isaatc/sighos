/*
 * FlujoSimultaneo.java
 *
 * Created on 17 de junio de 2005, 12:51
 */

package es.ull.cyc.simulation;

import es.ull.cyc.simulation.results.PendingFlowStatistics;

/**
 * Flujo compuesto de un conjunto de flujos que se solicitan simultáneamente.
 * @author Iván Castilla Rodríguez
 */
public class SimultaneousFlow extends GroupFlow {
    
    /**
     * Crea un nuevo FlujoSimultaneo
     * @param parent Padre de este flujo.
     * @param elem Elemento al que se asocia este flujo.
     */
    public SimultaneousFlow(SequenceFlow parent, Element elem) {
        super(parent, elem);
    }

    /**
     * Crea un nuevo FlujoSimultaneo
     * @param elem Elemento al que se asocia este flujo.
     */
    public SimultaneousFlow(Element elem) {
        super(elem);
    }

    /**
     * Termina la ejecución de este flujo. Si ya no quedan componentes por 
     * ejecutar llama a finalizar el padre.
     */    
    protected synchronized void finish() {
        finishedFlows++;
        if ((finishedFlows == list.size()) && (parent != null))
            parent.finish();
    }
    
    /**
     * Solicita todos los flujos de la lista.
     */    
    protected void request() {
        for (int i = 0; i < list.size(); i++) 
            list.get(i).request();
    }       

    public void saveState() {
    	if (finishedFlows < list.size()) {
    		elem.getSimul().addStatistic(new PendingFlowStatistics(elem.getIdentifier(), 
    			PendingFlowStatistics.SIMFLOW, list.size() - finishedFlows));
	        for (int i = 0; i < list.size(); i++)
	            list.get(i).saveState();
    	}
    }

}
