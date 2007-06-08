package es.ull.isaatc.simulation.fuzzy;

import java.util.ArrayList;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.ElementCreator;
import es.ull.isaatc.simulation.XMLSimulation;
import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.CycleIterator;

public class ProgrammedTaskList  {

	private static final long serialVersionUID = 1L;

	protected ArrayList<ProgrammedTaskListEntry> taskList = new ArrayList<ProgrammedTaskListEntry>();
	
	public void add(String description, int et, int mf, Cycle period, double qos) {
		taskList.add(new ProgrammedTaskListEntry(description, et, mf, period, qos));
	}
	
	/**
	 * @return the taskList
	 */
	public ArrayList<ProgrammedTaskListEntry> getTaskList() {
		return taskList;
	}
	
	public void initIterator(double absStart, double absEnd) {
		for (ProgrammedTaskListEntry entry : taskList) {
			entry.initIterator(absStart, absEnd);
		}
	}
	
	public class ProgrammedTaskListEntry {
		/** Task description */
		private String description;
		
		/** Element type identifier of this programmed task */
		private int et;
		
		/** Metaflow identifier for this programmed task */
		private int mf;
		
		/** Programmed task period */
		private Cycle period;
		
		/** Programmed task Quality of Service */
		private double qos;
		
		/** Cycle iterator */
		private CycleIterator cycleIterator;
		
		private double nextTs;

		public ProgrammedTaskListEntry(String description, int et, int mf, Cycle period, double qos) {
			super();
			this.description = description;
			this.et = et;
			this.mf = mf;
			this.period = period;
			this.qos = qos;
		}
		
		public void initIterator(double absStart, double absEnd) {
			cycleIterator = period.iterator(absStart, absEnd);
			next();
		}
		
		public double next() {
			nextTs = cycleIterator.next();
			return nextTs;
		}

		/**
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}
		
		/**
		 * @return the mf
		 */
		public int getMf() {
			return mf;
		}

		/**
		 * @return the qos
		 */
		public double getQos() {
			return qos;
		}

		/**
		 * @return the nextTs
		 */
		public double getNextTs() {
			return nextTs;
		}
		
		/**
		 * @return the et
		 */
		public int getEt() {
			return et;
		}

		public ElementCreator getElementCreator(XMLSimulation simul) {
			ElementCreator elementCreator = new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", 1));
			elementCreator.add(simul.getElementType(et), simul.getFlowList().get(mf), 1.0);
			return elementCreator;
		}
		
		public String toString() {
			return "TLE["+ description + "] ET[" + et + "] MF[" + mf + "]";
		}
	}
}
