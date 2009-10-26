/**
 * 
 */
package es.ull.isaatc.simulation.threaded.test.WFP;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.model.ModelPeriodicCycle;
import es.ull.isaatc.simulation.model.ModelTimeFunction;
import es.ull.isaatc.simulation.model.Time;
import es.ull.isaatc.simulation.model.TimeUnit;
import es.ull.isaatc.simulation.threaded.ElementCreator;
import es.ull.isaatc.simulation.threaded.ElementType;
import es.ull.isaatc.simulation.threaded.Resource;
import es.ull.isaatc.simulation.threaded.ResourceType;
import es.ull.isaatc.simulation.threaded.Simulation;
import es.ull.isaatc.simulation.threaded.StandAloneLPSimulation;
import es.ull.isaatc.simulation.threaded.TimeDrivenActivity;
import es.ull.isaatc.simulation.threaded.TimeDrivenGenerator;
import es.ull.isaatc.simulation.threaded.WorkGroup;
import es.ull.isaatc.simulation.threaded.flow.SingleFlow;
import es.ull.isaatc.simulation.threaded.flow.SynchronizedMultipleInstanceFlow;
import es.ull.isaatc.simulation.threaded.inforeceiver.StdInfoView;

class SimulationWFP13 extends StandAloneLPSimulation {
	int ndays;
	final static int RES = 6;
	
	public SimulationWFP13(int id, int ndays) {
		super(id, "WFP13: Multiple Instances with a priori design-time knowledge", TimeUnit.MINUTE, Time.getZero(), new Time(TimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
    protected void createModel() {
    	ResourceType rt = new ResourceType(0, this, "Director");
    	WorkGroup wg = new WorkGroup(rt, 1);
    	
    	new TimeDrivenActivity(0, this, "Sign Annual Report").addWorkGroup(new ModelTimeFunction(this, "ConstantVariate", 10.0), wg);
    	new TimeDrivenActivity(1, this, "Check acceptance").addWorkGroup(new ModelTimeFunction(this, "ConstantVariate", 10.0), wg);
    	
    	ModelPeriodicCycle c = new ModelPeriodicCycle(this, 0.0, new ModelTimeFunction(this, "ConstantVariate", 1440), 0); 
    	for (int i = 0; i < RES; i++)
    		new Resource(i, this, "Director" + i).addTimeTableEntry(c, 480, rt);

		SynchronizedMultipleInstanceFlow root = new SynchronizedMultipleInstanceFlow(this, 6);
    	root.addBranch(new SingleFlow(this, getActivity(0)));
    	root.link(new SingleFlow(this, getActivity(1)));
    	
        ModelPeriodicCycle cGen = new ModelPeriodicCycle(this, 0.0, new ModelTimeFunction(this, "ConstantVariate", 1040.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 3.0), new ElementType(0, this, "ET0"), root), cGen);        
    }	
}

/**
 * @author Iván Castilla Rodríguez
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
