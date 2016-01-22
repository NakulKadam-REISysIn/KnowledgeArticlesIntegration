package com.translations.globallink.connect.sf.model.vendor.service.impl;

import java.io.InputStream;
import java.util.List;

import com.translations.globallink.connect.sf.model.vendor.dto.SFArticle;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticleField;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticleType;
import com.translations.globallink.connect.sf.model.vendor.dto.SFConnectionConfig;
import com.translations.globallink.connect.sf.model.vendor.dto.SFLocale;
import com.translations.globallink.connect.sf.model.vendor.service.SFKnowledgeArticleService;
import com.translations.globallink.connect.sf.model.vendor.utility.SFUtility;
import com.translations.globallink.connect.sf.model.vendor.utility.Utility;

public class SFKnowledgeArticleServiceImpl implements SFKnowledgeArticleService {

	// Use this for connecting to SF via REST
	SFConnectionConfig connectionConfig;

	public SFKnowledgeArticleServiceImpl(SFConnectionConfig connectionConfig)
			throws Exception {
		this.connectionConfig = connectionConfig;
		// Add SF connection part here
	}

	/**
	 * @param targetSFLocale
	 *            Language of article is send in String format
	 * @param articleType
	 *            Knowledge Article Type inString Format
	 * @exception Exception
	 */
	public List<SFArticle> getReadyArticleIdsForTranslation(
			String targetSFLocale, String articleType) throws Exception {

		String accessToken = SFUtility.getAccessTokenFromSF();
		List<String> processInstanceIdList = SFUtility.getProcessInstanceIds(
				connectionConfig.getQueueName(), accessToken, connectionConfig.getUrl());
		List<String> targetInstaceIdList = SFUtility
				.getTargetobjectInstanceIds(processInstanceIdList, accessToken,
						connectionConfig.getUrl());
		List<SFArticle> sfArticleList = SFUtility
				.getKnowLedgeArticlesTranslatedVersions(targetInstaceIdList,
						articleType, accessToken, connectionConfig.getUrl(),
						targetSFLocale);
		return sfArticleList;
	}

	/**
	 * @param sourceArticle
	 *            object of SFArticle for feching data from it.
	 * @param fields
	 *            List of fields on which query is get perform.
	 * @exception Exception
	 */
	public InputStream getArticleStreamForTranslation(SFArticle sourceArticle,
			List<SFArticleField> fields) throws Exception {
		String accessToken = SFUtility.getAccessTokenFromSF();
		return SFUtility.getKnowLedgeArticlesRecords(sourceArticle, fields,
				accessToken, connectionConfig.getUrl());
	}

	/**
	 * @param sourceArticle
	 *            object of SFArticle for feching data from it.
	 * 
	 * @param targetSFLocale
	 *            target object language as a string
	 * 
	 * @param stream
	 *            Inserting data into SF InputStream is pass.
	 * 
	 * @exception Exception
	 * 
	 * 
	 */
	public void importTranslatedArticle(SFArticle sourceArticle,
			String targetSFLocale, InputStream stream) throws Exception {
		String accessToken = SFUtility.getAccessTokenFromSF();
		SFUtility.insertArticleIntoSF(sourceArticle, stream, accessToken,
				connectionConfig.getUrl());

	}

	// Phase II implementation
	public List<SFArticleType> getArticleTypes() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public List<SFArticleField> getFieldsForArticleType(String articleTypeName)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public List<SFLocale> getSFLocales() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
