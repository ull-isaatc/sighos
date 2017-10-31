package es.ull.iis.simulation.whellChairs;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.condition.PercentageCondition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.TimeDrivenElementGenerator;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.ExclusiveChoiceFlow;
import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;

/*Ejemplo de implementación de recursos, elementos y actividades. 
 *Autores: Jonel Alexander Rodríguez Rodríguez y Raquel Rodríguez Díaz
*/

public class BasicHUNSCsimulationAuto extends Simulation {
	final static private int N_TRAMOS = 3;  //número de tramos del trayecto
	final static private TimeFunction[] T_TRAMO_10 = {TimeFunctionFactory.getInstance("NormalVariate", 0.75, 0.017), TimeFunctionFactory.getInstance("NormalVariate", 1.60, 0.011), TimeFunctionFactory.getInstance("NormalVariate", 0.73, 0.011 )};
	final static private int N_DOCTORES = 2;
	final static private int N_SILLAS_AUTO = 20;
	/**
	 * @param id
	 * @param description
	 * @param unit
	 * @param startTs
	 * @param endTs
	 */
	public BasicHUNSCsimulationAuto(int id, String description, TimeUnit unit, long startTs, long endTs) {
		super(id, description, unit, startTs, endTs);
		
		// El paciente es el elemento del modelo
		
		ElementType etPaciente = new ElementType(this, "Paciente");
		
		// Los recursos son: Doctores y Silla
		
		ResourceType rtSillaA = new ResourceType(this, "Silla Automática");
		Resource[] resSillaA;
		resSillaA = new Resource[N_SILLAS_AUTO];
		
		for(int i = 0; i < N_SILLAS_AUTO; i++){
			resSillaA[i] = new Resource(this, "Silla Automática" + i);
			resSillaA[i].addTimeTableEntry(rtSillaA);
		}
		
		ResourceType rtDoctor = new ResourceType(this, "Doctor");
		Resource[] resDoctores = rtDoctor.addGenericResources(N_DOCTORES);
		
		//Horario de llegada de pacientes
		
		SimulationPeriodicCycle llegadaPacientesCycle = new SimulationPeriodicCycle(unit, 8 * 60, new SimulationTimeFunction(unit, "ConstantVariate", 30), 15 * 60L);

		// Distinguimos dos actividades: consulta y desplazamiento
		
		ActivityFlow actAppointment = new ActivityFlow(this, "Consulta");
		
		ActivityFlow[] actTramosIda;
		actTramosIda = new ActivityFlow [N_TRAMOS];
		
		ActivityFlow[] actTramosVuelta;
		actTramosVuelta = new ActivityFlow [N_TRAMOS];
		
		//Definición de los flujos de trabajo

		WorkGroup wgSillaAuto = new WorkGroup(this, rtSillaA, 1);
		
		RequestResourcesFlow reqSillaAuto = new RequestResourcesFlow(this, "Pedir Silla Auto", 1);
		reqSillaAuto.addWorkGroup(0, wgSillaAuto);
		
		ReleaseResourcesFlow relSillaAuto = new ReleaseResourcesFlow(this, "Soltar Silla Auto", 1);
		
		// Define the workgroups
		
		WorkGroup wgConsulta = new WorkGroup(this, new ResourceType[] {rtDoctor} , new int [] {1, 1});
		WorkGroup wgTramos = new WorkGroup(this);

		// Assign duration and workgroups to activities
		
		actAppointment.addWorkGroup(0, wgConsulta, TimeFunctionFactory.getInstance("UniformVariate", 50, 60));
		
		// Creamos los tramos de la ruta que siguen las sillas
		
		for (int i = 0; i < N_TRAMOS; i++) {
			
			actTramosIda[i] = new ActivityFlow(this, "Tramo (ida)" + i);
			actTramosIda[i].addWorkGroup(0, wgTramos,  T_TRAMO_10[i]);
			
			actTramosVuelta[i] = new ActivityFlow(this, "Tramo (vuelta)" + i);
			actTramosVuelta[i].addWorkGroup(0, wgTramos, T_TRAMO_10[i]);
		}
		
		reqSillaAuto.link(actTramosIda[0]);
		for (int i = 1; i < N_TRAMOS; i++) {
			actTramosIda[i-1].link(actTramosIda[i]);
		}
		
		actTramosIda[N_TRAMOS - 1].link(actAppointment);
		actAppointment.link(actTramosVuelta[N_TRAMOS - 1]);
		
		for (int i = N_TRAMOS - 1; i > 0; i--) {
			actTramosVuelta[i].link(actTramosVuelta[i-1]);
		}
		
		actTramosVuelta[0].link(relSillaAuto);
	
		//Crea el elemento, el paciente
		
		new TimeDrivenElementGenerator(this, 2, etPaciente, reqSillaAuto, llegadaPacientesCycle);
	}

}

