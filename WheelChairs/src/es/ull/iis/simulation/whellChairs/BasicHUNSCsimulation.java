package es.ull.iis.simulation.whellChairs;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.function.TimeFunctionParams;
import es.ull.iis.simulation.condition.ResourceTypeAcquiredCondition;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.TimeDrivenElementGenerator;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.DelayFlow;
import es.ull.iis.simulation.model.flow.ExclusiveChoiceFlow;
import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.simulation.model.flow.TimeFunctionDelayFlow;
import es.ull.iis.simulation.whellChairs.listener.EveryoneOutListener;

/**
 * Ejemplo de implementaci�n de recursos, elementos y actividades. 
 * @author Jonel Alexander Rodr�guez Rodr�guez 
 * @author Raquel Rodr�guez D�az
*/

public class BasicHUNSCsimulation extends Simulation {
	final public static String STR_REQ_CHAIR = "Pedir Silla"; 
	final public static String STR_REL_CHAIR = "Soltar Silla"; 
	final public static String STR_REQ_JANITOR = "Pedir bedel";
	final public static String STR_REL_JANITOR = "Soltar bedel";
	final public static String STR_AUTO_CHAIR = "Silla Autom�tica"; 
	final public static String STR_MANUAL_CHAIR = "Silla Manual"; 
	final public static String STR_SECTION = "Secci�n"; 
	final public static String STR_JANITOR = "Bedel"; 
	final public static String STR_DOCTOR = "Doctor"; 
	final public static String STR_PATIENT = "Paciente";
	final public static String STR_M_APPOINTMENT = "Consulta con silla manual";
	final public static String STR_A_APPOINTMENT = "Consulta con silla autom�tica";
	final public static String STR_M_STAND = "Levantarse de una silla manual";
	final public static String STR_A_STAND = "Levantarse de una silla autom�tica";
	public static enum Density {
		LOW,
		MEDIUM_LOW,
		MEDIUM_HIGH,
		HIGH
	}
	final public static int N_SECTIONS = 3; 
	/** Time unit of the simulations: seconds */
	final public static TimeUnit unit = TimeUnit.SECOND;
	/** Simulation length: 8 hours (in seconds) */
	final private static long END_TIME = 8 * 60 * 60;
	/** Last arrival of patients: one hour before simulation end */ 
	final private static long LAST_ARRIVAL = 7 * 60 * 60;
//	final private static TimeFunction[][] T_SECTIONS = 
//		{{TimeFunctionFactory.getInstance("NormalVariate", 0.75 * 60, 0.01653 * 60), TimeFunctionFactory.getInstance("NormalVariate", 1.60 * 60, 0.011 * 60), TimeFunctionFactory.getInstance("NormalVariate", 0.73 * 60, 0.011 * 60)},
//		{TimeFunctionFactory.getInstance("NormalVariate", 0.79 * 60, 0.017 * 60), TimeFunctionFactory.getInstance("NormalVariate", 1.79 * 60, 0.056 * 60), TimeFunctionFactory.getInstance("NormalVariate", 0.61 * 60, 0.054 * 60)},
//		{TimeFunctionFactory.getInstance("NormalVariate", 0.82 * 60, 0.018 * 60), TimeFunctionFactory.getInstance("NormalVariate", 1.86 * 60, 0.063 * 60), TimeFunctionFactory.getInstance("NormalVariate", 0.77 * 60, 0.012 * 60 )},
//		{TimeFunctionFactory.getInstance("NormalVariate", 0.896 * 60, 0.054 * 60), TimeFunctionFactory.getInstance("NormalVariate", 2.038 * 60, 0.075 * 60), TimeFunctionFactory.getInstance("NormalVariate", 0.895 * 60, 0.019 * 60 )}};
//	final private static TimeFunction T_APPOINTMENT = TimeFunctionFactory.getInstance("UniformVariate", 10*60, 15*60);
//	final private static TimeFunction T_M_SEAT = TimeFunctionFactory.getInstance("UniformVariate", 1*60, 3*60);
//	final private static TimeFunction T_A_SEAT = TimeFunctionFactory.getInstance("UniformVariate", 1*60, 3*60);
//	final private static TimeFunction T_M_STAND = TimeFunctionFactory.getInstance("UniformVariate", 1*60, 3*60);
//	final private static TimeFunction T_A_STAND = TimeFunctionFactory.getInstance("UniformVariate", 1*60, 3*60);
	final private static ElementReplicableTimeFunction[][] T_SECTIONS = 
		{{new ElementReplicableTimeFunction(TimeFunctionFactory.getInstance("NormalVariate", 0.75 * 60, 0.01653 * 60)), new ElementReplicableTimeFunction(TimeFunctionFactory.getInstance("NormalVariate", 1.60 * 60, 0.011 * 60)), new ElementReplicableTimeFunction(TimeFunctionFactory.getInstance("NormalVariate", 0.73 * 60, 0.011 * 60))},
		{new ElementReplicableTimeFunction(TimeFunctionFactory.getInstance("NormalVariate", 0.79 * 60, 0.017 * 60)), new ElementReplicableTimeFunction(TimeFunctionFactory.getInstance("NormalVariate", 1.79 * 60, 0.056 * 60)), new ElementReplicableTimeFunction(TimeFunctionFactory.getInstance("NormalVariate", 0.61 * 60, 0.054 * 60))},
		{new ElementReplicableTimeFunction(TimeFunctionFactory.getInstance("NormalVariate", 0.82 * 60, 0.018 * 60)), new ElementReplicableTimeFunction(TimeFunctionFactory.getInstance("NormalVariate", 1.86 * 60, 0.063 * 60)), new ElementReplicableTimeFunction(TimeFunctionFactory.getInstance("NormalVariate", 0.77 * 60, 0.012 * 60 ))},
		{new ElementReplicableTimeFunction(TimeFunctionFactory.getInstance("NormalVariate", 0.896 * 60, 0.054 * 60)), new ElementReplicableTimeFunction(TimeFunctionFactory.getInstance("NormalVariate", 2.038 * 60, 0.075 * 60)), new ElementReplicableTimeFunction(TimeFunctionFactory.getInstance("NormalVariate", 0.895 * 60, 0.019 * 60 ))}};
	final private static ElementReplicableTimeFunction T_APPOINTMENT = new ElementReplicableTimeFunction(TimeFunctionFactory.getInstance("UniformVariate", 10*60, 15*60));
	final private static ElementReplicableTimeFunction T_M_SEAT = new ElementReplicableTimeFunction(TimeFunctionFactory.getInstance("UniformVariate", 1*60, 3*60));
	final private static ElementReplicableTimeFunction T_A_SEAT = new ElementReplicableTimeFunction(TimeFunctionFactory.getInstance("UniformVariate", 1*60, 3*60));
	final private static ElementReplicableTimeFunction T_M_STAND = new ElementReplicableTimeFunction(TimeFunctionFactory.getInstance("UniformVariate", 1*60, 3*60));
	final private static ElementReplicableTimeFunction T_A_STAND = new ElementReplicableTimeFunction(TimeFunctionFactory.getInstance("UniformVariate", 1*60, 3*60));
	final private EveryoneOutListener endCondition;
	
