/**
 * 
 */
package es.ull.iis.simulation.retal;

import es.ull.iis.simulation.core.SimulationPeriodicCycle;
import es.ull.iis.simulation.core.SimulationTimeFunction;
import es.ull.iis.simulation.core.TimeStamp;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.sequential.Simulation;
import es.ull.iis.simulation.sequential.TimeDrivenGenerator;
import es.ull.isaatc.function.ConstantFunction;

/**
 * @author Iván Castilla
 *
 */
public class RETALSimulation extends Simulation {
	public final static TimeUnit SIMUNIT = TimeUnit.DAY;
	private final static String DESCRIPTION = "RETAL Simulation";
	public final static TimeStamp SIMSTART = TimeStamp.getZero();
	public final static TimeStamp SIMEND = new TimeStamp(TimeUnit.YEAR, 61);
	public final static int NPATIENTS = 1000;
	public final static double P_MEN = 0.5;
	private final CommonParams commonParams;
	private final ARMDParams armdParams;

	/**
	 * @param id
	 * @param baseCase
	 */
	public RETALSimulation(int id, CommonParams commonParams, ARMDParams armdParams) {
		super(id, DESCRIPTION, SIMUNIT, SIMSTART, SIMEND);
		this.commonParams = commonParams;
		this.armdParams = armdParams;
		PatientCreator creator = new OphthalmologicPatientCreator(this, new ConstantFunction(NPATIENTS), P_MEN);
		new TimeDrivenGenerator(this, creator, new SimulationPeriodicCycle(TimeUnit.YEAR, (long)0, new SimulationTimeFunction(TimeUnit.DAY, "ConstantVariate", 365), 1));
		addInfoReceiver(new PatientInfoView(this));
		addInfoReceiver(new PatientCounterView(this));
		addInfoReceiver(new PatientCounterHistogramView(this, 40, CommonParams.MAX_AGE, 5));
	}

	/**
	 * @return the deathtime
	 */
	protected long getTimeToDeath(Patient pat) {
		return getTs() + unit.convert(commonParams.getDeathTime(pat.getAge(), pat.getSex()), TimeUnit.YEAR);
	}

	protected long getTimeToEARM(OphthalmologicPatient pat) {
		return getTs() +  unit.convert(armdParams.getEARMTime(pat.getAge()), TimeUnit.YEAR);
	}

	protected long getTimeToAMD(OphthalmologicPatient pat) {
		return getTs() +  unit.convert(armdParams.getAMDTime(pat.getAge()), TimeUnit.YEAR);
	}
	
	protected long getTimeToAMDFromEARM(OphthalmologicPatient pat) {
		return getTs() +  unit.convert(armdParams.getEARM2AMDTime(pat.getAge(), pat.getDiseaseStage().contains(OphthalmologicPatient.DiseaseStage.EARM2)), TimeUnit.YEAR);		
	}

	protected double getProbabilityCNV(OphthalmologicPatient pat) {
		return armdParams.getProbabilityCNV(pat.getAge());
	}
	
	protected double getTimeToCNVFromGA(OphthalmologicPatient pat) {
		
	}
}
