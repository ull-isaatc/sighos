package es.ull.isaatc.simulation.threaded.test;

import java.util.EnumSet;

import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.threaded.StandAloneLPSimulation;
import es.ull.isaatc.simulation.threaded.TimeDrivenActivity;
import es.ull.isaatc.simulation.threaded.inforeceiver.StdInfoView;
import es.ull.isaatc.simulation.variable.EnumType;
import es.ull.isaatc.simulation.variable.EnumVariable;
import es.ull.isaatc.simulation.variable.IntVariable;

class SimulationTest1 extends StandAloneLPSimulation {
	int ndays;
	
	public SimulationTest1(int id, int ndays) {
		super(id, "Ej", SimulationTimeUnit.MINUTE, 0.0, ndays * 24 * 60.0);
		this.ndays = ndays;
    }
    
    protected void createModel() {
   	
    	new TimeDrivenActivity(0, this, "Verificar cuenta", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	new TimeDrivenActivity(1, this, "Obtener detalles tarjeta", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
        
    	this.putVar("Coste total", new IntVariable(0));
    	this.getActivity(0).putVar("Coste", new IntVariable(200));
    	
    	IntVariable temp = (IntVariable) this.getActivity(0).getVar("Coste");
    	temp.setValue(temp.getValue().intValue() * 10);  	
    	System.out.println("A0.Coste = " + this.getActivity(0).getVar("Coste").toString());
    	
    	((IntVariable)this.getVar("Coste total")).setValue(this.getActivity(0).getVar("Coste").getValue());
    	System.out.println("Coste total= " + this.getVar("Coste total").toString());
    	
    	EnumType type = new EnumType("Deportivo", "Familiar", "Gasoil");
    	this.putVar("tipoCoche", new EnumVariable(type, new Integer(0)));
    	((EnumVariable)this.getVar("tipoCoche")).setValue(2);
    	System.out.println("Valor del enumerado: " + this.getVar("tipoCoche").toString());
    	((EnumVariable)this.getVar("tipoCoche")).setValue("Deportivo");
    	System.out.println("Valor del enumerado: " + this.getVar("tipoCoche").toString());
    }
	
}
/**
 * 
 */
class ExperimentTest1 extends PooledExperiment {
	static final int NEXP = 1;
    static final int NDIAS = 1;
	static final double PERIOD = 1040.0;
	
	public ExperimentTest1() {
		super("Banco", NEXP);
	}

	public SimulationTest1 getSimulation(int ind) {
		SimulationTest1 sim = new SimulationTest1(ind, NDIAS);
		sim.addInfoReciever(new StdInfoView(sim));
		/*cont.addListener(new ActivityListener(PERIOD));
		cont.addListener(new ActivityTimeListener(PERIOD));*/
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
