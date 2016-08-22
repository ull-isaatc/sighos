/**
 * 
 */
package es.ull.iis.simulation.retal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Random;

import es.ull.iis.simulation.retal.info.PatientInfo;
import es.ull.iis.simulation.retal.params.ARMDParams;
import es.ull.iis.simulation.retal.params.CommonParams;
import es.ull.iis.simulation.retal.params.EyeStateAndValue;
import es.ull.iis.simulation.retal.params.VAProgressionPair;
import es.ull.iis.simulation.retal.params.CNVStage;
import es.ull.iis.simulation.retal.params.CNVStageAndValue;
import es.ull.iis.util.DiscreteCycleIterator;

/**
 * Within all the class, eye1 is always indexed as 0, while eye2 is indexed as 1  
 * @author Iván Castilla
 *
 */
public class OphthalmologicPatient extends Patient {
	public enum Disease {
		AMD,
		RD
	}

	// Random number generators for initial risks to be compared with specific probabilities
	private static final Random[] RNG_P_CNV = {new Random(), new Random()};
	private static final Random RNG_TYPE_POSITION_CNV = new Random();
	private static final Random RNG_PROG_TWICE_GA = new Random();
	private static final Random RNG_PROG_GA = new Random();
	private static final Random RNG_LEVELS_LOST_SF = new Random();
	private static final Random RNG_SENSITIVITY = new Random();
	private static final Random RNG_SPECIFICITY = new Random();
	
	// Events
	private final EARMEvent[] eARMEvent = {null, null};
	private final GAEvent[] gAEvent = {null, null};
	private final CNVEvent[] cNVEvent = {null, null};
	private final LinkedList<?>[] cNVStageEvents = {new LinkedList<CNVStageEvent>(), new LinkedList<CNVStageEvent>()};
	private final LinkedList<?>[] vaProgression = {new LinkedList<VAProgressionPair>(), new LinkedList<VAProgressionPair>()};
	/** Last timestamp when VA changed */
	private final long[] lastVAChangeTs = new long[2];
	
	/** The random reference to compare with the probability of developing CNV in the first and fellow eyes */ 
	private final double[] rndProbCNV = new double[2];
	/** The random reference to determine which is the initial type and position of the lesion in CNV */ 
	private final double rndTypeAndPositionCNV;
	/** The random reference to determine if the patient VA worsens upon GA */
	private final double rndProgGA;
	/** The random reference to whether this patient, if develops GA, gets a 6-lines instead of 3-lines penalty upon progression */
	private final double rndProgTwiceGA;
	/** The random reference to the number of levels that this patient loses whenever he/she loses VA in SF */
	private final ArrayList<Double> rndLevelsLostSF;
	/** The random reference to compare with the sensitivity */
	private final double rndSensitivity;
	/** The random reference to compare with the specificity */
	private final double rndSpecificity;
	/** Defines whether the patient is currently diagnosed */
	private boolean isDiagnosed = false;
	/** The current state of the eyes of the patient */
	private final EnumSet<?>[] eyes = new EnumSet<?>[2];
	/** The current type and position of the CNV lesions in each eye; null if no CNV */
	private final CNVStage[] currentCNVStage = new CNVStage[2];
	
	
	private final CommonParams commonParams;
	private final ARMDParams armdParams;

	/**
	 * @param simul
	 * @param initAge
	 * @param sex
	 */
	public OphthalmologicPatient(RETALSimulation simul, double initAge, int sex) {
		super(simul, initAge, sex);
		
		this.rndProbCNV[0] = RNG_P_CNV[0].nextDouble();
		this.rndProbCNV[1] = RNG_P_CNV[1].nextDouble();
		this.rndSensitivity = RNG_SENSITIVITY.nextDouble();
		this.rndSpecificity = RNG_SPECIFICITY.nextDouble();
		this.rndTypeAndPositionCNV = RNG_TYPE_POSITION_CNV.nextDouble();
		this.rndProgTwiceGA = RNG_PROG_TWICE_GA.nextDouble();
		this.rndProgGA = RNG_PROG_GA.nextDouble();
		this.rndLevelsLostSF = new ArrayList<Double>();
		this.commonParams = simul.getCommonParams();
		this.armdParams = simul.getArmdParams();
		
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
		setUtility(armdParams.getUtilityFromVA(this));
	}

