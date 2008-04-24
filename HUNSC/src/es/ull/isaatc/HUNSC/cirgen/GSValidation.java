package es.ull.isaatc.HUNSC.cirgen;

import java.util.concurrent.TimeUnit;

import es.ull.isaatc.HUNSC.cirgen.listener.GSListenerController;
import es.ull.isaatc.HUNSC.cirgen.listener.GSListenerControllerArray;
import es.ull.isaatc.HUNSC.cirgen.util.ExcelTools;
import es.ull.isaatc.simulation.Experiment;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.listener.ActivityListener;
import es.ull.isaatc.simulation.listener.ResourceStdUsageListener;

/**
 * Experimentos de validación. La unidad de tiempo es el minuto.
 */
class ExpGS extends Experiment {
	// Tiempo de simulación
	private GSListenerControllerArray listeners;
	private GSExcelInputWrapper input;
	
	public ExpGS(String filename) {
		super();
		setDescription("Validation HCG");
		input = new GSExcelInputWrapper(filename);
		setNExperiments(input.getNExperiments());
	}
	
	public Simulation getSimulation(int ind) {		
		SimGS sim = new SimGS(ind, 0.0, TimeUnit.MINUTES.convert(input.getSimulationDays(), TimeUnit.DAYS), input);
		GSListenerController cont = listeners.getController(ind);
		cont.addListener(new ResourceStdUsageListener(TimeUnit.MINUTES.convert(input.getSimulationDays(), TimeUnit.DAYS)));
		cont.addListener(new ActivityListener(TimeUnit.MINUTES.convert(input.getSimulationDays(), TimeUnit.DAYS)));

		sim.setListenerController(cont);
		return sim;
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Experiment#start()
	 */
	@Override
	public void start() {
		listeners = new GSListenerControllerArray(input);
		for (int i = 0; i < getNExperiments(); i++) {
			Simulation sim = getSimulation(i);
			sim.call();
		}
		listeners.writeResults(input.getOutputPath() + "_" + input.getOutputFileName() + ExcelTools.EXT);	
	}
	
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class GSValidation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		new ExpGS("C:\\Users\\Iván\\Documents\\HC\\Modelo quirófano CG\\input.xls").start();
		new ExpGS("S:\\Simulacion\\HC\\Modelo quirófano CG\\input.xls").start();
	}
}
