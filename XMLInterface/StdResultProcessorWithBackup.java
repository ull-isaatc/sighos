import java.io.FileWriter;
import java.io.IOException;

import es.ull.cyc.simulation.results.ActivityStatistics;
import es.ull.cyc.simulation.results.PendingFlowStatistics;
import es.ull.cyc.simulation.results.SimulationResults;
import es.ull.cyc.simulation.results.StdResultProcessor;

/**
 * 
 */

/**
 * @author Roberto Muñoz
 *
 */
public class StdResultProcessorWithBackup extends StdResultProcessor {
	
	/** Results directory path */
	String resDir;
	
	public StdResultProcessorWithBackup() {
		super();
	}
	
	/**
	 * @param period
	 * @param resDir
	 */
	public StdResultProcessorWithBackup(double period, String resDir) {
		super(period);
		this.resDir = resDir;
	}

	public void processStatistics(SimulationResults[] results) {
		super.processStatistics(results);
		try {
			for (int i = 0; i < results.length; i++) {
				FileWriter fout;
				fout = new FileWriter(resDir + "\\queue_" + results[i].getSimStart() + "_" + results[i].getSimEnd() + "_" + i + ".txt");
				processActivityStatistics(results[i], fout);
				fout.close();
				fout = new FileWriter(resDir + "\\pflows_" + results[i].getSimStart() + "_" + results[i].getSimEnd() + "_" + i + ".txt");
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
				ActivityStatistics as = (ActivityStatistics) res.getActivityStatistics().get(i);
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
				PendingFlowStatistics pfs = (PendingFlowStatistics) res.getPendingFlowStatistics().get(i);
				fout.write(pfs.getElemId() + "\t" + pfs.getType() + "\t" + pfs.getValue() + "\r\n");
				fout.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param resDir the resDir to set
	 */
	public void setResDir(String resDir) {
		this.resDir = resDir;
	}
}
