//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.06.15 at 11:57:10 AM BST 
//


package es.ull.isaatc.simulation.editor.project.xml;

import javax.xml.bind.annotation.XmlRegistry;

import es.ull.isaatc.simulation.editor.project.xml.ObjectFactory;
import es.ull.isaatc.simulation.editor.project.xml.Plugin;
import es.ull.isaatc.simulation.editor.project.xml.Project;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.ull.isaatc.simulation.editor.project.xml package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.ull.isaatc.simulation.editor.project.xml
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Project }
     * 
     */
    public Project createProject() {
        return new Project();
    }

    /**
     * Create an instance of {@link Plugin }
     * 
     */
    public Plugin createPlugin() {
        return new Plugin();
    }

}