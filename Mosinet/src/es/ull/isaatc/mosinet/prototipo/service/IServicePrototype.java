package es.ull.isaatc.mosinet.prototipo.service;

import java.util.List;

import es.ull.isaatc.mosinet.prototipo.model.SighosExperiment;
import es.ull.isaatc.mosinet.prototipo.model.SighosModel;

/**
 * Provided services interface
 * @author yuyu
 *
 */
public interface IServicePrototype {

	// for the SighosModel
	public SighosModel getSighosModel(Long id);
	public Boolean newSighosModel(SighosModel sighosModel);
	public Boolean newSighosModel(String modelName, String xmlSighosModel);
	public Boolean updateSighosModel(SighosModel sighosModel);
	public Boolean deleteSighosModel(Long id);
	
	// for the SighosExperiment
	public SighosExperiment getSighosExperiment(Long id);
	public Boolean newSighosExperiment(SighosExperiment sighosExperiment);
	public Boolean newSighosExperiment(String experimentName, String xmlSighosExperiment);
	public Boolean updateSighosExperiment(SighosExperiment sighosExperiment);
	public Boolean deleteSighosExperiment(Long id);

	public List<SighosModel> getAllSighosModel();
	public List<SighosExperiment> getAllSighosExperiment();
	
	public String simulate(SighosModel sighosModel, SighosExperiment sighosExperiment);
}
