package es.ull.isaatc.mosinet.prototipo.hibernate.generator;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

public class PruebaGenerator implements IdentifierGenerator {

	public Serializable generate(SessionImplementor arg0, Object arg1)
			throws HibernateException {
		// TODO Auto-generated method stub
		
		return 23L;
	}

}
