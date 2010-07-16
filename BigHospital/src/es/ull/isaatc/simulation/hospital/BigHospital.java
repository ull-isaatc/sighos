/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import simkit.random.RandomNumberFactory;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.Experiment;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.common.SimulationTimeFunction;
import es.ull.isaatc.simulation.common.SimulationWeeklyPeriodicCycle;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.factory.SimulationFactory;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.inforeceiver.StdInfoView;
import es.ull.isaatc.simulation.hospital.view.ActionsCounterView;
import es.ull.isaatc.simulation.hospital.view.ActivityLengthFileSafeView;
import es.ull.isaatc.simulation.hospital.view.ActivityQueueFileSafeView;
import es.ull.isaatc.simulation.hospital.view.ExecutionCounterFileSafeView;
import es.ull.isaatc.simulation.hospital.view.NurseUsageFileSafeView;
import es.ull.isaatc.simulation.hospital.view.PeriodStdInfoFileSafeView;
import es.ull.isaatc.simulation.hospital.view.ResourceUsageFileSafeView;
import es.ull.isaatc.simulation.hospital.view.SimultaneousEventFileSafeView;
import es.ull.isaatc.simulation.test.BenchmarkListener;
import es.ull.isaatc.util.Output;
import es.ull.isaatc.util.WeeklyPeriodicCycle;

class BigHospitalExperiment extends Experiment {
	private static final int NEXP = 1;
	private static final TimeUnit unit = TimeUnit.MINUTE;
	private static final SimulationFactory.SimulationType simType = SimulationType.GROUPED3PHASEX;
	private static final TimeStamp warmUp = TimeStamp.getZero();
	private static final TimeStamp endTs = new TimeStamp(TimeUnit.MONTH, 6);
//	private static final TimeStamp endTs = new TimeStamp(TimeUnit.DAY, 1);
	private static final TimeStamp scale = new TimeStamp(TimeUnit.MINUTE, 5);
	private static final TimeStamp viewPeriod = new TimeStamp(TimeUnit.WEEK, 1);
	/** Patients arrive 10 minutes before 8 am */
	private static final int PATIENTARRIVAL = 470;
	private static final int threads = 4;
	private int debug = 0;
	private boolean test = false;

	public BigHospitalExperiment() {
		super("Big Hospital Experiments", NEXP);
	}

	private void addViews(Simulation simul) {
		simul.setNThreads(threads);
//		simul.addInfoReceiver(new ActivityQueueFileSafeView(simul, "C:\\queue.txt", viewPeriod));
//		simul.addInfoReceiver(new ExecutionCounterFileSafeView(simul, "C:\\total.txt", warmUp, viewPeriod));
//		simul.addInfoReceiver(new ActivityLengthFileSafeView(simul, "C:\\act.txt", warmUp));
//		simul.addInfoReceiver(new NurseUsageFileSafeView(simul, "C:\\outTime.txt"));
//		simul.addInfoReceiver(new SimultaneousEventFileSafeView(simul, "C:\\events2.txt"));
//		simul.addInfoReceiver(new ResourceUsageFileSafeView(simul, "C:\\res.txt", viewPeriod));
		simul.addInfoReceiver(new ActionsCounterView(simul, System.out));
//		simul.addInfoReceiver(new PeriodStdInfoFileSafeView(simul, new TimeStamp(TimeUnit.DAY, 280), new TimeStamp(TimeUnit.DAY, 281), "C:\\trace.txt"));
//		simul.addInfoReceiver(new PeriodStdInfoFileSafeView(simul, TimeStamp.getZero(), endTs, "C:\\trace.txt"));
		if (debug == 1)
			simul.addInfoReceiver(new StdInfoView(simul));
		else if (debug == 2)
			simul.setOutput(new Output(true));		
	}
	
	private void createSmallModel(SimulationObjectFactory factory) {
		final Simulation simul = factory.getSimulation();
		final TimeUnit unit = simul.getTimeUnit();
		HospitalModelTools.setScale(scale);
		
		// Central services
		ModelParameterMap centralParams = new ModelParameterMap(CentralServicesSubModel.Parameters.values().length);
		centralParams.put(CentralServicesSubModel.Parameters.NTECHNUC, 1);
		centralParams.put(CentralServicesSubModel.Parameters.NTECHRAD, 2);
		centralParams.put(CentralServicesSubModel.Parameters.LENGTH_NUCTEST, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"UniformVariate", 4, 12));
		centralParams.put(CentralServicesSubModel.Parameters.LENGTH_RADTEST, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"UniformVariate", 4, 12));
		centralParams.put(CentralServicesSubModel.Parameters.LENGTH_NUCANALYSIS, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"UniformVariate", 8, 16));
		centralParams.put(CentralServicesSubModel.Parameters.LENGTH_RADANALYSIS, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"UniformVariate", 7, 14));
		CentralServicesSubModel.createModel(factory, centralParams);
		// Central Lab
		ModelParameterMap centralLabParams = new ModelParameterMap(CentralLabSubModel.Parameters.values().length);
		centralLabParams.put(CentralLabSubModel.Parameters.NTECH, 5);
		centralLabParams.put(CentralLabSubModel.Parameters.N24HTECH, 2);
		centralLabParams.put(CentralLabSubModel.Parameters.NNURSES, 5);
		centralLabParams.put(CentralLabSubModel.Parameters.NXNURSES, 2);
		centralLabParams.put(CentralLabSubModel.Parameters.NSLOTS, 20);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_SAMPLE, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 6));
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_CENT, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 10));
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_ANALYSIS, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 8));
		centralLabParams.put(CentralLabSubModel.Parameters.NHAETECH, 1);
		centralLabParams.put(CentralLabSubModel.Parameters.NHAENURSES, 2);
		centralLabParams.put(CentralLabSubModel.Parameters.NHAESLOTS, 20);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_HAEANALYSIS, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 15));
		centralLabParams.put(CentralLabSubModel.Parameters.NMICROTECH, 2);
		centralLabParams.put(CentralLabSubModel.Parameters.NMICRONURSES, 0);
		centralLabParams.put(CentralLabSubModel.Parameters.NMICROSLOTS, 20);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_MICROANALYSIS, HospitalModelTools.getScaledSimulationTimeFunction(unit,
				"ConstantVariate", 20));
		centralLabParams.put(CentralLabSubModel.Parameters.NPATTECH, 3);
		centralLabParams.put(CentralLabSubModel.Parameters.NPATNURSES, 1);
		centralLabParams.put(CentralLabSubModel.Parameters.NPATSLOTS, 30);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_PATANALYSIS, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 20));
		CentralLabSubModel.createModel(factory, centralLabParams);
		
		// Gynaegology
		ModelParameterMap gynParams = new ModelParameterMap(StdSurgicalSubModel.Parameters.values().length);
		gynParams.put(StdSurgicalSubModel.Parameters.NDOCTORS, 2);
