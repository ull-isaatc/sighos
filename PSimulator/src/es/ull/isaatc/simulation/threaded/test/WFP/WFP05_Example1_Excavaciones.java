package es.ull.isaatc.simulation.threaded.test.WFP;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.ModelPeriodicCycle;
import es.ull.isaatc.simulation.common.ModelTimeFunction;
import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.common.TimeUnit;
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
import es.ull.isaatc.simulation.threaded.flow.ParallelFlow;
import es.ull.isaatc.simulation.threaded.flow.SimpleMergeFlow;
import es.ull.isaatc.simulation.threaded.flow.SingleFlow;

class SimulationWFP5E1 extends StandAloneLPSimulation {
	int ndays;
	
	public SimulationWFP5E1(int id, int ndays) {
		super(id, "WFP5: Simple Merge. EjExcavaciones", TimeUnit.MINUTE, Time.getZero(), new Time(TimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
    protected void createModel() {
   	
    	new TimeDrivenActivity(0, this, "Excavacion bobcat", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	new TimeDrivenActivity(1, this, "Excavacion D9", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	new TimeDrivenActivity(2, this, "Facturacion", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	new TimeDrivenActivity(3, this, "Excavacion H8", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
        
        new ResourceType(0, this, "Excavadora bobcat");
        new ResourceType(1, this, "Excavadora D9");
        new ResourceType(2, this, "Conductor");
        new ResourceType(3, this, "Comercial");
        new ResourceType(4, this, "Excavadora H8");
        
        WorkGroup wgEBob = new WorkGroup(new ResourceType[] {getResourceType(0),getResourceType(2)}, new int[] {1, 1});
        WorkGroup wgED9 = new WorkGroup(new ResourceType[] {getResourceType(1),getResourceType(2)}, new int[] {1, 1});
        WorkGroup wgFacturacion = new WorkGroup(getResourceType(3), 1);
        WorkGroup wgEH8 = new WorkGroup(new ResourceType[] {getResourceType(4),getResourceType(2)}, new int[] {1, 1});
        
        ((TimeDrivenActivity)getActivity(0)).addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", 13.0), wgEBob);
        ((TimeDrivenActivity)getActivity(1)).addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", 13.0), wgED9);
        ((TimeDrivenActivity)getActivity(2)).addWorkGroup(new ModelTimeFunction(unit, "NormalVariate", 15.0, 2.0), wgFacturacion);
        ((TimeDrivenActivity)getActivity(3)).addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", 14.0), wgEH8);
   
        ModelPeriodicCycle subc2 = new ModelPeriodicCycle(unit, 480, new ModelTimeFunction(unit, "ConstantVariate", 1440.0), 5);
        ModelPeriodicCycle c2 = new ModelPeriodicCycle(unit, 0, new ModelTimeFunction(unit, "ConstantVariate", 1440.0 * 7), 0, subc2);
        ModelPeriodicCycle c3 = new ModelPeriodicCycle(unit, 0, new ModelTimeFunction(unit, "ConstantVariate", 1440.0 * 7), 600, subc2);

        new Resource(0, this, "Bobcat1").addTimeTableEntry(c2, 420, getResourceType(0));
        new Resource(1, this, "D91").addTimeTableEntry(c3, 100, getResourceType(1));
        new Resource(2, this, "D92").addTimeTableEntry(c2, 100, getResourceType(1));
        new Resource(3, this, "Conductor1").addTimeTableEntry(c2, 420, getResourceType(2));
        new Resource(4, this, "Conductor2").addTimeTableEntry(c2, 420, getResourceType(2));
        new Resource(5, this, "Comercial1").addTimeTableEntry(c2, 420, getResourceType(3));
        new Resource(6, this, "H81").addTimeTableEntry(c2, 420, getResourceType(4));
        new Resource(7, this, "Conductor3").addTimeTableEntry(c2, 420, getResourceType(2));
        
        ParallelFlow root = new ParallelFlow(this);
        SingleFlow sin1 = new SingleFlow(this, (TimeDrivenActivity)getActivity(0));
        SingleFlow sin2 = new SingleFlow(this, (TimeDrivenActivity)getActivity(1));
        SimpleMergeFlow simme1 = new SimpleMergeFlow(this);        
        SingleFlow sin3 = new SingleFlow(this, (TimeDrivenActivity)getActivity(2));
        SingleFlow sin4 = new SingleFlow(this, (TimeDrivenActivity)getActivity(3));
        
        root.link(sin1);
        root.link(sin2);     
        root.link(sin4);     
        sin1.link(simme1);
        sin2.link(simme1);
        sin4.link(simme1);
        simme1.link(sin3);
        
        new ElementType(0, this, "Excavacion");
        ModelPeriodicCycle cGen = new ModelPeriodicCycle(unit, 0.0, new ModelTimeFunction(unit, "ConstantVariate", 1040.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 3.0), getElementType(0), root), cGen);        
    }
	
}

/**
 * 
 */
class ExperimentWFP5E1 extends PooledExperiment {
	static final int NEXP = 1;
    static final int NDIAS = 1;
	static final double PERIOD = 1040.0;
	
	public ExperimentWFP5E1() {
		super("Mina", NEXP);
	}

	public SimulationWFP5E1 getSimulation(int ind) {
		SimulationWFP5E1 sim = new SimulationWFP5E1(ind, NDIAS);
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

public class WFP05_Example1_Excavaciones {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ExperimentWFP5E1().start();
	}

}
