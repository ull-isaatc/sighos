package es.ull.isaatc.simulation.test.WFP;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.PooledExperiment;
import es.ull.isaatc.simulation.common.ModelPeriodicCycle;
import es.ull.isaatc.simulation.common.ModelTimeFunction;
import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.sequential.ElementType;
import es.ull.isaatc.simulation.sequential.WorkGroup;
import es.ull.isaatc.simulation.common.Element;
import es.ull.isaatc.simulation.sequential.ElementCreator;
import es.ull.isaatc.simulation.sequential.Resource;
import es.ull.isaatc.simulation.sequential.ResourceType;
import es.ull.isaatc.simulation.sequential.Simulation;
import es.ull.isaatc.simulation.sequential.StandAloneLPSimulation;
import es.ull.isaatc.simulation.sequential.TimeDrivenActivity;
import es.ull.isaatc.simulation.sequential.TimeDrivenGenerator;
import es.ull.isaatc.simulation.common.condition.Condition;
import es.ull.isaatc.simulation.common.condition.NotCondition;
import es.ull.isaatc.simulation.common.flow.Flow;
import es.ull.isaatc.simulation.sequential.flow.MultiChoiceFlow;
import es.ull.isaatc.simulation.sequential.flow.SingleFlow;
import es.ull.isaatc.simulation.common.inforeceiver.StdInfoView;
import es.ull.isaatc.util.Output;

class SimulationWFP10E extends StandAloneLPSimulation {
	int ndays;
	
	public SimulationWFP10E(int id, int ndays) {
		super(id, "WFP10: Arbitrary Cycle. Ej", TimeUnit.MINUTE, Time.getZero(), new Time(TimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
    protected void createModel() {
   	
    	new TimeDrivenActivity(0, this, "Rellenar bidon", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	new TimeDrivenActivity(1, this, "Realizar envío de bidon", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	
    	this.getActivity(0).putVar("capacidadBidon", 20);
    	this.putVar("litrosIntroducidos", 0.0);
    	this.putVar("enviosRealizados", 0);
            
    	new ResourceType(0, this, "Operario");
    	new ResourceType(1, this, "Operario especial");
    	
        WorkGroup wg1 = new WorkGroup(getResourceType(0), 1);
        
        ((TimeDrivenActivity)getActivity(0)).addWorkGroup(new ModelTimeFunction(unit, "NormalVariate", 15.0, 2.0), wg1);
        ((TimeDrivenActivity)getActivity(1)).addWorkGroup(new ModelTimeFunction(unit, "NormalVariate", 15.0, 2.0), wg1);
   
        ModelPeriodicCycle subc2 = new ModelPeriodicCycle(unit, 480, new ModelTimeFunction(unit, "ConstantVariate", 1040.0), 5);
        ModelPeriodicCycle c2 = new ModelPeriodicCycle(unit, 0, new ModelTimeFunction(unit, "ConstantVariate", 1040.0 * 7), 0, subc2);

        new Resource(0, this, "Operario1").addTimeTableEntry(c2, 420, getResourceType(0));
        new Resource(1, this, "Operario1").addTimeTableEntry(c2, 420, getResourceType(0));
        new Resource(3, this, "Operario_especial1").addTimeTableEntry(c2, 420, getResourceType(1));
        
        Condition cond = new Condition(this) {
        	@Override
        	public boolean check(Element e) {
        		return simul.getVar("litrosIntroducidos").getValue().doubleValue() < ((Simulation)simul).getActivity(0).getVar("capacidadBidon").getValue().doubleValue();
        	}
        };
        
        Condition notCond = new NotCondition(cond);
        
        MultiChoiceFlow mul1 = new MultiChoiceFlow(this) {
        	@Override
        	public boolean beforeRequest(Element e) {
        		System.out.println("Volumen actual es " + simul.getVar("litrosIntroducidos") + " litros");
        		System.out.println("Capacidad bidon es " + simul.getActivity(0).getVar("capacidadBidon") + " litros");
        		return true;
        	}
        };
        
        SingleFlow root = new SingleFlow(this, (TimeDrivenActivity)getActivity(0)) {
        	@Override
        	public void afterFinalize(Element e) {
        		if (simul.getVar("litrosIntroducidos").getValue().doubleValue() < simul.getActivity(0).getVar("capacidadBidon").getValue().doubleValue()) {
        			double random = Math.random() * 50;
        			simul.putVar("litrosIntroducidos", simul.getVar("litrosIntroducidos").getValue().doubleValue() + random);
					System.out.println("Introducimos " + random + " litros y nuestro volumen actual es " + simul.getVar("litrosIntroducidos") + " litros");
        		}
        	}
        };
        
        SingleFlow sin1 = new SingleFlow(this, (TimeDrivenActivity)getActivity(1)) {
        	@Override
        	public boolean beforeRequest(Element e) {
        		simul.putVar("litrosIntroducidos", 0);
        		simul.putVar("enviosRealizados", simul.getVar("enviosRealizados").getValue().intValue() + 1);
        		System.out.println("Nuevo envio realizado");
        		return true;
        	}
        };
        
        root.link(mul1);
        ArrayList<Flow> succList = new ArrayList<Flow>();
        succList.add(root);
        succList.add(sin1);
        ArrayList<Condition> condList = new ArrayList<Condition>();
        condList.add(cond);
        condList.add(notCond);
        mul1.link(succList, condList);
        
        new ElementType(0, this, "Cliente");
        ModelPeriodicCycle cGen = new ModelPeriodicCycle(unit, 0.0, new ModelTimeFunction(unit, "ConstantVariate", 1040.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), getElementType(0), root), cGen);       
    }

	@Override
	public void finalize() {
		System.out.println("--------------------- RESULTADOS ---------------------");
		System.out.println("Envíos realizados: " + this.getVar("enviosRealizados"));
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	public void beforeClockTick() {
	}

	@Override
	public void afterClockTick() {
		// TODO Auto-generated method stub
		
	}
}

/**
 * 
 */
class ExperimentWFP10E extends PooledExperiment {
	static final int NEXP = 1;
    static final int NDIAS = 1;
	static final double PERIOD = 1040.0;
	
	public ExperimentWFP10E() {
		super("Banco", NEXP);
	}

	public SimulationWFP10E getSimulation(int ind) {
		SimulationWFP10E sim = null;
		sim = new SimulationWFP10E(ind, NDIAS);
		
		StdInfoView debugView = new StdInfoView(sim);
		sim.addInfoReceiver(debugView);
		try {
			sim.setOutput(new Output(true, new FileWriter("c:\\test.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sim;
	}
	
}

public class WFP10_Example_LlenadoBidon {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ExperimentWFP10E().start();
	}

}
