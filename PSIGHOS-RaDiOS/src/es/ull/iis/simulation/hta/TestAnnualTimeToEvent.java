/**
 * 
 */
package es.ull.iis.simulation.hta;

import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;

/**
 * @author icasrod
 *
 */
public class TestAnnualTimeToEvent {
	private static final int MAX_AGE = 100;
	private static final double[][] P_DNC_ALB1 = {{9.0, 0.022}, {MAX_AGE + 1, 0.036}};
	private static final double[][] P_BGRET_PRET = {{9.0, 0.0}, {MAX_AGE + 1, 0.011}};
	private static final double[][] P_TEST3 = {{MAX_AGE + 1, 0.025}};
	/**
	 * 
	 */
	public TestAnnualTimeToEvent() {
	}

	private static double getAnnualBasedTimeToEvent(double[][] probs, RandomNumber rng, double rr) {
		int i = 0;
		for (final double[] p : probs) {
			final double finalP = 1 - Math.exp(Math.log(1-p[1])*rr);
			for (; i < p[0]; i++) {
				if (rng.draw() < finalP)
					return i;
			}			
		}
		return Double.MAX_VALUE;		
	}

	private static double getAnnualBasedTimeToEvent(double[][] probs, double[] rnd, double rr) {
		double ref = 0.0;
		for (int i = 0; i < probs.length; i++) {
			final double time2Event = getAnnualBasedTimeToEvent(-1 / probs[i][1], rnd[i], rr) + ref;
			if (time2Event < probs[i][0])
				return time2Event;
			ref = probs[i][0];
		}
		return Double.MAX_VALUE;
	}

	public static double getAnnualBasedTimeToEvent(double p, RandomNumber rng, double rr) {
		final double finalP = 1 - Math.exp(Math.log(1-p)*rr);
		for (int i = 0; i <= MAX_AGE; i++) {
			if (rng.draw() < finalP)
				return i;
		}
		return Double.MAX_VALUE;
	}
	
	private static double getAnnualBasedTimeToEvent(double minusAvgTimeToEvent, double rnd, double rr) {
		// In case the probability of transition was 0
		if (Double.isInfinite(minusAvgTimeToEvent))
			return Double.MAX_VALUE;
		final double newMinus = -1 / (1-Math.exp(Math.log(1+1/minusAvgTimeToEvent)*rr));
		return (newMinus) * Math.log(rnd);
	}
	
	private static double getAnnualBasedTimeToEvent(double durationOfDiabetes, double[][] probs, double[] rnd, double rr) {
		double ref = 0.0;
		for (int i = 0; i < probs.length; i++) {
			if (probs[i][0] > durationOfDiabetes) {
				final double time2Event = getAnnualBasedTimeToEvent(-1 / probs[i][1], rnd[i], rr) + ref;
				if (time2Event + durationOfDiabetes < probs[i][0])
					return time2Event;
				ref = probs[i][0] - durationOfDiabetes;
			}
		}
		return Double.MAX_VALUE;
	}

	static double[][] getSurvival(int n, double p) {
		final double[][] survival = new double[2][MAX_AGE + 1];
		survival[1][0] = n * p;
		survival[0][0] = n - n * p;
		for (int i = 1; i < MAX_AGE; i++) {
			survival[1][i] = survival[0][i-1] * p;
			survival[0][i] = survival[0][i-1] * (1-p);
		}
		return survival;
	}
	static double[][] getSurvival(int n, double[][] probs) {
		final double[][] survival = new double[2][MAX_AGE + 1];
		survival[1][0] = n * probs[0][1];
		survival[0][0] = n * (1- probs[0][1]);
		int i = 1;
		for (double[] p : probs) {
			for (; i < p[0]; i++) {
				survival[1][i] = survival[0][i-1] * p[1];
				survival[0][i] = survival[0][i-1] * (1-p[1]);
			}
		}
		return survival;
	}
	static double[][] getSurvival(int n, double[][] probs, double duration) {
		int index = 0;
		while (probs[index][0] < duration)
			index++;
		final double[][] survival = new double[2][MAX_AGE + 1 - (int) duration];
		survival[1][0] = n * probs[index][1];
		survival[0][0] = n * (1- probs[index][1]);
		int i = 1;
		for (; index < probs.length; index++) {
			for (; i < Math.min(probs[index][0], MAX_AGE + 1) - duration; i++) {
				survival[1][i] = survival[0][i-1] * probs[index][1];
				survival[0][i] = survival[0][i-1] * (1-probs[index][1]);
			}
		}
		return survival;
	}
	static double[][] getSurvival(int n, double[] time2Event) {
		final double[][] survival = new double[2][MAX_AGE + 1];
		for (double t : time2Event) {
			if (t <= MAX_AGE)
				survival[1][(int)t]++;
		}
		survival[0][0] = n - survival[1][0];

		for (int i = 1; i < MAX_AGE; i++) {
			survival[0][i] = survival[0][i-1] - survival[1][i];
		}
		return survival;
	}
	static void testSingleProb(double p) {
		final double rr = 1.0;
		final int n = 100000;
		final double[] time2Event = new double[n];
		final double[] time2Event2 = new double[n];
		final RandomNumber rng = RandomNumberFactory.getInstance();
		long cpu = System.nanoTime();
		final double invMinP = -1.0 / p;
		for (int i = 0; i < n; i++) {
			time2Event[i] = getAnnualBasedTimeToEvent(invMinP, rng.draw(), rr);
		}
		final long t1 = System.nanoTime() - cpu;
		cpu = System.nanoTime();
		for (int i = 0; i < n; i++) {
			time2Event2[i] = getAnnualBasedTimeToEvent(p, rng, rr);
		}
		final long t2 = System.nanoTime() - cpu;
		
		double[][] surv1 = getSurvival(n, p);
		double[][] surv2 = getSurvival(n, time2Event);
		double[][] surv3 = getSurvival(n, time2Event2);
		printResults(surv1, surv2, surv3, t1, t2);
	}
	
