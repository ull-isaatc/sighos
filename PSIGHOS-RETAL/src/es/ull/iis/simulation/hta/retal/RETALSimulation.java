/**
 * 
 */
package es.ull.iis.simulation.hta.retal;

import java.util.EnumSet;

import es.ull.iis.function.ConstantFunction;
import es.ull.iis.simulation.hta.HTASimulation;
import es.ull.iis.simulation.hta.Intervention;
import es.ull.iis.simulation.hta.retal.inforeceiver.PatientPrevalenceView;
import es.ull.iis.simulation.hta.retal.outcome.Cost;
import es.ull.iis.simulation.hta.retal.outcome.QualityAdjustedLifeExpectancy;
import es.ull.iis.simulation.hta.retal.params.ARMDParams;
import es.ull.iis.simulation.hta.retal.params.CommonParams;
import es.ull.iis.simulation.hta.retal.params.DRParams;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iván Castilla
 *
 */
public class RETALSimulation extends HTASimulation {
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

	/**
	 * @param id
	 * @param secondOrder
	 */
	public RETALSimulation(int id, boolean baseCase, Intervention intervention) {
		super(id, DESCRIPTION, SIMUNIT, baseCase, intervention, new TimeStamp(TimeUnit.YEAR, (long) (CommonParams.MAX_AGE - CommonParams.MIN_AGE + 1)), NINTERVENTIONS, NPATIENTS);
		this.commonParams = new CommonParams();
		this.armdParams = new ARMDParams();
		this.drParams = new DRParams();
		new PatientCreator(this, NPATIENTS, new ConstantFunction(commonParams.getInitAge()), intervention, 
				new SimulationPeriodicCycle(TimeUnit.YEAR, (long)0, new SimulationTimeFunction(TimeUnit.DAY, "ConstantVariate", 365), 1));
		cost = new Cost(this, DISCOUNT_RATE);
		qaly = new QualityAdjustedLifeExpectancy(this, DISCOUNT_RATE);
		addInfoReceivers();
	}

	public RETALSimulation(RETALSimulation original, Intervention intervention) {
		super(original, intervention);
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

}
