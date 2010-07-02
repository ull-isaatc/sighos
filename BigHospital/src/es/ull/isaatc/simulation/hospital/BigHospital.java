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
import es.ull.isaatc.simulation.hospital.view.ActivityQueueFileSafeView;
import es.ull.isaatc.simulation.hospital.view.ExecutionCounterFileSafeView;
import es.ull.isaatc.simulation.test.BenchmarkListener;
import es.ull.isaatc.util.Output;

class BigHospitalExperiment extends Experiment {
	private static final int NEXP = 1;
	private static final TimeUnit unit = TimeUnit.MINUTE;
	private static final SimulationFactory.SimulationType simType = SimulationType.GROUPEDX;
	private static final TimeStamp warmUp = TimeStamp.getZero();
	private static final TimeStamp endTs = new TimeStamp(TimeUnit.MONTH, 12);
//	private static final TimeStamp endTs = new TimeStamp(TimeUnit.DAY, 1);
	private static final int threads = 4;
	private int debug = 0;
	private boolean test = false;

	public BigHospitalExperiment() {
		super("Big Hospital Experiments", NEXP);
	}
	
	private void createModel(SimulationObjectFactory factory) {
		final Simulation simul = factory.getSimulation();
		final TimeUnit unit = simul.getTimeUnit();
		
		// Central services
		ModelParameterMap centralParams = new ModelParameterMap(CentralServicesSubModel.Parameters.values().length);
		centralParams.put(CentralServicesSubModel.Parameters.NTECHNUC, test? 1:4);
		centralParams.put(CentralServicesSubModel.Parameters.NTECHRAD, test? 1:10);
		centralParams.put(CentralServicesSubModel.Parameters.LENGTH_NUCTEST, new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", 10));
		centralParams.put(CentralServicesSubModel.Parameters.LENGTH_RADTEST, new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", 10));
		centralParams.put(CentralServicesSubModel.Parameters.LENGTH_NUCANALYSIS, new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", 10));
		centralParams.put(CentralServicesSubModel.Parameters.LENGTH_RADANALYSIS, new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", 10));
		CentralServicesSubModel.createModel(factory, centralParams);
		// Central Lab
		ModelParameterMap centralLabParams = new ModelParameterMap(CentralLabSubModel.Parameters.values().length);
		centralLabParams.put(CentralLabSubModel.Parameters.NTECH, test? 1:23);
		centralLabParams.put(CentralLabSubModel.Parameters.N24HTECH, test? 1:5);
		centralLabParams.put(CentralLabSubModel.Parameters.NNURSES, test? 1:16);
		centralLabParams.put(CentralLabSubModel.Parameters.NXNURSES, test? 1:10);
		centralLabParams.put(CentralLabSubModel.Parameters.NSLOTS, test? 1:150);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_SAMPLE, new SimulationTimeFunction(unit, "ConstantVariate", 5));
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_CENT, new SimulationTimeFunction(unit, "UniformVariate", 10, 15));
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_ANALYSIS, new SimulationTimeFunction(unit, "ConstantVariate", 10));
		centralLabParams.put(CentralLabSubModel.Parameters.NHAETECH, test? 1:2);
		centralLabParams.put(CentralLabSubModel.Parameters.NHAENURSES, test? 1:5);
		centralLabParams.put(CentralLabSubModel.Parameters.NHAESLOTS, test? 1:40);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_HAEANALYSIS, new SimulationTimeFunction(unit, "ConstantVariate", 15));
		centralLabParams.put(CentralLabSubModel.Parameters.NMICROTECH, test? 1:10);
		centralLabParams.put(CentralLabSubModel.Parameters.NMICRONURSES, test? 0:0);
		centralLabParams.put(CentralLabSubModel.Parameters.NMICROSLOTS, test? 1:50);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_MICROANALYSIS, new SimulationTimeFunction(unit, "ConstantVariate", 20));
		centralLabParams.put(CentralLabSubModel.Parameters.NPATTECH, test? 1:6);
		centralLabParams.put(CentralLabSubModel.Parameters.NPATNURSES, test? 1:1);
		centralLabParams.put(CentralLabSubModel.Parameters.NPATSLOTS, test? 1:50);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_PATANALYSIS, new SimulationTimeFunction(unit, "ConstantVariate", 20));
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
		gynParams.put(StdSurgicalSubModel.Parameters.NSBEDS, test? 1:3);
		gynParams.put(StdSurgicalSubModel.Parameters.NSURGEONS, test? 1:4);
		gynParams.put(StdSurgicalSubModel.Parameters.NSURGERIES, test? 1:4);
		gynParams.put(StdSurgicalSubModel.Parameters.NDOCTORS, test? 1:7);
		gynParams.put(StdSurgicalSubModel.Parameters.NSCRUBNURSES, test? 1:4);
		gynParams.put(StdSurgicalSubModel.Parameters.NSURGERY_ASSIST, test? 1:2);		
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_NUC_OP, 0.1);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_RAD_OP, 0.01);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LAB_OP, 0.95);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABLAB_OP, 0.90);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABMIC_OP, 0.06);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABHAE_OP, 0.1);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABPAT_OP, 0.05);
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP1, new SimulationTimeFunction(unit, "ConstantVariate", 12));
		// Patients wait at least one day before being admitted after last appointment. Admissions happen at 18:00
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2ADM, HospitalModelTools.getNextHighFunction(unit, 
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 18), "ConstantVariate", TimeStamp.getDay())); 
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2OP, test ?
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.DAY, 1)) :
				new SimulationTimeFunction(unit, "UniformVariate", new TimeStamp(TimeUnit.WEEK, 8), new TimeStamp(TimeUnit.WEEK, 12)));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2, new SimulationTimeFunction(unit, "ConstantVariate", 10));
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_ADM, 0.5);
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_SUR, new SimulationTimeFunction(unit, "ConstantVariate", 90));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_SSUR, new SimulationTimeFunction(unit, "ConstantVariate", 30));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_ASUR, new SimulationTimeFunction(unit, "ConstantVariate", 20));
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
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_SPACU, test ?  
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.HOUR, 1)) :
				new SimulationTimeFunction(unit, "UniformVariate", 
				new TimeStamp(TimeUnit.HOUR, 1), new TimeStamp(TimeUnit.HOUR, 3)));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_APACU, test ?  
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 10)) :
				new SimulationTimeFunction(unit, "UniformVariate", 
				new TimeStamp(TimeUnit.MINUTE, 10), new TimeStamp(TimeUnit.MINUTE, 30)));
		// Patients wait at least one day after exiting, always at 12:00
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_SUR2EXIT, HospitalModelTools.getNextHighFunction(unit,
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 12), 
				"TriangleVariate", TimeStamp.getHour(), new TimeStamp(TimeUnit.DAY, 4), TimeStamp.getDay()));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_SSUR2EXIT, HospitalModelTools.getNextHighFunction(unit,
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 12), 
				"UniformVariate", TimeStamp.getHour(), TimeStamp.getDay()));
		gynParams.put(StdSurgicalSubModel.Parameters.NPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", test? 3:40));
		gynParams.put(StdSurgicalSubModel.Parameters.NSPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", test? 1:10));
		gynParams.put(StdSurgicalSubModel.Parameters.NAPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", test? 1:10));
		gynParams.put(StdSurgicalSubModel.Parameters.INTERARRIVAL, test ? 
				SimulationPeriodicCycle.newMonthlyCycle(unit, new TimeStamp(TimeUnit.MINUTE, 479)) :
				SimulationPeriodicCycle.newDailyCycle(unit, new TimeStamp(TimeUnit.MINUTE, 479)));
		gynParams.put(StdSurgicalSubModel.Parameters.SINTERARRIVAL, test ? 
				SimulationPeriodicCycle.newMonthlyCycle(unit, new TimeStamp(TimeUnit.MINUTE, 479)) :
				SimulationPeriodicCycle.newDailyCycle(unit, new TimeStamp(TimeUnit.MINUTE, 479)));
		gynParams.put(StdSurgicalSubModel.Parameters.AINTERARRIVAL, test ? 
				SimulationPeriodicCycle.newMonthlyCycle(unit, new TimeStamp(TimeUnit.MINUTE, 479)) :
				SimulationPeriodicCycle.newDailyCycle(unit, new TimeStamp(TimeUnit.MINUTE, 479)));
		gynParams.put(StdSurgicalSubModel.Parameters.ITERSUCC, test ?
				TimeFunctionFactory.getInstance("ConstantVariate", 1) :
				TimeFunctionFactory.getInstance("UniformVariate", 1, 5));
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_1ST_APP, 0.2);
		StdSurgicalSubModel.createModel(factory, "GYN", gynParams);

		// Rheumatology
		ModelParameterMap rheParams = new ModelParameterMap(StdMedicalSubModel.Parameters.values().length);
		rheParams.put(StdMedicalSubModel.Parameters.NDOCTORS, test? 1:5);
		rheParams.put(StdMedicalSubModel.Parameters.NBEDS, test? 1:5);
		rheParams.put(StdMedicalSubModel.Parameters.PROB_ADM, 0.5);
		rheParams.put(StdMedicalSubModel.Parameters.PROB_NUC_OP, 0.1);
		rheParams.put(StdMedicalSubModel.Parameters.PROB_RAD_OP, 0.15);
		rheParams.put(StdMedicalSubModel.Parameters.PROB_LAB_OP, 0.95);
		rheParams.put(StdMedicalSubModel.Parameters.PROB_LABLAB_OP, 0.90);
		rheParams.put(StdMedicalSubModel.Parameters.PROB_LABMIC_OP, 0.06);
		rheParams.put(StdMedicalSubModel.Parameters.PROB_LABHAE_OP, 0.07);
		rheParams.put(StdMedicalSubModel.Parameters.PROB_LABPAT_OP, 0.05);
		rheParams.put(StdMedicalSubModel.Parameters.PROB_NUC_IP, 0.01);
		rheParams.put(StdMedicalSubModel.Parameters.PROB_RAD_IP, 0.01);
		rheParams.put(StdMedicalSubModel.Parameters.PROB_LAB_IP, 0.5);
		rheParams.put(StdMedicalSubModel.Parameters.PROB_LABLAB_IP, 0.90);
		rheParams.put(StdMedicalSubModel.Parameters.PROB_LABMIC_IP, 0.06);
		rheParams.put(StdMedicalSubModel.Parameters.PROB_LABHAE_IP, 0.07);
		rheParams.put(StdMedicalSubModel.Parameters.PROB_LABPAT_IP, 0.05);
		rheParams.put(StdMedicalSubModel.Parameters.LENGTH_OP2ADM, HospitalModelTools.getNextHighFunction(unit, 
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 18), "ConstantVariate", TimeStamp.getDay())); 
		rheParams.put(StdMedicalSubModel.Parameters.LOS, HospitalModelTools.getNextHighFunction(unit,
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 12), 
				"TriangleVariate", TimeStamp.getHour(), new TimeStamp(TimeUnit.DAY, 7), new TimeStamp(TimeUnit.DAY, 2)));
		rheParams.put(StdMedicalSubModel.Parameters.LENGTH_OP1, new SimulationTimeFunction(unit, "ConstantVariate", 12));
		rheParams.put(StdMedicalSubModel.Parameters.LENGTH_OP2OP, test ?
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.DAY, 1)) :
				new SimulationTimeFunction(unit, "UniformVariate", new TimeStamp(TimeUnit.WEEK, 8), new TimeStamp(TimeUnit.WEEK, 12)));
		rheParams.put(StdMedicalSubModel.Parameters.LENGTH_OP2, new SimulationTimeFunction(unit, "ConstantVariate", 10));
		rheParams.put(StdMedicalSubModel.Parameters.NPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", test? 3:50));
		rheParams.put(StdMedicalSubModel.Parameters.INTERARRIVAL, test ? 
				SimulationPeriodicCycle.newMonthlyCycle(unit, new TimeStamp(TimeUnit.MINUTE, 479)) :
				SimulationPeriodicCycle.newDailyCycle(unit, new TimeStamp(TimeUnit.MINUTE, 479)));
		rheParams.put(StdMedicalSubModel.Parameters.ITERSUCC, test ?
				TimeFunctionFactory.getInstance("ConstantVariate", 1) :
				TimeFunctionFactory.getInstance("UniformVariate", 1, 5));
		rheParams.put(StdMedicalSubModel.Parameters.PROB_1ST_APP, 0.2);
		StdMedicalSubModel.createModel(factory, "RHE", rheParams);
	}
	
	@Override
	public Simulation getSimulation(int ind) {
		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, ind, "Big Hospital", unit, TimeStamp.getZero(), endTs);
		createModel(factory);

		Simulation simul = factory.getSimulation();		
		simul.setNThreads(threads);
		simul.addInfoReceiver(new ActivityQueueFileSafeView(simul, "C:\\queue.txt", TimeStamp.getWeek()));
		simul.addInfoReceiver(new ExecutionCounterFileSafeView(simul, "C:\\total.txt", warmUp, TimeStamp.getWeek()));
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
