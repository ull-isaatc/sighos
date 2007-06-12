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
	
	/** Experiment's description */
	protected String description;
	
	/** Number of experiments to carry out */
	protected int nExperiments;
	
	/** The class which process the states of each simulation */
	protected StateProcessor processor;

	/** A state previously stored */
	protected SimulationState previousState = null;

	/** Timestamp of simulations' start */
	protected double startTs;

	/** Timestamp of Simulations' end */
	protected double endTs;


	/**
	 * Default constructor
	 */
	public Experiment() {		
	}
	
	/**
	 * @param description
	 * @param nExperiments
	 * @param startTs
	 *            Timestamp of simulations' start.
	 * @param endTs
	 *            Timestamp of Simulations' end.
	 */
	public Experiment(String description, int nExperiments) {
		this.description = description;
		this.nExperiments = nExperiments;
		this.processor = new NullStateProcessor();
	}
	
	/**
	 * @param description
	 * @param nExperiments
	 * @param startTs
	 *            Timestamp of simulations' start.
	 * @param endTs
	 *            Timestamp of Simulations' end.
	 */
	public Experiment(String description, int nExperiments, double startTs, double endTs) {
		this(description, nExperiments, startTs, endTs, new NullStateProcessor());
	}
	
	/**
	 * @param description
	 * @param nExperiments
	 * @param startTs
	 *            Timestamp of simulations' start.
	 * @param endTs
	 *            Timestamp of Simulations' end.
	 * @param processor
	 */
	public Experiment(String description, int nExperiments, double startTs, double endTs, StateProcessor processor) {
		this.description = description;
		this.nExperiments = nExperiments;
		this.processor = processor;
		this.startTs = startTs;
		this.endTs = endTs;
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
			if (previousState != null)
				sim.start(previousState, endTs);
			else
				sim.start(startTs, endTs);
			processor.process(sim.getState());
		}
		end();
	}

	/**
	 * Makes the postprocess of the experiments. The user should place here the actions that must be
	 * performed when all the experiments have finished: close files, DB access...
	 */
	protected void end() {		
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
	 * @return the endTs
	 */
	public double getEndTs() {
		return endTs;
	}

	/**
	 * @return the startTs
	 */
	public double getStartTs() {
		return startTs;
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
	 * @param endTs the endTs to set
	 */
	public void setEndTs(double endTs) {
		this.endTs = endTs;
	}

	/**
	 * @param startTs the startTs to set
	 */
	public void setStartTs(double startTs) {
		this.startTs = startTs;
	}

	/**
	 * @param processor The processor to set.
	 */
	public void setProcessor(StateProcessor processor) {
		this.processor = processor;
	}

	/**
	 * @param previousState The previousState to set.
	 */
	public void setPreviousState(SimulationState previousState) {
		this.previousState = previousState;
	}

}
