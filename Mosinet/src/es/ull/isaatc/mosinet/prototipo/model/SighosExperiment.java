package es.ull.isaatc.mosinet.prototipo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.FetchMode;

import es.ull.isaatc.mosinet.pattern.model.impl.DefaultPersistenceImpl;

@Entity
public class SighosExperiment extends DefaultPersistenceImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8521357144640449696L;
	
	private String experimentName;
	private String xmlExperiment;
	private SighosModel sighosModel;
	
	public SighosExperiment() {
		
	}
	
	public SighosExperiment(String experimentName) {
		this.experimentName = experimentName;
	}
	
	public SighosExperiment(String experimentName, String xmlExperiment) {
		this.experimentName = experimentName;
		this.xmlExperiment = xmlExperiment;
	}

	@Column
	public String getXmlExperiment() {
		return xmlExperiment;
	}
	public void setXmlExperiment(String xmlExperiment) {
		this.xmlExperiment = xmlExperiment;
	}

	@ManyToOne(optional=true)
	@JoinColumn(nullable=true, name="experiments")
	public SighosModel getSighosModel() {
		return sighosModel;
	}

	public void setSighosModel(SighosModel sighosModel) {
		this.sighosModel = sighosModel;
	}

	@Column(nullable = true)
	public String getExperimentName() {
		return experimentName;
	}

	public void setExperimentName(String experimentName) {
		this.experimentName = experimentName;
	}
}
