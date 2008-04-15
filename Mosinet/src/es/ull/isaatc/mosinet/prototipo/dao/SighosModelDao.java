package es.ull.isaatc.mosinet.prototipo.dao;

import java.util.List;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import es.ull.isaatc.mosinet.pattern.dao.IDao;
import es.ull.isaatc.mosinet.prototipo.model.SighosModel;

/**
 * This class implements the DAO for the SighosModel. 
 * @author Yurena García
 */

public class SighosModelDao extends HibernateDaoSupport implements IDao<SighosModel, Long> {

	/**
	 * Delete a SighosModel
	 * @param sighosModel
	 */
	public void delete(SighosModel sighosModel) {
		getHibernateTemplate().delete(sighosModel);

	}

	/**
	 * Return the SighosModel which the specified id.
	 * @param sighosModelId
	 * @return SighosModel
	 */
	public SighosModel load(Long id) {
		return (SighosModel)getHibernateTemplate().load(SighosModel.class, id);
	}

	/**
	 * Update a SighosModel
	 * @param sighosModel
	 */
	@Transactional(readOnly=false)
	public void syncronize(SighosModel sighosModel) throws DataRetrievalFailureException{
		setFlushMode(FlushMode.ALWAYS);
		getHibernateTemplate().saveOrUpdate(sighosModel);
		//getHibernateTemplate().merge(sighosModel);
	}

	/**
	 * Return all the SighosModels
	 * @return List<SighosModel>
	 */
	public List<SighosModel> getAll() {
		return getHibernateTemplate().loadAll(SighosModel.class);
		
	}
	
	/**
	 * Configure Hibernate FlushMode 
	 * @param mode
	 */
	private void setFlushMode(FlushMode mode) {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		session.setFlushMode(mode);
	}
	
	public List<SighosModel> getPage(int first, int count, String campo, boolean order) {
		long primero = first;
		long cuenta = count;
		DetachedCriteria dc = DetachedCriteria.forClass(SighosModel.class).add(Restrictions.between("id", primero, primero + cuenta));
		if (order)
			dc.addOrder(Order.asc(campo));
		else
			dc.addOrder(Order.desc(campo));
		List<SighosModel> result = getHibernateTemplate().findByCriteria(dc);
		return result;
	}
}
