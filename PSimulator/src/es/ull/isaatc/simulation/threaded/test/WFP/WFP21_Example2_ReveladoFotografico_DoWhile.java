package es.ull.isaatc.simulation.threaded.test.WFP;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.model.ModelPeriodicCycle;
import es.ull.isaatc.simulation.model.ModelTimeFunction;
import es.ull.isaatc.simulation.model.Time;
import es.ull.isaatc.simulation.model.TimeUnit;
import es.ull.isaatc.simulation.threaded.Element;
import es.ull.isaatc.simulation.threaded.ElementCreator;
import es.ull.isaatc.simulation.threaded.ElementType;
import es.ull.isaatc.simulation.threaded.Resource;
import es.ull.isaatc.simulation.threaded.ResourceType;
import es.ull.isaatc.simulation.threaded.StandAloneLPSimulation;
import es.ull.isaatc.simulation.threaded.TimeDrivenActivity;
import es.ull.isaatc.simulation.threaded.TimeDrivenGenerator;
import es.ull.isaatc.simulation.threaded.WorkGroup;
import es.ull.isaatc.simulation.threaded.condition.Condition;
import es.ull.isaatc.simulation.threaded.flow.DoWhileFlow;
import es.ull.isaatc.simulation.threaded.flow.SingleFlow;
import es.ull.isaatc.simulation.threaded.inforeceiver.StdInfoView;

class SimulationWFP21E2_ extends StandAloneLPSimulation {
	int ndays;
	
	public SimulationWFP21E2_(int id, int ndays) {
		super(id, "WFP21: Structured Loop. EjReveladoFotografico", TimeUnit.MINUTE, Time.getZero(), new Time(TimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
    protected void createModel() {
    	
    	new TimeDrivenActivity(0, this, "Revelar foto", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));

        new ResourceType(0, this, "Maquina revelado");
        
        WorkGroup wg = new WorkGroup(getResourceType(0), 1);
        ((TimeDrivenActivity)getActivity(0)).addWorkGroup(new ModelTimeFunction(this, "NormalVariate", 15.0, 5.0), wg);

        ModelPeriodicCycle subc2 = new ModelPeriodicCycle(this, 480, new ModelTimeFunction(this, "ConstantVariate", 1040.0), 5);
        ModelPeriodicCycle c2 = new ModelPeriodicCycle(this, 0, new ModelTimeFunction(this, "ConstantVariate", 1040.0 * 7), 0, subc2);

        new Resource(0, this, "Maquina 1").addTimeTableEntry(c2, 420, getResourceType(0));        
        new Resource(1, this, "Maquina 2").addTimeTableEntry(c2, 420, getResourceType(0));
        
        Condition cond = new Condition(this) {
        	@Override
        	public boolean check(Element e) {
        		return (e.getVar("fotosReveladas").getValue().intValue() < 10);
        	}
        };
        
        SingleFlow sin1 = new SingleFlow(this, (TimeDrivenActivity)getActivity(0)) {
        	@Override
        	public void afterFinalize(Element e) {
        		e.putVar("fotosReveladas", e.getVar("fotosReveladas").getValue().intValue() + 1);
        		System.out.println("E" + e.getIdentifier() + ": " + e.getVar("fotosReveladas") + " fotos reveladas.");
        	}
        };
        DoWhileFlow root = new DoWhileFlow(this, sin1, cond);
        
        
        new ElementType(0, this, "Cliente").addElementVar("fotosReveladas", 0);
        ModelPeriodicCycle cGen = new ModelPeriodicCycle(this, 0.0, new ModelTimeFunction(this, "ConstantVariate", 1040.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 3.0), getElementType(0), root), cGen);        
    }
	
}

/**
 * 
 */
class ExperimentWFP21E2_ extends PooledExperiment {
	static final int NEXP = 1;
    static final int NDIAS = 1;
	static final double PERIOD = 1040.0;
	
	public ExperimentWFP21E2_() {
		super("Tienda Revelado", NEXP);
	}

	public SimulationWFP21E2_ getSimulation(int ind) {
		SimulationWFP21E2_ sim = new SimulationWFP21E2_(ind, NDIAS);;
		StdInfoView debugView = new StdInfoView(sim);

		sim.addInfoReciever(debugView);
		return sim;
	}
}

public class WFP21_Example2_ReveladoFotografico_DoWhile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ExperimentWFP21E2_().start();
	}

}