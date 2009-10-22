/**
 * 
 */
package es.ull.isaatc.simulation.sequential.test;

import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.sequential.Simulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestSimulationTime {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SimulationTime t;
		Simulation sim = new Simulation(0, "", SimulationTimeUnit.MINUTE) {

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
			t = new SimulationTime(SimulationTimeUnit.MINUTE, i);
//			System.out.print(t);
			if (sim.double2SimulationTime(sim.simulationTime2Double(t)).getValue() != t.getValue())
				System.out.println("STOP!!! " + t);
//				System.out.println(" NO!!!!!!");
//			else
//				System.out.println(" OK");
		}
		
	}

}
