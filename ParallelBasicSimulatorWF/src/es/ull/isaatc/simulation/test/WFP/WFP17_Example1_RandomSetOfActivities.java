/**
 * 
 */
package es.ull.isaatc.simulation.test.WFP;

import java.util.ArrayList;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.Activity;
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
import es.ull.isaatc.simulation.flow.InterleavedParallelRoutingFlow;
import es.ull.isaatc.simulation.flow.SingleFlow;
import es.ull.isaatc.simulation.inforeceiver.StdInfoView;

/**
 * Creates an interleaved paralell routing example with the following activities: A, B, C, D, E, F;
 * and the following dependencies: A -> B, A -> C, C -> D -> E, B -> E. F has no dependencies 
 * @author Iván Castilla Rodríguez
 *
 */
class SimulationWFP17 extends Simulation {
	final static int RES = 6;
	int ndays;
	
	public SimulationWFP17(int id, int ndays) {
		super(id, "WFP17: Interleaved Parallel Routing", SimulationTimeUnit.MINUTE, SimulationTime.getZero(), new SimulationTime(SimulationTimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
    protected void createModel() {
    	ArrayList<Activity> acts = new ArrayList<Activity>();
    	acts.add(new TimeDrivenActivity(0, this, "A"));
    	acts.add(new TimeDrivenActivity(1, this, "B"));
    	acts.add(new TimeDrivenActivity(2, this, "C"));
    	acts.add(new TimeDrivenActivity(3, this, "D"));
    	acts.add(new TimeDrivenActivity(4, this, "E"));
    	acts.add(new TimeDrivenActivity(5, this, "F"));
    	TimeDrivenActivity finalAct = new TimeDrivenActivity(6, this, "G");
    	
    	ResourceType rt = new ResourceType(0, this, "RT");
    	SimulationPeriodicCycle c = new SimulationPeriodicCycle(this, SimulationTime.getZero(), new SimulationTimeFunction(this, "ConstantVariate", endTs), endTs); 
    	for (int i = 0; i < RES; i++)
    		new Resource(i, this, "RES" + i).addTimeTableEntry(c, endTs, rt);
    	
    	WorkGroup wg = new WorkGroup(rt, 1);
    	for (Activity a : acts)
    		((TimeDrivenActivity)a).addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 10.0), wg);
    	finalAct.addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 10.0), wg);
    	
    	// Dependencies
    	ArrayList<Activity[]> dep = new ArrayList<Activity[]>();
    	dep.add(new Activity[] {acts.get(0), acts.get(1)});
    	dep.add(new Activity[] {acts.get(0), acts.get(2)});
    	dep.add(new Activity[] {acts.get(2), acts.get(3), acts.get(4)});
    	dep.add(new Activity[] {acts.get(1), acts.get(4)});
    	
    	InterleavedParallelRoutingFlow root = new InterleavedParallelRoutingFlow(this, acts, dep);
    	root.link(new SingleFlow(this, finalAct));
        SimulationPeriodicCycle cGen = new SimulationPeriodicCycle(this, 0.0, new SimulationTimeFunction(this, "ConstantVariate", 1040.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 3.0), new ElementType(0, this, "ET0"), root), cGen);        
    	
    } 	
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class WFP17_Example1_RandomSetOfActivities {
	static final int NEXP = 1;
    static final int NDIAS = 1;
	static final double PERIOD = 1040.0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PooledExperiment("Exp17", NEXP) {

			@Override
			public Simulation getSimulation(int ind) {
				SimulationWFP17 sim = new SimulationWFP17(ind, NDIAS);
				StdInfoView debugView = new StdInfoView(sim);
				sim.addInfoReciever(debugView);
				return sim;
			}
			
		}.start();
	}

}
