//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b01-EA2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.05.06 at 06:25:30 PM BST 
//


package es.ull.isaatc.simulation.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Generator complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Generator">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cycle" type="{}Cycle"/>
 *         &lt;element name="toGenerate" type="{}Generation"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Generator", propOrder = {
    "cycle",
    "toGenerate"
})
public class Generator {

    @XmlElement(required = true)
    protected Cycle cycle;
    @XmlElement(required = true)
    protected Generation toGenerate;

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
     * Gets the value of the toGenerate property.
     * 
     * @return
     *     possible object is
     *     {@link Generation }
     *     
     */
    public Generation getToGenerate() {
        return toGenerate;
    }

    /**
     * Sets the value of the toGenerate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Generation }
     *     
     */
    public void setToGenerate(Generation value) {
        this.toGenerate = value;
    }

}
