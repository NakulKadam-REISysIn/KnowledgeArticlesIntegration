package com.translations.globallink.connect.sf.model.vendor.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import com.translations.globallink.connect.sf.model.vendor.constants.Constants;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticle;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticleField;
import com.translations.globallink.connect.sf.model.vendor.dto.SFConnectionConfig;
import com.translations.globallink.connect.sf.model.vendor.exception.CustomException;

/**
 * @class Name-SalesforceUtility
 * 
 *        This class use for the fetching data from SFConnectionConfig class and
 *        then login into salesforce and then get
 *        QueueId,processInstance,TargetObjectId,KnowledgeArticle also insert
 *        the article into Salesforce.
 * 
 * @author-
 * 
 * @createdDate-
 */

public class SFUtility {
	/**
	 * 
	 * @param loginDetailbean
	 *            it is SFConnectionConfig variable that contains values to get
	 *            value from SFConnectionConfig class
	 * 
	 * @return StringBuffer generated data from SFConnectionConfig is get
	 *         return.
	 */

	private static String generateheaderString(
			SFConnectionConfig loginDetailBean) {
		return new StringBuffer("grant_type=password").append("&username=")
				.append(loginDetailBean.getUser()).append("&password=")
				.append(loginDetailBean.getPassword()).append("&client_id=")
				.append(loginDetailBean.getConsumerKey())
				.append("&client_secret=")
				.append(loginDetailBean.getConsumerSecret()).toString();
	}

	/**
	 * 
	 * @param accessToken
	 *            To Access the org it required aceessToken for REST call
	 * @return BasicHeader object Using accessToken generate BasicHeader object
	 *         that get return
	 */

	public static BasicHeader generateAuthHeader(String accessToken) {
		return new BasicHeader("Authorization", "OAuth " + accessToken);
	}

	/**
	 * 
	 * @return BasicHeader Object Craeting Object with X-PrettyPrint and get
	 *         return
	 */
	public static BasicHeader generatePrettyPrintHeader() {
		return new BasicHeader("X-PrettyPrint", "1");
	}

	/**
	 * @return accessToken return accesstoken String after login into
	 *         salesforce.
	 * 
	 * @throws ClientProtocolException
	 * 
	 * @throws IOException
	 * 
	 * @throws JSONException
	 * 
	 * @throws CustomException
	 */
	public static String getAccessTokenFromSF() throws JSONException,
			ClientProtocolException, IOException, CustomException {
		String accessToken = null;
		SFConnectionConfig loginDetailBean = Utility
				.getLoginDetailsFromMiddleware();
		HttpResponse response = null;
		String loginHostUri = loginDetailBean.getUrl() + Constants.AUTH_URL;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(loginHostUri);
		StringEntity requestBody = new StringEntity(
				generateheaderString(loginDetailBean));
		requestBody.setContentType(Constants.CONTENT_TYPE_VAL);
		httpPost.setEntity(requestBody);
		httpPost.addHeader(generatePrettyPrintHeader());
		response = httpClient.execute(httpPost);
		if (response.getStatusLine().getStatusCode() == 200) {
			String response_string = EntityUtils.toString(response.getEntity());
			JSONObject json = new JSONObject(response_string);
			accessToken = json.getString(Constants.ACCESS_TOKEN);
		} else {
			throw new CustomException(
					"Unable to connect salesforce org. Please check configurations.");
		}
		return accessToken;
	}

	/**
	 * 
	 * @param developerQueueName
	 *            Name of the queue is pass for query to get queueId
	 * 
	 * @param accessToken
	 *            To Access the org it required aceessToken for REST call
	 * 
	 * @param baseURL
	 *            Login into salesforce org the URL is get provided for httpGet
	 *            call.
	 * 
	 * @return queueId Return queueId get on queried the group object on the
	 *         basis of DeveloperQueueName
	 * @throws ClientProtocolException
	 * 
	 * @throws IOException
	 * 
	 * @throws JSONException
	 * 
	 * @throws CustomException
	 * 
	 */
	public static String getQueueId(String developerQueueName,
			String accessToken, String baseURL) throws ClientProtocolException,
			IOException, JSONException, CustomException {
		String queueId = "";
		String queryStr = "query?q=SELECT+Id+,+OwnerId+FROM+Group+WHERE+Type+=+'Queue'+and+DeveloperName+=+'"
				+ developerQueueName + "'";
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(baseURL + Constants.REST_URL + queryStr);
		httpGet.addHeader(generateAuthHeader(accessToken));
		httpGet.addHeader(generatePrettyPrintHeader());
		HttpResponse response = httpClient.execute(httpGet);
		System.out.println("response queue==== " + response);
		if (response.getStatusLine().getStatusCode() == Constants.SUCCESS_CODE) {
			JSONObject queueIdListJSON = new JSONObject(
					EntityUtils.toString(response.getEntity()));
			// todo- Pankaj : Need to add null check for queue
			queueId = queueIdListJSON.getJSONArray("records").getJSONObject(0)
					.getString("Id");
			System.out.println("queueId id   " + queueId);
		} else {
			throw new CustomException(
					"No Queue found for given name. Please check conffigurations. Queue Name:"
							+ developerQueueName);
		}
		return queueId;
	}

