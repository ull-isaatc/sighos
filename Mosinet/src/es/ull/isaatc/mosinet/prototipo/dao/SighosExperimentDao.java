package es.ull.isaatc.mosinet.prototipo.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import es.ull.isaatc.mosinet.pattern.dao.IDao;
import es.ull.isaatc.mosinet.prototipo.model.SighosExperiment;

public class SighosExperimentDao extends HibernateDaoSupport implements IDao<SighosExperiment, Long> {

	/**
	 * Delete a SighosExperiment
	 * @param sighosExperiment
	 */
	public void delete(SighosExperiment sighosExperiment) {
		getHibernateTemplate().delete(sighosExperiment);

	}
	
	/**
	 * Return the SighosExperiment which the specified id.
	 * @param sighosExperimentId
	 * @return SighosExperiment
	 */
	public SighosExperiment load(Long id) {
		return (SighosExperiment)getHibernateTemplate().load(SighosExperiment.class, id);
	}

	/**
	 * Update a SighosExperiment
	 * @param sighosExperiment
	 */
	public void syncronize(SighosExperiment sighosExperiment) {
		getHibernateTemplate().saveOrUpdate(sighosExperiment);
	}

	/**
	 * Return all the SighosExperiments
	 * @return List<SighosExperiment>
	 */
	public List<SighosExperiment> getAll() {
		return getHibernateTemplate().loadAll(SighosExperiment.class);
		
	}
	
	public List<SighosExperiment> getPage(int first, int count, String campo, boolean order) {
		long primero = first;
		long cuenta = count;
		DetachedCriteria dc = DetachedCriteria.forClass(SighosExperiment.class).add(Restrictions.between("id", primero, primero + cuenta));
		if (order)
			dc.addOrder(Order.asc(campo));
		else
			dc.addOrder(Order.desc(campo));
		List<SighosExperiment> result = getHibernateTemplate().findByCriteria(dc);
		return result;
	}
}
