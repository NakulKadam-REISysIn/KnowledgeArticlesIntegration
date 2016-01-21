package com.translations.globallink.connect.sf.model.vendor.utility;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.translations.globallink.connect.sf.model.vendor.dto.SFArticle;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticleField;
import com.translations.globallink.connect.sf.model.vendor.dto.SFConnectionConfig;
import com.translations.globallink.connect.sf.model.vendor.utility.xml.Content;
import com.translations.globallink.connect.sf.model.vendor.utility.xml.Field;
import com.translations.globallink.connect.sf.model.vendor.utility.xml.GloballinkContentXMLUtil;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONObject;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class Utility {
	public static SFConnectionConfig getLoginDetailsFromMiddleware() {
		SFConnectionConfig loginDetailBean = new SFConnectionConfig();
		loginDetailBean.setUser("Nakul@ka.dev");
		loginDetailBean.setPassword("test@123ELikr4MyfI1xfgtYZdCyxeTnv");
		loginDetailBean
				.setConsumerKey("3MVG9ZL0ppGP5UrBR.600kPJSKldTpds6SxCgEgj44lSgMJAcU9C5etNsi9y5GusxXFEuSKd3m3oylBiXdNtR");
		loginDetailBean.setConsumerSecret("5127982414795005574");
		loginDetailBean.setUrl("https://ap2.salesforce.com");
		loginDetailBean.setQueueName("UserQueue");
		return loginDetailBean;
	}

	public static String generateInCluaseString(List<String> strList) {
		String returnStr = "";
		for (String str : strList) {
			returnStr += "'" + str + "',";
		}
		return returnStr.substring(0, returnStr.length() - 1);
	}

	public static String generateInCluaseStringForSFArticle(
			List<SFArticle> sfarticle) {
		String masterVersionIdStr = "";

		for (SFArticle masterVersionIds : sfarticle) {

			masterVersionIdStr += "'" + masterVersionIds.getMasterVersionId()
					+ "',";
		}
		return masterVersionIdStr.substring(0, masterVersionIdStr.length() - 1);
	}

	public static StringEntity GenerateJsonBody() throws JSONException,
			IOException {

		JSONObject Article = new JSONObject();
		Article.put("Title", "ffgggh");
		Article.put("Name__c", "john cena");
		Article.put("UrlName", "johncena123");
		StringEntity body = new StringEntity(Article.toString(1));
		body.setContentType("application/json");
		return body;

	}

	public static String getFieldsStr(List<SFArticleField> fields) {
		String fieldsStr = "";
		for (SFArticleField str : fields) {
			fieldsStr += str.getName() + "+,+";
		}
		return fieldsStr.substring(0, fieldsStr.length() - 3);
	}

	public static InputStream generateXMLfromJSON(SFArticle sourceArticle,
			List<SFArticleField> customFields,
			List<SFArticleField> metadataField, JSONObject KAListjson)
			throws Exception {

		if (KAListjson.getJSONArray("records").length() == 0)
			return null;

		JSONObject jsonObject = KAListjson.getJSONArray("records")
				.getJSONObject(0);

		Content content = new Content("SF", sourceArticle.getType(),
				jsonObject.getString("Title"),
				sourceArticle.getMasterVersionId());

		for (SFArticleField sfArticle : metadataField) {
			content.getFields().add(
					new Field(jsonObject.getString(sfArticle.getName()),
							sfArticle.getName(), sfArticle.getLabel(),
							sfArticle.isTransalate(), sfArticle.getLength(),
							sfArticle.getType(), true));
		}
		for (SFArticleField sfArticle : customFields) {
			content.getFields().add(
					new Field(jsonObject.getString(sfArticle.getName()),
							sfArticle.getName(), sfArticle.getLabel(),
							sfArticle.isTransalate(), sfArticle.getLength(),
							sfArticle.getType(), false));
		}
		InputStream inputStream = new ByteArrayInputStream(
				new GloballinkContentXMLUtil()
						.getContentBytesForStream(content));
		return inputStream;
	}

	public static List<SFArticleField> getSFArticleMetadataFieldList() {
		List<SFArticleField> fields = new ArrayList<SFArticleField>();

		SFArticleField name2 = new SFArticleField();
		name2.setLabel("Id");
		name2.setLength(36);
		name2.setName("Id");
		name2.setType("Id");
		name2.setTransalate(false);

		SFArticleField name3 = new SFArticleField();
		name3.setLabel("Title");
		name3.setLength(256);
		name3.setName("Title");
		name3.setType("Text");
		name3.setTransalate(false);

		SFArticleField name6 = new SFArticleField();
		name6.setLabel("KnowledgeArticleId");
		name6.setLength(20);
		name6.setName("KnowledgeArticleId");
		name6.setType("Id");
		name6.setTransalate(false);

		SFArticleField name7 = new SFArticleField();
		name7.setLabel("OwnerId");
		name7.setLength(20);
		name7.setName("OwnerId");
		name7.setType("Id");
		name7.setTransalate(false);

		SFArticleField name8 = new SFArticleField();
		name8.setLabel("MasterVersionId");
		name8.setLength(20);
		name8.setName("MasterVersionId");
		name8.setType("Id");
		name8.setTransalate(false);
//
//		SFArticleField name9 = new SFArticleField();
//		name9.setLabel("Language");
//		name9.setLength(20);
//		name9.setName("Language");
//		name9.setType("Picklist");
//		name9.setTransalate(false);

		fields.add(name2);
		fields.add(name3);
		fields.add(name6);
		fields.add(name7);
		fields.add(name8);
		//fields.add(name9);
		return fields;
	}

	// Dummy code
	public static List<SFArticleField> getSFArticleCustomFieldList() {
		List<SFArticleField> fields = new ArrayList<SFArticleField>();
		SFArticleField name1 = new SFArticleField();
		name1.setLabel("name");
		name1.setLength(256);
		name1.setName("Name__c");
		name1.setType("Text");
		name1.setTransalate(true);

		SFArticleField name4 = new SFArticleField();
		name4.setLabel("Summary");
		name4.setLength(1026);
		name4.setName("Summary");
		name4.setType("Text");
		name4.setTransalate(true);

		fields.add(name1);
		fields.add(name4);

		return fields;

	}

	public static JSONObject conevertStreamToJSON(InputStream inputStream)
			throws ParserConfigurationException, SAXException, IOException,
			DOMException, JSONException {
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
