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
import es.ull.iis.simulation.hta.diabetes.params.TimeToEventParam;

/**
 * An abstract class representing the evolution of a chronic complication. Chronic complications can be represented 
 * as a progression of stages of any complexity, and incur in annual costs and disutilities. Additionally, starting
 * each stage of the complication can incur in one-time costs.
 * @author Iv�n Castilla Rodr�guez
 *
 */
public abstract class ChronicComplicationSubmodel extends ComplicationSubmodel {
	private final TreeMap<DiabetesComplicationStage, DiabetesComplicationStage.Instance> data;
	private final TimeToEventParam[] time2Event;
	protected final SecondOrderChronicComplicationSubmodel secOrder;
	
	/**
	 * Creates a submodel for a chronic complication.
	 */
	public ChronicComplicationSubmodel(SecondOrderChronicComplicationSubmodel secOrder) {
		super();
		this.secOrder = secOrder;
		this.data = new TreeMap<>();
		this.time2Event = new TimeToEventParam[secOrder.getNTransitions()];
	}

	public void setStageInstance(DiabetesComplicationStage stage, SecondOrderParamsRepository secParams) {
		final double initP = secParams.getInitProbParam(stage);
		data.put(stage, stage.getInstance(secParams.getDisutilityForChronicComplication(stage), 
				secParams.getCostsForChronicComplication(stage),
				initP, secParams.getIMR(stage), secParams.getnPatients()));
	}

	public void setStageInstance(DiabetesComplicationStage stage, double du, double[] cost, double initP, double imr, int nPatients) {
		data.put(stage, stage.getInstance(du, cost, initP, imr, nPatients)); 
	}
	
	public double getDisutility(DiabetesComplicationStage stage) {
		return data.get(stage).getDisutility();
	}
	
	public double[] getCosts(DiabetesComplicationStage stage) {
		return data.get(stage).getCosts();
	}
	
	public boolean hasComplicationAtStart(DiabetesComplicationStage stage, DiabetesPatient pat) {
		return data.get(stage).hasComplicationAtStart(pat);
	}
	
	public double getIMR(DiabetesComplicationStage stage) {
		return data.get(stage).getIMR();
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
	 * Adds progression actions in case they are needed. First checks if the new time to event is valid. Then checks
	 * if there was a previously scheduled event and adds a "cancel" action. Finally, adds a "new" action for the new time.
	 * @param prog Current progression of the patient
	 * @param stage Chronic complication stage
	 * @param timeToEvent New time to event
	 * @param previousTimeToEvent Previous time to event
	 */
	public void adjustProgression(DiabetesProgression prog, DiabetesComplicationStage stage, long timeToEvent, long previousTimeToEvent) {
		// Check previously scheduled events
		if (timeToEvent != Long.MAX_VALUE) {
			if (previousTimeToEvent < Long.MAX_VALUE) {
				prog.addCancelEvent(stage);
			}
			prog.addNewEvent(stage, timeToEvent);
		}
	}
	
	/**
	 * Returns the initial set of stages (one or more) that the patient will start with when this complication appears. 
	 * @param pat A patient
	 * @return the initial set of stages that the patient will start with when this complication appears
	 */
	public TreeSet<DiabetesComplicationStage> getInitialStage(DiabetesPatient pat) {
		final TreeSet<DiabetesComplicationStage> init = new TreeSet<>();
		for (final DiabetesComplicationStage stage : secOrder.getStages()) {
			if (hasComplicationAtStart(stage, pat))
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
		return getCosts(newComplication)[1];		
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
