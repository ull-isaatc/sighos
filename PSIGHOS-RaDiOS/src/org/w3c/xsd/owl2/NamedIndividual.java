//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2022.04.26 a las 05:42:17 PM BST 
//


package org.w3c.xsd.owl2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para NamedIndividual complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="NamedIndividual"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.w3.org/2002/07/owl#}Individual"&gt;
 *       &lt;attribute name="IRI" type="{http://www.w3.org/2001/XMLSchema}anyURI" /&gt;
 *       &lt;attribute name="abbreviatedIRI" type="{http://www.w3.org/2002/07/owl#}abbreviatedIRI" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NamedIndividual")
public class NamedIndividual
    extends Individual
{

    @XmlAttribute(name = "IRI")
    @XmlSchemaType(name = "anyURI")
    protected String iri;
    @XmlAttribute(name = "abbreviatedIRI")
    protected String abbreviatedIRI;

    /**
     * Obtiene el valor de la propiedad iri.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIRI() {
        return iri;
    }

    /**
     * Define el valor de la propiedad iri.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIRI(String value) {
        this.iri = value;
    }

    /**
     * Obtiene el valor de la propiedad abbreviatedIRI.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAbbreviatedIRI() {
        return abbreviatedIRI;
    }

    /**
     * Define el valor de la propiedad abbreviatedIRI.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAbbreviatedIRI(String value) {
        this.abbreviatedIRI = value;
    }

}
