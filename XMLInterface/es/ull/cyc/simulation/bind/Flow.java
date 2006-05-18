//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.04.12 at 12:22:02 PM BST 
//


package es.ull.cyc.simulation.bind;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.AccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import es.ull.cyc.simulation.bind.Flow;
import es.ull.cyc.simulation.bind.FlowType;
import es.ull.cyc.simulation.bind.RandomNumber;


/**
 * <p>Java class for Flow complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Flow">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="flow" type="{}Flow" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="iterations" type="{}RandomNumber" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="actId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="flowId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="prob" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="type" use="required" type="{}FlowType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(AccessType.FIELD)
@XmlType(name = "Flow", propOrder = {
    "flow",
    "iterations"
})
public class Flow {

    protected List<Flow> flow;
    protected RandomNumber iterations;
    @XmlAttribute
    protected Integer actId;
    @XmlAttribute
    protected Integer flowId;
    @XmlAttribute(required = true)
    protected int id;
    @XmlAttribute
    protected Float prob;
    @XmlAttribute(required = true)
    protected FlowType type;

    /**
     * Gets the value of the flow property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the flow property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFlow().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Flow }
     * 
     * 
     */
    public List<Flow> getFlow() {
        if (flow == null) {
            flow = new ArrayList<Flow>();
        }
        return this.flow;
    }

    /**
     * Gets the value of the iterations property.
     * 
     * @return
     *     possible object is
     *     {@link RandomNumber }
     *     
     */
    public RandomNumber getIterations() {
        return iterations;
    }

    /**
     * Sets the value of the iterations property.
     * 
     * @param value
     *     allowed object is
     *     {@link RandomNumber }
     *     
     */
    public void setIterations(RandomNumber value) {
        this.iterations = value;
    }

    /**
     * Gets the value of the actId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getActId() {
        return actId;
    }

    /**
     * Sets the value of the actId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setActId(Integer value) {
        this.actId = value;
    }

    /**
     * Gets the value of the flowId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFlowId() {
        return flowId;
    }

    /**
     * Sets the value of the flowId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFlowId(Integer value) {
        this.flowId = value;
    }

    /**
     * Gets the value of the id property.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(int value) {
        this.id = value;
    }

    /**
     * Gets the value of the prob property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getProb() {
        return prob;
    }

    /**
     * Sets the value of the prob property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setProb(Float value) {
        this.prob = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link FlowType }
     *     
     */
    public FlowType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link FlowType }
     *     
     */
    public void setType(FlowType value) {
        this.type = value;
    }

}
