package es.ull.isaatc.mosinet.pattern.model;

import java.io.Serializable;
import java.util.Date;

public interface IPersistence extends Serializable, Comparable<IPersistence> {
	
	/**
	 * Gets the element model identifier.
	 * 
	 * @return id
	 */
	Long getId();
	
	/**
	 * Update the element model identifier.
	 * Usually auto update by the ORM. 
	 * 
	 * @param identifier
	 */
	void setId(Long identifier);
	
	/**
	 * Gets the element version to check concurrency
	 * 
	 * @return version
	 */
	Long getVersion();
	
	/**
	 * Update the element model version.
	 * Usually auto update by the ORM.
	 * 
	 * @param version
	 */
	void setVersion(Long version);
	
	/**
	 * Get the last date and hour when the element was modified.
	 * 
	 * @return lastUpdate
	 */
	Date getLastUpdate();
	
	
	/**
	 * Update the element model last update.
	 * Usually auto update by the ORM.
	 * 
	 * @param lastUpdate
	 */
	void setLastUpdate(Date lastUpdate);

}

