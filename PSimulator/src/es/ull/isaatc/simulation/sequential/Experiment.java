/**
 * 
 */
package es.ull.isaatc.simulation.sequential;

import es.ull.isaatc.simulation.Describable;


/**
 * Controls a set of experiments. 
 * @author Iván Castilla Rodríguez
 */
public abstract class Experiment implements Describable {
	/** A short text describing this experiment */
	protected String description = "";
	/** Number of experiments to be carried out */
	protected int nExperiments = 1;	

	/**
	 * Default constructor
	 */
	public Experiment() {		
	}
	
	/**
	 * Creates a new experiment.
	 * @param description A short text describing this experiment
	 * @param nExperiments Number of experiments to be carried out
	 */
	public Experiment(String description, int nExperiments) {
		this.description = description;
		this.nExperiments = nExperiments;
	}
	
	/**
	 * Creates a simulation corresponding to the the #ind experiment.
	 * @param ind Number of the experiment
	 * @return A new simulation object.
	 */
	public abstract Simulation getSimulation(int ind);

	/**
	 * Executes the simulations obtained when <code>getSimulation</code> is invoked.
	 */
	public abstract void start();

	/**
	 * Makes the postprocess of the experiments. The user should place here the actions that must be
	 * performed when all the experiments have finished: close files, DB access...
	 */
	protected void end() {		
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Describable#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the number of experiments to be carried out.
	 * @return The number of experiments to be carried out.
	 */
	public int getNExperiments() {
		return nExperiments;
	}

	/**
	 * Sets a short text describing this experiment
	 * @param description A short text describing this experiment
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the number of experiments to be carried out.
	 * @param experiments The number of experiments to be carried out.
	 */
	public void setNExperiments(int experiments) {
		nExperiments = experiments;
	}
}
