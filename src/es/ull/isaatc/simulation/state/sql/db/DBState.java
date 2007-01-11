/**
 * 
 */
package es.ull.isaatc.simulation.state.sql.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import es.ull.isaatc.simulation.state.State;

/**
 * @author Roberto Muñoz
 *
 */
public abstract class DBState extends DBSighosRegistry {
    
    
    public DBState(Connection connection) throws SQLException {
	super(connection);
    }
    
    /**
     * @param connection
     * @param container
     * @throws SQLException
     */
    public DBState(Connection connection, DBSighosRegistry container) throws SQLException {
	super(connection, container);
    }

    public abstract List<State> getState() throws SQLException;
    
    public abstract State getState(int pk) throws SQLException;
    
    public abstract void storeState(State state) throws SQLException;
}
