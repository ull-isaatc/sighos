/**
 * 
 */
package es.ull.iis.simulation.hta.retal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;

import es.ull.iis.simulation.hta.retal.outcome.QualityAdjustedLifeExpectancy;
import es.ull.iis.simulation.hta.retal.info.PatientInfo;
import es.ull.iis.simulation.hta.retal.outcome.Cost;
import es.ull.iis.simulation.hta.retal.params.ARMDParams;
import es.ull.iis.simulation.hta.retal.params.CNVStage;
import es.ull.iis.simulation.hta.retal.params.CNVStageAndValue;
import es.ull.iis.simulation.hta.retal.params.CommonParams;
import es.ull.iis.simulation.hta.retal.params.DRParams;
import es.ull.iis.simulation.hta.retal.params.EyeStateAndValue;
import es.ull.iis.simulation.hta.retal.params.VAProgressionPair;
import es.ull.iis.simulation.model.DiscreteEvent;
import es.ull.iis.simulation.model.EventSource;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.VariableStoreSimulationObject;
import es.ull.iis.simulation.model.engine.SimulationEngine;
import es.ull.iis.util.cycle.DiscreteCycleIterator;

/**
 * Within all the class, eye1 is always indexed as 0, while eye2 is indexed as 1  
 * @author Iván Castilla
 *
 */
public class RetalPatient extends VariableStoreSimulationObject implements EventSource {
	private EnumSet<RETALSimulation.DISEASES> affectedBy = EnumSet.noneOf(RETALSimulation.DISEASES.class); 
	private final static String OBJ_TYPE_ID = "PAT";
	/** The original patient, this one was cloned from */ 
	private final RetalPatient clonedFrom;
	/** The intervention branch that this "clone" of the patient belongs to */
	protected final int nIntervention;
	/** The specific intervention assigned to the patient */
	protected final Intervention intervention;
	/** True if the patient is dead */
	private boolean dead; 
	/** Initial age of the patient (stored in days) */
	private final double initAge;
	/** Sex of the patient: 0 for men, 1 for women */
	private final int sex;
	/** Type of DM; -1 if no diabetic */
	private int diabetesType = -1;
	
	/** The timestamp of the last event executed (but the current one) */
	private long lastTs = -1;
	/** The timestamp when this patient enters the simulation */
	private long startTs;
	
	/** Random number generators for initial risks to be compared with specific probabilities */
	private final RandomForPatient rng;
	// Events
	protected DeathEvent deathEvent = null;
	private DiabetesEvent diabetesEvent = null;
	private final EARMEvent[] eARMEvent = {null, null};
	private final GAEvent[] gAEvent = {null, null};
	private final CNVEvent[] cNVEvent = {null, null};
	private final LinkedList<?>[] cNVStageEvents = {new LinkedList<CNVStageEvent>(), new LinkedList<CNVStageEvent>()};
	private final LinkedList<?>[] vaProgression = {new LinkedList<VAProgressionPair>(), new LinkedList<VAProgressionPair>()};
	private NonProliferativeDREvent[] nPDREvent = {null, null};;
	private NonHighRiskProliferativeDREvent[] nonHRPDREvent = {null, null};;
	private HighRiskProliferativeDREvent[] hRPDREvent = {null, null};;
	private ClinicallySignificantDMEEvent[] cSMEEvent = {null, null};;
	private DiagnoseEvent diagnoseEvent = null;
	/** Last timestamp when VA changed */
	private final long[] lastVAChangeTs = new long[2];
	/** Timestamp when treatmen with antiVEGF for CNV started */
	private final long[] onAntiVEGFCNV = {Long.MAX_VALUE, Long.MAX_VALUE};
	/** Timestamp when treatmen with antiVEGF for CSME started */
	private final long[] onAntiVEGFCSME = {Long.MAX_VALUE, Long.MAX_VALUE};
	
	/** The current state of the eyes of the patient */
	private final EnumSet<?>[] eyes = new EnumSet<?>[2];
	/** The current type and position of the CNV lesions in each eye; null if no CNV */
	private final CNVStage[] currentCNVStage = new CNVStage[2];
	
	private final CommonParams commonParams;
	private final ARMDParams armdParams;
	private final DRParams drParams;
	/** The cost measured for this patient */
	protected final Cost cost;
	/** The QALYs for this patient */
	protected final QualityAdjustedLifeExpectancy qaly; 

	/**
	 * Creates a patient and initializes the default events
	 * @param simul Simulation this patient is attached to
	 * @param initAge The initial age of the patient
	 * @param sex Sex of the patient
	 */
	@SuppressWarnings("unchecked")
	public RetalPatient(RETALSimulation simul, double initAge, Intervention intervention) {
		super(simul, simul.getPatientCounter(), OBJ_TYPE_ID);
		// Initialize patients with no complications
		this.intervention = intervention;
		this.nIntervention = intervention.getId();
		this.clonedFrom = null;
		this.dead = false;
		this.cost = simul.getCost();
		this.qaly = simul.getQALY();
		this.rng = new RandomForPatient();
		this.commonParams = simul.getCommonParams();
		this.armdParams = simul.getArmdParams();
		this.drParams = simul.getDrParams();

		this.initAge = 365*initAge;
		this.sex = commonParams.getSex(this);
		
		eyes[0] = EnumSet.of(EyeState.HEALTHY);
		eyes[1] = EnumSet.of(EyeState.HEALTHY);
		currentCNVStage[0] = null;
		currentCNVStage[1] = null;
		// Visual acuity supposed to be perfect at start
		final VAProgressionPair newVA = new VAProgressionPair(0, 0.0);
		lastVAChangeTs[0] += newVA.timeToChange;
		((LinkedList<VAProgressionPair>)vaProgression[0]).add(newVA);
		lastVAChangeTs[1] += newVA.timeToChange;
		((LinkedList<VAProgressionPair>)vaProgression[1]).add(newVA);
	}

	/**
	 * Creates a new patient who is a clone of another one and who is assigned to a different intervention
	 * @param original The original patient whose attributes will be cloned 
	 * @param nIntervention New intervention this clone is assigned to
	 */
	@SuppressWarnings("unchecked")
	public RetalPatient(RETALSimulation simul, RetalPatient original, Intervention intervention) {
		super(simul, original.id, OBJ_TYPE_ID);
		this.intervention = intervention;
		this.nIntervention = intervention.getId();
		this.clonedFrom = original;		
		this.dead = false;
		this.cost = original.cost;
		this.qaly = original.qaly;
		this.rng = new RandomForPatient(original.rng); 
		this.commonParams = original.commonParams;
		this.armdParams = original.armdParams;
		this.drParams = original.drParams;

		this.initAge = original.initAge;
		this.sex = original.sex;

		eyes[0] = EnumSet.of(EyeState.HEALTHY);
		eyes[1] = EnumSet.of(EyeState.HEALTHY);
		currentCNVStage[0] = null;
		currentCNVStage[1] = null;
		// Visual acuity supposed to be perfect at start
		final VAProgressionPair newVA = new VAProgressionPair(0, 0.0);
		lastVAChangeTs[0] += newVA.timeToChange;
		((LinkedList<VAProgressionPair>)vaProgression[0]).add(newVA);
		lastVAChangeTs[1] += newVA.timeToChange;
		((LinkedList<VAProgressionPair>)vaProgression[1]).add(newVA);
	}

