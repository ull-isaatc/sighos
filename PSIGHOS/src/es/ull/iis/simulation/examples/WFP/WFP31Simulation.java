package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.model.Model;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.core.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.ParallelFlow;
import es.ull.iis.simulation.model.flow.PartialJoinFlow;

/**
 * WFP 31. Banco
 * @author Yeray Callero
 * @author Iv�n Castilla
 *
 */
public class WFP31Simulation extends WFPTestSimulationFactory {

	public WFP31Simulation(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP31: Blocking Partial Join. EjBanco", detailed);
	}

	@Override
	protected Model createModel() {
		Model model = new Model(SIMUNIT);        
		ResourceType rt0 = getDefResourceType("Director");
	   	
        WorkGroup wg = new WorkGroup(model, new ResourceType[] {rt0}, new int[] {1});

        ActivityFlow act0_0 = getDefActivity("AprobarCuenta", wg, false);
        ActivityFlow act0_1 = getDefActivity("AprobarCuenta", wg, false);
        ActivityFlow act0_2 = getDefActivity("AprobarCuenta", wg, false);
        ActivityFlow act1 = getDefActivity("ExpedirCheque", wg, false);
    	
        getDefResource("Director 1", rt0);        
        getDefResource("Director 2", rt0);       
        
        ParallelFlow root = new ParallelFlow(model);

        PartialJoinFlow part1 = new PartialJoinFlow(model, 2);
        
        root.link(act0_0);
        root.link(act0_1);
        root.link(act0_2);
        act0_0.link(part1);
        act0_1.link(part1);
        act0_2.link(part1);
        part1.link(act1);
        
        getDefGenerator(getDefElementType("Cliente"), root);
    	return model;
	}

}
