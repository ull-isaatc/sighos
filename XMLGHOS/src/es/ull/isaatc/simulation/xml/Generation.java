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
 * <p>Java class for Generation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Generation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="nElem" type="{}FunctionChoice"/>
 *         &lt;element name="probTree" type="{}ProbTree"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Generation", propOrder = {
    "nElem",
    "probTree"
})
public class Generation {

    @XmlElement(required = true)
    protected FunctionChoice nElem;
    @XmlElement(required = true)
    protected ProbTree probTree;

    /**
     * Gets the value of the nElem property.
     * 
     * @return
     *     possible object is
     *     {@link FunctionChoice }
     *     
     */
    public FunctionChoice getNElem() {
        return nElem;
    }

    /**
     * Sets the value of the nElem property.
     * 
     * @param value
     *     allowed object is
     *     {@link FunctionChoice }
     *     
     */
    public void setNElem(FunctionChoice value) {
        this.nElem = value;
    }

    /**
     * Gets the value of the probTree property.
     * 
     * @return
     *     possible object is
     *     {@link ProbTree }
     *     
     */
    public ProbTree getProbTree() {
        return probTree;
    }

    /**
     * Sets the value of the probTree property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProbTree }
     *     
     */
    public void setProbTree(ProbTree value) {
        this.probTree = value;
    }

}
