package es.ull.isaatc.mosinet.prototipo.service.impl;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.List;

import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.springframework.transaction.annotation.Transactional;

import es.ull.isaatc.mosinet.prototipo.dao.SighosExperimentDao;
import es.ull.isaatc.mosinet.prototipo.dao.SighosModelDao;
import es.ull.isaatc.mosinet.prototipo.model.SighosExperiment;
import es.ull.isaatc.mosinet.prototipo.model.SighosModel;
import es.ull.isaatc.mosinet.prototipo.service.IServicePrototype;

/**
 * This class implements the services provided by the application
 * 
 * @author Yurena
 * 
 */
public class ServicePrototype implements IServicePrototype {
	
	SighosModelDao sighosModelDao;
	SighosExperimentDao sighosExperimentDao;
	
	/**
	 * Contructor
	 */
	public ServicePrototype() {
	}

	/**
	 * Delete
	 */
	public Boolean deleteSighosExperiment(Long id) {
		try {
			sighosExperimentDao.delete(sighosExperimentDao.load(id));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Boolean deleteSighosModel(Long id) {
		try {
			sighosModelDao.delete(sighosModelDao.load(id));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * getAll
	 */
	public List<SighosExperiment> getAllSighosExperiment() {
		try {
			return sighosExperimentDao.getAll();
		} catch (Exception e) {
			return null;
		}
	}

	public List<SighosModel> getAllSighosModel() {
		try {
			return sighosModelDao.getAll();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * get (by ID)
	 */
	public SighosExperiment getSighosExperiment(Long id) {
		try {
			return sighosExperimentDao.load(id);
		} catch (Exception e) {
			return null;
		}
	}

	public SighosModel getSighosModel(Long id) {
		try {
			return sighosModelDao.load(id);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * new (clone)
	 */
	public Boolean newSighosExperiment(SighosExperiment sighosExperiment) {
		try {
			sighosExperimentDao.syncronize(sighosExperiment);
			return true;
		}catch(Exception e) {
			return false;
		}
	}

	@Transactional(readOnly=false)
	public Boolean newSighosModel(SighosModel sighosModel) {
		try {
			System.out.println("entra en sp.newSiMo: " + sighosModel.getModelName());
			
			sighosModelDao.syncronize(sighosModel);
			System.out.println("sale try en sp.newSiMo");
			return true;
		}catch(Exception e) {
			System.out.println("esta en catch en sp.newSiMo");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * new (from string)
	 */
	@Transactional(readOnly = false)
	public Boolean newSighosExperiment(String experimentName, String xmlSighosExperiment) {
		SighosExperiment sighosExperiment= new SighosExperiment(experimentName, xmlSighosExperiment);
		try {
			sighosExperimentDao.syncronize(sighosExperiment);
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Boolean newSighosModel(String modelName, String xmlSighosModel) {
		SighosModel sighosModel= new SighosModel(modelName, xmlSighosModel);
		try {
			sighosModelDao.syncronize(sighosModel);
			return true;
		}catch(Exception e) {
			return false;
		}
	}

	/**
	 * update
	 */
	public Boolean updateSighosExperiment(SighosExperiment sighosExperiment) {
		try {
			sighosExperimentDao.syncronize(sighosExperiment);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Boolean updateSighosModel(SighosModel sighosModel) {
		try {
			sighosModelDao.syncronize(sighosModel);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * get/set DAO
	 * 
	 * @return
	 */
	public SighosModelDao getSighosModelDao() {
		return sighosModelDao;
	}

	public void setSighosModelDao(SighosModelDao sighosModelDao) {
		this.sighosModelDao = sighosModelDao;
	}

	public SighosExperimentDao getSighosExperimentDao() {
		return sighosExperimentDao;
	}

	public void setSighosExperimentDao(SighosExperimentDao sighosExperimentDao) {
		this.sighosExperimentDao = sighosExperimentDao;
	}

	@Override
	public String simulate(SighosModel sighosModel,
			SighosExperiment sighosExperiment) {

		try {
 String endpoint = "http://193.145.98.240:8080/SIGHOSWS/services/SIGHOSWS";
//			String endpoint = "http://localhost:8080/SIGHOSWS/services/SIGHOSWS";

			String methodName = "getExperiment";
			Service service = new Service();
			Call call = (Call) service.createCall();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName(methodName);

			String model = sighosModel.getXmlModel();
			String scenario = null;
			String experiment = sighosExperiment.getXmlExperiment();

			call.addParameter("model", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("scenario", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("experiment", XMLType.XSD_STRING, ParameterMode.IN);

			call.setReturnType(XMLType.XSD_STRING);
			String data = (String)call.invoke(new Object[] {model, scenario, experiment});
			return data;
			// ListenerChartFactory.getChart(data);

		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}
}
