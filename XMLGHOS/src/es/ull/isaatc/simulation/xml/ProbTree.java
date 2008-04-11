//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b24-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.07.26 at 12:20:59 PM BST 
//


package es.ull.isaatc.simulation.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProbTree complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProbTree">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="prob" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="subTree" type="{}ProbTree" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="elementType" type="{}ComponentRef" minOccurs="0"/>
 *         &lt;element name="metaFlow" type="{}ComponentRef" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProbTree", propOrder = {
    "prob",
    "subTree",
    "elementType",
    "metaFlow"
})
public class ProbTree {

    @XmlElement(defaultValue = "1.0")
    protected double prob;
    protected List<ProbTree> subTree;
    protected ComponentRef elementType;
    protected ComponentRef metaFlow;

    /**
     * Gets the value of the prob property.
     * 
     */
    public double getProb() {
        return prob;
    }

    /**
     * Sets the value of the prob property.
     * 
     */
    public void setProb(double value) {
        this.prob = value;
    }

    /**
     * Gets the value of the subTree property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subTree property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubTree().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProbTree }
     * 
     * 
     */
    public List<ProbTree> getSubTree() {
        if (subTree == null) {
            subTree = new ArrayList<ProbTree>();
        }
        return this.subTree;
    }

    /**
     * Gets the value of the elementType property.
     * 
     * @return
     *     possible object is
     *     {@link ComponentRef }
     *     
     */
    public ComponentRef getElementType() {
        return elementType;
    }

    /**
     * Sets the value of the elementType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ComponentRef }
     *     
     */
    public void setElementType(ComponentRef value) {
        this.elementType = value;
    }

    /**
     * Gets the value of the metaFlow property.
     * 
     * @return
     *     possible object is
     *     {@link ComponentRef }
     *     
     */
    public ComponentRef getMetaFlow() {
        return metaFlow;
    }

    /**
     * Sets the value of the metaFlow property.
     * 
     * @param value
     *     allowed object is
     *     {@link ComponentRef }
     *     
     */
    public void setMetaFlow(ComponentRef value) {
        this.metaFlow = value;
    }

}
