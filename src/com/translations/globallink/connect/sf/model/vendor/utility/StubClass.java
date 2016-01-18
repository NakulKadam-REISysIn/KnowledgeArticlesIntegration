package com.translations.globallink.connect.sf.model.vendor.utility; 


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.translations.globallink.connect.sf.model.vendor.dto.SFArticle;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticleType;
import com.translations.globallink.connect.sf.model.vendor.dto.SFConnectionConfig;
import com.translations.globallink.connect.sf.model.vendor.service.SFKnowledgeArticleService;
import com.translations.globallink.connect.sf.model.vendor.service.impl.SFKnowledgeArticleServiceImpl;

	/**
	 * @class Name-SFUtilityMainClass
	 *		this is Class which contains main() method to run and execute all methoed inside it.
	 *
	 * @author- 
	
	 *@createdDate-
	*/
public class StubClass {
	
	/**
	 * @throws Exception
	 */

	public static void main(String[] args) throws Exception {
		//callgetReadyArticleIdsForTranslation();
		callgetArticleStreamForTranslation();
		//callimportTranslatedArticle();
	}
	
	public static void callgetReadyArticleIdsForTranslation() throws Exception{
		SFConnectionConfig sfConnectionConfig= Utility.getLoginDetailsFromMiddleware();
		SFKnowledgeArticleService sfKnowledgeArticleImpl= new SFKnowledgeArticleServiceImpl(sfConnectionConfig);
		List<SFArticle> ReadyArticleIdsForTranslation=sfKnowledgeArticleImpl.getReadyArticleIdsForTranslation("fr", "offer");
		for(SFArticle sfArticle : ReadyArticleIdsForTranslation){
			System.out.println(sfArticle.getId()+"==========MasterVersion Id:" + sfArticle.getMasterVersionId());
		}
	}
	
	public static void callgetArticleStreamForTranslation() throws Exception{
		List<String> fields= new ArrayList<String>();
		fields.add("Id");
		fields.add("Name__c");
		fields.add("Summary");
			
		SFConnectionConfig sfConnectionConfig= Utility.getLoginDetailsFromMiddleware();
		SFKnowledgeArticleService sfKnowledgeArticleImpl= new SFKnowledgeArticleServiceImpl(sfConnectionConfig);
		List<SFArticle> ReadyArticleIdsForTranslation=sfKnowledgeArticleImpl.getReadyArticleIdsForTranslation("fr", "offer");
		SFArticle sfArticle = new SFArticle();
		for(SFArticle sfArticleObj : ReadyArticleIdsForTranslation){
			System.out.println(sfArticleObj.getId()+"==========MasterVersion Id:" + sfArticleObj.getMasterVersionId());
			sfArticle=sfArticleObj;
		}
		sfArticle.setType("offer");
		InputStream ArticleStreamTransaltion = sfKnowledgeArticleImpl.getArticleStreamForTranslation(sfArticle, fields);
		System.out.println("ArticleStreamTransaltion========"+ ArticleStreamTransaltion);
	}
	public static void callimportTranslatedArticle() throws Exception {
		SFConnectionConfig sfConnectionConfig= Utility.getLoginDetailsFromMiddleware();
		SFKnowledgeArticleService sfKnowledgeArticleImpl= new SFKnowledgeArticleServiceImpl(sfConnectionConfig);
		SFArticle sfArticle = new SFArticle();
		sfArticle.setId("ka028000000I2zgAAC");
		sfArticle.setType("offer");
		sfKnowledgeArticleImpl.importTranslatedArticle(sfArticle, null, null);
	}
}