	@Override
	public DiscreteEvent onCreate(long ts) {
		return new StartEvent(this, ts);
	}

	@Override
	public DiscreteEvent onDestroy(long ts) {
		return new FinalizeEvent(ts);
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
	 * @return
	 */
	public int getnIntervention() {
		return nIntervention;
	}

	/**
	 * @return the intervention
	 */
	public Intervention getIntervention() {
		return intervention;
	}

	/**
	 * @return the clonedFrom
	 */
	public RetalPatient getClonedFrom() {
		return clonedFrom;
	}

	/**
	 * @return the startTs
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

	
	private class PatientEvent extends DiscreteEvent {

		public PatientEvent(long ts) {
			super(ts);
		}

		/**
		 * Sets the current timestamp for this patient, saves the previous timestamp in @link(lastTs), and updates costs and QALYs.
		 * @param ts New timestamp to be assigned
		 */
		@Override
		public void event() {
			if (lastTs != ts) {
				final double initAge = TimeUnit.DAY.convert(lastTs, simul.getTimeUnit()) / 365.0; 
				final double endAge = TimeUnit.DAY.convert(ts, simul.getTimeUnit()) / 365.0;
				lastTs = this.ts;
				if (ts > 0) {
					final double periodCost = commonParams.getCostForState(RetalPatient.this, initAge, endAge);
					cost.update(RetalPatient.this, periodCost, initAge, endAge);
				}
			}
		}
	}
	private class StartEvent extends DiscreteEvent.DefaultStartEvent {

		public StartEvent(EventSource source, long ts) {
			super(source, ts);
		}
		
		@Override
		public void event() {
			super.event();
			startTs = this.getTs();
			simul.notifyInfo(new PatientInfo(simul, RetalPatient.this, PatientInfo.Type.START, this.getTs()));

			final long timeToDeath;
			final long timeToDiabetes = commonParams.getTimeToDiabetes(RetalPatient.this);
			// Checks if he/she is diabetic
			if (commonParams.isDiabetic(RetalPatient.this) || timeToDiabetes == 0) {
				diabetesType = commonParams.getDiabetesType(RetalPatient.this);
				simul.notifyInfo(new PatientInfo(simul, RetalPatient.this, PatientInfo.Type.DIABETES, this.getTs()));			
				timeToDeath = commonParams.getTimeToDeathDiabetic(RetalPatient.this);
			}
			else {
				if (timeToDiabetes < Long.MAX_VALUE) {
					diabetesEvent = new DiabetesEvent(timeToDiabetes);
					simul.addEvent(diabetesEvent);
				}
				timeToDeath = commonParams.getTimeToDeath(RetalPatient.this);
			}
			// Assign death event
			deathEvent = new DeathEvent(timeToDeath);
			simul.addEvent(deathEvent);
			
			if (RETALSimulation.ACTIVE_DISEASES.contains(RETALSimulation.DISEASES.ARMD)) {
				// Schedules ARMD-related events
				final long timeToEvent = armdParams.getTimeToEARM(RetalPatient.this);
				final EyeStateAndValue timeToAMD = armdParams.getTimeToE1AMD(RetalPatient.this);
				// Schedule an EARM event
				if (timeToEvent < Long.MAX_VALUE) {
					eARMEvent[0] = new EARMEvent(timeToEvent, 0);
					simul.addEvent(eARMEvent[0]);
				}
				// Schedule either a CNV or a GA event
				else if (timeToAMD != null) {
					if (timeToAMD.getState() == EyeState.AMD_CNV) {
						cNVEvent[0] = new CNVEvent(timeToAMD.getValue(), 0);
						simul.addEvent(cNVEvent[0]);				
					}
					else {
						gAEvent[0] = new GAEvent(timeToAMD.getValue(), 0);
						simul.addEvent(gAEvent[0]);				
					}
				}
			}
			if (RETALSimulation.ACTIVE_DISEASES.contains(RETALSimulation.DISEASES.DR)) {
				if (isDiabetic()) {
					// TODO: Check if it has sense to go directly from diabetes to some degree of DR. It would make sense if it were 
					// "diagnosed" diabetes and not "real" diabetes
					final EnumSet<EyeState>[] startWith = drParams.startsWith(RetalPatient.this);
					// Initializes the state of both eyes
					assignDiabetesInitialEyeState(startWith[0], 0);
					assignDiabetesInitialEyeState(startWith[1], 1);
					// Check if the new state arises the diagnosis
					checkDiagnosis();
				}
			}
			
			// Schedules screening-related events
			if (intervention instanceof Screening) {
				final DiscreteCycleIterator screeningIterator = ((Screening)intervention).getScreeningCycle().getCycle().iterator(simul.getStartTs(), simul.getEndTs());
				simul.addEvent(new ScreeningEvent(screeningIterator.next(), screeningIterator));
			}
		}
		
	}
	
	private class FinalizeEvent extends PatientEvent {

		public FinalizeEvent(long ts) {
			super(ts);
		}
		
		@Override
		public void event() {
			super.event();
        	debug("Ends execution");
			simul.notifyInfo(new PatientInfo(simul, RetalPatient.this, PatientInfo.Type.FINISH, this.getTs()));
		}
	}

	/**
	 * @return the initial age of the patient
	 */
	public double getInitAge() {
		return initAge / 365;
	}

	/**
	 * 
	 * @return the current age of the patient
	 */
	public double getAge() {
		return (initAge + simul.getSimulationEngine().getTs() - startTs) / 365.0;
	}
	
	/**
	 * @return the sex
	 */
	public int getSex() {
		return sex;
	}

	/**
	 * @return the diabetesType
	 */
	public int getDiabetesType() {
		return diabetesType;
	}

	public boolean isDiabetic() {
		return diabetesType != -1;
	}

	public double getAgeAtDeath() {
		final long timeToDeath = getTimeToDeath();
		return (timeToDeath == Long.MAX_VALUE) ? Double.MAX_VALUE : ((initAge + timeToDeath) / 365.0);
	}
	
	/**
	 * @return the timeToDeath
	 */
	public long getTimeToDeath() {
		return (deathEvent == null) ? Long.MAX_VALUE : deathEvent.getTs();
	}

	public long getTimeToDiabetes() {
		return (diabetesEvent == null) ? Long.MAX_VALUE : diabetesEvent.getTs();
	}

	public double getAgeAtDiabetes() {
		if (diabetesEvent == null)
			return Double.MAX_VALUE;
		return (initAge + diabetesEvent.getTs()) / 365.0;
	}
	
	/**
	 * Returns the timestamp when the patient started treatment with antiVEGF in the specified eye for CNV
	 * @param eyeIndex Index of the eye (0 for first eye, 1 for second eye)
	 * @return the timestamp when the patient started treatment with antiVEGF in the specified eye for CNV
	 */
	public long getOnAntiVEGFCNV(int eyeIndex) {
		return onAntiVEGFCNV[eyeIndex];
	}