//		gynParams.put(StdSurgicalSubModel.Parameters.PROB_NUC_OP, 0.0);
//		gynParams.put(StdSurgicalSubModel.Parameters.PROB_RAD_OP, 0.0);
//		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LAB_OP, 1.0);
//		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABLAB_OP, 1.0);
//		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABMIC_OP, 0.0);
//		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABHAE_OP, 0.0);
//		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABPAT_OP, 0.0);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_NUC_OP, 0.1);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_RAD_OP, 0.01);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LAB_OP, 0.95);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABLAB_OP, 0.90);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABMIC_OP, 0.06);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABHAE_OP, 0.1);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABPAT_OP, 0.05);
		// First OP Appointments takes up to the next quarter
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP1, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 12))); 
		// Patients wait at least one day before being admitted after last appointment. Admissions happen at 18:00
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2ADM, HospitalModelTools.getNextHighFunction(unit, 
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 18), "ConstantVariate", TimeStamp.getMinute()));
		// Next Appointment is at 8 am of several days later
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2OP, 
				HospitalModelTools.getNextHighFunction(unit, TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 8), 
						"ConstantVariate", new TimeStamp(TimeUnit.WEEK, 1)));
		// Subsequent OP Appointments always ends at 5 minute multiples
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 5))); 
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_ADM, 1.0);
		// Patients wait at least one day after exiting, always at 12:00
		gynParams.put(StdSurgicalSubModel.Parameters.NPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", 40));
		gynParams.put(StdSurgicalSubModel.Parameters.INTERARRIVAL, 
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL), 0));
		gynParams.put(StdSurgicalSubModel.Parameters.ITERSUCC, 
				TimeFunctionFactory.getInstance("ConstantVariate", 3));
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_1ST_APP, 0.2);
		StdSurgicalSubModel.createSmallModel(factory, "GYN", gynParams);
		StdSurgicalSubModel.createSmallModel(factory, "TRA", gynParams);
//		StdSurgicalSubModel.createSmallModel(factory, "NEU", gynParams);
//		StdSurgicalSubModel.createSmallModel(factory, "NEPH", gynParams);
	}
	
	private void createDeterministicModel(SimulationObjectFactory factory) {
		final Simulation simul = factory.getSimulation();
		final TimeUnit unit = simul.getTimeUnit();
		HospitalModelTools.setScale(scale);
		
		// Central services
		ModelParameterMap centralParams = new ModelParameterMap(CentralServicesSubModel.Parameters.values().length);
		centralParams.put(CentralServicesSubModel.Parameters.NTECHNUC, test? 1:4);
		centralParams.put(CentralServicesSubModel.Parameters.NTECHRAD, test? 1:10);
		centralParams.put(CentralServicesSubModel.Parameters.LENGTH_NUCTEST, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 10));
		centralParams.put(CentralServicesSubModel.Parameters.LENGTH_RADTEST, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 10));
		centralParams.put(CentralServicesSubModel.Parameters.LENGTH_NUCANALYSIS, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 12));
		centralParams.put(CentralServicesSubModel.Parameters.LENGTH_RADANALYSIS, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 10));
		CentralServicesSubModel.createModel(factory, centralParams);
		// Central Lab
		ModelParameterMap centralLabParams = new ModelParameterMap(CentralLabSubModel.Parameters.values().length);
		centralLabParams.put(CentralLabSubModel.Parameters.NTECH, test? 1:23);
		centralLabParams.put(CentralLabSubModel.Parameters.N24HTECH, test? 1:5);
		centralLabParams.put(CentralLabSubModel.Parameters.NNURSES, test? 1:16);
		centralLabParams.put(CentralLabSubModel.Parameters.NXNURSES, test? 1:10);
		centralLabParams.put(CentralLabSubModel.Parameters.NSLOTS, test? 1:150);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_SAMPLE, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 6));
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_CENT, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 10));
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_ANALYSIS, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 8));
		centralLabParams.put(CentralLabSubModel.Parameters.NHAETECH, 2);
		centralLabParams.put(CentralLabSubModel.Parameters.NHAENURSES, 5);
		centralLabParams.put(CentralLabSubModel.Parameters.NHAESLOTS, 40);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_HAEANALYSIS, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 15));
		centralLabParams.put(CentralLabSubModel.Parameters.NMICROTECH, 10);
		centralLabParams.put(CentralLabSubModel.Parameters.NMICRONURSES, 0);
		centralLabParams.put(CentralLabSubModel.Parameters.NMICROSLOTS, 50);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_MICROANALYSIS, HospitalModelTools.getScaledSimulationTimeFunction(unit,
				"ConstantVariate", 20));
		centralLabParams.put(CentralLabSubModel.Parameters.NPATTECH, 6);
		centralLabParams.put(CentralLabSubModel.Parameters.NPATNURSES, 1);
		centralLabParams.put(CentralLabSubModel.Parameters.NPATSLOTS, 50);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_PATANALYSIS, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 20));
		CentralLabSubModel.createModel(factory, centralLabParams);
		
		// Gynaegology
		ModelParameterMap gynParams = new ModelParameterMap(StdSurgicalSubModel.Parameters.values().length);
		gynParams.put(StdSurgicalSubModel.Parameters.NDOCTORS, 7);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_NUC_OP, 0.1);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_RAD_OP, 0.01);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LAB_OP, 0.95);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABLAB_OP, 0.90);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABMIC_OP, 0.06);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABHAE_OP, 0.1);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABPAT_OP, 0.05);
