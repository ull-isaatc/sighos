package es.ull.isaatc.simulation.threaded.test.WFP;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.ModelPeriodicCycle;
import es.ull.isaatc.simulation.common.ModelTimeFunction;
import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.condition.Condition;
import es.ull.isaatc.simulation.common.condition.NotCondition;
import es.ull.isaatc.simulation.common.condition.TrueCondition;
import es.ull.isaatc.simulation.common.inforeceiver.StdInfoView;
import es.ull.isaatc.simulation.threaded.ElementCreator;
import es.ull.isaatc.simulation.threaded.ElementType;
import es.ull.isaatc.simulation.threaded.PooledExperiment;
import es.ull.isaatc.simulation.threaded.Resource;
import es.ull.isaatc.simulation.threaded.ResourceType;
import es.ull.isaatc.simulation.threaded.StandAloneLPSimulation;
import es.ull.isaatc.simulation.threaded.TimeDrivenActivity;
import es.ull.isaatc.simulation.threaded.TimeDrivenGenerator;
import es.ull.isaatc.simulation.threaded.WorkGroup;
import es.ull.isaatc.simulation.threaded.flow.SingleFlow;
import es.ull.isaatc.simulation.threaded.flow.StructuredSynchroMergeFlow;

class SimulationWFP7E1 extends StandAloneLPSimulation {
	int ndays;
	
	public SimulationWFP7E1(int id, int ndays) {
		super(id, "WFP7: Structured Synchronizing Merge. EjTransferenciaPacientes", TimeUnit.MINUTE, Time.getZero(), new Time(TimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
    protected void createModel() {
   	
    	new TimeDrivenActivity(0, this, "Envio policias", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	new TimeDrivenActivity(1, this, "Envio ambulancias", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	new TimeDrivenActivity(2, this, "Transferencia pacientes", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));

        new ResourceType(0, this, "Operador");
        new ResourceType(1, this, "Medico");
        
        WorkGroup wgOp = new WorkGroup(getResourceType(0), 1);
        WorkGroup wgMe = new WorkGroup(getResourceType(1), 1);
        ((TimeDrivenActivity)getActivity(0)).addWorkGroup(new ModelTimeFunction(unit, "NormalVariate", 15.0, 2.0), wgOp);
        ((TimeDrivenActivity)getActivity(1)).addWorkGroup(new ModelTimeFunction(unit, "NormalVariate", 15.0, 2.0), wgOp);
        ((TimeDrivenActivity)getActivity(2)).addWorkGroup(new ModelTimeFunction(unit, "NormalVariate", 15.0, 2.0), wgMe);
        
        ModelPeriodicCycle subc2 = new ModelPeriodicCycle(unit, 480, new ModelTimeFunction(unit, "ConstantVariate", 1040.0), 5);
        ModelPeriodicCycle c2 = new ModelPeriodicCycle(unit, 0, new ModelTimeFunction(unit, "ConstantVariate", 1040.0 * 7), 0, subc2);

        new Resource(0, this, "Operador 1").addTimeTableEntry(c2, 420, getResourceType(0));
        new Resource(1, this, "Medico 1").addTimeTableEntry(c2, 420, getResourceType(1));
        
        SingleFlow sin1 = new SingleFlow(this, (TimeDrivenActivity)getActivity(0));
        SingleFlow sin2 = new SingleFlow(this, (TimeDrivenActivity)getActivity(1));
        SingleFlow sin3 = new SingleFlow(this, (TimeDrivenActivity)getActivity(2));
        StructuredSynchroMergeFlow root = new StructuredSynchroMergeFlow(this);
        
        Condition falseCond = new NotCondition(new TrueCondition());
        
        // Create leafs

        root.addBranch(sin1, falseCond);
        root.addBranch(sin2);
        root.link(sin3);
        
        new ElementType(0, this, "Emergencia");
        ModelPeriodicCycle cGen = new ModelPeriodicCycle(unit, 0.0, new ModelTimeFunction(unit, "ConstantVariate", 1040.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 3.0), getElementType(0), root), cGen);        
    }
	
}

/**
 * 
 */
class ExperimentY15 extends PooledExperiment {
	static final int NEXP = 1;
    static final int NDIAS = 1;
	static final double PERIOD = 1040.0;
	
	public ExperimentY15() {
		super("Hospital", NEXP);
	}

	public SimulationWFP7E1 getSimulation(int ind) {
		SimulationWFP7E1 sim = new SimulationWFP7E1(ind, NDIAS);
		StdInfoView debugView = new StdInfoView(sim);
		sim.addInfoReceiver(debugView);
//		try {
//			sim.setOutput(new Output(true, new FileWriter("c:\\test.txt")));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}		
		return sim;
	}
}

public class WFP07_Example1_TransferenciaPacientes {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ExperimentY15().start();
	}

}
