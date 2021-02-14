package es.ull.iis.simulation.hta.radios.transforms;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

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
		return (Datatable) getUnmarshaller().unmarshal(new StringReader(datatable));
	}

}