	/**
	 * Creates a simulation based on minutes, which lasts for a week (7 days X 24 hours X 60 minutes X 60 seconds)
	 * @param id
	 */
	public BasicHUNSCsimulation(int id, Density[] sections, int nJanitors, int nDoctors, int nAutoChairs, int nManualChairs, int patientsPerArrival, int minutesBetweenArrivals, double manualFactor) {
		super(id, "HUNSC" + id, unit, 0, END_TIME);
		restartTimeFunctions();
		// El paciente es el elemento del modelo
		
		final ElementType etPatient = new ElementType(this, STR_PATIENT);
		
		// Los recursos son: Doctores y Silla
		
		final ResourceType rtJanitor = new ResourceType(this, STR_JANITOR);
		// Simplificando
		rtJanitor.addGenericResources(nJanitors);
		
		final ResourceType rtAChair = new ResourceType(this, STR_AUTO_CHAIR);
		rtAChair.addGenericResources(nAutoChairs);
		final ResourceType rtMChair = new ResourceType(this, STR_MANUAL_CHAIR);
		rtMChair.addGenericResources(nManualChairs);
		
		final ResourceType rtDoctor = new ResourceType(this, STR_DOCTOR);
		rtDoctor.addGenericResources(nDoctors);
		
		//Definici�n de los flujos de trabajo
		
		final WorkGroup wgJanitorMChair = new WorkGroup(this, new ResourceType[] {rtJanitor, rtMChair}, new int[] {1,1});
		final WorkGroup wgJanitor = new WorkGroup(this, rtJanitor, 1);
		final WorkGroup wgJanitorAChair = new WorkGroup(this, new ResourceType[] {rtJanitor, rtAChair}, new int[] {1,1});
		final WorkGroup wgAppointment = new WorkGroup(this, new ResourceType[] {rtDoctor} , new int [] {1});

		// Creamos todos los pasos del proceso
		final DelayFlow[] actASections = new DelayFlow [N_SECTIONS];
		final DelayFlow[] actASectionsBack = new DelayFlow [N_SECTIONS];
		final DelayFlow[] actMSections = new DelayFlow [N_SECTIONS];
		final DelayFlow[] actMSectionsBack = new DelayFlow [N_SECTIONS];
		
		final RequestResourcesFlow reqChair = new RequestResourcesFlow(this, STR_REQ_CHAIR, 1, 2);
		reqChair.newWorkGroupAdder(wgJanitorAChair).withDelay(T_A_SEAT).add();        
		reqChair.newWorkGroupAdder(wgJanitorMChair).withDelay(T_M_SEAT).withPriority(1).add();

		final ReleaseResourcesFlow relJanitorBeforeAppointment = new ReleaseResourcesFlow(this, STR_REL_JANITOR, 1, wgJanitor);
		final ReleaseResourcesFlow relJanitorAfterSeat = new ReleaseResourcesFlow(this, STR_REL_JANITOR, 1, wgJanitor);
		final RequestResourcesFlow reqJanitor = new RequestResourcesFlow(this, STR_REQ_JANITOR, 1, 1);
		reqJanitor.newWorkGroupAdder(wgJanitor).add();
		
		final ReleaseResourcesFlow relChair = new ReleaseResourcesFlow(this, STR_REL_CHAIR, 1);
		
		// Creamos una actividad de consulta por cada tipo de silla
		final ActivityFlow actMAppointment = new ActivityFlow(this, STR_M_APPOINTMENT);
		actMAppointment.newWorkGroupAdder(wgAppointment).withDelay(T_APPOINTMENT).add();
		final ActivityFlow actAAppointment = new ActivityFlow(this, STR_A_APPOINTMENT);
		actAAppointment.newWorkGroupAdder(wgAppointment).withDelay(T_APPOINTMENT).add();
		
		// Creamos los tramos de la ruta que siguen las sillas
		for (int i = 0; i < N_SECTIONS; i++) {
			actASections[i] = new TimeFunctionDelayFlow(this, STR_SECTION + i, T_SECTIONS[sections[i].ordinal()][i]);
			actASectionsBack[N_SECTIONS - i - 1] = new TimeFunctionDelayFlow(this, STR_SECTION + " (back)" + (N_SECTIONS - i - 1), T_SECTIONS[sections[i].ordinal()][i]);
			actMSections[i] = new TimeFunctionDelayFlow(this, STR_SECTION + i, new ModifiedFunction(T_SECTIONS[sections[i].ordinal()][i], manualFactor, 0.0));
			actMSectionsBack[N_SECTIONS - i - 1] = new TimeFunctionDelayFlow(this, STR_SECTION + " (back)" + (N_SECTIONS - i - 1), new ModifiedFunction(T_SECTIONS[sections[i].ordinal()][i], manualFactor, 0.0));
		}
		
		// Conectamos los tramos de la ruta que siguen las sillas
		for (int i = 1; i < N_SECTIONS; i++) {
			actASections[i-1].link(actASections[i]);
			actASectionsBack[i - 1].link(actASectionsBack[i]);
			actMSections[i-1].link(actMSections[i]);
			actMSectionsBack[i - 1].link(actMSectionsBack[i]);
		}

		final ExclusiveChoiceFlow condFlow0 = new ExclusiveChoiceFlow(this);
		
		// If the chair is automated, release the janitor after being seated 
		reqChair.link(condFlow0);
		condFlow0.link(relJanitorAfterSeat, new ResourceTypeAcquiredCondition(rtAChair)).link(actASections[0]);
		condFlow0.link(actMSections[0]);

		actMSections[N_SECTIONS - 1].link(relJanitorBeforeAppointment).link(actMAppointment).link(reqJanitor).link(actMSectionsBack[0]);
		actASections[N_SECTIONS - 1].link(actAAppointment).link(actASectionsBack[0]);
		
		// Creamos una actividad para levantarse de cada tipo de silla
		final DelayFlow delMStand = new TimeFunctionDelayFlow(this, STR_M_STAND, T_M_STAND);
		final ActivityFlow actAStand = new ActivityFlow(this, STR_A_STAND);
		// En el caso de las sillas autom�ticas, requiere un bedel
		actAStand.newWorkGroupAdder(wgJanitor).withDelay(T_A_STAND).add();
		actMSectionsBack[N_SECTIONS - 1].link(delMStand).link(relChair);
		actASectionsBack[N_SECTIONS - 1].link(actAStand).link(relChair);
		
		//Horario de llegada de pacientes
		final SimulationPeriodicCycle arrivalCycle = new SimulationPeriodicCycle(unit, 0, new SimulationTimeFunction(unit, "ConstantVariate", minutesBetweenArrivals * 60), (int)(LAST_ARRIVAL / (minutesBetweenArrivals * 60)));
		new TimeDrivenElementGenerator(this, patientsPerArrival, etPatient, reqChair, arrivalCycle);
		endCondition = new EveryoneOutListener();
		addInfoReceiver(endCondition);
	}

