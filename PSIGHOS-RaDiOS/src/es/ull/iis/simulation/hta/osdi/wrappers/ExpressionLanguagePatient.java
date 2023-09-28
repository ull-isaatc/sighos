/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.jexl3.MapContext;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.progression.Manifestation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ExpressionLanguagePatient extends MapContext {

	/**
	 * 
	 */
	public ExpressionLanguagePatient(Patient pat) {
		set("AGE", pat.getAge());
		set("SEX", pat.getSex());
		set("DISEASE", pat.getDisease().name());
		set("DIAGNOSED", pat.isDiagnosed());
		set("INTERVENTION", pat.getIntervention().name());
		for (Manifestation manif : pat.getState()) {
			set(manif.name(), pat.getTimeToManifestation(manif));
		}
		final Collection<String> propNames = pat.getAttributeNames();
		for (String propName : propNames)
			set(propName, pat.getAttributeValue(propName));
	}

	/**
	 * @param vars
	 */
	public ExpressionLanguagePatient(Map<String, Object> vars) {
		super(vars);
	}

}
