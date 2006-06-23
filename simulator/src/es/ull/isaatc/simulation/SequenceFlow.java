/*
 * FlujoSecuencia.java
 *
 * Created on 17 de junio de 2005, 12:50
 */

package es.ull.isaatc.simulation;

import es.ull.isaatc.simulation.results.PendingFlowStatistics;

/**
 * Flujo compuesto de un conjunto de flujos que se ejecutan de manera secuencial.
 * @author Iván Castilla Rodríguez
 */
public class SequenceFlow extends GroupFlow {
    
    /**
     * Crea un nuevo FlujoSecuencia
     * @param parent Padre de este flujo.
     * @param elem Elemento al que se asocia este flujo.
     */
    public SequenceFlow(SimultaneousFlow parent, Element elem) {
        super(parent, elem);
    }

    /**
     * Crea un nuevo FlujoSecuencia
     * @param elem Elemento al que se asocia este flujo.
     */
    public SequenceFlow(Element elem) {
        super(elem);
    }

    /**
     * Termina la ejecución de este flujo. Si aún quedan componentes por ejecutar
     * solicita el siguiente de la secuencia; en otro caso llama a finalizar el padre.
     */    
    protected synchronized void finish() {
        finishedFlows++;
        if (finishedFlows < list.size())
            request();
        else if (parent != null)
            parent.finish();
    }
    
    /**
     * Solicita el siguiente flujo de la secuencia.
     */    
    protected void request() {
        list.get(finishedFlows).request();
    }        
    
    public void saveState() {
    	if (finishedFlows < list.size()) {
    		elem.getSimul().addStatistic(new PendingFlowStatistics(elem.getIdentifier(), 
    			PendingFlowStatistics.SECFLOW, list.size() - finishedFlows));
	        for (int i = finishedFlows; i < list.size(); i++)
	            list.get(i).saveState();
    	}
    }
}
