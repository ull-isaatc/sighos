/**
 * 
 */
package es.ull.isaatc.simulation.test;

import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.ModelPeriodicCycle;
import es.ull.isaatc.simulation.common.ModelTimeFunction;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.model.ElementType;
import es.ull.isaatc.simulation.model.Model;
import es.ull.isaatc.simulation.model.Resource;
import es.ull.isaatc.simulation.model.ResourceType;
import es.ull.isaatc.simulation.model.TimeDrivenActivity;
import es.ull.isaatc.simulation.model.WorkGroup;
import es.ull.isaatc.simulation.model.condition.Condition;
import es.ull.isaatc.simulation.model.condition.NotCondition;

class TestModel extends Model {
	public TestModel() {
		super(0, "TestModel", TimeUnit.MINUTE, 0.0, 1.0);
	}
	@Override
	protected void createModel() {
		ResourceType rt0 = new ResourceType(0, this, "RT0");
		ResourceType rt1 = new ResourceType(1, this, "RT1");
		
		Resource r0 = new Resource(0, this, "Res0");
		r0.addTimeTableEntry(ModelPeriodicCycle.newDailyCycle(unit), 1, rt0);
		Resource r1 = new Resource(1, this, "Res1");
		r1.addTimeTableEntry(ModelPeriodicCycle.newDailyCycle(unit), 1, rt1);
		
		WorkGroup wg0 = new WorkGroup(new ResourceType [] {rt0, rt1}, new int[] {1,1});
		
		Condition cond = new Condition("return false;");
		TimeDrivenActivity act0 = new TimeDrivenActivity(0, this, "ACT0");
		act0.addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", 10.0), wg0, new NotCondition(cond));
		
		ElementType et0 = new ElementType(0, this, "ET0");
	}
	
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestDynamicGeneration {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PooledExperiment(SimulationType.SEQUENTIAL, "Test", new TestModel(), 1).start();

	}

}
