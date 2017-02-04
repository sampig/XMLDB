//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.02.03 at 05:37:44 PM CET 
//


package org.zhuzhu.xml.jaxb.calexit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}name" maxOccurs="unbounded"/>
 *         &lt;element ref="{}islands" minOccurs="0"/>
 *         &lt;element ref="{}located" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}area" minOccurs="0"/>
 *         &lt;group ref="{}coordinates"/>
 *         &lt;element ref="{}elevation" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{}id-and-countries"/>
 *       &lt;attribute name="sea" type="{http://www.w3.org/2001/XMLSchema}IDREFS" />
 *       &lt;attribute name="lake" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *       &lt;attribute name="river" type="{http://www.w3.org/2001/XMLSchema}IDREFS" />
 *       &lt;attribute name="type">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="volcanic"/>
 *             &lt;enumeration value="coral"/>
 *             &lt;enumeration value="atoll"/>
 *             &lt;enumeration value="lime"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "islands",
    "located",
    "area",
    "latitude",
    "longitude",
    "elevation"
})
@XmlRootElement(name = "island")
public class Island {

    @XmlElement(required = true)
    protected List<String> name;
    protected String islands;
    protected List<Located> located;
    protected BigDecimal area;
    protected BigDecimal latitude;
    protected BigDecimal longitude;
    protected BigDecimal elevation;
    @XmlAttribute(name = "sea")
    @XmlIDREF
    @XmlSchemaType(name = "IDREFS")
    protected List<Object> sea;
    @XmlAttribute(name = "lake")
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object lake;
    @XmlAttribute(name = "river")
    @XmlIDREF
    @XmlSchemaType(name = "IDREFS")
    protected List<Object> river;
    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "country", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREFS")
    protected List<Object> country;

    /**
     * Gets the value of the name property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the name property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getName() {
        if (name == null) {
            name = new ArrayList<String>();
        }
        return this.name;
    }

    /**
     * Gets the value of the islands property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIslands() {
        return islands;
    }

    /**
     * Sets the value of the islands property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIslands(String value) {
        this.islands = value;
    }

    /**
     * Gets the value of the located property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the located property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLocated().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Located }
     * 
     * 
     */
    public List<Located> getLocated() {
        if (located == null) {
            located = new ArrayList<Located>();
        }
        return this.located;
    }

    /**
     * Gets the value of the area property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getArea() {
        return area;
    }

    /**
     * Sets the value of the area property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setArea(BigDecimal value) {
        this.area = value;
    }

    /**
     * Gets the value of the latitude property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLatitude() {
        return latitude;
    }

    /**
     * Sets the value of the latitude property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLatitude(BigDecimal value) {
        this.latitude = value;
    }

    /**
     * Gets the value of the longitude property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLongitude() {
        return longitude;
    }

    /**
     * Sets the value of the longitude property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLongitude(BigDecimal value) {
        this.longitude = value;
    }

    /**
     * Gets the value of the elevation property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getElevation() {
        return elevation;
    }

    /**
     * Sets the value of the elevation property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setElevation(BigDecimal value) {
        this.elevation = value;
    }

    /**
     * Gets the value of the sea property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sea property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSea().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getSea() {
        if (sea == null) {
            sea = new ArrayList<Object>();
        }
        return this.sea;
    }

    /**
     * Gets the value of the lake property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getLake() {
        return lake;
    }

    /**
     * Sets the value of the lake property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setLake(Object value) {
        this.lake = value;
    }

    /**
     * Gets the value of the river property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the river property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRiver().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getRiver() {
        if (river == null) {
            river = new ArrayList<Object>();
        }
        return this.river;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the id property.
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
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the country property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the country property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCountry().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getCountry() {
        if (country == null) {
            country = new ArrayList<Object>();
        }
        return this.country;
    }

}
