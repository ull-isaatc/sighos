package es.ull.isaatc.HUNSC.cirgen;

import es.ull.isaatc.HUNSC.cirgen.view.GSViewControllerArray;
import es.ull.isaatc.simulation.Experiment;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.util.ExcelTools;

/**
 * Se sobreescribe la clase de experimentación para añadir un control extra al final con el que 
 * poder escribir un fichero resumen en Excel.
 */
class ExpGS extends Experiment {
	/** Control de la salida por Excel */
	private GSViewControllerArray listeners;
	/** "Envoltorio" de la entrada */
	private GSExcelInputWrapper input;
	
	/**
	 * Crea un experimento usando como entrada un fichero de Excel y estableciendo como salida otra serie
	 * de ficheros Excel.
	 * @param filename Fichero Excel con configuración de entrada
	 */
	public ExpGS(String filename) {
		super();
		setDescription("Validation HCG");
		input = new GSExcelInputWrapper(filename);
		setNExperiments(input.getNExperiments());
	}
	
	@Override
	public Simulation getSimulation(int ind) {		
		return new SimGS(ind, listeners, input);
	}

	@Override
	public void start() {
		System.out.println("--------------------- Experiment started ---------------------");
		listeners = new GSViewControllerArray(input);
		for (int i = 0; i < getNExperiments(); i++)
			getSimulation(i).run();
		listeners.writeResults(input.getOutputPath() + "_" + input.getOutputFileName() + ExcelTools.EXT);	
		System.out.println("--------------------- Experiment finished ---------------------");
	}
	
}

/**
 * Programa principal. Lo único que hace es invocar el experimento escogido.
 * @author Iván Castilla Rodríguez
 *
 */
public class HUNSCSim {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Modo de uso: java -cp sighos.jar;utils.jar;simkit.jar;actions.jar HUNSCSim <nombre_fichero_entrada.xls>");
		}
		else
//			new ExpGS(args[0]).start();
			new ExpGS("S:\\Simulacion\\HC\\Modelo quirófano CG\\HUNSCSim\\inputTest.xls").start();
	}
}
