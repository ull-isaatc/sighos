//import java.io.OutputStreamWriter;

import es.ull.isaatc.random.Fixed;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.Output;

/**
 * 
 */
class StateSimulation extends Simulation {
	static final int NSIMPLE = 1;
	static final int NRT = 2;
	static final int NACT = 2;
	static final double DURACT = 25.0;
	static final int NELEMT = 2;
	static final int NRES = 2;
	static final double PERIOD = 100.0;
	static final double DURRES = 90.0;
	static final int NELEM = 5;
	int complexityDegree;
	
	StateSimulation(String desc, double startTs, double endTs, int complexityDegree) {
		super(desc, startTs, endTs, new Output(Output.DebugLevel.NODEBUG));
		this.complexityDegree = complexityDegree;
	}

	@Override
	protected void createModel() {
		Cycle cPeriod = new Cycle(0.0, new Fixed(PERIOD), 0);
		switch (complexityDegree) {
			case 0:				
				for (int i = 0; i < NSIMPLE; i++)
					new ResourceType(i, this, "RT" + i);
				for (int i = 0; i < NSIMPLE; i++)
					new Activity(i, this, "ACT" + i).getNewWorkGroup(0, new Fixed(DURACT)).add(getResourceType(i), 1);
				for (int i = 0; i < NSIMPLE; i++)
					new ElementType(i, this, "ELEMT" + i);
				for (int i = 0; i < NSIMPLE; i++)
					new Resource(i, this, "RES" + i).addTimeTableEntry(cPeriod, DURRES, getResourceType(i));
				for (int i = 0; i < NSIMPLE; i++)
					new ElementGenerator(this, new Fixed(NELEM), cPeriod.iterator(startTs, endTs), getElementType(i), new SingleMetaFlow(i, new Fixed(1), getActivity(i)));
				break;
			case 1:
				for (int i = 0; i < NSIMPLE; i++)
					new ResourceType(i, this, "RT" + i);
				for (int i = 0; i < NSIMPLE; i++)
					new Activity(i, this, "ACT" + i).getNewWorkGroup(0, new Fixed(DURACT)).add(getResourceType(i), 1);
				for (int i = 0; i < NSIMPLE; i++)
					new ElementType(i, this, "ELEMT" + i);
				for (int i = 0; i < NSIMPLE; i++)
					new Resource(i, this, "RES" + i).addTimeTableEntry(cPeriod, DURRES + 20, getResourceType(i));
				Cycle c4 = new Cycle(0.0, new Fixed(PERIOD), 0);
				for (int i = 0; i < NSIMPLE; i++)
					new ElementGenerator(this, new Fixed(NELEM), c4.iterator(startTs, endTs), getElementType(i), new SingleMetaFlow(i, new Fixed(1), getActivity(i)));
				break;
			case 2:
				ResourceType rt = new ResourceType(0, this, "RT0");
				Activity a = new Activity(0, this, "Non presential 1", 0, false);
				a.getNewWorkGroup(0, new Fixed(DURACT)).add(rt, 1); 
				ElementType et = new ElementType(0, this, "ELEMT0");
				new Resource(0, this, "RES0").addTimeTableEntry(cPeriod, DURRES, rt);
				new ElementGenerator(this, new Fixed(NELEM), cPeriod.iterator(startTs, endTs), et, new SingleMetaFlow(0, new Fixed(1), a));
			default:
				break;
		}		
	}

}

/**
 * 
 */
class StateExperiment extends Experiment {
	static final int NEXP = 2;
	static final int COMPLEXITY = 2;
	static final double simTime = 100.0; 
	
	StateExperiment() {
		super("Tests stopping and continuing simulations", NEXP);
		processor = new XMLStateProcessor();
	}

	@Override
	public Simulation getSimulation(int ind) {
		StateSimulation sim = new StateSimulation("Simulation PART: " + ind, simTime * ind, simTime * (ind + 1), COMPLEXITY);
		return sim;
	}	

//	public void start() {
//		Simulation prevSim = new StateSimulation("Simulation INITIAL", 0.0, simTime, COMPLEXITY);
//		prevSim.start();
//		System.out.println("---------------------------------------------------------------");
//		for (int i = 1; i < nExperiments; i++) {
//			Simulation sim = getSimulation(i);
//			sim.start(prevSim.getState());
//			prevSim = sim;
//			System.out.println("---------------------------------------------------------------");
//		}
//	}
}

/**
 * @author Iván Castilla Rodríguez
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