	/**
	 * Returns the timestamp when the patient started treatment with antiVEGF in the specified eye for CSME
	 * @param eyeIndex Index of the eye (0 for first eye, 1 for second eye)
	 * @return the timestamp when the patient started treatment with antiVEGF in the specified eye for CSME
	 */
	public long getOnAntiVEGFCSME(int eyeIndex) {
		return onAntiVEGFCSME[eyeIndex];
	}

	/**
	 * @return the diagnosed
	 */
	public boolean isDiagnosed() {
		if (diagnoseEvent == null)
			return false;
		return (diagnoseEvent.getTs() <= simul.getSimulationEngine().getTs());
	}
	
	/**
	 * Updates the state of the patient to reflect whether he/she has been diagnosed
	 */
	public void checkDiagnosis() {
		final long timeToDiagnosis = (diagnoseEvent == null) ? deathEvent.getTs() : diagnoseEvent.getTs(); 
		if (timeToDiagnosis > simul.getSimulationEngine().getTs()) {
			long timeToARMDDiagnosis = Long.MAX_VALUE;
			if (affectedBy.contains(RETALSimulation.DISEASES.ARMD)) {
				timeToARMDDiagnosis = armdParams.getTimeToClinicalPresentation(this);
			}
			long timeToDRDiagnosis = Long.MAX_VALUE;
			if (affectedBy.contains(RETALSimulation.DISEASES.DR)) {
				timeToDRDiagnosis = drParams.getTimeToClinicalPresentation(this);
			}
			if (timeToARMDDiagnosis < timeToDiagnosis) {
				if (timeToDiagnosis < deathEvent.getTs()) {
					diagnoseEvent.cancel();
				}					
				if (timeToDRDiagnosis < timeToARMDDiagnosis) {
					diagnoseEvent = new DiagnoseEvent(timeToDRDiagnosis);					
				}
				else {
					diagnoseEvent = new DiagnoseEvent(timeToARMDDiagnosis);					
				}
				simul.addEvent(diagnoseEvent);
			}
			else if (timeToDRDiagnosis < timeToDiagnosis) {
				if (timeToDiagnosis < deathEvent.getTs()) {
					diagnoseEvent.cancel();
				}					
				diagnoseEvent = new DiagnoseEvent(timeToDRDiagnosis);					
				simul.addEvent(diagnoseEvent);				
			}
		}
	}

	/**
	 * @return the affectedBy
	 */
	public EnumSet<RETALSimulation.DISEASES> getAffectedBy() {
		return affectedBy;
	}

	/**
	 * @param eyeIndex Index of the eye (0 for first eye, 1 for second eye)
	 * @return the state of the first eye
	 */
	@SuppressWarnings("unchecked")
	public EnumSet<EyeState> getEyeState(int eyeIndex) {
		return (EnumSet<EyeState>) eyes[eyeIndex];
	}

	/**
	 * @param eyeIndex Index of the eye (0 for first eye, 1 for second eye)
	 * @return the currentCNVStage
	 */
	public CNVStage getCurrentCNVStage(int eyeIndex) {
		return currentCNVStage[eyeIndex];
	}

	/**
	 * @return the va
	 */
	@SuppressWarnings("unchecked")
	public double getVA(int eyeIndex) {
		return ((LinkedList<VAProgressionPair>)vaProgression[eyeIndex]).getLast().va;
	}

	/** 
	 * Updates the progression and current visual acuity of the specified eye. The progression is updated with the list of changes.
	 * The last change is used to set the current visual acuity. 
	 * @param newVAs List of changes in visual acuity since the last update 
	 * @param eyeIndex Index of the affected eye (0 for first eye, 1 for second eye)
	 */
	@SuppressWarnings("unchecked")
	private void updateVA(ArrayList<VAProgressionPair> newVAs, int eyeIndex) {
		for (VAProgressionPair pair : newVAs) {
			lastVAChangeTs[eyeIndex] += pair.timeToChange;
			((LinkedList<VAProgressionPair>)vaProgression[eyeIndex]).add(pair);
		}
	}

	/**
	 * @return the vaProgression
	 */
	@SuppressWarnings("unchecked")
	public LinkedList<VAProgressionPair> getVaProgression(int eyeIndex) {
		return (LinkedList<VAProgressionPair>)vaProgression[eyeIndex];
	}

	/**
	 * @param eyeIndex Index of the affected eye (0 for first eye, 1 for second eye)
	 * @return the lastVAChangeTs
	 */
	public long getLastVAChangeTs(int eyeIndex) {
		return lastVAChangeTs[eyeIndex];
	}

	/**
	 * Returns true if both eyes are healthy
	 * @return True if both eyes are healthy; false in other case.
	 */
	public boolean isHealthy() {
		return eyes[0].contains(EyeState.HEALTHY) && eyes[1].contains(EyeState.HEALTHY);
	}
	
	/**
	 * Returns a random number between 0 and 1 for the specified item
	 * @param item The case that requires a random number
	 * @return a random number between 0 and 1 for the specified item
	 */
	public double draw(RandomForPatient.ITEM item) {
		return rng.draw(item);
	}
	
	/**
	 * Returns N random number between 0 and 1 for the specified item
	 * @param item The case that requires a random number
	 * @param n The number of random numbers to return
	 * @return N random number between 0 and 1 for the specified item
	 */
	public double[] draw(RandomForPatient.ITEM item, int n) {
		return rng.draw(item, n);
	}
	
	/**
	 * @return the time to a specific eye state
	 */
	public long getTimeToEyeState(EyeState state, int eyeIndex) {
		final long time;
		switch (state) {
		case AMD_CNV:
			time = (cNVEvent[eyeIndex] == null) ? Long.MAX_VALUE : cNVEvent[eyeIndex].getTs();
			break;
		case AMD_GA:
			time = (gAEvent[eyeIndex] == null) ? Long.MAX_VALUE : gAEvent[eyeIndex].getTs();
			break;
		case EARM:
			time = (eARMEvent[eyeIndex] == null) ? Long.MAX_VALUE : eARMEvent[eyeIndex].getTs();
			break;
		case HEALTHY:
			time = 0;
			break;
		case CSME:
			time = (cSMEEvent[eyeIndex] == null) ? Long.MAX_VALUE : cSMEEvent[eyeIndex].getTs();
			break;
		case NPDR:
			time = (nPDREvent[eyeIndex] == null) ? Long.MAX_VALUE : nPDREvent[eyeIndex].getTs(); 
			break;
		case NON_HR_PDR:
			time = (nonHRPDREvent[eyeIndex] == null) ? Long.MAX_VALUE : nonHRPDREvent[eyeIndex].getTs();
			break;
		case HR_PDR:
			time = (hRPDREvent[eyeIndex] == null) ? Long.MAX_VALUE : hRPDREvent[eyeIndex].getTs(); 
			break;
		default:
			time = Long.MAX_VALUE;
		}
		return time;
	}

