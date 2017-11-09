package es.ull.iis.simulation.whellChairs;


import java.util.TreeMap;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.ResourceUsageInfo;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.View;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.ResourceType;


/**
 * @author usuario
 *
 */
public class TotalResourceTimeViewManual extends View {
	TreeMap<Resource, Long> times = new TreeMap<Resource, Long>();
	TreeMap<Resource, Long> acumTimesDoctor = new TreeMap<Resource, Long>();
	TreeMap<Resource, Long> wheelchairusage = new TreeMap<Resource, Long>();
	TreeMap<Resource, Long> acumTimesBedel = new TreeMap<Resource, Long>();
	int contadorsillasmanual;
	int contadordoctor;
	int contadorbedel;

	public TotalResourceTimeViewManual() {
		super("Tiempo total por recurso");
		addEntrance(ResourceUsageInfo.class);
		addEntrance(SimulationEndInfo.class);
		contadorsillasmanual = 0;
		contadordoctor = 0;
		contadorbedel = 0;
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ResourceUsageInfo) {
			ResourceUsageInfo eInfo = (ResourceUsageInfo)info;
			switch(eInfo.getType()) {
			case CAUGHT:
				times.put( eInfo.getResource(), eInfo.getTs());
				break;
			case RELEASED:
				long usageTime = eInfo.getTs() - times.get(eInfo.getResource());
				if(eInfo.getResourceType().getDescription().contains("Silla")){
					if(eInfo.getResource().getDescription().contains("Silla Manual")){
						if(eInfo.getType() == ResourceUsageInfo.Type.RELEASED){
							contadorsillasmanual ++;
							/*contadorbedel ++;
							if(acumTimesBedel.containsKey(eInfo.getResource())) {
								acumTimesBedel.put(eInfo.getResource(), acumTimesBedel.get(eInfo.getResource()) + usageTime);  //Si no hay tiempo anterior, éste es el tiempo acumulado		
							}
							else {
								acumTimesBedel.put(eInfo.getResource(), usageTime);  //Si no hay tiempo anterior, éste es el tiempo acumulado		
							}*/
						}
					}
					if (wheelchairusage.containsKey(eInfo.getResource())) { //Si ya hay un tiempo acumulado, se suma
						wheelchairusage.put(eInfo.getResource(), wheelchairusage.get(eInfo.getResource()) + usageTime);
					}
					else {
						wheelchairusage.put(eInfo.getResource(), usageTime);						
					}
				}
				if(eInfo.getResourceType().getDescription().contains("Bedel")){
					if(eInfo.getType() == ResourceUsageInfo.Type.RELEASED){
							contadorbedel ++;
						}
					if(acumTimesBedel.containsKey(eInfo.getResource())) {
						acumTimesBedel.put(eInfo.getResource(), acumTimesBedel.get(eInfo.getResource()) + usageTime);  //Si no hay tiempo anterior, éste es el tiempo acumulado		
					}
					else {
						acumTimesBedel.put(eInfo.getResource(), usageTime);  //Si no hay tiempo anterior, éste es el tiempo acumulado		
					}
				}
				if(eInfo.getResourceType().getDescription().contains("Doctor")){
					if(eInfo.getType() == ResourceUsageInfo.Type.RELEASED){
							contadordoctor ++;
					}
				}
		
				break;
			default:
				break;
			
			}
		}

		else if (info instanceof SimulationEndInfo) {
			SimulationEndInfo sInfo = (SimulationEndInfo)info;
			for (Resource res : times.keySet()) {
				if((res.getDescription().contains("Silla Manual"))){
					System.out.println(res + "\t" + "TIEMPO DE USO DE RECURSO SILLA MANUAL" + "\t" + wheelchairusage.get(res) );
				}
				if((res.getDescription().contains("Bedel"))){
				    System.out.println(res + "\t" + "TIEMPO DE USO DE RECURSO BEDEL" + "\t" + acumTimesBedel.get(res) );
			    }
			}
		
			System.out.println("Nº USOS DE SILLA MANUAL" + "\t" + contadorsillasmanual);
			System.out.println("Nº USOS DE DOCTOR" + "\t" + contadordoctor);
			System.out.println("Nº USOS DE BEDEL" + "\t" + contadorbedel);

		}

	}

}