//		gynParams.put(StdSurgicalSubModel.Parameters.PROB_NUC_OP, 0.0);
//		gynParams.put(StdSurgicalSubModel.Parameters.PROB_RAD_OP, 0.0);
//		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LAB_OP, 1.0);
//		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABLAB_OP, 1.0);
//		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABMIC_OP, 0.0);
//		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABHAE_OP, 0.0);
//		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABPAT_OP, 0.0);
		// First OP Appointments takes up to the next quarter
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP1, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 12))); 
		// Patients wait at least one day before being admitted after last appointment. Admissions happen at 18:00
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2ADM, HospitalModelTools.getNextHighFunction(unit, 
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 18), "ConstantVariate", TimeStamp.getMinute()));
		// Next Appointment is at 8 am of several days later
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2OP, 
				HospitalModelTools.getNextHighFunction(unit, TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 8), 
						"ConstantVariate", new TimeStamp(TimeUnit.WEEK, 1)));
		// Subsequent OP Appointments always ends at 5 minute multiples
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 5))); 
		gynParams.put(StdSurgicalSubModel.Parameters.NPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", 40));
		gynParams.put(StdSurgicalSubModel.Parameters.INTERARRIVAL, 
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL), 0));
		gynParams.put(StdSurgicalSubModel.Parameters.ITERSUCC, 
				TimeFunctionFactory.getInstance("ConstantVariate", 3));
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_1ST_APP, 0.2);
		StdSurgicalSubModel.createDeterministicModel(factory, "GYN", gynParams);
		StdSurgicalSubModel.createDeterministicModel(factory, "TRA", gynParams);
		StdSurgicalSubModel.createDeterministicModel(factory, "NEU", gynParams);
		StdSurgicalSubModel.createDeterministicModel(factory, "NEPH", gynParams);
	}
	
	private void createModel(SimulationObjectFactory factory) {
		final Simulation simul = factory.getSimulation();
		final TimeUnit unit = simul.getTimeUnit();
		HospitalModelTools.setScale(scale);
		
		// Central services
		ModelParameterMap centralParams = new ModelParameterMap(CentralServicesSubModel.Parameters.values().length);
		centralParams.put(CentralServicesSubModel.Parameters.NTECHNUC, test? 1:4);
		centralParams.put(CentralServicesSubModel.Parameters.NTECHRAD, test? 1:10);
		centralParams.put(CentralServicesSubModel.Parameters.LENGTH_NUCTEST, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"UniformVariate", 4, 12));
		centralParams.put(CentralServicesSubModel.Parameters.LENGTH_RADTEST, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"UniformVariate", 4, 12));
		centralParams.put(CentralServicesSubModel.Parameters.LENGTH_NUCANALYSIS, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"UniformVariate", 8, 16));
		centralParams.put(CentralServicesSubModel.Parameters.LENGTH_RADANALYSIS, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"UniformVariate", 7, 14));
		CentralServicesSubModel.createModel(factory, centralParams);
		// Central Lab
		ModelParameterMap centralLabParams = new ModelParameterMap(CentralLabSubModel.Parameters.values().length);
		centralLabParams.put(CentralLabSubModel.Parameters.NTECH, test? 1:23);
		centralLabParams.put(CentralLabSubModel.Parameters.N24HTECH, test? 1:5);
		centralLabParams.put(CentralLabSubModel.Parameters.NNURSES, test? 1:16);
		centralLabParams.put(CentralLabSubModel.Parameters.NXNURSES, test? 1:10);
		centralLabParams.put(CentralLabSubModel.Parameters.NSLOTS, test? 1:150);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_SAMPLE, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"UniformVariate", 4, 9));
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_CENT, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"UniformVariate", 8, 15));
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_ANALYSIS, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 8));
		centralLabParams.put(CentralLabSubModel.Parameters.NHAETECH, test? 1:2);
		centralLabParams.put(CentralLabSubModel.Parameters.NHAENURSES, test? 1:5);
		centralLabParams.put(CentralLabSubModel.Parameters.NHAESLOTS, test? 1:40);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_HAEANALYSIS, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 15));
		centralLabParams.put(CentralLabSubModel.Parameters.NMICROTECH, test? 1:10);
		centralLabParams.put(CentralLabSubModel.Parameters.NMICRONURSES, test? 0:0);
		centralLabParams.put(CentralLabSubModel.Parameters.NMICROSLOTS, test? 1:50);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_MICROANALYSIS, HospitalModelTools.getScaledSimulationTimeFunction(unit,
				"ConstantVariate", 20));
		centralLabParams.put(CentralLabSubModel.Parameters.NPATTECH, test? 1:6);
		centralLabParams.put(CentralLabSubModel.Parameters.NPATNURSES, test? 1:1);
		centralLabParams.put(CentralLabSubModel.Parameters.NPATSLOTS, test? 1:50);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_PATANALYSIS, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 20));
		CentralLabSubModel.createModel(factory, centralLabParams);
		// Surgical common services
		ModelParameterMap surParams = new ModelParameterMap(SurgicalSubModel.Parameters.values().length);
		surParams.put(SurgicalSubModel.Parameters.NBEDS_ICU, test? 1:15);
		surParams.put(SurgicalSubModel.Parameters.NBEDS_PACU, test? 1:10);
		surParams.put(SurgicalSubModel.Parameters.NANAESTHETISTS, test? 1:4);
		SurgicalSubModel.createModel(factory, surParams);
		
		// Gynaegology
		ModelParameterMap gynParams = new ModelParameterMap(StdSurgicalSubModel.Parameters.values().length);
		gynParams.put(StdSurgicalSubModel.Parameters.NBEDS, test? 1:20);
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
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_NUC_IP, 0.01);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_RAD_IP, 0.01);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LAB_IP, 0.5);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABLAB_IP, 0.90);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABMIC_IP, 0.06);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABHAE_IP, 0.1);
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_LABPAT_IP, 0.05);
		// First OP Appointments takes up to the next quarter
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP1, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 12))); 
		// Patients wait at least one day before being admitted after last appointment. Admissions happen at 18:00
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2ADM, HospitalModelTools.getNextHighFunction(unit, 
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 18), "ConstantVariate", TimeStamp.getMinute()));
		// Next Appointment is at 8 am of several days later
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2OP, test ?
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.DAY, 1)) :
				HospitalModelTools.getNextHighFunction(unit, TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 8), 
						"UniformVariate", new TimeStamp(TimeUnit.WEEK, 8), new TimeStamp(TimeUnit.WEEK, 12)));
		// Subsequent OP Appointments always ends at 5 minute multiples
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 5))); 
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_ADM, 0.5);
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_SUR, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 90)));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_SSUR, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 30)));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_ASUR, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 20)));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_POP, HospitalModelTools.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 8)));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_SUR2POP, test ? 
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.DAY, 1)) :
				HospitalModelTools.getNextHighFunction(unit, TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 8), 
						"ConstantVariate", new TimeStamp(TimeUnit.DAY, 20)));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_ICU, test ? 
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.HOUR, 2)) :
				HospitalModelTools.getScaledSimulationTimeFunction(unit, "TriangleVariate", 
				new TimeStamp(TimeUnit.HOUR, 2), new TimeStamp(TimeUnit.WEEK, 2), new TimeStamp(TimeUnit.DAY, 3)));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_PACU, test ?  
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.HOUR, 1)) :
				HospitalModelTools.getScaledSimulationTimeFunction(unit, "TriangleVariate", 
				new TimeStamp(TimeUnit.HOUR, 1), new TimeStamp(TimeUnit.DAY, 3), new TimeStamp(TimeUnit.HOUR, 7)));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_SPACU, test ?  
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.HOUR, 1)) :
				HospitalModelTools.getScaledSimulationTimeFunction(unit, "UniformVariate", 
				new TimeStamp(TimeUnit.HOUR, 1), new TimeStamp(TimeUnit.HOUR, 3)));
		gynParams.put(StdSurgicalSubModel.Parameters.LENGTH_APACU, test ?  
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 10)) :
				HospitalModelTools.getScaledSimulationTimeFunction(unit, "UniformVariate", 
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
				SimulationPeriodicCycle.newMonthlyCycle(unit, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL)) :
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL), 0));
		gynParams.put(StdSurgicalSubModel.Parameters.SINTERARRIVAL, test ? 
				SimulationPeriodicCycle.newMonthlyCycle(unit, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL)) :
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL), 0));
		gynParams.put(StdSurgicalSubModel.Parameters.AINTERARRIVAL, test ? 
				SimulationPeriodicCycle.newMonthlyCycle(unit, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL)) :
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL), 0));
		gynParams.put(StdSurgicalSubModel.Parameters.ITERSUCC, test ?
				TimeFunctionFactory.getInstance("ConstantVariate", 1) :
				TimeFunctionFactory.getInstance("UniformVariate", 1, 5));
		gynParams.put(StdSurgicalSubModel.Parameters.PROB_1ST_APP, 0.2);
		StdSurgicalSubModel.createModel(factory, "GYN", gynParams);
		StdSurgicalSubModel.createModel(factory, "TRA", gynParams);
		StdSurgicalSubModel.createModel(factory, "NEU", gynParams);
		StdSurgicalSubModel.createModel(factory, "NEPH", gynParams);
		StdSurgicalSubModel.createModel(factory, "TRA2", gynParams);
		StdSurgicalSubModel.createModel(factory, "TRA3", gynParams);
		StdSurgicalSubModel.createModel(factory, "TRA4", gynParams);
		StdSurgicalSubModel.createModel(factory, "TRA5", gynParams);
		StdSurgicalSubModel.createModel(factory, "TRA6", gynParams);
		StdSurgicalSubModel.createModel(factory, "TRA7", gynParams);

		// Traumatology
		ModelParameterMap traParams = new ModelParameterMap(StdSurgicalSubModel.Parameters.values().length);
		traParams.put(StdSurgicalSubModel.Parameters.NBEDS, test? 1:15);
		traParams.put(StdSurgicalSubModel.Parameters.NSBEDS, test? 1:3);
		traParams.put(StdSurgicalSubModel.Parameters.NSURGEONS, test? 1:4);
		traParams.put(StdSurgicalSubModel.Parameters.NSURGERIES, test? 1:4);
		traParams.put(StdSurgicalSubModel.Parameters.NDOCTORS, test? 1:7);
		traParams.put(StdSurgicalSubModel.Parameters.NSCRUBNURSES, test? 1:4);
		traParams.put(StdSurgicalSubModel.Parameters.NSURGERY_ASSIST, test? 1:2);		
		traParams.put(StdSurgicalSubModel.Parameters.PROB_NUC_OP, 0.1);
		traParams.put(StdSurgicalSubModel.Parameters.PROB_RAD_OP, 0.5);
		traParams.put(StdSurgicalSubModel.Parameters.PROB_LAB_OP, 0.5);
		traParams.put(StdSurgicalSubModel.Parameters.PROB_LABLAB_OP, 0.90);
		traParams.put(StdSurgicalSubModel.Parameters.PROB_LABMIC_OP, 0.06);
		traParams.put(StdSurgicalSubModel.Parameters.PROB_LABHAE_OP, 0.1);
		traParams.put(StdSurgicalSubModel.Parameters.PROB_LABPAT_OP, 0.05);
		traParams.put(StdSurgicalSubModel.Parameters.PROB_NUC_IP, 0.01);
		traParams.put(StdSurgicalSubModel.Parameters.PROB_RAD_IP, 0.2);
		traParams.put(StdSurgicalSubModel.Parameters.PROB_LAB_IP, 0.2);
		traParams.put(StdSurgicalSubModel.Parameters.PROB_LABLAB_IP, 0.90);
		traParams.put(StdSurgicalSubModel.Parameters.PROB_LABMIC_IP, 0.06);
		traParams.put(StdSurgicalSubModel.Parameters.PROB_LABHAE_IP, 0.1);
		traParams.put(StdSurgicalSubModel.Parameters.PROB_LABPAT_IP, 0.05);
		traParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP1, new SimulationTimeFunction(unit, "ConstantVariate", 12));
		// Patients wait at least one day before being admitted after last appointment. Admissions happen at 18:00
		traParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2ADM, HospitalModelTools.getNextHighFunction(unit, 
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 18), "ConstantVariate", TimeStamp.getMinute())); 
		traParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2OP, test ?
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.DAY, 1)) :
				new SimulationTimeFunction(unit, "UniformVariate", new TimeStamp(TimeUnit.WEEK, 8), new TimeStamp(TimeUnit.WEEK, 12)));
		traParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2, new SimulationTimeFunction(unit, "ConstantVariate", 10));
		traParams.put(StdSurgicalSubModel.Parameters.PROB_ADM, 0.5);
		traParams.put(StdSurgicalSubModel.Parameters.LENGTH_SUR, new SimulationTimeFunction(unit, "ConstantVariate", 90));
		traParams.put(StdSurgicalSubModel.Parameters.LENGTH_SSUR, new SimulationTimeFunction(unit, "ConstantVariate", 30));
		traParams.put(StdSurgicalSubModel.Parameters.LENGTH_ASUR, new SimulationTimeFunction(unit, "ConstantVariate", 20));
		traParams.put(StdSurgicalSubModel.Parameters.LENGTH_POP, new SimulationTimeFunction(unit, "ConstantVariate", 10));
		traParams.put(StdSurgicalSubModel.Parameters.LENGTH_SUR2POP, test ? 
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.DAY, 1)) :
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.DAY, 20)));
		traParams.put(StdSurgicalSubModel.Parameters.LENGTH_ICU, test ? 
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.HOUR, 2)) :
				new SimulationTimeFunction(unit, "TriangleVariate", 
				new TimeStamp(TimeUnit.HOUR, 2), new TimeStamp(TimeUnit.WEEK, 2), new TimeStamp(TimeUnit.DAY, 3)));
		traParams.put(StdSurgicalSubModel.Parameters.LENGTH_PACU, test ?  
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.HOUR, 1)) :
				new SimulationTimeFunction(unit, "TriangleVariate", 
				new TimeStamp(TimeUnit.HOUR, 1), new TimeStamp(TimeUnit.DAY, 3), new TimeStamp(TimeUnit.HOUR, 7)));
		traParams.put(StdSurgicalSubModel.Parameters.LENGTH_SPACU, test ?  
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.HOUR, 1)) :
				new SimulationTimeFunction(unit, "UniformVariate", 
				new TimeStamp(TimeUnit.HOUR, 1), new TimeStamp(TimeUnit.HOUR, 3)));
		traParams.put(StdSurgicalSubModel.Parameters.LENGTH_APACU, test ?  
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 10)) :
				new SimulationTimeFunction(unit, "UniformVariate", 
				new TimeStamp(TimeUnit.MINUTE, 10), new TimeStamp(TimeUnit.MINUTE, 30)));
		// Patients wait at least one day after exiting, always at 12:00
		traParams.put(StdSurgicalSubModel.Parameters.LENGTH_SUR2EXIT, HospitalModelTools.getNextHighFunction(unit,
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 12), 
				"TriangleVariate", TimeStamp.getHour(), new TimeStamp(TimeUnit.DAY, 4), TimeStamp.getDay()));
		traParams.put(StdSurgicalSubModel.Parameters.LENGTH_SSUR2EXIT, HospitalModelTools.getNextHighFunction(unit,
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 12), 
				"UniformVariate", TimeStamp.getHour(), TimeStamp.getDay()));
		traParams.put(StdSurgicalSubModel.Parameters.NPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", test? 3:30));
		traParams.put(StdSurgicalSubModel.Parameters.NSPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", test? 1:5));
		traParams.put(StdSurgicalSubModel.Parameters.NAPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", test? 1:5));
		traParams.put(StdSurgicalSubModel.Parameters.INTERARRIVAL, test ? 
				SimulationPeriodicCycle.newMonthlyCycle(unit, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL)) :
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL), 0));
		traParams.put(StdSurgicalSubModel.Parameters.SINTERARRIVAL, test ? 
				SimulationPeriodicCycle.newMonthlyCycle(unit, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL)) :
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL), 0));
		traParams.put(StdSurgicalSubModel.Parameters.AINTERARRIVAL, test ? 
				SimulationPeriodicCycle.newMonthlyCycle(unit, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL)) :
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL), 0));
		traParams.put(StdSurgicalSubModel.Parameters.ITERSUCC, test ?
				TimeFunctionFactory.getInstance("ConstantVariate", 1) :
				TimeFunctionFactory.getInstance("UniformVariate", 1, 5));
		traParams.put(StdSurgicalSubModel.Parameters.PROB_1ST_APP, 0.2);
