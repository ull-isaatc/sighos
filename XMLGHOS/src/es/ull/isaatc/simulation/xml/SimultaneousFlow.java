//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b01-EA2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.05.06 at 06:25:30 PM BST 
//


package es.ull.isaatc.simulation.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SimultaneousFlow complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SimultaneousFlow">
 *   &lt;complexContent>
 *     &lt;extension base="{}Flow">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="single" type="{}SingleFlow"/>
 *           &lt;element name="package" type="{}PackageFlow"/>
 *           &lt;element name="sequence" type="{}SequenceFlow"/>
 *           &lt;element name="simultaneous" type="{}SimultaneousFlow"/>
 *           &lt;element name="decision" type="{}DecisionFlow"/>
 *           &lt;element name="type" type="{}TypeFlow"/>
 *           &lt;element name="exit" type="{}ExitFlow"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SimultaneousFlow", propOrder = {
    "singleOrPackageOrSequence"
})
public class SimultaneousFlow
    extends Flow
{

    @XmlElements({
        @XmlElement(name = "type", type = TypeFlow.class),
        @XmlElement(name = "sequence", type = SequenceFlow.class),
        @XmlElement(name = "exit", type = ExitFlow.class),
        @XmlElement(name = "decision", type = DecisionFlow.class),
        @XmlElement(name = "simultaneous", type = SimultaneousFlow.class),
        @XmlElement(name = "single", type = SingleFlow.class),
        @XmlElement(name = "package", type = PackageFlow.class)
    })
    protected List<Flow> singleOrPackageOrSequence;

    /**
     * Gets the value of the singleOrPackageOrSequence property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the singleOrPackageOrSequence property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSingleOrPackageOrSequence().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeFlow }
     * {@link SequenceFlow }
     * {@link ExitFlow }
     * {@link DecisionFlow }
     * {@link SimultaneousFlow }
     * {@link SingleFlow }
     * {@link PackageFlow }
     * 
     * 
     */
    public List<Flow> getSingleOrPackageOrSequence() {
        if (singleOrPackageOrSequence == null) {
            singleOrPackageOrSequence = new ArrayList<Flow>();
        }
        return this.singleOrPackageOrSequence;
    }

}
