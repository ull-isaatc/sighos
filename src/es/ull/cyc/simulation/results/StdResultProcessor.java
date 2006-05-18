/**
 * 
 */
package es.ull.cyc.simulation.results;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class StdResultProcessor implements ResultProcessor {
	protected double period = 1.0;

	/**
	 * Default constructor
	 *
	 */
	public StdResultProcessor() {
		
	}

	/**
	 * @param period
	 */
	public StdResultProcessor(double period) {
		this.period = period;
	}

	/* (non-Javadoc)
	 * @see es.ull.cyc.simulation.results.ResultProcessor#processStatistics()
	 */
	public void processStatistics(SimulationResults []results) {
		for (int i = 0; i < results.length; i++) {
			processSimulationTimeStatistics(results[i]);
			processElementStatistics(results[i]);
			processActivityStatistics(results[i], period);
			processPendingFlowStatistics(results[i]);			
		}
	}

	public static void processSimulationTimeStatistics(SimulationResults res) {
		System.out.println("INICIO SIMULACIÓN:\t" + res.getSimStart());
		System.out.println("FIN SIMULACIÓN:\t" + res.getSimEnd());
		System.out.println("FIN REAL SIMULACIÓN:\t" + res.getSimRealEnd());
		System.out.println("TIEMPO EJECUCIÓN SIMULACIÓN:\t" + (res.getEndT() - res.getIniT()));		
	}

	public static void processElementStatistics(SimulationResults res) {
		System.out.println("Element Statistics");
		System.out.println("Created: " + res.createdElements());
		for (int i = 0; i < res.getElementStatistics().size(); i++) {
			ElementStatistics es = (ElementStatistics) res.getElementStatistics().get(i);
			String msg = "";
			if (es.getType() == ElementStatistics.START) msg = "STARTED";
			else if (es.getType() == ElementStatistics.FINISH) msg = "FINISHED";
			else if (es.getType() == ElementStatistics.STAACT) msg = "STARTS ACTIVITY";
			else if (es.getType() == ElementStatistics.REQACT) msg = "REQUESTS ACTIVITY";
			else if (es.getType() == ElementStatistics.ENDACT) msg = "ENDS ACTIVITY";
			System.out.println("[" + es.getElemId() + "]\t" + es.getTs() + "\t" 
					+ msg + "\t" + es.getValue());
		}
	}

	public static void processActivityStatistics(SimulationResults res, double period) {
		System.out.println("Activity Statistics");
		for (int i = 0; i < res.getActivityStatistics().size(); i++) {
			ActivityStatistics as = (ActivityStatistics) res.getActivityStatistics().get(i);
			System.out.println(as.getActId() + "\t[" + as.getElemId() + "]\t" + as.getFlowId());
		}
		System.out.println("Activity Queues(PERIOD: " + period + ")");
		int[][]queues= res.computeQueueSizes(period);
		for (int i = 0; i < queues.length; i++) {
			System.out.print("A" + res.getActIds()[i][0] + ":");
			for (int j = 0; j < queues[i].length; j++) {
				System.out.print("\t" + queues[i][j]);
			}
			System.out.println("");
		}
	}

	public static void processPendingFlowStatistics(SimulationResults res) {
		System.out.println("Pending Flow Statistics");
		for (int i = 0; i < res.getPendingFlowStatistics().size(); i++) {
			PendingFlowStatistics pfs = (PendingFlowStatistics) res.getPendingFlowStatistics().get(i);
			String msg = "";
			if (pfs.getType() == PendingFlowStatistics.SINFLOW) msg = "Single Flow";
			else if (pfs.getType() == PendingFlowStatistics.SIMFLOW) msg = "Simultaneous Flow";
			else if (pfs.getType() == PendingFlowStatistics.SECFLOW) msg = "Sequence Flow";
			else if (pfs.getType() == PendingFlowStatistics.ACTFLOW) msg = "Activity";
			System.out.println("[" + pfs.getElemId() + "]\t" + msg + "\t" + pfs.getValue()); 
		}
	}

	/**
	 * @param period the period to set
	 */
	public void setPeriod(double period) {
		this.period = period;
	}

}
