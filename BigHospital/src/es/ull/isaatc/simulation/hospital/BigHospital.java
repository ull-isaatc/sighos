/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import simkit.random.RandomNumberFactory;
import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.ElementCreator;
import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.SimulationCycle;
import es.ull.isaatc.simulation.common.SimulationWeeklyPeriodicCycle;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.factory.SimulationFactory;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.Flow;
import es.ull.isaatc.simulation.common.flow.InitializerFlow;
import es.ull.isaatc.simulation.common.inforeceiver.ProgressView;
import es.ull.isaatc.simulation.common.inforeceiver.StdInfoView;
import es.ull.isaatc.simulation.hospital.view.ActionsCounterView;
import es.ull.isaatc.simulation.hospital.view.ActivityLengthFileSafeView;
import es.ull.isaatc.simulation.hospital.view.ActivityQueueFileSafeView;
import es.ull.isaatc.simulation.hospital.view.ExecutionCounterFileSafeView;
import es.ull.isaatc.simulation.hospital.view.ResourceUsageFileSafeView;
import es.ull.isaatc.simulation.hospital.view.SimultaneousEventFileSafeView;
import es.ull.isaatc.simulation.test.FileCPUTimeView;
import es.ull.isaatc.util.Output;
import es.ull.isaatc.util.WeeklyPeriodicCycle;

/**
 * The main class to simulate a big hospital.
 * 
 * @author Iván Castilla Rodríguez
 */
public class BigHospital {
	private static final String SEP = "----------------------------------------";
	/** Debug level utilized to decide which views use during the simulation */ 
	private static int debug = 0;
	/** The path where the output files are written to */
	private static String outputPath;
	/** Sampling rate for outputs */
	private static TimeStamp viewPeriod;
	/** Warm-up period. No results are shown during this time */
	private static final TimeStamp warmUp = TimeStamp.getZero();

	/**
	 * Set which views will be used during the simulation according to the debug level
	 * @param simul Current simulation
	 * @param debug Debug level
	 */
	private static void addViews(Simulation simul, int debug) {
		simul.addInfoReceiver(new ActionsCounterView(simul, System.out));
		if (debug == 1)
			simul.addInfoReceiver(new ProgressView(simul));
		else if (debug == 2) {
			simul.addInfoReceiver(new ActivityQueueFileSafeView(simul, outputPath + "queue" + simul.getIdentifier() + ".txt", viewPeriod));
			simul.addInfoReceiver(new ExecutionCounterFileSafeView(simul, outputPath + "total" + simul.getIdentifier() + ".txt", warmUp, viewPeriod));
			simul.addInfoReceiver(new ActivityLengthFileSafeView(simul, outputPath + "act" + simul.getIdentifier() + ".txt", warmUp));
			simul.addInfoReceiver(new SimultaneousEventFileSafeView(simul, outputPath + "events" + simul.getIdentifier() + ".txt"));
			simul.addInfoReceiver(new ResourceUsageFileSafeView(simul, outputPath + "res" + simul.getIdentifier() + ".txt", viewPeriod));
		}
		else if (debug == 3)
			simul.addInfoReceiver(new StdInfoView(simul));
		else if (debug == 4)
			simul.setOutput(new Output(true));		
	}
	
