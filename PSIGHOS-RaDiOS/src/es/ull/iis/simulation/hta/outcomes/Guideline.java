/**
 * 
 */
package es.ull.iis.simulation.hta.outcomes;

import java.util.TreeSet;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.hta.NamedAndDescribed;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class Guideline implements NamedAndDescribed {
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
	
	public double getCost(double unitCost, double startT, double endT, Discount discountRate) {
		if (endT <= startT)
			return 0.0;
		double cost = 0.0;
		double t = startT;
		for (GuidelineRange range : ranges) {
			if (range.getStart() > endT)
				break;
			double minEndInRange = Math.min(range.getEnd(), endT);
			double frequency = range.getFrequency();
			// Checks if the range involves more than a natural year
			int naturalYear = (int)t;
			while (naturalYear <= minEndInRange) {
				double period = Math.min(naturalYear + 1, minEndInRange) - t;
				cost += discountRate.applyPunctualDiscount(period * frequency * unitCost, naturalYear) ;
				t = ++naturalYear;
			}
			t = range.getEnd();
		}
		return cost;
	}
	
	private static class GuidelineRange implements Comparable<GuidelineRange> {
		private final double start;
		private final double end;
		private final double frequency;
		private final double dose;
		private final double uses;
		
		public GuidelineRange(TimeStamp start, TimeStamp end, double frequency, double dose) {
			this(SecondOrderParamsRepository.simulationTimeToYears(TimeUnit.DAY.convert(start)), SecondOrderParamsRepository.simulationTimeToYears(TimeUnit.DAY.convert(end)), frequency, dose);
		}
		
		public GuidelineRange(double start, double end, double frequency, double dose) {
			super();
			this.start = start;
			this.end = end;
			this.frequency = frequency;
			this.dose = dose;
			this.uses = (end - start) * frequency;
		}

		public double getStart() {
			return start;
		}

		public double getEnd() {
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
