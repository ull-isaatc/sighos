package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;

public class ConstantNatureParameter extends Parameter {
	private final double value;

    public ConstantNatureParameter(final SecondOrderParamsRepository secParams, String name, ParameterDescription desc, double value) {
        super(secParams, name, desc);
        this.value = value;
    }

	@Override
	public double getValue(Patient pat) {
		return value;
	}
    
}
