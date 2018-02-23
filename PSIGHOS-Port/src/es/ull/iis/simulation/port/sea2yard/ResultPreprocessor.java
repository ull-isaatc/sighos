/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import es.ull.iis.util.Statistics;

/**
 * Creates a single summary file from a set of simulation results.
 * Input files are output files from simulation (*.out).
 * Each line in the new file contains:
 * - INSTANCE: file name of the original instance
 * - SOL: Solution id
 * - VEHIC: Number of vehicles used to test this solution
 * - OVER: Overlapping computed for that solution
 * - OBJ: Objective value of the deterministic simulation
 * - AVG: Average objective value obtained when added perturbations
 * - SD: Standard deviation of the objective value obtained when added perturbations
 * - ROB0: Proportion of stochastic simulations whose objective value was lower or equal to the deterministic one
 * - ROB05: Proportion of stochastic simulations whose objective value was lower or equal to the deterministic one + 0.5%
 * - ROB1: Proportion of stochastic simulations whose objective value was lower or equal to the deterministic one + 1%
 * - ROB5: Proportion of stochastic simulations whose objective value was lower or equal to the deterministic one + 5%
 * @author Iván Castilla
 *
 */
public class ResultPreprocessor {
	final static private int N_SIM = 200;
	final static private int N_SOL = 25;
	final static private int N_VEHIC = 11;
	final static private String OUTPUT_FILE = "output3.txt";
	final private static String INPUT_EXT = ".out"; 
	final static private String INPUT_DIR = System.getProperty("user.home") + "/Dropbox/SimulationPorts/for_validation/more/";
	final static private boolean NORMAL_VEHICLE = true;
	final static private int FIRST_CRANE_FIELD = 6;
	final static private int N_CRANE_FIELDS = 4;

	private static Experiment[] preProcessFile(File file) {
		int lineNumber = 0;
		final Experiment[] experiments = new Experiment[N_SOL * N_VEHIC];
	    BufferedReader br = null;
	    String line = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			// Read header
			line = br.readLine();
			final String[] headFields = line.split("\t");
			final String lastField = headFields[headFields.length-1];
			// This will only work for nCranes < 10!!!
			final int nCranes = Integer.parseInt(lastField.substring(lastField.length()-1));
			lineNumber++;
			if (br != null) {
				// Fill deterministic results
				for (int i = 0; i < N_SOL; i++) {
					for (int j = 0; j < N_VEHIC; j++) {
						line = br.readLine();
						lineNumber++;
						final String[] fields = line.split("\t");
						if (Integer.parseInt(fields[0]) != 0) {
							System.err.println("Error leyendo línea " + lineNumber + " del fichero " + file.getName() + ". Se esperaba solución determinista.");
							System.exit(-1);
						}
						int nVehic = Integer.parseInt(fields[3]);
						if (NORMAL_VEHICLE) {
							nVehic = j - (N_VEHIC / 2);
						}
						final int[] totalTime = new int[nCranes];
						final int[] useTime = new int[nCranes];
						final int[] moveTime = new int[nCranes];
						final int[] opTime = new int[nCranes];
						for (int crane = 0; crane < nCranes; crane++) {
							totalTime[crane] = Integer.parseInt(fields[FIRST_CRANE_FIELD + crane * N_CRANE_FIELDS]);
							useTime[crane] = Integer.parseInt(fields[FIRST_CRANE_FIELD + crane * N_CRANE_FIELDS] + 1);
							opTime[crane] = Integer.parseInt(fields[FIRST_CRANE_FIELD + crane * N_CRANE_FIELDS] + 2);
							moveTime[crane] = Integer.parseInt(fields[FIRST_CRANE_FIELD + crane * N_CRANE_FIELDS + 3]);
						}
						experiments[i*N_VEHIC + j] = new Experiment(file.getName(), Integer.parseInt(fields[1]), nVehic, Integer.parseInt(fields[5]), nCranes, 
								totalTime, useTime, moveTime, opTime, Double.parseDouble(fields[2]));
					}
				}
				// Fill probabilistic results
				for (int k = 0; k < N_SIM; k++) {
					for (int i = 0; i < N_SOL; i++) {
						for (int j = 0; j < N_VEHIC; j++) {
							line = br.readLine();
							lineNumber++;
							final String[] fields = line.split("\t");
//							if (Integer.parseInt(fields[1]) != i) {
//								System.err.println("Error leyendo línea " + lineNumber + " del fichero " + fileName + ". Se encontró solución #" + Integer.parseInt(fields[1]));
//								System.exit(-1);
//							}
							final int[] totalTime = new int[nCranes];
							final int[] useTime = new int[nCranes];
							final int[] moveTime = new int[nCranes];
							final int[] opTime = new int[nCranes];
							for (int crane = 0; crane < nCranes; crane++) {
								totalTime[crane] = Integer.parseInt(fields[FIRST_CRANE_FIELD + crane * N_CRANE_FIELDS]);
								useTime[crane] = Integer.parseInt(fields[FIRST_CRANE_FIELD + crane * N_CRANE_FIELDS] + 1);
								opTime[crane] = Integer.parseInt(fields[FIRST_CRANE_FIELD + crane * N_CRANE_FIELDS] + 2);
								moveTime[crane] = Integer.parseInt(fields[FIRST_CRANE_FIELD + crane * N_CRANE_FIELDS + 3]);
							}
							experiments[i*N_VEHIC + j].addResult(Integer.parseInt(fields[5]), totalTime, useTime, moveTime, opTime);
						}
					}
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return experiments;
	}
	
	public static void preprocess(String folder) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(INPUT_DIR + OUTPUT_FILE)));
			
