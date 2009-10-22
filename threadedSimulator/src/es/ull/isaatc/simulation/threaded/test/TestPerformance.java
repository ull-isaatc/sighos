/**
 * 
 */
package es.ull.isaatc.simulation.threaded.test;

import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.threaded.ResourceType;
import es.ull.isaatc.simulation.threaded.Simulation;
import es.ull.isaatc.simulation.threaded.StandAloneLPSimulation;
import es.ull.isaatc.simulation.threaded.TimeDrivenActivity;


class TestMaxResourceTypesSimulation extends StandAloneLPSimulation {
	private final static SimulationTime STARTTS = SimulationTime.getZero();
	private final static SimulationTime ENDTS = SimulationTime.getZero();
	private final static int MAX = 200000;
	
	public TestMaxResourceTypesSimulation(int id) {
		super(id, "SimTest" + id, SimulationTimeUnit.MINUTE, STARTTS, ENDTS);
	}
	
	@Override
	protected void createModel() {
		int i = 0;
		try {
			for (; i < MAX; i++)
				new ResourceType(i, this, "RT" + i);
		} catch(OutOfMemoryError e) {
			System.out.println("Not enough memory with " + i + " res. types");
			resourceTypeList.clear();
		}
		
	}
	
}

class TestMaxActivitiesSimulation extends StandAloneLPSimulation {
	private final static SimulationTime STARTTS = SimulationTime.getZero();
	private final static SimulationTime ENDTS = SimulationTime.getZero();
	private final static int MAX = 200000;
	
	public TestMaxActivitiesSimulation(int id) {
		super(id, "SimTest" + id, SimulationTimeUnit.MINUTE, STARTTS, ENDTS);
	}
	
	@Override
	protected void createModel() {
		int i = 0;
		try {
			for (; i < MAX; i++)
				new TimeDrivenActivity(i, this, "ACT" + i);
		} catch(OutOfMemoryError e) {
			System.out.println("Not enough memory with " + i + " activities");
			activityList.clear();
		}
		
	}
	
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestPerformance {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PooledExperiment("EXP", 1) {

			@Override
			public Simulation getSimulation(int ind) {
//				return new TestMaxActivitiesSimulation(ind);
				return new TestMaxResourceTypesSimulation(ind);
			}			
		}.start();

	}

}
