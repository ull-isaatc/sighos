/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.Experiment;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.common.SimulationTimeFunction;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.factory.SimulationFactory;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.inforeceiver.StdInfoView;
import es.ull.isaatc.simulation.hospital.view.ActivityQueueFileView;
import es.ull.isaatc.simulation.test.BenchmarkListener;
import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.DiscreteCycleIterator;
import es.ull.isaatc.util.Output;

class BigHospitalExperiment extends Experiment {
	private static final int NEXP = 1;
	private static final TimeUnit unit = TimeUnit.MINUTE;
	private static final SimulationFactory.SimulationType simType = SimulationType.SEQUENTIAL;
	private static final TimeStamp warmup = new TimeStamp(TimeUnit.MONTH, 1);
	private static final TimeStamp endTs = new TimeStamp(TimeUnit.MONTH, 6);
//	private static final TimeStamp endTs = new TimeStamp(TimeUnit.DAY, 1);
	private static final int threads = 2;
	private int debug = 1;
	private boolean test = true;

	public BigHospitalExperiment() {
		super("Big Hospital Experiments", NEXP);
	}
	
	private void createModel(SimulationObjectFactory factory) {
		final Simulation simul = factory.getSimulation();
		final TimeUnit unit = simul.getTimeUnit();
		
		// Central services
		ModelParameterMap centralParams = new ModelParameterMap(CentralServicesSubModel.Parameters.values().length);
		centralParams.put(CentralServicesSubModel.Parameters.RESCAR, test? 1:2);
		centralParams.put(CentralServicesSubModel.Parameters.RESHAE, test? 1:2);
		centralParams.put(CentralServicesSubModel.Parameters.RESRAD, test? 1:2);
		CentralServicesSubModel.createModel(factory, centralParams);
		// Central Lab
		ModelParameterMap centralLabParams = new ModelParameterMap(CentralLabSubModel.Parameters.values().length);
		centralLabParams.put(CentralLabSubModel.Parameters.NTECH, test? 1:15);
		centralLabParams.put(CentralLabSubModel.Parameters.NCENT, test? 1:(4 * 48));
		centralLabParams.put(CentralLabSubModel.Parameters.NNURSES, test? 1:10);
		centralLabParams.put(CentralLabSubModel.Parameters.NSLOTS, test? 1:100);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_SAMPLE, new SimulationTimeFunction(unit, "ConstantVariate", 5));
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_CENT, new SimulationTimeFunction(unit, "UniformVariate", 10, 15));
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_CHECK, new SimulationTimeFunction(unit, "ConstantVariate", 10));
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_ANALYSIS, new SimulationTimeFunction(unit, "ConstantVariate", 45));
		CentralLabSubModel.createModel(factory, centralLabParams);
		// Surgical common services
		ModelParameterMap surParams = new ModelParameterMap(SurgicalSubModel.Parameters.values().length);
		surParams.put(SurgicalSubModel.Parameters.NBEDS_ICU, test? 1:15);
		surParams.put(SurgicalSubModel.Parameters.NBEDS_PACU, test? 1:10);
		surParams.put(SurgicalSubModel.Parameters.NANAESTHETISTS, test? 1:4);
		SurgicalSubModel.createModel(factory, surParams);
		
		// Gynaegology
		ModelParameterMap gynParams = new ModelParameterMap(StdSurgicalSubModel.Parameters.values().length);
		gynParams.put(StdSurgicalSubModel.Parameters.NBEDS, test? 1:15);
		gynParams.put(StdSurgicalSubModel.Parameters.NSURGEONS, test? 1:4);
		gynParams.put(StdSurgicalSubModel.Parameters.NSURGERIES, test? 1:4);
		gynParams.put(StdSurgicalSubModel.Parameters.NDOCTORS, test? 1:5);
		gynParams.put(StdSurgicalSubModel.Parameters.NSCRUBNURSES, test? 1:4);
		gynParams.put(StdSurgicalSubModel.Parameters.NSURGERY_ASSIST, test? 1:2);		
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP1, new SimulationTimeFunction(unit, "ConstantVariate", 12));
		// Patients wait at least one day before being admitted after last appointment. Admissions happen at 18:00
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2SUR, HospitalModelTools.getNextHighFunction(unit, 
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 18), "ConstantVariate", TimeStamp.getDay())); 
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2OP, test ?
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.DAY, 1)) :
				new SimulationTimeFunction(unit, "UniformVariate", new TimeStamp(TimeUnit.WEEK, 8), new TimeStamp(TimeUnit.WEEK, 12)));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2, new SimulationTimeFunction(unit, "ConstantVariate", 10));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_SUR, new SimulationTimeFunction(unit, "ConstantVariate", 30));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_POP, new SimulationTimeFunction(unit, "ConstantVariate", 10));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_SUR2POP, test ? 
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.DAY, 1)) :
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.DAY, 20)));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_ICU, test ? 
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.HOUR, 2)) :
				new SimulationTimeFunction(unit, "TriangleVariate", 
				new TimeStamp(TimeUnit.HOUR, 2), new TimeStamp(TimeUnit.WEEK, 2), new TimeStamp(TimeUnit.DAY, 3)));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_PACU, test ?  
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.HOUR, 1)) :
				new SimulationTimeFunction(unit, "TriangleVariate", 
				new TimeStamp(TimeUnit.HOUR, 1), new TimeStamp(TimeUnit.DAY, 3), new TimeStamp(TimeUnit.HOUR, 7)));
		// Patients wait at least one day after exiting, always at 12:00
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_SUR2EXIT, HospitalModelTools.getNextHighFunction(unit,
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 12), 
				"TriangleVariate", TimeStamp.getHour(), new TimeStamp(TimeUnit.DAY, 3), TimeStamp.getDay()));
		gynParams.put(StdSurgicalSubModel.Parameters.NPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", test? 1:50));
		gynParams.put(StdSurgicalSubModel.Parameters.INTERARRIVAL, test ? 
				SimulationPeriodicCycle.newMonthlyCycle(unit, new TimeStamp(TimeUnit.MINUTE, 479)) :
				SimulationPeriodicCycle.newDailyCycle(unit, new TimeStamp(TimeUnit.MINUTE, 479)));
		gynParams.put(StdSurgicalSubModel.Parameters.ITERSUCC, test ?
				TimeFunctionFactory.getInstance("ConstantVariate", 1) :
				TimeFunctionFactory.getInstance("UniformVariate", 1, 5));
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_1ST_APP, 0.2);
		StdSurgicalSubModel.createModel(factory, "GYN", gynParams);
	}
	
	@Override
	public Simulation getSimulation(int ind) {
		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, ind, "Big Hospital", unit, TimeStamp.getZero(), endTs);
