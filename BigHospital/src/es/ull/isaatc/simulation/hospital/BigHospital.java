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

class BigHospitalExperiment extends Experiment {
	private static final int NEXP = 1;
	private static final TimeUnit unit = TimeUnit.MINUTE;
	private static final SimulationFactory.SimulationType simType = SimulationType.GROUPEDX;
	private static final TimeStamp endTs = new TimeStamp(TimeUnit.DAY, 1);

	public BigHospitalExperiment() {
		super("Big Hospital Experiments", NEXP);
	}
	
	@Override
	public Simulation getSimulation(int ind) {
		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, ind, "Big Hospital", unit, TimeStamp.getZero(), endTs);
		Simulation sim = factory.getSimulation();
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
	public static final int CENTRALSERVICESID = 0;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new BigHospitalExperiment().start();
	}

}
