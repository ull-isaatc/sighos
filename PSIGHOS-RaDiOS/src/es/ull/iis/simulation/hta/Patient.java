/**
 * 
 */
package es.ull.iis.simulation.hta;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.info.PatientInfo;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.outcomes.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.Modification;
import es.ull.iis.simulation.hta.params.ProbabilityParamDescriptions;
import es.ull.iis.simulation.hta.populations.Population;
import es.ull.iis.simulation.hta.populations.PopulationAttribute;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionEventPair;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionEvents;
import es.ull.iis.simulation.model.DiscreteEvent;
import es.ull.iis.simulation.model.EventSource;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.VariableStoreSimulationObject;
import es.ull.iis.simulation.model.engine.SimulationEngine;

/**
 * A patient with Diabetes Mellitus. The patient is initially characterized with an age, sex, HbA1c level, and an intervention.
 * Depending on the effect of the intervention, HbA1c level may change. The effect of the intervention itself can be lifelong or restricted 
 * to a time period.
 * The patient may progress to several chronic complications (see {@link ChronicComplication}), or may develop acute complications 
 * (see {@link AcuteComplication}).
 * @author Iván Castilla Rodríguez
 *
 */
public class Patient extends VariableStoreSimulationObject implements EventSource {
	public enum PREDEFINED_RANDOM_VALUE_TYPE {
		ONSET,
		DIAGNOSE,
		DEATH;
		public String getKey(Patient pat, DiseaseProgression progression) {
			return name() + progression.name() + pat.getNDiseaseProgressions(progression);
		}
	}
	
	private final static String OBJ_TYPE_ID = "PAT";
	/** The original patient, this one was cloned from */ 
	private final Patient clonedFrom;
	/** The specific intervention assigned to the patient */
	protected final Intervention intervention;
	/** The timestamp when this patient enters the simulation */
	protected long startTs;
	/** True if the patient is dead */
	private boolean dead; 
	/** True if the patient has been diagnosed of his/her disease */
	private boolean diagnosed;
	/** The detailed state of the patient */
	private final TreeSet<DiseaseProgression> state;
	/** Sex of the patient: 0 for men, 1 for women */
	private final int sex;
	/** Initial age of the patient (stored as years) */
	private final double initAge;	
	/** Initial age of the patient (stored as simulation time units) */
	private final long simulInitAge;
	/** {@link Disease} of the patient or {@link Disease.HEALTHY} in case the patient is healthy */ 
	private final Disease disease;
	/** A collection of attributes */
	private final TreeMap<String, Number> attributes;
	
	// Events
	/** Events that this patient has suffered related to each disease progression */
	private final TreeMap<DiseaseProgression, ArrayDeque<DiseaseProgressionEvent>> progressionEvents;
	/** Next events scheduled for each disease progression */
	private final TreeMap<DiseaseProgression, DiseaseProgressionEvent> nextProgressionEvents;
	/** Death event */ 
	protected DeathEvent deathEvent = null;
	/** Population this patient belongs to */
	private final Population population;

	/** Common random numbers shared among the instances of this patient evaluated for different interventions.
	 * They ensure that the differences among interventions depend on what the intervention modifies and not on
	 * random numbers */
	private final PatientCommonRandomNumbers commonRN;

	/**
	 * Creates a new patient
	 * @param simul Simulation this patient belongs to
	 * @param intervention Intervention assigned to this patient
	 * @param population Population this patient belongs to
	 */
	public Patient(final DiseaseProgressionSimulation simul, final Intervention intervention, final Population population) {
		super(simul, simul.getPatientCounter(), OBJ_TYPE_ID);
		this.intervention = intervention;
		this.clonedFrom = null;
		this.dead = false;
		this.population = population;
		this.diagnosed = population.isDiagnosedFromStart(this);
		this.state = new TreeSet<>();
		this.sex = population.getSex(this);
		this.initAge = population.getInitAge(this);
		this.simulInitAge = BasicConfigParams.SIMUNIT.convert(initAge, TimeUnit.YEAR);
		this.disease = population.getDisease(this);
		this.attributes = new TreeMap<>();
		for (PopulationAttribute attribute : population.getPatientAttributes())
			addAttribute(attribute.name(), attribute.getInitialValue(this, simul));
		progressionEvents = new TreeMap<>();
		nextProgressionEvents = new TreeMap<>();
		commonRN = new PatientCommonRandomNumbers();
	}

