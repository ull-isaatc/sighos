package es.ull.isaatc.mosinet.pattern.model.impl;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang.builder.HashCodeBuilder;

import es.ull.isaatc.mosinet.pattern.model.IPersistence;

/**
 * Realiza una implementacion por defecto de las propiedades basicas de un
 * elemento del modelo persistente. Trabaja con anotaciones JPA compatibles 
 * con hibernate.
 * 
 * Tambien sobrescribe el equals y el hashcode para hibernate.
 */
@MappedSuperclass
@SuppressWarnings("serial")
public class DefaultPersistenceImpl implements IPersistence {

	protected Long id;
	protected Long version;
	protected Date lastUpdate;

	// ----------- getters ------------

	/**
	 * @see es.ull.isaatc.mosinet.model.Ipersistence#getId()
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	/**
	 * @see es.ull.isaatc.mosinet.model.Ipersistence#getLastUpdate()
	 */
	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * @see es.ull.isaatc.mosinet.model.Ipersistence#getVersion()
	 */
	@Version
	public Long getVersion() {
		return version;
	}

	// ----------- setters ------------

	/**
	 * @see es.ull.isaatc.mosinet.model.Ipersistence#setId(java.lang.Long)
	 */
	public void setId(final Long id) {
		this.id = id;
	}

	/**
	 * @see es.ull.isaatc.mosinet.model.Ipersistence#setLastUpdate(java.util.Date)
	 */
	public void setLastUpdate(final Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * @see es.ull.isaatc.mosinet.model.Ipersistence#setVersion(java.lang.Long)
	 */
	public void setVersion(final Long version) {
		this.version = version;
	}

	// ------ comparable interface -------

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(final IPersistence o) {
		return (o == null) ? 1 : new NullComparator().compare(id,
				((IPersistence) o).getId());
	}

	// --------- object methods ----------

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof IPersistence)) {
			return false;
		}
		return (compareTo((IPersistence) o) == 0);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
