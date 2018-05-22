/**
 * 
 */
package es.ull.iis.simulation.port.portYardEarth;

import java.util.TreeMap;
import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.View;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.util.Statistics;
/**
 * @author Daniel
 *
 */
public class TiempoEstanciaListener extends View {
	//Esto sirve para recoger el valor de las N simulaciones que se hagan
	protected static int nSim = 1;
	protected static double[] maxGlobal; 
	protected static double[] minGlobal;
	protected static double[] promedioGlobal;
	protected static double[] sumaGlobal;
	protected static double contadorGlobal[];
	protected static TreeMap<Element, Long> tEstancia;
	//Para recoger los datos internos de la clase, son propios de la clase
	private double max = 0.0;
	private Long min = Long.MAX_VALUE;
	private double promedio = 0.0;
	private double suma = 0.0;
	private Long contador = (long) 0;
	
	public TiempoEstanciaListener() {
		super("");
		tEstancia = new TreeMap<Element, Long>();
		addEntrance(ElementInfo.class);
		addEntrance(SimulationEndInfo.class);	
	}
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementInfo) {
			ElementInfo eInfo = (ElementInfo)info;
			switch(eInfo.getType()) {
			case FINISH:
				long tStart = tEstancia.get(eInfo.getElement());
				tEstancia.put(eInfo.getElement(), eInfo.getTs() + tStart);
				eInfo.getTs();
				break;
			case START:
				tEstancia.put(eInfo.getElement(), -eInfo.getTs());
				eInfo.getTs();
				break;
			default:
				break;
			}
		}
		else if (info instanceof SimulationEndInfo) {
		
			System.out.println("\tPetición\tNo acabadas\t");
		long endTs = ((SimulationEndInfo) info).getTs();
			for(Element elem : tEstancia.keySet()){
				if(tEstancia.get(elem) < 0){	
					if(-tEstancia.get(elem) < endTs){
						tEstancia.put(elem, endTs + tEstancia.get(elem));
						System.out.println("\t"+ elem + "\t\tNo acabó");	
						contador = contador + 1;
					}
					else{
						tEstancia.remove(elem);		
				}
			}	
		}			
			System.out.println("\tPetición\tTiempo extracción");
			for (Element elem : tEstancia.keySet()) {
				suma = (double)suma + tEstancia.get(elem);
				if(tEstancia.get(elem) > max){
					max = tEstancia.get(elem);				
				}
				if(tEstancia.get(elem) < min){
					min = tEstancia.get(elem);		
				}
				
				else {
					System.out.println( "\t"+ elem + "\t\t " + tEstancia.get(elem));
				}
			}
			promedio = (double)suma / (double)tEstancia.size(); 
			
			System.out.println("El tiempo total de extracción será: " + suma/60);
			System.out.println("El tiempo promedio de extracción será: " + promedio/60);
			System.out.println("El tiempo mínimo de extracción será: " + min/60);
			System.out.println("El tiempo máximo de extracción será: " + max/60);
			System.out.println("No acabaron: " + contador);
		
			sumaGlobal [info.getSimul().getIdentifier()] = suma;
			promedioGlobal[info.getSimul().getIdentifier()] = promedio;
			minGlobal [info.getSimul().getIdentifier()] = min;
			maxGlobal [info.getSimul().getIdentifier()] = max;
	    	contadorGlobal [info.getSimul().getIdentifier()] = contador;
	    	
		}	
	}
	public static int getnSim() {
		return nSim;
	}
	public static void setnSim(int nSim) {
		TiempoEstanciaListener.nSim = nSim;
		promedioGlobal = new double[nSim];
		minGlobal = new double[nSim];
		maxGlobal = new double[nSim];
		sumaGlobal = new double[nSim];
		contadorGlobal = new double[nSim];
	}
	
	public static double getPromedioGlobal() {
		return Statistics.average(promedioGlobal);
	}
	
	public static double getSDPromedioGlobal() {
		return Statistics.stdDev(promedioGlobal);
	}
	public static double getMinGlobal() {
		return Statistics.average(minGlobal);
	}
	
	public static double getSDMinGlobal() {
		return Statistics.stdDev(minGlobal);
	}
	public static double getMaxGlobal() {
		return Statistics.average(maxGlobal);
	}
	
	public static double getSDMaxGlobal() {
		return Statistics.stdDev(maxGlobal);
	}
	public static double getSumaGlobal() {
		return Statistics.average(sumaGlobal);
	}
	
	public static double getSDSumaGlobal() {
		return Statistics.stdDev(sumaGlobal);
	}
	public static double getContadorGlobal() {
		return Statistics.average(contadorGlobal);
	}
	
	public static double getSDContadorGlobal() {
		return Statistics.stdDev(contadorGlobal);
	}
	
	
	
} 
