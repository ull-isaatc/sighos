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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Cycle complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Cycle">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;sequence>
 *             &lt;element name="timeUnit" type="{}CommonFreq"/>
 *             &lt;element name="startTs" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *             &lt;choice>
 *               &lt;element name="iterations" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *               &lt;element name="endTs" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *             &lt;/choice>
 *             &lt;element name="period" type="{}FunctionChoice"/>
 *           &lt;/sequence>
 *           &lt;sequence>
 *             &lt;element name="ts" type="{http://www.w3.org/2001/XMLSchema}double" maxOccurs="unbounded"/>
 *           &lt;/sequence>
 *         &lt;/choice>
 *         &lt;element name="subCycle" type="{}Cycle" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="type" use="required" type="{}CycleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Cycle", propOrder = {
    "timeUnit",
    "startTs",
    "iterations",
    "endTs",
    "period",
    "ts",
    "subCycle"
})
public class Cycle {

    protected CommonFreq timeUnit;
    protected Double startTs;
    protected Integer iterations;
    protected Double endTs;
    protected FunctionChoice period;
    @XmlElement(type = Double.class)
    protected List<Double> ts;
    protected Cycle subCycle;
    @XmlAttribute(required = true)
    protected CycleType type;

    /**
     * Gets the value of the timeUnit property.
     * 
     * @return
     *     possible object is
     *     {@link CommonFreq }
     *     
     */
    public CommonFreq getTimeUnit() {
        return timeUnit;
    }

    /**
     * Sets the value of the timeUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommonFreq }
     *     
     */
    public void setTimeUnit(CommonFreq value) {
        this.timeUnit = value;
    }

    /**
     * Gets the value of the startTs property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getStartTs() {
        return startTs;
    }

    /**
     * Sets the value of the startTs property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setStartTs(Double value) {
        this.startTs = value;
    }

    /**
     * Gets the value of the iterations property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIterations() {
        return iterations;
    }

    /**
     * Sets the value of the iterations property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIterations(Integer value) {
        this.iterations = value;
    }

    /**
     * Gets the value of the endTs property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getEndTs() {
        return endTs;
    }

    /**
     * Sets the value of the endTs property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setEndTs(Double value) {
        this.endTs = value;
    }

    /**
     * Gets the value of the period property.
     * 
     * @return
     *     possible object is
     *     {@link FunctionChoice }
     *     
     */
    public FunctionChoice getPeriod() {
        return period;
    }

    /**
     * Sets the value of the period property.
     * 
     * @param value
     *     allowed object is
     *     {@link FunctionChoice }
     *     
     */
    public void setPeriod(FunctionChoice value) {
        this.period = value;
    }

    /**
     * Gets the value of the ts property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ts property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Double }
     * 
     * 
     */
    public List<Double> getTs() {
        if (ts == null) {
            ts = new ArrayList<Double>();
        }
        return this.ts;
    }

    /**
     * Gets the value of the subCycle property.
     * 
     * @return
     *     possible object is
     *     {@link Cycle }
     *     
     */
    public Cycle getSubCycle() {
        return subCycle;
    }

    /**
     * Sets the value of the subCycle property.
     * 
     * @param value
     *     allowed object is
     *     {@link Cycle }
     *     
     */
    public void setSubCycle(Cycle value) {
        this.subCycle = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link CycleType }
     *     
     */
    public CycleType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link CycleType }
     *     
     */
    public void setType(CycleType value) {
        this.type = value;
    }

}
