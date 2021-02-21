package es.ull.iis.simulation.hta.radios;

import java.util.ArrayList;
import java.util.List;

import es.ull.iis.ontology.radios.json.schema4simulation.Guideline;

public class NewbornGuideline extends Guideline {
	private static final long serialVersionUID = 1841174390950291237L;

	public static List<Guideline> getInstance() {
		List<Guideline> result = new ArrayList<>();
		Guideline newbornGuideline = new Guideline();
		newbornGuideline.setName("NewbornGuideline");
		newbornGuideline.setRange("0m-1m");
		newbornGuideline.setFrequency("1m");
		result.add(newbornGuideline);
		return result;
	}
}
