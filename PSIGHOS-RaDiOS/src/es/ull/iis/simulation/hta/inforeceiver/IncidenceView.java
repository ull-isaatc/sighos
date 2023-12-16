/**
 * 
 */
package es.ull.iis.simulation.hta.inforeceiver;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;

/**
 * A viewer for the incidence of mortality and manifestation of the simulated patients, which can be shown either relative or absolute (number of patients);
 * either by age or according to the time from the simulation start.
 * Incidence is he number of new cases divided by the persons at risk during an interval.
 * TODO Relative incidence is not completely accurate since the cohort at risk does not exclude exclusive manifestations
 * It can show a single result or aggregated results from various simulation experiments (by using the nExperiments parameter).
 * It shows separate results for each intervention.
 * It shows results for every manifestation. Is also shows general and manifestation-specific mortality.  
 * @author Iván Castilla Rodríguez
 *
 */
public class IncidenceView extends EpidemiologicView {
	/** A coefficient used to compute relative incidences. Computed only once. */
	private final double coefPatients;
	/** A coefficient used to compute average incidences (total / number of experiments). Computed only once. */
	private final double coefExperiments;

	/**
	 * Creates an incidence viewer
	 * @param nExperiments Number of experiments to be collected together
	 * @param model The original repository with the definition of the scenario
	 * @param length Length of the intervals (in years)
	 * @param absolute If true, shows number of patients; otherwise, shows ratios
	 * @param byAge If true, creates intervals depending on the current age of the patients; otherwise, creates intervals depending on the time from simulation start
	 */
	public IncidenceView(int nExperiments, HTAModel model, int length, boolean absolute, boolean byAge) {
		super("Incidence", nExperiments, model, length, absolute, byAge);
		this.coefPatients = 1.0 / (absolute ? 1.0 : (double)nPatients);
		this.coefExperiments = 1.0 / (double)nExperiments;
	}

	@Override
	public void updateExperiment(DiseaseProgressionSimulation simul, InnerListenerInstance listener) {
		final int interventionId = simul.getIntervention().ordinal();
		double alive = listener.getnBirths()[0];
		nBirths[interventionId][0] += alive * coefPatients;
		double coef = 1.0 / (absolute ? 1.0 : alive);		
		// Should always be 0
		nDeaths[interventionId][0] += listener.getnDeaths()[0] * coef;
		for (final Named cause : listener.getCausesOfDeath()) {
			nDeathsByCause.get(cause)[interventionId][0] += listener.getnDeathsByCause(cause)[0] * coef;
		}
		final double [] nAtRiskDisease = new double[model.getRegisteredDiseases().length]; 
		for (int j = 0; j < model.getRegisteredDiseases().length; j++) {
			nDisease[interventionId][j][0] += listener.getnDisease()[j][0] * coef;
			// Patients at risk of developing the disease in the next time interval are those who are alive and has not the disease yet
			nAtRiskDisease[j] = alive - listener.getnDisease()[j][0]; 
		}
		final double [] nAtRiskManifestation = new double[model.getRegisteredDiseaseProgressions().length]; 
		for (int j = 0; j < model.getRegisteredDiseaseProgressions().length; j++) {
			nManifestation[interventionId][j][0] += listener.getnManifestation()[j][0] * coef;
			// Patients at risk of developing the manifestation in the next time interval are those who are alive and has not the manifestation yet, in case the manifestation is chronic
			nAtRiskManifestation[j] = alive - (DiseaseProgression.Type.CHRONIC_MANIFESTATION.equals(model.getRegisteredDiseaseProgressions()[j].getType()) ? listener.getnManifestation()[j][0] : 0); 
		}
		// Rest of intervals
		for (int year = 1; year < nIntervals; year++) {
			alive += listener.getnBirths()[year] - listener.getnDeaths()[year - 1];
			coef = 1.0 / (absolute ? 1.0 : alive);
			if (listener.getnBirths()[year] != 0)
				nBirths[interventionId][year] += listener.getnBirths()[year] * coefPatients;
			if (listener.getnDeaths()[year] != 0)
				nDeaths[interventionId][year] +=  listener.getnDeaths()[year] * coef;
			for (final Named cause : listener.getCausesOfDeath()) {
				if (listener.getnDeathsByCause(cause)[year] != 0)
					nDeathsByCause.get(cause)[interventionId][year] += listener.getnDeathsByCause(cause)[year] * coef;
			}
			for (int j = 0; j < model.getRegisteredDiseases().length; j++) {
				if (listener.getnDisease()[j][year] != 0) {
					nDisease[interventionId][j][year] += listener.getnDisease()[j][year] / (absolute ? 1.0 : nAtRiskDisease[j]);
					// People who start suffering a disease must be excluded from the people at risk of suffering so
					nAtRiskDisease[j] -= listener.getnDisease()[j][year];
				}
				// Disease is supposed to be "chronic"; hence, the "end disease" group includes deceased individuals.
				nAtRiskDisease[j] -= listener.getnEndDisease()[j][year];
			}
			for (int j = 0; j < model.getRegisteredDiseaseProgressions().length; j++) {
				if (listener.getnManifestation()[j][year] != 0) {
					nManifestation[interventionId][j][year] += listener.getnManifestation()[j][year] / (absolute ? 1.0 : nAtRiskManifestation[j]);
					// People who start suffering a chronic manifestation must be excluded from the people at risk of suffering so
					if (DiseaseProgression.Type.CHRONIC_MANIFESTATION.equals(model.getRegisteredDiseaseProgressions()[j].getType()))
						nAtRiskManifestation[j] -= listener.getnManifestation()[j][year];					
				}
				// The account of "end manifestation" includes the decease individuals with the manifestation. Should be always 0 for acute ones
				nAtRiskManifestation[j] -= listener.getnEndManifestation()[j][year];
			}
		}
	}

	@Override
	public void notifyEndExperiments() {
		for (int year = 0; year < nIntervals; year++) {
			for (int i = 0; i < interventions.length; i++) {
				nBirths[i][year] *= coefExperiments;					
				nDeaths[i][year] *= coefExperiments;
				for (final Named cause : nDeathsByCause.keySet()) {
					nDeathsByCause.get(cause)[i][year] *= coefExperiments;
				}
				for (int j = 0; j < nDisease[i].length; j++) {
					nDisease[i][j][year] *= coefExperiments;
				}
				for (int j = 0; j < nManifestation[i].length; j++) {
					nManifestation[i][j][year] *= coefExperiments;
				}
			}
		}
		resultsReady = true;
	}
}

