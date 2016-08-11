/**
 * 
 */
package es.ull.iis.simulation.retal;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Random;

import es.ull.iis.simulation.retal.info.PatientInfo;
import es.ull.iis.simulation.retal.params.ARMDParams;
import es.ull.iis.simulation.retal.params.CommonParams;
import es.ull.iis.simulation.retal.params.EyeStateAndValue;
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
	private static final Random RNG_SENSITIVITY = new Random();
	private static final Random RNG_SPECIFICITY = new Random();
	
	// Events
	private final EARMEvent[] eARMEvent = {null, null};
	private final GAEvent[] gAEvent = {null, null};
	private final CNVEvent[] cNVEvent = {null, null};
	private final LinkedList<?>[] cNVStageEvents = {new LinkedList<CNVStageEvent>(), new LinkedList<CNVStageEvent>()};	
	
	/** The random reference to compare with the probability of developing CNV in the first and fellow eyes */ 
	private final double[] rndProbCNV = new double[2];
	/** The random reference to determine which is the initial type and position of the lesion in CNV */ 
	private final double rndTypeAndPositionCNV;
	/** The random reference to compare with the sensitivity */
	private final double rndSensitivity;
	/** The random reference to compare with the specificity */
	private final double rndSpecificity;
	/** Current visual acuity, measured as logMAR units: 0 is the best possible vision*/
//	private double va;
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
		eyes[0] = EnumSet.of(EyeState.HEALTHY);
		eyes[1] = EnumSet.of(EyeState.HEALTHY);
		currentCNVStage[0] = null;
		currentCNVStage[1] = null;
		
		this.rndProbCNV[0] = RNG_P_CNV[0].nextDouble();
		this.rndProbCNV[1] = RNG_P_CNV[1].nextDouble();
		this.rndSensitivity = RNG_SENSITIVITY.nextDouble();
		this.rndSpecificity = RNG_SPECIFICITY.nextDouble();
		this.rndTypeAndPositionCNV = RNG_TYPE_POSITION_CNV.nextDouble();
		this.commonParams = simul.getCommonParams();
		this.armdParams = simul.getArmdParams();
	}

	/**
	 * @param original
	 * @param nIntervention
	 */
	public OphthalmologicPatient(OphthalmologicPatient original, int nIntervention) {
		super(original, nIntervention);
		this.eyes[0] = EnumSet.copyOf(original.eyes[0]);
		this.eyes[1] = EnumSet.copyOf(original.eyes[1]);
		this.currentCNVStage[0] = original.currentCNVStage[0];
		this.currentCNVStage[1] = original.currentCNVStage[1];
		
		this.rndProbCNV[0] = original.rndProbCNV[0];
		this.rndProbCNV[1] = original.rndProbCNV[1];
		this.rndTypeAndPositionCNV = original.rndTypeAndPositionCNV;
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

	public boolean isHealthy() {
		return eyes[0].isEmpty() && eyes[1].isEmpty();
	}
		
	public double getRndProbCNV(int eye) {
		return rndProbCNV[eye];
	}

	public double getRndTypeAndPositionCNV() {
		return rndTypeAndPositionCNV;
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
	
	/**
	 * Computes the cost associated to the current state between initAge and endAge
	 * @param initAge Age at which the patient starts using the resources
	 * @param endAge Age at which the patient ends using the resources 
	 * @return The accumulated cost during the defined period
	 */
	@SuppressWarnings("unchecked")
	@Override
	public double computeCost(double initAge, double endAge) {
		double cost = 0.0;
		for(EyeState stage : (EnumSet<EyeState>)eyes[0]) {
			// FIXME: Check if res == null
			final ResourceUsageItem[] res = OphthalmologicResourceUsage.getResourceUsageItems(stage);
			if (res != null) {
				for (ResourceUsageItem usage : res) {
					cost += usage.computeCost(initAge, endAge);
				}
			}
		}
		for(EyeState stage :(EnumSet<EyeState>) eyes[1]) {
			final ResourceUsageItem[] res = OphthalmologicResourceUsage.getResourceUsageItems(stage);
			if (res != null) {
				for (ResourceUsageItem usage : res) {
					cost += usage.computeCost(initAge, endAge);
				}
			}
		}
		return cost;
	}
	
	public final class EARMEvent extends DiscreteEvent {
		private final int eyeIndex;
		
		public EARMEvent(long ts, int eyeIndex) {
			super(ts);
			this.eyeIndex = eyeIndex;
		}

		@Override
		public void event() {
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
			final CNVStage stage = armdParams.getInitialCNVStage(OphthalmologicPatient.this, eyeIndex);			
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
