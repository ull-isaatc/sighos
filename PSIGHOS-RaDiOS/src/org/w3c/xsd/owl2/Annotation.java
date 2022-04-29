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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Clase Java para Annotation complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="Annotation"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;group ref="{http://www.w3.org/2002/07/owl#}annotationAnnotations"/&gt;
 *         &lt;element ref="{http://www.w3.org/2002/07/owl#}AnnotationProperty"/&gt;
 *         &lt;group ref="{http://www.w3.org/2002/07/owl#}AnnotationValue"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attGroup ref="{http://www.w3.org/XML/1998/namespace}specialAttrs"/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Annotation", propOrder = {
    "annotation",
    "annotationProperty",
    "iri",
    "abbreviatedIRI",
    "anonymousIndividual",
    "literal"
})
public class Annotation {

    @XmlElement(name = "Annotation")
    protected List<Annotation> annotation;
    @XmlElement(name = "AnnotationProperty", required = true)
    protected AnnotationProperty annotationProperty;
    @XmlElement(name = "IRI")
    protected IRI iri;
    @XmlElement(name = "AbbreviatedIRI")
    protected AbbreviatedIRI abbreviatedIRI;
    @XmlElement(name = "AnonymousIndividual")
    protected AnonymousIndividual anonymousIndividual;
    @XmlElement(name = "Literal")
    protected Literal literal;
    @XmlAttribute(name = "base", namespace = "http://www.w3.org/XML/1998/namespace")
    @XmlSchemaType(name = "anyURI")
    protected String base;
    @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace")
    protected String lang;
    @XmlAttribute(name = "space", namespace = "http://www.w3.org/XML/1998/namespace")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String space;
    @XmlAttribute(name = "id", namespace = "http://www.w3.org/XML/1998/namespace")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Gets the value of the annotation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the annotation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnnotation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Annotation }
     * 
     * 
     */
    public List<Annotation> getAnnotation() {
        if (annotation == null) {
            annotation = new ArrayList<Annotation>();
        }
        return this.annotation;
    }

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

    /**
     * Obtiene el valor de la propiedad anonymousIndividual.
     * 
     * @return
     *     possible object is
     *     {@link AnonymousIndividual }
     *     
     */
    public AnonymousIndividual getAnonymousIndividual() {
        return anonymousIndividual;
    }

    /**
     * Define el valor de la propiedad anonymousIndividual.
     * 
     * @param value
     *     allowed object is
     *     {@link AnonymousIndividual }
     *     
     */
    public void setAnonymousIndividual(AnonymousIndividual value) {
        this.anonymousIndividual = value;
    }

    /**
     * Obtiene el valor de la propiedad literal.
     * 
     * @return
     *     possible object is
     *     {@link Literal }
     *     
     */
    public Literal getLiteral() {
        return literal;
    }

    /**
     * Define el valor de la propiedad literal.
     * 
     * @param value
     *     allowed object is
     *     {@link Literal }
     *     
     */
    public void setLiteral(Literal value) {
        this.literal = value;
    }

    /**
     * Obtiene el valor de la propiedad base.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBase() {
        return base;
    }

    /**
     * Define el valor de la propiedad base.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBase(String value) {
        this.base = value;
    }

    /**
     * Obtiene el valor de la propiedad lang.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLang() {
        return lang;
    }

    /**
     * Define el valor de la propiedad lang.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLang(String value) {
        this.lang = value;
    }

    /**
     * Obtiene el valor de la propiedad space.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpace() {
        return space;
    }

    /**
     * Define el valor de la propiedad space.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpace(String value) {
        this.space = value;
    }

    /**
     * Obtiene el valor de la propiedad id.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Define el valor de la propiedad id.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}
