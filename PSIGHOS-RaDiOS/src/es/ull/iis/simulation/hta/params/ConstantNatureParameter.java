package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;

public class ConstantNatureParameter extends Parameter {
	private final double value;

    public ConstantNatureParameter(String name, String description, String source, int year, ParameterType type, double value) {
        super(name, description, source, year, type);
        this.value = value;
    }

    public ConstantNatureParameter(String name, String description, String source, ParameterType type, double value) {
        super(name, description, source, type);
        this.value = value;
    }

	@Override
	public double getValue(Patient pat) {
		return value;
	}
    
}
