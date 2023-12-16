/**
 * 
 */
package es.ull.iis.simulation.hta.inforeceiver;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.info.PatientInfo;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

/**
 * A listener to capture the time until a chronic manifestation onsets. Time can be 0 (if the patient starts with the manifestation),
 * Long.MAX_VALUE if the simulation finishes and the patient never developed the manifestation, or any positive value among both extremes
 * (it is expected to be always inferior to the maximum age of the patient)
 * @author Iván Castilla
 *
 */
public class IndividualTime2ManifestationView extends Listener implements StructuredOutputListener {
	/** Interventions assessed */
	private final Intervention[] interventions;
	/** Number of patients simulated */
	private final int nPatients;
	/** Available chronic manifestations in the simulation */
	private final DiseaseProgression[] availableChronicManifestations;
	/** Available acute manifestations in the simulation */
	private final DiseaseProgression[] availableAcuteManifestations;
	/** Time to events (in years) for each patient, intervention and manifestation. NaN in case the patient never develops a manifestation */
	private final double[][][]innerTimeTo;
	/** Number of acute events for each patient, intervention and manifestation. */
	private final int[][][]innerNEvents;

	/**
	 * 
	 * @param model Main repository for the simulations
	 */
	public IndividualTime2ManifestationView(HTAModel model) {
		super("Viewer of time to event per patient");
		this.interventions = model.getRegisteredInterventions();
		this.nPatients = model.getExperiment().getNPatients();
		this.availableChronicManifestations = model.getRegisteredDiseaseProgressions(DiseaseProgression.Type.CHRONIC_MANIFESTATION);
		this.availableAcuteManifestations = model.getRegisteredDiseaseProgressions(DiseaseProgression.Type.ACUTE_MANIFESTATION);
		this.innerTimeTo = new double[nPatients][interventions.length][availableChronicManifestations.length];
		this.innerNEvents = new int[nPatients][interventions.length][availableAcuteManifestations.length];
		addGenerated(PatientInfo.class);
		addEntrance(PatientInfo.class);
	}
	
	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder("PAT\t");
		for (Intervention inter : interventions) {
			for (DiseaseProgression comp : availableChronicManifestations) {
				str.append(comp.name()).append("_").append(inter.name()).append(SEP);
			}			
		}
		str.append(System.lineSeparator());
		for (int i = 0; i < nPatients; i++) {
			str.append(i).append(SEP);
			for (int j = 0; j < interventions.length; j++) {
				for (int k = 0; k < availableChronicManifestations.length; k++) {
					str.append(innerTimeTo[i][j][k]).append(SEP);
				}
			}
			str.append(System.lineSeparator());
		}
		return str.toString();
	}
	
	/**
	 * Returns the time to events (in years) for each patient, intervention and manifestation. NaN in case the patient never develops a manifestation. 
	 * Both manifestations and interventions are ordered as in the repository
	 * @return the time to events (in years) for each patient, intervention and manifestation. NaN in case the patient never develops a manifestation
	 */
	public double[][][]getTimes() {
		return innerTimeTo;
	}
	
	/**
	 * Returns the number of acute events for each patient, intervention and manifestation. 
	 * Both manifestations and interventions are ordered as in the repository
	 * @return the number of acute events for each patient, intervention and manifestation. 
	 */
	public int[][][]getNEvents() {
		return innerNEvents;
	}
	
	@Override
	public void infoEmited(SimulationInfo info) {
		final PatientInfo pInfo = (PatientInfo) info;
		if (pInfo.getType() == PatientInfo.Type.DEATH) {
			final Patient pat = pInfo.getPatient();
			for (int i = 0; i < availableChronicManifestations.length; i++) {
				final long time = pat.getTimeToDiseaseProgression(availableChronicManifestations[i]);
				innerTimeTo[pat.getIdentifier()][pat.getnIntervention()][i] = (time == Long.MAX_VALUE) ? Double.NaN : SecondOrderParamsRepository.simulationTimeToYears(time);
			}
			for (int i = 0; i < availableAcuteManifestations.length; i++) {
				innerNEvents[pat.getIdentifier()][pat.getnIntervention()][i] = pat.getNDiseaseProgressions(availableAcuteManifestations[i]);
			}
		}
	}
}
