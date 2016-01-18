package com.translations.globallink.connect.sf.model.vendor.utility;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.translations.globallink.connect.sf.model.vendor.constants.Constants;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticle;
import com.translations.globallink.connect.sf.model.vendor.dto.SFConnectionConfig;
import com.translations.globallink.connect.sf.model.vendor.exception.CustomException;

import java.io.IOException; 
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

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
	
	public static String generateInCluaseString(List<String> strList){
		String returnStr = "";
		for(String str : strList){
			returnStr+="'" + str + "',";
		}
		return returnStr.substring(0, returnStr.length() -1);
	}
	
	public static String generateInCluaseStringForSFArticle(List<SFArticle> sfarticle)
	{
		String masterVersionIdStr="";
		
		for(SFArticle masterVersionIds:sfarticle){
			
			masterVersionIdStr+="'" + masterVersionIds.getMasterVersionId() + "',";
		}
		return masterVersionIdStr.substring(0, masterVersionIdStr.length() -1);
	}
	
	public static StringEntity GenerateJsonBody() throws JSONException, IOException{
		
		JSONObject Article = new JSONObject();
		Article.put("Title", "ffgggh");
		Article.put("Name__c", "john cena");
		Article.put("UrlName", "johncena123");
		StringEntity body = new StringEntity(Article.toString(1)); 
		body.setContentType("application/json");
		return body;
		
	}
	public static String getFieldsStr(List<String> fields){	
		
		String fieldsStr="";
		for(String str:fields){
			
			fieldsStr+=str+"+,+";
		}
		return fieldsStr.substring(0, fieldsStr.length()-3);
		
	}
	
}
