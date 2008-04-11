//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.0 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.09.20 at 10:31:31 AM BST 
//


package es.ull.isaatc.simulation.listener.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SelectableActivityListener complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SelectableActivityListener">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.example.org/SimulationListener}PeriodicListener">
 *       &lt;sequence>
 *         &lt;element name="activity" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="actQueue">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="queue" type="{http://www.w3.org/2001/XMLSchema}double" maxOccurs="unbounded"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="actPerformed">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="performed" type="{http://www.w3.org/2001/XMLSchema}double" maxOccurs="unbounded"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="actId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="desc" type="{http://www.w3.org/2001/XMLSchema}string" />
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
@XmlType(name = "SelectableActivityListener", propOrder = {
    "activity"
})
public class SelectableActivityListener
    extends PeriodicListener
{

    @XmlElement(required = true)
    protected List<SelectableActivityListener.Activity> activity;

    /**
     * Gets the value of the activity property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the activity property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getActivity().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SelectableActivityListener.Activity }
     * 
     * 
     */
    public List<SelectableActivityListener.Activity> getActivity() {
        if (activity == null) {
            activity = new ArrayList<SelectableActivityListener.Activity>();
        }
        return this.activity;
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
     *         &lt;element name="actQueue">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="queue" type="{http://www.w3.org/2001/XMLSchema}double" maxOccurs="unbounded"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="actPerformed">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="performed" type="{http://www.w3.org/2001/XMLSchema}double" maxOccurs="unbounded"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="actId" type="{http://www.w3.org/2001/XMLSchema}int" />
     *       &lt;attribute name="desc" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "actQueue",
        "actPerformed"
    })
    public static class Activity {

        @XmlElement(required = true)
        protected SelectableActivityListener.Activity.ActQueue actQueue;
        @XmlElement(required = true)
        protected SelectableActivityListener.Activity.ActPerformed actPerformed;
        @XmlAttribute
        protected Integer actId;
        @XmlAttribute
        protected String desc;

        /**
         * Gets the value of the actQueue property.
         * 
         * @return
         *     possible object is
         *     {@link SelectableActivityListener.Activity.ActQueue }
         *     
         */
        public SelectableActivityListener.Activity.ActQueue getActQueue() {
            return actQueue;
        }

        /**
         * Sets the value of the actQueue property.
         * 
         * @param value
         *     allowed object is
         *     {@link SelectableActivityListener.Activity.ActQueue }
         *     
         */
        public void setActQueue(SelectableActivityListener.Activity.ActQueue value) {
            this.actQueue = value;
        }

        /**
         * Gets the value of the actPerformed property.
         * 
         * @return
         *     possible object is
         *     {@link SelectableActivityListener.Activity.ActPerformed }
         *     
         */
        public SelectableActivityListener.Activity.ActPerformed getActPerformed() {
            return actPerformed;
        }

        /**
         * Sets the value of the actPerformed property.
         * 
         * @param value
         *     allowed object is
         *     {@link SelectableActivityListener.Activity.ActPerformed }
         *     
         */
        public void setActPerformed(SelectableActivityListener.Activity.ActPerformed value) {
            this.actPerformed = value;
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
         * Gets the value of the desc property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDesc() {
            return desc;
        }

        /**
         * Sets the value of the desc property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDesc(String value) {
            this.desc = value;
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
         *         &lt;element name="performed" type="{http://www.w3.org/2001/XMLSchema}double" maxOccurs="unbounded"/>
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
            "performed"
        })
        public static class ActPerformed {

            @XmlElement(type = Double.class)
            protected List<Double> performed;

            /**
             * Gets the value of the performed property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the performed property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getPerformed().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Double }
             * 
             * 
             */
            public List<Double> getPerformed() {
                if (performed == null) {
                    performed = new ArrayList<Double>();
                }
                return this.performed;
            }

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
         *         &lt;element name="queue" type="{http://www.w3.org/2001/XMLSchema}double" maxOccurs="unbounded"/>
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
            "queue"
        })
        public static class ActQueue {

            @XmlElement(type = Double.class)
            protected List<Double> queue;

            /**
             * Gets the value of the queue property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the queue property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getQueue().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Double }
             * 
             * 
             */
            public List<Double> getQueue() {
                if (queue == null) {
                    queue = new ArrayList<Double>();
                }
                return this.queue;
            }

        }

    }

}
