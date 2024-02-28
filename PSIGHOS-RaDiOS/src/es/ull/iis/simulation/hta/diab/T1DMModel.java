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
	public enum ModelConfig {
		SIMPLE_TEST,
		DCCT1,
		DCCT2,
		DCCT1_CONV,
		DCCT1_INTENS
	}

	public T1DMModel(HTAExperiment experiment, ModelConfig config) {
		super(experiment);
		final Disease dis = new T1DMDisease(this);
		try {
			switch (config) {
			case DCCT1:
				new DCCT_PrimaryPopulation(this, dis);
				new DCCT_ConventionalIntervention(this);
				new DCCT_IntensiveIntervention(this);
					break;
			case DCCT2:
				new DCCT_SecondaryPopulation(this, dis);
				new DCCT_ConventionalIntervention(this);
				new DCCT_IntensiveIntervention(this);
				break;
			case DCCT1_CONV:
				new DCCT_PrimaryConventionalPopulation(this, dis);
				new DCCT_ConventionalIntervention(this);
				break;
			case DCCT1_INTENS:
				new DCCT_PrimaryIntensivePopulation(this, dis);
				new DCCT_IntensiveIntervention(this);
				break;
			case SIMPLE_TEST:
			default:
				new T1DMSimpleTestPopulation(this, dis);
				break;
			}
		} catch (MalformedSimulationModelException e) {
			e.printStackTrace();
		}
	}

}