//		StdSurgicalSubModel.createModel(factory, "TRA", traParams);

		// Nephrology
		ModelParameterMap nepParams = new ModelParameterMap(StdSurgicalSubModel.Parameters.values().length);
		nepParams.put(StdSurgicalSubModel.Parameters.NBEDS, test? 1:20);
		nepParams.put(StdSurgicalSubModel.Parameters.NSBEDS, test? 1:3);
		nepParams.put(StdSurgicalSubModel.Parameters.NSURGEONS, test? 1:4);
		nepParams.put(StdSurgicalSubModel.Parameters.NSURGERIES, test? 1:4);
		nepParams.put(StdSurgicalSubModel.Parameters.NDOCTORS, test? 1:7);
		nepParams.put(StdSurgicalSubModel.Parameters.NSCRUBNURSES, test? 1:4);
		nepParams.put(StdSurgicalSubModel.Parameters.NSURGERY_ASSIST, test? 1:2);		
		nepParams.put(StdSurgicalSubModel.Parameters.PROB_NUC_OP, 0.1);
		nepParams.put(StdSurgicalSubModel.Parameters.PROB_RAD_OP, 0.01);
		nepParams.put(StdSurgicalSubModel.Parameters.PROB_LAB_OP, 0.95);
		nepParams.put(StdSurgicalSubModel.Parameters.PROB_LABLAB_OP, 0.90);
		nepParams.put(StdSurgicalSubModel.Parameters.PROB_LABMIC_OP, 0.06);
		nepParams.put(StdSurgicalSubModel.Parameters.PROB_LABHAE_OP, 0.1);
		nepParams.put(StdSurgicalSubModel.Parameters.PROB_LABPAT_OP, 0.05);
		nepParams.put(StdSurgicalSubModel.Parameters.PROB_NUC_IP, 0.01);
		nepParams.put(StdSurgicalSubModel.Parameters.PROB_RAD_IP, 0.01);
		nepParams.put(StdSurgicalSubModel.Parameters.PROB_LAB_IP, 0.5);
		nepParams.put(StdSurgicalSubModel.Parameters.PROB_LABLAB_IP, 0.90);
		nepParams.put(StdSurgicalSubModel.Parameters.PROB_LABMIC_IP, 0.06);
		nepParams.put(StdSurgicalSubModel.Parameters.PROB_LABHAE_IP, 0.1);
		nepParams.put(StdSurgicalSubModel.Parameters.PROB_LABPAT_IP, 0.05);
		nepParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP1, new SimulationTimeFunction(unit, "ConstantVariate", 12));
		// Patients wait at least one day before being admitted after last appointment. Admissions happen at 18:00
		nepParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2ADM, HospitalModelTools.getNextHighFunction(unit, 
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 18), "ConstantVariate", TimeStamp.getMinute())); 
		nepParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2OP, test ?
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.DAY, 1)) :
				new SimulationTimeFunction(unit, "UniformVariate", new TimeStamp(TimeUnit.WEEK, 8), new TimeStamp(TimeUnit.WEEK, 12)));
		nepParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2, new SimulationTimeFunction(unit, "ConstantVariate", 10));
		nepParams.put(StdSurgicalSubModel.Parameters.PROB_ADM, 0.5);
		nepParams.put(StdSurgicalSubModel.Parameters.LENGTH_SUR, new SimulationTimeFunction(unit, "ConstantVariate", 90));
		nepParams.put(StdSurgicalSubModel.Parameters.LENGTH_SSUR, new SimulationTimeFunction(unit, "ConstantVariate", 30));
		nepParams.put(StdSurgicalSubModel.Parameters.LENGTH_ASUR, new SimulationTimeFunction(unit, "ConstantVariate", 20));
		nepParams.put(StdSurgicalSubModel.Parameters.LENGTH_POP, new SimulationTimeFunction(unit, "ConstantVariate", 10));
		nepParams.put(StdSurgicalSubModel.Parameters.LENGTH_SUR2POP, test ? 
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.DAY, 1)) :
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.DAY, 20)));
		nepParams.put(StdSurgicalSubModel.Parameters.LENGTH_ICU, test ? 
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.HOUR, 2)) :
				new SimulationTimeFunction(unit, "TriangleVariate", 
				new TimeStamp(TimeUnit.HOUR, 2), new TimeStamp(TimeUnit.WEEK, 2), new TimeStamp(TimeUnit.DAY, 3)));
		nepParams.put(StdSurgicalSubModel.Parameters.LENGTH_PACU, test ?  
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.HOUR, 1)) :
				new SimulationTimeFunction(unit, "TriangleVariate", 
				new TimeStamp(TimeUnit.HOUR, 1), new TimeStamp(TimeUnit.DAY, 3), new TimeStamp(TimeUnit.HOUR, 7)));
		nepParams.put(StdSurgicalSubModel.Parameters.LENGTH_SPACU, test ?  
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.HOUR, 1)) :
				new SimulationTimeFunction(unit, "UniformVariate", 
				new TimeStamp(TimeUnit.HOUR, 1), new TimeStamp(TimeUnit.HOUR, 3)));
		nepParams.put(StdSurgicalSubModel.Parameters.LENGTH_APACU, test ?  
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 10)) :
				new SimulationTimeFunction(unit, "UniformVariate", 
				new TimeStamp(TimeUnit.MINUTE, 10), new TimeStamp(TimeUnit.MINUTE, 30)));
		// Patients wait at least one day after exiting, always at 12:00
		nepParams.put(StdSurgicalSubModel.Parameters.LENGTH_SUR2EXIT, HospitalModelTools.getNextHighFunction(unit,
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 12), 
				"TriangleVariate", TimeStamp.getHour(), new TimeStamp(TimeUnit.DAY, 4), TimeStamp.getDay()));
		nepParams.put(StdSurgicalSubModel.Parameters.LENGTH_SSUR2EXIT, HospitalModelTools.getNextHighFunction(unit,
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 12), 
				"UniformVariate", TimeStamp.getHour(), TimeStamp.getDay()));
		nepParams.put(StdSurgicalSubModel.Parameters.NPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", test? 3:40));
		nepParams.put(StdSurgicalSubModel.Parameters.NSPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", test? 1:5));
		nepParams.put(StdSurgicalSubModel.Parameters.NAPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", test? 1:5));
		nepParams.put(StdSurgicalSubModel.Parameters.INTERARRIVAL, test ? 
				SimulationPeriodicCycle.newMonthlyCycle(unit, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL)) :
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL), 0));
		nepParams.put(StdSurgicalSubModel.Parameters.SINTERARRIVAL, test ? 
				SimulationPeriodicCycle.newMonthlyCycle(unit, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL)) :
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL), 0));
		nepParams.put(StdSurgicalSubModel.Parameters.AINTERARRIVAL, test ? 
				SimulationPeriodicCycle.newMonthlyCycle(unit, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL)) :
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL), 0));
		nepParams.put(StdSurgicalSubModel.Parameters.ITERSUCC, test ?
				TimeFunctionFactory.getInstance("ConstantVariate", 1) :
				TimeFunctionFactory.getInstance("UniformVariate", 1, 5));
		nepParams.put(StdSurgicalSubModel.Parameters.PROB_1ST_APP, 0.2);
