/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.DCCT;

import es.ull.iis.simulation.hta.diabetes.interventions.DCCTConventionalIntervention;
import es.ull.iis.simulation.hta.diabetes.interventions.DCCTIntensiveIntervention;
import es.ull.iis.simulation.hta.diabetes.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.SubmodelCostCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.SubmodelUtilityCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.populations.DCCTPopulation;
import es.ull.iis.simulation.hta.diabetes.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.ChronicComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.DeathSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldNPHSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldRETSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleCHDSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleNEUSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.StandardSpainDeathSubmodel;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class DCCTSecondOrderParams extends SecondOrderParamsRepository {
	/** {Deterministic value, cases, no cases} for initial proportion of background retinopathy */
	private static final double [] P_INIT_BGRET = {0.187342817, 269.961, 1441 - 269.961}; // DCCT: https://www.ncbi.nlm.nih.gov/pmc/articles/PMC2866072/
	
	/**
	 * @param nPatients Number of patients to create
	 */
	public DCCTSecondOrderParams(int nPatients) {
		super(nPatients, new DCCTPopulation());

		addProbParam(new SecondOrderParam(getInitProbString(SheffieldRETSubmodel.BGRET), "Initial probability of background retinopathy", 
				"DCCT: https://www.ncbi.nlm.nih.gov/pmc/articles/PMC2866072/", P_INIT_BGRET[0], "BetaVariate", P_INIT_BGRET[1], P_INIT_BGRET[2]));
		
		
		registerComplication(new SheffieldRETSubmodel());
		registerComplication(new SheffieldNPHSubmodel());
		registerComplication(new SimpleCHDSubmodel());
		registerComplication(new SimpleNEUSubmodel());
		
		registerComplication(new DCCTSevereHypoglycemiaEvent());

		registerIntervention(new DCCTConventionalIntervention());
		registerIntervention(new DCCTIntensiveIntervention());
	}

	@Override
	public DeathSubmodel getDeathSubmodel() {
		return new StandardSpainDeathSubmodel(this);
	}
	
	@Override
	public CostCalculator getCostCalculator(double cDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelCostCalculator(cDNC, submodels, acuteSubmodels);
	}
	
	@Override
	public UtilityCalculator getUtilityCalculator(double duDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelUtilityCalculator(DisutilityCombinationMethod.ADD, duDNC, BasicConfigParams.DEF_U_GENERAL_POP, submodels, acuteSubmodels);
	}


}
