/**
 * 
 */
package es.ull.iis.simulation.test;

import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.TimeDrivenElementGenerator;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.ParallelFlow;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestConditionalResourceGenerator extends Experiment {
	private final static int NRT = 5;
	private final static long []DURATIONS = new long[] {5,6,7,8,9};

	class SpecialActivityFlow extends ActivityFlow {
		final private ResourceType[] rts;
		final private int specialId;
		
		public SpecialActivityFlow(Simulation model, String description, int specialId, ResourceType[] rts) {
			super(model, description, 0);
			this.specialId = specialId;
			this.rts = rts;
		}
		
		@Override
		public void afterFinalize(ElementInstance fe) {
			if (specialId < NRT - 1) {
				final Resource res = new Resource(simul, "Container " + (specialId + 1));
				res.addTimeTableEntry(rts[specialId + 1]);
				simul.addEvent(res.onCreate(simul.getTs()));
			}
		}
	}
	class TestModel extends Simulation {
		final ResourceType[] rts;
		
		public TestModel(int id) {
			super(id, "Testing conditional generation of resources " + id, TimeUnit.MINUTE, 0L, 24 * 60);
			final ElementType et = new ElementType(this, "Crane");
			rts = new ResourceType[NRT];
			final WorkGroup[] wgs = new WorkGroup[NRT];
			final ActivityFlow[] reqs = new ActivityFlow[NRT];
			final ParallelFlow pf = new ParallelFlow(this);
			for (int i = 0; i < NRT; i++) {
				rts[i] = new ResourceType(this, "Container type " + i);
				wgs[i] = new WorkGroup(this, rts[i], 1);
				reqs[i] = new SpecialActivityFlow(this, "Req " + i, i, rts);
				reqs[i].addWorkGroup(0, wgs[i], DURATIONS[i]);
				pf.link(reqs[i]);
			}
			// Only the first resource is available from the beginning
			final Resource res0 = new Resource(this, "Container " + 0);
			res0.addTimeTableEntry(rts[0]);
			
			new TimeDrivenElementGenerator(this, 1, et, pf, SimulationPeriodicCycle.newDailyCycle(getTimeUnit()));
		}
		
	}
	/**
	 * @param description
	 * @param nExperiments
	 */
	public TestConditionalResourceGenerator(int nExperiments) {
		super("Testing conditional generation of resources", nExperiments);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.model.Experiment#getModel(int)
	 */
	@Override
	public Simulation getSimulation(int ind) {
		Simulation model = new TestModel(ind);
		model.addInfoReceiver(new StdInfoView());
		return model;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TestConditionalResourceGenerator(1).start();

	}

}
