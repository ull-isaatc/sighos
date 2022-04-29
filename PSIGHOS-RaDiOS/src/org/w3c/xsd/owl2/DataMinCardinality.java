//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2022.04.26 a las 05:42:17 PM BST 
//


package org.w3c.xsd.owl2;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para DataMinCardinality complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DataMinCardinality"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.w3.org/2002/07/owl#}ClassExpression"&gt;
 *       &lt;sequence&gt;
 *         &lt;group ref="{http://www.w3.org/2002/07/owl#}DataPropertyExpression"/&gt;
 *         &lt;group ref="{http://www.w3.org/2002/07/owl#}DataRange" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="cardinality" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataMinCardinality", propOrder = {
    "dataProperty",
    "datatype",
    "dataIntersectionOf",
    "dataUnionOf",
    "dataComplementOf",
    "dataOneOf",
    "datatypeRestriction"
})
public class DataMinCardinality
    extends ClassExpression
{

    @XmlElement(name = "DataProperty", required = true)
    protected DataProperty dataProperty;
    @XmlElement(name = "Datatype")
    protected Datatype datatype;
    @XmlElement(name = "DataIntersectionOf")
    protected DataIntersectionOf dataIntersectionOf;
    @XmlElement(name = "DataUnionOf")
    protected DataUnionOf dataUnionOf;
    @XmlElement(name = "DataComplementOf")
    protected DataComplementOf dataComplementOf;
    @XmlElement(name = "DataOneOf")
    protected DataOneOf dataOneOf;
    @XmlElement(name = "DatatypeRestriction")
    protected DatatypeRestriction datatypeRestriction;
    @XmlAttribute(name = "cardinality", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger cardinality;

    /**
     * Obtiene el valor de la propiedad dataProperty.
     * 
     * @return
     *     possible object is
     *     {@link DataProperty }
     *     
     */
    public DataProperty getDataProperty() {
        return dataProperty;
    }

    /**
     * Define el valor de la propiedad dataProperty.
     * 
     * @param value
     *     allowed object is
     *     {@link DataProperty }
     *     
     */
    public void setDataProperty(DataProperty value) {
        this.dataProperty = value;
    }

    /**
     * Obtiene el valor de la propiedad datatype.
     * 
     * @return
     *     possible object is
     *     {@link Datatype }
     *     
     */
    public Datatype getDatatype() {
        return datatype;
    }

    /**
     * Define el valor de la propiedad datatype.
     * 
     * @param value
     *     allowed object is
     *     {@link Datatype }
     *     
     */
    public void setDatatype(Datatype value) {
        this.datatype = value;
    }

    /**
     * Obtiene el valor de la propiedad dataIntersectionOf.
     * 
     * @return
     *     possible object is
     *     {@link DataIntersectionOf }
     *     
     */
    public DataIntersectionOf getDataIntersectionOf() {
        return dataIntersectionOf;
    }

    /**
     * Define el valor de la propiedad dataIntersectionOf.
     * 
     * @param value
     *     allowed object is
     *     {@link DataIntersectionOf }
     *     
     */
    public void setDataIntersectionOf(DataIntersectionOf value) {
        this.dataIntersectionOf = value;
    }

    /**
     * Obtiene el valor de la propiedad dataUnionOf.
     * 
     * @return
     *     possible object is
     *     {@link DataUnionOf }
     *     
     */
    public DataUnionOf getDataUnionOf() {
        return dataUnionOf;
    }

    /**
     * Define el valor de la propiedad dataUnionOf.
     * 
     * @param value
     *     allowed object is
     *     {@link DataUnionOf }
     *     
     */
    public void setDataUnionOf(DataUnionOf value) {
        this.dataUnionOf = value;
    }

    /**
     * Obtiene el valor de la propiedad dataComplementOf.
     * 
     * @return
     *     possible object is
     *     {@link DataComplementOf }
     *     
     */
    public DataComplementOf getDataComplementOf() {
        return dataComplementOf;
    }

    /**
     * Define el valor de la propiedad dataComplementOf.
     * 
     * @param value
     *     allowed object is
     *     {@link DataComplementOf }
     *     
     */
    public void setDataComplementOf(DataComplementOf value) {
        this.dataComplementOf = value;
    }

    /**
     * Obtiene el valor de la propiedad dataOneOf.
     * 
     * @return
     *     possible object is
     *     {@link DataOneOf }
     *     
     */
    public DataOneOf getDataOneOf() {
        return dataOneOf;
    }

    /**
     * Define el valor de la propiedad dataOneOf.
     * 
     * @param value
     *     allowed object is
     *     {@link DataOneOf }
     *     
     */
    public void setDataOneOf(DataOneOf value) {
        this.dataOneOf = value;
    }

    /**
     * Obtiene el valor de la propiedad datatypeRestriction.
     * 
     * @return
     *     possible object is
     *     {@link DatatypeRestriction }
     *     
     */
    public DatatypeRestriction getDatatypeRestriction() {
        return datatypeRestriction;
    }

    /**
     * Define el valor de la propiedad datatypeRestriction.
     * 
     * @param value
     *     allowed object is
     *     {@link DatatypeRestriction }
     *     
     */
    public void setDatatypeRestriction(DatatypeRestriction value) {
        this.datatypeRestriction = value;
    }

    /**
     * Obtiene el valor de la propiedad cardinality.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCardinality() {
        return cardinality;
    }

    /**
     * Define el valor de la propiedad cardinality.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCardinality(BigInteger value) {
        this.cardinality = value;
    }

}
