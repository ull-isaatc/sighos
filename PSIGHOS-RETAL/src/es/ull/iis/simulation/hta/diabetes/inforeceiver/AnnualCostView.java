/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.inforeceiver;

import java.util.Arrays;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesSimulation;
import es.ull.iis.simulation.hta.diabetes.info.T1DMPatientInfo;
import es.ull.iis.simulation.hta.diabetes.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iv�n Castilla
 *
 */
public class AnnualCostView extends Listener {
	private final double[] cost;
	private final CostCalculator calc;
	private final double[]lastAge;
	private final int nPatients;

	/**
	 * @param simUnit The time unit used within the simulation
	 */
	public AnnualCostView(CostCalculator calc, int nPatients, int minAge, int maxAge) {
		super("Standard patient viewer");
		this.calc = calc;
		this.nPatients = nPatients;
		this.lastAge = new double[nPatients];
		Arrays.fill(lastAge, 0.0);
		cost = new double[maxAge-minAge+1];
		addGenerated(T1DMPatientInfo.class);
		addEntrance(T1DMPatientInfo.class);
		addEntrance(SimulationStartStopInfo.class);
	}

	public double[] getAnnualCosts() {
		return cost;
	}
	
	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		for (int i = 0; i < cost.length; i++) {
			str.append(cost[i] / nPatients).append(System.lineSeparator());
		}
		return str.toString();
	}
	
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationStartStopInfo) {
			final SimulationStartStopInfo tInfo = (SimulationStartStopInfo) info;
			if (SimulationStartStopInfo.Type.END.equals(tInfo.getType())) {
				final long ts = tInfo.getTs();
				final DiabetesSimulation simul = (DiabetesSimulation)tInfo.getSimul();
				final TimeUnit simUnit = simul.getTimeUnit();
				for (int i = 0; i < lastAge.length; i++) {
					final DiabetesPatient pat = (DiabetesPatient)simul.getGeneratedPatient(i);
					if (!pat.isDead()) {
						final double initAge = lastAge[pat.getIdentifier()]; 
						final double endAge = TimeUnit.DAY.convert(ts, simUnit) / BasicConfigParams.YEAR_CONVERSION;
						if (endAge > initAge) {
							final double periodCost = calc.getAnnualCostWithinPeriod(pat, initAge, endAge);
							update(pat, periodCost, initAge, endAge);							
						}						
					}
				}
			}
		}
		else if (info instanceof T1DMPatientInfo) {
			final T1DMPatientInfo pInfo = (T1DMPatientInfo) info;
			final DiabetesPatient pat = pInfo.getPatient();
			final long ts = pInfo.getTs();
			final TimeUnit simUnit = pat.getSimulation().getTimeUnit();
			final double initAge = lastAge[pat.getIdentifier()]; 
			final double endAge = TimeUnit.DAY.convert(ts, simUnit) / BasicConfigParams.YEAR_CONVERSION;
			switch(pInfo.getType()) {
			case COMPLICATION:
				update(calc.getCostOfComplication(pat, pInfo.getComplication()), endAge);
			case DEATH:
				// Update outcomes
				if (endAge > initAge) {
					final double periodCost = calc.getAnnualCostWithinPeriod(pat, initAge, endAge);
					update(pat, periodCost, initAge, endAge);
				}
				break;
			case ACUTE_EVENT:
				update(calc.getCostForAcuteEvent(pat, pInfo.getAcuteEvent()), endAge);
				// Update outcomes
				if (endAge > initAge) {
					final double periodCost = calc.getAnnualCostWithinPeriod(pat, initAge, endAge);
					update(pat, periodCost, initAge, endAge);
				}
				break;
			case START:
				break;
			default:
				break;
			
			}
			// Update lastTs
			lastAge[pat.getIdentifier()] = endAge;
		}
	}

	/**
	 * Updates the value of this outcome for a specified period
	 * @param value A constant value during the period
	 * @param initAge Initial age when the value is applied
	 * @param endAge End age when the value is applied
	 */
	private void update(DiabetesPatient pat, double value, double initAge, double endAge) {
		final int firstInterval = (int)initAge;
		final int lastInterval = (int)endAge;
		if ((int)initAge < (int)endAge)
			cost[firstInterval] += value * ((int)(initAge+1) - initAge);
		for (int i = firstInterval + 1; i < lastInterval; i++) {
			cost[i] += value;
		}
		cost[lastInterval] += value * (endAge - Math.max((int)endAge, initAge)); 
	}
	/**
	 * Updates the value of this outcome at a specified age
	 * @param value The value to update
	 * @param age The age at which the value is applied
	 */
	private void update(double value, double age) {
		cost[(int)age] += value;
	}
	
	
}