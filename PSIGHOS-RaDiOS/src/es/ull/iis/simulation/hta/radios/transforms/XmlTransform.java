package es.ull.iis.simulation.hta.radios.transforms;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import es.ull.iis.ontology.radios.xml.datatables.Datatable;

public class XmlTransform {
	private static String DATATABLE_PACKAGE = "es.ull.iis.ontology.radios.xml.datatables";
	private static Unmarshaller unmarshaller; 

	private static Unmarshaller getUnmarshaller () throws JAXBException {
		if (unmarshaller == null) {
			JAXBContext jc = JAXBContext.newInstance(DATATABLE_PACKAGE);
			unmarshaller = jc.createUnmarshaller();
		}
		return unmarshaller;
	}
	
	public static Datatable getDataTable(String datatable) throws JAXBException {
		InputStream is = new ByteArrayInputStream(datatable.getBytes());
		JAXBElement<Datatable> jaxbObject = getUnmarshaller().unmarshal(new StreamSource(is), Datatable.class);
		return jaxbObject.getValue();
	}

}
