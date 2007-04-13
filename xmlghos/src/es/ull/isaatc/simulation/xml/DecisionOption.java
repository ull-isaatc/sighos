//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b24-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.04.12 at 03:20:00 PM BST 
//


package es.ull.isaatc.simulation.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DecisionOption complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DecisionOption">
 *   &lt;complexContent>
 *     &lt;extension base="{}Flow">
 *       &lt;choice>
 *         &lt;element name="single" type="{}SingleFlow"/>
 *         &lt;element name="package" type="{}PackageFlow"/>
 *         &lt;element name="sequence" type="{}SequenceFlow"/>
 *         &lt;element name="simultaneous" type="{}SimultaneousFlow"/>
 *         &lt;element name="decision" type="{}DecisionFlow"/>
 *         &lt;element name="type" type="{}TypeFlow"/>
 *         &lt;element name="exit" type="{}ExitFlow"/>
 *       &lt;/choice>
 *       &lt;attribute name="prob" use="required" type="{http://www.w3.org/2001/XMLSchema}float" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DecisionOption", propOrder = {
    "single",
    "_package",
    "sequence",
    "simultaneous",
    "decision",
    "type",
    "exit"
})
public class DecisionOption
    extends Flow
{

    protected SingleFlow single;
    @XmlElement(name = "package")
    protected PackageFlow _package;
    protected SequenceFlow sequence;
    protected SimultaneousFlow simultaneous;
    protected DecisionFlow decision;
    protected TypeFlow type;
    protected ExitFlow exit;
    @XmlAttribute(required = true)
    protected float prob;

    /**
     * Gets the value of the single property.
     * 
     * @return
     *     possible object is
     *     {@link SingleFlow }
     *     
     */
    public SingleFlow getSingle() {
        return single;
    }

    /**
     * Sets the value of the single property.
     * 
     * @param value
     *     allowed object is
     *     {@link SingleFlow }
     *     
     */
    public void setSingle(SingleFlow value) {
        this.single = value;
    }

    /**
     * Gets the value of the package property.
     * 
     * @return
     *     possible object is
     *     {@link PackageFlow }
     *     
     */
    public PackageFlow getPackage() {
        return _package;
    }

    /**
     * Sets the value of the package property.
     * 
     * @param value
     *     allowed object is
     *     {@link PackageFlow }
     *     
     */
    public void setPackage(PackageFlow value) {
        this._package = value;
    }

    /**
     * Gets the value of the sequence property.
     * 
     * @return
     *     possible object is
     *     {@link SequenceFlow }
     *     
     */
    public SequenceFlow getSequence() {
        return sequence;
    }

    /**
     * Sets the value of the sequence property.
     * 
     * @param value
     *     allowed object is
     *     {@link SequenceFlow }
     *     
     */
    public void setSequence(SequenceFlow value) {
        this.sequence = value;
    }

    /**
     * Gets the value of the simultaneous property.
     * 
     * @return
     *     possible object is
     *     {@link SimultaneousFlow }
     *     
     */
    public SimultaneousFlow getSimultaneous() {
        return simultaneous;
    }

    /**
     * Sets the value of the simultaneous property.
     * 
     * @param value
     *     allowed object is
     *     {@link SimultaneousFlow }
     *     
     */
    public void setSimultaneous(SimultaneousFlow value) {
        this.simultaneous = value;
    }

    /**
     * Gets the value of the decision property.
     * 
     * @return
     *     possible object is
     *     {@link DecisionFlow }
     *     
     */
    public DecisionFlow getDecision() {
        return decision;
    }

    /**
     * Sets the value of the decision property.
     * 
     * @param value
     *     allowed object is
     *     {@link DecisionFlow }
     *     
     */
    public void setDecision(DecisionFlow value) {
        this.decision = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link TypeFlow }
     *     
     */
    public TypeFlow getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeFlow }
     *     
     */
    public void setType(TypeFlow value) {
        this.type = value;
    }

    /**
     * Gets the value of the exit property.
     * 
     * @return
     *     possible object is
     *     {@link ExitFlow }
     *     
     */
    public ExitFlow getExit() {
        return exit;
    }

    /**
     * Sets the value of the exit property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExitFlow }
     *     
     */
    public void setExit(ExitFlow value) {
        this.exit = value;
    }

    /**
     * Gets the value of the prob property.
     * 
     */
    public float getProb() {
        return prob;
    }

    /**
     * Sets the value of the prob property.
     * 
     */
    public void setProb(float value) {
        this.prob = value;
    }

}
