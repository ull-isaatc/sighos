//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2022.04.26 a las 05:42:17 PM BST 
//


package org.w3c.xsd.owl2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para SubClassOf complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="SubClassOf"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.w3.org/2002/07/owl#}ClassAxiom"&gt;
 *       &lt;sequence&gt;
 *         &lt;group ref="{http://www.w3.org/2002/07/owl#}ClassExpression"/&gt;
 *         &lt;group ref="{http://www.w3.org/2002/07/owl#}ClassExpression"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubClassOf", propOrder = {
    "rest"
})
public class SubClassOf
    extends ClassAxiom
{

    @XmlElementRefs({
        @XmlElementRef(name = "ObjectComplementOf", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "ObjectAllValuesFrom", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "ObjectHasValue", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "DataHasValue", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "ObjectHasSelf", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "ObjectUnionOf", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "ObjectMaxCardinality", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "ObjectOneOf", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "DataMinCardinality", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "ObjectMinCardinality", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "DataAllValuesFrom", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "ObjectSomeValuesFrom", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "DataSomeValuesFrom", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "DataMaxCardinality", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "ObjectExactCardinality", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "Class", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "ObjectIntersectionOf", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "DataExactCardinality", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false)
    })
    protected List<JAXBElement<? extends ClassExpression>> rest;

    /**
     * Obtiene el resto del modelo de contenido. 
     * 
     * <p>
     * Ha obtenido esta propiedad que permite capturar todo por el siguiente motivo: 
     * El nombre de campo "Clazz" se está utilizando en dos partes diferentes de un esquema. Consulte: 
     * línea 298 de file:/C:/Users/Iván%20Castilla/git/RaDiOS-MTT/src/main/resources/xsd/owl2.xsd
     * línea 298 de file:/C:/Users/Iván%20Castilla/git/RaDiOS-MTT/src/main/resources/xsd/owl2.xsd
     * <p>
     * Para deshacerse de esta propiedad, aplique una personalización de propiedad a una
     * de las dos declaraciones siguientes para cambiarles de nombre: 
     * Gets the value of the rest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link ObjectComplementOf }{@code >}
     * {@link JAXBElement }{@code <}{@link ObjectAllValuesFrom }{@code >}
     * {@link JAXBElement }{@code <}{@link ObjectHasValue }{@code >}
     * {@link JAXBElement }{@code <}{@link DataHasValue }{@code >}
     * {@link JAXBElement }{@code <}{@link ObjectHasSelf }{@code >}
     * {@link JAXBElement }{@code <}{@link ObjectUnionOf }{@code >}
     * {@link JAXBElement }{@code <}{@link ObjectMaxCardinality }{@code >}
     * {@link JAXBElement }{@code <}{@link ObjectOneOf }{@code >}
     * {@link JAXBElement }{@code <}{@link DataMinCardinality }{@code >}
     * {@link JAXBElement }{@code <}{@link ObjectMinCardinality }{@code >}
     * {@link JAXBElement }{@code <}{@link DataAllValuesFrom }{@code >}
     * {@link JAXBElement }{@code <}{@link ObjectSomeValuesFrom }{@code >}
     * {@link JAXBElement }{@code <}{@link DataSomeValuesFrom }{@code >}
     * {@link JAXBElement }{@code <}{@link DataMaxCardinality }{@code >}
     * {@link JAXBElement }{@code <}{@link ObjectExactCardinality }{@code >}
     * {@link JAXBElement }{@code <}{@link Class }{@code >}
     * {@link JAXBElement }{@code <}{@link ObjectIntersectionOf }{@code >}
     * {@link JAXBElement }{@code <}{@link DataExactCardinality }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends ClassExpression>> getRest() {
        if (rest == null) {
            rest = new ArrayList<JAXBElement<? extends ClassExpression>>();
        }
        return this.rest;
    }

}
