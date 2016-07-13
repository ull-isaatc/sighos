/**
 * 
 */
package es.ull.iis.simulation.retal;

import java.util.Iterator;
import java.util.LinkedList;

import es.ull.iis.function.TimeFunction;

/**
 * @author Iván Castilla
 *
 */
public class OphthalmologicPatientCreator extends PatientCreator {
	final private CalibratedOphtalmologicPatientData[] calibratedData;
	final private LinkedList<Long> AMDQueue = new LinkedList<Long>();
	final private LinkedList<Long> EARMQueue = new LinkedList<Long>();
	private int counter = 0;

	/**
	 * @param simul
	 * @param nPatients
	 * @param pMen
	 */
	public OphthalmologicPatientCreator(RETALSimulation simul, int nPatients, double pMen, TimeFunction initialAges, CalibratedOphtalmologicPatientData[] calibratedData) {
		super(simul, nPatients, pMen, initialAges);
		if (calibratedData == null) {
			this.calibratedData = new CalibratedOphtalmologicPatientData[nPatients];
			
			for (int i = 0; i < nPatients; i++) {
				final int sex = (RNG_SEX.nextDouble() < pMen) ? 0 : 1;
				final double age = initialAges.getValue(0);
				final long timeToDeath = simul.getTimeToDeath(age, sex); 
				final long timeToAMD = getValidTimeToAMD(age, timeToDeath);
				final long timeToEARM = getValidTimeToEARM(age, timeToDeath, timeToAMD);				
				this.calibratedData[i] = new CalibratedOphtalmologicPatientData(age, sex, timeToDeath, timeToEARM, timeToAMD);
			}
		}
		else
			this.calibratedData = calibratedData;
	}

	/**
	 * @param simul
	 * @param nPatients
	 * @param pMen
	 */
	public OphthalmologicPatientCreator(RETALSimulation simul, int nPatients, double pMen, TimeFunction initialAges) {
		this(simul, nPatients, pMen, initialAges, null);
	}

	private long getValidTimeToAMD(double age, long timeToDeath) {
		long timeToAMD;
		// If there are no stored values in the queue, generate a new one
		if (AMDQueue.isEmpty()) {
			timeToAMD = simul.getTimeToAMD(age);
		}
		// If there are stored values in the queue, I try with them in the first place
		else {
			final Iterator<Long> iter = AMDQueue.iterator();
			do {
				timeToAMD = iter.next();
				if (timeToAMD < timeToDeath)
					iter.remove();
			} while (iter.hasNext() && timeToAMD >= timeToDeath);
			// If no valid event is found, generate a new one
			if (timeToAMD >= timeToDeath)
				timeToAMD = simul.getTimeToAMD(age);
		}
		// Generate new times to event until we get a valid one
		while (timeToAMD != Long.MAX_VALUE && timeToAMD >= timeToDeath) {
			AMDQueue.push(timeToAMD);
			timeToAMD = simul.getTimeToAMD(age);
		}
		return timeToAMD;
	}
	
	private long getValidTimeToEARM(double age, long timeToDeath, long timeToAMD) {
		long timeToEARM;
		
		// If we obtained a valid time to AMD, we don't need time to EARM
		if (timeToAMD < timeToDeath) {
			timeToEARM = simul.getTimeToEARM(age);
			// Generate new times to event until we get a valid one
			while (timeToEARM != Long.MAX_VALUE) {
				EARMQueue.push(timeToEARM);
				timeToEARM = simul.getTimeToEARM(age);
			}
		}
		else {
			// If there are no stored values in the queue, generate a new one
			if (EARMQueue.isEmpty()) {
				timeToEARM = simul.getTimeToEARM(age);
			}
			// If there are stored values in the queue, I try with them in the first place
			else {
				final Iterator<Long> iter = EARMQueue.iterator();
				do {
					timeToEARM = iter.next();
					if (timeToEARM < timeToDeath)
						iter.remove();
				} while (iter.hasNext() && timeToEARM >= timeToDeath);
				// If no valid event is found, generate a new one
				if (timeToEARM >= timeToDeath)
					timeToEARM = simul.getTimeToEARM(age);
			}
			// Generate new times to event until we get a valid one
			while (timeToEARM != Long.MAX_VALUE && timeToEARM >= timeToDeath) {
				EARMQueue.push(timeToEARM);
				timeToEARM = simul.getTimeToEARM(age);
			}
		}			
		return timeToEARM;
	}
	
	@Override
	protected Patient createPatient() {
		return new OphthalmologicPatient(simul, calibratedData[counter].getInitAge(), calibratedData[counter].getSex(), 
				calibratedData[counter].getTimeToDeath(), calibratedData[counter].getTimeToEARM(), calibratedData[counter++].getTimeToAMD());
	}
}
