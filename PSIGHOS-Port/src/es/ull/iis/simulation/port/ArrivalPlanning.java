/**
 * 
 */
package es.ull.iis.simulation.port;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionParams;
import es.ull.iis.simulation.model.ModelCycle;
import es.ull.iis.util.Cycle;
import es.ull.iis.util.TableCycle;

/**
 * @author Iván Castilla
 *
 */
public class ArrivalPlanning extends TimeFunction implements ModelCycle {
	private static final String COMMENT_MARK = "//";
	/** Inner {@link es.ull.iis.util.TableCycle TableCycle} */ 
	private final TableCycle cycle;
	private final TreeMap<Double, int[]>arrivals;
	private final int berth;

	public ArrivalPlanning(int berth, TreeMap<Double, int[]>arrivals) {
		final double[] arrivalTimes = new double[arrivals.size()];
		int index = 0;
		for (Double key : arrivals.keySet()) {
			arrivalTimes[index++] = key;
		}
		this.cycle = new TableCycle(arrivalTimes);
		this.arrivals = arrivals;
		this.berth = berth;
	}

	public ArrivalPlanning(int berth, String fileName) {
		this.berth = berth;
		arrivals = new TreeMap<Double, int[]>();
		double[] arrivalTimes = null;
		BufferedReader f = null;
		int lineNumber = 0;
		try {
			f = new BufferedReader(new FileReader(fileName));
			String str = f.readLine();
			lineNumber++;
			while (str != null) {
				if (str.startsWith(COMMENT_MARK)) {
					// Just ignore it
				}
				else {
					final String[]timeAndValues = str.split("\t", 2);
					final String[]strValues = timeAndValues[1].split("\t");
					final int[]values = new int[strValues.length];
					for (int i = 0; i < strValues.length; i++) {
						values[i] = Integer.parseInt(strValues[i]);
					}
					arrivals.put(Double.parseDouble(timeAndValues[0]), values);
				}
				// Read next line
				str = f.readLine();
				lineNumber++;
			}
			arrivalTimes = new double[arrivals.size()];
			int index = 0;
			for (Double key : arrivals.keySet()) {
				arrivalTimes[index++] = key;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			System.err.println("Error parsing stowage plan file " + fileName + ". Line=" + lineNumber);
			e.printStackTrace();			
		} finally {
			if (arrivalTimes != null) {
				cycle = new TableCycle(arrivalTimes);				
			}
			else {
				cycle = null;
			}				
			try {
				if (f != null)
					f.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * @return the destinationBlocks
	 */
	public int[] getDestinationBlocks(double arrivalTime) {
		return arrivals.get(arrivalTime);
	}

	@Override
	public Cycle getCycle() {
		return cycle;
	}

	/**
	 * @return the berth
	 */
	public int getBerth() {
		return berth;
	}

	@Override
	public double getValue(TimeFunctionParams params) {
		double arrivalTime = params.getTime();
		return arrivals.get(arrivalTime).length;
	}

	@Override
	public void setParameters(Object... params) {
		// TODO Auto-generated method stub
		
	}

}
