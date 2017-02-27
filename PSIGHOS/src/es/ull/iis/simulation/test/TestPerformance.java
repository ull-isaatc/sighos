/**
 * 
 */
package es.ull.iis.simulation.test;

import es.ull.iis.simulation.core.factory.SimulationFactory;
import es.ull.iis.simulation.core.factory.SimulationObjectFactory;
import es.ull.iis.simulation.core.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.engine.SimulationEngine;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestPerformance {
	final static SimulationFactory.SimulationType simType = SimulationType.PARALLEL;
	final static TimeUnit unit = TimeUnit.MINUTE;
	final static TimeStamp STARTTS = TimeStamp.getZero();
	final static TimeStamp ENDTS = TimeStamp.getZero();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Experiment("EXP", 1) {

			@Override
			public SimulationEngine getSimulation(int ind) {
				SimulationObjectFactory factory = SimulationFactory.getInstance(simType, ind, "SimTest", unit, STARTTS, ENDTS);
				SimulationEngine sim = factory.getSimulation();
				int i = 0;
				try {
					for (; ; i++)
						factory.getResourceTypeInstance("RT" + i);
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