	/**
	 * @param original
	 * @param nIntervention
	 */
	@SuppressWarnings("unchecked")
	public OphthalmologicPatient(OphthalmologicPatient original, int nIntervention) {
		super(original, nIntervention);
		this.eyes[0] = EnumSet.copyOf(original.eyes[0]);
		this.eyes[1] = EnumSet.copyOf(original.eyes[1]);
		((LinkedList<VAProgressionPair>)this.vaProgression[0]).addAll(original.getVaProgression(0));
		((LinkedList<VAProgressionPair>)this.vaProgression[1]).addAll(original.getVaProgression(1));
		this.currentCNVStage[0] = original.currentCNVStage[0];
		this.currentCNVStage[1] = original.currentCNVStage[1];
		
		this.rndProbCNV[0] = original.rndProbCNV[0];
		this.rndProbCNV[1] = original.rndProbCNV[1];
		this.rndTypeAndPositionCNV = original.rndTypeAndPositionCNV;
		this.rndProgTwiceGA = original.rndProgTwiceGA;
		this.rndProgGA = original.rndProgGA;
		this.rndLevelsLostSF = new ArrayList<Double>(original.rndLevelsLostSF);
		this.rndSensitivity = original.rndSensitivity;
		this.rndSpecificity = original.rndSpecificity;
		this.commonParams = original.commonParams;
		this.armdParams = original.armdParams;
	}

	@Override
	protected void init() {
		super.init();
		long timeToEvent = armdParams.getTimeToEARM(this);
		final EyeStateAndValue timeToAMD = armdParams.getTimeToE1AMD(this);
		// Schedule an EARM event
		if (timeToEvent < Long.MAX_VALUE) {
			eARMEvent[0] = new EARMEvent(timeToEvent, 0);
			addEvent(eARMEvent[0]);
		}
		// Schedule either a CNV or a GA event
		else if (timeToAMD != null) {
			if (timeToAMD.getState() == EyeState.AMD_CNV) {
				cNVEvent[0] = new CNVEvent(timeToAMD.getValue(), 0);
				addEvent(cNVEvent[0]);				
			}
			else {
				gAEvent[0] = new GAEvent(timeToAMD.getValue(), 0);
				addEvent(gAEvent[0]);				
			}
		}
		
		if (intervention instanceof Screening) {
			final DiscreteCycleIterator screeningIterator = ((Screening)intervention).getScreeningCycle().getCycle().iterator(simul.getInternalStartTs(), simul.getInternalEndTs());
			addEvent(new ScreeningEvent(screeningIterator.next(), screeningIterator));
		}
	}
	
	/**
	 * @return the isDiagnosed
	 */
	public boolean isDiagnosed() {
		return isDiagnosed;
	}

	/**
	 * Updates the state of the patient to reflect that he/she has been diagnosed
	 */
	public void setDiagnosed() {
		this.isDiagnosed = true;
	}

	/**
	 * @param eyeIndex
	 * @return the state of the first eye
	 */
	@SuppressWarnings("unchecked")
	public EnumSet<EyeState> getEyeState(int eyeIndex) {
		return (EnumSet<EyeState>) eyes[eyeIndex];
	}

	/**
	 * @param eyeIndex
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
		setUtility(armdParams.getUtilityFromVA(this));
	}

	/**
	 * @return the vaProgression
	 */
	@SuppressWarnings("unchecked")
	public LinkedList<VAProgressionPair> getVaProgression(int eyeIndex) {
		return (LinkedList<VAProgressionPair>)vaProgression[eyeIndex];
	}

	/**
	 * @param eyeIndex
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
		
	public double getRndProbCNV(int eye) {
		return rndProbCNV[eye];
	}

	public double getRndTypeAndPositionCNV() {
		return rndTypeAndPositionCNV;
	}

	/**
	 * @return the rndProgGA
	 */
	public double getRndProgGA() {
		return rndProgGA;
	}

