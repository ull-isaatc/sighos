//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b01-EA2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.05.06 at 06:25:30 PM BST 
//


package es.ull.isaatc.simulation.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RootFlow complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RootFlow">
 *   &lt;complexContent>
 *     &lt;extension base="{}DescComponent">
 *       &lt;sequence>
 *         &lt;element name="flow" type="{}FlowChoice"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RootFlow", propOrder = {
    "flow"
})
public class RootFlow
    extends DescComponent
{

    @XmlElement(required = true)
    protected FlowChoice flow;

    /**
     * Gets the value of the flow property.
     * 
     * @return
     *     possible object is
     *     {@link FlowChoice }
     *     
     */
    public FlowChoice getFlow() {
        return flow;
    }

    /**
     * Sets the value of the flow property.
     * 
     * @param value
     *     allowed object is
     *     {@link FlowChoice }
     *     
     */
    public void setFlow(FlowChoice value) {
        this.flow = value;
    }

}
