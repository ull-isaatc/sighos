/**
 * 
 */
package es.ull.isaatc.simulation.threaded.test.WFP;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.SimulationTimeFunction;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.threaded.ElementCreator;
import es.ull.isaatc.simulation.threaded.ElementType;
import es.ull.isaatc.simulation.threaded.Resource;
import es.ull.isaatc.simulation.threaded.ResourceType;
import es.ull.isaatc.simulation.threaded.Simulation;
import es.ull.isaatc.simulation.threaded.StandAloneLPSimulation;
import es.ull.isaatc.simulation.threaded.TimeDrivenActivity;
import es.ull.isaatc.simulation.threaded.TimeDrivenGenerator;
import es.ull.isaatc.simulation.threaded.WorkGroup;
import es.ull.isaatc.simulation.threaded.flow.ParallelFlow;
import es.ull.isaatc.simulation.threaded.flow.SingleFlow;
import es.ull.isaatc.simulation.threaded.flow.ThreadSplitFlow;
import es.ull.isaatc.simulation.threaded.inforeceiver.StdInfoView;

class SimulationWFP12 extends StandAloneLPSimulation {
	static final int RES = 5;
	int ndays;
	
	public SimulationWFP12(int id, int ndays) {
		super(id, "WFP12: Multiple Instances without Synchronization", SimulationTimeUnit.MINUTE, SimulationTime.getZero(), new SimulationTime(SimulationTimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
    protected void createModel() {
    	ResourceType rt = new ResourceType(0, this, "Policeman");
    	WorkGroup wg = new WorkGroup(rt, 1);
    	
    	new TimeDrivenActivity(0, this, "Receive Infringment").addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 10.0), wg);
    	new TimeDrivenActivity(1, this, "Issue-Infringment-Notice", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL)).addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 30.0), wg);
    	new TimeDrivenActivity(2, this, "Coffee").addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 10.0), wg);
    	
    	SimulationPeriodicCycle c = new SimulationPeriodicCycle(this, 0.0, new SimulationTimeFunction(this, "ConstantVariate", 1440), 0); 
    	for (int i = 0; i < RES; i++)
    		new Resource(i, this, "RES" + i).addTimeTableEntry(c, 480, rt);
    	
    	SingleFlow root = new SingleFlow(this, getActivity(0));
    	ParallelFlow pf = new ParallelFlow(this);
    	root.link(pf);
    	ThreadSplitFlow tsf = new ThreadSplitFlow(this, 3);
    	tsf.link(new SingleFlow(this, getActivity(1)));
    	pf.link(tsf);
    	SingleFlow finalSf = new SingleFlow(this, getActivity(2));
    	pf.link(finalSf);
    	finalSf.link(root);
    	
        SimulationPeriodicCycle cGen = new SimulationPeriodicCycle(this, 0.0, new SimulationTimeFunction(this, "ConstantVariate", 1040.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 3.0), new ElementType(0, this, "ET0"), root), cGen);        
    }	
}

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class WFP12_Example1 {
	static final int NEXP = 1;
    static final int NDIAS = 1;
	static final double PERIOD = 1040.0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PooledExperiment("Exp12", NEXP) {

			@Override
			public Simulation getSimulation(int ind) {
				SimulationWFP12 sim = new SimulationWFP12(ind, NDIAS);
				StdInfoView debugView = new StdInfoView(sim);
				sim.addInfoReciever(debugView);
				return sim;
			}
			
		}.start();
	}

}