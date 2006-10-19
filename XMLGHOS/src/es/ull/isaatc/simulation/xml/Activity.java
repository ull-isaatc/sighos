//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.10.18 at 06:31:27 PM BST 
//

package es.ull.isaatc.simulation.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.AccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import es.ull.isaatc.simulation.xml.Activity;
import es.ull.isaatc.simulation.xml.Activity.WorkGroup;
import es.ull.isaatc.simulation.xml.Activity.WorkGroup.Role;
import es.ull.isaatc.simulation.xml.CommonFreq;
import es.ull.isaatc.simulation.xml.RandomNumber;

/**
 * <p>
 * Java class for Activity complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 *   &lt;complexType name=&quot;Activity&quot;&gt;
 *     &lt;complexContent&gt;
 *       &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *         &lt;sequence&gt;
 *           &lt;element name=&quot;description&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *           &lt;element name=&quot;priority&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}int&quot;/&gt;
 *           &lt;element name=&quot;presential&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}boolean&quot;/&gt;
 *           &lt;element name=&quot;workGroup&quot; maxOccurs=&quot;unbounded&quot;&gt;
 *             &lt;complexType&gt;
 *               &lt;complexContent&gt;
 *                 &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *                   &lt;sequence&gt;
 *                     &lt;element name=&quot;description&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *                     &lt;element name=&quot;role&quot; maxOccurs=&quot;unbounded&quot;&gt;
 *                       &lt;complexType&gt;
 *                         &lt;complexContent&gt;
 *                           &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *                             &lt;sequence&gt;
 *                               &lt;any/&gt;
 *                             &lt;/sequence&gt;
 *                             &lt;attribute name=&quot;rt_id&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}int&quot; /&gt;
 *                             &lt;attribute name=&quot;units&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}int&quot; /&gt;
 *                           &lt;/restriction&gt;
 *                         &lt;/complexContent&gt;
 *                       &lt;/complexType&gt;
 *                     &lt;/element&gt;
 *                     &lt;element name=&quot;priority&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}int&quot;/&gt;
 *                     &lt;element name=&quot;duration&quot; type=&quot;{}RandomNumber&quot;/&gt;
 *                   &lt;/sequence&gt;
 *                   &lt;attribute name=&quot;id&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}int&quot; /&gt;
 *                   &lt;attribute name=&quot;timeUnit&quot; type=&quot;{}CommonFreq&quot; /&gt;
 *                 &lt;/restriction&gt;
 *               &lt;/complexContent&gt;
 *             &lt;/complexType&gt;
 *           &lt;/element&gt;
 *         &lt;/sequence&gt;
 *         &lt;attribute name=&quot;id&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}int&quot; /&gt;
 *       &lt;/restriction&gt;
 *     &lt;/complexContent&gt;
 *   &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(AccessType.FIELD)
@XmlType(name = "Activity", propOrder = { "description", "priority",
	"presential", "workGroup" })
public class Activity {

    protected String description;

    @XmlElement(type = Integer.class, defaultValue = "0")
    protected int priority;

    @XmlElement(type = Boolean.class)
    protected boolean presential;

    protected List<WorkGroup> workGroup;

    @XmlAttribute(required = true)
    protected int id;

    /**
         * Gets the value of the description property.
         * 
         * @return possible object is {@link String }
         * 
         */
    public String getDescription() {
	return description;
    }

    /**
         * Sets the value of the description property.
         * 
         * @param value
         *                allowed object is {@link String }
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
         * Gets the value of the presential property.
         * 
         */
    public boolean isPresential() {
	return presential;
    }

    /**
         * Sets the value of the presential property.
         * 
         */
    public void setPresential(boolean value) {
	this.presential = value;
    }

    /**
         * Gets the value of the workGroup property.
         * 
         * <p>
         * This accessor method returns a reference to the live list, not a
         * snapshot. Therefore any modification you make to the returned list
         * will be present inside the JAXB object. This is why there is not a
         * <CODE>set</CODE> method for the workGroup property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * 
         * <pre>
         * getWorkGroup().add(newItem);
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
         * <p>
         * Java class for anonymous complex type.
         * 
         * <p>
         * The following schema fragment specifies the expected content
         * contained within this class.
         * 
         * <pre>
         *   &lt;complexType&gt;
         *     &lt;complexContent&gt;
         *       &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
         *         &lt;sequence&gt;
         *           &lt;element name=&quot;description&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
         *           &lt;element name=&quot;role&quot; maxOccurs=&quot;unbounded&quot;&gt;
         *             &lt;complexType&gt;
         *               &lt;complexContent&gt;
         *                 &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
         *                   &lt;sequence&gt;
         *                     &lt;any/&gt;
         *                   &lt;/sequence&gt;
         *                   &lt;attribute name=&quot;rt_id&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}int&quot; /&gt;
         *                   &lt;attribute name=&quot;units&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}int&quot; /&gt;
         *                 &lt;/restriction&gt;
         *               &lt;/complexContent&gt;
         *             &lt;/complexType&gt;
         *           &lt;/element&gt;
         *           &lt;element name=&quot;priority&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}int&quot;/&gt;
         *           &lt;element name=&quot;duration&quot; type=&quot;{}RandomNumber&quot;/&gt;
         *         &lt;/sequence&gt;
         *         &lt;attribute name=&quot;id&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}int&quot; /&gt;
         *         &lt;attribute name=&quot;timeUnit&quot; type=&quot;{}CommonFreq&quot; /&gt;
         *       &lt;/restriction&gt;
         *     &lt;/complexContent&gt;
         *   &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
    @XmlAccessorType(AccessType.FIELD)
    @XmlType(name = "", propOrder = { "description", "role", "priority",
	    "duration" })
    public static class WorkGroup {

	protected String description;

	protected List<Role> role;

	@XmlElement(type = Integer.class)
	protected int priority;

	protected RandomNumber duration;

	@XmlAttribute(required = true)
	protected int id;

	@XmlAttribute
	protected CommonFreq timeUnit;

	/**
         * Gets the value of the description property.
         * 
         * @return possible object is {@link String }
         * 
         */
	public String getDescription() {
	    return description;
	}

	/**
         * Sets the value of the description property.
         * 
         * @param value
         *                allowed object is {@link String }
         * 
         */
	public void setDescription(String value) {
	    this.description = value;
	}

	/**
         * Gets the value of the role property.
         * 
         * <p>
         * This accessor method returns a reference to the live list, not a
         * snapshot. Therefore any modification you make to the returned list
         * will be present inside the JAXB object. This is why there is not a
         * <CODE>set</CODE> method for the role property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * 
         * <pre>
         * getRole().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Role }
         * 
         * 
         */
	public List<Role> getRole() {
	    if (role == null) {
		role = new ArrayList<Role>();
	    }
	    return this.role;
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
         * @return possible object is {@link RandomNumber }
         * 
         */
	public RandomNumber getDuration() {
	    return duration;
	}

	/**
         * Sets the value of the duration property.
         * 
         * @param value
         *                allowed object is {@link RandomNumber }
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
         * Gets the value of the timeUnit property.
         * 
         * @return possible object is {@link CommonFreq }
         * 
         */
	public CommonFreq getTimeUnit() {
	    return timeUnit;
	}

	/**
         * Sets the value of the timeUnit property.
         * 
         * @param value
         *                allowed object is {@link CommonFreq }
         * 
         */
	public void setTimeUnit(CommonFreq value) {
	    this.timeUnit = value;
	}

	/**
         * <p>
         * Java class for anonymous complex type.
         * 
         * <p>
         * The following schema fragment specifies the expected content
         * contained within this class.
         * 
         * <pre>
         *   &lt;complexType&gt;
         *     &lt;complexContent&gt;
         *       &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
         *         &lt;sequence&gt;
         *           &lt;any/&gt;
         *         &lt;/sequence&gt;
         *         &lt;attribute name=&quot;rt_id&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}int&quot; /&gt;
         *         &lt;attribute name=&quot;units&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}int&quot; /&gt;
         *       &lt;/restriction&gt;
         *     &lt;/complexContent&gt;
         *   &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
	@XmlAccessorType(AccessType.FIELD)
	@XmlType(name = "", propOrder = { "any" })
	public static class Role {

	    @XmlAnyElement(lax = true)
	    protected Object any;

	    @XmlAttribute(name = "rt_id", required = true)
	    protected int rtId;

	    @XmlAttribute(required = true)
	    protected int units;

	    /**
                 * Gets the value of the any property.
                 * 
                 * @return possible object is {@link Object }
                 * 
                 */
	    public Object getAny() {
		return any;
	    }

	    /**
                 * Sets the value of the any property.
                 * 
                 * @param value
                 *                allowed object is {@link Object }
                 * 
                 */
	    public void setAny(Object value) {
		this.any = value;
	    }

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

	}

    }

}
