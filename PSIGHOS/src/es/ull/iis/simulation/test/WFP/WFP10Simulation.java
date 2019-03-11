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
	private final double capacidadBidon = 20.0;
	private double litrosIntroducidos;
	private int enviosRealizados;

	/**
	 * @param type
	 * @param id
	 * @param detailed
	 */
	public WFP10Simulation(int id) {
		super(id, "WFP10: Arbitrary Cycle. Ej");
		litrosIntroducidos = 0.0;
		enviosRealizados = 0;
	}

	/**
	 * @return the litrosIntroducidos
	 */
	public double getLitrosIntroducidos() {
		return litrosIntroducidos;
	}

	/**
	 * @param litrosIntroducidos the litrosIntroducidos to set
	 */
	public void setLitrosIntroducidos(double litrosIntroducidos) {
		this.litrosIntroducidos = litrosIntroducidos;
	}

	/**
	 * @return the enviosRealizados
	 */
	public int getEnviosRealizados() {
		return enviosRealizados;
	}

	/**
	 * 
	 */
	public void incEnviosRealizados() {
		this.enviosRealizados++;
	}

	/**
	 * @return the capacidadBidon
	 */
	public double getCapacidadBidon() {
		return capacidadBidon;
	}
	
	class WFP10Condition extends Condition {
		
		public WFP10Condition() {
			super();
		}
		
    	@Override
    	public boolean check(ElementInstance fe) {
    		return (getLitrosIntroducidos() < getCapacidadBidon());
    	}
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel(Model model)
	 */
	@Override
	protected void createModel() {
    	ResourceType rt0 = getDefResourceType("Operario");
    	ResourceType rt1 = getDefResourceType("Operario especial");
    	
        WorkGroup wg = new WorkGroup(this, new ResourceType[] {rt0}, new int[] {1});
	   	

        getDefResource("Operario1", rt0);
        getDefResource("Operario1", rt0);
        getDefResource("Operario_especial1", rt1);
        
        Condition cond = new WFP10Condition();
        
        Condition notCond = new NotCondition(cond);


        MultiChoiceFlow mul1 = new MultiChoiceFlow(this) {
        	@Override
        	public boolean beforeRequest(ElementInstance fe) {
        		System.out.println(fe.getElement() + "\tVolumen actual es " + getLitrosIntroducidos() + " litros");
        		System.out.println(fe.getElement() + "\tCapacidad bidon es " + getCapacidadBidon() + " litros");
         		return true;
        	}
        };
        
    	ActivityFlow act0 = new ActivityFlow(this, "Rellenar bidon", false, false) {
    		@Override
    		public void afterFinalize(ElementInstance fe) {
    			final double litros = getLitrosIntroducidos(); 
    			if (litros < getCapacidadBidon()) {
    				final double random = Math.random() * 5;
    				setLitrosIntroducidos(litros + random);
    				System.out.println("Introducimos " + random + " litros y nuestro volumen actual es " + getLitrosIntroducidos() + " litros");
    			}
    		}
    	};
    	act0.newWorkGroupAdder(wg).withDelay(DEFACTDURATION[0]).add();
    	
    	ActivityFlow act1 = new ActivityFlow(this, "Realizar envío de bidon", false, false) {
    		@Override
    		public boolean beforeRequest(ElementInstance fe) {
    			setLitrosIntroducidos(0);
    			incEnviosRealizados();
    			System.out.println("Nuevo envio realizado");
    			return true;
    		}
    	};
    	act1.newWorkGroupAdder(wg).withDelay(DEFACTDURATION[0]).add();
        
        act0.link(mul1);
        ArrayList<Flow> succList = new ArrayList<Flow>();
        succList.add(act0);
        succList.add(act1);
        ArrayList<Condition> condList = new ArrayList<Condition>();
        condList.add(cond);
        condList.add(notCond);
        mul1.link(succList, condList);
        
        getDefGenerator(getDefElementType("Cliente"), act0);
	}

}
