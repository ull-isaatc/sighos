/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import es.ull.isaatc.util.WeeklyPeriodicCycle;
import es.ull.isaatc.util.WeeklyPeriodicCycle.WeekDays;

/**
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
		tteList.add(new TimetableEntry(pt, at, new WeeklyPeriodicCycle(days, TimeUnit.MINUTES.convert(1, TimeUnit.DAYS), startTime, 0), openTime));
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
		private final WeeklyPeriodicCycle cycle;
		private final double openTime;
		
		/**
		 * @param pt
		 * @param at
		 * @param cycle
		 * @param openTime
		 */
		public TimetableEntry(PatientType pt, AdmissionType at, WeeklyPeriodicCycle cycle, double openTime) {
			this.pt = pt;
			this.at = at;
			this.cycle = cycle;
			this.openTime = openTime;
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
		public WeeklyPeriodicCycle getCycle() {
			return cycle;
		}

		/**
		 * @return the openTime
		 */
		public double getOpenTime() {
			return openTime;
		}
		
	}
	
}
