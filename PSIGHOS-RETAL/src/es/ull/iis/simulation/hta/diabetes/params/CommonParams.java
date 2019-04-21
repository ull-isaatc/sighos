/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.params;

import java.util.ArrayList;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.DiabetesProgression;
import es.ull.iis.simulation.hta.diabetes.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.ChronicComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.DeathSubmodel;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * A repository to handle the simulation values of second order parameters. At creation, draws a value for each second-order
 * parameter, and then stores the value to be used during the simulation.
 * TODO El cálculo de tiempo hasta complicación usa siempre el mismo número aleatorio para la misma complicación. Si aumenta el riesgo de esa
 * complicación en un momento de la simulación, se recalcula el tiempo, pero empezando en el instante actual. Esto produce que no necesariamente se acorte
 * el tiempo hasta evento en caso de un nuevo factor de riesgo. ¿debería reescalar de alguna manera el tiempo hasta evento en estos casos (¿proporcional al RR?)?
 * @author Iván Castilla Rodríguez
 *
 */
public class CommonParams {
	/** Discount rate applied to costs and effects */
	private final double discountRate;
	
	/** Chronic complication submodels included */
	private final ChronicComplicationSubmodel[] compSubmodels;
	/** Acute complication submodels included */
	private final AcuteComplicationSubmodel[] acuteCompSubmodels;
	/** Death submodel */
	private final DeathSubmodel deathSubmodel; 
	/** Chronic complication stages defined in the submodels and registered in the simulation */
	private final ArrayList<DiabetesComplicationStage> chronicComplicationStages;
	
	// FIXME: Add first order variation to these parameters
	/** Cost of a year with no complications */
	private final double cDNC;
	/** Disutility of a patient with no complications */
	private final double duDNC;
	
	/**
	 * Creates a repository for first order simulations 
	 * @param secondOrder The second order repository that defines the second-order uncertainty on the parameters
	 */
	public CommonParams(SecondOrderParamsRepository secParams) {
		compSubmodels = secParams.getComplicationSubmodels();
		acuteCompSubmodels = secParams.getAcuteComplicationSubmodels();
		deathSubmodel = secParams.getDeathSubmodel();
		// Add the complication stages defined in the submodels
		chronicComplicationStages = secParams.getRegisteredComplicationStages();
		discountRate = secParams.getDiscountRate();

		cDNC = secParams.getNoComplicationAnnualCost();
		duDNC = secParams.getNoComplicationDisutility();
	}

	/**
	 * Returns the complication stages related to the chronic complications
	 * @return the complication stages related to the chronic complications
	 */
	public ArrayList<DiabetesComplicationStage> getRegisteredComplicationStages() {
		return chronicComplicationStages;
	}

	/**
	 * Returns the chronic complication submodels
	 * @return the chronic complication submodels
	 */
	public ChronicComplicationSubmodel[] getCompSubmodels() {
		return compSubmodels;
	}

	/**
	 * Returns the acute complication submodels
	 * @return the acute complication submodels
	 */
	public AcuteComplicationSubmodel[] getAcuteCompSubmodels() {
		return acuteCompSubmodels;
	}
	
	/**
	 * Returns the annual cost of a patient with no complications
	 * @return the annual cost of a patient with no complications
	 */
	public double getAnnualNoComplicationCost() {
		return cDNC;
	}
	
	/**
	 * Returns the disutility applied to a patient with no complications
	 * @return the disutility applied to a patient with no complications
	 */
	public double getNoComplicationDisutility() {
		return duDNC;
	}

	/**
	 * Returns the discount rate applied to cost and effects during the simulation
	 * @return the discount rate applied to cost and effects during the simulation
	 */
	public double getDiscountRate() {
		return discountRate;
	}
	
	/**
	 * Returns the time that a patient waits until he/she suffers the specified acute complication
	 * @param pat A patient
	 * @param complication An acute complication
	 * @param cancelLast If true, the new event substitutes the former one
	 * @return the time that a patient waits until he/she suffers the specified acute complication
	 */
	public AcuteComplicationSubmodel.Progression getTimeToAcuteEvent(DiabetesPatient pat, DiabetesAcuteComplications complication, boolean cancelLast) {
		if (cancelLast)
			acuteCompSubmodels[complication.ordinal()].cancelLast(pat);
		return acuteCompSubmodels[complication.ordinal()].getValue(pat);
	}

	/**
	 * Returns the chronic complications that a patient suffers at the start of the simulation, in case there is any
	 * @param pat A patient
	 * @return the chronic complications that a patient suffers at the start of the simulation, in case there is any
	 */
	public TreeSet<DiabetesComplicationStage> getInitialState(DiabetesPatient pat) {
		TreeSet<DiabetesComplicationStage> initial = new TreeSet<>();
		for (ChronicComplicationSubmodel submodel : compSubmodels) {
			initial.addAll(submodel.getInitialStage(pat));
		}
		return initial;
	}
	
	/**
	 * Returns how this patient will progress from its current state with regards to a specified chronic complication. 
	 * The progress can include removal of events already scheduled, modification of previously scheduled events and new events.
	 * @param pat A patient
	 * @param complication A chronic complication
	 * @return how this patient will progress from its current state with regards to a specified chronic complication
	 */
	public DiabetesProgression getProgression(DiabetesPatient pat, DiabetesChronicComplications complication) {
		return compSubmodels[complication.ordinal()].getProgression(pat);
	}
	
	/**
	 * Returns the life expectancy of the patient
	 * @param pat A patient
	 * @return the life expectancy of the patient
	 */
	public long getTimeToDeath(DiabetesPatient pat) {
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
	public static long getAnnualBasedTimeToEvent(DiabetesPatient pat, double minusAvgTimeToEvent, double rnd, double rr) {
		final double lifetime = pat.getAgeAtDeath() - pat.getAge();
		if (Double.isInfinite(minusAvgTimeToEvent))
			return Long.MAX_VALUE;
		final double newMinus = -1 / (1-Math.exp(Math.log(1+1/minusAvgTimeToEvent)*rr));
		final double time = newMinus * Math.log(rnd);
		
//		final double time = (minusAvgTimeToEvent / rr) * Math.log(rnd);
		return (time >= lifetime) ? Long.MAX_VALUE : pat.getTs() + Math.max(BasicConfigParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));
	}

	/**
	 * Restarts the parameters among interventions. Useful to reuse already computed values for a previous intervention and
	 * preserve common random numbers
	 */
	public void reset() {
		for (AcuteComplicationSubmodel acuteSubmodel : acuteCompSubmodels)
			acuteSubmodel.reset();
	}
}
