/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.TreeMap;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RandomForPatient;
import es.ull.iis.simulation.retal.RandomForPatient.ITEM;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CNVStageParam extends Param {
	private final static Trio[] INIT_PROBS_EYE1 = {
			new Trio(CNVStage.Type.OCCULT, CNVStage.Position.EF, 0.520936059562493),
			new Trio(CNVStage.Type.OCCULT, CNVStage.Position.JF, 0.541544609414475),
			new Trio(CNVStage.Type.OCCULT, CNVStage.Position.SF, 0.510074352673803),
			new Trio(CNVStage.Type.MC, CNVStage.Position.EF, 0.482418567086109),
			new Trio(CNVStage.Type.MC, CNVStage.Position.JF, 0.475210374866824),
			new Trio(CNVStage.Type.MC, CNVStage.Position.SF, 0.485273789163514),
			new Trio(CNVStage.Type.PC, CNVStage.Position.EF, 0.500575496058361),
			new Trio(CNVStage.Type.PC, CNVStage.Position.JF, 0.492857677272789),
			new Trio(CNVStage.Type.PC, CNVStage.Position.SF, 0.481408566406402),
		};
	
	private final static Trio[] INIT_PROBS_EYE2 = {
			new Trio(CNVStage.Type.OCCULT, CNVStage.Position.EF, 0.491623875528603),
			new Trio(CNVStage.Type.OCCULT, CNVStage.Position.JF, 0.474205100995370),
			new Trio(CNVStage.Type.OCCULT, CNVStage.Position.SF, 0.517747772945830),
			new Trio(CNVStage.Type.MC, CNVStage.Position.EF, 0.520302252395168),
			new Trio(CNVStage.Type.MC, CNVStage.Position.JF, 0.498244677247978),
			new Trio(CNVStage.Type.MC, CNVStage.Position.SF, 0.529018726980679),
			new Trio(CNVStage.Type.PC, CNVStage.Position.EF, 0.497793268682108),
			new Trio(CNVStage.Type.PC, CNVStage.Position.JF, 0.493908407114213),
			new Trio(CNVStage.Type.PC, CNVStage.Position.SF, 0.493283838856238),
		};
	
	private final WeibullTimeToEventParam timeToPCFromMC;
	private final WeibullTimeToEventParam timeToPCFromOcc;
	private final WeibullTimeToEventParam timeToMCFromOcc;
	private final WeibullTimeToEventParam timeToJFFromEF;
	private final WeibullTimeToEventParam timeToSFFromEF;
	private final WeibullTimeToEventParam timeToSFFromJF;
	
	private final TreeMap<Double, CNVStage> initProb1 = new TreeMap<Double, CNVStage>();
	private final TreeMap<Double, CNVStage> initProb2 = new TreeMap<Double, CNVStage>();
	
	/**
	 * 
	 */
	public CNVStageParam(boolean baseCase) {
		super(baseCase);
		double total = 0.0;
		for (Trio trio : INIT_PROBS_EYE1) {
			total += trio.value;
		}
		double acc = 0.0;
		for (Trio trio : INIT_PROBS_EYE1) {
			acc += trio.value;
			initProb1.put(acc / total, trio.typeAndPos);
		}
		total = 0.0;
		for (Trio trio : INIT_PROBS_EYE2) {
			total += trio.value;
		}
		acc = 0.0;
		for (Trio trio : INIT_PROBS_EYE2) {
			acc += trio.value;
			initProb2.put(acc / total, trio.typeAndPos);
		}
		// Currently initialized as they are in the Karnon model
		timeToPCFromMC = new WeibullTimeToEventParam(baseCase, TimeUnit.DAY, RandomForPatient.getRandomNumber(ITEM.TIME_TO_PC_FROM_MC), 1.809603571, 500.6219586);
		timeToPCFromOcc = new WeibullTimeToEventParam(baseCase, TimeUnit.DAY, RandomForPatient.getRandomNumber(ITEM.TIME_TO_PC_FROM_OCC), 2.653739171, 592.4042407);
		timeToMCFromOcc = new WeibullTimeToEventParam(baseCase, TimeUnit.DAY, RandomForPatient.getRandomNumber(ITEM.TIME_TO_MC_FROM_OCC), 1.999174026, 478.7268718);
		timeToJFFromEF = new WeibullTimeToEventParam(baseCase, TimeUnit.DAY, RandomForPatient.getRandomNumber(ITEM.TIME_TO_JF_FROM_EF), 2.504770922, 186.7827681);
		timeToSFFromEF = new WeibullTimeToEventParam(baseCase, TimeUnit.DAY, RandomForPatient.getRandomNumber(ITEM.TIME_TO_SF_FROM_EF), 4.568238801, 225.9139343);
		timeToSFFromJF = new WeibullTimeToEventParam(baseCase, TimeUnit.DAY, RandomForPatient.getRandomNumber(ITEM.TIME_TO_SF_FROM_JF), 1.831164809, 197.2107448);

	}
	
	public CNVStage getInitialTypeAndPosition(Patient pat, int eyeIndex) {
		if (eyeIndex == 0) { 
			final double key = initProb1.ceilingKey(pat.draw(RandomForPatient.ITEM.ARMD_TYPE_POSITION_CNV));
			return initProb1.get(key);
		}
		else {
			final double key = initProb2.ceilingKey(pat.draw(RandomForPatient.ITEM.ARMD_TYPE_POSITION_CNV));
			return initProb2.get(key);
		}
		
	}
	
	public CNVStageAndValue getValidatedTimeToEvent(Patient pat, int eyeIndex) {
		CNVStage currentStage = pat.getCurrentCNVStage(eyeIndex);
		CNVStage.Type newType = currentStage.getType();
		CNVStage.Position newPosition = currentStage.getPosition();
		// Initialize timeToEvet to timeToDeath, so I only make changes if the new event happen before death
		long timeToEvent = pat.getTimeToDeath();
		// Check change in lesion type
		if (currentStage.getType() == CNVStage.Type.OCCULT) {
			final long timeToPC = timeToPCFromOcc.getTimeToEvent(pat);
			final long timeToMC = timeToMCFromOcc.getTimeToEvent(pat);
			if (timeToPC < timeToEvent || timeToMC < timeToEvent) {
				if (timeToPC <= timeToMC) {
					timeToEvent = timeToPC;
					newType = CNVStage.Type.PC;
				}
				else {
					timeToEvent = timeToMC;
					newType = CNVStage.Type.MC;
				}
			}
		}
		else if (currentStage.getType() == CNVStage.Type.MC) {
			final long timeToPC = timeToPCFromMC.getTimeToEvent(pat);			
			if (timeToPC < timeToEvent) {
				timeToEvent = timeToPC;
				newType = CNVStage.Type.PC;
			}
		}
		// Check change in lesion position
		if (currentStage.getPosition() == CNVStage.Position.EF) {
			final long timeToJF = timeToJFFromEF.getTimeToEvent(pat);
			final long timeToSF = timeToSFFromEF.getTimeToEvent(pat);
			if (timeToJF < timeToEvent || timeToSF < timeToEvent) {
				newType = currentStage.getType();
				if (timeToSF <= timeToJF) {
					timeToEvent = timeToSF;
					newPosition = CNVStage.Position.SF;
				}
				else {
					timeToEvent = timeToJF;
					newPosition = CNVStage.Position.JF;						
				}
			}
		}
		else if (currentStage.getPosition() == CNVStage.Position.JF) {
			final long timeToSF = timeToSFFromJF.getTimeToEvent(pat);
			if (timeToSF < timeToEvent) {
				newType = currentStage.getType();
				timeToEvent = timeToSF;
				newPosition = CNVStage.Position.SF;
			}
		}
		// Only if a new event that happens before death has been computed
		if (timeToEvent < pat.getTimeToDeath())
			return new CNVStageAndValue(newType, newPosition, timeToEvent);
		return null;
	}

	private final static class Trio {
		public final CNVStage typeAndPos;
		public double value;
		
		public Trio(CNVStage.Type type, CNVStage.Position position, double value) {
			this.typeAndPos = new CNVStage(type, position);
			this.value = value;
		}
	}
}
