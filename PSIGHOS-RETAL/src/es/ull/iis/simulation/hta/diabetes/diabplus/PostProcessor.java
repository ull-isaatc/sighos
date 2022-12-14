/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.diabplus;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * @author Iván Castilla
 *
 */
public class PostProcessor {
	private final static String ITEM_SEP = "\t";
	private final static String STR_SEP = "_";
	private final static String STR_INTERVENTION = "DIAB\\+EXPLORE_";
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
	private final static String STR_UP_TO = "UPTO";
	private final static String STR_COST = "C";
	private final static String STR_LY = "LY";
	private final static String STR_QALY = "QALY";
	/** Lines to skip to get to the results' header */
	private static final int N_SKIP_LINES = 23;
	/** Columns to skip to get to the results */
	private static final int N_SKIP_COLS = 6;
	
	private PrintWriter out;
	private BufferedReader in;
	private final int nExp;
	private final TreeMap<String, TreeMap<String, double[]>> avgMeasures;
	private final TreeMap<String, ExperimentItems> items;
	private final TreeMap<Integer, ExperimentItems> orderedItems;

	public PostProcessor(String inputFileName, String outputFileName, int nExp) {
		this.nExp = nExp;
		avgMeasures = new TreeMap<>();
		items = new TreeMap<>();
		orderedItems = new TreeMap<>();
		try {
			in = new BufferedReader(new FileReader(inputFileName));
			out = new PrintWriter(outputFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private String createIdentifier(String age, String duration, String hba1c) {
		return age + "_" + duration + "_" + hba1c;
	}
	
	private void error(String message) {
		System.err.println("Error parseando " + message);
		System.exit(-1);
	}

	private String checkedRead(Scanner scan, String reference) {
		String token = "";
		try {
			token = scan.next(reference);
		} catch(NoSuchElementException ex) {
			error(ex.getMessage());
		}
		return token;
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
				}
				else {
					final String id = parsedToken[1];
					ExperimentItems exp = items.get(id);
					if (exp == null) {
						exp = new ExperimentItems(id, ExperimentItems.Type.AVG);
						items.put(id, exp);
						orderedItems.put(index, exp);
					}
					exp.addHbA1cLevel(parsedToken[3], index);
					avgMeasures.put(parsedToken[1], new TreeMap<>());
					
					index += 2;
				}
			}
			else if (STR_N.equals(parsedToken[0])) {
				final String id = parsedToken[1] + STR_SEP + parsedToken[4] + STR_SEP + parsedToken[5];
				ExperimentItems exp = items.get(id);
				if (exp == null) {
					exp = new ExperimentItems(id, ExperimentItems.Type.N);
					items.put(id, exp);
					orderedItems.put(index, exp);
				}
				exp.addHbA1cLevel(parsedToken[3], index);
				
			}
			else if (STR_PREV.equals(parsedToken[0]) || STR_INC.equals(parsedToken[0])) {
				final String id = parsedToken[0] + STR_SEP + parsedToken[1];
				ExperimentItems exp = items.get(id);
				if (exp == null) {
					exp = new ExperimentItems(id, ExperimentItems.Type.AVG);
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
					String id = values[1] + ITEM_SEP + values[2] + ITEM_SEP;
					// Por aquí
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
		for (ExperimentItems item : orderedItems.values()) {
			out.write(item.getHeader());
		}
		out.flush();
		out.close();
	}

	private class ExperimentItems {
		public enum Type {
			AVG,
			N,
			TIMETO
		}
		private final TreeMap<String, Integer> srcColPerHbA1c;
		private final Type type;
		private final String name;
		private final TreeMap<String, double[]> values;
		
		public ExperimentItems(String name, Type type) {
			this.type = type;
			this.name = name;
			this.values = new TreeMap<>();
			srcColPerHbA1c = new TreeMap<>();
		}
		
		public void addHbA1cLevel(String level, int srcCol) {
			srcColPerHbA1c.put(level, srcCol);
		}

		public String getHeader() {
			String str = STR_SEP + name + ITEM_SEP;
			switch (type) {
			case AVG:
			case N:
				return STR_AVG + str + STR_L95CI + str + STR_U95CI + str;
			case TIMETO:
				return "";
			default:
				return "";
			
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
		final PostProcessor proc = new PostProcessor(System.getProperty("user.home") + "\\Downloads\\results.txt", System.getProperty("user.home") + "\\Downloads\\post.txt", 100);
		proc.readFile();
		proc.writeFile();
	}
}
