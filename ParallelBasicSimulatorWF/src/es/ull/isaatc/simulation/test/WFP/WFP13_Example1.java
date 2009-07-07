/**
 * 
 */
package es.ull.isaatc.simulation.test.WFP;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.ElementCreator;
import es.ull.isaatc.simulation.ElementType;
import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.Resource;
import es.ull.isaatc.simulation.ResourceType;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.SimulationTimeFunction;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.TimeDrivenActivity;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.WorkGroup;
import es.ull.isaatc.simulation.flow.SingleFlow;
import es.ull.isaatc.simulation.flow.SynchronizedMultipleInstanceFlow;
import es.ull.isaatc.simulation.inforeceiver.StdInfoView;

class SimulationWFP13 extends Simulation {
	int ndays;
	final static int RES = 6;
	
	public SimulationWFP13(int id, int ndays) {
		super(id, "WFP13: Multiple Instances with a priori design-time knowledge", SimulationTimeUnit.MINUTE, SimulationTime.getZero(), new SimulationTime(SimulationTimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
    protected void createModel() {
    	ResourceType rt = new ResourceType(0, this, "Director");
    	WorkGroup wg = new WorkGroup(rt, 1);
    	
    	new TimeDrivenActivity(0, this, "Sign Annual Report").addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 10.0), wg);
    	new TimeDrivenActivity(1, this, "Check acceptance").addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 10.0), wg);
    	
    	SimulationPeriodicCycle c = new SimulationPeriodicCycle(this, 0.0, new SimulationTimeFunction(this, "ConstantVariate", 1440), 0); 
    	for (int i = 0; i < RES; i++)
    		new Resource(i, this, "Director" + i).addTimeTableEntry(c, 480, rt);

		SynchronizedMultipleInstanceFlow root = new SynchronizedMultipleInstanceFlow(this, 6);
    	root.addBranch(new SingleFlow(this, getActivity(0)));
    	root.link(new SingleFlow(this, getActivity(1)));
    	
        SimulationPeriodicCycle cGen = new SimulationPeriodicCycle(this, 0.0, new SimulationTimeFunction(this, "ConstantVariate", 1040.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 3.0), new ElementType(0, this, "ET0"), root), cGen);        
    }	
}

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class WFP13_Example1 {
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
				SimulationWFP13 sim = new SimulationWFP13(ind, NDIAS);
				StdInfoView debugView = new StdInfoView(sim);
				sim.addInfoReciever(debugView);
				return sim;
			}
			
		}.start();
	}

}
