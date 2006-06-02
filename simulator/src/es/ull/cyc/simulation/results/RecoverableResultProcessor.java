/**
 * 
 */
package es.ull.cyc.simulation.results;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class RecoverableResultProcessor implements ResultProcessor {
	protected String baseDirName = "";

	public RecoverableResultProcessor() {		
	}
	
	public RecoverableResultProcessor(String baseDirName) {
		this.baseDirName = baseDirName;
	}
	
	public void processStatistics(SimulationResults[] results) {
		try {
			for (int i = 0; i < results.length; i++) {
				FileWriter fout;
				fout = new FileWriter(baseDirName + "queue_" + results[i].getSimStart() + "_" + results[i].getSimEnd() + "_" + i + ".txt");
				processActivityStatistics(results[i], fout);
				fout.close();
				fout = new FileWriter(baseDirName + "pflows_" + results[i].getSimStart() + "_" + results[i].getSimEnd() + "_" + i + ".txt");
				processPendingFlowStatistics(results[i], fout);
				fout.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void processActivityStatistics(SimulationResults res, FileWriter fout) {
		try {
			for (int i = 0; i < res.getActivityStatistics().size(); i++) {
				ActivityStatistics as = res.getActivityStatistics().get(i);
				fout.write(as.getActId() + "\t" + as.getElemId() + "\t" + as.getFlowId() + "\r\n");
				fout.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void processPendingFlowStatistics(SimulationResults res, FileWriter fout) {
		try {
			fout.write(res.getFirstElementId() + "\t" + res.getLastElementId() + "\r\n");
			for (int i = 0; i < res.getPendingFlowStatistics().size(); i++) {
				PendingFlowStatistics pfs = res.getPendingFlowStatistics().get(i);
				fout.write(pfs.getElemId() + "\t" + pfs.getType() + "\t" + pfs.getValue() + "\r\n");
				fout.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
