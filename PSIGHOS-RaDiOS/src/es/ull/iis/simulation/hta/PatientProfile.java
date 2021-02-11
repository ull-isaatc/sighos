/**
 * 
 */
package es.ull.iis.simulation.hta;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.populations.Population;
import es.ull.iis.simulation.hta.progression.Disease;

/**
 * The baseline information required to generate a patient from a {@link Population}. It contains also generic 
 * fields to add properties to the patient without modifying the {@link Patient} class itself, that may be 
 * used in new complication submodels.
 * @author icasrod
 *
 */
public class PatientProfile {
	/** A collection of integer properties */
	private final TreeMap<String, Integer> intProperties;
	/** A collection of double properties */
	private final TreeMap<String, Double> doubleProperties;
	/** A collection of boolean properties */
	private final TreeMap<String, Boolean> boolProperties;
	/** A collection of double properties */
	private final TreeMap<String, List<Double>> doubleListProperties;
	/** Initial age of the patient (stored as years) */
	private final double initAge;
	/** Sex of the patient: 0 for men, 1 for women */
	private final int sex;
	/** {@link Disease} of the patient or {@link Disease.HEALTHY} in case the patient is healthy */ 
	private final Disease disease;
	/** True if the patient is diagnosed from the start */
	private final boolean diagnosed;

	/**
	 * Creates a patient profile
	 * @param initAge Age at the creation of the patient
	 * @param sex Sex of the patient
	 * @param initDurationOfDiabetes Duration of diabetes at the creation of the patient
	 * @param initHbA1c HbA1c level at the creation of the patient
	 */
	public PatientProfile(final double initAge, final int sex, final Disease disease, final boolean diagnosed) {
		intProperties = new TreeMap<>();
		doubleProperties = new TreeMap<>();
		boolProperties = new TreeMap<>();
		doubleListProperties = new TreeMap<>();
		this.initAge = initAge;
		this.sex = sex;
		this.disease = disease;
		this.diagnosed = diagnosed;
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
	 * Returns the disease of the patient or {@link Disease.HEALTHY} in case the patient is healthy
	 * @return the disease of the patient or {@link Disease.HEALTHY} in case the patient is healthy
	 */
	public Disease getDisease() {
		return disease;
	}

	/**
	 * Returns true if the patient is diagnosed from the start 
	 * @return true if the patient is diagnosed from the start
	 */
	public boolean isDiagnosedFromStart() {
		return diagnosed;
	}

	/**
	 * Returns the value associated to the specified property
	 * @param property The name of the property
	 * @return the value associated to the specified property
	 */
	public Double getDoubleProperty(String property) {
		return doubleProperties.get(property);
	}

	/**
	 * Sets the value of the specified property
	 * @param property The name of the property
	 * @param value The new value of the property
	 */
	public void setDoubleProperty(String property, double value) {
		doubleProperties.put(property, value);
	}

	/**
	 * Returns the value associated to the specified property
	 * @param property The name of the property
	 * @return the value associated to the specified property
	 */
	public List<Double> getDoubleListProperty(String property) {
		return doubleListProperties.get(property);
	}

	/**
	 * Sets the value of the specified property
	 * @param property The name of the property
	 * @param value The new value of the property
	 */
	public void setDoubleListProperty(String property, double value) {
		List<Double> list = doubleListProperties.get(property);
		if (list == null)
			list = new ArrayList<Double>();
		list.add(value);
		doubleListProperties.put(property, list);
	}
	
	/**
	 * Returns the value associated to the specified property
	 * @param property The name of the property
	 * @return the value associated to the specified property
	 */
	public Integer getIntegerProperty(String property) {
		return intProperties.get(property);
	}
	
	/**
	 * Sets the value of the specified property
	 * @param property The name of the property
	 * @param value The new value of the property
	 */
	public void setIntegerProperty(String property, int value) {
		intProperties.put(property, value);
	}
	
	/**
	 * Returns the value associated to the specified property
	 * @param property The name of the property
	 * @return the value associated to the specified property
	 */
	public Boolean getBooleanProperty(String property) {
		return boolProperties.get(property);
	}
	
	/**
	 * Sets the value of the specified property
	 * @param property The name of the property
	 * @param value The new value of the property
	 */
	public void setBooleanProperty(String property, boolean value) {
		boolProperties.put(property, value);
	}
}
