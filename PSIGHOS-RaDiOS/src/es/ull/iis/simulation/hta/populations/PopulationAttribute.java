package es.ull.iis.simulation.hta.populations;

import java.util.TreeSet;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;

/**
 * Any extra clinical parameter or characteristic of the patient
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class PopulationAttribute implements Named {
	private final static TreeSet<String> usedNames = new TreeSet<>();
	private final String name;
	
	/**
	 * Creates an attribute with unique name
	 * @param name
	 * @throws MalformedSimulationModelException 
	 */
	public PopulationAttribute(String name) throws MalformedSimulationModelException {
		if (usedNames.contains(name))
			throw new MalformedSimulationModelException("Name " + name + " for clinical parameter already used");
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}
	
	/**
	 * Returns the initial value of an attribute for a specific patient 
	 * @param pat A patient, with baseline characteristics
	 * @param simul The current simulation
	 * @return The initial value of an attribute for a specific patient
	 */
	public abstract double getInitialValue(Patient pat, DiseaseProgressionSimulation simul);	
}