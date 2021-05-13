/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.diabplus;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.DiabetesSimulation;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.AcuteComplicationCounterListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.CostListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.HbA1cListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.LYListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.QALYListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.TimeFreeOfComplicationsView;
import es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention;
import es.ull.iis.util.Statistics;

/**
 * @author Iván Castilla
 *
 */
public class DiabPlusJSONWriter {
	final private static double PERCENT_CONFIDENCE = 0.05; 
	final private static String STR_INTERVENTIONS = "interventions";
	final private static String STR_COST = "cost";
	final private static String STR_QALY = "QALY";
	final private static String STR_LY = "LY";
	final private static String STR_TIME_TO = "time to manifestations";
	final private static String STR_NAME = "name";
	final private static String STR_BASE = "base";
	final private static String STR_AVG = "avg";
	final private static String STR_LCI = "lci" + (int)((1-PERCENT_CONFIDENCE)*100);
	final private static String STR_UCI = "uci" + (int)((1-PERCENT_CONFIDENCE)*100);
	
	final private double[][] cost;
	final private double[][] qaly;
	final private double[][] ly;
	final private double[][][] timeToManifestations;
	final private ArrayList<SecondOrderDiabetesIntervention> interventions;
	final private ArrayList<DiabetesComplicationStage> availableHealthStates;
	final private double[] percentiles = new double[2];
	
	final private JSONObject json;
	/**
	 * @param description
	 */
	public DiabPlusJSONWriter(int nRuns, ArrayList<SecondOrderDiabetesIntervention> interventions, ArrayList<DiabetesComplicationStage> availableHealthStates) {
		super();
		this.interventions = interventions;
		this.availableHealthStates = availableHealthStates;
		final int n = interventions.size(); 
		this.cost = new double[n][nRuns];
		this.qaly = new double[n][nRuns];
		this.ly = new double[n][nRuns];
		this.timeToManifestations = new double[n][availableHealthStates.size()][nRuns];
		percentiles[0] = PERCENT_CONFIDENCE / 2.0;
		percentiles[1] = 1.0 - percentiles[0];
		json = new JSONObject();
		json.put(STR_INTERVENTIONS, new JSONArray());
	}
	
	public void notifyEndBaseCase(DiabetesSimulation simul, HbA1cListener[] hba1cListeners, CostListener[] costListeners, LYListener[] lyListeners, QALYListener[] qalyListeners, CostListener[] costListeners0, LYListener[] lyListeners0, QALYListener[] qalyListeners0, AcuteComplicationCounterListener[] acuteListeners, TimeFreeOfComplicationsView timeFreeListener) {
		final JSONArray jintervs = json.getJSONArray(STR_INTERVENTIONS);
		
		double[][] timeTo = timeFreeListener.getAvgTimeToComplications();
		for (int i = 0; i < interventions.size(); i++) {
			final JSONObject jinterv = new JSONObject();
			jinterv.put(STR_NAME, interventions.get(i).getShortName());
			final JSONObject jcost = new JSONObject();
			jcost.put(STR_BASE, costListeners[i].getResults()[0]);
			final JSONObject jqaly = new JSONObject();
			jqaly.put(STR_BASE, qalyListeners[i].getResults()[0]);
			final JSONObject jly = new JSONObject();
			jly.put(STR_BASE, lyListeners[i].getResults()[0]);
			jinterv.put(STR_COST, jcost);
			jinterv.put(STR_QALY, jqaly);
			jinterv.put(STR_LY, jly);
			final JSONArray jmanifestations = new JSONArray(); 
			for (int j = 0; j < availableHealthStates.size(); j++) {
				final JSONObject jmanif = new JSONObject();
				jmanif.put(STR_NAME, availableHealthStates.get(j).getDescription());
				jmanif.put(STR_BASE, timeTo[i][j]);
				jmanifestations.put(j, jmanif);
			}
			jinterv.put(STR_TIME_TO, jmanifestations);
			jintervs.put(i, jinterv);
		}
	}
	
	public void notifyEndProbabilisticRun(DiabetesSimulation simul, HbA1cListener[] hba1cListeners, CostListener[] costListeners, LYListener[] lyListeners, QALYListener[] qalyListeners, CostListener[] costListeners0, LYListener[] lyListeners0, QALYListener[] qalyListeners0, AcuteComplicationCounterListener[] acuteListeners, TimeFreeOfComplicationsView timeFreeListener) {
		final int id = simul.getIdentifier() - 1;
		double[][] timeTo = timeFreeListener.getAvgTimeToComplications();
		for (int i = 0; i < interventions.size(); i++) {
			cost[i][id] = costListeners[i].getResults()[0];
			qaly[i][id] = qalyListeners[i].getResults()[0];
			ly[i][id] = lyListeners[i].getResults()[0];
			for (int j = 0; j < availableHealthStates.size(); j++) {
				timeToManifestations[i][j][id] = timeTo[i][j];				
			}
		}
	}
	
	private void setProbValues(JSONObject dest, double[] values) {
		double avg = Statistics.average(values);
		dest.put(STR_AVG, avg);
		double[] sortedCopy = Arrays.copyOf(values, values.length);
		Arrays.sort(sortedCopy);
		dest.put(STR_LCI, Statistics.percentile(sortedCopy, percentiles[0]));
		dest.put(STR_UCI, Statistics.percentile(sortedCopy, percentiles[1]));
	}
	
	public void notifyEndProbabilisticExperiments() {
		
		for (int i = 0; i < interventions.size(); i++) {
			final JSONObject jinterv = json.getJSONArray(STR_INTERVENTIONS).getJSONObject(i);
			setProbValues(jinterv.getJSONObject(STR_COST), cost[i]);
			setProbValues(jinterv.getJSONObject(STR_QALY), qaly[i]);
			setProbValues(jinterv.getJSONObject(STR_LY), ly[i]);
			for (int j = 0; j < availableHealthStates.size(); j++)
				setProbValues(jinterv.getJSONArray(STR_TIME_TO).getJSONObject(j), timeToManifestations[i][j]);
		}
	}
	
	public JSONObject getJSON() {
		return json;
	}

	public static void main(String[] args) {
		System.out.println(System.getProperty("user.dir"));
//		final int n = 100;
//		final double[] ar = new double[n];
//		for (int i = 0; i < n; i++)
//			ar[i] = i * 0.01;
//		System.out.println(Statistics.percentile(ar, 0.025));
//		System.out.println(Statistics.percentile(ar, 0.99));
	}
}
