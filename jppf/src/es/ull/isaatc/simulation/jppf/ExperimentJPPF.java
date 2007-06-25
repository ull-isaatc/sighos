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
import es.ull.isaatc.simulation.state.processor.StateProcessor;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class ExperimentJPPF extends Experiment implements Serializable {
	/**
	 * Default constructor
	 */
	public ExperimentJPPF() {		
	}
	
	/**
	 * @param description
	 * @param nExperiments
	 */
	public ExperimentJPPF(String description, int nExperiments) {
		super(description, nExperiments);
	}
	
	/**
	 * @param description
	 * @param nExperiments
	 * @param processor
	 */
	public ExperimentJPPF(String description, int nExperiments, StateProcessor processor) {
		super(description, nExperiments, processor);
	}
	
	public void start() {
		/* The client which requests tasks to the server. */
		JPPFClient client = new JPPFClient();
		List<JPPFTask> tasks = new ArrayList<JPPFTask>();
		for (int i = 0; i < nExperiments; i++)
			tasks.add(new SimulationTask(i));
		List<JPPFTask> results;
		try {
			System.out.println("Sending experiments");
			results = client.submit(tasks, null);
//			client.submitNonBlocking(tasks, null, null);
			System.out.println("Experiments sent");
			for (JPPFTask t : results) {
				Object result = t.getResult();
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
		private int index;
		
		/**
		 * @param simul
		 * @param startTs
		 * @param endTs
		 */
		public SimulationTask(int index) {
			super();
			this.index = index;
		}

		public void run() {
			setResult(getSimulation(index).call());
		}

	}
}
