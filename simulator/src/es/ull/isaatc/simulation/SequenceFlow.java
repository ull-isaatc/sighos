/*
 * FlujoSecuencia.java
 *
 * Created on 17 de junio de 2005, 12:50
 */

package es.ull.isaatc.simulation;

import java.util.ArrayList;

import es.ull.isaatc.simulation.state.*;

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
    protected synchronized ArrayList<SingleFlow> finish() {
    	ArrayList<SingleFlow> sfList = new ArrayList<SingleFlow>();
        finishedFlows++;
        if (finishedFlows < list.size())
            sfList.addAll(request());
        else if (parent != null)
            sfList.addAll(parent.finish());
        return sfList;
    }
    
    /**
     * Solicita el siguiente flujo de la secuencia.
     */    
    protected ArrayList<SingleFlow> request() {
    	ArrayList<SingleFlow> sfList = new ArrayList<SingleFlow>();
        sfList.addAll(list.get(finishedFlows).request());
        return sfList;
    }        
    
	public FlowState getState() {
		SequenceFlowState state = new SequenceFlowState(finishedFlows);
		for(Flow f : list)
			state.add((FlowState)f.getState());
		return state;
	}

	public void setState(FlowState state) {
		SequenceFlowState secState = (SequenceFlowState) state;
		finishedFlows = secState.getFinished();
		
		for (FlowState fState : secState.getDescendants()) {
			Flow f = null;
			if (fState instanceof SingleFlowState)
				f = new SingleFlow(this, elem, elem.getSimul().getActivity(((SingleFlowState)fState).getActId()));
			else if (fState instanceof SimultaneousFlowState)
				f = new SimultaneousFlow(this, elem);
			f.setState(fState);
		}
	}
}