	/**
	 * 
	 * @param stage
	 * @param eyeIndex
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public long getTimeToCNVStage(CNVStage stage, int eyeIndex) {
		for (CNVStageEvent event : ((LinkedList<CNVStageEvent>)cNVStageEvents[eyeIndex])) {			
			if (stage.equals(event.getNewStage()))
				return event.getTs();
		}
		return Long.MAX_VALUE;
	}
	
	/**
	 * 
	 * @param stage
	 * @param eyeIndex
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public double getAgeAt(CNVStage stage, int eyeIndex) {
		for (CNVStageEvent event : ((LinkedList<CNVStageEvent>)cNVStageEvents[eyeIndex])) {			
			if (stage.equals(event.getNewStage()))
				return (initAge + event.getTs()) / 365.0;
		}
		return Double.MAX_VALUE;
	}
	
	/**
	 * Returns the age that the patient had when a particular eye state was reached
	 * @param state
	 * @param eyeIndex
	 * @return The age that the patient had when a particular eye state was reached
	 */
	public double getAgeAt(EyeState state, int eyeIndex) {
		long ageAt = getTimeToEyeState(state, eyeIndex);
		if (ageAt != Long.MAX_VALUE)
			return (initAge + ageAt) / 365.0;
		return Double.MAX_VALUE;
	}
	
	/**
	 * Last things to do when the patient is death, and before the {@link FinalizeEvent} event is launched.
	 */
	protected void death() {
		simul.notifyInfo(new PatientInfo(simul, RetalPatient.this, PatientInfo.Type.DEATH, this.getTs()));
		// Updates VA changes produced since the last event
		updateVA(commonParams.getVAProgressionToDeath(this, 0), 0);
		updateVA(commonParams.getVAProgressionToDeath(this, 1), 1);
		((es.ull.iis.simulation.hta.retal.outcome.QualityAdjustedLifeExpectancy)qaly).update(this, commonParams.getUtilityFromVA(this));
	}
	
	@SuppressWarnings("unchecked")
	private void assignDiabetesInitialEyeState(EnumSet<EyeState> startWith, int eyeIndex) {
		final long ts = simul.getSimulationEngine().getTs();
		if (startWith.size() > 0) {
			eyes[eyeIndex].remove(EyeState.HEALTHY);
			for (EyeState state : startWith)
				((EnumSet<EyeState>)eyes[eyeIndex]).add(state);
			affectedBy.add(RETALSimulation.DISEASES.DR);

			if (eyes[eyeIndex].contains(EyeState.NPDR)) {
				// Assign the event but not execute it. This is done to ensure that "timeTo..." and "ageAt..." methods work fine 
				nPDREvent[eyeIndex] = new NonProliferativeDREvent(ts, eyeIndex);
				simul.notifyInfo(new PatientInfo(simul, RetalPatient.this, EyeState.NPDR, eyeIndex, ts));						
				final long timeToEvent = drParams.getTimeToPDR(RetalPatient.this);
				if (timeToEvent < Long.MAX_VALUE) {
					nonHRPDREvent[eyeIndex] = new NonHighRiskProliferativeDREvent(timeToEvent, eyeIndex);
					simul.addEvent(nonHRPDREvent[eyeIndex]);
				}
				if (!eyes[eyeIndex].contains(EyeState.CSME)) {
					final long timeToCSME = drParams.getTimeToCSME(RetalPatient.this);
					if (timeToCSME < Long.MAX_VALUE) {
						cSMEEvent[eyeIndex] = new ClinicallySignificantDMEEvent(timeToCSME, eyeIndex);
						simul.addEvent(cSMEEvent[eyeIndex]);
					}
				}
			}
			else if (eyes[eyeIndex].contains(EyeState.NON_HR_PDR)) {
				// Assign the event but not execute it. This is done to ensure that "timeTo..." and "ageAt..." methods work fine 
				nonHRPDREvent[eyeIndex] = new NonHighRiskProliferativeDREvent(ts, eyeIndex);
				simul.notifyInfo(new PatientInfo(simul, RetalPatient.this, EyeState.NON_HR_PDR, eyeIndex, ts));						
				if (!eyes[eyeIndex].contains(EyeState.CSME)) {
					final long timeToCSME = drParams.getTimeToCSMEAndNonHRPDRFromNonHRPDR(RetalPatient.this);
					if (timeToCSME < Long.MAX_VALUE) {
						cSMEEvent[eyeIndex] = new ClinicallySignificantDMEEvent(timeToCSME, eyeIndex);
						simul.addEvent(cSMEEvent[eyeIndex]);
					}
					final long timeToHRPDR = drParams.getTimeToHRPDRFromNonHRPDR(RetalPatient.this);
					if (timeToHRPDR < Long.MAX_VALUE) {
						hRPDREvent[eyeIndex] = new HighRiskProliferativeDREvent(timeToHRPDR, eyeIndex);
						simul.addEvent(hRPDREvent[eyeIndex]);
					}					
				}
				else {
					final long timeToHRPDR = drParams.getTimeToCSMEAndHRPDRFromCSMEAndNonHRPDR(RetalPatient.this);
					if (timeToHRPDR < Long.MAX_VALUE) {
						hRPDREvent[eyeIndex] = new HighRiskProliferativeDREvent(timeToHRPDR, eyeIndex);
						simul.addEvent(hRPDREvent[eyeIndex]);
					}										
				}				
			}
			else if (eyes[eyeIndex].contains(EyeState.HR_PDR)) {
				// Assign the event but not execute it. This is done to ensure that "timeTo..." and "ageAt..." methods work fine 
				hRPDREvent[eyeIndex] = new HighRiskProliferativeDREvent(ts, eyeIndex);
				simul.notifyInfo(new PatientInfo(simul, RetalPatient.this, EyeState.HR_PDR, eyeIndex, ts));						
				if (!eyes[eyeIndex].contains(EyeState.CSME)) {
					final long timeToCSME = drParams.getTimeToCSMEAndHRPDRFromHRPDR(RetalPatient.this);
					if (timeToCSME < Long.MAX_VALUE) {
						cSMEEvent[eyeIndex] = new ClinicallySignificantDMEEvent(timeToCSME, eyeIndex);
						simul.addEvent(cSMEEvent[eyeIndex]);
					}
				}
			}
			if (eyes[eyeIndex].contains(EyeState.CSME)) {
				// Assign the event but not execute it. This is done to ensure that "timeTo..." and "ageAt..." methods work fine 
				cSMEEvent[eyeIndex] = new ClinicallySignificantDMEEvent(ts, eyeIndex);
				simul.notifyInfo(new PatientInfo(simul, RetalPatient.this, EyeState.CSME, eyeIndex, ts));						
			}
			// Update the visual acuity to reflect the incident problem
			final ArrayList<VAProgressionPair> newVA = new ArrayList<VAProgressionPair>(); 
			newVA.add(new VAProgressionPair(ts, commonParams.getInitialVA(RetalPatient.this, eyeIndex)));			
			updateVA(newVA, eyeIndex);			
		}
		else {
			final long timeToEvent = drParams.getTimeToNPDR(RetalPatient.this);
			if (timeToEvent < Long.MAX_VALUE) {
				nPDREvent[eyeIndex] = new NonProliferativeDREvent(timeToEvent, eyeIndex);
				simul.addEvent(nPDREvent[eyeIndex]);
			}
		}
	}
	
	protected final class EARMEvent extends PatientEvent {
		private final int eyeIndex;
		
		public EARMEvent(long ts, int eyeIndex) {
			super(ts);
			this.eyeIndex = eyeIndex;
		}

