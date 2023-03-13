/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.diabplus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.TreeMap;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.SubmodelCostCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.SubmodelUtilityCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.ChronicComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.EmpiricalSpainDeathSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldNPHSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldRETSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleCHDSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleNEUSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.StandardSevereHypoglycemiaEvent;
import es.ull.iis.util.Statistics;

/**
 * @author icasrod
 *
 */
public class DiabPlusExplorationSecondOrderRepository extends SecondOrderParamsRepository {
	final private TreeMap<DiabetesComplicationStage, TreeSet<DiabetesComplicationStage>> exclusions;
	final private static ArrayList<DiabetesComplicationStage> STAGES;
	final private static ArrayList<DiabetesComplicationStage> RND_STAGES;
	static {
		STAGES = new ArrayList<>();
		int order = 0;
		// Complications of retinopathy
		for (DiabetesComplicationStage stage : SheffieldRETSubmodel.RETSubstates) {
			STAGES.add(stage);
			stage.setOrder(order++);
		}
		// Complications of nephropathy
		for (DiabetesComplicationStage stage : SheffieldNPHSubmodel.STAGES) {
			STAGES.add(stage);
			stage.setOrder(order++);
		}
		// Complications of CHD
		for (DiabetesComplicationStage stage : SimpleCHDSubmodel.CHDSubstates) {
			STAGES.add(stage);
			stage.setOrder(order++);
		}
		// Complications of neuropathy
		for (DiabetesComplicationStage stage : SimpleNEUSubmodel.NEUSubstates) {
			STAGES.add(stage);
			stage.setOrder(order++);
		}
		RND_STAGES = new ArrayList<>(STAGES);
	}

	protected DiabPlusExplorationSecondOrderRepository(int nPatients, DiabPlusStdPopulation population, ArrayList<Double> hba1cLevels) {
		super(nPatients, population);

		registerComplication(new SheffieldRETSubmodel());
		registerComplication(new SheffieldNPHSubmodel());
		registerComplication(new SimpleCHDSubmodel());
		registerComplication(new SimpleNEUSubmodel());
		
		this.exclusions = initializeExclusions();
		
		final StandardSevereHypoglycemiaEvent hypoEvent = new StandardSevereHypoglycemiaEvent(
				new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_P_HYPO, "Annual rate of severe hypoglycemia events", "Assumption", population.getHypoRate()), 
				new SecondOrderParam(StandardSevereHypoglycemiaEvent.STR_RR_HYPO, "No RR", "Assumption", 1.0), 
				EnumSet.of(DiabetesType.T1), true);
		registerComplication(hypoEvent);
		
