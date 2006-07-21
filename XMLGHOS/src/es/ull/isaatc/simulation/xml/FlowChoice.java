//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.07.17 at 03:27:34 PM BST 
//


package es.ull.isaatc.simulation.xml;

import javax.xml.bind.annotation.AccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import es.ull.isaatc.simulation.xml.DecisionFlow;
import es.ull.isaatc.simulation.xml.ExitFlow;
import es.ull.isaatc.simulation.xml.FlowChoice;
import es.ull.isaatc.simulation.xml.PackageFlow;
import es.ull.isaatc.simulation.xml.SequenceFlow;
import es.ull.isaatc.simulation.xml.SimultaneousFlow;
import es.ull.isaatc.simulation.xml.SingleFlow;
import es.ull.isaatc.simulation.xml.TypeFlow;


/**
 * <p>Java class for FlowChoice complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FlowChoice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="single" type="{}SingleFlow"/>
 *         &lt;element name="package" type="{}PackageFlow"/>
 *         &lt;element name="sequence" type="{}SequenceFlow"/>
 *         &lt;element name="simultaneous" type="{}SimultaneousFlow"/>
 *         &lt;element name="decision" type="{}DecisionFlow"/>
 *         &lt;element name="type" type="{}TypeFlow"/>
 *         &lt;element name="exit" type="{}ExitFlow"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(AccessType.FIELD)
@XmlType(name = "FlowChoice", propOrder = {
    "single",
    "_package",
    "sequence",
    "simultaneous",
    "decision",
    "type",
    "exit"
})
public class FlowChoice {

    protected SingleFlow single;
    @XmlElement(name = "package")
    protected PackageFlow _package;
    protected SequenceFlow sequence;
    protected SimultaneousFlow simultaneous;
    protected DecisionFlow decision;
    protected TypeFlow type;
    protected ExitFlow exit;

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

}
