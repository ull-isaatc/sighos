package es.ull.isaatc.simulation.sequential.test.WFP;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.PooledExperiment;
import es.ull.isaatc.simulation.common.ModelPeriodicCycle;
import es.ull.isaatc.simulation.common.ModelTimeFunction;
import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.sequential.ElementType;
import es.ull.isaatc.simulation.sequential.WorkGroup;
import es.ull.isaatc.simulation.sequential.ElementCreator;
import es.ull.isaatc.simulation.sequential.Resource;
import es.ull.isaatc.simulation.sequential.ResourceType;
import es.ull.isaatc.simulation.sequential.StandAloneLPSimulation;
import es.ull.isaatc.simulation.sequential.TimeDrivenActivity;
import es.ull.isaatc.simulation.sequential.TimeDrivenGenerator;
import es.ull.isaatc.simulation.sequential.flow.MultiMergeFlow;
import es.ull.isaatc.simulation.sequential.flow.ParallelFlow;
import es.ull.isaatc.simulation.sequential.flow.SingleFlow;
import es.ull.isaatc.simulation.common.inforeceiver.StdInfoView;

class SimulationWFP8E1 extends StandAloneLPSimulation {
	int ndays;
	
	public SimulationWFP8E1(int id, int ndays) {
		super(id, "WFP8: Multi-Merge. EjControlCalidad", TimeUnit.MINUTE, Time.getZero(), new Time(TimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
    protected void createModel() {
   	
    	new TimeDrivenActivity(0, this, "Crear destornilladores", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	new TimeDrivenActivity(1, this, "Crear llaves", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	new TimeDrivenActivity(2, this, "Crear niveladores", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	new TimeDrivenActivity(3, this, "Control de calidad", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
        
        new ResourceType(0, this, "Maquina productora");
        new ResourceType(1, this, "Empleados");
        
        WorkGroup wgMa = new WorkGroup(getResourceType(0), 1);
        WorkGroup wgEm = new WorkGroup(getResourceType(1), 1);
        ((TimeDrivenActivity)getActivity(0)).addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", 15.0), wgMa);
        ((TimeDrivenActivity)getActivity(1)).addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", 15.0), wgMa);
        ((TimeDrivenActivity)getActivity(2)).addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", 10.0), wgMa);
        ((TimeDrivenActivity)getActivity(3)).addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", 15.0), wgEm);

        ModelPeriodicCycle subc2 = new ModelPeriodicCycle(unit, 480, new ModelTimeFunction(unit, "ConstantVariate", 1040.0), 5);
        ModelPeriodicCycle c2 = new ModelPeriodicCycle(unit, 0, new ModelTimeFunction(unit, "ConstantVariate", 1040.0 * 7), 0, subc2);

        new Resource(0, this, "Maquina1").addTimeTableEntry(c2, 420, getResourceType(0));
        new Resource(1, this, "Maquina2").addTimeTableEntry(c2, 420, getResourceType(0));
        new Resource(2, this, "Empleado1").addTimeTableEntry(c2, 420, getResourceType(1));        
        
        ParallelFlow root = new ParallelFlow(this);
        SingleFlow sin1 = new SingleFlow(this, (TimeDrivenActivity)getActivity(0));
        SingleFlow sin2 = new SingleFlow(this, (TimeDrivenActivity)getActivity(1));
        SingleFlow sin3 = new SingleFlow(this, (TimeDrivenActivity)getActivity(2));
        MultiMergeFlow mulmer1 = new MultiMergeFlow(this);
        SingleFlow sin4 = new SingleFlow(this, (TimeDrivenActivity)getActivity(3));
        
        
        root.link(sin1);
        root.link(sin2);
        root.link(sin3);
        sin1.link(mulmer1);
        sin2.link(mulmer1);
        sin3.link(mulmer1);
        mulmer1.link(sin4);
        
        new ElementType(0, this, "Remesa productos");
        ModelPeriodicCycle cGen = new ModelPeriodicCycle(unit, 0.0, new ModelTimeFunction(unit, "ConstantVariate", 1040.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 3.0), getElementType(0), root), cGen);        
    }
	
}

/**
 * 
 */
class ExperimentWFP8E1 extends PooledExperiment {
	static final int NEXP = 1;
    static final int NDIAS = 1;
	static final double PERIOD = 1040.0;
	
	public ExperimentWFP8E1() {
		super("Fabrica", NEXP);
	}

	public SimulationWFP8E1 getSimulation(int ind) {
		SimulationWFP8E1 sim = null;
		sim = new SimulationWFP8E1(ind, NDIAS);
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

public class WFP08_Example1_ControlCalidad {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ExperimentWFP8E1().start();
	}

}
