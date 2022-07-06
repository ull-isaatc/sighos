/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.util.List;

import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.utils.CostUtils;
import es.ull.iis.simulation.hta.osdi.wrappers.Matrix;
import es.ull.iis.simulation.hta.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.outcomes.DiseaseCostCalculator;
import es.ull.iis.simulation.hta.outcomes.DiseaseUtilityCalculator;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.EmpiricalSpainDeathSubmodel;

/**
 * @author masbe
 *
 */
public class OSDiGenericRepository extends SecondOrderParamsRepository {
	private final CostCalculator costCalc;
	private final UtilityCalculator utilCalc;
	private final Matrix treatmentCosts;
	private final Matrix followUpCosts;

	/**
	 * @param nRuns
	 * @param nPatients
	 */
	public OSDiGenericRepository(int nRuns, int nPatients, String diseaseId, String populationId, DisutilityCombinationMethod method, double generalPopulationUtility) {
		super(nRuns, nPatients);
		costCalc = new DiseaseCostCalculator(this);
		utilCalc = new DiseaseUtilityCalculator(this, method, generalPopulationUtility);
		treatmentCosts = new Matrix();
		followUpCosts = new Matrix();
		// TODO
//		CostUtils.loadCostFromTreatmentStrategies(this.treatmentCosts, naturalDevelopment.getName(), diseaseJSON.getTreatmentStrategies(), timeHorizon);
//		CostUtils.loadCostFromFollowUpStrategies(this.followUpCosts, naturalDevelopment.getName(), diseaseJSON.getFollowUpStrategies(), timeHorizon);

		Disease disease = DiseaseBuilder.getDiseaseInstance(this, diseaseId);
		setPopulation(PopulationBuilder.getPopulationInstance(this, disease, populationId));
		
		// TODO: Death submodel should be context specific, depending on the population
		setDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
		
		// Build interventions
		List<String> interventions = OwlHelper.getChildsByClassName(disease.name(), OSDiNames.Class.INTERVENTION.getDescription());
		try {
			for (String interventionName : interventions) {
				InterventionBuilder.getInterventionInstance(this, interventionName);
			}
		} catch (TranspilerException e) {
			e.printStackTrace();
		}
		
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
