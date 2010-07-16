package es.ull.isaatc.simulation.hospital.view;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	private static Connection conn = null;

	public static Connection getConnection() {
		if (conn == null) {
			try {
				conn = DriverManager.getConnection("jdbc:mysql://193.145.98.254/hospital?" +
				"user=myreview&password=");
			} catch (SQLException ex) {
				// handle any errors
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
			}
			finally {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
		return conn;
	}
}
