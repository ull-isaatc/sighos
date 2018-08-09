/**
 * 
 */
package es.ull.iis.simulation.hta.retal.params;

import java.util.TreeMap;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public final class VisualAcuity {
	final static public double LETTERS_BLINDNESS = 10;
	/**
	 * The maximum logMAR score, equivalent to blindness 
	 */
	final static public double MAX_LOGMAR = 1.6; 
	final static private double[][] LOGMAR_AND_LETTERS = {
			{1.60, 0},
			{1.50, 10},
			{1.40, 15},
			{1.30, 20},
			{1.20, 25},
			{1.10, 30},
			{1.00, 35},
			{0.90, 40},
			{0.80, 45},
			{0.70, 50},
			{0.60, 55},
			{0.50, 60},
			{0.40, 65},
			{0.30, 70},
			{0.20, 75},
			{0.10, 80},
			{0.00, 85}
	};


	final static private TreeMap<Double, Double> LOGMAR2LETTERS = new TreeMap<Double, Double>();
	final static private TreeMap<Double, Double> LETTERS2LOGMAR = new TreeMap<Double, Double>();
	
	static {
		for (int i = 0; i < LOGMAR_AND_LETTERS.length; i++) {
			LOGMAR2LETTERS.put(LOGMAR_AND_LETTERS[i][0], LOGMAR_AND_LETTERS[i][1]);
			LETTERS2LOGMAR.put(LOGMAR_AND_LETTERS[i][1], LOGMAR_AND_LETTERS[i][0]);
		}
	}

	public static double getLogMARFromLetters(double letters) {
		return LETTERS2LOGMAR.get(LETTERS2LOGMAR.floorKey(letters));
	}
	
	public static double getLettersFromLogMAR(double logMAR) {
		return LOGMAR2LETTERS.get(LOGMAR2LETTERS.floorKey(logMAR));
	}
}
