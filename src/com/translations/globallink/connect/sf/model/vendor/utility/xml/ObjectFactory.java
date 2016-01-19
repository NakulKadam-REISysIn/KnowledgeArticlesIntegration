//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2015.07.30 at 03:44:20 PM PDT
//

package com.translations.globallink.connect.sf.model.vendor.utility.xml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java element interface generated in the com.translations.globallink.connect.core.model.xml package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content. The Java representation of XML content can consist of schema derived interfaces and classes representing the binding of
 * schema type definitions, element declarations and model groups. Factory methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Content_QNAME = new QName("", "content");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.translations.globallink.connect.core.model.xml
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Content }
     * 
     */
    public Content createContent(String connectType, String sourceType, String sourceName, String sourceId) {
	return new Content(connectType, sourceType, sourceName, sourceId);
    }

    /**
     * Create an instance of {@link Field }
     * 
     */
    public Field createField(String id, String value, String name, Integer maxLength, boolean translate) {
	return new Field(id, value, name, maxLength, translate);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Content } {@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "content")
    public JAXBElement<Content> createContent(Content value) {
	return new JAXBElement<Content>(_Content_QNAME, Content.class, null, value);
    }

}
