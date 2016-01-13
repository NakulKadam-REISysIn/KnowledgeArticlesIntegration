package com.translations.globallink.connect.sf.model.vendor.service.impl;

import java.io.InputStream;
import java.util.List;

import com.translations.globallink.connect.sf.model.vendor.Utility.SFUtility;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticle;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticleField;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticleType;
import com.translations.globallink.connect.sf.model.vendor.dto.SFConnectionConfig;
import com.translations.globallink.connect.sf.model.vendor.dto.SFLocale;
import com.translations.globallink.connect.sf.model.vendor.service.SFKnowledgeArticleService;

public class SFKnowledgeArticleServiceImpl implements SFKnowledgeArticleService {
    
    // Use this for connecting to SF via REST
    SFConnectionConfig connectionConfig;
    
    public SFKnowledgeArticleServiceImpl(SFConnectionConfig connectionConfig) throws Exception {
	this.connectionConfig = connectionConfig;
	// Add SF connection part here
    }
    
    public List<SFArticle> getReadyArticleIdsForTranslation(String targetSFLocale, String articleType) throws Exception {
		// TODO Auto-generated method stub
		return null;
    }

    public InputStream getArticleStreamForTranslation(SFArticle sourceArticle, List<String> fields) throws Exception {
	// TODO Auto-generated method stub
	return null;
    }
    
    public void importTranslatedArticle(SFArticle sourceArticle, String targetSFLocale, InputStream stream) throws Exception {
	// TODO Auto-generated method stub

    }

    // Phase II implementation
    public List<SFArticleType> getArticleTypes() throws Exception {
	// TODO Auto-generated method stub
	return null;
    }

    public List<SFArticleField> getFieldsForArticleType(String articleTypeName) throws Exception {
	// TODO Auto-generated method stub
	return null;
    }

    public List<SFLocale> getSFLocales() throws Exception {
	// TODO Auto-generated method stub
	return null;
    }
}
