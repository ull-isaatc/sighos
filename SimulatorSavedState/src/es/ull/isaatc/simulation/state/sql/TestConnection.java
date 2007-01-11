/**
 * 
 */
package es.ull.isaatc.simulation.state.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Roberto Muñoz
 *
 */
public class TestConnection {

    /**
     * @param args
     */
    public static void main(String[] args) {
	try {
	    Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://garimba:3306/simulationstate";
		String user = "sighos";
		String password = "sighos";
		Connection con = DriverManager.getConnection(url, user, password);
		System.out.println("Connection OK");
		con.close();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

}
