/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.inforeceiver;

import es.ull.iis.simulation.hta.diabetes.DiabetesSimulation;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface ExperimentListener {
	void addListener(DiabetesSimulation simul); 
	
	interface InnerListener {
		void updateExperiment(DiabetesSimulation simul);
	}
}
