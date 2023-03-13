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
	final private static TreeMap<DiabetesComplicationStage, TreeSet<DiabetesComplicationStage>> EXCLUSIONS;
	final private static ArrayList<ArrayList<TreeSet<DiabetesComplicationStage>>> STAGE_COMBINATIONS;
	final private static ArrayList<DiabetesComplicationStage> STAGES;
	final private static int MAX_COMBINATION_SIZE = 3;
	final private static int[] COMBINATION_COUNTER = new int[MAX_COMBINATION_SIZE];
	static {
		STAGES = initializeStages();
		EXCLUSIONS = initializeExclusions();
		STAGE_COMBINATIONS = initializeStageCombinations();		
		shuffleCombinations();
	}

	protected DiabPlusExplorationSecondOrderRepository(int nPatients, DiabPlusStdPopulation population, ArrayList<Double> hba1cLevels) {
		super(nPatients, population);

		registerComplication(new SheffieldRETSubmodel());
		registerComplication(new SheffieldNPHSubmodel());
		registerComplication(new SimpleCHDSubmodel());
		registerComplication(new SimpleNEUSubmodel());
		
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
	private static TreeMap<DiabetesComplicationStage, TreeSet<DiabetesComplicationStage>> initializeExclusions() {
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
	
	private static ArrayList<DiabetesComplicationStage> initializeStages() {
		ArrayList<DiabetesComplicationStage> stages = new ArrayList<>();
		int order = 0;
		// Complications of retinopathy
		for (DiabetesComplicationStage stage : SheffieldRETSubmodel.RETSubstates) {
			stages.add(stage);
			stage.setOrder(order++);
		}
		// Complications of nephropathy
		for (DiabetesComplicationStage stage : SheffieldNPHSubmodel.STAGES) {
			stages.add(stage);
			stage.setOrder(order++);
		}
		// Complications of CHD
		for (DiabetesComplicationStage stage : SimpleCHDSubmodel.CHDSubstates) {
			stages.add(stage);
			stage.setOrder(order++);
		}
		// Complications of neuropathy
		for (DiabetesComplicationStage stage : SimpleNEUSubmodel.NEUSubstates) {
			stages.add(stage);
			stage.setOrder(order++);
		}
		return stages;
	}
	
	private static ArrayList<ArrayList<TreeSet<DiabetesComplicationStage>>> initializeStageCombinations() {
		ArrayList<ArrayList<TreeSet<DiabetesComplicationStage>>> combinations = new ArrayList<>();
		// Create combinations up to three stages
		for (int i = 0; i < MAX_COMBINATION_SIZE; i++) {
			combinations.add(new ArrayList<>());
		}
		for (int i = 0; i < STAGES.size(); i++) {
			// Create combinations of just one stage
			final DiabetesComplicationStage stage1 = STAGES.get(i);
			TreeSet<DiabetesComplicationStage> comb1 = new TreeSet<>();
			comb1.add(stage1);
			combinations.get(0).add(comb1);			
			// Create combinations of two stages
			for (int j = i + 1; j < STAGES.size(); j++) {
				final DiabetesComplicationStage stage2 = STAGES.get(j);
				if (!EXCLUSIONS.get(stage1).contains(stage2)) {
					TreeSet<DiabetesComplicationStage> comb2 = new TreeSet<>(comb1);
					comb2.add(stage2);
					combinations.get(1).add(comb2);			
					// Create combinations of three stages
					for (int k = j + 1; k < STAGES.size(); k++) {
						final DiabetesComplicationStage stage3 = STAGES.get(k);
						if (!EXCLUSIONS.get(stage1).contains(stage3) && !EXCLUSIONS.get(stage2).contains(stage3)) {
							TreeSet<DiabetesComplicationStage> comb3 = new TreeSet<>(comb2);
							comb3.add(stage3);
							combinations.get(2).add(comb3);			
						}
					}
				}
			}
		}
		return combinations;		
	}
	
	private static void shuffleCombinations() {
		for (int i = 0; i < MAX_COMBINATION_SIZE; i++) {
			Collections.shuffle(STAGE_COMBINATIONS.get(i));
			COMBINATION_COUNTER[i] = 0;
		}
	}
	/**
	 * @return the exclusions
	 */
	public static TreeMap<DiabetesComplicationStage, TreeSet<DiabetesComplicationStage>> getExclusions() {
		return EXCLUSIONS;
	}
	
	public static ArrayList<TreeSet<DiabetesComplicationStage>> getStageCombinations(int size) {
		if (size == 0) 
			return new ArrayList<>();
		if (size > MAX_COMBINATION_SIZE)
			return null;
		return STAGE_COMBINATIONS.get(size - 1);
	}

	/**
	 * Returns a random collection of initial chronic complication stages for a patient. The collection should fit to the specified size, but due to the way it 
	 * is constructed, it may include less stages. If size is 0, returns a null collection; if size is 1, returns a single random stage.
	 * @param size Maximum size of the collection
	 * @return A random collection of initial chronic complication stages for a patient
	 */
	public static TreeSet<DiabetesComplicationStage> getRndCollectionOfStages(int size) {
		final ArrayList<TreeSet<DiabetesComplicationStage>> combinations = getStageCombinations(size);
		if (size > MAX_COMBINATION_SIZE)
			return null;
		if (combinations.size() == 0)
			return new TreeSet<>();
		final TreeSet<DiabetesComplicationStage> stages = combinations.get(COMBINATION_COUNTER[size - 1]);
		COMBINATION_COUNTER[size - 1] = (COMBINATION_COUNTER[size - 1] + 1) % combinations.size();
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

	public static void main(String[] args) {
		for (int size = 0; size <= 3; size++) {
			ArrayList<TreeSet<DiabetesComplicationStage>> comb = getStageCombinations(size);
			Collections.shuffle(comb);
			if (comb.size() == 0) {
				System.out.println("No Combinations of level " + size);
			}
			else {
				System.out.println("Combinations of level " + size);
				for (TreeSet<DiabetesComplicationStage> stages : comb) {
					System.out.println(print(stages));
				}
			}
		}
		System.out.println("Get random element of size 0");
		System.out.println(print(getRndCollectionOfStages(0)));
		System.out.println("Get random elements of size 1");
		for (int i = 0; i < 15; i++)
			System.out.println(print(getRndCollectionOfStages(1)));
//		System.out.println(print(getRndCollectionOfStages(1)));
		System.out.println("Get random elements of size 2");
		System.out.println(print(getRndCollectionOfStages(2)));
		System.out.println(print(getRndCollectionOfStages(2)));
		System.out.println("Get random elements of size 3");
		System.out.println(print(getRndCollectionOfStages(3)));
		System.out.println(print(getRndCollectionOfStages(3)));
	}
}
