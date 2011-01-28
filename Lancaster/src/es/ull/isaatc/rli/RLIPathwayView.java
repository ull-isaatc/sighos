/**
 * 
 */
package es.ull.isaatc.rli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.ull.isaatc.rli.RLIIP7GSimulation.AdmissionMethod;
import es.ull.isaatc.rli.RLIIP7GSimulation.Specialty;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.info.ElementInfo;
import es.ull.isaatc.simulation.info.ResourceUsageInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationInfo;
import es.ull.isaatc.simulation.inforeceiver.View;

/**
 * Catches the patients' pathways through the different wards.
 * @author Iván Castilla Rodríguez
 *
 */
public class RLIPathwayView extends View {
	private HashMap<Integer, PatientPathway>patients;
	
	public RLIPathwayView(Simulation simul) {
		super(simul, "RLI Pathway Viewer");
		patients = new HashMap<Integer, PatientPathway>();
		addEntrance(ElementInfo.class);
		addEntrance(ResourceUsageInfo.class);
		addEntrance(SimulationEndInfo.class);
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementInfo) {
			ElementInfo eInfo = (ElementInfo) info;
			String eType = eInfo.getElem().getElementType().getDescription();
			String []admSpec = eType.split("/");
			switch (eInfo.getType()) {
				case START: // Initializes the pathway
					patients.put(eInfo.getElem().getIdentifier(), new PatientPathway(AdmissionMethod.valueOf(admSpec[0]), Specialty.valueOf(admSpec[1]), eInfo.getTs()));
					break;
				case FINISH: // Assigns the corresponding discharge date
					patients.get(eInfo.getElem().getIdentifier()).disDate = eInfo.getTs();
					break;
			}
		}
		// This part is to handle bed episodes
		else if (info instanceof ResourceUsageInfo) {
			ResourceUsageInfo rInfo = (ResourceUsageInfo) info;
			if (rInfo.getType() == ResourceUsageInfo.Type.CAUGHT) {
				PatientPathway path = patients.get(rInfo.getSf().getElement().getIdentifier());
				// Assigns the real admission date, in case it haven't been previously set
				if (SimulationTime.isNotATime(path.realAdmDate))
					path.realAdmDate = rInfo.getTs();
				path.pathway.add(new PatientEpisode(rInfo.getTs(), rInfo.getRt().getDescription()));
			}
		}
		else if (info instanceof SimulationEndInfo) {
			System.out.println(this);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("PATIENT\tADMMETHOD\tSPECIALTY\tADMDATE\tREALADMDATE\tDISDATE\n");
		for (Map.Entry<Integer, PatientPathway> path : patients.entrySet()) {
			str.append("" + path.getKey() +	"\t" + path.getValue().adm + "\t" + 
					path.getValue().spec + "\t" + path.getValue().admDate + "\t" + 
					path.getValue().realAdmDate + "\t" + path.getValue().disDate);
			for (PatientEpisode ep : path.getValue().pathway)
				str.append("\t" + ep.date + "\t" + ep.ward);
			str.append("\n");
		}
		return str.toString();
	}
	
	class PatientPathway {
		public AdmissionMethod adm;
		public Specialty spec;
		public SimulationTime admDate = SimulationTime.getNotATime();
		public SimulationTime realAdmDate = SimulationTime.getNotATime();
		public SimulationTime disDate;
		public ArrayList<PatientEpisode> pathway;
		
		/**
		 * @param adm
		 * @param spec
		 * @param admDate
		 */
		private PatientPathway(AdmissionMethod adm, Specialty spec, SimulationTime admDate) {
			this.adm = adm;
			this.spec = spec;
			this.admDate = admDate;
			pathway = new ArrayList<PatientEpisode>();
		}
		
		@Override
		public String toString() {
			return adm + "\t" + spec + "\t" + admDate + "\t" + realAdmDate + "\t" + disDate + "\t" + pathway;
		}
	}

	class PatientEpisode {
		public SimulationTime date;
		public String ward;
		/**
		 * @param date
		 * @param ward
		 */
		private PatientEpisode(SimulationTime date, String ward) {
			this.date = date;
			this.ward = ward;
		}
		
		@Override
		public String toString() {
			return "" + date + "\t" + ward;
		}
	}
}
