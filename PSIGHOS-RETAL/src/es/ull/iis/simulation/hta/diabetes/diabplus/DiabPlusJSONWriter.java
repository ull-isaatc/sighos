/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.diabplus;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import es.ull.iis.simulation.hta.diabetes.DiabetesSimulation;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.AcuteComplicationCounterListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.CostListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.HbA1cListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.LYListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.QALYListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.TimeFreeOfComplicationsView;
import es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention;

/**
 * @author Iván Castilla
 *
 */
public class DiabPlusJSONWriter {
	final private static String STR_INTERVENTIONS = "interventions";
	final private static String STR_COST = "cost";
	final private static String STR_QALY = "QALY";
	final private static String STR_LY = "LY";
	final private static String STR_TIME_TO = "time_to_";
	final private static String STR_NAME = "name";
	final private static String STR_BASE = "base";
	final private static String STR_AVG = "avg";
	final private static String STR_LCI = "ci-";
	final private static String STR_UCI = "ci+";
	
	// FIXME: Necesito un array por intervención
	final private double[] cost;
	final private double[] qaly;
	final private double[] ly;
	final private ArrayList<SecondOrderDiabetesIntervention> interventions;
	
	final private JSONObject json;
	/**
	 * @param description
	 */
	public DiabPlusJSONWriter(int nRuns, ArrayList<SecondOrderDiabetesIntervention> interventions) {
		super();
		this.cost = new double[nRuns];
		this.qaly = new double[nRuns];
		this.ly = new double[nRuns];
		this.interventions = interventions;
		
		json = new JSONObject();
		json.put(STR_INTERVENTIONS, new JSONArray());
	}
	
	public void notifyEndBaseCase(DiabetesSimulation simul, HbA1cListener[] hba1cListeners, CostListener[] costListeners, LYListener[] lyListeners, QALYListener[] qalyListeners, CostListener[] costListeners0, LYListener[] lyListeners0, QALYListener[] qalyListeners0, AcuteComplicationCounterListener[] acuteListeners, TimeFreeOfComplicationsView timeFreeListener) {
		final JSONArray jintervs = json.getJSONArray(STR_INTERVENTIONS);
		
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
			jintervs.put(i, jinterv);
		}
	}
	// TODO: Terminar e invocar donde corresponda. Añadir también un método final
	public void notifyEndProbabilisticRun(DiabetesSimulation simul, HbA1cListener[] hba1cListeners, CostListener[] costListeners, LYListener[] lyListeners, QALYListener[] qalyListeners, CostListener[] costListeners0, LYListener[] lyListeners0, QALYListener[] qalyListeners0, AcuteComplicationCounterListener[] acuteListeners, TimeFreeOfComplicationsView timeFreeListener) {
		final int id = simul.getIdentifier();
		for (int i = 0; i < interventions.size(); i++) {
			cost[id] = costListeners[i].getResults()[0];
			final JSONObject jinterv = new JSONObject();
			jinterv.put(STR_NAME, interventions.get(i).getShortName());
			final JSONObject jcost = new JSONObject();
			jcost.put(STR_BASE, costListeners[i].getResults()[0]);
			final JSONObject jqaly = new JSONObject();
			jqaly.put(STR_BASE, qalyListeners[i].getResults()[0]);
			final JSONObject jly = new JSONObject();
			jly.put(STR_BASE, lyListeners[i].getResults()[0]);
		}
	}
	
	public JSONObject getJSON() {
		return json;
	}

	public static void main(String[] args) {
		System.out.println(System.getProperty("user.dir"));
//		final JSONArray jintervs = new JSONArray(2);
//		for (int i = 0; i < 2; i++) {
//			final JSONObject jcost = new JSONObject();
//			jcost.put(STR_BASE, 100);
//			final JSONObject jqaly = new JSONObject();
//			jqaly.put(STR_BASE, 34.6);
//			final JSONObject jly = new JSONObject();
//			jly.put(STR_BASE, 50.2);
//			final JSONObject jinterv = new JSONObject();
//			jinterv.put(STR_COST, jcost);
//			jinterv.put(STR_QALY, jqaly);
//			jinterv.put(STR_LY, jly);
//			jintervs.put(i, jinterv);
//		}
//		final JSONObject json = new JSONObject();
//		json.put(STR_INTERVENTIONS, jintervs);
//		try {
//			BufferedWriter writer = Files.newBufferedWriter(Paths.get("customer.json"));
//			json.write(writer);
//			writer.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}
