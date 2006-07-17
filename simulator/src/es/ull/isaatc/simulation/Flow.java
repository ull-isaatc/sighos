/*
 * Flow.java
 *
 * Created on 17 de junio de 2005, 12:46
 */

package es.ull.isaatc.simulation;

import es.ull.isaatc.simulation.state.RecoverableState;
import es.ull.isaatc.simulation.state.FlowState;

/**
 * Representaci�n abstracta del flujo de ejecuci�n de un elemento. Un flujo 
 * tiene una estructura de �rbol. Cada nodo del �rbol puede ser un grupo de 
 * flujos o un flujo simple, que contiene una �nica actividad. En este �ltimo 
 * caso se trata de un nodo hoja.
 * @author Iv�n Castilla Rodr�guez
 */
public abstract class Flow implements RecoverableState<FlowState> {
    /**
     * Padre del flujo actual.
     */    
    protected Flow parent = null;
    /**
     * Elemento al que se asocia este flujo.
     */    
    protected Element elem;
    
    /**
     * Crea una nueva instancia de un flujo
     * @param parent Padre del flujo actual.
     * @param elem Elemento al que se asocia el flujo.
     */
    public Flow(Flow parent, Element elem) {
        this.parent = parent;
        this.elem = elem;
    }
    
    /**
     * Crea una nueva instancia de un flujo que es ra�z del �rbol (no tiene padre).
     * @param elem Elemento al que se asocia el flujo.
     */
    public Flow(Element elem) {
        this.elem = elem;
    }
    
    /**
     * Getter for property parent.
     * @return Value of property parent.
     */
    public es.ull.isaatc.simulation.Flow getParent() {
        return parent;
    }
    
    /**
     * Setter for property parent.
     * @param parent New value of property parent.
     */
    public void setParent(es.ull.isaatc.simulation.Flow parent) {
        this.parent = parent;
    }
    
    /**
     * Getter para la propiedad elem.
     * @return Valor de la propiedad elem.
     */
    public Element getElement() {
        return elem;
    }
    
    /**
     * Permite solicitar el flujo actual para su ejecuci�n.
     */    
    protected abstract void request();
    
    /**
     * Permite concluir la ejecuci�n del flujo actual
     */    
    protected abstract void finish();

    /**
     * Devuelve el n�mero de actividades que contiene el flujo actual. El valor 
     * se devuelve como un array de dos componentes: el 0 para las actividades
     * presenciales y el 1 para las no presenciales.
     * @return El n�mero de componentes del flujo actual.
     */    
    protected abstract int[]countActivities();
 
    /**
     * Searches a single flow in the flow structure
     * @param id single flow's identifier
     * @return The single flow with identifier id.
     */
    protected abstract SingleFlow search(int id);
    
    /**
     * Returns true if the element has finished with this flow.
     * @return True if the flow is finished; false in other case.
     */
    protected abstract boolean isFinished();
}
