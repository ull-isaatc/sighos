/**
 * 
 */
package es.ull.isaatc.simulation.state.sql;

import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationListener;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;
import es.ull.isaatc.simulation.info.TimeChangeInfo;

/**
 * A standard listener. It only shows the events on the standard output.
 * 
 * @author Iván Castilla Rodríguez
 * 
 */
public class SimulationStateListener implements SimulationListener {

    /**
     * Creates a listener for storing the state in a database.
     */
    public SimulationStateListener() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationEndInfo)
     */
    public void infoEmited(SimulationEndInfo info) {
	System.out.println("SIMULATION END");
	DBStateProcessor dbStateProcessor = new DBStateProcessor();
	dbStateProcessor.process(info.getSimulation().getState());
	System.out.println("This simulation id is :\t" + dbStateProcessor.getSimulationId());
    }

    public void infoEmited(SimulationObjectInfo info) {
	// TODO Auto-generated method stub
    }

    public void infoEmited(SimulationStartInfo info) {
	// TODO Auto-generated method stub
    }

    public void infoEmited(TimeChangeInfo info) {
	// TODO Auto-generated method stub
    }
}
