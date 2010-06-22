/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import es.ull.isaatc.simulation.common.Experiment;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.factory.SimulationFactory;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.inforeceiver.StdInfoView;
import es.ull.isaatc.util.Output;

class BigHospitalExperiment extends Experiment {
	private static final int NEXP = 1;
	private static final TimeUnit unit = TimeUnit.MINUTE;
	private static final SimulationFactory.SimulationType simType = SimulationType.GROUPEDX;
	private static final TimeStamp warmup = new TimeStamp(TimeUnit.MONTH, 1);
	private static final TimeStamp endTs = new TimeStamp(TimeUnit.MONTH, 6);
	private static final int threads = 4;
	private int debug = 1;

	public BigHospitalExperiment() {
		super("Big Hospital Experiments", NEXP);
	}
	
	@Override
	public Simulation getSimulation(int ind) {
		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, ind, "Big Hospital", unit, TimeStamp.getZero(), endTs);
		
		// Common services
		CentralServicesSubModel.createModel(factory);
		SurgicalSubModel.createModel(factory);
		
		new GynaecologySubModel(factory).createModel();
		
		Simulation sim = factory.getSimulation();
		sim.setNThreads(threads);
		if (debug == 1)
			sim.addInfoReceiver(new StdInfoView(sim));
		else if (debug == 2)
			sim.setOutput(new Output(true));
		return sim;
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
	public static final int MAXENTITIESXSERVICE = 100;
	public static final int CENTRALSERVICESID = 0;
	public static final int SURGICALID = MAXENTITIESXSERVICE;
	public static final int GYNAECOLOGYID = MAXENTITIESXSERVICE * 2;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new BigHospitalExperiment().start();
	}

}
