//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.10.26 at 06:36:48 PM BST 
//


package es.ull.isaatc.simulation.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.AccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import es.ull.isaatc.simulation.xml.CommonFreq;
import es.ull.isaatc.simulation.xml.Cycle;
import es.ull.isaatc.simulation.xml.Resource;
import es.ull.isaatc.simulation.xml.Resource.TimeTable;
import es.ull.isaatc.simulation.xml.Resource.TimeTable.Dur;


/**
 * <p>Java class for Resource complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Resource">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="units" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="timeTable" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="rt_id" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded"/>
 *                   &lt;element name="cycle" type="{}Cycle"/>
 *                   &lt;element name="dur">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>int">
 *                           &lt;attribute name="timeUnit" type="{}CommonFreq" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
@XmlType(name = "Resource", propOrder = {
    "description",
    "units",
    "timeTable"
})
public class Resource {

    protected String description;
    @XmlElement(type = Integer.class, defaultValue = "1")
    protected int units;
    protected List<TimeTable> timeTable;
    @XmlAttribute(required = true)
    protected int id;

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
     * Gets the value of the units property.
     * 
     */
    public int getUnits() {
        return units;
    }

    /**
     * Sets the value of the units property.
     * 
     */
    public void setUnits(int value) {
        this.units = value;
    }

    /**
     * Gets the value of the timeTable property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the timeTable property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTimeTable().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TimeTable }
     * 
     * 
     */
    public List<TimeTable> getTimeTable() {
        if (timeTable == null) {
            timeTable = new ArrayList<TimeTable>();
        }
        return this.timeTable;
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
     *         &lt;element name="rt_id" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded"/>
     *         &lt;element name="cycle" type="{}Cycle"/>
     *         &lt;element name="dur">
     *           &lt;complexType>
     *             &lt;simpleContent>
     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>int">
     *                 &lt;attribute name="timeUnit" type="{}CommonFreq" />
     *               &lt;/extension>
     *             &lt;/simpleContent>
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
    @XmlAccessorType(AccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "rtId",
        "cycle",
        "dur"
    })
    public static class TimeTable {

        @XmlElement(name = "rt_id", type = Integer.class, defaultValue = "-1")
        protected List<Integer> rtId;
        protected Cycle cycle;
        protected Dur dur;

        /**
         * Gets the value of the rtId property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the rtId property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRtId().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Integer }
         * 
         * 
         */
        public List<Integer> getRtId() {
            if (rtId == null) {
                rtId = new ArrayList<Integer>();
            }
            return this.rtId;
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
         * Gets the value of the dur property.
         * 
         * @return
         *     possible object is
         *     {@link Dur }
         *     
         */
        public Dur getDur() {
            return dur;
        }

        /**
         * Sets the value of the dur property.
         * 
         * @param value
         *     allowed object is
         *     {@link Dur }
         *     
         */
        public void setDur(Dur value) {
            this.dur = value;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;simpleContent>
         *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>int">
         *       &lt;attribute name="timeUnit" type="{}CommonFreq" />
         *     &lt;/extension>
         *   &lt;/simpleContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(AccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class Dur {

            @XmlValue
            protected int value;
            @XmlAttribute
            protected CommonFreq timeUnit;

            /**
             * Gets the value of the value property.
             * 
             */
            public int getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             */
            public void setValue(int value) {
                this.value = value;
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

        }

    }

}
