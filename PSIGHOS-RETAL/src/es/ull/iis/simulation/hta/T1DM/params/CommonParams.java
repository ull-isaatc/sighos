/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import java.util.ArrayList;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.T1DM.ComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.DeathSubmodel;
import es.ull.iis.simulation.hta.T1DM.MainComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMComorbidity;
import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.T1DMProgression;
import es.ull.iis.simulation.hta.params.ModelParams;
import es.ull.iis.simulation.model.TimeUnit;
import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;
import simkit.random.RandomVariate;

/**
 * TODO El cálculo de tiempo hasta complicación usa siempre el mismo número aleatorio para la misma complicación. Si aumenta el riesgo de esa
 * complicación en un momento de la simulación, se recalcula el tiempo, pero empezando en el instante actual. Esto produce que no necesariamente se acorte
 * el tiempo hasta evento en caso de un nuevo factor de riesgo. ¿debería reescalar de alguna manera el tiempo hasta evento en estos casos (¿proporcional al RR?)?
 * @author Iván Castilla Rodríguez
 *
 */
public class CommonParams extends ModelParams {
	protected T1DMMonitoringIntervention[] interventions = null;
	private final SevereHypoglycemicEventParam hypoParam;
	private final RandomNumber rng;

	private final double pMan;
	private final RandomVariate baselineAge;
	private final RandomVariate baselineHBA1c;
	private final RandomVariate weeklySensorUsage;
	private final double discountRate;
	
	private final ComplicationSubmodel[] compSubmodels;
	private final DeathSubmodel deathSubmodel; 
	private final ArrayList<T1DMComorbidity> availableHealthStates;
	private final CostCalculator costCalc;
	private final UtilityCalculator utilCalc;
	
	/**
	 * @param secondOrder
	 */
	public CommonParams(SecondOrderParamsRepository secParams) {
		super();
		compSubmodels = secParams.getComplicationSubmodels();
		deathSubmodel = secParams.getDeathSubmodel();
		costCalc = secParams.getCostCalculator();
		utilCalc = secParams.getUtilityCalculator();
		// Add the health availableHealthStates defined in the submodels
		availableHealthStates = secParams.getAvailableHealthStates();
		rng = RandomNumberFactory.getInstance();
		interventions = secParams.getInterventions();

		hypoParam = new SevereHypoglycemicEventParam(secParams.getnPatients(), secParams.getProbability(SecondOrderParamsRepository.STR_P_HYPO), secParams.getHypoRR(), secParams.getProbability(SecondOrderParamsRepository.STR_P_DEATH_HYPO));
		
		pMan = secParams.getPMan();
		baselineAge = secParams.getBaselineAge();
		baselineHBA1c = secParams.getBaselineHBA1c();
		weeklySensorUsage = secParams.getWeeklySensorUsage();
		discountRate = secParams.getDiscountRate();

	}

	/**
	 * @return the availableHealthStates
	 */
	public ArrayList<T1DMComorbidity> getAvailableHealthStates() {
		return availableHealthStates;
	}

	/**
	 * @return the interventions
	 */
	public T1DMMonitoringIntervention[] getInterventions() {
		return interventions;
	}
	
	public int getSex(T1DMPatient pat) {
		return (rng.draw() < pMan) ? 0 : 1;
	}
	
	public double getBaselineAge() {
		return Math.max(baselineAge.generate(), BasicConfigParams.MIN_AGE);		
	}

	public double getBaselineHBA1c() {
		return baselineHBA1c.generate();		
	}

	public double getWeeklySensorUsage() {
		return weeklySensorUsage.generate();		
	}

	public double getDiscountRate() {
		return discountRate;
	}
	
	public SevereHypoglycemicEventParam.ReturnValue getTimeToSevereHypoglycemicEvent(T1DMPatient pat, boolean cancelLast) {
		if (cancelLast)
			hypoParam.cancelLast(pat);
		return hypoParam.getValue(pat);
	}

	public TreeSet<T1DMComorbidity> getInitialState(T1DMPatient pat) {
		TreeSet<T1DMComorbidity> initial = new TreeSet<>();
		for (ComplicationSubmodel submodel : compSubmodels) {
			initial.addAll(submodel.getInitialState(pat));
		}
		return initial;
	}
	
	public T1DMProgression getNextComplication(T1DMPatient pat, MainComplications complication) {
		return compSubmodels[complication.ordinal()].getNextComplication(pat);
	}
	public long getTimeToDeath(T1DMPatient pat) {
		return deathSubmodel.getTimeToDeath(pat);
	}

	/**
	 * Generates a time to event based on annual risk. The time to event is absolute, i.e., can be used directly to schedule a new event. 
	 * @param pat A patient
	 * @param minusAvgTimeToEvent -1/(annual risk of the event)
	 * @param rnd A random number
	 * @param rr Relative risk for the patient
	 * @return a time to event based on annual risk
	 */
	public static long getAnnualBasedTimeToEvent(T1DMPatient pat, double minusAvgTimeToEvent, double rnd, double rr) {
		final double lifetime = pat.getAgeAtDeath() - pat.getAge();
		final double time = (minusAvgTimeToEvent / rr) * Math.log(rnd);
		return (time >= lifetime) ? Long.MAX_VALUE : pat.getTs() + Math.max(BasicConfigParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));
	}

	/**
	 * Return the annual cost for the specified patient during a period of time. The initAge and endAge parameters
	 * can be used to select different frequency of treatments according to the age of the patient 
	 * @param pat A patient
	 * @param initAge The age of the patient at the beginning of the period
	 * @param endAge The age of the patient at the end of the period
	 * @return the annual cost for the specified patient during a period of time.
	 */
	public double getAnnualCostWithinPeriod(T1DMPatient pat, double initAge, double endAge) {
		return costCalc.getAnnualCostWithinPeriod(pat, initAge, endAge);
	}

	/**
	 * Returns the cost of a complication upon incidence.
	 * @param pat A patient
	 * @param newEvent A new complication for the patient
	 * @return the cost of a complication upon incidence
	 */
	public double getCostOfComplication(T1DMPatient pat, T1DMComorbidity newEvent) {
		return costCalc.getCostOfComplication(pat, newEvent);
	}

	/**
	 * Returns the cost of a severe hypoglycemic episode
	 * @param pat A patient
	 * @return the cost of a severe hypoglycemic episode
	 */
	public double getCostForSevereHypoglycemicEpisode(T1DMPatient pat) {
		return costCalc.getCostForSevereHypoglycemicEpisode(pat);
	}

	public double getHypoEventDisutilityValue() {
		return utilCalc.getHypoEventDisutilityValue();
	}
	public double getUtilityValue(T1DMPatient pat) {
		return utilCalc.getUtilityValue(pat);
	}

	public void reset() {
		hypoParam.reset();
	}
	
	public double getRandomNumber() {
		return rng.draw();
	}
}
