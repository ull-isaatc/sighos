/**
 * 
 */
package es.ull.isaatc.simulation.sequential.test.WFP;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.common.ModelPeriodicCycle;
import es.ull.isaatc.simulation.common.ModelTimeFunction;
import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.model.ElementType;
import es.ull.isaatc.simulation.model.WorkGroup;
import es.ull.isaatc.simulation.sequential.ElementCreator;
import es.ull.isaatc.simulation.sequential.Resource;
import es.ull.isaatc.simulation.sequential.ResourceType;
import es.ull.isaatc.simulation.sequential.Simulation;
import es.ull.isaatc.simulation.sequential.StandAloneLPSimulation;
import es.ull.isaatc.simulation.sequential.TimeDrivenActivity;
import es.ull.isaatc.simulation.sequential.TimeDrivenGenerator;
import es.ull.isaatc.simulation.sequential.flow.SingleFlow;
import es.ull.isaatc.simulation.sequential.flow.ThreadMergeFlow;
import es.ull.isaatc.simulation.sequential.flow.ThreadSplitFlow;
import es.ull.isaatc.simulation.sequential.inforeceiver.StdInfoView;

class SimulationWFP41_42 extends StandAloneLPSimulation {
	int ndays;
	
	public SimulationWFP41_42(int id, int ndays) {
		super(id, "WFP41_42: Thread Split-Merge", TimeUnit.MINUTE, Time.getZero(), new Time(TimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
	@Override
	protected void createModel() {		
		new ResourceType(0, this, "Program Chair"); 
		new ResourceType(1, this, "Peer Referee");
    	ModelPeriodicCycle c = new ModelPeriodicCycle(this, Time.getZero(), new ModelTimeFunction(this, "ConstantVariate", endTs), endTs);
    	new Resource(0, this, "PC1").addTimeTableEntry(c, endTs, getResourceType(0));
    	new Resource(1, this, "Ref0").addTimeTableEntry(c, endTs, getResourceType(1));
    	new Resource(2, this, "Ref1").addTimeTableEntry(c, endTs, getResourceType(1));
    	new Resource(3, this, "Ref2").addTimeTableEntry(c, endTs, getResourceType(1));
		
		WorkGroup wg0 = new WorkGroup(getResourceType(0), 1);
		WorkGroup wg1 = new WorkGroup(getResourceType(1), 1);
		new TimeDrivenActivity(0, this, "Confirm paper receival", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL))
			.addWorkGroup(new ModelTimeFunction(this, "ConstantVariate", 5.0), wg0);		
		new TimeDrivenActivity(1, this, "Independent Peer review", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL))
			.addWorkGroup(new ModelTimeFunction(this, "ConstantVariate", 120.0), wg1);;
		new TimeDrivenActivity(2, this, "Notify authors", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL))
			.addWorkGroup(new ModelTimeFunction(this, "ConstantVariate", 5.0), wg0);;
		
		SingleFlow root = new SingleFlow(this, getActivity(0));
		ThreadSplitFlow split = new ThreadSplitFlow(this, 3);
		root.link(split);
		SingleFlow peer = new SingleFlow(this, getActivity(1));
		split.link(peer);
		ThreadMergeFlow merge = new ThreadMergeFlow(this, 3);
		peer.link(merge);
		merge.link(new SingleFlow(this, getActivity(2)));

        ModelPeriodicCycle cGen = new ModelPeriodicCycle(this, 0.0, new ModelTimeFunction(this, "ConstantVariate", 1040.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 3.0), new ElementType(0, this, "ET0"), root), cGen);       
		
	}
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class WFP41_42_Example1 {
	static final int NEXP = 1;
    static final int NDIAS = 1;
	static final double PERIOD = 1040.0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PooledExperiment("Exp41_42", NEXP) {

			@Override
			public Simulation getSimulation(int ind) {
				SimulationWFP41_42 sim = new SimulationWFP41_42(ind, NDIAS);
				StdInfoView debugView = new StdInfoView(sim);
				sim.addInfoReceiver(debugView);
				return sim;
			}
			
		}.start();
	}

}
