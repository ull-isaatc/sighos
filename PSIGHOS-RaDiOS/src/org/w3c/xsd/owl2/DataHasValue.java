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
 * <p>Clase Java para DataHasValue complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DataHasValue"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.w3.org/2002/07/owl#}ClassExpression"&gt;
 *       &lt;sequence&gt;
 *         &lt;group ref="{http://www.w3.org/2002/07/owl#}DataPropertyExpression"/&gt;
 *         &lt;element ref="{http://www.w3.org/2002/07/owl#}Literal"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataHasValue", propOrder = {
    "dataProperty",
    "literal"
})
public class DataHasValue
    extends ClassExpression
{

    @XmlElement(name = "DataProperty", required = true)
    protected DataProperty dataProperty;
    @XmlElement(name = "Literal", required = true)
    protected Literal literal;

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

}
