package es.ull.isaatc.simulation.benchmark;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 */

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class Result2DB {
	public static int[]NPROCS = {1, 2, 4, 8};
	public static int[]NTHREADS = {1, 2, 4, 8, 16, 32, 64, 128};
	public static int[]NACTS = {1, 2, 4, 8, 16, 32};
	public static int NELEM = 2000;
	public static String DIR = "S:\\simulacion\\Benchmark\\Iris\\p3_" + NELEM + "el_cpu2\\";
	public static String DATE = "\"2009/01/09\"";
	public static int NEXP = 10;
	public static int EXPTYPE = 1; // Correspondiente a la BBDD

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://193.145.98.240/SIGHOS?" +
				"user=sighos&password=sighos");

			for (int nProc : NPROCS) {
				for (int nTh : NTHREADS) {
					for (int nAct : NACTS) {
						Statement stmt = null;
						ResultSet rs = null;
						try {
							BufferedReader inFiles = new BufferedReader(new FileReader(DIR + "p" + nProc + "a" + nAct + "th" + nTh + "" + NELEM + ".txt"));
							stmt = conn.createStatement();
							String qry = "INSERT INTO experiment (date, type, nelem, nact, nthr, nproc) "
								+ "values (" + DATE + ", " + EXPTYPE + ", " + NELEM  + ", " + nAct + ", " + nTh + ", " + nProc + ")";
							stmt.executeUpdate(qry,	Statement.RETURN_GENERATED_KEYS);

							int expId = -1;
							rs = stmt.getGeneratedKeys();
							if (rs.next())
								expId = rs.getInt(1);

							String line = null;
							int nExp = 1;
							while ((line = inFiles.readLine()) != null) {
								qry = "INSERT INTO simulation (expid, niter, value) " +
								"values (" + expId + ", " + (nExp++) + ", " + Double.parseDouble(line) + ")";
								stmt.executeUpdate(qry);
							}
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						finally {
							rs.close();
							stmt.close();
						}
					}
					
				}

			}

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

}
