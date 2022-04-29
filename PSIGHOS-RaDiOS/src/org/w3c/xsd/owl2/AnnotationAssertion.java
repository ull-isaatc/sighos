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
 * <p>Clase Java para AnnotationAssertion complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="AnnotationAssertion"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.w3.org/2002/07/owl#}AnnotationAxiom"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://www.w3.org/2002/07/owl#}AnnotationProperty"/&gt;
 *         &lt;group ref="{http://www.w3.org/2002/07/owl#}AnnotationSubject"/&gt;
 *         &lt;group ref="{http://www.w3.org/2002/07/owl#}AnnotationValue"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AnnotationAssertion", propOrder = {
    "rest"
})
public class AnnotationAssertion
    extends AnnotationAxiom
{

    @XmlElementRefs({
        @XmlElementRef(name = "Literal", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "AbbreviatedIRI", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "AnnotationProperty", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "IRI", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "AnonymousIndividual", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false)
    })
    protected List<JAXBElement<?>> rest;

    /**
     * Obtiene el resto del modelo de contenido. 
     * 
     * <p>
     * Ha obtenido esta propiedad que permite capturar todo por el siguiente motivo: 
     * El nombre de campo "IRI" se está utilizando en dos partes diferentes de un esquema. Consulte: 
     * línea 1053 de file:/C:/Users/Iván%20Castilla/git/RaDiOS-MTT/src/main/resources/xsd/owl2.xsd
     * línea 1045 de file:/C:/Users/Iván%20Castilla/git/RaDiOS-MTT/src/main/resources/xsd/owl2.xsd
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
     * {@link JAXBElement }{@code <}{@link Literal }{@code >}
     * {@link JAXBElement }{@code <}{@link AnnotationProperty }{@code >}
     * {@link JAXBElement }{@code <}{@link IRI }{@code >}
     * {@link JAXBElement }{@code <}{@link AnonymousIndividual }{@code >}
     * {@link JAXBElement }{@code <}{@link AbbreviatedIRI }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getRest() {
        if (rest == null) {
            rest = new ArrayList<JAXBElement<?>>();
        }
        return this.rest;
    }

}
