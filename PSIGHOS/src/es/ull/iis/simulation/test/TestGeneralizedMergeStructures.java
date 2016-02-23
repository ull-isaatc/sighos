/**
 * 
 */
package es.ull.iis.simulation.test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Control of incoming branches per element. Counts how many incoming branches have
 * arrived, how many of them were true, and if this element has ever activated the 
 * outgoing branch, i.e. if at least one of the incoming branches was able to pass. 
 * @author ycallero
 */
class IncomingBranchesControl {
	protected TreeMap<Integer, LinkedList<Boolean>> incBranches;
	/** True if the outgoing branch was successfully activated */
	protected boolean activated = false;
	/** Current amount of valid arrived branches */
	protected int trueChecked = 0;
	protected int incomingBranches;
	
	/**
	 * Create a new control structure for the incoming branches of an element.
	 */
	public IncomingBranchesControl(int incomingBranches) {
		incBranches = new TreeMap<Integer, LinkedList<Boolean>>();
		this.incomingBranches = incomingBranches;
	}
	
	public void arrive(Integer ind, Boolean exec) {
		System.out.println("ARRIVING " + ind + "[" + exec + "]");
		// New incoming branch
		if (!incBranches.containsKey(ind)) {
			incBranches.put(ind, new LinkedList<Boolean>());
			if (exec)
				trueChecked++;
		}
		// The new incoming branch is added
		incBranches.get(ind).add(exec);
	}
	
	public boolean canReset() {
		return (incBranches.size() == incomingBranches);
	}
	
	public boolean reset() {
		Iterator<Map.Entry<Integer, LinkedList<Boolean>>> iter = incBranches.entrySet().iterator();
		trueChecked = 0;
		while (iter.hasNext()) {
			Map.Entry<Integer, LinkedList<Boolean>> entry = iter.next();
			entry.getValue().removeFirst();
			if (!entry.getValue().isEmpty()) {
				if (entry.getValue().peek())
					trueChecked++;
			}
			else
				iter.remove();
		}
		return incBranches.isEmpty();
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("Checked: " + incBranches.size() + "\n");
		str.append("TrueChecked: " + trueChecked + "\n");
		for (Map.Entry<Integer, LinkedList<Boolean>> entry : incBranches.entrySet()) {
			str.append(entry.getKey() + "[ ");
			for (Boolean b : entry.getValue())
				str.append(b + " ");
			str.append("]\n");
		}
		return str.toString();
	}
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestGeneralizedMergeStructures {
	static IncomingBranchesControl control = new IncomingBranchesControl(3);

	public static void testArrival(int ind, boolean b) {
		control.arrive(ind, b);
		System.out.println(control);
		if (control.canReset()) {
			control.reset();
			System.out.println("RESET\n" + control);
		}		
	}
	public static void testAllTrueArrivals() {
		testArrival(1, true);
		testArrival(2, true);
		testArrival(3, true);
	}
	public static void testAllFalseArrivals() {
		testArrival(1, false);
		testArrival(2, false);
		testArrival(3, false);
	}
	
	public static void testAllSameBranchArrivals() {
		testArrival(1, true);
		testArrival(1, true);
		testArrival(1, true);		
	}
	
	public static void testMixedArrival() {
		testArrival(1, true);
		testArrival(2, false);
		testArrival(1, false);
		testArrival(2, true);
		testArrival(3, true);
		testArrival(1, false);
		testArrival(2, true);
		testArrival(3, true);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		testAllSameBranchArrivals();
//		testAllFalseArrivals();
		testMixedArrival();
	}

}
