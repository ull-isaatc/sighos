/**
 * 
 */
package es.ull.iis.simulation.hta.inforeceiver;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.info.PatientInfo;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Manifestation;
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
	private final Manifestation[] availableChronicManifestations;
	/** Time to events (in years) for each patient, intervention and manifestation. NaN in case the patient never develops a manifestation */
	private final double[][][]innerTimeTo;

	/**
	 * 
	 * @param secParams Main repository for the simulations
	 */
	public IndividualTime2ManifestationView(SecondOrderParamsRepository secParams) {
		super("Viewer of time to event per patient");
		this.interventions = secParams.getRegisteredInterventions();
		this.nPatients = secParams.getnPatients();
		this.availableChronicManifestations = secParams.getRegisteredManifestations(Manifestation.Type.CHRONIC);
		this.innerTimeTo = new double[nPatients][interventions.length][availableChronicManifestations.length];
		addGenerated(PatientInfo.class);
		addEntrance(PatientInfo.class);
	}
	
	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder("PAT\t");
		for (Intervention inter : interventions) {
			for (Manifestation comp : availableChronicManifestations) {
				str.append(comp.name()).append("_").append(inter.name()).append("\t");
			}			
		}
		str.append(System.lineSeparator());
		for (int i = 0; i < nPatients; i++) {
			str.append(i).append("\t");
			for (int j = 0; j < interventions.length; j++) {
				for (int k = 0; k < availableChronicManifestations.length; k++) {
					str.append(innerTimeTo[i][j][k]).append("\t");
				}
			}
			str.append(System.lineSeparator());
		}
		return str.toString();
	}
	
	@Override
	public void infoEmited(SimulationInfo info) {
		final PatientInfo pInfo = (PatientInfo) info;
		if (pInfo.getType() == PatientInfo.Type.DEATH) {
			final Patient pat = pInfo.getPatient();
			for (int i = 0; i < availableChronicManifestations.length; i++) {
				final long time = pat.getTimeToManifestation(availableChronicManifestations[i]);
				innerTimeTo[pat.getIdentifier()][pat.getnIntervention()][i] = (time == Long.MAX_VALUE) ? Double.NaN : ((double)time) /BasicConfigParams.YEAR_CONVERSION;
			}
		}
	}
}
