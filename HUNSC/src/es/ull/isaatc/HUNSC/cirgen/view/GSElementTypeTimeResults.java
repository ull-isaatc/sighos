/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen.view;

/**
 * @author Iván
 *
 */
public class GSElementTypeTimeResults implements GSResult {
	private final int []createdElements;
	private final int []finishedElements;
	private final double []workTime;
	
	/**
	 * @param createdElements
	 * @param finishedElements
	 * @param workTime
	 */
	public GSElementTypeTimeResults(int[] createdElements, int[] finishedElements, double[] workTime) {
		this.createdElements = createdElements;
		this.finishedElements = finishedElements;
		this.workTime = workTime;
	}

	/**
	 * @return the createdElements
	 */
	public int[] getCreatedElements() {
		return createdElements;
	}

	/**
	 * @return the finishedElements
	 */
	public int[] getFinishedElements() {
		return finishedElements;
	}

	/**
	 * @return the workTime
	 */
	public double[] getWorkTime() {
		return workTime;
	}

}
