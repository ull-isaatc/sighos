import es.ull.cyc.simulation.*;
import es.ull.cyc.simulation.results.*;
import es.ull.cyc.util.*;
import es.ull.cyc.random.*;
import java.util.ArrayList;

/** 
 * Se corresponde con la unidad que se va a simular.
 * Se est� simulando el servicio de Ginecolog�a, separado en cuatro patolog�as:
 * Funcional, Org�nica, Oncol�gica y Precoz. Para cada patolog�a se define una
 * clase de m�dico y una clase de consulta.
 * Unicamente se ha definido un recurso activo por cada clase de recurso.
 * El resultado de todo esto ser�a que se dispone de 4 gestores de actividades, 
 * uno por cada patolog�a.
 */
class Analisis extends Simulation {
    static final int NPACIENTES = 10;
    int ndays;
    
	Analisis(double startTs, int ndays, Output out) {
		super("Sistema de an�lisis", startTs, ndays * 24 * 60.0, out);
		this.ndays = ndays;
    }
    
	Analisis(int lastday, Output out, SimulationResults res) {
		super("Sistema de an�lisis", lastday * 24 * 60.0, out, res);
		this.ndays = lastday;
    }
    
	protected void createGenerators() {
		createMetaFlow3();
	}
	
    protected void createModel() {
        // PASO 1: Inicializo las Activityes de las que se compone
        Activity actPrueba1 = new Activity(0, this, "Extracci�n de sangre, muestra de orina");
        Activity actPrueba1a = new Activity(1, this, "An�lisis de sangre");
        Activity actPrueba1b = new Activity(2, this, "An�lisis de orina");
        Activity actPrueba1c = new Activity(3, this, "An�lisis de sangre2a");
        Activity actPrueba1d = new Activity(4, this, "An�lisis de orina2");
        Activity actPrueba1e = new Activity(5, this, "An�lisis de sangre2b");
 
        // PASO 2: Inicializo las clases de recursos
        ResourceType crSangre = new ResourceType(0, this, "M�quina An�lisis Sangre");
        ResourceType crOrina = new ResourceType(1, this, "M�quina An�lisis Orina");
        ResourceType crEnfermero = new ResourceType(2, this, "Enfermero");

        // PASO 3: Creo las tablas de clases de recursos
//        WorkGroup wg1 = actPrueba1.getNewWorkGroup(0, new Normal(20.0, 5.0));
        WorkGroup wg1 = actPrueba1.getNewWorkGroup(0, new Fixed(60));
        wg1.add(crEnfermero, 1);
        WorkGroup wg2 = actPrueba1a.getNewWorkGroup(0, new Normal(10.0, 2.0));
        wg2.add(crSangre, 1);
        WorkGroup wg3 = actPrueba1c.getNewWorkGroup(0, new Normal(10.0, 2.0));
        wg3.add(crSangre, 1);
        wg3.add(crEnfermero, 1);
        WorkGroup wg4 = actPrueba1e.getNewWorkGroup(0, new Normal(10.0, 2.0));
        wg4.add(crSangre, 1);
        wg4.add(crEnfermero, 1);
        WorkGroup wg5 = actPrueba1b.getNewWorkGroup(0, new Normal(10.0, 5.0));
        wg5.add(crOrina, 1);
        WorkGroup wg6 = actPrueba1d.getNewWorkGroup(0, new Normal(10.0, 5.0));
        wg6.add(crOrina, 1);
        wg6.add(crEnfermero, 1);
       
//		Resource sangre1 = new Resource(this, "M�quina An�lisis Sangre 1");
//		sangre1.addTimeTableEntry(480, 1440, 480, crSangre, 0);
//		Resource orina1 = new Resource(this, "M�quina An�lisis Orina 1");
//		orina1.addTimeTableEntry(480, 1440, 480, crOrina, 0);

//        Cycle c = new Cycle(480, new Fixed(1440.0), 3);
//        Cycle c1 = new Cycle(0, new Fixed(1440.0 * 7), 0, c);
//        Resource poli = new Resource(0, this, "M�quina polivalente");
//        ArrayList list = new ArrayList();
//        list.add(crOrina);
//        list.add(crSangre);
//        poli.addTimeTableEntry(c1, 480, list);
        // Y a�ado los Enfermeros necesarios para que vaya "sobrado" el asunto
        
        Cycle c = new Cycle(480, new Fixed(1440.0), 0);
		Resource tec1 = new Resource(1, this, "Enfermero 1");
		tec1.addTimeTableEntry(c, 480, crEnfermero);
//		Resource tec2 = new Resource(this, "Enfermero 2");
//		tec2.addTimeTableEntry(c, 480, crEnfermero);
		
		
    }
    
