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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import es.ull.cyc.simulation.bind.Activity;
import es.ull.cyc.simulation.bind.Activity.WorkGroup;
import es.ull.cyc.simulation.bind.Activity.WorkGroup.ResourceType;
import es.ull.cyc.simulation.bind.CommonFreq;
import es.ull.cyc.simulation.bind.RandomNumber;


/**
 * <p>Java class for Activity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Activity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="priority" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="presencial" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="workGroup" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="resourceType" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="rt_id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="needed" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                   &lt;element name="priority" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="duration" type="{}RandomNumber"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="timeUnit" type="{}CommonFreq" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(AccessType.FIELD)
@XmlType(name = "Activity", propOrder = {
    "description",
    "priority",
    "presencial",
    "workGroup"
})
public class Activity {

    protected String description;
    @XmlElement(type = Integer.class, defaultValue = "0")
    protected int priority;
    @XmlElement(type = Boolean.class)
    protected boolean presencial;
    protected List<WorkGroup> workGroup;
    @XmlAttribute(required = true)
    protected int id;
    @XmlAttribute
    protected CommonFreq timeUnit;

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
     * Gets the value of the priority property.
     * 
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets the value of the priority property.
     * 
     */
    public void setPriority(int value) {
        this.priority = value;
    }

    /**
     * Gets the value of the presencial property.
     * 
     */
    public boolean isPresencial() {
        return presencial;
    }

    /**
     * Sets the value of the presencial property.
     * 
     */
    public void setPresencial(boolean value) {
        this.presencial = value;
    }

    /**
     * Gets the value of the workGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the workGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWorkGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WorkGroup }
     * 
     * 
     */
    public List<WorkGroup> getWorkGroup() {
        if (workGroup == null) {
            workGroup = new ArrayList<WorkGroup>();
        }
        return this.workGroup;
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
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="resourceType" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="rt_id" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *                   &lt;element name="needed" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *         &lt;element name="priority" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *         &lt;element name="duration" type="{}RandomNumber"/>
     *       &lt;/sequence>
     *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(AccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "resourceType",
        "cost",
        "priority",
        "duration"
    })
    public static class WorkGroup {

        protected List<ResourceType> resourceType;
        @XmlElement(type = Double.class)
        protected double cost;
        @XmlElement(type = Integer.class)
        protected int priority;
        protected RandomNumber duration;
        @XmlAttribute(required = true)
        protected int id;

        /**
         * Gets the value of the resourceType property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the resourceType property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getResourceType().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ResourceType }
         * 
         * 
         */
        public List<ResourceType> getResourceType() {
            if (resourceType == null) {
                resourceType = new ArrayList<ResourceType>();
            }
            return this.resourceType;
        }

        /**
         * Gets the value of the cost property.
         * 
         */
        public double getCost() {
            return cost;
        }

        /**
         * Sets the value of the cost property.
         * 
         */
        public void setCost(double value) {
            this.cost = value;
        }

        /**
         * Gets the value of the priority property.
         * 
         */
        public int getPriority() {
            return priority;
        }

        /**
         * Sets the value of the priority property.
         * 
         */
        public void setPriority(int value) {
            this.priority = value;
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
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="rt_id" type="{http://www.w3.org/2001/XMLSchema}int"/>
         *         &lt;element name="needed" type="{http://www.w3.org/2001/XMLSchema}int"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(AccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "rtId",
            "needed"
        })
        public static class ResourceType {

            @XmlElement(name = "rt_id", type = Integer.class)
            protected int rtId;
            @XmlElement(type = Integer.class)
            protected int needed;

            /**
             * Gets the value of the rtId property.
             * 
             */
            public int getRtId() {
                return rtId;
            }

            /**
             * Sets the value of the rtId property.
             * 
             */
            public void setRtId(int value) {
                this.rtId = value;
            }

            /**
             * Gets the value of the needed property.
             * 
             */
            public int getNeeded() {
                return needed;
            }

            /**
             * Sets the value of the needed property.
             * 
             */
            public void setNeeded(int value) {
                this.needed = value;
            }

        }

    }

}
