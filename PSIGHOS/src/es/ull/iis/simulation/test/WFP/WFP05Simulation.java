package es.ull.iis.simulation.test.WFP;

import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.ParallelFlow;
import es.ull.iis.simulation.model.flow.SimpleMergeFlow;

/**
 * WFP 5. Example 1: Excavaciones
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP05Simulation extends WFPTestSimulation {
	
	public WFP05Simulation(int id) {
		super(id, "WFP5: Simple Merge. EjExcavaciones");
    }
    
    protected void createModel() {
        ResourceType rt0 = getDefResourceType("Excavadora bobcat");
        ResourceType rt1 = getDefResourceType("Excavadora D9");
        ResourceType rt2 = getDefResourceType("Conductor");
        ResourceType rt3 = getDefResourceType("Comercial");
        ResourceType rt4 = getDefResourceType("Excavadora H8");
        
        WorkGroup wgEBob = new WorkGroup(this, new ResourceType[] {rt0, rt2}, new int[] {1, 1});
        WorkGroup wgED9 = new WorkGroup(this, new ResourceType[] {rt1, rt2}, new int[] {1, 1});
        WorkGroup wgFacturacion = new WorkGroup(this, new ResourceType[] {rt3}, new int[] {1});
        WorkGroup wgEH8 = new WorkGroup(this, new ResourceType[] {rt4, rt2}, new int[] {1, 1});
        
        ActivityFlow act0 = getDefActivity("Excavacion bobcat", wgEBob, false);
        ActivityFlow act1 = getDefActivity("Excavacion D9", wgED9, false);
        ActivityFlow act2 = getDefActivity("Facturacion", 2, wgFacturacion, false);
        ActivityFlow act3 = getDefActivity("Excavacion H8", 1, wgEH8, false);
        
        getDefResource("Bobcat1", rt0);
        getDefResource("D91", rt1);
        getDefResource("D92", rt1);
        getDefResource("Conductor1", rt2);
        getDefResource("Conductor2", rt2);
        getDefResource("Conductor3", rt2);
        getDefResource("Comercial1", rt3);
        getDefResource("H81", rt4);

        ParallelFlow root = new ParallelFlow(this);
        SimpleMergeFlow simme1 = new SimpleMergeFlow(this);        
        
        root.link(act0);
        root.link(act1);     
        root.link(act3);     
        act0.link(simme1);
        act1.link(simme1);
        act3.link(simme1);
        simme1.link(act2);
        
        getDefGenerator(getDefElementType("Excavacion"), root);
//        getSimulation().addInfoReceiver(new WFP05CheckView(getSimulation(), detailed));

    }
	
}
