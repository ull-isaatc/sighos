//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.06.15 at 12:02:13 PM BST 
//


package es.ull.isaatc.simulation.editor.project.model.validation;

import javax.xml.bind.annotation.AccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import es.ull.isaatc.simulation.editor.project.model.validation.Component;
import es.ull.isaatc.simulation.xml.Cycle;
import es.ull.isaatc.simulation.xml.RandomNumber;

/**
 * <p>Java class for component element declaration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="component">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;choice>
 *           &lt;element name="cycle" type="{}Cycle"/>
 *           &lt;element name="duration" type="{}RandomNumber"/>
 *         &lt;/choice>
 *       &lt;/restriction>
 *     &lt;/complexContent>
 *   &lt;/complexType>
 * &lt;/element>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(AccessType.FIELD)
@XmlType(name = "", propOrder = {
    "cycle",
    "duration"
})
@XmlRootElement(name = "component")
public class Component {

    protected Cycle cycle;
    protected RandomNumber duration;

    /**
     * Gets the value of the cycle property.
     * 
     * @return
     *     possible object is
     *     {@link Cycle }
     *     
     */
    public Cycle getCycle() {
        return cycle;
    }

    /**
     * Sets the value of the cycle property.
     * 
     * @param value
     *     allowed object is
     *     {@link Cycle }
     *     
     */
    public void setCycle(Cycle value) {
        this.cycle = value;
    }

    /**
     * Gets the value of the duration property.
     * 
     * @return
     *     possible object is
     *     {@link RandomNumber }
     *     
     */
    public RandomNumber getDuration() {
        return duration;
    }

    /**
     * Sets the value of the duration property.
     * 
     * @param value
     *     allowed object is
     *     {@link RandomNumber }
     *     
     */
    public void setDuration(RandomNumber value) {
        this.duration = value;
    }

}