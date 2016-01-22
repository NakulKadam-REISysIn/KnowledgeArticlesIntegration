package com.translations.globallink.connect.sf.model.vendor.dto;

public class SFArticle {

    private String id;
    private String masterVersionId;
    private String type;
    private String language;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

	/**
	 * @return the masterVersionId
	 */
	public String getMasterVersionId() {
		return masterVersionId;
	}

	/**
	 * @param masterVersionId the masterVersionId to set
	 */
	public void setMasterVersionId(String masterVersionId) {
		this.masterVersionId = masterVersionId;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
}
