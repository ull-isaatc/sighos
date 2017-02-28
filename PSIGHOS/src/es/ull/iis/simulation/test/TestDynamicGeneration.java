/**
 * 
 */
package es.ull.iis.simulation.test;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.NotCondition;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.factory.SimulationFactory;
import es.ull.iis.simulation.core.factory.SimulationObjectFactory;
import es.ull.iis.simulation.core.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.ModelPeriodicCycle;
import es.ull.iis.simulation.model.ModelTimeFunction;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.engine.ResourceEngine;
import es.ull.iis.simulation.model.engine.ResourceTypeEngine;
import es.ull.iis.simulation.model.engine.SimulationEngine;

class TestDynamicGenerationExperiment extends Experiment {
	SimulationType type;
	
	public TestDynamicGenerationExperiment(SimulationType type) {
		super("Test Dynamic Generation", 1);
		this.type = type;
	}

	@Override
	public SimulationEngine getSimulation(int ind) {
		TimeUnit unit = TimeUnit.MINUTE;
		SimulationObjectFactory factory = SimulationFactory.getInstance(type, ind, "Test Dynamic", unit, 0, 1);
		
		ResourceTypeEngine rt0 = factory.getResourceTypeInstance("RT0");
		ResourceTypeEngine rt1 = factory.getResourceTypeInstance("RT1");
		
		ResourceEngine r0 =  factory.getResourceInstance("Res0");
		r0.addTimeTableEntry(ModelPeriodicCycle.newDailyCycle(unit), 1, rt0);
		ResourceEngine r1 = factory.getResourceInstance("Res1");
		r1.addTimeTableEntry(ModelPeriodicCycle.newDailyCycle(unit), 1, rt1);
		
		WorkGroup wg0 = factory.getWorkGroupInstance(new ResourceTypeEngine [] {rt0, rt1}, new int[] {1,1});
		
		Condition cond = factory.getCustomizedConditionInstance(null, "false");
		ActivityFlow act0 = (ActivityFlow)factory.getFlowInstance("ActivityFlow", "ACT0");
		act0.addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", 10.0), 0, wg0, new NotCondition(cond));
		
		factory.getElementTypeInstance("ET0");
		factory.getFlowInstance("SingleFlow", act0);
		return factory.getSimulationEngine();
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
