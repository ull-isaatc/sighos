/**
 * 
 */
package es.ull.isaatc.simulation.state.sql;

import java.sql.SQLException;

import es.ull.isaatc.simulation.state.SimulationState;
import es.ull.isaatc.simulation.state.StateProcessor;
import es.ull.isaatc.simulation.state.sql.db.DBSimulationState;

/**
 * This class provides the mechanism for storing a simulation into a database.
 * @author Roberto Muñoz
 */
public class DBStateProcessor implements StateProcessor {

    DBConnection dbConnection;
    
    DBSimulationState dbSimulationState;
    
    /* (non-Javadoc)
     * @see es.ull.isaatc.simulation.state.StateProcessor#process(es.ull.isaatc.simulation.state.SimulationState)
     */
    public void process(SimulationState state) {
	
	dbConnection = new DBConnection("com.mysql.jdbc.Driver", "jdbc:mysql://garimba:3306/simulationstate", "sighos", "sighos");

	try {
	    dbSimulationState = new DBSimulationState(dbConnection.getConnection());
	    dbSimulationState.storeState(state);
	} catch (SQLException e) {
	    e.printStackTrace();
	    System.exit(-1);
	}
	
	dbConnection.close();
    }
    
    public SimulationState getState(int simulationId) {
	dbConnection = new DBConnection("com.mysql.jdbc.Driver", "jdbc:mysql://garimba:3306/simulationstate", "sighos", "sighos");
	try {
	    dbSimulationState = new DBSimulationState(dbConnection.getConnection());
	    SimulationState state = (SimulationState)dbSimulationState.getState(simulationId);
	    dbConnection.close();
	    return state;	    
	} catch (SQLException e) {
	    e.printStackTrace();
	    System.exit(-1);
	}
	return null;
    }
    
    public int getSimulationId() {
	return dbSimulationState.getPk();
    }

}