	/**
	 * 
	 * 
	 * @param developerQueueId
	 *            Pass as Parameter to get ProcessInstanceId from
	 *            ProcessInstanceWorkitem
	 * 
	 * @param accessToken
	 *            To Access the org it required aceessToken for REST call
	 * 
	 * @param baseURL
	 *            Login into SF org the URL is get provided for httpGet call.
	 * 
	 * @return processInstanceIdList return List of string of processInstanceId
	 * 
	 * @throws ClientProtocolException
	 * 
	 * @throws IOException
	 * 
	 * @throws JSONException
	 * 
	 * @throws CustomException
	 * 
	 */
	public static List<String> getProcessInstanceIds(String developerQueueId,
			String accessToken, String baseURL) throws ClientProtocolException,
			IOException, JSONException, CustomException {
		List<String> processInstanceIdList = new ArrayList<String>();
		String queryStr = "query?q=SELECT+ProcessInstanceId+FROM+ProcessInstanceWorkitem+where+ActorId=+'"
				+ developerQueueId + "'";
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(baseURL + Constants.REST_URL + queryStr);
		httpGet.addHeader(generateAuthHeader(accessToken));
		httpGet.addHeader(generatePrettyPrintHeader());
		HttpResponse response = httpClient.execute(httpGet);
		System.out.println("response queue==== " + response);
		if (response.getStatusLine().getStatusCode() == Constants.SUCCESS_CODE) {
			JSONObject processInstanceIdListJSON = new JSONObject(
					EntityUtils.toString(response.getEntity()));
			JSONArray jsonObjArray = processInstanceIdListJSON
					.getJSONArray("records");
			System.out.println("josn==" + jsonObjArray.length());
			// If no records found , return empty list
			if (jsonObjArray.length() == 0) {
				return new ArrayList<String>();
			}
			for (int i = 0; i < jsonObjArray.length(); i++) {
				processInstanceIdList.add(jsonObjArray.getJSONObject(i)
						.getString("ProcessInstanceId"));
			}
		} else {
			throw new CustomException(
					"issue with fetching process instance Id.");
		}
		return processInstanceIdList;
	}

	/**
	 * @param processInstanceIdList
	 *            List of ProcessInstanceId to query on ProcessInstance object
	 *            to get targetObjectId
	 * 
	 * @param accessToken
	 *            To Access the org it required aceessToken for REST call
	 * 
	 * @param baseURL
	 *            Login into SF org the URL is get provided for httpGet call.
	 * 
	 * @throws ClientProtocolException
	 * 
	 * @throws IOException
	 * 
	 * @throws JSONException
	 * 
	 * @throws CustomException
	 * 
	 * 
	 * @return targetobjectIdList return the List of String of targetObjectid
	 *         list
	 */
	public static List<String> getTargetobjectInstanceIds(
			List<String> processInstanceIdList, String accessToken,
			String baseURL) throws ClientProtocolException, IOException,
			JSONException, CustomException {
		if (processInstanceIdList.size() == 0) {
			return new ArrayList<String>();
		}
		List<String> targetobjectIdList = new ArrayList<String>();
		String processInstanceStr = Utility
				.generateInCluaseString(processInstanceIdList);
		String queryStr = "query?q=SELECT+Id+,+targetObjectid+FROM+ProcessInstance+where+id+in+("
				+ processInstanceStr + ")";
		System.out.println("query string===" + queryStr);
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(baseURL + Constants.REST_URL + queryStr);
		httpGet.addHeader(generateAuthHeader(accessToken));
		httpGet.addHeader(generatePrettyPrintHeader());
		HttpResponse response = httpClient.execute(httpGet);
		if (response.getStatusLine().getStatusCode() == Constants.SUCCESS_CODE) {
			JSONObject targetInstanceIdListJSON = new JSONObject(
					EntityUtils.toString(response.getEntity()));
			JSONArray jsonArray = targetInstanceIdListJSON
					.getJSONArray("records");
			if (jsonArray.length() == 0) {
				return new ArrayList<String>();
			}
			for (int i = 0; i < jsonArray.length(); i++) {
				targetobjectIdList.add(jsonArray.getJSONObject(i).getString(
						"TargetObjectId"));
			}
		} else {
			throw new CustomException(
					"Issue while fetching targetObjectInstance Found:");
		}
		return targetobjectIdList;
	}

	/**
	 * 
	 * @param targetobjectInstanceIdList
	 *            targetObjectId list as a parameter to query on
	 *            KnowledgeArticle Type
	 * 
	 * @param articleType
	 *            Knowledge Article type to get Ids.
	 * 
	 * @param accessToken
	 *            To Access the org it required aceessToken for REST call
	 * 
	 * @param baseURL
	 *            Login into SF org the URL is get provided for httpGet call.
	 * 
	 * @param locale
	 *            Language of the article that is going to be return
	 * 
	 * @param fieldsInfo
	 *            contains info about fields of type SFArticleField
	 * @return sfArticleList return List of String of Knowledge Articles
	 * @throws Exception
	 */