		@Override
		public void event() {
			// Update VA changes before changing the state. Theoretically, no changes should occur with healthy eyes
			updateVA(commonParams.getVAProgression(RetalPatient.this, eyeIndex, EyeState.EARM), eyeIndex);
			@SuppressWarnings("unchecked")
			final EnumSet<EyeState> affectedEye = (EnumSet<EyeState>)eyes[eyeIndex];
			affectedEye.remove(EyeState.HEALTHY);
			affectedEye.add(EyeState.EARM);
			simul.notifyInfo(new PatientInfo(simul, RetalPatient.this, EyeState.EARM, eyeIndex, this.getTs()));
			affectedBy.add(RETALSimulation.DISEASES.ARMD);

			EyeStateAndValue timeAndState = armdParams.getTimeToAMDFromEARM(RetalPatient.this, eyeIndex);
			if (timeAndState != null) {
				if (EyeState.AMD_CNV == timeAndState.getState()) {
					cNVEvent[eyeIndex] = new CNVEvent(timeAndState.getValue(), eyeIndex);
					simul.addEvent(cNVEvent[eyeIndex]);
				}
				else if (EyeState.AMD_GA == timeAndState.getState()) {
					gAEvent[eyeIndex] = new GAEvent(timeAndState.getValue(), eyeIndex);
					simul.addEvent(gAEvent[eyeIndex]);
				}
			}
			// Schedule events for fellow eye if needed
			if (eyeIndex == 0) {
				final EyeStateAndValue timeAndStateE2 = armdParams.getTimeToE2AMD(RetalPatient.this);
				if (timeAndStateE2 != null) {
					if (EyeState.AMD_CNV == timeAndStateE2.getState()) {
						cNVEvent[1] = new CNVEvent(timeAndStateE2.getValue(), 1);
						simul.addEvent(cNVEvent[1]);
					}
					else if (EyeState.AMD_GA == timeAndStateE2.getState()) {
						gAEvent[1] = new GAEvent(timeAndStateE2.getValue(), 1);
						simul.addEvent(gAEvent[1]);
					}
				}
			}
			// Checks diagnosis
			checkDiagnosis();
		}
		
	}

	protected abstract class AMDEvent extends PatientEvent {
		protected final int eyeIndex;
		public AMDEvent(long ts, int eyeIndex) {
			super(ts);
			this.eyeIndex = eyeIndex;
		}
		
		protected void checkFellowEye() {
			@SuppressWarnings("unchecked")
			final EnumSet<EyeState> fellowEye = (EnumSet<EyeState>)eyes[1 - eyeIndex];
			// If the other eye had CNV there's nothing else to do...
			//... but if it had GA...
			if (fellowEye.contains(EyeState.AMD_GA)) {
				final long newTimeToCNV = armdParams.getTimeToCNVFromGA(RetalPatient.this, 1 - eyeIndex);
				if (newTimeToCNV < getTimeToEyeState(EyeState.AMD_CNV, 1 - eyeIndex)) {
					// If a CNV event was previously scheduled to happen later than the new event
					// we have to cancel it 
					if (cNVEvent[1 - eyeIndex] != null) {
						cNVEvent[1 - eyeIndex].cancel();
					}
					cNVEvent[1 - eyeIndex] = new CNVEvent(newTimeToCNV, 1 - eyeIndex);
					simul.addEvent(cNVEvent[1 - eyeIndex]);
				}
			}
			// Only the first eye could have EARM
			else if (fellowEye.contains(EyeState.EARM)) {
				// Recompute time to event
				final EyeStateAndValue timeAndState = armdParams.getTimeToAMDFromEARM(RetalPatient.this, 1 - eyeIndex);
				// If a valid event appeared
				if (timeAndState != null) {
					rescheduleAMDEvent(timeAndState, 1 - eyeIndex);
				}
			}
			// Only the second eye could be healthy
			else if (fellowEye.contains(EyeState.HEALTHY)) {
				// Recompute time to event
				final EyeStateAndValue timeAndState = armdParams.getTimeToE2AMD(RetalPatient.this);
				// If a valid event appeared
				if (timeAndState != null) {
					rescheduleAMDEvent(timeAndState, 1 - eyeIndex);
				}
			}
		}
		
		private void rescheduleAMDEvent(EyeStateAndValue timeAndState, int eye) {
			// If the new event happens before the already scheduled ones (in case an AMD was already schedule)
			if (timeAndState.getValue() < getTimeToEyeState(EyeState.AMD_CNV, eye) || timeAndState.getValue() < getTimeToEyeState(EyeState.AMD_GA, eye)) {
				// If a CNV event was previously scheduled to happen later than the new event
				// we have to cancel it 
				if (cNVEvent[eye] != null) {
					cNVEvent[eye].cancel();
				}
				// If a GA event was previously scheduled to happen later than the new event
				// we have to cancel it 
				if (gAEvent[eye] != null) {
					gAEvent[eye].cancel();
				}
				// Schedule the new event
				if (EyeState.AMD_CNV == timeAndState.getState()) {
					cNVEvent[eye] = new CNVEvent(timeAndState.getValue(), eye);
					simul.addEvent(cNVEvent[eye]);
				}
				else if (EyeState.AMD_GA == timeAndState.getState()) {
					gAEvent[eye] = new GAEvent(timeAndState.getValue(), eye);
					simul.addEvent(gAEvent[eye]);
				}
			}
		}
	}
	
	protected final class GAEvent extends AMDEvent {

		public GAEvent(long ts, int eyeIndex) {
			super(ts, eyeIndex);
		}

		@Override
		public boolean cancel() {
			if (super.cancel()) {
				gAEvent[eyeIndex] = null;
				return true;
			}
			return false;
		}
		
		@Override
		public void event() {
			// Update VA changes before changing the state. Theoretically, no changes should occur with EARM eyes
			updateVA(commonParams.getVAProgression(RetalPatient.this, eyeIndex, EyeState.AMD_GA), eyeIndex);
			@SuppressWarnings("unchecked")
			final EnumSet<EyeState> affectedEye = (EnumSet<EyeState>)eyes[eyeIndex];
			// Remove previous stages
			affectedEye.remove(EyeState.HEALTHY);
			affectedEye.remove(EyeState.EARM);
			
			// Assign new stage
			affectedEye.add(EyeState.AMD_GA);
			simul.notifyInfo(new PatientInfo(simul, RetalPatient.this, EyeState.AMD_GA, eyeIndex, this.getTs()));
			affectedBy.add(RETALSimulation.DISEASES.ARMD);

			// Schedule a CNV event
			final long timeToEvent = armdParams.getTimeToCNVFromGA(RetalPatient.this, eyeIndex);
			if (timeToEvent < Long.MAX_VALUE) {
				cNVEvent[eyeIndex] = new CNVEvent(timeToEvent, eyeIndex);
				simul.addEvent(cNVEvent[eyeIndex]);
			}
			
			// When the disease advances in an eye, the risk for the fellow eye increases
			checkFellowEye();
			
			// Checks diagnosis
			checkDiagnosis();
		}			
		
	}
	
	protected final class CNVEvent extends AMDEvent {

