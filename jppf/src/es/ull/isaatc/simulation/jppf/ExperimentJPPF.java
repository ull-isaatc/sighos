/**
 * 
 */
package es.ull.isaatc.simulation.jppf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jppf.client.JPPFClient;
import org.jppf.server.protocol.JPPFTask;

import es.ull.isaatc.simulation.Experiment;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.listener.SimulationListener;
import es.ull.isaatc.simulation.state.SimulationState;
import es.ull.isaatc.simulation.state.processor.*;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class ExperimentJPPF extends Experiment implements Serializable {
	/**
	 * Default constructor
	 */
	public ExperimentJPPF() {		
		super();
	}
	
	/**
	 * @param description
	 * @param nExperiments
	 * @param startTs
	 *            Timestamp of simulations' start.
	 * @param endTs
	 *            Timestamp of Simulations' end.
	 */
	public ExperimentJPPF(String description, int nExperiments) {
		super(description, nExperiments);
	}
	
	/**
	 * @param description
	 * @param nExperiments
	 * @param startTs
	 *            Timestamp of simulations' start.
	 * @param endTs
	 *            Timestamp of Simulations' end.
	 */
	public ExperimentJPPF(String description, int nExperiments, double startTs, double endTs) {
		super(description, nExperiments, startTs, endTs, new NullStateProcessor());
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
	public ExperimentJPPF(String description, int nExperiments, double startTs, double endTs, StateProcessor processor) {
		super(description, nExperiments, startTs, endTs, processor);
	}
		
	public void start() {
		/* The client which requests tasks to the server. */
		JPPFClient client = new JPPFClient();
		List<JPPFTask> tasks = new ArrayList<JPPFTask>();
		for (int i = 0; i < nExperiments; i++)
			if (previousState != null)
				tasks.add(new SimulationTask(i, previousState, endTs));
			else
				tasks.add(new SimulationTask(i, startTs, endTs));
		List<JPPFTask> results;
		try {
			System.out.println("Sending experiments");
			results = client.submit(tasks, null);
//			client.submitNonBlocking(tasks, null, null);
			System.out.println("Experiments sent");
			for (JPPFTask t : results) {
				Object result = t.getResult();
				processor.process(((SimulationResult)result).getSimState());
				System.out.println("Experiment finished");
				System.out.println(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			end();
			client.close();
			System.exit(0);		
		}
	}

	class SimulationTask extends JPPFTask {
		private static final long serialVersionUID = -8526139100047592312L;
		private double startTs;
		private double endTs;
		private int index;
		private SimulationState previousState;
		
		/**
		 * @param simul
		 * @param startTs
		 * @param endTs
		 */
		public SimulationTask(int index, double startTs, double endTs) {
			super();
			this.startTs = startTs;
			this.endTs = endTs;
			this.previousState = null;
			this.index = index;
		}

		/**
		 * @param simul
		 * @param startTs
		 * @param endTs
		 */
		public SimulationTask(int index, SimulationState previousState, double endTs) {
			super();
			this.startTs = previousState.getEndTs();
			this.endTs = endTs;
			this.previousState = previousState;
			this.index = index;
		}

		public void run() {
			Simulation sim = getSimulation(index);
			if (previousState == null)
				sim.start(startTs, endTs);
			else
				sim.start(previousState, endTs);
			SimulationResult res = new SimulationResult(sim);
			setResult(res);
		}

	}
	
	class SimulationResult implements Serializable {
		private static final long serialVersionUID = -2079818841854490210L;
		private SimulationState simState;
		private String[] listenerRes;
		
		/**
		 * @param simState
		 * @param listenerRes
		 */
		public SimulationResult(Simulation sim) {
			this.simState = sim.getState();
			this.listenerRes = new String[sim.getListeners().size()];
			int count = 0;
			for (SimulationListener listener : sim.getListeners())
				listenerRes[count++] = listener.toString();
		}

		/**
		 * @return the listenerRes
		 */
		public String[] getListenerResults() {
			return listenerRes;
		}

		/**
		 * @return the simState
		 */
		public SimulationState getSimState() {
			return simState;
		}
		
		public String toString() {
			StringBuilder str = new StringBuilder(simState.toString() + "\n");
			for (String s : listenerRes)
				str.append(s + "\n");
			return str.toString();
		}
	}
}
