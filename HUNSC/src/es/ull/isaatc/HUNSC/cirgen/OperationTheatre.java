/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen;

import java.util.ArrayList;
import java.util.EnumSet;

import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.SimulationWeeklyPeriodicCycle;
import es.ull.isaatc.simulation.SimulationWeeklyPeriodicCycle.WeekDays;;

/**
 * Tipos de quirófano del hotspital
 * @author Iván
 *
 */
public class OperationTheatre {
	private static int indexCount = 0;	
	private final String name;
	private final double realUsage;
	private final double realAva;
	private final int index;
	private final ArrayList<TimetableEntry> tteList;

	public OperationTheatre(String name, double realUsage, double realAva) {
		this.index = indexCount++;
		this.name = name;
		this.realUsage = realUsage;
		this.realAva = realAva;
		tteList = new ArrayList<TimetableEntry>();
	}

	public void addTimeTableEntry(PatientType pt, AdmissionType at, double startTime, double openTime, EnumSet<WeekDays> days) {
		tteList.add(new TimetableEntry(pt, at, startTime, openTime, days));
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the realUsage
	 */
	public double getRealUsage() {
		return realUsage;
	}

	/**
	 * @return the real availability
	 */
	public double getRealAva() {
		return realAva;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return the tteList
	 */
	public ArrayList<TimetableEntry> getTimetableEntryList() {
		return tteList;
	}

	public class TimetableEntry {
		private final PatientType pt;
		private final AdmissionType at;
		private final EnumSet<WeekDays> days;
		private final double openTime;
		private final double startTime;
		
		/**
		 * @param pt
		 * @param at
		 * @param cycle
		 * @param openTime
		 */
		public TimetableEntry(PatientType pt, AdmissionType at, double startTime, double openTime, EnumSet<WeekDays> days) {
			this.pt = pt;
			this.at = at;
			this.startTime = startTime;
			this.openTime = openTime;
			this.days = days;
		}

		/**
		 * @return the pt
		 */
		public PatientType getPatientType() {
			return pt;
		}

		/**
		 * @return the at
		 */
		public AdmissionType getAdmissionType() {
			return at;
		}

		/**
		 * @return the cycle
		 */
		public SimulationWeeklyPeriodicCycle getCycle(Simulation simul) {
			return new SimulationWeeklyPeriodicCycle(simul, days, startTime, 0);
		}

		/**
		 * @return the openTime
		 */
		public double getOpenTime() {
			return openTime;
		}
		
	}
	
}
