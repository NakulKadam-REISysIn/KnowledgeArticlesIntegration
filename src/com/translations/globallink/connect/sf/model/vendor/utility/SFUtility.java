package com.translations.globallink.connect.sf.model.vendor.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONException;
import org.json.JSONObject;

import com.translations.globallink.connect.sf.model.vendor.constants.Constants;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticle;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticleField;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticleType;
import com.translations.globallink.connect.sf.model.vendor.dto.SFConnectionConfig;
import com.translations.globallink.connect.sf.model.vendor.dto.SFLocale;
import com.translations.globallink.connect.sf.model.vendor.dto.SFQueue;
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

	public static String generateheaderString(SFConnectionConfig loginDetailBean) {
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

	/*public static BasicHeader generateAuthHeader(String accessToken) {
		return new BasicHeader("Authorization", "OAuth " + accessToken);
	}*/

	/**
	 * 
	 * @return BasicHeader Object Craeting Object with X-PrettyPrint and get
	 *         return
	 */
	/*public static BasicHeader generatePrettyPrintHeader() {
		return new BasicHeader("X-PrettyPrint", "1");
	}*/

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
			 IOException, CustomException {

		SFConnectionConfig loginDetailBean = Utility
				.getLoginDetailsFromMiddleware();
		String loingResponse = Utility.getHttpPostResponce(loginDetailBean);
		String accessToken = Utility.getSessionId(loingResponse);
		System.out.println("json    " + accessToken);
		/*
		 * 
		 * if (response.getStatusLine().getStatusCode() == 200) { String
		 * response_string = EntityUtils.toString(response.getEntity());
		 * JSONObject json = new JSONObject(response_string); accessToken =
		 * json.getString(Constants.ACCESS_TOKEN); } else { throw new
		 * CustomException(
		 * "Unable to connect salesforce org. Please check configurations."); }
		 */
		return accessToken;
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
			String accessToken, String baseURL) throws 
			IOException, JSONException, CustomException {
		System.out.println("inside getProcessInstanceIds");
		List<String> processInstanceIdList = new ArrayList<String>();
		// we are thinking of using XML files to configure all the queries and read it from there - low priority.
		String queryStr = "query?q=SELECT+ProcessInstanceId+FROM+ProcessInstanceWorkitem+where+ActorId=+'"
				+ developerQueueId + "'";

		String response = Utility.getHttpGetResponce(baseURL, queryStr,
				accessToken);

		JSONObject processInstanceIdListJSON = new JSONObject(response);
		JSONArray jsonObjArray = processInstanceIdListJSON
				.getJSONArray("records");
		System.out.println("josn==" + jsonObjArray.length());
		// If no records found , return empty list
		if (jsonObjArray.length() == 0) {
			return new ArrayList<String>();
		}
		for (int i = 0; i < jsonObjArray.length(); i++) {
			processInstanceIdList.add(jsonObjArray.getJSONObject(i).getString(
					"ProcessInstanceId"));
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
			String baseURL) throws  IOException,
			JSONException, CustomException {
		System.out.println("inside getTargetobjectInstanceIds");
		if (processInstanceIdList.size() == 0) {
			return new ArrayList<String>();
		}
		List<String> targetobjectIdList = new ArrayList<String>();
		String processInstanceStr = Utility
				.generateInCluaseString(processInstanceIdList);
		String queryStr = "query?q=SELECT+Id+,+targetObjectid+FROM+ProcessInstance+where+id+in+("
				+ processInstanceStr + ")";
		System.out.println("query string===" + queryStr);
		String response = Utility.getHttpGetResponce(baseURL, queryStr,
				accessToken);

		JSONObject targetInstanceIdListJSON = new JSONObject((response));
		System.out.println("josn object" + targetInstanceIdListJSON);
		JSONArray jsonArray = targetInstanceIdListJSON.getJSONArray("records");
		if (jsonArray.length() == 0) {
			return new ArrayList<String>();
		}
		for (int i = 0; i < jsonArray.length(); i++) {
			targetobjectIdList.add(jsonArray.getJSONObject(i).getString(
					"TargetObjectId"));
		}
		System.out.println("targetobject" + targetobjectIdList);
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
		System.out.println("inside getKnowLedgeArticlesTranslatedVersions");
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
		String response = Utility.getHttpGetResponce(baseURL, queryStr,
				accessToken);

		JSONObject kaListJSON = new JSONObject((response));

		JSONArray jsonArray = kaListJSON.getJSONArray("records");
		for (int i = 0; i < jsonArray.length(); i++) {
			SFArticle sfArticle = new SFArticle();
			JSONObject jsonObj = jsonArray.getJSONObject(i);
			sfArticle.setId(jsonObj.getString("Id"));
			sfArticle.setLanguage(locale);
			sfArticle.setMasterVersionId(jsonObj.getString("MasterVersionId"));
			sfArticle.setType(articleType);
			sfArticleList.add(sfArticle);
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
		System.out.println("inside getKnowLedgeArticlesRecords");
		List<SFArticleField> metadataFields = Utility
				.getSFArticleMetadataFieldList();

		String queryStr = "query?q=select+" + Utility.getFieldsStr(fields)
				+ "+,+" + Utility.getFieldsStr(metadataFields) + "+from+"
				+ sourceArticle.getType() + "__kav+where+Id+in+('"
				+ sourceArticle.getMasterVersionId() + "')";
		System.out.println("last query==" + queryStr);
		String response = Utility.getHttpGetResponce(baseURL, queryStr,
				accessToken);

		JSONObject KAListjson = new JSONObject((response));

		return Utility.generateXMLfromJSON(sourceArticle, fields,
				metadataFields, KAListjson);

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
		String body = json.toString();
		//body.setContentType("application/json");
		
		Utility.getHttpPatchResponce(baseURL, queryStr,
				accessToken, body);
		

	}

	public static List<SFQueue> SFQueueList(String accessToken, String baseURL)
			throws CustomException, JSONException, 
			Exception {
		List<SFQueue> queueList = new ArrayList<SFQueue>();
		String queryStr = "query?q=SELECT+Id+,+name+FROM+Group+WHERE+Type+=+'Queue'";
		String response = Utility.getHttpGetResponce(baseURL, queryStr,
				accessToken);

		JSONObject queueIdListJSON = new JSONObject((response));

		JSONArray jsonObjArray = queueIdListJSON.getJSONArray("records");

		for (int i = 0; i < jsonObjArray.length(); i++) {
			SFQueue sfQueue = new SFQueue();

			sfQueue.setQueueId(jsonObjArray.getJSONObject(i).getString("Id"));
			sfQueue.setQueueName(jsonObjArray.getJSONObject(i)
					.getString("Name"));
			queueList.add(sfQueue);

		}

		return queueList;

	}

	public static List<SFLocale> getlang(String knowledgeArticleType,
			String accessToken, String baseURL) throws 
			IOException, JSONException, CustomException {
		List<SFLocale> Languages = new ArrayList<SFLocale>();
		String queryStr = "sobjects/Offer__kav/describe";
		String response = Utility.getHttpGetResponce(baseURL, queryStr,
				accessToken);
		JSONObject json = new JSONObject(response);
		JSONArray jsonArray = json.getJSONArray("fields").getJSONObject(17)
				.getJSONArray("picklistValues");

		for (int i = 0; i < json.getJSONArray("fields").getJSONObject(17)
				.getJSONArray("picklistValues").length(); i++) {

			SFLocale lang = new SFLocale();
			lang.setCode(jsonArray.getJSONObject(i).getString("value"));
			lang.setLabel(jsonArray.getJSONObject(i).getString("label"));
			Languages.add(lang);

		}

		return Languages;
	}

	public static List<SFArticleType> gettype(String accessToken, String baseURL)
			throws  IOException, JSONException,
			CustomException {
		List<SFArticleType> type = new ArrayList<SFArticleType>();
		String queryStr = "sobjects";

		String response = Utility.getHttpGetResponce(baseURL, queryStr,
				accessToken);
		JSONObject json = new JSONObject(response);

		System.out.println("json" + json.getJSONArray("sobjects"));
		for (int i = 0; i < json.getJSONArray("sobjects").length(); i++) {
			System.out.println("object="
					+ json.getJSONArray("sobjects").getJSONObject(i)
							.getString("name"));

		}

		return type;
	}
}
