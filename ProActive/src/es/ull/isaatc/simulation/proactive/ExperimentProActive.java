/**
 * 
 */
package es.ull.isaatc.simulation.proactive;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.core.node.NodeException;

import es.ull.isaatc.simulation.Experiment;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.state.processor.StateProcessor;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ExperimentProActive extends Experiment {
	String []nodeList;
	private int nodeCounter = 0;
	private String simulClassName;
	private Object [] params;
	
	/**
	 * 
	 */
	public ExperimentProActive(String simulClassName, Object [] params, String []nodeList) {
		super();
		this.nodeList = nodeList;
		this.simulClassName = simulClassName;
		this.params = params;
	}

	/**
	 * @param description
	 * @param nExperiments
	 * @param startTs
	 * @param endTs
	 * @param processor
	 */
	public ExperimentProActive(String simulClassName, Object [] params, String description, int nExperiments, double startTs, double endTs, StateProcessor processor, String []nodeList) {
		super(description, nExperiments, startTs, endTs, processor);
		this.nodeList = nodeList;
		this.simulClassName = simulClassName;
		this.params = params;
	}

	/**
	 * @param description
	 * @param nExperiments
	 * @param startTs
	 * @param endTs
	 */
	public ExperimentProActive(String simulClassName, Object [] params, String description, int nExperiments, double startTs, double endTs, String []nodeList) {
		super(description, nExperiments, startTs, endTs);
		this.nodeList = nodeList;
		this.simulClassName = simulClassName;
		this.params = params;
	}

	/**
	 * @param description
	 * @param nExperiments
	 */
	public ExperimentProActive(String simulClassName, Object [] params, String description, int nExperiments, String []nodeList) {
		super(description, nExperiments);
		this.nodeList = nodeList;
		this.simulClassName = simulClassName;
		this.params = params;
	}

	public void start() {
		for (int i = 0; i < nExperiments; i++) {
			SimulationProActive sim = null;
			try {
				sim = (SimulationProActive)org.objectweb.proactive.ProActive.newActive
				(simulClassName, null, params, nodeList[nodeCounter]);
				if (previousState == null)
					sim.start(startTs, endTs);
				else
					sim.start(previousState, endTs);
				processor.process(sim.getState());
				System.out.println(sim.getListenerResults());
			} catch (ActiveObjectCreationException e) {
				e.printStackTrace();
			} catch (NodeException e) {
				e.printStackTrace();
			} finally {
				nodeCounter = (nodeCounter + 1) % nodeList.length;
			}
		}
		end();
	}

	@Override
	public Simulation getSimulation(int ind) {
		return null;
	}

}