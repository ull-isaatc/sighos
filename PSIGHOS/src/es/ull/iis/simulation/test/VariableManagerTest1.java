package es.ull.iis.simulation.test;

import es.ull.iis.simulation.factory.SimulationFactory;
import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.parallel.ParallelSimulationEngine;
import es.ull.iis.simulation.variable.EnumType;
import es.ull.iis.simulation.variable.EnumVariable;
import es.ull.iis.simulation.variable.IntVariable;

/**
 * 
 */
class ExperimentTest1 extends Experiment {
	final static int NTHREADS = 2;
	final static TimeUnit unit = TimeUnit.MINUTE;
	static final int NEXP = 1;
    static final int NDAYS = 1;
	static final double PERIOD = 1040.0;
	
	public ExperimentTest1() {
		super("Banco", NEXP);
	}

	public Simulation getSimulation(int ind) {
		SimulationFactory factory = new SimulationFactory(ind, "Ej", unit, TimeStamp.getZero(), new TimeStamp(TimeUnit.DAY, NDAYS));
		Simulation sim = factory.getSimulation();

		factory.getFlowInstance("ActivityFlow", "Verificar cuenta", 0, false, false);
        
    	sim.putVar("Coste total", new IntVariable(0));
    	sim.putVar("Coste", new IntVariable(200));
    	
    	IntVariable temp = (IntVariable) sim.getVar("Coste");
    	temp.setValue(temp.getValue().intValue() * 10);  	
    	System.out.println("Coste de la actividad = " + sim.getVar("Coste").toString());
    	
    	((IntVariable)sim.getVar("Coste total")).setValue(sim.getVar("Coste").getValue());
    	System.out.println("Coste total= " + sim.getVar("Coste total").toString());
    	
    	EnumType type = new EnumType("Deportivo", "Familiar", "Gasoil");
    	sim.putVar("tipoCoche", new EnumVariable(type, new Integer(0)));
    	((EnumVariable)sim.getVar("tipoCoche")).setValue(2);
    	System.out.println("Valor del enumerado: " + sim.getVar("tipoCoche").toString());
    	((EnumVariable)sim.getVar("tipoCoche")).setValue("Deportivo");
    	System.out.println("Valor del enumerado: " + sim.getVar("tipoCoche").toString());

		sim.addInfoReceiver(new StdInfoView());
		if (NTHREADS > 1) {
			sim.setSimulationEngine(new ParallelSimulationEngine(ind, sim, NTHREADS));
		}
		return sim;
	}
	

}

public class VariableManagerTest1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ExperimentTest1().start();
	}

}
