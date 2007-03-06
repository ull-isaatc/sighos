//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.0 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.02.28 at 10:32:01 AM GMT 
//


package es.ull.isaatc.simulation.fuzzy.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import es.ull.isaatc.simulation.xml.ComponentRef;
import es.ull.isaatc.simulation.xml.Cycle;


/**
 * <p>Java class for Sampler complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Sampler">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cycle" type="{}Cycle"/>
 *         &lt;element name="filename" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="task" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="cycle" type="{}Cycle"/>
 *                   &lt;element name="elementType" type="{}ComponentRef"/>
 *                   &lt;element name="metaFlow" type="{}ComponentRef"/>
 *                   &lt;element name="qos" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Sampler", propOrder = {
    "cycle",
    "filename",
    "task"
})
public class Sampler {

    @XmlElement(required = true)
    protected Cycle cycle;
    @XmlElement(required = true)
    protected String filename;
    protected List<Sampler.Task> task;

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
     * Gets the value of the filename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the value of the filename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilename(String value) {
        this.filename = value;
    }

    /**
     * Gets the value of the task property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the task property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTask().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Sampler.Task }
     * 
     * 
     */
    public List<Sampler.Task> getTask() {
        if (task == null) {
            task = new ArrayList<Sampler.Task>();
        }
        return this.task;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="cycle" type="{}Cycle"/>
     *         &lt;element name="elementType" type="{}ComponentRef"/>
     *         &lt;element name="metaFlow" type="{}ComponentRef"/>
     *         &lt;element name="qos" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "description",
        "cycle",
        "elementType",
        "metaFlow",
        "qos"
    })
    public static class Task {

        @XmlElement(required = true)
        protected String description;
        @XmlElement(required = true)
        protected Cycle cycle;
        @XmlElement(required = true)
        protected ComponentRef elementType;
        @XmlElement(required = true)
        protected ComponentRef metaFlow;
        protected double qos;

        /**
         * Gets the value of the description property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDescription() {
            return description;
        }

        /**
         * Sets the value of the description property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDescription(String value) {
            this.description = value;
        }

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

        /**
         * Gets the value of the qos property.
         * 
         */
        public double getQos() {
            return qos;
        }

        /**
         * Sets the value of the qos property.
         * 
         */
        public void setQos(double value) {
            this.qos = value;
        }

    }

}