		for (double hba1cLevel : hba1cLevels)
			registerIntervention(new DiabPlusExplorationStdIntervention(0.0, hba1cLevel));

		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + STR_NO_COMPLICATIONS, "Cost of Diabetes with no complications", 
				BasicConfigParams.DEF_C_DNC.SOURCE, BasicConfigParams.DEF_C_DNC.YEAR, 
				BasicConfigParams.DEF_C_DNC.VALUE, getRandomVariateForCost(BasicConfigParams.DEF_C_DNC.VALUE)));

		final double[] paramsDuDNC = Statistics.betaParametersFromNormal(BasicConfigParams.DEF_DU_DNC[0], BasicConfigParams.DEF_DU_DNC[1]);
		addUtilParam(new SecondOrderParam(STR_DISUTILITY_PREFIX + STR_NO_COMPLICATIONS, "Disutility of DNC", "", 
				BasicConfigParams.DEF_DU_DNC[0], "BetaVariate", paramsDuDNC[0], paramsDuDNC[1]));
		
		registerDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
	}
	
	/**
	 * Initializes an exclusion list to take into account stages that should not affect a patient at the same time 
	 * @return A map where each key is a stage of a chronic complication, and the values are those other stages that cannot affect the patient at the same time 
	 */
	private TreeMap<DiabetesComplicationStage, TreeSet<DiabetesComplicationStage>> initializeExclusions() {
		final TreeMap<DiabetesComplicationStage, TreeSet<DiabetesComplicationStage>> exclusions = new TreeMap<>();
		TreeSet<DiabetesComplicationStage> excl;
		
		// Complications of retinopathy
		for (DiabetesComplicationStage stage : SheffieldRETSubmodel.RETSubstates) {
			excl = new TreeSet<>();
			// Exclude themselves
			excl.add(stage);
			exclusions.put(stage, excl);
		}
		excl = exclusions.get(SheffieldRETSubmodel.BGRET);
		excl.add(SheffieldRETSubmodel.PRET);
		excl.add(SheffieldRETSubmodel.BLI);
		excl = exclusions.get(SheffieldRETSubmodel.PRET);
		excl.add(SheffieldRETSubmodel.BGRET);
		excl.add(SheffieldRETSubmodel.BLI);
		excl = exclusions.get(SheffieldRETSubmodel.BLI);
		excl.add(SheffieldRETSubmodel.BGRET);
		excl.add(SheffieldRETSubmodel.PRET);
		excl.add(SheffieldRETSubmodel.ME);
		excl = exclusions.get(SheffieldRETSubmodel.ME);
		excl.add(SheffieldRETSubmodel.BLI);
		
		// Complications of nephropathy
		for (DiabetesComplicationStage stage : SheffieldNPHSubmodel.STAGES) {
			excl = new TreeSet<>();
			// Exclude everything else
			for (DiabetesComplicationStage otherStage : SheffieldNPHSubmodel.STAGES)
				excl.add(otherStage);
			exclusions.put(stage, excl);
		}
		// Complications of CHD
		for (DiabetesComplicationStage stage : SimpleCHDSubmodel.CHDSubstates) {
			excl = new TreeSet<>();
			// Exclude everything else
			for (DiabetesComplicationStage otherStage : SimpleCHDSubmodel.CHDSubstates)
				excl.add(otherStage);
			exclusions.put(stage, excl);
		}
		// Complications of neuropathy
		for (DiabetesComplicationStage stage : SimpleNEUSubmodel.NEUSubstates) {
			excl = new TreeSet<>();
			// Exclude everything else
			for (DiabetesComplicationStage otherStage : SimpleNEUSubmodel.NEUSubstates)
				excl.add(otherStage);
			exclusions.put(stage, excl);
		}
		return exclusions;
	}
	
	/**
	 * @return the exclusions
	 */
	public TreeMap<DiabetesComplicationStage, TreeSet<DiabetesComplicationStage>> getExclusions() {
		return exclusions;
	}

	/**
	 * Returns a random collection of initial chronic complication stages for a patient. The collection should fit to the specified size, but due to the way it 
	 * is constructed, it may include less stages. If size is 0, returns a null collection; if size is 1, returns a single random stage.
	 * @param size Maximum size of the collection
	 * @return A random collection of initial chronic complication stages for a patient
	 */
	public TreeSet<DiabetesComplicationStage> getRndCollectionOfStages(int size) {
		// Orders the list randomly
		Collections.shuffle(RND_STAGES);
		final TreeSet<DiabetesComplicationStage> stages = new TreeSet<>();
		final TreeSet<DiabetesComplicationStage> excl = new TreeSet<>();
		if (size > 0) {
			// Iterates to build the list of "compatible" stages
			for (int i = 0; i < size; i++) {
				int candidateIndex = i;
				boolean found = false;
				// Iterates over the random ordered list
				while (!found && candidateIndex < size) {
					final DiabetesComplicationStage candidate = RND_STAGES.get(candidateIndex);
					if (!excl.contains(candidate)) {
						stages.add(candidate);
						excl.addAll(exclusions.get(candidate));
						found = true;
					}
					candidateIndex++;
				}
			}
		}
		return stages;
	}
	@Override
	public CostCalculator getCostCalculator(double cDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelCostCalculator(cDNC, submodels, acuteSubmodels);
	}
	
	@Override
	public UtilityCalculator getUtilityCalculator(double duDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelUtilityCalculator(DisutilityCombinationMethod.ADD, duDNC, BasicConfigParams.DEF_U_GENERAL_POP, submodels, acuteSubmodels);
	}
	
	// For testing only
	public static String print(TreeSet<DiabetesComplicationStage> stages) {
		String str = "";
		for (DiabetesComplicationStage stage : stages) {
			str += stage + ":";
		}
		return str;
	}
}
