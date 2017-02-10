/**
 * 
 */
package es.ull.iis.simulation.test;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.NotCondition;
import es.ull.iis.simulation.core.Experiment;
import es.ull.iis.simulation.core.Resource;
import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.core.SimulationPeriodicCycle;
import es.ull.iis.simulation.core.SimulationTimeFunction;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.simulation.factory.SimulationFactory;
import es.ull.iis.simulation.factory.SimulationObjectFactory;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;

class TestDynamicGenerationExperiment extends Experiment {
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
		ActivityFlow<?,?> act0 = (ActivityFlow<?,?>)factory.getFlowInstance("ActivityFlow", "ACT0");
		act0.addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", 10.0), 0, wg0, new NotCondition(cond));
		
		factory.getElementTypeInstance("ET0");
		factory.getFlowInstance("SingleFlow", act0);
		return factory.getSimulation();
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
		new TestDynamicGenerationExperiment(SimulationType.SEQUENTIAL).start();

	}

}
