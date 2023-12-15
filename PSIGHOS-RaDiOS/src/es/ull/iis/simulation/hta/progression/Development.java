/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.HTAModelComponent;
import es.ull.iis.simulation.hta.PrettyPrintable;

/**
 * A development for a disease. A disease may have different developments (natural vs modified, severe vs mild, ...).
 * @author Iván Castilla Rodríguez
 *
 */
public class Development extends HTAModelComponent implements PrettyPrintable {
	private final Disease disease;
	/**
	 * 
	 */
	public Development(HTAModel model, String name, String description, Disease disease) {
		super(model, name, description);
		this.disease = disease;
		if (!model.register(this))
			throw new IllegalArgumentException("Development " + name + " already registered");
		disease.addDevelopment(this);
	}

	/**
	 * @return the disease
	 */
	public Disease getDisease() {
		return disease;
	}

	@Override
	public String prettyPrint(String linePrefix) {
		final StringBuilder str = new StringBuilder(linePrefix).append("Development: ").append(name()).append(System.lineSeparator());
		if (!"".equals(getDescription()))
			str.append(linePrefix + "\t").append(getDescription()).append(System.lineSeparator());
		return str.toString();
	}
}
