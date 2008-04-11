package es.ull.isaatc.simulation.listener.xml;

/**
 * 
 * @author Yurena García-Hevia
 *
 */
public class XMLActivityListenerProcessor extends XMLPeriodicListenerProcessor {

	private ActivityListener listener;

	/**
	 * Constructor
	 * @param Number of experiments in the simulation
	 * @param The SimulationListener
	 */
	public XMLActivityListenerProcessor(int experiments,
			SimulationListener simListener) {

		super(experiments, simListener);
		initialize((ActivityListener) simListener);
	}

	/**
	 * Initialize the listener to the first element
	 * @param The activityListener
	 */
	private void initialize(ActivityListener actList) {
		listener = new ActivityListener();

		// For each activity
		for (ActivityListener.Activity a : actList.getActivity()) {
			ActivityListener.Activity act = new ActivityListener.Activity();
			ActivityListener.Activity.ActQueue queue = new ActivityListener.Activity.ActQueue();
			ActivityListener.Activity.ActPerformed performed = new ActivityListener.Activity.ActPerformed();

			act.setActId(a.getActId());
			
			queue.getQueue().addAll(a.getActQueue().getQueue());	// Copy queue list
			act.setActQueue(queue);

			performed.getPerformed().addAll(a.getActPerformed().getPerformed());	// Copy performed list
			act.setActPerformed(performed);

			listener.getActivity().add(act);
		}
	}

	/**
	 * @return the average listener 
	 */
	@Override
	public void average() {
		// For each activity
		for (int a = 0; a < listener.getActivity().size(); a++) {
			
			ActivityListener.Activity act = listener.getActivity().get(a); // listener activity
			
			// Divides each result between the number of experiments
			for (int q = 0; q < act.getActQueue().getQueue().size(); q++) {
				act.getActQueue().getQueue().set(q, act.getActQueue().getQueue().get(q) / (double)experiments);
				act.getActPerformed().getPerformed().set(q, act.getActPerformed().getPerformed().get(q) / (double)experiments);
			}
		}
	}

	/** 
	 * Increase each value of the listener 
	 * @param the listener to be processed
	 */

	@Override
	public void process(SimulationListener simList) {
		ActivityListener list = (ActivityListener) simList;
		
		// For each activity
		for (int a = 0; a < listener.getActivity().size(); a++) {
			
			ActivityListener.Activity act = listener.getActivity().get(a); // listener activity
			ActivityListener.Activity simAct = list.getActivity().get(a); // new activity to process
			
			// Increase the queue and performed values in each period
			for (int q = 0; q < act.getActQueue().getQueue().size(); q++) {
				act.getActQueue().getQueue().set(q, act.getActQueue().getQueue().get(q) + simAct.getActQueue().getQueue().get(q));
				act.getActPerformed().getPerformed().set(q, act.getActPerformed().getPerformed().get(q) + simAct.getActPerformed().getPerformed().get(q));
			}
		}
	}

	/**
	 * @return the listener
	 */
	@Override
	public SimulationListener getListener() {
		return listener;
	}

}
