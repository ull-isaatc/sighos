/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.populations.Population;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class RangeWrapper {
	public enum Type {
		RANGE,
		SPECIFIC,
		DURATION,
		UNITS
	}
	private static final String STR_PREFIX = "PREFIX";
	private static final String STR_PREF_RANGE = "RANGE";
	private static final String STR_PREF_UNIT = "UNIT";
	private static final String STR_LOW_LIMIT = "RANGE1";
	private static final String STR_UPP_LIMIT = "RANGE2";
	private static final String STR_LOW_LIMIT_UNIT = "UNIT1";
	private static final String STR_UPP_LIMIT_UNIT = "UNIT2";
	private static final String REGEXP_RANGE = "^(?<" + STR_PREFIX + ">[&@])(?<" + STR_PREF_RANGE + ">[0-9]+)(?<" + STR_PREF_UNIT + ">[uhdmy])$|" +
			"^(?<" + STR_LOW_LIMIT + ">[0-9]+)(?<" + STR_LOW_LIMIT_UNIT + ">[hdmy])(-(?<" + STR_UPP_LIMIT + ">[0-9]+)(?<" + STR_UPP_LIMIT_UNIT + ">[hdmy]))?$";
	private static final Pattern rangePattern = Pattern.compile(REGEXP_RANGE);
	private final TimeStamp[] rangeLimits = new TimeStamp[2];
	private final TimeStamp prefixedRange;
	private final int noTimeRange;
	private final Type type;

	/**
	 * 
	 */
	public RangeWrapper(String strRange) throws TranspilerException {
		Matcher matcher = rangePattern.matcher(strRange.trim());
		if (matcher.find()) {
			String strPrefix = matcher.group(STR_PREFIX);
			if (strPrefix != null) {
				char prefix = strPrefix.charAt(0);
				String range = matcher.group(STR_PREF_RANGE);
				char unit = matcher.group(STR_PREF_UNIT).charAt(0);
				if (unit == 'u') {
					noTimeRange = Integer.parseInt(strRange);
					prefixedRange = null;
					type = Type.UNITS;
				}
				else {
					noTimeRange = -1;
					prefixedRange = rangeLimit2TimeStamp(range, unit);
					type = (prefix == '@') ? Type.SPECIFIC : Type.DURATION; 
				}
			}
			else {
				type = Type.RANGE;
				noTimeRange = -1;
				prefixedRange = null;
				String range1 = matcher.group(STR_LOW_LIMIT);
				char unit1 = matcher.group(STR_LOW_LIMIT_UNIT).charAt(0);
				rangeLimits[0] = rangeLimit2TimeStamp(range1, unit1);
				String range2 = matcher.group(STR_UPP_LIMIT);
				if (range2 != null) {
					char unit2 = matcher.group(STR_UPP_LIMIT_UNIT).charAt(0);
					rangeLimits[1] = rangeLimit2TimeStamp(range2, unit2);
				}
				else {
					rangeLimits[1] = new TimeStamp(TimeUnit.YEAR, Population.DEF_MAX_AGE);
				}
			}
		}
		else {
			throw new TranspilerException("Error parsing range " + strRange);
		}
	}

	private TimeStamp rangeLimit2TimeStamp(String range, char unit) {
		TimeUnit tUnit = null;
		switch(unit) {
		case 'h':
			tUnit = TimeUnit.HOUR; break;
		case 'd':
			tUnit = TimeUnit.DAY; break;
		case 'm':
			tUnit = TimeUnit.MONTH; break;
		case 'y':
		default:
			tUnit = TimeUnit.YEAR; break;
		}
		final long value = Long.parseLong(range);
		return new TimeStamp(tUnit, value);
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return the rangeLimits
	 */
	public TimeStamp[] getRangeLimits() {
		return rangeLimits;
	}

	/**
	 * @return the prefixedRange
	 */
	public TimeStamp getPrefixedRange() {
		return prefixedRange;
	}

	/**
	 * @return the unitlessRange
	 */
	public int getUnitlessRange() {
		return noTimeRange;
	}
	
}