//		StdSurgicalSubModel.createModel(factory, "NEP", nepParams);
		
		// Neurology
		ModelParameterMap neuParams = new ModelParameterMap(StdSurgicalSubModel.Parameters.values().length);
		neuParams.put(StdSurgicalSubModel.Parameters.NBEDS, test? 1:20);
		neuParams.put(StdSurgicalSubModel.Parameters.NSBEDS, test? 1:3);
		neuParams.put(StdSurgicalSubModel.Parameters.NSURGEONS, test? 1:4);
		neuParams.put(StdSurgicalSubModel.Parameters.NSURGERIES, test? 1:4);
		neuParams.put(StdSurgicalSubModel.Parameters.NDOCTORS, test? 1:7);
		neuParams.put(StdSurgicalSubModel.Parameters.NSCRUBNURSES, test? 1:4);
		neuParams.put(StdSurgicalSubModel.Parameters.NSURGERY_ASSIST, test? 1:2);		
		neuParams.put(StdSurgicalSubModel.Parameters.PROB_NUC_OP, 0.1);
		neuParams.put(StdSurgicalSubModel.Parameters.PROB_RAD_OP, 0.01);
		neuParams.put(StdSurgicalSubModel.Parameters.PROB_LAB_OP, 0.5);
		neuParams.put(StdSurgicalSubModel.Parameters.PROB_LABLAB_OP, 0.90);
		neuParams.put(StdSurgicalSubModel.Parameters.PROB_LABMIC_OP, 0.06);
		neuParams.put(StdSurgicalSubModel.Parameters.PROB_LABHAE_OP, 0.1);
		neuParams.put(StdSurgicalSubModel.Parameters.PROB_LABPAT_OP, 0.05);
		neuParams.put(StdSurgicalSubModel.Parameters.PROB_NUC_IP, 0.01);
		neuParams.put(StdSurgicalSubModel.Parameters.PROB_RAD_IP, 0.01);
		neuParams.put(StdSurgicalSubModel.Parameters.PROB_LAB_IP, 0.2);
		neuParams.put(StdSurgicalSubModel.Parameters.PROB_LABLAB_IP, 0.90);
		neuParams.put(StdSurgicalSubModel.Parameters.PROB_LABMIC_IP, 0.06);
		neuParams.put(StdSurgicalSubModel.Parameters.PROB_LABHAE_IP, 0.1);
		neuParams.put(StdSurgicalSubModel.Parameters.PROB_LABPAT_IP, 0.05);
		neuParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP1, new SimulationTimeFunction(unit, "ConstantVariate", 12));
		// Patients wait at least one day before being admitted after last appointment. Admissions happen at 18:00
		neuParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2ADM, HospitalModelTools.getNextHighFunction(unit, 
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 18), "ConstantVariate", TimeStamp.getMinute())); 
		neuParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2OP, test ?
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.DAY, 1)) :
				new SimulationTimeFunction(unit, "UniformVariate", new TimeStamp(TimeUnit.WEEK, 8), new TimeStamp(TimeUnit.WEEK, 12)));
		neuParams.put(StdSurgicalSubModel.Parameters.LENGTH_OP2, new SimulationTimeFunction(unit, "ConstantVariate", 10));
		neuParams.put(StdSurgicalSubModel.Parameters.PROB_ADM, 0.5);
		neuParams.put(StdSurgicalSubModel.Parameters.LENGTH_SUR, new SimulationTimeFunction(unit, "ConstantVariate", 90));
		neuParams.put(StdSurgicalSubModel.Parameters.LENGTH_SSUR, new SimulationTimeFunction(unit, "ConstantVariate", 30));
		neuParams.put(StdSurgicalSubModel.Parameters.LENGTH_ASUR, new SimulationTimeFunction(unit, "ConstantVariate", 20));
		neuParams.put(StdSurgicalSubModel.Parameters.LENGTH_POP, new SimulationTimeFunction(unit, "ConstantVariate", 10));
		neuParams.put(StdSurgicalSubModel.Parameters.LENGTH_SUR2POP, test ? 
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.DAY, 1)) :
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.DAY, 20)));
		neuParams.put(StdSurgicalSubModel.Parameters.LENGTH_ICU, test ? 
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.HOUR, 2)) :
				new SimulationTimeFunction(unit, "TriangleVariate", 
				new TimeStamp(TimeUnit.HOUR, 2), new TimeStamp(TimeUnit.WEEK, 2), new TimeStamp(TimeUnit.DAY, 3)));
		neuParams.put(StdSurgicalSubModel.Parameters.LENGTH_PACU, test ?  
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.HOUR, 1)) :
				new SimulationTimeFunction(unit, "TriangleVariate", 
				new TimeStamp(TimeUnit.HOUR, 1), new TimeStamp(TimeUnit.DAY, 3), new TimeStamp(TimeUnit.HOUR, 7)));
		neuParams.put(StdSurgicalSubModel.Parameters.LENGTH_SPACU, test ?  
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.HOUR, 1)) :
				new SimulationTimeFunction(unit, "UniformVariate", 
				new TimeStamp(TimeUnit.HOUR, 1), new TimeStamp(TimeUnit.HOUR, 3)));
		neuParams.put(StdSurgicalSubModel.Parameters.LENGTH_APACU, test ?  
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 10)) :
				new SimulationTimeFunction(unit, "UniformVariate", 
				new TimeStamp(TimeUnit.MINUTE, 10), new TimeStamp(TimeUnit.MINUTE, 30)));
		// Patients wait at least one day after exiting, always at 12:00
		neuParams.put(StdSurgicalSubModel.Parameters.LENGTH_SUR2EXIT, HospitalModelTools.getNextHighFunction(unit,
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 12), 
				"TriangleVariate", TimeStamp.getHour(), new TimeStamp(TimeUnit.DAY, 4), TimeStamp.getDay()));
		neuParams.put(StdSurgicalSubModel.Parameters.LENGTH_SSUR2EXIT, HospitalModelTools.getNextHighFunction(unit,
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 12), 
				"UniformVariate", TimeStamp.getHour(), TimeStamp.getDay()));
		neuParams.put(StdSurgicalSubModel.Parameters.NPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", test? 3:40));
		neuParams.put(StdSurgicalSubModel.Parameters.NSPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", test? 1:10));
		neuParams.put(StdSurgicalSubModel.Parameters.NAPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", test? 1:10));
		neuParams.put(StdSurgicalSubModel.Parameters.INTERARRIVAL, test ? 
				SimulationPeriodicCycle.newMonthlyCycle(unit, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL)) :
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL), 0));
		neuParams.put(StdSurgicalSubModel.Parameters.SINTERARRIVAL, test ? 
				SimulationPeriodicCycle.newMonthlyCycle(unit, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL)) :
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL), 0));
		neuParams.put(StdSurgicalSubModel.Parameters.AINTERARRIVAL, test ? 
				SimulationPeriodicCycle.newMonthlyCycle(unit, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL)) :
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL), 0));
		neuParams.put(StdSurgicalSubModel.Parameters.ITERSUCC, test ?
				TimeFunctionFactory.getInstance("ConstantVariate", 1) :
				TimeFunctionFactory.getInstance("UniformVariate", 1, 5));
		neuParams.put(StdSurgicalSubModel.Parameters.PROB_1ST_APP, 0.2);
