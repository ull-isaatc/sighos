/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes;

import java.util.TreeMap;

import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams.Sex;
import es.ull.iis.simulation.hta.diabetes.populations.DiabetesPopulation;

/**
 * The baseline information required to generate a patient from a {@link DiabetesPopulation}. It contains also generic 
 * fields to add properties to the patient without modifying the {@link DiabetesPatient} class itself, that may be 
 * used in new complication submodels.
 * @author icasrod
 *
 */
public class DiabetesPatientProfile {
	/** A collection of integer properties */
	private final TreeMap<String, Integer> intProperties;
	/** A collection of double properties */
	private final TreeMap<String, Double> doubleProperties;
	/** A collection of boolean properties */
	private final TreeMap<String, Boolean> boolProperties;
	/** Initial age of the patient (stored as years) */
	private final double initAge;
	/** Sex of the patient */
	private final Sex sex;
	/** Duration of diabetes at the creation of the patient (as years) */
	private final double initDurationOfDiabetes; 
	/** Initial level of HBA1c */
	private final double initHBA1c;
	/** True if the patient smokes */
	private final boolean smoker;
	/** True if the patient has atrial fibrillation */
	private final boolean atrialFib;
	/** Systolic blood presure, per 10 mm Hg */
	private final double sbp;
	/** Lipid ratio, T:H */
	private final double lipidRatio;

	/**
	 * Creates a patient profile
	 * @param initAge Age at the creation of the patient
	 * @param sex Sex of the patient
	 * @param initDurationOfDiabetes Duration of diabetes at the creation of the patient
	 * @param initHbA1c HbA1c level at the creation of the patient
	 */
	public DiabetesPatientProfile(final double initAge, final Sex sex, final double initDurationOfDiabetes, final double initHbA1c, 
			boolean smoker, boolean atrialFib, double sbp, double lipidRatio) {
		intProperties = new TreeMap<>();
		doubleProperties = new TreeMap<>();
		boolProperties = new TreeMap<>();
		this.initAge = initAge;
		this.sex = sex;
		this.initDurationOfDiabetes = initDurationOfDiabetes;
		this.initHBA1c = initHbA1c;
		this.smoker = smoker;
		this.atrialFib = atrialFib;
		this.sbp = sbp;
		this.lipidRatio = lipidRatio;
	}

	/**
	 * Returns the sex assigned to the patient (0: male; 1: female)
	 * @return The sex assigned to the patient (0: male; 1: female)
	 */
	public Sex getSex() {
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

	/**
	 * Returns true if the patient is an active smoker
	 * @return true if the patient is an active smoker; false otherwise
	 */
	public boolean isSmoker() {
		return smoker;
	}
	
	/**
	 * Returns true if the patient suffers from atrial fibrillation
	 * @return true if the patient suffers from atrial fibrillation; false otherwise
	 */
	public boolean hasAtrialFibrillation() {
		return atrialFib;
	}
	
	/**
	 * @return the sbp
	 */
	public double getSbp() {
		return sbp;
	}

	/**
	 * @return the lipidRatio
	 */
	public double getLipidRatio() {
		return lipidRatio;
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

	
	public static String getStrHeader() {
		return "AGE\tSEX\tHBA1c\tDURATION\tSMOKER\tFIB\tSBP\tLIPID";
	}
	
	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		str.append(this.getInitAge()).append("\t").append(this.getSex());
		str.append("\t").append(this.getInitHBA1c()).append("\t").append(this.getInitDurationOfDiabetes());
		str.append("\t").append(this.isSmoker()? 1:0).append("\t").append(this.hasAtrialFibrillation() ? 1:0);
		str.append("\t").append(this.getSbp()).append("\t").append(this.getLipidRatio());
		
		return str.toString();
	}
}
