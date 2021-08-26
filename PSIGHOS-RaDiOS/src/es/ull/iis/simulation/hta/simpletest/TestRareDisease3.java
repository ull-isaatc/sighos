/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.AnnualRiskBasedTimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.progression.PreviousManifestationCondition;
import es.ull.iis.simulation.hta.progression.PathwayCondition;
import es.ull.iis.simulation.hta.progression.ManifestationPathway;
import es.ull.iis.simulation.hta.progression.TimeToEventCalculator;

/**
 * A disease with two chronic (and independent) manifestations. The first manifestation increases the risk of the second. 
 * The intervention reduces the risk of the second manifestation
 * @author Iván Castilla Rodríguez
 *
 */
public class TestRareDisease3 extends TemplateTestRareDisease {
	final private static double P_MANIF1 = 0.1;
	final private static double P_MANIF2 = 0.02;
	final private static double P_MANIF1_MANIF2 = 0.05;
	/** First manifestation (mild) */
	final private Manifestation manif1;
	/** Second manifestation (severe) */
	final private Manifestation manif2;
	
	/**
	 * @param secParams Repository with common information about the disease 
	 */
	public TestRareDisease3(SecondOrderParamsRepository secParams) {
		super(secParams, "RD3", "Test rare disease 3");
		manif1 = new TestManifestationStage1(secParams, this);
		manif2 = new TestManifestationStage2(secParams, this);
		addManifestation(manif1);
		addManifestation(manif2);
		TimeToEventCalculator tte = new AnnualRiskBasedTimeToEventCalculator(SecondOrderParamsRepository.getProbString(manif1), secParams, manif1);
		new ManifestationPathway(secParams, manif1, tte);
		tte = new AnnualRiskBasedTimeToEventCalculator(SecondOrderParamsRepository.getProbString(manif2), secParams, manif2);
		new ManifestationPathway(secParams, manif2, tte);
		final PathwayCondition cond = new PreviousManifestationCondition(manif1);
		tte = new AnnualRiskBasedTimeToEventCalculator(SecondOrderParamsRepository.getProbString(manif1, manif2), secParams, manif2);
		new ManifestationPathway(secParams, manif2, cond, tte); 
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addProbParam(manif1, "Test", P_MANIF1, SecondOrderParamsRepository.getRandomVariateForProbability(P_MANIF1));
		secParams.addProbParam(manif2, "Test", P_MANIF2, SecondOrderParamsRepository.getRandomVariateForProbability(P_MANIF2));
		secParams.addProbParam(manif1, manif2, "Test", P_MANIF1_MANIF2, SecondOrderParamsRepository.getRandomVariateForProbability(P_MANIF1_MANIF2));
	}

	@Override
	public ArrayList<String> getParamNames() {
		ArrayList<String> list = new ArrayList<>();
		list.add(SecondOrderParamsRepository.getProbString(manif1));
		list.add(SecondOrderParamsRepository.getProbString(manif2));
		list.add(SecondOrderParamsRepository.getProbString(manif1, manif2));		
		return list;
	}
}
