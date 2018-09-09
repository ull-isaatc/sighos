/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import java.util.ArrayList;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.T1DM.MainAcuteComplications;
import es.ull.iis.simulation.hta.T1DM.MainChronicComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMComorbidity;
import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.T1DMProgression;
import es.ull.iis.simulation.hta.T1DM.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.ChronicComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.DeathSubmodel;
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
	private final T1DMMonitoringIntervention[] interventions;
	private final RandomNumber rng;

	private final double pMan;
	private final RandomVariate baselineAge;
	private final RandomVariate baselineHBA1c;
	private final double discountRate;
	
	private final ChronicComplicationSubmodel[] compSubmodels;
	private final AcuteComplicationSubmodel[] acuteCompSubmodels;
	private final DeathSubmodel deathSubmodel; 
	private final ArrayList<T1DMComorbidity> availableHealthStates;
	
	private final double cDNC;
	private final double duDNC;
	
	/**
	 * @param secondOrder
	 */
	public CommonParams(SecondOrderParamsRepository secParams) {
		super();
		compSubmodels = secParams.getComplicationSubmodels();
		acuteCompSubmodels = secParams.getAcuteComplicationSubmodels();
		deathSubmodel = secParams.getDeathSubmodel();
		// Add the health availableHealthStates defined in the submodels
		availableHealthStates = secParams.getAvailableHealthStates();
		rng = RandomNumberFactory.getInstance();
		interventions = secParams.getInterventions();

		pMan = secParams.getPMan();
		baselineAge = secParams.getBaselineAge();
		baselineHBA1c = secParams.getBaselineHBA1c();
		discountRate = secParams.getDiscountRate();

		cDNC = secParams.getAnnualNoComplicationCost();
		duDNC = secParams.getNoComplicationDisutility();
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
	
	public ChronicComplicationSubmodel[] getCompSubmodels() {
		return compSubmodels;
	}

	public AcuteComplicationSubmodel[] getAcuteCompSubmodels() {
		return acuteCompSubmodels;
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
	
	public double getAnnualNoComplicationCost() {
		return cDNC;
	}
	
	public double getNoComplicationDisutility() {
		return duDNC;
	}

	public double getDiscountRate() {
		return discountRate;
	}
	
	public AcuteEventParam.Progression getTimeToAcuteEvent(T1DMPatient pat, MainAcuteComplications complication, boolean cancelLast) {
		AcuteEventParam param = acuteCompSubmodels[complication.ordinal()].getParam();
		if (cancelLast)
			param.cancelLast(pat);
		return param.getValue(pat);
	}

	public TreeSet<T1DMComorbidity> getInitialState(T1DMPatient pat) {
		TreeSet<T1DMComorbidity> initial = new TreeSet<>();
		for (ChronicComplicationSubmodel submodel : compSubmodels) {
			initial.addAll(submodel.getInitialState(pat));
		}
		return initial;
	}
	
	public T1DMProgression getNextComplication(T1DMPatient pat, MainChronicComplications complication) {
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

	public void reset() {
		for (AcuteComplicationSubmodel acuteSubmodel : acuteCompSubmodels)
			acuteSubmodel.getParam().reset();
	}
	
	public double getRandomNumber() {
		return rng.draw();
	}
}
