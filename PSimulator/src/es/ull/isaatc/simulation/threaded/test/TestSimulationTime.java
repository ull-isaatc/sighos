/**
 * 
 */
package es.ull.isaatc.simulation.threaded.test;

import es.ull.isaatc.simulation.model.Time;
import es.ull.isaatc.simulation.model.TimeUnit;
import es.ull.isaatc.simulation.threaded.Simulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestSimulationTime {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Time t;
		Simulation sim = new Simulation(0, "", TimeUnit.MINUTE) {

			@Override
			protected void createActivityManagers() {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected void createLogicalProcesses() {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected void createModel() {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		for (int i = 0; i < 1000000; i++) {
			t = new Time(TimeUnit.MINUTE, i);
//			System.out.print(t);
			if (sim.double2SimulationTime(sim.simulationTime2Double(t)).getValue() != t.getValue())
				System.out.println("STOP!!! " + t);
//				System.out.println(" NO!!!!!!");
//			else
//				System.out.println(" OK");
		}
		
	}

}
