package es.ull.iis.simulation.whellChairs;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
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
import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;

/*Ejemplo de implementación de recursos, elementos y actividades. 
 *Autores: Jonel Alexander Rodríguez Rodríguez y Raquel Rodríguez Díaz
*/

public class BasicHUNSCsimulationManual extends Simulation {
	
	final static private int N_BEDELES = 2; //número de bedeles que están en el modelo
	final static private int N_TRAMOS = 3;  //número de tramos del trayecto
	final static private TimeFunction[] T_TRAMO_10 = {TimeFunctionFactory.getInstance("NormalVariate", 0.75, 0.01653), TimeFunctionFactory.getInstance("NormalVariate", 1.60, 0.011), TimeFunctionFactory.getInstance("NormalVariate", 0.73, 0.011 )};
	final static private TimeFunction[] T_TRAMO_25 = {TimeFunctionFactory.getInstance("NormalVariate", 0.79, 0.017), TimeFunctionFactory.getInstance("NormalVariate", 1.79, 0.056), TimeFunctionFactory.getInstance("NormalVariate", 0.61, 0.054 )};
	final static private TimeFunction[] T_TRAMO_50 = {TimeFunctionFactory.getInstance("NormalVariate", 0.82 * 60, 0.018 * 60), TimeFunctionFactory.getInstance("NormalVariate", 1.86 * 60, 0.063 * 60), TimeFunctionFactory.getInstance("NormalVariate", 0.77 * 60, 0.012 * 60 )};
	final static private TimeFunction[] T_TRAMO_100 = {TimeFunctionFactory.getInstance("NormalVariate", 0.896 * 60, 0.054 * 60), TimeFunctionFactory.getInstance("NormalVariate", 2.038 * 60, 0.075 * 60), TimeFunctionFactory.getInstance("NormalVariate", 0.895 * 60, 0.019 * 60 )};
	final static private int N_DOCTORES = 2;
	final static private int N_SILLAS_MANUALES = 20;
	/**
	 * @param id
	 * @param description
	 * @param unit
	 * @param startTs
	 * @param endTs
	 */
	public BasicHUNSCsimulationManual(int id, String description, TimeUnit unit, long startTs, long endTs) {
		super(id, description, unit, startTs, endTs);
		
		// El paciente es el elemento del modelo
		
		ElementType etPaciente = new ElementType(this, "Paciente");
		
		// Los recursos son: Doctores y Silla
		ResourceType rtBedel = new ResourceType(this, "Bedel");
		// Simplificando
		Resource[] resBedel = rtBedel.addGenericResources(N_BEDELES);

		ResourceType rtSillaM = new ResourceType(this, "Silla Manual");
		Resource[] resSillaM;
		resSillaM = new Resource[N_SILLAS_MANUALES];
				
		for(int i = 0; i < N_SILLAS_MANUALES; i++){
			resSillaM[i] = new Resource(this, "Silla Manual" + i);
			resSillaM[i].addTimeTableEntry(rtSillaM);
		}

		ResourceType rtDoctor = new ResourceType(this, "Doctor");
		Resource[] resDoctores = rtDoctor.addGenericResources(N_DOCTORES);
		
		//Horario de llegada de pacientes
		
		SimulationPeriodicCycle llegadaPacientesCycle = new SimulationPeriodicCycle(unit, 8 * 60 * 60, new SimulationTimeFunction(unit, "ConstantVariate", 30 * 60), 19 * 60 * 60L);

		// Distinguimos dos actividades: consulta y desplazamiento
		
		ActivityFlow actAppointment = new ActivityFlow(this, "Consulta");
		
		ActivityFlow[] actTramosIda;
		actTramosIda = new ActivityFlow [N_TRAMOS];
		
		ActivityFlow[] actTramosVuelta;
		actTramosVuelta = new ActivityFlow [N_TRAMOS];
			
		//Definición de los flujos de trabajo
		
		WorkGroup wgBedelSilla = new WorkGroup(this, new ResourceType[] {rtBedel, rtSillaM}, new int[] {1,1});
		WorkGroup wgBedel = new WorkGroup(this, rtBedel, 1);

		RequestResourcesFlow reqBedelSilla = new RequestResourcesFlow(this, "Pedir Silla y Bedel", 1);
		reqBedelSilla.addWorkGroup(1, wgBedelSilla);

		ReleaseResourcesFlow relBedel = new ReleaseResourcesFlow(this, "Soltar Bedel", 1, wgBedel);
		RequestResourcesFlow reqBedel = new RequestResourcesFlow(this, "Pedir Bedel", 2);
		reqBedel.addWorkGroup(wgBedel);
		
		ReleaseResourcesFlow relSilla = new ReleaseResourcesFlow(this, "Soltar Silla", 1);
		ReleaseResourcesFlow relBedel2 = new ReleaseResourcesFlow(this, "Soltar Bedel again", 2);
		
		WorkGroup wgConsulta = new WorkGroup(this, new ResourceType[] {rtDoctor} , new int [] {1, 1});
		WorkGroup wgTramos = new WorkGroup(this);

		
		// Assign duration and workgroups to activities
		
		actAppointment.addWorkGroup(0, wgConsulta, TimeFunctionFactory.getInstance("UniformVariate", 50 * 60, 60 * 60));
		
		// Creamos los tramos de la ruta que siguen las sillas
		
		for (int i = 0; i < N_TRAMOS; i++) {
			
			actTramosIda[i] = new ActivityFlow(this, "Tramo (ida)" + i);
			actTramosIda[i].addWorkGroup(0, wgTramos,  T_TRAMO_50[i]);
			
			actTramosVuelta[i] = new ActivityFlow(this, "Tramo (vuelta)" + i);
			actTramosVuelta[i].addWorkGroup(0, wgTramos, T_TRAMO_50[i]);
		}
		
		
		reqBedelSilla.link(actTramosIda[0]);
		for (int i = 1; i < N_TRAMOS; i++) {
			actTramosIda[i-1].link(actTramosIda[i]);
		}
		
		actTramosIda[N_TRAMOS - 1].link(relBedel);
		relBedel.link(actAppointment);
		actAppointment.link(reqBedel);
		reqBedel.link(actTramosVuelta[N_TRAMOS - 1]);
		
		for (int i = N_TRAMOS - 1; i > 0; i--) {
			actTramosVuelta[i].link(actTramosVuelta[i-1]);
		}
		
		actTramosVuelta[0].link(relSilla).link(relBedel2);
		
		//Crea el elemento, el paciente
		
		new TimeDrivenElementGenerator(this, 3, etPaciente, reqBedelSilla, llegadaPacientesCycle);
	}

}

