/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.simulation.state.*;

/**
 * Controls a set of experiments. 
 * @author Iván Castilla Rodríguez
 */
public abstract class Experiment {
	protected String description;
	protected int nExperiments;
	protected StateProcessor processor;


	/**
	 * Default constructor
	 *
	 */
	public Experiment() {		
	}
	
	/**
	 * @param description
	 * @param experiments
	 */
	public Experiment(String description, int experiments) {
		this(description, experiments, new NullStateProcessor());
	}
	
	/**
	 * @param description
	 * @param experiments
	 * @param processor
	 */
	public Experiment(String description, int experiments, StateProcessor processor) {
		this.description = description;
		nExperiments = experiments;
		this.processor = processor;
	}
	
	/**
	 * Creates a simulation corresponding to the the #ind experiment.
	 * @param ind Number of the experiment
	 * @return A new simulation object.
	 */
	public abstract Simulation getSimulation(int ind);
	
	public void start() {
		for (int i = 0; i < nExperiments; i++) {
			Simulation sim = getSimulation(i);
			sim.start();
			processor.process((SimulationState)sim.getState());
		}
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return Returns the nExperiments.
	 */
	public int getNExperiments() {
		return nExperiments;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param experiments The nExperiments to set.
	 */
	public void setNExperiments(int experiments) {
		nExperiments = experiments;
	}

	/**
	 * @param processor The processor to set.
	 */
	public void setProcessor(StateProcessor processor) {
		this.processor = processor;
	}

}
