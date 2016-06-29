package com.translations.globallink.connect.sf.model.vendor.utility;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.translations.globallink.connect.sf.model.vendor.dto.SFArticle;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticleField;
import com.translations.globallink.connect.sf.model.vendor.utility.xml.Content;
import com.translations.globallink.connect.sf.model.vendor.utility.xml.Field;
import com.translations.globallink.connect.sf.model.vendor.utility.xml.GloballinkContentXMLUtil;

public class Utility {

    /**
     * Generate In clause for Ids.
     * 
     * @param strList
     *            list of string of ids
     * @return In clause string
     * 
     */
    public static String generateInClauseString(List<String> strList) {
	String returnStr = "";
	for (String str : strList) {
	    returnStr += "'" + str + "',";
	}
	return returnStr.substring(0, returnStr.length() - 1);
    }

    /**
     * Generate In clause for SFArticle
     * 
     * @param sfarticle
     *            It is List of SFArticle data type.
     * @return In clause string.
     */
    public static String generateInClauseStringForSFArticle(List<SFArticle> sfarticle) {
	String masterVersionIdStr = "";

	for (SFArticle masterVersionIds : sfarticle) {

	    masterVersionIdStr += "'" + masterVersionIds.getMasterVersionId() + "',";
	}
	return masterVersionIdStr.substring(0, masterVersionIdStr.length() - 1);
    }

    /**
     * Generate In clause for SFArticleField.
     * 
     * @param fields
     *            List of SFArticleField data type.
     * @return In clause string
     */
    public static String getFieldsStr(List<SFArticleField> fields) {
	boolean isTitleIncluded = false;
	for (SFArticleField str : fields) {
	    if (str.getName().equalsIgnoreCase("Title")) {
		isTitleIncluded = true;
	    }
	}

	if (!isTitleIncluded) {
	    SFArticleField field = new SFArticleField("Title", "Title", "string", 255, false);
	    fields.add(field);
	}

	String fieldsStr = "";
	for (SFArticleField str : fields) {
	    fieldsStr += str.getName() + "+,+";
	}

	return fieldsStr.substring(0, fieldsStr.length() - 3);
    }

    /**
     * Generate In clause for Metadata SFArticleField.
     * 
     * @param fields
     *            List of Metadata SFArticleField data type.
     * @return In clause string
     */
    public static String getMetaDataFieldsStr(List<SFArticleField> fields) {

	String fieldsStr = "";
	for (SFArticleField str : fields) {
	    fieldsStr += str.getName() + "+,+";
	}

	return fieldsStr.substring(0, fieldsStr.length() - 3);
    }

    /**
     * Generate XML for given JSON
     * 
     * @param sourceArticle
     *            SFArticle data type fields for data.
     * @param customFields
     *            SFArticleField custom data for xml generation.
     * @param metadataField
     *            SFArticleField meta data for xml generation.
     * @param KAListjson
     *            JSONObject KAListjson for xml generation.
     * @return InputStream of xml data
     * @throws Exception
     */
    public static InputStream generateXMLfromJSON(SFArticle sourceArticle, List<SFArticleField> customFields, List<SFArticleField> metadataField, JSONObject KAListjson) throws Exception {

	if (KAListjson.getJSONArray("records").length() == 0)
	    return null;

	JSONObject jsonObject = KAListjson.getJSONArray("records").getJSONObject(0);

	Content content = new Content("SF", sourceArticle.getType(), jsonObject.getString("Title"), sourceArticle.getMasterVersionId());

	content.getFields().add(new Field(sourceArticle.getId(), "Id", "Id", false, 18, "Id", true));

	content.getFields().add(new Field(sourceArticle.getLanguage(), "Language", "Language", false, 18, "Text", true));

	for (SFArticleField sfArticleField : metadataField) {
	    content.getFields().add(new Field(jsonObject.getString(sfArticleField.getName()), sfArticleField.getName(), sfArticleField.getLabel(), sfArticleField.isTransalate(), sfArticleField.getLength(), sfArticleField.getType(), true));
	}

	boolean titlePresent = false;

	for (SFArticleField sfArticleField : customFields) {
	    if (!jsonObject.isNull(sfArticleField.getName())) {
		if (sfArticleField.getName().equalsIgnoreCase("Title")) {
		    titlePresent = true;
		}
		content.getFields().add(new Field(jsonObject.getString(sfArticleField.getName()), sfArticleField.getName(), sfArticleField.getLabel(), sfArticleField.isTransalate(), sfArticleField.getLength(), sfArticleField.getType(), false));
	    }
	}

	if (!titlePresent) {
	    content.getFields().add(new Field(jsonObject.getString("Title"), "Title", "Title", false, 255, "string", false));
	}

	InputStream inputStream = new ByteArrayInputStream(new GloballinkContentXMLUtil().getContentBytesForStream(content));
	return inputStream;
    }

    public static List<SFArticleField> getSFArticleMetadataFieldList() {
	List<SFArticleField> fields = new ArrayList<SFArticleField>();
	fields.add(new SFArticleField("KnowledgeArticleId", "KnowledgeArticleId", "Id", 20, false));
	fields.add(new SFArticleField("CreatedById", "CreatedById", "Id", 20, false));
	fields.add(new SFArticleField("MasterVersionId", "MasterVersionId", "Id", 20, false));
	return fields;

    }

    /**
     * generate json from xml file
     * 
     * @param inputStream
     *            inputStream of xml file
     * @return JSONObject of converted xml file
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws DOMException
     * @throws JSONException
     */
    public static JSONObject conevertStreamToJSON(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException, DOMException, JSONException {
	JSONObject article = new JSONObject();
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(inputStream);
	doc.getDocumentElement().normalize();
	NodeList nodeList = doc.getElementsByTagName("field");
	for (int index = 0; index < nodeList.getLength(); index++) {
	    Node nNode = nodeList.item(index);
	    Element eElement = (Element) nNode;
	    if (eElement.getAttribute("metadata").contentEquals("false")) {
		article.put(eElement.getAttribute("id"), nNode.getTextContent());
	    }
	}
	return article;
    }

}
