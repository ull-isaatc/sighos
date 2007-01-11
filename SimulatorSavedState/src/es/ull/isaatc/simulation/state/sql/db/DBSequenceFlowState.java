/**
 * 
 */
package es.ull.isaatc.simulation.state.sql.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Roberto Muñoz
 *
 */
public class DBSequenceFlowState extends DBGroupFlowState {

    public DBSequenceFlowState(Connection connection, DBSighosRegistry container) throws SQLException {
	super(connection, container, "SEQ");
    }
}
