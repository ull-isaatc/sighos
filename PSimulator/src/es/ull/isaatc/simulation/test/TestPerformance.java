/**
 * 
 */
package es.ull.isaatc.simulation.test;

import es.ull.isaatc.simulation.common.PooledExperiment;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.factory.SimulationFactory;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestPerformance {
	final static SimulationFactory.SimulationType simType = SimulationType.GROUPEDX;
	final static TimeUnit unit = TimeUnit.MINUTE;
	final static TimeStamp STARTTS = TimeStamp.getZero();
	final static TimeStamp ENDTS = TimeStamp.getZero();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PooledExperiment("EXP", 1) {

			@Override
			public Simulation getSimulation(int ind) {
				SimulationObjectFactory factory = SimulationFactory.getInstance(simType, ind, "SimTest", unit, STARTTS, ENDTS);
				Simulation sim = factory.getSimulation();
				int i = 0;
				try {
					for (; ; i++)
						factory.getResourceTypeInstance(i, "RT" + i);
				} catch(OutOfMemoryError e) {
					System.out.println("Not enough memory with " + i + " res. types");
				} 
//				try {
//					for (; ; i++)
//						factory.getTimeDrivenActivityInstance(i, "ACT" + i);
//				} catch(OutOfMemoryError e) {
//					System.out.println("Not enough memory with " + i + " activities");
//				}
				
				return sim;
			}			
		}.start();

	}

}
