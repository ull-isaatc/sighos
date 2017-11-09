package es.ull.iis.simulation.whellChairs;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
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

/**
 * Ejemplo de implementación de recursos, elementos y actividades. 
 * @author Jonel Alexander Rodríguez Rodríguez 
 * @author Raquel Rodríguez Díaz
*/

public class BasicHUNSCsimulation extends Simulation {
	final public static String STR_AUTO_CHAIR = "Silla Automática"; 
	final public static String STR_MANUAL_CHAIR = "Silla Manual"; 
	final public static String STR_JANITOR = "Bedel"; 
	final public static String STR_DOCTOR = "Doctor"; 
	final public static String STR_PATIENT = "Paciente";
	final public static String STR_M_APPOINTMENT = "Consulta con silla manual";
	final public static String STR_A_APPOINTMENT = "Consulta con silla automática";
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
	final private static TimeFunction[][] T_SECTIONS = 
		{{TimeFunctionFactory.getInstance("NormalVariate", 0.75 * 60, 0.01653 * 60), TimeFunctionFactory.getInstance("NormalVariate", 1.60 * 60, 0.011 * 60), TimeFunctionFactory.getInstance("NormalVariate", 0.73 * 60, 0.011 * 60)},
		{TimeFunctionFactory.getInstance("NormalVariate", 0.79 * 60, 0.017 * 60), TimeFunctionFactory.getInstance("NormalVariate", 1.79 * 60, 0.056 * 60), TimeFunctionFactory.getInstance("NormalVariate", 0.61 * 60, 0.054 * 60)},
		{TimeFunctionFactory.getInstance("NormalVariate", 0.82 * 60, 0.018 * 60), TimeFunctionFactory.getInstance("NormalVariate", 1.86 * 60, 0.063 * 60), TimeFunctionFactory.getInstance("NormalVariate", 0.77 * 60, 0.012 * 60 )},
		{TimeFunctionFactory.getInstance("NormalVariate", 0.896 * 60, 0.054 * 60), TimeFunctionFactory.getInstance("NormalVariate", 2.038 * 60, 0.075 * 60), TimeFunctionFactory.getInstance("NormalVariate", 0.895 * 60, 0.019 * 60 )}};
	final private static TimeFunction T_APPOINTMENT = TimeFunctionFactory.getInstance("UniformVariate", 10*60, 15*60);
	
	/**
	 * Creates a simulation based on minutes, which lasts for a week (7 days X 24 hours X 60 minutes X 60 seconds)
	 * @param id
	 */
	public BasicHUNSCsimulation(int id, Density[] sections, int nJanitors, int nDoctors, int nAutoChairs, int nManualChairs, int patientsPerArrival, int minutesBetweenArrivals) {
		super(id, "HUNSC" + id, unit, 0, END_TIME);
		
		// El paciente es el elemento del modelo
		
		ElementType etPatient = new ElementType(this, STR_PATIENT);
		
		// Los recursos son: Doctores y Silla
		
		ResourceType rtJanitor = new ResourceType(this, STR_JANITOR);
		// Simplificando
		rtJanitor.addGenericResources(nJanitors);
		
		ResourceType rtAChair = new ResourceType(this, STR_AUTO_CHAIR);
		rtAChair.addGenericResources(nAutoChairs);
		ResourceType rtMChair = new ResourceType(this, STR_MANUAL_CHAIR);
		rtMChair.addGenericResources(nManualChairs);
		
		ResourceType rtDoctor = new ResourceType(this, STR_DOCTOR);
		rtDoctor.addGenericResources(nDoctors);
		
		// Distinguimos dos actividades: consulta y desplazamiento
		
		ActivityFlow actMAppointment = new ActivityFlow(this, STR_M_APPOINTMENT);
		ActivityFlow actAAppointment = new ActivityFlow(this, STR_A_APPOINTMENT);
		
		//Caso Ruta Corta
		
		DelayFlow[] actSections;
		actSections = new DelayFlow [N_SECTIONS];
		
		DelayFlow[] actSectionsBack;
		actSectionsBack = new DelayFlow [N_SECTIONS];
		
		//Definición de los flujos de trabajo
		
		WorkGroup wgJanitorChair = new WorkGroup(this, new ResourceType[] {rtJanitor, rtMChair}, new int[] {1,1});
		WorkGroup wgJanitor = new WorkGroup(this, rtJanitor, 1);
		WorkGroup wgAChair = new WorkGroup(this, rtAChair, 1);
		WorkGroup wgAppointment = new WorkGroup(this, new ResourceType[] {rtDoctor} , new int [] {1});

		// Assign duration and workgroups to activities

		RequestResourcesFlow reqChair = new RequestResourcesFlow(this, "Pedir Silla", 1, 2);
		reqChair.addWorkGroup(0, wgAChair);       //Definir prioridad 
		reqChair.addWorkGroup(1, wgJanitorChair);

		ReleaseResourcesFlow relJanitor = new ReleaseResourcesFlow(this, "Soltar Bedel", 1, wgJanitor);
		RequestResourcesFlow reqJanitor = new RequestResourcesFlow(this, "Pedir Bedel", 1, 1);
		reqJanitor.addWorkGroup(wgJanitor);
		
		ReleaseResourcesFlow relChair = new ReleaseResourcesFlow(this, "Soltar Silla", 1);
		
		actMAppointment.addWorkGroup(0, wgAppointment, T_APPOINTMENT);
		actAAppointment.addWorkGroup(0, wgAppointment, T_APPOINTMENT);
		
		// Creamos los tramos de la ruta que siguen las sillas
		for (int i = 0; i < N_SECTIONS; i++) {
			actSections[i] = new DelayFlow(this, "Section" + i, T_SECTIONS[sections[i].ordinal()][i]);
			actSectionsBack[N_SECTIONS - i - 1] = new DelayFlow(this, "Section (back)" + (N_SECTIONS - i - 1), T_SECTIONS[sections[i].ordinal()][i]);
		}
		
		// Conectamos los tramos de la ruta que siguen las sillas
		for (int i = 1; i < N_SECTIONS; i++) {
			actSections[i-1].link(actSections[i]);
			actSectionsBack[i - 1].link(actSectionsBack[i]);
		}

		reqChair.link(actSections[0]);

		ExclusiveChoiceFlow condFlow = new ExclusiveChoiceFlow(this);
		
		actSections[N_SECTIONS - 1].link(condFlow);
		
		condFlow.link(relJanitor, new ResourceTypeAcquiredCondition(rtMChair)).link(actMAppointment).link(reqJanitor).link(actSectionsBack[0]);
		condFlow.link(actAAppointment).link(actSectionsBack[0]);
		
		actSectionsBack[N_SECTIONS - 1].link(relChair);
		
		//Horario de llegada de pacientes
		SimulationPeriodicCycle arrivalCycle = new SimulationPeriodicCycle(unit, 0, new SimulationTimeFunction(unit, "ConstantVariate", minutesBetweenArrivals * 60), (int)(END_TIME / (minutesBetweenArrivals * 60)));
		new TimeDrivenElementGenerator(this, patientsPerArrival, etPatient, reqChair, arrivalCycle);
	}

}

