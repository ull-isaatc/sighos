package es.ull.isaatc.HUNSC.cirgen;

import java.util.EnumSet;

import simkit.random.RandomNumberFactory;
import simkit.random.RandomVariateFactory;
import es.ull.isaatc.HUNSC.cirgen.listener.GSListenerControllerArray;
import es.ull.isaatc.HUNSC.cirgen.listener.GSListenerController;
import es.ull.isaatc.HUNSC.cirgen.util.ExcelTools;
import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.Activity;
import es.ull.isaatc.simulation.ElementCreator;
import es.ull.isaatc.simulation.ElementType;
import es.ull.isaatc.simulation.Experiment;
import es.ull.isaatc.simulation.Resource;
import es.ull.isaatc.simulation.ResourceType;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.SingleMetaFlow;
import es.ull.isaatc.simulation.StandAloneLPSimulation;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.WorkGroup;
import es.ull.isaatc.simulation.listener.ActivityListener;
import es.ull.isaatc.simulation.listener.ResourceStdUsageListener;
import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.RoundedPeriodicCycle;
import es.ull.isaatc.util.WeeklyPeriodicCycle;
import es.ull.isaatc.util.WeeklyPeriodicCycle.WeekDays;

class SimVerify extends StandAloneLPSimulation {
	// Tiempo total del estudio: 3 años en minutos (un año bisiesto)
	public static final double TOTALTIME = 1096 * 1440.0;
	// Tiempo de simulación
	public static final double ENDTIME = 7 * 1440.0;
	// Tiempo de uso diario de los quirófanos (7 horas)
	public static final double AVAILABILITY = 7 * 60.0;
	// Hora de apertura de los quirófanos (mañana: 8 am)
	public static final double STARTTIME = 8 * 60.0;
	public enum OpTheatre {
		Q31 ("3-1", false, EnumSet.of(WeekDays.FRIDAY)),
		AMB ("AMB", true, EnumSet.of(WeekDays.MONDAY)),
		Q45 ("4-5", false, WeeklyPeriodicCycle.WEEKDAYS),
		Q46 ("4-6", false, WeeklyPeriodicCycle.WEEKDAYS);
		
		private final String name;
		private final WeeklyPeriodicCycle cycle;
		private final boolean daycase;
		private OpTheatre(String name, boolean daycase, EnumSet<WeekDays> days) {
			this.name = name;
			this.daycase = daycase;
			cycle = new WeeklyPeriodicCycle(days, 1440.0, STARTTIME, 0);
		}
		
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @return the daycase
		 */
		public boolean isDaycase() {
			return daycase;
		}

		/**
		 * @return the cycle
		 */
		public WeeklyPeriodicCycle getCycle() {
			return cycle;
		}

	}

	public SimVerify(int id) {
		super(id, "General Surgery (" + id +")", 0.0, ENDTIME);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Simulation#createModel()
	 */
	@Override
	protected void createModel() {
		// Añado una semilla aleatoria
		RandomVariateFactory.setDefaultRandomNumber(RandomNumberFactory.getInstance(System.currentTimeMillis()));
		
		// Tipos de Quirófanos (ambulantes y no ambulantes)
		ResourceType rtNoAmb = new ResourceType(0, this, "OROT");
		WorkGroup wgNoAmb = new WorkGroup(0, this, "ORWG");
		wgNoAmb.add(rtNoAmb, 1);
		ResourceType rtAmb = new ResourceType(1, this, "DCOT");
		WorkGroup wgAmb = new WorkGroup(1, this, "DCWG");
		wgAmb.add(rtAmb, 1);
		
		// Quirófanos
		for (OpTheatre op : OpTheatre.values()) {
			Resource r = new Resource(op.ordinal(), this, op.getName());
			if (op.isDaycase())
				r.addTimeTableEntry(op.getCycle(), AVAILABILITY, rtAmb);
			else
				r.addTimeTableEntry(op.getCycle(), AVAILABILITY, rtNoAmb);
		}
		
		TimeFunction constant = TimeFunctionFactory.getInstance("ConstantVariate", 1440.0);
		Cycle c = new RoundedPeriodicCycle(0.0, constant, 0, RoundedPeriodicCycle.Type.ROUND, 1440.0);
		ElementType etNoAmb = new ElementType(0, this, "OR");
		Activity actNoAmb = new Activity(0, this, "OR");
		actNoAmb.addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", new Object[] {7 * 60.0}), wgNoAmb);
		ElementCreator ecNoAmb = new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", 1), etNoAmb, new SingleMetaFlow(0, RandomVariateFactory.getInstance("ConstantVariate", 1), actNoAmb));
        new TimeDrivenGenerator(this, ecNoAmb, c);

		ElementType etAmb = new ElementType(1, this, "DC");
		Activity actAmb = new Activity(1, this, "DC");
		actAmb.addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", new Object[] {7 * 60.0}), wgAmb);
		ElementCreator ecAmb = new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", 1), etAmb, new SingleMetaFlow(1, RandomVariateFactory.getInstance("ConstantVariate", 1), actAmb));
        new TimeDrivenGenerator(this, ecAmb, c);		
	}
}

/**
 * Experimentos de validación. La unidad de tiempo es el minuto.
 */
class ExpGS extends Experiment {
	final static String PATH = "S:\\simulacion\\HC\\Modelo quirófano CG\\resultados\\";
	final static String FILENAME = "output";
	final static int NEXP = 10;
	// Tiempo de simulación
	public static final double ENDTIME = 1096 * 1440.0;
	private GSListenerControllerArray listeners;
	
	public ExpGS() {
		super("Validation HOFT", NEXP);
	}
	
	public Simulation getSimulation(int ind) {		
		SimGS sim = new SimGS(ind, 0.0, ENDTIME);
		GSListenerController cont = listeners.getController(ind);
		cont.addListener(new ResourceStdUsageListener(ENDTIME));
		cont.addListener(new ActivityListener(ENDTIME));

//		ListenerController cont = new ListenerController() {
//			@Override
//			public void end() {
//				super.end();
//				for (String str : getListenerResults()) {
//					System.out.println(str);
//				}
//			}
//		};
//		cont.addListener(new StdInfoListener());
		sim.setListenerController(cont);
//		sim.setOutput(new Output(true));
		return sim;
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Experiment#start()
	 */
	@Override
	public void start() {
		listeners = new GSListenerControllerArray(PATH + FILENAME, NEXP, ENDTIME);
		for (int i = 0; i < NEXP; i++) {
			Simulation sim = getSimulation(i);
			sim.call();
		}
		listeners.writeResults(PATH + "_" + FILENAME + ExcelTools.EXT);	
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
		new ExpGS().start();
	}

}
