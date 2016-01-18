package com.translations.globallink.connect.sf.model.vendor.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.translations.globallink.connect.sf.model.vendor.constants.Constants;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticle;
import com.translations.globallink.connect.sf.model.vendor.dto.SFConnectionConfig;
import com.translations.globallink.connect.sf.model.vendor.exception.CustomException;
	/**
	 * @class Name-SalesforceUtility
	 *		
	 *		This class use for the fetching data from SFConnectionConfig class and then login into salesforce
	 *and then get QueueId,processInstance,TargetObjectId,KnowledgeArticle
	 *also insert the article into Salesforce.
	 *
	 * @author- 
	
	 *@createdDate-
	*/

public class SFUtility {
	/**
	 * 
	 *@param loginDetailbean
	 *			it is SFConnectionConfig variable that contains values to get value from SFConnectionConfig class
	 *  
	 *  @return StringBuffer
	 *  		generated data from SFConnectionConfig is get return.
	 */

	private static String generateheaderString(SFConnectionConfig loginDetailBean) {
		return new StringBuffer("grant_type=password")
				.append("&username=").append(loginDetailBean.getUser())
				.append("&password=").append(loginDetailBean.getPassword())
				.append("&client_id=").append(loginDetailBean.getConsumerKey())
				.append("&client_secret=").append(loginDetailBean.getConsumerSecret()).toString();
	}
	/**
	 * 
	 * @param accessToken
	 * 		To Access the org it required aceessToken for REST call
	 * @return BasicHeader object
	 * 			Using accessToken generate BasicHeader object that get return 
	 */
	
	public static BasicHeader generateAuthHeader(String accessToken){
		return new BasicHeader("Authorization", "OAuth "
				+ accessToken);
	}
	
