package es.ull.iis.simulation.examples.WFP;

import java.util.ArrayList;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.NotCondition;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.TimeUnit;
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
public class WFP10Simulation extends WFPTestSimulationFactory {

	/**
	 * @param type
	 * @param id
	 * @param detailed
	 */
	public WFP10Simulation(int id, boolean detailed) {
		super(id, "WFP10: Arbitrary Cycle. Ej", detailed);
	}

	static class WFP10Model extends Simulation {
		private final double capacidadBidon = 20.0;
		private double litrosIntroducidos;
		private int enviosRealizados;
		
		public WFP10Model(int id, String description, TimeUnit unit, long startTs, long endTs) {
			super(id, description, unit, startTs, endTs);
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
	}
	
	static class WFP10Condition extends Condition {
		private final WFP10Model model;
		
		public WFP10Condition(WFP10Model model) {
			super();
			this.model = model;
		}
		
    	@Override
    	public boolean check(ElementInstance fe) {
    		return (model.getLitrosIntroducidos() < model.getCapacidadBidon());
    	}
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.test.WFP.WFPTestSimulationFactory#createModel(Model model)
	 */
	@Override
	protected Simulation createModel() {
		simul = new WFP10Model(id, description, SIMUNIT, SIMSTART, SIMEND);        
    	ResourceType rt0 = getDefResourceType("Operario");
    	ResourceType rt1 = getDefResourceType("Operario especial");
    	
        WorkGroup wg = new WorkGroup(simul, new ResourceType[] {rt0}, new int[] {1});
	   	

        getDefResource("Operario1", rt0);
        getDefResource("Operario1", rt0);
        getDefResource("Operario_especial1", rt1);
        
        Condition cond = new WFP10Condition((WFP10Model)simul);
        
        Condition notCond = new NotCondition(cond);


        MultiChoiceFlow mul1 = new MultiChoiceFlow(simul) {
        	@Override
        	public boolean beforeRequest(ElementInstance fe) {
        		System.out.println(fe.getElement() + "\tVolumen actual es " + ((WFP10Model)simul).getLitrosIntroducidos() + " litros");
        		System.out.println(fe.getElement() + "\tCapacidad bidon es " + ((WFP10Model)simul).getCapacidadBidon() + " litros");
         		return true;
        	}
        };
        
    	ActivityFlow act0 = new ActivityFlow(simul, "Rellenar bidon", false, false) {
    		@Override
    		public void afterFinalize(ElementInstance fe) {
    			final double litros = ((WFP10Model)simul).getLitrosIntroducidos(); 
    			if (litros < ((WFP10Model)simul).getCapacidadBidon()) {
    				final double random = Math.random() * 5;
    				((WFP10Model)simul).setLitrosIntroducidos(litros + random);
    				System.out.println("Introducimos " + random + " litros y nuestro volumen actual es " + ((WFP10Model)simul).getLitrosIntroducidos() + " litros");
    			}
    		}
    	};
    	act0.addWorkGroup(0, wg, DEFACTDURATION[0]);
    	
    	ActivityFlow act1 = new ActivityFlow(simul, "Realizar envío de bidon", false, false) {
    		@Override
    		public boolean beforeRequest(ElementInstance fe) {
    			((WFP10Model)simul).setLitrosIntroducidos(0);
    			((WFP10Model)simul).incEnviosRealizados();
    			System.out.println("Nuevo envio realizado");
    			return true;
    		}
    	};
    	act1.addWorkGroup(0, wg, DEFACTDURATION[0]);
        
        act0.link(mul1);
        ArrayList<Flow> succList = new ArrayList<Flow>();
        succList.add(act0);
        succList.add(act1);
        ArrayList<Condition> condList = new ArrayList<Condition>();
        condList.add(cond);
        condList.add(notCond);
        mul1.link(succList, condList);
        
        getDefGenerator(getDefElementType("Cliente"), act0);
    	return simul;
	}

}
