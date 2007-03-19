
import es.ull.isaatc.random.*;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.simulation.info.StatisticListener;
import es.ull.isaatc.simulation.info.StdInfoListener;
import es.ull.isaatc.util.*;

/** 
 * Se corresponde con la unidad que se va a simular.
 * Se está simulando el servicio de Ginecología, separado en cuatro patologías:
 * Funcional, Orgánica, Oncológica y Precoz. Para cada patología se define una
 * clase de médico y una clase de consulta.
 * Unicamente se ha definido un recurso activo por cada clase de recurso.
 * El resultado de todo esto sería que se dispone de 4 gestores de actividades, 
 * uno por cada patología.
 */
class Analisis extends Simulation {
    static final int NPACIENTES = 10;
    int ndays;
    
	Analisis(double startTs, int ndays, Output out) {
		super("Sistema de análisis", startTs, ndays * 24 * 60.0, out);
		this.ndays = ndays;
    }
    
	Analisis(double startTs, int ndays) {
		super("Sistema de análisis", startTs, ndays * 24 * 60.0);
		this.ndays = ndays;
    }
    
    protected void createModel() {
        // PASO 1: Inicializo las Activityes de las que se compone
        Activity actPrueba1 = new Activity(0, this, "Extracción de sangre, muestra de orina");
        Activity actPrueba1a = new Activity(1, this, "Análisis de sangre");
        Activity actPrueba1b = new Activity(2, this, "Análisis de orina");
        Activity actPrueba1c = new Activity(3, this, "Análisis de sangre2a");
        Activity actPrueba1d = new Activity(4, this, "Análisis de orina2");
        Activity actPrueba1e = new Activity(5, this, "Análisis de sangre2b");
 
        // PASO 2: Inicializo las clases de recursos
        ResourceType crSangre = new ResourceType(0, this, "Máquina Análisis Sangre");
        ResourceType crOrina = new ResourceType(1, this, "Máquina Análisis Orina");
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
        
        new ElementType(0, this, "Paciente");
		
//		Resource sangre1 = new Resource(this, "Máquina Análisis Sangre 1");
//		sangre1.addTimeTableEntry(480, 1440, 480, getResourceType(0), 0);
//		Resource orina1 = new Resource(this, "Máquina Análisis Orina 1");
//		orina1.addTimeTableEntry(480, 1440, 480, getResourceType(1), 0);

//        Cycle c = new Cycle(480, new Fixed(1440.0), 3);
//        Cycle c1 = new Cycle(0, new Fixed(1440.0 * 7), 0, c);
//        Resource poli = new Resource(0, this, "Máquina polivalente");
//        ArrayList list = new ArrayList();
//        list.add(getResourceType(1));
//        list.add(getResourceType(0));
//        poli.addTimeTableEntry(c1, 480, list);
        // Y añado los Enfermeros necesarios para que vaya "sobrado" el asunto
        
        Cycle c = new PeriodicCycle(480, new Fixed(1440.0), 0);
		new Resource(1, this, "Enfermero 1").addTimeTableEntry(c, 480, getResourceType(2));
//		new Resource(2, this, "Enfermero 2").addTimeTableEntry(c, 480, getResourceType(2));
		createMetaFlow3();
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
        Cycle c = new PeriodicCycle(0.0, new Fixed(1440.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(new Fixed(NPACIENTES), getElementType(0), sec), c.iterator(startTs, endTs));
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
        Cycle c = new PeriodicCycle(0.0, new Fixed(1440.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(new Fixed(NPACIENTES), getElementType(0), simPruebas), c.iterator(startTs, endTs));
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
        Cycle c = new PeriodicCycle(0.0, new Fixed(1440.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(new Fixed(NPACIENTES), getElementType(0), metaFlow), c.iterator(startTs, endTs));
    }
    
    protected void createMetaFlow3() {
        Cycle c = new PeriodicCycle(0.0, new Fixed(1440.0), 0);
        new TimeDrivenGenerator(this, new ElementCreator(new Fixed(NPACIENTES), getElementType(0), new SingleMetaFlow(23, new Fixed(1), getActivity(0))), c.iterator(startTs, endTs));
    }

}

class ExpHospitalSecuencia extends Experiment {
	static final int NEXP = 1;
    static final int NDIAS = 1;
	static final double PERIOD = 1440.0;
	double prevStart = 0.0, prevEnd = 0.0;
	
	public ExpHospitalSecuencia() {
//		super("Hospital", NEXP, new StdResultProcessor(1440.0), new Output(Output.DEBUGLEVEL));
		super("Hospital", NEXP);
	}

	public ExpHospitalSecuencia(double prevStart, double prevEnd) {
		super("Hospital", NEXP);
		this.prevStart = prevStart;
		this.prevEnd = prevEnd;		
	}

	public Simulation getSimulation(int ind) {
		Analisis sim = null;
//		if (Double.compare(prevEnd, 0.0) != 0)
//			sim = new Analisis(NDIAS + (int)(prevEnd / (60 * 24)), new Output(Output.DebugLevel.XDEBUG), new PreviousSimulationResults(prevStart, prevEnd, ind, "c:\\"));
//		else
			sim = new Analisis(0.0, NDIAS);
		sim.addListener(new StdInfoListener());
		sim.addListener(new StatisticListener(1440.0));		
		return sim;
	}
}

public class HospitalSecuencia {
	
	public static void main(String arg[]) {
		new ExpHospitalSecuencia().start();
	} // fin del main
} // fin de Hospital
