/**
 * 
 */
package es.ull.iis.simulation.hta.interventions;

import java.util.Arrays;

import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.params.MultipleRandomSeedPerPatient;
import es.ull.iis.simulation.hta.params.RandomSeedForPatients;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.model.Describable;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ScreeningStrategy implements Named, Describable {
	public enum ScreeningResult implements Named {
		TP,
		FP,
		TN,
		FN
	}
	private final String description;
	private final String name;
	private final double sensitivity;
	private final double specificity;
	private final SecondOrderParamsRepository secParams;
	private final RandomSeedForPatients[] randomSeeds;
	
	/**
	 * 
	 */
	public ScreeningStrategy(SecondOrderParamsRepository secParams, String name, String description, double sensitivity, double specificity) {
		this.secParams = secParams;
		this.name = name;
		this.description = description;
		this.sensitivity = sensitivity;
		this.specificity = specificity;
		this.randomSeeds = new RandomSeedForPatients[secParams.getnRuns() + 1];
		Arrays.fill(randomSeeds, null);
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public String name() {
		return name;
	}
	
	public void reset(int id) {
		randomSeeds[id].reset();
	}
	
	public RandomSeedForPatients getRandomSeedForPatients(int id) {
		if (randomSeeds[id] == null) {
			randomSeeds[id] = new MultipleRandomSeedPerPatient(secParams.getnPatients(), true);
		}
		return randomSeeds[id];
	}
	
	/**
	 * @return the sensitivity
	 */
	public double getSensitivity() {
		return sensitivity;
	}

	/**
	 * @return the specificity
	 */
	public double getSpecificity() {
		return specificity;
	}
	
}
