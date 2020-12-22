package es.ull.iis.simulation.hta.radios;

import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.TimeToEventParam;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.progression.Transition;

public class RadiosTransition extends Transition {

	public RadiosTransition(SecondOrderParamsRepository repository, Manifestation srcManifestation, Manifestation destManifestation, boolean replacesPrevious) {
		super(repository, srcManifestation, destManifestation, replacesPrevious);
	}

	@Override
	protected TimeToEventParam getTimeToEventParam(int id) {
		return null;
	}

}
