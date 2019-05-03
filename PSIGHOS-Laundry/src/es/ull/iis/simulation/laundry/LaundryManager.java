package es.ull.iis.simulation.laundry;

import es.ull.iis.simulation.model.DiscreteEvent;
import es.ull.iis.simulation.model.EventSource;
import es.ull.iis.simulation.model.SimulationObject;
import es.ull.iis.simulation.model.engine.SimulationEngine;

public class LaundryManager extends SimulationObject implements EventSource {
	final static private int[][] TEST_ORDERS;
	static {
		if (LaundrySimulation.SIMPLE)
			TEST_ORDERS = new int[][] {{2,2,2,2,2}};
		else
			TEST_ORDERS = new int[][] {{10,10,10,10,10},{5,15,5,15,10},{15,5,15,5,10},{0,10,20,10,0},{15,7,6,7,15}};
	}
	private int test_counter;
	private long nextInjection;
	final static private String OBJ_ID = "MAN";
	final private BagsGenerator generator;
	final private long inspectionRate;
	final private long injectionRate;

	public LaundryManager(LaundrySimulation simul, BagsGenerator generator, long inspectionRate, long injectionRate) {
		super(simul, 0, OBJ_ID);
		this.generator = generator;
		this.inspectionRate = inspectionRate;
		this.injectionRate = injectionRate;
	}


	@Override
	public DiscreteEvent onCreate(long ts) {
		return new InspectEvent(ts);
	}

	@Override
	public DiscreteEvent onDestroy(long ts) {
		return new DiscreteEvent.DefaultFinalizeEvent(this, ts);
	}

	@Override
	public void notifyEnd() {
        simul.addEvent(onDestroy(getTs()));		
	}


	@Override
	protected void assignSimulation(SimulationEngine engine) {
		// Nothing to do
	}

	public class InspectEvent extends DiscreteEvent {

		public InspectEvent(long ts) {
			super(ts);
		}

		@Override
		public void event() {
			if (ts == nextInjection) {
				nextInjection = ts + injectionRate;
				simul.addEvent(new InjectionOrder(ts, TEST_ORDERS[test_counter]));
				test_counter = (test_counter + 1) % TEST_ORDERS.length;
			}
			simul.addEvent(new InspectEvent(ts + inspectionRate));
		}
		
	}
	public class InjectionOrder extends DiscreteEvent {
		private final int[] orders;
		
		public InjectionOrder(long ts, int[] orders) {
			super(ts);
			this.orders = orders;
		}

		@Override
		public void event() {
			System.out.println(getTs() + "\tManager injecting");
			generator.inject(orders);
		}
		
	}

}
