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
	public enum Density {
		LOW,
		MEDIUM_LOW,
		MEDIUM_HIGH,
		HIGH
	}
	final public static int N_SECTIONS = 3; 
	final static private TimeFunction[][] T_SECTIONS = 
		{{TimeFunctionFactory.getInstance("NormalVariate", 0.75 * 60, 0.01653 * 60), TimeFunctionFactory.getInstance("NormalVariate", 1.60 * 60, 0.011 * 60), TimeFunctionFactory.getInstance("NormalVariate", 0.73 * 60, 0.011 * 60)},
		{TimeFunctionFactory.getInstance("NormalVariate", 0.79 * 60, 0.017 * 60), TimeFunctionFactory.getInstance("NormalVariate", 1.79 * 60, 0.056 * 60), TimeFunctionFactory.getInstance("NormalVariate", 0.61 * 60, 0.054 * 60)},
		{TimeFunctionFactory.getInstance("NormalVariate", 0.82 * 60, 0.018 * 60), TimeFunctionFactory.getInstance("NormalVariate", 1.86 * 60, 0.063 * 60), TimeFunctionFactory.getInstance("NormalVariate", 0.77 * 60, 0.012 * 60 )},
		{TimeFunctionFactory.getInstance("NormalVariate", 0.896 * 60, 0.054 * 60), TimeFunctionFactory.getInstance("NormalVariate", 2.038 * 60, 0.075 * 60), TimeFunctionFactory.getInstance("NormalVariate", 0.895 * 60, 0.019 * 60 )}};
	
	/**
	 * Creates a simulation based on minutes, which lasts for a week (7 days X 24 hours X 60 minutes X 60 seconds)
	 * @param id
	 */
	public BasicHUNSCsimulation(int id, Density[] sections, int nJanitors, int nDoctors, int nAutoChairs, int nManualChairs) {
		super(id, "HUNSC" + id, TimeUnit.SECOND, 0, 7 * 24 * 60 * 60);
		
		// El paciente es el elemento del modelo
		
		ElementType etPaciente = new ElementType(this, "Paciente");
		
		// Los recursos son: Doctores y Silla
		
		ResourceType rtBedel = new ResourceType(this, "Bedel");
		// Simplificando
		rtBedel.addGenericResources(nJanitors);
		
		ResourceType rtSillaA = new ResourceType(this, "Silla Automática");
		rtSillaA.addGenericResources(nAutoChairs);
		ResourceType rtSillaM = new ResourceType(this, "Silla Manual");
		rtSillaM.addGenericResources(nManualChairs);
		
		ResourceType rtDoctor = new ResourceType(this, "Doctor");
		rtDoctor.addGenericResources(nDoctors);
		
		//Horario de llegada de pacientes
		
		SimulationPeriodicCycle llegadaPacientesCycle = new SimulationPeriodicCycle(unit, 8 * 60 * 60, new SimulationTimeFunction(unit, "ConstantVariate", 30 * 60), 19 * 60 * 60L);

		// Distinguimos dos actividades: consulta y desplazamiento
		
		ActivityFlow actAppointmentManual = new ActivityFlow(this, "Consulta manual");
		ActivityFlow actAppointmentAuto = new ActivityFlow(this, "Consulta auto");
		
		//Caso Ruta Corta
		
		DelayFlow[] actSections;
		actSections = new DelayFlow [N_SECTIONS];
		
		DelayFlow[] actSectionsBack;
		actSectionsBack = new DelayFlow [N_SECTIONS];
		
		//Definición de los flujos de trabajo
		
		WorkGroup wgBedelSilla = new WorkGroup(this, new ResourceType[] {rtBedel, rtSillaM}, new int[] {1,1});
		WorkGroup wgBedel = new WorkGroup(this, rtBedel, 1);
		WorkGroup wgSillaAutomática = new WorkGroup(this, rtSillaA, 1);
		WorkGroup wgAppointment = new WorkGroup(this, new ResourceType[] {rtDoctor} , new int [] {1});

		// Assign duration and workgroups to activities

		RequestResourcesFlow reqSilla = new RequestResourcesFlow(this, "Pedir Silla", 1);
		reqSilla.addWorkGroup(0, wgSillaAutomática);       //Definir prioridad 
		reqSilla.addWorkGroup(1, wgBedelSilla);

		ReleaseResourcesFlow relBedel = new ReleaseResourcesFlow(this, "Soltar Bedel", 1, wgBedel);
		RequestResourcesFlow reqBedel = new RequestResourcesFlow(this, "Pedir Bedel", 1);
		reqBedel.addWorkGroup(wgBedel);
		
		ReleaseResourcesFlow relSilla = new ReleaseResourcesFlow(this, "Soltar Silla", 1);
		
		actAppointmentManual.addWorkGroup(0, wgAppointment, TimeFunctionFactory.getInstance("UniformVariate", 50, 60));
		actAppointmentAuto.addWorkGroup(0, wgAppointment, TimeFunctionFactory.getInstance("UniformVariate", 50, 60));
		
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

		reqSilla.link(actSections[0]);

		ExclusiveChoiceFlow condFlow = new ExclusiveChoiceFlow(this);
		
		actSections[N_SECTIONS - 1].link(condFlow);
		
		condFlow.link(relBedel, new ResourceTypeAcquiredCondition(rtSillaM)).link(actAppointmentManual).link(reqBedel).link(actSectionsBack[0]);
		condFlow.link(actAppointmentAuto).link(actSectionsBack[0]);
		
		actSectionsBack[N_SECTIONS - 1].link(relSilla);
		
		new TimeDrivenElementGenerator(this, 2, etPaciente, reqSilla, llegadaPacientesCycle);
	}

}

