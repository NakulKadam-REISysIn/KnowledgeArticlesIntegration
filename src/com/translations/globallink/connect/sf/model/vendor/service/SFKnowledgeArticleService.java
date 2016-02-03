package com.translations.globallink.connect.sf.model.vendor.service;

import java.io.InputStream;
import java.util.List;

import com.translations.globallink.connect.sf.model.vendor.dto.SFArticle;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticleField;
import com.translations.globallink.connect.sf.model.vendor.dto.SFArticleType;
import com.translations.globallink.connect.sf.model.vendor.dto.SFConnectionConfig;
import com.translations.globallink.connect.sf.model.vendor.dto.SFLocale;
import com.translations.globallink.connect.sf.model.vendor.dto.SFQueue;
import com.translations.globallink.connect.sf.model.vendor.dto.SFUser;

public interface SFKnowledgeArticleService {

	/**
	 * @param targetSFLocale
	 *            Target SF locale code
	 * @param articleType
	 *            Type of SF article
	 * @return List of SFArticle populated with source Knowledge Article Ids and
	 *         Article Types that are ready for translation for provided
	 *         targetSFLocale code.
	 * @throws Exception
	 */
	public List<SFArticle> getReadyArticleIdsForTranslation(
			String targetSFLocale, String articleType) throws Exception;

	/**
	 * @param sourceArticle
	 *            Source Knowledge Article
	 * @param fields
	 *            List of configured fields for this sourceArticleType
	 * @return InputStream of the source XML
	 * @throws Exception
	 */
	public InputStream getArticleStreamForTranslation(SFArticle sourceArticle,
			List<SFArticleField> fields) throws Exception;

	/**
	 * @param sourceArticle
	 *            Source Knowledge Article
	 * @param targetSFLocale
	 *            Target SF locale code
	 * @param stream
	 *            InputStream of translated XML
	 * @throws Exception
	 */
	public void importTranslatedArticle(SFArticle sourceArticle,
			String targetSFLocale, InputStream stream) throws Exception;

	// Phase II implementation
	/**
	 * @return List of SFArticleType populated with Article Type details
	 * @throws Exception
	 */
	public List<SFArticleType> getArticleTypes() throws Exception;

	/**
	 * @param articleTypeName
	 *            Name of the Knowledge Article Type
	 * @return List of SFArticleField populated with field details for this
	 *         Knowledge Article Type
	 * @throws Exception
	 */
	public List<SFArticleField> getFieldsForArticleType(String articleTypeName)
			throws Exception;

	/**
	 * @return List of SFLocale populated with all the configured locale in
	 *         SalesForce
	 * @throws Exception
	 */
	public List<SFLocale> getSFLocales() throws Exception;
	
	/**
	 * @return List of SFQueue populated with all the configured Queues in
	 *         SalesForce
	 * @throws Exception
	 */
	public List<SFQueue> getSFQueues() throws Exception;
	
	/**
	 * @param config SalesForce Connection details
	 * @return Success or Failure
	 * @throws Exception
	 */
	public boolean testConnection(SFConnectionConfig config) throws Exception;

	/**
	 * @param sourceArticle Source Knowledge Article
	 * @param userId User Id
	 * @throws Exception
	 */
	public void setAssignee(SFArticle sourceArticle, String userId) throws Exception;

	/**
	 * @return List of SF Users
	 * @throws Exception
	 */
	public List<SFUser> getUsers() throws Exception;
}
