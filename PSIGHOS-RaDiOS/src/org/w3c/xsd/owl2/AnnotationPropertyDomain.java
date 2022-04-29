//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2022.04.26 a las 05:42:17 PM BST 
//


package org.w3c.xsd.owl2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para AnnotationPropertyDomain complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="AnnotationPropertyDomain"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.w3.org/2002/07/owl#}AnnotationAxiom"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://www.w3.org/2002/07/owl#}AnnotationProperty"/&gt;
 *         &lt;element ref="{http://www.w3.org/2002/07/owl#}IRI"/&gt;
 *         &lt;element ref="{http://www.w3.org/2002/07/owl#}AbbreviatedIRI"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AnnotationPropertyDomain", propOrder = {
    "annotationProperty",
    "iri",
    "abbreviatedIRI"
})
public class AnnotationPropertyDomain
    extends AnnotationAxiom
{

    @XmlElement(name = "AnnotationProperty", required = true)
    protected AnnotationProperty annotationProperty;
    @XmlElement(name = "IRI", required = true)
    protected IRI iri;
    @XmlElement(name = "AbbreviatedIRI", required = true)
    protected AbbreviatedIRI abbreviatedIRI;

    /**
     * Obtiene el valor de la propiedad annotationProperty.
     * 
     * @return
     *     possible object is
     *     {@link AnnotationProperty }
     *     
     */
    public AnnotationProperty getAnnotationProperty() {
        return annotationProperty;
    }

    /**
     * Define el valor de la propiedad annotationProperty.
     * 
     * @param value
     *     allowed object is
     *     {@link AnnotationProperty }
     *     
     */
    public void setAnnotationProperty(AnnotationProperty value) {
        this.annotationProperty = value;
    }

    /**
     * Obtiene el valor de la propiedad iri.
     * 
     * @return
     *     possible object is
     *     {@link IRI }
     *     
     */
    public IRI getIRI() {
        return iri;
    }

    /**
     * Define el valor de la propiedad iri.
     * 
     * @param value
     *     allowed object is
     *     {@link IRI }
     *     
     */
    public void setIRI(IRI value) {
        this.iri = value;
    }

    /**
     * Obtiene el valor de la propiedad abbreviatedIRI.
     * 
     * @return
     *     possible object is
     *     {@link AbbreviatedIRI }
     *     
     */
    public AbbreviatedIRI getAbbreviatedIRI() {
        return abbreviatedIRI;
    }

    /**
     * Define el valor de la propiedad abbreviatedIRI.
     * 
     * @param value
     *     allowed object is
     *     {@link AbbreviatedIRI }
     *     
     */
    public void setAbbreviatedIRI(AbbreviatedIRI value) {
        this.abbreviatedIRI = value;
    }

}