	/**
	 * A regular experiment that creates a fixed amount of departments from each predefined type. 
	 * @param nReplicas Replicas of each experiment to perform 
	 * @param expType Types of experiments to perform
	 * @param nDept The amount of departments of each predefined type to create
	 * @param months Length of the simulation in months
	 * @param minuteScale The finest grain of the simulation time (in minutes). No process lasts less than this value.
	 */
	private static void normalExp(int nReplicas, String expType, int nDept, int months, int minuteScale) {
		TimeStamp endTs = new TimeStamp(TimeUnit.MONTH, months);
		TimeStamp scale = new TimeStamp(TimeUnit.MINUTE, minuteScale);
		int simIndex = 0;
		// FIXME: Default generator (Mersenne twister) is failing with multiple threads
		RandomNumberFactory.setDefaultClass("simkit.random.Congruential");

		int []nDeparments = new int[6];
		Arrays.fill(nDeparments, nDept);
		
		// First sequential for warmup
		if (expType.contains("w")) {
			System.out.println("INITIALIZING...");
			SimulationObjectFactory factory = SimulationFactory.getInstance(SimulationType.SEQUENTIAL, simIndex++, "Big Hospital", HospitalModelConfig.UNIT, TimeStamp.getZero(), endTs);			
			HospitalModel.createModel(factory, scale, new int[] {1,1,1,1,1,1});	
			Simulation simul = factory.getSimulation();
			simul.run();
			System.out.println(SEP);
		}	
		
		System.out.println("EXPERIMENT CONFIG:");
		System.out.println("Scale\t" + scale);
		System.out.print("N. departments");
		for (int dept : nDeparments)
			System.out.print("\t" + dept);
		System.out.println();
		System.out.println(SEP);
		
		if (expType.contains("s")) {
			System.out.println("STARTING SEQUENTIAL EXPERIMENTS...");
			// Now sequential experiments
			for (int i = 0; i < nReplicas; i++) {
				System.out.println(SimulationType.SEQUENTIAL + "\t" + i);
				SimulationObjectFactory factory = SimulationFactory.getInstance(SimulationType.SEQUENTIAL, simIndex++, "Big Hospital", HospitalModelConfig.UNIT, TimeStamp.getZero(), endTs);
				HospitalModel.createModel(factory, scale, nDeparments);	
				Simulation simul = factory.getSimulation();
				addViews(simul, debug);
				simul.run();
				System.out.println(SEP);
			}
		}
		if (expType.contains("3")) {
			// Now "better" sequential experiments
			for (int i = 0; i < nReplicas; i++) {
				System.out.println(SimulationType.SEQ3PHASE2 + "\t" + i);
				SimulationObjectFactory factory = SimulationFactory.getInstance(SimulationType.SEQ3PHASE2, simIndex++, "Big Hospital", HospitalModelConfig.UNIT, TimeStamp.getZero(), endTs);
				HospitalModel.createModel(factory, scale, nDeparments);	
				Simulation simul = factory.getSimulation();
				addViews(simul, debug);
				simul.run();
				System.out.println(SEP);
			}
		}
		
		ArrayList<SimulationFactory.SimulationType> simTypes = new ArrayList<SimulationFactory.SimulationType>();			
		if (expType.contains("p"))
			simTypes.add(SimulationType.GROUPED3PHASEX);
		if (expType.contains("g"))
			simTypes.add(SimulationType.GROUPEDX);
		// Now parallel experiments
		if (simTypes.size() > 0)
			System.out.println("STARTING PARALLEL EXPERIMENTS...");
		int maxThreads = Runtime.getRuntime().availableProcessors();
		for (SimulationType type : simTypes) {
			for (int th = 1; th <= maxThreads; th++) {
				for (int i = 0; i < nReplicas; i++) {
					System.out.println(type + "[" + th + "]\t" + i);
					SimulationObjectFactory factory = SimulationFactory.getInstance(type, simIndex++, "Big Hospital", HospitalModelConfig.UNIT, TimeStamp.getZero(), endTs);
					HospitalModel.createModel(factory, scale, nDeparments);	
					Simulation simul = factory.getSimulation();
					addViews(simul, debug);
					simul.setNThreads(th);
					simul.run();
					System.out.println(SEP);					
				}
			}
		}		
	}