    protected void createMetaFlow0() {
        SequenceMetaFlow sec = new SequenceMetaFlow(1, new Fixed(1));
        new SingleMetaFlow(2, sec, new Fixed(1), getActivity(0));
        SimultaneousMetaFlow simPruebas = new SimultaneousMetaFlow(3, sec, new Fixed(1));
        SequenceMetaFlow secOrina = new SequenceMetaFlow(4, simPruebas, new Fixed(1));
        new SingleMetaFlow(5, secOrina, new Fixed(1), getActivity(2));
        new SingleMetaFlow(6, secOrina, new Fixed(1), getActivity(4));
        SequenceMetaFlow secSangre = new SequenceMetaFlow(7, simPruebas, new Fixed(1));
        new SingleMetaFlow(8, secSangre, new Fixed(1), getActivity(1));
        SimultaneousMetaFlow simSangre = new SimultaneousMetaFlow(9, secSangre, new Fixed(1));
        new SingleMetaFlow(10, simSangre, new Fixed(1), getActivity(3));
        new SingleMetaFlow(11, simSangre, new Fixed(1), getActivity(5));
        Cycle c = new Cycle(0.0, new Fixed(1440.0), ndays);
        Generation gen = new Generation(new Fixed(NPACIENTES));
        gen.add(sec, 1.0);
        gen.createGenerators(this, c);
    }
    
    protected void createMetaFlow1() {
    	SimultaneousMetaFlow simPruebas = new SimultaneousMetaFlow(12, new Uniform(1, 4));
    	new SingleMetaFlow(13, simPruebas, new Fixed(1), getActivity(0));
    	DecisionMetaFlow dec = new DecisionMetaFlow(14, simPruebas, new Fixed(1));        
        OptionMetaFlow op1 = new OptionMetaFlow(15, dec, 0.5);
        OptionMetaFlow op2 = new OptionMetaFlow(16, dec, 0.5);
        new SingleMetaFlow(17, op1, new Fixed(1), getActivity(2));
        SimultaneousMetaFlow simSangre = new SimultaneousMetaFlow(18, op2, new Fixed(1));
        new SingleMetaFlow(19, simSangre, new Fixed(1), getActivity(3));
        new SingleMetaFlow(20, simSangre, new Fixed(1), getActivity(5));
        Cycle c = new Cycle(0.0, new Fixed(1440.0), ndays);
        Generation gen = new Generation(new Fixed(NPACIENTES));
        gen.add(simPruebas, 1.0);
        gen.createGenerators(this, c);
    }
    protected void createMetaFlow2() {
        SimultaneousMetaFlow metaFlow = new SimultaneousMetaFlow(21, new Fixed(1));
        new SingleMetaFlow(22, metaFlow, new Fixed(1), getActivity(2));
        new SingleMetaFlow(23, metaFlow, new Fixed(1), getActivity(1));
        // FIXME: Con end = 180.0 falla!!! 
//        Cycle c3 = new Cycle(480.0, new Fixed(50.0), 960.0);
//        Cycle c2 = new Cycle(0.0, new Fixed(1440.0), 5, c3);
////        Cycle c2 = new Cycle(480.0, new Fixed(120.0), 0);
//        Cycle c = new Cycle(0.0, new Fixed(1440.0 * 7), 0, c2);
        Cycle c = new Cycle(0.0, new Fixed(1440.0), ndays);
        Generation gen = new Generation(new Fixed(NPACIENTES));
        gen.add(metaFlow, 1.0);
        gen.createGenerators(this, c);
    }
    
    protected void createMetaFlow3() {
        Cycle c = new Cycle(0.0, new Fixed(1440.0), 0);
        Generation gen = new Generation(new Fixed(NPACIENTES));
        gen.add(new SingleMetaFlow(23, new Fixed(1), getActivity(0)), 1.0);
        gen.createGenerators(this, c);
    	
    }

}

class NullResultProcessor implements ResultProcessor {

	public void processStatistics(SimulationResults[] results) {
	}		
}

class ExpHospitalSecuencia extends Experiment {
	static final int NEXP = 1;
    static final int NDIAS = 1;
	static final double PERIOD = 1440.0;
	double prevStart = 0.0, prevEnd = 0.0;
	
	public ExpHospitalSecuencia() {
//		super("Hospital", NEXP, new StdResultProcessor(1440.0), new Output(Output.DEBUGLEVEL));
		super("Hospital", NEXP, new NullResultProcessor(), new Output(Output.XDEBUGLEVEL));
	}

	public ExpHospitalSecuencia(double prevStart, double prevEnd) {
		super("Hospital", NEXP, new RecoverableResultProcessor("c:\\"), new Output(Output.DEBUGLEVEL));
		this.prevStart = prevStart;
		this.prevEnd = prevEnd;		
	}

	public Simulation getSimulation(int ind) {
		if (Double.compare(prevEnd, 0.0) != 0)
			return new Analisis(NDIAS + (int)(prevEnd / (60 * 24)), out, new PreviousSimulationResults(prevStart, prevEnd, ind, "c:\\"));
		return new Analisis(0.0, NDIAS, out);		
	}
}

public class HospitalSecuencia {
	
	public static void main(String arg[]) {
		new ExpHospitalSecuencia().start();
	} // fin del main
} // fin de Hospital