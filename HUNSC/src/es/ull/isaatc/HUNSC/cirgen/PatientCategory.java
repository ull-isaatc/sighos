/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen;

/**
 * Categorías de paciente del hospital
 * @author Iván Castilla Rodríguez
 *
 */
public class PatientCategory {
	private static int indexCount = 0;
	private final String name;
	private final AdmissionType at;
	private final PatientType pt;
	private final int total;
	private final double totalTime;
	private final String dist;
	private final double param1;
	private final double param2;
	private final int index;
	
	public PatientCategory(String name, AdmissionType at, PatientType pt, int total, double totalTime, String dist, double param1, double param2) {
		this.index = indexCount++;
		this.name = name;
		this.at = at;
		this.pt = pt;
		this.total = total;
		this.totalTime = totalTime;
		this.dist = dist;
		this.param1 = param1;
		this.param2 = param2;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the at
	 */
	public AdmissionType getAdmissionType() {
		return at;
	}

	/**
	 * @return the pt
	 */
	public PatientType getPatientType() {
		return pt;
	}

	/**
	 * @return the total
	 */
	public int getTotal() {
		return total;
	}

	public double getTotalTime() {
		return totalTime;
	}

	public String getDist() {
		return dist;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getParam1() {
		return param1;
	}
	/**
	 * @return
	 */
	public double getParam2() {
		return param2;
	}
	
	public int getIndex() {
		return index;
	}
}
