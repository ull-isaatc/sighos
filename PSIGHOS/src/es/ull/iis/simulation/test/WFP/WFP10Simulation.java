package es.ull.iis.simulation.test.WFP;

import java.util.ArrayList;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.NotCondition;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.MultiChoiceFlow;

/**
 * WFP 10. Arbitrary Cycle
 * @author Yeray Callero
 * @author Iván Castilla
 *
 */
public class WFP10Simulation extends WFPTestSimulation {
	private static final double MAX_CAPACITY = 20.0;
	private double capacity;
	private int deliveries;

	/**
	 * @param type
	 * @param id
	 * @param detailed
	 */
	public WFP10Simulation(int id) {
		super(id, "WFP10: Arbitrary Cycle. Ej");
		capacity = 0.0;
		deliveries = 0;
	}

	/**
	 * @return the litrosIntroducidos
	 */
	private double getCapacity() {
		return capacity;
	}

	/**
	 * 
	 */
	private double fillBin() {
		final double filling = Math.min(Math.random() * 5, MAX_CAPACITY - capacity);
		capacity += filling;
		return filling;
	}

	/**
	 * 
	 */
	private void incDeliveries() {
		this.capacity = 0.0;
		this.deliveries++;
	}
	
	private class WFP10Condition extends Condition<ElementInstance> {
		
		public WFP10Condition() {
			super();
		}
		
    	@Override
    	public boolean check(ElementInstance fe) {
    		return (getCapacity() < MAX_CAPACITY);
    	}
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel(Model model)
	 */
	@Override
	protected void createModel() {
    	final ResourceType rt0 = getDefResourceType("Operator");
    	final ResourceType rt1 = getDefResourceType("Special operator");
    	
        final WorkGroup wg = new WorkGroup(this, new ResourceType[] {rt0}, new int[] {1});
	   	

        getDefResource("Operator1", rt0);
        getDefResource("Operator1", rt0);
        getDefResource("Special_operator1", rt1);
        
        final Condition<ElementInstance> cond = new WFP10Condition();
        
        final MultiChoiceFlow mul1 = new MultiChoiceFlow(this) {
        	@Override
        	public boolean beforeRequest(ElementInstance ei) {
        		ei.getElement().debug("Current volume: " + getCapacity());
         		return true;
        	}
        };
        
    	final ActivityFlow act0 = new TestActivityFlow("Fill bin", 0, wg, false) {
    		@Override
    		public void afterFinalize(ElementInstance ei) {
    			final double filling = fillBin();
    			ei.getElement().debug("Added: "+ filling + " / Current volume: " + getCapacity());
    		}
    	};
    	
    	final ActivityFlow act1 = new TestActivityFlow("Sent bin", 0, wg, false) {
    		@Override
    		public boolean beforeRequest(ElementInstance ei) {
    			incDeliveries();
    			ei.getElement().debug("Sent: " + deliveries);
    			return true;
    		}
    	};
        
        act0.link(mul1);
        ArrayList<Flow> succList = new ArrayList<Flow>();
        succList.add(act0);
        succList.add(act1);
        ArrayList<Condition<ElementInstance>> condList = new ArrayList<>();
        condList.add(cond);
        condList.add(new NotCondition<ElementInstance>(cond));
        mul1.link(succList, condList);
        
        getDefGenerator(getDefElementType("Client"), act0);
	}

}
