/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import simkit.random.RandomNumberFactory;
import es.ull.isaatc.simulation.common.Experiment;
import es.ull.isaatc.simulation.common.Simulation;
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
import es.ull.isaatc.simulation.hospital.view.ResourceUsageFileSafeView;
import es.ull.isaatc.simulation.hospital.view.SimultaneousEventFileSafeView;
import es.ull.isaatc.util.Output;

class BigHospitalExperiment extends Experiment {
	private static int debug = 0;
	private static final String SEP = "----------------------------------------";

	private static final String OUTPATH = "N:\\Tesis\\hResults\\";
	private static final int NEXP = 1;
	private static final SimulationFactory.SimulationType []simTypes = {SimulationType.GROUPED3PHASEX, SimulationType.GROUPEDX};
	private static final TimeStamp warmUp = TimeStamp.getZero();
	private static final TimeStamp viewPeriod = new TimeStamp(TimeUnit.WEEK, 1);
	private static final TimeStamp endTs = new TimeStamp(TimeUnit.MONTH, 24);
	private static final TimeStamp scale = new TimeStamp(TimeUnit.MINUTE, 5);
	private static final int [] nServices = {2,2,2,2,2,2};

	public BigHospitalExperiment() {
		super("Big Hospital Experiments", NEXP);
	}

	private void addViews(Simulation simul, int debug) {
		simul.addInfoReceiver(new ActionsCounterView(simul, System.out));
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

	@Override
	public void start() {
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
		for (int i = 0; i < nExperiments; i++) {
			System.out.println(SimulationType.SEQUENTIAL + "\t" + i);
			factory = SimulationFactory.getInstance(SimulationType.SEQUENTIAL, simIndex++, "Big Hospital", HospitalModelConfig.UNIT, TimeStamp.getZero(), endTs);
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