		public CNVEvent(long ts, int eyeIndex) {
			super(ts, eyeIndex);
		}

		@Override
		public boolean cancel() {
			if (super.cancel()) {
				cNVEvent[eyeIndex] = null;
				return true;
			}
			return false;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void event() {
			if (cancelled)
				error(this.getClass().getName() + " event was cancelled");
			// Advances the calculation of the incident CNV stage
			final CNVStage stage = armdParams.getInitialCNVStage(RetalPatient.this, eyeIndex);			
			// Update VA changes before changing the state. 
			updateVA(commonParams.getVAProgression(RetalPatient.this, eyeIndex, stage), eyeIndex);
			
			final EnumSet<EyeState> affectedEye = (EnumSet<EyeState>)eyes[eyeIndex];
			// Remove previous stages
			affectedEye.remove(EyeState.HEALTHY);
			affectedEye.remove(EyeState.EARM);
			affectedEye.remove(EyeState.AMD_GA);

			// Assign new stage
			affectedEye.add(EyeState.AMD_CNV);
			simul.notifyInfo(new PatientInfo(simul, RetalPatient.this, EyeState.AMD_CNV, eyeIndex, this.getTs()));
			affectedBy.add(RETALSimulation.DISEASES.ARMD);

			// Assign specific CNV stage
			final CNVStageEvent newEvent = new CNVStageEvent(ts, stage, eyeIndex);
			((LinkedList<CNVStageEvent>)cNVStageEvents[eyeIndex]).add(newEvent);
			simul.addEvent(newEvent);
			// When the disease advances in an eye, the risk for the fellow eye increases
			checkFellowEye();
			
			// Checks diagnosis
			checkDiagnosis();
			// If already diagnosed, the treatment with antiVEGF starts
			if (isDiagnosed()) {
				onAntiVEGFCNV[eyeIndex] = ts;
			}
		}		
	}
	
	protected final class CNVStageEvent extends PatientEvent {
		private final int eyeIndex;
		private final CNVStage newStage;
		
		public CNVStageEvent(long ts, CNVStage newStage, int eyeIndex) {
			super(ts);
			this.eyeIndex = eyeIndex;
			this.newStage = newStage;
		}

		/**
		 * @return the newStage
		 */
		public CNVStage getNewStage() {
			return newStage;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void event() {
			if (cancelled)
				error(this.getClass().getName() + " event was cancelled");
			// Only update VA if it's a progression in CNV stage and not setting the first CNV stage
			if (currentCNVStage[eyeIndex] != null) {
				updateVA(commonParams.getVAProgression(RetalPatient.this, eyeIndex, newStage), eyeIndex);				
			}
			currentCNVStage[eyeIndex] = newStage;
			simul.notifyInfo(new PatientInfo(simul, RetalPatient.this, newStage, eyeIndex, this.getTs()));
			// Schedule next CNV stage
			CNVStageAndValue nextStage = armdParams.getTimeToNextCNVStage(RetalPatient.this, eyeIndex);
			if (nextStage != null) {
				final CNVStageEvent newEvent = new CNVStageEvent(nextStage.getValue(), nextStage.getStage(), eyeIndex);
				((LinkedList<CNVStageEvent>)cNVStageEvents[eyeIndex]).add(newEvent);
				simul.addEvent(newEvent);
			}
			
			// Checks diagnosis
			checkDiagnosis();
			// If state worsens, the treatment with antiVEGF starts again
			if (isDiagnosed()) {
				onAntiVEGFCNV[eyeIndex] = ts;
			}
		}
		
	}
	
	protected final class ScreeningEvent extends PatientEvent {
		private final DiscreteCycleIterator iterator;

		public ScreeningEvent(long ts, DiscreteCycleIterator screeningIterator) {
			super(ts);
			this.iterator = screeningIterator;
		}

		@Override
		public void event() {
			if (((Screening)intervention).isAttending(RetalPatient.this)) {
				final double screenCost = commonParams.getScreeningCost(RetalPatient.this);
				cost.update(RetalPatient.this, screenCost, getAge() - CommonParams.MIN_AGE);
				// RetalPatient healthy
				if (isHealthy()) {
					// True negative
					if (rng.draw(RandomForPatient.ITEM.SPECIFICITY) <= ((Screening)intervention).getSpecificity(RetalPatient.this)) {
						simul.notifyInfo(new PatientInfo(simul, RetalPatient.this, PatientInfo.ScreeningResult.TN, this.getTs()));
					}
					// False positive
					else {
						simul.notifyInfo(new PatientInfo(simul, RetalPatient.this, PatientInfo.ScreeningResult.FP, this.getTs()));
						final double diagnosisCost = commonParams.getDiagnosisCost(RetalPatient.this);
						cost.update(RetalPatient.this, diagnosisCost, getAge() - CommonParams.MIN_AGE);
					}
					// Schedule next screening appointment (if required) 
					long next = iterator.next();
					if (next != -1) {
						simul.addEvent(new ScreeningEvent(next, iterator));
					}
				}
				// RetalPatient ill
				else {
					// False negative
					if (rng.draw(RandomForPatient.ITEM.SENSITIVITY) > ((Screening)intervention).getSensitivity(RetalPatient.this)) {
						simul.notifyInfo(new PatientInfo(simul, RetalPatient.this, PatientInfo.ScreeningResult.FN, this.getTs()));
						// Schedule next screening appointment (if required) 
						long next = iterator.next();
						if (next != -1) {
							simul.addEvent(new ScreeningEvent(next, iterator));
						}
					}
					// True positive
					else {
						simul.notifyInfo(new PatientInfo(simul, RetalPatient.this, PatientInfo.ScreeningResult.TP, this.getTs()));
						if (diagnoseEvent != null)
							diagnoseEvent.cancel();
						diagnoseEvent = new DiagnoseEvent(ts);
						simul.addEvent(diagnoseEvent);
						// TODO: Add changes of true positive						
					}					
				}
			}
			else {
				simul.notifyInfo(new PatientInfo(simul, RetalPatient.this, PatientInfo.ScreeningResult.NA, this.getTs()));
				// Schedule next screening appointment (if required) 
				long next = iterator.next();
				if (next != -1) {
					simul.addEvent(new ScreeningEvent(next, iterator));
				}

			}
		}
		
	}
	
	protected final class DiagnoseEvent extends PatientEvent {
		
		public DiagnoseEvent(long ts) {
			super(ts);
		}

		@Override
		public void event() {
			final double diagnosisCost = commonParams.getDiagnosisCost(RetalPatient.this);
			cost.update(RetalPatient.this, diagnosisCost, getAge() - CommonParams.MIN_AGE);
			simul.notifyInfo(new PatientInfo(simul, RetalPatient.this, PatientInfo.Type.DIAGNOSED, this.getTs()));
			// If suffering CNV when diagnosed, the treatment with antiVEGF starts
			if (eyes[0].contains(EyeState.AMD_CNV))
				onAntiVEGFCNV[0] = ts;
			if (eyes[1].contains(EyeState.AMD_CNV))
				onAntiVEGFCNV[1] = ts;
			// If suffering CSME when diagnosed, the treatment with antiVEGF starts
			if (eyes[0].contains(EyeState.CSME))
				onAntiVEGFCSME[0] = ts;
			if (eyes[1].contains(EyeState.CSME))
				onAntiVEGFCSME[1] = ts;
		}
		
