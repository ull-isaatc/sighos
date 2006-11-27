//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.11.23 at 01:07:00 PM GMT 
//


package es.ull.isaatc.simulation.xml;

import javax.xml.bind.annotation.XmlEnum;
import es.ull.isaatc.simulation.xml.Distribution;


/**
 * <p>Java class for Distribution.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Distribution">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="BETA"/>
 *     &lt;enumeration value="CHISQUARE"/>
 *     &lt;enumeration value="ERLANG"/>
 *     &lt;enumeration value="EXPONENTIAL"/>
 *     &lt;enumeration value="FIXED"/>
 *     &lt;enumeration value="GEOMETRIC"/>
 *     &lt;enumeration value="NORMAL"/>
 *     &lt;enumeration value="POISSON"/>
 *     &lt;enumeration value="TRIANGULAR"/>
 *     &lt;enumeration value="UNIFORM"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlEnum
public enum Distribution {

    BETA,
    CHISQUARE,
    ERLANG,
    EXPONENTIAL,
    FIXED,
    GEOMETRIC,
    NORMAL,
    POISSON,
    TRIANGULAR,
    UNIFORM;

    public String value() {
        return name();
    }

    public Distribution fromValue(String v) {
        return valueOf(v);
    }

}
