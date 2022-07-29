/**
 * 
 */
package es.ull.iis.simulation.hta.diab;

import es.ull.iis.simulation.hta.diab.interventions.DCCT_ConventionalIntervention;
import es.ull.iis.simulation.hta.diab.interventions.DCCT_IntensiveIntervention;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.EmpiricalSpainDeathSubmodel;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMRepository extends SecondOrderParamsRepository {
	public static final String STR_HBA1C = "HbA1c";
	public static final String STR_DURATION = "Duration";

	public T1DMRepository(int nRuns, int nPatients) {
		super(nRuns, nPatients);
		final Disease dis = new T1DMDisease(this);
//		setPopulation(new T1DMSimpleTestPopulation(this, dis));
//		new SMBG_Intervention(this);
//		new CGM_Intervention(this);
		setPopulation(new DCCTPopulation1(this, dis));
		setDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
		new DCCT_ConventionalIntervention(this);
		new DCCT_IntensiveIntervention(this);
	}

}
