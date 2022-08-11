/**
 * 
 */
package es.ull.iis.simulation.hta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.params.Modification;
import es.ull.iis.simulation.hta.populations.Population;
import es.ull.iis.simulation.hta.progression.Disease;

/**
 * The baseline information required to generate a patient from a {@link Population}. It contains also generic 
 * fields to add properties to the patient without modifying the {@link Patient} class itself, that may be 
 * used in new manifestations or diseases.
 * @author Iván Castilla Rodríguez
 *
 */
public class PatientProfile {
	/** A collection of properties */
	private final TreeMap<String, Number> properties;
	/** A collection of lists of numeric properties */
	private final TreeMap<String, List<Number>> listProperties;
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
		properties = new TreeMap<>();
		listProperties = new TreeMap<>();
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
	public List<Number> getListProperty(String property) {
		return listProperties.get(property);
	}

	/**
	 * Adds an element to a listed property
	 * @param property The name of the property
	 * @param value The new element of the list
	 * @return This profile, so you can concatenate calls to this method 
	 */
	public PatientProfile addElementToListProperty(String property, Number value) {
		List<Number> list = listProperties.get(property);
		if (list == null)
			list = new ArrayList<Number>();
		list.add(value);
		listProperties.put(property, list);
		return this;
	}

	/**
	 * Adds an element to a listed property. Replaces any existing list with the same name 
	 * @param property The name of the property
	 * @param value The new element of the list
	 * @return This profile, so you can concatenate calls to this method 
	 */
	public PatientProfile addListProperty(String property, List<Number> list) {
		listProperties.put(property, list);
		return this;
	}

	/**
	 * Returns the value associated to the specified property modified depending on the patient
	 * @param property The name of the property
	 * @return the value associated to the specified property
	 */
	public Number getPropertyValue(String property, Patient pat) {
		final int id = pat.getSimulation().getIdentifier();
		final Modification modif = pat.getSimulation().getIntervention().getClinicalParameterModification(property);
		double value = properties.get(property).doubleValue(); 
		switch(modif.getType()) {
		case DIFF:
			value -= modif.getValue(id);
			break;
		case RR:
			value *= modif.getValue(id);
			break;
		case SET:
			value = modif.getValue(id);
			break;
		default:
			break;
		}
		return value;
	}

	/**
	 * Sets the value of the specified property. Replaces the property if already exists.
	 * @param property The name of the property
	 * @param value The new value of the property
	 * @return This profile, so you can concatenate calls to this method 
	 */
	public PatientProfile addProperty(String property, Number value) {
		properties.put(property, value);
		return this;
	}

	/**
	 * Returns the list of properties 	
	 * @return
	 */
	public Collection<String> getPropertyNames() {
		return properties.keySet();
	}

	public Collection<String> getListPropertyNames() {
		return listProperties.keySet();
	}

}
