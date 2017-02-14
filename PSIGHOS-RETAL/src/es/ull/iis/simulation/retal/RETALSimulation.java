/**
 * 
 */
package es.ull.iis.simulation.retal;

import java.util.EnumSet;

import es.ull.iis.function.ConstantFunction;
import es.ull.iis.simulation.core.SimulationPeriodicCycle;
import es.ull.iis.simulation.core.SimulationTimeFunction;
import es.ull.iis.simulation.core.TimeStamp;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.inforeceiver.AffectedPatientHistoryVAView;
import es.ull.iis.simulation.retal.inforeceiver.AffectedPatientHistoryView;
import es.ull.iis.simulation.retal.inforeceiver.DiagnosticView;
import es.ull.iis.simulation.retal.inforeceiver.ICERView;
import es.ull.iis.simulation.retal.inforeceiver.PatientCounterHistogramView;
import es.ull.iis.simulation.retal.inforeceiver.PatientCounterView;
import es.ull.iis.simulation.retal.inforeceiver.PatientInfoView;
import es.ull.iis.simulation.retal.inforeceiver.PatientPrevalenceView;
import es.ull.iis.simulation.retal.outcome.Cost;
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
	public static EnumSet<DISEASES> ACTIVE_DISEASES = EnumSet.of(DISEASES.ARMD, DISEASES.DR); 
	/** Number of interventions that will be compared for a patient. This value is also used to determine the id of the patient */ 
	public final static int NINTERVENTIONS = 2;
	private final static String[] INTERVENTION_DESC = {"Clinical detection", "Screening"};
	private final static double DISCOUNT_RATE = 0.03; 
	private final static String DESCRIPTION = "RETAL Simulation";
	/** Counter to assign a unique id to each patient */
	private int patientCounter = 0;
	public final static int NPATIENTS = 1000;
	public final static TimeUnit SIMUNIT = TimeUnit.DAY;
	
	private final Patient[] generatedPatients = new Patient[NPATIENTS]; 
	private final boolean baseCase;
	private final CommonParams commonParams;
	private final ARMDParams armdParams;
	private final DRParams drParams;
	private final Intervention intervention;
	private final Cost cost;
	private final QualityAdjustedLifeExpectancy qaly;
	/** True if this is a clone of an original simulation; false otherwise */
	private final boolean cloned;

	/**
	 * @param id
	 * @param baseCase
	 */
	public RETALSimulation(int id, boolean baseCase, Intervention intervention) {
		super(id, DESCRIPTION + " " + INTERVENTION_DESC[intervention.getId()], SIMUNIT, TimeStamp.getZero(), new TimeStamp(TimeUnit.YEAR, (long) (CommonParams.MAX_AGE - CommonParams.MIN_AGE + 1)));
		this.cloned = false;
		this.baseCase = baseCase;
		this.commonParams = new CommonParams(baseCase);
		this.armdParams = new ARMDParams(baseCase);
		this.drParams = new DRParams(baseCase);
		this.intervention = intervention;
		PatientCreator creator = new PatientCreator(this, NPATIENTS, new ConstantFunction(commonParams.getInitAge()), intervention);
		new TimeDrivenGenerator(this, creator, new SimulationPeriodicCycle(TimeUnit.YEAR, (long)0, new SimulationTimeFunction(TimeUnit.DAY, "ConstantVariate", 365), 1));
		cost = new Cost(this, DISCOUNT_RATE);
		qaly = new QualityAdjustedLifeExpectancy(this, DISCOUNT_RATE);
		addInfoReceivers();
	}

	public RETALSimulation(RETALSimulation original, Intervention intervention) {
		super(original.id, DESCRIPTION+ " " + INTERVENTION_DESC[intervention.getId()], original.getTimeUnit(), original.getStartTs(), original.getEndTs());
		this.cloned = true;
		this.intervention = intervention;
		this.baseCase = original.baseCase;
		this.commonParams = original.commonParams;
		this.armdParams = original.armdParams;
		this.drParams = original.drParams;
		PatientCreator creator = new PatientCreator(this, original.generatedPatients, intervention);
		new TimeDrivenGenerator(this, creator, new SimulationPeriodicCycle(TimeUnit.YEAR, (long)0, new SimulationTimeFunction(TimeUnit.DAY, "ConstantVariate", 365), 1));
		this.cost = original.cost;
		this.qaly = original.qaly;
		addInfoReceivers();
	}
	
	private void addInfoReceivers() {
//		addInfoReceiver(new PatientInfoView(this));
//		addInfoReceiver(new AffectedPatientHistoryView(this, ACTIVE_DISEASES, true));
//		addInfoReceiver(new AffectedPatientHistoryVAView(this, ACTIVE_DISEASES, true));
//		addInfoReceiver(new DiagnosticView(this));
//		addInfoReceiver(new PatientPrevalenceView(this, ACTIVE_DISEASES));
//		addInfoReceiver(new PatientCounterView(this, ACTIVE_DISEASES));
//		addInfoReceiver(new PatientCounterHistogramView(this, 40, CommonParams.MAX_AGE, 5, ACTIVE_DISEASES));
//		addInfoReceiver(new ICERView(this, false, true, true, false));
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
	 * @return the cost
	 */
	public Cost getCost() {
		return cost;
	}

	/**
	 * @return the qaly
	 */
	public QualityAdjustedLifeExpectancy getQaly() {
		return qaly;
	}

	/**
	 * @return the patientCounter
	 */
	public int getPatientCounter() {
		return patientCounter++;
	}

	/**
	 * @return False if this is a copy of another simulation; true otherwise
	 */
	public boolean isCloned() {
		return cloned;
	}

	/**
	 * 
	 * @return The intervention being analyzed with this simulation
	 */
	public Intervention getIntervention() {
		return intervention;
	}
	
	public void addGeneratedPatient(Patient pat, int order) {
		generatedPatients[order] = pat;
	}

	@Override
	public void end() {
		// FIXME: Prepare to compute outcomes in case not all the patients are dead
		super.end();
//		cost.print(true, false);
//		qaly.print(true, false);
	}
}
