//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2015.07.30 at 03:44:20 PM PDT
//

package com.translations.globallink.connect.sf.model.vendor.utility.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Java class for field complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="field">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="translate" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="maxLength" type="{http://www.w3.org/2001/XMLSchema}int" default="0" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "field", propOrder = { "value" })
public class Field {

	@XmlValue
	@XmlJavaTypeAdapter(XmlCDATA.class)
	protected String value;
	@XmlAttribute(required = true)
	protected String id;
	@XmlAttribute(required = true)
	protected String name;
	@XmlAttribute(required = true)
	protected boolean translate;
	@XmlAttribute
	protected Integer maxLength;
	@XmlAttribute
	protected String type;
	@XmlAttribute
	protected boolean metadata;

	public Field() {
		super();
	}

	public Field(String value, String id, String name, boolean translate, Integer maxLength, String type, boolean metadata) {
		super();
		this.value = value;
		this.id = id;
		this.name = name;
		this.translate = translate;
		this.maxLength = maxLength;
		this.type = type;
		this.metadata = metadata;
	}

	public Field(String id, String value, String name, Integer maxLength, boolean translate) {
		super();
		this.value = value;
		this.id = id;
		this.name = name;
		this.translate = translate;
		this.maxLength = maxLength;
	}

	/**
	 * Gets the value of the value property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value of the value property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Gets the value of the id property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the value of the id property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setId(String value) {
		this.id = value;
	}

	/**
	 * Gets the value of the name property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the value of the name property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setName(String value) {
		this.name = value;
	}

	/**
	 * Gets the value of the translate property.
	 * 
	 */
	public boolean isTranslate() {
		return translate;
	}

	/**
	 * Sets the value of the translate property.
	 * 
	 */
	public void setTranslate(boolean value) {
		this.translate = value;
	}

	/**
	 * Gets the value of the maxLength property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public int getMaxLength() {
		if (maxLength == null) {
			return 0;
		} else {
			return maxLength;
		}
	}

	/**
	 * Sets the value of the maxLength property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setMaxLength(Integer value) {
		this.maxLength = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isMetadata() {
		return metadata;
	}

	public void setMetadata(boolean metadata) {
		this.metadata = metadata;
	}

}
