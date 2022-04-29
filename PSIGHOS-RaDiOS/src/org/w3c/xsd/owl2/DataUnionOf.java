//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2022.04.26 a las 05:42:17 PM BST 
//


package org.w3c.xsd.owl2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para DataUnionOf complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DataUnionOf"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.w3.org/2002/07/owl#}DataRange"&gt;
 *       &lt;sequence&gt;
 *         &lt;group ref="{http://www.w3.org/2002/07/owl#}DataRange" maxOccurs="unbounded" minOccurs="2"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataUnionOf", propOrder = {
    "dataRange"
})
public class DataUnionOf
    extends DataRange
{

    @XmlElements({
        @XmlElement(name = "Datatype", type = Datatype.class),
        @XmlElement(name = "DataIntersectionOf", type = DataIntersectionOf.class),
        @XmlElement(name = "DataUnionOf", type = DataUnionOf.class),
        @XmlElement(name = "DataComplementOf", type = DataComplementOf.class),
        @XmlElement(name = "DataOneOf", type = DataOneOf.class),
        @XmlElement(name = "DatatypeRestriction", type = DatatypeRestriction.class)
    })
    protected List<DataRange> dataRange;

    /**
     * Gets the value of the dataRange property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dataRange property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDataRange().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Datatype }
     * {@link DataIntersectionOf }
     * {@link DataUnionOf }
     * {@link DataComplementOf }
     * {@link DataOneOf }
     * {@link DatatypeRestriction }
     * 
     * 
     */
    public List<DataRange> getDataRange() {
        if (dataRange == null) {
            dataRange = new ArrayList<DataRange>();
        }
        return this.dataRange;
    }

}
