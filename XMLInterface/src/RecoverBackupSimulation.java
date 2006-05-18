import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import es.ull.cyc.simulation.results.ActivityStatistics;
import es.ull.cyc.simulation.results.PendingFlowStatistics;
import es.ull.cyc.simulation.results.SimulationResults;

/**
 * 
 * @author Roberto Muñoz
 */
public class RecoverBackupSimulation extends SimulationResults {
	int idExperiment;
	String resDir;
	
	RecoverBackupSimulation(double start, double end, int idExperiment, String resDir) {
		super();
		this.simStart = start;
		this.simEnd = end;
		this.idExperiment = idExperiment;
		this.resDir = resDir;
		createContents();
	}
	
	void createContents() {
		try {
			BufferedReader fin;
			String linea;
			fin = new BufferedReader(new FileReader(resDir + "\\queue_" + simStart + "_" + simEnd + "_" + idExperiment + ".txt"));
            while ((linea = fin.readLine()) != null) {
                int actId = Integer.parseInt(linea.substring(0, linea.indexOf('\t')));
                linea = linea.substring(linea.indexOf('\t') + 1);
                int elemId = Integer.parseInt(linea.substring(0, linea.indexOf('\t')));
                int flowId = Integer.parseInt(linea.substring(linea.indexOf('\t') + 1));
            	ActivityStatistics as = new ActivityStatistics(actId, flowId, elemId);
            	add(as);
            }
			fin = new BufferedReader(new FileReader(resDir + "\\pflows_" + simStart + "_" + simEnd + "_" + idExperiment + ".txt"));
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
