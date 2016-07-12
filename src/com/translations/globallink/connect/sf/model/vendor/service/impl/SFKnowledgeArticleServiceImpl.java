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
import com.translations.globallink.connect.sf.model.vendor.utility.SalesForceService;

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
    public List<SFArticle> getReadyArticleIdsForTranslation(String targetSFLocale, String articleType, String sfQueueId, Boolean includeDraft) throws Exception {
	SalesForceService sfService = new SalesForceService(this.connectionConfig);
	List<String> targetInstanceIdList = sfService.getTargetInstanceIds(sfQueueId);
	List<SFArticle> sfArticleList = sfService.getKnowLedgeArticlesTranslatedVersions(targetInstanceIdList, articleType, targetSFLocale, includeDraft);
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
	SalesForceService sfService = new SalesForceService(this.connectionConfig);
	return sfService.getKnowLedgeArticlesRecords(sourceArticle, fields);
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
	SalesForceService sfService = new SalesForceService(this.connectionConfig);
	sfService.insertArticleIntoSF(sourceArticle, stream);
    }

    // Phase II implementation
    public List<SFArticleType> getArticleTypes() throws Exception {
	SalesForceService sfService = new SalesForceService(this.connectionConfig);
	return sfService.gettype();
    }

    public List<SFArticleField> getFieldsForArticleType(String articleTypeName) throws Exception {
	SalesForceService sfService = new SalesForceService(this.connectionConfig);
	return sfService.FieldsForArticleType(articleTypeName);
    }

    public List<SFLocale> getSFLocales() throws Exception {
	SalesForceService sfService = new SalesForceService(this.connectionConfig);
	return sfService.getlocales();
    }

    public List<SFQueue> getSFQueues() throws Exception {
	SalesForceService sfService = new SalesForceService(this.connectionConfig);
	return sfService.SFQueueList();
    }

    public boolean testConnection(SFConnectionConfig config) throws Exception {
	SalesForceService sfService = new SalesForceService(config);
	return sfService.testConnection();
    }

    public void setAssignee(SFArticle sourceArticle, String userId) throws Exception {
	SalesForceService sfService = new SalesForceService(this.connectionConfig);
	sfService.insertAssignedId(sourceArticle, userId);
    }

    public List<SFUser> getUsers() throws Exception {
	SalesForceService sfService = new SalesForceService(this.connectionConfig);
	return sfService.getUserInfo();
    }
}
