/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.diabplus;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import es.ull.iis.util.Statistics;

/**
 * @author Iván Castilla
 *
 */
public class PostProcessor {
	private final static String ITEM_SEP = "\t";
	private final static String STR_SEP = "_";
	private final static String STR_SIM = "SIM";
	private final static String STR_AGE = "AGE";
	private final static String STR_DURATION = "DURATION";
	private final static String STR_HBA1C = "HBA1C";
	private final static String STR_AVG = "AVG";
	private final static String STR_L95CI = "L95CI";
	private final static String STR_U95CI = "U95CI";
	private final static String STR_N = "N";
	private final static String STR_PREV = "PREV";
	private final static String STR_INC = "INC";
	private final static String STR_TIME_TO = "TIMETO";
	/** Lines to skip to get to the results' header */
	private static final int N_SKIP_LINES = 23;
	/** Columns to skip to get to the results */
	private static final int N_SKIP_COLS = 3;
	
	private PrintWriter out;
	private BufferedReader in;
	private final int nExp;
	private final TreeMap<String, ExperimentItem> items;
	private final TreeMap<Integer, ExperimentItem> orderedItems;
	private final ArrayList<String> orderedAgeDurations;
	private final TreeSet<String> ageDurations;

	public PostProcessor(String inputFileName, String outputFileName, int nExp) {
		this.nExp = nExp;
		items = new TreeMap<>();
		orderedItems = new TreeMap<>();
		orderedAgeDurations = new ArrayList<>();
		ageDurations = new TreeSet<>();
		try {
			in = new BufferedReader(new FileReader(inputFileName));
			out = new PrintWriter(outputFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void processHeaders(String[] headers) {
		// Here it should start the first result column
		int index = N_SKIP_COLS;
		while (index < headers.length) {
			final String[] parsedToken = headers[index].split(STR_SEP);
			// If this is an "average of something" column
			if (STR_AVG.equals(parsedToken[0])) {
				// Timeto columns are different from other average columns
				if (STR_TIME_TO.equals(parsedToken[1])) {
					final String id = parsedToken[1] + STR_SEP + parsedToken[2];
					ExperimentItem exp = items.get(id);
					if (exp == null) {
						exp = new ExperimentItem(id, ExperimentItem.Type.TIMETO);
						items.put(id, exp);
						orderedItems.put(index, exp);
					}
					exp.addHbA1cLevel(parsedToken[3], index);
				}
				else {
					final String id = parsedToken[1];
					ExperimentItem exp = items.get(id);
					if (exp == null) {
						exp = new ExperimentItem(id, ExperimentItem.Type.AVG);
						items.put(id, exp);
						orderedItems.put(index, exp);
					}
					exp.addHbA1cLevel(parsedToken[3], index);
					
					index += 2;
				}
			}
			else if (STR_N.equals(parsedToken[0])) {
				final String id = parsedToken[1] + STR_SEP + parsedToken[4] + STR_SEP + parsedToken[5];
				ExperimentItem exp = items.get(id);
				if (exp == null) {
					exp = new ExperimentItem(id, ExperimentItem.Type.N);
					items.put(id, exp);
					orderedItems.put(index, exp);
				}
				exp.addHbA1cLevel(parsedToken[3], index);
				
			}
			else if (STR_PREV.equals(parsedToken[0]) || STR_INC.equals(parsedToken[0])) {
				final String id = parsedToken[0] + STR_SEP + parsedToken[1];
				ExperimentItem exp = items.get(id);
				if (exp == null) {
					exp = new ExperimentItem(id, ExperimentItem.Type.AVG);
					items.put(id, exp);
					orderedItems.put(index, exp);
				}
				exp.addHbA1cLevel(parsedToken[3], index);					
			}
			index++;
		}		
	}
	
	public void readFile() {
		try {
			// Skip general info
			for (int i = 0; i < N_SKIP_LINES; i++) {
				in.readLine();
			}
			// Read first header and create basic structures
			processHeaders(in.readLine().split(ITEM_SEP));
			String line = in.readLine();
			while (line != null) {
				String[] values = line.split(ITEM_SEP);
				if (!STR_SIM.equals(values[0])) {
					final String key = values[1] + ITEM_SEP + values[2];
					if (!ageDurations.contains(key)) {
						ageDurations.add(key);
						orderedAgeDurations.add(key);
					}
					for (ExperimentItem exp : items.values()) {
						exp.addValues(key, values);
					}
				}
				line = in.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeFile() {
		// Write the header
		out.write(STR_AGE + ITEM_SEP + STR_DURATION + ITEM_SEP + STR_HBA1C + ITEM_SEP);
		for (ExperimentItem item : orderedItems.values()) {
			out.write(item.getHeader());
		}
		out.write(System.lineSeparator());
		for (String keyAgeDur : orderedAgeDurations) {
			out.write(keyAgeDur + ITEM_SEP);
			for (ExperimentItem item : orderedItems.values()) {
				// TODO: Conseguir imprimir las cosas en orden. Seguramente requiera calcular previamente un array de resultados (incluso el tamaño de ese array mientras se construye) 
			}
			out.write(System.lineSeparator());
		}
			
		out.flush();
		out.close();
	}

	private class HbA1cLevelItem {
		private final int srcCol;
		private final TreeMap<String, double[]> values;
		private final TreeMap<String, Integer> counters;

		public HbA1cLevelItem(int srcCol) {
			this.srcCol = srcCol;
			values = new TreeMap<>();
			counters = new TreeMap<>();
		}
		
		public void addValue(String key, String[] line) {
			if (!values.containsKey(key)) {
				values.put(key,  new double[nExp]);
				counters.put(key, 0);
			}
			values.get(key)[counters.get(key)] = Double.parseDouble(line[srcCol]);
			counters.put(key, counters.get(key) + 1);
		}
		
		public double[] getValues(String key) {
			return values.get(key);
		}
		
	}
	private class ExperimentItem {
		public enum Type {
			AVG,
			N,
			TIMETO
		}
		private final TreeMap<String, HbA1cLevelItem> hba1cLevels;
		private final Type type;
		private final String name;
		
		public ExperimentItem(String name, Type type) {
			this.type = type;
			this.name = name;
			hba1cLevels = new TreeMap<>();
		}
		
		public void addHbA1cLevel(String level, int srcCol) {
			hba1cLevels.put(level, new HbA1cLevelItem(srcCol));
		}
		
		public void addValues(String key, String[] line) {
			for (HbA1cLevelItem item : hba1cLevels.values()) {
				item.addValue(key, line);
			}
		}
		public double getValue(String keyAgeDur, String hba1cLevel) {
			return Statistics.average(hba1cLevels.get(hba1cLevel).getValues(keyAgeDur));
		}

		public String getHeader() {
			String str = STR_SEP + name + ITEM_SEP;
			switch (type) {
			case AVG:
			case N:
			case TIMETO:
				return STR_AVG + str + STR_L95CI + str + STR_U95CI + str;
			default:
				return "";
			
			}
		}
		
		public void print(String keyAgeDur, String hba1cLevel) {
			System.out.println("Values for " + keyAgeDur + ITEM_SEP + hba1cLevel);
			double[] values = hba1cLevels.get(hba1cLevel).getValues(keyAgeDur);
			for (double val : values) {
				System.out.println(val);
			}			
		}
	}
	
	public static void main(String[] args) {
//		Scanner scan = new Scanner("AVG_C_DIAB+EXPLORE_6.0\tL95CI_C_DIAB+EXPLORE_6.0");
//		scan.useDelimiter("\t");
//		String token = scan.next();
//		final String[] parsedToken = token.split("_");
//		if (STR_AVG.equals(parsedToken[0])) {
//			if (STR_COST.equals(parsedToken[1])) {
//				System.out.println("FOUND");
//			}
//			else {
//				System.out.println("NOT FOUND " + token);
//			}
//		}
//		token = scan.next();
//		if (STR_AVG.equals(token.substring(0, STR_AVG.length()))) {
//			System.out.println("FOUND");
//		}
//		else {
//			System.out.println("NOT FOUND " + token);
//			
//		}
		final PostProcessor proc = new PostProcessor(System.getProperty("user.home") + "\\Downloads\\results.txt", System.getProperty("user.home") + "\\Downloads\\post.txt", 11);
		proc.readFile();
		proc.writeFile();
		
	}
}
