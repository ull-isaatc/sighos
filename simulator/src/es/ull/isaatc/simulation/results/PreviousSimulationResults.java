/**
 * 
 */
package es.ull.isaatc.simulation.results;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class PreviousSimulationResults extends SimulationResults {
	protected int idExperiment;
	protected String baseDirName = "";

	public PreviousSimulationResults(double start, double end, int idExperiment, String baseDirName) {
		super();
		this.simStart = start;
		this.simEnd = end;
		this.idExperiment = idExperiment;
		this.baseDirName = baseDirName;
		createContents();
	}
	
	public PreviousSimulationResults(double start, double end, int idExperiment) {
		super();
		this.simStart = start;
		this.simEnd = end;
		this.idExperiment = idExperiment;
		createContents();
	}
	
	void createContents() {
		try {
			BufferedReader fin;
			String linea;
			fin = new BufferedReader(new FileReader(baseDirName + "queue_" + simStart + "_" + simEnd + "_" + idExperiment + ".txt"));
            while ((linea = fin.readLine()) != null) {
                int actId = Integer.parseInt(linea.substring(0, linea.indexOf('\t')));
                linea = linea.substring(linea.indexOf('\t') + 1);
                int elemId = Integer.parseInt(linea.substring(0, linea.indexOf('\t')));
                int flowId = Integer.parseInt(linea.substring(linea.indexOf('\t') + 1));
            	ActivityStatistics as = new ActivityStatistics(actId, flowId, elemId);
            	add(as);
            }
			fin = new BufferedReader(new FileReader(baseDirName + "pflows_" + simStart + "_" + simEnd + "_" + idExperiment + ".txt"));
			linea = fin.readLine();
			firstElementId = Integer.parseInt(linea.substring(0, linea.indexOf('\t')));
			lastElementId = Integer.parseInt(linea.substring(linea.indexOf('\t') + 1));
            while ((linea = fin.readLine()) != null) {
                int elemId = Integer.parseInt(linea.substring(0, linea.indexOf('\t')));
                linea = linea.substring(linea.indexOf('\t') + 1);
                int type = Integer.parseInt(linea.substring(0, linea.indexOf('\t')));
                int value = Integer.parseInt(linea.substring(linea.indexOf('\t') + 1));
                PendingFlowStatistics pfs = new PendingFlowStatistics(elemId, type, value);
            	add(pfs);
            }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