			File dir = new File(folder);
			File[] files = dir.listFiles(new FilenameFilter() {
				@Override
			    public boolean accept(File dir, String name) {
			        return name.toLowerCase().endsWith(INPUT_EXT);
			    }
			});
			out.println(expHeader());
			for (File file : files) {
				System.out.println("Preprocesando " + file.getName());
				final Experiment[] experiments = preProcessFile(file);
				for (Experiment exp : experiments) {
					out.println(exp2String(exp));
				}
			}
		
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static String expHeader() {
		return "INSTANCE\tSOL\tVEHIC\tCRANES\tOVER\tOBJ\tSUM\tAVG_OBJ\tSD_OBJ\tAVG_SUM\tSD_SUM\tROB0\tROB05\tROB1\tROB5";
	}
	public static String exp2String(Experiment exp) {
		final double[] avgSDObj = exp.getAvgSDObj();
		final double[] avgSDSumTotal = exp.getAvgSDSumTotalTime();
		return exp.getInstance() + "\t" + exp.getSolution() + "\t" + exp.getnVehic() + "\t" + exp.getnCranes() + "\t" + exp.getOverlap() 
			+ "\t" + exp.getObj() + "\t" + exp.getSumTotalTime() + "\t" + avgSDObj[0] + "\t" + avgSDObj[1] + "\t" + avgSDSumTotal[0] 
			+ "\t" + avgSDSumTotal[1] + "\t" + exp.getRobustness(0.0) + "\t" + exp.getRobustness(0.005) + "\t" + exp.getRobustness(0.01) 
			+ "\t" + exp.getRobustness(0.05);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		preprocess(INPUT_DIR);
	}

	final static class Experiment {
		private final String instance;
		private final int solution;
		private final int nVehic;
		private final int obj;
		private final int[] totalTimeBase;
		private final int[] useTimeBase;
		private final int[] moveTimeBase;
		private final int[] opTimeBase;
		private final int nCranes;
		private final double overlap;
		private final int[] results = new int[N_SIM];
		private final int[][] totalTime;
		private final int[][] useTime;
		private final int[][] moveTime;
		private final int[][] opTime;
		private int currentResult = 0;
		
		/**
		 * @param instance
		 * @param solution
		 * @param nVehic
		 * @param obj
		 * @param overlap
		 */
		public Experiment(String instance, int solution, int nVehic, int obj, int nCranes, int[] totalTimeBase, int[] useTimeBase, int[] moveTimeBase,
				int[] opTimeBase, double overlap) {
			this.instance = instance;
			this.solution = solution;
			this.nVehic = nVehic;
			this.obj = obj;
			this.nCranes = nCranes;
			this.overlap = overlap;
			this.totalTime = new int[N_SIM][nCranes];
			this.useTime = new int[N_SIM][nCranes];
			this.opTime = new int[N_SIM][nCranes];
			this.moveTime = new int[N_SIM][nCranes];
			this.totalTimeBase = totalTimeBase;
			this.useTimeBase = useTimeBase;
			this.moveTimeBase = moveTimeBase;
			this.opTimeBase = opTimeBase;		
		}

		public void addResult(int value, int[] totalTime, int[] useTime, int[] moveTime, int[] opTime) {
			results[currentResult] = value;
			this.totalTime[currentResult] = totalTime;
			this.useTime[currentResult] = useTime;
			this.moveTime[currentResult] = moveTime;
			this.opTime[currentResult] = opTime;
			currentResult++;
		}
		
		/**
		 * @return the objective value
		 */
		public int getObj() {
			return obj;
		}

		public double[] getAvgSDObj() {
			final double avg = Statistics.average(results);
			return new double[] {avg, Statistics.stdDev(results, avg)};
		}
		
		/**
		 * @return the total time that cranes spend in the system
		 */
		public int getSumTotalTime() {
			return getSumTime(totalTimeBase);
		}

		public double[] getAvgSDSumTotalTime() {
			return getAvgSDSumTimes(totalTime);
		}
		
		private int getSumTime(int[] times) {
			int tot = 0;
			for (int i = 0; i < nCranes; i++)
				tot += times[i];
			return tot;
		}
		
		private double[] getAvgSDSumTimes(int[][] times) {
			int [] tot = new int[N_SIM];
			for (int i = 0; i < N_SIM; i++) {
				tot[i] = 0;
				for (int j = 0; j < nCranes; j++) {
					tot[i] += times[i][j]; 
				}
			}
			final double avg = Statistics.average(tot);
			return new double[] {avg, Statistics.stdDev(tot, avg)};
		}
		
		public double getRobustness(double robCoef) {
			final double refValue = obj * (1 + robCoef);
			int cont = 0;
			for (int value : results) {
				if (value <= refValue)
					cont++;
			}
			return cont / (double)N_SIM;
		}
		
		/**
		 * @return the instance
		 */
		public String getInstance() {
			return instance;
		}

		/**
		 * @return the solution
		 */
		public int getSolution() {
			return solution;
		}

		/**
		 * @return the nVehic
		 */
		public int getnVehic() {
			return nVehic;
		}

		/**
		 * @return the nCranes
		 */
		public int getnCranes() {
			return nCranes;
		}

		/**
		 * @return the overlap
		 */
		public double getOverlap() {
			return overlap;
		}

		/**
		 * @return the results
		 */
		public int[] getResults() {
			return results;
		}
		
	}
}
