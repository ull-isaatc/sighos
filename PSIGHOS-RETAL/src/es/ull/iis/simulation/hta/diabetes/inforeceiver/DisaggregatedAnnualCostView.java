/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.inforeceiver;

import java.util.Arrays;
import java.util.Locale;

import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesSimulation;
import es.ull.iis.simulation.hta.diabetes.info.T1DMPatientInfo;
import es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.Discount;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * A viewer to show the breakdown of annual costs 
 * 
 * FIXME: When using discount, the final result does not fit with the original cost
 * @author Iván Castilla
 *
 */
public class DisaggregatedAnnualCostView extends Listener {
	private final double[][] chronicCost;
	private final double[][] acuteCost;
	private final double[] interventionCost;
	private final double[] managementCost;
	private final CostCalculator calc;
	private final double[]lastAge;
	private final int nPatients;
	private final Discount discount;
	private final SecondOrderDiabetesIntervention intervention;

	/**
	 * 
	 * @param calc
	 * @param nPatients
	 * @param minAge
	 * @param maxAge
	 */
	public DisaggregatedAnnualCostView(SecondOrderDiabetesIntervention intervention, CostCalculator calc, Discount discount, int nPatients, int minAge, int maxAge) {
		super("Standard patient viewer");
		this.discount = discount;
		this.intervention = intervention;
		this.calc = calc;
		this.nPatients = nPatients;
		this.lastAge = new double[nPatients];
		Arrays.fill(lastAge, 0.0);
		chronicCost = new double[DiabetesChronicComplications.values().length][maxAge-minAge+1];
		acuteCost = new double[DiabetesAcuteComplications.values().length][maxAge-minAge+1];
		interventionCost = new double[maxAge-minAge+1];
		managementCost = new double[maxAge-minAge+1];
		addGenerated(T1DMPatientInfo.class);
		addEntrance(T1DMPatientInfo.class);
		addEntrance(SimulationStartStopInfo.class);
	}

	public double[][] getAnnualChronicComplicationCosts() {
		return chronicCost;
	}
	
	public double[][] getAnnualAcuteComplicationCosts() {
		return acuteCost;
	}
	
	public double[] getAnnualInterventionCost() {
		return interventionCost;
	}

	public double[] getAnnualManagementCost() {
		return managementCost;
	}
	
	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		str.append("Year\t").append(intervention.getShortName()).append("\tManagement");
		for (DiabetesChronicComplications comp : DiabetesChronicComplications.values()) {
			str.append("\t").append(comp);
		}
		for (DiabetesAcuteComplications comp : DiabetesAcuteComplications.values()) {
			str.append("\t").append(comp);			
		}
		for (int i = 0; i < interventionCost.length; i++) {
			str.append(System.lineSeparator());
			str.append(i).append("\t").append(String.format(Locale.US, "%.2f", interventionCost[i] /nPatients));
			str.append("\t").append(String.format(Locale.US, "%.2f", managementCost[i] /nPatients));
			for (int j = 0; j < DiabetesChronicComplications.values().length; j++) {
				str.append("\t").append(String.format(Locale.US, "%.2f", chronicCost[j][i] / nPatients));
			}
			for (int j = 0; j < DiabetesAcuteComplications.values().length; j++) {
				str.append("\t").append(String.format(Locale.US, "%.2f", acuteCost[j][i] / nPatients));
			}
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
						updateAll(pat, initAge, endAge);
					}
				}
				System.out.println(this);
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
				chronicCost[pInfo.getComplication().getComplication().ordinal()][(int) endAge] += discount.applyPunctualDiscount(calc.getCostOfComplication(pat, pInfo.getComplication()), endAge);
			case DEATH:
				// Update outcomes
				updateAll(pat, initAge, endAge);
				break;
			case ACUTE_EVENT:
				acuteCost[pInfo.getAcuteEvent().ordinal()][(int) endAge] += discount.applyPunctualDiscount(calc.getCostForAcuteEvent(pat, pInfo.getAcuteEvent()), endAge);
				// Update outcomes
				updateAll(pat, initAge, endAge);
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

	private void updateAll(DiabetesPatient pat, double initAge, double endAge) {
		if (endAge > initAge) {
			update(interventionCost, calc.getAnnualInterventionCostWithinPeriod(pat, initAge, endAge), initAge, endAge);
			update(managementCost, calc.getStdManagementCostWithinPeriod(pat, initAge, endAge), initAge, endAge);
			final double[] complicationPeriodCost = calc.getAnnualChronicComplicationCostWithinPeriod(pat, initAge, endAge);
			for (int i = 0; i < DiabetesChronicComplications.values().length; i++) {
				update(chronicCost[i], complicationPeriodCost[i], initAge, endAge);
			}
		}
	}
	
	/**
	 * Updates the value of this outcome for a specified period
	 * @param value A constant value during the period
	 * @param initAge Initial age when the value is applied
	 * @param endAge End age when the value is applied
	 */
	private void update(double []result, double value, double initAge, double endAge) {
		final int firstInterval = (int)initAge;
		final int lastInterval = (int)endAge;
		if ((int)initAge < (int)endAge)
			result[firstInterval] += discount.applyDiscount(value, initAge, (int)(initAge+1));
//		result[firstInterval] += value * ((int)(initAge+1) - initAge);
		for (int i = firstInterval + 1; i < lastInterval; i++) {
			result[i] += discount.applyDiscount(value, firstInterval, firstInterval + 1);
		}
		result[lastInterval] += discount.applyDiscount(value, Math.max((int)endAge, initAge), endAge); 
//		result[lastInterval] += value * (endAge - Math.max((int)endAge, initAge)); 
	}	
	
}
