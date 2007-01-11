/**
 * 
 */
package es.ull.isaatc.simulation.state.sql.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Roberto Muñoz
 *
 */
public class DBSighosRegistry {
    /** Database conn */
    protected Connection conn;

    /** Results set */
    protected ResultSet cdr;
    
    /** Primary key of the container registry */
    protected DBSighosRegistry container;
    
    /** Primary key of the registry */
    protected int pk;
    
    /** SQL update statement */
    protected PreparedStatement st;
    
    /** Error strings */
    public static String GENKEY_ERROR = "GeratedKey error";

    public DBSighosRegistry(Connection connection) throws SQLException {
	this.conn = connection;
    }

    public DBSighosRegistry(Connection connection, DBSighosRegistry container) throws SQLException {
	this.conn = connection;
	this.container = container;
    }

    public void closeConnection() throws SQLException {
	if (cdr != null)
	    cdr.close();
	if (conn != null)
	    conn.close();
    }

    
    /**
     * @return the container
     */
    public DBSighosRegistry getContainer() {
        return container;
    }

    /**
     * @param container the container to set
     */
    public void setContainer(DBSighosRegistry container) {
        this.container = container;
    }

    /**
     * @return the pk
     */
    public int getPk() {
        return pk;
    }

    
    protected void executeUpdate() throws SQLException {
	st.executeUpdate();
	ResultSet rs = st.getGeneratedKeys();
	if (rs.next())
	    pk = rs.getInt(1);
	else {
	    throw new SQLException(DBState.GENKEY_ERROR);
	}
    }
    
    public void executeQuery() throws SQLException {
//	System.out.println(st.toString());
	cdr = st.executeQuery();
    }     

}
