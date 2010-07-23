/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import simkit.random.RandomNumberFactory;
import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.ElementCreator;
import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.Experiment;
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
import es.ull.isaatc.simulation.common.inforeceiver.StdInfoView;
import es.ull.isaatc.simulation.hospital.view.ActionsCounterView;
import es.ull.isaatc.simulation.hospital.view.ActivityLengthFileSafeView;
import es.ull.isaatc.simulation.hospital.view.ActivityQueueFileSafeView;
import es.ull.isaatc.simulation.hospital.view.ExecutionCounterFileSafeView;
import es.ull.isaatc.simulation.hospital.view.ResourceUsageFileSafeView;
import es.ull.isaatc.simulation.hospital.view.SimultaneousEventFileSafeView;
import es.ull.isaatc.util.Output;
import es.ull.isaatc.util.WeeklyPeriodicCycle;

class BigHospitalExperiment extends Experiment {
	private static int debug = 0;
	private static final String SEP = "----------------------------------------";

	private static final String OUTPATH = "N:\\Tesis\\hResults\\";
	private static final int NEXP = 5;
	private static final SimulationFactory.SimulationType []simTypes = {SimulationType.GROUPED3PHASEX};
	private static final TimeStamp warmUp = TimeStamp.getZero();
	private static final TimeStamp viewPeriod = new TimeStamp(TimeUnit.WEEK, 1);
	private static final TimeStamp endTs = new TimeStamp(TimeUnit.MONTH, 24);
	private static final TimeStamp scale = new TimeStamp(TimeUnit.MINUTE, 5);
	private static final int [] nServices = {3,3,3,3,3,3};

	public BigHospitalExperiment() {
		super("Big Hospital Experiments", NEXP);
	}

	private void addViews(Simulation simul, int debug) {
		simul.addInfoReceiver(new ActionsCounterView(simul, System.out));
//		simul.addInfoReceiver(new ProgressView(simul));
		if (debug == 1) {
			simul.addInfoReceiver(new ActivityQueueFileSafeView(simul, OUTPATH + "queue" + simul.getIdentifier() + ".txt", viewPeriod));
			simul.addInfoReceiver(new ExecutionCounterFileSafeView(simul, OUTPATH + "total" + simul.getIdentifier() + ".txt", warmUp, viewPeriod));
			simul.addInfoReceiver(new ActivityLengthFileSafeView(simul, OUTPATH + "act" + simul.getIdentifier() + ".txt", warmUp));
			simul.addInfoReceiver(new SimultaneousEventFileSafeView(simul, OUTPATH + "events" + simul.getIdentifier() + ".txt"));
			simul.addInfoReceiver(new ResourceUsageFileSafeView(simul, OUTPATH + "res" + simul.getIdentifier() + ".txt", viewPeriod));
		}
		else if (debug == 2)
			simul.addInfoReceiver(new StdInfoView(simul));
		else if (debug == 3)
			simul.setOutput(new Output(true));		
	}
	
	@Override
	// This method is not used here
	public Simulation getSimulation(int ind) {
		return null;
	}

