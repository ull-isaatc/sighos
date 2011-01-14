package es.ull.isaatc.simulation.test;

import es.ull.isaatc.simulation.core.PooledExperiment;
import es.ull.isaatc.simulation.core.ResourceType;
import es.ull.isaatc.simulation.core.Simulation;
import es.ull.isaatc.simulation.core.SimulationWeeklyPeriodicCycle;
import es.ull.isaatc.util.WeeklyPeriodicCycle;
import es.ull.isaatc.simulation.core.TimeUnit;
import es.ull.isaatc.simulation.factory.SimulationFactory;
import es.ull.isaatc.simulation.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.parallel.ElementType;
import es.ull.isaatc.simulation.parallel.Resource;

public class BarrelShipping extends PooledExperiment {

	@Override
	public Simulation getSimulation(int ind) {
		
		SimulationObjectFactory factory = SimulationFactory.getInstance(SimulationType.PARALLEL, ind, "Barrel shipping", TimeUnit.MINUTE, 0, 200);
		Simulation sim = factory.getSimulation();
		
		ElementType etShipping = (ElementType) factory.getElementTypeInstance("etShipping");
		
		ResourceType rtOperator = (ResourceType) factory.getResourceTypeInstance("rtOperator");
    	
		// Defines the resource timetables: Operators work only the weekdays, starting at 8 am 
		SimulationWeeklyPeriodicCycle resCycle = new SimulationWeeklyPeriodicCycle(sim.getTimeUnit(), WeeklyPeriodicCycle.WEEKDAYS, 480, 0);

		// Declares two operators who work 7 hours a day
		Resource operator1 = (Resource) factory.getResourceInstance("Operator1");
		operator1.addTimeTableEntry(resCycle, 420, rtOperator);
		Resource operator2 = (Resource) factory.getResourceInstance("Operator2");
		operator2.addTimeTableEntry(resCycle, 420, rtOperator);
		

    	
		return null;
	}

}
