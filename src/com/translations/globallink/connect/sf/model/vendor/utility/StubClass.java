package com.translations.globallink.connect.sf.model.vendor.utility;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.translations.globallink.connect.sf.model.vendor.dto.SFArticle;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticleField;
import com.translations.globallink.connect.sf.model.vendor.dto.SFConnectionConfig;
import com.translations.globallink.connect.sf.model.vendor.service.SFKnowledgeArticleService;
import com.translations.globallink.connect.sf.model.vendor.service.impl.SFKnowledgeArticleServiceImpl;

/**
 * @class Name-SFUtilityMainClass this is Class which contains main() method to
 *        run and execute all methoed inside it.
 * 
 * @author-
 * 
 * @createdDate-
 */
public class StubClass {

	/**
	 * @throws Exception
	 */

	public static void main(String[] args) throws Exception {
		//callgetReadyArticleIdsForTranslation();//after translation
		 callgetArticleStreamForTranslation();//before transaltion
		 //callimportTranslatedArticle();

	}

	public static void callgetReadyArticleIdsForTranslation() throws Exception {
		SFConnectionConfig sfConnectionConfig = Utility
				.getLoginDetailsFromMiddleware();
		SFKnowledgeArticleService sfKnowledgeArticleImpl = new SFKnowledgeArticleServiceImpl(
				sfConnectionConfig);
		List<SFArticle> ReadyArticleIdsForTranslation = sfKnowledgeArticleImpl
				.getReadyArticleIdsForTranslation("fr", "offer");
		for (SFArticle sfArticle : ReadyArticleIdsForTranslation) {
			System.out.println("ArticleId:" + sfArticle.getId()
					+ "==========MasterVersion Id:"
					+ sfArticle.getMasterVersionId()+"=======language:"+sfArticle.getLanguage());

		}

	}

	public static void callgetArticleStreamForTranslation() throws Exception {
		SFConnectionConfig sfConnectionConfig = Utility
				.getLoginDetailsFromMiddleware();
		SFKnowledgeArticleService sfKnowledgeArticleImpl = new SFKnowledgeArticleServiceImpl(
				sfConnectionConfig);
		List<SFArticle> ReadyArticleIdsForTranslation = sfKnowledgeArticleImpl
				.getReadyArticleIdsForTranslation("fr", "offer");
		SFArticle sfArticle = new SFArticle();
		for (SFArticle sfArticleObj : ReadyArticleIdsForTranslation) {
			System.out.println(sfArticleObj.getId()
					+ "==========MasterVersion Id:"
					+ sfArticleObj.getMasterVersionId());
			sfArticle = sfArticleObj;
		}
		
		sfArticle.setType("offer");
		sfArticle.setLanguage("fr");

		List<SFArticleField> fieldsInfo = Utility.getSFArticleCustomFieldList();
		InputStream ArticleStreamTransaltion = sfKnowledgeArticleImpl
				.getArticleStreamForTranslation(sfArticle, fieldsInfo);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				ArticleStreamTransaltion));
		String read;

		while ((read = br.readLine()) != null) {
			System.out.println(read);
		}
	}

	public static void callimportTranslatedArticle() throws Exception {
		SFConnectionConfig sfConnectionConfig = Utility
				.getLoginDetailsFromMiddleware();
		SFKnowledgeArticleService sfKnowledgeArticleImpl = new SFKnowledgeArticleServiceImpl(
				sfConnectionConfig);
		SFArticle sfArticle = new SFArticle();
		sfArticle.setId("ka028000000I5JZAA0");
		sfArticle.setType("offer");
		InputStream inputstream = new FileInputStream("E://input1.txt");
		sfKnowledgeArticleImpl.importTranslatedArticle(sfArticle, null, inputstream);
	}
}
