/**
 * 
 */
package es.ull.iis.simulation.hta;

import java.util.ArrayDeque;
import java.util.TreeMap;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.info.PatientInfo;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.populations.Population;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionPair;
import es.ull.iis.simulation.hta.progression.Manifestation;
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
	private final TreeSet<Manifestation> state;
	/** Patient profile */
	private final PatientProfile profile;
	/** Initial age of the patient (stored as simulation time units) */
	private final long initAge;
	
	// Events
	/** Events that this patient has suffered related to each manifestation */
	private final TreeMap<Manifestation, ArrayDeque<ManifestationEvent>> manifestationEvents;
	/** Next events scheduled for each manifestation */
	private final TreeMap<Manifestation, ManifestationEvent> nextManifestationEvents;
	/** Death event */ 
	protected DeathEvent deathEvent = null;

	/**
	 * Creates a new patient
	 * @param simul Simulation this patient belongs to
	 * @param intervention Intervention assigned to this patient
	 */
	public Patient(final DiseaseProgressionSimulation simul, final Intervention intervention, final Population population) {
		super(simul, simul.getPatientCounter(), OBJ_TYPE_ID);
		this.intervention = intervention;
		this.clonedFrom = null;
		this.dead = false;
		this.profile = population.getPatientProfile(simul);
		this.diagnosed = profile.isDiagnosedFromStart();
		this.state = new TreeSet<>();
		this.initAge = BasicConfigParams.SIMUNIT.convert(profile.getInitAge(), TimeUnit.YEAR);
		manifestationEvents = new TreeMap<>();
		nextManifestationEvents = new TreeMap<>();
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
		this.profile = original.profile;
		this.diagnosed = profile.isDiagnosedFromStart();
		this.initAge = original.initAge;
		manifestationEvents = new TreeMap<>();
		nextManifestationEvents = new TreeMap<>();
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
		return profile.getDisease();
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
		return ((DiseaseProgressionSimulation) simul).getCommonParams().HEALTHY.equals(getDisease());
	}
	
	/**
	 * Returns the state of the patient as regards to the detailed progression of main chronic manifestations
	 * @return the state of the patient as regards to the detailed progression of main chronic manifestations
	 */
	public TreeSet<Manifestation> getState() {
		return state;
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
	 * Returns the patient's profile
	 * @return the profile
	 */
	public PatientProfile getProfile() {
		return profile;
	}

	/**
	 * Returns the initial age assigned to the patient
	 * @return The initial age assigned to the patient
	 */
	public double getInitAge() {
		return profile.getInitAge();
	}

	/**
	 * Returns the current age of the patient
	 * @return The current age of the patient
	 */
	public double getAge() {
		return ((double)(initAge + simul.getSimulationEngine().getTs() - startTs)) / BasicConfigParams.YEAR_CONVERSION;
	}
	
	
	/**
	 * Returns the sex assigned to the patient (0: male; 1: female)
	 * @return The sex assigned to the patient (0: male; 1: female)
	 */
	public int getSex() {
		return profile.getSex();
	}

	/**
	 * Returns the predicted age at death of the patient. If not yet predicted, returns Double.MAX_VALUE.
	 * This age is susceptible to change during the simulation. 
	 * @return the predicted age at death of the patient
	 */
	public double getAgeAtDeath() {
		final long timeToDeath = getTimeToDeath();
		return (timeToDeath == Long.MAX_VALUE) ? Double.MAX_VALUE : (((double)(initAge + timeToDeath)) / BasicConfigParams.YEAR_CONVERSION);
	}
	
	/**
	 * Returns the predicted time to death of the patient (in simulation time units). If not yet predicted, returns Long.MAX_VALUE.
	 * @return the predicted time to death of the patient (in simulation time units)
	 */
	public long getTimeToDeath() {
		return (deathEvent == null) ? Long.MAX_VALUE : deathEvent.getTs();
	}

	/**
	 * Returns the timestamp when certain manifestation complication started  
	 * @param manif A manifestation
	 * @return the timestamp when certain manifestation complication started
	 */
	public long getTimeToManifestation(Manifestation manif) {
		if (!manifestationEvents.containsKey(manif)) 
			return Long.MAX_VALUE;
		if (manifestationEvents.get(manif).size() == 0)
			return Long.MAX_VALUE;
		return manifestationEvents.get(manif).peekLast().getTs(); 
	}
	
	/**
	 * Returns the timestamp when the next manifestation of the specified type is planned to start  
	 * @param manif A manifestation
	 * @return the timestamp when the next manifestation of the specified type is planned to start
	 */
	public long getTimeToNextManifestation(Manifestation manif) {
		if (!nextManifestationEvents.containsKey(manif)) 
			return Long.MAX_VALUE;
		return nextManifestationEvents.get(manif).getTs(); 
	}
	
	/**
	 * Returns the number of events of this manifestation already suffered by the patient.
	 * If the manifestation is chronic, this method returns at most 1; otherwise, it can return an arbitrary value >= 0 
	 * @param manif A manifestation
	 * @return The number of events of this manifestation already suffered by the patient
	 */
	public int getNManifestations(Manifestation manif) {
		if (!manifestationEvents.containsKey(manif))
			return 0;
		return manifestationEvents.get(manif).size(); 		
	}
	
	/**
	 * Recomputes time to death in case the risk increases
	 * @param manif Manifestation that (potentially) induces a change in the risk of death 
	 */
	private void readjustDeath(Manifestation manif) {
		final long newTimeToDeath = ((DiseaseProgressionSimulation) simul).getCommonParams().getTimeToDeath(this);
		if (newTimeToDeath < deathEvent.getTs()) {
			deathEvent.cancel();
			deathEvent = new DeathEvent(newTimeToDeath, manif);
			simul.addEvent(deathEvent);
		}		
	}
	
	/**
	 * Applies the indicated progression to the patient, adding the new events
	 * @param progs ManifestationPathway of the disease for this patient
	 */
	private void applyProgression(DiseaseProgression progs) {
		for (DiseaseProgressionPair pr : progs.getNewEvents()) {
			final ManifestationEvent ev = new ManifestationEvent(pr); 
			nextManifestationEvents.put(pr.getManifestation(), ev);
			simul.addEvent(ev);
		}
	}
	
	/**
	 * Returns true if progression to the specified manifestation is hindered by the current state of the patient  
	 * @param newManif New manifestation to which the patient may progress
	 * @return True if progression to the specified manifestation is hindered by the current state of the patient
	 */
	public boolean mustBeExcluded(Manifestation newManif) {
		for (Manifestation manif : state) {
			final TreeSet<Manifestation> excluded = getDisease().getExcluded(manif);
			if (excluded.contains(newManif))
				return true;
		}
		return false;
	}
	
	protected void startAction() {
		startTs = this.getTs();
		simul.notifyInfo(new PatientInfo(simul, this, PatientInfo.Type.START, getTs()));

		// Assign death event
		final long timeToDeath = ((DiseaseProgressionSimulation) simul).getCommonParams().getTimeToDeath(this);
		deathEvent = new DeathEvent(timeToDeath);
		simul.addEvent(deathEvent);
		
		for (Manifestation manif : getDisease().getInitialStage(this)) {
			ArrayDeque<ManifestationEvent> events = new ArrayDeque<>();
			events.add(new ManifestationEvent(new DiseaseProgressionPair(manif, 0)));				
			// I was scheduling these events in the usual way, but they were not executed before the next loop and progression fails
			manifestationEvents.put(manif, events);
			// This should never happen
			if (Patient.this.state.contains(manif)) {
				error("Health state already assigned!! " + manif.name());
			}
			else {
				simul.notifyInfo(new PatientInfo(simul, this, manif, getTs()));
				Patient.this.state.add(manif);
				
				// Recompute time to death in case the risk increases
				readjustDeath(manif);
			}
		}
		// Assign manifestation events
		final DiseaseProgression progs = getDisease().getProgression(this);
		if (progs.getCancelEvents().size() > 0)
			error("Cancel complications at start?");
		applyProgression(progs);
		// Schedules events related to the intervention
		for (DiscreteEvent ev : intervention.getEvents(this)) {
			simul.addEvent(ev);
		}
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
	 * An event related to the progression to a new manifestation. Updates the state of the patient and recomputes 
	 * time to develop other complications in case the risks change.
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public class ManifestationEvent extends DiscreteEvent {
		private final DiseaseProgressionPair progress;

		public ManifestationEvent(DiseaseProgressionPair progress) {
			super(progress.getTimeToEvent());
			this.progress = progress;
		}

		@Override
		public void event() {
			final Manifestation manifestation = progress.getManifestation();
			if (Patient.this.state.contains(manifestation)) {
				error("Health state already assigned!! " + manifestation.name());
			}
			else if (mustBeExcluded(manifestation)) {
				this.cancel();
			}
			else {
				// Add the event to the list of manifestation events
				ArrayDeque<ManifestationEvent> events = null;
				if (manifestationEvents.get(manifestation) == null) {
					events = new ArrayDeque<>();
				}
				else {
					events = manifestationEvents.get(manifestation);
				}
				events.add(this);
				manifestationEvents.put(manifestation, events);
				// ...and remove it from the list of next events
				nextManifestationEvents.remove(manifestation);
				
				simul.notifyInfo(new PatientInfo(simul, Patient.this, manifestation, this.getTs()));
				if (Manifestation.Type.CHRONIC.equals(manifestation.getType())) {
					Patient.this.state.add(manifestation);
					// Removes chronic manifestations excluded by the new one
					for (Manifestation excluded : getDisease().getExcluded(manifestation)) {
						Patient.this.state.remove(excluded);						
					}
				}

				// If not already diagnosed, checks whether this manifestation leads to a diagnosis
				if (!isDiagnosed()) {
					if (manifestation.leadsToDiagnose(Patient.this)) {
						setDiagnosed(true);
						simul.notifyInfo(new PatientInfo(simul, Patient.this, PatientInfo.Type.DIAGNOSIS, manifestation, this.getTs()));
					}					
				}
				if (manifestation.leadsToDeath(Patient.this)) {
					deathEvent.cancel();
					deathEvent = new DeathEvent(ts, manifestation);
					simul.addEvent(deathEvent);
				}
				else {
					// Recompute time to death in case the risk increases
					readjustDeath(manifestation);
					// Update complications
					final DiseaseProgression progs = getDisease().getProgression(Patient.this);
					for (Manifestation st: progs.getCancelEvents()) {
						nextManifestationEvents.get(st).cancel();
						nextManifestationEvents.remove(st);
					}
					applyProgression(progs);
				}
			}
		}
		
		@Override
		public String toString() {
			return Patient.ManifestationEvent.class.getSimpleName() + "\t" + progress.getManifestation().name() + "[" + progress.getTimeToEvent() + "]";
		}

	}

	/**
	 * The event of the death of the patient.  
	 * @author Ivan Castilla Rodriguez
	 *
	 */
	public final class DeathEvent extends DiscreteEvent {
		/** Cause of the death; null if non-specific */
		final Manifestation cause;
		
		public DeathEvent(long ts) {
			this(ts, null);
		}

		public DeathEvent(long ts, Manifestation cause) {
			super(ts);
			this.cause = cause;
		}

		@Override
		public void event() {
			// Cancel all events that cannot happen now but the one that causes the death
			for (final Manifestation manif : nextManifestationEvents.keySet()) {
				if (!manif.equals(cause)) {
					final ManifestationEvent event = nextManifestationEvents.get(manif);
					if (event.getTs() >= getTs()) {
						event.cancel();
					}
				}
			}
			nextManifestationEvents.clear();
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
