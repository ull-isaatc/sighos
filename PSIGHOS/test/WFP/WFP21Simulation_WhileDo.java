package es.ull.iis.simulation.test.WFP;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.ElementType;
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
public class WFP21Simulation_WhileDo extends WFPTestSimulation {

	/**
	 * @param type
	 * @param id
	 * @param detailed
	 */
	public WFP21Simulation_WhileDo(int id) {
		super(id, "WFP21: Structured Loop (WhileDo). EjReveladoFotografico");
	}

	class WFP21Condition extends Condition<ElementInstance> {
		
		public WFP21Condition() {
			super();
		}
		
    	@Override
    	public boolean check(ElementInstance fe) {
    		return (fe.getElement().getVar("fotosReveladas").getValue(fe).intValue() < 10);
    	}	
	}	

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel(Model model)
	 */
	@Override
	protected void createModel() {
		final ResourceType rt0 = getDefResourceType("Maquina revelado");
        
        final WorkGroup wg = new WorkGroup(this, new ResourceType[] {rt0}, new int[] {1});
    	
        getDefResource("Maquina 1", rt0);        
        getDefResource("Maquina 2", rt0);
        
        final Condition<ElementInstance> cond = new WFP21Condition();
        
    	final ActivityFlow act0 = new TestActivityFlow("Revelar foto", 0, wg, false) {
    		@Override
    		public void afterFinalize(ElementInstance fe) {
    			fe.getElement().putVar("fotosReveladas", fe.getElement().getVar("fotosReveladas").getValue(fe).intValue() + 1);
//    			System.out.println(fe.getElement() + ": " + fe.getElement().getVar("fotosReveladas").getValue(fe) + " fotos reveladas.");
    		}
    	};
        final WhileDoFlow root = new WhileDoFlow(this, act0, cond);

        final ElementType et = getDefElementType("Cliente");
        et.addElementVar("fotosReveladas", 0);
        getDefGenerator(et, root);
	}
}
