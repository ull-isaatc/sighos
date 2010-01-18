/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Stores a matrix which expresses transitions between pairs of T objects.
 * @author Iván Castilla Rodríguez
 *
 */
public class TransitionMatrix<T> {
	private TreeMap<T, TransitionMatrix<T>.TransitionRow> trans;

	public TransitionMatrix() {
		trans = new TreeMap<T, TransitionMatrix<T>.TransitionRow>();
	}
	
	/**
	 * Adds a new transition
	 * @param fromTrans Source T
	 * @param toTrans Destiny T
	 * @param prob Probability of the transition
	 */
	public void add(T fromTrans, T toTrans, double prob) {
		add(fromTrans);
		trans.get(fromTrans).add(toTrans, prob);
	}
	
	public void add(T fromTrans) {
		if (!trans.containsKey(fromTrans))
			trans.put(fromTrans, new TransitionRow());		
	}
	
	public T getNext(T fromTrans, double prob) {
		return trans.get(fromTrans).getNext(prob);
	}
	
	private boolean checkSubTransition(TreeSet<T> visited, T fromTrans, T finalState) {
		if (fromTrans == finalState)
			return true;
		visited.add(fromTrans);
		if (!trans.containsKey(fromTrans))
			return false;
		if (trans.get(fromTrans).size() == 0)
			return false;
		boolean end = true;
		for (T toTrans : trans.get(fromTrans).keySet()) {
			if (!visited.contains(toTrans))
				end &= checkSubTransition(visited, toTrans, finalState);
		}
		return end;
	}
	
	public boolean checkTransitions(T initialState, T finalState) {
		TreeSet<T> visited = new TreeSet<T>();
		return checkSubTransition(visited, initialState, finalState);
	}
	
	/**
	 * Returns a clone of the indicated row
	 * @param fromTrans
	 * @return
	 */
	public TransitionMatrix<T>.TransitionRow getRow(T fromTrans) {
		return (TransitionMatrix<T>.TransitionRow)trans.get(fromTrans).clone();			
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for(Map.Entry<T, TransitionMatrix<T>.TransitionRow> entry : trans.entrySet())
			str.append("[" + entry.getKey() + "]\t" + entry.getValue() + "\n");
		return str.toString();
	}
	
	/**
	 * A row in the transition matrix. Contains all the possible transitions from one
	 * T. 
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public class TransitionRow extends TreeMap<T, Double> {
		private static final long serialVersionUID = -7186360649603356719L;
		
		public TransitionRow() {
			super();
		}
		
		public void add(T toTrans, double prob) {
			put(toTrans, prob);
		}
		
		/**
		 * Gets the next transition according to the probability indicated. It 
		 * could return null if there's no valid transition.
		 * @param prob Value used to find the following transition
		 * @return The following valid transition. null if no valid transitions are available
		 */
		public T getNext(double prob) {
			double delta = 0.0;
			for (Map.Entry<T, Double> trans : entrySet()) {
				delta += trans.getValue();
				if (delta >= prob)
					return trans.getKey();
			}
			return null;			
		}

		/**
		 * Removes the indicated transition and normalizes the rest of probabilities.
		 * @param noValidTrans The transition to be removed 
		 */
		public void reAdjust(T noValidTrans) {
			double newValue = 1 - get(noValidTrans);
			remove(noValidTrans);
			for (Map.Entry<T, Double> trans : entrySet()) 
				trans.setValue(trans.getValue() / newValue);
		}
		
		@Override
		public Object clone() {
			TransitionRow tr = new TransitionRow();
			for (Map.Entry<T, Double> trans : entrySet())
				tr.add(trans.getKey(), trans.getValue());
			return tr;
		}
		
		public String toString() {
			StringBuilder str = new StringBuilder();
			for (Map.Entry<T, Double> trans : entrySet())
				str.append(trans.getKey() + "(" + trans.getValue() + ")\t");
			return str.toString();
		}
	}

}
