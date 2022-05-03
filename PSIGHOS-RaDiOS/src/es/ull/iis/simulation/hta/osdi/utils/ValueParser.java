package es.ull.iis.simulation.hta.osdi.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import es.ull.iis.ontology.radios.xml.datatables.ColumnType;
import es.ull.iis.ontology.radios.xml.datatables.ContentKind;
import es.ull.iis.ontology.radios.xml.datatables.Datatable;
import es.ull.iis.ontology.radios.xml.datatables.RowType;
import es.ull.iis.simulation.hta.osdi.Constants;
import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.hta.params.SecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

public class ValueParser {
	private static int DETERMINISTIC_VALUE_POS = 1;
	private static int DISTRIBUTION_NAME_POS = 3;
	private static int FIRST_PARAM_4_DISTRIBUTION_POS = 4;
	private static int SECOND_PARAM_4_DISTRIBUTION_POS = 6;

	private static String DISTRIBUTION_NAME_SUFFIX = "Variate";
	private static String REGEX_RANGE = "(-?[0-9]+(\\.[0-9]+)?)-(-?[0-9]+(\\.[0-9]+)?)";
	private static String REGEX_NUMERICVALUE_DISTRO_EXTENDED = "^([0-9\\.,E-]+)?(#?([A-Z]+)\\(([+-]?[0-9]+\\.?[0-9]*)(,([+-]?[0-9]+\\.?[0-9]*))?\\))?$";	
	
   /**
    * @param distributionName
    * @param firstParameter
    * @param secondParameter
    * @return
    */
	private static RandomVariate buildDistributionVariate(double detValue, String distributionName, String firstParameter, String secondParameter) {
		if (distributionName == null) {
			return RandomVariateFactory.getInstance("ConstantVariate", detValue);
		}
		if (distributionName.toUpperCase().contains("EXP")) {
			distributionName = "Exponential";
		}
		try {
		   	if (secondParameter != null) {
		   		return RandomVariateFactory.getInstance(WordUtils.capitalizeFully(distributionName) + DISTRIBUTION_NAME_SUFFIX, toDoubleValue(firstParameter), toDoubleValue(secondParameter));
		   	} else {
		   		return RandomVariateFactory.getInstance(WordUtils.capitalizeFully(distributionName) + DISTRIBUTION_NAME_SUFFIX, toDoubleValue(firstParameter));
		   	}
		} catch(NumberFormatException e) {
			return null;
		}
	}
	
	/**
	 * @param value
	 * @return
	 */
	public static ProbabilityDistribution splitProbabilityDistribution (String value) {
		ProbabilityDistribution result = null;		
		if (value != null) {
			String valueNormalized = value.toUpperCase().replace(" ", "");
			Pattern pattern = Pattern.compile(REGEX_NUMERICVALUE_DISTRO_EXTENDED);
			Matcher matcher = pattern.matcher(valueNormalized);
			if (matcher.find()) {			
				Double deterministicValue = (matcher.group(DETERMINISTIC_VALUE_POS) != null) ? toDoubleValue(matcher.group(DETERMINISTIC_VALUE_POS)) : 1.0;
				String distributionName = matcher.group(DISTRIBUTION_NAME_POS);
				String firstParameter = matcher.group(FIRST_PARAM_4_DISTRIBUTION_POS);
				String secondParameter = matcher.group(SECOND_PARAM_4_DISTRIBUTION_POS);
				RandomVariate rnd = buildDistributionVariate(deterministicValue, distributionName, firstParameter, secondParameter);
				result = (rnd == null) ? null : new ProbabilityDistribution(deterministicValue, rnd);
			}
		}
		return result;		
	}
	
	/**
	 * If it is a table in which the first column is age ranges, we assume that they are correctly ordered and that there is no intersection between the ranges.
	 * @param datatable Radios datatable transformed.
	 * @return Matrix of values.
	 */
	public static Object[][] rangeDatatableToMatrix (Datatable datatable, SecondOrderParamsRepository repository) {
		Object[][] result = null; 
		if (datatable != null) {
			List<RowType> rows = datatable.getContent().getRow();
			if (CollectionUtils.isNotEmpty(rows)) {
				result = new Object[rows.size()][3];
				for (int i = 0; i < rows.size(); i++) {
					for (ColumnType column : rows.get(i).getColumn()) {						
						if (ContentKind.RANGE.equals(column.getType())) {
							Pattern pattern = Pattern.compile(REGEX_RANGE);
							Matcher matcher = pattern.matcher(column.getValue());
							if (matcher.find()) {
								result[i][0] = toDoubleValue(matcher.group(1).replace(",", "."));
								result[i][1] = toDoubleValue(matcher.group(3).replace(",", "."));
							}
						} else if (ContentKind.NUMBER.equals(column.getType())) {
							String str = Constants.CONSTANT_EMPTY_STRING;
							result[i][2] = (!StringUtils.isEmpty(column.getValue()) ? new SecondOrderParam(repository, str, str, str, toDoubleValue(column.getValue().replace(",", "."))) : 0.0);
						} else if (ContentKind.NUMBER_DISTRO.equals(column.getType())) {
							ProbabilityDistribution probabilityDistribution = ValueParser.splitProbabilityDistribution(column.getValue());
							if (probabilityDistribution != null) {
								String str = Constants.CONSTANT_EMPTY_STRING;
								result[i][2] = (!StringUtils.isEmpty(column.getValue()) ?
										new SecondOrderParam(repository, str, str, str, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValue()) : 0.0);
							}
						}
					}
				}
			}
		}		
		return result;
	}
	
	/**
	 * @param strValue
	 * @return
	 */
	public static Double toDoubleValue (String strValue) throws NumberFormatException {
		if (strValue == null) {
			return null;
		}
		return Double.parseDouble(strValue);
	}
}