/**
 * 
 */
package es.ull.isaatc.simulation.state.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class provides a connection to a database
 * @author Roberto Muñoz
 */
public class DBConnection {

    Connection connection;
    
    public DBConnection(String driver, String url, String user, String password) {
	try {
	    Class.forName(driver);
	    connection = DriverManager.getConnection(url, user, password);
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * @return the connection
     */
    public Connection getConnection() {
        return connection;
    }
    
    /**
     * Close the current connection
     */
    public void close() {
	try {
	    connection.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }
}