		@Override
		public boolean cancel() {
			if (super.cancel()) {
				diagnoseEvent = null;
				return true;
			}
			return false;
		}
	}
	
	protected final class DiabetesEvent extends PatientEvent {
		
		public DiabetesEvent(long ts) {
			super(ts);
		}
		
		@Override
		public void event() {
			diabetesType = commonParams.getDiabetesType(RetalPatient.this);
			simul.notifyInfo(new PatientInfo(simul, RetalPatient.this, PatientInfo.Type.DIABETES, this.getTs()));			
			
			if (RETALSimulation.ACTIVE_DISEASES.contains(RETALSimulation.DISEASES.DR)) {
				// TODO: Check if it has sense to go directly from diabetes to some degree of DR. It would make sense if it were 
				// "diagnosed" diabetes and not "real" diabetes
				final EnumSet<EyeState>[] startWith = drParams.startsWith(RetalPatient.this);
				// Initializes the state of both eyes
				assignDiabetesInitialEyeState(startWith[0], 0);
				assignDiabetesInitialEyeState(startWith[1], 1);
				// Check if the new state arises the diagnosis
				checkDiagnosis();
			}
			long newTimeToDeath = commonParams.getTimeToDeathDiabetic(RetalPatient.this);
			if (newTimeToDeath < getTimeToDeath()) {
				if (deathEvent != null) {
					deathEvent.cancel();
					deathEvent = new DeathEvent(newTimeToDeath);
					simul.addEvent(deathEvent);
					// Cancel all pending events scheduled to happen after death
					if (diabetesEvent != null) {
						if (diabetesEvent.getTs() >= newTimeToDeath) {
							diabetesEvent.cancel();
							diabetesEvent = null;
						}
					}
					if (diagnoseEvent != null) {
						if (diagnoseEvent.getTs() >= newTimeToDeath) {
							diagnoseEvent.cancel();
							diagnoseEvent = null;
						}
					}
					for (int i = 0; i < 2; i++) {
						if (eARMEvent[i] != null) {
							if (eARMEvent[i].getTs() >= newTimeToDeath) {
								eARMEvent[i].cancel();
								eARMEvent[i] = null;
							}
						}
						if (gAEvent[i] != null) {
							if (gAEvent[i].getTs() >= newTimeToDeath) {
								gAEvent[i].cancel();
								gAEvent[i] = null;
							}
						}
						if (cNVEvent[i] != null) {
							if (cNVEvent[i].getTs() >= newTimeToDeath) {
								cNVEvent[i].cancel();
								cNVEvent[i] = null;
							}
						}
						if (nPDREvent[i] != null) {
							if (nPDREvent[i].getTs() >= newTimeToDeath) {
								nPDREvent[i].cancel();
								nPDREvent[i] = null;
							}
						}
						if (nonHRPDREvent[i] != null) {
							if (nonHRPDREvent[i].getTs() >= newTimeToDeath) {
								nonHRPDREvent[i].cancel();
								nonHRPDREvent[i] = null;
							}
						}
						if (hRPDREvent[i] != null) {
							if (hRPDREvent[i].getTs() >= newTimeToDeath) {
								hRPDREvent[i].cancel();
								hRPDREvent[i] = null;
							}
						}
						if (cSMEEvent[i] != null) {
							if (cSMEEvent[i].getTs() >= newTimeToDeath) {
								cSMEEvent[i].cancel();
								cSMEEvent[i] = null;
							}
						}
						@SuppressWarnings("unchecked")
						Iterator<CNVStageEvent> iter = (Iterator<CNVStageEvent>)cNVStageEvents[i].iterator();
						while (iter.hasNext()) {
							final CNVStageEvent event = iter.next();
							if (event.getTs() >= newTimeToDeath) {
								event.cancel();
								iter.remove();
							}
						}
					}
				}
				else {
					deathEvent = new DeathEvent(newTimeToDeath);
					simul.addEvent(deathEvent);
				}
			}
		}		
	}
	
	protected final class NonProliferativeDREvent extends PatientEvent {
		private final int eyeIndex;
		
		public NonProliferativeDREvent(long ts, int eyeIndex) {
			super(ts);
			this.eyeIndex = eyeIndex;
		}

		@Override
		public boolean cancel() {
			if (super.cancel()) {
				nPDREvent[eyeIndex] = null;
				return true;
			}
			return false;
		}
		
		@Override
		public void event() {
			// Update VA changes before changing the state. 
			updateVA(commonParams.getVAProgression(RetalPatient.this, eyeIndex, EyeState.NPDR), eyeIndex);
			@SuppressWarnings("unchecked")
			final EnumSet<EyeState> affectedEye = (EnumSet<EyeState>)eyes[eyeIndex];

			// Remove previous stages
			affectedEye.remove(EyeState.HEALTHY);
			
			// Assign new stage
			affectedEye.add(EyeState.NPDR);
			simul.notifyInfo(new PatientInfo(simul, RetalPatient.this, EyeState.NPDR, eyeIndex, this.getTs()));
			affectedBy.add(RETALSimulation.DISEASES.DR);
			
			// Schedules events for PDR and CSME 
			final long timeToEvent = drParams.getTimeToPDR(RetalPatient.this);
			if (timeToEvent < Long.MAX_VALUE) {
				nonHRPDREvent[eyeIndex] = new NonHighRiskProliferativeDREvent(timeToEvent, eyeIndex);
				simul.addEvent(nonHRPDREvent[eyeIndex]);
			}
			final long timeToCSME = drParams.getTimeToCSME(RetalPatient.this);
			if (timeToCSME < Long.MAX_VALUE) {
				cSMEEvent[eyeIndex] = new ClinicallySignificantDMEEvent(timeToCSME, eyeIndex);
				simul.addEvent(cSMEEvent[eyeIndex]);
			}
			
			// Checks diagnosis
			checkDiagnosis();
		}		
	}
	
	protected final class NonHighRiskProliferativeDREvent extends PatientEvent {
		private final int eyeIndex;
		
		public NonHighRiskProliferativeDREvent(long ts, int eyeIndex) {
			super(ts);
			this.eyeIndex = eyeIndex;
		}

		@Override
		public boolean cancel() {
			if (super.cancel()) {
				nonHRPDREvent[eyeIndex] = null;
				return true;
			}
			return false;
		}
		
