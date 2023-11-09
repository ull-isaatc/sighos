/**
 * 
 */
package es.ull.iis.simulation.hta.inforeceiver;

import java.util.HashMap;
import java.util.Locale;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;

/**
 * A viewer for the prevalence of manifestation of the simulated patients, which can be shown either relative or absolute (number of patients);
 * either by age or according to the time from the simulation start.
 * It can show a single result or aggregated results from various simulation experiments (by using the nExperiments parameter).
 * It shows separate results for each intervention.
 * It shows results for every manifestation. 
 * No results are shown for mortality
 * @author Iván Castilla Rodríguez
 *
 */
public class PrevalenceView extends EpidemiologicView {

	/**
	 * Creates a prevalence viewer
	 * @param nExperiments Number of experiments to be collected together
	 * @param secParams The original repository with the definition of the scenario
	 * @param length Length of the intervals (in years)
	 * @param absolute If true, shows number of patients; otherwise, shows ratios
	 * @param byAge If true, creates intervals depending on the current age of the patients; otherwise, creates intervals depending on the time from simulation start
	 */
	public PrevalenceView(int nExperiments, SecondOrderParamsRepository secParams, int length, boolean absolute, boolean byAge) {
		super("Prevalence", nExperiments, secParams, length, absolute, byAge);
	}


	@Override
	public void updateExperiment(DiseaseProgressionSimulation simul, InnerListenerInstance listener) {
		final int interventionId = simul.getIntervention().ordinal();
		// First process base time interval
		double accDeaths = listener.getnDeaths()[0];
		double accPatients = listener.getnBirths()[0];
		final double []accManifestation = new double[secParams.getRegisteredDiseaseProgressions().length];
		final double []accDisease = new double[secParams.getRegisteredDiseases().length];
		final HashMap<Named, Double> accDeathsByCause = new HashMap<>();
		for (final Named cause : listener.getCausesOfDeath()) {
			accDeathsByCause.put(cause, (double)listener.getnDeathsByCause(cause)[0]);
			nDeathsByCause.get(cause)[interventionId][0] += accDeathsByCause.get(cause);
		}
		nDeaths[interventionId][0] += accDeaths;
		nBirths[interventionId][0] += accPatients;
		for (int j = 0; j < secParams.getRegisteredDiseases().length; j++) {
			accDisease[j] = listener.getnDisease()[j][0];
			nDisease[interventionId][j][0] += accDisease[j];
		}
		for (int j = 0; j < secParams.getRegisteredDiseaseProgressions().length; j++) {
			accManifestation[j] = listener.getnManifestation()[j][0];
			nManifestation[interventionId][j][0] += accManifestation[j];
		}
		// Now process the rest of time intervals
		for (int i = 1; i < nIntervals; i++) {
			accPatients += listener.getnBirths()[i];
			accDeaths += listener.getnDeaths()[i];
			nDeaths[interventionId][i] += accDeaths;
			nBirths[interventionId][i] += accPatients;
			for (final Named cause : listener.getCausesOfDeath()) {
				accDeathsByCause.put(cause, accDeathsByCause.get(cause) + listener.getnDeathsByCause(cause)[i]);
				nDeathsByCause.get(cause)[interventionId][i] += accDeathsByCause.get(cause);
			}
			for (int j = 0; j < secParams.getRegisteredDiseases().length; j++) {
				accDisease[j] += listener.getnDisease()[j][i] - listener.getnEndDisease()[j][i-1];
				nDisease[interventionId][j][i] += accDisease[j];
			}
			for (int j = 0; j < secParams.getRegisteredDiseaseProgressions().length; j++) {
				accManifestation[j] += listener.getnManifestation()[j][i] - listener.getnEndManifestation()[j][i-1];
				nManifestation[interventionId][j][i] += accManifestation[j];
			}
		}
	}

	@Override
	public void notifyEndExperiments() {

		for (int i = 0; i < interventions.length; i++) {
			double coef = (absolute) ? nExperiments : nBirths[i][0];

			nBirths[i][0] /= coef;					
			for (int j = 0; j < nDisease[i].length; j++) {
				nDisease[i][j][0] /= coef;
			}
			for (int j = 0; j < nManifestation[i].length; j++) {
				nManifestation[i][j][0] /= coef;
			}
			
			for (int year = 1; year < nIntervals; year++) {
				// Computes people at risk as number of accumulated births minus the number of accumulated deaths in the previous interval 
				coef = (absolute) ? nExperiments : (nBirths[i][year] - nDeaths[i][year - 1]);
				nBirths[i][year] /= coef;					
				for (int j = 0; j < nDisease[i].length; j++) {
					nDisease[i][j][year] /= coef;
				}
				for (int j = 0; j < nManifestation[i].length; j++) {
					nManifestation[i][j][year] /= coef;
				}
			}
		}				
		resultsReady = true;		
	}

	
	protected String printHeader() {
		final StringBuilder str = new StringBuilder(description).append(absolute ? " ABS" : " REL").append(byAge ? " AGE" : "");
		str.append(System.lineSeparator()).append(byAge ? "AGE" : "YEAR");
		for (int i = 0; i < interventions.length; i++) {
			final String name = interventions[i].name();
			if (byAge)
				str.append("\t" + name + "_N");
			for (Disease dis : secParams.getRegisteredDiseases()) {
				str.append("\t" + name + "_").append(dis.name());
			}
			for (DiseaseProgression comp : secParams.getRegisteredDiseaseProgressions()) {
				str.append("\t" + name + "_").append(comp.name());
			}
		}
		str.append(System.lineSeparator());
		return str.toString();
	}
	
	protected String print() {
		final StringBuilder str = new StringBuilder();
		for (int year = 0; year < nIntervals; year++) {
			str.append(byAge ? (length * year) + minAge : (length *year));
			for (int i = 0; i < interventions.length; i++) {
				if (byAge) {
					str.append("\t").append(String.format(Locale.US, format, nBirths[i][year]));					
				}
				for (int j = 0; j < nDisease[i].length; j++) {
					str.append("\t").append(String.format(Locale.US, format, nDisease[i][j][year]));
				}
				for (int j = 0; j < nManifestation[i].length; j++) {
					str.append("\t").append(String.format(Locale.US, format, nManifestation[i][j][year]));
				}
			}
			str.append(System.lineSeparator());
		}
		return str.toString();
	}
	
}
