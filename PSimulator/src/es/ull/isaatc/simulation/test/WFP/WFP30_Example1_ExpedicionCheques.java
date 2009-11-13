package es.ull.isaatc.simulation.test.WFP;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.PooledExperiment;
import es.ull.isaatc.simulation.common.ModelPeriodicCycle;
import es.ull.isaatc.simulation.common.ModelTimeFunction;
import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.ElementCreator;
import es.ull.isaatc.simulation.common.Resource;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.StandAloneLPSimulation;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.TimeDrivenGenerator;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.flow.StructuredPartialJoinFlow;
import es.ull.isaatc.simulation.common.inforeceiver.StdInfoView;


class SimulationWFP30E1 extends StandAloneLPSimulation {
	int ndays;
	
	public SimulationWFP30E1(int id, int ndays) {
		super(id, "EjExpedicionCheques", TimeUnit.MINUTE, Time.getZero(), new Time(TimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
    protected void createModel() {
   	
    	new TimeDrivenActivity(0, this, "AprobarCuenta", false);
    	new TimeDrivenActivity(1, this, "ExpedirCheque", false);
    	
        new ResourceType(0, this, "Director");
        
        WorkGroup wg = factory.getWorkGroupInstance(getResourceType(0), 1);
        ((TimeDrivenActivity)getActivity(0)).addWorkGroup(new ModelTimeFunction(unit, "NormalVariate", 15.0, 5.0), wg);
        ((TimeDrivenActivity)getActivity(1)).addWorkGroup(new ModelTimeFunction(unit, "NormalVariate", 15.0, 5.0), wg);

        ModelPeriodicCycle subc2 = new ModelPeriodicCycle(unit, 480, new ModelTimeFunction(unit, "ConstantVariate", 1040.0), 5);
        ModelPeriodicCycle c2 = new ModelPeriodicCycle(unit, 0, new ModelTimeFunction(unit, "ConstantVariate", 1040.0 * 7), 0, subc2);

        new Resource(0, this, "Director 1").addTimeTableEntry(c2, 420, getResourceType(0));        
        new Resource(1, this, "Director 2").addTimeTableEntry(c2, 420, getResourceType(0));
        
        StructuredPartialJoinFlow root = new StructuredPartialJoinFlow(this, 2);
        SingleFlow sin1 = new SingleFlow(this, (TimeDrivenActivity)getActivity(0));
        SingleFlow sin2 = new SingleFlow(this, (TimeDrivenActivity)getActivity(0));
        SingleFlow sin3 = new SingleFlow(this, (TimeDrivenActivity)getActivity(0));
        SingleFlow sin4 = new SingleFlow(this, (TimeDrivenActivity)getActivity(1));
        root.addBranch(sin1);
        root.addBranch(sin2);
        root.addBranch(sin3);
        root.link(sin4);
        
        
        new ElementType(0, this, "Peticion de cheque");
        ModelPeriodicCycle cGen = new ModelPeriodicCycle(unit, 0.0, new ModelTimeFunction(unit, "ConstantVariate", 1040.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 3.0), getElementType(0), root), cGen);        
    }
	
}

/**
 * 
 */
class ExperimentWFP30E1 extends PooledExperiment {
	static final int NEXP = 1;
    static final int NDIAS = 1;
	static final double PERIOD = 1040.0;
	
	public ExperimentWFP30E1() {
		super("Banco", NEXP);
	}

	public SimulationWFP30E1 getSimulation(int ind) {
		SimulationWFP30E1 sim = null;
		sim = new SimulationWFP30E1(ind, NDIAS);
		
		StdInfoView debugView = new StdInfoView(sim);
		sim.addInfoReceiver(debugView);
		return sim;
	}
}

public class WFP30_Example1_ExpedicionCheques {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ExperimentWFP30E1().start();
	}
}
