/*
 * FlujoSimultaneo.java
 *
 * Created on 17 de junio de 2005, 12:51
 */

package es.ull.isaatc.simulation;

import es.ull.isaatc.simulation.state.FlowState;
import es.ull.isaatc.simulation.state.SequenceFlowState;
import es.ull.isaatc.simulation.state.SimultaneousFlowState;
import es.ull.isaatc.simulation.state.SingleFlowState;

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

	public FlowState getState() {
		SimultaneousFlowState state = new SimultaneousFlowState(finishedFlows);
		for(Flow f : list)
			state.add((FlowState)f.getState());
		return state;
	}

	public void setState(FlowState state) {
		SimultaneousFlowState simState = (SimultaneousFlowState) state;
		finishedFlows = simState.getFinished();
		
		for (FlowState fState : simState.getDescendants()) {
			Flow f = null;
			if (fState instanceof SingleFlowState)
				f = new SingleFlow(this, elem, elem.getSimul().getActivity(((SingleFlowState)fState).getActId()));
			else if (fState instanceof SequenceFlowState)
				f = new SequenceFlow(this, elem);
			f.setState(fState);
		}
	}
}
