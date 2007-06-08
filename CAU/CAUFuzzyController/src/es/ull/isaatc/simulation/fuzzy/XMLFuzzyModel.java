/**
 * 
 */
package es.ull.isaatc.simulation.fuzzy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLClassLoader;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import es.ull.isaatc.simulation.fuzzy.xml.ProgrammedTasks;
import es.ull.isaatc.simulation.xml.XMLWrapper;

/**
 * @author Roberto Muñoz
 */
public class XMLFuzzyModel extends XMLWrapper {

	private ProgrammedTasks xmlProgTasks;

	/**
	 * @param xmlModelFileName
	 * @param xmlScenarioFileName
	 * @param xmlExperimentFileName
	 * @throws FileNotFoundException 
	 */
	public XMLFuzzyModel(String xmlModelFileName, String xmlScenarioFileName,
			String xmlExperimentFileName, String xmlProgramedTaskFileName) throws FileNotFoundException {
		super(xmlModelFileName, xmlScenarioFileName, xmlExperimentFileName);
		xmlProgTasks = unMarshallProgTask(xmlProgramedTaskFileName);
	}

	/**
	 * @return the xmlProgTasks
	 */
	public ProgrammedTasks getXmlProgTasks() {
		return xmlProgTasks;
	}

	/**
	 * Unmarshall the XML file with the programmed tasks description.
	 * @param fileName programmed task XML file name
	 * @return the programmed tasks loaded
	 */
	public static ProgrammedTasks unMarshallProgTask(String fileName) {
		ProgrammedTasks progTask = null;
		try {
			JAXBContext jc = JAXBContext
					.newInstance("es.ull.isaatc.simulation.fuzzy.xml");
			Unmarshaller u = jc.createUnmarshaller();
			SchemaFactory schemaFactory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//			u.setSchema(schemaFactory.newSchema(new File("programmedtask.xsd")));
			u.setSchema(schemaFactory.newSchema(URLClassLoader.getSystemResource("programmedtask.xsd")));
			progTask = (ProgrammedTasks) u.unmarshal(new FileInputStream(
					fileName));
		} catch (JAXBException je) {
			System.out.println("ERROR : Error found in the programmed task XML file");
			je.printStackTrace();
			System.exit(-1);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(-1);
		} catch (SAXException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return progTask;
	}
}