	/**
	 * @return the rndProgTwiceGA
	 */
	public double getRndProgTwiceGA() {
		return rndProgTwiceGA;
	}

	/**
	 * @return the rndLevelsLostSF
	 */
	public double getRndLevelsLostSF() {
		final double rnd = RNG_LEVELS_LOST_SF.nextDouble();
		rndLevelsLostSF.add(rnd);
		return rnd;
	}

	/**
	 * @return the timeToEARM
	 */
	public long getTimeToEARM(int eye) {
		return (eARMEvent[eye] == null) ? Long.MAX_VALUE : eARMEvent[eye].getTs();
	}

	/**
	 * @return the timeToCNV
	 */
	public long getTimeToCNV(int eye) {
		return (cNVEvent[eye] == null) ? Long.MAX_VALUE : cNVEvent[eye].getTs();
	}

	/**
	 * @return the timeToGA
	 */
	public long getTimeToGA(int eye) {
		return (gAEvent[eye] == null) ? Long.MAX_VALUE : gAEvent[eye].getTs();
	}

	// Only for testing purposes
	public int getNCNVStageEvents(int eye) {
		return cNVStageEvents[eye].size();
	}

	/**
	 * 
	 * @param stage
	 * @param eye
	 * @return
	 */
	public long getTimeToCNVStage(CNVStage stage, int eye) {
		for (CNVStageEvent event : ((LinkedList<CNVStageEvent>)cNVStageEvents[eye])) {			
			if (stage.equals(event.getNewStage()))
				return event.getTs();
		}
		return Long.MAX_VALUE;
	}
	
	/**
	 * 
	 * @param stage
	 * @param eye
	 * @return
	 */
	public double getAgeAt(CNVStage stage, int eye) {
		for (CNVStageEvent event : ((LinkedList<CNVStageEvent>)cNVStageEvents[eye])) {			
			if (stage.equals(event.getNewStage()))
				return (initAge + event.getTs()) / 365.0;
		}
		return Double.MAX_VALUE;
	}
	
	/**
	 * 
	 * @param state
	 * @param eye
	 * @return
	 */
	public double getAgeAt(EyeState state, int eye) {
		long ageAt = Long.MAX_VALUE;
		switch(state) {
		case EARM:
			// FIXME: Currently no EARM in fellow eye
			ageAt = getTimeToEARM(eye);
			break;
		case AMD_CNV:
			ageAt = getTimeToCNV(eye);
			break;
		case AMD_GA:
			ageAt = getTimeToGA(eye);
			break;
		case CDME:
			break;
		case HEALTHY:
			ageAt = 0;
			break;
		case NCDME:
			break;
		case NPDR:
			break;
		case PDR:
			break;
		default:
			break;
		}
		if (ageAt != Long.MAX_VALUE)
			return (initAge + ageAt) / 365.0;
		return Double.MAX_VALUE;
	}
	
	@Override
	public void death() {
		super.death();
		// Updates VA changes produced since the last event
		updateVA(armdParams.getVAProgressionToDeath(this, 0), 0);
		updateVA(armdParams.getVAProgressionToDeath(this, 1), 1);
	}
	
	public final class EARMEvent extends DiscreteEvent {
		private final int eyeIndex;
		
		public EARMEvent(long ts, int eyeIndex) {
			super(ts);
			this.eyeIndex = eyeIndex;
		}

