/**
 * 
 */
package es.ull.cyc.simulation;

import es.ull.cyc.simulation.results.*;
import es.ull.cyc.util.Output;

/**
 * Controls a set of experiments. 
 * @author Iván Castilla Rodríguez
 */
public abstract class Experiment {
	protected String description;
	protected int nExperiments;
	protected SimulationResults[] results;
	protected ResultProcessor proc;
	protected Output out;


	/**
	 * Default constructor
	 *
	 */
	public Experiment() {		

	}
	
	/**
	 * 
	 * @param proc
	 * @param out
	 */
	public Experiment(ResultProcessor proc, Output out) {
		this.proc = proc;
		this.out = out;
	}

	/**
	 * @param description
	 * @param experiments
	 * @param proc
	 * @param out
	 */
	public Experiment(String description, int experiments, ResultProcessor proc, Output out) {
		this.description = description;
		nExperiments = experiments;
		this.proc = proc;
		results = new SimulationResults[nExperiments];
		this.out = out;
	}
	
	/**
	 * @param proc
	 */
	public Experiment(ResultProcessor proc) {		
		this(proc, new Output());
	}

	/**
	 * @param description
	 * @param experiments
	 * @param proc
	 */
	public Experiment(String description, int experiments, ResultProcessor proc) {
		this(description, experiments, proc, new Output());
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
			sim.run();
			results[i] = sim.getResults();
		}
		proc.processStatistics(results);
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
		results = new SimulationResults[nExperiments];
	}

	/**
	 * @param out the out to set
	 */
	public void setOut(Output out) {
		this.out = out;
	}

	/**
	 * @param proc the proc to set
	 */
	public void setProc(ResultProcessor proc) {
		this.proc = proc;
	}
	
}