//		StdSurgicalSubModel.createModel(factory, "NEU", neuParams);
		
		// Rheumatology
		ModelParameterMap rheParams = new ModelParameterMap(StdMedicalSubModel.Parameters.values().length);
		rheParams.put(StdMedicalSubModel.Parameters.NDOCTORS, test? 1:5);
		rheParams.put(StdMedicalSubModel.Parameters.NBEDS, test? 1:5);
		rheParams.put(StdMedicalSubModel.Parameters.PROB_ADM, 0.01);
		rheParams.put(StdMedicalSubModel.Parameters.PROB_NUC_OP, 0.05);
		rheParams.put(StdMedicalSubModel.Parameters.PROB_RAD_OP, 0.1);
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
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 18), "ConstantVariate", TimeStamp.getMinute())); 
		rheParams.put(StdMedicalSubModel.Parameters.LOS, HospitalModelTools.getNextHighFunction(unit,
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 12), 
				"TriangleVariate", TimeStamp.getHour(), new TimeStamp(TimeUnit.DAY, 7), new TimeStamp(TimeUnit.DAY, 2)));
		rheParams.put(StdMedicalSubModel.Parameters.LENGTH_OP1, new SimulationTimeFunction(unit, "ConstantVariate", 12));
		rheParams.put(StdMedicalSubModel.Parameters.LENGTH_OP2OP, test ?
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.DAY, 1)) :
				new SimulationTimeFunction(unit, "UniformVariate", new TimeStamp(TimeUnit.WEEK, 8), new TimeStamp(TimeUnit.WEEK, 12)));
		rheParams.put(StdMedicalSubModel.Parameters.LENGTH_OP2, new SimulationTimeFunction(unit, "ConstantVariate", 10));
		rheParams.put(StdMedicalSubModel.Parameters.NPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", test? 3:40));
		rheParams.put(StdMedicalSubModel.Parameters.INTERARRIVAL, test ? 
				SimulationPeriodicCycle.newMonthlyCycle(unit, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL)) :
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL), 0));
		rheParams.put(StdMedicalSubModel.Parameters.ITERSUCC, test ?
				TimeFunctionFactory.getInstance("ConstantVariate", 1) :
				TimeFunctionFactory.getInstance("UniformVariate", 1, 5));
		rheParams.put(StdMedicalSubModel.Parameters.PROB_1ST_APP, 0.2);
