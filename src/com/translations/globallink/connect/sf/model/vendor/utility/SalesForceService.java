package com.translations.globallink.connect.sf.model.vendor.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
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
 * @class Name-SalesForceService
 * 
 *        This class use for the fetching data from SFConnectionConfig class and then login into salesforce and then get QueueId,processInstance,TargetObjectId,KnowledgeArticle also insert the article into Salesforce.
 * 
 * @author-
 * 
 * @createdDate-
 */

public class SalesForceService {

    private static Logger logger = Logger.getLogger(SalesForceService.class);

    private SFConnectionConfig sfConnectionConfig;

    public SalesForceService(SFConnectionConfig sfConnectionConfig) {
	this.sfConnectionConfig = sfConnectionConfig;
    }

    /**
     * @return StringBuffer generated data from SFConnectionConfig is get return.
     */

    public String generateheaderString() {
	return new StringBuffer("grant_type=password").append("&username=").append(this.sfConnectionConfig.getUser()).append("&password=").append(this.sfConnectionConfig.getPassword()).append("&client_id=").append(this.sfConnectionConfig.getConsumerKey()).append("&client_secret=").append(this.sfConnectionConfig.getConsumerSecret()).toString();
    }

    /**
     * @return accessToken return accesstoken String after login into salesforce.
     * @throws Exception
     */
    private String getAccessTokenFromSF() throws Exception {
	String loginResponse = getHttpPostResponse(this.sfConnectionConfig);
	String accessToken = new JSONObject(loginResponse).getString("access_token");
	logger.debug("Access Token [" + accessToken + "]");
	return accessToken;
    }

    /**
     * 
     * 
     * @param developerQueueId
     *            Pass as Parameter to get ProcessInstanceId from ProcessInstanceWorkitem
     * 
     * @param accessToken
     *            To Access the org it required aceessToken for REST call
     * 
     * @return processInstanceIdList return List of string of processInstanceId
     * 
     * @throws Exception
     * 
     */
    public List<String> getProcessInstanceIds(String developerQueueId) throws Exception {
	logger.debug("inside getProcessInstanceIds");
	List<String> processInstanceIdList = new ArrayList<String>();
	// we are thinking of using XML files to configure all the queries and
	// read it from there - low priority.
	String queryStr = "query?q=SELECT+ProcessInstanceId+FROM+ProcessInstanceWorkitem+where+ActorId=+'" + developerQueueId + "'";

	String response = getHttpGetResponse(this.sfConnectionConfig.getUrl(), queryStr);

	JSONObject processInstanceIdListJSON = new JSONObject(response);
	JSONArray jsonObjArray = processInstanceIdListJSON.getJSONArray("records");
	// If no records found , return empty list
	if (jsonObjArray.length() == 0) {
	    return new ArrayList<String>();
	}
	for (int i = 0; i < jsonObjArray.length(); i++) {
	    processInstanceIdList.add(jsonObjArray.getJSONObject(i).getString("ProcessInstanceId"));
	}

	return processInstanceIdList;
    }

    /**
     * @param processInstanceIdList
     *            List of ProcessInstanceId to query on ProcessInstance object to get targetObjectId
     * 
     * @param accessToken
     *            To Access the org it required aceessToken for REST call
     * 
     * @throws Exception
     * 
     * @return targetobjectIdList return the List of String of targetObjectid list
     */
    public List<String> getTargetobjectInstanceIds(List<String> processInstanceIdList) throws Exception {
	if (processInstanceIdList.size() == 0) {
	    return new ArrayList<String>();
	}
	List<String> targetobjectIdList = new ArrayList<String>();
	String processInstanceStr = Utility.generateInClauseString(processInstanceIdList);
	String queryStr = "query?q=SELECT+Id+,+targetObjectid+FROM+ProcessInstance+where+id+in+(" + processInstanceStr + ")";
	logger.debug("query string===" + queryStr);
	String response = getHttpGetResponse(this.sfConnectionConfig.getUrl(), queryStr);

	JSONObject targetInstanceIdListJSON = new JSONObject((response));
	logger.trace("josn object" + targetInstanceIdListJSON);
	JSONArray jsonArray = targetInstanceIdListJSON.getJSONArray("records");
	if (jsonArray.length() == 0) {
	    return new ArrayList<String>();
	}
	for (int i = 0; i < jsonArray.length(); i++) {
	    targetobjectIdList.add(jsonArray.getJSONObject(i).getString("TargetObjectId"));
	}
	logger.debug("targetobject" + targetobjectIdList);
	return targetobjectIdList;
    }

