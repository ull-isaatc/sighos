/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen;

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
	private final WeeklyPeriodicCycle cycle;
	private final OperationTheatreType type;
	private final double realUsage;
	private final double realAva;
	private final int index;

	public OperationTheatre(String name, OperationTheatreType type, EnumSet<WeekDays> days, double startTs, double realUsage, double realAva) {
		this.index = indexCount++;
		this.name = name;
		this.type = type;
		this.cycle = new WeeklyPeriodicCycle(days, TimeUnit.MINUTES.convert(1, TimeUnit.DAYS), startTs, 0);
		this.realUsage = realUsage;
		this.realAva = realAva;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the type
	 */
	public OperationTheatreType getType() {
		return type;
	}

	/**
	 * @return the cycle
	 */
	public WeeklyPeriodicCycle getCycle() {
		return cycle;
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

	
}
