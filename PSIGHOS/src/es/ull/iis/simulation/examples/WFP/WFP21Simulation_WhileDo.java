package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.core.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.FlowExecutor;
import es.ull.iis.simulation.model.Model;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.WhileDoFlow;

/**
 * WFP 21. Example 2: Revelado fotográfico (implemented with a while-do structure)
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP21Simulation_WhileDo extends WFPTestSimulationFactory {

	/**
	 * @param type
	 * @param id
	 * @param detailed
	 */
	public WFP21Simulation_WhileDo(SimulationType type, int id, boolean detailed) {
		super(type, id, "WFP21: Structured Loop. EjReveladoFotografico", detailed);
	}

	class WFP21Condition extends Condition {
		
		public WFP21Condition() {
			super();
		}
		
    	@Override
    	public boolean check(FlowExecutor fe) {
    		return (fe.getElement().getVar("fotosReveladas").getValue(fe).intValue() < 10);
    	}	
	}	

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel(Model model)
	 */
	@Override
	protected Model createModel() {
		model = new Model(id, description, SIMUNIT, SIMSTART, SIMEND);        
		ResourceType rt0 = getDefResourceType("Maquina revelado");
        
        WorkGroup wg = new WorkGroup(model, new ResourceType[] {rt0}, new int[] {1});
    	
        getDefResource("Maquina 1", rt0);        
        getDefResource("Maquina 2", rt0);
        
        Condition cond = new WFP21Condition();
        
    	ActivityFlow act0 = new ActivityFlow(model, "Revelar foto", false, false) {
    		@Override
    		public void afterFinalize(FlowExecutor fe) {
    			fe.getElement().putVar("fotosReveladas", fe.getElement().getVar("fotosReveladas").getValue(fe).intValue() + 1);
    			System.out.println(fe.getElement() + ": " + fe.getElement().getVar("fotosReveladas").getValue(fe) + " fotos reveladas.");
    		}
    	};
    	act0.addWorkGroup(0, wg, DEFACTDURATION[0]);
        WhileDoFlow root = new WhileDoFlow(model, act0, cond);

        ElementType et = getDefElementType("Cliente");
        et.addElementVar("fotosReveladas", 0);
        getDefGenerator(et, root);
    	return model;
	}
}