	/**
	 * Creates a patient who replicates another patient.
	 * @param simul Simulation this patient belongs to
	 * @param original Original patient
	 * @param intervention Intervention assigned to this patient
	 */
	public Patient(DiseaseProgressionSimulation simul, Patient original, Intervention intervention) {
		super(simul, original.id, OBJ_TYPE_ID);
		this.intervention = intervention;
		this.clonedFrom = original;		
		this.dead = false;
		this.state = new TreeSet<>();
		this.population = original.population;
		this.diagnosed = population.isDiagnosedFromStart(this);
		this.sex = original.sex;
		this.initAge = original.initAge;
		this.simulInitAge = original.simulInitAge;
		this.disease = original.disease;
		this.attributes = original.attributes;
		this.commonRN = original.commonRN;
		progressionEvents = new TreeMap<>();
		nextProgressionEvents = new TreeMap<>();
	}

	public List<Double> getRandomNumbers(String key, int n) {
		return commonRN.draw(key, n);
	}

	public double getRandomNumber(String key) {
		return commonRN.draw(key);
	}

	public List<Double> getRandomNumbersForIncidence(DiseaseProgression progression, int n) {
		return getRandomNumbers(PREDEFINED_RANDOM_VALUE_TYPE.ONSET.getKey(this, progression), n);
	}

	public double getRandomNumberForIncidence(DiseaseProgression progression) {
		return getRandomNumber(PREDEFINED_RANDOM_VALUE_TYPE.ONSET.getKey(this, progression));
	}

	@Override
	public DiseaseProgressionSimulation getSimulation() {
		return (DiseaseProgressionSimulation)super.getSimulation();
	}
	/**
	 * Returns the disease of the patient
	 * @return the disease of the patient
	 */
	public Disease getDisease() {
		return disease;
	}

	/**
	 * Returns true if the patient currently has a specified disease; false otherwise
	 * @param disease One {@link Disease}
	 * @return True if the patient currently has a specified disease; false otherwise
	 */
	public boolean hasDisease(Disease disease) {
		return getDisease().equals(disease);
	}

	/**
	 * Returns true if the patient is currently healthy; false otherwise
	 * @return True if the patient is currently healthy; false otherwise
	 */
	public boolean isHealthy() {
		return ((DiseaseProgressionSimulation) simul).getRepository().HEALTHY.equals(getDisease());
	}
	
	/**
	 * Returns the state of the patient as regards to the detailed progression of main chronic manifestations and stages
	 * @return the state of the patient as regards to the detailed progression of main chronic manifestations and stages
	 */
	public TreeSet<DiseaseProgression> getState() {
		return state;
	}
	
	/**
	 * Returns true if this patient starts in the simulation with the specified progression
	 * @param progression A progression
	 * @return true if this patient starts in the simulation with the specified progression
	 */
	public boolean startsWithDiseaseProgression(DiseaseProgression progression) {
		return commonRN.draw(PREDEFINED_RANDOM_VALUE_TYPE.ONSET.getKey(this, progression)) < ProbabilityParamDescriptions.INITIAL_PROPORTION.getValue(getSimulation().getRepository(), progression, getSimulation());
	}

	/**
	 * Returns true if the acute onset of a progression produces the death of the patient 
	 * @param progression A progression
	 * @return True if the acute onset of the progression produces the death of the patient
	 */
	public boolean deadsByDiseaseProgression(DiseaseProgression progression) {
		return commonRN.draw(PREDEFINED_RANDOM_VALUE_TYPE.DEATH.getKey(this, progression)) < ProbabilityParamDescriptions.PROBABILITY_DEATH.getValue(getSimulation().getRepository(), progression, getSimulation());
	}
	
	/**
	 * Returns true if the acute onset of a progression leads to the diagnosis of the patient 
	 * @param progression A progression
	 * @return True if the acute onset of a progression leads to the diagnosis of the patient
	 */
	public boolean isDiagnosedByDiseaseProgression(DiseaseProgression progression) {
		return commonRN.draw(PREDEFINED_RANDOM_VALUE_TYPE.DIAGNOSE.getKey(this, progression)) < ProbabilityParamDescriptions.PROBABILITY_DIAGNOSIS.getValue(getSimulation().getRepository(), progression, getSimulation());
	}
	
	@Override
	public void notifyEnd() {
        simul.addEvent(onDestroy(simul.getSimulationEngine().getTs()));
	}

	@Override
	protected void assignSimulation(SimulationEngine simul) {
		// Nothing to do
	}
	
	/**
	 * Returns the identifier of the intervention applied to this patient
	 * @return the identifier of the intervention applied to this patient
	 */
	public int getnIntervention() {
		return intervention.ordinal();
	}

