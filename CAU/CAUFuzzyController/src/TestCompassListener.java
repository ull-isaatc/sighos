

import es.ull.isaatc.simulation.Generator;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.info.CompassListener;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;
import es.ull.isaatc.simulation.info.TimeChangeInfo;
import es.ull.isaatc.util.CycleIterator;

/**
 * 
 * @author Roberto Muñoz
 */
public class TestCompassListener extends CompassListener {

	public TestCompassListener(CycleIterator cycleIterator) {
		super(cycleIterator);
	}

	@Override
	public void takeSample(Generator gen) {
		System.out.println("TS :\t" + gen.getTs());
		
	}

	@Override
	public void infoEmited(SimulationObjectInfo info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void infoEmited(SimulationStartInfo info) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void infoEmited(SimulationEndInfo info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void infoEmited(TimeChangeInfo info) {
		// TODO Auto-generated method stub
		
	}
}
