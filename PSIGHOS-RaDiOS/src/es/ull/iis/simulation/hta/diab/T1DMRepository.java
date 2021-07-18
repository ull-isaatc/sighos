/**
 * 
 */
package es.ull.iis.simulation.hta.diab;

import es.ull.iis.simulation.hta.diab.interventions.CGM_Intervention;
import es.ull.iis.simulation.hta.diab.interventions.SMBG_Intervention;
import es.ull.iis.simulation.hta.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.outcomes.DiseaseCostCalculator;
import es.ull.iis.simulation.hta.outcomes.DiseaseUtilityCalculator;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator.DisutilityCombinationMethod;
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
	/** Default utility for general population: From adult Spanish population but those with DM */ 
	public static double DEF_U_GENERAL_POP = 0.911400915;
	private final CostCalculator costCalc;
	private final UtilityCalculator utilCalc;

	public T1DMRepository(int nRuns, int nPatients) {
		super(nRuns, nPatients);
		costCalc = new DiseaseCostCalculator(this);
		utilCalc = new DiseaseUtilityCalculator(this, DisutilityCombinationMethod.ADD, 0.911400915);
		final Disease dis = new T1DMDisease(this);
		// FIXME: ¿Deberíamos hacer esto siempre?
		//registerDisease(HEALTHY);
		registerDisease(dis);
		registerPopulation(new T1DMGoldDiamondPopulation(this, dis));
		registerIntervention(new CGM_Intervention(this));
		registerIntervention(new SMBG_Intervention(this));
		registerDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
	}

	@Override
	public CostCalculator getCostCalculator() {
		return costCalc;
	}

	@Override
	public UtilityCalculator getUtilityCalculator() {
		return utilCalc;
	}

}
