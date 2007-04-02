package es.ull.isaatc.test;
//import java.io.OutputStreamWriter;

import simkit.random.RandomVariateFactory;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.simulation.state.SimulationState;
import es.ull.isaatc.simulation.state.StateProcessor;
import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.Output;
import es.ull.isaatc.util.PeriodicCycle;

/**
 * 
 */
class StateSimulation extends StandAloneLPSimulation {
	static final int NSIMPLE = 1;
	static final int NRT = 2;
	static final int NACT = 2;
	static final double DURACT = 25.0;
	static final int NELEMT = 2;
	static final int NRES = 2;
	static final double PERIOD = 100.0;
	static final double DURRES = 90.0;
	static final int NELEM = 15;
	int complexityDegree;
	
	StateSimulation(String desc, int complexityDegree) {
		super(desc);
		out = new Output(true);
//		super(desc, startTs, endTs, new Output(true, new OutputStreamWriter(System.out), new OutputStreamWriter(System.out)));
		this.complexityDegree = complexityDegree;
	}

	@Override
	protected void createModel() {
		Cycle cPeriod = new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", PERIOD), 0);
		switch (complexityDegree) {
			case 0:				
				for (int i = 0; i < NSIMPLE; i++)
					new ResourceType(i, this, "RT" + i);
				for (int i = 0; i < NSIMPLE; i++) {
					int wgId = new Activity(i, this, "ACT" + i).addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", DURACT));
					getActivity(i).addWorkGroupEntry(wgId, getResourceType(i), 1);					
				}
				for (int i = 0; i < NSIMPLE; i++)
					new ElementType(i, this, "ELEMT" + i);
				for (int i = 0; i < NSIMPLE; i++)
					new Resource(i, this, "RES" + i).addTimeTableEntry(cPeriod, DURRES, getResourceType(i));
				for (int i = 0; i < NSIMPLE; i++) {
					ElementCreator ec = new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", NELEM), getElementType(i), new SingleMetaFlow(i, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(i)));
					new TimeDrivenGenerator(this, ec, cPeriod);
				}
				break;
			case 1:
				for (int i = 0; i < NSIMPLE; i++)
					new ResourceType(i, this, "RT" + i);
				for (int i = 0; i < NSIMPLE; i++) {
					int wgId = new Activity(i, this, "ACT" + i).addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", DURACT));
					getActivity(i).addWorkGroupEntry(wgId, getResourceType(i), 1);
				}
				for (int i = 0; i < NSIMPLE; i++)
					new ElementType(i, this, "ELEMT" + i);
				for (int i = 0; i < NSIMPLE; i++)
					new Resource(i, this, "RES" + i).addTimeTableEntry(cPeriod, DURRES + 20, getResourceType(i));
				Cycle c4 = new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", PERIOD), 0);
				for (int i = 0; i < NSIMPLE; i++) {
					ElementCreator ec = new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", NELEM), getElementType(i), new SingleMetaFlow(i, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(i)));
					new TimeDrivenGenerator(this, ec, c4);
				}
				break;
			case 2:
				ResourceType rt = new ResourceType(0, this, "RT0");
				Activity a = new Activity(0, this, "Non presential 0", false);
				int wgId = a.addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", DURACT));
				a.addWorkGroupEntry(wgId, rt, 1); 
				ElementType et = new ElementType(0, this, "ELEMT0");
				new Resource(0, this, "RES0").addTimeTableEntry(cPeriod, DURRES, rt);
				ElementCreator ec = new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", NELEM), et, new SingleMetaFlow(0, RandomVariateFactory.getInstance("ConstantVariate", 1), a));
				new TimeDrivenGenerator(this, ec, cPeriod);
				break;
			case 3:
				ResourceType rt1 = new ResourceType(1, this, "RT1");
				WorkGroup wg = new WorkGroup(0, this, "");
				wg.add(rt1, 1);
				new Activity(0, this, "Non presential 0", false).addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", DURACT), wg); 
				new Activity(1, this, "Non presential 1", false).addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", DURACT), wg); 
				ElementType et1 = new ElementType(1, this, "ELEMT1");
				for (int i = 0; i < NRES; i++)
					new Resource(i, this, "RES" + i).addTimeTableEntry(cPeriod, DURRES, rt1);
				SimultaneousMetaFlow sim = new SimultaneousMetaFlow(0, RandomVariateFactory.getInstance("ConstantVariate", 1));
				new SingleMetaFlow(1, sim, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(0));
				new SingleMetaFlow(2, sim, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(1));
				ElementCreator ec1 = new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", NELEM), et1, sim);
				new TimeDrivenGenerator(this, ec1, cPeriod);
				break;
/*			case 4:
				for (int i = 0; i < NSIMPLE; i++)
					new ResourceType(i, this, "RT" + i);
				for (int i = 0; i < NSIMPLE; i++)
					new Activity(i, this, "ACT" + i).getNewWorkGroup(0, RandomVariateFactory.getInstance("ConstantVariate", DURACT)).add(getResourceType(i), 1);
				for (int i = 0; i < NSIMPLE; i++)
					new ElementType(i, this, "ELEMT" + i);
				for (int i = 0; i < NSIMPLE; i++)
					new Resource(i, this, "RES" + i).addTimeTableEntry(cPeriod, DURRES + 20, getResourceType(i));
				DeterministicElementGenerator gen = new DeterministicElementGenerator(this);
				SingleMetaFlow meta = new SingleMetaFlow(0, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(0));
				double t = 10.0;
				for (int i = 0; i < NELEM; i++, t+= 10.0)
					gen.addElement(getElementType(0), meta, t);
				break;				
*/			default:
				break;
		}		
	}

}

/**
 * 
 */
class StateExperiment extends Experiment {
	static final int NEXP = 6;
	static final int COMPLEXITY = 2;
	static final double SIMTIME = 100.0; 
	SimulationState prevState = null;
	
	StateExperiment() {
		super("Tests stopping and continuing simulations", NEXP, 0.0, SIMTIME);
		setProcessor(new StateProcessor() {
			public void process(SimulationState state) {
				prevState = state;
			}
		});
	}

	@Override
	public Simulation getSimulation(int ind) {
		StateSimulation sim = new StateSimulation("Simulation PART: " + ind, COMPLEXITY);
		if (ind > 0)
			sim.setState(prevState);
//		sim.addListener(new StdInfoListener());
		startTs = SIMTIME * ind;
		endTs = SIMTIME * (ind + 1);
		return sim;
	}	
}

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class TestState {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new StateExperiment().start();
	}

}
