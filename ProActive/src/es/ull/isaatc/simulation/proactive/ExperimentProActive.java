/**
 * 
 */
package es.ull.isaatc.simulation.proactive;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.node.NodeException;

import es.ull.isaatc.simulation.Experiment;
import es.ull.isaatc.simulation.Simulation;

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
	 */
	public ExperimentProActive(String simulClassName, Object [] params, String description, int nExperiments, String []nodeList) {
		super(description, nExperiments);
		this.nodeList = nodeList;
		this.simulClassName = simulClassName;
		this.params = params;
	}

	public void start() {
		for (int i = 0; i < nExperiments; i++) {
			Simulation sim = null;
			try {
				sim = (Simulation)org.objectweb.proactive.ProActive.newActive
				(simulClassName, params, nodeList[nodeCounter]);
				// FIXME: Esto es erróneo si se utiliza como thread
				System.out.println(sim.call());
			} catch (ActiveObjectCreationException e) {
				e.printStackTrace();
			} catch (NodeException e) {
				e.printStackTrace();
			} finally {
				nodeCounter = (nodeCounter + 1) % nodeList.length;
			}
		}
		end();
		ProActive.exitSuccess();
	}

	@Override
	public Simulation getSimulation(int ind) {
		return null;
	}

}