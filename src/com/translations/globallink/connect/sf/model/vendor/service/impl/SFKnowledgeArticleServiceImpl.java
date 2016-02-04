package com.translations.globallink.connect.sf.model.vendor.service.impl;

import java.io.InputStream;
import java.util.List;

import com.translations.globallink.connect.sf.model.vendor.dto.SFArticle;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticleField;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticleType;
import com.translations.globallink.connect.sf.model.vendor.dto.SFConnectionConfig;
import com.translations.globallink.connect.sf.model.vendor.dto.SFLocale;
import com.translations.globallink.connect.sf.model.vendor.dto.SFQueue;
import com.translations.globallink.connect.sf.model.vendor.dto.SFUser;
import com.translations.globallink.connect.sf.model.vendor.service.SFKnowledgeArticleService;
import com.translations.globallink.connect.sf.model.vendor.utility.SFUtility;

public class SFKnowledgeArticleServiceImpl implements SFKnowledgeArticleService {

    // Use this for connecting to SF via REST
    SFConnectionConfig connectionConfig;

    public SFKnowledgeArticleServiceImpl(SFConnectionConfig connectionConfig) throws Exception {
	this.connectionConfig = connectionConfig;
    }

    /**
     * @param targetSFLocale
     *            Language of article is send in String format
     * @param articleType
     *            Knowledge Article Type inString Format
     * @exception Exception
     */
    public List<SFArticle> getReadyArticleIdsForTranslation(String targetSFLocale, String articleType, String sfQueueId) throws Exception {

	String accessToken = SFUtility.getAccessTokenFromSF(this.connectionConfig);
	//String queueId = SFUtility.getQueueId(connectionConfig.getQueueName(),
	//	accessToken, connectionConfig.getUrl());
	List<String> processInstanceIdList = SFUtility.getProcessInstanceIds(sfQueueId, accessToken, connectionConfig.getUrl());
	List<String> targetInstaceIdList = SFUtility.getTargetobjectInstanceIds(processInstanceIdList, accessToken, connectionConfig.getUrl());
	List<SFArticle> sfArticleList = SFUtility.getKnowLedgeArticlesTranslatedVersions(targetInstaceIdList, articleType, accessToken, connectionConfig.getUrl(), targetSFLocale);
	return sfArticleList;
    }

    /**
     * @param sourceArticle
     *            object of SFArticle for feching data from it.
     * @param fields
     *            List of fields on which query is get perform.
     * @exception Exception
     */
    public InputStream getArticleStreamForTranslation(SFArticle sourceArticle, List<SFArticleField> fields) throws Exception {
	String accessToken = SFUtility.getAccessTokenFromSF(this.connectionConfig);
	return SFUtility.getKnowLedgeArticlesRecords(sourceArticle, fields, accessToken, connectionConfig.getUrl());
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
    public void importTranslatedArticle(SFArticle sourceArticle, String targetSFLocale, InputStream stream) throws Exception {
	String accessToken = SFUtility.getAccessTokenFromSF(this.connectionConfig);
	SFUtility.insertArticleIntoSF(sourceArticle, stream, accessToken, this.connectionConfig.getUrl());

    }

    // Phase II implementation
    public List<SFArticleType> getArticleTypes() throws Exception {
	return SFUtility.gettype(SFUtility.getAccessTokenFromSF(this.connectionConfig), this.connectionConfig.getUrl());

    }

    public List<SFArticleField> getFieldsForArticleType(String articleTypeName) throws Exception {
	return SFUtility.FieldsForArticleType(articleTypeName, SFUtility.getAccessTokenFromSF(this.connectionConfig), this.connectionConfig.getUrl());
    }

    public List<SFLocale> getSFLocales() throws Exception {
	return SFUtility.getlocales(SFUtility.getAccessTokenFromSF(this.connectionConfig), this.connectionConfig.getUrl());
    }

    public List<SFQueue> getSFQueues() throws Exception {
	return SFUtility.SFQueueList(SFUtility.getAccessTokenFromSF(this.connectionConfig), this.connectionConfig.getUrl());
    }

    public boolean testConnection(SFConnectionConfig config) throws Exception {
	try {
	    SFUtility.getAccessTokenFromSF(config);
	    return true;
	} catch (Exception ex) {
	    throw ex;
	}
    }

	public void setAssignee(SFArticle sourceArticle, String userId)
			throws Exception {
		SFUtility.insertAssignedId(sourceArticle, userId,
				SFUtility.getAccessTokenFromSF(this.connectionConfig),
				this.connectionConfig.getUrl());
	
    }

    public List<SFUser> getUsers() throws Exception {
		return SFUtility.getUserInfo(SFUtility.getAccessTokenFromSF(this.connectionConfig),
				this.connectionConfig.getUrl());
		
		
    }
}
