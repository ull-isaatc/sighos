/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import java.util.Collection;

import com.fathzer.soft.javaluator.StaticVariableSet;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.progression.Manifestation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class JavaluatorPatient extends StaticVariableSet<Double> {

	/**
	 * 
	 */
	public JavaluatorPatient(Patient pat) {
		set("AGE", pat.getAge());
		set("SEX", (double)pat.getSex());
		set("DIAGNOSED", pat.isDiagnosed() ? 1.0 : 0.0);
		for (Manifestation manif : pat.getState()) {
			set(manif.name(), (double)pat.getTimeToDiseaseProgression(manif));
		}
		final Collection<String> propNames = pat.getAttributeNames();
		for (String propName : propNames)
			set(propName, (double)pat.getAttributeValue(propName));
	}

}
