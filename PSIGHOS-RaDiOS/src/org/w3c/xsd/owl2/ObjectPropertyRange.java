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
 * <p>Clase Java para ObjectPropertyRange complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ObjectPropertyRange"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.w3.org/2002/07/owl#}ObjectPropertyAxiom"&gt;
 *       &lt;sequence&gt;
 *         &lt;group ref="{http://www.w3.org/2002/07/owl#}ObjectPropertyExpression"/&gt;
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
@XmlType(name = "ObjectPropertyRange", propOrder = {
    "objectProperty",
    "objectInverseOf",
    "clazz",
    "objectIntersectionOf",
    "objectUnionOf",
    "objectComplementOf",
    "objectOneOf",
    "objectSomeValuesFrom",
    "objectAllValuesFrom",
    "objectHasValue",
    "objectHasSelf",
    "objectMinCardinality",
    "objectMaxCardinality",
    "objectExactCardinality",
    "dataSomeValuesFrom",
    "dataAllValuesFrom",
    "dataHasValue",
    "dataMinCardinality",
    "dataMaxCardinality",
    "dataExactCardinality"
})
public class ObjectPropertyRange
    extends ObjectPropertyAxiom
{

    @XmlElement(name = "ObjectProperty")
    protected ObjectProperty objectProperty;
    @XmlElement(name = "ObjectInverseOf")
    protected ObjectInverseOf objectInverseOf;
    @XmlElement(name = "Class")
    protected Class clazz;
    @XmlElement(name = "ObjectIntersectionOf")
    protected ObjectIntersectionOf objectIntersectionOf;
    @XmlElement(name = "ObjectUnionOf")
    protected ObjectUnionOf objectUnionOf;
    @XmlElement(name = "ObjectComplementOf")
    protected ObjectComplementOf objectComplementOf;
    @XmlElement(name = "ObjectOneOf")
    protected ObjectOneOf objectOneOf;
    @XmlElement(name = "ObjectSomeValuesFrom")
    protected ObjectSomeValuesFrom objectSomeValuesFrom;
    @XmlElement(name = "ObjectAllValuesFrom")
    protected ObjectAllValuesFrom objectAllValuesFrom;
    @XmlElement(name = "ObjectHasValue")
    protected ObjectHasValue objectHasValue;
    @XmlElement(name = "ObjectHasSelf")
    protected ObjectHasSelf objectHasSelf;
    @XmlElement(name = "ObjectMinCardinality")
    protected ObjectMinCardinality objectMinCardinality;
    @XmlElement(name = "ObjectMaxCardinality")
    protected ObjectMaxCardinality objectMaxCardinality;
    @XmlElement(name = "ObjectExactCardinality")
    protected ObjectExactCardinality objectExactCardinality;
    @XmlElement(name = "DataSomeValuesFrom")
    protected DataSomeValuesFrom dataSomeValuesFrom;
    @XmlElement(name = "DataAllValuesFrom")
    protected DataAllValuesFrom dataAllValuesFrom;
    @XmlElement(name = "DataHasValue")
    protected DataHasValue dataHasValue;
    @XmlElement(name = "DataMinCardinality")
    protected DataMinCardinality dataMinCardinality;
    @XmlElement(name = "DataMaxCardinality")
    protected DataMaxCardinality dataMaxCardinality;
    @XmlElement(name = "DataExactCardinality")
    protected DataExactCardinality dataExactCardinality;

    /**
     * Obtiene el valor de la propiedad objectProperty.
     * 
     * @return
     *     possible object is
     *     {@link ObjectProperty }
     *     
     */
    public ObjectProperty getObjectProperty() {
        return objectProperty;
    }

    /**
     * Define el valor de la propiedad objectProperty.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectProperty }
     *     
     */
    public void setObjectProperty(ObjectProperty value) {
        this.objectProperty = value;
    }

    /**
     * Obtiene el valor de la propiedad objectInverseOf.
     * 
     * @return
     *     possible object is
     *     {@link ObjectInverseOf }
     *     
     */
    public ObjectInverseOf getObjectInverseOf() {
        return objectInverseOf;
    }

    /**
     * Define el valor de la propiedad objectInverseOf.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectInverseOf }
     *     
     */
    public void setObjectInverseOf(ObjectInverseOf value) {
        this.objectInverseOf = value;
    }

    /**
     * Obtiene el valor de la propiedad clazz.
     * 
     * @return
     *     possible object is
     *     {@link Class }
     *     
     */
    public Class getClazz() {
        return clazz;
    }

    /**
     * Define el valor de la propiedad clazz.
     * 
     * @param value
     *     allowed object is
     *     {@link Class }
     *     
     */
    public void setClazz(Class value) {
        this.clazz = value;
    }

    /**
     * Obtiene el valor de la propiedad objectIntersectionOf.
     * 
     * @return
     *     possible object is
     *     {@link ObjectIntersectionOf }
     *     
     */
    public ObjectIntersectionOf getObjectIntersectionOf() {
        return objectIntersectionOf;
    }

    /**
     * Define el valor de la propiedad objectIntersectionOf.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectIntersectionOf }
     *     
     */
    public void setObjectIntersectionOf(ObjectIntersectionOf value) {
        this.objectIntersectionOf = value;
    }

    /**
     * Obtiene el valor de la propiedad objectUnionOf.
     * 
     * @return
     *     possible object is
     *     {@link ObjectUnionOf }
     *     
     */
    public ObjectUnionOf getObjectUnionOf() {
        return objectUnionOf;
    }

    /**
     * Define el valor de la propiedad objectUnionOf.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectUnionOf }
     *     
     */
    public void setObjectUnionOf(ObjectUnionOf value) {
        this.objectUnionOf = value;
    }

    /**
     * Obtiene el valor de la propiedad objectComplementOf.
     * 
     * @return
     *     possible object is
     *     {@link ObjectComplementOf }
     *     
     */
    public ObjectComplementOf getObjectComplementOf() {
        return objectComplementOf;
    }

    /**
     * Define el valor de la propiedad objectComplementOf.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectComplementOf }
     *     
     */
    public void setObjectComplementOf(ObjectComplementOf value) {
        this.objectComplementOf = value;
    }

    /**
     * Obtiene el valor de la propiedad objectOneOf.
     * 
     * @return
     *     possible object is
     *     {@link ObjectOneOf }
     *     
     */
    public ObjectOneOf getObjectOneOf() {
        return objectOneOf;
    }

    /**
     * Define el valor de la propiedad objectOneOf.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectOneOf }
     *     
     */
    public void setObjectOneOf(ObjectOneOf value) {
        this.objectOneOf = value;
    }

    /**
     * Obtiene el valor de la propiedad objectSomeValuesFrom.
     * 
     * @return
     *     possible object is
     *     {@link ObjectSomeValuesFrom }
     *     
     */
    public ObjectSomeValuesFrom getObjectSomeValuesFrom() {
        return objectSomeValuesFrom;
    }

    /**
     * Define el valor de la propiedad objectSomeValuesFrom.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectSomeValuesFrom }
     *     
     */
    public void setObjectSomeValuesFrom(ObjectSomeValuesFrom value) {
        this.objectSomeValuesFrom = value;
    }

    /**
     * Obtiene el valor de la propiedad objectAllValuesFrom.
     * 
     * @return
     *     possible object is
     *     {@link ObjectAllValuesFrom }
     *     
     */
    public ObjectAllValuesFrom getObjectAllValuesFrom() {
        return objectAllValuesFrom;
    }

    /**
     * Define el valor de la propiedad objectAllValuesFrom.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectAllValuesFrom }
     *     
     */
    public void setObjectAllValuesFrom(ObjectAllValuesFrom value) {
        this.objectAllValuesFrom = value;
    }

    /**
     * Obtiene el valor de la propiedad objectHasValue.
     * 
     * @return
     *     possible object is
     *     {@link ObjectHasValue }
     *     
     */
    public ObjectHasValue getObjectHasValue() {
        return objectHasValue;
    }

    /**
     * Define el valor de la propiedad objectHasValue.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectHasValue }
     *     
     */
    public void setObjectHasValue(ObjectHasValue value) {
        this.objectHasValue = value;
    }

    /**
     * Obtiene el valor de la propiedad objectHasSelf.
     * 
     * @return
     *     possible object is
     *     {@link ObjectHasSelf }
     *     
     */
    public ObjectHasSelf getObjectHasSelf() {
        return objectHasSelf;
    }

    /**
     * Define el valor de la propiedad objectHasSelf.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectHasSelf }
     *     
     */
    public void setObjectHasSelf(ObjectHasSelf value) {
        this.objectHasSelf = value;
    }

    /**
     * Obtiene el valor de la propiedad objectMinCardinality.
     * 
     * @return
     *     possible object is
     *     {@link ObjectMinCardinality }
     *     
     */
    public ObjectMinCardinality getObjectMinCardinality() {
        return objectMinCardinality;
    }

    /**
     * Define el valor de la propiedad objectMinCardinality.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectMinCardinality }
     *     
     */
    public void setObjectMinCardinality(ObjectMinCardinality value) {
        this.objectMinCardinality = value;
    }

    /**
     * Obtiene el valor de la propiedad objectMaxCardinality.
     * 
     * @return
     *     possible object is
     *     {@link ObjectMaxCardinality }
     *     
     */
    public ObjectMaxCardinality getObjectMaxCardinality() {
        return objectMaxCardinality;
    }

    /**
     * Define el valor de la propiedad objectMaxCardinality.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectMaxCardinality }
     *     
     */
    public void setObjectMaxCardinality(ObjectMaxCardinality value) {
        this.objectMaxCardinality = value;
    }

    /**
     * Obtiene el valor de la propiedad objectExactCardinality.
     * 
     * @return
     *     possible object is
     *     {@link ObjectExactCardinality }
     *     
     */
    public ObjectExactCardinality getObjectExactCardinality() {
        return objectExactCardinality;
    }

    /**
     * Define el valor de la propiedad objectExactCardinality.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectExactCardinality }
     *     
     */
    public void setObjectExactCardinality(ObjectExactCardinality value) {
        this.objectExactCardinality = value;
    }

    /**
     * Obtiene el valor de la propiedad dataSomeValuesFrom.
     * 
     * @return
     *     possible object is
     *     {@link DataSomeValuesFrom }
     *     
     */
    public DataSomeValuesFrom getDataSomeValuesFrom() {
        return dataSomeValuesFrom;
    }

    /**
     * Define el valor de la propiedad dataSomeValuesFrom.
     * 
     * @param value
     *     allowed object is
     *     {@link DataSomeValuesFrom }
     *     
     */
    public void setDataSomeValuesFrom(DataSomeValuesFrom value) {
        this.dataSomeValuesFrom = value;
    }

    /**
     * Obtiene el valor de la propiedad dataAllValuesFrom.
     * 
     * @return
     *     possible object is
     *     {@link DataAllValuesFrom }
     *     
     */
    public DataAllValuesFrom getDataAllValuesFrom() {
        return dataAllValuesFrom;
    }

    /**
     * Define el valor de la propiedad dataAllValuesFrom.
     * 
     * @param value
     *     allowed object is
     *     {@link DataAllValuesFrom }
     *     
     */
    public void setDataAllValuesFrom(DataAllValuesFrom value) {
        this.dataAllValuesFrom = value;
    }

    /**
     * Obtiene el valor de la propiedad dataHasValue.
     * 
     * @return
     *     possible object is
     *     {@link DataHasValue }
     *     
     */
    public DataHasValue getDataHasValue() {
        return dataHasValue;
    }

    /**
     * Define el valor de la propiedad dataHasValue.
     * 
     * @param value
     *     allowed object is
     *     {@link DataHasValue }
     *     
     */
    public void setDataHasValue(DataHasValue value) {
        this.dataHasValue = value;
    }

    /**
     * Obtiene el valor de la propiedad dataMinCardinality.
     * 
     * @return
     *     possible object is
     *     {@link DataMinCardinality }
     *     
     */
    public DataMinCardinality getDataMinCardinality() {
        return dataMinCardinality;
    }

    /**
     * Define el valor de la propiedad dataMinCardinality.
     * 
     * @param value
     *     allowed object is
     *     {@link DataMinCardinality }
     *     
     */
    public void setDataMinCardinality(DataMinCardinality value) {
        this.dataMinCardinality = value;
    }

    /**
     * Obtiene el valor de la propiedad dataMaxCardinality.
     * 
     * @return
     *     possible object is
     *     {@link DataMaxCardinality }
     *     
     */
    public DataMaxCardinality getDataMaxCardinality() {
        return dataMaxCardinality;
    }

    /**
     * Define el valor de la propiedad dataMaxCardinality.
     * 
     * @param value
     *     allowed object is
     *     {@link DataMaxCardinality }
     *     
     */
    public void setDataMaxCardinality(DataMaxCardinality value) {
        this.dataMaxCardinality = value;
    }

    /**
     * Obtiene el valor de la propiedad dataExactCardinality.
     * 
     * @return
     *     possible object is
     *     {@link DataExactCardinality }
     *     
     */
    public DataExactCardinality getDataExactCardinality() {
        return dataExactCardinality;
    }

    /**
     * Define el valor de la propiedad dataExactCardinality.
     * 
     * @param value
     *     allowed object is
     *     {@link DataExactCardinality }
     *     
     */
    public void setDataExactCardinality(DataExactCardinality value) {
        this.dataExactCardinality = value;
    }

}
