/**
 * 
 */
package es.ull.iis.simulation.port.portYardEarth;

import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.flow.DelayFlow;
import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.TimeDrivenElementGenerator;


/**
 * @author Daniel
 *
 */

public class PortSimulation extends Simulation {
			
// Se definen los tiempos de todas las actividades
	private static double ERROR = 0.25;
	private static final long MEDIA_DESCARGA_CONTENEDOR = 5*60;
	private static final double VARIANZA_DESCARGA_CONTENEDOR = ERROR * MEDIA_DESCARGA_CONTENEDOR * ERROR * MEDIA_DESCARGA_CONTENEDOR;
	private static final double ALFA_DESCARGA_CONTENEDOR = (MEDIA_DESCARGA_CONTENEDOR * MEDIA_DESCARGA_CONTENEDOR * 1.0) / VARIANZA_DESCARGA_CONTENEDOR;
	private static final double BETA_DESCARGA_CONTENEDOR = VARIANZA_DESCARGA_CONTENEDOR / (MEDIA_DESCARGA_CONTENEDOR * 1.0);
	
	private static final long MEDIA_TRANSFERENCIA = 3*60;
	private static final double VARIANZA_TRANSFERENCIA = ERROR * MEDIA_TRANSFERENCIA * ERROR * MEDIA_TRANSFERENCIA;
	private static final double ALFA_TRANSFERENCIA = (MEDIA_TRANSFERENCIA * MEDIA_TRANSFERENCIA * 1.0) / VARIANZA_TRANSFERENCIA;
	private static final double BETA_TRANSFERENCIA = VARIANZA_TRANSFERENCIA / (MEDIA_TRANSFERENCIA * 1.0);
	
	private static final long MEDIA_TRAMO_PATIO = 1*60; //Tramo1
	private static final double VARIANZA_TRAMO_PATIO = ERROR * MEDIA_TRAMO_PATIO * ERROR * MEDIA_TRAMO_PATIO;
	private static final double ALFA_TRAMO_PATIO = (MEDIA_TRAMO_PATIO * MEDIA_TRAMO_PATIO * 1.0) / VARIANZA_TRAMO_PATIO;
	private static final double BETA_TRAMO_PATIO = VARIANZA_TRAMO_PATIO / (MEDIA_TRAMO_PATIO * 1.0);
	
	private static final long MEDIA_TRAMO_CENTRO = 3*60; //Tramo2
	private static final double VARIANZA_TRAMO_CENTRO = ERROR * MEDIA_TRAMO_CENTRO * ERROR * MEDIA_TRAMO_CENTRO;
	private static final double ALFA_TRAMO_CENTRO = (MEDIA_TRAMO_CENTRO * MEDIA_TRAMO_CENTRO * 1.0) / VARIANZA_TRAMO_CENTRO;
	private static final double BETA_TRAMO_CENTRO = VARIANZA_TRAMO_CENTRO / (MEDIA_TRAMO_CENTRO * 1.0);
	
	private static final long MEDIA_TRAMO_TIERRA = 3*60; //Tramo3
	private static final double VARIANZA_TRAMO_TIERRA = ERROR * MEDIA_TRAMO_TIERRA * ERROR * MEDIA_TRAMO_TIERRA;
	private static final double ALFA_TRAMO_TIERRA = (MEDIA_TRAMO_TIERRA * MEDIA_TRAMO_TIERRA * 1.0) / VARIANZA_TRAMO_TIERRA;
	private static final double BETA_TRAMO_TIERRA = VARIANZA_TRAMO_TIERRA / (MEDIA_TRAMO_TIERRA * 1.0);
	
	private static final long MEDIA_CENTRO_VUELTA = 1*60; //Tramo4
	private static final double VARIANZA_CENTRO_VUELTA = ERROR * MEDIA_CENTRO_VUELTA * ERROR * MEDIA_CENTRO_VUELTA;
	private static final double ALFA_CENTRO_VUELTA = (MEDIA_CENTRO_VUELTA * MEDIA_CENTRO_VUELTA * 1.0) / VARIANZA_CENTRO_VUELTA;
	private static final double BETA_CENTRO_VUELTA = VARIANZA_CENTRO_VUELTA / (MEDIA_CENTRO_VUELTA * 1.0);
	