	/**
	 * Returns the intervention applied to this patient
	 * @return the intervention applied to this patient
	 */
	public Intervention getIntervention() {
		return intervention;
	}
	
	/**
	 * Returns the patient this patient was cloned from; null if this is an original patient
	 * @return the patient this patient was cloned from; null if this is an original patient
	 */
	public Patient getClonedFrom() {
		return clonedFrom;
	}

	/**
	 * Returns the timestamp when the patient was added to the simulation
	 * @return the timestamp when the patient was added to the simulation
	 */
	public long getStartTs() {
		return startTs;
	}

	/**
	 * Returns true if the patient is dead; false otherwise
	 * @return true if the patient is dead; false otherwise
	 */
	public boolean isDead() {
		return dead;
	}

	/**
	 * Sets the patient as dead
	 */
	public void setDead() {
		this.dead = true;
	}

	/**
	 * @return the diagnosed
	 */
	public boolean isDiagnosed() {
		return diagnosed;
	}

	/**
	 * @param diagnosed the diagnosed to set
	 */
	public void setDiagnosed(boolean diagnosed) {
		this.diagnosed = diagnosed;
	}

	@Override
	public DiscreteEvent onCreate(long ts) {
		return new StartEvent(this, ts);
	}

	@Override
	public DiscreteEvent onDestroy(long ts) {
		return new DiscreteEvent.DefaultFinalizeEvent(this, ts);
	}

	/**
	 * Returns the initial age assigned to the patient
	 * @return The initial age assigned to the patient
	 */
	public double getInitAge() {
		return initAge;
	}

	/**
	 * Returns the current age of the patient
	 * @return The current age of the patient
	 */
	public double getAge() {
		return ((double)(simulInitAge + simul.getSimulationEngine().getTs() - startTs)) / BasicConfigParams.YEAR_CONVERSION;
	}
	
	
	/**
	 * Returns the sex assigned to the patient (0: male; 1: female)
	 * @return The sex assigned to the patient (0: male; 1: female)
	 */
	public int getSex() {
		return sex;
	}

	/**
	 * Returns the utility currently associated to this patient
	 * @param method Method used to compute the combined disutility of the items that contribute to the final QALE for this period
	 * @return the utility currently associated to this patient
	 */
	public double getUtilityValue(DisutilityCombinationMethod method) {
		// Uses the base disutility for the disease if available 
		double du = getDisease().getAnnualDisutility(this);
		for (final DiseaseProgression progression : state) {
			du = method.combine(du, progression.getAnnualDisutility(this));
		}
		du = method.combine(du, intervention.getAnnualDisutility(this));
		return population.getBaseUtility(this) - du;		
	}
	
	/**
	 * Returns the predicted age at death of the patient. If not yet predicted, returns Double.MAX_VALUE.
	 * This age is susceptible to change during the simulation. 
	 * @return the predicted age at death of the patient
	 */
	public double getAgeAtDeath() {
		final long timeToDeath = getTimeToDeath();
		return (timeToDeath == Long.MAX_VALUE) ? Double.MAX_VALUE : (((double)(simulInitAge + timeToDeath)) / BasicConfigParams.YEAR_CONVERSION);
	}
	
	/**
	 * Returns the predicted time to death of the patient (in simulation time units). If not yet predicted, returns Long.MAX_VALUE.
	 * @return the predicted time to death of the patient (in simulation time units)
	 */
	public long getTimeToDeath() {
		return (deathEvent == null) ? Long.MAX_VALUE : deathEvent.getTs();
	}

	/**
	 * Returns the timestamp when certain progression complication started  
	 * @param progression A progression
	 * @return the timestamp when certain progression complication started
	 */
	public long getTimeToDiseaseProgression(DiseaseProgression progression) {
		if (!progressionEvents.containsKey(progression)) 
			return Long.MAX_VALUE;
		if (progressionEvents.get(progression).size() == 0)
			return Long.MAX_VALUE;
		return progressionEvents.get(progression).peekLast().getTs(); 
	}
	
	/**
	 * Returns the timestamp when the next progression of the specified type is planned to start  
	 * @param progression A progression
	 * @return the timestamp when the next progression of the specified type is planned to start
	 */
	public long getTimeToNextDiseaseProgression(DiseaseProgression progression) {
		if (!nextProgressionEvents.containsKey(progression)) 
			return Long.MAX_VALUE;
		return nextProgressionEvents.get(progression).getTs(); 
	}
	