	//FIXME: Doesn't work
	@Override
	public boolean isSimulationEnd(long currentTs) {
		return super.isSimulationEnd(currentTs) && endCondition.isEveryoneOut();
	}
	
	private static void restartTimeFunctions() {
		T_APPOINTMENT.restart();
		T_A_SEAT.restart();
		T_M_SEAT.restart();
		T_A_STAND.restart();
		T_M_STAND.restart();
		for (ElementReplicableTimeFunction []functions : T_SECTIONS) {
			for (ElementReplicableTimeFunction f : functions) {
				f.restart();
			}
		}
	}
	
	public static void resetTimeFunctions() {
		T_APPOINTMENT.reset();
		T_A_SEAT.reset();
		T_M_SEAT.reset();
		T_A_STAND.reset();
		T_M_STAND.reset();
		for (ElementReplicableTimeFunction []functions : T_SECTIONS) {
			for (ElementReplicableTimeFunction f : functions) {
				f.reset();
			}
		}
	}
	
	private static class ModifiedFunction extends TimeFunction {
		private double scale;
		private double offset;
		private TimeFunction function;
		
		/**
		 * @param scale
		 * @param offset
		 */
		public ModifiedFunction(TimeFunction function, double scale, double offset) {
			this.scale = scale;
			this.offset = offset;
			this.function = function;
		}

		@Override
		public double getValue(TimeFunctionParams params) {
			return scale * function.getValue(params) + offset;
		}

		@Override
		public void setParameters(Object... params) {
			// TODO Auto-generated method stub
			
		}
		
	}
}

