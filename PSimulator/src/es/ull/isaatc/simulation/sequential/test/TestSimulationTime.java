/**
 * 
 */
package es.ull.isaatc.simulation.sequential.test;

import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.sequential.Simulation;

/**
 * @author Iv�n Castilla Rodr�guez
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
			if (sim.long2SimulationTime(sim.simulationTime2Long(t)).getValue() != t.getValue())
				System.out.println("STOP!!! " + t);
//				System.out.println(" NO!!!!!!");
//			else
//				System.out.println(" OK");
		}
		
	}

}
