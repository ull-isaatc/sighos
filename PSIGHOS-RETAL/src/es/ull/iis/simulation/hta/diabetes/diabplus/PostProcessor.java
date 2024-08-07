/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.diabplus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams.Sex;
import es.ull.iis.util.Statistics;

/**
 * Includes the methods required to postprocess the resulting file of a simulation, as generated by {@link DiabPlusExplorationMain}. Such a file normally contains a few descriptive
 * lines with the default values for most parameters (that must be ignored by adequately configuring {@link #N_SKIP_LINES} and then headers + data. 
 * 
 * The original simulations create an experiment that generates "m" patients with the same age and duration of diabetes, and each experiment is repeated "n" times (or "runs") by using 
 * the second order uncertainty of the model parameters. Each experiment includes its own header row, and for each run, the same patient is tested for different initial levels of HbA1c.
 * For each data row, the first three columns (as defined in {@link #N_SKIP_COLS}) are the "run" id, the age, and the duration.  
 *     
 * The resulting file is intended to create a row per each <age, duration, HbA1c level>, by aggregating the results from all the "runs". Therefore, a kind of transposition of
 * data is required. To do so, the "postprocessor" goes through the header, to identify which HbA1c levels are being simulated; and then through the different experiments. 
 * @author Iv�n Castilla
 *
 */
public class PostProcessor {
	private static final String FILE_SUFFIX = ".txt";
	private final static String STR_FILE_FILTER1 = "res_";
	private final static String STR_FILE_FILTER2 = "_[MW].*" + FILE_SUFFIX;
	private final static String STR_FILE_FILTER2_SUMMARY = "_SUMMARY" + FILE_SUFFIX;
	private final static String STR_FILE_OUTPUT_PREFIX = "out_";
	private final static String ITEM_SEP = "\t";
	private final static String STR_SEP = "_";
	private final static String STR_SIM = "SIM";
	private final static String STR_AGE = "AGE";
	private final static String STR_DURATION = "DURATION";
	private final static String STR_HYPO_RATE = "HYPO_RATE";	
	private final static String STR_SEX = "SEX";
	private final static String STR_HBA1C = "HBA1C";
	private final static String STR_AVG = "AVG";
	private final static String STR_L95CI = "L95CI";
	private final static String STR_U95CI = "U95CI";
	private final static String STR_N = "N";
	private final static String STR_PREV = "PREV";
	private final static String STR_INC = "INC";
	private final static String STR_TIME_TO = "TIMETO";
	/** Columns to skip to get to the results */
	private static final int N_SKIP_COLS = 3;
	
	private final TreeMap<String, ExperimentItem> items;
	private final TreeMap<Integer, ExperimentItem> orderedItems;
	private final TreeMap<String, ExperimentItem> expByManifestation;
	private final SummaryParser summary;

	public PostProcessor(SummaryParser summary) {
		this.summary = summary;
		items = new TreeMap<>();
		orderedItems = new TreeMap<>();
		expByManifestation = new TreeMap<>(); 
	}
	