	public static List<SFArticle> getKnowLedgeArticlesTranslatedVersions(
			List<String> targetobjectInstanceIdList, String articleType,
			String accessToken, String baseURL, String locale) throws Exception {
		if (targetobjectInstanceIdList.size() == 0) {
			return new ArrayList<SFArticle>();
		}
		List<SFArticle> sfArticleList = new ArrayList<SFArticle>();
		String targetObjectIdStr = Utility
				.generateInCluaseString(targetobjectInstanceIdList);
		String queryStr = "query?q=SELECT+Id+,+KnowledgeArticleId+,+Title+,+Summary+,+OwnerId+,+MasterVersionId+,+Language+FROM+"
				+ articleType
				+ "__kav+WHERE+language+=+'"
				+ locale
				+ "'+AND+PublishStatus+=+'draft'+AND+Id+IN+("
				+ targetObjectIdStr + ")";
		System.out.println("query " + queryStr);
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(baseURL + Constants.REST_URL + queryStr);
		httpGet.addHeader(generateAuthHeader(accessToken));
		httpGet.addHeader(generatePrettyPrintHeader());
		HttpResponse response = httpClient.execute(httpGet);
		JSONObject kaListJSON = new JSONObject();
		if (response.getStatusLine().getStatusCode() == Constants.SUCCESS_CODE) {
			kaListJSON = new JSONObject(EntityUtils.toString(response
					.getEntity()));
			JSONArray jsonArray = kaListJSON.getJSONArray("records");
			for (int i = 0; i < jsonArray.length(); i++) {
				SFArticle sfArticle = new SFArticle();
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				sfArticle.setId(jsonObj.getString("Id"));
				sfArticle.setMasterVersionId(jsonObj
						.getString("MasterVersionId"));
				sfArticleList.add(sfArticle);
			}
		} else {
			throw new CustomException("Issue while fetching knowledge article.");
		}
		return sfArticleList;
	}

	/**
	 * 
	 * @param sourceArticle
	 *            object of SFArticle for feching data from it.
	 * 
	 * @param fields
	 *            List of Fields which we will be part of XML.
	 * 
	 * @param accessToken
	 *            To Access the org it required aceessToken for REST call
	 * 
	 * @param baseURL
	 *            Login into SF org the URL is get provided for httpGet call.
	 * @return sfArticleList return list of SFArticle class variables.
	 * @throws Exception
	 */
	public static InputStream getKnowLedgeArticlesRecords(
			SFArticle sourceArticle, List<SFArticleField> fields,
			String accessToken, String baseURL) throws Exception {
		// todo - Add metadata fields hardcoded
		List<SFArticleField> metadataFields = Utility
				.getSFArticleMetadataFieldList();

		String queryStr = "query?q=select+" + Utility.getFieldsStr(fields)
				+ "+,+" + Utility.getFieldsStr(metadataFields) + "+from+"
				+ sourceArticle.getType() + "__kav+where+Id+in+('"
				+ sourceArticle.getMasterVersionId() + "')";
		System.out.println("last query==" + queryStr);
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(baseURL + Constants.REST_URL + queryStr);
		httpGet.addHeader(generateAuthHeader(accessToken));
		httpGet.addHeader(generatePrettyPrintHeader());
		HttpResponse response = httpClient.execute(httpGet);
		if (response.getStatusLine().getStatusCode() == Constants.SUCCESS_CODE) {
			JSONObject KAListjson = new JSONObject(
					EntityUtils.toString(response.getEntity()));
			return Utility.generateXMLfromJSON(sourceArticle, fields,
					metadataFields, KAListjson);

		} else {
			throw new CustomException(
					"Issue while fetching knowledge article records.");
		}
	}

	/**
	 * 
	 * @param sourceArticle
	 *            sourceArticle object of SFArticle for data
	 * @param body
	 *            JSON data into StringEntity fromat
	 * @param accessToken
	 *            To Access the org it required aceessToken for REST call
	 * 
	 * @param baseURL
	 *            Login into SF org the URL is get provided for httpGet call.
	 * @throws Exception
	 */
	public static void insertArticleIntoSF(SFArticle sourceArticle,
			InputStream stream, String accessToken, String baseURL)
			throws Exception {
		String queryStr = "sobjects/" + sourceArticle.getType() + "__kav" + "/"
				+ sourceArticle.getId();
		System.out.println("query== " + queryStr);
		JSONObject json = Utility.conevertStreamToJSON(stream);
		StringEntity body = new StringEntity(json.toString(1));
		body.setContentType("application/json");

		HttpClient httpClient = new DefaultHttpClient();
		HttpPatch httppatch = new HttpPatch(baseURL + Constants.REST_URL
				+ queryStr);
		httppatch.addHeader(generateAuthHeader(accessToken));
		httppatch.addHeader(generatePrettyPrintHeader());
		httppatch.setEntity(body);

		HttpResponse response = httpClient.execute(httppatch);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == 204) {
			System.out.println("Record Inserted Succesufully");
		} else {
			throw new Exception();
		}

	}

}
