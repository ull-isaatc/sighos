/**
 * 
 */
package es.ull.isaatc.simulation.xml.util;

import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.net.URLClassLoader;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import es.ull.isaatc.simulation.xml.Experiment;
import es.ull.isaatc.simulation.xml.Model;
import es.ull.isaatc.simulation.xml.SighosValidationEventHandler;

/**
 * @author Roberto Muñoz
 * 
 */
public class XMLMarshallUtils {

	/**
	 * Stores in memory the content of an XML file with a model description
	 * @param modelReader the file name of the model
	 * @return the content of the file in memory
	 */
	public static Model unMarshallModel(Reader modelReader) {
		return (Model)unmarshallObject(modelReader, "es.ull.isaatc.simulation.xml", "model.xsd");
	}
	
	/**
	 * Stores in memory the content of an XML file with an experiment
	 * description
	 * @param experimentReader the file name of the experiemnt
	 * @return the content of the file in memory
	 */
	public static Experiment unMarshallExperiment(Reader experimentReader) {
		return (Experiment)unmarshallObject(experimentReader, "es.ull.isaatc.simulation.xml", "experiment.xsd");
	}

	/**
	 * Unmarshalls an object.
	 * @param reader the input stream that contains the XML
	 * @param contextPath
	 * @param systemResource
	 * @return the object obtained from the XML
	 */
	public static Object unmarshallObject(Reader reader, String contextPath, String systemResource) {
		Object experiment = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(contextPath);
			Unmarshaller u = jc.createUnmarshaller();
			u.setEventHandler(new SighosValidationEventHandler());
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			u.setSchema(schemaFactory.newSchema(URLClassLoader.getSystemResource(systemResource)));
			experiment = u.unmarshal(reader);
		} catch (JAXBException je) {
			System.out.println("ERROR : Error found in one of the XML files");
			je.printStackTrace();
			System.exit(-1);
		} catch (SAXException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return experiment;
	}
	
	/**
	 * Marshalls an object.
	 * @param object the object in memory
	 * @param contextPath
	 * @return the string with the XML serialization of the object
	 */
	public static String marshallObject(Object object, String contextPath) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			JAXBContext jc = JAXBContext.newInstance(contextPath);
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(object, output);
		} catch (JAXBException je) {
			je.printStackTrace();
			System.exit(-1);
		}
		return output.toString();
	}
}