	/**
	 * Adds an identified manifestation to be able to use its incidence to compute weighted averages and quantiles
	 * @param manifName Name of the manifestation
	 * @param exp Experiment item that contains the information regarding the incidence of the manifestation
	 */
	private void addManifestation(String manifName, ExperimentItem exp) {
		final ExperimentItem incidenceExp = items.get(STR_INC + STR_SEP + manifName);
		expByManifestation.put(exp.name, incidenceExp);
	}
	/**
	 * Processes the headers of the original file to create the structure for results.
	 * @param headers Headers of the original results
	 */
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
					final String hba1cLevel = parsedToken[4];
					final ExperimentItem exp = addExperiment(id, index, ExperimentItem.Type.TIMETO, hba1cLevel);
					addManifestation(parsedToken[2], exp);
				}
				else {
					final String id = parsedToken[1];
					final String hba1cLevel = parsedToken[3];
					addExperiment(id, index, ExperimentItem.Type.AVG, hba1cLevel);				
					index += 2;
				}
			}
			else if (STR_N.equals(parsedToken[0])) {
				final String id = parsedToken[1] + STR_SEP + parsedToken[4] + STR_SEP + parsedToken[5];
				final String hba1cLevel = parsedToken[3];
				addExperiment(id, index, ExperimentItem.Type.N, hba1cLevel);				
			}
			else if (STR_PREV.equals(parsedToken[0]) || STR_INC.equals(parsedToken[0])) {
				final String id = parsedToken[0] + STR_SEP + parsedToken[1];
				final String hba1cLevel = parsedToken[3];
				addExperiment(id, index, ExperimentItem.Type.AVG, hba1cLevel);				
			}
			index++;
		}		
	}
	
	private ExperimentItem addExperiment(String id, int index, ExperimentItem.Type type, String hba1cLevel) {
		ExperimentItem exp = items.get(id);
		if (exp == null) {
			exp = new ExperimentItem(id, type);
			items.put(id, exp);
			orderedItems.put(index, exp);
		}
		exp.addHbA1cLevel(hba1cLevel, index);
		return exp;
	}
	
	public ArrayList<ExperimentSet> readFile(File inputFile) {
		final ArrayList<ExperimentSet> experiments = new ArrayList<>();
		try {
			final BufferedReader in = new BufferedReader(new FileReader(inputFile));
			// Skip general info
			ExperimentSet f = new ExperimentSet(in.readLine(), summary);
			experiments.add(f);
			// Read first header and create basic structures
			processHeaders(in.readLine().split(ITEM_SEP));
			String line = in.readLine();
			while (line != null) {
				if (line.contains("EXPERIMENT")) {
					f = new ExperimentSet(line, summary);
					experiments.add(f);
				}
				else {
					String[] values = line.split(ITEM_SEP);
					if (!STR_SIM.equals(values[0])) {
						for (ExperimentItem exp : items.values()) {
							exp.addValues(f.toString(), values);
						}
					}
				}
				line = in.readLine();
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return experiments;
	}
	
	public void writeFile(String outputFileName, ArrayList<ExperimentSet> experiments) {
		PrintWriter out;
		try {
			out = new PrintWriter(outputFileName);
			// Write the header
			out.write(ExperimentSet.getHeader(summary));
			out.write(STR_HBA1C + ITEM_SEP);
			for (ExperimentItem item : orderedItems.values()) {
				out.write(item.getHeader());
			}
			out.write(System.lineSeparator());
			for (ExperimentSet exp : experiments) {
				for (String hba1cLevel : summary.getHba1cLevels()) {
					out.write(exp + hba1cLevel + ITEM_SEP);
					for (ExperimentItem item : orderedItems.values()) {
						final double[] results = item.getValues(exp.toString(), hba1cLevel);
						for (double val : results)
							out.write(val + ITEM_SEP);
					}
					out.write(System.lineSeparator());
				}
			}
				
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				values.put(key,  new double[summary.getnRuns()]);
				counters.put(key, 0);
			}
			values.get(key)[counters.get(key)] = Double.parseDouble(line[srcCol]);
			counters.put(key, counters.get(key) + 1);
		}
		
		public double[] getValues(String key) {
			return values.get(key);
		}
		
	}
	
	/**
	 * An information piece of an experiment; represents the type of information, the name of the item, and the set of values for each HbA1c level
	 * @author Iv�n Castilla
	 *
	 */
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
		public double[] getValues(String keyAgeDur, String hba1cLevel) {
			switch (type) {
			case AVG:
			case N:
				final double avg = Statistics.average(hba1cLevels.get(hba1cLevel).getValues(keyAgeDur));
				final double[] ci = Statistics.getPercentile95CI(hba1cLevels.get(hba1cLevel).getValues(keyAgeDur));
				return new double[] {avg, ci[0], ci[1]};
			case TIMETO:
				final ExperimentItem incidenceExperiment = expByManifestation.get(this.name);
				return new double[] {Statistics.weightedAverage(incidenceExperiment.hba1cLevels.get(hba1cLevel).getValues(keyAgeDur), hba1cLevels.get(hba1cLevel).getValues(keyAgeDur)), 
						Statistics.weightedPercentile(incidenceExperiment.hba1cLevels.get(hba1cLevel).getValues(keyAgeDur), hba1cLevels.get(hba1cLevel).getValues(keyAgeDur), 0.025, false), 
						Statistics.weightedPercentile(incidenceExperiment.hba1cLevels.get(hba1cLevel).getValues(keyAgeDur), hba1cLevels.get(hba1cLevel).getValues(keyAgeDur), 0.975, false)};
			default:
				return null;
			
			}
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
		
		@SuppressWarnings("unused")
		public void print(String keyAgeDur, String hba1cLevel) {
			System.out.println("Values for " + keyAgeDur + ITEM_SEP + hba1cLevel);
			double[] values = hba1cLevels.get(hba1cLevel).getValues(keyAgeDur);
			for (double val : values) {
				System.out.println(val);
			}			
		}
	}
	
	private class ExperimentSet {
		private Sex sex;
		private double age;
		private double duration;
		private double hypoRate;
		private final TreeSet<String> initManif; 
		private final SummaryParser summary;
		
		public ExperimentSet(String title, SummaryParser summary) {
			initManif = new TreeSet<>();
			this.summary = summary;
			final String []items = title.split(ITEM_SEP);
			for (String item : items) {
				final String [] key_value = item.split("=");
				if ("EXPERIMENT FOR SEX".equals(key_value[0]))
					this.sex = Sex.valueOf(key_value[1]);
				else if (STR_AGE.equals(key_value[0]))
					this.age = Double.parseDouble(key_value[1]);
				else if (STR_DURATION.equals(key_value[0]))
					this.duration = Double.parseDouble(key_value[1]);
				else if (STR_HYPO_RATE.equals(key_value[0]))
					this.hypoRate = Double.parseDouble(key_value[1]);
				else if ("INIT_MANIF".equals(key_value[0])) {
					if (key_value.length > 1) {
						String []manifs = key_value[1].split(":");
						for (String manif : manifs)
							this.initManif.add(manif);
					}
				}
			}
		}
		
		public static String getHeader(SummaryParser summary) {
			String str = STR_SEX + ITEM_SEP + STR_AGE + ITEM_SEP + STR_DURATION + ITEM_SEP + 
					STR_HYPO_RATE + ITEM_SEP;
			final String[] chronicComplications = summary.getChronicCompNames();
			for (String comp : chronicComplications)
				str += comp + ITEM_SEP;
			return str;
		}
		
		@Override
		public String toString() {
			String str = sex + ITEM_SEP + age + ITEM_SEP + duration + ITEM_SEP + hypoRate + ITEM_SEP; 
			final String[] chronicComplications = summary.getChronicCompNames();
			for (String comp : chronicComplications)
				str += (initManif.contains(comp) ? 1 : 0) + ITEM_SEP;
			return str;
		}
	}
	
	private static class SummaryParser {
		private static final String STR_NRUNS = "N_RUNS:"; 
		private int nRuns;
		private BufferedReader inputSummary;
		private String[] hba1cLevels;
		private String[] chronicCompNames;
		private String[] acuteCompNames;
		
		public SummaryParser(String folderName, String expId) {
			final String fileName = folderName + STR_FILE_FILTER1 + expId + STR_FILE_FILTER2_SUMMARY;
			try {
				inputSummary = new BufferedReader(new FileReader(fileName));
				String[] line;
				// Skip general info
				do {
					line = inputSummary.readLine().split(ITEM_SEP);
					if (STR_NRUNS.equals(line[0]))
						nRuns = Integer.parseInt(line[1]) + 1;
				} while (!"".equals(line[0]));
				// Assumed next line contains tab separated chronic complication stages
				chronicCompNames = inputSummary.readLine().split(ITEM_SEP);
				// Assumed next line contains tab separated acute complications
				acuteCompNames = inputSummary.readLine().split(ITEM_SEP);
				// Assumed next line contains tab separated HbA1c levels
				hba1cLevels = inputSummary.readLine().split(ITEM_SEP);
			} catch (IOException e) {
				e.printStackTrace();
			}	finally {
				try {
					inputSummary.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * @return the nRuns
		 */
		public int getnRuns() {
			return nRuns;
		}

		/**
		 * @return the hba1cLevels
		 */
		public String[] getHba1cLevels() {
			return hba1cLevels;
		}

		/**
		 * @return the chronicCompNames
		 */
		public String[] getChronicCompNames() {
			return chronicCompNames;
		}

		/**
		 * @return the acuteCompNames
		 */
		public String[] getAcuteCompNames() {
			return acuteCompNames;
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
//		double[] w = new double[] {1, 2, 3, 4};
//		double[] v = new double[] {20, 10, 5, 2};
//		System.out.println(Statistics.weightedAverage(w, v));
		final String folderName = System.getProperty("user.home") +"\\Downloads\\test\\";
		final String expId = "20230314";
		final File folder = new File(folderName);
		final File[] files = folder.listFiles(new FilenameFilter() {			
			@Override
			public boolean accept(File dir, String name) {
				return name.matches(STR_FILE_FILTER1 + expId + STR_FILE_FILTER2);
			}
		});
		SummaryParser summary = new SummaryParser(folderName, expId);
		for (File inputFile : files) {
			final PostProcessor proc = new PostProcessor(summary);
			final ArrayList<ExperimentSet> experiments = proc.readFile(inputFile);
			proc.writeFile(folderName + STR_FILE_OUTPUT_PREFIX + inputFile.getName(), experiments);
		}
		
	}
}
