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
 * <p>Clase Java para ObjectUnionOf complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ObjectUnionOf"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.w3.org/2002/07/owl#}ClassExpression"&gt;
 *       &lt;sequence&gt;
 *         &lt;group ref="{http://www.w3.org/2002/07/owl#}ClassExpression" maxOccurs="unbounded" minOccurs="2"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjectUnionOf", propOrder = {
    "classExpression"
})
public class ObjectUnionOf
    extends ClassExpression
{

    @XmlElements({
        @XmlElement(name = "Class", type = Class.class),
        @XmlElement(name = "ObjectIntersectionOf", type = ObjectIntersectionOf.class),
        @XmlElement(name = "ObjectUnionOf", type = ObjectUnionOf.class),
        @XmlElement(name = "ObjectComplementOf", type = ObjectComplementOf.class),
        @XmlElement(name = "ObjectOneOf", type = ObjectOneOf.class),
        @XmlElement(name = "ObjectSomeValuesFrom", type = ObjectSomeValuesFrom.class),
        @XmlElement(name = "ObjectAllValuesFrom", type = ObjectAllValuesFrom.class),
        @XmlElement(name = "ObjectHasValue", type = ObjectHasValue.class),
        @XmlElement(name = "ObjectHasSelf", type = ObjectHasSelf.class),
        @XmlElement(name = "ObjectMinCardinality", type = ObjectMinCardinality.class),
        @XmlElement(name = "ObjectMaxCardinality", type = ObjectMaxCardinality.class),
        @XmlElement(name = "ObjectExactCardinality", type = ObjectExactCardinality.class),
        @XmlElement(name = "DataSomeValuesFrom", type = DataSomeValuesFrom.class),
        @XmlElement(name = "DataAllValuesFrom", type = DataAllValuesFrom.class),
        @XmlElement(name = "DataHasValue", type = DataHasValue.class),
        @XmlElement(name = "DataMinCardinality", type = DataMinCardinality.class),
        @XmlElement(name = "DataMaxCardinality", type = DataMaxCardinality.class),
        @XmlElement(name = "DataExactCardinality", type = DataExactCardinality.class)
    })
    protected List<ClassExpression> classExpression;

    /**
     * Gets the value of the classExpression property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the classExpression property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClassExpression().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Class }
     * {@link ObjectIntersectionOf }
     * {@link ObjectUnionOf }
     * {@link ObjectComplementOf }
     * {@link ObjectOneOf }
     * {@link ObjectSomeValuesFrom }
     * {@link ObjectAllValuesFrom }
     * {@link ObjectHasValue }
     * {@link ObjectHasSelf }
     * {@link ObjectMinCardinality }
     * {@link ObjectMaxCardinality }
     * {@link ObjectExactCardinality }
     * {@link DataSomeValuesFrom }
     * {@link DataAllValuesFrom }
     * {@link DataHasValue }
     * {@link DataMinCardinality }
     * {@link DataMaxCardinality }
     * {@link DataExactCardinality }
     * 
     * 
     */
    public List<ClassExpression> getClassExpression() {
        if (classExpression == null) {
            classExpression = new ArrayList<ClassExpression>();
        }
        return this.classExpression;
    }

}