		@Override
		public void event() {
			// Update VA changes before changing the state. Theoretically, no changes should occur with healthy eyes
			updateVA(armdParams.getVAProgression(OphthalmologicPatient.this, eyeIndex, EyeState.EARM), eyeIndex);
			@SuppressWarnings("unchecked")
			final EnumSet<EyeState> affectedEye = (EnumSet<EyeState>)eyes[eyeIndex];
			affectedEye.remove(EyeState.HEALTHY);
			affectedEye.add(EyeState.EARM);
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, OphthalmologicPatient.this, PatientInfo.Type.CHANGE_EYE_STATE, eyeIndex, this.getTs()));
			EyeStateAndValue timeAndState = armdParams.getTimeToAMDFromEARM(OphthalmologicPatient.this, eyeIndex);
			if (timeAndState != null) {
				if (EyeState.AMD_CNV == timeAndState.getState()) {
					cNVEvent[eyeIndex] = new CNVEvent(timeAndState.getValue(), eyeIndex);
					addEvent(cNVEvent[eyeIndex]);
				}
				else if (EyeState.AMD_GA == timeAndState.getState()) {
					gAEvent[eyeIndex] = new GAEvent(timeAndState.getValue(), eyeIndex);
					addEvent(gAEvent[eyeIndex]);
				}
			}
			// Schedule events for fellow eye if needed
			if (eyeIndex == 0) {
				final EyeStateAndValue timeAndStateE2 = armdParams.getTimeToE2AMD(OphthalmologicPatient.this);
				if (timeAndStateE2 != null) {
					if (EyeState.AMD_CNV == timeAndStateE2.getState()) {
						cNVEvent[1] = new CNVEvent(timeAndStateE2.getValue(), 1);
						addEvent(cNVEvent[1]);
					}
					else if (EyeState.AMD_GA == timeAndStateE2.getState()) {
						gAEvent[1] = new GAEvent(timeAndStateE2.getValue(), 1);
						addEvent(gAEvent[1]);
					}
				}
			}
		}
		
	}

	public abstract class AMDEvent extends DiscreteEvent {
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
				final long newTimeToCNV = armdParams.getTimeToCNVFromGA(OphthalmologicPatient.this, 1 - eyeIndex);
				if (newTimeToCNV < getTimeToCNV(1 - eyeIndex)) {
					// If a CNV event was previously scheduled to happen later than the new event
					// we have to cancel it 
					if (cNVEvent[1 - eyeIndex] != null) {
						cNVEvent[1 - eyeIndex].cancel();
					}
					cNVEvent[1 - eyeIndex] = new CNVEvent(newTimeToCNV, 1 - eyeIndex);
					addEvent(cNVEvent[1 - eyeIndex]);
				}
			}
			// Only the first eye could have EARM
			else if (fellowEye.contains(EyeState.EARM)) {
				// Recompute time to event
				final EyeStateAndValue timeAndState = armdParams.getTimeToAMDFromEARM(OphthalmologicPatient.this, 1 - eyeIndex);
				// If a valid event appeared
				if (timeAndState != null) {
					rescheduleAMDEvent(timeAndState, 1 - eyeIndex);
				}
			}
			// Only the second eye could be healthy
			else if (fellowEye.contains(EyeState.HEALTHY)) {
				// Recompute time to event
				final EyeStateAndValue timeAndState = armdParams.getTimeToE2AMD(OphthalmologicPatient.this);
				// If a valid event appeared
				if (timeAndState != null) {
					rescheduleAMDEvent(timeAndState, 1 - eyeIndex);
				}
			}
		}
		
		private void rescheduleAMDEvent(EyeStateAndValue timeAndState, int eye) {
			// If the new event happens before the already scheduled ones (in case an AMD was already schedule)
			if (timeAndState.getValue() < getTimeToCNV(eye) || timeAndState.getValue() < getTimeToGA(eye)) {
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
					addEvent(cNVEvent[eye]);
				}
				else if (EyeState.AMD_GA == timeAndState.getState()) {
					gAEvent[eye] = new GAEvent(timeAndState.getValue(), eye);
					addEvent(gAEvent[eye]);
				}
			}
		}
	}
	
	public final class GAEvent extends AMDEvent {

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
			updateVA(armdParams.getVAProgression(OphthalmologicPatient.this, eyeIndex, EyeState.AMD_GA), eyeIndex);
			@SuppressWarnings("unchecked")
			final EnumSet<EyeState> affectedEye = (EnumSet<EyeState>)eyes[eyeIndex];
			// Remove previous stages
			affectedEye.remove(EyeState.HEALTHY);
			affectedEye.remove(EyeState.EARM);
			
			// Assign new stage
			affectedEye.add(EyeState.AMD_GA);
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, OphthalmologicPatient.this, PatientInfo.Type.CHANGE_EYE_STATE, eyeIndex, this.getTs()));
			
			// Schedule a CNV event
			final long timeToEvent = armdParams.getTimeToCNVFromGA(OphthalmologicPatient.this, eyeIndex);
			if (timeToEvent < Long.MAX_VALUE) {
				cNVEvent[eyeIndex] = new CNVEvent(timeToEvent, eyeIndex);
				addEvent(cNVEvent[eyeIndex]);
			}
			
			// When the disease advances in an eye, the risk for the fellow eye increases
			checkFellowEye();
		}			
		
	}
	
	public final class CNVEvent extends AMDEvent {

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
		
		@Override
		public void event() {
			// Advances the calculation of the incident CNV stage
			final CNVStage stage = armdParams.getInitialCNVStage(OphthalmologicPatient.this, eyeIndex);			
			// Update VA changes before changing the state. 
			updateVA(armdParams.getVAProgression(OphthalmologicPatient.this, eyeIndex, stage), eyeIndex);
			
			@SuppressWarnings("unchecked")
			final EnumSet<EyeState> affectedEye = (EnumSet<EyeState>)eyes[eyeIndex];
			// Remove previous stages
			affectedEye.remove(EyeState.HEALTHY);
			affectedEye.remove(EyeState.EARM);
			affectedEye.remove(EyeState.AMD_GA);

			// Assign new stage
			affectedEye.add(EyeState.AMD_CNV);
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, OphthalmologicPatient.this, PatientInfo.Type.CHANGE_EYE_STATE, eyeIndex, this.getTs()));
			// Assign specific CNV stage
			final CNVStageEvent newEvent = new CNVStageEvent(ts, stage, eyeIndex);
			((LinkedList<CNVStageEvent>)cNVStageEvents[eyeIndex]).add(newEvent);
			addEvent(newEvent);
			// When the disease advances in an eye, the risk for the fellow eye increases
			checkFellowEye();
		}		
	}
	
	public final class CNVStageEvent extends DiscreteEvent {
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

		@Override
		public void event() {
			// Only update VA if it's a progression in CNV stage and not setting the first CNV stage
			if (currentCNVStage[eyeIndex] != null) {
				updateVA(armdParams.getVAProgression(OphthalmologicPatient.this, eyeIndex, newStage), eyeIndex);				
			}
			currentCNVStage[eyeIndex] = newStage;
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, OphthalmologicPatient.this, PatientInfo.Type.CHANGE_CNV_STAGE, eyeIndex, this.getTs()));
			// Schedule next CNV stage
			CNVStageAndValue nextStage = armdParams.getTimeToNextCNVStage(OphthalmologicPatient.this, eyeIndex);
			if (nextStage != null) {
				final CNVStageEvent newEvent = new CNVStageEvent(nextStage.getValue(), nextStage.getStage(), eyeIndex);
				((LinkedList<CNVStageEvent>)cNVStageEvents[eyeIndex]).add(newEvent);
				addEvent(newEvent);
			}						
		}
		
	}
	public final class ScreeningEvent extends DiscreteEvent {
		private final DiscreteCycleIterator iterator;

		public ScreeningEvent(long ts, DiscreteCycleIterator screeningIterator) {
			super(ts);
			this.iterator = screeningIterator;
		}

		@Override
		public void event() {
			// Patient healthy
			if (isHealthy()) {
				// True negative
				if (rndSpecificity > ((Screening)intervention).getSpecificity()) {
					// Schedule next screening appointment (if required) 
					long next = iterator.next();
					if (next != -1) {
						addEvent(new ScreeningEvent(next, iterator));
					}
				}
				// False positive
				else {
					// TODO: Add costs of false positive						
				}
			}
			// Patient ill
			else {
				// False negative
				if (rndSensitivity > ((Screening)intervention).getSensitivity()) {
					// Schedule next screening appointment (if required) 
					long next = iterator.next();
					if (next != -1) {
						addEvent(new ScreeningEvent(next, iterator));
					}
				}
				// True positive
				else {
					setDiagnosed();
					// TODO: Add costs of true positive						
				}					
			}
		}
		
	}
	
}