		@Override
		public void event() {
			// Update VA changes before changing the state. 
			updateVA(commonParams.getVAProgression(RetalPatient.this, eyeIndex, EyeState.NON_HR_PDR), eyeIndex);

			@SuppressWarnings("unchecked")
			final EnumSet<EyeState> affectedEye = (EnumSet<EyeState>)eyes[eyeIndex];

			// Remove previous stages
			affectedEye.remove(EyeState.HEALTHY);
			affectedEye.remove(EyeState.NPDR);
			
			// Assign new stage
			affectedEye.add(EyeState.NON_HR_PDR);
			simul.notifyInfo(new PatientInfo(simul, RetalPatient.this, EyeState.NON_HR_PDR, eyeIndex, this.getTs()));
			affectedBy.add(RETALSimulation.DISEASES.DR);
			
			// schedule new events or reschedule existing ones
			final long timeToHRPDR;
			if (!affectedEye.contains(EyeState.CSME)) {
				final long timeToCSME = drParams.getTimeToCSMEAndNonHRPDRFromNonHRPDR(RetalPatient.this);
				if (timeToCSME < getTimeToEyeState(EyeState.CSME, eyeIndex)) {
					if (cSMEEvent[eyeIndex] != null) {
						cSMEEvent[eyeIndex].cancel();
					}
					cSMEEvent[eyeIndex] = new ClinicallySignificantDMEEvent(timeToCSME, eyeIndex);
					simul.addEvent(cSMEEvent[eyeIndex]);
				}
				timeToHRPDR = drParams.getTimeToHRPDRFromNonHRPDR(RetalPatient.this);
			}
			else {
				timeToHRPDR = drParams.getTimeToCSMEAndHRPDRFromCSMEAndNonHRPDR(RetalPatient.this);
			}
			// TODO: Check if it could arrive here with an already scheduled HR_DR event
			if (timeToHRPDR < Long.MAX_VALUE) {
				hRPDREvent[eyeIndex] = new HighRiskProliferativeDREvent(timeToHRPDR, eyeIndex);
				simul.addEvent(hRPDREvent[eyeIndex]);
			}										
			
			// Checks diagnosis
			checkDiagnosis();
		}		
	}
	
	protected final class HighRiskProliferativeDREvent extends PatientEvent {
		private final int eyeIndex;
		
		public HighRiskProliferativeDREvent(long ts, int eyeIndex) {
			super(ts);
			this.eyeIndex = eyeIndex;
		}

		@Override
		public boolean cancel() {
			if (super.cancel()) {
				hRPDREvent[eyeIndex] = null;
				return true;
			}
			return false;
		}
		
		@Override
		public void event() {
			// Update VA changes before changing the state. 
			updateVA(commonParams.getVAProgression(RetalPatient.this, eyeIndex, EyeState.HR_PDR), eyeIndex);

			@SuppressWarnings("unchecked")
			final EnumSet<EyeState> affectedEye = (EnumSet<EyeState>)eyes[eyeIndex];

			// Remove previous stages
			affectedEye.remove(EyeState.HEALTHY);
			affectedEye.remove(EyeState.NPDR);
			affectedEye.remove(EyeState.NON_HR_PDR);
			
			// Assign new stage
			affectedEye.add(EyeState.HR_PDR);
			simul.notifyInfo(new PatientInfo(simul, RetalPatient.this, EyeState.HR_PDR, eyeIndex, this.getTs()));
			affectedBy.add(RETALSimulation.DISEASES.DR);
			
			// schedule new events or reschedule existing ones
			if (!affectedEye.contains(EyeState.CSME)) {
				final long timeToCSME = drParams.getTimeToCSMEAndHRPDRFromHRPDR(RetalPatient.this);
				if (timeToCSME < getTimeToEyeState(EyeState.CSME, eyeIndex)) {
					if (cSMEEvent[eyeIndex] != null) {
						cSMEEvent[eyeIndex].cancel();
					}
					cSMEEvent[eyeIndex] = new ClinicallySignificantDMEEvent(timeToCSME, eyeIndex);
					simul.addEvent(cSMEEvent[eyeIndex]);
				}
			}

			// Checks diagnosis
			checkDiagnosis();
		}		
	}
	
	protected final class ClinicallySignificantDMEEvent extends PatientEvent {
		private final int eyeIndex;
		
		public ClinicallySignificantDMEEvent(long ts, int eyeIndex) {
			super(ts);
			this.eyeIndex = eyeIndex;
		}

		@Override
		public boolean cancel() {
			if (super.cancel()) {
				cSMEEvent[eyeIndex] = null;
				return true;
			}
			return false;
		}
		
		@Override
		public void event() {
			// Update VA changes before changing the state. 
			updateVA(commonParams.getVAProgression(RetalPatient.this, eyeIndex, EyeState.CSME), eyeIndex);

			@SuppressWarnings("unchecked")
			final EnumSet<EyeState> affectedEye = (EnumSet<EyeState>)eyes[eyeIndex];

			// Remove previous stages
			affectedEye.remove(EyeState.HEALTHY);
			
			// Assign new stage
			affectedEye.add(EyeState.CSME);
			simul.notifyInfo(new PatientInfo(simul, RetalPatient.this, EyeState.CSME, eyeIndex, this.getTs()));
			affectedBy.add(RETALSimulation.DISEASES.DR);
			
			// schedule new events
			// If the eye is affected by non-high risk proliferative DR
			if (affectedEye.contains(EyeState.NON_HR_PDR)) {
				final long timeToHRPDR = drParams.getTimeToCSMEAndHRPDRFromCSME(RetalPatient.this);
				if (timeToHRPDR < getTimeToEyeState(EyeState.HR_PDR, eyeIndex)) {
					if (hRPDREvent[eyeIndex] != null) {
						hRPDREvent[eyeIndex].cancel();
					}
					hRPDREvent[eyeIndex] = new HighRiskProliferativeDREvent(timeToHRPDR, eyeIndex);
					simul.addEvent(hRPDREvent[eyeIndex]);
				}
			}
			// If the eye hadn't reached PDR yet
			else if (affectedEye.contains(EyeState.NPDR)) {
				final long timeToNonHRPDR = drParams.getTimeToCSMEAndNonHRPDRFromCSME(RetalPatient.this);
				final long timeToHRPDR = drParams.getTimeToCSMEAndHRPDRFromCSME(RetalPatient.this);
				if (timeToNonHRPDR < timeToHRPDR) {
					if (timeToNonHRPDR < getTimeToEyeState(EyeState.NON_HR_PDR, eyeIndex)) {
						if (nonHRPDREvent[eyeIndex] != null) {
							nonHRPDREvent[eyeIndex].cancel();
						}
						nonHRPDREvent[eyeIndex] = new NonHighRiskProliferativeDREvent(timeToNonHRPDR, eyeIndex);
						simul.addEvent(nonHRPDREvent[eyeIndex]);
					}
				}
				else if (timeToHRPDR < Long.MAX_VALUE) {
					if (timeToHRPDR < getTimeToEyeState(EyeState.HR_PDR, eyeIndex)) {
						if (hRPDREvent[eyeIndex] != null) {
							hRPDREvent[eyeIndex].cancel();
						}
						hRPDREvent[eyeIndex] = new HighRiskProliferativeDREvent(timeToHRPDR, eyeIndex);
						simul.addEvent(hRPDREvent[eyeIndex]);
					}
				}
			}
			
			// Checks diagnosis
			checkDiagnosis();
			// If already diagnosed, the treatment with antiVEGF starts
			if (isDiagnosed()) {
				onAntiVEGFCSME[eyeIndex] = ts;
			}
		}		
	}
	
	/**
	 * The event of the death of the patient.  
	 * @author Ivan Castilla Rodriguez
	 *
	 */
	public final class DeathEvent extends PatientEvent {
		
		public DeathEvent(long ts) {
			super(ts);
		}

		@Override
		public void event() {
			death();
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

