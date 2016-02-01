package com.translations.globallink.connect.sf.model.vendor.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.translations.globallink.connect.sf.model.vendor.dto.SFArticle;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticleField;
import com.translations.globallink.connect.sf.model.vendor.dto.SFConnectionConfig;
import com.translations.globallink.connect.sf.model.vendor.exception.CustomException;
import com.translations.globallink.connect.sf.model.vendor.utility.xml.Content;
import com.translations.globallink.connect.sf.model.vendor.utility.xml.Field;
import com.translations.globallink.connect.sf.model.vendor.utility.xml.GloballinkContentXMLUtil;
import com.translations.globallink.connect.sf.model.vendor.constants.Constants;

import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class Utility {
	/**
	 * This method will be used to get data from middleWare.
	 * 
	 * @return loginDetailBean it rerun SFConnectionConfig data
	 */
	public static SFConnectionConfig getLoginDetailsFromMiddleware() {
		SFConnectionConfig loginDetailBean = new SFConnectionConfig();
		loginDetailBean.setUser("Nakul@ka.dev");
		loginDetailBean.setPassword("test@123ELikr4MyfI1xfgtYZdCyxeTnv");
		loginDetailBean
				.setConsumerKey("3MVG9ZL0ppGP5UrBR.600kPJSKldTpds6SxCgEgj44lSgMJAcU9C5etNsi9y5GusxXFEuSKd3m3oylBiXdNtR");
		loginDetailBean.setConsumerSecret("5127982414795005574");
		loginDetailBean.setUrl("https://ap2.salesforce.com");
		loginDetailBean.setQueueId("00G28000001Ep0TEAS");
		return loginDetailBean;
	}

	/**
	 * Generate In clause for Ids.
	 * 
	 * @param strList
	 *            list of string of ids
	 * @return In clause string
	 * 
	 */
	public static String generateInCluaseString(List<String> strList) {
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
	public static String generateInCluaseStringForSFArticle(
			List<SFArticle> sfarticle) {
		String masterVersionIdStr = "";

		for (SFArticle masterVersionIds : sfarticle) {

			masterVersionIdStr += "'" + masterVersionIds.getMasterVersionId()
					+ "',";
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

		content.getFields().add(
				new Field(sourceArticle.getId(), "Id", "Id", false, 18, "Id",
						true));

		content.getFields().add(
				new Field(sourceArticle.getLanguage(), "Language", "Language",
						false, 18, "Text", true));

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
		// fields.add(new SFArticleField("Id", "Id", "Id", 36, false));
		fields.add(new SFArticleField("KnowledgeArticleId",
				"KnowledgeArticleId", "Id", 20, false));
		fields.add(new SFArticleField("CreatedById", "CreatedById", "Id", 20,
				false));
		fields.add(new SFArticleField("MasterVersionId", "MasterVersionId",
				"Id", 20, false));

		return fields;

	}

	// Dummy code
	/**
	 * hard code data insertion in the SFArticleField-custum fileds
	 * 
	 * @return
	 */
	public static List<SFArticleField> getSFArticleCustomFieldList() {
		List<SFArticleField> fields = new ArrayList<SFArticleField>();
		fields.add(new SFArticleField("Name__c", "name", "Text", 256, true));
		fields.add(new SFArticleField("Summary", "Summary", "Text", 1026, true));
		fields.add(new SFArticleField("Title", "Title", "Text", 256, true));
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

	/**
	 * 
	 * @param baseURL
	 *            crate Httpget method
	 * @param queryStr
	 *            query that pass in url
	 * @param accessToken
	 *            to add header using sdUtility
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws CustomException
	 */
	public static String getHttpGetResponce(String baseURL, String queryStr,
			String accessToken) throws IOException, CustomException {
		URL url;
		HttpsURLConnection connection = null;

		url = new URL(baseURL + "/" + Constants.REST_URL + queryStr);
		connection = (HttpsURLConnection) url.openConnection();
		connection.setRequestProperty("Authorization", "OAuth " + accessToken);
		connection.setRequestProperty("accept", Constants.ACCEPT_STRING);
		connection.setRequestMethod("GET");

		connection.setRequestProperty("Content-Type",
				Constants.CONTENT_TYPE_VAL);
		connection.setRequestProperty("Content-Language", "en-US");
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);

		if (connection.getResponseCode() == Constants.SUCCESS_CODE) {
			System.out.println("responcecode" + connection.getResponseCode());

			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();
		} else {
			throw new CustomException(
					"Issue while fetching targetObjectInstance Found:");
		}

	}

	public static String getHttpPostResponce(SFConnectionConfig loginDetailBean)
			throws IOException {
		URL url;
		HttpsURLConnection connection = null;
		String urlParameters = "grant_type=password&client_id="
				+ loginDetailBean.getConsumerKey() + "&client_secret="
				+ loginDetailBean.getConsumerSecret() + "&username="
				+ loginDetailBean.getUser() + "&password="
				+ loginDetailBean.getPassword();

		url = new URL(Constants.POST_LOGIN_URL);
		connection = (HttpsURLConnection) url.openConnection();
		connection.setRequestProperty("accept", "application/json");
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Length",
				"" + Integer.toString(urlParameters.getBytes().length));
		connection.setRequestProperty("Content-Type",
				Constants.CONTENT_TYPE_VAL);
		connection.setRequestProperty("Content-Language", "en-US");
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		OutputStream os = connection.getOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,
				"UTF-8"));
		writer.write(urlParameters);
		writer.flush();
		writer.close();
		os.close();

		InputStream is = connection.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		String line;
		StringBuffer response = new StringBuffer();
		while ((line = rd.readLine()) != null) {
			response.append(line);
			response.append('\r');
		}
		rd.close();
		return response.toString();

	}

	public static void getHttpPatchResponce(String baseURL, String queryStr,
			String accessToken, String body) throws IOException, IOException,
			CustomException {

		URL url;
		HttpsURLConnection connection = null;
		url = new URL(baseURL + Constants.REST_URL + queryStr);
		connection = (HttpsURLConnection) url.openConnection();
		connection.setRequestProperty("Authorization", "OAuth " + accessToken);
		setRequestMethodUsingWorkaround(connection, "PATCH");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		OutputStream os = connection.getOutputStream();
		os.write(body.getBytes("UTF-8"));
		os.flush();
		os.close();

		InputStream is = connection.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		String line;
		StringBuffer response = new StringBuffer();
		while ((line = rd.readLine()) != null) {
			response.append(line);
			response.append('\r');
		}
		rd.close();
		// return response.toString();
		if (connection.getResponseCode() != 204) {
			System.out.println("responce code  " + connection.getResponseCode()
					+ "  " + connection.getResponseMessage() + " Message :"
					+ response.toString());
			throw new CustomException("Error while update knowledge article Id");

		} else {
			System.out.println("record Inserted successfully!!!!");
			System.out.println("responce code  " + connection.getResponseCode()
					+ "  " + connection.getResponseMessage() + " Message :"
					+ response.toString());
		}

	}

	private static final void setRequestMethodUsingWorkaround(
			final HttpURLConnection httpURLConnection, final String method) {
		try {
			httpURLConnection.setRequestMethod(method);
			// Check whether we are running on a buggy JRE
		} catch (final ProtocolException pe) {
			Class<?> connectionClass = httpURLConnection.getClass();
			java.lang.reflect.Field delegateField = null;
			try {
				delegateField = connectionClass.getDeclaredField("delegate");
				delegateField.setAccessible(true);
				HttpURLConnection delegateConnection = (HttpURLConnection) delegateField
						.get(httpURLConnection);
				setRequestMethodUsingWorkaround(delegateConnection, method);
			} catch (NoSuchFieldException e) {
				// Ignore for now, keep going
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			try {
				java.lang.reflect.Field methodField;
				while (connectionClass != null) {
					try {
						methodField = connectionClass
								.getDeclaredField("method");
					} catch (NoSuchFieldException e) {
						connectionClass = connectionClass.getSuperclass();
						continue;
					}
					methodField.setAccessible(true);
					methodField.set(httpURLConnection, method);
					break;
				}
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

//	public static String getSessionId(String loingResponse) {
//		java.io.InputStream sbis = new java.io.StringBufferInputStream(
//				loingResponse.toString());
//		javax.xml.parsers.DocumentBuilderFactory b = javax.xml.parsers.DocumentBuilderFactory
//				.newInstance();
//		b.setNamespaceAware(false);
//		org.w3c.dom.Document doc = null;
//		javax.xml.parsers.DocumentBuilder db = null;
//		try {
//			db = b.newDocumentBuilder();
//			doc = db.parse(sbis);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		org.w3c.dom.Element element = doc.getDocumentElement();
//		String access_token = "";
//		NodeList nodeList = element.getElementsByTagName("access_token");
//		if (nodeList != null && nodeList.getLength() > 0) {
//			Element myElement = (Element) nodeList.item(0);
//			access_token = myElement.getFirstChild().getNodeValue();
//		}
//		return access_token;
//	}

}
