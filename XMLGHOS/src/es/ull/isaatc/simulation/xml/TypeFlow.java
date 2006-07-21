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
import javax.xml.bind.annotation.XmlType;
import es.ull.isaatc.simulation.xml.Flow;
import es.ull.isaatc.simulation.xml.TypeBranch;
import es.ull.isaatc.simulation.xml.TypeFlow;


/**
 * <p>Java class for TypeFlow complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TypeFlow">
 *   &lt;complexContent>
 *     &lt;extension base="{}Flow">
 *       &lt;sequence>
 *         &lt;element name="branch" type="{}TypeBranch" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(AccessType.FIELD)
@XmlType(name = "TypeFlow", propOrder = {
    "branch"
})
public class TypeFlow
    extends Flow
{

    protected List<TypeBranch> branch;

    /**
     * Gets the value of the branch property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the branch property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBranch().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeBranch }
     * 
     * 
     */
    public List<TypeBranch> getBranch() {
        if (branch == null) {
            branch = new ArrayList<TypeBranch>();
        }
        return this.branch;
    }

}
