//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.02.03 at 05:37:44 PM CET 
//


package org.zhuzhu.xml.jaxb.calexit;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute ref="{}country use="required""/>
 *       &lt;attribute name="province" type="{http://www.w3.org/2001/XMLSchema}IDREFS" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "located")
public class Located {

    @XmlAttribute(name = "country", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object country;
    @XmlAttribute(name = "province")
    @XmlIDREF
    @XmlSchemaType(name = "IDREFS")
    protected List<Object> province;

    /**
     * Gets the value of the country property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setCountry(Object value) {
        this.country = value;
    }

    /**
     * Gets the value of the province property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the province property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProvince().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getProvince() {
        if (province == null) {
            province = new ArrayList<Object>();
        }
        return this.province;
    }

}
