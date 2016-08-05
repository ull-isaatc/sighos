/**
 * 
 */
package es.ull.iis.simulation.retal;

import java.util.EnumSet;
import java.util.Random;

import es.ull.iis.simulation.retal.info.PatientInfo;
import es.ull.iis.simulation.retal.params.ARMDParams;
import es.ull.iis.simulation.retal.params.CommonParams;
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
	private static final Random RNG_SENSITIVITY = new Random();
	private static final Random RNG_SPECIFICITY = new Random();
	
	// Time to events, and events
	private long [] timeToEARM = {Long.MAX_VALUE, Long.MAX_VALUE};
	private long [] timeToGA = {Long.MAX_VALUE, Long.MAX_VALUE};
	private long [] timeToCNV = {Long.MAX_VALUE, Long.MAX_VALUE};
	private EARMEvent[] eARMEvent = {null, null};
	private CNVEvent[] cNVEvent = {null, null};
	private GAEvent[] gAEvent = {null, null};
	
	/** The random reference to compare with the probability of developing CNV in the first and fellow eyes */ 
	private final double[] rndProbCNV = new double[2];
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
		
		this.rndProbCNV[0] = RNG_P_CNV[0].nextDouble();
		this.rndProbCNV[1] = RNG_P_CNV[1].nextDouble();
		this.rndSensitivity = RNG_SENSITIVITY.nextDouble();
		this.rndSpecificity = RNG_SPECIFICITY.nextDouble();
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
		
		// FIXME: Modify to implement new times to events
		this.timeToEARM = original.timeToEARM;
		this.timeToGA = original.timeToGA;
		this.timeToCNV = original.timeToCNV;
		
		this.rndProbCNV[0] = original.rndProbCNV[0];
		this.rndProbCNV[1] = original.rndProbCNV[1];
		this.rndSensitivity = original.rndSensitivity;
		this.rndSpecificity = original.rndSpecificity;
		this.commonParams = original.commonParams;
		this.armdParams = original.armdParams;
	}

	@Override
	protected void init() {
		super.init();
		timeToEARM[0] = armdParams.getTimeToEARM().getValidatedTimeToEvent(this);
		final long[] timeToAMD = armdParams.getTimeToE1AMD().getValidatedTimeToEventAndState(this);
		// Schedule an EARM event
		if (timeToEARM[0] < Long.MAX_VALUE) {
			eARMEvent[0] = new EARMEvent(timeToEARM[0], 0);
			addEvent(eARMEvent[0]);
		}
		// Schedule either a CNV or a GA event
		else if (timeToAMD != null) {
			if (timeToAMD[1] == EyeState.AMD_CNV.ordinal()) {
				timeToCNV[0] = timeToAMD[0];
				cNVEvent[0] = new CNVEvent(timeToCNV[0], 0);
				addEvent(cNVEvent[0]);				
			}
			else {
				timeToGA[0] = timeToAMD[0];
				gAEvent[0] = new GAEvent(timeToGA[0], 0);
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
	 * @return the state of the first eye
	 */
	@SuppressWarnings("unchecked")
	public EnumSet<EyeState> getEyeState(int eyeIndex) {
		return (EnumSet<EyeState>) eyes[eyeIndex];
	}

	public boolean isHealthy() {
		return eyes[0].isEmpty() && eyes[1].isEmpty();
	}
		
	public double getRndProbCNV(int eye) {
		return rndProbCNV[eye];
	}

	/**
	 * @return the timeToEARM
	 */
	public long getTimeToEARM(int eye) {
		return timeToEARM[eye];
	}

	/**
	 * @return the timeToCNV
	 */
	public long getTimeToCNV(int eye) {
		return timeToCNV[eye];
	}

	/**
	 * @return the timeToGA
	 */
	public long getTimeToGA(int eye) {
		return timeToGA[eye];
	}

	public double getAgeAt(EyeState state, int eye) {
		long ageAt = Long.MAX_VALUE;
		switch(state) {
		case EARM:
			// FIXME: Currently no EARM in fellow eye
			ageAt = timeToEARM[eye];
			break;
		case AMD_CNV:
			ageAt = timeToCNV[eye];
			break;
		case AMD_GA:
			ageAt = timeToGA[eye];
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
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, OphthalmologicPatient.this, (eyeIndex == 0)? PatientInfo.Type.EARM1 : PatientInfo.Type.EARM2, this.getTs()));
			long[] timeAndState = armdParams.getTimeToAMDFromEARM().getValidatedTimeToEvent(OphthalmologicPatient.this, eyeIndex);
			if (timeAndState != null) {
				if (EyeState.AMD_CNV.ordinal() == (int)timeAndState[1]) {
					cNVEvent[eyeIndex] = new CNVEvent(timeAndState[0], eyeIndex);
					timeToCNV[eyeIndex] = timeAndState[0];
					addEvent(cNVEvent[eyeIndex]);
				}
				else if (EyeState.AMD_GA.ordinal() == (int)timeAndState[1]) {
					gAEvent[eyeIndex] = new GAEvent(timeAndState[0], eyeIndex);
					timeToGA[eyeIndex] = timeAndState[0];
					addEvent(gAEvent[eyeIndex]);
				}
			}
			// Schedule events for fellow eye if needed
			if (eyeIndex == 0) {
				final long[] timeAndStateE2 = armdParams.getTimeToE2AMD().getValidatedTimeToEventAndState(OphthalmologicPatient.this);
				if (timeAndStateE2 != null) {
					if (EyeState.AMD_CNV.ordinal() == (int)timeAndStateE2[1]) {
						cNVEvent[1] = new CNVEvent(timeAndStateE2[0], 1);
						timeToCNV[1] = timeAndStateE2[0];
						addEvent(cNVEvent[1]);
					}
					else if (EyeState.AMD_GA.ordinal() == (int)timeAndStateE2[1]) {
						gAEvent[1] = new GAEvent(timeAndStateE2[0], 1);
						timeToGA[1] = timeAndStateE2[0];
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
				final long newTimeToCNV = armdParams.getTimeToCNVFromGA().getValidatedTimeToEvent(OphthalmologicPatient.this, 1 - eyeIndex);
				if (newTimeToCNV < timeToCNV[1 - eyeIndex]) {
					// If a CNV event was previously scheduled to happen later than the new event
					// we have to cancel it 
					if (timeToCNV[1 - eyeIndex] < Long.MAX_VALUE) {
						cNVEvent[1 - eyeIndex].cancel();
					}
					cNVEvent[1 - eyeIndex] = new CNVEvent(newTimeToCNV, 1 - eyeIndex);
					timeToCNV[1 - eyeIndex] = newTimeToCNV;
					addEvent(cNVEvent[1 - eyeIndex]);
				}
			}
			// Only the first eye could have EARM
			else if (fellowEye.contains(EyeState.EARM)) {
				// Recompute time to event
				final long[] timeAndState = armdParams.getTimeToAMDFromEARM().getValidatedTimeToEvent(OphthalmologicPatient.this, 1 - eyeIndex);
				// If a valid event appeared
				if (timeAndState != null) {
					rescheduleAMDEvent(timeAndState, 1 - eyeIndex);
				}
			}
			// Only the second eye could be healthy
			else if (fellowEye.contains(EyeState.HEALTHY)) {
				// Recompute time to event
				final long[] timeAndState = armdParams.getTimeToE2AMD().getValidatedTimeToEventAndState(OphthalmologicPatient.this);
				// If a valid event appeared
				if (timeAndState != null) {
					rescheduleAMDEvent(timeAndState, 1 - eyeIndex);
				}
			}
		}
		
		private void rescheduleAMDEvent(long[] timeAndState, int eye) {
			// If the new event happens before the already scheduled ones (in case an AMD was already schedule)
			if (timeAndState[0] < timeToCNV[eye] || timeAndState[0] < timeToGA[eye]) {
				// If a CNV event was previously scheduled to happen later than the new event
				// we have to cancel it 
				if (timeToCNV[eye] < Long.MAX_VALUE) {
					cNVEvent[eye].cancel();
				}
				// If a GA event was previously scheduled to happen later than the new event
				// we have to cancel it 
				if (timeToGA[eye] < Long.MAX_VALUE) {
					gAEvent[eye].cancel();
				}
				// Schedule the new event
				if (EyeState.AMD_CNV.ordinal() == (int)timeAndState[1]) {
					cNVEvent[eye] = new CNVEvent(timeAndState[0], eye);
					timeToCNV[eye] = timeAndState[0];
					addEvent(cNVEvent[eye]);
				}
				else if (EyeState.AMD_GA.ordinal() == (int)timeAndState[1]) {
					gAEvent[eye] = new GAEvent(timeAndState[0], eye);
					timeToGA[eye] = timeAndState[0];
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
				timeToGA[eyeIndex] = Long.MAX_VALUE;
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
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, OphthalmologicPatient.this, (eyeIndex == 0)? PatientInfo.Type.GA1 : PatientInfo.Type.GA2, this.getTs()));
			
			// Schedule a CNV event
			timeToCNV[eyeIndex] = armdParams.getTimeToCNVFromGA().getValidatedTimeToEvent(OphthalmologicPatient.this, eyeIndex);
			if (timeToCNV[eyeIndex] < Long.MAX_VALUE) {
				cNVEvent[eyeIndex] = new CNVEvent(timeToCNV[eyeIndex], eyeIndex);
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
				timeToCNV[eyeIndex] = Long.MAX_VALUE;
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
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, OphthalmologicPatient.this, (eyeIndex == 0)? PatientInfo.Type.CNV1 : PatientInfo.Type.CNV2, this.getTs()));

			// When the disease advances in an eye, the risk for the fellow eye increases
			checkFellowEye();
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
