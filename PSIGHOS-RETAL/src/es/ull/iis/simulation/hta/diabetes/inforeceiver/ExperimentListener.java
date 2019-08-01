/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.inforeceiver;

import es.ull.iis.simulation.hta.diabetes.DiabetesSimulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface ExperimentListener<L extends ExperimentListener.InnerListener> {
	void addListener(DiabetesSimulation simul); 
	
	interface InnerListener {
		void updateExperiment(DiabetesSimulation simul);
	}
}
