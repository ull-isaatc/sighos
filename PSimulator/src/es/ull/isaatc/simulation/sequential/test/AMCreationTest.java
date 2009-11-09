/**
 * 
 */
package es.ull.isaatc.simulation.sequential.test;

import es.ull.isaatc.simulation.common.Experiment;
import es.ull.isaatc.simulation.common.PooledExperiment;
import es.ull.isaatc.simulation.common.ModelTimeFunction;
import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.model.WorkGroup;
import es.ull.isaatc.simulation.sequential.*;

class MySimulation extends StandAloneLPSimulation {
	public static final Time START = Time.getZero();
	public static final Time END = Time.getZero();
	private long t;
	
	public MySimulation(int id, String description) {
		super(id, description, TimeUnit.MINUTE, START, END);
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
			new TimeDrivenActivity(i, this, "ACT" + i).addWorkGroup(new ModelTimeFunction(this, "ConstantVariate", 10.0), wg);
		}
	}

	protected void model2() {
		final int NACT = 20000;
		final int NRT = NACT;
		for (int i = 0; i < NRT; i++)
			new ResourceType(i, this, "RT" + i);
		for (int i = 0; i < NACT; i++) {
			WorkGroup wg = new WorkGroup(resourceTypeList.get(i), 1);
			new TimeDrivenActivity(i, this, "ACT" + i).addWorkGroup(new ModelTimeFunction(this, "ConstantVariate", 10.0), wg);
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
