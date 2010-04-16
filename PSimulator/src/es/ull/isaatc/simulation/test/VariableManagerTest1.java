package es.ull.isaatc.simulation.test;

import java.util.EnumSet;

import es.ull.isaatc.simulation.common.PooledExperiment;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.factory.SimulationFactory;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.inforeceiver.StdInfoView;
import es.ull.isaatc.simulation.common.variable.EnumType;
import es.ull.isaatc.simulation.common.variable.EnumVariable;
import es.ull.isaatc.simulation.common.variable.IntVariable;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;

/**
 * 
 */
class ExperimentTest1 extends PooledExperiment {
	final static SimulationFactory.SimulationType simType = SimulationType.GROUPEDX;
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

    	TimeDrivenActivity act0 = factory.getTimeDrivenActivityInstance(0, "Verificar cuenta", 0, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
        
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
