/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import es.ull.iis.simulation.model.DiscreteEvent;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.EventSource;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.flow.GeneratorFlow;
import es.ull.iis.simulation.model.flow.InitializerFlow;

/**
 * @author Iván Castilla
 *
 */
public class TruckCreatorFlow extends GeneratorFlow {
	final private ElementType[] truckSources;
	final private InitializerFlow initialFlow;
	/**
	 * @param model
	 * @param nElem
	 * @param et
	 * @param flow
	 * @param cycle
	 */
	public TruckCreatorFlow(Simulation model, ElementType[] truckSources, InitializerFlow initialFlow) {
		super(model, "Create trucks to serve vessel");
		this.initialFlow = initialFlow;
		this.truckSources = truckSources;
	}

	@Override
	public void create(ElementInstance ei) {
		super.create(ei);
		final Vessel vessel = (Vessel)ei.getElement();
		final WaresType wares = vessel.getWares();
//		final int nTrucks = (int) (vessel.getInitLoad() / wares.getTonesPerTruck());
		final int nTrucks = 1;
		final EventSource[] elems = new EventSource[nTrucks];
        for (int i = 0; i < nTrucks; i++) {
            double p = Math.random();
            for (TruckSource source : TruckSource.values()) {
            	p -= wares.getProportionPerTruckSource()[source.ordinal()];
            	if (p <= 0.0){
            		elems[i] = new Truck(getSimulation(), truckSources[source.ordinal()], initialFlow, vessel, source);
            		// Some generators may not create the element for some reason
            		if (elems[i] != null) {
	    	            final DiscreteEvent e = elems[i].onCreate(getTs());
	    	            simul.addEvent(e);
            		}
    	            break;
            	}
            }
        }
	}
}
