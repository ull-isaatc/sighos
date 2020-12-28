package es.ull.iis.simulation.hta.radios.transforms;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import es.ull.iis.ontology.radios.Constants;
import es.ull.iis.ontology.radios.utils.CollectionUtils;
import es.ull.iis.ontology.radios.xml.datatables.ColumnType;
import es.ull.iis.ontology.radios.xml.datatables.ContentKind;
import es.ull.iis.ontology.radios.xml.datatables.Datatable;
import es.ull.iis.ontology.radios.xml.datatables.RowType;
import es.ull.iis.simulation.hta.radios.wrappers.ProbabilityDistribution;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

public class ValueTransform {
	private static int DETERMINISTIC_VALUE_POS = 2;
	private static int DISTRIBUTION_NAME_POS = 3;
	private static int FIRST_PARAM_4_DISTRIBUTION_POS = 4;
	private static int SECOND_PARAM_4_DISTRIBUTION_POS = 6;

	private static String DISTRIBUTION_NAME_SUFFIX = "Variate";
	private static String REGEX_RANGE = "(-?[0-9]+(\\.[0-9]+)?)-(-?[0-9]+(\\.[0-9]+)?)";
	
   /**
    * @param distributionName
    * @param firstParameter
    * @param secondParameter
    * @return
    */
   private static RandomVariate buildDistributionVariate(String distributionName, String firstParameter, String secondParameter) {
   	if (distributionName.toUpperCase().contains("EXP")) {
   		distributionName = "Exponential";
   	}
   	
   	if (secondParameter != null) {
   		RandomVariateFactory.getInstance(WordUtils.capitalizeFully(distributionName) + DISTRIBUTION_NAME_SUFFIX, new Double(firstParameter), new Double(secondParameter));
   	} else {
   		RandomVariateFactory.getInstance(WordUtils.capitalizeFully(distributionName) + DISTRIBUTION_NAME_SUFFIX, new Double(firstParameter));
   	}
		return null;   	
   }
	
	/**
	 * @param value
	 * @return
	 */
	public static ProbabilityDistribution splitProbabilityDistribution (String value) {
		ProbabilityDistribution result = null;		
		if (value != null) {
			String valueNormalized = value.toUpperCase().replace(" ", "");
			Pattern pattern = Pattern.compile(Constants.REGEX_NUMERICVALUE_DISTRO_EXTENDED);
			Matcher matcher = pattern.matcher(valueNormalized);
			if (matcher.find()) {			
				Double deterministicValue = (matcher.group(DETERMINISTIC_VALUE_POS) != null) ? new Double(matcher.group(DETERMINISTIC_VALUE_POS)) : 1.0;
				String distributionName = matcher.group(DISTRIBUTION_NAME_POS);
				String firstParameter = matcher.group(FIRST_PARAM_4_DISTRIBUTION_POS);
				String secondParameter = matcher.group(SECOND_PARAM_4_DISTRIBUTION_POS);
				result = new ProbabilityDistribution(deterministicValue, buildDistributionVariate(distributionName, firstParameter, secondParameter));
			}
		}
		return result;		
	}
	
	/**
	 * If it is a table in which the first column is age ranges, we assume that they are correctly ordered and that there is no intersection between the ranges.
	 * @param datatable Radios datatable transformed.
	 * @return Matrix of values.
	 */
	public static double[][] rangeDatatableToMatrix (Datatable datatable) {
		double[][] result = null; 
		if (datatable != null) {
			List<RowType> rows = datatable.getContent().getRow();
			if (CollectionUtils.notIsEmpty(rows)) {
				result = new double[rows.size()][3];
				for (int i = 0; i < rows.size(); i++) {
					for (ColumnType column : rows.get(i).getColumn()) {						
						if (ContentKind.RANGE.equals(column.getType())) {
							Pattern pattern = Pattern.compile(REGEX_RANGE);
							Matcher matcher = pattern.matcher(column.getValue());
							if (matcher.find()) {
								result[i][0] = new Double(matcher.group(1).replace(",", ".")); 
								result[i][1] = new Double(matcher.group(3).replace(",", "."));
							}
						} else if (ContentKind.NUMBER.equals(column.getType())) {
							result[i][2] = (!StringUtils.isEmpty(column.getValue()) ? new Double(column.getValue().replace(",", ".")) : 0.0);
						}
					}
				}
			}
		}		
		return result;
	}
}