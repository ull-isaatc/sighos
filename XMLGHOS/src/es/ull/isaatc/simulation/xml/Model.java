//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.07.17 at 03:27:34 PM BST 
//


package es.ull.isaatc.simulation.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.AccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import es.ull.isaatc.simulation.xml.Activity;
import es.ull.isaatc.simulation.xml.CommonFreq;
import es.ull.isaatc.simulation.xml.ElementType;
import es.ull.isaatc.simulation.xml.Model;
import es.ull.isaatc.simulation.xml.Resource;
import es.ull.isaatc.simulation.xml.ResourceType;
import es.ull.isaatc.simulation.xml.RootFlow;


/**
 * <p>Java class for Model element declaration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="Model">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *           &lt;element name="baseTimeUnit" type="{}CommonFreq"/>
 *           &lt;element name="resourceType" type="{}ResourceType" maxOccurs="unbounded"/>
 *           &lt;element name="resource" type="{}Resource" maxOccurs="unbounded"/>
 *           &lt;element name="activity" type="{}Activity" maxOccurs="unbounded"/>
 *           &lt;element name="elementType" type="{}ElementType" maxOccurs="unbounded"/>
 *           &lt;element name="rootFlow" type="{}RootFlow" maxOccurs="unbounded"/>
 *         &lt;/sequence>
 *       &lt;/restriction>
 *     &lt;/complexContent>
 *   &lt;/complexType>
 * &lt;/element>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(AccessType.FIELD)
@XmlType(name = "", propOrder = {
    "description",
    "baseTimeUnit",
    "resourceType",
    "resource",
    "activity",
    "elementType",
    "rootFlow"
})
@XmlRootElement(name = "Model")
public class Model {

    protected String description;
    protected CommonFreq baseTimeUnit;
    protected List<ResourceType> resourceType;
    protected List<Resource> resource;
    protected List<Activity> activity;
    protected List<ElementType> elementType;
    protected List<RootFlow> rootFlow;

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
     * Gets the value of the baseTimeUnit property.
     * 
     * @return
     *     possible object is
     *     {@link CommonFreq }
     *     
     */
    public CommonFreq getBaseTimeUnit() {
        return baseTimeUnit;
    }

    /**
     * Sets the value of the baseTimeUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommonFreq }
     *     
     */
    public void setBaseTimeUnit(CommonFreq value) {
        this.baseTimeUnit = value;
    }

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
     * Gets the value of the resource property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resource property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResource().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Resource }
     * 
     * 
     */
    public List<Resource> getResource() {
        if (resource == null) {
            resource = new ArrayList<Resource>();
        }
        return this.resource;
    }

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
     * {@link Activity }
     * 
     * 
     */
    public List<Activity> getActivity() {
        if (activity == null) {
            activity = new ArrayList<Activity>();
        }
        return this.activity;
    }

    /**
     * Gets the value of the elementType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the elementType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getElementType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ElementType }
     * 
     * 
     */
    public List<ElementType> getElementType() {
        if (elementType == null) {
            elementType = new ArrayList<ElementType>();
        }
        return this.elementType;
    }

    /**
     * Gets the value of the rootFlow property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rootFlow property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRootFlow().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RootFlow }
     * 
     * 
     */
    public List<RootFlow> getRootFlow() {
        if (rootFlow == null) {
            rootFlow = new ArrayList<RootFlow>();
        }
        return this.rootFlow;
    }

}