	/**
	 * 
	 * @return BasicHeader Object
	 * 		Craeting Object with X-PrettyPrint and get return
	 */
	public static BasicHeader generatePrettyPrintHeader(){
		return new BasicHeader("X-PrettyPrint","1");
	}
	/**
	 * @return accessToken
	 * 			return accesstoken String after login into salesforce.
	 * 
	 * @throws ClientProtocolException
	 *  
	 * @throws IOException
	 *  
	 * @throws JSONException
	 *  
	 * @throws CustomException
	 */
	public static String getAccessTokenFromSF() throws JSONException,ClientProtocolException, IOException, CustomException {
		String accessToken= null;
		SFConnectionConfig loginDetailBean = Utility.getLoginDetailsFromMiddleware();
		HttpResponse response = null;
		String loginHostUri = loginDetailBean.getUrl()+Constants.AUTH_URL;
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
			throw new CustomException("Unable to connect salesforce org. Please check configurations.");
		}
		return accessToken;
	}
	/**
	 * 
	 * @param developerQueueName
	 * 			Name of the queue is pass for query to get queueId
	 * 
	 * @param accessToken
	 * 			To Access the org it required aceessToken for REST call
	 * 
	 * @param baseURL
	 *			Login into salesforce org the URL is get provided for httpGet call.
	 * 
	 * @return queueId
	 *			Return queueId get on queried the group object on the basis of DeveloperQueueName 
	 * @throws ClientProtocolException
	 *  
	 * @throws IOException
	 *  
	 * @throws JSONException
	 *  
	 * @throws CustomException
	 *
	 */
	public static String getQueueId(String developerQueueName, String accessToken, String baseURL) throws ClientProtocolException, IOException, JSONException, CustomException{
		String queueId = "";
		String queryStr = "query?q=SELECT+Id+,+OwnerId+FROM+Group+WHERE+Type+=+'Queue'+and+DeveloperName+=+'"+developerQueueName+"'";
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(baseURL+Constants.REST_URL+ queryStr);
		httpGet.addHeader(generateAuthHeader(accessToken));
		httpGet.addHeader(generatePrettyPrintHeader());
		HttpResponse response = httpClient.execute(httpGet);
		System.out.println("response queue==== " + response);
		if(response.getStatusLine().getStatusCode() == Constants.SUCCESS_CODE) {
			JSONObject queueIdListJSON = new JSONObject(EntityUtils.toString(response.getEntity()));
			// todo- Pankaj : Need to add null check for queue
			queueId = queueIdListJSON.getJSONArray("records").getJSONObject(0).getString("Id");
			System.out.println("queueId id   " + queueId);
		} else {
			throw new CustomException("No Queue found for given name. Please check conffigurations. Queue Name:"+developerQueueName);
		}
		return queueId;
	}
	
	/**
	 * 
	 * 
	 * @param developerQueueId
	 * 			Pass as Parameter to get ProcessInstanceId from ProcessInstanceWorkitem 
	 * 
	 * @param accessToken
	 * 			To Access the org it required aceessToken for REST call
	 * 
	 * @param baseURL
	 *			Login into SF org the URL is get provided for httpGet call.
	 * 
	 * @return processInstanceIdList
	 * 			return List of string of processInstanceId
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
	public static List<String> getProcessInstanceIds(String developerQueueId, String accessToken, String baseURL) throws ClientProtocolException, IOException, JSONException, CustomException{
		List<String> processInstanceIdList = new ArrayList<String>();
		String queryStr = "query?q=SELECT+ProcessInstanceId+FROM+ProcessInstanceWorkitem+where+ActorId=+'"+developerQueueId+"'";
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(baseURL+Constants.REST_URL+ queryStr);
		httpGet.addHeader(generateAuthHeader(accessToken));
		httpGet.addHeader(generatePrettyPrintHeader());
		HttpResponse response = httpClient.execute(httpGet);
		System.out.println("response queue==== " + response);
		if(response.getStatusLine().getStatusCode() == Constants.SUCCESS_CODE) {
			JSONObject processInstanceIdListJSON = new JSONObject(EntityUtils.toString(response.getEntity()));
			for(int i = 0;i<processInstanceIdListJSON.length()-1; i++) {
				processInstanceIdList.add( processInstanceIdListJSON.getJSONArray("records").getJSONObject(i).getString("ProcessInstanceId") );
			}
		} else {
			throw new CustomException("issue with fetching process instance Id.");
		}
		return processInstanceIdList;
	}
	/**
	 * @param processInstanceIdList
	 * 			List of ProcessInstanceId to query on ProcessInstance object to get targetObjectId
	 * 
	 * @param accessToken
	 * 			To Access the org it required aceessToken for REST call
	 * 
	 * @param baseURL
	 *			Login into SF org the URL is get provided for httpGet call.
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
	 * @return targetobjectIdList
	 * 			return the List of String of targetObjectid list 
	 */
	public static List<String> getTargetobjectInstanceIds(List<String> processInstanceIdList, String accessToken, String baseURL) throws ClientProtocolException, IOException, JSONException, CustomException{
		List<String> targetobjectIdList = new ArrayList<String>();
		String processInstanceStr = Utility.generateInCluaseString(processInstanceIdList);
		String queryStr = "query?q=SELECT+Id+,+targetObjectid+FROM+ProcessInstance+where+id+in+("+ processInstanceStr + ")";
		System.out.println("query string==="+ queryStr);
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(baseURL+Constants.REST_URL+ queryStr);
		httpGet.addHeader(generateAuthHeader(accessToken));
		httpGet.addHeader(generatePrettyPrintHeader());
		HttpResponse response = httpClient.execute(httpGet);
		if(response.getStatusLine().getStatusCode() == Constants.SUCCESS_CODE) {
			JSONObject targetInstanceIdListJSON = new JSONObject(EntityUtils.toString(response.getEntity()));
			for(int i = 0;i<targetInstanceIdListJSON.length()-1; i++) {
				targetobjectIdList.add(targetInstanceIdListJSON.getJSONArray("records").getJSONObject(i).getString("TargetObjectId"));
			}
		} else {
			throw new CustomException("No Queue found for given name. Please check conffigurations. Queue Name:");
		}
		return targetobjectIdList;
	}
	/**
	 * 
	 * @param targetobjectInstanceIdList
	 * 			targetObjectId list as a parameter to query on KnowledgeArticle Type
	 * 
	 * @param articleType
	 * 			Knowledge Article type to get Ids.
	 * 
	 * @param accessToken
	 * 			To Access the org it required aceessToken for REST call
	 * 
	 * @param baseURL
	 *			Login into SF org the URL is get provided for httpGet call.
	 * 
	 * @param locale
	 * 			Language of the article that is going to be return
	 * 
	 * @return sfArticleList
	 * 			return List of String of Knowledge Articles
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 * @throws CustomException
	 */
	
	public static List<SFArticle> getKnowLedgeArticlesTranslatedVersions(List<String> targetobjectInstanceIdList,String articleType, String accessToken, String baseURL, String locale) throws ClientProtocolException, IOException, JSONException, CustomException{
		List<SFArticle> sfArticleList = new ArrayList<SFArticle>();
		String targetObjectIdStr = Utility.generateInCluaseString(targetobjectInstanceIdList);
		String queryStr="query?q=SELECT+Id+,+KnowledgeArticleId+,+MasterVersionId+FROM+"+articleType+"__kav+WHERE+language+=+'"+locale+"'+AND+PublishStatus+=+'draft'+AND+Id+IN+("+targetObjectIdStr+")";
		System.out.println("query "+queryStr);
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(baseURL+Constants.REST_URL+ queryStr);
		httpGet.addHeader(generateAuthHeader(accessToken));
		httpGet.addHeader(generatePrettyPrintHeader());
		HttpResponse response = httpClient.execute(httpGet);
		if(response.getStatusLine().getStatusCode() == Constants.SUCCESS_CODE) {
			JSONObject kaListJSON = new JSONObject(EntityUtils.toString(response.getEntity()));
			String xmlFileData=XML.toString(kaListJSON);
			System.out.println("json to xml of Knowledge Article record"+ xmlFileData);
			for(int i = 0;i<kaListJSON.length()-2; i++) {
				SFArticle sfArticle = new SFArticle();
				// Pankaj : Need to change
				sfArticle.setId(kaListJSON.getJSONArray("records").getJSONObject(i).getString("Id"));
				sfArticle.setMasterVersionId(kaListJSON.getJSONArray("records").getJSONObject(i).getString("MasterVersionId"));
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
	 * 			object of SFArticle for feching data from it.
	 * 
	 * @param fields
	 * 			List of Fields which is going to fetch from Article type
	 * 
	 * @param articleType
	 * 			Knowledge Article Type on which query get perform
	 *
	 * @param accessToken
	 * 			To Access the org it required aceessToken for REST call
	 * 
	 * @param baseURL
	 *			Login into SF org the URL is get provided for httpGet call.
	 * @return sfArticleList
	 * 		return list of SFArticle class variables.
	 * @throws Exception
	 */
	public static InputStream getKnowLedgeArticlesRecords(SFArticle sourceArticle , List<String> fields,String accessToken,String baseURL) throws Exception{
		String queryStr="query?q=select+"+Utility.getFieldsStr(fields)+"+from+"+sourceArticle.getType()+"__kav+where+Id+in+('"+sourceArticle.getMasterVersionId()+"')";
		System.out.println("last query==" + queryStr );
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(baseURL+Constants.REST_URL+ queryStr);
		httpGet.addHeader(generateAuthHeader(accessToken));
		httpGet.addHeader(generatePrettyPrintHeader());
		HttpResponse response = httpClient.execute(httpGet);
		if (response.getStatusLine().getStatusCode() == Constants.SUCCESS_CODE) {
			JSONObject KAListjson = new JSONObject(EntityUtils.toString(response.getEntity()));
						
		} else {
			throw new CustomException("Issue while fetching knowledge article records.");
		}
		return null;
	}	
	/**
	 * 
	 * @param sourceArticle
	 * 			sourceArticle object of SFArticle for data
	 * @param body
	 * 			JSON data into StringEntity fromat 
	 * @param accessToken
	 * 			To Access the org it required aceessToken for REST call
	 * 
	 * @param baseURL
	 *			Login into SF org the URL is get provided for httpGet call.
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 * @throws CustomException
	 */
	public static void insertArticleIntoSF(SFArticle sourceArticle, StringEntity body,String accessToken,String baseURL) throws ClientProtocolException, IOException, JSONException, CustomException{
		String queryStr ="sobjects/"+sourceArticle.getType()+"__kav"+"/"+sourceArticle.getId();
		System.out.println("query== "+ queryStr);
		HttpClient httpClient = new DefaultHttpClient();
		HttpPatch httppatch = new HttpPatch(baseURL+Constants.REST_URL+ queryStr);
		httppatch.addHeader(generateAuthHeader(accessToken));
		httppatch.addHeader(generatePrettyPrintHeader());
		httppatch.setEntity(body);
			
		HttpResponse response = httpClient.execute(httppatch);
		int statusCode = response.getStatusLine().getStatusCode();
			
		if (statusCode == 204) {
			System.out.println("Record Inseted Succesufully");
			} else {
				System.out.println("Insertion Fail");
			}
		
	}	

	
}
