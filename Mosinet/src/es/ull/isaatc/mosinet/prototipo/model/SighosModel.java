package es.ull.isaatc.mosinet.prototipo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import es.ull.isaatc.mosinet.pattern.model.impl.DefaultPersistenceImpl;

@Entity
public class SighosModel extends DefaultPersistenceImpl {

	private static final long serialVersionUID = -1704974983865707852L;
	
	private String modelName;
	private String xmlModel;
	private List<SighosExperiment> sighosExperiments = new ArrayList();
	
	
	public SighosModel() {
	}
	
	public SighosModel(String modelName) {
		this.modelName = modelName;
	}
	
	public SighosModel(String modelName, String xmlModel) {
		this.modelName = modelName;
		this.xmlModel = xmlModel;
	}
	
	// ---------- Getters ------------
	
	@Column
	public String getXmlModel() {
		return xmlModel;
	}
	
	@OneToMany
	@JoinColumn(name="experiments")
	public List<SighosExperiment> getSighosExperiments() {
		return sighosExperiments;
	}

	@Column
	public String getModelName() {
		return modelName;
	}
	
	// --------- Setters ---------
		
	public void setXmlModel(String xmlModel) {
		this.xmlModel = xmlModel;
	}
	

	public void setSighosExperiments(List<SighosExperiment> sighosExperiments) {
		this.sighosExperiments = sighosExperiments;
	}
	
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	
	/**
	 * Attach one experiment to the model
	 * @param sighosExperiment
	 */	
	public void addSighosExperiments(SighosExperiment sighosExperiment) {
		this.sighosExperiments.add(sighosExperiment);
	}
	



}
