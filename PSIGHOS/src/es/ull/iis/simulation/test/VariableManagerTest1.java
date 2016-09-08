package es.ull.iis.simulation.test;

import java.util.EnumSet;

import es.ull.iis.simulation.core.Experiment;
import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.core.TimeDrivenActivity;
import es.ull.iis.simulation.core.TimeStamp;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.factory.SimulationFactory;
import es.ull.iis.simulation.factory.SimulationObjectFactory;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.variable.EnumType;
import es.ull.iis.simulation.variable.EnumVariable;
import es.ull.iis.simulation.variable.IntVariable;

/**
 * 
 */
class ExperimentTest1 extends Experiment {
	final static SimulationFactory.SimulationType simType = SimulationType.PARALLEL;
	final static TimeUnit unit = TimeUnit.MINUTE;
	static final int NEXP = 1;
    static final int NDAYS = 1;
	static final double PERIOD = 1040.0;
	
	public ExperimentTest1() {
		super("Banco", NEXP);
	}

	public Simulation getSimulation(int ind) {
		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, ind, "Ej", unit, TimeStamp.getZero(), new TimeStamp(TimeUnit.DAY, NDAYS));
		Simulation sim = factory.getSimulation();

    	TimeDrivenActivity act0 = factory.getTimeDrivenActivityInstance("Verificar cuenta", 0, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
        
    	sim.putVar("Coste total", new IntVariable(0));
    	act0.putVar("Coste", new IntVariable(200));
    	
    	IntVariable temp = (IntVariable) act0.getVar("Coste");
    	temp.setValue(temp.getValue().intValue() * 10);  	
    	System.out.println("A0.Coste = " + act0.getVar("Coste").toString());
    	
    	((IntVariable)sim.getVar("Coste total")).setValue(act0.getVar("Coste").getValue());
    	System.out.println("Coste total= " + sim.getVar("Coste total").toString());
    	
    	EnumType type = new EnumType("Deportivo", "Familiar", "Gasoil");
    	sim.putVar("tipoCoche", new EnumVariable(type, new Integer(0)));
    	((EnumVariable)sim.getVar("tipoCoche")).setValue(2);
    	System.out.println("Valor del enumerado: " + sim.getVar("tipoCoche").toString());
    	((EnumVariable)sim.getVar("tipoCoche")).setValue("Deportivo");
    	System.out.println("Valor del enumerado: " + sim.getVar("tipoCoche").toString());

		sim.addInfoReceiver(new StdInfoView(sim));
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