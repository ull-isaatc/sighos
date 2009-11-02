/**
 * 
 */
package es.ull.isaatc.simulation.sequential.test;

import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.sequential.ResourceType;
import es.ull.isaatc.simulation.sequential.Simulation;
import es.ull.isaatc.simulation.sequential.StandAloneLPSimulation;
import es.ull.isaatc.simulation.sequential.TimeDrivenActivity;


class TestMaxResourceTypesSimulation extends StandAloneLPSimulation {
	private final static Time STARTTS = Time.getZero();
	private final static Time ENDTS = Time.getZero();
	private final static int MAX = 200000;
	
	public TestMaxResourceTypesSimulation(int id) {
		super(id, "SimTest" + id, TimeUnit.MINUTE, STARTTS, ENDTS);
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
	private final static Time STARTTS = Time.getZero();
	private final static Time ENDTS = Time.getZero();
	private final static int MAX = 200000;
	
	public TestMaxActivitiesSimulation(int id) {
		super(id, "SimTest" + id, TimeUnit.MINUTE, STARTTS, ENDTS);
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
 * @author Iv�n Castilla Rodr�guez
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
