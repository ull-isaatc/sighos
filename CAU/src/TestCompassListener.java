

import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.info.CompassListener;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.TimeChangeInfo;

/**
 * 
 * @author Roberto Muñoz
 */
public class TestCompassListener extends CompassListener {

	/**
	 * 
	 */
	public TestCompassListener(double period) {
		super(period);
	}

	@Override
	protected void takeSample(Simulation simul, double ts) {
		System.out.println("TS :\t" + ts);
		
	}

	public void infoEmited(SimulationEndInfo info) {
		// TODO Auto-generated method stub
		
	}

	public void infoEmited(TimeChangeInfo info) {
		// TODO Auto-generated method stub
		
	}

}
