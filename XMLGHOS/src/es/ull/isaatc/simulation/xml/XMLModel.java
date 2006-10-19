package es.ull.isaatc.simulation.xml;

/**
 * XMLModel.java
 * 
 * Created on 13 February 2006
 */

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import es.ull.isaatc.simulation.xml.util.MarshallModelUtil;
import es.ull.isaatc.util.Output;
import es.ull.isaatc.util.Output.DebugLevel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * Extends the bind.Model class. Load a model and a scenario stored in a XML
 * file and merge the content to create a model stored in memory.
 * 
 * @author Roberto Muñoz
 */
public class XMLModel {

    /** Model loaded from a XML file */
    Model model;

    /** Experiement data */
    Experiment experiment;

    /**
     * Load a model and a scenario stored in a XML file and merge the
     * content to create a model stored in memory.
     * 
     * @param xmlModelFileName
     * @param xmlExperimentFileName
     */
    public XMLModel(String xmlModelFileName, String xmlExperimentFileName) {

	this.model = MarshallModelUtil.unMarshallModel(xmlModelFileName);
	this.experiment = MarshallModelUtil
		.unMarshallExperiment(xmlExperimentFileName);
    }

    /**
     * Returns the Output debug mode
     * 
     * @return
     */
    public DebugLevel getDebugMode() {

	String debugMode = experiment.getDebugMode();
	if (debugMode.equals("NO"))
	    return Output.DebugLevel.NODEBUG;
	if (debugMode.equals("DEBUG"))
	    return Output.DebugLevel.DEBUG;
	if (debugMode.equals("XDEBUG"))
	    return Output.DebugLevel.XDEBUG;
	return Output.DebugLevel.NODEBUG;
    }

    /**
     * Find a resource by its description
     * 
     * @param description
     *                Resource description
     * @return resource or null if the resource isn't found
     */
    protected Resource findResource(String description) {

	Iterator<Resource> resIt = model.getResource().iterator();
	while (resIt.hasNext()) {
	    Resource res = resIt.next();
	    if (res.description.equals(description))
		return res;
	}
	return null;
    }

    /**
     * @return XML model stored in memory
     */
    public Model getModel() {

	return model;
    }

    /**
     * @return XML experiement stored in memory
     */
    public Experiment getExperiment() {

	return experiment;
    }
}
