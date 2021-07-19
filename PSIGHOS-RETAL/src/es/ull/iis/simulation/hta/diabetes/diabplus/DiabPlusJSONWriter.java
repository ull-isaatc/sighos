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
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.submodels.SecondOrderAcuteComplicationSubmodel;
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
	final private static String STR_CHRONIC_MANIFESTATIONS = "chronic manifestations";
	final private static String STR_ACUTE_MANIFESTATIONS = "acute manifestations";	
	final private static String STR_TIME_TO = "time to event";
	final private static String STR_N_EVENTS = "number of events";
	final private static String STR_INCIDENCE = "incidence";	
	final private static String STR_NAME = "name";
	final private static String STR_BASE = "base";
	final private static String STR_AVG = "avg";
	final private static String STR_LCI = "lci" + (int)((1-PERCENT_CONFIDENCE)*100);
	final private static String STR_UCI = "uci" + (int)((1-PERCENT_CONFIDENCE)*100);
	
	final private double[][] cost;
	final private double[][] qaly;
	final private double[][] ly;
	/** Time to each chronic manifestation for those who suffer any of them */
	final private double[][][] timeToManifestations;
	/** Percentual incidence of chronic manifestations */
	final private double[][][] incidenceManifestations;
	/** Total number of acute manifestations during the lifetime of the patient */
	final private double[][][] nAcuteManifestations;
	final private SecondOrderParamsRepository secParams;
	final private ArrayList<SecondOrderDiabetesIntervention> interventions;
	final private double[] percentiles = new double[2];
	
	final private JSONObject json;
	/**
	 * @param description
	 */
	public DiabPlusJSONWriter(int nRuns, ArrayList<SecondOrderDiabetesIntervention> interventions, SecondOrderParamsRepository secParams) {
		super();
		this.interventions = interventions;
		this.secParams = secParams;
		final int n = interventions.size(); 
		this.cost = new double[n][nRuns];
		this.qaly = new double[n][nRuns];
		this.ly = new double[n][nRuns];
		this.timeToManifestations = new double[n][secParams.getRegisteredComplicationStages().size()][nRuns];
		this.incidenceManifestations = new double[n][secParams.getRegisteredComplicationStages().size()][nRuns];
		this.nAcuteManifestations = new double[n][secParams.getRegisteredAcuteComplications().length][nRuns];
		percentiles[0] = PERCENT_CONFIDENCE / 2.0;
		percentiles[1] = 1.0 - percentiles[0];
		json = new JSONObject();
		json.put(STR_INTERVENTIONS, new JSONArray());
	}
	
	public void notifyEndBaseCase(DiabetesSimulation simul, HbA1cListener[] hba1cListeners, CostListener[] costListeners, LYListener[] lyListeners, QALYListener[] qalyListeners, CostListener[] costListeners0, LYListener[] lyListeners0, QALYListener[] qalyListeners0, AcuteComplicationCounterListener[] acuteListeners, TimeFreeOfComplicationsView timeFreeListener) {
		final JSONArray jintervs = json.getJSONArray(STR_INTERVENTIONS);
		
		final double[][] timeTo = timeFreeListener.getAvgTimeToComplications();
		final double[][] incidence = timeFreeListener.getIncidence();
		for (int i = 0; i < interventions.size(); i++) {
			final JSONObject jinterv = new JSONObject();
			jinterv.put(STR_NAME, interventions.get(i).getShortName());
			// Write outcomes (cost, QALYs and LYs
			final JSONObject jcost = new JSONObject();
			jcost.put(STR_BASE, costListeners[i].getResults()[0]);
			final JSONObject jqaly = new JSONObject();
			jqaly.put(STR_BASE, qalyListeners[i].getResults()[0]);
			final JSONObject jly = new JSONObject();
			jly.put(STR_BASE, lyListeners[i].getResults()[0]);
			jinterv.put(STR_COST, jcost);
			jinterv.put(STR_QALY, jqaly);
			jinterv.put(STR_LY, jly);
			// Write time to chronic manifestations 
			final JSONArray jmanifestations = new JSONArray(); 
			ArrayList<DiabetesComplicationStage> availableHealthStates = secParams.getRegisteredComplicationStages();
			for (int j = 0; j < availableHealthStates.size(); j++) {
				final JSONObject jmanif = new JSONObject();
				jmanif.put(STR_NAME, availableHealthStates.get(j).getDescription());
				final JSONObject jtimeto = new JSONObject();
				final JSONObject jincidence = new JSONObject();
				jtimeto.put(STR_BASE, Double.isNaN(timeTo[i][j]) ? null : timeTo[i][j]);
				jincidence.put(STR_BASE, incidence[i][j]);
				jmanif.put(STR_TIME_TO, jtimeto);
				jmanif.put(STR_INCIDENCE, jincidence);
				jmanifestations.put(j, jmanif);
			}
			jinterv.put(STR_CHRONIC_MANIFESTATIONS, jmanifestations);
			// Write number of acute events
			final JSONArray jacuteComps = new JSONArray();
			SecondOrderAcuteComplicationSubmodel[] acuteComps = secParams.getRegisteredAcuteComplications();
			for (int j = 0; j < acuteComps.length; j++) {
				final JSONObject jacute = new JSONObject();
				jacute.put(STR_NAME, acuteComps[j].getComplicationType().getDescription());
				final JSONObject jnevents = new JSONObject();
				jnevents.put(STR_BASE, acuteListeners[i].getAvgNComplications()[j]);
				jacute.put(STR_N_EVENTS, jnevents);
				jacuteComps.put(j, jacute);
			}
			jinterv.put(STR_ACUTE_MANIFESTATIONS, jacuteComps);
			// Write the intervention
			jintervs.put(i, jinterv);
		}
	}
	
	public void notifyEndProbabilisticRun(DiabetesSimulation simul, HbA1cListener[] hba1cListeners, CostListener[] costListeners, LYListener[] lyListeners, QALYListener[] qalyListeners, CostListener[] costListeners0, LYListener[] lyListeners0, QALYListener[] qalyListeners0, AcuteComplicationCounterListener[] acuteListeners, TimeFreeOfComplicationsView timeFreeListener) {
		final int id = simul.getIdentifier() - 1;
		final double[][] timeTo = timeFreeListener.getAvgTimeToComplications();
		final double[][] incidence = timeFreeListener.getIncidence();
		for (int i = 0; i < interventions.size(); i++) {
			cost[i][id] = costListeners[i].getResults()[0];
			qaly[i][id] = qalyListeners[i].getResults()[0];
			ly[i][id] = lyListeners[i].getResults()[0];
			for (int j = 0; j < secParams.getRegisteredComplicationStages().size(); j++) {
				timeToManifestations[i][j][id] = timeTo[i][j];
				incidenceManifestations[i][j][id] = incidence[i][j];
			}
			double []nAcute = acuteListeners[i].getAvgNComplications();
			for (int j = 0; j < secParams.getRegisteredAcuteComplications().length; j++) {
				nAcuteManifestations[i][j][id] = nAcute[j];
			}
		}
	}
	
	private void setProbValues(JSONObject dest, double[] values) {
		double avg = Statistics.average(values);
		dest.put(STR_AVG, Double.isNaN(avg) ? null : avg);
		double[] sortedCopy = Arrays.copyOf(values, values.length);
		Arrays.sort(sortedCopy);
		double[] cis = new double[2];
		cis[0] = Statistics.percentile(sortedCopy, percentiles[0]);
		cis[1] = Statistics.percentile(sortedCopy, percentiles[1]);		
		dest.put(STR_LCI, Double.isNaN(cis[0]) ? null : cis[0]);
		dest.put(STR_UCI, Double.isNaN(cis[1]) ? null : cis[1]);
	}
	
	private void setWeightedProbValues(JSONObject dest, double[] values, double[] weights) {
		double totalWeight = 0.0;
		double avg = 0.0;
		for (int i = 0; i < values.length; i++) {
			avg += values[i] * weights[i];
			totalWeight += weights[i];
		}
		avg = avg / totalWeight;
		dest.put(STR_AVG, Double.isNaN(avg) ? null : avg);
		// TODO: Compute CIs more precisely
		double[] sortedCopy = Arrays.copyOf(values, values.length);
		Arrays.sort(sortedCopy);
		double[] cis = new double[2];
		cis[0] = Statistics.percentile(sortedCopy, percentiles[0]);
		cis[1] = Statistics.percentile(sortedCopy, percentiles[1]);		
		dest.put(STR_LCI, Double.isNaN(cis[0]) ? null : cis[0]);
		dest.put(STR_UCI, Double.isNaN(cis[1]) ? null : cis[1]);
	}
	
	public void notifyEndProbabilisticExperiments() {
		
		for (int i = 0; i < interventions.size(); i++) {
			final JSONObject jinterv = json.getJSONArray(STR_INTERVENTIONS).getJSONObject(i);
			setProbValues(jinterv.getJSONObject(STR_COST), cost[i]);
			setProbValues(jinterv.getJSONObject(STR_QALY), qaly[i]);
			setProbValues(jinterv.getJSONObject(STR_LY), ly[i]);
			for (int j = 0; j < secParams.getRegisteredComplicationStages().size(); j++) {
				final JSONObject jchronic = jinterv.getJSONArray(STR_CHRONIC_MANIFESTATIONS).getJSONObject(j);
				setWeightedProbValues(jchronic.getJSONObject(STR_TIME_TO), timeToManifestations[i][j], incidenceManifestations[i][j]);
				setProbValues(jchronic.getJSONObject(STR_INCIDENCE), incidenceManifestations[i][j]);
			}
			for (int j = 0; j < secParams.getRegisteredAcuteComplications().length; j++) {
				setProbValues(jinterv.getJSONArray(STR_ACUTE_MANIFESTATIONS).getJSONObject(j).getJSONObject(STR_N_EVENTS), nAcuteManifestations[i][j]);
			}
		}
	}
	
	public JSONObject getJSON() {
		return json;
	}
}
