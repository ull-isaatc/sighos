/**
 * 
 */
package es.ull.isaatc.simulation.test.WFP;

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
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.StandAloneLPSimulation;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.TimeDrivenGenerator;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.flow.StaticPartialJoinMultipleInstancesFlow;
import es.ull.isaatc.simulation.common.inforeceiver.StdInfoView;

class SimulationWFP34 extends StandAloneLPSimulation {
	int ndays;
	final static int RES = 6;
	
	public SimulationWFP34(int id, int ndays) {
		super(id, "WFP34: Static Partial Join for Multiple Instances", TimeUnit.MINUTE, Time.getZero(), new Time(TimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
    protected void createModel() {
    	ResourceType rt = new ResourceType(0, this, "Director");
    	WorkGroup wg = factory.getWorkGroupInstance(0, new ResourceType[] {rt}, new int[] {1});
    	
    	new TimeDrivenActivity(0, this, "Sign Annual Report").addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", 10.0), wg);
    	new TimeDrivenActivity(1, this, "Check acceptance").addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", 10.0), wg);
    	
    	ModelPeriodicCycle c = new ModelPeriodicCycle(unit, Time.getZero(), new ModelTimeFunction(unit, "ConstantVariate", 1440), endTs); 
    	for (int i = 0; i < RES; i++)
    		new Resource(i, this, "Director" + i).addTimeTableEntry(c, 480, rt);

		StaticPartialJoinMultipleInstancesFlow root = new StaticPartialJoinMultipleInstancesFlow(this, 6, 4);
    	root.addBranch(new SingleFlow(this, getActivity(0)));
    	root.link(new SingleFlow(this, getActivity(1)));
    	
        ModelPeriodicCycle cGen = new ModelPeriodicCycle(unit, 0.0, new ModelTimeFunction(unit, "ConstantVariate", 1040.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 3.0), new ElementType(0, this, "ET0"), root), cGen);        
    }	
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class WFP34_Example1 {
	static final int NEXP = 1;
    static final int NDIAS = 1;
	static final double PERIOD = 1040.0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PooledExperiment("Exp13", NEXP) {

			@Override
			public Simulation getSimulation(int ind) {
				SimulationWFP34 sim = new SimulationWFP34(ind, NDIAS);
				StdInfoView debugView = new StdInfoView(sim);
				sim.addInfoReceiver(debugView);
				return sim;
			}
			
		}.start();
	}

}
