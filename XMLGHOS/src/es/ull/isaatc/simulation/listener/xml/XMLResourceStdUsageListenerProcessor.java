package es.ull.isaatc.simulation.listener.xml;


/**
 * 
 * @author Yurena García-Hevia
 *
 */
public class XMLResourceStdUsageListenerProcessor extends XMLPeriodicListenerProcessor {

	private ResourceStdUsageListener listener;

	/**
	 * Constructor
	 * @param Number of experiments in the simulation
	 * @param The SimulationListener
	 */
	public XMLResourceStdUsageListenerProcessor(int experiments,
			SimulationListener simListener) {

		super(experiments, simListener);
		initialize((ResourceStdUsageListener) simListener);
	}

	/**
	 * Initialize the listener to the first element
	 * @param The activityListener
	 */
	private void initialize(ResourceStdUsageListener actList) {
		listener = new ResourceStdUsageListener();

		// For each resource
		for (ResourceStdUsageListener.Resource r : actList.getResource()) {
			ResourceStdUsageListener.Resource resource = new ResourceStdUsageListener.Resource();
			ResourceStdUsageListener.Resource.Usage usage = new ResourceStdUsageListener.Resource.Usage();
			ResourceStdUsageListener.Resource.Available available = new ResourceStdUsageListener.Resource.Available();

			resource.setId(r.getId());
			
			// Sets the usage by resource type
			for (ResourceStdUsageListener.Resource.Usage.Rt rt : r.getUsage().getRt()) { // For each resource type
				ResourceStdUsageListener.Resource.Usage.Rt rtUsage = new ResourceStdUsageListener.Resource.Usage.Rt(); 
				rtUsage.setId(rt.getId());
				rtUsage.getValue().addAll(rt.getValue());
				usage.getRt().add(rtUsage);
			}
			resource.setUsage(usage);
			
			// Sets the availability by resource type
			for (ResourceStdUsageListener.Resource.Available.Rt rt : r.getAvailable().getRt()) {
				ResourceStdUsageListener.Resource.Available.Rt rtAvailable = new ResourceStdUsageListener.Resource.Available.Rt();
				rtAvailable.setId(rt.getId());
				rtAvailable.getValue().addAll(rt.getValue());
				available.getRt().add(rtAvailable);
			}
			resource.setAvailable(available);
			
			listener.getResource().add(resource);
		}
	}

	/**
	 * @return the average listener 
	 */
	@Override
	public void average() {
		// for each resource
		for (int r = 0; r < listener.getResource().size(); r ++) {
			ResourceStdUsageListener.Resource resource = listener.getResource().get(r);
			
			// for each resource type in usage
			for (int rt = 0; rt < resource.getUsage().getRt().size(); rt++) {
				// for each period
				for (int i = 0; i < resource.getUsage().getRt().get(rt).getValue().size(); i++) {
					resource.getUsage().getRt().get(rt).getValue().set(i, resource.getUsage().getRt().get(rt).getValue().get(i) / (double)experiments);
				}
			}
			
			// for each resource type in availability
			for (int rt = 0; rt < resource.getAvailable().getRt().size(); rt++) {
				// for each period
				for (int i = 0; i < resource.getAvailable().getRt().get(rt).getValue().size(); i++) {
					resource.getAvailable().getRt().get(rt).getValue().set(i, resource.getAvailable().getRt().get(rt).getValue().get(i) / (double)experiments);
				}
			}
		}
	}

	/** 
	 * Increase each value of the listener 
	 * @param the listener to be processed
	 */
	@Override
	public void process(SimulationListener simList) {
		ResourceStdUsageListener list = (ResourceStdUsageListener) simList;
		
		// for each resource
		for (int r = 0; r < list.getResource().size(); r ++) {
			ResourceStdUsageListener.Resource resource = listener.getResource().get(r);
			ResourceStdUsageListener.Resource simRes = list.getResource().get(r);
			
			// for each resource type in usage
			for (int rt = 0; rt < simRes.getUsage().getRt().size(); rt++) {
				// for each period
				for (int i = 0; i < simRes.getUsage().getRt().get(rt).getValue().size(); i++) {
					resource.getUsage().getRt().get(rt).getValue().set(i, resource.getUsage().getRt().get(rt).getValue().get(i) + simRes.getUsage().getRt().get(rt).getValue().get(i));
				}
			}
			// for each resource type in availability
			for (int rt = 0; rt < simRes.getAvailable().getRt().size(); rt++) {
				// for each period
				for (int i = 0; i < simRes.getAvailable().getRt().get(rt).getValue().size(); i++) {
					resource.getAvailable().getRt().get(rt).getValue().set(i, resource.getAvailable().getRt().get(rt).getValue().get(i) + simRes.getAvailable().getRt().get(rt).getValue().get(i));
				}
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
