/**
 * 
 */
package es.ull.isaatc.simulation.xml.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
public class MarshallModelUtil {

    /**
     * Stores in memory the content of an XML file with a model description
     * 
     * @param fileName
     *                the file name of the model
     * @return the content of the file in memory
     */
    public static Model unMarshallModel(String fileName) {
	Model model = null;
	try {
	    JAXBContext jc = JAXBContext
		    .newInstance("es.ull.isaatc.simulation.xml");
	    Unmarshaller u = jc.createUnmarshaller();
	    u.setEventHandler(new SighosValidationEventHandler());
	    SchemaFactory schemaFactory = SchemaFactory
		    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	    u.setSchema(schemaFactory.newSchema(new File("model.xsd")));
	    model = (Model) u.unmarshal(new FileInputStream(fileName));
	} catch (JAXBException je) {
	    System.out.println("ERROR : Error found in one of the XML files");
	    System.exit(-1);
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	    System.exit(-1);
	} catch (SAXException e) {
	    e.printStackTrace();
	    System.exit(-1);
	}
	return model;
    }

    /**
     * Stores in memory the content of an XML file with an experiment
     * description
     * 
     * @param fileName
     *                the file name of the experiemnt
     * @return the content of the file in memory
     */
    public static Experiment unMarshallExperiment(String fileName) {
	Experiment experiment = null;
	try {
	    JAXBContext jc = JAXBContext
		    .newInstance("es.ull.isaatc.simulation.xml");
	    Unmarshaller u = jc.createUnmarshaller();
	    u.setEventHandler(new SighosValidationEventHandler());
	    SchemaFactory schemaFactory = SchemaFactory
		    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	    u.setSchema(schemaFactory.newSchema(new File("experiment.xsd")));
	    experiment = (Experiment) u
		    .unmarshal(new FileInputStream(fileName));
	} catch (JAXBException je) {
	    System.out.println("ERROR : Error found in one of the XML files");
	    System.exit(-1);
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	    System.exit(-1);
	} catch (SAXException e) {
	    e.printStackTrace();
	    System.exit(-1);
	}
	return experiment;
    }

    /**
     * Stores in an XML file the definition of a model
     * 
     * @param model
     *                the model in memory
     * @param fileName
     *                model file name
     */
    public static void marshallModel(Model model, String fileName) {
	try {
	    JAXBContext jc = JAXBContext
		    .newInstance("es.ull.isaatc.simulation.xml");
	    Marshaller m = jc.createMarshaller();
	    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	    m.marshal(model, new FileOutputStream(fileName));
	} catch (JAXBException je) {
	    je.printStackTrace();
	    System.exit(-1);
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	    System.exit(-1);
	}
    }

    /**
     * Stores in an XML file the definition of an experiment
     * 
     * @param experiment
     *                the experiment in memory
     * @param fileName
     *                model file name
     */
    public static void marshallExperiment(Experiment experiment, String fileName) {
	try {
	    JAXBContext jc = JAXBContext
		    .newInstance("es.ull.isaatc.simulation.xml");
	    Marshaller m = jc.createMarshaller();
	    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	    m.marshal(experiment, new FileOutputStream(fileName));
	} catch (JAXBException je) {
	    je.printStackTrace();
	    System.exit(-1);
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	    System.exit(-1);
	}
    }
}
