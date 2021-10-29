/**
 * 
 */
package es.ull.iis.simulation.hta.inforeceiver;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface ExperimentListener {
	void addListener(DiseaseProgressionSimulation simul); 
	void notifyEndExperiments();
	
	interface InnerListener {
		void updateExperiment(DiseaseProgressionSimulation simul);
	}
}