	private void normalExp() {
		int simIndex = 0;
		// FIXME: Default generator (Mersenne twister) is failing with multiple threads
		RandomNumberFactory.setDefaultClass("simkit.random.Congruential");

		// First sequential for warmup
		System.out.println("INITIALIZING...");
		SimulationObjectFactory factory = SimulationFactory.getInstance(SimulationType.SEQUENTIAL, simIndex++, "Big Hospital", HospitalModelConfig.UNIT, TimeStamp.getZero(), TimeStamp.getDay());
		HospitalSubModel.createModel(factory, scale, nServices);	
		Simulation simul = factory.getSimulation();
		simul.run();
		
		System.out.println(SEP);
		System.out.println("EXPERIMENT CONFIG:");
		System.out.println("Scale\t" + scale);
		System.out.print("N. services");
		for (int serv : nServices)
			System.out.print("\t" + serv);
		System.out.println();
		System.out.println(SEP);
		System.out.println("STARTING SEQUENTIAL EXPERIMENTS...");
		// Now sequential experiments
		for (int i = 0; i < nExperiments + 1; i++) {
			System.out.println(SimulationType.SEQUENTIAL + "\t" + i);
			factory = SimulationFactory.getInstance(SimulationType.SEQUENTIAL, simIndex++, "Big Hospital", HospitalModelConfig.UNIT, TimeStamp.getZero(), endTs);
			HospitalSubModel.createModel(factory, scale, nServices);	
			simul = factory.getSimulation();
			addViews(simul, debug);
			simul.run();
			System.out.println(SEP);
		}
		// Now "better" sequential experiments
		for (int i = 0; i < nExperiments; i++) {
			System.out.println(SimulationType.SEQ3PHASE2 + "\t" + i);
			factory = SimulationFactory.getInstance(SimulationType.SEQ3PHASE2, simIndex++, "Big Hospital", HospitalModelConfig.UNIT, TimeStamp.getZero(), endTs);
			HospitalSubModel.createModel(factory, scale, nServices);	
			simul = factory.getSimulation();
			addViews(simul, debug);
			simul.run();
			System.out.println(SEP);
		}
		
		// Now parallel experiments
		System.out.println("STARTING PARALLEL EXPERIMENTS...");
		int maxThreads = Runtime.getRuntime().availableProcessors();
		for (SimulationType type : simTypes) {
			for (int th = 1; th <= maxThreads; th++) {
				for (int i = 0; i < nExperiments; i++) {
					System.out.println(type + "[" + th + "]\t" + i);
					factory = SimulationFactory.getInstance(type, simIndex++, "Big Hospital", HospitalModelConfig.UNIT, TimeStamp.getZero(), endTs);
					HospitalSubModel.createModel(factory, scale, nServices);	
					simul = factory.getSimulation();
					addViews(simul, debug);
					simul.setNThreads(th);
					simul.run();
					System.out.println(SEP);					
				}
			}
		}		
	}

