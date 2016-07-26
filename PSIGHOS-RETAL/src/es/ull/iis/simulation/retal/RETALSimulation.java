/**
 * 
 */
package es.ull.iis.simulation.retal;

import java.util.EnumSet;

import es.ull.iis.simulation.core.SimulationPeriodicCycle;
import es.ull.iis.simulation.core.SimulationTimeFunction;
import es.ull.iis.simulation.core.TimeStamp;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.params.ARMDParams;
import es.ull.iis.simulation.retal.params.CommonParams;
import es.ull.iis.simulation.sequential.Simulation;
import es.ull.iis.simulation.sequential.TimeDrivenGenerator;
import es.ull.iis.function.ConstantFunction;

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

	protected long getTimeToEARM(double age) {
		final double time = armdParams.getEARMTime(age);
		return (time == Double.MAX_VALUE) ? Long.MAX_VALUE : getTs() +  unit.convert(time, TimeUnit.YEAR);
	}

	protected long getTimeToAMD(double age) {
		final double time = armdParams.getAMDTime(age, EnumSet.of(EyeState.HEALTHY), EnumSet.of(EyeState.HEALTHY));
		return (time == Double.MAX_VALUE) ? Long.MAX_VALUE : getTs() +  unit.convert(time, TimeUnit.YEAR);
	}
	
	protected long getTimeToAMDFromEARM(OphthalmologicPatient pat) {
		final double time = armdParams.getEARM2AMDTime(pat.getAge(), pat.getEye2State());
		return (time == Double.MAX_VALUE) ? Long.MAX_VALUE : getTs() +  unit.convert(time, TimeUnit.YEAR);		
	}

	protected double getProbabilityCNV(OphthalmologicPatient pat) {
		return armdParams.getProbabilityCNV(pat.getAge());
	}
	
	/**
	 * 
	 * @param pat Patient with GA in an eye that may progress to CNV
	 * @param fellowEye State of the fellow eye.
	 * @return time to 
	 */
	protected long getTimeToCNVFromGA(OphthalmologicPatient pat, EnumSet<EyeState> fellowEye) {
		final double time = armdParams.getGA2CNVTime(pat.getAge(), fellowEye); 
		return (time == Double.MAX_VALUE) ? Long.MAX_VALUE : getTs() +  unit.convert(time, TimeUnit.YEAR);	
	}
}
