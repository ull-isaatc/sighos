/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import java.util.TreeSet;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.model.Describable;
import es.ull.iis.simulation.model.TimeStamp;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class Guideline implements Named, Describable {
	private final String description;
	private final String name;
	private final TreeSet<GuidelineRange> ranges;
	private final Condition<Patient> condition;

	/**
	 * 
	 */
	public Guideline(String name, String description) {
		this(name, description, new TrueCondition<Patient>());
	}

	/**
	 * 
	 */
	public Guideline(String name, String description, Condition<Patient> condition) {
		this.name = name;
		this.description = description;
		this.ranges = new TreeSet<>();
		this.condition = condition;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String name() {
		return name;
	}

	/**
	 * @return the condition
	 */
	public Condition<Patient> getCondition() {
		return condition;
	}

	public void addRange(TimeStamp start, TimeStamp end, double frequency, double duration) {
		ranges.add(new GuidelineRange(start, end, frequency, duration));
	}
	
	private static class GuidelineRange implements Comparable<GuidelineRange> {
		private final long start;
		private final long end;
		private final double frequency;
		private final double dose;
		
		public GuidelineRange(TimeStamp start, TimeStamp end, double frequency, double dose) {
			super();
			this.start = BasicConfigParams.SIMUNIT.convert(start);
			this.end = BasicConfigParams.SIMUNIT.convert(end);
			this.frequency = frequency;
			this.dose = dose;
		}

		public long getStart() {
			return start;
		}

		public long getEnd() {
			return end;
		}

		public double getFrequency() {
			return frequency;
		}

		public double getDose() {
			return dose;
		}

		@Override
		public int compareTo(GuidelineRange o) {
			if (start < o.start)
				return -1;
			else if (start > o.start)
				return 1;
			else {
				if (end < o.end)
					return -1;
				else if (end > o.end)
					return 1;
			}
			return 0;
		}
		
	}
	
	
}
