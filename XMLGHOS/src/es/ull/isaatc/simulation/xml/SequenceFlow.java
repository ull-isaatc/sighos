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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import es.ull.isaatc.simulation.xml.DecisionFlow;
import es.ull.isaatc.simulation.xml.ExitFlow;
import es.ull.isaatc.simulation.xml.Flow;
import es.ull.isaatc.simulation.xml.PackageFlow;
import es.ull.isaatc.simulation.xml.SequenceFlow;
import es.ull.isaatc.simulation.xml.SimultaneousFlow;
import es.ull.isaatc.simulation.xml.SingleFlow;
import es.ull.isaatc.simulation.xml.TypeFlow;

/**
 * <p>
 * Java class for SequenceFlow complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 *   &lt;complexType name=&quot;SequenceFlow&quot;&gt;
 *     &lt;complexContent&gt;
 *       &lt;extension base=&quot;{}Flow&quot;&gt;
 *         &lt;sequence&gt;
 *           &lt;choice maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;&gt;
 *             &lt;element name=&quot;single&quot; type=&quot;{}SingleFlow&quot;/&gt;
 *             &lt;element name=&quot;package&quot; type=&quot;{}PackageFlow&quot;/&gt;
 *             &lt;element name=&quot;sequence&quot; type=&quot;{}SequenceFlow&quot;/&gt;
 *             &lt;element name=&quot;simultaneous&quot; type=&quot;{}SimultaneousFlow&quot;/&gt;
 *             &lt;element name=&quot;decision&quot; type=&quot;{}DecisionFlow&quot;/&gt;
 *             &lt;element name=&quot;type&quot; type=&quot;{}TypeFlow&quot;/&gt;
 *             &lt;element name=&quot;exit&quot; type=&quot;{}ExitFlow&quot;/&gt;
 *           &lt;/choice&gt;
 *         &lt;/sequence&gt;
 *       &lt;/extension&gt;
 *     &lt;/complexContent&gt;
 *   &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(AccessType.FIELD)
@XmlType(name = "SequenceFlow", propOrder = { "singleOrPackageOrSequence" })
public class SequenceFlow extends Flow {

    @XmlElements( { @XmlElement(name = "decision", type = DecisionFlow.class),
	    @XmlElement(name = "sequence", type = SequenceFlow.class),
	    @XmlElement(name = "package", type = PackageFlow.class),
	    @XmlElement(name = "simultaneous", type = SimultaneousFlow.class),
	    @XmlElement(name = "single", type = SingleFlow.class),
	    @XmlElement(name = "type", type = TypeFlow.class),
	    @XmlElement(name = "exit", type = ExitFlow.class) })
    protected List<Flow> singleOrPackageOrSequence;

    /**
         * Gets the value of the singleOrPackageOrSequence property.
         * 
         * <p>
         * This accessor method returns a reference to the live list, not a
         * snapshot. Therefore any modification you make to the returned list
         * will be present inside the JAXB object. This is why there is not a
         * <CODE>set</CODE> method for the singleOrPackageOrSequence property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * 
         * <pre>
         * getSingleOrPackageOrSequence().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link DecisionFlow } {@link SequenceFlow } {@link PackageFlow }
         * {@link SimultaneousFlow } {@link SingleFlow } {@link TypeFlow }
         * {@link ExitFlow }
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