	private static final long MEDIA_VUELTA_PATIO1 = 1*60; //Tramo1Vuelta
	private static final double VARIANZA_VUELTA_PATIO1 = ERROR * MEDIA_VUELTA_PATIO1 * ERROR * MEDIA_VUELTA_PATIO1;
	private static final double ALFA_VUELTA_PATIO1 = (MEDIA_VUELTA_PATIO1* MEDIA_VUELTA_PATIO1 * 1.0) / VARIANZA_VUELTA_PATIO1;
	private static final double BETA_VUELTA_PATIO1 = VARIANZA_VUELTA_PATIO1 / (MEDIA_VUELTA_PATIO1* 1.0);
	
	
	protected static final String PETICION_A = "Peticion A";
	protected static final String LLEGADA = "LLegada de camion, coger aparcamiento";
	protected static final String FINAL = "Final. Dejar libre aparcamiento";
	protected static final String GRUAIN = "Coger grúa";
	protected static final String GRUAOFF = "Fin de grúa";
	protected static final String ON_TRAMO1 = "Coger tramo 1 del patio";
	protected static final String OFF_TRAMO1 = "Soltar tramo 1 del patio";
	protected static final String ON_TRAMO2 = "Coger tramo 2 del patio";
	protected static final String OFF_TRAMO2 = "Soltar tramo 2 del patio";
	protected static final String ON_TRAMO3 = "Coger tramo 3 del patio";
	protected static final String OFF_TRAMO3 = "Soltar tramo 3 del patio";
	protected static final String ON_TRAMO4 = "Coger tramo 4 del patio";
	protected static final String OFF_TRAMO4 = "Soltar tramo 4 del patio";
	protected static final String ON_TRAMO1VUELTA = "Retorno por tramo 1 del patio";
	protected static final String OFF_TRAMO1VUELTA = "Retorno para soltar tramo 1 del patio";
	protected static final String DESCARGA = "Localizacion y descarga del contenedor en el patio";
	private static final String TRANSFERENCIA = "Transferencia del contenedor grúa-camión";
	
