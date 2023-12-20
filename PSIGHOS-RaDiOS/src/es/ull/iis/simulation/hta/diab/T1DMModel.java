/**
 * 
 */
package es.ull.iis.simulation.hta.diab;

import es.ull.iis.simulation.hta.HTAExperiment;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.diab.interventions.DCCT_ConventionalIntervention;
import es.ull.iis.simulation.hta.diab.interventions.DCCT_IntensiveIntervention;
import es.ull.iis.simulation.hta.progression.Disease;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMModel extends HTAModel {
	public static final String STR_HBA1C = "HbA1c";
	public static final String STR_DURATION = "Duration";

	public T1DMModel(HTAExperiment experiment) {
		super(experiment);
		final Disease dis = new T1DMDisease(this);
//		setPopulation(new T1DMSimpleTestPopulation(this, dis));
//		new SMBG_Intervention(this);
//		new CGM_Intervention(this);
		try {
			new DCCTPopulation1(this, dis);
			new DCCT_ConventionalIntervention(this);
			new DCCT_IntensiveIntervention(this);
		} catch (MalformedSimulationModelException e) {
			e.printStackTrace();
		}
	}

}