	/**
	 * Returns the number of events of this progression already suffered by the patient.
	 * If the progression is chronic, this method returns at most 1; otherwise, it can return an arbitrary value >= 0 
	 * @param progression A progression
	 * @return The number of events of this progression already suffered by the patient
	 */
	public int getNDiseaseProgressions(DiseaseProgression progression) {
		if (!progressionEvents.containsKey(progression))
			return 0;
		return progressionEvents.get(progression).size(); 		
	}
	
	/**
	 * Recomputes time to death in case the risk increases
	 * @param progression Manifestation that (potentially) induces a change in the risk of death 
	 */
	private void readjustDeath(DiseaseProgression progression) {
		final long newTimeToDeath = ((DiseaseProgressionSimulation) simul).getRepository().getTimeToDeath(this);
		if (newTimeToDeath < deathEvent.getTs()) {
			deathEvent.cancel();
			deathEvent = new DeathEvent(newTimeToDeath, progression);
			simul.addEvent(deathEvent);
		}		
	}
	
	/**
	 * Applies the indicated progression to the patient, adding the new events
	 * @param progs ManifestationPathway of the disease for this patient
	 */
	private void applyProgression(DiseaseProgressionEvents progs) {
		for (DiseaseProgressionEventPair pr : progs.getNewEvents()) {
			final DiseaseProgressionEvent ev = new DiseaseProgressionEvent(pr); 
			nextProgressionEvents.put(pr.getDiseaseProgression(), ev);
			simul.addEvent(ev);
		}
	}
	
	/**
	 * Returns true if certain progression is hindered by the current state of the patient  
	 * @param newProgression New stage, manifestation... to which the patient may progress
	 * @return True if certain progression is hindered by the current state of the patient
	 */
	public boolean mustBeExcluded(DiseaseProgression newProgression) {
		for (DiseaseProgression progression : state) {
			final TreeSet<DiseaseProgression> excluded = getDisease().getExcluded(progression);
			if (excluded.contains(newProgression))
				return true;
		}
		return false;
	}
	
	protected void startAction() {
		startTs = this.getTs();
		simul.notifyInfo(new PatientInfo(simul, this, PatientInfo.Type.START, getTs()));

		// Assign death event
		final long timeToDeath = ((DiseaseProgressionSimulation) simul).getRepository().getTimeToDeath(this);
		deathEvent = new DeathEvent(timeToDeath);
		simul.addEvent(deathEvent);
		
		for (DiseaseProgression progression : getDisease().getInitialStage(this)) {
			ArrayDeque<DiseaseProgressionEvent> events = new ArrayDeque<>();
			events.add(new DiseaseProgressionEvent(new DiseaseProgressionEventPair(progression, 0)));				
			// I was scheduling these events in the usual way, but they were not executed before the next loop and progression fails
			progressionEvents.put(progression, events);
			// This should never happen
			if (Patient.this.state.contains(progression)) {
				error("Health state already assigned!! " + progression.name());
			}
			else {
				simul.notifyInfo(new PatientInfo(simul, this, progression, getTs()));
				Patient.this.state.add(progression);
				
				// Recompute time to death in case the risk increases
				readjustDeath(progression);
			}
		}
		// Assign progression events
		final DiseaseProgressionEvents progs = getDisease().getProgression(this);
		if (progs.getCancelEvents().size() > 0)
			error("Cancel complications at start?");
		applyProgression(progs);
		// Schedules events related to the intervention
		for (DiscreteEvent ev : intervention.getEvents(this)) {
			simul.addEvent(ev);
		}
	}

	/**
	 * Returns the value associated to the specified attribute modified depending on the patient
	 * @param attribute The name of the attribute
	 * @return the value associated to the specified attribute
	 */
	public Number getAttributeValue(String attribute) {
		final int id = getSimulation().getIdentifier();
		final Modification modif = getSimulation().getIntervention().getClinicalParameterModification(attribute);
		double value = attributes.get(attribute).doubleValue(); 
		switch(modif.getType()) {
		case DIFF:
			value -= modif.getValue(id);
			break;
		case RR:
			value *= modif.getValue(id);
			break;
		case SET:
			value = modif.getValue(id);
			break;
		default:
			break;
		}
		return value;
	}

	/**
	 * Sets the value of the specified attribute. Replaces the attribute value if already declared.
	 * @param attribute The name of the attribute
	 * @param value The new value of the attribute
	 * @return This patient, so you can concatenate calls to this method 
	 */
	public Patient addAttribute(String attribute, Number value) {
		attributes.put(attribute, value);
		return this;
	}

	/**
	 * Returns the list of attributes defined for this patient 	
	 * @return the list of attributes defined for this patient
	 */
	public Collection<String> getAttributeNames() {
		return attributes.keySet();
	}

