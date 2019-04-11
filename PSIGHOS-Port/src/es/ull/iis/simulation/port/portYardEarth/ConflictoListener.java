package es.ull.iis.simulation.port.portYardEarth;

import java.util.TreeMap;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.inforeceiver.Listener;


public class ConflictoListener extends Listener {
	
	// Esta variable se procesa igual que promedioGlobal en TiempoEstanciaListener
	protected static double contadorEsperaInicioGlobal = 0.0;
	protected static double contadorReservaGruaGlobal = 0.0;
	protected static double contadorTramo1Global = 0.0;
	protected static double contadorTramo2Global = 0.0;
	protected static double contadorTramo3Global = 0.0;
	protected static double contadorTramo4Global = 0.0;
	
	protected static double promedioEsperaInicioGlobal = 0.0;
	protected static double promedioReservaGruaGlobal = 0.0;
	protected static double promedioTramo1Global = 0.0;
	protected static double promedioTramo2Global = 0.0;
	protected static double promedioTramo3Global = 0.0;
	protected static double promedioTramo4Global = 0.0;
	
	TreeMap<Integer, Long> esperaInicio = new TreeMap<Integer, Long>();
	TreeMap<Integer, Long> reservaGrua = new TreeMap<Integer, Long>();
	TreeMap<Integer, Long> esperaFin = new TreeMap<Integer, Long>();
	TreeMap<Integer, Long> reservaGruaFin = new TreeMap<Integer, Long>();
	TreeMap<Integer, Long> tramo1 = new TreeMap<Integer, Long>();
	TreeMap<Integer, Long> tramo2 = new TreeMap<Integer, Long>();
	TreeMap<Integer, Long> tramo3 = new TreeMap<Integer, Long>();
	TreeMap<Integer, Long> tramo4 = new TreeMap<Integer, Long>();
	TreeMap<Integer, Long> tramo1Fin = new TreeMap<Integer, Long>();
	TreeMap<Integer, Long> tramo2Fin = new TreeMap<Integer, Long>();
	TreeMap<Integer, Long> tramo3Fin = new TreeMap<Integer, Long>();
	TreeMap<Integer, Long> tramo4Fin = new TreeMap<Integer, Long>();
	TreeMap<Integer, Long> InicioParking = new TreeMap<Integer, Long>();
	TreeMap<Integer, Long> InicioGrua = new TreeMap<Integer, Long>();

	private int nSim;
	public ConflictoListener(int nSim){
		super("");
		addEntrance(ElementActionInfo.class);
		addEntrance(SimulationStartStopInfo.class);
		this.nSim = nSim;
	}
	
