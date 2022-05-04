package es.ull.iis.simulation.hta.osdi;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.utils.ValueParser;
import es.ull.iis.simulation.hta.osdi.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.hta.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.AcuteManifestation;
import es.ull.iis.simulation.hta.progression.ChronicManifestation;
import es.ull.iis.simulation.hta.progression.Manifestation;

public class SecondOrderParamsBuilder {
	public static String recalculatePropabilityField(String manifestationName, String dataPropertyValue, String dataPropertyValueDistro) {
		String mf = "";
		if (OwlHelper.getDataPropertyValue(manifestationName, dataPropertyValue) != null) {
			mf += OwlHelper.getDataPropertyValue(manifestationName, dataPropertyValue);
		} 		
		if (!mf.isEmpty() && OwlHelper.getDataPropertyValue(manifestationName, dataPropertyValueDistro) != null) {
			mf += Constants.CONSTANT_HASHTAG + OwlHelper.getDataPropertyValue(manifestationName, dataPropertyValueDistro);
		} else if (mf.isEmpty() && OwlHelper.getDataPropertyValue(manifestationName, dataPropertyValueDistro) != null) {
			mf += OwlHelper.getDataPropertyValue(manifestationName, dataPropertyValueDistro);
		}
		return !mf.isEmpty() ? mf : null;
	}
	
	public static List<Guideline> getGuidelines(String objectName) {
		List<Guideline> result = new ArrayList<>();
		List<String> guidelines = OwlHelper.getChildsByClassName(objectName, Constants.CLASS_GUIDELINES);
		for (String guidelineName: guidelines) {
			Guideline guideline = new Guideline();
			guideline.setName(guidelineName);
			guideline.setConditions(OwlHelper.getDataPropertyValue(guidelineName, Constants.DATAPROPERTY_CONDITIONS));
			guideline.setDose(OwlHelper.getDataPropertyValue(guidelineName, Constants.DATAPROPERTY_DOSE));
			guideline.setFrequency(OwlHelper.getDataPropertyValue(guidelineName, Constants.DATAPROPERTY_FREQUENCY));
			guideline.setHoursIntervals(OwlHelper.getDataPropertyValue(guidelineName, Constants.DATAPROPERTY_HOURSINTERVAL));
			guideline.setRange(OwlHelper.getDataPropertyValue(guidelineName, Constants.DATAPROPERTY_RANGE));
			guideline.setUtilities(getUtilities(guidelineName));
			result.add(guideline);
		}
		return !result.isEmpty() ? result : null;
	}
}
