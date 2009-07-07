/**
 * 
 */
package es.ull.isaatc.simulation.test;

import es.ull.isaatc.simulation.*;

class MySimulation extends Simulation {
	public static final SimulationTime START = SimulationTime.getZero();
	public static final SimulationTime END = SimulationTime.getZero();
	
	public MySimulation(int id, String description) {
		super(id, description, SimulationTimeUnit.MINUTE, START, END);
	}

	protected void model1() {
		final int NACT = 5;
		final int NRT = NACT + 1;
		for (int i = 0; i < NRT; i++)
			new ResourceType(i, this, "RT" + i);
		for (int i = 0; i < NACT; i++) {
			WorkGroup wg = new WorkGroup();
			wg.add(resourceTypeList.get(i), 1);
			wg.add(resourceTypeList.get(i + 1), 1);
			new TimeDrivenActivity(i, this, "ACT" + i).addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 10.0), wg);
		}
	}

	protected void model2() {
		final int NACT = 20000;
		final int NRT = NACT;
		for (int i = 0; i < NRT; i++)
			new ResourceType(i, this, "RT" + i);
		for (int i = 0; i < NACT; i++) {
			WorkGroup wg = new WorkGroup(resourceTypeList.get(i), 1);
			new TimeDrivenActivity(i, this, "ACT" + i).addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 10.0), wg);
		}
	}
	
	@Override
	protected void createModel() {
		model2();
	}

}
/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class AMCreationTest {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Experiment e = new PooledExperiment("Test AM", 10) {
			@Override
			public Simulation getSimulation(int ind) {
				MySimulation sim = new MySimulation(ind, "Sim AM");
				return sim;
			}			
		};
		e.start();
	}

}
