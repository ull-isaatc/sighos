/**
 * 
 */
package es.ull.isaatc.test;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.*;

class MySimulation extends StandAloneLPSimulation {
	private long t;
	public MySimulation(String description) {
		super(description);
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
			new Activity(i, this, "ACT" + i).addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", 10.0), wg);
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
			new Activity(i, this, "ACT" + i).addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", 10.0), wg);
		}
	}
	
	@Override
	protected void createModel() {
		t = System.currentTimeMillis();
		model2();
	}
	
	protected void createLogicalProcesses() {
		super.createLogicalProcesses();
		System.out.println("TTree: " + (System.currentTimeMillis() - t));
	}
}
/**
 * @author Iván Castilla Rodríguez
 *
 */
public class AMCreationTest {
	public static final double START = 0.0;
	public static final double END = 0.0;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Experiment e = new Experiment("Test AM", 10, START, END) {
			@Override
			public Simulation getSimulation(int ind) {
				MySimulation sim = new MySimulation("Sim AM");
				return sim;
			}			
		};
		e.start();
	}

}