//		StdMedicalSubModel.createModel(factory, "RHE", rheParams);

		// Dermatology
		ModelParameterMap derParams = new ModelParameterMap(StdMedicalSubModel.Parameters.values().length);
		derParams.put(StdMedicalSubModel.Parameters.NDOCTORS, test? 1:5);
		derParams.put(StdMedicalSubModel.Parameters.NBEDS, test? 1:5);
		derParams.put(StdMedicalSubModel.Parameters.PROB_ADM, 0.01);
		derParams.put(StdMedicalSubModel.Parameters.PROB_NUC_OP, 0.01);
		derParams.put(StdMedicalSubModel.Parameters.PROB_RAD_OP, 0.01);
		derParams.put(StdMedicalSubModel.Parameters.PROB_LAB_OP, 0.3);
		derParams.put(StdMedicalSubModel.Parameters.PROB_LABLAB_OP, 0.90);
		derParams.put(StdMedicalSubModel.Parameters.PROB_LABMIC_OP, 0.06);
		derParams.put(StdMedicalSubModel.Parameters.PROB_LABHAE_OP, 0.07);
		derParams.put(StdMedicalSubModel.Parameters.PROB_LABPAT_OP, 0.05);
		derParams.put(StdMedicalSubModel.Parameters.PROB_NUC_IP, 0.01);
		derParams.put(StdMedicalSubModel.Parameters.PROB_RAD_IP, 0.01);
		derParams.put(StdMedicalSubModel.Parameters.PROB_LAB_IP, 0.1);
		derParams.put(StdMedicalSubModel.Parameters.PROB_LABLAB_IP, 0.90);
		derParams.put(StdMedicalSubModel.Parameters.PROB_LABMIC_IP, 0.06);
		derParams.put(StdMedicalSubModel.Parameters.PROB_LABHAE_IP, 0.07);
		derParams.put(StdMedicalSubModel.Parameters.PROB_LABPAT_IP, 0.05);
		derParams.put(StdMedicalSubModel.Parameters.LENGTH_OP2ADM, HospitalModelTools.getNextHighFunction(unit, 
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 18), "ConstantVariate", TimeStamp.getMinute())); 
		derParams.put(StdMedicalSubModel.Parameters.LOS, HospitalModelTools.getNextHighFunction(unit,
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 12), 
				"TriangleVariate", TimeStamp.getHour(), new TimeStamp(TimeUnit.DAY, 7), new TimeStamp(TimeUnit.DAY, 2)));
		derParams.put(StdMedicalSubModel.Parameters.LENGTH_OP1, new SimulationTimeFunction(unit, "ConstantVariate", 12));
		derParams.put(StdMedicalSubModel.Parameters.LENGTH_OP2OP, test ?
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.DAY, 1)) :
				new SimulationTimeFunction(unit, "UniformVariate", new TimeStamp(TimeUnit.WEEK, 8), new TimeStamp(TimeUnit.WEEK, 12)));
		derParams.put(StdMedicalSubModel.Parameters.LENGTH_OP2, new SimulationTimeFunction(unit, "ConstantVariate", 10));
		derParams.put(StdMedicalSubModel.Parameters.NPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", test? 3:40));
		derParams.put(StdMedicalSubModel.Parameters.INTERARRIVAL, test ? 
				SimulationPeriodicCycle.newMonthlyCycle(unit, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL)) :
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL), 0));
		derParams.put(StdMedicalSubModel.Parameters.ITERSUCC, test ?
				TimeFunctionFactory.getInstance("ConstantVariate", 1) :
				TimeFunctionFactory.getInstance("UniformVariate", 1, 5));
		derParams.put(StdMedicalSubModel.Parameters.PROB_1ST_APP, 0.2);