//		createModel(factory);

		Simulation simul = factory.getSimulation();		
		SimulationPeriodicCycle cc = SimulationPeriodicCycle.newDailyCycle(simul.getTimeUnit(), new TimeStamp(TimeUnit.HOUR, 2));
		DiscreteCycleIterator iter = cc.getCycle().iterator(0, 2880);
		for (int i = 0; i < 10; i++)
			System.out.println(iter.next() + "\t");
//		factory.getResourceInstance("TT1").addTimeTableEntry(
//				//HospitalModelTools.getStdHumanResourceCycle(simul),
//				SimulationPeriodicCycle.newDailyCycle(simul.getTimeUnit(), new TimeStamp(TimeUnit.HOUR, 2)),
//				HospitalModelTools.getStdHumanResourceAvailability(simul), 
//				factory.getResourceTypeInstance("TT"));
		simul.setNThreads(threads);
		simul.addInfoReceiver(new ActivityQueueFileView(simul, "C:\\queue.txt", TimeStamp.getWeek()));
		simul.addInfoReceiver(new BenchmarkListener(simul, System.out));
		if (debug == 1)
			simul.addInfoReceiver(new StdInfoView(simul));
		else if (debug == 2)
			simul.setOutput(new Output(true));
		return simul;
	}

	@Override
	public void start() {
		for (int i = 0; i < nExperiments; i++)
			getSimulation(i).run();
		end();		
	}
	
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class BigHospital {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new BigHospitalExperiment().start();
	}

}
