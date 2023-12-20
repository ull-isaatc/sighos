package es.ull.iis.simulation.hta.osdi.builders;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.osdi.OSDiGenericModel;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper.DataItemType;
import es.ull.iis.simulation.hta.osdi.wrappers.ParameterWrapper;
import es.ull.iis.simulation.hta.params.AnnualRiskBasedTimeToEventParameter;
import es.ull.iis.simulation.hta.params.Parameter;
import es.ull.iis.simulation.hta.params.ProportionBasedTimeToEventParameter;
import es.ull.iis.simulation.hta.params.RiskParamDescriptions;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;

public interface TimeToEventCalculatorBuilder {
    public enum SupportedCombinations {
        PROBABILITY(new DataItemType[] { DataItemType.DI_PROBABILITY}),
        PROPORTION(new DataItemType[] { DataItemType.DI_PROPORTION}),
        // TIME_TO_EVENT(new DataItemType[] { DataItemType.DI_TIME_TO_EVENT}),
        PROBABILITY_RR(new DataItemType[] { DataItemType.DI_PROBABILITY, DataItemType.DI_RELATIVE_RISK}),
        PROPORTION_RR(new DataItemType[] { DataItemType.DI_PROPORTION, DataItemType.DI_RELATIVE_RISK});
        private final int n;
        private final DataItemType[] dataItems;

        private SupportedCombinations(DataItemType[] dataItems) {
            this.dataItems = dataItems;
            this.n = dataItems.length;
        }
    }

	/**
	 * Creates the calculator for the time to event associated to this pathway. Currently only allows the time to be expressed as an annual risk and, consequently, uses 
	 * a {@link AnnualRiskBasedTimeToEventParameter}. 
	 * @param secParams Repository
	 * @param progression The destination progression for this pathway
	 * @return
	 * @throws MalformedOSDiModelException 
	 */
	public static Parameter getTimeToEventCalculator(OSDiGenericModel secParams, DiseaseProgression progression, ArrayList<ParameterWrapper> riskWrappers) throws MalformedOSDiModelException {
        final SupportedCombinations comb = foundValidCombination(riskWrappers);
        if (comb == null) {
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.MANIFESTATION_PATHWAY, progression.name(), OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION, "Unsupported combination of parameters for risk characterization.");
        }
        // TODO: RRs require being parameters and not calculators. Hence, all the timetoevent calculators should use the name of the RR parameter and not the calculator
        switch(comb) {
            case PROPORTION:
                return new ProportionBasedTimeToEventParameter(secParams, progression, RiskParamDescriptions.PROPORTION.getParameterName(riskWrappers.get(0).getOriginalIndividualIRI()));
            case PROBABILITY_RR:
                if (riskWrappers.get(0).getDataItemTypes().contains(DataItemType.DI_RELATIVE_RISK)) {
                    return new AnnualRiskBasedTimeToEventParameter(secParams, progression, RiskParamDescriptions.PROBABILITY.getParameterName(riskWrappers.get(1).getOriginalIndividualIRI()));
                } else {
                    return new AnnualRiskBasedTimeToEventParameter(secParams, progression, RiskParamDescriptions.PROBABILITY.getParameterName(riskWrappers.get(0).getOriginalIndividualIRI()));
                }
            case PROPORTION_RR:
                if (riskWrappers.get(0).getDataItemTypes().contains(DataItemType.DI_RELATIVE_RISK)) {
                    return new ProportionBasedTimeToEventParameter(secParams, progression, RiskParamDescriptions.PROPORTION.getParameterName(riskWrappers.get(1).getOriginalIndividualIRI()));
                } else {
                    return new ProportionBasedTimeToEventParameter(secParams, progression, RiskParamDescriptions.PROPORTION.getParameterName(riskWrappers.get(0).getOriginalIndividualIRI()));
                }
            case PROBABILITY:
            default:
                return new AnnualRiskBasedTimeToEventParameter(secParams, progression, RiskParamDescriptions.PROBABILITY.getParameterName(riskWrappers.get(0).getOriginalIndividualIRI()));
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
                        if (temp.get(j).getDataItemTypes().contains(comb.dataItems[i])) {
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
