/**
 * 
 */
package es.ull.cyc.util;

import java.util.Random;

/**
 * This class provides a method to generate permutations.
 */
public class RandomPermutation {
	private static Random generator = null;
	
	/**
	 * Gets the next permutation
	 * 
	 * @param n
	 *            the maximum number in the permutation
	 * @param r
	 *            the array containing the permutations
	 */
	public static int[] nextPermutation(int n) {
		if (generator == null)
			generator = new Random();			
		int[] p = new int[n];
		for (int i = 0; i < n; i++)
			p[i] = i;

		int pSize = n;
		int[] r = new int[n];

		for (int i = 0; i < n; i++) {
			int pos = generator.nextInt(pSize);
			r[i] = p[pos];
			p[pos] = p[pSize - 1];// this
			pSize--;// this
		}
		return r;
	}
}
