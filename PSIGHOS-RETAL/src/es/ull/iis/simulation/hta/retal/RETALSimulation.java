/**
 * 
 */
package es.ull.iis.simulation.hta.retal;

import java.util.EnumSet;

import es.ull.iis.function.ConstantFunction;
import es.ull.iis.simulation.hta.retal.inforeceiver.PatientPrevalenceView;
import es.ull.iis.simulation.hta.retal.outcome.Cost;
import es.ull.iis.simulation.hta.retal.outcome.QualityAdjustedLifeExpectancy;
import es.ull.iis.simulation.hta.retal.params.ARMDParams;
import es.ull.iis.simulation.hta.retal.params.CommonParams;
import es.ull.iis.simulation.hta.retal.params.DRParams;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.TimeUnit;

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
	private final static double DISCOUNT_RATE = 0.03; 
	private final static String DESCRIPTION = "RETAL Simulation";
	public final static int NPATIENTS = 1000;
	public final static TimeUnit SIMUNIT = TimeUnit.DAY;
	private final CommonParams commonParams;
	private final ARMDParams armdParams;
	private final DRParams drParams;
	protected Cost cost;
	protected QualityAdjustedLifeExpectancy qaly;
	/** Counter to assign a unique id to each patient */
	private int patientCounter = 0;
	public final static int DEF_NPATIENTS = 1000;

	protected final Intervention intervention;
	protected final int nPatients;
	
	/** True if this is a clone of an original simulation; false otherwise */
	protected final boolean cloned;
	
	protected final RetalPatient[] generatedPatients; 

	/**
	 * @param id
	 * @param secondOrder
	 */
	public RETALSimulation(int id, Intervention intervention) {
		super(id, DESCRIPTION + " " + intervention.getDescription(), SIMUNIT, 0L, SIMUNIT.convert((CommonParams.MAX_AGE - CommonParams.MIN_AGE + 1), TimeUnit.YEAR));
		this.commonParams = new CommonParams();
		this.armdParams = new ARMDParams();
		this.drParams = new DRParams();
		new PatientCreator(this, NPATIENTS, new ConstantFunction(commonParams.getInitAge()), intervention, 
				new SimulationPeriodicCycle(TimeUnit.YEAR, (long)0, new SimulationTimeFunction(TimeUnit.DAY, "ConstantVariate", 365), 1));
		cost = new Cost(NINTERVENTIONS, this, DISCOUNT_RATE);
		qaly = new QualityAdjustedLifeExpectancy(NINTERVENTIONS, this, DISCOUNT_RATE);
		this.cloned = false;
		this.intervention = intervention;
		this.nPatients = NPATIENTS;
		this.generatedPatients = new RetalPatient[nPatients];
		addInfoReceivers();
	}

	public RETALSimulation(RETALSimulation original, Intervention intervention) {
		super(original.id, DESCRIPTION + " " + intervention.getDescription(), original.getTimeUnit(), original.getStartTs(), original.getEndTs());
		this.cloned = true;
		this.intervention = intervention;
		this.nPatients = original.nPatients;
		this.generatedPatients = new RetalPatient[nPatients];
		this.commonParams = original.commonParams;
		this.armdParams = original.armdParams;
		this.drParams = original.drParams;
		new PatientCreator(this, original.generatedPatients, intervention, 
				new SimulationPeriodicCycle(TimeUnit.YEAR, (long)0, new SimulationTimeFunction(TimeUnit.DAY, "ConstantVariate", 365), 1));
		this.cost = original.cost;
		this.qaly = original.qaly;
		addInfoReceivers();
	}
	
	private void addInfoReceivers() {
//		addInfoReceiver(new PatientInfoView(this));
//		addInfoReceiver(new AffectedPatientHistoryView(this, ACTIVE_DISEASES, true));
//		addInfoReceiver(new AffectedPatientHistoryVAView(this, ACTIVE_DISEASES, true));
//		addInfoReceiver(new DiagnosticView(this));
		addInfoReceiver(new PatientPrevalenceView(ACTIVE_DISEASES));
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
	 * @return the cost for this simulation
	 */
	public Cost getCost() {
		return cost;
	}

	/**
	 * @return the Qality-adjusted life expectancy for this simulation
	 */
	public QualityAdjustedLifeExpectancy getQALY() {
		return qaly;
	}

	/**
	 * @return the nPatients
	 */
	public int getnPatients() {
		return nPatients;
	}

	/**
	 * Returns the counter of patients created
	 * @return the counter of patients created
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
	
	/**
	 * Adds a new patient
	 * @param pat A patient
	 * @param index Order of the patient
	 */
	public void addGeneratedPatient(RetalPatient pat, int index) {
		generatedPatients[index] = pat;
	}

	/**
	 * Returns the specified generated patient
	 * @param index Order of the patient
	 * @return the specified generated patient; null if the index is not valid
	 */
	public RetalPatient getGeneratedPatient(int index) {
		return (index < 0 || index >= nPatients) ? null : generatedPatients[index];
	}
}
