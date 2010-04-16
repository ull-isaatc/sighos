/**
 * 
 */
package es.ull.isaatc.simulation.common.factory;

import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class SimulationFactory {
	/**
	 * Define los distintos tipos de simulaci�n que pueden probarse<ul>
	 * <il>SEQUENTIAL: Ejecuci�n secuencial</li>
	 * <il>SEQUENTIAL2: Ejecuci�n secuencial optimizada</li>
	 * <il>EXTERNALPOOL: Ejecuci�n que intenta optimizar los eventos simult�neos usando un pool de threads</li>
	 * <il>EXTERNALPOOL3PHASE: Ejecuci�n que intenta optimizar los eventos simult�neos usando un pool de threads. En este caso se
	 * trata de mejorar el resultado haciendo una ejecuci�n en dos fases (m�s propiamente en 3) que elimina muchos de los bloqueos.</li>
	 * <il>INTERVAL: Modificaci�n de SIMEVENTS para que se pueda adaptar en el futuro para una ejecuci�n con reloj intervalar.
	 * La mayor diferencia es que los hilos de ejecuci�n se definen internamente a la simulaci�n.</li>
	 * <il>BUFFERED: Versi�n modificada de INTERVAL que incluye buffers internos a cada hilo de ejecuci�n para almacenar
	 * localmente eventos de pr�xima ejecuci�n y eventos a planificar en la lista de espera</li>
	 * <il>GROUPED: Versi�n modificada de BUFFERED que env�a los eventos en bloques. Esto tambi�n obliga a modificar la
	 * cola de eventos futuros para que los almacene en bloques.</li>
	 * <il>GROUPEDX: Extensi�n de GROUPED que utiliza el hilo principal (que reparte los eventos) como otro hilo ejecutor m�s</li>
	 * <il>GROUPED3PHASE: H�brido de GROUPED y SIMEVENTS3PHASE , que elimina completamente los sem�foros del AM.</li>
	 * <il>GROUPED3PHASEX: Extensi�n de GROUPED3PHASE que no tiene un hilo principal controlando.</li>
	 * <il>BONN3PHASE: Modificaci�n de GROUPED3PHASE que usa la barrera Bonn.</li>
	 * <il>BONNGROUPEDX: Modificaci�n de GROUPEDX que usa la barrera Bonn.</li>
	 * <il>BONNGROUPEDXX: Modificaci�n de BONNGROUPEDX que intenta paralelizar un poquito m�s usando las ideas de Patrick de 
	 * que cada hilo actualice la lista de eventos futuros.</li>
	 * @author Iv�n Castilla Rodr�guez
	 */
	public enum SimulationType {
		SEQUENTIAL, 
		SEQUENTIAL2, 
		EXTERNALPOOL,
		EXTERNALPOOL3PHASE,
		INTERVAL,
		BUFFERED,
		GROUPED,
		GROUPEDX,
		GROUPEDXX,
		PASIVE,
		GROUPED3PHASE,
		GROUPED3PHASEX,
//		GROUPEDXHYBRID,
		BONN3PHASE,
		BONNGROUPEDX,
		BONNGROUPEDXX
		
	}
	public static SimulationObjectFactory getInstance(SimulationType type, int id, String description, TimeUnit unit, TimeStamp startTs, TimeStamp endTs) {
		switch (type) {
		case SEQUENTIAL: return new es.ull.isaatc.simulation.sequential.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case SEQUENTIAL2: return new es.ull.isaatc.simulation.sequential.factory.SimulationFactory(id, description, true, unit, startTs, endTs);
		case EXTERNALPOOL: return new es.ull.isaatc.simulation.threaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case EXTERNALPOOL3PHASE: return new es.ull.isaatc.simulation.optThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case INTERVAL: return new es.ull.isaatc.simulation.intervalThreaded.factory.SimulationFactory(id, description, false, unit, startTs, endTs);
		case BUFFERED: return new es.ull.isaatc.simulation.intervalThreaded.factory.SimulationFactory(id, description, true, unit, startTs, endTs);
		case GROUPED: return new es.ull.isaatc.simulation.groupedThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case GROUPEDX: return new es.ull.isaatc.simulation.groupedExtraThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case GROUPEDXX: return new es.ull.isaatc.simulation.halfSeqGroupedExtraThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case PASIVE: return new es.ull.isaatc.simulation.groupedExtraPasiveThreads.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case GROUPED3PHASE: return new es.ull.isaatc.simulation.grouped3Phase.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case GROUPED3PHASEX: return new es.ull.isaatc.simulation.groupedExtra3Phase.factory.SimulationFactory(id, description, unit, startTs, endTs);
//		case GROUPEDXHYBRID: return new es.ull.isaatc.simulation.groupedXHybrid.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case BONN3PHASE: return new es.ull.isaatc.simulation.bonn3Phase.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case BONNGROUPEDX: return new es.ull.isaatc.simulation.bonnThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case BONNGROUPEDXX: return new es.ull.isaatc.simulation.bonnXThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		}
		return null;
	}

	public static SimulationObjectFactory getInstance(SimulationType type, int id, String description, TimeUnit unit, long startTs, long endTs) {
		switch (type) {
		case SEQUENTIAL: return new es.ull.isaatc.simulation.sequential.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case SEQUENTIAL2: return new es.ull.isaatc.simulation.sequential.factory.SimulationFactory(id, description, true, unit, startTs, endTs);
		case EXTERNALPOOL: return new es.ull.isaatc.simulation.threaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case EXTERNALPOOL3PHASE: return new es.ull.isaatc.simulation.optThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case INTERVAL: return new es.ull.isaatc.simulation.intervalThreaded.factory.SimulationFactory(id, description, false, unit, startTs, endTs);
		case BUFFERED: return new es.ull.isaatc.simulation.intervalThreaded.factory.SimulationFactory(id, description, true, unit, startTs, endTs);
		case GROUPED: return new es.ull.isaatc.simulation.groupedThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case GROUPEDX: return new es.ull.isaatc.simulation.groupedExtraThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case GROUPEDXX: return new es.ull.isaatc.simulation.halfSeqGroupedExtraThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case PASIVE: return new es.ull.isaatc.simulation.groupedExtraPasiveThreads.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case GROUPED3PHASE: return new es.ull.isaatc.simulation.grouped3Phase.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case GROUPED3PHASEX: return new es.ull.isaatc.simulation.groupedExtra3Phase.factory.SimulationFactory(id, description, unit, startTs, endTs);
//		case GROUPEDXHYBRID: return new es.ull.isaatc.simulation.groupedXHybrid.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case BONN3PHASE: return new es.ull.isaatc.simulation.bonn3Phase.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case BONNGROUPEDX: return new es.ull.isaatc.simulation.bonnThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		case BONNGROUPEDXX: return new es.ull.isaatc.simulation.bonnXThreaded.factory.SimulationFactory(id, description, unit, startTs, endTs);
		}
		return null;
	}
}