	public void infoEmited(SimulationInfo info){
		if (info instanceof ElementActionInfo){
			final ElementActionInfo eInfo = (ElementActionInfo) info;
			final int conflictoId = eInfo.getElement().getIdentifier();
			switch (eInfo.getType()){
			case REQ:
				// Lo ponemos en negativo para identificarlo en caso que llegue el final de simulación y no 
				// se haya cogido el recurso correspondiente
				if(eInfo.getActivity().getDescription().contains(PortSimulation.LLEGADA)){
					esperaInicio.put(conflictoId, -eInfo.getTs());
				}
				if(eInfo.getActivity().getDescription().contains(PortSimulation.GRUAIN)){
					reservaGrua.put(conflictoId, eInfo.getTs());
				}
				if(eInfo.getActivity().getDescription().contains(PortSimulation.ON_TRAMO1)){
					tramo1.put(conflictoId, eInfo.getTs());
				}
				if(eInfo.getActivity().getDescription().contains(PortSimulation.ON_TRAMO2)){
					tramo2.put(conflictoId, eInfo.getTs());
				}
				if(eInfo.getActivity().getDescription().contains(PortSimulation.ON_TRAMO3)){
					tramo3.put(conflictoId, eInfo.getTs());
				}
				if(eInfo.getActivity().getDescription().contains(PortSimulation.ON_TRAMO4)){
					tramo4.put(conflictoId, eInfo.getTs());
				}
				break;
			case ACQ:
				if(eInfo.getActivity().getDescription().contains(PortSimulation.LLEGADA)){
					if (eInfo.getTs() > -esperaInicio.get(conflictoId)) {
						esperaInicio.put(conflictoId, eInfo.getTs() + esperaInicio.get(conflictoId));//tiempo que esta en cola o esperando por el aparcamiento
					}
					else {
						esperaInicio.remove(conflictoId);
					}	
				}
				if(eInfo.getActivity().getDescription().contains(PortSimulation.GRUAIN)){
					if (eInfo.getTs() > reservaGrua.get(conflictoId)){
						reservaGrua.put(conflictoId, eInfo.getTs() - reservaGrua.get(conflictoId));//tiempo que espera por el recurso grua
					}
				else{
					reservaGrua.remove(conflictoId);
					}
				}
				
				if(eInfo.getActivity().getDescription().contains(PortSimulation.ON_TRAMO1)){
					if (eInfo.getTs() > tramo1.get(conflictoId)){
						tramo1.put(conflictoId, eInfo.getTs() - tramo1.get(conflictoId));//tiempo que espera por el recurso tramo 1
				}
				else{
					tramo1.remove(conflictoId);
					}
				}
				if(eInfo.getActivity().getDescription().contains(PortSimulation.ON_TRAMO2)){
					if (eInfo.getTs() > tramo2.get(conflictoId)){
						tramo2.put(conflictoId, eInfo.getTs() - tramo2.get(conflictoId));//tiempo que espera por el recurso tramo 2
				}
				else{
					tramo2.remove(conflictoId);
					}
				}
				if(eInfo.getActivity().getDescription().contains(PortSimulation.ON_TRAMO3)){
					if (eInfo.getTs() > tramo3.get(conflictoId)){
						tramo3.put(conflictoId, eInfo.getTs() - tramo3.get(conflictoId));//tiempo que espera por el recurso tramo 3
				}
				else{
					tramo3.remove(conflictoId);
					}
				}
				if(eInfo.getActivity().getDescription().contains(PortSimulation.ON_TRAMO4)){
					if (eInfo.getTs() > tramo4.get(conflictoId)){
						tramo4.put(conflictoId, eInfo.getTs() - tramo4.get(conflictoId));//tiempo que espera por el recurso tramo 4
				}
				else{
					tramo4.remove(conflictoId);
					}
				}
				break;
			case END:
				if(eInfo.getActivity().getDescription().contains(PortSimulation.LLEGADA)){
					esperaFin.put(conflictoId, eInfo.getTs());
				}
				if(eInfo.getActivity().getDescription().contains(PortSimulation.GRUAIN)){
					reservaGruaFin.put(conflictoId, eInfo.getTs());
				}
				if(eInfo.getActivity().getDescription().contains(PortSimulation.ON_TRAMO1)){
					tramo1Fin.put(conflictoId, eInfo.getTs());
				}
				if(eInfo.getActivity().getDescription().contains(PortSimulation.ON_TRAMO2)){
					tramo2Fin.put(conflictoId, eInfo.getTs());
				}
				if(eInfo.getActivity().getDescription().contains(PortSimulation.ON_TRAMO3)){
					tramo3Fin.put(conflictoId, eInfo.getTs());
				}
				if(eInfo.getActivity().getDescription().contains(PortSimulation.ON_TRAMO4)){
					tramo4Fin.put(conflictoId, eInfo.getTs());
				}
				break;
			case START:
				if(eInfo.getActivity().getDescription().contains(PortSimulation.LLEGADA)){
					InicioParking.put(conflictoId, eInfo.getTs());
				}
				if(eInfo.getActivity().getDescription().contains(PortSimulation.GRUAIN)){
					InicioGrua.put(conflictoId, eInfo.getTs());
				}
				
				break;
			default:
				break;
			}
		}
		else if (info instanceof SimulationStartStopInfo) {
			if (SimulationStartStopInfo.Type.END.equals(((SimulationStartStopInfo)info).getType())) {
				long endTs = ((SimulationStartStopInfo) info).getTs();
				
				contadorEsperaInicioGlobal = contadorEsperaInicioGlobal + esperaInicio.size() / (double) nSim;
				contadorReservaGruaGlobal = contadorReservaGruaGlobal + reservaGrua.size() / (double) nSim;
				contadorTramo1Global = contadorTramo1Global + tramo1.size() / (double) nSim;
				contadorTramo2Global = contadorTramo2Global + tramo2.size() / (double) nSim;
				contadorTramo3Global = contadorTramo3Global + tramo3.size() / (double) nSim;
				contadorTramo4Global = contadorTramo4Global + tramo4.size() / (double) nSim;
				
				System.out.println("\tTipo conflicto\tNúmero de conflictos");
				System.out.println("\tAparcamiento "+"\t\t" + String.format("%.2f",contadorEsperaInicioGlobal));
				System.out.println("\tGrúa " + "\t\t\t" + String.format("%.2f",contadorReservaGruaGlobal));
				System.out.println("\tTramo 1 " + "\t\t" + String.format("%.2f",contadorTramo1Global));
				System.out.println("\tTramo 2 " + "\t\t" + String.format("%.2f",contadorTramo2Global));
				System.out.println("\tTramo 3 " + "\t\t" + String.format("%.2f",contadorTramo3Global));
				System.out.println("\tTramo 4 " + "\t\t" + String.format("%.2f",contadorTramo4Global));
				
		
				System.out.println("\tConflicto\tPetición\tTiempo de espera");
				promedioEsperaInicioGlobal = promedioEsperaInicioGlobal + (limpiaYCalculaConflictos(esperaInicio, "Aparcamiento", endTs) / (double)esperaInicio.size()) / (double)nSim;
				promedioReservaGruaGlobal = promedioReservaGruaGlobal + (limpiaYCalculaConflictos(reservaGrua, "Grúa", endTs) / (double)reservaGrua.size()) / (double)nSim;
				promedioTramo1Global = promedioTramo1Global + (limpiaYCalculaConflictos(tramo1, "Tramo 1", endTs) / (double) tramo1.size()) / (double)nSim;
				promedioTramo2Global = promedioTramo2Global + (limpiaYCalculaConflictos(tramo2, "Tramo 2", endTs) / (double) tramo2.size()) / (double)nSim;
				promedioTramo3Global = promedioTramo3Global + (limpiaYCalculaConflictos(tramo3, "Tramo 3", endTs) / (double) tramo3.size()) / (double)nSim;
				promedioTramo4Global = promedioTramo4Global + (limpiaYCalculaConflictos(tramo4, "Tramo 4", endTs) / (double) tramo4.size()) / (double)nSim;
			}
		}
	}

//Este es un metodo que utilizamos para comprobar que no hayan peticiones de recursos que no se hayan atendido
//antes de terminar la simulación
	
	long limpiaYCalculaConflictos(TreeMap<Integer, Long> conflictos, String texto, long endTs) {
		// Comprobamos que no haya peticiones de recursos que no se hayan atendido antes de terminar la simulación
		for (Integer conflictoId : conflictos.keySet()) {		
			if (conflictos.get(conflictoId) < 0) {
				if (-conflictos.get(conflictoId) < endTs) {
					conflictos.put(conflictoId, endTs + conflictos.get(conflictoId));//tiempo que esta en cola o esperando por el aparcamiento
				}
				else {
					conflictos.remove(conflictoId);
				}
			}
		}
		long sumaconflictos = 0;
		for (Integer conflictoId : conflictos.keySet()) {		
			System.out.println( "\t" + texto + "  "+ "\t" +" "+ conflictoId + "\t\t" +   conflictos.get(conflictoId));	
			sumaconflictos = sumaconflictos + conflictos.get(conflictoId);
		}
		return sumaconflictos;
	}
}
	



