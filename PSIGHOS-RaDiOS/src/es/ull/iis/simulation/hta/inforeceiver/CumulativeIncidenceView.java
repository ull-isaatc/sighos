/**
 * 
 */
package es.ull.iis.simulation.hta.inforeceiver;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * A viewer for the cumulative incidence of mortality and manifestation of the simulated patients, which can be shown either relative or absolute (number of patients);
 * either by age or according to the time from the simulation start.
 * It can show a single result or aggregated results from various simulation experiments (by using the nExperiments parameter).
 * It shows separate results for each intervention.
 * It shows results for every manifestation. Is also shows general and manifestation-specific mortality.  
 * @author Iván Castilla Rodríguez
 *
 */
public class CumulativeIncidenceView extends EpidemiologicView {

	/**
	 * Creates a cumulative incidence viewer
	 * @param nExperiments Number of experiments to be collected together
	 * @param secParams The original repository with the definition of the scenario
	 * @param length Length of the intervals (in years)
	 * @param absolute If true, shows number of patients; otherwise, shows ratios
	 * @param byAge If true, creates intervals depending on the current age of the patients; otherwise, creates intervals depending on the time from simulation start
	 */
	public CumulativeIncidenceView(int nExperiments, SecondOrderParamsRepository secParams, int length, boolean absolute, boolean byAge) {
		super("Cumulative incidence", nExperiments, secParams, length, absolute, byAge);
	}

	@Override
	public void updateExperiment(DiseaseProgressionSimulation simul, InnerListenerInstance listener) {
		final int interventionId = simul.getIntervention().ordinal();
		for (int year = 0; year < nIntervals; year++) {
			nBirths[interventionId][year] += listener.getnBirths()[year];
			nDeaths[interventionId][year] += listener.getnDeaths()[year];
			for (final Named cause : listener.getCausesOfDeath()) {
				nDeathsByCause.get(cause)[interventionId][year] += listener.getnDeathsByCause(cause)[year];
			}
			for (int j = 0; j < secParams.getRegisteredDiseases().length; j++) {
				nDisease[interventionId][j][year] += listener.getnDisease()[j][year];
			}
			for (int j = 0; j < secParams.getRegisteredManifestations().length; j++) {
				nManifestation[interventionId][j][year] += listener.getnManifestation()[j][year];
			}
		}			
	}

	@Override
	public void notifyEndExperiments() {
		if (absolute) {
			for (int i = 0; i < interventions.length; i++) {
				int year = 0;
				nBirths[i][year] /= (double)nExperiments;					
				nDeaths[i][year] /= (double)nExperiments;
				for (final Named cause : nDeathsByCause.keySet()) {
					nDeathsByCause.get(cause)[i][year] /= (double)nExperiments;
				}
				for (int j = 0; j < nDisease[i].length; j++) {
					nDisease[i][j][year] /= (double)nExperiments;
				}
				for (int j = 0; j < nManifestation[i].length; j++) {
					nManifestation[i][j][year] /= (double)nExperiments;
				}
				year++;
				for (; year < nIntervals; year++) {
					nBirths[i][year] = nBirths[i][year - 1] + nBirths[i][year] / (double)nExperiments;					
					nDeaths[i][year] = nDeaths[i][year - 1] + nDeaths[i][year] / (double)nExperiments;
					for (final Named cause : nDeathsByCause.keySet()) {
						nDeathsByCause.get(cause)[i][year] = nDeathsByCause.get(cause)[i][year - 1] + nDeathsByCause.get(cause)[i][year] / (double)nExperiments;
					}
					for (int j = 0; j < nDisease[i].length; j++) {
						nDisease[i][j][year] = nDisease[i][j][year - 1] + nDisease[i][j][year] / (double)nExperiments;
					}
					for (int j = 0; j < nManifestation[i].length; j++) {
						nManifestation[i][j][year] = nManifestation[i][j][year - 1] + nManifestation[i][j][year] / (double)nExperiments;
					}
				}
			}
		}
		else {
			for (int i = 0; i < interventions.length; i++) {
				int year = 0;
				nBirths[i][year] /= (double)nExperiments;
				double alive = nBirths[i][year];
				nDeaths[i][year] /= ((double)nExperiments * alive);
				for (final Named cause : nDeathsByCause.keySet()) {
					nDeathsByCause.get(cause)[i][year] /= ((double)nExperiments * alive);
				}
				for (int j = 0; j < nDisease[i].length; j++) {
					nDisease[i][j][year] /= ((double)nExperiments * alive);
				}
				for (int j = 0; j < nManifestation[i].length; j++) {
					nManifestation[i][j][year] /= ((double)nExperiments * alive);
				}
				year++;
				for (; year < nIntervals; year++) {
					alive += nBirths[i][year] / (double)nExperiments;
					nBirths[i][year] = nBirths[i][year - 1] + nBirths[i][year] / ((double)nExperiments * alive);					
					nDeaths[i][year] = nDeaths[i][year - 1] + nDeaths[i][year] / ((double)nExperiments * alive);
					for (final Named cause : nDeathsByCause.keySet()) {
						nDeathsByCause.get(cause)[i][year] = nDeathsByCause.get(cause)[i][year - 1] + nDeathsByCause.get(cause)[i][year] / ((double)nExperiments * alive);
					}
					for (int j = 0; j < nDisease[i].length; j++) {
						nDisease[i][j][year] = nDisease[i][j][year - 1] + nDisease[i][j][year] / ((double)nExperiments * alive);
					}
					for (int j = 0; j < nManifestation[i].length; j++) {
						nManifestation[i][j][year] = nManifestation[i][j][year - 1] + nManifestation[i][j][year] / ((double)nExperiments * alive);
					}
				}
			}
		}
		resultsReady = true;
	}
}

