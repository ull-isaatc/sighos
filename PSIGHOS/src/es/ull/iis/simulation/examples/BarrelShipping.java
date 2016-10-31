package es.ull.iis.simulation.examples;

import java.util.ArrayList;
import java.util.EnumSet;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.NotCondition;
import es.ull.iis.simulation.core.ElementCreator;
import es.ull.iis.simulation.core.ElementType;
import es.ull.iis.simulation.core.Experiment;
import es.ull.iis.simulation.core.Resource;
import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.core.SimulationTimeFunction;
import es.ull.iis.simulation.core.SimulationWeeklyPeriodicCycle;
import es.ull.iis.simulation.core.TimeDrivenActivity;
import es.ull.iis.simulation.core.TimeStamp;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.Flow;
import es.ull.iis.simulation.core.flow.MultiChoiceFlow;
import es.ull.iis.simulation.core.flow.SingleFlow;
import es.ull.iis.simulation.factory.SimulationFactory;
import es.ull.iis.simulation.factory.SimulationObjectFactory;
import es.ull.iis.simulation.factory.SimulationUserCode;
import es.ull.iis.simulation.factory.UserMethod;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.util.WeeklyPeriodicCycle;

class BarrelShippingExperiment extends Experiment {

	static final int NDAYS = 1;
	static final int NTHREADS = 2;
	
	public BarrelShippingExperiment() {
		super("Barrel Shipping Experiment", 1);
	}
	
	@Override
	public Simulation getSimulation(int ind) {
		
		SimulationObjectFactory factory = SimulationFactory.getInstance(SimulationType.PARALLEL, ind, "Barrel shipping", TimeUnit.MINUTE, TimeStamp.getZero(), new TimeStamp(TimeUnit.DAY, NDAYS));
		Simulation sim = factory.getSimulation();
		
		// Declares global model variables
		sim.putVar("totalLiters", 0.0);
		sim.putVar("shipments", 0);

		ElementType etShipping = factory.getElementTypeInstance("etShipping");
		
		ResourceType rtOperator = factory.getResourceTypeInstance("rtOperator");
    	
		// Defines the resource timetables: Operators work only the weekdays, starting at 8 am 
		SimulationWeeklyPeriodicCycle resCycle = new SimulationWeeklyPeriodicCycle(sim.getTimeUnit(), WeeklyPeriodicCycle.WEEKDAYS, 480, 0);

		// Declares two operators who work 8 hours a day
		Resource operator1 = factory.getResourceInstance("Operator1");
		operator1.addTimeTableEntry(resCycle, 480, rtOperator);
		Resource operator2 = factory.getResourceInstance("Operator2");
		operator2.addTimeTableEntry(resCycle, 480, rtOperator);
		
		// Defines the needs of the activities in terms of resources
		WorkGroup wgOperator = factory.getWorkGroupInstance(new ResourceType [] {rtOperator}, new int[] {1});	

		// Declares activities (Tasks)
		TimeDrivenActivity actFilling = factory.getTimeDrivenActivityInstance("Barrel Filling", 0, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		TimeDrivenActivity actShipping = factory.getTimeDrivenActivityInstance("Barrel Shipping", 0, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));

		// Declares variables for the Barrel Filling activity
		actFilling.putVar("barrelCapacity", 100);

		// Defines duration of activities
		actFilling.addWorkGroup(new SimulationTimeFunction(sim.getTimeUnit(), "ConstantVariate", 15.0), 0, wgOperator);
		actShipping.addWorkGroup(new SimulationTimeFunction(sim.getTimeUnit(), "ConstantVariate", 20.0), 0, wgOperator);

		// Defines loop conditions	
		Condition cond = factory.getCustomizedConditionInstance("", "<%GET(S.totalLiters)%> < <%GET(A0.barrelCapacity)%>");
		NotCondition notCond = new NotCondition(cond);

		// Declares a MultiChoice node	
		MultiChoiceFlow mul1 = (MultiChoiceFlow) factory.getFlowInstance("MultiChoiceFlow");

		// Defines the way the variables are updated when filling the barrels	
		SimulationUserCode userMethods = new SimulationUserCode();
		userMethods.add(UserMethod.AFTER_FINALIZE, 
				"if (<%GET(S.totalLiters)%> < <%GET(A0.barrelCapacity)%>) {" +
					"double random = Math.random() * 50; " +
					"<%SET(S.totalLiters, <%GET(S.totalLiters)%> + random)%>;" +	
				"}");
		SingleFlow root = (SingleFlow) factory.getFlowInstance("SingleFlow", userMethods , actFilling);
			

		// Defines the way the variables are updated when shipping the barrels
		userMethods.clear();
		userMethods.add(UserMethod.BEFORE_REQUEST, "<%SET(S.totalLiters, 0)%>;" +
					"<%SET(S.shipments, <%GET(S.shipments)%> + 1)%>;" +
					"return true;");
		SingleFlow sin1 = (SingleFlow) factory.getFlowInstance("SingleFlow", userMethods, actShipping);

		// Defines the workflow
		root.link(mul1);
		ArrayList<Flow> succList = new ArrayList<Flow>();
		succList.add(root);
		succList.add(sin1);
		ArrayList<Condition> condList = new ArrayList<Condition>();
		condList.add(cond);
		condList.add(notCond);
		mul1.link(succList, condList);

		// Defines the way the processes are created
		SimulationWeeklyPeriodicCycle cGen = new SimulationWeeklyPeriodicCycle(sim.getTimeUnit(), WeeklyPeriodicCycle.WEEKDAYS, 0, NDAYS);
		ElementCreator ec = factory.getElementCreatorInstance(TimeFunctionFactory.getInstance("ConstantVariate", 1.0), etShipping, root);
		factory.getTimeDrivenGeneratorInstance(ec, cGen);

		sim.addInfoReceiver(new StdInfoView(sim));
		sim.setNThreads(NTHREADS);
		
    	return sim;
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
