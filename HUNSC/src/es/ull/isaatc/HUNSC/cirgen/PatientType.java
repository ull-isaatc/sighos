/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class PatientType {
	private static int indexCount = 0;
	private final String name;
	private final int []total;
	private final double percOR;
	private final double []avg;
	private final double []std;
	private final int []index;
	
	public PatientType(String name, int totalOR, int totalDC, double avgOR, double stdOR, double avgDC, double stdDC) {
		index = new int[2];
		index[OperationTheatreType.OR.ordinal()] = indexCount++;
		index[OperationTheatreType.DC.ordinal()] = indexCount++;
		this.name = name;
		this.total = new int[2];
		this.total[OperationTheatreType.OR.ordinal()] = totalOR;
		this.total[OperationTheatreType.DC.ordinal()] = totalDC;
		percOR = (double)totalOR / (double)(totalOR + totalDC);
		this.avg = new double[2];
		this.std = new double[2];
		this.avg[OperationTheatreType.OR.ordinal()] = avgOR; 
		this.avg[OperationTheatreType.DC.ordinal()] = avgDC; 
		this.std[OperationTheatreType.OR.ordinal()] = stdOR; 
		this.std[OperationTheatreType.DC.ordinal()] = stdDC; 
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the total
	 */
	public int getTotal() {
		return total[0] + total[1];
	}

	public int getTotal(OperationTheatreType type) {
		return total[type.ordinal()];
	}

	public double getTotalTime(OperationTheatreType type) {
		return total[type.ordinal()] * avg[type.ordinal()];
	}

	/**
	 * Returns the probability of a patient type of being Ordinary
	 * or daycase.
	 * @param type Ordinary or daycase.
	 * @return The probability of a patient type of being Ordinary or daycase.
	 */
	public double getProbability(OperationTheatreType type) {
		if (type == OperationTheatreType.OR)
			return percOR;
		return 1 - percOR;
	}
	
	/**
	 * 
	 * @param type Ordinary or daycase.
	 * @return
	 */
	public double getAverage(OperationTheatreType type) {
		return avg[type.ordinal()];
	}
	/**
	 * @param type Ordinary or daycase.
	 * @return
	 */
	public double getStdDev(OperationTheatreType type) {
		return std[type.ordinal()];
	}
	
	public int getIndex(OperationTheatreType type) {
		return index[type.ordinal()];
	}
}
