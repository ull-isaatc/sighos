//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b24-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.04.12 at 03:20:00 PM BST 
//


package es.ull.isaatc.simulation.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for Resource complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Resource">
 *   &lt;complexContent>
 *     &lt;extension base="{}DescComponent">
 *       &lt;sequence>
 *         &lt;element name="units" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="timeTable" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="rt_ref" type="{}ComponentRef" maxOccurs="unbounded"/>
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
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Resource", propOrder = {
    "units",
    "timeTable"
})
public class Resource
    extends DescComponent
{

    @XmlElement(defaultValue = "1")
    protected int units;
    protected List<Resource.TimeTable> timeTable;

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
     * {@link Resource.TimeTable }
     * 
     * 
     */
    public List<Resource.TimeTable> getTimeTable() {
        if (timeTable == null) {
            timeTable = new ArrayList<Resource.TimeTable>();
        }
        return this.timeTable;
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
     *         &lt;element name="rt_ref" type="{}ComponentRef" maxOccurs="unbounded"/>
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
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "rtRef",
        "cycle",
        "dur"
    })
    public static class TimeTable {

        @XmlElement(name = "rt_ref", required = true)
        protected List<ComponentRef> rtRef;
        @XmlElement(required = true)
        protected Cycle cycle;
        @XmlElement(required = true)
        protected Resource.TimeTable.Dur dur;

        /**
         * Gets the value of the rtRef property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the rtRef property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRtRef().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ComponentRef }
         * 
         * 
         */
        public List<ComponentRef> getRtRef() {
            if (rtRef == null) {
                rtRef = new ArrayList<ComponentRef>();
            }
            return this.rtRef;
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
         *     {@link Resource.TimeTable.Dur }
         *     
         */
        public Resource.TimeTable.Dur getDur() {
            return dur;
        }

        /**
         * Sets the value of the dur property.
         * 
         * @param value
         *     allowed object is
         *     {@link Resource.TimeTable.Dur }
         *     
         */
        public void setDur(Resource.TimeTable.Dur value) {
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
        @XmlAccessorType(XmlAccessType.FIELD)
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