    /**
     * 
     * @param targetobjectInstanceIdList
     *            targetObjectId list as a parameter to query on KnowledgeArticle Type
     * 
     * @param articleType
     *            Knowledge Article type to get Ids.
     * 
     * @param accessToken
     *            To Access the org it required aceessToken for REST call
     * 
     * @param locale
     *            Language of the article that is going to be return
     * 
     * @param fieldsInfo
     *            contains info about fields of type SFArticleField
     * @return sfArticleList return List of String of Knowledge Articles
     * @throws Exception
     */

    public List<SFArticle> getKnowLedgeArticlesTranslatedVersions(List<String> targetobjectInstanceIdList, String articleType, String locale, Boolean includeDraft) throws Exception {
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
	    String targetObjectIdStr = Utility.generateInClauseString(targetobjectInstanceIdList);
	    String queryStr = new String();
	    queryStr = "query?q=SELECT+Id+,+KnowledgeArticleId+,+Title+,+Summary+,+OwnerId+,+MasterVersionId+,+Language+FROM+" + articleType + "+WHERE+language+=+'" + locale + "'+AND+MasterVersion.PublishStatus+=+'" + status + "'+AND+Id+IN+(" + targetObjectIdStr + ")";
	    logger.debug("query " + queryStr);
	    String response = getHttpGetResponse(this.sfConnectionConfig.getUrl(), queryStr);

	    JSONObject kaListJSON = new JSONObject((response));

	    JSONArray jsonArray = kaListJSON.getJSONArray("records");
	    for (int i = 0; i < jsonArray.length(); i++) {
		SFArticle sfArticle = new SFArticle();
		JSONObject jsonObj = jsonArray.getJSONObject(i);
		sfArticle.setId(jsonObj.getString("Id"));
		sfArticle.setLanguage(locale);
		sfArticle.setMasterVersionId(jsonObj.getString("MasterVersionId"));
		sfArticle.setType(articleType);
		sfArticle.setDueDate(getDueDate(sfArticle.getId()));
		sfArticleList.add(sfArticle);
	    }
	}
	return sfArticleList;
    }

    private Date getDueDate(String sourceArticleId) throws Exception {
	String DueDateQueryStr = "knowledgeManagement/articleVersions/translations/" + sourceArticleId;
	String DueDateResponce = getHttpGetResponse(this.sfConnectionConfig.getUrl(), DueDateQueryStr);
	JSONObject KAListjson = new JSONObject((DueDateResponce));
	if (!KAListjson.isNull("dueDate")) {
	    String DueDate = KAListjson.getString("dueDate");
	    DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	    Date date = format.parse(DueDate);
	    return date;
	}

	return null;

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
     * @return sfArticleList return list of SFArticle class variables.
     * @throws Exception
     */
    public InputStream getKnowLedgeArticlesRecords(SFArticle sourceArticle, List<SFArticleField> fields) throws Exception {
	// todo - Add metadata fields hardcoded
	List<SFArticleField> metadataFields = Utility.getSFArticleMetadataFieldList();

	String queryStr = "query?q=select+" + Utility.getFieldsStr(fields) + "+,+" + Utility.getMetaDataFieldsStr(metadataFields) + "+from+" + sourceArticle.getType() + "+where+Id+in+('" + sourceArticle.getMasterVersionId() + "')";
	logger.debug("last query==" + queryStr);
	String response = getHttpGetResponse(this.sfConnectionConfig.getUrl(), queryStr);

	JSONObject KAListjson = new JSONObject((response));

	return Utility.generateXMLfromJSON(sourceArticle, fields, metadataFields, KAListjson);

    }

    /**
     * 
     * @param sourceArticle
     *            sourceArticle object of SFArticle for data
     * @param body
     *            JSON data into StringEntity fromat
     * @param accessToken
     *            To Access the org it required aceessToken for REST call
     * @throws Exception
     */
    public void insertArticleIntoSF(SFArticle sourceArticle, InputStream stream) throws Exception {
	String queryStr = "sobjects/" + sourceArticle.getType() + "" + "/" + sourceArticle.getId();
	JSONObject json = Utility.conevertStreamToJSON(stream);
	String body = json.toString();
	// body.setContentType("application/json");
	getHttpPatchResponse(this.sfConnectionConfig.getUrl(), queryStr, body);

    }

    public List<SFQueue> SFQueueList() throws Exception {
	List<SFQueue> queueList = new ArrayList<SFQueue>();
	String queryStr = "query?q=SELECT+Id+,+name+FROM+Group+WHERE+Type+=+'Queue'";
	String response = getHttpGetResponse(this.sfConnectionConfig.getUrl(), queryStr);

	JSONObject queueIdListJSON = new JSONObject((response));
	JSONArray jsonObjArray = queueIdListJSON.getJSONArray("records");
	if (jsonObjArray.length() == 0)
	    return new ArrayList<SFQueue>();
	for (int i = 0; i < jsonObjArray.length(); i++) {
	    SFQueue sfQueue = new SFQueue();
	    sfQueue.setQueueId(jsonObjArray.getJSONObject(i).getString("Id"));
	    sfQueue.setQueueName(jsonObjArray.getJSONObject(i).getString("Name"));
	    queueList.add(sfQueue);
	}
	return queueList;

    }

    public List<SFLocale> getlocales() throws Exception {
	List<SFLocale> Languages = new ArrayList<SFLocale>();
	String queryStr = Constants.GET_LANG_QUERY;
	String response = getHttpGetResponse(this.sfConnectionConfig.getUrl(), queryStr);
	JSONObject json = new JSONObject(response);
	JSONArray jsonArray = json.getJSONArray("fields").getJSONObject(3).getJSONArray("picklistValues");
	if (jsonArray.length() == 0)
	    return new ArrayList<SFLocale>();
	for (int i = 0; i < jsonArray.length(); i++) {
	    SFLocale lang = new SFLocale();
	    lang.setCode(jsonArray.getJSONObject(i).getString("value"));
	    lang.setLabel(jsonArray.getJSONObject(i).getString("label"));
	    Languages.add(lang);
	}
	return Languages;
    }

    public List<SFArticleType> gettype() throws Exception {
	List<SFArticleType> type = new ArrayList<SFArticleType>();
	String queryStr = Constants.GET_OBJ_QUERY;
	String response = getHttpGetResponse(this.sfConnectionConfig.getUrl(), queryStr);
	JSONObject json = new JSONObject(response);
	JSONArray josnArray = json.getJSONArray("sobjects");
	if (josnArray.length() == 0) {
	    return new ArrayList<SFArticleType>();
	}
	for (int i = 0; i < josnArray.length(); i++) {
	    String kaType = josnArray.getJSONObject(i).getString("name");
	    if (kaType.endsWith("__kav")) {
		SFArticleType sfarticle = new SFArticleType();
		sfarticle.setLabel(josnArray.getJSONObject(i).getString("label"));
		sfarticle.setName(kaType);
		type.add(sfarticle);
	    }
	}
	return type;
    }

    public List<SFArticleField> FieldsForArticleType(String knowledgeArticleType) throws IOException, Exception {
	List<SFArticleField> articleFields = new ArrayList<SFArticleField>();
	String queryStr = "sobjects/" + knowledgeArticleType + "/describe";
	String response = getHttpGetResponse(this.sfConnectionConfig.getUrl(), queryStr);
	JSONObject json = new JSONObject(response);
	JSONArray jsonArray = json.getJSONArray("fields");
	if (jsonArray.length() == 0)
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
	    if ((typeList.contains(typeStr) && jsonArray.getJSONObject(i).getString("name").endsWith("__c")) || (nameList.contains(jsonArray.getJSONObject(i).getString("name")))) {
		articleFields.add(new SFArticleField(jsonArray.getJSONObject(i).getString("name"), jsonArray.getJSONObject(i).getString("label"), jsonArray.getJSONObject(i).getString("type"), jsonArray.getJSONObject(i).getInt("length"), true));
	    }
	}
	return articleFields;

    }

    public void insertAssignedId(SFArticle sourceArticle, String userId) throws IOException, Exception {
	String queryStr = "knowledgeManagement/articleVersions/translations/" + sourceArticle.getId();
	String body = "{\"assigneeId\":\"" + userId + "\"}";
	getHttpPatchResponse(this.sfConnectionConfig.getUrl(), queryStr, body);
    }

    public List<SFUser> getUserInfo() throws Exception {
	List<SFUser> userList = new ArrayList<SFUser>();
	String queryStr = "query?q=SELECT+Id+,+Name+FROM+User+WHERE+UserPermissionsKnowledgeUser+=+true";
	String response = getHttpGetResponse(this.sfConnectionConfig.getUrl(), queryStr);

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
    private String getHttpGetResponse(String baseURL, String queryStr) throws Exception {
	URL url;
	HttpsURLConnection connection = null;
	StringBuffer response = new StringBuffer();
	try {
	    url = new URL(baseURL + "/" + Constants.REST_URL + queryStr);
	    logger.info("HTTP GET URL - " + url);
	    connection = (HttpsURLConnection) url.openConnection();
	    connection.setRequestProperty("Authorization", "OAuth " + this.getAccessTokenFromSF());
	    connection.setRequestProperty("accept", Constants.ACCEPT_STRING);
	    connection.setRequestMethod("GET");

	    connection.setRequestProperty("Content-Type", Constants.CONTENT_TYPE_VAL);
	    connection.setRequestProperty("Content-Language", "en-US");
	    connection.setUseCaches(false);
	    connection.setDoInput(true);
	    connection.setDoOutput(true);

	    if (connection.getResponseCode() == Constants.SUCCESS_CODE) {
		logger.info("RESPONSE CODE - " + connection.getResponseCode());

		InputStream is = connection.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		String line;

		while ((line = rd.readLine()) != null) {
		    response.append(line);
		    response.append('\r');
		}
		rd.close();

		// return response.toString();
	    } else {
		throw new CustomException("Error in callout. Error code:" + connection.getResponseCode() + " Error message:" + connection.getResponseMessage());
	    }
	} catch (IOException ex) {
	    logger.error(ex.getMessage(), ex);
	    throw ex;

	}
	return response.toString();
    }

    private String getHttpPostResponse(SFConnectionConfig loginDetailBean) throws Exception {
	URL url;
	HttpsURLConnection connection = null;
	StringBuffer response = new StringBuffer();
	String urlParameters = "grant_type=password&client_id=" + loginDetailBean.getConsumerKey() + "&client_secret=" + loginDetailBean.getConsumerSecret() + "&username=" + loginDetailBean.getUser() + "&password=" + loginDetailBean.getPassword();
	try {
	    url = new URL(loginDetailBean.getUrl() + Constants.AUTH_URL);
	    logger.info("HTTP POST URL - " + url);
	    logger.trace(urlParameters);
	    connection = (HttpsURLConnection) url.openConnection();
	    connection.setRequestProperty("accept", "application/json");
	    connection.setRequestMethod("POST");
	    connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
	    connection.setRequestProperty("Content-Type", Constants.CONTENT_TYPE_VAL);
	    connection.setRequestProperty("Content-Language", "en-US");
	    connection.setUseCaches(false);
	    connection.setDoInput(true);
	    connection.setDoOutput(true);
	    OutputStream os = connection.getOutputStream();
	    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
	    writer.write(urlParameters);
	    writer.flush();
	    writer.close();
	    os.close();

	    InputStream is = connection.getInputStream();
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	    String line;
	    while ((line = rd.readLine()) != null) {
		response.append(line);
		response.append('\r');
	    }
	    rd.close();
	} catch (IOException ex) {
	    logger.error(ex.getMessage(), ex);
	    throw new Exception("Please enter valid SalesForce Configuartion Values.");
	}

	return response.toString();

    }

    private void getHttpPatchResponse(String baseURL, String queryStr, String body) throws Exception {

	URL url;
	HttpsURLConnection connection = null;
	try {
	    url = new URL(baseURL + Constants.REST_URL + queryStr);
	    logger.info("HTTP PATCH URL - " + url);
	    logger.trace(body);
	    connection = (HttpsURLConnection) url.openConnection();
	    connection.setRequestProperty("Authorization", "OAuth " + this.getAccessTokenFromSF());
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
		logger.error("responce code  " + connection.getResponseCode() + "  " + connection.getResponseMessage() + " Message :" + response.toString());
		throw new CustomException("HTTP PATCH Responce Error" + connection.getResponseCode());

	    } else {
		logger.debug("record Inserted successfully!!!!");
		logger.debug("responce code  " + connection.getResponseCode() + "  " + connection.getResponseMessage() + " Message :" + response.toString());
	    }
	} catch (IOException ex) {
	    logger.error(ex.getMessage(), ex);
	    throw ex;
	}

    }

    private final void setRequestMethodUsingWorkaround(final HttpURLConnection httpURLConnection, final String method) {
	try {
	    httpURLConnection.setRequestMethod(method);
	    // Check whether we are running on a buggy JRE
	} catch (final ProtocolException pe) {
	    Class<?> connectionClass = httpURLConnection.getClass();
	    java.lang.reflect.Field delegateField = null;
	    try {
		delegateField = connectionClass.getDeclaredField("delegate");
		delegateField.setAccessible(true);
		HttpURLConnection delegateConnection = (HttpURLConnection) delegateField.get(httpURLConnection);
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
			methodField = connectionClass.getDeclaredField("method");
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

    public boolean testConnection() throws Exception {
	if (StringUtils.isNotBlank(this.getAccessTokenFromSF())) {
	    return true;
	}
	return false;
    }
}
