/**
 * 
 */
package es.ull.iis.simulation.retal;

import java.util.ArrayList;
import java.util.EnumSet;

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
import es.ull.iis.simulation.retal.params.DRParams;
import es.ull.iis.simulation.sequential.Simulation;
import es.ull.iis.simulation.sequential.TimeDrivenGenerator;

/**
 * @author Iván Castilla
 *
 */
public class RETALSimulation extends Simulation {
	public enum DISEASES {
		ARMD,
		DR
	};	
	/** Selects the diseases included in the model. For debug, mainly */
	public static EnumSet<DISEASES> ACTIVE_DISEASES = EnumSet.of(DISEASES.DR); 
	/** Number of interventions that will be compared for a patient. This value is also used to determine the id of the patient */ 
	public final static int NINTERVENTIONS = 2;
	/** Counter to assign a unique id to each patient */
	private int patientCounter = 0;
	private final boolean baseCase;
	public final static int NPATIENTS = 10000;
	
	private final Patient[] generatedPatients = new Patient[NPATIENTS]; 
	
	private final static double DISCOUNT_RATE = 0.0; 
	public final static TimeUnit SIMUNIT = TimeUnit.DAY;
	private final static String DESCRIPTION = "RETAL Simulation";
	private final static String[] INTERVENTION_DESC = {"Clinical detection", "Screening"};
	private final CommonParams commonParams;
	private final ARMDParams armdParams;
	private final DRParams drParams;
	private final ArrayList<Outcome> outcomes;
	private final int nIntervention;

	/**
	 * @param id
	 * @param baseCase
	 */
	public RETALSimulation(int id, boolean baseCase) {
		super(id, DESCRIPTION + " " + INTERVENTION_DESC[0], SIMUNIT, TimeStamp.getZero(), new TimeStamp(TimeUnit.YEAR, (long) (CommonParams.MAX_AGE - CommonParams.MIN_AGE + 1)));
		this.baseCase = baseCase;
		this.commonParams = new CommonParams(this, baseCase);
		this.armdParams = new ARMDParams(this, baseCase);
		this.drParams = new DRParams(this, baseCase);
		this.nIntervention = 0;
		PatientCreator creator = new PatientCreator(this, NPATIENTS, new ConstantFunction(commonParams.getInitAge()));
		new TimeDrivenGenerator(this, creator, new SimulationPeriodicCycle(TimeUnit.YEAR, (long)0, new SimulationTimeFunction(TimeUnit.DAY, "ConstantVariate", 365), 1));
		outcomes = new ArrayList<Outcome>();
		outcomes.add(new Cost(this, DISCOUNT_RATE));
		outcomes.add(new QualityAdjustedLifeExpectancy(this, DISCOUNT_RATE));
		addInfoReceivers();
	}

	public RETALSimulation(RETALSimulation original, int nIntervention) {
		super(original.id, DESCRIPTION+ " " + INTERVENTION_DESC[nIntervention], original.getTimeUnit(), original.getStartTs(), original.getEndTs());
		this.baseCase = original.baseCase;
		this.commonParams = original.commonParams;
		this.armdParams = original.armdParams;
		this.drParams = original.drParams;
		this.nIntervention = nIntervention;
		PatientCreator creator = new PatientCreator(this, original.generatedPatients, nIntervention);
		new TimeDrivenGenerator(this, creator, new SimulationPeriodicCycle(TimeUnit.YEAR, (long)0, new SimulationTimeFunction(TimeUnit.DAY, "ConstantVariate", 365), 1));
		this.outcomes = new ArrayList<Outcome>(original.outcomes);
		addInfoReceivers();
	}
	
	private void addInfoReceivers() {
//		addInfoReceiver(new PatientInfoView(this));
		addInfoReceiver(new AffectedPatientHistoryView(this, ACTIVE_DISEASES, true));
//		addInfoReceiver(new AffectedPatientHistoryVAView(this, EyeState.AMD_CNV));
		addInfoReceiver(new PatientPrevalenceView(this, ACTIVE_DISEASES));
		addInfoReceiver(new PatientCounterView(this, ACTIVE_DISEASES));
		addInfoReceiver(new PatientCounterHistogramView(this, 40, CommonParams.MAX_AGE, 5, ACTIVE_DISEASES));
	}
	
	public CommonParams getCommonParams() {
		return commonParams;
	}

	public ARMDParams getArmdParams() {
		return armdParams;
	}

	/**
	 * @return the drParams
	 */
	public DRParams getDrParams() {
		return drParams;
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
	
	public void addGeneratedPatient(Patient pat, int order) {
		generatedPatients[order] = pat;
	}

	@Override
	public void end() {
		// FIXME: Prepare to compute outcomes in case not all the patients are dead
		super.end();
//		if (nIntervention == NINTERVENTIONS - 1)
//			for (Outcome outcome : outcomes)
//				outcome.print(true);
	}
}