	private void testExp() {
		SimulationObjectFactory factory = SimulationFactory.getInstance(SimulationType.SEQUENTIAL, 0, "Big Hospital", HospitalModelConfig.UNIT, TimeStamp.getZero(), new TimeStamp(TimeUnit.DAY, 3));
		final TimeUnit unit = factory.getSimulation().getTimeUnit();
		HospitalModelConfig.setScale(scale);
		
		// Central Lab
		ModelParameterMap centralLabParams = new ModelParameterMap(CentralLabSubModel.Parameters.values().length);
		centralLabParams.put(CentralLabSubModel.Parameters.NTECH, 23);
		centralLabParams.put(CentralLabSubModel.Parameters.N24HTECH, 5);
		centralLabParams.put(CentralLabSubModel.Parameters.NNURSES, 16);
		centralLabParams.put(CentralLabSubModel.Parameters.NXNURSES, 10);
		centralLabParams.put(CentralLabSubModel.Parameters.NSLOTS, 150);
		centralLabParams.put(CentralLabSubModel.Parameters.NCENT, 160);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_SAMPLE, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 2));
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_CENT, HospitalModelConfig.getNextHighFunction(unit, 
				new TimeStamp(TimeUnit.MINUTE, 15), TimeStamp.getZero(), "ConstantVariate", 6));
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_ANALYSIS, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 8));
		centralLabParams.put(CentralLabSubModel.Parameters.NHAETECH, 2);
		centralLabParams.put(CentralLabSubModel.Parameters.NHAENURSES, 5);
		centralLabParams.put(CentralLabSubModel.Parameters.NHAESLOTS, 40);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_HAEANALYSIS, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 15));
		centralLabParams.put(CentralLabSubModel.Parameters.NMICROTECH, 10);
		centralLabParams.put(CentralLabSubModel.Parameters.NMICRONURSES, 0);
		centralLabParams.put(CentralLabSubModel.Parameters.NMICROSLOTS, 50);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_MICROANALYSIS, HospitalModelConfig.getScaledSimulationTimeFunction(unit,
				"ConstantVariate", 20));
		centralLabParams.put(CentralLabSubModel.Parameters.NPATTECH, 6);
		centralLabParams.put(CentralLabSubModel.Parameters.NPATNURSES, 1);
		centralLabParams.put(CentralLabSubModel.Parameters.NPATSLOTS, 50);
		centralLabParams.put(CentralLabSubModel.Parameters.LENGTH_PATANALYSIS, HospitalModelConfig.getScaledSimulationTimeFunction(unit, 
				"ConstantVariate", 20));
		CentralLabSubModel.createModel(factory, centralLabParams);
		Flow[] root = CentralLabSubModel.getOPFlow(factory, 0.5, 1.0, 0.07, 0.06, 0.05);
		
		ElementType et = factory.getElementTypeInstance("P");
		TimeFunction nPatients = TimeFunctionFactory.getInstance("ConstantVariate", 1000);
		ElementCreator ec = factory.getElementCreatorInstance(nPatients, et, (InitializerFlow)root[0]);
		SimulationCycle interArrival = new SimulationWeeklyPeriodicCycle(unit, WeeklyPeriodicCycle.WEEKDAYS, HospitalModelConfig.PATIENTARRIVAL, 0);
		factory.getTimeDrivenGeneratorInstance(ec, interArrival);
		
		Simulation simul = factory.getSimulation();
		simul.addInfoReceiver(new ActionsCounterView(simul, System.out));
		simul.addInfoReceiver(new ActivityQueueFileSafeView(simul, OUTPATH + "queue" + simul.getIdentifier() + ".txt", TimeStamp.getDay()));
		simul.addInfoReceiver(new ExecutionCounterFileSafeView(simul, OUTPATH + "total" + simul.getIdentifier() + ".txt", warmUp, TimeStamp.getDay()));
		simul.addInfoReceiver(new ActivityLengthFileSafeView(simul, OUTPATH + "act" + simul.getIdentifier() + ".txt", warmUp));
		simul.addInfoReceiver(new SimultaneousEventFileSafeView(simul, OUTPATH + "events" + simul.getIdentifier() + ".txt"));
		simul.addInfoReceiver(new ResourceUsageFileSafeView(simul, OUTPATH + "res" + simul.getIdentifier() + ".txt", TimeStamp.getDay()));
		simul.run();
		
	}
	@Override
	public void start() {
//		SimulationObjectFactory factory = SimulationFactory.getInstance(SimulationType.SEQUENTIAL, 0, "Big Hospital", HospitalModelConfig.UNIT, TimeStamp.getZero(), new TimeStamp(TimeUnit.MONTH, 12));
//		HospitalSubModel.createModel(factory, scale, new int[] {3,3,3,3,3,3});	
//		Simulation simul = factory.getSimulation();
//		simul.addInfoReceiver(new ActionsCounterView(simul, System.out));
//		simul.addInfoReceiver(new ActivityQueueFileSafeView(simul, OUTPATH + "queue" + simul.getIdentifier() + ".txt", viewPeriod));
//		simul.addInfoReceiver(new ExecutionCounterFileSafeView(simul, OUTPATH + "total" + simul.getIdentifier() + ".txt", warmUp, viewPeriod));
//		simul.addInfoReceiver(new ActivityLengthFileSafeView(simul, OUTPATH + "act" + simul.getIdentifier() + ".txt", warmUp));
//		simul.addInfoReceiver(new SimultaneousEventFileSafeView(simul, OUTPATH + "events" + simul.getIdentifier() + ".txt"));
//		simul.addInfoReceiver(new ResourceUsageFileSafeView(simul, OUTPATH + "res" + simul.getIdentifier() + ".txt", viewPeriod));
//		simul.run();
		normalExp();
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