	/**
	 * The first event of the patient that initializes everything and computes initial time to events.
	 * @author Iván Castilla Rodríguez
	 *
	 */
	private class StartEvent extends DiscreteEvent.DefaultStartEvent {

		public StartEvent(EventSource source, long ts) {
			super(source, ts);
		}
		
		@Override
		public void event() {
			super.event();
			startAction();
		}
		
	}
	
	/**
	 * An event related to the progression to a new manifestation, stage... Updates the state of the patient and recomputes 
	 * time to develop other progressions in case the risks change.
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public class DiseaseProgressionEvent extends DiscreteEvent {
		private final DiseaseProgressionEventPair progress;

		public DiseaseProgressionEvent(DiseaseProgressionEventPair progress) {
			super(progress.getTimeToEvent());
			this.progress = progress;
		}

		@Override
		public void event() {
			final DiseaseProgression progression = progress.getDiseaseProgression();
			if (Patient.this.state.contains(progression)) {
				error("Health state already assigned!! " + progression.name());
			}
			else if (mustBeExcluded(progression)) {
				this.cancel();
			}
			else {
				updateProgressionEvents();				
				simul.notifyInfo(new PatientInfo(simul, Patient.this, progression, this.getTs()));
				if (!DiseaseProgression.Type.ACUTE_MANIFESTATION.equals(progression.getType())) {
					Patient.this.state.add(progression);
					// Removes chronic progressions excluded by the new one
					for (DiseaseProgression excluded : getDisease().getExcluded(progression)) {
						if (Patient.this.state.remove(excluded))						
							simul.notifyInfo(new PatientInfo(simul, Patient.this, excluded, this.getTs(), true));
					}
				}

				// If not already diagnosed, checks whether this progression leads to a diagnosis
				if (!isDiagnosed()) {
					if (isDiagnosedByDiseaseProgression(progression)) {
						setDiagnosed(true);
						simul.notifyInfo(new PatientInfo(simul, Patient.this, PatientInfo.Type.DIAGNOSIS, progression, this.getTs()));
					}					
				}				
				if (deadsByDiseaseProgression(progression)) {
					deathEvent.cancel();
					deathEvent = new DeathEvent(ts, progression);
					simul.addEvent(deathEvent);
				}
				else {
					// Recompute time to death in case the risk increases
					readjustDeath(progression);
					// Update complications
					final DiseaseProgressionEvents progs = getDisease().getProgression(Patient.this);
					for (DiseaseProgression st: progs.getCancelEvents()) {
						nextProgressionEvents.get(st).cancel();
						nextProgressionEvents.remove(st);
					}
					applyProgression(progs);
				}
			}
		}
		
		private void updateProgressionEvents() {
			final DiseaseProgression progression = progress.getDiseaseProgression();
			// Add the event to the list of progression events
			ArrayDeque<DiseaseProgressionEvent> events = null;
			if (progressionEvents.get(progression) == null) {
				events = new ArrayDeque<>();
			}
			else {
				events = progressionEvents.get(progression);
			}
			events.add(this);
			progressionEvents.put(progression, events);
			// ...and remove it from the list of next events
			nextProgressionEvents.remove(progression);
		}
		
		@Override
		public String toString() {
			return Patient.DiseaseProgressionEvent.class.getSimpleName() + "\t" + progress.getDiseaseProgression().name() + "[" + progress.getTimeToEvent() + "]";
		}

	}

	/**
	 * The event of the death of the patient.  
	 * @author Ivan Castilla Rodriguez
	 *
	 */
	public final class DeathEvent extends DiscreteEvent {
		/** Cause of the death; null if non-specific */
		final DiseaseProgression cause;
		
		public DeathEvent(long ts) {
			this(ts, null);
		}

		public DeathEvent(long ts, DiseaseProgression cause) {
			super(ts);
			this.cause = cause;
		}

		@Override
		public void event() {
			// Cancel all events that cannot happen now but the one that causes the death
			for (final DiseaseProgression progression : nextProgressionEvents.keySet()) {
				if (!progression.equals(cause)) {
					final DiseaseProgressionEvent event = nextProgressionEvents.get(progression);
					if (event.getTs() >= getTs()) {
						event.cancel();
					}
				}
			}
			nextProgressionEvents.clear();
			setDead();
			simul.notifyInfo(new PatientInfo(simul, Patient.this, PatientInfo.Type.DEATH, cause, this.getTs()));
			notifyEnd();
		}
	
		@Override
		public boolean cancel() {
			if (super.cancel()) {
				deathEvent = null;
				return true;
			}
			return false;
		}
	}
}
