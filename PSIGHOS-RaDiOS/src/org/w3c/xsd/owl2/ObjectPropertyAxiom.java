//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2022.04.26 a las 05:42:17 PM BST 
//


package org.w3c.xsd.owl2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ObjectPropertyAxiom complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ObjectPropertyAxiom"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.w3.org/2002/07/owl#}Axiom"&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjectPropertyAxiom")
@XmlSeeAlso({
    SubObjectPropertyOf.class,
    EquivalentObjectProperties.class,
    DisjointObjectProperties.class,
    ObjectPropertyDomain.class,
    ObjectPropertyRange.class,
    InverseObjectProperties.class,
    FunctionalObjectProperty.class,
    InverseFunctionalObjectProperty.class,
    ReflexiveObjectProperty.class,
    IrreflexiveObjectProperty.class,
    SymmetricObjectProperty.class,
    AsymmetricObjectProperty.class,
    TransitiveObjectProperty.class
})
public abstract class ObjectPropertyAxiom
    extends Axiom
{


}
