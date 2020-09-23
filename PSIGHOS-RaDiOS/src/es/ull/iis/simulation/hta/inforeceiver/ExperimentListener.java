/**
 * 
 */
package es.ull.iis.simulation.hta.inforeceiver;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface ExperimentListener {
	void addListener(DiseaseProgressionSimulation simul); 
	
	interface InnerListener {
		void updateExperiment(DiseaseProgressionSimulation simul);
	}
}
