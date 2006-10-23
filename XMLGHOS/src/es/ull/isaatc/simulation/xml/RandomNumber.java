//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.10.23 at 09:55:05 AM BST 
//


package es.ull.isaatc.simulation.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.AccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import es.ull.isaatc.simulation.xml.Distribution;
import es.ull.isaatc.simulation.xml.Operation;
import es.ull.isaatc.simulation.xml.RandomNumber;


/**
 * <p>Java class for RandomNumber complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RandomNumber">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element name="op" type="{}Operation"/>
 *           &lt;element name="operand" type="{}RandomNumber" maxOccurs="2" minOccurs="2"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element name="dist" type="{}Distribution"/>
 *           &lt;element name="p1" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *           &lt;element name="p2" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *           &lt;element name="p3" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;/sequence>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(AccessType.FIELD)
@XmlType(name = "RandomNumber", propOrder = {
    "op",
    "operand",
    "dist",
    "p1",
    "p2",
    "p3"
})
public class RandomNumber {

    protected Operation op;
    protected List<RandomNumber> operand;
    protected Distribution dist;
    protected Double p1;
    protected Double p2;
    protected Double p3;

    /**
     * Gets the value of the op property.
     * 
     * @return
     *     possible object is
     *     {@link Operation }
     *     
     */
    public Operation getOp() {
        return op;
    }

    /**
     * Sets the value of the op property.
     * 
     * @param value
     *     allowed object is
     *     {@link Operation }
     *     
     */
    public void setOp(Operation value) {
        this.op = value;
    }

    /**
     * Gets the value of the operand property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the operand property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOperand().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RandomNumber }
     * 
     * 
     */
    public List<RandomNumber> getOperand() {
        if (operand == null) {
            operand = new ArrayList<RandomNumber>();
        }
        return this.operand;
    }

    /**
     * Gets the value of the dist property.
     * 
     * @return
     *     possible object is
     *     {@link Distribution }
     *     
     */
    public Distribution getDist() {
        return dist;
    }

    /**
     * Sets the value of the dist property.
     * 
     * @param value
     *     allowed object is
     *     {@link Distribution }
     *     
     */
    public void setDist(Distribution value) {
        this.dist = value;
    }

    /**
     * Gets the value of the p1 property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getP1() {
        return p1;
    }

    /**
     * Sets the value of the p1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setP1(Double value) {
        this.p1 = value;
    }

    /**
     * Gets the value of the p2 property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getP2() {
        return p2;
    }

    /**
     * Sets the value of the p2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setP2(Double value) {
        this.p2 = value;
    }

    /**
     * Gets the value of the p3 property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getP3() {
        return p3;
    }

    /**
     * Sets the value of the p3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setP3(Double value) {
        this.p3 = value;
    }

}
