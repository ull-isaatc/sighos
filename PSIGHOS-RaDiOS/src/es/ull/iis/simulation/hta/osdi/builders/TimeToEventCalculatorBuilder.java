package es.ull.iis.simulation.hta.osdi.builders;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.osdi.OSDiGenericModel;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiDataItemTypes;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiClasses;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiObjectProperties;
import es.ull.iis.simulation.hta.osdi.ontology.ParameterWrapper;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.calculator.AnnualRiskBasedTimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.calculator.ProportionBasedTimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.calculator.TimeToEventCalculator;

public interface TimeToEventCalculatorBuilder {
    public enum SupportedCombinations {
        PROBABILITY(new OSDiDataItemTypes[] { OSDiDataItemTypes.DI_PROBABILITY}),
        PROPORTION(new OSDiDataItemTypes[] { OSDiDataItemTypes.DI_PROPORTION}),
        // TIME_TO_EVENT(new DataItemType[] { DataItemType.DI_TIME_TO_EVENT}),
        PROBABILITY_RR(new OSDiDataItemTypes[] { OSDiDataItemTypes.DI_PROBABILITY, OSDiDataItemTypes.DI_RELATIVE_RISK});
        private final int n;
        private final OSDiDataItemTypes[] dataItems;

        private SupportedCombinations(OSDiDataItemTypes[] dataItems) {
            this.dataItems = dataItems;
            this.n = dataItems.length;
        }
    }

	/**
	 * Creates the calculator for the time to event associated to this pathway. Currently only allows the time to be expressed as an annual risk and, consequently, uses 
	 * a {@link AnnualRiskBasedTimeToEventCalculator}. 
	 * @param model Repository
	 * @param progression The destination progression for this pathway
	 * @return
	 * @throws MalformedOSDiModelException 
	 */
	public static TimeToEventCalculator getTimeToEventCalculator(OSDiGenericModel model, DiseaseProgression progression, ArrayList<ParameterWrapper> riskWrappers) throws MalformedOSDiModelException {
        final SupportedCombinations comb = foundValidCombination(riskWrappers);
        if (comb == null) {
			throw new MalformedOSDiModelException(OSDiClasses.DISEASE_PROGRESSION_PATHWAY, progression.name(), OSDiObjectProperties.HAS_RISK_CHARACTERIZATION, "Unsupported combination of parameters for risk characterization.");
        }
        switch(comb) {
            case PROPORTION:
                return new ProportionBasedTimeToEventCalculator(progression, StandardParameter.PROPORTION.createName(riskWrappers.get(0).getOriginalIndividualIRI()));
            case PROBABILITY_RR:
                if (riskWrappers.get(0).getDataItemType().equals(OSDiDataItemTypes.DI_RELATIVE_RISK)) {
                    return new AnnualRiskBasedTimeToEventCalculator(progression, StandardParameter.PROBABILITY.createName(riskWrappers.get(1).getOriginalIndividualIRI()), StandardParameter.RELATIVE_RISK.createName(riskWrappers.get(0).getOriginalIndividualIRI()));
                } else {
                    return new AnnualRiskBasedTimeToEventCalculator(progression, StandardParameter.PROBABILITY.createName(riskWrappers.get(0).getOriginalIndividualIRI()));
                }
            case PROBABILITY:
            default:
                return new AnnualRiskBasedTimeToEventCalculator(progression, StandardParameter.PROBABILITY.createName(riskWrappers.get(0).getOriginalIndividualIRI()));
        }
	}
 
    public static SupportedCombinations foundValidCombination(ArrayList<ParameterWrapper> riskWrappers) {
        for (SupportedCombinations comb : SupportedCombinations.values()) {
            if (comb.n == riskWrappers.size()) {
                final ArrayList<ParameterWrapper> temp = new ArrayList<>(riskWrappers);

                // Starts the search
                boolean valid = true;
                for (int i = 0; i < comb.n && valid; i++) {
                    // Resets the condition until a valid combination is found
                    valid = false;
                    // One of the remaining wrappers must contain the data item
                    for (int j = 0; j < temp.size() && !valid; j++) {
                        if (temp.get(j).getDataItemType().equals(comb.dataItems[i])) {
                            valid = true;
                            temp.remove(j);
                        }
                    }
                }
                if (valid) {
                    return comb;
                }
            }
        }
        return null;
    }
}
