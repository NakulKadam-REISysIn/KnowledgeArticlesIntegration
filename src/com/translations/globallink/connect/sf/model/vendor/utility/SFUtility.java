package com.translations.globallink.connect.sf.model.vendor.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.translations.globallink.connect.sf.model.vendor.constants.Constants;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticle;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticleField;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticleType;
import com.translations.globallink.connect.sf.model.vendor.dto.SFConnectionConfig;
import com.translations.globallink.connect.sf.model.vendor.dto.SFLocale;
import com.translations.globallink.connect.sf.model.vendor.dto.SFQueue;
import com.translations.globallink.connect.sf.model.vendor.dto.SFUser;
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
	public static String getAccessTokenFromSF(SFConnectionConfig sfConnectionConfig) throws JSONException,
			IOException, CustomException {

//		SFConnectionConfig loginDetailBean = Utility
//				.getLoginDetailsFromMiddleware();
		String loingResponse = Utility.getHttpPostResponce(sfConnectionConfig);
		String accessToken = new JSONObject(loingResponse).getString("access_token");
		System.out.println("Access Token [" + accessToken + "]");
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
			String accessToken, String baseURL) throws IOException,
			JSONException, CustomException {
		System.out.println("inside getProcessInstanceIds");
		List<String> processInstanceIdList = new ArrayList<String>();
		// we are thinking of using XML files to configure all the queries and
		// read it from there - low priority.
		String queryStr = "query?q=SELECT+ProcessInstanceId+FROM+ProcessInstanceWorkitem+where+ActorId=+'"
				+ developerQueueId + "'";

		String response = Utility.getHttpGetResponce(baseURL, queryStr,
				accessToken);

		JSONObject processInstanceIdListJSON = new JSONObject(response);
		JSONArray jsonObjArray = processInstanceIdListJSON
				.getJSONArray("records");
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
			String baseURL) throws IOException, JSONException, CustomException {
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
			String accessToken, String baseURL, String locale, Boolean includeDraft) throws Exception {
		if (targetobjectInstanceIdList.size() == 0) {
			return new ArrayList<SFArticle>();
		}
		List<String> statusList = new ArrayList<String>();
		if (includeDraft) {
			statusList.add("Draft");
			statusList.add("Online");
		} else {
			statusList.add("Online");
		}
		List<SFArticle> sfArticleList = new ArrayList<SFArticle>();
		for (String status : statusList) {
			String targetObjectIdStr = Utility
					.generateInCluaseString(targetobjectInstanceIdList);
			String queryStr = new String();
			queryStr = "query?q=SELECT+Id+,+KnowledgeArticleId+,+Title+,+Summary+,+OwnerId+,+MasterVersionId+,+Language+FROM+"
				+ articleType
				+ "+WHERE+language+=+'"
				+ locale
					+ "'+AND+MasterVersion.PublishStatus+=+'"
					+ status
					+ "'+AND+Id+IN+(" + targetObjectIdStr + ")";
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
				sfArticle.setMasterVersionId(jsonObj
						.getString("MasterVersionId"));
				sfArticle.setType(articleType);
				sfArticleList.add(sfArticle);
			}
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
				+ sourceArticle.getType() + "+where+Id+in+('"
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
		String queryStr = "sobjects/" + sourceArticle.getType() + "" + "/"
				+ sourceArticle.getId();
		JSONObject json = Utility.conevertStreamToJSON(stream);
		String body = json.toString();
		// body.setContentType("application/json");

		Utility.getHttpPatchResponce(baseURL, queryStr, accessToken, body);

	}

	public static List<SFQueue> SFQueueList(String accessToken, String baseURL)
			throws CustomException, JSONException, Exception {
		List<SFQueue> queueList = new ArrayList<SFQueue>();
		String queryStr = "query?q=SELECT+Id+,+name+FROM+Group+WHERE+Type+=+'Queue'";
		String response = Utility.getHttpGetResponce(baseURL, queryStr,
				accessToken);
				
		JSONObject queueIdListJSON = new JSONObject((response));
		JSONArray jsonObjArray = queueIdListJSON.getJSONArray("records");
		if(jsonObjArray.length()==0)
			return new ArrayList<SFQueue>();
		for (int i = 0; i < jsonObjArray.length(); i++) {
			SFQueue sfQueue = new SFQueue();
			sfQueue.setQueueId(jsonObjArray.getJSONObject(i).getString("Id"));
			sfQueue.setQueueName(jsonObjArray.getJSONObject(i)
					.getString("Name"));
			queueList.add(sfQueue);
		}
		return queueList;

	}

	public static List<SFLocale> getlocales(String accessToken, String baseURL)
			throws IOException, JSONException, CustomException {
		List<SFLocale> Languages = new ArrayList<SFLocale>();
		String queryStr = Constants.GET_LANG_QUERY;
		String response = Utility.getHttpGetResponce(baseURL, queryStr,
				accessToken);
		JSONObject json = new JSONObject(response);
		JSONArray jsonArray = json.getJSONArray("fields").getJSONObject(3)
				.getJSONArray("picklistValues");
		if(jsonArray.length()==0)
			return new ArrayList<SFLocale>();
		for (int i = 0; i < jsonArray.length(); i++) {
			SFLocale lang = new SFLocale();
			lang.setCode(jsonArray.getJSONObject(i).getString("value"));
			lang.setLabel(jsonArray.getJSONObject(i).getString("label"));
			Languages.add(lang);
		}
		return Languages;
	}

	public static List<SFArticleType> gettype(String accessToken, String baseURL)
			throws IOException, JSONException, CustomException {
		List<SFArticleType> type = new ArrayList<SFArticleType>();
		String queryStr = Constants.GET_OBJ_QUERY;

		String response = Utility.getHttpGetResponce(baseURL, queryStr,
				accessToken);
		
		JSONObject json = new JSONObject(response);
		JSONArray josnArray =json.getJSONArray("sobjects");
		if(josnArray.length() == 0){
			return new ArrayList<SFArticleType>();
		}
		for (int i = 0; i < josnArray.length(); i++) {
			String kaType = josnArray.getJSONObject(i)
					.getString("name");
			if (kaType.endsWith("__kav")) {
				SFArticleType sfarticle = new SFArticleType();
				sfarticle.setLabel(josnArray.getJSONObject(i).getString("label"));
				sfarticle.setName(kaType);
				type.add(sfarticle);
			}
		}
		return type;
	}

	public static List<SFArticleField> FieldsForArticleType(
			String knowledgeArticleType, String accessToken, String baseURL)
			throws IOException, Exception {
		List<SFArticleField> articleFields = new ArrayList<SFArticleField>();
		String queryStr = "sobjects/" + knowledgeArticleType + "/describe";
		String response = Utility.getHttpGetResponce(baseURL, queryStr,
				accessToken);
		JSONObject json = new JSONObject(response);
		JSONArray jsonArray = json.getJSONArray("fields");
		if(jsonArray.length()==0)
			return new ArrayList<SFArticleField>();
		Set<String> typeList = new HashSet<String>();
		typeList.add("string");
		typeList.add("Long Text Area");
		typeList.add("textarea");
		typeList.add("Rich Text Area");
		Set<String> nameList = new HashSet<String>();
		nameList.add("Summary");
		nameList.add("Title");
		
		for (int i = 0; i < jsonArray.length(); i++) {
			String typeStr = jsonArray.getJSONObject(i).getString("type");
			if ((typeList.contains(typeStr) && jsonArray.getJSONObject(i)
					.getString("name").endsWith("__c"))
					|| (nameList.contains(jsonArray.getJSONObject(i).getString(
							"name")))) {
				articleFields.add(new SFArticleField(jsonArray.getJSONObject(i)
						.getString("name"), jsonArray.getJSONObject(i)
						.getString("label"), jsonArray.getJSONObject(i)
						.getString("type"), jsonArray.getJSONObject(i).getInt(
						"length"), true));
			}
		}
		return articleFields;

	}

	public static void insertAssignedId(SFArticle sourceArticle,String userId,String accessToken, String baseURL)
			throws IOException, Exception {
		String queryStr = "knowledgeManagement/articleVersions/translations/"+sourceArticle.getId();

		String body = "{\"assigneeId\":\""+userId+"\"}";

		Utility.getHttpPatchResponce(baseURL, queryStr, accessToken, body);
	}
	
	public static List<SFUser> getUserInfo(String accessToken, String baseURL)
			throws IOException, CustomException, JSONException {
		List<SFUser> userList = new ArrayList<SFUser>();
		String queryStr = "query?q=SELECT+Id+,+Name+FROM+User+WHERE+UserPermissionsKnowledgeUser+=+true";
		String response = Utility.getHttpGetResponce(baseURL, queryStr,
				accessToken);

		JSONObject kaListJSON = new JSONObject((response));
		JSONArray jsonArray = kaListJSON.getJSONArray("records");
		if (jsonArray.length() == 0) {
			return new ArrayList<SFUser>();
		}
		for (int i = 0; i < jsonArray.length(); i++) {
			SFUser user = new SFUser();
			user.setUserId(jsonArray.getJSONObject(i).getString("Id"));
			user.setUserName(jsonArray.getJSONObject(i).getString("Name"));
			userList.add(user);

		}
		return userList;

	}
}
