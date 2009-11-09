/**
 * 
 */
package es.ull.isaatc.simulation.test;

import java.util.ArrayList;

import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.ModelPeriodicCycle;
import es.ull.isaatc.simulation.common.ModelTimeFunction;
import es.ull.isaatc.simulation.common.PooledExperiment;
import es.ull.isaatc.simulation.common.Resource;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.condition.Condition;
import es.ull.isaatc.simulation.common.condition.NotCondition;
import es.ull.isaatc.simulation.common.factory.SimulationFactory;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.factory.SimulationUserCode;
import es.ull.isaatc.simulation.common.factory.UserMethod;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.SingleFlow;

class TestDynamicGenerationExperiment extends PooledExperiment {
	SimulationType type;
	
	public TestDynamicGenerationExperiment(SimulationType type) {
		super("Test Dynamic Generation", 1);
		this.type = type;
	}

	@Override
	public Simulation getSimulation(int ind) {
		TimeUnit unit = TimeUnit.MINUTE;
		SimulationObjectFactory factory = SimulationFactory.getInstance(type, ind, "Test Dynamic", unit, 0.0, 1.0);
		
		ResourceType rt0 = factory.getResourceTypeInstance(0, "RT0");
		ResourceType rt1 = factory.getResourceTypeInstance(1, "RT1");
		
		Resource r0 =  factory.getResourceInstance(0, "Res0");
		r0.addTimeTableEntry(ModelPeriodicCycle.newDailyCycle(unit), 1, rt0);
		Resource r1 = factory.getResourceInstance(1, "Res1");
		r1.addTimeTableEntry(ModelPeriodicCycle.newDailyCycle(unit), 1, rt1);
		
		WorkGroup wg0 = factory.getWorkGroupInstance(0, new ResourceType [] {rt0, rt1}, new int[] {1,1});
		
		Condition cond = factory.getCustomizedConditionInstance(0, null, "false");
		TimeDrivenActivity act0 = factory.getTimeDrivenActivityInstance(0, "ACT0");
		act0.addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", 10.0), wg0, new NotCondition(cond));
		
		ElementType et0 = factory.getElementTypeInstance(0, "ET0");
		SingleFlow sf0 = (SingleFlow)factory.getFlowInstance(0, "SingleFlow", act0);
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
