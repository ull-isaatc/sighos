/**
 * 
 */
package es.ull.isaatc.test;

import simkit.random.RandomVariateFactory;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.simulation.state.SimulationState;
import es.ull.isaatc.util.Output;

class MySimulation extends StandAloneLPSimulation {
	public MySimulation(String description, double startTs, double endTs) {
		super(description, startTs, endTs, new Output());
	}

	protected void model1() {
		final int NACT = 5;
		final int NRT = NACT + 1;
		for (int i = 0; i < NRT; i++)
			new ResourceType(i, this, "RT" + i);
		for (int i = 0; i < NACT; i++) {
			WorkGroup wg = new WorkGroup(i, this, "");
			wg.add(resourceTypeList.get(i), 1);
			wg.add(resourceTypeList.get(i + 1), 1);
			new Activity(i, this, "ACT" + i).addWorkGroup(RandomVariateFactory.getInstance("ConstantVariate", 10.0), wg);
		}
	}

	protected void model2() {
		final int NACT = 20000;
		final int NRT = NACT;
		for (int i = 0; i < NRT; i++)
			new ResourceType(i, this, "RT" + i);
		for (int i = 0; i < NACT; i++) {
			WorkGroup wg = new WorkGroup(i, this, "");
			wg.add(resourceTypeList.get(i), 1);
			new Activity(i, this, "ACT" + i).addWorkGroup(RandomVariateFactory.getInstance("ConstantVariate", 10.0), wg);
		}
	}
	
	@Override
	protected void createModel() {
		model2();
	}
	
	protected void init(SimulationState state) {
		long t = System.currentTimeMillis();
		super.init(state);
		System.out.println("TTree: " + (System.currentTimeMillis() - t));
	}
}
/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class AMCreationTest {
	public static final double START = 0.0;
	public static final double END = 0.0;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Experiment e = new Experiment("Test AM", 10) {
			@Override
			public Simulation getSimulation(int ind) {
				MySimulation sim = new MySimulation("Sim AM", START, END);
				return sim;
			}			
		};
		e.start();
	}

}