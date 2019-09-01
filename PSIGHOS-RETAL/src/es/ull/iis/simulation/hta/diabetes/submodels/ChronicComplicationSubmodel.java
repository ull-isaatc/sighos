/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import java.util.TreeMap;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesProgression;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.params.StartWithComplicationParam;
import es.ull.iis.simulation.hta.diabetes.params.TimeToEventParam;

/**
 * An abstract class representing the evolution of a chronic complication. Chronic complications can be represented 
 * as a progression of stages of any complexity, and incur in annual costs and disutilities. Additionally, starting
 * each stage of the complication can incur in one-time costs.
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class ChronicComplicationSubmodel extends ComplicationSubmodel {
	private final TreeMap<DiabetesComplicationStage, StageData> data;
	private final TimeToEventParam[] time2Event;
	protected final SecondOrderChronicComplicationSubmodel secOrder;
	
	public class StageData {
		private final double du;
		private final double[] cost;
		private final StartWithComplicationParam initP;
		/**
		 * @param du
		 * @param cost
		 * @param initP
		 */
		public StageData(double du, double[] cost, StartWithComplicationParam initP) {
			this.du = du;
			this.cost = cost;
			this.initP = initP;
		}
		
		public double getDisutility() {
			return du;
		}
		public double[] getCosts() {
			return cost;
		}
		public boolean hasComplicationAtStart(DiabetesPatient pat) {
			return (initP == null) ? false : initP.getValue(pat);
		}
	}
	/**
	 * Creates a submodel for a chronic complication.
	 */
	public ChronicComplicationSubmodel(SecondOrderChronicComplicationSubmodel secOrder) {
		super();
		this.secOrder = secOrder;
		this.data = new TreeMap<>();
		this.time2Event = new TimeToEventParam[secOrder.getNTransitions()];
	}

	public void addData(SecondOrderParamsRepository secParams, DiabetesComplicationStage stage) {
		final double initP = secParams.getInitProbParam(stage);
		data.put(stage, new StageData(secParams.getDisutilityForChronicComplication(stage),
			secParams.getCostsForChronicComplication(stage), 
			(initP > 0.0) ? new StartWithComplicationParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), secParams.getnPatients(), initP) : null));
	}

	public void addData(double du, double[] cost, double initP, SecondOrderParamsRepository secParams, DiabetesComplicationStage stage) {
		data.put(stage, new StageData(du, cost,
			(initP > 0.0) ? new StartWithComplicationParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), secParams.getnPatients(), initP) : null)); 
	}
	public StageData getData(DiabetesComplicationStage stage) {
		return data.get(stage);
	}
	
	/**
	 * Adds a time to event parameter related to a specific transition
	 * @param id The identifier of the transition. Transitions are expected to be ordered elsewhere.
	 * @param param Time to event parameter
	 */
	public void addTime2Event(int id, TimeToEventParam param) {
		time2Event[id] = param;
	}
	
	/**
	 * Returns the time to event for a patient
	 * @param pat A patient
	 * @param id The identifier of the transition. Transitions are expected to be ordered elsewhere.
	 * @param limit The upper limit for the occurrence of the event. If the computed time to event is higher or equal 
	 * than the limit, returns Long.MAX_VALUE
	 * @return The time to event for the patient; Long.MAX_VALUE if the event will never happen.
	 */
	public long getTimeToEvent(DiabetesPatient pat, int id, long limit) {
		final long time = time2Event[id].getValue(pat);
		return (time >= limit) ? Long.MAX_VALUE : time;
	}

	
	/**
	 * Returns how this patient will progress from its current state with regards to this
	 * chronic complication. The progress can include removal of events already scheduled, modification of 
	 * previously scheduled events and new events.
	 * @param pat A patient
	 * @return how this patient will progress from its current state with regards to this
	 * chronic complication
	 */
	public abstract DiabetesProgression getProgression(DiabetesPatient pat);
	
	/**
	 * Returns the initial set of stages (one or more) that the patient will start with when this complication appears. 
	 * @param pat A patient
	 * @return the initial set of stages that the patient will start with when this complication appears
	 */
	public TreeSet<DiabetesComplicationStage> getInitialStage(DiabetesPatient pat) {
		final TreeSet<DiabetesComplicationStage> init = new TreeSet<>();
		for (final DiabetesComplicationStage stage : secOrder.getStages()) {
			if (getData(stage).hasComplicationAtStart(pat))
				init.add(stage);
		}
		return init;
		
	}
	
	/**
	 * Returns the annual cost associated to the current state of the patient and during the defined period
	 * @param pat A patient
	 * @param initAge Starting time of the period (in years)
	 * @param endAge Ending time of the period
	 * @return the annual cost associated to the current state of the patient and during the defined period
	 */
	public abstract double getAnnualCostWithinPeriod(DiabetesPatient pat, double initAge, double endAge);
	
	/**
	 * Returns the cost associated to the start of a new stage of this chronic complication
	 * @param pat A patient
	 * @param newComplication New stage of this chronic complication
	 * @return the cost associated to the start of a new stage of this chronic complication
	 */
	public double getCostOfComplication(DiabetesPatient pat, DiabetesComplicationStage newComplication) {
		return getData(newComplication).getCosts()[1];		
	}
	
	/**
	 * Returns the disutility value associated to the current stage of this chronic complication
	 * @param pat A patient
	 * @param method Method used to compute the disutility of this chronic complication in case the 
	 * complication allows for several stages to be concurrently active
	 * @return The disutility value associated to the current stage of this chronic complication
	 */
	public abstract double getDisutility(DiabetesPatient pat, DisutilityCombinationMethod method);
	
}
