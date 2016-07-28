/**
 * 
 */
package es.ull.iis.simulation.retal;

import es.ull.iis.function.ConstantFunction;
import es.ull.iis.simulation.core.SimulationPeriodicCycle;
import es.ull.iis.simulation.core.SimulationTimeFunction;
import es.ull.iis.simulation.core.TimeStamp;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.params.ARMDParams;
import es.ull.iis.simulation.retal.params.CommonParams;
import es.ull.iis.simulation.sequential.Simulation;
import es.ull.iis.simulation.sequential.TimeDrivenGenerator;

/**
 * @author Iván Castilla
 *
 */
public class RETALSimulation extends Simulation {
	public final static TimeUnit SIMUNIT = TimeUnit.DAY;
	private final static String DESCRIPTION = "RETAL Simulation";
	private final CommonParams commonParams;
	private final ARMDParams armdParams;

	/**
	 * @param id
	 * @param baseCase
	 */
	public RETALSimulation(int id, CommonParams commonParams, ARMDParams armdParams, int nPatients) {
		super(id, DESCRIPTION, SIMUNIT, TimeStamp.getZero(), new TimeStamp(TimeUnit.YEAR, (long) (CommonParams.MAX_AGE - commonParams.getInitAge() + 1)));
		this.commonParams = commonParams;
		this.armdParams = armdParams;
		PatientCreator creator = new OphthalmologicPatientCreator(this, nPatients, commonParams.getPMen(), new ConstantFunction(commonParams.getInitAge()));
		new TimeDrivenGenerator(this, creator, new SimulationPeriodicCycle(TimeUnit.YEAR, (long)0, new SimulationTimeFunction(TimeUnit.DAY, "ConstantVariate", 365), 1));
//		addInfoReceiver(new PatientInfoView(this));
		addInfoReceiver(new PatientCounterView(this));
		addInfoReceiver(new PatientCounterHistogramView(this, 40, CommonParams.MAX_AGE, 5, true));
	}

	/**
	 * @return the deathtime
	 */
	protected long getTimeToDeath(double age, int sex) {
		final double time = commonParams.getDeathTime(age, sex);
		return getTs() + unit.convert(time, TimeUnit.YEAR);
	}

	/**
	 * Returns years to first eye incidence of early ARM; Long.MAX_VALUE if event is not happening
	 * @param age
	 * @return
	 */
	protected long getTimeToEARM(OphthalmologicPatient pat) {
		return armdParams.getTimeToEARM().getValidatedTimeToEvent(pat, true);
	}

	protected long getTimeToAMD(OphthalmologicPatient pat) {
		return armdParams.getTimeToAMD().getValidatedTimeToEvent(pat, true);
	}
	
	protected long getTimeToAMDFromEARM(OphthalmologicPatient pat, boolean firstEye) {
		return armdParams.getTimeToAMDFromEARM().getValidatedTimeToEvent(pat, firstEye);
	}

	/**
	 * 
	 * @param pat Patient with GA in an eye that may progress to CNV
	 * @param firstEye True if the event applies to the first eye; false if the event applies to the fellow eye
	 * @return time to 
	 */
	protected long getTimeToCNVFromGA(OphthalmologicPatient pat, boolean firstEye) {
		return armdParams.getTimeToE1CNV().getValidatedTimeToEvent(pat, firstEye); 
	}

	protected double getProbabilityCNV(OphthalmologicPatient pat) {
		return armdParams.getProbabilityCNV(pat.getAge());
	}
	
}
