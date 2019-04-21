/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes;

import java.util.TreeMap;

/**
 * @author icasrod
 *
 */
public class DiabetesPatientProfile {
	private final TreeMap<String, Integer> intProperties;
	private final TreeMap<String, Double> doubleProperties;
	private final TreeMap<String, Boolean> boolProperties;
	/** Initial age of the patient (stored as years) */
	private final double initAge;
	/** Sex of the patient: 0 for men, 1 for women */
	private final int sex;
	/** Duration of diabetes at the creation of the patient (as years) */
	private final double initDurationOfDiabetes; 
	/** Initial level of HBA1c */
	private final double initHBA1c;

	/**
	 * 
	 */
	public DiabetesPatientProfile(final double initAge, final int sex, final double initDurationOfDiabetes, final double initHbA1c) {
		intProperties = new TreeMap<>();
		doubleProperties = new TreeMap<>();
		boolProperties = new TreeMap<>();
		this.initAge = initAge;
		this.sex = sex;
		this.initDurationOfDiabetes = initDurationOfDiabetes;
		this.initHBA1c = initHbA1c;
	}

	/**
	 * Returns the sex assigned to the patient (0: male; 1: female)
	 * @return The sex assigned to the patient (0: male; 1: female)
	 */
	public int getSex() {
		return sex;
	}

	/**
	 * Returns the initial age (in years) assigned to the patient
	 * @return The initial age (in years) assigned to the patient
	 */
	public double getInitAge() {
		return initAge;
	}
	
	/**
	 * Returns the duration of the diabetes at the creation of the patient
	 * @return the duration of the diabetes at the creation of the patient
	 */
	public double getInitDurationOfDiabetes() {
		return initDurationOfDiabetes;
	}
	
	/**
	 * Returns the HbA1c level initially assigned to the patient
	 * @return The HbA1c level initially assigned to the patient
	 */
	public double getInitHBA1c() {
		return initHBA1c;
	}

	public Double getDoubleProperty(String property) {
		return doubleProperties.get(property);
	}
	
	public void setDoubleProperty(String property, double value) {
		doubleProperties.put(property, value);
	}
	
	public Integer getIntegerProperty(String property) {
		return intProperties.get(property);
	}
	
	public void setIntegerProperty(String property, int value) {
		intProperties.put(property, value);
	}
	
	public Boolean getBooleanProperty(String property) {
		return boolProperties.get(property);
	}
	
	public void setBooleanProperty(String property, boolean value) {
		boolProperties.put(property, value);
	}
}
