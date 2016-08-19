/**
 * 
 */
package es.ull.iis.simulation.retal;

import java.util.ArrayList;

import es.ull.iis.function.ConstantFunction;
import es.ull.iis.simulation.core.SimulationPeriodicCycle;
import es.ull.iis.simulation.core.SimulationTimeFunction;
import es.ull.iis.simulation.core.TimeStamp;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.inforeceiver.AffectedPatientHistoryVAView;
import es.ull.iis.simulation.retal.inforeceiver.AffectedPatientHistoryView;
import es.ull.iis.simulation.retal.inforeceiver.PatientCounterHistogramView;
import es.ull.iis.simulation.retal.inforeceiver.PatientCounterView;
import es.ull.iis.simulation.retal.inforeceiver.PatientInfoView;
import es.ull.iis.simulation.retal.inforeceiver.PatientPrevalenceView;
import es.ull.iis.simulation.retal.outcome.Cost;
import es.ull.iis.simulation.retal.outcome.Outcome;
import es.ull.iis.simulation.retal.outcome.QualityAdjustedLifeExpectancy;
import es.ull.iis.simulation.retal.params.ARMDParams;
import es.ull.iis.simulation.retal.params.CommonParams;
import es.ull.iis.simulation.sequential.Simulation;
import es.ull.iis.simulation.sequential.TimeDrivenGenerator;

/**
 * @author Iván Castilla
 *
 */
public class RETALSimulation extends Simulation {
	/** Number of interventions that will be compared for a patient. This value is also used to determine the id of the patient */ 
	public final static int NINTERVENTIONS = 2;
	/** Counter to assign a unique id to each patient */
	private int patientCounter = 0;
	
	private final static double DISCOUNT_RATE = 0.0; 
	public final static int NPATIENTS = 1000;
	public final static TimeUnit SIMUNIT = TimeUnit.DAY;
	private final static String DESCRIPTION = "RETAL Simulation";
	private final CommonParams commonParams;
	private final ARMDParams armdParams;
	private final ArrayList<Outcome> outcomes = new ArrayList<Outcome>();

	/**
	 * @param id
	 * @param baseCase
	 */
	public RETALSimulation(int id) {
		super(id, DESCRIPTION, SIMUNIT, TimeStamp.getZero(), new TimeStamp(TimeUnit.YEAR, (long) (CommonParams.MAX_AGE - CommonParams.MIN_AGE + 1)));
		this.commonParams = new CommonParams(this, true);
		this.armdParams = new ARMDParams(this, true);
		PatientCreator creator = new OphthalmologicPatientCreator(this, NPATIENTS, commonParams.getPMen(), new ConstantFunction(commonParams.getInitAge()));
		new TimeDrivenGenerator(this, creator, new SimulationPeriodicCycle(TimeUnit.YEAR, (long)0, new SimulationTimeFunction(TimeUnit.DAY, "ConstantVariate", 365), 1));
//		addInfoReceiver(new PatientInfoView(this));
		addInfoReceiver(new AffectedPatientHistoryView(this, true, EyeState.AMD_CNV));
		addInfoReceiver(new AffectedPatientHistoryVAView(this, EyeState.AMD_CNV));
		addInfoReceiver(new PatientPrevalenceView(this));
//		addInfoReceiver(new PatientCounterView(this));
		addInfoReceiver(new PatientCounterHistogramView(this, 40, CommonParams.MAX_AGE, 5));
		outcomes.add(new Cost(this, DISCOUNT_RATE));
		outcomes.add(new QualityAdjustedLifeExpectancy(this, DISCOUNT_RATE));
	}

	public CommonParams getCommonParams() {
		return commonParams;
	}

	public ARMDParams getArmdParams() {
		return armdParams;
	}

	/**
	 * @return the patientCounter
	 */
	public int getPatientCounter() {
		return patientCounter++;
	}

	/**
	 * @return the outcomes
	 */
	public ArrayList<Outcome> getOutcomes() {
		return outcomes;
	}

	@Override
	public void end() {
		// FIXME: Prepare to compute outcomes in case not all the patients are dead
		super.end();
	}
}
