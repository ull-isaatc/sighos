/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import java.util.TreeSet;

import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.model.Describable;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class Guideline implements Named, Describable {
	private final String description;
	private final String name;
	private final TreeSet<GuidelineRange> ranges;

	/**
	 * 
	 */
	public Guideline(String name, String description) {
		this.name = name;
		this.description = description;
		this.ranges = new TreeSet<>();
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String name() {
		return name;
	}

	public void addRange(double startAge, double endAge, double frequency, double duration) {
		ranges.add(new GuidelineRange(startAge, endAge, frequency, duration));
	}
	
	private static class GuidelineRange implements Comparable<GuidelineRange> {
		private final double startAge;
		private final double endAge;
		private final double frequency;
		private final double duration;
		
		public GuidelineRange(double startAge, double endAge, double frequency, double duration) {
			super();
			this.startAge = startAge;
			this.endAge = endAge;
			this.frequency = frequency;
			this.duration = duration;
		}

		public double getStartAge() {
			return startAge;
		}

		public double getEndAge() {
			return endAge;
		}

		public double getFrequency() {
			return frequency;
		}

		public double getDuration() {
			return duration;
		}

		@Override
		public int compareTo(GuidelineRange o) {
			if (startAge < o.startAge)
				return -1;
			else if (startAge > o.startAge)
				return 1;
			else {
				if (endAge < o.endAge)
					return -1;
				else if (endAge > o.endAge)
					return 1;
			}
			return 0;
		}
		
	}
	
	
}