	public PortSimulation(int id, String description, TimeUnit unit, long startTs, long endTs, int nGruas, int nCallesA,
			int nCamiones,int nCallesInt1,int nCallesInt2, int nCallesInt3, int nCallesInt4, long tramo1, long tramo2, 
			long tramo3, long tramo4, long tramo1Vuelta, long descarga, long transferencia) {
		
		super(id, "Simulación Puerto Patio-Tierra", unit, startTs, endTs);
		
//TIPOS DE ELEMENTOS DE NUESTRO PROGRAMA
//SIRVE PARA AGRUPAR A TODOS LOS ELEMENTOS DE UN TIPO CONCRETO
		
	ElementType Peticion = new ElementType(this, PETICION_A);
		
// TIPOS DE RECURSOS DE NUESTRO PROGRAMA 
	 ResourceType rtGrua = new ResourceType(this, "Grúa");
	 ResourceType rtParkingA = new ResourceType(this, "Aparcamiento Bloque A");
	 ResourceType rtTramoA1 = new ResourceType(this, "Tramo A1");
	 ResourceType rtTramoA2 = new ResourceType(this, "Tramo A2");	
	 ResourceType rtTramoA3 = new ResourceType(this, "Tramo A3");
	 ResourceType rtTramoA4 = new ResourceType(this, "Tramo A4");
	 
// RECURSO DE NUESTRO PROGRAMA
// SE DEFINEN A TRAVES DE LOS TIPOS DE RECURSOS
// CREAMOS LOS RECURSOS ESPECIFICOS
	 Resource[] resGruas = rtGrua.addGenericResources(nGruas);
	 Resource[] resParkingA = rtParkingA.addGenericResources(nCallesA);
	 Resource[] resTramoA1 = rtTramoA1.addGenericResources(nCallesInt1); //Tramo 1 tiene 2 carriles[0,1]
	 Resource[] resTramoA2 = rtTramoA2.addGenericResources(nCallesInt2); //Tramo 2 tiene 2 carriles[0,1]
	 Resource[] resTramoA3 = rtTramoA3.addGenericResources(nCallesInt3); //Tramo 3 tiene 2 carriles[0,1]
	 Resource[] resTramoA4 = rtTramoA4.addGenericResources(nCallesInt4); //Tramo 4 tiene 2 carriles[0,1]
		 
//CON ESTO DEFINIMOS LOS CAMIONES QUE LLEGARAN POR HORA
	SimulationPeriodicCycle CamionCycle = SimulationPeriodicCycle.newHourlyCycle(unit);
	
// WORKGROUP DE NUESTRO PROGRAMA
// REPRESENTAN EL NUMERO Y TIPO DE RECURSOS QUE NECESITO 
// PARA REALIZAR UNA TAREA
	  WorkGroup wgGrua = new WorkGroup(this, rtGrua, 1);
	  WorkGroup wgParkingA = new WorkGroup(this, rtParkingA, 1);
	  WorkGroup wgTramoA1 = new WorkGroup(this, rtTramoA1, 1);
	  WorkGroup wgTramoA2 = new WorkGroup(this, rtTramoA2, 1);
	  WorkGroup wgTramoA3 = new WorkGroup(this, rtTramoA3, 1);
	  WorkGroup wgTramoA4 = new WorkGroup(this, rtTramoA4, 1);
	  WorkGroup wgVacio = new WorkGroup(this);
		
// DEFINIMOS TODAS LAS ACCIONES QUE LLEVARA A CABO NUESTRO PROCESO
// TODO ELLO ESTA DEFINIDO EN PAPEL MEDIANTE UN ESQUEMA
		RequestResourcesFlow reqCamion = new RequestResourcesFlow(this, LLEGADA);
		reqCamion.newWorkGroupAdder(wgParkingA).addWorkGroup();
		ReleaseResourcesFlow relCamion = new ReleaseResourcesFlow(this, FINAL, wgParkingA);
		
		RequestResourcesFlow reqGrua = new RequestResourcesFlow(this, GRUAIN);
		reqGrua.newWorkGroupAdder(wgGrua).addWorkGroup();
		ReleaseResourcesFlow relGrua = new ReleaseResourcesFlow(this,GRUAOFF,wgGrua);
		
		RequestResourcesFlow reqTramoPatio1 = new RequestResourcesFlow(this, ON_TRAMO1);
		reqTramoPatio1.newWorkGroupAdder(wgTramoA1).addWorkGroup();
		ReleaseResourcesFlow relTramoPatio1 = new ReleaseResourcesFlow(this,OFF_TRAMO1,wgTramoA1);
		
		RequestResourcesFlow reqTramoCentro1 = new RequestResourcesFlow(this, ON_TRAMO2);
		reqTramoCentro1.newWorkGroupAdder(wgTramoA2).addWorkGroup();
		ReleaseResourcesFlow relTramoCentro1 = new ReleaseResourcesFlow(this,OFF_TRAMO2,wgTramoA2);
		
		RequestResourcesFlow reqtramotierra1 = new RequestResourcesFlow(this, ON_TRAMO3);
		reqtramotierra1.newWorkGroupAdder(wgTramoA3).addWorkGroup();
		ReleaseResourcesFlow reltramotierra1 = new ReleaseResourcesFlow(this,OFF_TRAMO3,wgTramoA3);
		
		RequestResourcesFlow reqtramocentro2 = new RequestResourcesFlow(this, ON_TRAMO4);
		reqtramocentro2.newWorkGroupAdder(wgTramoA4).addWorkGroup();
		ReleaseResourcesFlow reltramocentro2 = new ReleaseResourcesFlow(this,OFF_TRAMO4,wgTramoA4);
		
		RequestResourcesFlow reqtramopatio1vuelta = new RequestResourcesFlow(this, ON_TRAMO1VUELTA);
		reqtramopatio1vuelta.newWorkGroupAdder(wgVacio).addWorkGroup();
		ReleaseResourcesFlow reltramopatio1vuelta = new ReleaseResourcesFlow(this,OFF_TRAMO1VUELTA,wgVacio);
		
// SE DEFINEN LOS TIEMPOS QUE TARDARA EL PROCESO EN PASAR POR LOS TRAMOS DEL PATIO Y EN REALIZAR
// LAS ACTIVIDADES DE LOCALIZACION,DESCARGA, TRANSFERENCIA, ETC
		DelayFlow treqPatio1 = new DelayFlow(this, "Tiempo Tramo 1", TimeFunctionFactory.getInstance("GammaVariate", ALFA_TRAMO_PATIO, BETA_TRAMO_PATIO));
		DelayFlow treqCentro1 = new DelayFlow(this, "Tiempo Tramo 2", TimeFunctionFactory.getInstance("GammaVariate", ALFA_TRAMO_CENTRO, BETA_TRAMO_CENTRO));
		DelayFlow tDescargar = new DelayFlow(this, DESCARGA, TimeFunctionFactory.getInstance("GammaVariate", ALFA_DESCARGA_CONTENEDOR, BETA_DESCARGA_CONTENEDOR));
		DelayFlow treqTierra1 = new DelayFlow(this, "Tiempo Tramo 3", TimeFunctionFactory.getInstance("GammaVariate", ALFA_TRAMO_TIERRA, BETA_TRAMO_TIERRA));
		DelayFlow tTransferencia = new DelayFlow(this,TRANSFERENCIA,TimeFunctionFactory.getInstance("GammaVariate", ALFA_TRANSFERENCIA, BETA_TRANSFERENCIA ));
		DelayFlow treqCentro2 = new DelayFlow(this,"Tiempo Tramo 4", TimeFunctionFactory.getInstance("GammaVariate", ALFA_CENTRO_VUELTA, BETA_CENTRO_VUELTA));
		DelayFlow tTramopatio1vuelta = new DelayFlow(this, "Tiempo Tramo 1 vuelta", TimeFunctionFactory.getInstance("GammaVariate", ALFA_VUELTA_PATIO1, BETA_VUELTA_PATIO1));
		
//ACTIVIDADES LINKEADAS DE NUESTRO PROCESO. AQUI PODEMOS VER COMO FLUIRA NUESTRO PROCESO
		// Primero enlazo las acciones desde que se inicia el pedido hasta que la grúa llega al tramo central			
		reqCamion.link(reqGrua).link(reqTramoPatio1).link(treqPatio1).link(reqTramoCentro1).link(relTramoPatio1).link(treqCentro1);
		// Después enlazo con la descarga
		treqCentro1.link(tDescargar);
		// Ahora enlazo las acciones desde que se descarga hasta que la grúa llega a donde está el camión
		tDescargar.link(reqtramotierra1).link(relTramoCentro1).link(treqTierra1);
		// Ahora enlazo desde que se realiza la tranferencia hasta que empieza la vuelta de la grua hasta el tramo central de vuelta
		treqTierra1.link(tTransferencia).link(reqtramocentro2).link(reltramotierra1).link(treqCentro2);
		// Se enlazan las actividades que van desde el tramo central de vuelta hasta la zona de parking de las gruas
		treqCentro2.link(reqtramopatio1vuelta).link(reltramocentro2).link(tTramopatio1vuelta).link(reltramopatio1vuelta).link(relGrua).link(relCamion);
				
//GENERADOR DE ELEMENTOS
		TimeDrivenElementGenerator gen = new TimeDrivenElementGenerator(this, nCamiones, Peticion, reqCamion, CamionCycle);
	
	}

	public static double getERROR() {
		return ERROR;
	}

	public static void setERROR(double eRROR) {
		ERROR = eRROR;
	}	
	
}	
	
	
	



