package es.ull.iis.simulation.examples;

import java.util.ArrayList;

import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.NotCondition;
import es.ull.iis.simulation.factory.SimulationFactory;
import es.ull.iis.simulation.factory.SimulationUserCode;
import es.ull.iis.simulation.factory.UserMethod;
import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.SimulationWeeklyPeriodicCycle;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.MultiChoiceFlow;
import es.ull.iis.simulation.parallel.ParallelSimulationEngine;
import es.ull.iis.util.WeeklyPeriodicCycle;

class BarrelShippingExperiment extends Experiment {

	static final int NDAYS = 1;
	static final int NTHREADS = 2;
	
	public BarrelShippingExperiment() {
		super("Barrel Shipping Experiment", 1);
	}
	
	@Override
	public Simulation getSimulation(int ind) {
		SimulationFactory factory = new SimulationFactory(ind, "Barrel shipping", TimeUnit.MINUTE, 0, NDAYS * 24 * 60);
		Simulation simul = factory.getSimulation();
		
		// Declares global model variables
		simul.putVar("totalLiters", 0.0);
		simul.putVar("shipments", 0);

		ElementType etShipping = factory.getElementTypeInstance("etShipping");
		
		ResourceType rtOperator = factory.getResourceTypeInstance("rtOperator");
    	
		// Defines the resource timetables: Operators work only the weekdays, starting at 8 am 
		SimulationWeeklyPeriodicCycle resCycle = new SimulationWeeklyPeriodicCycle(simul.getTimeUnit(), WeeklyPeriodicCycle.WEEKDAYS, 480, 0);

		// Declares two operators who work 8 hours a day
		Resource operator1 = factory.getResourceInstance("Operator1");
		operator1.addTimeTableEntry(resCycle, 480, rtOperator);
		Resource operator2 = factory.getResourceInstance("Operator2");
		operator2.addTimeTableEntry(resCycle, 480, rtOperator);
		
		// Defines the needs of the activities in terms of resources
		WorkGroup wgOperator = factory.getWorkGroupInstance(new ResourceType [] {rtOperator}, new int[] {1});	

		// Defines the way the variables are updated when filling the barrels	
		SimulationUserCode userMethods = new SimulationUserCode();
		userMethods.add(UserMethod.AFTER_FINALIZE, 
				"if (<%GET(S.totalLiters)%> < <%GET(S.barrelCapacity)%>) {" +
					"double random = Math.random() * 50; " +
					"<%SET(S.totalLiters, <%GET(S.totalLiters)%> + random)%>;" +	
				"}");
			
		// Declares activities (Tasks)
		ActivityFlow actFilling = (ActivityFlow)factory.getFlowInstance("ActivityFlow", userMethods, "Barrel Filling", 0, false, false);
		// Defines the way the variables are updated when shipping the barrels
		userMethods.clear();
		userMethods.add(UserMethod.BEFORE_REQUEST, "<%SET(S.totalLiters, 0)%>;" +
					"<%SET(S.shipments, <%GET(S.shipments)%> + 1)%>;" +
					"return true;");

		ActivityFlow actShipping = (ActivityFlow)factory.getFlowInstance("ActivityFlow", userMethods, "Barrel Shipping", 0, false, false);

		// Declares variables for the Barrel Filling activity
		simul.putVar("barrelCapacity", 100);

		// Defines duration of activities
		actFilling.addWorkGroup(0, wgOperator, new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", 15.0));
		actShipping.addWorkGroup(0, wgOperator, new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", 20.0));

		// Defines loop conditions	
		Condition cond = factory.getCustomizedConditionInstance("", "<%GET(S.totalLiters)%> < <%GET(S.barrelCapacity)%>");
		NotCondition notCond = new NotCondition(cond);

		// Declares a MultiChoice node	
		MultiChoiceFlow mul1 = (MultiChoiceFlow) factory.getFlowInstance("MultiChoiceFlow");


		// Defines the workflow
		actFilling.link(mul1);
		ArrayList<Flow> succList = new ArrayList<Flow>();
		succList.add(actFilling);
		succList.add(actShipping);
		ArrayList<Condition> condList = new ArrayList<Condition>();
		condList.add(cond);
		condList.add(notCond);
		mul1.link(succList, condList);

		// Defines the way the processes are created
		SimulationWeeklyPeriodicCycle cGen = new SimulationWeeklyPeriodicCycle(simul.getTimeUnit(), WeeklyPeriodicCycle.WEEKDAYS, 0, NDAYS);
		factory.getTimeDrivenElementGeneratorInstance(TimeFunctionFactory.getInstance("ConstantVariate", 1.0), etShipping, actFilling, cGen);

		simul.addInfoReceiver(new StdInfoView());
		if (NTHREADS > 1)
			simul.setSimulationEngine(new ParallelSimulationEngine(ind, simul, NTHREADS));
		
    	return simul;
	}

}

public class BarrelShipping {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new BarrelShippingExperiment().start();
	}

}
