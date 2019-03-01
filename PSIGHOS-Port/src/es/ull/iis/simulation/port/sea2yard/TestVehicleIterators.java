/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestVehicleIterators {

	/**
	 * 
	 */
	public TestVehicleIterators() {
		// TODO Auto-generated constructor stub
	}

	private static void sequentialIterator(int n) {
		int [] array = new int[4];
		
		for (int i = n; i >= 0; i--) {
			for (int j = n - i; j >= 0; j--) {
				for (int k = n - i - j; k >= 0; k--) {
					System.out.println(i + "\t" + j + "\t" + k + "\t" + (n - i - j - k));
				}
			}
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		sequentialIterator(12);
//		int[] start = new int[] {0, 0, 10};
//		printArray(start);
//		Iterator<int[]> it = new SpecificVehiclesIterator(start, true);
//		Iterator<int[]> it = new LeftToRightSpecificVehiclesIterator(12, 3);
		Iterator<int[]> it = new RightToLeftSpecificVehiclesIterator(12, 3);
		while (it.hasNext()) {
			printArray(it.next());
		}
		
	}

	private static void printArray(int [] array) {
		System.out.print("[" + array[array.length - 1]);
		for (int i = 0; i < array.length - 1; i++)
			System.out.print(", " + array[i]);
		System.out.println("]");
	}
	
	private static class LeftToRightSpecificVehiclesIterator implements Iterator<int[]> {
		private final int[] currentState;
		private final int[] nextState;
		private final int nVehicles;
		private final int nCranes;

		public LeftToRightSpecificVehiclesIterator(int nVehicles, int nCranes) {
			this.nVehicles = nVehicles;
			this.nCranes = nCranes;
			this.currentState = new int[nCranes + 1];
			this.nextState = new int[nCranes + 1];
			this.nextState[nCranes] = nVehicles;
		}

		public LeftToRightSpecificVehiclesIterator(int[] start) {
			this.currentState = new int[start.length + 1];
			nextState = Arrays.copyOf(start, start.length);
			int counter = 0;
			for (int nv : start) {
				counter += nv;
			}
			nVehicles = counter;
			nCranes = start.length - 1;
		}
		
		@Override
		public boolean hasNext() {
			return currentState[nCranes] != 0 || currentState[nCranes - 1] != (nVehicles - nCranes + 1);
		}

		@Override
		public int[] next() {
			System.arraycopy(nextState, 0, currentState, 0, currentState.length);
			do {
				int level = nCranes - 2;
				while (nextState[level] == 0) {
					level = (level == 0) ? nCranes: level - 1;
				}
				if (level == nCranes) {
					if (nextState[level] != 0) {
						nextState[level]--;
						final int remainding = nextState[nCranes - 1];
						nextState[nCranes - 1] = 0;
						nextState[0] = remainding + 1;
					}
				}
				else {
					nextState[level]--;
					final int remainding = nextState[nCranes - 1];
					nextState[nCranes - 1] = 0;
					nextState[level + 1] = remainding + 1;  
				}
			} while (hasNext() && !isValid());
			return currentState;
		}
		
		private boolean isValid() {
			boolean valid = true;
			if (nextState[nCranes] == 0) {
				for (int i = 0; i < nCranes && valid; i++) {
					valid = (nextState[i] != 0);
				}
			}
			return valid;
		}
	}

	private static class RightToLeftSpecificVehiclesIterator implements Iterator<int[]> {
		private final int[] currentState;
		private final int[] nextState;
		private final int nVehicles;
		private final int nCranes;

		public RightToLeftSpecificVehiclesIterator(int nVehicles, int nCranes) {
			this.nVehicles = nVehicles;
			this.nCranes = nCranes;
			this.currentState = new int[nCranes + 1];
			this.nextState = new int[nCranes + 1];
			this.nextState[nCranes] = nVehicles;
		}

		public RightToLeftSpecificVehiclesIterator(int[] start) {
			this.currentState = new int[start.length + 1];
			nextState = Arrays.copyOf(start, start.length);
			int counter = 0;
			for (int nv : start) {
				counter += nv;
			}
			nVehicles = counter;
			nCranes = start.length - 1;
		}
		
		@Override
		public boolean hasNext() {
			return currentState[nCranes] != 0 || currentState[0] != (nVehicles - nCranes + 1);
		}

		@Override
		public int[] next() {
			System.arraycopy(nextState, 0, currentState, 0, currentState.length);
			do {
				if (currentState[0] != nVehicles) {
					int level = 1;
					while (nextState[level] == 0) {
						level++;
					}
					if (level == nCranes) {
						if (nextState[level] != 0) {
							nextState[level]--;
							final int remainding = nextState[0];
							nextState[0] = 0;
							nextState[nCranes - 1] = remainding + 1;
						}
					}
					else {
						nextState[level]--;
						final int remainding = nextState[0];
						nextState[0] = 0;
						nextState[level - 1] = remainding + 1;  
					}
				}
			} while (hasNext() && !isValid());
			return currentState;
		}
		
		private boolean isValid() {
			boolean valid = true;
			if (nextState[nCranes] == 0) {
				for (int i = 0; i < nCranes && valid; i++) {
					valid = (nextState[i] != 0);
				}
			}
			return valid;
		}
	}

}
