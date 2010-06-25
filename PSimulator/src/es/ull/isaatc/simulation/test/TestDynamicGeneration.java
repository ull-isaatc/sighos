/**
 * 
 */
package es.ull.isaatc.simulation.test;

import es.ull.isaatc.simulation.common.PooledExperiment;
import es.ull.isaatc.simulation.common.Resource;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.common.SimulationTimeFunction;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.condition.Condition;
import es.ull.isaatc.simulation.common.condition.NotCondition;
import es.ull.isaatc.simulation.common.factory.SimulationFactory;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;

class TestDynamicGenerationExperiment extends PooledExperiment {
	SimulationType type;
	
	public TestDynamicGenerationExperiment(SimulationType type) {
		super("Test Dynamic Generation", 1);
		this.type = type;
	}

	@Override
	public Simulation getSimulation(int ind) {
		TimeUnit unit = TimeUnit.MINUTE;
		SimulationObjectFactory factory = SimulationFactory.getInstance(type, ind, "Test Dynamic", unit, 0, 1);
		
		ResourceType rt0 = factory.getResourceTypeInstance("RT0");
		ResourceType rt1 = factory.getResourceTypeInstance("RT1");
		
		Resource r0 =  factory.getResourceInstance("Res0");
		r0.addTimeTableEntry(SimulationPeriodicCycle.newDailyCycle(unit), 1, rt0);
		Resource r1 = factory.getResourceInstance("Res1");
		r1.addTimeTableEntry(SimulationPeriodicCycle.newDailyCycle(unit), 1, rt1);
		
		WorkGroup wg0 = factory.getWorkGroupInstance(new ResourceType [] {rt0, rt1}, new int[] {1,1});
		
		Condition cond = factory.getCustomizedConditionInstance(null, "false");
		TimeDrivenActivity act0 = factory.getTimeDrivenActivityInstance("ACT0");
		act0.addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", 10.0), wg0, new NotCondition(cond));
		
		factory.getElementTypeInstance("ET0");
		factory.getFlowInstance("SingleFlow", act0);
		return factory.getSimulation();
	}
}

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class TestDynamicGeneration {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TestDynamicGenerationExperiment(SimulationType.SEQUENTIAL).start();

	}

}
