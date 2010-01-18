/**
 * 
 */
package es.ull.isaatc.rli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.ull.isaatc.rli.RLIIP7GSimulation.AdmissionMethod;
import es.ull.isaatc.rli.RLIIP7GSimulation.Specialty;
import es.ull.isaatc.simulation.Element;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.info.*;
import es.ull.isaatc.simulation.listener.SimulationListener;
import es.ull.isaatc.simulation.listener.SimulationObjectListener;

/**
 * Catches the patients' pathways through the different wards.
 * @author Iván Castilla Rodríguez
 *
 */
public class RLIPathwayListener implements SimulationObjectListener, SimulationListener {
	private HashMap<Integer, PatientPathway>patients;
	private Simulation simul;
	
	public RLIPathwayListener() {
		patients = new HashMap<Integer, PatientPathway>();
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.listener.SimulationObjectListener#infoEmited(es.ull.isaatc.simulation.info.SimulationObjectInfo)
	 */
	public void infoEmited(SimulationObjectInfo info) {
		if (info instanceof ElementInfo) {
			ElementInfo eInfo = (ElementInfo) info;
			String eType = ((Element)info.getSource()).getElementType().getDescription();
			String []admSpec = eType.split("/");
			switch (eInfo.getType()) {
				case START: // Initializes the pathway
					patients.put(eInfo.getIdentifier(), new PatientPathway(AdmissionMethod.valueOf(admSpec[0]), Specialty.valueOf(admSpec[1]), eInfo.getTs()));
					break;
				case FINISH: // Assigns the corresponding discharge date
					if (eInfo.getValue() == 0) // No pending activities
						patients.get(eInfo.getIdentifier()).disDate = eInfo.getTs();
					break;
			}
		}
		// This part is to handle bed episodes
		else if (info instanceof ResourceUsageInfo) {
			ResourceUsageInfo rInfo = (ResourceUsageInfo) info;
			if (rInfo.getType() == ResourceUsageInfo.Type.CAUGHT) {
				PatientPathway path = patients.get(rInfo.getElemId());
				// Assigns the real admission date, in case it haven't been previously set
				if (Double.isNaN(path.realAdmDate))
					path.realAdmDate = rInfo.getTs();
				path.pathway.add(new PatientEpisode(rInfo.getTs(), simul.getResourceType(rInfo.getRtId()).getDescription()));
			}
		}
	}

	public void infoEmited(SimulationStartInfo info) {
		simul = info.getSimulation();
		System.out.println("SIMULATION " + simul + " STARTED");
	}

	public void infoEmited(SimulationEndInfo info) {
		System.out.println("\nSIMULATION " + simul + " FINISHED: Starting printing to file");
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
		public double admDate = Double.NaN;
		public double realAdmDate = Double.NaN;
		public double disDate;
		public ArrayList<PatientEpisode> pathway;
		
		/**
		 * @param adm
		 * @param spec
		 * @param admDate
		 */
		private PatientPathway(AdmissionMethod adm, Specialty spec, double admDate) {
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
		public double date;
		public String ward;
		/**
		 * @param date
		 * @param ward
		 */
		private PatientEpisode(double date, String ward) {
			this.date = date;
			this.ward = ward;
		}
		
		@Override
		public String toString() {
			return "" + date + "\t" + ward;
		}
	}
}