//		StdMedicalSubModel.createModel(factory, "DER", derParams);

		// Ophthalmology
		ModelParameterMap ophParams = new ModelParameterMap(StdMedicalSubModel.Parameters.values().length);
		ophParams.put(StdMedicalSubModel.Parameters.NDOCTORS, test? 1:5);
		ophParams.put(StdMedicalSubModel.Parameters.NBEDS, test? 1:5);
		ophParams.put(StdMedicalSubModel.Parameters.PROB_ADM, 0.001);
		ophParams.put(StdMedicalSubModel.Parameters.PROB_NUC_OP, 0.05);
		ophParams.put(StdMedicalSubModel.Parameters.PROB_RAD_OP, 0.05);
		ophParams.put(StdMedicalSubModel.Parameters.PROB_LAB_OP, 0.05);
		ophParams.put(StdMedicalSubModel.Parameters.PROB_LABLAB_OP, 0.90);
		ophParams.put(StdMedicalSubModel.Parameters.PROB_LABMIC_OP, 0.06);
		ophParams.put(StdMedicalSubModel.Parameters.PROB_LABHAE_OP, 0.07);
		ophParams.put(StdMedicalSubModel.Parameters.PROB_LABPAT_OP, 0.05);
		ophParams.put(StdMedicalSubModel.Parameters.PROB_NUC_IP, 0.01);
		ophParams.put(StdMedicalSubModel.Parameters.PROB_RAD_IP, 0.01);
		ophParams.put(StdMedicalSubModel.Parameters.PROB_LAB_IP, 0.1);
		ophParams.put(StdMedicalSubModel.Parameters.PROB_LABLAB_IP, 0.90);
		ophParams.put(StdMedicalSubModel.Parameters.PROB_LABMIC_IP, 0.06);
		ophParams.put(StdMedicalSubModel.Parameters.PROB_LABHAE_IP, 0.07);
		ophParams.put(StdMedicalSubModel.Parameters.PROB_LABPAT_IP, 0.05);
		ophParams.put(StdMedicalSubModel.Parameters.LENGTH_OP2ADM, HospitalModelTools.getNextHighFunction(unit, 
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 18), "ConstantVariate", TimeStamp.getMinute())); 
		ophParams.put(StdMedicalSubModel.Parameters.LOS, HospitalModelTools.getNextHighFunction(unit,
				TimeStamp.getDay(), new TimeStamp(TimeUnit.HOUR, 12), 
				"TriangleVariate", TimeStamp.getHour(), new TimeStamp(TimeUnit.DAY, 7), new TimeStamp(TimeUnit.DAY, 2)));
		ophParams.put(StdMedicalSubModel.Parameters.LENGTH_OP1, new SimulationTimeFunction(unit, "ConstantVariate", 12));
		ophParams.put(StdMedicalSubModel.Parameters.LENGTH_OP2OP, test ?
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.DAY, 1)) :
				new SimulationTimeFunction(unit, "ConstantVariate", new TimeStamp(TimeUnit.YEAR, 1)));
		ophParams.put(StdMedicalSubModel.Parameters.LENGTH_OP2, new SimulationTimeFunction(unit, "ConstantVariate", 10));
		ophParams.put(StdMedicalSubModel.Parameters.NPATIENTS, TimeFunctionFactory.getInstance("ConstantVariate", test? 3:40));
		ophParams.put(StdMedicalSubModel.Parameters.INTERARRIVAL, test ? 
				SimulationPeriodicCycle.newMonthlyCycle(unit, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL)) :
				new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, new TimeStamp(TimeUnit.MINUTE, PATIENTARRIVAL), 0));
		ophParams.put(StdMedicalSubModel.Parameters.ITERSUCC, test ?
				TimeFunctionFactory.getInstance("ConstantVariate", 1) :
				TimeFunctionFactory.getInstance("UniformVariate", 1, 5));
		ophParams.put(StdMedicalSubModel.Parameters.PROB_1ST_APP, 0.2);
//		StdMedicalSubModel.createModel(factory, "OPH", ophParams);
	}
	
	@Override
	public Simulation getSimulation(int ind) {
		// FIXME: Default generator (Mersenne twister) is failing with multiple threads
		RandomNumberFactory.setDefaultClass("simkit.random.Congruential");
		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, ind, "Big Hospital", unit, TimeStamp.getZero(), endTs);
		createModel(factory);

		Simulation simul = factory.getSimulation();
		addViews(simul);
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
