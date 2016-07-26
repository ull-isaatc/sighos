/**
 * 
 */
package es.ull.iis.simulation.retal;

import es.ull.iis.simulation.retal.params.CommonParams;

/**
 * @author Iván Castilla
 *
 */
public class CalibratedOphtalmologicPatientData {
	private final double initAge;
	private final int sex;
	private final long timeToDeath;
	private final long timeToEARM;
	private final long timeToAMD;

	/**
	 * @param initAge
	 * @param sex
	 * @param timeToDeath
	 * @param timeToEARM
	 * @param timeToAMD
	 */
	public CalibratedOphtalmologicPatientData(double initAge, int sex, long timeToDeath, long timeToEARM, long timeToAMD) {
		this.initAge = initAge;
		this.sex = sex;
		this.timeToDeath = timeToDeath;
		this.timeToEARM = timeToEARM;
		this.timeToAMD = timeToAMD;
	}

	/**
	 * @return the initAge
	 */
	public double getInitAge() {
		return initAge;
	}

	/**
	 * @return the sex
	 */
	public int getSex() {
		return sex;
	}

	/**
	 * @return the timeToDeath
	 */
	public long getTimeToDeath() {
		return timeToDeath;
	}

	/**
	 * @return the timeToEARM
	 */
	public long getTimeToEARM() {
		return timeToEARM;
	}

	/**
	 * @return the timeToAMD
	 */
	public long getTimeToAMD() {
		return timeToAMD;
	}

	public boolean isCoherent() {
		if (initAge < 0 || initAge > CommonParams.MAX_AGE)
			return false;
		if (timeToDeath + initAge > CommonParams.MAX_AGE)
			return false;
		if (timeToDeath == Long.MAX_VALUE || timeToDeath < 0)
			return false;
		if ((timeToEARM != Long.MAX_VALUE && timeToEARM >= timeToDeath) || (timeToAMD != Long.MAX_VALUE && timeToAMD >= timeToDeath))
			return false;
		if (timeToEARM != Long.MAX_VALUE && timeToAMD != Long.MAX_VALUE)
			return false;
		return true;
		
	}
	@Override
	public String toString() {
		return "Age: " + initAge + " Sex: " + ((sex == 1) ? "Female (1)" : "Male (0)") + " Time_to_death: " + timeToDeath 
				+ " Time_to_EARM: " + ((timeToEARM == Long.MAX_VALUE) ? "NO" : ("" + timeToEARM))
				+ " Time_to_AMD: " + ((timeToAMD == Long.MAX_VALUE) ? "NO" : ("" + timeToAMD));
	}
}