	private static void autoExp(int nReplicas, String expType, int months, int minuteScale, String fileName) {
		TimeStamp endTs = new TimeStamp(TimeUnit.MONTH, months);
		TimeStamp scale = new TimeStamp(TimeUnit.MINUTE, minuteScale);
		int simIndex = 0;
		int []nThreads = new int[] {2,4,8,16};
		int []nThreads2 = new int[] {2,4,8,15};
		int []nDept = new int[] {2,3,4};
		int [][]nDeparments = new int[nDept.length][];
		for (int i = 0; i < nDept.length; i++) {
			nDeparments[i] = new int[6];
			Arrays.fill(nDeparments[i], nDept[i]);
		}
		// FIXME: Default generator (Mersenne twister) is failing with multiple threads
		RandomNumberFactory.setDefaultClass("simkit.random.Congruential");

		PrintWriter buf = null;
		try {
			buf = new PrintWriter(new BufferedWriter(new FileWriter(fileName)), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// First sequential for warmup
		if (expType.contains("w")) {
			System.out.println("INITIALIZING...");
			SimulationObjectFactory factory = SimulationFactory.getInstance(SimulationType.SEQUENTIAL, simIndex++, "Big Hospital", HospitalModelConfig.UNIT, TimeStamp.getZero(), endTs);			
			HospitalModel.createModel(factory, scale, new int[] {1,1,1,1,1,1});	
			Simulation simul = factory.getSimulation();
			simul.run();
			System.out.println(SEP);
		}	

		
		System.out.println("EXPERIMENT CONFIG:");
		System.out.println("Scale\t" + scale);
		System.out.println();
		System.out.println(SEP);
		
		for (int[] nDepart : nDeparments) {
			if (expType.contains("s")) {
				System.out.println("STARTING SEQUENTIAL EXPERIMENTS...");
				// Now sequential experiments
				for (int i = 0; i < nReplicas; i++) {
					System.out.println(SimulationType.SEQUENTIAL + "\t" + i);
					buf.print(SimulationType.SEQUENTIAL + "\t" + 1 + "\t" + nDept[0]);
					SimulationObjectFactory factory = SimulationFactory.getInstance(SimulationType.SEQUENTIAL, simIndex++, "Big Hospital", HospitalModelConfig.UNIT, TimeStamp.getZero(), endTs);
					HospitalModel.createModel(factory, scale, nDepart);	
					Simulation simul = factory.getSimulation();
					simul.addInfoReceiver(new FileCPUTimeView(simul, buf));
//					simul.run();
					System.out.println(SEP);
				}
				buf.println();
			}
			if (expType.contains("3")) {
				// Now "better" sequential experiments
				for (int i = 0; i < nReplicas; i++) {
					System.out.println(SimulationType.SEQ3PHASE2 + "\t" + i);
					buf.print(SimulationType.SEQ3PHASE2 + "\t" + 1 + "\t" + nDept[0]);
					SimulationObjectFactory factory = SimulationFactory.getInstance(SimulationType.SEQ3PHASE2, simIndex++, "Big Hospital", HospitalModelConfig.UNIT, TimeStamp.getZero(), endTs);
					HospitalModel.createModel(factory, scale, nDepart);	
					Simulation simul = factory.getSimulation();
					simul.addInfoReceiver(new FileCPUTimeView(simul, buf));
//					simul.run();
					System.out.println(SEP);
				}
				buf.println();
			}
			// Now parallel experiments
			if (expType.contains("p")) {
				SimulationType type = SimulationType.GROUPED3PHASEX;
				for (int th : nThreads) {
					for (int i = 0; i < nReplicas; i++) {
						System.out.println(type + "[" + th + "]\t" + i);
						buf.print(type + "\t" + th + "\t" + nDept[0]);
						SimulationObjectFactory factory = SimulationFactory.getInstance(type, simIndex++, "Big Hospital", HospitalModelConfig.UNIT, TimeStamp.getZero(), endTs);
						HospitalModel.createModel(factory, scale, nDepart);	
						Simulation simul = factory.getSimulation();
						simul.addInfoReceiver(new FileCPUTimeView(simul, buf));
						simul.setNThreads(th);
//						simul.run();
						System.out.println(SEP);					
					}
					buf.println();
				}
			}
			if (expType.contains("g")) {
				SimulationType type = SimulationType.GROUPED3PHASE;
				for (int th : nThreads2) {
					for (int i = 0; i < nReplicas; i++) {
						System.out.println(type + "[" + th + "]\t" + i);
						buf.print(type + "\t" + th + "\t" + nDept[0]);
						SimulationObjectFactory factory = SimulationFactory.getInstance(type, simIndex++, "Big Hospital", HospitalModelConfig.UNIT, TimeStamp.getZero(), endTs);
						HospitalModel.createModel(factory, scale, nDepart);	
						Simulation simul = factory.getSimulation();
						simul.addInfoReceiver(new FileCPUTimeView(simul, buf));
						simul.setNThreads(th);
//						simul.run();
						System.out.println(SEP);					
					}
					buf.println();
				}
			}
		}
		buf.close();
	}

	/**
	 * Creates a small experiment for testing purposes
	 * @param minuteScale The finest grain of the simulation time (in minutes). No process lasts less than this value.
	 */
	@SuppressWarnings("unused")
	private static void testExp(int minuteScale) {
		TimeStamp scale = new TimeStamp(TimeUnit.MINUTE, minuteScale);
		SimulationObjectFactory factory = SimulationFactory.getInstance(SimulationType.SEQUENTIAL, 0, "Big Hospital", HospitalModelConfig.UNIT, TimeStamp.getZero(), new TimeStamp(TimeUnit.DAY, 3));
		final TimeUnit unit = factory.getSimulation().getTimeUnit();
		HospitalModelConfig.setScale(scale);
		
		// Central Lab
		ModelParameterMap centralLabParams = new ModelParameterMap(CentralLabModel.Parameters.values().length);
		centralLabParams.put(CentralLabModel.Parameters.NTECH, 23);
		centralLabParams.put(CentralLabModel.Parameters.N24HTECH, 5);
		centralLabParams.put(CentralLabModel.Parameters.NNURSES, 16);
		centralLabParams.put(CentralLabModel.Parameters.NXNURSES, 10);
		centralLabParams.put(CentralLabModel.Parameters.NSLOTS, 150);
		centralLabParams.put(CentralLabModel.Parameters.NCENT, 160);
		centralLabParams.put(CentralLabModel.Parameters.LENGTH_SAMPLE, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 2));
		centralLabParams.put(CentralLabModel.Parameters.LENGTH_CENT, HospitalModelConfig.getNextHighFunction(unit, 
				new TimeStamp(TimeUnit.MINUTE, 15), TimeStamp.getZero(), "ConstantVariate", 6));
		centralLabParams.put(CentralLabModel.Parameters.LENGTH_TEST, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 8));
		centralLabParams.put(CentralLabModel.Parameters.NHAETECH, 2);
		centralLabParams.put(CentralLabModel.Parameters.NHAENURSES, 5);
		centralLabParams.put(CentralLabModel.Parameters.NHAESLOTS, 40);
		centralLabParams.put(CentralLabModel.Parameters.LENGTH_HAETEST, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 15));
		centralLabParams.put(CentralLabModel.Parameters.NMICROTECH, 10);
		centralLabParams.put(CentralLabModel.Parameters.NMICRONURSES, 0);
		centralLabParams.put(CentralLabModel.Parameters.NMICROSLOTS, 50);
		centralLabParams.put(CentralLabModel.Parameters.LENGTH_MICROTEST, HospitalModelConfig.getScaledSimulationTimeFunction(unit,
				"ConstantVariate", 20));
		centralLabParams.put(CentralLabModel.Parameters.NPATTECH, 6);
		centralLabParams.put(CentralLabModel.Parameters.NPATNURSES, 1);
		centralLabParams.put(CentralLabModel.Parameters.NPATSLOTS, 50);
		centralLabParams.put(CentralLabModel.Parameters.LENGTH_PATTEST, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 20));
		CentralLabModel.createModel(factory, centralLabParams);
		Flow[] root = CentralLabModel.getOPFlow(factory, 0.5, 1.0, 0.07, 0.06, 0.05);
		
		ElementType et = factory.getElementTypeInstance("P");
		TimeFunction nPatients = TimeFunctionFactory.getInstance("ConstantVariate", 1000);
		ElementCreator ec = factory.getElementCreatorInstance(nPatients, et, (InitializerFlow)root[0]);
		SimulationCycle interArrival = new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, HospitalModelConfig.PATIENTARRIVAL, 0);
		factory.getTimeDrivenGeneratorInstance(ec, interArrival);
		
		Simulation simul = factory.getSimulation();
		simul.addInfoReceiver(new ActionsCounterView(simul, System.out));
		simul.addInfoReceiver(new ActivityQueueFileSafeView(simul, outputPath + "queue" + simul.getIdentifier() + ".txt", TimeStamp.getDay()));
		simul.addInfoReceiver(new ExecutionCounterFileSafeView(simul, outputPath + "total" + simul.getIdentifier() + ".txt", warmUp, TimeStamp.getDay()));
		simul.addInfoReceiver(new ActivityLengthFileSafeView(simul, outputPath + "act" + simul.getIdentifier() + ".txt", warmUp));
		simul.addInfoReceiver(new SimultaneousEventFileSafeView(simul, outputPath + "events" + simul.getIdentifier() + ".txt"));
		simul.addInfoReceiver(new ResourceUsageFileSafeView(simul, outputPath + "res" + simul.getIdentifier() + ".txt", TimeStamp.getDay()));
		simul.run();
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		RandomNumberFactory.setDefaultClass("simkit.random.Congruential");
//		SimulationObjectFactory factory = SimulationFactory.getInstance(SimulationType.GROUPEDX, 0, "Big Hospital", HospitalModelConfig.UNIT, TimeStamp.getZero(), new TimeStamp(TimeUnit.MONTH, 12));
//		HospitalSubModel.createModel(factory, scale, new int[] {3,3,3,3,3,3});	
//		Simulation simul = factory.getSimulation();
//		simul.setNThreads(4);
//		simul.addInfoReceiver(new ActionsCounterView(simul, System.out));
//		simul.addInfoReceiver(new ActivityQueueFileSafeView(simul, OUTPATH + "queue" + simul.getIdentifier() + ".txt", viewPeriod));
//		simul.addInfoReceiver(new ExecutionCounterFileSafeView(simul, OUTPATH + "total" + simul.getIdentifier() + ".txt", warmUp, viewPeriod));
//		simul.addInfoReceiver(new ActivityLengthFileSafeView(simul, OUTPATH + "act" + simul.getIdentifier() + ".txt", warmUp));
//		simul.addInfoReceiver(new SimultaneousEventFileSafeView(simul, OUTPATH + "events" + simul.getIdentifier() + ".txt"));
//		simul.addInfoReceiver(new ResourceUsageFileSafeView(simul, OUTPATH + "res" + simul.getIdentifier() + ".txt", viewPeriod));
//		simul.run();

		
//		if (args.length < 5) {
//			System.out.println("Wrong argument number");
//			System.exit(-1);
//		}
//		int nExperiments = Integer.parseInt(args[0]);
//		String expType = args[1];
//		int nDepartments = Integer.parseInt(args[2]);
//		int months = Integer.parseInt(args[3]);
//		int minuteScale = Integer.parseInt(args[4]);
//		if (args.length > 5) {
//			debug = Integer.parseInt(args[5]);
//			if (debug == 2) {
//				outputPath = args[6];
//				viewPeriod = new TimeStamp(TimeUnit.DAY, Integer.parseInt(args[7]));
//			}
//		}
//		normalExp(nExperiments, expType, nDepartments, months, minuteScale);

		if (args.length < 4) {
			System.out.println("Wrong argument number");
			System.exit(-1);
		}
		int nExperiments = Integer.parseInt(args[0]);
		String expType = args[1];
		int months = Integer.parseInt(args[2]);
		int minuteScale = Integer.parseInt(args[3]);
		String fileName = args[4];
		autoExp(nExperiments, expType, months, minuteScale, fileName);
	}

}