	static void testCompoundProb(double[][] probs) {
		final double rr = 1.0;
		final int n = 1000000;
		final double[] time2Event = new double[n];
		final double[] time2Event2 = new double[n];
		final RandomNumber rng = RandomNumberFactory.getInstance();
		long cpu = System.nanoTime();
		final double[] rnd = new double[probs.length];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < probs.length; j++)
				rnd[j] = rng.draw();
			time2Event[i] = getAnnualBasedTimeToEvent(probs, rnd, rr);
		}
		final long t1 = System.nanoTime() - cpu;
		cpu = System.nanoTime();
		for (int i = 0; i < n; i++) {
			time2Event2[i] = getAnnualBasedTimeToEvent(probs, rng, rr);
		}
		final long t2 = System.nanoTime() - cpu;
		
		final double[][] surv1 = getSurvival(n, probs);
		final double[][] surv2 = getSurvival(n, time2Event);
		final double[][] surv3 = getSurvival(n, time2Event2);
		printResults(surv1, surv2, surv3, t1, t2);
	}

	static void testDurationProb(double duration, double[][] probs) {
		final double rr = 1.0;
		final int n = 1000000;
		final double[] time2Event = new double[n];
		final RandomNumber rng = RandomNumberFactory.getInstance();
		long cpu = System.nanoTime();
		final double[] rnd = new double[probs.length];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < probs.length; j++)
				rnd[j] = rng.draw();
			time2Event[i] = getAnnualBasedTimeToEvent(duration, probs, rnd, rr);
		}
		final long t1 = System.nanoTime() - cpu;
		
		final double[][] surv1 = getSurvival(n, probs, duration);
		final double[][] surv2 = getSurvival(n, time2Event);
		printResults(surv1, surv2, t1, duration);
	}

	static void printResults(double[][] surv1, double[][] surv2, double[][] surv3, long t1, long t2) {
		final double[][] err = new double[2][surv1[0].length];
		final double[] errAcc = new double[2];
		errAcc[0] = 0.0;
		errAcc[1] = 0.0;
		for (int i = 0; i <= MAX_AGE; i++) {
			err[0][i] = (surv1[1][i] == 0) ? 0.0 : ((surv2[1][i] - surv1[1][i]) /surv1[1][i]);
			err[1][i] = (surv1[1][i] == 0) ? 0.0 : ((surv3[1][i] - surv1[1][i]) /surv1[1][i]);
			errAcc[0] += (err[0][i] * err[0][i]) / err[0].length;
			errAcc[1] += (err[1][i] * err[1][i]) / err[1].length;
		}
		System.out.println("MET\tCPU\tERR");
		System.out.println("1\t" + (t1/1000) + "\t" + String.format("%.3f", errAcc[0]));
		System.out.println("2\t" + (t2/1000) + "\t" + String.format("%.3f", errAcc[1]));
		System.out.println("AGE\tSURV1\tINC1\tSURV2\tINC2\tSURV3\tINC3");
		for (int i = 0; i < surv1[0].length; i++) {
			System.out.println(i + "\t" + surv1[0][i] + "\t" + surv1[1][i] + "\t" + surv2[0][i] + "\t" + surv2[1][i] + "\t"
					+ surv3[0][i] + "\t" + surv3[1][i]);
		}
	}

	static void printResults(double[][] surv1, double[][] surv2, long t1, double duration) {
		final double[][] err = new double[2][surv1[0].length];
		double errAcc = 0.0;
		for (int i = 0; i <= MAX_AGE - (int)duration; i++) {
			err[0][i] = (surv1[1][i] == 0) ? 0.0 : ((surv2[1][i] - surv1[1][i]) /surv1[1][i]);
			errAcc += (err[0][i] * err[0][i]) / err[0].length;
		}
		System.out.println("MET\tCPU\tERR");
		System.out.println("1\t" + (t1/1000) + "\t" + String.format("%.4f", errAcc));
		System.out.println("AGE\tSURV1\tINC1\tSURV2\tINC2");
		for (int i = 0; i < surv1[0].length; i++) {
			System.out.println(i + "\t" + surv1[0][i] + "\t" + surv1[1][i] + "\t" + surv2[0][i] + "\t" + surv2[1][i]);
		}
	}
	/**
	 * Single tests benefit the iterative method when p is high; otherwise errors are similar and formula-based method is faster
	 * @param args
	 */
	public static void main(String[] args) {
//		testSingleProb(0.022);
//		testCompoundProb(P_DNC_ALB1);
		testDurationProb(5, P_TEST3);
	}

}
